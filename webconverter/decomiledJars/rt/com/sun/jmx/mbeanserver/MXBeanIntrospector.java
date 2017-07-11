package com.sun.jmx.mbeanserver;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;

class MXBeanIntrospector
  extends MBeanIntrospector<ConvertingMethod>
{
  private static final MXBeanIntrospector instance = new MXBeanIntrospector();
  private final MBeanIntrospector.PerInterfaceMap<ConvertingMethod> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
  private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();
  
  MXBeanIntrospector() {}
  
  static MXBeanIntrospector getInstance()
  {
    return instance;
  }
  
  MBeanIntrospector.PerInterfaceMap<ConvertingMethod> getPerInterfaceMap()
  {
    return this.perInterfaceMap;
  }
  
  MBeanIntrospector.MBeanInfoMap getMBeanInfoMap()
  {
    return mbeanInfoMap;
  }
  
  MBeanAnalyzer<ConvertingMethod> getAnalyzer(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    return MBeanAnalyzer.analyzer(paramClass, this);
  }
  
  boolean isMXBean()
  {
    return true;
  }
  
  ConvertingMethod mFrom(Method paramMethod)
  {
    return ConvertingMethod.from(paramMethod);
  }
  
  String getName(ConvertingMethod paramConvertingMethod)
  {
    return paramConvertingMethod.getName();
  }
  
  Type getGenericReturnType(ConvertingMethod paramConvertingMethod)
  {
    return paramConvertingMethod.getGenericReturnType();
  }
  
  Type[] getGenericParameterTypes(ConvertingMethod paramConvertingMethod)
  {
    return paramConvertingMethod.getGenericParameterTypes();
  }
  
  String[] getSignature(ConvertingMethod paramConvertingMethod)
  {
    return paramConvertingMethod.getOpenSignature();
  }
  
  void checkMethod(ConvertingMethod paramConvertingMethod)
  {
    paramConvertingMethod.checkCallFromOpen();
  }
  
  Object invokeM2(ConvertingMethod paramConvertingMethod, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
    throws InvocationTargetException, IllegalAccessException, MBeanException
  {
    return paramConvertingMethod.invokeWithOpenReturn((MXBeanLookup)paramObject2, paramObject1, paramArrayOfObject);
  }
  
  boolean validParameter(ConvertingMethod paramConvertingMethod, Object paramObject1, int paramInt, Object paramObject2)
  {
    Object localObject;
    if (paramObject1 == null)
    {
      localObject = paramConvertingMethod.getGenericParameterTypes()[paramInt];
      return (!(localObject instanceof Class)) || (!((Class)localObject).isPrimitive());
    }
    try
    {
      localObject = paramConvertingMethod.fromOpenParameter((MXBeanLookup)paramObject2, paramObject1, paramInt);
    }
    catch (Exception localException)
    {
      return true;
    }
    return isValidParameter(paramConvertingMethod.getMethod(), localObject, paramInt);
  }
  
  MBeanAttributeInfo getMBeanAttributeInfo(String paramString, ConvertingMethod paramConvertingMethod1, ConvertingMethod paramConvertingMethod2)
  {
    boolean bool1 = paramConvertingMethod1 != null;
    boolean bool2 = paramConvertingMethod2 != null;
    boolean bool3 = (bool1) && (getName(paramConvertingMethod1).startsWith("is"));
    String str = paramString;
    OpenType localOpenType;
    Type localType;
    if (bool1)
    {
      localOpenType = paramConvertingMethod1.getOpenReturnType();
      localType = paramConvertingMethod1.getGenericReturnType();
    }
    else
    {
      localOpenType = paramConvertingMethod2.getOpenParameterTypes()[0];
      localType = paramConvertingMethod2.getGenericParameterTypes()[0];
    }
    Object localObject1 = typeDescriptor(localOpenType, localType);
    if (bool1) {
      localObject1 = ImmutableDescriptor.union(new Descriptor[] { localObject1, paramConvertingMethod1.getDescriptor() });
    }
    if (bool2) {
      localObject1 = ImmutableDescriptor.union(new Descriptor[] { localObject1, paramConvertingMethod2.getDescriptor() });
    }
    Object localObject2;
    if (canUseOpenInfo(localType)) {
      localObject2 = new OpenMBeanAttributeInfoSupport(paramString, str, localOpenType, bool1, bool2, bool3, (Descriptor)localObject1);
    } else {
      localObject2 = new MBeanAttributeInfo(paramString, originalTypeString(localType), str, bool1, bool2, bool3, (Descriptor)localObject1);
    }
    return localObject2;
  }
  
  MBeanOperationInfo getMBeanOperationInfo(String paramString, ConvertingMethod paramConvertingMethod)
  {
    Method localMethod = paramConvertingMethod.getMethod();
    String str = paramString;
    OpenType localOpenType1 = paramConvertingMethod.getOpenReturnType();
    Type localType1 = paramConvertingMethod.getGenericReturnType();
    OpenType[] arrayOfOpenType = paramConvertingMethod.getOpenParameterTypes();
    Type[] arrayOfType = paramConvertingMethod.getGenericParameterTypes();
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[arrayOfOpenType.length];
    boolean bool = canUseOpenInfo(localType1);
    int i = 1;
    Annotation[][] arrayOfAnnotation = localMethod.getParameterAnnotations();
    Object localObject2;
    Object localObject3;
    for (int j = 0; j < arrayOfOpenType.length; j++)
    {
      localObject2 = "p" + j;
      localObject3 = localObject2;
      OpenType localOpenType2 = arrayOfOpenType[j];
      Type localType2 = arrayOfType[j];
      Object localObject4 = typeDescriptor(localOpenType2, localType2);
      localObject4 = ImmutableDescriptor.union(new Descriptor[] { localObject4, Introspector.descriptorForAnnotations(arrayOfAnnotation[j]) });
      Object localObject5;
      if (canUseOpenInfo(localType2))
      {
        localObject5 = new OpenMBeanParameterInfoSupport((String)localObject2, (String)localObject3, localOpenType2, (Descriptor)localObject4);
      }
      else
      {
        i = 0;
        localObject5 = new MBeanParameterInfo((String)localObject2, originalTypeString(localType2), (String)localObject3, (Descriptor)localObject4);
      }
      arrayOfMBeanParameterInfo[j] = localObject5;
    }
    Object localObject1 = typeDescriptor(localOpenType1, localType1);
    localObject1 = ImmutableDescriptor.union(new Descriptor[] { localObject1, Introspector.descriptorForElement(localMethod) });
    if ((bool) && (i != 0))
    {
      localObject3 = new OpenMBeanParameterInfo[arrayOfMBeanParameterInfo.length];
      System.arraycopy(arrayOfMBeanParameterInfo, 0, localObject3, 0, arrayOfMBeanParameterInfo.length);
      localObject2 = new OpenMBeanOperationInfoSupport(paramString, str, (OpenMBeanParameterInfo[])localObject3, localOpenType1, 3, (Descriptor)localObject1);
    }
    else
    {
      localObject2 = new MBeanOperationInfo(paramString, str, arrayOfMBeanParameterInfo, bool ? localOpenType1.getClassName() : originalTypeString(localType1), 3, (Descriptor)localObject1);
    }
    return localObject2;
  }
  
  Descriptor getBasicMBeanDescriptor()
  {
    return new ImmutableDescriptor(new String[] { "mxbean=true", "immutableInfo=true" });
  }
  
  Descriptor getMBeanDescriptor(Class<?> paramClass)
  {
    return ImmutableDescriptor.EMPTY_DESCRIPTOR;
  }
  
  private static Descriptor typeDescriptor(OpenType<?> paramOpenType, Type paramType)
  {
    return new ImmutableDescriptor(new String[] { "openType", "originalType" }, new Object[] { paramOpenType, originalTypeString(paramType) });
  }
  
  private static boolean canUseOpenInfo(Type paramType)
  {
    if ((paramType instanceof GenericArrayType)) {
      return canUseOpenInfo(((GenericArrayType)paramType).getGenericComponentType());
    }
    if (((paramType instanceof Class)) && (((Class)paramType).isArray())) {
      return canUseOpenInfo(((Class)paramType).getComponentType());
    }
    return (!(paramType instanceof Class)) || (!((Class)paramType).isPrimitive());
  }
  
  private static String originalTypeString(Type paramType)
  {
    if ((paramType instanceof Class)) {
      return ((Class)paramType).getName();
    }
    return typeName(paramType);
  }
  
  static String typeName(Type paramType)
  {
    Object localObject;
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      if (((Class)localObject).isArray()) {
        return typeName(((Class)localObject).getComponentType()) + "[]";
      }
      return ((Class)localObject).getName();
    }
    if ((paramType instanceof GenericArrayType))
    {
      localObject = (GenericArrayType)paramType;
      return typeName(((GenericArrayType)localObject).getGenericComponentType()) + "[]";
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(typeName(((ParameterizedType)localObject).getRawType())).append("<");
      String str = "";
      for (Type localType : ((ParameterizedType)localObject).getActualTypeArguments())
      {
        localStringBuilder.append(str).append(typeName(localType));
        str = ", ";
      }
      return ">";
    }
    return "???";
  }
}
