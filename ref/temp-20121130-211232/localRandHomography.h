/********************************************************
 * File Name: loacalRandHomography.h
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-11-30 12:00]
 * Last Modified: [2012-11-30 12:15]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#ifndef LRHGRAPHY_H
#define LRHGRAPHY_H

#define RNDP2 0.15*((double)rand()/RAND_MAX)-0.5

void localRandHomography(double**, int, int, int, int, int);

#endif

