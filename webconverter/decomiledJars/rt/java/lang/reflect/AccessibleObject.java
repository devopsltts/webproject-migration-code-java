package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.Permission;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class AccessibleObject
  implements AnnotatedElement
{
  private static final Permission ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
  boolean override;
  static final ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
  volatile Object securityCheckCache;
  
  public static void setAccessible(AccessibleObject[] paramArrayOfAccessibleObject, boolean paramBoolean)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(ACCESS_PERMISSION);
    }
    for (int i = 0; i < paramArrayOfAccessibleObject.length; i++) {
      setAccessible0(paramArrayOfAccessibleObject[i], paramBoolean);
    }
  }
  
  public void setAccessible(boolean paramBoolean)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(ACCESS_PERMISSION);
    }
    setAccessible0(this, paramBoolean);
  }
  
  private static void setAccessible0(AccessibleObject paramAccessibleObject, boolean paramBoolean)
    throws SecurityException
  {
    if (((paramAccessibleObject instanceof Constructor)) && (paramBoolean == true))
    {
      Constructor localConstructor = (Constructor)paramAccessibleObject;
      if (localConstructor.getDeclaringClass() == Class.class) {
        throw new SecurityException("Cannot make a java.lang.Class constructor accessible");
      }
    }
    paramAccessibleObject.override = paramBoolean;
  }
  
  public boolean isAccessible()
  {
    return this.override;
  }
  
  protected AccessibleObject() {}
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    throw new AssertionError("All subclasses should override this method");
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass)
  {
    return super.isAnnotationPresent(paramClass);
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
  {
    throw new AssertionError("All subclasses should override this method");
  }
  
  public Annotation[] getAnnotations()
  {
    return getDeclaredAnnotations();
  }
  
  public <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass)
  {
    return getAnnotation(paramClass);
  }
  
  public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass)
  {
    return getAnnotationsByType(paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    throw new AssertionError("All subclasses should override this method");
  }
  
  void checkAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt)
    throws IllegalAccessException
  {
    if (paramClass1 == paramClass2) {
      return;
    }
    Object localObject1 = this.securityCheckCache;
    Object localObject2 = paramClass2;
    if ((paramObject != null) && (Modifier.isProtected(paramInt)) && ((localObject2 = paramObject.getClass()) != paramClass2))
    {
      if ((localObject1 instanceof Class[]))
      {
        Class[] arrayOfClass = (Class[])localObject1;
        if ((arrayOfClass[1] == localObject2) && (arrayOfClass[0] == paramClass1)) {
          return;
        }
      }
    }
    else if (localObject1 == paramClass1) {
      return;
    }
    slowCheckMemberAccess(paramClass1, paramClass2, paramObject, paramInt, (Class)localObject2);
  }
  
  void slowCheckMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt, Class<?> paramClass3)
    throws IllegalAccessException
  {
    Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, paramInt);
    Class[] arrayOfClass = { paramClass1, paramClass3 == paramClass2 ? paramClass1 : paramClass3 };
    this.securityCheckCache = arrayOfClass;
  }
}
