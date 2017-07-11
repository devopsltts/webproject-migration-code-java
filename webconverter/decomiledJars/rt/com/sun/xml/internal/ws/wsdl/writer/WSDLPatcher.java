package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class WSDLPatcher
  extends XMLStreamReaderToXMLStreamWriter
{
  private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
  private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
  private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
  private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.wsdl.patcher");
  private final DocumentLocationResolver docResolver;
  private final PortAddressResolver portAddressResolver;
  private String targetNamespace;
  private QName serviceName;
  private QName portName;
  private String portAddress;
  private boolean inEpr;
  private boolean inEprAddress;
  
  public WSDLPatcher(@NotNull PortAddressResolver paramPortAddressResolver, @NotNull DocumentLocationResolver paramDocumentLocationResolver)
  {
    this.portAddressResolver = paramPortAddressResolver;
    this.docResolver = paramDocumentLocationResolver;
  }
  
  protected void handleAttribute(int paramInt)
    throws XMLStreamException
  {
    QName localQName = this.in.getName();
    String str1 = this.in.getAttributeLocalName(paramInt);
    String str2;
    if (((localQName.equals(SCHEMA_INCLUDE_QNAME)) && (str1.equals("schemaLocation"))) || ((localQName.equals(SCHEMA_IMPORT_QNAME)) && (str1.equals("schemaLocation"))) || ((localQName.equals(SCHEMA_REDEFINE_QNAME)) && (str1.equals("schemaLocation"))) || ((localQName.equals(WSDLConstants.QNAME_IMPORT)) && (str1.equals("location"))))
    {
      str2 = this.in.getAttributeValue(paramInt);
      String str3 = getPatchedImportLocation(str2);
      if (str3 == null) {
        return;
      }
      logger.fine("Fixing the relative location:" + str2 + " with absolute location:" + str3);
      writeAttribute(paramInt, str3);
      return;
    }
    if (((localQName.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS)) || (localQName.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS))) && (str1.equals("location")))
    {
      this.portAddress = this.in.getAttributeValue(paramInt);
      str2 = getAddressLocation();
      if (str2 != null)
      {
        logger.fine("Service:" + this.serviceName + " port:" + this.portName + " current address " + this.portAddress + " Patching it with " + str2);
        writeAttribute(paramInt, str2);
        return;
      }
    }
    super.handleAttribute(paramInt);
  }
  
  private void writeAttribute(int paramInt, String paramString)
    throws XMLStreamException
  {
    String str = this.in.getAttributeNamespace(paramInt);
    if (str != null) {
      this.out.writeAttribute(this.in.getAttributePrefix(paramInt), str, this.in.getAttributeLocalName(paramInt), paramString);
    } else {
      this.out.writeAttribute(this.in.getAttributeLocalName(paramInt), paramString);
    }
  }
  
  protected void handleStartElement()
    throws XMLStreamException
  {
    QName localQName = this.in.getName();
    String str;
    if (localQName.equals(WSDLConstants.QNAME_DEFINITIONS))
    {
      str = this.in.getAttributeValue(null, "targetNamespace");
      if (str != null) {
        this.targetNamespace = str;
      }
    }
    else if (localQName.equals(WSDLConstants.QNAME_SERVICE))
    {
      str = this.in.getAttributeValue(null, "name");
      if (str != null) {
        this.serviceName = new QName(this.targetNamespace, str);
      }
    }
    else if (localQName.equals(WSDLConstants.QNAME_PORT))
    {
      str = this.in.getAttributeValue(null, "name");
      if (str != null) {
        this.portName = new QName(this.targetNamespace, str);
      }
    }
    else if ((localQName.equals(W3CAddressingConstants.WSA_EPR_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)))
    {
      if ((this.serviceName != null) && (this.portName != null)) {
        this.inEpr = true;
      }
    }
    else if (((localQName.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME))) && (this.inEpr))
    {
      this.inEprAddress = true;
    }
    super.handleStartElement();
  }
  
  protected void handleEndElement()
    throws XMLStreamException
  {
    QName localQName = this.in.getName();
    if (localQName.equals(WSDLConstants.QNAME_SERVICE))
    {
      this.serviceName = null;
    }
    else if (localQName.equals(WSDLConstants.QNAME_PORT))
    {
      this.portName = null;
    }
    else if ((localQName.equals(W3CAddressingConstants.WSA_EPR_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)))
    {
      if (this.inEpr) {
        this.inEpr = false;
      }
    }
    else if (((localQName.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME))) && (this.inEprAddress))
    {
      String str = getAddressLocation();
      if (str != null)
      {
        logger.fine("Fixing EPR Address for service:" + this.serviceName + " port:" + this.portName + " address with " + str);
        this.out.writeCharacters(str);
      }
      this.inEprAddress = false;
    }
    super.handleEndElement();
  }
  
  protected void handleCharacters()
    throws XMLStreamException
  {
    if (this.inEprAddress)
    {
      String str = getAddressLocation();
      if (str != null) {
        return;
      }
    }
    super.handleCharacters();
  }
  
  @Nullable
  private String getPatchedImportLocation(String paramString)
  {
    return this.docResolver.getLocationFor(null, paramString);
  }
  
  private String getAddressLocation()
  {
    return (this.portAddressResolver == null) || (this.portName == null) ? null : this.portAddressResolver.getAddressFor(this.serviceName, this.portName.getLocalPart(), this.portAddress);
  }
}
