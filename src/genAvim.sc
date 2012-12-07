/********************************************************
 * File Name: genAvim.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-05 18:55]
 * Last Modified: [2012-12-06 11:45]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "superresolution.sh"


import "i_receive";



behavior GenAvim(inout double av[H_IMG_HEIGHT][H_IMG_WIDTH], 
                 inout double ms[H_IMG_HEIGHT][H_IMG_WIDTH],
                 i_receive start_gen)
{
  int hw, hh;

  void main(void) {

    while(1) {

      start_gen.receive();

      for (hw = 0; hw < H_IMG_WIDTH; hw++) {
        for (hh = 0; hh < H_IMG_HEIGHT; hh++) {
          if (ms[hh][hw] > 0.0000001) {
            av[hh][hw] = av[hh][hw]/ms[hh][hw];
            ms[hh][hw] = 0;
          }
          else {
            av[hh][hw] = 0;
            ms[hh][hw] = 1;
          }
        }
      }
    }
  }
};
