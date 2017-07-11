package java.lang;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import sun.misc.MessageUtils;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;
import sun.nio.cs.HistoricallyNamedCharset;

class StringCoding
{
  private static final ThreadLocal<SoftReference<StringDecoder>> decoder = new ThreadLocal();
  private static final ThreadLocal<SoftReference<StringEncoder>> encoder = new ThreadLocal();
  private static boolean warnUnsupportedCharset = true;
  
  private StringCoding() {}
  
  private static <T> T deref(ThreadLocal<SoftReference<T>> paramThreadLocal)
  {
    SoftReference localSoftReference = (SoftReference)paramThreadLocal.get();
    if (localSoftReference == null) {
      return null;
    }
    return localSoftReference.get();
  }
  
  private static <T> void set(ThreadLocal<SoftReference<T>> paramThreadLocal, T paramT)
  {
    paramThreadLocal.set(new SoftReference(paramT));
  }
  
  private static byte[] safeTrim(byte[] paramArrayOfByte, int paramInt, Charset paramCharset, boolean paramBoolean)
  {
    if ((paramInt == paramArrayOfByte.length) && ((paramBoolean) || (System.getSecurityManager() == null))) {
      return paramArrayOfByte;
    }
    return Arrays.copyOf(paramArrayOfByte, paramInt);
  }
  
  private static char[] safeTrim(char[] paramArrayOfChar, int paramInt, Charset paramCharset, boolean paramBoolean)
  {
    if ((paramInt == paramArrayOfChar.length) && ((paramBoolean) || (System.getSecurityManager() == null))) {
      return paramArrayOfChar;
    }
    return Arrays.copyOf(paramArrayOfChar, paramInt);
  }
  
  private static int scale(int paramInt, float paramFloat)
  {
    return (int)(paramInt * paramFloat);
  }
  
  private static Charset lookupCharset(String paramString)
  {
    if (Charset.isSupported(paramString)) {
      try
      {
        return Charset.forName(paramString);
      }
      catch (UnsupportedCharsetException localUnsupportedCharsetException)
      {
        throw new Error(localUnsupportedCharsetException);
      }
    }
    return null;
  }
  
  private static void warnUnsupportedCharset(String paramString)
  {
    if (warnUnsupportedCharset)
    {
      MessageUtils.err("WARNING: Default charset " + paramString + " not supported, using ISO-8859-1 instead");
      warnUnsupportedCharset = false;
    }
  }
  
