package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.EnvelopeFactory;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;

public class SOAPPart1_1Impl
  extends SOAPPartImpl
  implements SOAPConstants
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
  
  public SOAPPart1_1Impl() {}
  
  public SOAPPart1_1Impl(MessageImpl paramMessageImpl)
  {
    super(paramMessageImpl);
  }
  
  protected String getContentType()
  {
    return isFastInfoset() ? "application/fastinfoset" : "text/xml";
  }
  
  protected Envelope createEnvelopeFromSource()
    throws SOAPException
  {
    XMLDeclarationParser localXMLDeclarationParser = lookForXmlDecl();
    Source localSource = this.source;
    this.source = null;
    EnvelopeImpl localEnvelopeImpl = (EnvelopeImpl)EnvelopeFactory.createEnvelope(localSource, this);
    if (!localEnvelopeImpl.getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/envelope/"))
    {
      log.severe("SAAJ0304.ver1_1.msg.invalid.SOAP1.1");
      throw new SOAPException("InputStream does not represent a valid SOAP 1.1 Message");
    }
    if ((localXMLDeclarationParser != null) && (!this.omitXmlDecl))
    {
      localEnvelopeImpl.setOmitXmlDecl("no");
      localEnvelopeImpl.setXmlDecl(localXMLDeclarationParser.getXmlDeclaration());
      localEnvelopeImpl.setCharsetEncoding(localXMLDeclarationParser.getEncoding());
    }
    return localEnvelopeImpl;
  }
  
  protected Envelope createEmptyEnvelope(String paramString)
    throws SOAPException
  {
    return new Envelope1_1Impl(getDocument(), paramString, true, true);
  }
  
  protected SOAPPartImpl duplicateType()
  {
    return new SOAPPart1_1Impl();
  }
}
