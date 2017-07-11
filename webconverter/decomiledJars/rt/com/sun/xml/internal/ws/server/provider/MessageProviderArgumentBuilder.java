package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;

final class MessageProviderArgumentBuilder
  extends ProviderArgumentsBuilder<Message>
{
  private final SOAPVersion soapVersion;
  
  public MessageProviderArgumentBuilder(SOAPVersion paramSOAPVersion)
  {
    this.soapVersion = paramSOAPVersion;
  }
  
  public Message getParameter(Packet paramPacket)
  {
    return paramPacket.getMessage();
  }
  
  protected Message getResponseMessage(Message paramMessage)
  {
    return paramMessage;
  }
  
  protected Message getResponseMessage(Exception paramException)
  {
    return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, paramException);
  }
}
