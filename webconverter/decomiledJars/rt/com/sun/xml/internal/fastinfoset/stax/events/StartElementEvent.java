package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class StartElementEvent
  extends EventBase
  implements StartElement
{
  private Map _attributes;
  private List _namespaces;
  private NamespaceContext _context = null;
  private QName _qname;
  
  public void reset()
  {
    if (this._attributes != null) {
      this._attributes.clear();
    }
    if (this._namespaces != null) {
      this._namespaces.clear();
    }
    if (this._context != null) {
      this._context = null;
    }
  }
  
  public StartElementEvent()
  {
    init();
  }
  
  public StartElementEvent(String paramString1, String paramString2, String paramString3)
  {
    init();
    if (paramString2 == null) {
      paramString2 = "";
    }
    if (paramString1 == null) {
      paramString1 = "";
    }
    this._qname = new QName(paramString2, paramString3, paramString1);
    setEventType(1);
  }
  
  public StartElementEvent(QName paramQName)
  {
    init();
    this._qname = paramQName;
  }
  
  public StartElementEvent(StartElement paramStartElement)
  {
    this(paramStartElement.getName());
    addAttributes(paramStartElement.getAttributes());
    addNamespaces(paramStartElement.getNamespaces());
  }
  
  protected void init()
  {
    setEventType(1);
    this._attributes = new HashMap();
    this._namespaces = new ArrayList();
  }
  
  public QName getName()
  {
    return this._qname;
  }
  
  public Iterator getAttributes()
  {
    if (this._attributes != null)
    {
      Collection localCollection = this._attributes.values();
      return new ReadIterator(localCollection.iterator());
    }
    return EmptyIterator.getInstance();
  }
  
  public Iterator getNamespaces()
  {
    if (this._namespaces != null) {
      return new ReadIterator(this._namespaces.iterator());
    }
    return EmptyIterator.getInstance();
  }
  
  public Attribute getAttributeByName(QName paramQName)
  {
    if (paramQName == null) {
      return null;
    }
    return (Attribute)this._attributes.get(paramQName);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return this._context;
  }
  
  public void setName(QName paramQName)
  {
    this._qname = paramQName;
  }
  
  public String getNamespace()
  {
    return this._qname.getNamespaceURI();
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (getNamespace() != null) {
      return getNamespace();
    }
    if (this._context != null) {
      return this._context.getNamespaceURI(paramString);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    localStringBuilder.append('<').append(nameAsString());
    Iterator localIterator;
    Object localObject;
    if (this._attributes != null)
    {
      localIterator = getAttributes();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Attribute)localIterator.next();
        localStringBuilder.append(' ').append(localObject.toString());
      }
    }
    if (this._namespaces != null)
    {
      localIterator = this._namespaces.iterator();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Namespace)localIterator.next();
        localStringBuilder.append(' ').append(localObject.toString());
      }
    }
    localStringBuilder.append('>');
    return localStringBuilder.toString();
  }
  
  public String nameAsString()
  {
    if ("".equals(this._qname.getNamespaceURI())) {
      return this._qname.getLocalPart();
    }
    if (this._qname.getPrefix() != null) {
      return "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart();
    }
    return "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart();
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
  {
    this._context = paramNamespaceContext;
  }
  
  public void addAttribute(Attribute paramAttribute)
  {
    this._attributes.put(paramAttribute.getName(), paramAttribute);
  }
  
  public void addAttributes(Iterator paramIterator)
  {
    if (paramIterator != null) {
      while (paramIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)paramIterator.next();
        this._attributes.put(localAttribute.getName(), localAttribute);
      }
    }
  }
  
  public void addNamespace(Namespace paramNamespace)
  {
    if (paramNamespace != null) {
      this._namespaces.add(paramNamespace);
    }
  }
  
  public void addNamespaces(Iterator paramIterator)
  {
    if (paramIterator != null) {
      while (paramIterator.hasNext())
      {
        Namespace localNamespace = (Namespace)paramIterator.next();
        this._namespaces.add(localNamespace);
      }
    }
  }
}
