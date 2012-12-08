/********************************************************
 * File Name: superresolution.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:23]
 * Last Modified: [2012-12-08 17:02]
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

behavior TestBench {

  unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH];
  double HLG[12];
  c_handshake start_m;
  c_handshake start_p;
  const unsigned long qSize = sizeof(char[20400]);
  c_queue q_bmp(qSize);

  Stimulus stimulus(ScanBuffer, HLG, start_m, start_p);
  Design design(ScanBuffer, HLG, start_m, start_p, q_bmp);
  Monitor monitor(q_bmp);

  void main(void) {

    par {  
      stimulus.main();
      design.main();
      monitor.main();    
    } 
  }
};

behavior Main {

  TestBench testBench;

  int main(void) {
    testBench.main();

    return 0;
  }
};
