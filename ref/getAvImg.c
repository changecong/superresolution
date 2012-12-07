/********************************************************
 * File Name: getAvImg.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-30 12:26]
 * Last Modified: [2012-12-01 03:36]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <math.h>
#include "getAvImg.h"

#include <stdio.h>

const double stdlim = 5;  // controls how many standard deviations

void getLambdaGauss(const double apars[11], const double ismv, const double ismh, 
                    double *nuv, double *nuh, double *b11, double *b12, 
                    double *b22, double *deltav, double *deltah) {

    // double sqgam = apars[10];
    
    double denom = (ismh+1)*apars[6] + (ismv+1)*apars[7] + 1;
    
    *nuh = ((ismh+1)*apars[0] + (ismv+1)*apars[1]  + apars[2])/denom -1; // the final -1 takes us back to indexed-from-0 coords.
    *nuv = ((ismh+1)*apars[3] + (ismv+1)*apars[4]  + apars[5])/denom -1; // the final -1 takes us back to indexed-from-0 coords.
    
    denom = denom * denom;

    // H = [h11,h12;h21,h22] is the Hessian of the transform given by apars, evaluated at
    // the point that (ismv,ismh) maps to under that transform.

    double h11 = ((apars[0]*apars[7] - apars[1]*apars[6])*(ismv+1) + apars[0] - apars[2]*apars[6])/denom;
    double h12 = ((apars[1]*apars[6] - apars[0]*apars[7])*(ismh+1) + apars[1] - apars[2]*apars[7])/denom;
    double h21 = ((apars[3]*apars[7] - apars[4]*apars[6])*(ismv+1) + apars[3] - apars[5]*apars[6])/denom;
    double h22 = ((apars[4]*apars[6] - apars[3]*apars[7])*(ismh+1) + apars[4] - apars[5]*apars[7])/denom;
    
    // I also know that the covariance of the psf will be H*Sig*H', where sig was the
    // original covariance in the low-res image, which has a variance of sqgam.
    double detH = h11*h22 - h12*h21;
    
    detH = 1/(apars[10]*(detH*detH));
    
    *b11 = detH*(h21*h21+h22*h22);  // inv(H*Sig*H')(1,1)
    *b12 = -2*detH*(h11*h21+h12*h22);  // inv(H*Sig*H')(1,2)+inv(H*Sig*H')(2,1)
    *b22 = detH*(h11*h11+h12*h12); // inv(H*Sig*H')(2,2)

    // Now find the greatest h and v extend of this PSF that's within 4*gam in the original (low-res) PSF.
    // Derive this by expressing x in terms of y, using the quadratic formula, 
    // and finding the value of y^2 necessary to set the sqrt(b^2-4ac) part to zero.
    denom = sqrt(4*(*b11)*(*b22)-(*b12)*(*b12));

    double gam = sqrt(apars[10]);

    *deltav = 2*stdlim*gam*sqrt(*b11)/denom; // vertical extent of the new psf kernel
    *deltah = 2*stdlim*gam*sqrt(*b22)/denom; // vertical extent of the new psf kernel
}

void getAvImg(ImagePack o, double avim[H_IMG_HEIGHT][H_IMG_WIDTH])
{

  // initialization
  double M[K][11] = {{0.0}};
  int i, j, mptr = 0;  // iterator and pointer
  double *H;  // points to o[i].H

  // double avim[H_IMG_HEIGHT][H_IMG_WIDTH] = {{0.0}};  // avim image

  // calculate M
  for (i = 0; i < K; i++) {
    H = o[i].H;
    for (j = 0; j < 8; j++) {
      M[i][mptr++] = H[j] / H[8]; 
    }
    M[i][mptr++] = o[i].la;
    M[i][mptr++] = o[i].lb;
    M[i][mptr++] = pow(o[i].g, 2);

    mptr = 0;
  }  

#ifdef DEBUG_M
  int l, m;
  for (l = 0; l < K; l++) {
    for (m = 0; m < 11; m++) {
      printf("%f ", M[l][m]);
    }
    printf("\n");
  }
#endif

 // generate the average image.
 avimFromoN10Gauss(avim, M, o);
  
}

void avimFromoN10Gauss(double av[H_IMG_HEIGHT][H_IMG_WIDTH], double M[K][11], ImagePack o)
{
  // initialization
  // iterators
  int k;  // for images
  int lh, lw, hh, hw;  // low-res height & width, high-res height & width
  
  // parameters for getLambdaGauss
  double b11, b12, b22, nuh, nuv, deltav, deltah; 

  // others 
  double  nh, nv;
  double myscaler = 0;
  int lowh, highh, lowv, highv;

  double h[H_IMG_HEIGHT][H_IMG_WIDTH] = {{0.0}};  //
  double ms[H_IMG_HEIGHT][H_IMG_WIDTH] = {{0.0}};  // mask

  for (k = 0; k < K; k++) {  // for each image

    for (lh = 0; lh < L_IMG_HEIGHT; lh++) {
      for (lw = 0; lw < L_IMG_WIDTH; lw++) {  // for each pixil of low-res iamge

          
        // maps the location to the HR frame and find the params of the affine 
        // approximation to the PSF under projection. 
        getLambdaGauss(M[k], lh, lw, &nuv, &nuh, &b11, &b12, &b22, &deltav, &deltah);

#ifdef DEBUG_LAMB
        printf("%f %f %f %f %f %f %f\n", nuv, nuh, b11, b12, b22, deltav, deltah);
#endif

        if ((nuh >= 0) && (nuh < H_IMG_WIDTH) && (nuv >= 0) && (nuv < H_IMG_HEIGHT)) {
          // the new point is in the high-res image

          // this means the mapped low-res pixel lands somewhere in the superimage.
          lowh = (floor(nuh-deltah)+1 > 0) ? (int)floor(nuh-deltah+1) : 0;
          lowv = (floor(nuv-deltav)+1 > 0) ? (int)floor(nuv-deltav+1) : 0;

          highh = (ceil(nuh+deltah) < H_IMG_WIDTH) ? (int)ceil(nuh+deltah) : H_IMG_WIDTH;
          highv = (ceil(nuv+deltav) < H_IMG_HEIGHT) ? (int)ceil(nuv+deltav) : H_IMG_HEIGHT; 
          // Find the right box in the HR image to scan over.

          myscaler = 0;

          // Find all the values at pixel locations 
          // (and for a 1-pixel border around region).
          // insertion
          for (hw = lowh; hw < highh; hw++) {

            for (hh = lowv; hh < highv; hh++) {
      
              nv = hh-nuv;
              nh = hw-nuh;
              h[hh][hw] = exp(-0.5*(b11*nh*nh + b12*nh*nv + b22*nv*nv));
              myscaler += h[hh][hw];
            }
          }
          if (myscaler > 0.0000001) {

            for (hw = lowh; hw < highh; hw++) {

              for (hh = lowv; hh < highv; hh++) {
            
                h[hh][hw] = h[hh][hw]/myscaler;
                av[hh][hw] += o[k].im[lh][lw]*h[hh][hw];
                ms[hh][hw] += h[hh][hw];
              }
            }
          }
        }
      }
    }
  }
ls
}

