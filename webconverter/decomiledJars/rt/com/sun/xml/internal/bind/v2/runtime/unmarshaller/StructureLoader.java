package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.StructureLoaderBuilder;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class StructureLoader
  extends Loader
{
  private final QNameMap<ChildLoader> childUnmarshallers = new QNameMap();
  private ChildLoader catchAll;
  private ChildLoader textHandler;
  private QNameMap<TransducedAccessor> attUnmarshallers;
  private Accessor<Object, Map<QName, String>> attCatchAll;
  private final JaxBeanInfo beanInfo;
  private int frameSize;
  private static final QNameMap<TransducedAccessor> EMPTY = new QNameMap();
  
  public StructureLoader(ClassBeanInfoImpl paramClassBeanInfoImpl)
  {
    super(true);
    this.beanInfo = paramClassBeanInfoImpl;
  }
  
  public void init(JAXBContextImpl paramJAXBContextImpl, ClassBeanInfoImpl paramClassBeanInfoImpl, Accessor<?, Map<QName, String>> paramAccessor)
  {
    UnmarshallerChain localUnmarshallerChain = new UnmarshallerChain(paramJAXBContextImpl);
    for (ClassBeanInfoImpl localClassBeanInfoImpl = paramClassBeanInfoImpl; localClassBeanInfoImpl != null; localClassBeanInfoImpl = localClassBeanInfoImpl.superClazz) {
      for (int i = localClassBeanInfoImpl.properties.length - 1; i >= 0; i--)
      {
        Property localProperty = localClassBeanInfoImpl.properties[i];
        switch (1.$SwitchMap$com$sun$xml$internal$bind$v2$model$core$PropertyKind[localProperty.getKind().ordinal()])
        {
        case 1: 
          if (this.attUnmarshallers == null) {
            this.attUnmarshallers = new QNameMap();
          }
          AttributeProperty localAttributeProperty = (AttributeProperty)localProperty;
          this.attUnmarshallers.put(localAttributeProperty.attName.toQName(), localAttributeProperty.xacc);
          break;
        case 2: 
        case 3: 
        case 4: 
        case 5: 
          localProperty.buildChildElementUnmarshallers(localUnmarshallerChain, this.childUnmarshallers);
        }
      }
    }
    this.frameSize = localUnmarshallerChain.getScopeSize();
    this.textHandler = ((ChildLoader)this.childUnmarshallers.get(StructureLoaderBuilder.TEXT_HANDLER));
    this.catchAll = ((ChildLoader)this.childUnmarshallers.get(StructureLoaderBuilder.CATCH_ALL));
    if (paramAccessor != null)
    {
      this.attCatchAll = paramAccessor;
      if (this.attUnmarshallers == null) {
        this.attUnmarshallers = EMPTY;
      }
    }
    else
    {
      this.attCatchAll = null;
    }
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    assert (!this.beanInfo.isImmutable());
    Object localObject1 = localUnmarshallingContext.getInnerPeer();
    if ((localObject1 != null) && (this.beanInfo.jaxbType != localObject1.getClass())) {
      localObject1 = null;
    }
    if (localObject1 != null) {
      this.beanInfo.reset(localObject1, localUnmarshallingContext);
    }
    if (localObject1 == null) {
      localObject1 = localUnmarshallingContext.createInstance(this.beanInfo);
    }
    localUnmarshallingContext.recordInnerPeer(localObject1);
    paramState.setTarget(localObject1);
    fireBeforeUnmarshal(this.beanInfo, localObject1, paramState);
    localUnmarshallingContext.startScope(this.frameSize);
    if (this.attUnmarshallers != null)
    {
      Attributes localAttributes = paramTagName.atts;
      for (int i = 0; i < localAttributes.getLength(); i++)
      {
        String str1 = localAttributes.getURI(i);
        String str2 = localAttributes.getLocalName(i);
        if ("".equals(str2)) {
          str2 = localAttributes.getQName(i);
        }
        String str3 = localAttributes.getValue(i);
        TransducedAccessor localTransducedAccessor = (TransducedAccessor)this.attUnmarshallers.get(str1, str2);
        try
        {
          if (localTransducedAccessor != null)
          {
            localTransducedAccessor.parse(localObject1, str3);
          }
          else if (this.attCatchAll != null)
          {
            String str4 = localAttributes.getQName(i);
            if (localAttributes.getURI(i).equals("http://www.w3.org/2001/XMLSchema-instance")) {
              continue;
            }
            Object localObject2 = paramState.getTarget();
            Object localObject3 = (Map)this.attCatchAll.get(localObject2);
            if (localObject3 == null)
            {
              if (this.attCatchAll.valueType.isAssignableFrom(HashMap.class))
              {
                localObject3 = new HashMap();
              }
              else
              {
                localUnmarshallingContext.handleError(Messages.UNABLE_TO_CREATE_MAP.format(new Object[] { this.attCatchAll.valueType }));
                return;
              }
              this.attCatchAll.set(localObject2, localObject3);
            }
            int j = str4.indexOf(':');
            String str5;
            if (j < 0) {
              str5 = "";
            } else {
              str5 = str4.substring(0, j);
            }
            ((Map)localObject3).put(new QName(str1, str2, str5), str3);
          }
        }
        catch (AccessorException localAccessorException)
        {
          handleGenericException(localAccessorException, true);
        }
      }
    }
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    ChildLoader localChildLoader = (ChildLoader)this.childUnmarshallers.get(paramTagName.uri, paramTagName.local);
    if (localChildLoader == null)
    {
      if ((this.beanInfo != null) && (this.beanInfo.getTypeNames() != null))
      {
        Iterator localIterator = this.beanInfo.getTypeNames().iterator();
        QName localQName = null;
        if ((localIterator != null) && (localIterator.hasNext()) && (this.catchAll == null))
        {
          localQName = (QName)localIterator.next();
          String str = localQName.getNamespaceURI();
          localChildLoader = (ChildLoader)this.childUnmarshallers.get(str, paramTagName.local);
        }
      }
      if (localChildLoader == null)
      {
        localChildLoader = this.catchAll;
        if (localChildLoader == null)
        {
          super.childElement(paramState, paramTagName);
          return;
        }
      }
    }
    paramState.setLoader(localChildLoader.loader);
    paramState.setReceiver(localChildLoader.receiver);
  }
  
  public Collection<QName> getExpectedChildElements()
  {
    return this.childUnmarshallers.keySet();
  }
  
  public Collection<QName> getExpectedAttributes()
  {
    return this.attUnmarshallers.keySet();
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    if (this.textHandler != null) {
      this.textHandler.loader.text(paramState, paramCharSequence);
    }
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    paramState.getContext().endScope(this.frameSize);
    fireAfterUnmarshal(this.beanInfo, paramState.getTarget(), paramState.getPrev());
  }
  
  public JaxBeanInfo getBeanInfo()
  {
    return this.beanInfo;
  }
}
