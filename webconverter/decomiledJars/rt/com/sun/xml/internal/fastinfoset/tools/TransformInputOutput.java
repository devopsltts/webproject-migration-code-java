package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class TransformInputOutput
{
  private static URI currentJavaWorkingDirectory = new File(System.getProperty("user.dir")).toURI();
  
  public TransformInputOutput() {}
  
  public void parse(String[] paramArrayOfString)
    throws Exception
  {
    BufferedInputStream localBufferedInputStream = null;
    BufferedOutputStream localBufferedOutputStream = null;
    if (paramArrayOfString.length == 0)
    {
      localBufferedInputStream = new BufferedInputStream(System.in);
      localBufferedOutputStream = new BufferedOutputStream(System.out);
    }
    else if (paramArrayOfString.length == 1)
    {
      localBufferedInputStream = new BufferedInputStream(new FileInputStream(paramArrayOfString[0]));
      localBufferedOutputStream = new BufferedOutputStream(System.out);
    }
    else if (paramArrayOfString.length == 2)
    {
      localBufferedInputStream = new BufferedInputStream(new FileInputStream(paramArrayOfString[0]));
      localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramArrayOfString[1]));
    }
    else
    {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.optinalFileNotSpecified"));
    }
    parse(localBufferedInputStream, localBufferedOutputStream);
  }
  
  public abstract void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception;
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    throw new UnsupportedOperationException();
  }
  
  protected static EntityResolver createRelativePathResolver(String paramString)
  {
    new EntityResolver()
    {
      public InputSource resolveEntity(String paramAnonymousString1, String paramAnonymousString2)
        throws SAXException, IOException
      {
        if ((paramAnonymousString2 != null) && (paramAnonymousString2.startsWith("file:/")))
        {
          URI localURI1 = new File(this.val$workingDirectory).toURI();
          try
          {
            URI localURI2 = TransformInputOutput.convertToNewWorkingDirectory(TransformInputOutput.currentJavaWorkingDirectory, localURI1, new File(new URI(paramAnonymousString2)).toURI());
            return new InputSource(localURI2.toString());
          }
          catch (URISyntaxException localURISyntaxException) {}
        }
        return null;
      }
    };
  }
  
  private static URI convertToNewWorkingDirectory(URI paramURI1, URI paramURI2, URI paramURI3)
    throws IOException, URISyntaxException
  {
    String str1 = paramURI1.toString();
    String str2 = paramURI2.toString();
    String str3 = paramURI3.toString();
    String str4 = null;
    if ((str3.startsWith(str1)) && ((str4 = str3.substring(str1.length())).indexOf('/') == -1)) {
      return new URI(str2 + '/' + str4);
    }
    String[] arrayOfString1 = str1.split("/");
    String[] arrayOfString2 = str2.split("/");
    String[] arrayOfString3 = str3.split("/");
    for (int i = 0; (i < arrayOfString1.length) && (i < arrayOfString3.length) && (arrayOfString1[i].equals(arrayOfString3[i])); i++) {}
    for (int j = 0; (j < arrayOfString2.length) && (j < arrayOfString3.length) && (arrayOfString2[j].equals(arrayOfString3[j])); j++) {}
    if (j > i) {
      return paramURI3;
    }
    int k = arrayOfString1.length - i;
    StringBuffer localStringBuffer = new StringBuffer(100);
    for (int m = 0; m < arrayOfString2.length - k; m++)
    {
      localStringBuffer.append(arrayOfString2[m]);
      localStringBuffer.append('/');
    }
    for (m = i; m < arrayOfString3.length; m++)
    {
      localStringBuffer.append(arrayOfString3[m]);
      if (m < arrayOfString3.length - 1) {
        localStringBuffer.append('/');
      }
    }
    return new URI(localStringBuffer.toString());
  }
}
