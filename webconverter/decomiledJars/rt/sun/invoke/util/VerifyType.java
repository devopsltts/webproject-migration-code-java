package sun.invoke.util;

import java.lang.invoke.MethodType;
import sun.invoke.empty.Empty;

public class VerifyType
{
  private VerifyType() {}
  
  public static boolean isNullConversion(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean)
  {
    if (paramClass1 == paramClass2) {
      return true;
    }
    if (!paramBoolean)
    {
      if (paramClass2.isInterface()) {
        paramClass2 = Object.class;
      }
      if (paramClass1.isInterface()) {
        paramClass1 = Object.class;
      }
      if (paramClass1 == paramClass2) {
        return true;
      }
    }
    if (isNullType(paramClass1)) {
      return !paramClass2.isPrimitive();
    }
    if (!paramClass1.isPrimitive()) {
      return paramClass2.isAssignableFrom(paramClass1);
    }
    if (!paramClass2.isPrimitive()) {
      return false;
    }
    Wrapper localWrapper1 = Wrapper.forPrimitiveType(paramClass1);
    if (paramClass2 == Integer.TYPE) {
      return localWrapper1.isSubwordOrInt();
    }
    Wrapper localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
    if (!localWrapper1.isSubwordOrInt()) {
      return false;
    }
    if (!localWrapper2.isSubwordOrInt()) {
      return false;
    }
    if ((!localWrapper2.isSigned()) && (localWrapper1.isSigned())) {
      return false;
    }
    return localWrapper2.bitWidth() > localWrapper1.bitWidth();
  }
  
  public static boolean isNullReferenceConversion(Class<?> paramClass1, Class<?> paramClass2)
  {
    assert (!paramClass2.isPrimitive());
    if (paramClass2.isInterface()) {
      return true;
    }
    if (isNullType(paramClass1)) {
      return true;
    }
    return paramClass2.isAssignableFrom(paramClass1);
  }
  
  public static boolean isNullType(Class<?> paramClass)
  {
    if (paramClass == Void.class) {
      return true;
    }
    return paramClass == Empty.class;
  }
  
  public static boolean isNullConversion(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean)
  {
    if (paramMethodType1 == paramMethodType2) {
      return true;
    }
    int i = paramMethodType1.parameterCount();
    if (i != paramMethodType2.parameterCount()) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (!isNullConversion(paramMethodType1.parameterType(j), paramMethodType2.parameterType(j), paramBoolean)) {
        return false;
      }
    }
    return isNullConversion(paramMethodType2.returnType(), paramMethodType1.returnType(), paramBoolean);
  }
  
  public static int canPassUnchecked(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass1 == paramClass2) {
      return 1;
    }
    if (paramClass2.isPrimitive())
    {
      if (paramClass2 == Void.TYPE) {
        return 1;
      }
      if (paramClass1 == Void.TYPE) {
        return 0;
      }
      if (!paramClass1.isPrimitive()) {
        return 0;
      }
      Wrapper localWrapper1 = Wrapper.forPrimitiveType(paramClass1);
      Wrapper localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
      if ((localWrapper1.isSubwordOrInt()) && (localWrapper2.isSubwordOrInt()))
      {
        if (localWrapper1.bitWidth() >= localWrapper2.bitWidth()) {
          return -1;
        }
        if ((!localWrapper2.isSigned()) && (localWrapper1.isSigned())) {
          return -1;
        }
        return 1;
      }
      if ((paramClass1 == Float.TYPE) || (paramClass2 == Float.TYPE))
      {
        if ((paramClass1 == Double.TYPE) || (paramClass2 == Double.TYPE)) {
          return -1;
        }
        return 0;
      }
      return 0;
    }
    if (paramClass1.isPrimitive()) {
      return 0;
    }
    if (isNullReferenceConversion(paramClass1, paramClass2)) {
      return 1;
    }
    return -1;
  }
  
  public static boolean isSpreadArgType(Class<?> paramClass)
  {
    return paramClass.isArray();
  }
  
  public static Class<?> spreadArgElementType(Class<?> paramClass, int paramInt)
  {
    return paramClass.getComponentType();
  }
}
