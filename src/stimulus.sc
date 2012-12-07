/********************************************************
 * File Name: stimulus.sc
 * Created By: Zhicong Chen -- chen.zhico@husky.neu.edu
 * Creation Date: [2012-12-06 11:04]
 * Last Modified: [2012-12-07 01:39]
 * Licence: chenzc (c) 2012 | all rights reserved
 * Description:  
 *********************************************************/

#include <sim.sh>
#include "stimulus.sh"

import "i_send";

// Paremeters

const double LA[K] = {IMG_1_LA, IMG_2_LA, IMG_3_LA, IMG_4_LA, IMG_5_LA};
const double LB[K] = {IMG_1_LB, IMG_2_LB, IMG_3_LB, IMG_4_LB, IMG_5_LB};

const double H[K][9] = {IMG_1_H, IMG_2_H, IMG_3_H, IMG_4_H, IMG_5_H};

const double gam = 0.4;

// option to use BMPCOLORS, default grayscale only
// #define USE_BMPCOLORS

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


FILE* ifp;
BITMAPFILEHEADER BmpFileHeader;
BITMAPINFOHEADER BmpInfoHeader;
RGBTRIPLE *BmpColors;
int BmpScanWidth, BmpScanHeight;

  
int ReadRevWord()
{
  int c;
  c = fgetc(ifp);
  c |= fgetc(ifp) << 8;
    
  return c;
}

int ReadWord()
{
  int c;
  c = fgetc(ifp) << 8;
  c |= fgetc(ifp);

  return c;
}

int ReadByte()
{
  return fgetc(ifp);
}

long ReadRevDWord()
{
  long c;
  c = fgetc(ifp);
  c |= fgetc(ifp) << 8;
  c |= fgetc(ifp) << 16;
  c |= fgetc(ifp) << 24;
    
  return c;
}

long ReadDWord()
{
  long c;
  c = fgetc(ifp) << 24;
  c |= fgetc(ifp) << 16;
  c |= fgetc(ifp) << 8;
  c |= fgetc(ifp);
    
  return c;
}
    
int  IsBmpFile()
{
  int t = ('B'<<8) | 'M';
  int c;
  c = ReadWord();
  fseek(ifp, -2, 1);
    
  return t == c;
}

