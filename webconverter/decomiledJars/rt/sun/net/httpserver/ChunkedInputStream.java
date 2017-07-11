package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class ChunkedInputStream
  extends LeftOverInputStream
{
  private int remaining;
  private boolean needToReadHeader = true;
  static final char CR = '\r';
  static final char LF = '\n';
  private static final int MAX_CHUNK_HEADER_SIZE = 2050;
  
  ChunkedInputStream(ExchangeImpl paramExchangeImpl, InputStream paramInputStream)
  {
    super(paramExchangeImpl, paramInputStream);
  }
  
  private int numeric(char[] paramArrayOfChar, int paramInt)
    throws IOException
  {
    assert (paramArrayOfChar.length >= paramInt);
    int i = 0;
    for (int j = 0; j < paramInt; j++)
    {
      int k = paramArrayOfChar[j];
      int m = 0;
      if ((k >= 48) && (k <= 57)) {
        m = k - 48;
      } else if ((k >= 97) && (k <= 102)) {
        m = k - 97 + 10;
      } else if ((k >= 65) && (k <= 70)) {
        m = k - 65 + 10;
      } else {
        throw new IOException("invalid chunk length");
      }
      i = i * 16 + m;
    }
    return i;
  }
  
  private int readChunkHeader()
    throws IOException
  {
    int i = 0;
    char[] arrayOfChar = new char[16];
    int k = 0;
    int m = 0;
    int n = 0;
    int j;
    while ((j = this.in.read()) != -1)
    {
      int i1 = (char)j;
      n++;
      if ((k == arrayOfChar.length - 1) || (n > 2050)) {
        throw new IOException("invalid chunk header");
      }
      if (i != 0)
      {
        if (i1 == 10)
        {
          int i2 = numeric(arrayOfChar, k);
          return i2;
        }
        i = 0;
        if (m == 0) {
          arrayOfChar[(k++)] = i1;
        }
      }
      else if (i1 == 13)
      {
        i = 1;
      }
      else if (i1 == 59)
      {
        m = 1;
      }
      else if (m == 0)
      {
        arrayOfChar[(k++)] = i1;
      }
    }
    throw new IOException("end of stream reading chunk header");
  }
  
  protected int readImpl(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.eof) {
      return -1;
    }
    if (this.needToReadHeader)
    {
      this.remaining = readChunkHeader();
      if (this.remaining == 0)
      {
        this.eof = true;
        consumeCRLF();
        this.t.getServerImpl().requestCompleted(this.t.getConnection());
        return -1;
      }
      this.needToReadHeader = false;
    }
    if (paramInt2 > this.remaining) {
      paramInt2 = this.remaining;
    }
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i > -1) {
      this.remaining -= i;
    }
    if (this.remaining == 0)
    {
      this.needToReadHeader = true;
      consumeCRLF();
    }
    return i;
  }
  
  private void consumeCRLF()
    throws IOException
  {
    int i = (char)this.in.read();
    if (i != 13) {
      throw new IOException("invalid chunk end");
    }
    i = (char)this.in.read();
    if (i != 10) {
      throw new IOException("invalid chunk end");
    }
  }
  
  public int available()
    throws IOException
  {
    if ((this.eof) || (this.closed)) {
      return 0;
    }
    int i = this.in.available();
    return i > this.remaining ? this.remaining : i;
  }
  
  public boolean isDataBuffered()
    throws IOException
  {
    assert (this.eof);
    return this.in.available() > 0;
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void mark(int paramInt) {}
  
  public void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
}
