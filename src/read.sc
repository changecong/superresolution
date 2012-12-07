/********************************************************
 * File Name: read.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 14:17]
 * Last Modified: [2012-12-06 19:39]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"

import "i_receive";
import "i_send";
import "i_sender";

import "getM";
import "getpixel";

behavior Read(in unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH],
              in double HLG[12], i_receive start, i_sender dh_m,
              i_send start_av, i_sender q_pixel)
{

  /* getM
     * @input: HLP[12] -- parameters which come with images.
     * @output: dh_m -- a double handshake channel used to send M[11]
     * @output: start_av -- a signal tell avim to work.
   */
  GetM getM(HLG, dh_m, start_av);
 
  /* getPixel
     * @input: one image.
     * @output: a queue used to send each pixel.
   */
  GetPixel getPixel(ScanBuffer, q_pixel);

  void main(void) {

    while(1) {

      start.receive();  // means one image is ready.

        
        getM.main();
        getPixel.main();
     
    }
  }
};
