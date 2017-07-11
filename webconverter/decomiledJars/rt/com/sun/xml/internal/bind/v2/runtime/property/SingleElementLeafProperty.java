package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyXsiLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementLeafProperty<BeanT>
  extends PropertyImpl<BeanT>
{
  private final Name tagName;
  private final boolean nillable;
  private final Accessor acc;
  private final String defaultValue;
  private final TransducedAccessor<BeanT> xacc;
  private final boolean improvedXsiTypeHandling;
  private final boolean idRef;
  
  public SingleElementLeafProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    RuntimeTypeRef localRuntimeTypeRef = (RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0);
    this.tagName = paramJAXBContextImpl.nameBuilder.createElementName(localRuntimeTypeRef.getTagName());
    assert (this.tagName != null);
    this.nillable = localRuntimeTypeRef.isNillable();
    this.defaultValue = localRuntimeTypeRef.getDefaultValue();
    this.acc = paramRuntimeElementPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    this.xacc = TransducedAccessor.get(paramJAXBContextImpl, localRuntimeTypeRef);
    assert (this.xacc != null);
    this.improvedXsiTypeHandling = paramJAXBContextImpl.improvedXsiTypeHandling;
    this.idRef = (localRuntimeTypeRef.getSource().id() == ID.IDREF);
  }
  
  public void reset(BeanT paramBeanT)
    throws AccessorException
  {
    this.acc.set(paramBeanT, null);
  }
  
  public String getIdValue(BeanT paramBeanT)
    throws AccessorException, SAXException
  {
    return this.xacc.print(paramBeanT).toString();
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    boolean bool = this.xacc.hasValue(paramBeanT);
    Object localObject = null;
    try
    {
      localObject = this.acc.getUnadapted(paramBeanT);
    }
    catch (AccessorException localAccessorException) {}
    Class localClass = this.acc.getValueType();
    if (xsiTypeNeeded(paramBeanT, paramXMLSerializer, localObject, localClass))
    {
      paramXMLSerializer.startElement(this.tagName, paramObject);
      paramXMLSerializer.childAsXsiType(localObject, this.fieldName, paramXMLSerializer.grammar.getBeanInfo(localClass), false);
      paramXMLSerializer.endElement();
    }
    else if (bool)
    {
      this.xacc.writeLeafElement(paramXMLSerializer, this.tagName, paramBeanT, this.fieldName);
    }
    else if (this.nillable)
    {
      paramXMLSerializer.startElement(this.tagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    }
  }
  
  private boolean xsiTypeNeeded(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject, Class paramClass)
  {
    if (!this.improvedXsiTypeHandling) {
      return false;
    }
    if (this.acc.isAdapted()) {
      return false;
    }
    if (paramObject == null) {
      return false;
    }
    if (paramObject.getClass().equals(paramClass)) {
      return false;
    }
    if (this.idRef) {
      return false;
    }
    if (paramClass.isPrimitive()) {
      return false;
    }
    return (this.acc.isValueTypeAbstractable()) || (isNillableAbstract(paramBeanT, paramXMLSerializer.grammar, paramObject, paramClass));
  }
  
  private boolean isNillableAbstract(BeanT paramBeanT, JAXBContextImpl paramJAXBContextImpl, Object paramObject, Class paramClass)
  {
    if (!this.nillable) {
      return false;
    }
    if (paramClass != Object.class) {
      return false;
    }
    if (paramBeanT.getClass() != JAXBElement.class) {
      return false;
    }
    JAXBElement localJAXBElement = (JAXBElement)paramBeanT;
    Class localClass1 = paramObject.getClass();
    Class localClass2 = localJAXBElement.getDeclaredType();
    if (localClass2.equals(localClass1)) {
      return false;
    }
    if (!localClass2.isAssignableFrom(localClass1)) {
      return false;
    }
    if (!Modifier.isAbstract(localClass2.getModifiers())) {
      return false;
    }
    return this.acc.isAbstractable(localClass2);
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    Object localObject = new LeafPropertyLoader(this.xacc);
    if (this.defaultValue != null) {
      localObject = new DefaultValueLoaderDecorator((Loader)localObject, this.defaultValue);
    }
    if ((this.nillable) || (paramUnmarshallerChain.context.allNillable)) {
      localObject = new XsiNilLoader.Single((Loader)localObject, this.acc);
    }
    if (this.improvedXsiTypeHandling) {
      localObject = new LeafPropertyXsiLoader((Loader)localObject, this.xacc, this.acc);
    }
    paramQNameMap.put(this.tagName, new ChildLoader((Loader)localObject, null));
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.ELEMENT;
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if (this.tagName.equals(paramString1, paramString2)) {
      return this.acc;
    }
    return null;
  }
}