// read and analyze BMP header  
void ReadBmpHeader()
{
  int i, count;

  if (!IsBmpFile()) {
    fprintf(stderr, "This file is not compatible with BMP format.\n");
    exit(1);
  }

  /* BMP file header */
  BmpFileHeader.bfType = ReadWord();
  BmpFileHeader.bfSize = ReadRevDWord();
  BmpFileHeader.bfReserved1 = ReadRevWord();
  BmpFileHeader.bfReserved2 = ReadRevWord();
  BmpFileHeader.bfOffBits = ReadRevDWord();

  /* BMP core info */
  BmpInfoHeader.biSize = ReadRevDWord();
  BmpInfoHeader.biWidth = ReadRevDWord();
  BmpInfoHeader.biHeight = ReadRevDWord();
  BmpInfoHeader.biPlanes = ReadRevWord();
  BmpInfoHeader.biBitCount = ReadRevWord();



  if (BmpInfoHeader.biSize > 12) {
    BmpInfoHeader.biCompression = ReadRevDWord();
    BmpInfoHeader.biSizeImage = ReadRevDWord();
    BmpInfoHeader.biXPelsPerMeter = ReadRevDWord();
    BmpInfoHeader.biYPelsPerMeter = ReadRevDWord();
    BmpInfoHeader.biClrUsed = ReadRevDWord();
    BmpInfoHeader.biClrImportant = ReadRevDWord();

    /* read RGBQUAD */
    count = BmpFileHeader.bfOffBits - ftell(ifp);
    count >>= 2;
 
#ifdef USE_BMPCOLORS
    // disable dynamic allocation of color array
    BmpColors = (RGBTRIPLE*) calloc(sizeof(RGBTRIPLE), count);
#endif

    for (i=0; i<count; i++) {
#ifdef USE_BMPCOLORS
      BmpColors[i].B = ReadByte();
      BmpColors[i].G = ReadByte();
      BmpColors[i].R = ReadByte();
      ReadByte();
#else
      // still do the read bytes to maintain file pointer operation
      ReadByte();
      ReadByte();
      ReadByte();
      ReadByte();
#endif
     }
  }
  else {
    /* read RGBTRIPLE */
    count = BmpFileHeader.bfOffBits - ftell(ifp);
    count /= 3;
      
#ifdef USE_BMPCOLORS
    BmpColors = (RGBTRIPLE*) calloc(sizeof(RGBTRIPLE), count);
#endif
      
    for (i=0; i<count; i++) {
#ifdef USE_BMPCOLORS
      BmpColors[i].B = ReadByte();
      BmpColors[i].G = ReadByte();
      BmpColors[i].R = ReadByte();
#else
      ReadByte();
      ReadByte();
      ReadByte();
#endif
    }
  }

  /* BMP scan line is aligned by LONG boundary */
  if (BmpInfoHeader.biBitCount == 24) {
    BmpScanWidth = ((BmpInfoHeader.biWidth*3 + 3) >> 2) << 2;
    //BmpScanWidth = BmpInfoHeader.biWidth;
    BmpScanHeight = BmpInfoHeader.biHeight;
  }
  else {
    BmpScanWidth = ((BmpInfoHeader.biWidth + 3) >> 2) << 2;
 //   printf("%ld\n", BmpInfoHeader.biWidth);
 //   BmpScanWidth = BmpInfoHeader.biWidth;
    BmpScanHeight = BmpInfoHeader.biHeight;
  }

  // validate that image dimensions are according to specification
/*  if (BmpScanWidth != L_IMG_WIDTH) {
    printf("Image width (%d) not according to specification (%d)\n", 
           BmpScanWidth, L_IMG_WIDTH);
    exit(1);
  }
*/
  if (BmpScanHeight != L_IMG_HEIGHT) {
    printf("Image height (%d) not according to specification (%d)\n", 
           BmpScanHeight, L_IMG_HEIGHT);
    exit(1);
  }

#ifdef DEBUG_BMP

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
    


behavior Stimulus(inout unsigned char ScanBuffer[L_IMG_HEIGHT][L_IMG_WIDTH],
                  out double HLG[12], i_send start)
{

  int i, r, k, l;
  char fname[20];

  // Open file

  void main(void) {

    for(k = 0; k < K; k++) {

      // read image start

      sprintf(fname, "ccd%d.bmp", k);
    
      ifp = fopen(fname, "rb");
      if (!ifp) {
        fprintf(stderr, "Cannot open input file %s\n", fname);
        exit(1);
      }

      // Read BMP file header
      ReadBmpHeader();

      // Loop over rows
      for (r = 0; r < BmpInfoHeader.biHeight; r++) {
        // Position file pointer to corresponding row
        fseek (ifp, BmpFileHeader.bfOffBits 
                 + (BmpInfoHeader.biHeight - r - 1) * BmpScanWidth , 0);
        
        // Read pixel row, throw error on unexpected end of file, and bitwidth
        if (ferror(ifp) || 
           (fread(ScanBuffer[r], 1, BmpInfoHeader.biWidth, ifp) 
           != BmpInfoHeader.biWidth)) {
          fprintf(stderr, "Error reading data from file %s\n", fname);
          fclose (ifp);
          exit(1);
        }
    
        // fill remaining overhang pixels by copying last pixels 
        for(i = BmpInfoHeader.biWidth; i < MDU(BmpInfoHeader.biWidth) * 8; i++) {
          ScanBuffer[r][i] = ScanBuffer[r][BmpInfoHeader.biWidth-1];
        }
      }
    
      fclose (ifp);

      // read image end
      // read parameters start
   
      // H[k][9]
      for (l = 0; l < 9; l++) {
        HLG[l] = H[k][l];
      }
      // la
      HLG[l++] = LA[k];
      // lb
      HLG[l++] = LB[k];
      // gam
      HLG[l] = gam;
    
      // read parameters end
      // one page done. send sigal.
      start.send();
#ifdef DEBUG_H_START
  printf("Send start signal.\n");
#endif   
      waitfor(200 MILLI_SEC);
    }
  }

};
