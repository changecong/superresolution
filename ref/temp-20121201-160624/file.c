/********************************************************
 * File Name: file.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-01 15:02]
 * Last Modified: [2012-12-01 16:03]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "file.h"

int flag = 0;

typedef short WORD;
typedef long DWORD;
typedef char BYTE;

typedef struct tagBITMAPFILEHEADER {
	WORD bfType;
	DWORD bfSize;
	WORD bfReserved1;
	WORD bfReserved2;
	DWORD bfOffBits;
} BITMAPFILEHEADER;

typedef struct tagBITMAPINFOHEADER {
	/* BITMAP core header info -> OS/2 */
	DWORD biSize;
	DWORD biWidth;
	DWORD biHeight;
	WORD biPlanes;
	WORD biBitCount;

	/* BITMAP info -> Windows 3.1 */
	DWORD biCompression;
	DWORD biSizeImage;
	DWORD biXPelsPerMeter;
	DWORD biYPelsPerMeter;
	DWORD biClrUsed;
	DWORD biClrImportant;
} BITMAPINFOHEADER;

typedef struct tagRGBTRIPLE {
	BYTE B, G, R;
} RGBTRIPLE;


  FILE* f;
  BITMAPFILEHEADER BmpFileHeader;
  BITMAPINFOHEADER BmpInfoHeader;
  RGBTRIPLE *BmpColors;

void initBMPHeader()  
{  
 
 BmpFileHeader.bfType = 0x4d42;  
 BmpFileHeader.bfSize = INT_H_IMG_HEIGHT * INT_H_IMG_WIDTH + 54;
 BmpFileHeader.bfReserved1 = 0x0000;
 BmpFileHeader.bfReserved2 = 0x0000;
 BmpFileHeader.bfOffBits = 54;
   
 BmpInfoHeader.biSize = sizeof(BmpInfoHeader);
 BmpInfoHeader.biWidth = INT_H_IMG_WIDTH;
 BmpInfoHeader.biHeight = INT_H_IMG_HEIGHT;
 BmpInfoHeader.biPlanes = 1;
 BmpInfoHeader.biBitCount = 8;

 BmpInfoHeader.biCompression = 0;
 BmpInfoHeader.biSizeImage = INT_H_IMG_HEIGHT * INT_H_IMG_WIDTH;
 BmpInfoHeader.biXPelsPerMeter = 0;
 BmpInfoHeader.biYPelsPerMeter = 0;
 BmpInfoHeader.biClrUsed = 0;
 BmpInfoHeader.biClrImportant = 0;

}  

void FileWrite(unsigned int *bytes, unsigned long num)
{
  if(!f) {
     f=fopen("test.bmp","wb");
  }
  if(!f) {
      fprintf(stderr, "Cannot open output file %s\n", "bmp.jpg");
  }

  if (!flag) {

    initBMPHeader();
    fwrite(&BmpFileHeader, sizeof(BmpFileHeader), 1, f);
    fwrite(&BmpInfoHeader, sizeof(BmpInfoHeader), 1, f);

    flag++;
  }

  if (num < 5) {
    fclose(f);
    f = NULL;
    printf ("BMP file written successfully!\n");
  }

  if (fwrite(bytes, sizeof(int), num, f) != num) {
      fprintf(stderr, "Error writing output file %s\n", "bmp.jpg");
      fclose(f);
      exit(1);
  }

}
