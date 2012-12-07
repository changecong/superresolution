/********************************************************
 * File Name: read.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-28 18:42]
 * Last Modified: [2012-12-01 03:30]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "read.h"

double LA[K] = {IMG_1_LA, IMG_2_LA, IMG_3_LA, IMG_4_LA, IMG_5_LA};
double LB[K] = {IMG_1_LB, IMG_2_LB, IMG_3_LB, IMG_4_LB, IMG_5_LB};

double H[K][9] = {IMG_1_H, IMG_2_H, IMG_3_H, IMG_4_H, IMG_5_H};


void read(unsigned char ScanBuffer[K][L_IMG_HEIGHT][L_IMG_WIDTH], ImagePack o)
{

  int k, lrh, lrw;
  int i;
  double gam = 0.4;
  for (k = 0; k < K; k++) {
    // 
    for (lrh = 0; lrh < L_IMG_HEIGHT; lrh++) {
      for (lrw = 0; lrw < L_IMG_WIDTH; lrw++) {
        o[k].im[lrh][lrw] = ((double)ScanBuffer[k][lrh][lrw]/255) - 0.5;
      }
    }

    o[k].g = gam;
    o[k].la = LA[k];
    o[k].lb = LB[k];
    
    for (i = 0; i < 9; i++) {
      o[k].H[i] = H[k][i];
    }
  }
}
