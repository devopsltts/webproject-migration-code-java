package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;

public final class MimePartDataSource
  implements DataSource
{
  private final MimeBodyPart part;
  
  public MimePartDataSource(MimeBodyPart paramMimeBodyPart)
  {
    this.part = paramMimeBodyPart;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    try
    {
      InputStream localInputStream = this.part.getContentStream();
      String str = this.part.getEncoding();
      if (str != null) {
        return MimeUtility.decode(localInputStream, str);
      }
      return localInputStream;
    }
    catch (MessagingException localMessagingException)
    {
      throw new IOException(localMessagingException.getMessage());
    }
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    throw new UnknownServiceException();
  }
  
  public String getContentType()
  {
    return this.part.getContentType();
  }
  
  public String getName()
  {
    try
    {
      return this.part.getFileName();
    }
    catch (MessagingException localMessagingException) {}
    return "";
  }
}
