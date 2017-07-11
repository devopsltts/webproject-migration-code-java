package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;

public class BufferManagerWriteGrow
  extends BufferManagerWrite
{
  BufferManagerWriteGrow(ORB paramORB)
  {
    super(paramORB);
  }
  
  public boolean sentFragment()
  {
    return false;
  }
  
  public int getBufferSize()
  {
    return this.orb.getORBData().getGIOPBufferSize();
  }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    paramByteBufferWithInfo.growBuffer(this.orb);
    paramByteBufferWithInfo.fragmented = false;
  }
  
  public void sendMessage()
  {
    Connection localConnection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
    localConnection.writeLock();
    try
    {
      localConnection.sendWithoutLock((OutputObject)this.outputObject);
      this.sentFullMessage = true;
      localConnection.writeUnlock();
    }
    finally
    {
      localConnection.writeUnlock();
    }
  }
  
  public void close() {}
}
