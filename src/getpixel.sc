/********************************************************
 * File Name: getpixel.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:08]
 * Last Modified: [2012-12-08 16:31]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

import "i_sender";
import "i_receive";

#include "superresolution.sh"

behavior GetPixel(i_receive start_p, in unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH], i_sender q_pixel)
{

  int i, j;
  double pixel[1];
  int k;

  void main(void) {

    for (k = 0; k < K; k++) {

      start_p.receive();
#ifdef DEBUG_START
  printf("receive start_p\n");
#endif
      for (i = 0; i < L_IMG_HEIGHT; i++) {
        for (j = 0; j < L_IMG_WIDTH; j++) {

          pixel[0] = ((double)ScanBuffer[i][j]/255) - 0.5;

#ifdef DEBUG_Q_PIXEL
  printf("send: %f\n", pixel[0]);
#endif
          q_pixel.send(pixel, sizeof(double));  // one pixel each time.
        }
      }
    }
  }
};
