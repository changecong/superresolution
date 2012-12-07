/********************************************************
 * File Name: getnewpixel.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:58]
 * Last Modified: [2012-12-07 01:13]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"


import "i_receive";
import "i_receiver";
import "i_send";

import "getLambdaGauss";
import "avimFromoN10Gauss";


behavior GetNewPixel(i_receiver dh_m, i_receiver q_pixel, 
                     inout double avim[H_IMG_HEIGHT][H_IMG_WIDTH],
                     inout double h[H_IMG_HEIGHT][H_IMG_WIDTH],
                     inout double ms[H_IMG_HEIGHT][H_IMG_WIDTH])
{

  double nuv, nuh, b11, b12, b22, deltav, deltah;
  int x, y, iter;
  int counter = 0;
  double M[11] = {0.0};

  GetLambdaGauss getLambdaGauss(M, x, y,
                                nuv, nuh, b11, b12, b22, deltav, deltah);

  AvimFromoN10Gauss avimFromoN10Gauss(nuv, nuh, b11, b12, b22, deltav, deltah,
                                      q_pixel, avim, h, ms);


  void main(void) {

    dh_m.receive(M, sizeof(double[11]));

    for(y = 0; y < L_IMG_HEIGHT; y++) {
      for(x = 0; x < L_IMG_WIDTH; x++) { 

        getLambdaGauss.main();
        avimFromoN10Gauss.main();
      }
    }   
  }
};

