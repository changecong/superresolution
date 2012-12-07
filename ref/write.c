/********************************************************
 * File Name: write.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-01 14:30]
 * Last Modified: [2012-12-01 19:54]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "write.h"
#include "file.h"
#include <stdio.h>

typedef unsigned char uint8;

int gap = GAP;
uint8 buffer[1024];
uint8 end[1] = {0};

int buff_ptr = 0;

void imgGen(double avim[H_IMG_HEIGHT][H_IMG_WIDTH])
{
  uint8 newAvIm[INT_H_IMG_HEIGHT][INT_H_IMG_WIDTH];
  int i, j, l; 
  double temp;

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

  // write to a bmp file
  for (i = INT_H_IMG_HEIGHT - 1; i >= 0; i--) {
    for (j = 0; j < INT_H_IMG_WIDTH; j++) {
      for (l = 0; l < 3; l++) {
        buffer[buff_ptr++] = newAvIm[i][j];
      }
    }
    FileWrite(buffer, buff_ptr);
    buff_ptr = 0;
  }
  
  FileWrite(end, 1);
}

