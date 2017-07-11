package sun.awt.windows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final class WDropTargetContextPeerFileStream
  extends FileInputStream
{
  private long stgmedium;
  
  WDropTargetContextPeerFileStream(String paramString, long paramLong)
    throws FileNotFoundException
  {
    super(paramString);
    this.stgmedium = paramLong;
  }
  
  public void close()
    throws IOException
  {
    if (this.stgmedium != 0L)
    {
      super.close();
      freeStgMedium(this.stgmedium);
      this.stgmedium = 0L;
    }
  }
  
  private native void freeStgMedium(long paramLong);
}
