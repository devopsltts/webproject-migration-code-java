package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.annotation.FieldLocatable;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class RuntimeEnumLeafInfoImpl<T extends Enum<T>, B>
  extends EnumLeafInfoImpl<Type, Class, Field, Method>
  implements RuntimeEnumLeafInfo, Transducer<T>
{
  private final Transducer<B> baseXducer;
  private final Map<B, T> parseMap = new HashMap();
  private final Map<T, B> printMap;
  
  public Transducer<T> getTransducer()
  {
    return this;
  }
  
  RuntimeEnumLeafInfoImpl(RuntimeModelBuilder paramRuntimeModelBuilder, Locatable paramLocatable, Class<T> paramClass)
  {
    super(paramRuntimeModelBuilder, paramLocatable, paramClass, paramClass);
    this.printMap = new EnumMap(paramClass);
    this.baseXducer = ((RuntimeNonElement)this.baseType).getTransducer();
  }
  
  public RuntimeEnumConstantImpl createEnumConstant(String paramString1, String paramString2, Field paramField, EnumConstantImpl<Type, Class, Field, Method> paramEnumConstantImpl)
  {
    Enum localEnum;
    try
    {
      try
      {
        paramField.setAccessible(true);
      }
      catch (SecurityException localSecurityException) {}
      localEnum = (Enum)paramField.get(null);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalAccessError(localIllegalAccessException.getMessage());
    }
    Object localObject = null;
    try
    {
      localObject = this.baseXducer.parse(paramString2);
    }
    catch (Exception localException)
    {
      this.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ENUM_VALUE.format(new Object[] { paramString2, ((Type)this.baseType.getType()).toString() }), localException, new FieldLocatable(this, paramField, nav())));
    }
    this.parseMap.put(localObject, localEnum);
    this.printMap.put(localEnum, localObject);
    return new RuntimeEnumConstantImpl(this, paramString1, paramString2, paramEnumConstantImpl);
  }
  
  public QName[] getTypeNames()
  {
    return new QName[] { getTypeName() };
  }
  
  public boolean isDefault()
  {
    return false;
  }
  
  public Class getClazz()
  {
    return (Class)this.clazz;
  }
  
  public boolean useNamespace()
  {
    return this.baseXducer.useNamespace();
  }
  
  public void declareNamespace(T paramT, XMLSerializer paramXMLSerializer)
    throws AccessorException
  {
    this.baseXducer.declareNamespace(this.printMap.get(paramT), paramXMLSerializer);
  }
  
  public CharSequence print(T paramT)
    throws AccessorException
  {
    return this.baseXducer.print(this.printMap.get(paramT));
  }
  
  public T parse(CharSequence paramCharSequence)
    throws AccessorException, SAXException
  {
    Object localObject = this.baseXducer.parse(paramCharSequence);
    if (this.tokenStringType) {
      localObject = ((String)localObject).trim();
    }
    return (Enum)this.parseMap.get(localObject);
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, T paramT, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    this.baseXducer.writeText(paramXMLSerializer, this.printMap.get(paramT), paramString);
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, T paramT, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    this.baseXducer.writeLeafElement(paramXMLSerializer, paramName, this.printMap.get(paramT), paramString);
  }
  
  public QName getTypeName(T paramT)
  {
    return null;
  }
}
