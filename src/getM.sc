/********************************************************
 * File Name: getM.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 13:18]
 * Last Modified: [2012-12-06 14:16]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"

import "i_send";
import "i_sender";


behavior GetM(in double HLG[12], i_sender dh_m, i_send start_av)
{

  // initialization
  int j, mptr = 0;  // iterator and pointer
  double M[11] = {0.0};

  void main(void) {
    // calculate M

    // HLP[0-8] are H
    for (j = 0; j < 8; j++) {
      M[mptr++] = HLG[j] / HLG[8]; 
    }

    // HLG[9-11] = [la, lb, g]
    M[mptr++] = HLG[++j];
    M[mptr++] = HLG[++j];
    M[mptr++] = pow(HLG[++j], 2);

    mptr = 0;
    
    dh_m.send(M, sizeof(double)*11);
#ifdef DEBUG_DH_M
  printf("M[11] is send\n");
#endif
    
//    start_av.send();
  }

};
