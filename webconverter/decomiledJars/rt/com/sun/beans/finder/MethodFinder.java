package com.sun.beans.finder;

import com.sun.beans.TypeResolver;
import com.sun.beans.util.Cache;
import com.sun.beans.util.Cache.Kind;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import sun.reflect.misc.ReflectUtil;

public final class MethodFinder
  extends AbstractFinder<Method>
{
  private static final Cache<Signature, Method> CACHE = new Cache(Cache.Kind.SOFT, Cache.Kind.SOFT)
  {
    public Method create(Signature paramAnonymousSignature)
    {
      try
      {
        MethodFinder localMethodFinder = new MethodFinder(paramAnonymousSignature.getName(), paramAnonymousSignature.getArgs(), null);
        return MethodFinder.findAccessibleMethod((Method)localMethodFinder.find(paramAnonymousSignature.getType().getMethods()));
      }
      catch (Exception localException)
      {
        throw new SignatureException(localException);
      }
    }
  };
  private final String name;
  
  public static Method findMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Method name is not set");
    }
    PrimitiveWrapperMap.replacePrimitivesWithWrappers(paramVarArgs);
    Signature localSignature = new Signature(paramClass, paramString, paramVarArgs);
    try
    {
      Method localMethod = (Method)CACHE.get(localSignature);
      return (localMethod == null) || (ReflectUtil.isPackageAccessible(localMethod.getDeclaringClass())) ? localMethod : (Method)CACHE.create(localSignature);
    }
    catch (SignatureException localSignatureException)
    {
      throw localSignatureException.toNoSuchMethodException("Method '" + paramString + "' is not found");
    }
  }
  
  public static Method findInstanceMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    Method localMethod = findMethod(paramClass, paramString, paramVarArgs);
    if (Modifier.isStatic(localMethod.getModifiers())) {
      throw new NoSuchMethodException("Method '" + paramString + "' is static");
    }
    return localMethod;
  }
  
  public static Method findStaticMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    Method localMethod = findMethod(paramClass, paramString, paramVarArgs);
    if (!Modifier.isStatic(localMethod.getModifiers())) {
      throw new NoSuchMethodException("Method '" + paramString + "' is not static");
    }
    return localMethod;
  }
  
  public static Method findAccessibleMethod(Method paramMethod)
    throws NoSuchMethodException
  {
    Class localClass = paramMethod.getDeclaringClass();
    if ((Modifier.isPublic(localClass.getModifiers())) && (ReflectUtil.isPackageAccessible(localClass))) {
      return paramMethod;
    }
    if (Modifier.isStatic(paramMethod.getModifiers())) {
      throw new NoSuchMethodException("Method '" + paramMethod.getName() + "' is not accessible");
    }
    Type[] arrayOfType = localClass.getGenericInterfaces();
    int i = arrayOfType.length;
    int j = 0;
    while (j < i)
    {
      Type localType = arrayOfType[j];
      try
      {
        return findAccessibleMethod(paramMethod, localType);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        j++;
      }
    }
    return findAccessibleMethod(paramMethod, localClass.getGenericSuperclass());
  }
  
  private static Method findAccessibleMethod(Method paramMethod, Type paramType)
    throws NoSuchMethodException
  {
    String str = paramMethod.getName();
    Class[] arrayOfClass1 = paramMethod.getParameterTypes();
    Object localObject;
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      return findAccessibleMethod(((Class)localObject).getMethod(str, arrayOfClass1));
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      Class localClass = (Class)((ParameterizedType)localObject).getRawType();
      for (Method localMethod : localClass.getMethods()) {
        if (localMethod.getName().equals(str))
        {
          Class[] arrayOfClass2 = localMethod.getParameterTypes();
          if (arrayOfClass2.length == arrayOfClass1.length)
          {
            if (Arrays.equals(arrayOfClass1, arrayOfClass2)) {
              return findAccessibleMethod(localMethod);
            }
            Type[] arrayOfType = localMethod.getGenericParameterTypes();
            if ((arrayOfClass1.length == arrayOfType.length) && (Arrays.equals(arrayOfClass1, TypeResolver.erase(TypeResolver.resolve((Type)localObject, arrayOfType))))) {
              return findAccessibleMethod(localMethod);
            }
          }
        }
      }
    }
    throw new NoSuchMethodException("Method '" + str + "' is not accessible");
  }
  
  private MethodFinder(String paramString, Class<?>[] paramArrayOfClass)
  {
    super(paramArrayOfClass);
    this.name = paramString;
  }
  
  protected boolean isValid(Method paramMethod)
  {
    return (super.isValid(paramMethod)) && (paramMethod.getName().equals(this.name));
  }
}
