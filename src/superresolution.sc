/********************************************************
 * File Name: superresolution.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:23]
 * Last Modified: [2012-12-06 14:15]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"
#include <stdlib.h>

import "stimulus";
import "design";
import "monitor";

import "c_handshake";
import "c_queue";

behavior Main {

  unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH];
  double HLG[12];
  c_handshake start;
  const unsigned long qSize = sizeof(char[H_IMG_HEIGHT*4]);
  c_queue q_bmp(qSize);

  Stimulus stimulus(ScanBuffer, HLG, start);
  Design design(ScanBuffer, HLG, start, q_bmp);
  Monitor monitor(q_bmp);

  int main(void) {

    
   
      stimulus.main();
      design.main();
      monitor.main();
    
    return 0;
 
  }
};
