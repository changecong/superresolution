/******************************************************
 * File Name: superresolution.sh
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:21]
 * Last Modified: [2012-12-07 02:04]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *****************************************************/

#ifndef SUPERRES_H
#define SUPERRES_H

#include <math.h>
#include <stdio.h>

// debug
// DEBUG_NONE 
// debug channels: _Q_PIXEL _DH_M _H_START _Q_BMP
// debug behaviors: _B_GETNEWPIXEL _B_ONEIMAGEDONE 
//                  _B_READ_GETNEWPIXEL_DONE
//#define DEBUG_H_START
//#define DEBUG_DH_M
//#define DEBUG_Q_BMP
// #define DEBUG_Q_PIXEL
// #define DEBUG_B_GETNEWPIXEL
//#define DEBUG_B_ONEIMAGEDONE
//#define DEBUG_B_READ_GETNEWPIXEL_DONE

// DEBUG_NONE _M _LAMB _AVIM _O _SCANBUFFER _INTAVIM _BMP  _BMP_OP _BYTES
//#define DEBUG_INTAVIM
// #define DEBUG_LAMB
// #define DEBUG_MAP
// #define DEBUG_M
// #define DEBUG_DONE
// #define DEBUG_INTAVIM
// #define DEBUG_BMP_OP

#define K 5  // number of low-res images

// data

#define ZOOM 2

#define H_IMG_WIDTH 256
#define H_IMG_HEIGHT 256

#define L_IMG_WIDTH (int)(H_IMG_WIDTH*0.8)/ZOOM
#define L_IMG_HEIGHT (int)(H_IMG_HEIGHT*0.8)/ZOOM

#endif
