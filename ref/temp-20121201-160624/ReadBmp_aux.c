#include "ReadBmp_aux.h"

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
  
}
    

// read BMP image from file "ccd.bmp" and store it into ScanBuffer
// 
void ReadBmp(unsigned char ScanBuffer[K][L_IMG_HEIGHT][L_IMG_WIDTH]) 
{	
  unsigned int i, r, k;
  char fname[20];

  // Open file

  for(k = 0; k < K; k++) {

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
         (fread(ScanBuffer[k][r], 1, BmpInfoHeader.biWidth, ifp) 
         != BmpInfoHeader.biWidth)) {
        fprintf(stderr, "Error reading data from file %s\n", fname);
        fclose (ifp);
        exit(1);
      }
    
      // fill remaining overhang pixels by copying last pixels 
      for(i = BmpInfoHeader.biWidth; i < MDU(BmpInfoHeader.biWidth) * 8; i++) {
        ScanBuffer[k][r][i] = ScanBuffer[k][r][BmpInfoHeader.biWidth-1];
      }
    }
    
    fclose (ifp);

    // return the number of 8x8 blocks to be processed
    // no longer needed due to static dimensions
  }
}

