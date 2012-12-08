/********************************************************
 * File Name: design.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:43]
 * Last Modified: [2012-12-08 16:25]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <stdio.h>
#include "superresolution.sh"

import "i_receive";

import "c_queue";
import "c_handshake";
import "c_double_handshake";

import "readpixel";
import "genAvim";
import "write";

behavior Design(in unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH],
                in double HLG[12], i_receive start_m, i_receive start_p,
                i_sender q_bmp)
{

  // memory  
  double avim[H_IMG_HEIGHT][H_IMG_WIDTH];
  double h[H_IMG_HEIGHT][H_IMG_WIDTH];
  double ms[H_IMG_HEIGHT][H_IMG_WIDTH];

  ReadPixel readPixel(start_m, start_p, ScanBuffer, HLG, avim, h, ms);

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

    readPixel.main();

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
