package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FragmentMessage_1_1
  extends Message_1_1
  implements FragmentMessage
{
  FragmentMessage_1_1() {}
  
  FragmentMessage_1_1(Message_1_1 paramMessage_1_1)
  {
    this.magic = paramMessage_1_1.magic;
    this.GIOP_version = paramMessage_1_1.GIOP_version;
    this.flags = paramMessage_1_1.flags;
    this.message_type = 7;
    this.message_size = 0;
  }
  
  public int getRequestId()
  {
    return -1;
  }
  
  public int getHeaderLength()
  {
    return 12;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}
