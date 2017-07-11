package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class BitFile
{
  ImageOutputStream output;
  byte[] buffer;
  int index;
  int bitsLeft;
  boolean blocks = false;
  
  public BitFile(ImageOutputStream paramImageOutputStream, boolean paramBoolean)
  {
    this.output = paramImageOutputStream;
    this.blocks = paramBoolean;
    this.buffer = new byte['Ā'];
    this.index = 0;
    this.bitsLeft = 8;
  }
  
  public void flush()
    throws IOException
  {
    int i = this.index + (this.bitsLeft == 8 ? 0 : 1);
    if (i > 0)
    {
      if (this.blocks) {
        this.output.write(i);
      }
      this.output.write(this.buffer, 0, i);
      this.buffer[0] = 0;
      this.index = 0;
      this.bitsLeft = 8;
    }
  }
  
  public void writeBits(int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j = 255;
    do
    {
      if (((this.index == 254) && (this.bitsLeft == 0)) || (this.index > 254))
      {
        if (this.blocks) {
          this.output.write(j);
        }
        this.output.write(this.buffer, 0, j);
        this.buffer[0] = 0;
        this.index = 0;
        this.bitsLeft = 8;
      }
      if (paramInt2 <= this.bitsLeft)
      {
        if (this.blocks)
        {
          int tmp109_106 = this.index;
          byte[] tmp109_102 = this.buffer;
          tmp109_102[tmp109_106] = ((byte)(tmp109_102[tmp109_106] | (paramInt1 & (1 << paramInt2) - 1) << 8 - this.bitsLeft));
          i += paramInt2;
          this.bitsLeft -= paramInt2;
          paramInt2 = 0;
        }
        else
        {
          int tmp156_153 = this.index;
          byte[] tmp156_149 = this.buffer;
          tmp156_149[tmp156_153] = ((byte)(tmp156_149[tmp156_153] | (paramInt1 & (1 << paramInt2) - 1) << this.bitsLeft - paramInt2));
          i += paramInt2;
          this.bitsLeft -= paramInt2;
          paramInt2 = 0;
        }
      }
      else if (this.blocks)
      {
        int tmp209_206 = this.index;
        byte[] tmp209_202 = this.buffer;
        tmp209_202[tmp209_206] = ((byte)(tmp209_202[tmp209_206] | (paramInt1 & (1 << this.bitsLeft) - 1) << 8 - this.bitsLeft));
        i += this.bitsLeft;
        paramInt1 >>= this.bitsLeft;
        paramInt2 -= this.bitsLeft;
        this.buffer[(++this.index)] = 0;
        this.bitsLeft = 8;
      }
      else
      {
        int k = paramInt1 >>> paramInt2 - this.bitsLeft & (1 << this.bitsLeft) - 1;
        int tmp306_303 = this.index;
        byte[] tmp306_299 = this.buffer;
        tmp306_299[tmp306_303] = ((byte)(tmp306_299[tmp306_303] | k));
        paramInt2 -= this.bitsLeft;
        i += this.bitsLeft;
        this.buffer[(++this.index)] = 0;
        this.bitsLeft = 8;
      }
    } while (paramInt2 != 0);
  }
}
