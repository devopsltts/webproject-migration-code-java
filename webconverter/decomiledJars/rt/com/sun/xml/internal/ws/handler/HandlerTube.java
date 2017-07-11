package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

public abstract class HandlerTube
  extends AbstractFilterTubeImpl
{
  HandlerTube cousinTube;
  protected List<Handler> handlers;
  HandlerProcessor processor;
  boolean remedyActionTaken = false;
  @Nullable
  protected final WSDLPort port;
  boolean requestProcessingSucessful = false;
  private WSBinding binding;
  private HandlerConfiguration hc;
  private HandlerTubeExchange exchange;
  
  public HandlerTube(Tube paramTube, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
  {
    super(paramTube);
    this.port = paramWSDLPort;
    this.binding = paramWSBinding;
  }
  
  public HandlerTube(Tube paramTube, HandlerTube paramHandlerTube, WSBinding paramWSBinding)
  {
    super(paramTube);
    this.cousinTube = paramHandlerTube;
    this.binding = paramWSBinding;
    if (paramHandlerTube != null) {
      this.port = paramHandlerTube.port;
    } else {
      this.port = null;
    }
  }
  
  protected HandlerTube(HandlerTube paramHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramHandlerTube, paramTubeCloner);
    if (paramHandlerTube.cousinTube != null) {
      this.cousinTube = ((HandlerTube)paramTubeCloner.copy(paramHandlerTube.cousinTube));
    }
    this.port = paramHandlerTube.port;
    this.binding = paramHandlerTube.binding;
  }
  
  protected WSBinding getBinding()
  {
    return this.binding;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    setupExchange();
    if (isHandleFalse())
    {
      this.remedyActionTaken = true;
      return doInvoke(this.next, paramPacket);
    }
    setUpProcessorInternal();
    MessageUpdatableContext localMessageUpdatableContext = getContext(paramPacket);
    boolean bool1 = checkOneWay(paramPacket);
    try
    {
      if (!isHandlerChainEmpty())
      {
        boolean bool2 = callHandlersOnRequest(localMessageUpdatableContext, bool1);
        localMessageUpdatableContext.updatePacket();
        if ((!bool1) && (!bool2))
        {
          localNextAction2 = doReturnWith(paramPacket);
          return localNextAction2;
        }
      }
      this.requestProcessingSucessful = true;
      NextAction localNextAction1 = doInvoke(this.next, paramPacket);
      return localNextAction1;
    }
    catch (RuntimeException localRuntimeException)
    {
      NextAction localNextAction2;
      if (bool1)
      {
        if (paramPacket.transportBackChannel != null) {
          paramPacket.transportBackChannel.close();
        }
        paramPacket.setMessage(null);
        localNextAction2 = doReturnWith(paramPacket);
        return localNextAction2;
      }
      throw localRuntimeException;
    }
    finally
    {
      if (!this.requestProcessingSucessful) {
        initiateClosing(localMessageUpdatableContext.getMessageContext());
      }
    }
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    setupExchange();
    MessageUpdatableContext localMessageUpdatableContext = getContext(paramPacket);
    try
    {
      if ((isHandleFalse()) || (paramPacket.getMessage() == null))
      {
        NextAction localNextAction = doReturnWith(paramPacket);
        return localNextAction;
      }
      setUpProcessorInternal();
      boolean bool = isHandleFault(paramPacket);
      if (!isHandlerChainEmpty()) {
        callHandlersOnResponse(localMessageUpdatableContext, bool);
      }
    }
    finally
    {
      initiateClosing(localMessageUpdatableContext.getMessageContext());
    }
    localMessageUpdatableContext.updatePacket();
    return doReturnWith(paramPacket);
  }
  
  public NextAction processException(Throwable paramThrowable)
  {
    try
    {
      NextAction localNextAction = doThrow(paramThrowable);
      Packet localPacket1;
      MessageUpdatableContext localMessageUpdatableContext1;
      return localNextAction;
    }
    finally
    {
      Packet localPacket2 = Fiber.current().getPacket();
      MessageUpdatableContext localMessageUpdatableContext2 = getContext(localPacket2);
      initiateClosing(localMessageUpdatableContext2.getMessageContext());
    }
  }
  
  protected void initiateClosing(MessageContext paramMessageContext) {}
  
  public final void close(MessageContext paramMessageContext)
  {
    if ((this.requestProcessingSucessful) && (this.cousinTube != null)) {
      this.cousinTube.close(paramMessageContext);
    }
    if (this.processor != null) {
      closeHandlers(paramMessageContext);
    }
    this.exchange = null;
    this.requestProcessingSucessful = false;
  }
  
  abstract void closeHandlers(MessageContext paramMessageContext);
  
  protected void closeClientsideHandlers(MessageContext paramMessageContext)
  {
    if (this.processor == null) {
      return;
    }
    if (this.remedyActionTaken)
    {
      this.processor.closeHandlers(paramMessageContext, this.processor.getIndex(), 0);
      this.processor.setIndex(-1);
      this.remedyActionTaken = false;
    }
    else
    {
      this.processor.closeHandlers(paramMessageContext, this.handlers.size() - 1, 0);
    }
  }
  
  protected void closeServersideHandlers(MessageContext paramMessageContext)
  {
    if (this.processor == null) {
      return;
    }
    if (this.remedyActionTaken)
    {
      this.processor.closeHandlers(paramMessageContext, this.processor.getIndex(), this.handlers.size() - 1);
      this.processor.setIndex(-1);
      this.remedyActionTaken = false;
    }
    else
    {
      this.processor.closeHandlers(paramMessageContext, 0, this.handlers.size() - 1);
    }
  }
  
  abstract void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean);
  
  abstract boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean);
  
  private boolean checkOneWay(Packet paramPacket)
  {
    if (this.port != null) {
      return paramPacket.getMessage().isOneWay(this.port);
    }
    return (paramPacket.expectReply == null) || (!paramPacket.expectReply.booleanValue());
  }
  
  private void setUpProcessorInternal()
  {
    HandlerConfiguration localHandlerConfiguration = ((BindingImpl)this.binding).getHandlerConfig();
    if (localHandlerConfiguration != this.hc) {
      resetProcessor();
    }
    this.hc = localHandlerConfiguration;
    setUpProcessor();
  }
  
  abstract void setUpProcessor();
  
  protected void resetProcessor()
  {
    this.handlers = null;
  }
  
  public final boolean isHandlerChainEmpty()
  {
    return this.handlers.isEmpty();
  }
  
  abstract MessageUpdatableContext getContext(Packet paramPacket);
  
  private boolean isHandleFault(Packet paramPacket)
  {
    if (this.cousinTube != null) {
      return this.exchange.isHandleFault();
    }
    boolean bool = paramPacket.getMessage().isFault();
    this.exchange.setHandleFault(bool);
    return bool;
  }
  
  final void setHandleFault()
  {
    this.exchange.setHandleFault(true);
  }
  
  private boolean isHandleFalse()
  {
    return this.exchange.isHandleFalse();
  }
  
  final void setHandleFalse()
  {
    this.exchange.setHandleFalse();
  }
  
  private void setupExchange()
  {
    if (this.exchange == null)
    {
      this.exchange = new HandlerTubeExchange();
      if (this.cousinTube != null) {
        this.cousinTube.exchange = this.exchange;
      }
    }
    else if (this.cousinTube != null)
    {
      this.cousinTube.exchange = this.exchange;
    }
  }
  
  static final class HandlerTubeExchange
  {
    private boolean handleFalse;
    private boolean handleFault;
    
    HandlerTubeExchange() {}
    
    boolean isHandleFault()
    {
      return this.handleFault;
    }
    
    void setHandleFault(boolean paramBoolean)
    {
      this.handleFault = paramBoolean;
    }
    
    public boolean isHandleFalse()
    {
      return this.handleFalse;
    }
    
    void setHandleFalse()
    {
      this.handleFalse = true;
    }
  }
}
