package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public abstract interface BindingContext
{
  public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.internal.bind.defaultNamespaceRemap";
  public static final String TYPE_REFERENCES = "com.sun.xml.internal.bind.typeReferences";
  public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.internal.bind.c14n";
  public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.internal.bind.treatEverythingNillable";
  public static final String ENABLE_XOP = "com.sun.xml.internal.bind.XOP";
  public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.internal.bind.subclassReplacements";
  public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.internal.bind.XmlAccessorFactory";
  public static final String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";
  
  public abstract Marshaller createMarshaller()
    throws JAXBException;
  
  public abstract Unmarshaller createUnmarshaller()
    throws JAXBException;
  
  public abstract JAXBContext getJAXBContext();
  
  public abstract Object newWrapperInstace(Class<?> paramClass)
    throws InstantiationException, IllegalAccessException;
  
  public abstract boolean hasSwaRef();
  
  @Nullable
  public abstract QName getElementName(@NotNull Object paramObject)
    throws JAXBException;
  
  @Nullable
  public abstract QName getElementName(@NotNull Class paramClass)
    throws JAXBException;
  
  public abstract XMLBridge createBridge(@NotNull TypeInfo paramTypeInfo);
  
  public abstract XMLBridge createFragmentBridge();
  
  public abstract <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> paramClass, String paramString1, String paramString2)
    throws JAXBException;
  
  @NotNull
  public abstract List<String> getKnownNamespaceURIs();
  
  public abstract void generateSchema(@NotNull SchemaOutputResolver paramSchemaOutputResolver)
    throws IOException;
  
  public abstract QName getTypeName(@NotNull TypeInfo paramTypeInfo);
  
  @NotNull
  public abstract String getBuildId();
}
