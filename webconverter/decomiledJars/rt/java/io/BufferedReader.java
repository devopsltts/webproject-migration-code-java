package java.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BufferedReader
  extends Reader
{
  private Reader in;
  private char[] cb;
  private int nChars;
  private int nextChar;
  private static final int INVALIDATED = -2;
  private static final int UNMARKED = -1;
  private int markedChar = -1;
  private int readAheadLimit = 0;
  private boolean skipLF = false;
  private boolean markedSkipLF = false;
  private static int defaultCharBufferSize = 8192;
  private static int defaultExpectedLineLength = 80;
  
  public BufferedReader(Reader paramReader, int paramInt)
  {
    super(paramReader);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    this.in = paramReader;
    this.cb = new char[paramInt];
    this.nextChar = (this.nChars = 0);
  }
  
  public BufferedReader(Reader paramReader)
  {
    this(paramReader, defaultCharBufferSize);
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (this.in == null) {
      throw new IOException("Stream closed");
    }
  }
  
  private void fill()
    throws IOException
  {
    int i;
    int j;
    if (this.markedChar <= -1)
    {
      i = 0;
    }
    else
    {
      j = this.nextChar - this.markedChar;
      if (j >= this.readAheadLimit)
      {
        this.markedChar = -2;
        this.readAheadLimit = 0;
        i = 0;
      }
      else
      {
        if (this.readAheadLimit <= this.cb.length)
        {
          System.arraycopy(this.cb, this.markedChar, this.cb, 0, j);
          this.markedChar = 0;
          i = j;
        }
        else
        {
          char[] arrayOfChar = new char[this.readAheadLimit];
          System.arraycopy(this.cb, this.markedChar, arrayOfChar, 0, j);
          this.cb = arrayOfChar;
          this.markedChar = 0;
          i = j;
        }
        this.nextChar = (this.nChars = j);
      }
    }
    do
    {
      j = this.in.read(this.cb, i, this.cb.length - i);
    } while (j == 0);
    if (j > 0)
    {
      this.nChars = (i + j);
      this.nextChar = i;
    }
  }
  
  public int read()
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();
      for (;;)
      {
        if (this.nextChar >= this.nChars)
        {
          fill();
          if (this.nextChar >= this.nChars) {
            return -1;
          }
        }
        if (!this.skipLF) {
          break;
        }
        this.skipLF = false;
        if (this.cb[this.nextChar] != '\n') {
          break;
        }
        this.nextChar += 1;
      }
      return this.cb[(this.nextChar++)];
    }
  }
  
  private int read1(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.nextChar >= this.nChars)
    {
      if ((paramInt2 >= this.cb.length) && (this.markedChar <= -1) && (!this.skipLF)) {
        return this.in.read(paramArrayOfChar, paramInt1, paramInt2);
      }
      fill();
    }
    if (this.nextChar >= this.nChars) {
      return -1;
    }
    if (this.skipLF)
    {
      this.skipLF = false;
      if (this.cb[this.nextChar] == '\n')
      {
        this.nextChar += 1;
        if (this.nextChar >= this.nChars) {
          fill();
        }
        if (this.nextChar >= this.nChars) {
          return -1;
        }
      }
    }
    int i = Math.min(paramInt2, this.nChars - this.nextChar);
    System.arraycopy(this.cb, this.nextChar, paramArrayOfChar, paramInt1, i);
    this.nextChar += i;
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return 0;
      }
      int i = read1(paramArrayOfChar, paramInt1, paramInt2);
      if (i <= 0) {
        return i;
      }
      while ((i < paramInt2) && (this.in.ready()))
      {
        int j = read1(paramArrayOfChar, paramInt1 + i, paramInt2 - i);
        if (j <= 0) {
          break;
        }
        i += j;
      }
      return i;
    }
  }
  
  String readLine(boolean paramBoolean)
    throws IOException
  {
    StringBuffer localStringBuffer = null;
    synchronized (this.lock)
    {
      ensureOpen();
      int j = (paramBoolean) || (this.skipLF) ? 1 : 0;
      if (this.nextChar >= this.nChars) {
        fill();
      }
      if (this.nextChar >= this.nChars)
      {
        if ((localStringBuffer != null) && (localStringBuffer.length() > 0)) {
          return localStringBuffer.toString();
        }
        return null;
      }
      int k = 0;
      int m = 0;
      if ((j != 0) && (this.cb[this.nextChar] == '\n')) {
        this.nextChar += 1;
      }
      this.skipLF = false;
      j = 0;
      for (int n = this.nextChar; n < this.nChars; n++)
      {
        m = this.cb[n];
        if ((m == 10) || (m == 13))
        {
          k = 1;
          break;
        }
      }
      int i = this.nextChar;
      this.nextChar = n;
      if (k != 0)
      {
        String str;
        if (localStringBuffer == null)
        {
          str = new String(this.cb, i, n - i);
        }
        else
        {
          localStringBuffer.append(this.cb, i, n - i);
          str = localStringBuffer.toString();
        }
        this.nextChar += 1;
        if (m == 13) {
          this.skipLF = true;
        }
        return str;
      }
      if (localStringBuffer == null) {
        localStringBuffer = new StringBuffer(defaultExpectedLineLength);
      }
      localStringBuffer.append(this.cb, i, n - i);
    }
  }
  
  public String readLine()
    throws IOException
  {
    return readLine(false);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("skip value is negative");
    }
    synchronized (this.lock)
    {
      ensureOpen();
      long l1 = paramLong;
      while (l1 > 0L)
      {
        if (this.nextChar >= this.nChars) {
          fill();
        }
        if (this.nextChar >= this.nChars) {
          break;
        }
        if (this.skipLF)
        {
          this.skipLF = false;
          if (this.cb[this.nextChar] == '\n') {
            this.nextChar += 1;
          }
        }
        long l2 = this.nChars - this.nextChar;
        if (l1 <= l2)
        {
          this.nextChar = ((int)(this.nextChar + l1));
          l1 = 0L;
          break;
        }
        l1 -= l2;
        this.nextChar = this.nChars;
      }
      return paramLong - l1;
    }
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();
      if (this.skipLF)
      {
        if ((this.nextChar >= this.nChars) && (this.in.ready())) {
          fill();
        }
        if (this.nextChar < this.nChars)
        {
          if (this.cb[this.nextChar] == '\n') {
            this.nextChar += 1;
          }
          this.skipLF = false;
        }
      }
      return (this.nextChar < this.nChars) || (this.in.ready());
    }
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Read-ahead limit < 0");
    }
    synchronized (this.lock)
    {
      ensureOpen();
      this.readAheadLimit = paramInt;
      this.markedChar = this.nextChar;
      this.markedSkipLF = this.skipLF;
    }
  }
  
  public void reset()
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();
      if (this.markedChar < 0) {
        throw new IOException(this.markedChar == -2 ? "Mark invalid" : "Stream not marked");
      }
      this.nextChar = this.markedChar;
      this.skipLF = this.markedSkipLF;
    }
  }
  
  public void close()
    throws IOException
  {
    synchronized (this.lock)
    {
      if (this.in == null) {
        return;
      }
      try
      {
        this.in.close();
        this.in = null;
        this.cb = null;
      }
      finally
      {
        this.in = null;
        this.cb = null;
      }
    }
  }
  
  public Stream<String> lines()
  {
    Iterator local1 = new Iterator()
    {
      String nextLine = null;
      
      public boolean hasNext()
      {
        if (this.nextLine != null) {
          return true;
        }
        try
        {
          this.nextLine = BufferedReader.this.readLine();
          return this.nextLine != null;
        }
        catch (IOException localIOException)
        {
          throw new UncheckedIOException(localIOException);
        }
      }
      
      public String next()
      {
        if ((this.nextLine != null) || (hasNext()))
        {
          String str = this.nextLine;
          this.nextLine = null;
          return str;
        }
        throw new NoSuchElementException();
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(local1, 272), false);
  }
}
