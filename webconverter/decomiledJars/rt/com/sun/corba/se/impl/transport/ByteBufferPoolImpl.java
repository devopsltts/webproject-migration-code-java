package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteBufferPoolImpl
  implements ByteBufferPool
{
  private ORB itsOrb;
  private int itsByteBufferSize;
  private ArrayList itsPool;
  private int itsObjectCounter = 0;
  private boolean debug;
  
  public ByteBufferPoolImpl(ORB paramORB)
  {
    this.itsByteBufferSize = paramORB.getORBData().getGIOPFragmentSize();
    this.itsPool = new ArrayList();
    this.itsOrb = paramORB;
    this.debug = paramORB.transportDebugFlag;
  }
  
  public ByteBuffer getByteBuffer(int paramInt)
  {
    ByteBuffer localByteBuffer = null;
    if ((paramInt <= this.itsByteBufferSize) && (!this.itsOrb.getORBData().disableDirectByteBufferUse()))
    {
      int i;
      synchronized (this.itsPool)
      {
        i = this.itsPool.size();
        if (i > 0)
        {
          localByteBuffer = (ByteBuffer)this.itsPool.remove(i - 1);
          localByteBuffer.clear();
        }
      }
      if (i <= 0) {
        localByteBuffer = ByteBuffer.allocateDirect(this.itsByteBufferSize);
      }
      this.itsObjectCounter += 1;
    }
    else
    {
      localByteBuffer = ByteBuffer.allocate(paramInt);
    }
    return localByteBuffer;
  }
  
  public void releaseByteBuffer(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.isDirect())
    {
      synchronized (this.itsPool)
      {
        int i = 0;
        int j = 0;
        Object localObject1;
        if (this.debug) {
          for (int k = 0; (k < this.itsPool.size()) && (i == 0); k++)
          {
            localObject1 = (ByteBuffer)this.itsPool.get(k);
            if (paramByteBuffer == localObject1)
            {
              i = 1;
              j = System.identityHashCode(paramByteBuffer);
            }
          }
        }
        if ((i == 0) || (!this.debug))
        {
          this.itsPool.add(paramByteBuffer);
        }
        else
        {
          String str = Thread.currentThread().getName();
          localObject1 = new Throwable(str + ": Duplicate ByteBuffer reference (" + j + ")");
          ((Throwable)localObject1).printStackTrace(System.out);
        }
      }
      this.itsObjectCounter -= 1;
    }
    else
    {
      paramByteBuffer = null;
    }
  }
  
  public int activeCount()
  {
    return this.itsObjectCounter;
  }
}
