/********************************************************
 * File Name: file.c
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-01 15:02]
 * Last Modified: [2012-12-01 19:45]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "file.h"


int flag = 0;

typedef short WORD;
typedef long DWORD;
typedef char BYTE;

#pragma pack(push, 1)

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

#pragma pack(pop)

  FILE* f;
  BITMAPFILEHEADER BmpFileHeader;
  BITMAPINFOHEADER BmpInfoHeader;
  RGBTRIPLE BmpColors[256];

void initBMPHeader()  
{  

 long width = INT_H_IMG_WIDTH;
 long height = INT_H_IMG_HEIGHT;
 

 BmpFileHeader.bfType = 0x4d42; 

 // size
 BmpFileHeader.bfSize = width * height * 3 + sizeof(BITMAPFILEHEADER)+sizeof(BITMAPINFOHEADER);
 BmpFileHeader.bfReserved1 = 0;
 BmpFileHeader.bfReserved2 = 0;
 BmpFileHeader.bfOffBits = sizeof(BITMAPFILEHEADER)+sizeof(BITMAPINFOHEADER);
   
 BmpInfoHeader.biSize = sizeof(BITMAPINFOHEADER);
 BmpInfoHeader.biWidth = width;
 BmpInfoHeader.biHeight = height;
 BmpInfoHeader.biPlanes = 1;
 BmpInfoHeader.biBitCount = 24;

 BmpInfoHeader.biCompression = 0;
 BmpInfoHeader.biSizeImage = width * height * 3;
 BmpInfoHeader.biXPelsPerMeter = 0;
 BmpInfoHeader.biYPelsPerMeter = 0;
 BmpInfoHeader.biClrUsed = 0;
 BmpInfoHeader.biClrImportant = 0;

#ifdef DEBUG_BMP_OP

  printf("bfType: %d\n", BmpFileHeader.bfType);
  printf("bfSize: %ld\n", BmpFileHeader.bfSize);
  printf("bfReserved1: %d\n", BmpFileHeader.bfReserved1);
  printf("bfReserved2: %d\n", BmpFileHeader.bfReserved2);
  printf("bfOffBits: %ld\n", BmpFileHeader.bfOffBits);

  printf("biSize: %ld\n", BmpInfoHeader.biSize);
  printf("biWidth: %ld\n", BmpInfoHeader.biWidth);
  printf("biHeight: %ld\n", BmpInfoHeader.biHeight);
  printf("biPlanes: %d\n", BmpInfoHeader.biPlanes);
  printf("biBitCount: %d\n", BmpInfoHeader.biBitCount);

  printf("biCompression: %ld\n", BmpInfoHeader.biCompression);
  printf("biSizeImage: %ld\n", BmpInfoHeader.biSizeImage);
  printf("biXPelsPerMeter: %ld\n", BmpInfoHeader.biXPelsPerMeter);
  printf("biYPelsPerMeter: %ld\n", BmpInfoHeader.biYPelsPerMeter);
  printf("biClrUsed: %ld\n", BmpInfoHeader.biClrUsed);
  printf("biClrImportant: %ld\n", BmpInfoHeader.biClrImportant);

#endif
}  

void initColorMap()
{
  int i;

  for (i = 0; i < 256; i++) { 
	BmpColors[i].B = BmpColors[i].G = BmpColors[i].R = i/256;
  }
}

void FileWrite(unsigned char *bytes, unsigned long num)
{
  if(!f) {
     f=fopen("test.bmp","wb");
  }
  if(!f) {
      fprintf(stderr, "Cannot open output file %s\n", "test.bmp");
  }

  if (!flag) {

    initBMPHeader();
    fwrite(&BmpFileHeader, sizeof(BITMAPFILEHEADER), 1, f);
    fwrite(&BmpInfoHeader, sizeof(BITMAPINFOHEADER), 1, f);

/*    initColorMap();
    fwrite(BmpColors, sizeof(RGBTRIPLE)*256, 1, f);
*/
    flag++;
  }

  if (num < 5) {
    fclose(f);
    f = NULL;
    printf ("BMP file written successfully!\n");
    exit(1);
  }

#ifdef DEBUG_BYTES
  int i;
  for (i = 0; i < num; i++) {
    printf("%u ", bytes[i]);
  }
  printf("\n\n");

#endif

  if (fwrite(bytes, sizeof(char), num, f) != num) {
      fprintf(stderr, "Error writing output file %s\n", "test.bmp");
      fclose(f);
      exit(1);
  }
}
