package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class EndpointResponseMessageBuilder
{
  public static final EndpointResponseMessageBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
  public static final EndpointResponseMessageBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
  
  public EndpointResponseMessageBuilder() {}
  
  public abstract Message createMessage(Object[] paramArrayOfObject, Object paramObject);
  
  public static final class Bare
    extends EndpointResponseMessageBuilder.JAXB
  {
    private final int methodPos;
    private final ValueGetter getter;
    
    public Bare(ParameterImpl paramParameterImpl, SOAPVersion paramSOAPVersion)
    {
      super(paramSOAPVersion);
      this.methodPos = paramParameterImpl.getIndex();
      this.getter = ValueGetter.get(paramParameterImpl);
    }
    
    Object build(Object[] paramArrayOfObject, Object paramObject)
    {
      if (this.methodPos == -1) {
        return paramObject;
      }
      return this.getter.get(paramArrayOfObject[this.methodPos]);
    }
  }
  
  public static final class DocLit
    extends EndpointResponseMessageBuilder.Wrapped
  {
    private final PropertyAccessor[] accessors;
    private final Class wrapper;
    private boolean dynamicWrapper;
    private BindingContext bindingContext;
    
    public DocLit(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion)
    {
      super(paramSOAPVersion);
      this.bindingContext = paramWrapperParameter.getOwner().getBindingContext();
      this.wrapper = ((Class)paramWrapperParameter.getXMLBridge().getTypeInfo().type);
      this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
      this.children = paramWrapperParameter.getWrapperChildren();
      this.parameterBridges = new XMLBridge[this.children.size()];
      this.accessors = new PropertyAccessor[this.children.size()];
      for (int i = 0; i < this.accessors.length; i++)
      {
        ParameterImpl localParameterImpl = (ParameterImpl)this.children.get(i);
        QName localQName = localParameterImpl.getName();
        if (this.dynamicWrapper)
        {
          this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getInlinedRepeatedElementBridge();
          if (this.parameterBridges[i] == null) {
            this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
          }
        }
        else
        {
          try
          {
            this.accessors[i] = (this.dynamicWrapper ? null : localParameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(this.wrapper, localQName.getNamespaceURI(), localQName.getLocalPart()));
          }
          catch (JAXBException localJAXBException)
          {
            throw new WebServiceException(this.wrapper + " do not have a property of the name " + localQName, localJAXBException);
          }
        }
      }
    }
    
    Object build(Object[] paramArrayOfObject, Object paramObject)
    {
      if (this.dynamicWrapper) {
        return buildWrapperComposite(paramArrayOfObject, paramObject);
      }
      try
      {
        Object localObject1 = this.bindingContext.newWrapperInstace(this.wrapper);
        for (int i = this.indices.length - 1; i >= 0; i--) {
          if (this.indices[i] == -1) {
            this.accessors[i].set(localObject1, paramObject);
          } else {
            this.accessors[i].set(localObject1, this.getters[i].get(paramArrayOfObject[this.indices[i]]));
          }
        }
        return localObject1;
      }
      catch (InstantiationException localInstantiationException)
      {
        localObject2 = new InstantiationError(localInstantiationException.getMessage());
        ((Error)localObject2).initCause(localInstantiationException);
        throw ((Throwable)localObject2);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Object localObject2 = new IllegalAccessError(localIllegalAccessException.getMessage());
        ((Error)localObject2).initCause(localIllegalAccessException);
        throw ((Throwable)localObject2);
      }
      catch (DatabindingException localDatabindingException)
      {
        throw new WebServiceException(localDatabindingException);
      }
    }
  }
  
  private static final class Empty
    extends EndpointResponseMessageBuilder
  {
    private final SOAPVersion soapVersion;
    
    public Empty(SOAPVersion paramSOAPVersion)
    {
      this.soapVersion = paramSOAPVersion;
    }
    
    public Message createMessage(Object[] paramArrayOfObject, Object paramObject)
    {
      return Messages.createEmpty(this.soapVersion);
    }
  }
  
  private static abstract class JAXB
    extends EndpointResponseMessageBuilder
  {
    private final XMLBridge bridge;
    private final SOAPVersion soapVersion;
    
    protected JAXB(XMLBridge paramXMLBridge, SOAPVersion paramSOAPVersion)
    {
      assert (paramXMLBridge != null);
      this.bridge = paramXMLBridge;
      this.soapVersion = paramSOAPVersion;
    }
    
    public final Message createMessage(Object[] paramArrayOfObject, Object paramObject)
    {
      return JAXBMessage.create(this.bridge, build(paramArrayOfObject, paramObject), this.soapVersion);
    }
    
    abstract Object build(Object[] paramArrayOfObject, Object paramObject);
  }
  
  public static final class RpcLit
    extends EndpointResponseMessageBuilder.Wrapped
  {
    public RpcLit(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion)
    {
      super(paramSOAPVersion);
      assert (paramWrapperParameter.getTypeInfo().type == WrapperComposite.class);
      this.parameterBridges = new XMLBridge[this.children.size()];
      for (int i = 0; i < this.parameterBridges.length; i++) {
        this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
      }
    }
    
    Object build(Object[] paramArrayOfObject, Object paramObject)
    {
      return buildWrapperComposite(paramArrayOfObject, paramObject);
    }
  }
  
  static abstract class Wrapped
    extends EndpointResponseMessageBuilder.JAXB
  {
    protected final int[] indices;
    protected final ValueGetter[] getters;
    protected XMLBridge[] parameterBridges;
    protected List<ParameterImpl> children;
    
    protected Wrapped(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion)
    {
      super(paramSOAPVersion);
      this.children = paramWrapperParameter.getWrapperChildren();
      this.indices = new int[this.children.size()];
      this.getters = new ValueGetter[this.children.size()];
      for (int i = 0; i < this.indices.length; i++)
      {
        ParameterImpl localParameterImpl = (ParameterImpl)this.children.get(i);
        this.indices[i] = localParameterImpl.getIndex();
        this.getters[i] = ValueGetter.get(localParameterImpl);
      }
    }
    
    WrapperComposite buildWrapperComposite(Object[] paramArrayOfObject, Object paramObject)
    {
      WrapperComposite localWrapperComposite = new WrapperComposite();
      localWrapperComposite.bridges = this.parameterBridges;
      localWrapperComposite.values = new Object[this.parameterBridges.length];
      for (int i = this.indices.length - 1; i >= 0; i--)
      {
        Object localObject;
        if (this.indices[i] == -1) {
          localObject = this.getters[i].get(paramObject);
        } else {
          localObject = this.getters[i].get(paramArrayOfObject[this.indices[i]]);
        }
        if (localObject == null) {
          throw new WebServiceException("Method Parameter: " + ((ParameterImpl)this.children.get(i)).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
        }
        localWrapperComposite.values[i] = localObject;
      }
      return localWrapperComposite;
    }
  }
}
