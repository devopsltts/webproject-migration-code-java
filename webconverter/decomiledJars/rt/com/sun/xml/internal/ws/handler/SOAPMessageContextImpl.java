package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageContextImpl
  extends MessageUpdatableContext
  implements SOAPMessageContext
{
  private Set<String> roles;
  private SOAPMessage soapMsg = null;
  private WSBinding binding;
  
  public SOAPMessageContextImpl(WSBinding paramWSBinding, Packet paramPacket, Set<String> paramSet)
  {
    super(paramPacket);
    this.binding = paramWSBinding;
    this.roles = paramSet;
  }
  
  public SOAPMessage getMessage()
  {
    if (this.soapMsg == null) {
      try
      {
        Message localMessage = this.packet.getMessage();
        this.soapMsg = (localMessage != null ? localMessage.readAsSOAPMessage() : null);
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    return this.soapMsg;
  }
  
  public void setMessage(SOAPMessage paramSOAPMessage)
  {
    try
    {
      this.soapMsg = paramSOAPMessage;
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  void setPacketMessage(Message paramMessage)
  {
    if (paramMessage != null)
    {
      this.packet.setMessage(paramMessage);
      this.soapMsg = null;
    }
  }
  
  protected void updateMessage()
  {
    if (this.soapMsg != null)
    {
      this.packet.setMessage(SAAJFactory.create(this.soapMsg));
      this.soapMsg = null;
    }
  }
  
  public Object[] getHeaders(QName paramQName, JAXBContext paramJAXBContext, boolean paramBoolean)
  {
    SOAPVersion localSOAPVersion = this.binding.getSOAPVersion();
    ArrayList localArrayList = new ArrayList();
    try
    {
      Iterator localIterator = this.packet.getMessage().getHeaders().getHeaders(paramQName, false);
      if (paramBoolean) {
        while (localIterator.hasNext()) {
          localArrayList.add(((Header)localIterator.next()).readAsJAXB(paramJAXBContext.createUnmarshaller()));
        }
      }
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        String str = localHeader.getRole(localSOAPVersion);
        if (getRoles().contains(str)) {
          localArrayList.add(localHeader.readAsJAXB(paramJAXBContext.createUnmarshaller()));
        }
      }
      return localArrayList.toArray();
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  public Set<String> getRoles()
  {
    return this.roles;
  }
}