  static char[] decode(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws UnsupportedEncodingException
  {
    StringDecoder localStringDecoder = (StringDecoder)deref(decoder);
    String str = paramString == null ? "ISO-8859-1" : paramString;
    if ((localStringDecoder == null) || ((!str.equals(localStringDecoder.requestedCharsetName())) && (!str.equals(localStringDecoder.charsetName()))))
    {
      localStringDecoder = null;
      try
      {
        Charset localCharset = lookupCharset(str);
        if (localCharset != null) {
          localStringDecoder = new StringDecoder(localCharset, str, null);
        }
      }
      catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
      if (localStringDecoder == null) {
        throw new UnsupportedEncodingException(str);
      }
      set(decoder, localStringDecoder);
    }
    return localStringDecoder.decode(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  static char[] decode(Charset paramCharset, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    CharsetDecoder localCharsetDecoder = paramCharset.newDecoder();
    int i = scale(paramInt2, localCharsetDecoder.maxCharsPerByte());
    char[] arrayOfChar = new char[i];
    if (paramInt2 == 0) {
      return arrayOfChar;
    }
    boolean bool = false;
    if (System.getSecurityManager() != null) {
      if ((bool = paramCharset.getClass().getClassLoader0() == null ? 1 : 0) == 0)
      {
        paramArrayOfByte = Arrays.copyOfRange(paramArrayOfByte, paramInt1, paramInt1 + paramInt2);
        paramInt1 = 0;
      }
    }
    localCharsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
    if ((localCharsetDecoder instanceof ArrayDecoder))
    {
      int j = ((ArrayDecoder)localCharsetDecoder).decode(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar);
      return safeTrim(arrayOfChar, j, paramCharset, bool);
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2);
    CharBuffer localCharBuffer = CharBuffer.wrap(arrayOfChar);
    try
    {
      CoderResult localCoderResult = localCharsetDecoder.decode(localByteBuffer, localCharBuffer, true);
      if (!localCoderResult.isUnderflow()) {
        localCoderResult.throwException();
      }
      localCoderResult = localCharsetDecoder.flush(localCharBuffer);
      if (!localCoderResult.isUnderflow()) {
        localCoderResult.throwException();
      }
    }
    catch (CharacterCodingException localCharacterCodingException)
    {
      throw new Error(localCharacterCodingException);
    }
    return safeTrim(arrayOfChar, localCharBuffer.position(), paramCharset, bool);
  }
  
  static char[] decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    String str = Charset.defaultCharset().name();
    try
    {
      return decode(str, paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException1)
    {
      warnUnsupportedCharset(str);
      try
      {
        return decode("ISO-8859-1", paramArrayOfByte, paramInt1, paramInt2);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException2)
      {
        MessageUtils.err("ISO-8859-1 charset not available: " + localUnsupportedEncodingException2.toString());
        System.exit(1);
      }
    }
    return null;
  }
  
  static byte[] encode(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws UnsupportedEncodingException
  {
    StringEncoder localStringEncoder = (StringEncoder)deref(encoder);
    String str = paramString == null ? "ISO-8859-1" : paramString;
    if ((localStringEncoder == null) || ((!str.equals(localStringEncoder.requestedCharsetName())) && (!str.equals(localStringEncoder.charsetName()))))
    {
      localStringEncoder = null;
      try
      {
        Charset localCharset = lookupCharset(str);
        if (localCharset != null) {
          localStringEncoder = new StringEncoder(localCharset, str, null);
        }
      }
      catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
      if (localStringEncoder == null) {
        throw new UnsupportedEncodingException(str);
      }
      set(encoder, localStringEncoder);
    }
    return localStringEncoder.encode(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  static byte[] encode(Charset paramCharset, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    CharsetEncoder localCharsetEncoder = paramCharset.newEncoder();
    int i = scale(paramInt2, localCharsetEncoder.maxBytesPerChar());
    byte[] arrayOfByte = new byte[i];
    if (paramInt2 == 0) {
      return arrayOfByte;
    }
    boolean bool = false;
    if (System.getSecurityManager() != null) {
      if ((bool = paramCharset.getClass().getClassLoader0() == null ? 1 : 0) == 0)
      {
        paramArrayOfChar = Arrays.copyOfRange(paramArrayOfChar, paramInt1, paramInt1 + paramInt2);
        paramInt1 = 0;
      }
    }
    localCharsetEncoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
    if ((localCharsetEncoder instanceof ArrayEncoder))
    {
      int j = ((ArrayEncoder)localCharsetEncoder).encode(paramArrayOfChar, paramInt1, paramInt2, arrayOfByte);
      return safeTrim(arrayOfByte, j, paramCharset, bool);
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
    CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    try
    {
      CoderResult localCoderResult = localCharsetEncoder.encode(localCharBuffer, localByteBuffer, true);
      if (!localCoderResult.isUnderflow()) {
        localCoderResult.throwException();
      }
      localCoderResult = localCharsetEncoder.flush(localByteBuffer);
      if (!localCoderResult.isUnderflow()) {
        localCoderResult.throwException();
      }
    }
    catch (CharacterCodingException localCharacterCodingException)
    {
      throw new Error(localCharacterCodingException);
    }
    return safeTrim(arrayOfByte, localByteBuffer.position(), paramCharset, bool);
  }
  
  static byte[] encode(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    String str = Charset.defaultCharset().name();
    try
    {
      return encode(str, paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException1)
    {
      warnUnsupportedCharset(str);
      try
      {
        return encode("ISO-8859-1", paramArrayOfChar, paramInt1, paramInt2);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException2)
      {
        MessageUtils.err("ISO-8859-1 charset not available: " + localUnsupportedEncodingException2.toString());
        System.exit(1);
      }
    }
    return null;
  }
  
  private static class StringDecoder
  {
    private final String requestedCharsetName;
    private final Charset cs;
    private final CharsetDecoder cd;
    private final boolean isTrusted;
    
    private StringDecoder(Charset paramCharset, String paramString)
    {
      this.requestedCharsetName = paramString;
      this.cs = paramCharset;
      this.cd = paramCharset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.isTrusted = (paramCharset.getClass().getClassLoader0() == null);
    }
    
    String charsetName()
    {
      if ((this.cs instanceof HistoricallyNamedCharset)) {
        return ((HistoricallyNamedCharset)this.cs).historicalName();
      }
      return this.cs.name();
    }
    
    final String requestedCharsetName()
    {
      return this.requestedCharsetName;
    }
    
    char[] decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      int i = StringCoding.scale(paramInt2, this.cd.maxCharsPerByte());
      char[] arrayOfChar = new char[i];
      if (paramInt2 == 0) {
        return arrayOfChar;
      }
      if ((this.cd instanceof ArrayDecoder))
      {
        int j = ((ArrayDecoder)this.cd).decode(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar);
        return StringCoding.safeTrim(arrayOfChar, j, this.cs, this.isTrusted);
      }
      this.cd.reset();
      ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2);
      CharBuffer localCharBuffer = CharBuffer.wrap(arrayOfChar);
      try
      {
        CoderResult localCoderResult = this.cd.decode(localByteBuffer, localCharBuffer, true);
        if (!localCoderResult.isUnderflow()) {
          localCoderResult.throwException();
        }
        localCoderResult = this.cd.flush(localCharBuffer);
        if (!localCoderResult.isUnderflow()) {
          localCoderResult.throwException();
        }
      }
      catch (CharacterCodingException localCharacterCodingException)
      {
        throw new Error(localCharacterCodingException);
      }
      return StringCoding.safeTrim(arrayOfChar, localCharBuffer.position(), this.cs, this.isTrusted);
    }
  }
  
  private static class StringEncoder
  {
    private Charset cs;
    private CharsetEncoder ce;
    private final String requestedCharsetName;
    private final boolean isTrusted;
    
    private StringEncoder(Charset paramCharset, String paramString)
    {
      this.requestedCharsetName = paramString;
      this.cs = paramCharset;
      this.ce = paramCharset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.isTrusted = (paramCharset.getClass().getClassLoader0() == null);
    }
    
    String charsetName()
    {
      if ((this.cs instanceof HistoricallyNamedCharset)) {
        return ((HistoricallyNamedCharset)this.cs).historicalName();
      }
      return this.cs.name();
    }
    
    final String requestedCharsetName()
    {
      return this.requestedCharsetName;
    }
    
    byte[] encode(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      int i = StringCoding.scale(paramInt2, this.ce.maxBytesPerChar());
      byte[] arrayOfByte = new byte[i];
      if (paramInt2 == 0) {
        return arrayOfByte;
      }
      if ((this.ce instanceof ArrayEncoder))
      {
        int j = ((ArrayEncoder)this.ce).encode(paramArrayOfChar, paramInt1, paramInt2, arrayOfByte);
        return StringCoding.safeTrim(arrayOfByte, j, this.cs, this.isTrusted);
      }
      this.ce.reset();
      ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
      CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
      try
      {
        CoderResult localCoderResult = this.ce.encode(localCharBuffer, localByteBuffer, true);
        if (!localCoderResult.isUnderflow()) {
          localCoderResult.throwException();
        }
        localCoderResult = this.ce.flush(localByteBuffer);
        if (!localCoderResult.isUnderflow()) {
          localCoderResult.throwException();
        }
      }
      catch (CharacterCodingException localCharacterCodingException)
      {
        throw new Error(localCharacterCodingException);
      }
      return StringCoding.safeTrim(arrayOfByte, localByteBuffer.position(), this.cs, this.isTrusted);
    }
  }
}
