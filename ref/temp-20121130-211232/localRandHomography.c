/********************************************************
 * File Name: localRandHomography.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-30 11:34]
 * Last Modified: [2012-11-30 12:25]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <time.h>
#include <stdlib.h>

/* localRandHomography()
 * the function used to return a 3x3 matrix of H
   * @para: h[3][3]  used to store homography
   * @para: lrh, lrw  low-resolution image hight and width
   * @para: hrh, hrw  high-resolution image hight and width
   * @para: zm  zoom scale
 */
void localRandHomography(double H[3][3], int lrh, int lrw, int hrh, int hrw, int zm)
{
  double T[3][3] = {1/(lrw-1), 0.0, 0,0;
                    0.0, 1/(lrh-1), 0.0;
                    0.0, 0.0, 1};
  
  // shift low-res image coords to be zero-centered
  double lrshift[3][3] = {1.0, 0.0, 0.5*(lrw-1);
                          0.0, 1.0, 0.5*(lrh-1); 
                          0.0, 0.0, 1.0};

  // shift high-res image coords to be zero-centered
  double hrshift[3][3] = {1.0, 0.0, 0.5*(hrw-1);
                          0.0, 1.0, 0.5*(hrh-1);
                          0.0, 0.0, 0.1};

  double p1[3][4] = {-0.5, 0.5, 0.5, -0.5;
                     -0.5, -0.5, 0.5, 0.5;
                     1, 1, 1, 1};

  // get the rand number seed
  srand(time(NULL));
  double p2[3][4] = {RNDP2, RNDP2, RANDP2, RANDP2;
                     RNDP2, RNDP2, RANDP2, PANDP2;
                     1, 1, 1, 1};

  // S
  double S[8][9] = {0.0};
  createS(S);

}


void createS(double S[8][9]) {


}

void 





