package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.handler.HandlerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;

public class HandlerConfiguration
{
  private final Set<String> roles;
  private final List<Handler> handlerChain;
  private final List<LogicalHandler> logicalHandlers;
  private final List<SOAPHandler> soapHandlers;
  private final List<MessageHandler> messageHandlers;
  private final Set<QName> handlerKnownHeaders;
  
  public HandlerConfiguration(Set<String> paramSet, List<Handler> paramList)
  {
    this.roles = paramSet;
    this.handlerChain = paramList;
    this.logicalHandlers = new ArrayList();
    this.soapHandlers = new ArrayList();
    this.messageHandlers = new ArrayList();
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Handler localHandler = (Handler)localIterator.next();
      if ((localHandler instanceof LogicalHandler))
      {
        this.logicalHandlers.add((LogicalHandler)localHandler);
      }
      else
      {
        Set localSet;
        if ((localHandler instanceof SOAPHandler))
        {
          this.soapHandlers.add((SOAPHandler)localHandler);
          localSet = ((SOAPHandler)localHandler).getHeaders();
          if (localSet != null) {
            localHashSet.addAll(localSet);
          }
        }
        else if ((localHandler instanceof MessageHandler))
        {
          this.messageHandlers.add((MessageHandler)localHandler);
          localSet = ((MessageHandler)localHandler).getHeaders();
          if (localSet != null) {
            localHashSet.addAll(localSet);
          }
        }
        else
        {
          throw new HandlerException("handler.not.valid.type", new Object[] { localHandler.getClass() });
        }
      }
    }
    this.handlerKnownHeaders = Collections.unmodifiableSet(localHashSet);
  }
  
  public HandlerConfiguration(Set<String> paramSet, HandlerConfiguration paramHandlerConfiguration)
  {
    this.roles = paramSet;
    this.handlerChain = paramHandlerConfiguration.handlerChain;
    this.logicalHandlers = paramHandlerConfiguration.logicalHandlers;
    this.soapHandlers = paramHandlerConfiguration.soapHandlers;
    this.messageHandlers = paramHandlerConfiguration.messageHandlers;
    this.handlerKnownHeaders = paramHandlerConfiguration.handlerKnownHeaders;
  }
  
  public Set<String> getRoles()
  {
    return this.roles;
  }
  
  public List<Handler> getHandlerChain()
  {
    if (this.handlerChain == null) {
      return Collections.emptyList();
    }
    return new ArrayList(this.handlerChain);
  }
  
  public List<LogicalHandler> getLogicalHandlers()
  {
    return this.logicalHandlers;
  }
  
  public List<SOAPHandler> getSoapHandlers()
  {
    return this.soapHandlers;
  }
  
  public List<MessageHandler> getMessageHandlers()
  {
    return this.messageHandlers;
  }
  
  public Set<QName> getHandlerKnownHeaders()
  {
    return this.handlerKnownHeaders;
  }
}
