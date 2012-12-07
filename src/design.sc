/********************************************************
 * File Name: design.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:43]
 * Last Modified: [2012-12-06 19:39]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <stdio.h>
#include "superresolution.sh"

import "i_receive";

import "c_queue";
import "c_handshake";
import "c_double_handshake";

import "read";
import "getnewpixel";
import "genAvim";
import "write";

behavior Design(in unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH],
                in double HLG[12], i_receive start, i_sender q_bmp)
{

  // channels
  c_handshake start_av;
  c_handshake start_gen;
  const unsigned long qSize_pixel = sizeof(double[128]);
  c_queue q_pixel(qSize_pixel);
  const unsigned long qSize_avim = sizeof(char[H_IMG_WIDTH]);
  c_queue q_avim(qSize_avim);
  const unsigned long qSize_m = sizeof(double[11]);
  c_queue dh_m(qSize_m);
  //c_double_handshake dh_m;


  // memory  
  double avim[H_IMG_HEIGHT][H_IMG_WIDTH];
  double h[H_IMG_HEIGHT][H_IMG_WIDTH];
  double ms[H_IMG_HEIGHT][H_IMG_WIDTH];


  /*
   * read()
     * @input: ScanBuffer -- a single low-res image.
     * @input: HLP -- parameters that related to the image.
     * @input: start -- an event that tell getPixil to start.
     * @output: dh_m -- a double handshake channel used to send M[11].
     * @output: start_av -- an event that tell getNewPixel to start.
     * @output: q_pixel -- an channel used to send each pixel.
   */
  Read read(ScanBuffer, HLG, start, dh_m, start_av, q_pixel);
 
  /*
   * getNewPixel()
     * @input: dh_m -- a double handshake channel used to receive M[11].
     * @input: start_av -- an event that used to issue this behavior.
     * @input: q_pixel -- a channel used to receive each pixel.
     * @output: start_gen -- an event that tell getAvim to start.
     * @output: avim -- original average image.
     * @output: h
     * @output: ms 
   */
  GetNewPixel getNewPixel(dh_m, start_av, q_pixel, start_gen,
                          avim, h, ms);

  /*
   * genAvim()
     * @input: avim -- used to store the average image.
     * @input: ms -- used to store the scaler.
     * @output: start_gen -- tell the write to generate image.
   */
  GenAvim genAvim(avim, ms, start_gen);

  /*
   * write()
   * @inupt: avim -- used to store the average image.
   * @output: q_bmp -- a queue channel used to send image data
   */
  Write write(avim, q_bmp);   

  void main(void) {

    
    par {
    
        read.main();
        getNewPixel.main();
    };
    // after all the images are processed
    // generate an average image
    genAvim.main();
    
    write.main();
  }
};
