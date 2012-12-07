/********************************************************
 * File Name: write.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:45]
 * Last Modified: [2012-12-07 01:51]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "write.sh"

import "i_sender";

behavior Write(in double avim[H_IMG_HEIGHT][H_IMG_WIDTH],
               i_sender q_bmp)
{
  typedef unsigned char uint8;

  int gap = GAP;
  uint8 buffer[1024];
  uint8 end[4] = "eof";
  int buff_ptr = 0;

  uint8 newAvIm[INT_H_IMG_HEIGHT][INT_H_IMG_WIDTH];
  int i, j, l; 
  double temp;  

  void main(void) {

    // re-range and re-size the image
    for (i = gap; i < H_IMG_HEIGHT - gap; i++) { 
      for (j = gap; j < H_IMG_WIDTH - gap; j++) {

        temp = (avim[i][j] + 0.5) * 255;
        newAvIm[i - gap][j - gap] = (uint8)temp;
 
#ifdef DEBUG_INTAVIM
  printf("%d ", newAvIm[i - gap][j - gap]);
#endif
      }
#ifdef DEBUG_INTAVIM
  printf("\n\n");
#endif 
    }

    // send into the queue 
    for (i = INT_H_IMG_HEIGHT - 1; i >= 0; i--) {
      for (j = 0; j < INT_H_IMG_WIDTH; j++) {
        for (l = 0; l < 3; l++) {
          buffer[buff_ptr++] = newAvIm[i][j];
        }
      }
      q_bmp.send(buffer, buff_ptr);
#ifdef DEBUG_Q_BMP
  printf("Send one row.\n");
#endif
      buff_ptr = 0;
    }

#ifdef DEBUG_Done
  printf("Done.\n");
#endif
    // send an file end acknowledgement
    q_bmp.send(end, 4);
  }

};
