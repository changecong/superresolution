/********************************************************
 * File Name: avimFromoN10Gauss.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 15:28]
 * Last Modified: [2012-12-06 13:45]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"

import "i_receiver";

behavior AvimFromoN10Gauss(in int nuv, in int nuh, in int b11, 
                           in int b12, in int b22, in int deltav, in int deltah,
                           i_receiver q_pixel, inout double avim[H_IMG_HEIGHT][H_IMG_WIDTH],
                           inout double h[H_IMG_HEIGHT][H_IMG_WIDTH], 
                           inout double ms[H_IMG_HEIGHT][H_IMG_WIDTH])
{


  double limg[1] = {0.0};

  double  nh, nv;
  double myscaler = 0;
  int hw, hh, lowh, highh, lowv, highv;

  void main(void) {

    if ((nuh >= 0) && (nuh < H_IMG_WIDTH) && (nuv >= 0) && (nuv < H_IMG_HEIGHT)) {
      // the new point is in the high-res image

      q_pixel.receive(limg, sizeof(double));  // read the pixel

#ifdef DEBUG_Q_PIXEL
  printf("receive: %f\n", limg[0]);
#endif

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
            avim[hh][hw] += limg[0] * h[hh][hw]; // using pixel.
            ms[hh][hw] += h[hh][hw];
          }
        }
      }
    }
  }
};
