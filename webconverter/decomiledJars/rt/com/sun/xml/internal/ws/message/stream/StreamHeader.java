package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.Map;
import java.util.Set;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class StreamHeader
  extends AbstractHeaderImpl
{
  protected final XMLStreamBuffer _mark;
  protected boolean _isMustUnderstand;
  @NotNull
  protected String _role;
  protected boolean _isRelay;
  protected String _localName;
  protected String _namespaceURI;
  private final FinalArrayList<Attribute> attributes;
  
  protected StreamHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer)
  {
    assert ((paramXMLStreamReader != null) && (paramXMLStreamBuffer != null));
    this._mark = paramXMLStreamBuffer;
    this._localName = paramXMLStreamReader.getLocalName();
    this._namespaceURI = paramXMLStreamReader.getNamespaceURI();
    this.attributes = processHeaderAttributes(paramXMLStreamReader);
  }
  
  protected StreamHeader(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    this._localName = paramXMLStreamReader.getLocalName();
    this._namespaceURI = paramXMLStreamReader.getNamespaceURI();
    this.attributes = processHeaderAttributes(paramXMLStreamReader);
    this._mark = XMLStreamBuffer.createNewBufferFromXMLStreamReader(paramXMLStreamReader);
  }
  
  public final boolean isIgnorable(@NotNull SOAPVersion paramSOAPVersion, @NotNull Set<String> paramSet)
  {
    if (!this._isMustUnderstand) {
      return true;
    }
    if (paramSet == null) {
      return true;
    }
    return !paramSet.contains(this._role);
  }
  
  @NotNull
  public String getRole(@NotNull SOAPVersion paramSOAPVersion)
  {
    assert (this._role != null);
    return this._role;
  }
  
  public boolean isRelay()
  {
    return this._isRelay;
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    return this._namespaceURI;
  }
  
  @NotNull
  public String getLocalPart()
  {
    return this._localName;
  }
  
  public String getAttribute(String paramString1, String paramString2)
  {
    if (this.attributes != null) {
      for (int i = this.attributes.size() - 1; i >= 0; i--)
      {
        Attribute localAttribute = (Attribute)this.attributes.get(i);
        if ((localAttribute.localName.equals(paramString2)) && (localAttribute.nsUri.equals(paramString1))) {
          return localAttribute.value;
        }
      }
    }
    return null;
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    return this._mark.readAsXMLStreamReader();
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (this._mark.getInscopeNamespaces().size() > 0) {
      this._mark.writeToXMLStreamWriter(paramXMLStreamWriter, true);
    } else {
      this._mark.writeToXMLStreamWriter(paramXMLStreamWriter);
    }
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    try
    {
      TransformerFactory localTransformerFactory = XmlUtil.newTransformerFactory();
      Transformer localTransformer = localTransformerFactory.newTransformer();
      XMLStreamBufferSource localXMLStreamBufferSource = new XMLStreamBufferSource(this._mark);
      DOMResult localDOMResult = new DOMResult();
      localTransformer.transform(localXMLStreamBufferSource, localDOMResult);
      Node localNode1 = localDOMResult.getNode();
      if (localNode1.getNodeType() == 9) {
        localNode1 = localNode1.getFirstChild();
      }
      SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (localSOAPHeader == null) {
        localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
      }
      Node localNode2 = localSOAPHeader.getOwnerDocument().importNode(localNode1, true);
      localSOAPHeader.appendChild(localNode2);
    }
    catch (Exception localException)
    {
      throw new SOAPException(localException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    this._mark.writeTo(paramContentHandler);
  }
  
  @NotNull
  public WSEndpointReference readAsEPR(AddressingVersion paramAddressingVersion)
    throws XMLStreamException
  {
    return new WSEndpointReference(this._mark, paramAddressingVersion);
  }
  
  protected abstract FinalArrayList<Attribute> processHeaderAttributes(XMLStreamReader paramXMLStreamReader);
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  protected static final class Attribute
  {
    final String nsUri;
    final String localName;
    final String value;
    
    public Attribute(String paramString1, String paramString2, String paramString3)
    {
      this.nsUri = StreamHeader.fixNull(paramString1);
      this.localName = paramString2;
      this.value = paramString3;
    }
  }
}
