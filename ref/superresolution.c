/********************************************************
 * File Name: superresolution.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-28 00:32]
 * Last Modified: [2012-12-01 19:01]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/
#include "superresolution.h"
#include "ReadBmp_aux.h"
#include "getAvImg.h"
#include "read.h"
#include "write.h"

#include <stdio.h>

int main() {

  int bit64 = 0;

  if (sizeof (void *) == 8 ) {
    printf("The program does not suport generating BMP file in 64 bit system.\n");
    bit64 = 1;    
  }

  // initialization
  ImagePack o;
  double avim[H_IMG_HEIGHT][H_IMG_WIDTH];
  unsigned char ScanBuffer[K][L_IMG_HEIGHT][L_IMG_WIDTH];

  // read images in
  ReadBmp(ScanBuffer);   

  // design
  read(ScanBuffer, o);

  getAvImg(o, avim);    


#ifdef DEBUG_SCANBUFFER

  int i, j;
  for (i = 0; i < L_IMG_HEIGHT; i++) {
    for (j = 0; j < L_IMG_WIDTH; j++) {
      printf("%u ", ScanBuffer[0][i][j]);
    }
    printf("\n\n");
  }

#endif

#ifdef DEBUG_O

  int i, j;
  for (i = 0; i < L_IMG_HEIGHT; i++) {
    for (j = 0; j < L_IMG_WIDTH; j++) {
      printf("%f ", o[0].im[i][j]);
    }
    printf("\n\n");
  }

#endif

#ifdef DEBUG_AVIM

  int i, j;
  for (i = 0; i < H_IMG_HEIGHT; i++) {
    for (j = 0; j < H_IMG_WIDTH; j++) {
      printf("%f ", avim[i][j]);
    }
    printf("\n\n");
  }

#endif

  if (!bit64) {
    // write
    imgGen(avim);
  }

  return 0;
}
