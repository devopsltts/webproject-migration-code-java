package sun.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TelnetOutputStream
  extends BufferedOutputStream
{
  boolean stickyCRLF = false;
  boolean seenCR = false;
  public boolean binaryMode = false;
  
  public TelnetOutputStream(OutputStream paramOutputStream, boolean paramBoolean)
  {
    super(paramOutputStream);
    this.binaryMode = paramBoolean;
  }
  
  public void setStickyCRLF(boolean paramBoolean)
  {
    this.stickyCRLF = paramBoolean;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (this.binaryMode)
    {
      super.write(paramInt);
      return;
    }
    if (this.seenCR)
    {
      if (paramInt != 10) {
        super.write(0);
      }
      super.write(paramInt);
      if (paramInt != 13) {
        this.seenCR = false;
      }
    }
    else
    {
      if (paramInt == 10)
      {
        super.write(13);
        super.write(10);
        return;
      }
      if (paramInt == 13) {
        if (this.stickyCRLF)
        {
          this.seenCR = true;
        }
        else
        {
          super.write(13);
          paramInt = 0;
        }
      }
      super.write(paramInt);
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.binaryMode)
    {
      super.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    for (;;)
    {
      paramInt2--;
      if (paramInt2 < 0) {
        break;
      }
      write(paramArrayOfByte[(paramInt1++)]);
    }
  }
}
