package com.sun.xml.internal.ws.message;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class RelatesToHeader
  extends StringHeader
{
  protected String type;
  private final QName typeAttributeName;
  
  public RelatesToHeader(QName paramQName, String paramString1, String paramString2)
  {
    super(paramQName, paramString1);
    this.type = paramString2;
    this.typeAttributeName = new QName(paramQName.getNamespaceURI(), "type");
  }
  
  public RelatesToHeader(QName paramQName, String paramString)
  {
    super(paramQName, paramString);
    this.typeAttributeName = new QName(paramQName.getNamespaceURI(), "type");
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    paramXMLStreamWriter.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(this.name.getNamespaceURI());
    if (this.type != null) {
      paramXMLStreamWriter.writeAttribute("type", this.type);
    }
    paramXMLStreamWriter.writeCharacters(this.value);
    paramXMLStreamWriter.writeEndElement();
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (localSOAPHeader == null) {
      localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
    }
    SOAPHeaderElement localSOAPHeaderElement = localSOAPHeader.addHeaderElement(this.name);
    if (this.type != null) {
      localSOAPHeaderElement.addAttribute(this.typeAttributeName, this.type);
    }
    localSOAPHeaderElement.addTextNode(this.value);
  }
}
