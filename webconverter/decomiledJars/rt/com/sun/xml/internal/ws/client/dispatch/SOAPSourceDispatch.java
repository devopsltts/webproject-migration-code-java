package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

final class SOAPSourceDispatch
  extends DispatchImpl<Source>
{
  @Deprecated
  public SOAPSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    assert (!isXMLHttp(paramBindingImpl));
  }
  
  public SOAPSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    assert (!isXMLHttp(paramBindingImpl));
  }
  
  Source toReturnValue(Packet paramPacket)
  {
    Message localMessage = paramPacket.getMessage();
    switch (1.$SwitchMap$javax$xml$ws$Service$Mode[this.mode.ordinal()])
    {
    case 1: 
      return localMessage.readPayloadAsSource();
    case 2: 
      return localMessage.readEnvelopeAsSource();
    }
    throw new WebServiceException("Unrecognized dispatch mode");
  }
  
  Packet createPacket(Source paramSource)
  {
    Object localObject;
    if (paramSource == null) {
      localObject = Messages.createEmpty(this.soapVersion);
    } else {
      switch (1.$SwitchMap$javax$xml$ws$Service$Mode[this.mode.ordinal()])
      {
      case 1: 
        localObject = new PayloadSourceMessage(null, paramSource, setOutboundAttachments(), this.soapVersion);
        break;
      case 2: 
        localObject = Messages.create(paramSource, this.soapVersion);
        break;
      default: 
        throw new WebServiceException("Unrecognized message mode");
      }
    }
    return new Packet((Message)localObject);
  }
}
