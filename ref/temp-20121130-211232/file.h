#include "file.h"

FILE *f = NULL;


void FileWrite(unsigned char *bytes, unsigned long num)
{
  if(!f) {
     f=fopen("test.jpg","wb");
  }
  if(!f) {
      fprintf(stderr, "Cannot open output file %s\n", "test.jpg");
  }

  if (fwrite(bytes,sizeof(char),num,f) != num) {
      fprintf(stderr, "Error writing output file %s\n", "test.jpg");
      fclose(f);
      exit(1);
  }

  if (bytes[num-2] == 0xff && bytes[num-1] == 0xd9) {
    fclose(f);
    f = NULL;
    printf ("Encoded JPEG file written successfully!\n");
  }
}

