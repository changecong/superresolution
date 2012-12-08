/********************************************************
 * File Name: getLambdaGauss.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 15:13]
 * Last Modified: [2012-12-08 13:01]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <math.h> 
#include "superresolution.sh"

import "i_sender";
import "i_receiver";

const double stdlim = 5; 

// getLambdaGauss(M[k], lh, lw, &nuv, &nuh, &b11, &b12, &b22, &deltav, &deltah);
behavior GetLambdaGauss(i_receiver dh_m, i_sender q_nuv, i_sender q_nuh, i_sender q_b11, 
                        i_sender q_b12, i_sender q_b22, i_sender q_deltav, i_sender q_deltah)
{ 
  int x, y;
  double nuv, nuh, b11, b12, b22, deltav, deltah;
  double denom, h11, h12, h21, h22, detH, gam;
  double M[11];

  void main(void) {

    dh_m.receive(M, sizeof(double[11]));

    for (y = 0; y < L_IMG_HEIGHT; y++) {
      for (x = 0; x < L_IMG_WIDTH; x++) {


    denom = (x+1)*M[6] + (y+1)*M[7] + 1;        

    nuh = ((x+1)*M[0] + (y+1)*M[1]  + M[2])/denom -1; // the final -1 takes us back to indexed-from-0 coords.
    nuv = ((x+1)*M[3] + (y+1)*M[4]  + M[5])/denom -1; // the final -1 takes us back to indexed-from-0 coords.
    
    denom = denom * denom;

    // H = [h11,h12;h21,h22] is the Hessian of the transform given by apars, evaluated at
    // the point that (ismv,ismh) maps to under that transform.

    h11 = ((M[0]*M[7] - M[1]*M[6])*(y+1) + M[0] - M[2]*M[6])/denom;
    h12 = ((M[1]*M[6] - M[0]*M[7])*(x+1) + M[1] - M[2]*M[7])/denom;
    h21 = ((M[3]*M[7] - M[4]*M[6])*(y+1) + M[3] - M[5]*M[6])/denom;
    h22 = ((M[4]*M[6] - M[3]*M[7])*(x+1) + M[4] - M[5]*M[7])/denom;
    
    // I also know that the covariance of the psf will be H*Sig*H', where sig was the
    // original covariance in the low-res image, which has a variance of sqgam.
    detH = h11*h22 - h12*h21;
    
    detH = 1/(M[10]*(detH*detH));
    
    b11 = detH*(h21*h21+h22*h22);  // inv(H*Sig*H')(1,1)
    b12 = -2*detH*(h11*h21+h12*h22);  // inv(H*Sig*H')(1,2)+inv(H*Sig*H')(2,1)
    b22 = detH*(h11*h11+h12*h12); // inv(H*Sig*H')(2,2)

    // Now find the greatest h and v extend of this PSF that's within 4*gam in the original (low-res) PSF.
    // Derive this by expressing x in terms of y, using the quadratic formula, 
    // and finding the value of y^2 necessary to set the sqrt(b^2-4ac) part to zero.
    denom = sqrt(4*(b11)*(b22)-(b12)*(b12));

    gam = sqrt(M[10]);

    deltav = 2*stdlim*gam*sqrt(b11)/denom; // vertical extent of the new psf kernel
    deltah = 2*stdlim*gam*sqrt(b22)/denom; // vertical extent of the new psf kernel

    q_nuv.send(&nuv, sizeof(double));
    q_nuh.send(&nuh, sizeof(double));
    q_b11.send(&b11, sizeof(double));
    q_b12.send(&b12, sizeof(double));
    q_b22.send(&b22, sizeof(double));
    q_deltav.send(&deltav, sizeof(double));
    q_deltah.send(&deltah, sizeof(double));

      }
    }
  }
};
