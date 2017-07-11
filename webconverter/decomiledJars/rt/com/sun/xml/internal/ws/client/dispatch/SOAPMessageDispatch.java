package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

public class SOAPMessageDispatch
  extends DispatchImpl<SOAPMessage>
{
  @Deprecated
  public SOAPMessageDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
  }
  
  public SOAPMessageDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
  }
  
  Packet createPacket(SOAPMessage paramSOAPMessage)
  {
    Iterator localIterator = paramSOAPMessage.getMimeHeaders().getAllHeaders();
    Headers localHeaders = new Headers();
    while (localIterator.hasNext())
    {
      localObject = (MimeHeader)localIterator.next();
      localHeaders.add(((MimeHeader)localObject).getName(), ((MimeHeader)localObject).getValue());
    }
    Object localObject = new Packet(SAAJFactory.create(paramSOAPMessage));
    ((Packet)localObject).invocationProperties.put("javax.xml.ws.http.request.headers", localHeaders);
    return localObject;
  }
  
  SOAPMessage toReturnValue(Packet paramPacket)
  {
    try
    {
      if ((paramPacket == null) || (paramPacket.getMessage() == null)) {
        throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
      }
      return paramPacket.getMessage().readAsSOAPMessage();
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
}
