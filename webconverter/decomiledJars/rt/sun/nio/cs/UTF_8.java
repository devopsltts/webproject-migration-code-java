package sun.nio.cs;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

class UTF_8
  extends Unicode
{
  public UTF_8()
  {
    super("UTF-8", StandardCharsets.aliases_UTF_8);
  }
  
  public String historicalName()
  {
    return "UTF8";
  }
  
  public CharsetDecoder newDecoder()
  {
    return new Decoder(this, null);
  }
  
  public CharsetEncoder newEncoder()
  {
    return new Encoder(this, null);
  }
  
  private static final void updatePositions(Buffer paramBuffer1, int paramInt1, Buffer paramBuffer2, int paramInt2)
  {
    paramBuffer1.position(paramInt1 - paramBuffer1.arrayOffset());
    paramBuffer2.position(paramInt2 - paramBuffer2.arrayOffset());
  }
  
  private static class Decoder
    extends CharsetDecoder
    implements ArrayDecoder
  {
    private Decoder(Charset paramCharset)
    {
      super(1.0F, 1.0F);
    }
    
    private static boolean isNotContinuation(int paramInt)
    {
      return (paramInt & 0xC0) != 128;
    }
    
    private static boolean isMalformed3(int paramInt1, int paramInt2, int paramInt3)
    {
      return ((paramInt1 == -32) && ((paramInt2 & 0xE0) == 128)) || ((paramInt2 & 0xC0) != 128) || ((paramInt3 & 0xC0) != 128);
    }
    
    private static boolean isMalformed3_2(int paramInt1, int paramInt2)
    {
      return ((paramInt1 == -32) && ((paramInt2 & 0xE0) == 128)) || ((paramInt2 & 0xC0) != 128);
    }
    
    private static boolean isMalformed4(int paramInt1, int paramInt2, int paramInt3)
    {
      return ((paramInt1 & 0xC0) != 128) || ((paramInt2 & 0xC0) != 128) || ((paramInt3 & 0xC0) != 128);
    }
    
    private static boolean isMalformed4_2(int paramInt1, int paramInt2)
    {
      return ((paramInt1 == 240) && ((paramInt2 < 144) || (paramInt2 > 191))) || ((paramInt1 == 244) && ((paramInt2 & 0xF0) != 128)) || ((paramInt2 & 0xC0) != 128);
    }
    
    private static boolean isMalformed4_3(int paramInt)
    {
      return (paramInt & 0xC0) != 128;
    }
    
    private static CoderResult lookupN(ByteBuffer paramByteBuffer, int paramInt)
    {
      for (int i = 1; i < paramInt; i++) {
        if (isNotContinuation(paramByteBuffer.get())) {
          return CoderResult.malformedForLength(i);
        }
      }
      return CoderResult.malformedForLength(paramInt);
    }
    
    private static CoderResult malformedN(ByteBuffer paramByteBuffer, int paramInt)
    {
      int i;
      int j;
      switch (paramInt)
      {
      case 1: 
      case 2: 
        return CoderResult.malformedForLength(1);
      case 3: 
        i = paramByteBuffer.get();
        j = paramByteBuffer.get();
        return CoderResult.malformedForLength(((i == -32) && ((j & 0xE0) == 128)) || (isNotContinuation(j)) ? 1 : 2);
      case 4: 
        i = paramByteBuffer.get() & 0xFF;
        j = paramByteBuffer.get() & 0xFF;
        if ((i > 244) || ((i == 240) && ((j < 144) || (j > 191))) || ((i == 244) && ((j & 0xF0) != 128)) || (isNotContinuation(j))) {
          return CoderResult.malformedForLength(1);
        }
        if (isNotContinuation(paramByteBuffer.get())) {
          return CoderResult.malformedForLength(2);
        }
        return CoderResult.malformedForLength(3);
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return null;
    }
    
    private static CoderResult malformed(ByteBuffer paramByteBuffer, int paramInt1, CharBuffer paramCharBuffer, int paramInt2, int paramInt3)
    {
      paramByteBuffer.position(paramInt1 - paramByteBuffer.arrayOffset());
      CoderResult localCoderResult = malformedN(paramByteBuffer, paramInt3);
      UTF_8.updatePositions(paramByteBuffer, paramInt1, paramCharBuffer, paramInt2);
      return localCoderResult;
    }
    
    private static CoderResult malformed(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
    {
      paramByteBuffer.position(paramInt1);
      CoderResult localCoderResult = malformedN(paramByteBuffer, paramInt2);
      paramByteBuffer.position(paramInt1);
      return localCoderResult;
    }
    
    private static CoderResult malformedForLength(ByteBuffer paramByteBuffer, int paramInt1, CharBuffer paramCharBuffer, int paramInt2, int paramInt3)
    {
      UTF_8.updatePositions(paramByteBuffer, paramInt1, paramCharBuffer, paramInt2);
      return CoderResult.malformedForLength(paramInt3);
    }
    
    private static CoderResult malformedForLength(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
    {
      paramByteBuffer.position(paramInt1);
      return CoderResult.malformedForLength(paramInt2);
    }
    
    private static CoderResult xflow(Buffer paramBuffer1, int paramInt1, int paramInt2, Buffer paramBuffer2, int paramInt3, int paramInt4)
    {
      UTF_8.updatePositions(paramBuffer1, paramInt1, paramBuffer2, paramInt3);
      return (paramInt4 == 0) || (paramInt2 - paramInt1 < paramInt4) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
    }
    
    private static CoderResult xflow(Buffer paramBuffer, int paramInt1, int paramInt2)
    {
      paramBuffer.position(paramInt1);
      return (paramInt2 == 0) || (paramBuffer.remaining() < paramInt2) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
    }
    
    private CoderResult decodeArrayLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
      char[] arrayOfChar = paramCharBuffer.array();
      int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
      int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
      int n = k + Math.min(j - i, m - k);
      while ((k < n) && (arrayOfByte[i] >= 0)) {
        arrayOfChar[(k++)] = ((char)arrayOfByte[(i++)]);
      }
      while (i < j)
      {
        int i1 = arrayOfByte[i];
        if (i1 >= 0)
        {
          if (k >= m) {
            return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 1);
          }
          arrayOfChar[(k++)] = ((char)i1);
          i++;
        }
        else
        {
          int i2;
          if ((i1 >> 5 == -2) && ((i1 & 0x1E) != 0))
          {
            if ((j - i < 2) || (k >= m)) {
              return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 2);
            }
            i2 = arrayOfByte[(i + 1)];
            if (isNotContinuation(i2)) {
              return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
            }
            arrayOfChar[(k++)] = ((char)(i1 << 6 ^ i2 ^ 0xF80));
            i += 2;
          }
          else
          {
            int i3;
            int i4;
            if (i1 >> 4 == -2)
            {
              i2 = j - i;
              if ((i2 < 3) || (k >= m))
              {
                if ((i2 > 1) && (isMalformed3_2(i1, arrayOfByte[(i + 1)]))) {
                  return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
                }
                return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 3);
              }
              i3 = arrayOfByte[(i + 1)];
              i4 = arrayOfByte[(i + 2)];
              if (isMalformed3(i1, i3, i4)) {
                return malformed(paramByteBuffer, i, paramCharBuffer, k, 3);
              }
              char c = (char)(i1 << 12 ^ i3 << 6 ^ i4 ^ 0xFFFE1F80);
              if (Character.isSurrogate(c)) {
                return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 3);
              }
              arrayOfChar[(k++)] = c;
              i += 3;
            }
            else if (i1 >> 3 == -2)
            {
              i2 = j - i;
              if ((i2 < 4) || (m - k < 2))
              {
                i1 &= 0xFF;
                if ((i1 > 244) || ((i2 > 1) && (isMalformed4_2(i1, arrayOfByte[(i + 1)] & 0xFF)))) {
                  return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
                }
                if ((i2 > 2) && (isMalformed4_3(arrayOfByte[(i + 2)]))) {
                  return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 2);
                }
                return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 4);
              }
              i3 = arrayOfByte[(i + 1)];
              i4 = arrayOfByte[(i + 2)];
              int i5 = arrayOfByte[(i + 3)];
              int i6 = i1 << 18 ^ i3 << 12 ^ i4 << 6 ^ i5 ^ 0x381F80;
              if ((isMalformed4(i3, i4, i5)) || (!Character.isSupplementaryCodePoint(i6))) {
                return malformed(paramByteBuffer, i, paramCharBuffer, k, 4);
              }
              arrayOfChar[(k++)] = Character.highSurrogate(i6);
              arrayOfChar[(k++)] = Character.lowSurrogate(i6);
              i += 4;
            }
            else
            {
              return malformed(paramByteBuffer, i, paramCharBuffer, k, 1);
            }
          }
        }
      }
      return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 0);
    }
    
    private CoderResult decodeBufferLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      int i = paramByteBuffer.position();
      int j = paramByteBuffer.limit();
      while (i < j)
      {
        int k = paramByteBuffer.get();
        if (k >= 0)
        {
          if (paramCharBuffer.remaining() < 1) {
            return xflow(paramByteBuffer, i, 1);
          }
          paramCharBuffer.put((char)k);
          i++;
        }
        else
        {
          int m;
          if ((k >> 5 == -2) && ((k & 0x1E) != 0))
          {
            if ((j - i < 2) || (paramCharBuffer.remaining() < 1)) {
              return xflow(paramByteBuffer, i, 2);
            }
            m = paramByteBuffer.get();
            if (isNotContinuation(m)) {
              return malformedForLength(paramByteBuffer, i, 1);
            }
            paramCharBuffer.put((char)(k << 6 ^ m ^ 0xF80));
            i += 2;
          }
          else
          {
            int n;
            int i1;
            if (k >> 4 == -2)
            {
              m = j - i;
              if ((m < 3) || (paramCharBuffer.remaining() < 1))
              {
                if ((m > 1) && (isMalformed3_2(k, paramByteBuffer.get()))) {
                  return malformedForLength(paramByteBuffer, i, 1);
                }
                return xflow(paramByteBuffer, i, 3);
              }
              n = paramByteBuffer.get();
              i1 = paramByteBuffer.get();
              if (isMalformed3(k, n, i1)) {
                return malformed(paramByteBuffer, i, 3);
              }
              char c = (char)(k << 12 ^ n << 6 ^ i1 ^ 0xFFFE1F80);
              if (Character.isSurrogate(c)) {
                return malformedForLength(paramByteBuffer, i, 3);
              }
              paramCharBuffer.put(c);
              i += 3;
            }
            else if (k >> 3 == -2)
            {
              m = j - i;
              if ((m < 4) || (paramCharBuffer.remaining() < 2))
              {
                k &= 0xFF;
                if ((k > 244) || ((m > 1) && (isMalformed4_2(k, paramByteBuffer.get() & 0xFF)))) {
                  return malformedForLength(paramByteBuffer, i, 1);
                }
                if ((m > 2) && (isMalformed4_3(paramByteBuffer.get()))) {
                  return malformedForLength(paramByteBuffer, i, 2);
                }
                return xflow(paramByteBuffer, i, 4);
              }
              n = paramByteBuffer.get();
              i1 = paramByteBuffer.get();
              int i2 = paramByteBuffer.get();
              int i3 = k << 18 ^ n << 12 ^ i1 << 6 ^ i2 ^ 0x381F80;
              if ((isMalformed4(n, i1, i2)) || (!Character.isSupplementaryCodePoint(i3))) {
                return malformed(paramByteBuffer, i, 4);
              }
              paramCharBuffer.put(Character.highSurrogate(i3));
              paramCharBuffer.put(Character.lowSurrogate(i3));
              i += 4;
            }
            else
            {
              return malformed(paramByteBuffer, i, 1);
            }
          }
        }
      }
      return xflow(paramByteBuffer, i, 0);
    }
    
    protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      if ((paramByteBuffer.hasArray()) && (paramCharBuffer.hasArray())) {
        return decodeArrayLoop(paramByteBuffer, paramCharBuffer);
      }
      return decodeBufferLoop(paramByteBuffer, paramCharBuffer);
    }
    
    private static ByteBuffer getByteBuffer(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte, int paramInt)
    {
      if (paramByteBuffer == null) {
        paramByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
      }
      paramByteBuffer.position(paramInt);
      return paramByteBuffer;
    }
    
    public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar)
    {
      int i = paramInt1 + paramInt2;
      int j = 0;
      int k = Math.min(paramInt2, paramArrayOfChar.length);
      ByteBuffer localByteBuffer = null;
      while ((j < k) && (paramArrayOfByte[paramInt1] >= 0)) {
        paramArrayOfChar[(j++)] = ((char)paramArrayOfByte[(paramInt1++)]);
      }
      while (paramInt1 < i)
      {
        int m = paramArrayOfByte[(paramInt1++)];
        if (m >= 0)
        {
          paramArrayOfChar[(j++)] = ((char)m);
        }
        else
        {
          int n;
          if ((m >> 5 == -2) && ((m & 0x1E) != 0))
          {
            if (paramInt1 < i)
            {
              n = paramArrayOfByte[(paramInt1++)];
              if (isNotContinuation(n))
              {
                if (malformedInputAction() != CodingErrorAction.REPLACE) {
                  return -1;
                }
                paramArrayOfChar[(j++)] = replacement().charAt(0);
                paramInt1--;
              }
              else
              {
                paramArrayOfChar[(j++)] = ((char)(m << 6 ^ n ^ 0xF80));
              }
            }
            else
            {
              if (malformedInputAction() != CodingErrorAction.REPLACE) {
                return -1;
              }
              paramArrayOfChar[(j++)] = replacement().charAt(0);
              return j;
            }
          }
          else
          {
            int i1;
            if (m >> 4 == -2)
            {
              if (paramInt1 + 1 < i)
              {
                n = paramArrayOfByte[(paramInt1++)];
                i1 = paramArrayOfByte[(paramInt1++)];
                if (isMalformed3(m, n, i1))
                {
                  if (malformedInputAction() != CodingErrorAction.REPLACE) {
                    return -1;
                  }
                  paramArrayOfChar[(j++)] = replacement().charAt(0);
                  paramInt1 -= 3;
                  localByteBuffer = getByteBuffer(localByteBuffer, paramArrayOfByte, paramInt1);
                  paramInt1 += malformedN(localByteBuffer, 3).length();
                }
                else
                {
                  char c = (char)(m << 12 ^ n << 6 ^ i1 ^ 0xFFFE1F80);
                  if (Character.isSurrogate(c))
                  {
                    if (malformedInputAction() != CodingErrorAction.REPLACE) {
                      return -1;
                    }
                    paramArrayOfChar[(j++)] = replacement().charAt(0);
                  }
                  else
                  {
                    paramArrayOfChar[(j++)] = c;
                  }
                }
              }
              else
              {
                if (malformedInputAction() != CodingErrorAction.REPLACE) {
                  return -1;
                }
                if ((paramInt1 < i) && (isMalformed3_2(m, paramArrayOfByte[paramInt1])))
                {
                  paramArrayOfChar[(j++)] = replacement().charAt(0);
                }
                else
                {
                  paramArrayOfChar[(j++)] = replacement().charAt(0);
                  return j;
                }
              }
            }
            else if (m >> 3 == -2)
            {
              if (paramInt1 + 2 < i)
              {
                n = paramArrayOfByte[(paramInt1++)];
                i1 = paramArrayOfByte[(paramInt1++)];
                int i2 = paramArrayOfByte[(paramInt1++)];
                int i3 = m << 18 ^ n << 12 ^ i1 << 6 ^ i2 ^ 0x381F80;
                if ((isMalformed4(n, i1, i2)) || (!Character.isSupplementaryCodePoint(i3)))
                {
                  if (malformedInputAction() != CodingErrorAction.REPLACE) {
                    return -1;
                  }
                  paramArrayOfChar[(j++)] = replacement().charAt(0);
                  paramInt1 -= 4;
                  localByteBuffer = getByteBuffer(localByteBuffer, paramArrayOfByte, paramInt1);
                  paramInt1 += malformedN(localByteBuffer, 4).length();
                }
                else
                {
                  paramArrayOfChar[(j++)] = Character.highSurrogate(i3);
                  paramArrayOfChar[(j++)] = Character.lowSurrogate(i3);
                }
              }
              else
              {
                if (malformedInputAction() != CodingErrorAction.REPLACE) {
                  return -1;
                }
                m &= 0xFF;
                if ((m > 244) || ((paramInt1 < i) && (isMalformed4_2(m, paramArrayOfByte[paramInt1] & 0xFF))))
                {
                  paramArrayOfChar[(j++)] = replacement().charAt(0);
                }
                else
                {
                  paramInt1++;
                  if ((paramInt1 < i) && (isMalformed4_3(paramArrayOfByte[paramInt1])))
                  {
                    paramArrayOfChar[(j++)] = replacement().charAt(0);
                  }
                  else
                  {
                    paramArrayOfChar[(j++)] = replacement().charAt(0);
                    return j;
                  }
                }
              }
            }
            else
            {
              if (malformedInputAction() != CodingErrorAction.REPLACE) {
                return -1;
              }
              paramArrayOfChar[(j++)] = replacement().charAt(0);
            }
          }
        }
      }
      return j;
    }
  }
  
  private static final class Encoder
    extends CharsetEncoder
    implements ArrayEncoder
  {
    private Surrogate.Parser sgp;
    private byte repl = 63;
    
    private Encoder(Charset paramCharset)
    {
      super(1.1F, 3.0F);
    }
    
    public boolean canEncode(char paramChar)
    {
      return !Character.isSurrogate(paramChar);
    }
    
    public boolean isLegalReplacement(byte[] paramArrayOfByte)
    {
      return ((paramArrayOfByte.length == 1) && (paramArrayOfByte[0] >= 0)) || (super.isLegalReplacement(paramArrayOfByte));
    }
    
    private static CoderResult overflow(CharBuffer paramCharBuffer, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2)
    {
      UTF_8.updatePositions(paramCharBuffer, paramInt1, paramByteBuffer, paramInt2);
      return CoderResult.OVERFLOW;
    }
    
    private static CoderResult overflow(CharBuffer paramCharBuffer, int paramInt)
    {
      paramCharBuffer.position(paramInt);
      return CoderResult.OVERFLOW;
    }
    
    private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      char[] arrayOfChar = paramCharBuffer.array();
      int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
      int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
      byte[] arrayOfByte = paramByteBuffer.array();
      int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
      int n = k + Math.min(j - i, m - k);
      while ((k < n) && (arrayOfChar[i] < '')) {
        arrayOfByte[(k++)] = ((byte)arrayOfChar[(i++)]);
      }
      while (i < j)
      {
        char c = arrayOfChar[i];
        if (c < '')
        {
          if (k >= m) {
            return overflow(paramCharBuffer, i, paramByteBuffer, k);
          }
          arrayOfByte[(k++)] = ((byte)c);
        }
        else if (c < 'ࠀ')
        {
          if (m - k < 2) {
            return overflow(paramCharBuffer, i, paramByteBuffer, k);
          }
          arrayOfByte[(k++)] = ((byte)(0xC0 | c >> '\006'));
          arrayOfByte[(k++)] = ((byte)(0x80 | c & 0x3F));
        }
        else if (Character.isSurrogate(c))
        {
          if (this.sgp == null) {
            this.sgp = new Surrogate.Parser();
          }
          int i1 = this.sgp.parse(c, arrayOfChar, i, j);
          if (i1 < 0)
          {
            UTF_8.updatePositions(paramCharBuffer, i, paramByteBuffer, k);
            return this.sgp.error();
          }
          if (m - k < 4) {
            return overflow(paramCharBuffer, i, paramByteBuffer, k);
          }
          arrayOfByte[(k++)] = ((byte)(0xF0 | i1 >> 18));
          arrayOfByte[(k++)] = ((byte)(0x80 | i1 >> 12 & 0x3F));
          arrayOfByte[(k++)] = ((byte)(0x80 | i1 >> 6 & 0x3F));
          arrayOfByte[(k++)] = ((byte)(0x80 | i1 & 0x3F));
          i++;
        }
        else
        {
          if (m - k < 3) {
            return overflow(paramCharBuffer, i, paramByteBuffer, k);
          }
          arrayOfByte[(k++)] = ((byte)(0xE0 | c >> '\f'));
          arrayOfByte[(k++)] = ((byte)(0x80 | c >> '\006' & 0x3F));
          arrayOfByte[(k++)] = ((byte)(0x80 | c & 0x3F));
        }
        i++;
      }
      UTF_8.updatePositions(paramCharBuffer, i, paramByteBuffer, k);
      return CoderResult.UNDERFLOW;
    }
    
    private CoderResult encodeBufferLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      for (int i = paramCharBuffer.position(); paramCharBuffer.hasRemaining(); i++)
      {
        char c = paramCharBuffer.get();
        if (c < '')
        {
          if (!paramByteBuffer.hasRemaining()) {
            return overflow(paramCharBuffer, i);
          }
          paramByteBuffer.put((byte)c);
        }
        else if (c < 'ࠀ')
        {
          if (paramByteBuffer.remaining() < 2) {
            return overflow(paramCharBuffer, i);
          }
          paramByteBuffer.put((byte)(0xC0 | c >> '\006'));
          paramByteBuffer.put((byte)(0x80 | c & 0x3F));
        }
        else if (Character.isSurrogate(c))
        {
          if (this.sgp == null) {
            this.sgp = new Surrogate.Parser();
          }
          int j = this.sgp.parse(c, paramCharBuffer);
          if (j < 0)
          {
            paramCharBuffer.position(i);
            return this.sgp.error();
          }
          if (paramByteBuffer.remaining() < 4) {
            return overflow(paramCharBuffer, i);
          }
          paramByteBuffer.put((byte)(0xF0 | j >> 18));
          paramByteBuffer.put((byte)(0x80 | j >> 12 & 0x3F));
          paramByteBuffer.put((byte)(0x80 | j >> 6 & 0x3F));
          paramByteBuffer.put((byte)(0x80 | j & 0x3F));
          i++;
        }
        else
        {
          if (paramByteBuffer.remaining() < 3) {
            return overflow(paramCharBuffer, i);
          }
          paramByteBuffer.put((byte)(0xE0 | c >> '\f'));
          paramByteBuffer.put((byte)(0x80 | c >> '\006' & 0x3F));
          paramByteBuffer.put((byte)(0x80 | c & 0x3F));
        }
      }
      paramCharBuffer.position(i);
      return CoderResult.UNDERFLOW;
    }
    
    protected final CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      if ((paramCharBuffer.hasArray()) && (paramByteBuffer.hasArray())) {
        return encodeArrayLoop(paramCharBuffer, paramByteBuffer);
      }
      return encodeBufferLoop(paramCharBuffer, paramByteBuffer);
    }
    
    protected void implReplaceWith(byte[] paramArrayOfByte)
    {
      this.repl = paramArrayOfByte[0];
    }
    
    public int encode(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      int i = paramInt1 + paramInt2;
      int j = 0;
      int k = j + Math.min(paramInt2, paramArrayOfByte.length);
      while ((j < k) && (paramArrayOfChar[paramInt1] < '')) {
        paramArrayOfByte[(j++)] = ((byte)paramArrayOfChar[(paramInt1++)]);
      }
      while (paramInt1 < i)
      {
        char c = paramArrayOfChar[(paramInt1++)];
        if (c < '')
        {
          paramArrayOfByte[(j++)] = ((byte)c);
        }
        else if (c < 'ࠀ')
        {
          paramArrayOfByte[(j++)] = ((byte)(0xC0 | c >> '\006'));
          paramArrayOfByte[(j++)] = ((byte)(0x80 | c & 0x3F));
        }
        else if (Character.isSurrogate(c))
        {
          if (this.sgp == null) {
            this.sgp = new Surrogate.Parser();
          }
          int m = this.sgp.parse(c, paramArrayOfChar, paramInt1 - 1, i);
          if (m < 0)
          {
            if (malformedInputAction() != CodingErrorAction.REPLACE) {
              return -1;
            }
            paramArrayOfByte[(j++)] = this.repl;
          }
          else
          {
            paramArrayOfByte[(j++)] = ((byte)(0xF0 | m >> 18));
            paramArrayOfByte[(j++)] = ((byte)(0x80 | m >> 12 & 0x3F));
            paramArrayOfByte[(j++)] = ((byte)(0x80 | m >> 6 & 0x3F));
            paramArrayOfByte[(j++)] = ((byte)(0x80 | m & 0x3F));
            paramInt1++;
          }
        }
        else
        {
          paramArrayOfByte[(j++)] = ((byte)(0xE0 | c >> '\f'));
          paramArrayOfByte[(j++)] = ((byte)(0x80 | c >> '\006' & 0x3F));
          paramArrayOfByte[(j++)] = ((byte)(0x80 | c & 0x3F));
        }
      }
      return j;
    }
  }
}
