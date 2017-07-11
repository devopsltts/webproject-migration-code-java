package com.sun.imageio.stream;

import javax.imageio.stream.ImageInputStream;

public class StreamFinalizer
{
  private ImageInputStream stream;
  
  public StreamFinalizer(ImageInputStream paramImageInputStream)
  {
    this.stream = paramImageInputStream;
  }
  
  /* Error */
  protected void finalize()
    throws java.lang.Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 25	com/sun/imageio/stream/StreamFinalizer:stream	Ljavax/imageio/stream/ImageInputStream;
    //   4: invokeinterface 28 1 0
    //   9: aload_0
    //   10: aconst_null
    //   11: putfield 25	com/sun/imageio/stream/StreamFinalizer:stream	Ljavax/imageio/stream/ImageInputStream;
    //   14: aload_0
    //   15: invokespecial 27	java/lang/Object:finalize	()V
    //   18: goto +28 -> 46
    //   21: astore_1
    //   22: aload_0
    //   23: aconst_null
    //   24: putfield 25	com/sun/imageio/stream/StreamFinalizer:stream	Ljavax/imageio/stream/ImageInputStream;
    //   27: aload_0
    //   28: invokespecial 27	java/lang/Object:finalize	()V
    //   31: goto +15 -> 46
    //   34: astore_2
    //   35: aload_0
    //   36: aconst_null
    //   37: putfield 25	com/sun/imageio/stream/StreamFinalizer:stream	Ljavax/imageio/stream/ImageInputStream;
    //   40: aload_0
    //   41: invokespecial 27	java/lang/Object:finalize	()V
    //   44: aload_2
    //   45: athrow
    //   46: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	StreamFinalizer
    //   21	1	1	localIOException	java.io.IOException
    //   34	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	9	21	java/io/IOException
    //   0	9	34	finally
  }
}
