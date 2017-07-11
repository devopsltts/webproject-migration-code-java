package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class LogicalMessageImpl
  implements LogicalMessage
{
  private Packet packet;
  protected BindingContext defaultJaxbContext;
  private ImmutableLM lm = null;
  
  public LogicalMessageImpl(BindingContext paramBindingContext, Packet paramPacket)
  {
    this.packet = paramPacket;
    this.defaultJaxbContext = paramBindingContext;
  }
  
  public Source getPayload()
  {
    if (this.lm == null)
    {
      Source localSource = this.packet.getMessage().copy().readPayloadAsSource();
      if ((localSource instanceof DOMSource)) {
        this.lm = createLogicalMessageImpl(localSource);
      }
      return localSource;
    }
    return this.lm.getPayload();
  }
  
  public void setPayload(Source paramSource)
  {
    this.lm = createLogicalMessageImpl(paramSource);
  }
  
  private ImmutableLM createLogicalMessageImpl(Source paramSource)
  {
    if (paramSource == null) {
      this.lm = new EmptyLogicalMessageImpl();
    } else if ((paramSource instanceof DOMSource)) {
      this.lm = new DOMLogicalMessageImpl((DOMSource)paramSource);
    } else {
      this.lm = new SourceLogicalMessageImpl(paramSource);
    }
    return this.lm;
  }
  
  public Object getPayload(BindingContext paramBindingContext)
  {
    if (paramBindingContext == null) {
      paramBindingContext = this.defaultJaxbContext;
    }
    if (paramBindingContext == null) {
      throw new WebServiceException("JAXBContext parameter cannot be null");
    }
    Object localObject;
    if (this.lm == null)
    {
      try
      {
        localObject = this.packet.getMessage().copy().readPayloadAsJAXB(paramBindingContext.createUnmarshaller());
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    else
    {
      localObject = this.lm.getPayload(paramBindingContext);
      this.lm = new JAXBLogicalMessageImpl(paramBindingContext.getJAXBContext(), localObject);
    }
    return localObject;
  }
  
  public Object getPayload(JAXBContext paramJAXBContext)
  {
    if (paramJAXBContext == null) {
      return getPayload(this.defaultJaxbContext);
    }
    if (paramJAXBContext == null) {
      throw new WebServiceException("JAXBContext parameter cannot be null");
    }
    Object localObject;
    if (this.lm == null)
    {
      try
      {
        localObject = this.packet.getMessage().copy().readPayloadAsJAXB(paramJAXBContext.createUnmarshaller());
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    else
    {
      localObject = this.lm.getPayload(paramJAXBContext);
      this.lm = new JAXBLogicalMessageImpl(paramJAXBContext, localObject);
    }
    return localObject;
  }
  
  public void setPayload(Object paramObject, BindingContext paramBindingContext)
  {
    if (paramBindingContext == null) {
      paramBindingContext = this.defaultJaxbContext;
    }
    if (paramObject == null) {
      this.lm = new EmptyLogicalMessageImpl();
    } else {
      this.lm = new JAXBLogicalMessageImpl(paramBindingContext.getJAXBContext(), paramObject);
    }
  }
  
  public void setPayload(Object paramObject, JAXBContext paramJAXBContext)
  {
    if (paramJAXBContext == null) {
      setPayload(paramObject, this.defaultJaxbContext);
    }
    if (paramObject == null) {
      this.lm = new EmptyLogicalMessageImpl();
    } else {
      this.lm = new JAXBLogicalMessageImpl(paramJAXBContext, paramObject);
    }
  }
  
  public boolean isPayloadModifed()
  {
    return this.lm != null;
  }
  
  public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
  {
    assert (isPayloadModifed());
    if (isPayloadModifed()) {
      return this.lm.getMessage(paramMessageHeaders, paramAttachmentSet, paramWSBinding);
    }
    return this.packet.getMessage();
  }
  
  private class DOMLogicalMessageImpl
    extends LogicalMessageImpl.SourceLogicalMessageImpl
  {
    private DOMSource dom;
    
    public DOMLogicalMessageImpl(DOMSource paramDOMSource)
    {
      super(paramDOMSource);
      this.dom = paramDOMSource;
    }
    
    public Source getPayload()
    {
      return this.dom;
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      Object localObject = this.dom.getNode();
      if (((Node)localObject).getNodeType() == 9) {
        localObject = ((Document)localObject).getDocumentElement();
      }
      return new DOMMessage(paramWSBinding.getSOAPVersion(), paramMessageHeaders, (Element)localObject, paramAttachmentSet);
    }
  }
  
  private class EmptyLogicalMessageImpl
    extends LogicalMessageImpl.ImmutableLM
  {
    public EmptyLogicalMessageImpl()
    {
      super(null);
    }
    
    public Source getPayload()
    {
      return null;
    }
    
    public Object getPayload(JAXBContext paramJAXBContext)
    {
      return null;
    }
    
    public Object getPayload(BindingContext paramBindingContext)
    {
      return null;
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      return new EmptyMessageImpl(paramMessageHeaders, paramAttachmentSet, paramWSBinding.getSOAPVersion());
    }
  }
  
  private abstract class ImmutableLM
  {
    private ImmutableLM() {}
    
    public abstract Source getPayload();
    
    public abstract Object getPayload(BindingContext paramBindingContext);
    
    public abstract Object getPayload(JAXBContext paramJAXBContext);
    
    public abstract Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding);
  }
  
  private class JAXBLogicalMessageImpl
    extends LogicalMessageImpl.ImmutableLM
  {
    private JAXBContext ctxt;
    private Object o;
    
    public JAXBLogicalMessageImpl(JAXBContext paramJAXBContext, Object paramObject)
    {
      super(null);
      this.ctxt = paramJAXBContext;
      this.o = paramObject;
    }
    
    public Source getPayload()
    {
      JAXBContext localJAXBContext = this.ctxt;
      if (localJAXBContext == null) {
        localJAXBContext = LogicalMessageImpl.this.defaultJaxbContext.getJAXBContext();
      }
      try
      {
        return new JAXBSource(localJAXBContext, this.o);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Object getPayload(JAXBContext paramJAXBContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramJAXBContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Object getPayload(BindingContext paramBindingContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramBindingContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      return JAXBMessage.create(BindingContextFactory.create(this.ctxt), this.o, paramWSBinding.getSOAPVersion(), paramMessageHeaders, paramAttachmentSet);
    }
  }
  
  private class SourceLogicalMessageImpl
    extends LogicalMessageImpl.ImmutableLM
  {
    private Source payloadSrc;
    
    public SourceLogicalMessageImpl(Source paramSource)
    {
      super(null);
      this.payloadSrc = paramSource;
    }
    
    public Source getPayload()
    {
      assert (!(this.payloadSrc instanceof DOMSource));
      try
      {
        Transformer localTransformer = XmlUtil.newTransformer();
        DOMResult localDOMResult = new DOMResult();
        localTransformer.transform(this.payloadSrc, localDOMResult);
        DOMSource localDOMSource = new DOMSource(localDOMResult.getNode());
        LogicalMessageImpl.this.lm = new LogicalMessageImpl.DOMLogicalMessageImpl(LogicalMessageImpl.this, localDOMSource);
        this.payloadSrc = null;
        return localDOMSource;
      }
      catch (TransformerException localTransformerException)
      {
        throw new WebServiceException(localTransformerException);
      }
    }
    
    public Object getPayload(JAXBContext paramJAXBContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramJAXBContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Object getPayload(BindingContext paramBindingContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramBindingContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      assert (this.payloadSrc != null);
      return new PayloadSourceMessage(paramMessageHeaders, this.payloadSrc, paramAttachmentSet, paramWSBinding.getSOAPVersion());
    }
  }
}
