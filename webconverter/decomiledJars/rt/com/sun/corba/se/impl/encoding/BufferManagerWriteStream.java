package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.PIHandler;
import java.nio.ByteBuffer;
import org.omg.CORBA.SystemException;

public class BufferManagerWriteStream
  extends BufferManagerWrite
{
  private int fragmentCount = 0;
  
  BufferManagerWriteStream(ORB paramORB)
  {
    super(paramORB);
  }
  
  public boolean sentFragment()
  {
    return this.fragmentCount > 0;
  }
  
  public int getBufferSize()
  {
    return this.orb.getORBData().getGIOPFragmentSize();
  }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    MessageBase.setFlag(paramByteBufferWithInfo.byteBuffer, 2);
    try
    {
      sendFragment(false);
    }
    catch (SystemException localSystemException)
    {
      this.orb.getPIHandler().invokeClientPIEndingPoint(2, localSystemException);
      throw localSystemException;
    }
    paramByteBufferWithInfo.position(0);
    paramByteBufferWithInfo.buflen = paramByteBufferWithInfo.byteBuffer.limit();
    paramByteBufferWithInfo.fragmented = true;
    FragmentMessage localFragmentMessage = ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage();
    localFragmentMessage.write((CDROutputObject)this.outputObject);
  }
  
  private void sendFragment(boolean paramBoolean)
  {
    Connection localConnection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
    localConnection.writeLock();
    try
    {
      localConnection.sendWithoutLock((OutputObject)this.outputObject);
      this.fragmentCount += 1;
      localConnection.writeUnlock();
    }
    finally
    {
      localConnection.writeUnlock();
    }
  }
  
  public void sendMessage()
  {
    sendFragment(true);
    this.sentFullMessage = true;
  }
  
  public void close() {}
}
