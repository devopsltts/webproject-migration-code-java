package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.ProblemActionHeader;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.message.source.ProtocolSourceMessage;
import com.sun.xml.internal.ws.message.stream.PayloadStreamReaderMessage;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Messages
{
  private Messages() {}
  
  /**
   * @deprecated
   */
  public static Message create(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    return JAXBMessage.create(paramJAXBContext, paramObject, paramSOAPVersion);
  }
  
  /**
   * @deprecated
   */
  public static Message createRaw(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    return JAXBMessage.createRaw(paramJAXBContext, paramObject, paramSOAPVersion);
  }
  
  /**
   * @deprecated
   */
  public static Message create(Marshaller paramMarshaller, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    return create(BindingContextFactory.getBindingContext(paramMarshaller).getJAXBContext(), paramObject, paramSOAPVersion);
  }
  
  public static Message create(SOAPMessage paramSOAPMessage)
  {
    return SAAJFactory.create(paramSOAPMessage);
  }
  
  public static Message createUsingPayload(Source paramSource, SOAPVersion paramSOAPVersion)
  {
    if ((paramSource instanceof DOMSource))
    {
      if (((DOMSource)paramSource).getNode() == null) {
        return new EmptyMessageImpl(paramSOAPVersion);
      }
    }
    else
    {
      Object localObject;
      if ((paramSource instanceof StreamSource))
      {
        localObject = (StreamSource)paramSource;
        if ((((StreamSource)localObject).getInputStream() == null) && (((StreamSource)localObject).getReader() == null) && (((StreamSource)localObject).getSystemId() == null)) {
          return new EmptyMessageImpl(paramSOAPVersion);
        }
      }
      else if ((paramSource instanceof SAXSource))
      {
        localObject = (SAXSource)paramSource;
        if ((((SAXSource)localObject).getInputSource() == null) && (((SAXSource)localObject).getXMLReader() == null)) {
          return new EmptyMessageImpl(paramSOAPVersion);
        }
      }
    }
    return new PayloadSourceMessage(paramSource, paramSOAPVersion);
  }
  
  public static Message createUsingPayload(XMLStreamReader paramXMLStreamReader, SOAPVersion paramSOAPVersion)
  {
    return new PayloadStreamReaderMessage(paramXMLStreamReader, paramSOAPVersion);
  }
  
  public static Message createUsingPayload(Element paramElement, SOAPVersion paramSOAPVersion)
  {
    return new DOMMessage(paramSOAPVersion, paramElement);
  }
  
  public static Message create(Element paramElement)
  {
    SOAPVersion localSOAPVersion = SOAPVersion.fromNsUri(paramElement.getNamespaceURI());
    Element localElement1 = DOMUtil.getFirstChild(paramElement, localSOAPVersion.nsUri, "Header");
    HeaderList localHeaderList = null;
    if (localElement1 != null) {
      for (localObject = localElement1.getFirstChild(); localObject != null; localObject = ((Node)localObject).getNextSibling()) {
        if (((Node)localObject).getNodeType() == 1)
        {
          if (localHeaderList == null) {
            localHeaderList = new HeaderList(localSOAPVersion);
          }
          localHeaderList.add(Headers.create((Element)localObject));
        }
      }
    }
    Object localObject = DOMUtil.getFirstChild(paramElement, localSOAPVersion.nsUri, "Body");
    if (localObject == null) {
      throw new WebServiceException("Message doesn't have <S:Body> " + paramElement);
    }
    Element localElement2 = DOMUtil.getFirstChild(paramElement, localSOAPVersion.nsUri, "Body");
    if (localElement2 == null) {
      return new EmptyMessageImpl(localHeaderList, new AttachmentSetImpl(), localSOAPVersion);
    }
    return new DOMMessage(localSOAPVersion, localHeaderList, localElement2);
  }
  
  public static Message create(Source paramSource, SOAPVersion paramSOAPVersion)
  {
    return new ProtocolSourceMessage(paramSource, paramSOAPVersion);
  }
  
  public static Message createEmpty(SOAPVersion paramSOAPVersion)
  {
    return new EmptyMessageImpl(paramSOAPVersion);
  }
  
  @NotNull
  public static Message create(@NotNull XMLStreamReader paramXMLStreamReader)
  {
    if (paramXMLStreamReader.getEventType() != 1) {
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    }
    assert (paramXMLStreamReader.getEventType() == 1) : paramXMLStreamReader.getEventType();
    SOAPVersion localSOAPVersion = SOAPVersion.fromNsUri(paramXMLStreamReader.getNamespaceURI());
    return Codecs.createSOAPEnvelopeXmlCodec(localSOAPVersion).decode(paramXMLStreamReader);
  }
  
  @NotNull
  public static Message create(@NotNull XMLStreamBuffer paramXMLStreamBuffer)
  {
    try
    {
      return create(paramXMLStreamBuffer.readAsXMLStreamReader());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new XMLStreamReaderException(localXMLStreamException);
    }
  }
  
  public static Message create(Throwable paramThrowable, SOAPVersion paramSOAPVersion)
  {
    return SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, null, paramThrowable);
  }
  
  public static Message create(SOAPFault paramSOAPFault)
  {
    SOAPVersion localSOAPVersion = SOAPVersion.fromNsUri(paramSOAPFault.getNamespaceURI());
    return new DOMMessage(localSOAPVersion, paramSOAPFault);
  }
  
  /**
   * @deprecated
   */
  public static Message createAddressingFaultMessage(WSBinding paramWSBinding, QName paramQName)
  {
    return createAddressingFaultMessage(paramWSBinding, null, paramQName);
  }
  
  public static Message createAddressingFaultMessage(WSBinding paramWSBinding, Packet paramPacket, QName paramQName)
  {
    AddressingVersion localAddressingVersion = paramWSBinding.getAddressingVersion();
    if (localAddressingVersion == null) {
      throw new WebServiceException(AddressingMessages.ADDRESSING_SHOULD_BE_ENABLED());
    }
    WsaTubeHelper localWsaTubeHelper = localAddressingVersion.getWsaHelper(null, null, paramWSBinding);
    return create(localWsaTubeHelper.newMapRequiredFault(new MissingAddressingHeaderException(paramQName, paramPacket)));
  }
  
  public static Message create(@NotNull String paramString, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    QName localQName = paramAddressingVersion.actionNotSupportedTag;
    String str = String.format(paramAddressingVersion.actionNotSupportedText, new Object[] { paramString });
    Message localMessage;
    try
    {
      SOAPFault localSOAPFault;
      if (paramSOAPVersion == SOAPVersion.SOAP_12)
      {
        localSOAPFault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
        localSOAPFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        localSOAPFault.appendFaultSubcode(localQName);
        Detail localDetail = localSOAPFault.addDetail();
        SOAPElement localSOAPElement = localDetail.addChildElement(paramAddressingVersion.problemActionTag);
        localSOAPElement = localSOAPElement.addChildElement(paramAddressingVersion.actionTag);
        localSOAPElement.addTextNode(paramString);
      }
      else
      {
        localSOAPFault = SOAPVersion.SOAP_11.getSOAPFactory().createFault();
        localSOAPFault.setFaultCode(localQName);
      }
      localSOAPFault.setFaultString(str);
      localMessage = SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, localSOAPFault);
      if (paramSOAPVersion == SOAPVersion.SOAP_11) {
        localMessage.getHeaders().add(new ProblemActionHeader(paramString, paramAddressingVersion));
      }
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
    return localMessage;
  }
  
  @NotNull
  public static Message create(@NotNull SOAPVersion paramSOAPVersion, @NotNull ProtocolException paramProtocolException, @Nullable QName paramQName)
  {
    return SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, paramProtocolException, paramQName);
  }
}
