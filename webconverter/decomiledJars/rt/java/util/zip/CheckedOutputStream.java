package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CheckedOutputStream
  extends FilterOutputStream
{
  private Checksum cksum;
  
  public CheckedOutputStream(OutputStream paramOutputStream, Checksum paramChecksum)
  {
    super(paramOutputStream);
    this.cksum = paramChecksum;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    this.out.write(paramInt);
    this.cksum.update(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    this.cksum.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public Checksum getChecksum()
  {
    return this.cksum;
  }
}
