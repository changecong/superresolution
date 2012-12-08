/********************************************************
 * File Name: getM.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 13:18]
 * Last Modified: [2012-12-08 16:31]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"

import "i_send";
import "i_sender";
import "i_receive";

behavior GetM(i_receive start_m, in double HLG[12], i_sender dh_m)
{

  // initialization
  int j, mptr = 0, m;  // iterator and pointer
  double M[11] = {0.0};
  int k;

  void main(void) {

    for (k = 0; k < K; k++) {
  
      start_m.receive();
#ifdef DEBUG_START
  printf("receive start_m\n");
#endif

      // calculate M

      // HLP[0-8] are H
      for (j = 0; j < 8; j++) {
        M[mptr++] = HLG[j] / HLG[8]; 
      }

      // HLG[9-11] = [la, lb, g]
      M[mptr++] = HLG[++j];
      M[mptr++] = HLG[++j];
      M[mptr++] = pow(HLG[++j], 2);

#ifdef DEBUG_M
    for (m = 0; m < 11; m++) {
      printf("%f ", M[m]);
    }
    printf("\n");
#endif

      mptr = 0;
    
      dh_m.send(M, sizeof(double[11]));
#ifdef DEBUG_DH_M
  printf("M[11] is send\n");
#endif
    }

  }
};
