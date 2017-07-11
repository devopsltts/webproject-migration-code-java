package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import javax.crypto.Mac;

public class MacOutputStream
  extends ByteArrayOutputStream
{
  private final Mac mac;
  
  public MacOutputStream(Mac paramMac)
  {
    this.mac = paramMac;
  }
  
  public void write(int paramInt)
  {
    super.write(paramInt);
    this.mac.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    this.mac.update(paramArrayOfByte, paramInt1, paramInt2);
  }
}
