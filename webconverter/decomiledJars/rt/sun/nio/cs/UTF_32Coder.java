package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class UTF_32Coder
{
  protected static final int BOM_BIG = 65279;
  protected static final int BOM_LITTLE = -131072;
  protected static final int NONE = 0;
  protected static final int BIG = 1;
  protected static final int LITTLE = 2;
  
  UTF_32Coder() {}
  
  protected static class Decoder
    extends CharsetDecoder
  {
    private int currentBO;
    private int expectedBO;
    
    protected Decoder(Charset paramCharset, int paramInt)
    {
      super(0.25F, 1.0F);
      this.expectedBO = paramInt;
      this.currentBO = 0;
    }
    
    private int getCP(ByteBuffer paramByteBuffer)
    {
      return this.currentBO == 1 ? (paramByteBuffer.get() & 0xFF) << 24 | (paramByteBuffer.get() & 0xFF) << 16 | (paramByteBuffer.get() & 0xFF) << 8 | paramByteBuffer.get() & 0xFF : paramByteBuffer.get() & 0xFF | (paramByteBuffer.get() & 0xFF) << 8 | (paramByteBuffer.get() & 0xFF) << 16 | (paramByteBuffer.get() & 0xFF) << 24;
    }
    
    protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      if (paramByteBuffer.remaining() < 4) {
        return CoderResult.UNDERFLOW;
      }
      int i = paramByteBuffer.position();
      try
      {
        int j;
        if (this.currentBO == 0)
        {
          j = (paramByteBuffer.get() & 0xFF) << 24 | (paramByteBuffer.get() & 0xFF) << 16 | (paramByteBuffer.get() & 0xFF) << 8 | paramByteBuffer.get() & 0xFF;
          if ((j == 65279) && (this.expectedBO != 2))
          {
            this.currentBO = 1;
            i += 4;
          }
          else if ((j == -131072) && (this.expectedBO != 1))
          {
            this.currentBO = 2;
            i += 4;
          }
          else
          {
            if (this.expectedBO == 0) {
              this.currentBO = 1;
            } else {
              this.currentBO = this.expectedBO;
            }
            paramByteBuffer.position(i);
          }
        }
        while (paramByteBuffer.remaining() >= 4)
        {
          j = getCP(paramByteBuffer);
          if (Character.isBmpCodePoint(j))
          {
            if (!paramCharBuffer.hasRemaining())
            {
              localCoderResult = CoderResult.OVERFLOW;
              return localCoderResult;
            }
            i += 4;
            paramCharBuffer.put((char)j);
          }
          else if (Character.isValidCodePoint(j))
          {
            if (paramCharBuffer.remaining() < 2)
            {
              localCoderResult = CoderResult.OVERFLOW;
              return localCoderResult;
            }
            i += 4;
            paramCharBuffer.put(Character.highSurrogate(j));
            paramCharBuffer.put(Character.lowSurrogate(j));
          }
          else
          {
            localCoderResult = CoderResult.malformedForLength(4);
            return localCoderResult;
          }
        }
        CoderResult localCoderResult = CoderResult.UNDERFLOW;
        return localCoderResult;
      }
      finally
      {
        paramByteBuffer.position(i);
      }
    }
    
    protected void implReset()
    {
      this.currentBO = 0;
    }
  }
  
  protected static class Encoder
    extends CharsetEncoder
  {
    private boolean doBOM = false;
    private boolean doneBOM = true;
    private int byteOrder;
    
    protected void put(int paramInt, ByteBuffer paramByteBuffer)
    {
      if (this.byteOrder == 1)
      {
        paramByteBuffer.put((byte)(paramInt >> 24));
        paramByteBuffer.put((byte)(paramInt >> 16));
        paramByteBuffer.put((byte)(paramInt >> 8));
        paramByteBuffer.put((byte)paramInt);
      }
      else
      {
        paramByteBuffer.put((byte)paramInt);
        paramByteBuffer.put((byte)(paramInt >> 8));
        paramByteBuffer.put((byte)(paramInt >> 16));
        paramByteBuffer.put((byte)(paramInt >> 24));
      }
    }
    
    protected Encoder(Charset paramCharset, int paramInt, boolean paramBoolean)
    {
      super(4.0F, paramBoolean ? 8.0F : 4.0F, new byte[] { -3, -1, 0, paramInt == 1 ? new byte[] { 0, 0, -1, -3 } : 0 });
      this.byteOrder = paramInt;
      this.doBOM = paramBoolean;
      this.doneBOM = (!paramBoolean);
    }
    
    protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      int i = paramCharBuffer.position();
      if ((!this.doneBOM) && (paramCharBuffer.hasRemaining()))
      {
        if (paramByteBuffer.remaining() < 4) {
          return CoderResult.OVERFLOW;
        }
        put(65279, paramByteBuffer);
        this.doneBOM = true;
      }
      try
      {
        while (paramCharBuffer.hasRemaining())
        {
          char c1 = paramCharBuffer.get();
          CoderResult localCoderResult2;
          if (!Character.isSurrogate(c1))
          {
            if (paramByteBuffer.remaining() < 4)
            {
              localCoderResult2 = CoderResult.OVERFLOW;
              return localCoderResult2;
            }
            i++;
            put(c1, paramByteBuffer);
          }
          else if (Character.isHighSurrogate(c1))
          {
            if (!paramCharBuffer.hasRemaining())
            {
              localCoderResult2 = CoderResult.UNDERFLOW;
              return localCoderResult2;
            }
            char c2 = paramCharBuffer.get();
            CoderResult localCoderResult4;
            if (Character.isLowSurrogate(c2))
            {
              if (paramByteBuffer.remaining() < 4)
              {
                localCoderResult4 = CoderResult.OVERFLOW;
                return localCoderResult4;
              }
              i += 2;
              put(Character.toCodePoint(c1, c2), paramByteBuffer);
            }
            else
            {
              localCoderResult4 = CoderResult.malformedForLength(1);
              return localCoderResult4;
            }
          }
          else
          {
            CoderResult localCoderResult3 = CoderResult.malformedForLength(1);
            return localCoderResult3;
          }
        }
        CoderResult localCoderResult1 = CoderResult.UNDERFLOW;
        return localCoderResult1;
      }
      finally
      {
        paramCharBuffer.position(i);
      }
    }
    
    protected void implReset()
    {
      this.doneBOM = (!this.doBOM);
    }
  }
}
