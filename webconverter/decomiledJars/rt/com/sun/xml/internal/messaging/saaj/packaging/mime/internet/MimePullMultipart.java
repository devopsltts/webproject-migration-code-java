package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.soap.AttachmentPartImpl;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataSource;

public class MimePullMultipart
  extends MimeMultipart
{
  private InputStream in = null;
  private String boundary = null;
  private MIMEMessage mm = null;
  private DataSource dataSource = null;
  private ContentType contType = null;
  private String startParam = null;
  private MIMEPart soapPart = null;
  
  public MimePullMultipart(DataSource paramDataSource, ContentType paramContentType)
    throws MessagingException
  {
    this.parsed = false;
    if (paramContentType == null) {
      this.contType = new ContentType(paramDataSource.getContentType());
    } else {
      this.contType = paramContentType;
    }
    this.dataSource = paramDataSource;
    this.boundary = this.contType.getParameter("boundary");
  }
  
  public MIMEPart readAndReturnSOAPPart()
    throws MessagingException
  {
    if (this.soapPart != null) {
      throw new MessagingException("Inputstream from datasource was already consumed");
    }
    readSOAPPart();
    return this.soapPart;
  }
  
  protected void readSOAPPart()
    throws MessagingException
  {
    try
    {
      if (this.soapPart != null) {
        return;
      }
      this.in = this.dataSource.getInputStream();
      MIMEConfig localMIMEConfig = new MIMEConfig();
      this.mm = new MIMEMessage(this.in, this.boundary, localMIMEConfig);
      String str = this.contType.getParameter("start");
      if (this.startParam == null)
      {
        this.soapPart = this.mm.getPart(0);
      }
      else
      {
        if ((str != null) && (str.length() > 2) && (str.charAt(0) == '<') && (str.charAt(str.length() - 1) == '>')) {
          str = str.substring(1, str.length() - 1);
        }
        this.startParam = str;
        this.soapPart = this.mm.getPart(this.startParam);
      }
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("No inputstream from datasource", localIOException);
    }
  }
  
  public void parseAll()
    throws MessagingException
  {
    if (this.parsed) {
      return;
    }
    if (this.soapPart == null) {
      readSOAPPart();
    }
    List localList = this.mm.getAttachments();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      MIMEPart localMIMEPart = (MIMEPart)localIterator.next();
      if (localMIMEPart != this.soapPart)
      {
        AttachmentPartImpl localAttachmentPartImpl = new AttachmentPartImpl(localMIMEPart);
        addBodyPart(new MimeBodyPart(localMIMEPart));
      }
    }
    this.parsed = true;
  }
  
  protected void parse()
    throws MessagingException
  {
    parseAll();
  }
}
