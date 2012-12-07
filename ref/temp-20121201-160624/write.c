/********************************************************
 * File Name: write.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-01 14:30]
 * Last Modified: [2012-12-01 16:01]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "write.h"
#include "file.h"
#include <stdio.h>

int gap = GAP;
unsigned int buffer[512];
unsigned int end[1];
int buff_ptr = 0;

void imgGen(double avim[H_IMG_HEIGHT][H_IMG_WIDTH])
{
  int newAvIm[INT_H_IMG_HEIGHT][INT_H_IMG_WIDTH];
  int i, j; 
  double temp;

  for (i = gap; i < H_IMG_HEIGHT - gap; i++) {

    for (j = gap; j < H_IMG_WIDTH - gap; j++) {

      temp = (avim[i][j] + 0.5) * 255;

      newAvIm[i - gap][j - gap] = (int)temp;

#ifdef DEBUG_INTAVIM
      printf("%d ", newAvIm[i - gap][j - gap]);
#endif
    }
#ifdef DEBUG_INTAVIM
    printf("\n\n");
#endif

  }

  // write to a bmp file
  for (i = 0; i < INT_H_IMG_HEIGHT; i++) {
    for (j = 0; j < INT_H_IMG_WIDTH; j++) {

      buffer[buff_ptr++] = newAvIm[i][j];
    }
    FileWrite(buffer, buff_ptr);
    buff_ptr = 0;
  }
  
  FileWrite(end, 1);
}

