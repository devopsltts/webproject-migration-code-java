package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

class ArrayReferenceNodeProperty<BeanT, ListT, ItemT>
  extends ArrayERProperty<BeanT, ListT, ItemT>
{
  private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
  private final boolean isMixed;
  private final DomHandler domHandler;
  private final WildcardMode wcMode;
  
  public ArrayReferenceNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeReferencePropertyInfo paramRuntimeReferencePropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeReferencePropertyInfo, paramRuntimeReferencePropertyInfo.getXmlName(), paramRuntimeReferencePropertyInfo.isCollectionNillable());
    Iterator localIterator = paramRuntimeReferencePropertyInfo.getElements().iterator();
    while (localIterator.hasNext())
    {
      RuntimeElement localRuntimeElement = (RuntimeElement)localIterator.next();
      JaxBeanInfo localJaxBeanInfo = paramJAXBContextImpl.getOrCreate(localRuntimeElement);
      this.expectedElements.put(localRuntimeElement.getElementName().getNamespaceURI(), localRuntimeElement.getElementName().getLocalPart(), localJaxBeanInfo);
    }
    this.isMixed = paramRuntimeReferencePropertyInfo.isMixed();
    if (paramRuntimeReferencePropertyInfo.getWildcard() != null)
    {
      this.domHandler = ((DomHandler)ClassFactory.create((Class)paramRuntimeReferencePropertyInfo.getDOMHandler()));
      this.wcMode = paramRuntimeReferencePropertyInfo.getWildcard();
    }
    else
    {
      this.domHandler = null;
      this.wcMode = null;
    }
  }
  
  protected final void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT)
    throws IOException, XMLStreamException, SAXException
  {
    ListIterator localListIterator = this.lister.iterator(paramListT, paramXMLSerializer);
    while (localListIterator.hasNext()) {
      try
      {
        Object localObject = localListIterator.next();
        if (localObject != null) {
          if ((this.isMixed) && (localObject.getClass() == String.class))
          {
            paramXMLSerializer.text((String)localObject, null);
          }
          else
          {
            JaxBeanInfo localJaxBeanInfo = paramXMLSerializer.grammar.getBeanInfo(localObject, true);
            if ((localJaxBeanInfo.jaxbType == Object.class) && (this.domHandler != null)) {
              paramXMLSerializer.writeDom(localObject, this.domHandler, paramBeanT, this.fieldName);
            } else {
              localJaxBeanInfo.serializeRoot(localObject, paramXMLSerializer);
            }
          }
        }
      }
      catch (JAXBException localJAXBException)
      {
        paramXMLSerializer.reportError(this.fieldName, localJAXBException);
      }
    }
  }
  
  public void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    int i = paramUnmarshallerChain.allocateOffset();
    ArrayERProperty.ReceiverImpl localReceiverImpl = new ArrayERProperty.ReceiverImpl(this, i);
    Iterator localIterator = this.expectedElements.entrySet().iterator();
    while (localIterator.hasNext())
    {
      QNameMap.Entry localEntry = (QNameMap.Entry)localIterator.next();
      JaxBeanInfo localJaxBeanInfo = (JaxBeanInfo)localEntry.getValue();
      paramQNameMap.put(localEntry.nsUri, localEntry.localName, new ChildLoader(localJaxBeanInfo.getLoader(paramUnmarshallerChain.context, true), localReceiverImpl));
    }
    if (this.isMixed) {
      paramQNameMap.put(TEXT_HANDLER, new ChildLoader(new MixedTextLoader(localReceiverImpl), null));
    }
    if (this.domHandler != null) {
      paramQNameMap.put(CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), localReceiverImpl));
    }
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.REFERENCE;
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if (this.wrapperTagName != null)
    {
      if (this.wrapperTagName.equals(paramString1, paramString2)) {
        return this.acc;
      }
    }
    else if (this.expectedElements.containsKey(paramString1, paramString2)) {
      return this.acc;
    }
    return null;
  }
  
  private static final class MixedTextLoader
    extends Loader
  {
    private final Receiver recv;
    
    public MixedTextLoader(Receiver paramReceiver)
    {
      super();
      this.recv = paramReceiver;
    }
    
    public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
      throws SAXException
    {
      if (paramCharSequence.length() != 0) {
        this.recv.receive(paramState, paramCharSequence.toString());
      }
    }
  }
}
