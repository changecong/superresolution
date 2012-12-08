/********************************************************
 * File Name: superresolution.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:23]
 * Last Modified: [2012-12-08 16:08]
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
  c_handshake start_m;
  c_handshake start_p;
  const unsigned long qSize = sizeof(char[20400]);
  c_queue q_bmp(qSize);

  Stimulus stimulus(ScanBuffer, HLG, start_m, start_p);
  Design design(ScanBuffer, HLG, start_m, start_p, q_bmp);
  Monitor monitor(q_bmp);

  int main(void) {

  par {  
    stimulus.main();
    design.main();
    monitor.main();    
  }
    return 0;
 
  }
};
