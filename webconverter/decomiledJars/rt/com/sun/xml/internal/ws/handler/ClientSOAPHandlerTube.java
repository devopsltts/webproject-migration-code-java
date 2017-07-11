package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ClientSOAPHandlerTube
  extends HandlerTube
{
  private Set<String> roles;
  
  public ClientSOAPHandlerTube(WSBinding paramWSBinding, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramTube, paramWSDLPort, paramWSBinding);
    if (paramWSBinding.getSOAPVersion() != null) {}
  }
  
  public ClientSOAPHandlerTube(WSBinding paramWSBinding, Tube paramTube, HandlerTube paramHandlerTube)
  {
    super(paramTube, paramHandlerTube, paramWSBinding);
  }
  
  private ClientSOAPHandlerTube(ClientSOAPHandlerTube paramClientSOAPHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramClientSOAPHandlerTube, paramTubeCloner);
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ClientSOAPHandlerTube(this, paramTubeCloner);
  }
  
  void setUpProcessor()
  {
    if (this.handlers == null)
    {
      this.handlers = new ArrayList();
      HandlerConfiguration localHandlerConfiguration = ((BindingImpl)getBinding()).getHandlerConfig();
      List localList = localHandlerConfiguration.getSoapHandlers();
      if (!localList.isEmpty())
      {
        this.handlers.addAll(localList);
        this.roles = new HashSet();
        this.roles.addAll(localHandlerConfiguration.getRoles());
        this.processor = new SOAPHandlerProcessor(true, this, getBinding(), this.handlers);
      }
    }
  }
  
  MessageUpdatableContext getContext(Packet paramPacket)
  {
    SOAPMessageContextImpl localSOAPMessageContextImpl = new SOAPMessageContextImpl(getBinding(), paramPacket, this.roles);
    return localSOAPMessageContextImpl;
  }
  
  boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean)
  {
    Map localMap = (Map)paramMessageUpdatableContext.get("javax.xml.ws.binding.attachments.outbound");
    AttachmentSet localAttachmentSet = paramMessageUpdatableContext.packet.getMessage().getAttachments();
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      if (localAttachmentSet.get(str) == null)
      {
        DataHandlerAttachment localDataHandlerAttachment = new DataHandlerAttachment(str, (DataHandler)localMap.get(str));
        localAttachmentSet.add(localDataHandlerAttachment);
      }
    }
    boolean bool;
    try
    {
      bool = this.processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, paramMessageUpdatableContext, !paramBoolean);
    }
    catch (WebServiceException localWebServiceException)
    {
      this.remedyActionTaken = true;
      throw localWebServiceException;
    }
    catch (RuntimeException localRuntimeException)
    {
      this.remedyActionTaken = true;
      throw new WebServiceException(localRuntimeException);
    }
    if (!bool) {
      this.remedyActionTaken = true;
    }
    return bool;
  }
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean)
  {
    try
    {
      this.processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, paramMessageUpdatableContext, paramBoolean);
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new WebServiceException(localRuntimeException);
    }
  }
  
  void closeHandlers(MessageContext paramMessageContext)
  {
    closeClientsideHandlers(paramMessageContext);
  }
}
