package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class RepeatedElementBridge<T>
  implements XMLBridge<T>
{
  XMLBridge<T> delegate;
  CollectionHandler collectionHandler;
  static final CollectionHandler ListHandler = new BaseCollectionHandler(List.class)
  {
    public Object convert(List paramAnonymousList)
    {
      return paramAnonymousList;
    }
  };
  static final CollectionHandler HashSetHandler = new BaseCollectionHandler(HashSet.class)
  {
    public Object convert(List paramAnonymousList)
    {
      return new HashSet(paramAnonymousList);
    }
  };
  
  public RepeatedElementBridge(TypeInfo paramTypeInfo, XMLBridge paramXMLBridge)
  {
    this.delegate = paramXMLBridge;
    this.collectionHandler = create(paramTypeInfo);
  }
  
  public CollectionHandler collectionHandler()
  {
    return this.collectionHandler;
  }
  
  public BindingContext context()
  {
    return this.delegate.context();
  }
  
  public void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    this.delegate.marshal(paramT, paramXMLStreamWriter, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    this.delegate.marshal(paramT, paramOutputStream, paramNamespaceContext, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    this.delegate.marshal(paramT, paramNode);
  }
  
  public void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    this.delegate.marshal(paramT, paramContentHandler, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    this.delegate.marshal(paramT, paramResult);
  }
  
  public T unmarshal(XMLStreamReader paramXMLStreamReader, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return this.delegate.unmarshal(paramXMLStreamReader, paramAttachmentUnmarshaller);
  }
  
  public T unmarshal(Source paramSource, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return this.delegate.unmarshal(paramSource, paramAttachmentUnmarshaller);
  }
  
  public T unmarshal(InputStream paramInputStream)
    throws JAXBException
  {
    return this.delegate.unmarshal(paramInputStream);
  }
  
  public T unmarshal(Node paramNode, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return this.delegate.unmarshal(paramNode, paramAttachmentUnmarshaller);
  }
  
  public TypeInfo getTypeInfo()
  {
    return this.delegate.getTypeInfo();
  }
  
  public boolean supportOutputStream()
  {
    return this.delegate.supportOutputStream();
  }
  
  public static CollectionHandler create(TypeInfo paramTypeInfo)
  {
    Class localClass = (Class)paramTypeInfo.type;
    if (localClass.isArray()) {
      return new ArrayHandler((Class)paramTypeInfo.getItemType().type);
    }
    if ((List.class.equals(localClass)) || (Collection.class.equals(localClass))) {
      return ListHandler;
    }
    if ((Set.class.equals(localClass)) || (HashSet.class.equals(localClass))) {
      return HashSetHandler;
    }
    return new BaseCollectionHandler(localClass);
  }
  
  static class ArrayHandler
    implements RepeatedElementBridge.CollectionHandler
  {
    Class componentClass;
    
    public ArrayHandler(Class paramClass)
    {
      this.componentClass = paramClass;
    }
    
    public int getSize(Object paramObject)
    {
      return Array.getLength(paramObject);
    }
    
    public Object convert(List paramList)
    {
      Object localObject = Array.newInstance(this.componentClass, paramList.size());
      for (int i = 0; i < paramList.size(); i++) {
        Array.set(localObject, i, paramList.get(i));
      }
      return localObject;
    }
    
    public Iterator iterator(final Object paramObject)
    {
      new Iterator()
      {
        int index = 0;
        
        public boolean hasNext()
        {
          if ((paramObject == null) || (Array.getLength(paramObject) == 0)) {
            return false;
          }
          return this.index != Array.getLength(paramObject);
        }
        
        public Object next()
          throws NoSuchElementException
        {
          Object localObject = null;
          try
          {
            localObject = Array.get(paramObject, this.index++);
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
          {
            throw new NoSuchElementException();
          }
          return localObject;
        }
        
        public void remove() {}
      };
    }
  }
  
  static class BaseCollectionHandler
    implements RepeatedElementBridge.CollectionHandler
  {
    Class type;
    
    BaseCollectionHandler(Class paramClass)
    {
      this.type = paramClass;
    }
    
    public int getSize(Object paramObject)
    {
      return ((Collection)paramObject).size();
    }
    
    public Object convert(List paramList)
    {
      try
      {
        Object localObject = this.type.newInstance();
        ((Collection)localObject).addAll(paramList);
        return localObject;
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      return paramList;
    }
    
    public Iterator iterator(Object paramObject)
    {
      return ((Collection)paramObject).iterator();
    }
  }
  
  public static abstract interface CollectionHandler
  {
    public abstract int getSize(Object paramObject);
    
    public abstract Iterator iterator(Object paramObject);
    
    public abstract Object convert(List paramList);
  }
}
