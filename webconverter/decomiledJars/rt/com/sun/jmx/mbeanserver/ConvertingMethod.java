package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import sun.reflect.misc.MethodUtil;

final class ConvertingMethod
{
  private static final String[] noStrings = new String[0];
  private final Method method;
  private final MXBeanMapping returnMapping;
  private final MXBeanMapping[] paramMappings;
  private final boolean paramConversionIsIdentity;
  
  static ConvertingMethod from(Method paramMethod)
  {
    try
    {
      return new ConvertingMethod(paramMethod);
    }
    catch (OpenDataException localOpenDataException)
    {
      String str = "Method " + paramMethod.getDeclaringClass().getName() + "." + paramMethod.getName() + " has parameter or return type that " + "cannot be translated into an open type";
      throw new IllegalArgumentException(str, localOpenDataException);
    }
  }
  
  Method getMethod()
  {
    return this.method;
  }
  
  Descriptor getDescriptor()
  {
    return Introspector.descriptorForElement(this.method);
  }
  
  Type getGenericReturnType()
  {
    return this.method.getGenericReturnType();
  }
  
  Type[] getGenericParameterTypes()
  {
    return this.method.getGenericParameterTypes();
  }
  
  String getName()
  {
    return this.method.getName();
  }
  
  OpenType<?> getOpenReturnType()
  {
    return this.returnMapping.getOpenType();
  }
  
  OpenType<?>[] getOpenParameterTypes()
  {
    OpenType[] arrayOfOpenType = new OpenType[this.paramMappings.length];
    for (int i = 0; i < this.paramMappings.length; i++) {
      arrayOfOpenType[i] = this.paramMappings[i].getOpenType();
    }
    return arrayOfOpenType;
  }
  
  void checkCallFromOpen()
  {
    try
    {
      for (MXBeanMapping localMXBeanMapping : this.paramMappings) {
        localMXBeanMapping.checkReconstructible();
      }
    }
    catch (InvalidObjectException localInvalidObjectException)
    {
      throw new IllegalArgumentException(localInvalidObjectException);
    }
  }
  
  void checkCallToOpen()
  {
    try
    {
      this.returnMapping.checkReconstructible();
    }
    catch (InvalidObjectException localInvalidObjectException)
    {
      throw new IllegalArgumentException(localInvalidObjectException);
    }
  }
  
  String[] getOpenSignature()
  {
    if (this.paramMappings.length == 0) {
      return noStrings;
    }
    String[] arrayOfString = new String[this.paramMappings.length];
    for (int i = 0; i < this.paramMappings.length; i++) {
      arrayOfString[i] = this.paramMappings[i].getOpenClass().getName();
    }
    return arrayOfString;
  }
  
  final Object toOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject)
    throws OpenDataException
  {
    return this.returnMapping.toOpenValue(paramObject);
  }
  
  final Object fromOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject)
    throws InvalidObjectException
  {
    return this.returnMapping.fromOpenValue(paramObject);
  }
  
  final Object[] toOpenParameters(MXBeanLookup paramMXBeanLookup, Object[] paramArrayOfObject)
    throws OpenDataException
  {
    if ((this.paramConversionIsIdentity) || (paramArrayOfObject == null)) {
      return paramArrayOfObject;
    }
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      arrayOfObject[i] = this.paramMappings[i].toOpenValue(paramArrayOfObject[i]);
    }
    return arrayOfObject;
  }
  
  final Object[] fromOpenParameters(Object[] paramArrayOfObject)
    throws InvalidObjectException
  {
    if ((this.paramConversionIsIdentity) || (paramArrayOfObject == null)) {
      return paramArrayOfObject;
    }
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      arrayOfObject[i] = this.paramMappings[i].fromOpenValue(paramArrayOfObject[i]);
    }
    return arrayOfObject;
  }
  
  final Object toOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt)
    throws OpenDataException
  {
    return this.paramMappings[paramInt].toOpenValue(paramObject);
  }
  
  final Object fromOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt)
    throws InvalidObjectException
  {
    return this.paramMappings[paramInt].fromOpenValue(paramObject);
  }
  
  Object invokeWithOpenReturn(MXBeanLookup paramMXBeanLookup, Object paramObject, Object[] paramArrayOfObject)
    throws MBeanException, IllegalAccessException, InvocationTargetException
  {
    MXBeanLookup localMXBeanLookup = MXBeanLookup.getLookup();
    try
    {
      MXBeanLookup.setLookup(paramMXBeanLookup);
      Object localObject1 = invokeWithOpenReturn(paramObject, paramArrayOfObject);
      return localObject1;
    }
    finally
    {
      MXBeanLookup.setLookup(localMXBeanLookup);
    }
  }
  
  private Object invokeWithOpenReturn(Object paramObject, Object[] paramArrayOfObject)
    throws MBeanException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject;
    try
    {
      arrayOfObject = fromOpenParameters(paramArrayOfObject);
    }
    catch (InvalidObjectException localInvalidObjectException)
    {
      String str1 = methodName() + ": cannot convert parameters " + "from open values: " + localInvalidObjectException;
      throw new MBeanException(localInvalidObjectException, str1);
    }
    Object localObject = MethodUtil.invoke(this.method, paramObject, arrayOfObject);
    try
    {
      return this.returnMapping.toOpenValue(localObject);
    }
    catch (OpenDataException localOpenDataException)
    {
      String str2 = methodName() + ": cannot convert return " + "value to open value: " + localOpenDataException;
      throw new MBeanException(localOpenDataException, str2);
    }
  }
  
  private String methodName()
  {
    return this.method.getDeclaringClass() + "." + this.method.getName();
  }
  
  private ConvertingMethod(Method paramMethod)
    throws OpenDataException
  {
    this.method = paramMethod;
    MXBeanMappingFactory localMXBeanMappingFactory = MXBeanMappingFactory.DEFAULT;
    this.returnMapping = localMXBeanMappingFactory.mappingForType(paramMethod.getGenericReturnType(), localMXBeanMappingFactory);
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    this.paramMappings = new MXBeanMapping[arrayOfType.length];
    boolean bool = true;
    for (int i = 0; i < arrayOfType.length; i++)
    {
      this.paramMappings[i] = localMXBeanMappingFactory.mappingForType(arrayOfType[i], localMXBeanMappingFactory);
      bool &= DefaultMXBeanMappingFactory.isIdentity(this.paramMappings[i]);
    }
    this.paramConversionIsIdentity = bool;
  }
}
