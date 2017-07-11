package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.IOException;
import java.io.Writer;
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
  extends DummyEvent
  implements StartElement
{
  private Map fAttributes;
  private List fNamespaces;
  private NamespaceContext fNamespaceContext = null;
  private QName fQName;
  
  public StartElementEvent(String paramString1, String paramString2, String paramString3)
  {
    this(new QName(paramString2, paramString3, paramString1));
  }
  
  public StartElementEvent(QName paramQName)
  {
    this.fQName = paramQName;
    init();
  }
  
  public StartElementEvent(StartElement paramStartElement)
  {
    this(paramStartElement.getName());
    addAttributes(paramStartElement.getAttributes());
    addNamespaceAttributes(paramStartElement.getNamespaces());
  }
  
  protected void init()
  {
    setEventType(1);
    this.fAttributes = new HashMap();
    this.fNamespaces = new ArrayList();
  }
  
  public QName getName()
  {
    return this.fQName;
  }
  
  public void setName(QName paramQName)
  {
    this.fQName = paramQName;
  }
  
  public Iterator getAttributes()
  {
    if (this.fAttributes != null)
    {
      Collection localCollection = this.fAttributes.values();
      return new ReadOnlyIterator(localCollection.iterator());
    }
    return new ReadOnlyIterator();
  }
  
  public Iterator getNamespaces()
  {
    if (this.fNamespaces != null) {
      return new ReadOnlyIterator(this.fNamespaces.iterator());
    }
    return new ReadOnlyIterator();
  }
  
  public Attribute getAttributeByName(QName paramQName)
  {
    if (paramQName == null) {
      return null;
    }
    return (Attribute)this.fAttributes.get(paramQName);
  }
  
  public String getNamespace()
  {
    return this.fQName.getNamespaceURI();
  }
  
  public String getNamespaceURI(String paramString)
  {
    if ((getNamespace() != null) && (this.fQName.getPrefix().equals(paramString))) {
      return getNamespace();
    }
    if (this.fNamespaceContext != null) {
      return this.fNamespaceContext.getNamespaceURI(paramString);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("<");
    localStringBuffer.append(nameAsString());
    Iterator localIterator;
    Object localObject;
    if (this.fAttributes != null)
    {
      localIterator = getAttributes();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Attribute)localIterator.next();
        localStringBuffer.append(" ");
        localStringBuffer.append(localObject.toString());
      }
    }
    if (this.fNamespaces != null)
    {
      localIterator = this.fNamespaces.iterator();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Namespace)localIterator.next();
        localStringBuffer.append(" ");
        localStringBuffer.append(localObject.toString());
      }
    }
    localStringBuffer.append(">");
    return localStringBuffer.toString();
  }
  
  public String nameAsString()
  {
    if ("".equals(this.fQName.getNamespaceURI())) {
      return this.fQName.getLocalPart();
    }
    if (this.fQName.getPrefix() != null) {
      return "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getPrefix() + ":" + this.fQName.getLocalPart();
    }
    return "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getLocalPart();
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return this.fNamespaceContext;
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
  {
    this.fNamespaceContext = paramNamespaceContext;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(toString());
  }
  
  void addAttribute(Attribute paramAttribute)
  {
    if (paramAttribute.isNamespace()) {
      this.fNamespaces.add(paramAttribute);
    } else {
      this.fAttributes.put(paramAttribute.getName(), paramAttribute);
    }
  }
  
  void addAttributes(Iterator paramIterator)
  {
    if (paramIterator == null) {
      return;
    }
    while (paramIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)paramIterator.next();
      this.fAttributes.put(localAttribute.getName(), localAttribute);
    }
  }
  
  void addNamespaceAttribute(Namespace paramNamespace)
  {
    if (paramNamespace == null) {
      return;
    }
    this.fNamespaces.add(paramNamespace);
  }
  
  void addNamespaceAttributes(Iterator paramIterator)
  {
    if (paramIterator == null) {
      return;
    }
    while (paramIterator.hasNext())
    {
      Namespace localNamespace = (Namespace)paramIterator.next();
      this.fNamespaces.add(localNamespace);
    }
  }
}
