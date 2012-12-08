/********************************************************
 * File Name: avimFromoN10Gauss.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 15:28]
 * Last Modified: [2012-12-08 16:43]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"

import "i_receiver";
import "i_receive";

void assignment(double i[H_IMG_HEIGHT][H_IMG_WIDTH], double o[H_IMG_HEIGHT][H_IMG_WIDTH])
{

  int x, y;
  for(y = 0; y < H_IMG_HEIGHT; y++) { 
    for(x = 0; x < H_IMG_HEIGHT; x++) {

      o[y][x] = i[y][x];

    }
  }
}

behavior AvimFromoN10Gauss(i_receiver q_nuv, i_receiver q_nuh, i_receiver q_b11, 
                           i_receiver q_b12, i_receiver q_b22, i_receiver q_deltav, i_receiver q_deltah,
                           i_receiver q_pixel, inout double p_avim[H_IMG_HEIGHT][H_IMG_WIDTH],
                           inout double p_h[H_IMG_HEIGHT][H_IMG_WIDTH], 
                           inout double p_ms[H_IMG_HEIGHT][H_IMG_WIDTH])
{

  double limg[1] = {0.0};

  double  nh, nv;
  double myscaler = 0;
  int hw, hh, lowh, highh, lowv, highv;
  int x, y, i, j, k;
  
  double nuv, nuh, b11, b12, b22, deltav, deltah;

  double avim[H_IMG_HEIGHT][H_IMG_WIDTH];
  double h[H_IMG_HEIGHT][H_IMG_WIDTH];
  double ms[H_IMG_HEIGHT][H_IMG_WIDTH];

  void main(void) {

    for (k = 0; k < K; k++) {

    assignment(p_avim, avim);
    assignment(p_h, h);
    assignment(p_ms, ms);

#ifdef DEBUG_B
  for (i = 0; i < H_IMG_HEIGHT; i++) {
    for (j = 0; j < H_IMG_WIDTH; j++) {
      printf("%f ", avim[i][j]);
    }
    printf("\n\n");
  }
 
#endif


    for(y = 0; y < L_IMG_HEIGHT; y++) {
      for(x = 0; x < L_IMG_HEIGHT; x++) {
  

        q_nuv.receive(&nuv, sizeof(double));
        q_nuh.receive(&nuh, sizeof(double));
        q_b11.receive(&b11, sizeof(double));
        q_b12.receive(&b12, sizeof(double));
        q_b22.receive(&b22, sizeof(double));
        q_deltav.receive(&deltav, sizeof(double));
        q_deltah.receive(&deltah, sizeof(double));

#ifdef DEBUG_LAMB
  printf("%f %f %f %f %f %f %f\n", nuv, nuh, b11, b12, b22, deltav, deltah);
#endif

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

#ifdef DEBUG_MAP
  printf("%d %d %d %d\n", lowh, lowv, highh, highv);
#endif


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


#ifdef DEBUG_C
  for (i = 0; i < H_IMG_HEIGHT; i++) {
    for (j = 0; j < H_IMG_WIDTH; j++) {
      printf("%f ", avim[i][j]);
    }
    printf("\n\n");
  }

#endif


    }  

#ifdef DEBUG_A
  for (i = 0; i < H_IMG_HEIGHT; i++) {
    for (j = 0; j < H_IMG_WIDTH; j++) {
      printf("%f ", avim[i][j]);
    }
    printf("\n\n");
  }

#endif

    assignment(avim, p_avim);
    assignment(h, p_h);
    assignment(ms, p_ms);
  } }
};
