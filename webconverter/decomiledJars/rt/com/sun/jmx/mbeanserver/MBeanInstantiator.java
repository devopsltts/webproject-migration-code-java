package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ConstructorUtil;
import sun.reflect.misc.ReflectUtil;

public class MBeanInstantiator
{
  private final ModifiableClassLoaderRepository clr;
  private static final Map<String, Class<?>> primitiveClasses = ;
  
  MBeanInstantiator(ModifiableClassLoaderRepository paramModifiableClassLoaderRepository)
  {
    this.clr = paramModifiableClassLoaderRepository;
  }
  
  public void testCreation(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    Introspector.testCreation(paramClass);
  }
  
  public Class<?> findClassWithDefaultLoaderRepository(String paramString)
    throws ReflectionException
  {
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
    }
    ReflectUtil.checkPackageAccess(paramString);
    Class localClass;
    try
    {
      if (this.clr == null) {
        throw new ClassNotFoundException(paramString);
      }
      localClass = this.clr.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ReflectionException(localClassNotFoundException, "The MBean class could not be loaded by the default loader repository");
    }
    return localClass;
  }
  
  public Class<?> findClass(String paramString, ClassLoader paramClassLoader)
    throws ReflectionException
  {
    return loadClass(paramString, paramClassLoader);
  }
  
  public Class<?> findClass(String paramString, ObjectName paramObjectName)
    throws ReflectionException, InstanceNotFoundException
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null loader passed in parameter");
    }
    ClassLoader localClassLoader = null;
    synchronized (this)
    {
      localClassLoader = getClassLoader(paramObjectName);
    }
    if (localClassLoader == null) {
      throw new InstanceNotFoundException("The loader named " + paramObjectName + " is not registered in the MBeanServer");
    }
    return findClass(paramString, localClassLoader);
  }
  
  public Class<?>[] findSignatureClasses(String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws ReflectionException
  {
    if (paramArrayOfString == null) {
      return null;
    }
    ClassLoader localClassLoader = paramClassLoader;
    int i = paramArrayOfString.length;
    Class[] arrayOfClass = new Class[i];
    if (i == 0) {
      return arrayOfClass;
    }
    try
    {
      for (int j = 0; j < i; j++)
      {
        Class localClass = (Class)primitiveClasses.get(paramArrayOfString[j]);
        if (localClass != null)
        {
          arrayOfClass[j] = localClass;
        }
        else
        {
          ReflectUtil.checkPackageAccess(paramArrayOfString[j]);
          if (localClassLoader != null) {
            arrayOfClass[j] = Class.forName(paramArrayOfString[j], false, localClassLoader);
          } else {
            arrayOfClass[j] = findClass(paramArrayOfString[j], getClass().getClassLoader());
          }
        }
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", localClassNotFoundException);
      }
      throw new ReflectionException(localClassNotFoundException, "The parameter class could not be found");
    }
    catch (RuntimeException localRuntimeException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", localRuntimeException);
      }
      throw localRuntimeException;
    }
    return arrayOfClass;
  }
  
  public Object instantiate(Class<?> paramClass)
    throws ReflectionException, MBeanException
  {
    checkMBeanPermission(paramClass, null, null, "instantiate");
    Constructor localConstructor = findConstructor(paramClass, null);
    if (localConstructor == null) {
      throw new ReflectionException(new NoSuchMethodException("No such constructor"));
    }
    Object localObject;
    try
    {
      ReflectUtil.checkPackageAccess(paramClass);
      ensureClassAccess(paramClass);
      localObject = localConstructor.newInstance(new Object[0]);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      if ((localThrowable instanceof RuntimeException)) {
        throw new RuntimeMBeanException((RuntimeException)localThrowable, "RuntimeException thrown in the MBean's empty constructor");
      }
      if ((localThrowable instanceof Error)) {
        throw new RuntimeErrorException((Error)localThrowable, "Error thrown in the MBean's empty constructor");
      }
      throw new MBeanException((Exception)localThrowable, "Exception thrown in the MBean's empty constructor");
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      throw new ReflectionException(new NoSuchMethodException("No constructor"), "No such constructor");
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new ReflectionException(localInstantiationException, "Exception thrown trying to invoke the MBean's empty constructor");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionException(localIllegalAccessException, "Exception thrown trying to invoke the MBean's empty constructor");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ReflectionException(localIllegalArgumentException, "Exception thrown trying to invoke the MBean's empty constructor");
    }
    return localObject;
  }
  
  public Object instantiate(Class<?> paramClass, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws ReflectionException, MBeanException
  {
    checkMBeanPermission(paramClass, null, null, "instantiate");
    Class[] arrayOfClass;
    try
    {
      ClassLoader localClassLoader = paramClass.getClassLoader();
      arrayOfClass = paramArrayOfString == null ? null : findSignatureClasses(paramArrayOfString, localClassLoader);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ReflectionException(localIllegalArgumentException, "The constructor parameter classes could not be loaded");
    }
    Constructor localConstructor = findConstructor(paramClass, arrayOfClass);
    if (localConstructor == null) {
      throw new ReflectionException(new NoSuchMethodException("No such constructor"));
    }
    Object localObject;
    try
    {
      ReflectUtil.checkPackageAccess(paramClass);
      ensureClassAccess(paramClass);
      localObject = localConstructor.newInstance(paramArrayOfObject);
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      throw new ReflectionException(new NoSuchMethodException("No such constructor found"), "No such constructor");
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new ReflectionException(localInstantiationException, "Exception thrown trying to invoke the MBean's constructor");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionException(localIllegalAccessException, "Exception thrown trying to invoke the MBean's constructor");
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      if ((localThrowable instanceof RuntimeException)) {
        throw new RuntimeMBeanException((RuntimeException)localThrowable, "RuntimeException thrown in the MBean's constructor");
      }
      if ((localThrowable instanceof Error)) {
        throw new RuntimeErrorException((Error)localThrowable, "Error thrown in the MBean's constructor");
      }
      throw new MBeanException((Exception)localThrowable, "Exception thrown in the MBean's constructor");
    }
    return localObject;
  }
  
  public ObjectInputStream deserialize(ClassLoader paramClassLoader, byte[] paramArrayOfByte)
    throws OperationsException
  {
    if (paramArrayOfByte == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
    }
    if (paramArrayOfByte.length == 0) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
    }
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    ObjectInputStreamWithLoader localObjectInputStreamWithLoader;
    try
    {
      localObjectInputStreamWithLoader = new ObjectInputStreamWithLoader(localByteArrayInputStream, paramClassLoader);
    }
    catch (IOException localIOException)
    {
      throw new OperationsException("An IOException occurred trying to de-serialize the data");
    }
    return localObjectInputStreamWithLoader;
  }
  
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte, ClassLoader paramClassLoader)
    throws InstanceNotFoundException, OperationsException, ReflectionException
  {
    if (paramArrayOfByte == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
    }
    if (paramArrayOfByte.length == 0) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
    }
    ReflectUtil.checkPackageAccess(paramString);
    Class localClass;
    if (paramObjectName == null) {
      localClass = findClass(paramString, paramClassLoader);
    } else {
      try
      {
        ClassLoader localClassLoader = null;
        localClassLoader = getClassLoader(paramObjectName);
        if (localClassLoader == null) {
          throw new ClassNotFoundException(paramString);
        }
        localClass = Class.forName(paramString, false, localClassLoader);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new ReflectionException(localClassNotFoundException, "The MBean class could not be loaded by the " + paramObjectName.toString() + " class loader");
      }
    }
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    ObjectInputStreamWithLoader localObjectInputStreamWithLoader;
    try
    {
      localObjectInputStreamWithLoader = new ObjectInputStreamWithLoader(localByteArrayInputStream, localClass.getClassLoader());
    }
    catch (IOException localIOException)
    {
      throw new OperationsException("An IOException occurred trying to de-serialize the data");
    }
    return localObjectInputStreamWithLoader;
  }
  
  public Object instantiate(String paramString)
    throws ReflectionException, MBeanException
  {
    return instantiate(paramString, (Object[])null, (String[])null, null);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, ClassLoader paramClassLoader)
    throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    return instantiate(paramString, paramObjectName, (Object[])null, (String[])null, paramClassLoader);
  }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws ReflectionException, MBeanException
  {
    Class localClass = findClassWithDefaultLoaderRepository(paramString);
    return instantiate(localClass, paramArrayOfObject, paramArrayOfString, paramClassLoader);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    Class localClass;
    if (paramObjectName == null) {
      localClass = findClass(paramString, paramClassLoader);
    } else {
      localClass = findClass(paramString, paramObjectName);
    }
    return instantiate(localClass, paramArrayOfObject, paramArrayOfString, paramClassLoader);
  }
  
  public ModifiableClassLoaderRepository getClassLoaderRepository()
  {
    checkMBeanPermission((String)null, null, null, "getClassLoaderRepository");
    return this.clr;
  }
  
  static Class<?> loadClass(String paramString, ClassLoader paramClassLoader)
    throws ReflectionException
  {
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
    }
    ReflectUtil.checkPackageAccess(paramString);
    Class localClass;
    try
    {
      if (paramClassLoader == null) {
        paramClassLoader = MBeanInstantiator.class.getClassLoader();
      }
      if (paramClassLoader != null) {
        localClass = Class.forName(paramString, false, paramClassLoader);
      } else {
        localClass = Class.forName(paramString);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ReflectionException(localClassNotFoundException, "The MBean class could not be loaded");
    }
    return localClass;
  }
  
  static Class<?>[] loadSignatureClasses(String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws ReflectionException
  {
    if (paramArrayOfString == null) {
      return null;
    }
    ClassLoader localClassLoader = paramClassLoader == null ? MBeanInstantiator.class.getClassLoader() : paramClassLoader;
    int i = paramArrayOfString.length;
    Class[] arrayOfClass = new Class[i];
    if (i == 0) {
      return arrayOfClass;
    }
    try
    {
      for (int j = 0; j < i; j++)
      {
        Class localClass = (Class)primitiveClasses.get(paramArrayOfString[j]);
        if (localClass != null)
        {
          arrayOfClass[j] = localClass;
        }
        else
        {
          ReflectUtil.checkPackageAccess(paramArrayOfString[j]);
          arrayOfClass[j] = Class.forName(paramArrayOfString[j], false, localClassLoader);
        }
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", localClassNotFoundException);
      }
      throw new ReflectionException(localClassNotFoundException, "The parameter class could not be found");
    }
    catch (RuntimeException localRuntimeException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", localRuntimeException);
      }
      throw localRuntimeException;
    }
    return arrayOfClass;
  }
  
  private Constructor<?> findConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass)
  {
    try
    {
      return ConstructorUtil.getConstructor(paramClass, paramArrayOfClass);
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static void checkMBeanPermission(Class<?> paramClass, String paramString1, ObjectName paramObjectName, String paramString2)
  {
    if (paramClass != null) {
      checkMBeanPermission(paramClass.getName(), paramString1, paramObjectName, paramString2);
    }
  }
  
  private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      MBeanPermission localMBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
      localSecurityManager.checkPermission(localMBeanPermission);
    }
  }
  
  private static void ensureClassAccess(Class paramClass)
    throws IllegalAccessException
  {
    int i = paramClass.getModifiers();
    if (!Modifier.isPublic(i)) {
      throw new IllegalAccessException("Class is not public and can't be instantiated");
    }
  }
  
  private ClassLoader getClassLoader(final ObjectName paramObjectName)
  {
    if (this.clr == null) {
      return null;
    }
    Permissions localPermissions = new Permissions();
    localPermissions.add(new MBeanPermission("*", null, paramObjectName, "getClassLoader"));
    ProtectionDomain localProtectionDomain = new ProtectionDomain(null, localPermissions);
    ProtectionDomain[] arrayOfProtectionDomain = { localProtectionDomain };
    AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return MBeanInstantiator.this.clr.getClassLoader(paramObjectName);
      }
    }, localAccessControlContext);
    return localClassLoader;
  }
  
  static
  {
    for (Class localClass : new Class[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Character.TYPE, Boolean.TYPE }) {
      primitiveClasses.put(localClass.getName(), localClass);
    }
  }
}
