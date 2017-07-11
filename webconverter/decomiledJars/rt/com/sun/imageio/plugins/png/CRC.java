package com.sun.imageio.plugins.png;

class CRC
{
  private static int[] crcTable = new int['Ā'];
  private int crc = -1;
  
  public CRC() {}
  
  public void reset()
  {
    this.crc = -1;
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      this.crc = (crcTable[((this.crc ^ paramArrayOfByte[(paramInt1 + i)]) & 0xFF)] ^ this.crc >>> 8);
    }
  }
  
  public void update(int paramInt)
  {
    this.crc = (crcTable[((this.crc ^ paramInt) & 0xFF)] ^ this.crc >>> 8);
  }
  
  public int getValue()
  {
    return this.crc ^ 0xFFFFFFFF;
  }
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      int j = i;
      for (int k = 0; k < 8; k++)
      {
        if ((j & 0x1) == 1) {
          j = 0xEDB88320 ^ j >>> 1;
        } else {
          j >>>= 1;
        }
        crcTable[i] = j;
      }
    }
  }
}
