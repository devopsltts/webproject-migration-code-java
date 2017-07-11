package com.sun.xml.internal.ws.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

final class FieldSignature
{
  FieldSignature() {}
  
  static String vms(Type paramType)
  {
    Object localObject;
    if (((paramType instanceof Class)) && (((Class)paramType).isPrimitive()))
    {
      localObject = (Class)paramType;
      if (localObject == Integer.TYPE) {
        return "I";
      }
      if (localObject == Void.TYPE) {
        return "V";
      }
      if (localObject == Boolean.TYPE) {
        return "Z";
      }
      if (localObject == Byte.TYPE) {
        return "B";
      }
      if (localObject == Character.TYPE) {
        return "C";
      }
      if (localObject == Short.TYPE) {
        return "S";
      }
      if (localObject == Double.TYPE) {
        return "D";
      }
      if (localObject == Float.TYPE) {
        return "F";
      }
      if (localObject == Long.TYPE) {
        return "J";
      }
    }
    else
    {
      if (((paramType instanceof Class)) && (((Class)paramType).isArray())) {
        return "[" + vms(((Class)paramType).getComponentType());
      }
      if (((paramType instanceof Class)) || ((paramType instanceof ParameterizedType))) {
        return "L" + fqcn(paramType) + ";";
      }
      if ((paramType instanceof GenericArrayType)) {
        return "[" + vms(((GenericArrayType)paramType).getGenericComponentType());
      }
      if ((paramType instanceof TypeVariable)) {
        return "Ljava/lang/Object;";
      }
      if ((paramType instanceof WildcardType))
      {
        localObject = (WildcardType)paramType;
        if (((WildcardType)localObject).getLowerBounds().length > 0) {
          return "-" + vms(localObject.getLowerBounds()[0]);
        }
        if (((WildcardType)localObject).getUpperBounds().length > 0)
        {
          Type localType = localObject.getUpperBounds()[0];
          if (localType.equals(Object.class)) {
            return "*";
          }
          return "+" + vms(localType);
        }
      }
    }
    throw new IllegalArgumentException("Illegal vms arg " + paramType);
  }
  
  private static String fqcn(Type paramType)
  {
    Object localObject;
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      if (((Class)localObject).getDeclaringClass() == null) {
        return ((Class)localObject).getName().replace('.', '/');
      }
      return fqcn(((Class)localObject).getDeclaringClass()) + "$" + ((Class)localObject).getSimpleName();
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      if (((ParameterizedType)localObject).getOwnerType() == null) {
        return fqcn(((ParameterizedType)localObject).getRawType()) + args((ParameterizedType)localObject);
      }
      assert ((((ParameterizedType)localObject).getRawType() instanceof Class));
      return fqcn(((ParameterizedType)localObject).getOwnerType()) + "." + ((Class)((ParameterizedType)localObject).getRawType()).getSimpleName() + args((ParameterizedType)localObject);
    }
    throw new IllegalArgumentException("Illegal fqcn arg = " + paramType);
  }
  
  private static String args(ParameterizedType paramParameterizedType)
  {
    StringBuilder localStringBuilder = new StringBuilder("<");
    for (Type localType : paramParameterizedType.getActualTypeArguments()) {
      localStringBuilder.append(vms(localType));
    }
    return ">";
  }
}
