package java.lang.invoke;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

public abstract interface MethodHandleInfo
{
  public static final int REF_getField = 1;
  public static final int REF_getStatic = 2;
  public static final int REF_putField = 3;
  public static final int REF_putStatic = 4;
  public static final int REF_invokeVirtual = 5;
  public static final int REF_invokeStatic = 6;
  public static final int REF_invokeSpecial = 7;
  public static final int REF_newInvokeSpecial = 8;
  public static final int REF_invokeInterface = 9;
  
  public abstract int getReferenceKind();
  
  public abstract Class<?> getDeclaringClass();
  
  public abstract String getName();
  
  public abstract MethodType getMethodType();
  
  public abstract <T extends Member> T reflectAs(Class<T> paramClass, MethodHandles.Lookup paramLookup);
  
  public abstract int getModifiers();
  
  public boolean isVarArgs()
  {
    if (MethodHandleNatives.refKindIsField((byte)getReferenceKind())) {
      return false;
    }
    return Modifier.isTransient(getModifiers());
  }
  
  public static String referenceKindToString(int paramInt)
  {
    if (!MethodHandleNatives.refKindIsValid(paramInt)) {
      throw MethodHandleStatics.newIllegalArgumentException("invalid reference kind", Integer.valueOf(paramInt));
    }
    return MethodHandleNatives.refKindName((byte)paramInt);
  }
  
  public static String toString(int paramInt, Class<?> paramClass, String paramString, MethodType paramMethodType)
  {
    Objects.requireNonNull(paramString);
    Objects.requireNonNull(paramMethodType);
    return String.format("%s %s.%s:%s", new Object[] { referenceKindToString(paramInt), paramClass.getName(), paramString, paramMethodType });
  }
}
