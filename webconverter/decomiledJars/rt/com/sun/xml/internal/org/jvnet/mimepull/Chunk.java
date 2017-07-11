package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class Chunk
{
  volatile Chunk next;
  volatile Data data;
  
  public Chunk(Data paramData)
  {
    this.data = paramData;
  }
  
  public Chunk createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer)
  {
    return this.next = new Chunk(this.data.createNext(paramDataHead, paramByteBuffer));
  }
}
