package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class CancelRequestMessage_1_0
  extends Message_1_0
  implements CancelRequestMessage
{
  private int request_id = 0;
  
  CancelRequestMessage_1_0() {}
  
  CancelRequestMessage_1_0(int paramInt)
  {
    super(1195986768, false, (byte)2, 4);
    this.request_id = paramInt;
  }
  
  public int getRequestId()
  {
    return this.request_id;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}
