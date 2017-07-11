package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import java.nio.ByteBuffer;

public class ByteBufferWithInfo
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private boolean debug;
  private int index;
  public ByteBuffer byteBuffer;
  public int buflen;
  public int needed;
  public boolean fragmented;
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt)
  {
    this.orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    this.debug = this.orb.transportDebugFlag;
    this.byteBuffer = paramByteBuffer;
    if (paramByteBuffer != null) {
      this.buflen = paramByteBuffer.limit();
    }
    position(paramInt);
    this.needed = 0;
    this.fragmented = false;
  }
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer)
  {
    this(paramORB, paramByteBuffer, 0);
  }
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, BufferManagerWrite paramBufferManagerWrite)
  {
    this(paramORB, paramBufferManagerWrite, true);
  }
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, BufferManagerWrite paramBufferManagerWrite, boolean paramBoolean)
  {
    this.orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    this.debug = this.orb.transportDebugFlag;
    int i = paramBufferManagerWrite.getBufferSize();
    if (paramBoolean)
    {
      ByteBufferPool localByteBufferPool = this.orb.getByteBufferPool();
      this.byteBuffer = localByteBufferPool.getByteBuffer(i);
      if (this.debug)
      {
        int j = System.identityHashCode(this.byteBuffer);
        StringBuffer localStringBuffer = new StringBuffer(80);
        localStringBuffer.append("constructor (ORB, BufferManagerWrite) - got ").append("ByteBuffer id (").append(j).append(") from ByteBufferPool.");
        String str = localStringBuffer.toString();
        dprint(str);
      }
    }
    else
    {
      this.byteBuffer = ByteBuffer.allocate(i);
    }
    position(0);
    this.buflen = i;
    this.byteBuffer.limit(this.buflen);
    this.needed = 0;
    this.fragmented = false;
  }
  
  public ByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    this.orb = paramByteBufferWithInfo.orb;
    this.debug = paramByteBufferWithInfo.debug;
    this.byteBuffer = paramByteBufferWithInfo.byteBuffer;
    this.buflen = paramByteBufferWithInfo.buflen;
    this.byteBuffer.limit(this.buflen);
    position(paramByteBufferWithInfo.position());
    this.needed = paramByteBufferWithInfo.needed;
    this.fragmented = paramByteBufferWithInfo.fragmented;
  }
  
  public int getSize()
  {
    return position();
  }
  
  public int getLength()
  {
    return this.buflen;
  }
  
  public int position()
  {
    return this.index;
  }
  
  public void position(int paramInt)
  {
    this.byteBuffer.position(paramInt);
    this.index = paramInt;
  }
  
  public void setLength(int paramInt)
  {
    this.buflen = paramInt;
    this.byteBuffer.limit(this.buflen);
  }
  
  public void growBuffer(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    int i = this.byteBuffer.limit() * 2;
    while (position() + this.needed >= i) {
      i *= 2;
    }
    ByteBufferPool localByteBufferPool = paramORB.getByteBufferPool();
    ByteBuffer localByteBuffer = localByteBufferPool.getByteBuffer(i);
    int j;
    StringBuffer localStringBuffer;
    String str;
    if (this.debug)
    {
      j = System.identityHashCode(localByteBuffer);
      localStringBuffer = new StringBuffer(80);
      localStringBuffer.append("growBuffer() - got ByteBuffer id (");
      localStringBuffer.append(j).append(") from ByteBufferPool.");
      str = localStringBuffer.toString();
      dprint(str);
    }
    this.byteBuffer.position(0);
    localByteBuffer.put(this.byteBuffer);
    if (this.debug)
    {
      j = System.identityHashCode(this.byteBuffer);
      localStringBuffer = new StringBuffer(80);
      localStringBuffer.append("growBuffer() - releasing ByteBuffer id (");
      localStringBuffer.append(j).append(") to ByteBufferPool.");
      str = localStringBuffer.toString();
      dprint(str);
    }
    localByteBufferPool.releaseByteBuffer(this.byteBuffer);
    this.byteBuffer = localByteBuffer;
    this.buflen = i;
    this.byteBuffer.limit(this.buflen);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("ByteBufferWithInfo:");
    localStringBuffer.append(" buflen = " + this.buflen);
    localStringBuffer.append(" byteBuffer.limit = " + this.byteBuffer.limit());
    localStringBuffer.append(" index = " + this.index);
    localStringBuffer.append(" position = " + position());
    localStringBuffer.append(" needed = " + this.needed);
    localStringBuffer.append(" byteBuffer = " + (this.byteBuffer == null ? "null" : "not null"));
    localStringBuffer.append(" fragmented = " + this.fragmented);
    return localStringBuffer.toString();
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("ByteBufferWithInfo", paramString);
  }
}
