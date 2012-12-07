/********************************************************
 * File Name: file.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-01 04:36]
 * Last Modified: [2012-12-01 07:52]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "file.h"

FILE *f = NULL;

// write number of bytes to file  
void FileWrite(unsigned int *bytes, unsigned long num)
{
 
  if(!f) {
     f=fopen("highres.bmp","wb");
  }
  if(!f) {
    fprintf(stderr, "Cannot open output file %s\n", "highres.jpg");
  }

  if (fwrite(bytes,sizeof(int),num,f) != num) {
    fprintf(stderr, "Error writing output file %s\n", "highres.jpg");
    fclose(f);
    exit(1);
  }

  if (bytes[num-2] == 0xff && bytes[num-1] == 0xd9) {
    fclose(f);
    f = NULL;
    printf ("JPEG file written successfully!\n");
  }
}

