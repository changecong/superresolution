/********************************************************
 * File Name: superresultion.h
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-30 13:03]
 * Last Modified: [2012-12-07 01:26]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/


#ifndef SUPERRES_H
#define SUPERRES_H

#include <math.h>

// debug
// DEBUG_NONE _M _LAMB _AVIM _O _SCANBUFFER _INTAVIM _BMP  _BMP_OP _BYTES
#define DEBUG_M

#define K 5  // number of low-res images

// data

#define ZOOM 2

#define H_IMG_WIDTH 256
#define H_IMG_HEIGHT 256

#define H_IMG_SIZE H_IMG_WIDTH*H_IMG_HEIGHT

#define L_IMG_WIDTH (int)(H_IMG_WIDTH*0.8)/ZOOM
#define L_IMG_HEIGHT (int)(H_IMG_HEIGHT*0.8)/ZOOM

#define GAP 26
#define INT_H_IMG_WIDTH H_IMG_WIDTH-2*GAP
#define INT_H_IMG_HEIGHT H_IMG_HEIGHT-2*GAP

// convert pixel to MDU 
#define MDU(pixel) ((pixel+7)>>3)

#define IMG_1_LA 1.052006010145546
#define IMG_1_LB -7.854059467661992e-04
#define IMG_1_H {2.071818534502559, 0.006640572973319, 25.829368377421353, \
                 0.013936322398215, 1.826990801699942, 30.207254544365280, \
                 8.066747900069584e-04, -8.873312965501025e-04, 1.0000000000000000}

#define IMG_2_LA 0.970624640226458
#define IMG_2_LB 0.033252009554429
#define IMG_2_H {1.659974667742252, -0.098506251303128, 35.594050468141080, \
                 0.069575593586513, 1.661377408156428, 14.771071649972463, \
                 -5.032690145746029e-04, -0.001525361999678, 1.0000000000000000}

#define IMG_3_LA 0.866799557868475
#define IMG_3_LB -0.091367339443336
#define IMG_3_H {1.778627465231991, -0.048946103740283, 34.781460873223914, \
                 -0.007369980886664, 1.841535864439940, 21.449767710353864, \
                 -2.807903695422428e-04, -5.567181875660170e-04, 1.0000000000000000}

#define IMG_4_LA 0.863830552912925
#define IMG_4_LB 0.017844296331150
#define IMG_4_H {1.925359954425408, -0.210562556155483, 34.614151124845620, \
                 -0.089429764715089, 1.619326330505545, 28.784503558132574, \
                 2.494311125416592e-04, -0.001567755416489, 1.0000000000000000}

#define IMG_5_LA 0.980477880210125
#define IMG_5_LB -0.008533582358557
#define IMG_5_H {1.794836048032307, -0.009758384722480, 31.027471296929694, \
                 -0.107471795785257, 1.991174076385839, 25.089913008752706, \
                 -6.654110862234392e-04, 3.287123369369304e-04, 1.0000000000000000}

// extern unsigned char ScanBuffer[K][L_IMG_HEIGHT][L_IMG_WIDTH];

// image structure
typedef struct Image {
  double g;  // standard divation of Gaussian point-spread function
//  unsigned int w;  // low-res image width
//  unsigned int h;  // low-res image height 
  double la;  // multiplicative photometric parameter (lambda_alpha)
  double lb;  // additive photometric parameter (lambda_beta)
  double H[9];  // homography
  double im[L_IMG_HEIGHT][L_IMG_WIDTH];
} Image;

typedef struct Image ImagePack[K];

#endif
