package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class AWTCharset
  extends Charset
{
  protected Charset awtCs;
  protected Charset javaCs;
  
  public AWTCharset(String paramString, Charset paramCharset)
  {
    super(paramString, null);
    this.javaCs = paramCharset;
    this.awtCs = this;
  }
  
  public boolean contains(Charset paramCharset)
  {
    if (this.javaCs == null) {
      return false;
    }
    return this.javaCs.contains(paramCharset);
  }
  
  public CharsetEncoder newEncoder()
  {
    if (this.javaCs == null) {
      throw new Error("Encoder is not supported by this Charset");
    }
    return new Encoder(this.javaCs.newEncoder());
  }
  
  public CharsetDecoder newDecoder()
  {
    if (this.javaCs == null) {
      throw new Error("Decoder is not supported by this Charset");
    }
    return new Decoder(this.javaCs.newDecoder());
  }
  
  public class Decoder
    extends CharsetDecoder
  {
    protected CharsetDecoder dec;
    private String nr;
    ByteBuffer fbb = ByteBuffer.allocate(0);
    
    protected Decoder()
    {
      this(AWTCharset.this.javaCs.newDecoder());
    }
    
    protected Decoder(CharsetDecoder paramCharsetDecoder)
    {
      super(paramCharsetDecoder.averageCharsPerByte(), paramCharsetDecoder.maxCharsPerByte());
      this.dec = paramCharsetDecoder;
    }
    
    protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      return this.dec.decode(paramByteBuffer, paramCharBuffer, true);
    }
    
    protected CoderResult implFlush(CharBuffer paramCharBuffer)
    {
      this.dec.decode(this.fbb, paramCharBuffer, true);
      return this.dec.flush(paramCharBuffer);
    }
    
    protected void implReset()
    {
      this.dec.reset();
    }
    
    protected void implReplaceWith(String paramString)
    {
      if (this.dec != null) {
        this.dec.replaceWith(paramString);
      }
    }
    
    protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction)
    {
      this.dec.onMalformedInput(paramCodingErrorAction);
    }
    
    protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction)
    {
      this.dec.onUnmappableCharacter(paramCodingErrorAction);
    }
  }
  
  public class Encoder
    extends CharsetEncoder
  {
    protected CharsetEncoder enc;
    
    protected Encoder()
    {
      this(AWTCharset.this.javaCs.newEncoder());
    }
    
    protected Encoder(CharsetEncoder paramCharsetEncoder)
    {
      super(paramCharsetEncoder.averageBytesPerChar(), paramCharsetEncoder.maxBytesPerChar());
      this.enc = paramCharsetEncoder;
    }
    
    public boolean canEncode(char paramChar)
    {
      return this.enc.canEncode(paramChar);
    }
    
    public boolean canEncode(CharSequence paramCharSequence)
    {
      return this.enc.canEncode(paramCharSequence);
    }
    
    protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      return this.enc.encode(paramCharBuffer, paramByteBuffer, true);
    }
    
    protected CoderResult implFlush(ByteBuffer paramByteBuffer)
    {
      return this.enc.flush(paramByteBuffer);
    }
    
    protected void implReset()
    {
      this.enc.reset();
    }
    
    protected void implReplaceWith(byte[] paramArrayOfByte)
    {
      if (this.enc != null) {
        this.enc.replaceWith(paramArrayOfByte);
      }
    }
    
    protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction)
    {
      this.enc.onMalformedInput(paramCodingErrorAction);
    }
    
    protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction)
    {
      this.enc.onUnmappableCharacter(paramCodingErrorAction);
    }
    
    public boolean isLegalReplacement(byte[] paramArrayOfByte)
    {
      return true;
    }
  }
}
