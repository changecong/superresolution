/********************************************************
 * File Name: readpixel.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-08 16:11]
 * Last Modified: [2012-12-08 16:26]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"

import "i_receive";
import "c_queue";
import "c_double_handshake";

import "read";
import "getnewpixel";

behavior ReadPixel(i_receive start_m, i_receive start_p,
                   in unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH],
                   in double HLG[12], 
                   inout double avim[H_IMG_HEIGHT][H_IMG_WIDTH],
                   inout double h[H_IMG_HEIGHT][H_IMG_WIDTH],
                   inout double ms[H_IMG_HEIGHT][H_IMG_WIDTH])
{


  // channels
  const unsigned long qSize_pixel = sizeof(double[128]);
  c_queue q_pixel(qSize_pixel);
  c_double_handshake dh_m;

  /*
   * read()
     * @input: ScanBuffer -- a single low-res image.
     * @input: HLP -- parameters that related to the image.
     * @output: dh_m -- a double handshake channel used to send M[11].
     * @output: q_pixel -- an channel used to send each pixel.
  */
  Read read(start_m, start_p, ScanBuffer, HLG, dh_m, q_pixel);
 
  /*
   * getNewPixel()
     * @input: dh_m -- a double handshake channel used to receive M[11].
     * @input: q_pixel -- a channel used to receive each pixel.
     * @output: avim -- original average image.
     * @output: h
     * @output: ms
   */
  GetNewPixel getNewPixel(dh_m, q_pixel, avim, h, ms);

  void main(void) {

    par {
      read.main();
      getNewPixel.main();
    }
  }
};
