package java.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import jdk.internal.util.xml.BasicXmlPropertiesProvider;
import sun.util.spi.XmlPropertiesProvider;

public class Properties
  extends Hashtable<Object, Object>
{
  private static final long serialVersionUID = 4112578634029874840L;
  protected Properties defaults;
  private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public Properties()
  {
    this(null);
  }
  
  public Properties(Properties paramProperties)
  {
    this.defaults = paramProperties;
  }
  
  public synchronized Object setProperty(String paramString1, String paramString2)
  {
    return put(paramString1, paramString2);
  }
  
  public synchronized void load(Reader paramReader)
    throws IOException
  {
    load0(new LineReader(paramReader));
  }
  
  public synchronized void load(InputStream paramInputStream)
    throws IOException
  {
    load0(new LineReader(paramInputStream));
  }
  
  private void load0(LineReader paramLineReader)
    throws IOException
  {
    char[] arrayOfChar = new char['Ѐ'];
    int i;
    while ((i = paramLineReader.readLine()) >= 0)
    {
      int m = 0;
      int j = 0;
      int k = i;
      int n = 0;
      int i1 = 0;
      while (j < i)
      {
        m = paramLineReader.lineBuf[j];
        if (((m == 61) || (m == 58)) && (i1 == 0))
        {
          k = j + 1;
          n = 1;
          break;
        }
        if (((m == 32) || (m == 9) || (m == 12)) && (i1 == 0))
        {
          k = j + 1;
          break;
        }
        if (m == 92) {
          i1 = i1 == 0 ? 1 : 0;
        } else {
          i1 = 0;
        }
        j++;
      }
      while (k < i)
      {
        m = paramLineReader.lineBuf[k];
        if ((m != 32) && (m != 9) && (m != 12))
        {
          if ((n != 0) || ((m != 61) && (m != 58))) {
            break;
          }
          n = 1;
        }
        k++;
      }
      String str1 = loadConvert(paramLineReader.lineBuf, 0, j, arrayOfChar);
      String str2 = loadConvert(paramLineReader.lineBuf, k, i - k, arrayOfChar);
      put(str1, str2);
    }
  }
  
  private String loadConvert(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2)
  {
    int i;
    if (paramArrayOfChar2.length < paramInt2)
    {
      i = paramInt2 * 2;
      if (i < 0) {
        i = Integer.MAX_VALUE;
      }
      paramArrayOfChar2 = new char[i];
    }
    char[] arrayOfChar = paramArrayOfChar2;
    int j = 0;
    int k = paramInt1 + paramInt2;
    while (paramInt1 < k)
    {
      i = paramArrayOfChar1[(paramInt1++)];
      if (i == 92)
      {
        i = paramArrayOfChar1[(paramInt1++)];
        if (i == 117)
        {
          int m = 0;
          for (int n = 0; n < 4; n++)
          {
            i = paramArrayOfChar1[(paramInt1++)];
            switch (i)
            {
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
              m = (m << 4) + i - 48;
              break;
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: 
              m = (m << 4) + 10 + i - 97;
              break;
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: 
              m = (m << 4) + 10 + i - 65;
              break;
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 76: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            default: 
              throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            }
          }
          arrayOfChar[(j++)] = ((char)m);
        }
        else
        {
          if (i == 116) {
            i = 9;
          } else if (i == 114) {
            i = 13;
          } else if (i == 110) {
            i = 10;
          } else if (i == 102) {
            i = 12;
          }
          arrayOfChar[(j++)] = i;
        }
      }
      else
      {
        arrayOfChar[(j++)] = i;
      }
    }
    return new String(arrayOfChar, 0, j);
  }
  
  private String saveConvert(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramString.length();
    int j = i * 2;
    if (j < 0) {
      j = Integer.MAX_VALUE;
    }
    StringBuffer localStringBuffer = new StringBuffer(j);
    for (int k = 0; k < i; k++)
    {
      char c = paramString.charAt(k);
      if ((c > '=') && (c < ''))
      {
        if (c == '\\')
        {
          localStringBuffer.append('\\');
          localStringBuffer.append('\\');
        }
        else
        {
          localStringBuffer.append(c);
        }
      }
      else {
        switch (c)
        {
        case ' ': 
          if ((k == 0) || (paramBoolean1)) {
            localStringBuffer.append('\\');
          }
          localStringBuffer.append(' ');
          break;
        case '\t': 
          localStringBuffer.append('\\');
          localStringBuffer.append('t');
          break;
        case '\n': 
          localStringBuffer.append('\\');
          localStringBuffer.append('n');
          break;
        case '\r': 
          localStringBuffer.append('\\');
          localStringBuffer.append('r');
          break;
        case '\f': 
          localStringBuffer.append('\\');
          localStringBuffer.append('f');
          break;
        case '!': 
        case '#': 
        case ':': 
        case '=': 
          localStringBuffer.append('\\');
          localStringBuffer.append(c);
          break;
        default: 
          if ((((c < ' ') || (c > '~')) & paramBoolean2))
          {
            localStringBuffer.append('\\');
            localStringBuffer.append('u');
            localStringBuffer.append(toHex(c >> '\f' & 0xF));
            localStringBuffer.append(toHex(c >> '\b' & 0xF));
            localStringBuffer.append(toHex(c >> '\004' & 0xF));
            localStringBuffer.append(toHex(c & 0xF));
          }
          else
          {
            localStringBuffer.append(c);
          }
          break;
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  private static void writeComments(BufferedWriter paramBufferedWriter, String paramString)
    throws IOException
  {
    paramBufferedWriter.write("#");
    int i = paramString.length();
    int j = 0;
    int k = 0;
    char[] arrayOfChar = new char[6];
    arrayOfChar[0] = '\\';
    arrayOfChar[1] = 'u';
    while (j < i)
    {
      int m = paramString.charAt(j);
      if ((m > 255) || (m == 10) || (m == 13))
      {
        if (k != j) {
          paramBufferedWriter.write(paramString.substring(k, j));
        }
        if (m > 255)
        {
          arrayOfChar[2] = toHex(m >> 12 & 0xF);
          arrayOfChar[3] = toHex(m >> 8 & 0xF);
          arrayOfChar[4] = toHex(m >> 4 & 0xF);
          arrayOfChar[5] = toHex(m & 0xF);
          paramBufferedWriter.write(new String(arrayOfChar));
        }
        else
        {
          paramBufferedWriter.newLine();
          if ((m == 13) && (j != i - 1) && (paramString.charAt(j + 1) == '\n')) {
            j++;
          }
          if ((j == i - 1) || ((paramString.charAt(j + 1) != '#') && (paramString.charAt(j + 1) != '!'))) {
            paramBufferedWriter.write("#");
          }
        }
        k = j + 1;
      }
      j++;
    }
    if (k != j) {
      paramBufferedWriter.write(paramString.substring(k, j));
    }
    paramBufferedWriter.newLine();
  }
  
  @Deprecated
  public void save(OutputStream paramOutputStream, String paramString)
  {
    try
    {
      store(paramOutputStream, paramString);
    }
    catch (IOException localIOException) {}
  }
  
  public void store(Writer paramWriter, String paramString)
    throws IOException
  {
    store0((paramWriter instanceof BufferedWriter) ? (BufferedWriter)paramWriter : new BufferedWriter(paramWriter), paramString, false);
  }
  
  public void store(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    store0(new BufferedWriter(new OutputStreamWriter(paramOutputStream, "8859_1")), paramString, true);
  }
  
  private void store0(BufferedWriter paramBufferedWriter, String paramString, boolean paramBoolean)
    throws IOException
  {
    if (paramString != null) {
      writeComments(paramBufferedWriter, paramString);
    }
    paramBufferedWriter.write("#" + new Date().toString());
    paramBufferedWriter.newLine();
    synchronized (this)
    {
      Enumeration localEnumeration = keys();
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        String str2 = (String)get(str1);
        str1 = saveConvert(str1, true, paramBoolean);
        str2 = saveConvert(str2, false, paramBoolean);
        paramBufferedWriter.write(str1 + "=" + str2);
        paramBufferedWriter.newLine();
      }
    }
    paramBufferedWriter.flush();
  }
  
  public synchronized void loadFromXML(InputStream paramInputStream)
    throws IOException, InvalidPropertiesFormatException
  {
    XmlSupport.load(this, (InputStream)Objects.requireNonNull(paramInputStream));
    paramInputStream.close();
  }
  
  public void storeToXML(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    storeToXML(paramOutputStream, paramString, "UTF-8");
  }
  
  public void storeToXML(OutputStream paramOutputStream, String paramString1, String paramString2)
    throws IOException
  {
    XmlSupport.save(this, (OutputStream)Objects.requireNonNull(paramOutputStream), paramString1, (String)Objects.requireNonNull(paramString2));
  }
  
  public String getProperty(String paramString)
  {
    Object localObject = super.get(paramString);
    String str = (localObject instanceof String) ? (String)localObject : null;
    return (str == null) && (this.defaults != null) ? this.defaults.getProperty(paramString) : str;
  }
  
  public String getProperty(String paramString1, String paramString2)
  {
    String str = getProperty(paramString1);
    return str == null ? paramString2 : str;
  }
  
  public Enumeration<?> propertyNames()
  {
    Hashtable localHashtable = new Hashtable();
    enumerate(localHashtable);
    return localHashtable.keys();
  }
  
  public Set<String> stringPropertyNames()
  {
    Hashtable localHashtable = new Hashtable();
    enumerateStringProperties(localHashtable);
    return localHashtable.keySet();
  }
  
  public void list(PrintStream paramPrintStream)
  {
    paramPrintStream.println("-- listing properties --");
    Hashtable localHashtable = new Hashtable();
    enumerate(localHashtable);
    Enumeration localEnumeration = localHashtable.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = (String)localHashtable.get(str1);
      if (str2.length() > 40) {
        str2 = str2.substring(0, 37) + "...";
      }
      paramPrintStream.println(str1 + "=" + str2);
    }
  }
  
  public void list(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("-- listing properties --");
    Hashtable localHashtable = new Hashtable();
    enumerate(localHashtable);
    Enumeration localEnumeration = localHashtable.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = (String)localHashtable.get(str1);
      if (str2.length() > 40) {
        str2 = str2.substring(0, 37) + "...";
      }
      paramPrintWriter.println(str1 + "=" + str2);
    }
  }
  
  private synchronized void enumerate(Hashtable<String, Object> paramHashtable)
  {
    if (this.defaults != null) {
      this.defaults.enumerate(paramHashtable);
    }
    Enumeration localEnumeration = keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      paramHashtable.put(str, get(str));
    }
  }
  
  private synchronized void enumerateStringProperties(Hashtable<String, String> paramHashtable)
  {
    if (this.defaults != null) {
      this.defaults.enumerateStringProperties(paramHashtable);
    }
    Enumeration localEnumeration = keys();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      Object localObject2 = get(localObject1);
      if (((localObject1 instanceof String)) && ((localObject2 instanceof String))) {
        paramHashtable.put((String)localObject1, (String)localObject2);
      }
    }
  }
  
  private static char toHex(int paramInt)
  {
    return hexDigit[(paramInt & 0xF)];
  }
  
  class LineReader
  {
    byte[] inByteBuf;
    char[] inCharBuf;
    char[] lineBuf = new char['Ѐ'];
    int inLimit = 0;
    int inOff = 0;
    InputStream inStream;
    Reader reader;
    
    public LineReader(InputStream paramInputStream)
    {
      this.inStream = paramInputStream;
      this.inByteBuf = new byte[' '];
    }
    
    public LineReader(Reader paramReader)
    {
      this.reader = paramReader;
      this.inCharBuf = new char[' '];
    }
    
    int readLine()
      throws IOException
    {
      int i = 0;
      int j = 0;
      int k = 1;
      int m = 0;
      int n = 1;
      int i1 = 0;
      int i2 = 0;
      int i3 = 0;
      for (;;)
      {
        if (this.inOff >= this.inLimit)
        {
          this.inLimit = (this.inStream == null ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf));
          this.inOff = 0;
          if (this.inLimit <= 0)
          {
            if ((i == 0) || (m != 0)) {
              return -1;
            }
            if (i2 != 0) {
              i--;
            }
            return i;
          }
        }
        if (this.inStream != null) {
          j = (char)(0xFF & this.inByteBuf[(this.inOff++)]);
        } else {
          j = this.inCharBuf[(this.inOff++)];
        }
        if (i3 != 0)
        {
          i3 = 0;
          if (j == 10) {}
        }
        else if (k != 0)
        {
          if ((j != 32) && (j != 9) && (j != 12) && ((i1 != 0) || ((j != 13) && (j != 10))))
          {
            k = 0;
            i1 = 0;
          }
        }
        else
        {
          if (n != 0)
          {
            n = 0;
            if ((j == 35) || (j == 33))
            {
              m = 1;
              continue;
            }
          }
          if ((j != 10) && (j != 13))
          {
            this.lineBuf[(i++)] = j;
            if (i == this.lineBuf.length)
            {
              int i4 = this.lineBuf.length * 2;
              if (i4 < 0) {
                i4 = Integer.MAX_VALUE;
              }
              char[] arrayOfChar = new char[i4];
              System.arraycopy(this.lineBuf, 0, arrayOfChar, 0, this.lineBuf.length);
              this.lineBuf = arrayOfChar;
            }
            if (j == 92) {
              i2 = i2 == 0 ? 1 : 0;
            } else {
              i2 = 0;
            }
          }
          else if ((m != 0) || (i == 0))
          {
            m = 0;
            n = 1;
            k = 1;
            i = 0;
          }
          else
          {
            if (this.inOff >= this.inLimit)
            {
              this.inLimit = (this.inStream == null ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf));
              this.inOff = 0;
              if (this.inLimit <= 0)
              {
                if (i2 != 0) {
                  i--;
                }
                return i;
              }
            }
            if (i2 == 0) {
              break;
            }
            i--;
            k = 1;
            i1 = 1;
            i2 = 0;
            if (j == 13) {
              i3 = 1;
            }
          }
        }
      }
      return i;
    }
  }
  
  private static class XmlSupport
  {
    private static final XmlPropertiesProvider PROVIDER = ;
    
    private XmlSupport() {}
    
    private static XmlPropertiesProvider loadProviderFromProperty(ClassLoader paramClassLoader)
    {
      String str = System.getProperty("sun.util.spi.XmlPropertiesProvider");
      if (str == null) {
        return null;
      }
      try
      {
        Class localClass = Class.forName(str, true, paramClassLoader);
        return (XmlPropertiesProvider)localClass.newInstance();
      }
      catch (ClassNotFoundException|IllegalAccessException|InstantiationException localClassNotFoundException)
      {
        throw new ServiceConfigurationError(null, localClassNotFoundException);
      }
    }
    
    private static XmlPropertiesProvider loadProviderAsService(ClassLoader paramClassLoader)
    {
      Iterator localIterator = ServiceLoader.load(XmlPropertiesProvider.class, paramClassLoader).iterator();
      return localIterator.hasNext() ? (XmlPropertiesProvider)localIterator.next() : null;
    }
    
    private static XmlPropertiesProvider loadProvider()
    {
      (XmlPropertiesProvider)AccessController.doPrivileged(new PrivilegedAction()
      {
        public XmlPropertiesProvider run()
        {
          ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
          XmlPropertiesProvider localXmlPropertiesProvider = Properties.XmlSupport.loadProviderFromProperty(localClassLoader);
          if (localXmlPropertiesProvider != null) {
            return localXmlPropertiesProvider;
          }
          localXmlPropertiesProvider = Properties.XmlSupport.loadProviderAsService(localClassLoader);
          if (localXmlPropertiesProvider != null) {
            return localXmlPropertiesProvider;
          }
          return new BasicXmlPropertiesProvider();
        }
      });
    }
    
    static void load(Properties paramProperties, InputStream paramInputStream)
      throws IOException, InvalidPropertiesFormatException
    {
      PROVIDER.load(paramProperties, paramInputStream);
    }
    
    static void save(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2)
      throws IOException
    {
      PROVIDER.store(paramProperties, paramOutputStream, paramString1, paramString2);
    }
  }
}
