package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

class JAXBRIContextWrapper
  implements BindingContext
{
  private Map<TypeInfo, TypeReference> typeRefs;
  private Map<TypeReference, TypeInfo> typeInfos;
  private JAXBRIContext context;
  
  JAXBRIContextWrapper(JAXBRIContext paramJAXBRIContext, Map<TypeInfo, TypeReference> paramMap)
  {
    this.context = paramJAXBRIContext;
    this.typeRefs = paramMap;
    if (paramMap != null)
    {
      this.typeInfos = new HashMap();
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        TypeInfo localTypeInfo = (TypeInfo)localIterator.next();
        this.typeInfos.put(this.typeRefs.get(localTypeInfo), localTypeInfo);
      }
    }
  }
  
  TypeReference typeReference(TypeInfo paramTypeInfo)
  {
    return this.typeRefs != null ? (TypeReference)this.typeRefs.get(paramTypeInfo) : null;
  }
  
  TypeInfo typeInfo(TypeReference paramTypeReference)
  {
    return this.typeInfos != null ? (TypeInfo)this.typeInfos.get(paramTypeReference) : null;
  }
  
  public Marshaller createMarshaller()
    throws JAXBException
  {
    return this.context.createMarshaller();
  }
  
  public Unmarshaller createUnmarshaller()
    throws JAXBException
  {
    return this.context.createUnmarshaller();
  }
  
  public void generateSchema(SchemaOutputResolver paramSchemaOutputResolver)
    throws IOException
  {
    this.context.generateSchema(paramSchemaOutputResolver);
  }
  
  public String getBuildId()
  {
    return this.context.getBuildId();
  }
  
  public QName getElementName(Class paramClass)
    throws JAXBException
  {
    return this.context.getElementName(paramClass);
  }
  
  public QName getElementName(Object paramObject)
    throws JAXBException
  {
    return this.context.getElementName(paramObject);
  }
  
  public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> paramClass, String paramString1, String paramString2)
    throws JAXBException
  {
    return new RawAccessorWrapper(this.context.getElementPropertyAccessor(paramClass, paramString1, paramString2));
  }
  
  public List<String> getKnownNamespaceURIs()
  {
    return this.context.getKnownNamespaceURIs();
  }
  
  public RuntimeTypeInfoSet getRuntimeTypeInfoSet()
  {
    return this.context.getRuntimeTypeInfoSet();
  }
  
  public QName getTypeName(TypeReference paramTypeReference)
  {
    return this.context.getTypeName(paramTypeReference);
  }
  
  public int hashCode()
  {
    return this.context.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    JAXBRIContextWrapper localJAXBRIContextWrapper = (JAXBRIContextWrapper)paramObject;
    return (this.context == localJAXBRIContextWrapper.context) || ((this.context != null) && (this.context.equals(localJAXBRIContextWrapper.context)));
  }
  
  public boolean hasSwaRef()
  {
    return this.context.hasSwaRef();
  }
  
  public String toString()
  {
    return JAXBRIContextWrapper.class.getName() + " : " + this.context.toString();
  }
  
  public XMLBridge createBridge(TypeInfo paramTypeInfo)
  {
    TypeReference localTypeReference = (TypeReference)this.typeRefs.get(paramTypeInfo);
    Bridge localBridge = this.context.createBridge(localTypeReference);
    return WrapperComposite.class.equals(paramTypeInfo.type) ? new WrapperBridge(this, localBridge) : new BridgeWrapper(this, localBridge);
  }
  
  public JAXBContext getJAXBContext()
  {
    return this.context;
  }
  
  public QName getTypeName(TypeInfo paramTypeInfo)
  {
    TypeReference localTypeReference = (TypeReference)this.typeRefs.get(paramTypeInfo);
    return this.context.getTypeName(localTypeReference);
  }
  
  public XMLBridge createFragmentBridge()
  {
    return new MarshallerBridge((JAXBContextImpl)this.context);
  }
  
  public Object newWrapperInstace(Class<?> paramClass)
    throws InstantiationException, IllegalAccessException
  {
    return paramClass.newInstance();
  }
}
