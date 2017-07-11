package java.nio.charset;

import java.lang.ref.WeakReference;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public abstract class CharsetEncoder
{
  private final Charset charset;
  private final float averageBytesPerChar;
  private final float maxBytesPerChar;
  private byte[] replacement;
  private CodingErrorAction malformedInputAction = CodingErrorAction.REPORT;
  private CodingErrorAction unmappableCharacterAction = CodingErrorAction.REPORT;
  private static final int ST_RESET = 0;
  private static final int ST_CODING = 1;
  private static final int ST_END = 2;
  private static final int ST_FLUSHED = 3;
  private int state = 0;
  private static String[] stateNames = { "RESET", "CODING", "CODING_END", "FLUSHED" };
  private WeakReference<CharsetDecoder> cachedDecoder = null;
  
  protected CharsetEncoder(Charset paramCharset, float paramFloat1, float paramFloat2, byte[] paramArrayOfByte)
  {
    this.charset = paramCharset;
    if (paramFloat1 <= 0.0F) {
      throw new IllegalArgumentException("Non-positive averageBytesPerChar");
    }
    if (paramFloat2 <= 0.0F) {
      throw new IllegalArgumentException("Non-positive maxBytesPerChar");
    }
    if ((!Charset.atBugLevel("1.4")) && (paramFloat1 > paramFloat2)) {
      throw new IllegalArgumentException("averageBytesPerChar exceeds maxBytesPerChar");
    }
    this.replacement = paramArrayOfByte;
    this.averageBytesPerChar = paramFloat1;
    this.maxBytesPerChar = paramFloat2;
    replaceWith(paramArrayOfByte);
  }
  
  protected CharsetEncoder(Charset paramCharset, float paramFloat1, float paramFloat2)
  {
    this(paramCharset, paramFloat1, paramFloat2, new byte[] { 63 });
  }
  
  public final Charset charset()
  {
    return this.charset;
  }
  
  public final byte[] replacement()
  {
    return Arrays.copyOf(this.replacement, this.replacement.length);
  }
  
  public final CharsetEncoder replaceWith(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("Null replacement");
    }
    int i = paramArrayOfByte.length;
    if (i == 0) {
      throw new IllegalArgumentException("Empty replacement");
    }
    if (i > this.maxBytesPerChar) {
      throw new IllegalArgumentException("Replacement too long");
    }
    if (!isLegalReplacement(paramArrayOfByte)) {
      throw new IllegalArgumentException("Illegal replacement");
    }
    this.replacement = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
    implReplaceWith(this.replacement);
    return this;
  }
  
  protected void implReplaceWith(byte[] paramArrayOfByte) {}
  
  public boolean isLegalReplacement(byte[] paramArrayOfByte)
  {
    WeakReference localWeakReference = this.cachedDecoder;
    CharsetDecoder localCharsetDecoder = null;
    if ((localWeakReference == null) || ((localCharsetDecoder = (CharsetDecoder)localWeakReference.get()) == null))
    {
      localCharsetDecoder = charset().newDecoder();
      localCharsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
      localCharsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
      this.cachedDecoder = new WeakReference(localCharsetDecoder);
    }
    else
    {
      localCharsetDecoder.reset();
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    CharBuffer localCharBuffer = CharBuffer.allocate((int)(localByteBuffer.remaining() * localCharsetDecoder.maxCharsPerByte()));
    CoderResult localCoderResult = localCharsetDecoder.decode(localByteBuffer, localCharBuffer, true);
    return !localCoderResult.isError();
  }
  
  public CodingErrorAction malformedInputAction()
  {
    return this.malformedInputAction;
  }
  
  public final CharsetEncoder onMalformedInput(CodingErrorAction paramCodingErrorAction)
  {
    if (paramCodingErrorAction == null) {
      throw new IllegalArgumentException("Null action");
    }
    this.malformedInputAction = paramCodingErrorAction;
    implOnMalformedInput(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction) {}
  
  public CodingErrorAction unmappableCharacterAction()
  {
    return this.unmappableCharacterAction;
  }
  
  public final CharsetEncoder onUnmappableCharacter(CodingErrorAction paramCodingErrorAction)
  {
    if (paramCodingErrorAction == null) {
      throw new IllegalArgumentException("Null action");
    }
    this.unmappableCharacterAction = paramCodingErrorAction;
    implOnUnmappableCharacter(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction) {}
  
  public final float averageBytesPerChar()
  {
    return this.averageBytesPerChar;
  }
  
  public final float maxBytesPerChar()
  {
    return this.maxBytesPerChar;
  }
  
  public final CoderResult encode(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer, boolean paramBoolean)
  {
    int i = paramBoolean ? 2 : 1;
    if ((this.state != 0) && (this.state != 1) && ((!paramBoolean) || (this.state != 2))) {
      throwIllegalStateException(this.state, i);
    }
    this.state = i;
    for (;;)
    {
      CoderResult localCoderResult;
      try
      {
        localCoderResult = encodeLoop(paramCharBuffer, paramByteBuffer);
      }
      catch (BufferUnderflowException localBufferUnderflowException)
      {
        throw new CoderMalfunctionError(localBufferUnderflowException);
      }
      catch (BufferOverflowException localBufferOverflowException)
      {
        throw new CoderMalfunctionError(localBufferOverflowException);
      }
      if (localCoderResult.isOverflow()) {
        return localCoderResult;
      }
      if (localCoderResult.isUnderflow()) {
        if ((paramBoolean) && (paramCharBuffer.hasRemaining())) {
          localCoderResult = CoderResult.malformedForLength(paramCharBuffer.remaining());
        } else {
          return localCoderResult;
        }
      }
      CodingErrorAction localCodingErrorAction = null;
      if (localCoderResult.isMalformed()) {
        localCodingErrorAction = this.malformedInputAction;
      } else if (localCoderResult.isUnmappable()) {
        localCodingErrorAction = this.unmappableCharacterAction;
      } else if (!$assertionsDisabled) {
        throw new AssertionError(localCoderResult.toString());
      }
      if (localCodingErrorAction == CodingErrorAction.REPORT) {
        return localCoderResult;
      }
      if (localCodingErrorAction == CodingErrorAction.REPLACE)
      {
        if (paramByteBuffer.remaining() < this.replacement.length) {
          return CoderResult.OVERFLOW;
        }
        paramByteBuffer.put(this.replacement);
      }
      if ((localCodingErrorAction == CodingErrorAction.IGNORE) || (localCodingErrorAction == CodingErrorAction.REPLACE)) {
        paramCharBuffer.position(paramCharBuffer.position() + localCoderResult.length());
      } else if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
  }
  
  public final CoderResult flush(ByteBuffer paramByteBuffer)
  {
    if (this.state == 2)
    {
      CoderResult localCoderResult = implFlush(paramByteBuffer);
      if (localCoderResult.isUnderflow()) {
        this.state = 3;
      }
      return localCoderResult;
    }
    if (this.state != 3) {
      throwIllegalStateException(this.state, 3);
    }
    return CoderResult.UNDERFLOW;
  }
  
  protected CoderResult implFlush(ByteBuffer paramByteBuffer)
  {
    return CoderResult.UNDERFLOW;
  }
  
  public final CharsetEncoder reset()
  {
    implReset();
    this.state = 0;
    return this;
  }
  
  protected void implReset() {}
  
  protected abstract CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer);
  
  public final ByteBuffer encode(CharBuffer paramCharBuffer)
    throws CharacterCodingException
  {
    int i = (int)(paramCharBuffer.remaining() * averageBytesPerChar());
    Object localObject = ByteBuffer.allocate(i);
    if ((i == 0) && (paramCharBuffer.remaining() == 0)) {
      return localObject;
    }
    reset();
    for (;;)
    {
      CoderResult localCoderResult = paramCharBuffer.hasRemaining() ? encode(paramCharBuffer, (ByteBuffer)localObject, true) : CoderResult.UNDERFLOW;
      if (localCoderResult.isUnderflow()) {
        localCoderResult = flush((ByteBuffer)localObject);
      }
      if (localCoderResult.isUnderflow()) {
        break;
      }
      if (localCoderResult.isOverflow())
      {
        i = 2 * i + 1;
        ByteBuffer localByteBuffer = ByteBuffer.allocate(i);
        ((ByteBuffer)localObject).flip();
        localByteBuffer.put((ByteBuffer)localObject);
        localObject = localByteBuffer;
      }
      else
      {
        localCoderResult.throwException();
      }
    }
    ((ByteBuffer)localObject).flip();
    return localObject;
  }
  
  private boolean canEncode(CharBuffer paramCharBuffer)
  {
    if (this.state == 3) {
      reset();
    } else if (this.state != 0) {
      throwIllegalStateException(this.state, 1);
    }
    CodingErrorAction localCodingErrorAction1 = malformedInputAction();
    CodingErrorAction localCodingErrorAction2 = unmappableCharacterAction();
    try
    {
      onMalformedInput(CodingErrorAction.REPORT);
      onUnmappableCharacter(CodingErrorAction.REPORT);
      encode(paramCharBuffer);
    }
    catch (CharacterCodingException localCharacterCodingException)
    {
      boolean bool = false;
      return bool;
    }
    finally
    {
      onMalformedInput(localCodingErrorAction1);
      onUnmappableCharacter(localCodingErrorAction2);
      reset();
    }
    return true;
  }
  
  public boolean canEncode(char paramChar)
  {
    CharBuffer localCharBuffer = CharBuffer.allocate(1);
    localCharBuffer.put(paramChar);
    localCharBuffer.flip();
    return canEncode(localCharBuffer);
  }
  
  public boolean canEncode(CharSequence paramCharSequence)
  {
    CharBuffer localCharBuffer;
    if ((paramCharSequence instanceof CharBuffer)) {
      localCharBuffer = ((CharBuffer)paramCharSequence).duplicate();
    } else {
      localCharBuffer = CharBuffer.wrap(paramCharSequence.toString());
    }
    return canEncode(localCharBuffer);
  }
  
  private void throwIllegalStateException(int paramInt1, int paramInt2)
  {
    throw new IllegalStateException("Current state = " + stateNames[paramInt1] + ", new state = " + stateNames[paramInt2]);
  }
}
