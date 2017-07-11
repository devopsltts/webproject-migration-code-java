package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

abstract class UnicodeDecoder
  extends CharsetDecoder
{
  protected static final char BYTE_ORDER_MARK = '﻿';
  protected static final char REVERSED_MARK = '￾';
  protected static final int NONE = 0;
  protected static final int BIG = 1;
  protected static final int LITTLE = 2;
  private final int expectedByteOrder;
  private int currentByteOrder;
  private int defaultByteOrder = 1;
  
  public UnicodeDecoder(Charset paramCharset, int paramInt)
  {
    super(paramCharset, 0.5F, 1.0F);
    this.expectedByteOrder = (this.currentByteOrder = paramInt);
  }
  
  public UnicodeDecoder(Charset paramCharset, int paramInt1, int paramInt2)
  {
    this(paramCharset, paramInt1);
  }
  
  private char decode(int paramInt1, int paramInt2)
  {
    if (this.currentByteOrder == 1) {
      return (char)(paramInt1 << 8 | paramInt2);
    }
    return (char)(paramInt2 << 8 | paramInt1);
  }
  
  protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
  {
    int i = paramByteBuffer.position();
    try
    {
      while (paramByteBuffer.remaining() > 1)
      {
        int j = paramByteBuffer.get() & 0xFF;
        int k = paramByteBuffer.get() & 0xFF;
        int m;
        if (this.currentByteOrder == 0)
        {
          m = (char)(j << 8 | k);
          if (m == 65279)
          {
            this.currentByteOrder = 1;
            i += 2;
          }
          else if (m == 65534)
          {
            this.currentByteOrder = 2;
            i += 2;
          }
          else
          {
            this.currentByteOrder = this.defaultByteOrder;
          }
        }
        else
        {
          m = decode(j, k);
          CoderResult localCoderResult2;
          if (m == 65534)
          {
            localCoderResult2 = CoderResult.malformedForLength(2);
            return localCoderResult2;
          }
          CoderResult localCoderResult3;
          if (Character.isSurrogate(m))
          {
            if (Character.isHighSurrogate(m))
            {
              if (paramByteBuffer.remaining() < 2)
              {
                localCoderResult2 = CoderResult.UNDERFLOW;
                return localCoderResult2;
              }
              char c = decode(paramByteBuffer.get() & 0xFF, paramByteBuffer.get() & 0xFF);
              CoderResult localCoderResult4;
              if (!Character.isLowSurrogate(c))
              {
                localCoderResult4 = CoderResult.malformedForLength(4);
                return localCoderResult4;
              }
              if (paramCharBuffer.remaining() < 2)
              {
                localCoderResult4 = CoderResult.OVERFLOW;
                return localCoderResult4;
              }
              i += 4;
              paramCharBuffer.put(m);
              paramCharBuffer.put(c);
            }
            else
            {
              localCoderResult3 = CoderResult.malformedForLength(2);
              return localCoderResult3;
            }
          }
          else
          {
            if (!paramCharBuffer.hasRemaining())
            {
              localCoderResult3 = CoderResult.OVERFLOW;
              return localCoderResult3;
            }
            i += 2;
            paramCharBuffer.put(m);
          }
        }
      }
      CoderResult localCoderResult1 = CoderResult.UNDERFLOW;
      return localCoderResult1;
    }
    finally
    {
      paramByteBuffer.position(i);
    }
  }
  
  protected void implReset()
  {
    this.currentByteOrder = this.expectedByteOrder;
  }
}
