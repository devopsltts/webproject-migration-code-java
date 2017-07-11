package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class UndefLengthOutputStream
  extends FilterOutputStream
{
  private boolean closed = false;
  ExchangeImpl t;
  
  UndefLengthOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    this.t = paramExchangeImpl;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (this.closed) {
      throw new IOException("stream closed");
    }
    this.out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.closed) {
      throw new IOException("stream closed");
    }
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void close()
    throws IOException
  {
    if (this.closed) {
      return;
    }
    this.closed = true;
    flush();
    LeftOverInputStream localLeftOverInputStream = this.t.getOriginalInputStream();
    if (!localLeftOverInputStream.isClosed()) {
      try
      {
        localLeftOverInputStream.close();
      }
      catch (IOException localIOException) {}
    }
    WriteFinishedEvent localWriteFinishedEvent = new WriteFinishedEvent(this.t);
    this.t.getHttpContext().getServerImpl().addEvent(localWriteFinishedEvent);
  }
}
