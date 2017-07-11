package sun.nio.cs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;

public class StreamDecoder
  extends Reader
{
  private static final int MIN_BYTE_BUFFER_SIZE = 32;
  private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
  private volatile boolean isOpen = true;
  private boolean haveLeftoverChar = false;
  private char leftoverChar;
  private static volatile boolean channelsAvailable = true;
  private Charset cs;
  private CharsetDecoder decoder;
  private ByteBuffer bb;
  private InputStream in;
  private ReadableByteChannel ch;
  
  private void ensureOpen()
    throws IOException
  {
    if (!this.isOpen) {
      throw new IOException("Stream closed");
    }
  }
  
  public static StreamDecoder forInputStreamReader(InputStream paramInputStream, Object paramObject, String paramString)
    throws UnsupportedEncodingException
  {
    String str = paramString;
    if (str == null) {
      str = Charset.defaultCharset().name();
    }
    try
    {
      if (Charset.isSupported(str)) {
        return new StreamDecoder(paramInputStream, paramObject, Charset.forName(str));
      }
    }
    catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
    throw new UnsupportedEncodingException(str);
  }
  
  public static StreamDecoder forInputStreamReader(InputStream paramInputStream, Object paramObject, Charset paramCharset)
  {
    return new StreamDecoder(paramInputStream, paramObject, paramCharset);
  }
  
  public static StreamDecoder forInputStreamReader(InputStream paramInputStream, Object paramObject, CharsetDecoder paramCharsetDecoder)
  {
    return new StreamDecoder(paramInputStream, paramObject, paramCharsetDecoder);
  }
  
  public static StreamDecoder forDecoder(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder, int paramInt)
  {
    return new StreamDecoder(paramReadableByteChannel, paramCharsetDecoder, paramInt);
  }
  
  public String getEncoding()
  {
    if (isOpen()) {
      return encodingName();
    }
    return null;
  }
  
  public int read()
    throws IOException
  {
    return read0();
  }
  
  private int read0()
    throws IOException
  {
    synchronized (this.lock)
    {
      if (this.haveLeftoverChar)
      {
        this.haveLeftoverChar = false;
        return this.leftoverChar;
      }
      char[] arrayOfChar = new char[2];
      int i = read(arrayOfChar, 0, 2);
      switch (i)
      {
      case -1: 
        return -1;
      case 2: 
        this.leftoverChar = arrayOfChar[1];
        this.haveLeftoverChar = true;
      case 1: 
        return arrayOfChar[0];
      }
      if (!$assertionsDisabled) {
        throw new AssertionError(i);
      }
      return -1;
    }
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    int j = paramInt2;
    synchronized (this.lock)
    {
      ensureOpen();
      if ((i < 0) || (i > paramArrayOfChar.length) || (j < 0) || (i + j > paramArrayOfChar.length) || (i + j < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (j == 0) {
        return 0;
      }
      int k = 0;
      if (this.haveLeftoverChar)
      {
        paramArrayOfChar[i] = this.leftoverChar;
        i++;
        j--;
        this.haveLeftoverChar = false;
        k = 1;
        if ((j == 0) || (!implReady())) {
          return k;
        }
      }
      if (j == 1)
      {
        int m = read0();
        if (m == -1) {
          return k == 0 ? -1 : k;
        }
        paramArrayOfChar[i] = ((char)m);
        return k + 1;
      }
      return k + implRead(paramArrayOfChar, i, i + j);
    }
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();
      return (this.haveLeftoverChar) || (implReady());
    }
  }
  
  public void close()
    throws IOException
  {
    synchronized (this.lock)
    {
      if (!this.isOpen) {
        return;
      }
      implClose();
      this.isOpen = false;
    }
  }
  
  private boolean isOpen()
  {
    return this.isOpen;
  }
  
  private static FileChannel getChannel(FileInputStream paramFileInputStream)
  {
    if (!channelsAvailable) {
      return null;
    }
    try
    {
      return paramFileInputStream.getChannel();
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      channelsAvailable = false;
    }
    return null;
  }
  
  StreamDecoder(InputStream paramInputStream, Object paramObject, Charset paramCharset)
  {
    this(paramInputStream, paramObject, paramCharset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
  }
  
  StreamDecoder(InputStream paramInputStream, Object paramObject, CharsetDecoder paramCharsetDecoder)
  {
    super(paramObject);
    this.cs = paramCharsetDecoder.charset();
    this.decoder = paramCharsetDecoder;
    if (this.ch == null)
    {
      this.in = paramInputStream;
      this.ch = null;
      this.bb = ByteBuffer.allocate(8192);
    }
    this.bb.flip();
  }
  
  StreamDecoder(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder, int paramInt)
  {
    this.in = null;
    this.ch = paramReadableByteChannel;
    this.decoder = paramCharsetDecoder;
    this.cs = paramCharsetDecoder.charset();
    this.bb = ByteBuffer.allocate(paramInt < 32 ? 32 : paramInt < 0 ? 8192 : paramInt);
    this.bb.flip();
  }
  
  private int readBytes()
    throws IOException
  {
    this.bb.compact();
    try
    {
      int j;
      if (this.ch != null)
      {
        i = this.ch.read(this.bb);
        if (i < 0)
        {
          j = i;
          return j;
        }
      }
      else
      {
        i = this.bb.limit();
        j = this.bb.position();
        assert (j <= i);
        int k = j <= i ? i - j : 0;
        assert (k > 0);
        int m = this.in.read(this.bb.array(), this.bb.arrayOffset() + j, k);
        if (m < 0)
        {
          int n = m;
          return n;
        }
        if (m == 0) {
          throw new IOException("Underlying input stream returned zero bytes");
        }
        assert (m <= k) : ("n = " + m + ", rem = " + k);
        this.bb.position(j + m);
      }
    }
    finally
    {
      this.bb.flip();
    }
    int i = this.bb.remaining();
    assert (i != 0) : i;
    return i;
  }
  
  int implRead(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    assert (paramInt2 - paramInt1 > 1);
    CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2 - paramInt1);
    if (localCharBuffer.position() != 0) {
      localCharBuffer = localCharBuffer.slice();
    }
    boolean bool = false;
    for (;;)
    {
      CoderResult localCoderResult = this.decoder.decode(this.bb, localCharBuffer, bool);
      if (localCoderResult.isUnderflow())
      {
        if ((bool) || (!localCharBuffer.hasRemaining()) || ((localCharBuffer.position() > 0) && (!inReady()))) {
          break;
        }
        int i = readBytes();
        if (i < 0)
        {
          bool = true;
          if ((localCharBuffer.position() == 0) && (!this.bb.hasRemaining())) {
            break;
          }
          this.decoder.reset();
        }
      }
      else
      {
        if (localCoderResult.isOverflow())
        {
          if (($assertionsDisabled) || (localCharBuffer.position() > 0)) {
            break;
          }
          throw new AssertionError();
        }
        localCoderResult.throwException();
      }
    }
    if (bool) {
      this.decoder.reset();
    }
    if (localCharBuffer.position() == 0)
    {
      if (bool) {
        return -1;
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return localCharBuffer.position();
  }
  
  String encodingName()
  {
    return (this.cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
  }
  
  private boolean inReady()
  {
    try
    {
      return ((this.in != null) && (this.in.available() > 0)) || ((this.ch instanceof FileChannel));
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  boolean implReady()
  {
    return (this.bb.hasRemaining()) || (inReady());
  }
  
  void implClose()
    throws IOException
  {
    if (this.ch != null) {
      this.ch.close();
    } else {
      this.in.close();
    }
  }
}
