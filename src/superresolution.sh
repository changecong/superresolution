/******************************************************
 * File Name: superresolution.sh
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:21]
 * Last Modified: [2012-12-06 14:21]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *****************************************************/

#ifndef SUPERRES_H
#define SUPERRES_H

#include <math.h>
#include <stdio.h>

// debug
// DEBUG_NONE 
// for channels: _Q_PIXEL _DH_M 
// for behaviors: _B_GETNEWPIXEL
#define DEBUG_DH_M

#define K 5  // number of low-res images

// data

#define ZOOM 2

#define H_IMG_WIDTH 256
#define H_IMG_HEIGHT 256

#define L_IMG_WIDTH (int)(H_IMG_WIDTH*0.8)/ZOOM
#define L_IMG_HEIGHT (int)(H_IMG_HEIGHT*0.8)/ZOOM

#endif
