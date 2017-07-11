package com.sun.xml.internal.ws.encoding.xml;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class XMLMessage
{
  private static final int PLAIN_XML_FLAG = 1;
  private static final int MIME_MULTIPART_FLAG = 2;
  private static final int FI_ENCODED_FLAG = 16;
  
  public XMLMessage() {}
  
  public static Message create(String paramString, InputStream paramInputStream, WSFeatureList paramWSFeatureList)
  {
    Object localObject;
    try
    {
      paramInputStream = StreamUtils.hasSomeData(paramInputStream);
      if (paramInputStream == null) {
        return Messages.createEmpty(SOAPVersion.SOAP_11);
      }
      if (paramString != null)
      {
        com.sun.xml.internal.ws.encoding.ContentType localContentType = new com.sun.xml.internal.ws.encoding.ContentType(paramString);
        int i = identifyContentType(localContentType);
        if ((i & 0x2) != 0) {
          localObject = new XMLMultiPart(paramString, paramInputStream, paramWSFeatureList);
        } else if ((i & 0x1) != 0) {
          localObject = new XmlContent(paramString, paramInputStream, paramWSFeatureList);
        } else {
          localObject = new UnknownContent(paramString, paramInputStream);
        }
      }
      else
      {
        localObject = new UnknownContent("application/octet-stream", paramInputStream);
      }
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    return localObject;
  }
  
  public static Message create(Source paramSource)
  {
    return paramSource == null ? Messages.createEmpty(SOAPVersion.SOAP_11) : Messages.createUsingPayload(paramSource, SOAPVersion.SOAP_11);
  }
  
  public static Message create(DataSource paramDataSource, WSFeatureList paramWSFeatureList)
  {
    try
    {
      return paramDataSource == null ? Messages.createEmpty(SOAPVersion.SOAP_11) : create(paramDataSource.getContentType(), paramDataSource.getInputStream(), paramWSFeatureList);
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  public static Message create(Exception paramException)
  {
    return new FaultMessage(SOAPVersion.SOAP_11);
  }
  
  private static int getContentId(String paramString)
  {
    try
    {
      com.sun.xml.internal.ws.encoding.ContentType localContentType = new com.sun.xml.internal.ws.encoding.ContentType(paramString);
      return identifyContentType(localContentType);
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  public static boolean isFastInfoset(String paramString)
  {
    return (getContentId(paramString) & 0x10) != 0;
  }
  
  public static int identifyContentType(com.sun.xml.internal.ws.encoding.ContentType paramContentType)
  {
    String str1 = paramContentType.getPrimaryType();
    String str2 = paramContentType.getSubType();
    if ((str1.equalsIgnoreCase("multipart")) && (str2.equalsIgnoreCase("related")))
    {
      String str3 = paramContentType.getParameter("type");
      if (str3 != null)
      {
        if (isXMLType(str3)) {
          return 3;
        }
        if (isFastInfosetType(str3)) {
          return 18;
        }
      }
      return 0;
    }
    if (isXMLType(str1, str2)) {
      return 1;
    }
    if (isFastInfosetType(str1, str2)) {
      return 16;
    }
    return 0;
  }
  
  protected static boolean isXMLType(@NotNull String paramString1, @NotNull String paramString2)
  {
    return ((paramString1.equalsIgnoreCase("text")) && (paramString2.equalsIgnoreCase("xml"))) || ((paramString1.equalsIgnoreCase("application")) && (paramString2.equalsIgnoreCase("xml"))) || ((paramString1.equalsIgnoreCase("application")) && (paramString2.toLowerCase().endsWith("+xml")));
  }
  
  protected static boolean isXMLType(String paramString)
  {
    String str = paramString.toLowerCase();
    return (str.startsWith("text/xml")) || (str.startsWith("application/xml")) || ((str.startsWith("application/")) && (str.indexOf("+xml") != -1));
  }
  
  protected static boolean isFastInfosetType(String paramString1, String paramString2)
  {
    return (paramString1.equalsIgnoreCase("application")) && (paramString2.equalsIgnoreCase("fastinfoset"));
  }
  
  protected static boolean isFastInfosetType(String paramString)
  {
    return paramString.toLowerCase().startsWith("application/fastinfoset");
  }
  
  public static DataSource getDataSource(Message paramMessage, WSFeatureList paramWSFeatureList)
  {
    if (paramMessage == null) {
      return null;
    }
    if ((paramMessage instanceof MessageDataSource)) {
      return ((MessageDataSource)paramMessage).getDataSource();
    }
    AttachmentSet localAttachmentSet = paramMessage.getAttachments();
    if ((localAttachmentSet != null) && (!localAttachmentSet.isEmpty()))
    {
      localByteArrayBuffer = new ByteArrayBuffer();
      try
      {
        XMLHTTPBindingCodec localXMLHTTPBindingCodec = new XMLHTTPBindingCodec(paramWSFeatureList);
        Packet localPacket = new Packet(paramMessage);
        com.sun.xml.internal.ws.api.pipe.ContentType localContentType = localXMLHTTPBindingCodec.getStaticContentType(localPacket);
        localXMLHTTPBindingCodec.encode(localPacket, localByteArrayBuffer);
        return createDataSource(localContentType.getContentType(), localByteArrayBuffer.newInputStream());
      }
      catch (IOException localIOException)
      {
        throw new WebServiceException(localIOException);
      }
    }
    ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
    XMLStreamWriter localXMLStreamWriter = XMLStreamWriterFactory.create(localByteArrayBuffer);
    try
    {
      paramMessage.writePayloadTo(localXMLStreamWriter);
      localXMLStreamWriter.flush();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
    return createDataSource("text/xml", localByteArrayBuffer.newInputStream());
  }
  
  public static DataSource createDataSource(String paramString, InputStream paramInputStream)
  {
    return new XmlDataSource(paramString, paramInputStream);
  }
  
  private static class FaultMessage
    extends EmptyMessageImpl
  {
    public FaultMessage(SOAPVersion paramSOAPVersion)
    {
      super();
    }
    
    public boolean isFault()
    {
      return true;
    }
  }
  
  public static abstract interface MessageDataSource
  {
    public abstract boolean hasUnconsumedDataSource();
    
    public abstract DataSource getDataSource();
  }
  
  public static class UnknownContent
    extends AbstractMessageImpl
    implements XMLMessage.MessageDataSource
  {
    private final DataSource ds;
    private final HeaderList headerList;
    
    public UnknownContent(String paramString, InputStream paramInputStream)
    {
      this(XMLMessage.createDataSource(paramString, paramInputStream));
    }
    
    public UnknownContent(DataSource paramDataSource)
    {
      super();
      this.ds = paramDataSource;
      this.headerList = new HeaderList(SOAPVersion.SOAP_11);
    }
    
    private UnknownContent(UnknownContent paramUnknownContent)
    {
      super();
      this.ds = paramUnknownContent.ds;
      this.headerList = HeaderList.copy(paramUnknownContent.headerList);
    }
    
    public boolean hasUnconsumedDataSource()
    {
      return true;
    }
    
    public DataSource getDataSource()
    {
      assert (this.ds != null);
      return this.ds;
    }
    
    protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
      throws SAXException
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean hasHeaders()
    {
      return false;
    }
    
    public boolean isFault()
    {
      return false;
    }
    
    public MessageHeaders getHeaders()
    {
      return this.headerList;
    }
    
    public String getPayloadLocalPart()
    {
      throw new UnsupportedOperationException();
    }
    
    public String getPayloadNamespaceURI()
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean hasPayload()
    {
      return false;
    }
    
    public Source readPayloadAsSource()
    {
      return null;
    }
    
    public XMLStreamReader readPayload()
      throws XMLStreamException
    {
      throw new WebServiceException("There isn't XML payload. Shouldn't come here.");
    }
    
    public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
      throws XMLStreamException
    {}
    
    public Message copy()
    {
      return new UnknownContent(this);
    }
  }
  
  public static final class XMLMultiPart
    extends AbstractMessageImpl
    implements XMLMessage.MessageDataSource
  {
    private final DataSource dataSource;
    private final StreamingAttachmentFeature feature;
    private Message delegate;
    private HeaderList headerList = new HeaderList(SOAPVersion.SOAP_11);
    private final WSFeatureList features;
    
    public XMLMultiPart(String paramString, InputStream paramInputStream, WSFeatureList paramWSFeatureList)
    {
      super();
      this.dataSource = XMLMessage.createDataSource(paramString, paramInputStream);
      this.feature = ((StreamingAttachmentFeature)paramWSFeatureList.get(StreamingAttachmentFeature.class));
      this.features = paramWSFeatureList;
    }
    
    private Message getMessage()
    {
      if (this.delegate == null)
      {
        MimeMultipartParser localMimeMultipartParser;
        try
        {
          localMimeMultipartParser = new MimeMultipartParser(this.dataSource.getInputStream(), this.dataSource.getContentType(), this.feature);
        }
        catch (IOException localIOException)
        {
          throw new WebServiceException(localIOException);
        }
        InputStream localInputStream = localMimeMultipartParser.getRootPart().asInputStream();
        assert (localInputStream != null);
        this.delegate = new PayloadSourceMessage(this.headerList, new StreamSource(localInputStream), new MimeAttachmentSet(localMimeMultipartParser), SOAPVersion.SOAP_11);
      }
      return this.delegate;
    }
    
    public boolean hasUnconsumedDataSource()
    {
      return this.delegate == null;
    }
    
    public DataSource getDataSource()
    {
      return hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(getMessage(), this.features);
    }
    
    public boolean hasHeaders()
    {
      return false;
    }
    
    @NotNull
    public MessageHeaders getHeaders()
    {
      return this.headerList;
    }
    
    public String getPayloadLocalPart()
    {
      return getMessage().getPayloadLocalPart();
    }
    
    public String getPayloadNamespaceURI()
    {
      return getMessage().getPayloadNamespaceURI();
    }
    
    public boolean hasPayload()
    {
      return true;
    }
    
    public boolean isFault()
    {
      return false;
    }
    
    public Source readEnvelopeAsSource()
    {
      return getMessage().readEnvelopeAsSource();
    }
    
    public Source readPayloadAsSource()
    {
      return getMessage().readPayloadAsSource();
    }
    
    public SOAPMessage readAsSOAPMessage()
      throws SOAPException
    {
      return getMessage().readAsSOAPMessage();
    }
    
    public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean)
      throws SOAPException
    {
      return getMessage().readAsSOAPMessage(paramPacket, paramBoolean);
    }
    
    public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
      throws JAXBException
    {
      return getMessage().readPayloadAsJAXB(paramUnmarshaller);
    }
    
    public <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
      throws JAXBException
    {
      return getMessage().readPayloadAsJAXB(paramBridge);
    }
    
    public XMLStreamReader readPayload()
      throws XMLStreamException
    {
      return getMessage().readPayload();
    }
    
    public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
      throws XMLStreamException
    {
      getMessage().writePayloadTo(paramXMLStreamWriter);
    }
    
    public void writeTo(XMLStreamWriter paramXMLStreamWriter)
      throws XMLStreamException
    {
      getMessage().writeTo(paramXMLStreamWriter);
    }
    
    public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
      throws SAXException
    {
      getMessage().writeTo(paramContentHandler, paramErrorHandler);
    }
    
    public Message copy()
    {
      return getMessage().copy();
    }
    
    protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
      throws SAXException
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean isOneWay(@NotNull WSDLPort paramWSDLPort)
    {
      return false;
    }
    
    @NotNull
    public AttachmentSet getAttachments()
    {
      return getMessage().getAttachments();
    }
  }
  
  private static class XmlContent
    extends AbstractMessageImpl
    implements XMLMessage.MessageDataSource
  {
    private final XMLMessage.XmlDataSource dataSource;
    private boolean consumed;
    private Message delegate;
    private final HeaderList headerList;
    private WSFeatureList features;
    
    public XmlContent(String paramString, InputStream paramInputStream, WSFeatureList paramWSFeatureList)
    {
      super();
      this.dataSource = new XMLMessage.XmlDataSource(paramString, paramInputStream);
      this.headerList = new HeaderList(SOAPVersion.SOAP_11);
      this.features = paramWSFeatureList;
    }
    
    private Message getMessage()
    {
      if (this.delegate == null)
      {
        InputStream localInputStream = this.dataSource.getInputStream();
        assert (localInputStream != null);
        this.delegate = Messages.createUsingPayload(new StreamSource(localInputStream), SOAPVersion.SOAP_11);
        this.consumed = true;
      }
      return this.delegate;
    }
    
    public boolean hasUnconsumedDataSource()
    {
      return (!this.dataSource.consumed()) && (!this.consumed);
    }
    
    public DataSource getDataSource()
    {
      return hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(getMessage(), this.features);
    }
    
    public boolean hasHeaders()
    {
      return false;
    }
    
    @NotNull
    public MessageHeaders getHeaders()
    {
      return this.headerList;
    }
    
    public String getPayloadLocalPart()
    {
      return getMessage().getPayloadLocalPart();
    }
    
    public String getPayloadNamespaceURI()
    {
      return getMessage().getPayloadNamespaceURI();
    }
    
    public boolean hasPayload()
    {
      return true;
    }
    
    public boolean isFault()
    {
      return false;
    }
    
    public Source readEnvelopeAsSource()
    {
      return getMessage().readEnvelopeAsSource();
    }
    
    public Source readPayloadAsSource()
    {
      return getMessage().readPayloadAsSource();
    }
    
    public SOAPMessage readAsSOAPMessage()
      throws SOAPException
    {
      return getMessage().readAsSOAPMessage();
    }
    
    public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean)
      throws SOAPException
    {
      return getMessage().readAsSOAPMessage(paramPacket, paramBoolean);
    }
    
    public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
      throws JAXBException
    {
      return getMessage().readPayloadAsJAXB(paramUnmarshaller);
    }
    
    /**
     * @deprecated
     */
    public <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
      throws JAXBException
    {
      return getMessage().readPayloadAsJAXB(paramBridge);
    }
    
    public XMLStreamReader readPayload()
      throws XMLStreamException
    {
      return getMessage().readPayload();
    }
    
    public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
      throws XMLStreamException
    {
      getMessage().writePayloadTo(paramXMLStreamWriter);
    }
    
    public void writeTo(XMLStreamWriter paramXMLStreamWriter)
      throws XMLStreamException
    {
      getMessage().writeTo(paramXMLStreamWriter);
    }
    
    public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
      throws SAXException
    {
      getMessage().writeTo(paramContentHandler, paramErrorHandler);
    }
    
    public Message copy()
    {
      return getMessage().copy();
    }
    
    protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
      throws SAXException
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class XmlDataSource
    implements DataSource
  {
    private final String contentType;
    private final InputStream is;
    private boolean consumed;
    
    XmlDataSource(String paramString, InputStream paramInputStream)
    {
      this.contentType = paramString;
      this.is = paramInputStream;
    }
    
    public boolean consumed()
    {
      return this.consumed;
    }
    
    public InputStream getInputStream()
    {
      this.consumed = (!this.consumed);
      return this.is;
    }
    
    public OutputStream getOutputStream()
    {
      return null;
    }
    
    public String getContentType()
    {
      return this.contentType;
    }
    
    public String getName()
    {
      return "";
    }
  }
}
