/********************************************************
 * File Name: getnewpixel.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:58]
 * Last Modified: [2012-12-08 12:59]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"


import "i_receive";
import "i_receiver";
import "i_send";
import "c_queue";

import "getLambdaGauss";
import "avimFromoN10Gauss";


behavior GetNewPixel(i_receiver dh_m, i_receiver q_pixel, 
                     inout double avim[H_IMG_HEIGHT][H_IMG_WIDTH],
                     inout double h[H_IMG_HEIGHT][H_IMG_WIDTH],
                     inout double ms[H_IMG_HEIGHT][H_IMG_WIDTH])
{

  const unsigned long qSize = sizeof(double[128]);
  c_queue q_nuv(qSize);
  c_queue q_nuh(qSize);
  c_queue q_b11(qSize);
  c_queue q_b12(qSize);
  c_queue q_b22(qSize);
  c_queue q_deltav(qSize);
  c_queue q_deltah(qSize);

//  double nuv, nuh, b11, b12, b22, deltav, deltah;
  int counter = 0;
  double M[11] = {0.0};

  GetLambdaGauss getLambdaGauss(dh_m, q_nuv, q_nuh, q_b11, q_b12, q_b22, q_deltav, q_deltah);

  AvimFromoN10Gauss avimFromoN10Gauss(q_nuv, q_nuh, q_b11, q_b12, q_b22, q_deltav, q_deltah,
                                      q_pixel, avim, h, ms);


  void main(void) {

//    dh_m.receive(M, sizeof(double[11]));

    par {
      getLambdaGauss.main();
      avimFromoN10Gauss.main();
    }
  }
};

