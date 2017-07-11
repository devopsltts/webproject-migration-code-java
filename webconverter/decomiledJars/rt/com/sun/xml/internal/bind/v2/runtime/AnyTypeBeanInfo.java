package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DomLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

final class AnyTypeBeanInfo
  extends JaxBeanInfo<Object>
  implements AttributeAccessor
{
  private boolean nilIncluded = false;
  private static final W3CDomHandler domHandler = new W3CDomHandler();
  private static final DomLoader domLoader = new DomLoader(domHandler);
  private final XsiTypeLoader substLoader = new XsiTypeLoader(this);
  
  public AnyTypeBeanInfo(JAXBContextImpl paramJAXBContextImpl, RuntimeTypeInfo paramRuntimeTypeInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeTypeInfo, Object.class, new QName("http://www.w3.org/2001/XMLSchema", "anyType"), false, true, false);
  }
  
  public String getElementNamespaceURI(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public String getElementLocalName(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object createInstance(UnmarshallingContext paramUnmarshallingContext)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean reset(Object paramObject, UnmarshallingContext paramUnmarshallingContext)
  {
    return false;
  }
  
  public String getId(Object paramObject, XMLSerializer paramXMLSerializer)
  {
    return null;
  }
  
  public void serializeBody(Object paramObject, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    NodeList localNodeList = ((Element)paramObject).getChildNodes();
    int i = localNodeList.getLength();
    for (int j = 0; j < i; j++)
    {
      Node localNode = localNodeList.item(j);
      switch (localNode.getNodeType())
      {
      case 3: 
      case 4: 
        paramXMLSerializer.text(localNode.getNodeValue(), null);
        break;
      case 1: 
        paramXMLSerializer.writeDom((Element)localNode, domHandler, null, null);
      }
    }
  }
  
  public void serializeAttributes(Object paramObject, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    NamedNodeMap localNamedNodeMap = ((Element)paramObject).getAttributes();
    int i = localNamedNodeMap.getLength();
    for (int j = 0; j < i; j++)
    {
      Attr localAttr = (Attr)localNamedNodeMap.item(j);
      String str1 = localAttr.getNamespaceURI();
      if (str1 == null) {
        str1 = "";
      }
      Object localObject = localAttr.getLocalName();
      String str2 = localAttr.getName();
      if (localObject == null) {
        localObject = str2;
      }
      if ((str1.equals("http://www.w3.org/2001/XMLSchema-instance")) && ("nil".equals(localObject))) {
        this.isNilIncluded = true;
      }
      if (!str2.startsWith("xmlns")) {
        paramXMLSerializer.attribute(str1, (String)localObject, localAttr.getValue());
      }
    }
  }
  
  public void serializeRoot(Object paramObject, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramObject.getClass().getName() }), null, null));
  }
  
  public void serializeURIs(Object paramObject, XMLSerializer paramXMLSerializer)
  {
    NamedNodeMap localNamedNodeMap = ((Element)paramObject).getAttributes();
    int i = localNamedNodeMap.getLength();
    NamespaceContext2 localNamespaceContext2 = paramXMLSerializer.getNamespaceContext();
    for (int j = 0; j < i; j++)
    {
      Attr localAttr = (Attr)localNamedNodeMap.item(j);
      if ("xmlns".equals(localAttr.getPrefix()))
      {
        localNamespaceContext2.force(localAttr.getValue(), localAttr.getLocalName());
      }
      else if ("xmlns".equals(localAttr.getName()))
      {
        if ((paramObject instanceof Element)) {
          localNamespaceContext2.declareNamespace(localAttr.getValue(), null, false);
        } else {
          localNamespaceContext2.force(localAttr.getValue(), "");
        }
      }
      else
      {
        String str = localAttr.getNamespaceURI();
        if ((str != null) && (str.length() > 0)) {
          localNamespaceContext2.declareNamespace(str, localAttr.getPrefix(), true);
        }
      }
    }
  }
  
  public Transducer<Object> getTransducer()
  {
    return null;
  }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.substLoader;
    }
    return domLoader;
  }
}
