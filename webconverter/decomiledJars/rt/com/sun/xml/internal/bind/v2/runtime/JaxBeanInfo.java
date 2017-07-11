package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class JaxBeanInfo<BeanT>
{
  protected boolean isNilIncluded = false;
  protected short flag;
  private static final short FLAG_IS_ELEMENT = 1;
  private static final short FLAG_IS_IMMUTABLE = 2;
  private static final short FLAG_HAS_ELEMENT_ONLY_CONTENTMODEL = 4;
  private static final short FLAG_HAS_BEFORE_UNMARSHAL_METHOD = 8;
  private static final short FLAG_HAS_AFTER_UNMARSHAL_METHOD = 16;
  private static final short FLAG_HAS_BEFORE_MARSHAL_METHOD = 32;
  private static final short FLAG_HAS_AFTER_MARSHAL_METHOD = 64;
  private static final short FLAG_HAS_LIFECYCLE_EVENTS = 128;
  private LifecycleMethods lcm = null;
  public final Class<BeanT> jaxbType;
  private final Object typeName;
  private static final Class[] unmarshalEventParams = { Unmarshaller.class, Object.class };
  private static Class[] marshalEventParams = { Marshaller.class };
  private static final Logger logger = Util.getClassLogger();
  
  protected JaxBeanInfo(JAXBContextImpl paramJAXBContextImpl, RuntimeTypeInfo paramRuntimeTypeInfo, Class<BeanT> paramClass, QName[] paramArrayOfQName, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramJAXBContextImpl, paramRuntimeTypeInfo, paramClass, paramArrayOfQName, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  protected JaxBeanInfo(JAXBContextImpl paramJAXBContextImpl, RuntimeTypeInfo paramRuntimeTypeInfo, Class<BeanT> paramClass, QName paramQName, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramJAXBContextImpl, paramRuntimeTypeInfo, paramClass, paramQName, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  protected JaxBeanInfo(JAXBContextImpl paramJAXBContextImpl, RuntimeTypeInfo paramRuntimeTypeInfo, Class<BeanT> paramClass, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramJAXBContextImpl, paramRuntimeTypeInfo, paramClass, null, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  private JaxBeanInfo(JAXBContextImpl paramJAXBContextImpl, RuntimeTypeInfo paramRuntimeTypeInfo, Class<BeanT> paramClass, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    paramJAXBContextImpl.beanInfos.put(paramRuntimeTypeInfo, this);
    this.jaxbType = paramClass;
    this.typeName = paramObject;
    this.flag = ((short)((paramBoolean1 ? 1 : 0) | (paramBoolean2 ? 2 : 0) | (paramBoolean3 ? 128 : 0)));
  }
  
  public final boolean hasBeforeUnmarshalMethod()
  {
    return (this.flag & 0x8) != 0;
  }
  
  public final boolean hasAfterUnmarshalMethod()
  {
    return (this.flag & 0x10) != 0;
  }
  
  public final boolean hasBeforeMarshalMethod()
  {
    return (this.flag & 0x20) != 0;
  }
  
  public final boolean hasAfterMarshalMethod()
  {
    return (this.flag & 0x40) != 0;
  }
  
  public final boolean isElement()
  {
    return (this.flag & 0x1) != 0;
  }
  
  public final boolean isImmutable()
  {
    return (this.flag & 0x2) != 0;
  }
  
  public final boolean hasElementOnlyContentModel()
  {
    return (this.flag & 0x4) != 0;
  }
  
  protected final void hasElementOnlyContentModel(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.flag = ((short)(this.flag | 0x4));
    } else {
      this.flag = ((short)(this.flag & 0xFFFFFFFB));
    }
  }
  
  public boolean isNilIncluded()
  {
    return this.isNilIncluded;
  }
  
  public boolean lookForLifecycleMethods()
  {
    return (this.flag & 0x80) != 0;
  }
  
  public abstract String getElementNamespaceURI(BeanT paramBeanT);
  
  public abstract String getElementLocalName(BeanT paramBeanT);
  
  public Collection<QName> getTypeNames()
  {
    if (this.typeName == null) {
      return Collections.emptyList();
    }
    if ((this.typeName instanceof QName)) {
      return Collections.singletonList((QName)this.typeName);
    }
    return Arrays.asList((QName[])this.typeName);
  }
  
  public QName getTypeName(@NotNull BeanT paramBeanT)
  {
    if (this.typeName == null) {
      return null;
    }
    if ((this.typeName instanceof QName)) {
      return (QName)this.typeName;
    }
    return ((QName[])(QName[])this.typeName)[0];
  }
  
  public abstract BeanT createInstance(UnmarshallingContext paramUnmarshallingContext)
    throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException;
  
  public abstract boolean reset(BeanT paramBeanT, UnmarshallingContext paramUnmarshallingContext)
    throws SAXException;
  
  public abstract String getId(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException;
  
  public abstract void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException;
  
  public abstract void serializeAttributes(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException;
  
  public abstract void serializeRoot(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException;
  
  public abstract void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException;
  
  public abstract Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean);
  
  public abstract Transducer<BeanT> getTransducer();
  
  protected void link(JAXBContextImpl paramJAXBContextImpl) {}
  
  public void wrapUp() {}
  
  protected final void setLifecycleFlags()
  {
    try
    {
      Class localClass = this.jaxbType;
      if (this.lcm == null) {
        this.lcm = new LifecycleMethods();
      }
      while (localClass != null)
      {
        for (Method localMethod : localClass.getDeclaredMethods())
        {
          String str = localMethod.getName();
          if ((this.lcm.beforeUnmarshal == null) && (str.equals("beforeUnmarshal")) && (match(localMethod, unmarshalEventParams))) {
            cacheLifecycleMethod(localMethod, (short)8);
          }
          if ((this.lcm.afterUnmarshal == null) && (str.equals("afterUnmarshal")) && (match(localMethod, unmarshalEventParams))) {
            cacheLifecycleMethod(localMethod, (short)16);
          }
          if ((this.lcm.beforeMarshal == null) && (str.equals("beforeMarshal")) && (match(localMethod, marshalEventParams))) {
            cacheLifecycleMethod(localMethod, (short)32);
          }
          if ((this.lcm.afterMarshal == null) && (str.equals("afterMarshal")) && (match(localMethod, marshalEventParams))) {
            cacheLifecycleMethod(localMethod, (short)64);
          }
        }
        localClass = localClass.getSuperclass();
      }
    }
    catch (SecurityException localSecurityException)
    {
      logger.log(Level.WARNING, Messages.UNABLE_TO_DISCOVER_EVENTHANDLER.format(new Object[] { this.jaxbType.getName(), localSecurityException }));
    }
  }
  
  private boolean match(Method paramMethod, Class[] paramArrayOfClass)
  {
    return Arrays.equals(paramMethod.getParameterTypes(), paramArrayOfClass);
  }
  
  private void cacheLifecycleMethod(Method paramMethod, short paramShort)
  {
    if (this.lcm == null) {
      this.lcm = new LifecycleMethods();
    }
    paramMethod.setAccessible(true);
    this.flag = ((short)(this.flag | paramShort));
    switch (paramShort)
    {
    case 8: 
      this.lcm.beforeUnmarshal = paramMethod;
      break;
    case 16: 
      this.lcm.afterUnmarshal = paramMethod;
      break;
    case 32: 
      this.lcm.beforeMarshal = paramMethod;
      break;
    case 64: 
      this.lcm.afterMarshal = paramMethod;
    }
  }
  
  public final LifecycleMethods getLifecycleMethods()
  {
    return this.lcm;
  }
  
  public final void invokeBeforeUnmarshalMethod(UnmarshallerImpl paramUnmarshallerImpl, Object paramObject1, Object paramObject2)
    throws SAXException
  {
    Method localMethod = getLifecycleMethods().beforeUnmarshal;
    invokeUnmarshallCallback(localMethod, paramObject1, paramUnmarshallerImpl, paramObject2);
  }
  
  public final void invokeAfterUnmarshalMethod(UnmarshallerImpl paramUnmarshallerImpl, Object paramObject1, Object paramObject2)
    throws SAXException
  {
    Method localMethod = getLifecycleMethods().afterUnmarshal;
    invokeUnmarshallCallback(localMethod, paramObject1, paramUnmarshallerImpl, paramObject2);
  }
  
  private void invokeUnmarshallCallback(Method paramMethod, Object paramObject1, UnmarshallerImpl paramUnmarshallerImpl, Object paramObject2)
    throws SAXException
  {
    try
    {
      paramMethod.invoke(paramObject1, new Object[] { paramUnmarshallerImpl, paramObject2 });
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      UnmarshallingContext.getInstance().handleError(localIllegalAccessException, false);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      UnmarshallingContext.getInstance().handleError(localInvocationTargetException, false);
    }
  }
}
