package com.oracle.webservices.internal.api.message;

import com.oracle.webservices.internal.api.EnvelopeStyle.Style;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;

public abstract class MessageContextFactory
{
  private static final MessageContextFactory DEFAULT = new com.sun.xml.internal.ws.api.message.MessageContextFactory(new WebServiceFeature[0]);
  
  public MessageContextFactory() {}
  
  protected abstract MessageContextFactory newFactory(WebServiceFeature... paramVarArgs);
  
  public abstract MessageContext createContext();
  
  public abstract MessageContext createContext(SOAPMessage paramSOAPMessage);
  
  public abstract MessageContext createContext(Source paramSource);
  
  public abstract MessageContext createContext(Source paramSource, EnvelopeStyle.Style paramStyle);
  
  public abstract MessageContext createContext(InputStream paramInputStream, String paramString)
    throws IOException;
  
  @Deprecated
  public abstract MessageContext createContext(InputStream paramInputStream, MimeHeaders paramMimeHeaders)
    throws IOException;
  
  public static MessageContextFactory createFactory(WebServiceFeature... paramVarArgs)
  {
    return createFactory(null, paramVarArgs);
  }
  
  public static MessageContextFactory createFactory(ClassLoader paramClassLoader, WebServiceFeature... paramVarArgs)
  {
    Iterator localIterator = ServiceFinder.find(MessageContextFactory.class, paramClassLoader).iterator();
    while (localIterator.hasNext())
    {
      MessageContextFactory localMessageContextFactory1 = (MessageContextFactory)localIterator.next();
      MessageContextFactory localMessageContextFactory2 = localMessageContextFactory1.newFactory(paramVarArgs);
      if (localMessageContextFactory2 != null) {
        return localMessageContextFactory2;
      }
    }
    return new com.sun.xml.internal.ws.api.message.MessageContextFactory(paramVarArgs);
  }
  
  @Deprecated
  public abstract MessageContext doCreate();
  
  @Deprecated
  public abstract MessageContext doCreate(SOAPMessage paramSOAPMessage);
  
  @Deprecated
  public abstract MessageContext doCreate(Source paramSource, SOAPVersion paramSOAPVersion);
  
  @Deprecated
  public static MessageContext create(ClassLoader... paramVarArgs)
  {
    serviceFinder(paramVarArgs, new Creator()
    {
      public MessageContext create(MessageContextFactory paramAnonymousMessageContextFactory)
      {
        return paramAnonymousMessageContextFactory.doCreate();
      }
    });
  }
  
  @Deprecated
  public static MessageContext create(SOAPMessage paramSOAPMessage, ClassLoader... paramVarArgs)
  {
    serviceFinder(paramVarArgs, new Creator()
    {
      public MessageContext create(MessageContextFactory paramAnonymousMessageContextFactory)
      {
        return paramAnonymousMessageContextFactory.doCreate(this.val$m);
      }
    });
  }
  
  @Deprecated
  public static MessageContext create(Source paramSource, final SOAPVersion paramSOAPVersion, ClassLoader... paramVarArgs)
  {
    serviceFinder(paramVarArgs, new Creator()
    {
      public MessageContext create(MessageContextFactory paramAnonymousMessageContextFactory)
      {
        return paramAnonymousMessageContextFactory.doCreate(this.val$m, paramSOAPVersion);
      }
    });
  }
  
  @Deprecated
  private static MessageContext serviceFinder(ClassLoader[] paramArrayOfClassLoader, Creator paramCreator)
  {
    ClassLoader localClassLoader = paramArrayOfClassLoader.length == 0 ? null : paramArrayOfClassLoader[0];
    Iterator localIterator = ServiceFinder.find(MessageContextFactory.class, localClassLoader).iterator();
    while (localIterator.hasNext())
    {
      MessageContextFactory localMessageContextFactory = (MessageContextFactory)localIterator.next();
      MessageContext localMessageContext = paramCreator.create(localMessageContextFactory);
      if (localMessageContext != null) {
        return localMessageContext;
      }
    }
    return paramCreator.create(DEFAULT);
  }
  
  @Deprecated
  private static abstract interface Creator
  {
    public abstract MessageContext create(MessageContextFactory paramMessageContextFactory);
  }
}
