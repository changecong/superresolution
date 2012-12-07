/********************************************************
 * File Name: getAvImg.h
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-30 21:13]
 * Last Modified: [2012-11-30 21:58]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#ifndef GETAVIMG_H
#define GETAVIMG_H

#include "superresolution.h"

void getLambdaGauss(const double*, const double, const double, double*, double*, double*, double*, double*, double*, double*);

void getAvImg(ImagePack, double avim[H_IMG_HEIGHT][H_IMG_WIDTH]);

void avimFromoN10Gauss(double avim[H_IMG_HEIGHT][H_IMG_WIDTH], double M[K][11], ImagePack);


#endif

