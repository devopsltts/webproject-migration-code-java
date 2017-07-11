package java.io;

public class DataOutputStream
  extends FilterOutputStream
  implements DataOutput
{
  protected int written;
  private byte[] bytearr = null;
  private byte[] writeBuffer = new byte[8];
  
  public DataOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  private void incCount(int paramInt)
  {
    int i = this.written + paramInt;
    if (i < 0) {
      i = Integer.MAX_VALUE;
    }
    this.written = i;
  }
  
  public synchronized void write(int paramInt)
    throws IOException
  {
    this.out.write(paramInt);
    incCount(1);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    incCount(paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    this.out.flush();
  }
  
  public final void writeBoolean(boolean paramBoolean)
    throws IOException
  {
    this.out.write(paramBoolean ? 1 : 0);
    incCount(1);
  }
  
  public final void writeByte(int paramInt)
    throws IOException
  {
    this.out.write(paramInt);
    incCount(1);
  }
  
  public final void writeShort(int paramInt)
    throws IOException
  {
    this.out.write(paramInt >>> 8 & 0xFF);
    this.out.write(paramInt >>> 0 & 0xFF);
    incCount(2);
  }
  
  public final void writeChar(int paramInt)
    throws IOException
  {
    this.out.write(paramInt >>> 8 & 0xFF);
    this.out.write(paramInt >>> 0 & 0xFF);
    incCount(2);
  }
  
  public final void writeInt(int paramInt)
    throws IOException
  {
    this.out.write(paramInt >>> 24 & 0xFF);
    this.out.write(paramInt >>> 16 & 0xFF);
    this.out.write(paramInt >>> 8 & 0xFF);
    this.out.write(paramInt >>> 0 & 0xFF);
    incCount(4);
  }
  
  public final void writeLong(long paramLong)
    throws IOException
  {
    this.writeBuffer[0] = ((byte)(int)(paramLong >>> 56));
    this.writeBuffer[1] = ((byte)(int)(paramLong >>> 48));
    this.writeBuffer[2] = ((byte)(int)(paramLong >>> 40));
    this.writeBuffer[3] = ((byte)(int)(paramLong >>> 32));
    this.writeBuffer[4] = ((byte)(int)(paramLong >>> 24));
    this.writeBuffer[5] = ((byte)(int)(paramLong >>> 16));
    this.writeBuffer[6] = ((byte)(int)(paramLong >>> 8));
    this.writeBuffer[7] = ((byte)(int)(paramLong >>> 0));
    this.out.write(this.writeBuffer, 0, 8);
    incCount(8);
  }
  
  public final void writeFloat(float paramFloat)
    throws IOException
  {
    writeInt(Float.floatToIntBits(paramFloat));
  }
  
  public final void writeDouble(double paramDouble)
    throws IOException
  {
    writeLong(Double.doubleToLongBits(paramDouble));
  }
  
  public final void writeBytes(String paramString)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      this.out.write((byte)paramString.charAt(j));
    }
    incCount(i);
  }
  
  public final void writeChars(String paramString)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      this.out.write(k >>> 8 & 0xFF);
      this.out.write(k >>> 0 & 0xFF);
    }
    incCount(i * 2);
  }
  
  public final void writeUTF(String paramString)
    throws IOException
  {
    writeUTF(paramString, this);
  }
  
  static int writeUTF(String paramString, DataOutput paramDataOutput)
    throws IOException
  {
    int i = paramString.length();
    int j = 0;
    int m = 0;
    int k;
    for (int n = 0; n < i; n++)
    {
      k = paramString.charAt(n);
      if ((k >= 1) && (k <= 127)) {
        j++;
      } else if (k > 2047) {
        j += 3;
      } else {
        j += 2;
      }
    }
    if (j > 65535) {
      throw new UTFDataFormatException("encoded string too long: " + j + " bytes");
    }
    byte[] arrayOfByte = null;
    if ((paramDataOutput instanceof DataOutputStream))
    {
      DataOutputStream localDataOutputStream = (DataOutputStream)paramDataOutput;
      if ((localDataOutputStream.bytearr == null) || (localDataOutputStream.bytearr.length < j + 2)) {
        localDataOutputStream.bytearr = new byte[j * 2 + 2];
      }
      arrayOfByte = localDataOutputStream.bytearr;
    }
    else
    {
      arrayOfByte = new byte[j + 2];
    }
    arrayOfByte[(m++)] = ((byte)(j >>> 8 & 0xFF));
    arrayOfByte[(m++)] = ((byte)(j >>> 0 & 0xFF));
    int i1 = 0;
    for (i1 = 0; i1 < i; i1++)
    {
      k = paramString.charAt(i1);
      if ((k < 1) || (k > 127)) {
        break;
      }
      arrayOfByte[(m++)] = ((byte)k);
    }
    while (i1 < i)
    {
      k = paramString.charAt(i1);
      if ((k >= 1) && (k <= 127))
      {
        arrayOfByte[(m++)] = ((byte)k);
      }
      else if (k > 2047)
      {
        arrayOfByte[(m++)] = ((byte)(0xE0 | k >> 12 & 0xF));
        arrayOfByte[(m++)] = ((byte)(0x80 | k >> 6 & 0x3F));
        arrayOfByte[(m++)] = ((byte)(0x80 | k >> 0 & 0x3F));
      }
      else
      {
        arrayOfByte[(m++)] = ((byte)(0xC0 | k >> 6 & 0x1F));
        arrayOfByte[(m++)] = ((byte)(0x80 | k >> 0 & 0x3F));
      }
      i1++;
    }
    paramDataOutput.write(arrayOfByte, 0, j + 2);
    return j + 2;
  }
  
  public final int size()
  {
    return this.written;
  }
}
