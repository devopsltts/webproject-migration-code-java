package com.sun.xml.internal.ws.encoding;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class StringDataContentHandler
  implements DataContentHandler
{
  private static final ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, "text/plain", "Text String");
  
  public StringDataContentHandler() {}
  
  protected ActivationDataFlavor getDF()
  {
    return myDF;
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { getDF() };
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    if (getDF().equals(paramDataFlavor)) {
      return getContent(paramDataSource);
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    String str1 = null;
    InputStreamReader localInputStreamReader;
    try
    {
      str1 = getCharset(paramDataSource.getContentType());
      localInputStreamReader = new InputStreamReader(paramDataSource.getInputStream(), str1);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new UnsupportedEncodingException(str1);
    }
    try
    {
      int i = 0;
      Object localObject1 = new char['Ѐ'];
      int j;
      while ((j = localInputStreamReader.read((char[])localObject1, i, localObject1.length - i)) != -1)
      {
        i += j;
        if (i >= localObject1.length)
        {
          int k = localObject1.length;
          if (k < 262144) {
            k += k;
          } else {
            k += 262144;
          }
          char[] arrayOfChar = new char[k];
          System.arraycopy(localObject1, 0, arrayOfChar, 0, i);
          localObject1 = arrayOfChar;
        }
      }
      String str2 = new String((char[])localObject1, 0, i);
      return str2;
    }
    finally
    {
      try
      {
        localInputStreamReader.close();
      }
      catch (IOException localIOException2) {}
    }
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (!(paramObject instanceof String)) {
      throw new IOException("\"" + getDF().getMimeType() + "\" DataContentHandler requires String object, " + "was given object of type " + paramObject.getClass().toString());
    }
    String str1 = null;
    OutputStreamWriter localOutputStreamWriter;
    try
    {
      str1 = getCharset(paramString);
      localOutputStreamWriter = new OutputStreamWriter(paramOutputStream, str1);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new UnsupportedEncodingException(str1);
    }
    String str2 = (String)paramObject;
    localOutputStreamWriter.write(str2, 0, str2.length());
    localOutputStreamWriter.flush();
  }
  
  private String getCharset(String paramString)
  {
    try
    {
      ContentType localContentType = new ContentType(paramString);
      String str = localContentType.getParameter("charset");
      if (str == null) {
        str = "us-ascii";
      }
      return Charset.forName(str).name();
    }
    catch (Exception localException) {}
    return null;
  }
}
