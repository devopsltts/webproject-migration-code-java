package sun.awt.windows;

import java.io.IOException;
import java.io.InputStream;

final class WDropTargetContextPeerIStream
  extends InputStream
{
  private long istream;
  
  WDropTargetContextPeerIStream(long paramLong)
    throws IOException
  {
    if (paramLong == 0L) {
      throw new IOException("No IStream");
    }
    this.istream = paramLong;
  }
  
  public int available()
    throws IOException
  {
    if (this.istream == 0L) {
      throw new IOException("No IStream");
    }
    return Available(this.istream);
  }
  
  private native int Available(long paramLong);
  
  public int read()
    throws IOException
  {
    if (this.istream == 0L) {
      throw new IOException("No IStream");
    }
    return Read(this.istream);
  }
  
  private native int Read(long paramLong)
    throws IOException;
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.istream == 0L) {
      throw new IOException("No IStream");
    }
    return ReadBytes(this.istream, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private native int ReadBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public void close()
    throws IOException
  {
    if (this.istream != 0L)
    {
      super.close();
      Close(this.istream);
      this.istream = 0L;
    }
  }
  
  private native void Close(long paramLong);
}
