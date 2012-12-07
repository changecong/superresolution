/********************************************************
 * File Name: monitor.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:56]
 * Last Modified: [2012-12-06 13:02]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include "write.sh" 

import "i_receiver";

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
BITMAPFILEHEADER NewBmpFileHeader;
BITMAPINFOHEADER NewBmpInfoHeader;
//RGBTRIPLE BmpColors[256];

void initBMPHeader()  
{  

 long width = INT_H_IMG_WIDTH;
 long height = INT_H_IMG_HEIGHT;
 

 NewBmpFileHeader.bfType = 0x4d42; 

 // size
 NewBmpFileHeader.bfSize = width * height * 3 + sizeof(BITMAPFILEHEADER)+sizeof(BITMAPINFOHEADER);
 NewBmpFileHeader.bfReserved1 = 0;
 NewBmpFileHeader.bfReserved2 = 0;
 NewBmpFileHeader.bfOffBits = sizeof(BITMAPFILEHEADER)+sizeof(BITMAPINFOHEADER);
   
 NewBmpInfoHeader.biSize = sizeof(BITMAPINFOHEADER);
 NewBmpInfoHeader.biWidth = width;
 NewBmpInfoHeader.biHeight = height;
 NewBmpInfoHeader.biPlanes = 1;
 NewBmpInfoHeader.biBitCount = 24;

 NewBmpInfoHeader.biCompression = 0;
 NewBmpInfoHeader.biSizeImage = width * height * 3;
 NewBmpInfoHeader.biXPelsPerMeter = 0;
 NewBmpInfoHeader.biYPelsPerMeter = 0;
 NewBmpInfoHeader.biClrUsed = 0;
 NewBmpInfoHeader.biClrImportant = 0;

#ifdef DEBUG_BMP_OP

  printf("bfType: %d\n", NewBmpFileHeader.bfType);
  printf("bfSize: %ld\n", NewBmpFileHeader.bfSize);
  printf("bfReserved1: %d\n", NewBmpFileHeader.bfReserved1);
  printf("bfReserved2: %d\n", NewBmpFileHeader.bfReserved2);
  printf("bfOffBits: %ld\n", NewBmpFileHeader.bfOffBits);

  printf("biSize: %ld\n", NewBmpInfoHeader.biSize);
  printf("biWidth: %ld\n", NewBmpInfoHeader.biWidth);
  printf("biHeight: %ld\n", NewBmpInfoHeader.biHeight);
  printf("biPlanes: %d\n", NewBmpInfoHeader.biPlanes);
  printf("biBitCount: %d\n", NewBmpInfoHeader.biBitCount);

  printf("biCompression: %ld\n", NewBmpInfoHeader.biCompression);
  printf("biSizeImage: %ld\n", NewBmpInfoHeader.biSizeImage);
  printf("biXPelsPerMeter: %ld\n", NewBmpInfoHeader.biXPelsPerMeter);
  printf("biYPelsPerMeter: %ld\n", NewBmpInfoHeader.biYPelsPerMeter);
  printf("biClrUsed: %ld\n", NewBmpInfoHeader.biClrUsed);
  printf("biClrImportant: %ld\n", NewBmpInfoHeader.biClrImportant);

#endif
}  


/* I don't need color map
void initColorMap()
{
  int i;

  for (i = 0; i < 256; i++) { 
	BmpColors[i].B = BmpColors[i].G = BmpColors[i].R = i/256;
  }
}

*/


behavior Monitor(i_receiver q_bmp)
{

  unsigned char buffer[INT_H_IMG_WIDTH];


  void main(void) {

    if(!f) {
      f=fopen("test.bmp","wb");
    }
  
    if(!f) {
      fprintf(stderr, "Cannot open output file %s\n", "test.bmp");
    }

    if (!flag) {

      initBMPHeader();
      fwrite(&NewBmpFileHeader, sizeof(BITMAPFILEHEADER), 1, f);
      fwrite(&NewBmpInfoHeader, sizeof(BITMAPINFOHEADER), 1, f);

      flag++;
    }

    // read data from the queue.
    while (1) {

      q_bmp.receive(buffer, INT_H_IMG_WIDTH);

      if (buffer[0] == 'e' && buffer[1] == 'o' && buffer[2] == 'f') {
        fclose(f);
        // f = NULL;
        printf ("BMP file written successfully!\n");
        exit(1);
      }

      if(fwrite(buffer, sizeof(char), INT_H_IMG_WIDTH, f) != INT_H_IMG_WIDTH) {
        fprintf(stderr, "Error writing output file %s\n", "test.bmp");
        fclose(f);
        exit(1);
      }
    }
  }
};
