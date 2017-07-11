package com.sun.xml.internal.ws.addressing;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

public class WsaPropertyBag
  extends BasePropertySet
{
  public static final String WSA_REPLYTO_FROM_REQUEST = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest";
  public static final String WSA_FAULTTO_FROM_REQUEST = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest";
  public static final String WSA_MSGID_FROM_REQUEST = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest";
  public static final String WSA_TO = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.To";
  @NotNull
  private final AddressingVersion addressingVersion;
  @NotNull
  private final SOAPVersion soapVersion;
  @NotNull
  private final Packet packet;
  private static final BasePropertySet.PropertyMap model = parse(WsaPropertyBag.class);
  private WSEndpointReference _replyToFromRequest = null;
  private WSEndpointReference _faultToFromRequest = null;
  private String _msgIdFromRequest = null;
  
  public WsaPropertyBag(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, Packet paramPacket)
  {
    this.addressingVersion = paramAddressingVersion;
    this.soapVersion = paramSOAPVersion;
    this.packet = paramPacket;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.to"})
  public String getTo()
    throws XMLStreamException
  {
    if (this.packet.getMessage() == null) {
      return null;
    }
    Header localHeader = this.packet.getMessage().getHeaders().get(this.addressingVersion.toTag, false);
    if (localHeader == null) {
      return null;
    }
    return localHeader.getStringContent();
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.addressing.WsaPropertyBag.To"})
  public WSEndpointReference getToAsReference()
    throws XMLStreamException
  {
    if (this.packet.getMessage() == null) {
      return null;
    }
    Header localHeader = this.packet.getMessage().getHeaders().get(this.addressingVersion.toTag, false);
    if (localHeader == null) {
      return null;
    }
    return new WSEndpointReference(localHeader.getStringContent(), this.addressingVersion);
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.from"})
  public WSEndpointReference getFrom()
    throws XMLStreamException
  {
    return getEPR(this.addressingVersion.fromTag);
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.action"})
  public String getAction()
  {
    if (this.packet.getMessage() == null) {
      return null;
    }
    Header localHeader = this.packet.getMessage().getHeaders().get(this.addressingVersion.actionTag, false);
    if (localHeader == null) {
      return null;
    }
    return localHeader.getStringContent();
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.messageId", "com.sun.xml.internal.ws.addressing.request.messageID"})
  public String getMessageID()
  {
    if (this.packet.getMessage() == null) {
      return null;
    }
    return AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
  }
  
  private WSEndpointReference getEPR(QName paramQName)
    throws XMLStreamException
  {
    if (this.packet.getMessage() == null) {
      return null;
    }
    Header localHeader = this.packet.getMessage().getHeaders().get(paramQName, false);
    if (localHeader == null) {
      return null;
    }
    return localHeader.readAsEPR(this.addressingVersion);
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest"})
  public WSEndpointReference getReplyToFromRequest()
  {
    return this._replyToFromRequest;
  }
  
  public void setReplyToFromRequest(WSEndpointReference paramWSEndpointReference)
  {
    this._replyToFromRequest = paramWSEndpointReference;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest"})
  public WSEndpointReference getFaultToFromRequest()
  {
    return this._faultToFromRequest;
  }
  
  public void setFaultToFromRequest(WSEndpointReference paramWSEndpointReference)
  {
    this._faultToFromRequest = paramWSEndpointReference;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest"})
  public String getMessageIdFromRequest()
  {
    return this._msgIdFromRequest;
  }
  
  public void setMessageIdFromRequest(String paramString)
  {
    this._msgIdFromRequest = paramString;
  }
}
