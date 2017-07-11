package java.io;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import sun.misc.JavaIOAccess;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Console
  implements Flushable
{
  private Object readLock = new Object();
  private Object writeLock = new Object();
  private Reader reader;
  private Writer out;
  private PrintWriter pw;
  private Formatter formatter;
  private Charset cs;
  private char[] rcb;
  private static boolean echoOff;
  private static Console cons;
  
  public PrintWriter writer()
  {
    return this.pw;
  }
  
  public Reader reader()
  {
    return this.reader;
  }
  
  public Console format(String paramString, Object... paramVarArgs)
  {
    this.formatter.format(paramString, paramVarArgs).flush();
    return this;
  }
  
  public Console printf(String paramString, Object... paramVarArgs)
  {
    return format(paramString, paramVarArgs);
  }
  
  public String readLine(String paramString, Object... paramVarArgs)
  {
    String str = null;
    synchronized (this.writeLock)
    {
      synchronized (this.readLock)
      {
        if (paramString.length() != 0) {
          this.pw.format(paramString, paramVarArgs);
        }
        try
        {
          char[] arrayOfChar = readline(false);
          if (arrayOfChar != null) {
            str = new String(arrayOfChar);
          }
        }
        catch (IOException localIOException)
        {
          throw new IOError(localIOException);
        }
      }
    }
    return str;
  }
  
  public String readLine()
  {
    return readLine("", new Object[0]);
  }
  
  public char[] readPassword(String paramString, Object... paramVarArgs)
  {
    char[] arrayOfChar = null;
    synchronized (this.writeLock)
    {
      synchronized (this.readLock)
      {
        try
        {
          echoOff = echo(false);
        }
        catch (IOException localIOException1)
        {
          throw new IOError(localIOException1);
        }
        IOError localIOError = null;
        try
        {
          if (paramString.length() != 0) {
            this.pw.format(paramString, paramVarArgs);
          }
          arrayOfChar = readline(true);
        }
        catch (IOException localIOException3)
        {
          localIOError = new IOError(localIOException3);
        }
        finally
        {
          try
          {
            echoOff = echo(true);
          }
          catch (IOException localIOException5)
          {
            if (localIOError == null) {
              localIOError = new IOError(localIOException5);
            } else {
              localIOError.addSuppressed(localIOException5);
            }
          }
          if (localIOError != null) {
            throw localIOError;
          }
        }
        this.pw.println();
      }
    }
    return arrayOfChar;
  }
  
  public char[] readPassword()
  {
    return readPassword("", new Object[0]);
  }
  
  public void flush()
  {
    this.pw.flush();
  }
  
  private static native String encoding();
  
  private static native boolean echo(boolean paramBoolean)
    throws IOException;
  
  private char[] readline(boolean paramBoolean)
    throws IOException
  {
    int i = this.reader.read(this.rcb, 0, this.rcb.length);
    if (i < 0) {
      return null;
    }
    if (this.rcb[(i - 1)] == '\r')
    {
      i--;
    }
    else if (this.rcb[(i - 1)] == '\n')
    {
      i--;
      if ((i > 0) && (this.rcb[(i - 1)] == '\r')) {
        i--;
      }
    }
    char[] arrayOfChar = new char[i];
    if (i > 0)
    {
      System.arraycopy(this.rcb, 0, arrayOfChar, 0, i);
      if (paramBoolean) {
        Arrays.fill(this.rcb, 0, i, ' ');
      }
    }
    return arrayOfChar;
  }
  
  private char[] grow()
  {
    assert (Thread.holdsLock(this.readLock));
    char[] arrayOfChar = new char[this.rcb.length * 2];
    System.arraycopy(this.rcb, 0, arrayOfChar, 0, this.rcb.length);
    this.rcb = arrayOfChar;
    return this.rcb;
  }
  
  private static native boolean istty();
  
  private Console()
  {
    String str = encoding();
    if (str != null) {
      try
      {
        this.cs = Charset.forName(str);
      }
      catch (Exception localException) {}
    }
    if (this.cs == null) {
      this.cs = Charset.defaultCharset();
    }
    this.out = StreamEncoder.forOutputStreamWriter(new FileOutputStream(FileDescriptor.out), this.writeLock, this.cs);
    this.pw = new PrintWriter(this.out, true)
    {
      public void close() {}
    };
    this.formatter = new Formatter(this.out);
    this.reader = new LineReader(StreamDecoder.forInputStreamReader(new FileInputStream(FileDescriptor.in), this.readLock, this.cs));
    this.rcb = new char['Ѐ'];
  }
  
  static
  {
    try
    {
      SharedSecrets.getJavaLangAccess().registerShutdownHook(0, false, new Runnable()
      {
        public void run()
        {
          try
          {
            if (Console.echoOff) {
              Console.echo(true);
            }
          }
          catch (IOException localIOException) {}
        }
      });
    }
    catch (IllegalStateException localIllegalStateException) {}
    SharedSecrets.setJavaIOAccess(new JavaIOAccess()
    {
      public Console console()
      {
        if (Console.access$500())
        {
          if (Console.cons == null) {
            Console.access$602(new Console(null));
          }
          return Console.cons;
        }
        return null;
      }
      
      public Charset charset()
      {
        return Console.cons.cs;
      }
    });
  }
  
  class LineReader
    extends Reader
  {
    private Reader in;
    private char[] cb;
    private int nChars;
    private int nextChar;
    boolean leftoverLF;
    
    LineReader(Reader paramReader)
    {
      this.in = paramReader;
      this.cb = new char['Ѐ'];
      this.nextChar = (this.nChars = 0);
      this.leftoverLF = false;
    }
    
    public void close() {}
    
    public boolean ready()
      throws IOException
    {
      return this.in.ready();
    }
    
    public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (j < 0) || (j > paramArrayOfChar.length)) {
        throw new IndexOutOfBoundsException();
      }
      synchronized (Console.this.readLock)
      {
        int k = 0;
        int m = 0;
        do
        {
          if (this.nextChar >= this.nChars)
          {
            int n = 0;
            do
            {
              n = this.in.read(this.cb, 0, this.cb.length);
            } while (n == 0);
            if (n > 0)
            {
              this.nChars = n;
              this.nextChar = 0;
              if ((n < this.cb.length) && (this.cb[(n - 1)] != '\n') && (this.cb[(n - 1)] != '\r')) {
                k = 1;
              }
            }
            else
            {
              if (i - paramInt1 == 0) {
                return -1;
              }
              return i - paramInt1;
            }
          }
          if ((this.leftoverLF) && (paramArrayOfChar == Console.this.rcb) && (this.cb[this.nextChar] == '\n')) {
            this.nextChar += 1;
          }
          this.leftoverLF = false;
          while (this.nextChar < this.nChars)
          {
            m = paramArrayOfChar[(i++)] = this.cb[this.nextChar];
            this.cb[(this.nextChar++)] = '\000';
            if (m == 10) {
              return i - paramInt1;
            }
            if (m == 13)
            {
              if (i == j) {
                if (paramArrayOfChar == Console.this.rcb)
                {
                  paramArrayOfChar = Console.this.grow();
                  j = paramArrayOfChar.length;
                }
                else
                {
                  this.leftoverLF = true;
                  return i - paramInt1;
                }
              }
              if ((this.nextChar == this.nChars) && (this.in.ready()))
              {
                this.nChars = this.in.read(this.cb, 0, this.cb.length);
                this.nextChar = 0;
              }
              if ((this.nextChar < this.nChars) && (this.cb[this.nextChar] == '\n'))
              {
                paramArrayOfChar[(i++)] = '\n';
                this.nextChar += 1;
              }
              return i - paramInt1;
            }
            if (i == j) {
              if (paramArrayOfChar == Console.this.rcb)
              {
                paramArrayOfChar = Console.this.grow();
                j = paramArrayOfChar.length;
              }
              else
              {
                return i - paramInt1;
              }
            }
          }
        } while (k == 0);
        return i - paramInt1;
      }
    }
  }
}
