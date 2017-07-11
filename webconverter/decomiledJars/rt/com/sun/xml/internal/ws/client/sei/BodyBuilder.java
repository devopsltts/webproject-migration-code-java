package com.sun.xml.internal.ws.client.sei;

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

abstract class BodyBuilder
{
  static final BodyBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
  static final BodyBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
  
  BodyBuilder() {}
  
  abstract Message createMessage(Object[] paramArrayOfObject);
  
  static final class Bare
    extends BodyBuilder.JAXB
  {
    private final int methodPos;
    private final ValueGetter getter;
    
    Bare(ParameterImpl paramParameterImpl, SOAPVersion paramSOAPVersion, ValueGetter paramValueGetter)
    {
      super(paramSOAPVersion);
      this.methodPos = paramParameterImpl.getIndex();
      this.getter = paramValueGetter;
    }
    
    Object build(Object[] paramArrayOfObject)
    {
      return this.getter.get(paramArrayOfObject[this.methodPos]);
    }
  }
  
  static final class DocLit
    extends BodyBuilder.Wrapped
  {
    private final PropertyAccessor[] accessors;
    private final Class wrapper;
    private BindingContext bindingContext;
    private boolean dynamicWrapper;
    
    DocLit(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion, ValueGetterFactory paramValueGetterFactory)
    {
      super(paramSOAPVersion, paramValueGetterFactory);
      this.bindingContext = paramWrapperParameter.getOwner().getBindingContext();
      this.wrapper = ((Class)paramWrapperParameter.getXMLBridge().getTypeInfo().type);
      this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
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
            this.accessors[i] = localParameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(this.wrapper, localQName.getNamespaceURI(), localQName.getLocalPart());
          }
          catch (JAXBException localJAXBException)
          {
            throw new WebServiceException(this.wrapper + " do not have a property of the name " + localQName, localJAXBException);
          }
        }
      }
    }
    
    Object build(Object[] paramArrayOfObject)
    {
      if (this.dynamicWrapper) {
        return buildWrapperComposite(paramArrayOfObject);
      }
      try
      {
        Object localObject1 = this.bindingContext.newWrapperInstace(this.wrapper);
        for (int i = this.indices.length - 1; i >= 0; i--) {
          this.accessors[i].set(localObject1, this.getters[i].get(paramArrayOfObject[this.indices[i]]));
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
    extends BodyBuilder
  {
    private final SOAPVersion soapVersion;
    
    public Empty(SOAPVersion paramSOAPVersion)
    {
      this.soapVersion = paramSOAPVersion;
    }
    
    Message createMessage(Object[] paramArrayOfObject)
    {
      return Messages.createEmpty(this.soapVersion);
    }
  }
  
  private static abstract class JAXB
    extends BodyBuilder
  {
    private final XMLBridge bridge;
    private final SOAPVersion soapVersion;
    
    protected JAXB(XMLBridge paramXMLBridge, SOAPVersion paramSOAPVersion)
    {
      assert (paramXMLBridge != null);
      this.bridge = paramXMLBridge;
      this.soapVersion = paramSOAPVersion;
    }
    
    final Message createMessage(Object[] paramArrayOfObject)
    {
      return JAXBMessage.create(this.bridge, build(paramArrayOfObject), this.soapVersion);
    }
    
    abstract Object build(Object[] paramArrayOfObject);
  }
  
  static final class RpcLit
    extends BodyBuilder.Wrapped
  {
    RpcLit(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion, ValueGetterFactory paramValueGetterFactory)
    {
      super(paramSOAPVersion, paramValueGetterFactory);
      assert (paramWrapperParameter.getTypeInfo().type == WrapperComposite.class);
      this.parameterBridges = new XMLBridge[this.children.size()];
      for (int i = 0; i < this.parameterBridges.length; i++) {
        this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
      }
    }
    
    Object build(Object[] paramArrayOfObject)
    {
      return buildWrapperComposite(paramArrayOfObject);
    }
  }
  
  static abstract class Wrapped
    extends BodyBuilder.JAXB
  {
    protected final int[] indices;
    protected final ValueGetter[] getters;
    protected XMLBridge[] parameterBridges;
    protected List<ParameterImpl> children;
    
    protected Wrapped(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion, ValueGetterFactory paramValueGetterFactory)
    {
      super(paramSOAPVersion);
      this.children = paramWrapperParameter.getWrapperChildren();
      this.indices = new int[this.children.size()];
      this.getters = new ValueGetter[this.children.size()];
      for (int i = 0; i < this.indices.length; i++)
      {
        ParameterImpl localParameterImpl = (ParameterImpl)this.children.get(i);
        this.indices[i] = localParameterImpl.getIndex();
        this.getters[i] = paramValueGetterFactory.get(localParameterImpl);
      }
    }
    
    protected WrapperComposite buildWrapperComposite(Object[] paramArrayOfObject)
    {
      WrapperComposite localWrapperComposite = new WrapperComposite();
      localWrapperComposite.bridges = this.parameterBridges;
      localWrapperComposite.values = new Object[this.parameterBridges.length];
      for (int i = this.indices.length - 1; i >= 0; i--)
      {
        Object localObject = this.getters[i].get(paramArrayOfObject[this.indices[i]]);
        if (localObject == null) {
          throw new WebServiceException("Method Parameter: " + ((ParameterImpl)this.children.get(i)).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
        }
        localWrapperComposite.values[i] = localObject;
      }
      return localWrapperComposite;
    }
  }
}
