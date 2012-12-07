/********************************************************
 * File Name: design.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:43]
 * Last Modified: [2012-12-07 00:27]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <stdio.h>
#include "superresolution.sh"

import "i_receive";

import "c_queue";
import "c_handshake";
import "c_double_handshake";

import "getM";
import "read";
import "getnewpixel";
import "genAvim";
import "write";

behavior Design(in unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH],
                in double HLG[12], i_receive start, i_sender q_bmp)
{

  // channels
  const unsigned long qSize_pixel = sizeof(double[128]);
  c_queue q_pixel(qSize_pixel);
  const unsigned long qSize_avim = sizeof(char[H_IMG_WIDTH]);
  c_queue q_avim(qSize_avim);
  // const unsigned long qSize_m = sizeof(double[11]);
  // c_queue dh_m(qSize_m);
  c_double_handshake dh_m;


  // memory  
  double avim[H_IMG_HEIGHT][H_IMG_WIDTH];
  double h[H_IMG_HEIGHT][H_IMG_WIDTH];
  double ms[H_IMG_HEIGHT][H_IMG_WIDTH];

  // variables
  int k;

  /*
   * read()
     * @input: ScanBuffer -- a single low-res image.
     * @input: HLP -- parameters that related to the image.
     * @output: dh_m -- a double handshake channel used to send M[11].
     * @output: q_pixel -- an channel used to send each pixel.
   */
  Read read(ScanBuffer, HLG, dh_m, q_pixel);
 
  /*
   * getNewPixel()
     * @input: dh_m -- a double handshake channel used to receive M[11].
     * @input: q_pixel -- a channel used to receive each pixel.
     * @output: avim -- original average image.
     * @output: h
     * @output: ms
   */
  GetNewPixel getNewPixel(dh_m, q_pixel, avim, h, ms);

  /*
   * genAvim()
     * @input: avim -- used to store the average image.
     * @input: ms -- used to store the scaler.
   */
  GenAvim genAvim(avim, ms);

  /*
   * write()
   * @inupt: avim -- used to store the average image.
   * @output: q_bmp -- a queue channel used to send image data
   */
  Write write(avim, q_bmp);   


#ifdef DEBUG_AVIM
  int i, j;
#endif

  void main(void) {

    while(k < K) {
      start.receive();
#ifdef DEBUG_H_START
  printf("Receive start signal.\n");
#endif
      par {
        read.main();
        getNewPixel.main();
      };

      k++;
    }
#ifdef DEBUG_B_READ_GETNEWPIXEL_DONE
  printf("Let's generate the image.\n");
#endif

#ifdef DEBUG_AVIM

 for (i = 0; i < H_IMG_HEIGHT; i++) {
    for (j = 0; j < H_IMG_WIDTH; j++) {
      printf("%f ", avim[i][j]);
    }
    printf("\n\n");
  }

#endif

    // after all the images are processed
    // generate an average image
    genAvim.main();
    
    write.main();
  }
};
