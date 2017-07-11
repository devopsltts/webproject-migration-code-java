package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class AttributeProperty<BeanT>
  extends PropertyImpl<BeanT>
  implements Comparable<AttributeProperty>
{
  public final Name attName;
  public final TransducedAccessor<BeanT> xacc;
  private final Accessor acc;
  
  public AttributeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeAttributePropertyInfo paramRuntimeAttributePropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeAttributePropertyInfo);
    this.attName = paramJAXBContextImpl.nameBuilder.createAttributeName(paramRuntimeAttributePropertyInfo.getXmlName());
    this.xacc = TransducedAccessor.get(paramJAXBContextImpl, paramRuntimeAttributePropertyInfo);
    this.acc = paramRuntimeAttributePropertyInfo.getAccessor();
  }
  
  public void serializeAttributes(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    CharSequence localCharSequence = this.xacc.print(paramBeanT);
    if (localCharSequence != null) {
      paramXMLSerializer.attribute(this.attName, localCharSequence.toString());
    }
  }
  
  public void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws AccessorException, SAXException
  {
    this.xacc.declareNamespace(paramBeanT, paramXMLSerializer);
  }
  
  public boolean hasSerializeURIAction()
  {
    return this.xacc.useNamespace();
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    throw new IllegalStateException();
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.ATTRIBUTE;
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
  
  public int compareTo(AttributeProperty paramAttributeProperty)
  {
    return this.attName.compareTo(paramAttributeProperty.attName);
  }
}
