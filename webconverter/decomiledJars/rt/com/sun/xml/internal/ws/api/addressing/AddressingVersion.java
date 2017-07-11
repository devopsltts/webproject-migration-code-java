package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.message.stream.OutboundStreamHeader;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public enum AddressingVersion
{
  W3C("http://www.w3.org/2005/08/addressing", "wsa", "<EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\">\n    <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>\n</EndpointReference>", "http://www.w3.org/2006/05/addressing/wsdl", "http://www.w3.org/2006/05/addressing/wsdl", "http://www.w3.org/2005/08/addressing/anonymous", "http://www.w3.org/2005/08/addressing/none", new EPR(W3CEndpointReference.class, "Address", "ServiceName", "EndpointName", "InterfaceName", new QName("http://www.w3.org/2005/08/addressing", "Metadata", "wsa"), "ReferenceParameters", null)),  MEMBER("http://schemas.xmlsoap.org/ws/2004/08/addressing", "wsa", "<EndpointReference xmlns=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n    <Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</Address>\n</EndpointReference>", "http://schemas.xmlsoap.org/ws/2004/08/addressing", "http://schemas.xmlsoap.org/ws/2004/08/addressing/policy", "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous", "", new EPR(MemberSubmissionEndpointReference.class, "Address", "ServiceName", "PortName", "PortType", MemberSubmissionAddressingConstants.MEX_METADATA, "ReferenceParameters", "ReferenceProperties"));
  
  public final String nsUri;
  public final String wsdlNsUri;
  public final EPR eprType;
  public final String policyNsUri;
  @NotNull
  public final String anonymousUri;
  @NotNull
  public final String noneUri;
  public final WSEndpointReference anonymousEpr;
  public final QName toTag;
  public final QName fromTag;
  public final QName replyToTag;
  public final QName faultToTag;
  public final QName actionTag;
  public final QName messageIDTag;
  public final QName relatesToTag;
  public final QName mapRequiredTag;
  public final QName actionMismatchTag;
  public final QName actionNotSupportedTag;
  public final String actionNotSupportedText;
  public final QName invalidMapTag;
  public final QName invalidCardinalityTag;
  public final QName invalidAddressTag;
  public final QName problemHeaderQNameTag;
  public final QName problemActionTag;
  public final QName faultDetailTag;
  public final QName fault_missingAddressInEpr;
  public final QName wsdlActionTag;
  public final QName wsdlExtensionTag;
  public final QName wsdlAnonymousTag;
  public final QName isReferenceParameterTag;
  private static final String EXTENDED_FAULT_NAMESPACE = "http://jax-ws.dev.java.net/addressing/fault";
  public static final String UNSET_OUTPUT_ACTION = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
  public static final String UNSET_INPUT_ACTION = "http://jax-ws.dev.java.net/addressing/input-action-not-set";
  public static final QName fault_duplicateAddressInEpr = new QName("http://jax-ws.dev.java.net/addressing/fault", "DuplicateAddressInEpr", "wsa");
  
  private AddressingVersion(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, EPR paramEPR)
  {
    this.nsUri = paramString1;
    this.wsdlNsUri = paramString4;
    this.policyNsUri = paramString5;
    this.anonymousUri = paramString6;
    this.noneUri = paramString7;
    this.toTag = new QName(paramString1, "To", paramString2);
    this.fromTag = new QName(paramString1, "From", paramString2);
    this.replyToTag = new QName(paramString1, "ReplyTo", paramString2);
    this.faultToTag = new QName(paramString1, "FaultTo", paramString2);
    this.actionTag = new QName(paramString1, "Action", paramString2);
    this.messageIDTag = new QName(paramString1, "MessageID", paramString2);
    this.relatesToTag = new QName(paramString1, "RelatesTo", paramString2);
    this.mapRequiredTag = new QName(paramString1, getMapRequiredLocalName(), paramString2);
    this.actionMismatchTag = new QName(paramString1, getActionMismatchLocalName(), paramString2);
    this.actionNotSupportedTag = new QName(paramString1, "ActionNotSupported", paramString2);
    this.actionNotSupportedText = "The \"%s\" cannot be processed at the receiver";
    this.invalidMapTag = new QName(paramString1, getInvalidMapLocalName(), paramString2);
    this.invalidAddressTag = new QName(paramString1, getInvalidAddressLocalName(), paramString2);
    this.invalidCardinalityTag = new QName(paramString1, getInvalidCardinalityLocalName(), paramString2);
    this.faultDetailTag = new QName(paramString1, "FaultDetail", paramString2);
    this.problemHeaderQNameTag = new QName(paramString1, "ProblemHeaderQName", paramString2);
    this.problemActionTag = new QName(paramString1, "ProblemAction", paramString2);
    this.fault_missingAddressInEpr = new QName(paramString1, "MissingAddressInEPR", paramString2);
    this.isReferenceParameterTag = new QName(paramString1, getIsReferenceParameterLocalName(), paramString2);
    this.wsdlActionTag = new QName(paramString4, "Action", paramString2);
    this.wsdlExtensionTag = new QName(paramString4, "UsingAddressing", paramString2);
    this.wsdlAnonymousTag = new QName(paramString4, getWsdlAnonymousLocalName(), paramString2);
    try
    {
      this.anonymousEpr = new WSEndpointReference(new ByteArrayInputStream(paramString3.getBytes("UTF-8")), this);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new Error(localXMLStreamException);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error(localUnsupportedEncodingException);
    }
    this.eprType = paramEPR;
  }
  
  abstract String getActionMismatchLocalName();
  
  public static AddressingVersion fromNsUri(String paramString)
  {
    if (paramString.equals(W3C.nsUri)) {
      return W3C;
    }
    if (paramString.equals(MEMBER.nsUri)) {
      return MEMBER;
    }
    return null;
  }
  
  @Nullable
  public static AddressingVersion fromBinding(WSBinding paramWSBinding)
  {
    if (paramWSBinding.isFeatureEnabled(AddressingFeature.class)) {
      return W3C;
    }
    if (paramWSBinding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) {
      return MEMBER;
    }
    return null;
  }
  
  public static AddressingVersion fromPort(WSDLPort paramWSDLPort)
  {
    if (paramWSDLPort == null) {
      return null;
    }
    WebServiceFeature localWebServiceFeature = paramWSDLPort.getFeature(AddressingFeature.class);
    if (localWebServiceFeature == null) {
      localWebServiceFeature = paramWSDLPort.getFeature(MemberSubmissionAddressingFeature.class);
    }
    if (localWebServiceFeature == null) {
      return null;
    }
    return fromFeature(localWebServiceFeature);
  }
  
  /**
   * @deprecated
   */
  public String getNsUri()
  {
    return this.nsUri;
  }
  
  public abstract boolean isReferenceParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract WsaTubeHelper getWsaHelper(WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding);
  
  /**
   * @deprecated
   */
  public final String getNoneUri()
  {
    return this.noneUri;
  }
  
  /**
   * @deprecated
   */
  public final String getAnonymousUri()
  {
    return this.anonymousUri;
  }
  
  public String getDefaultFaultAction()
  {
    return this.nsUri + "/fault";
  }
  
  abstract String getMapRequiredLocalName();
  
  public abstract String getMapRequiredText();
  
  abstract String getInvalidAddressLocalName();
  
  abstract String getInvalidMapLocalName();
  
  public abstract String getInvalidMapText();
  
  abstract String getInvalidCardinalityLocalName();
  
  abstract String getWsdlAnonymousLocalName();
  
  public abstract String getPrefix();
  
  public abstract String getWsdlPrefix();
  
  public abstract Class<? extends WebServiceFeature> getFeatureClass();
  
  abstract Header createReferenceParameterHeader(XMLStreamBuffer paramXMLStreamBuffer, String paramString1, String paramString2);
  
  abstract String getIsReferenceParameterLocalName();
  
  public static AddressingVersion fromFeature(WebServiceFeature paramWebServiceFeature)
  {
    if (paramWebServiceFeature.getID().equals("http://www.w3.org/2005/08/addressing/module")) {
      return W3C;
    }
    if (paramWebServiceFeature.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing")) {
      return MEMBER;
    }
    return null;
  }
  
  @NotNull
  public static WebServiceFeature getFeature(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramString.equals(W3C.policyNsUri)) {
      return new AddressingFeature(paramBoolean1, paramBoolean2);
    }
    if (paramString.equals(MEMBER.policyNsUri)) {
      return new MemberSubmissionAddressingFeature(paramBoolean1, paramBoolean2);
    }
    throw new WebServiceException("Unsupported namespace URI: " + paramString);
  }
  
  @NotNull
  public static AddressingVersion fromSpecClass(Class<? extends EndpointReference> paramClass)
  {
    if (paramClass == W3CEndpointReference.class) {
      return W3C;
    }
    if (paramClass == MemberSubmissionEndpointReference.class) {
      return MEMBER;
    }
    throw new WebServiceException("Unsupported EPR type: " + paramClass);
  }
  
  public static boolean isRequired(WebServiceFeature paramWebServiceFeature)
  {
    if (paramWebServiceFeature.getID().equals("http://www.w3.org/2005/08/addressing/module")) {
      return ((AddressingFeature)paramWebServiceFeature).isRequired();
    }
    if (paramWebServiceFeature.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing")) {
      return ((MemberSubmissionAddressingFeature)paramWebServiceFeature).isRequired();
    }
    throw new WebServiceException("WebServiceFeature not an Addressing feature: " + paramWebServiceFeature.getID());
  }
  
  public static boolean isRequired(WSBinding paramWSBinding)
  {
    AddressingFeature localAddressingFeature = (AddressingFeature)paramWSBinding.getFeature(AddressingFeature.class);
    if (localAddressingFeature != null) {
      return localAddressingFeature.isRequired();
    }
    MemberSubmissionAddressingFeature localMemberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)paramWSBinding.getFeature(MemberSubmissionAddressingFeature.class);
    if (localMemberSubmissionAddressingFeature != null) {
      return localMemberSubmissionAddressingFeature.isRequired();
    }
    return false;
  }
  
  public static boolean isEnabled(WSBinding paramWSBinding)
  {
    return (paramWSBinding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) || (paramWSBinding.isFeatureEnabled(AddressingFeature.class));
  }
  
  public static final class EPR
  {
    public final Class<? extends EndpointReference> eprClass;
    public final String address;
    public final String serviceName;
    public final String portName;
    public final String portTypeName;
    public final String referenceParameters;
    public final QName wsdlMetadata;
    public final String referenceProperties;
    
    public EPR(Class<? extends EndpointReference> paramClass, String paramString1, String paramString2, String paramString3, String paramString4, QName paramQName, String paramString5, String paramString6)
    {
      this.eprClass = paramClass;
      this.address = paramString1;
      this.serviceName = paramString2;
      this.portName = paramString3;
      this.portTypeName = paramString4;
      this.referenceParameters = paramString5;
      this.referenceProperties = paramString6;
      this.wsdlMetadata = paramQName;
    }
  }
}
