package java.io;

public class FilterInputStream
  extends InputStream
{
  protected volatile InputStream in;
  
  protected FilterInputStream(InputStream paramInputStream)
  {
    this.in = paramInputStream;
  }
  
  public int read()
    throws IOException
  {
    return this.in.read();
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return this.in.skip(paramLong);
  }
  
  public int available()
    throws IOException
  {
    return this.in.available();
  }
  
  public void close()
    throws IOException
  {
    this.in.close();
  }
  
  public synchronized void mark(int paramInt)
  {
    this.in.mark(paramInt);
  }
  
  public synchronized void reset()
    throws IOException
  {
    this.in.reset();
  }
  
  public boolean markSupported()
  {
    return this.in.markSupported();
  }
}
