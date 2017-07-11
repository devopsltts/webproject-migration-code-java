package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DescriptorKey;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.CompositeData;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class Introspector
{
  public static final boolean ALLOW_NONPUBLIC_MBEAN;
  
  private Introspector() {}
  
  public static final boolean isDynamic(Class<?> paramClass)
  {
    return DynamicMBean.class.isAssignableFrom(paramClass);
  }
  
  public static void testCreation(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    int i = paramClass.getModifiers();
    if ((Modifier.isAbstract(i)) || (Modifier.isInterface(i))) {
      throw new NotCompliantMBeanException("MBean class must be concrete");
    }
    Constructor[] arrayOfConstructor = paramClass.getConstructors();
    if (arrayOfConstructor.length == 0) {
      throw new NotCompliantMBeanException("MBean class must have public constructor");
    }
  }
  
  public static void checkCompliance(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    if (DynamicMBean.class.isAssignableFrom(paramClass)) {
      return;
    }
    try
    {
      getStandardMBeanInterface(paramClass);
      return;
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException2)
    {
      NotCompliantMBeanException localNotCompliantMBeanException1 = localNotCompliantMBeanException2;
      try
      {
        getMXBeanInterface(paramClass);
        return;
      }
      catch (NotCompliantMBeanException localNotCompliantMBeanException3)
      {
        Object localObject = localNotCompliantMBeanException3;
        String str = "MBean class " + paramClass.getName() + " does not implement " + "DynamicMBean, and neither follows the Standard MBean conventions (" + localNotCompliantMBeanException1.toString() + ") nor the MXBean conventions (" + localObject.toString() + ")";
        throw new NotCompliantMBeanException(str);
      }
    }
  }
  
  public static <T> DynamicMBean makeDynamicMBean(T paramT)
    throws NotCompliantMBeanException
  {
    if ((paramT instanceof DynamicMBean)) {
      return (DynamicMBean)paramT;
    }
    Class localClass1 = paramT.getClass();
    Class localClass2 = null;
    try
    {
      localClass2 = (Class)Util.cast(getStandardMBeanInterface(localClass1));
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException1) {}
    if (localClass2 != null) {
      return new StandardMBeanSupport(paramT, localClass2);
    }
    try
    {
      localClass2 = (Class)Util.cast(getMXBeanInterface(localClass1));
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException2) {}
    if (localClass2 != null) {
      return new MXBeanSupport(paramT, localClass2);
    }
    checkCompliance(localClass1);
    throw new NotCompliantMBeanException("Not compliant");
  }
  
  public static MBeanInfo testCompliance(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    if (isDynamic(paramClass)) {
      return null;
    }
    return testCompliance(paramClass, null);
  }
  
  public static void testComplianceMXBeanInterface(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    MXBeanIntrospector.getInstance().getAnalyzer(paramClass);
  }
  
  public static void testComplianceMBeanInterface(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    StandardMBeanIntrospector.getInstance().getAnalyzer(paramClass);
  }
  
  public static synchronized MBeanInfo testCompliance(Class<?> paramClass1, Class<?> paramClass2)
    throws NotCompliantMBeanException
  {
    if (paramClass2 == null) {
      paramClass2 = getStandardMBeanInterface(paramClass1);
    }
    ReflectUtil.checkPackageAccess(paramClass2);
    StandardMBeanIntrospector localStandardMBeanIntrospector = StandardMBeanIntrospector.getInstance();
    return getClassMBeanInfo(localStandardMBeanIntrospector, paramClass1, paramClass2);
  }
  
  private static <M> MBeanInfo getClassMBeanInfo(MBeanIntrospector<M> paramMBeanIntrospector, Class<?> paramClass1, Class<?> paramClass2)
    throws NotCompliantMBeanException
  {
    PerInterface localPerInterface = paramMBeanIntrospector.getPerInterface(paramClass2);
    return paramMBeanIntrospector.getClassMBeanInfo(paramClass1, localPerInterface);
  }
  
  public static Class<?> getMBeanInterface(Class<?> paramClass)
  {
    if (isDynamic(paramClass)) {
      return null;
    }
    try
    {
      return getStandardMBeanInterface(paramClass);
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException) {}
    return null;
  }
  
  public static <T> Class<? super T> getStandardMBeanInterface(Class<T> paramClass)
    throws NotCompliantMBeanException
  {
    Object localObject = paramClass;
    Class localClass = null;
    while (localObject != null)
    {
      localClass = findMBeanInterface((Class)localObject, ((Class)localObject).getName());
      if (localClass != null) {
        break;
      }
      localObject = ((Class)localObject).getSuperclass();
    }
    if (localClass != null) {
      return localClass;
    }
    String str = "Class " + paramClass.getName() + " is not a JMX compliant Standard MBean";
    throw new NotCompliantMBeanException(str);
  }
  
  public static <T> Class<? super T> getMXBeanInterface(Class<T> paramClass)
    throws NotCompliantMBeanException
  {
    try
    {
      return MXBeanSupport.findMXBeanInterface(paramClass);
    }
    catch (Exception localException)
    {
      throw throwException(paramClass, localException);
    }
  }
  
  private static <T> Class<? super T> findMBeanInterface(Class<T> paramClass, String paramString)
  {
    for (Object localObject = paramClass; localObject != null; localObject = ((Class)localObject).getSuperclass())
    {
      Class[] arrayOfClass = ((Class)localObject).getInterfaces();
      int i = arrayOfClass.length;
      for (int j = 0; j < i; j++)
      {
        Class localClass = (Class)Util.cast(arrayOfClass[j]);
        localClass = implementsMBean(localClass, paramString);
        if (localClass != null) {
          return localClass;
        }
      }
    }
    return null;
  }
  
  public static Descriptor descriptorForElement(AnnotatedElement paramAnnotatedElement)
  {
    if (paramAnnotatedElement == null) {
      return ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }
    Annotation[] arrayOfAnnotation = paramAnnotatedElement.getAnnotations();
    return descriptorForAnnotations(arrayOfAnnotation);
  }
  
  public static Descriptor descriptorForAnnotations(Annotation[] paramArrayOfAnnotation)
  {
    if (paramArrayOfAnnotation.length == 0) {
      return ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }
    HashMap localHashMap = new HashMap();
    for (Annotation localAnnotation : paramArrayOfAnnotation)
    {
      Class localClass = localAnnotation.annotationType();
      Method[] arrayOfMethod1 = localClass.getMethods();
      int k = 0;
      for (Method localMethod : arrayOfMethod1)
      {
        DescriptorKey localDescriptorKey = (DescriptorKey)localMethod.getAnnotation(DescriptorKey.class);
        if (localDescriptorKey != null)
        {
          String str1 = localDescriptorKey.value();
          try
          {
            if (k == 0)
            {
              ReflectUtil.checkPackageAccess(localClass);
              k = 1;
            }
            localObject1 = MethodUtil.invoke(localMethod, localAnnotation, null);
          }
          catch (RuntimeException localRuntimeException)
          {
            throw localRuntimeException;
          }
          catch (Exception localException)
          {
            throw new UndeclaredThrowableException(localException);
          }
          Object localObject1 = annotationToField(localObject1);
          Object localObject2 = localHashMap.put(str1, localObject1);
          if ((localObject2 != null) && (!equals(localObject2, localObject1)))
          {
            String str2 = "Inconsistent values for descriptor field " + str1 + " from annotations: " + localObject1 + " :: " + localObject2;
            throw new IllegalArgumentException(str2);
          }
        }
      }
    }
    if (localHashMap.isEmpty()) {
      return ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }
    return new ImmutableDescriptor(localHashMap);
  }
  
  static NotCompliantMBeanException throwException(Class<?> paramClass, Throwable paramThrowable)
    throws NotCompliantMBeanException, SecurityException
  {
    if ((paramThrowable instanceof SecurityException)) {
      throw ((SecurityException)paramThrowable);
    }
    if ((paramThrowable instanceof NotCompliantMBeanException)) {
      throw ((NotCompliantMBeanException)paramThrowable);
    }
    String str1 = paramClass == null ? "null class" : paramClass.getName();
    String str2 = paramThrowable == null ? "Not compliant" : paramThrowable.getMessage();
    NotCompliantMBeanException localNotCompliantMBeanException = new NotCompliantMBeanException(str1 + ": " + str2);
    localNotCompliantMBeanException.initCause(paramThrowable);
    throw localNotCompliantMBeanException;
  }
  
  private static Object annotationToField(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if (((paramObject instanceof Number)) || ((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof Boolean)) || ((paramObject instanceof String[]))) {
      return paramObject;
    }
    Class localClass = paramObject.getClass();
    if (localClass.isArray())
    {
      if (localClass.getComponentType().isPrimitive()) {
        return paramObject;
      }
      Object[] arrayOfObject = (Object[])paramObject;
      String[] arrayOfString = new String[arrayOfObject.length];
      for (int i = 0; i < arrayOfObject.length; i++) {
        arrayOfString[i] = ((String)annotationToField(arrayOfObject[i]));
      }
      return arrayOfString;
    }
    if ((paramObject instanceof Class)) {
      return ((Class)paramObject).getName();
    }
    if ((paramObject instanceof Enum)) {
      return ((Enum)paramObject).name();
    }
    if (Proxy.isProxyClass(localClass)) {
      localClass = localClass.getInterfaces()[0];
    }
    throw new IllegalArgumentException("Illegal type for annotation element using @DescriptorKey: " + localClass.getName());
  }
  
  private static boolean equals(Object paramObject1, Object paramObject2)
  {
    return Arrays.deepEquals(new Object[] { paramObject1 }, new Object[] { paramObject2 });
  }
  
  private static <T> Class<? super T> implementsMBean(Class<T> paramClass, String paramString)
  {
    String str = paramString + "MBean";
    if (paramClass.getName().equals(str)) {
      return paramClass;
    }
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++) {
      if ((arrayOfClass[i].getName().equals(str)) && ((Modifier.isPublic(arrayOfClass[i].getModifiers())) || (ALLOW_NONPUBLIC_MBEAN))) {
        return (Class)Util.cast(arrayOfClass[i]);
      }
    }
    return null;
  }
  
  public static Object elementFromComplex(Object paramObject, String paramString)
    throws AttributeNotFoundException
  {
    try
    {
      if ((paramObject.getClass().isArray()) && (paramString.equals("length"))) {
        return Integer.valueOf(Array.getLength(paramObject));
      }
      if ((paramObject instanceof CompositeData)) {
        return ((CompositeData)paramObject).get(paramString);
      }
      Class localClass = paramObject.getClass();
      Method localMethod = null;
      if (BeansHelper.isAvailable())
      {
        Object localObject1 = BeansHelper.getBeanInfo(localClass);
        Object[] arrayOfObject1 = BeansHelper.getPropertyDescriptors(localObject1);
        for (Object localObject2 : arrayOfObject1) {
          if (BeansHelper.getPropertyName(localObject2).equals(paramString))
          {
            localMethod = BeansHelper.getReadMethod(localObject2);
            break;
          }
        }
      }
      else
      {
        localMethod = SimpleIntrospector.getReadMethod(localClass, paramString);
      }
      if (localMethod != null)
      {
        ReflectUtil.checkPackageAccess(localMethod.getDeclaringClass());
        return MethodUtil.invoke(localMethod, paramObject, new Class[0]);
      }
      throw new AttributeNotFoundException("Could not find the getter method for the property " + paramString + " using the Java Beans introspector");
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new IllegalArgumentException(localInvocationTargetException);
    }
    catch (AttributeNotFoundException localAttributeNotFoundException)
    {
      throw localAttributeNotFoundException;
    }
    catch (Exception localException)
    {
      throw ((AttributeNotFoundException)EnvHelp.initCause(new AttributeNotFoundException(localException.getMessage()), localException));
    }
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.jmx.mbeans.allowNonPublic"));
    ALLOW_NONPUBLIC_MBEAN = Boolean.parseBoolean(str);
  }
  
  private static class BeansHelper
  {
    private static final Class<?> introspectorClass = getClass("java.beans.Introspector");
    private static final Class<?> beanInfoClass = introspectorClass == null ? null : getClass("java.beans.BeanInfo");
    private static final Class<?> getPropertyDescriptorClass = beanInfoClass == null ? null : getClass("java.beans.PropertyDescriptor");
    private static final Method getBeanInfo = getMethod(introspectorClass, "getBeanInfo", new Class[] { Class.class });
    private static final Method getPropertyDescriptors = getMethod(beanInfoClass, "getPropertyDescriptors", new Class[0]);
    private static final Method getPropertyName = getMethod(getPropertyDescriptorClass, "getName", new Class[0]);
    private static final Method getReadMethod = getMethod(getPropertyDescriptorClass, "getReadMethod", new Class[0]);
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, null);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return null;
    }
    
    private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    {
      if (paramClass != null) {
        try
        {
          return paramClass.getMethod(paramString, paramVarArgs);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw new AssertionError(localNoSuchMethodException);
        }
      }
      return null;
    }
    
    private BeansHelper() {}
    
    static boolean isAvailable()
    {
      return introspectorClass != null;
    }
    
    static Object getBeanInfo(Class<?> paramClass)
      throws Exception
    {
      try
      {
        return getBeanInfo.invoke(null, new Object[] { paramClass });
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof Exception)) {
          throw ((Exception)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
    
    static Object[] getPropertyDescriptors(Object paramObject)
    {
      try
      {
        return (Object[])getPropertyDescriptors.invoke(paramObject, new Object[0]);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
    
    static String getPropertyName(Object paramObject)
    {
      try
      {
        return (String)getPropertyName.invoke(paramObject, new Object[0]);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
    
    static Method getReadMethod(Object paramObject)
    {
      try
      {
        return (Method)getReadMethod.invoke(paramObject, new Object[0]);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
  }
  
  private static class SimpleIntrospector
  {
    private static final String GET_METHOD_PREFIX = "get";
    private static final String IS_METHOD_PREFIX = "is";
    private static final Map<Class<?>, SoftReference<List<Method>>> cache = Collections.synchronizedMap(new WeakHashMap());
    
    private SimpleIntrospector() {}
    
    private static List<Method> getCachedMethods(Class<?> paramClass)
    {
      SoftReference localSoftReference = (SoftReference)cache.get(paramClass);
      if (localSoftReference != null)
      {
        List localList = (List)localSoftReference.get();
        if (localList != null) {
          return localList;
        }
      }
      return null;
    }
    
    static boolean isReadMethod(Method paramMethod)
    {
      int i = paramMethod.getModifiers();
      if (Modifier.isStatic(i)) {
        return false;
      }
      String str = paramMethod.getName();
      Class[] arrayOfClass = paramMethod.getParameterTypes();
      int j = arrayOfClass.length;
      if ((j == 0) && (str.length() > 2))
      {
        if (str.startsWith("is")) {
          return paramMethod.getReturnType() == Boolean.TYPE;
        }
        if ((str.length() > 3) && (str.startsWith("get"))) {
          return paramMethod.getReturnType() != Void.TYPE;
        }
      }
      return false;
    }
    
    static List<Method> getReadMethods(Class<?> paramClass)
    {
      List localList1 = getCachedMethods(paramClass);
      if (localList1 != null) {
        return localList1;
      }
      List localList2 = StandardMBeanIntrospector.getInstance().getMethods(paramClass);
      localList2 = MBeanAnalyzer.eliminateCovariantMethods(localList2);
      LinkedList localLinkedList = new LinkedList();
      Iterator localIterator = localList2.iterator();
      while (localIterator.hasNext())
      {
        Method localMethod = (Method)localIterator.next();
        if (isReadMethod(localMethod)) {
          if (localMethod.getName().startsWith("is")) {
            localLinkedList.add(0, localMethod);
          } else {
            localLinkedList.add(localMethod);
          }
        }
      }
      cache.put(paramClass, new SoftReference(localLinkedList));
      return localLinkedList;
    }
    
    static Method getReadMethod(Class<?> paramClass, String paramString)
    {
      paramString = paramString.substring(0, 1).toUpperCase(Locale.ENGLISH) + paramString.substring(1);
      String str1 = "get" + paramString;
      String str2 = "is" + paramString;
      Iterator localIterator = getReadMethods(paramClass).iterator();
      while (localIterator.hasNext())
      {
        Method localMethod = (Method)localIterator.next();
        String str3 = localMethod.getName();
        if ((str3.equals(str2)) || (str3.equals(str1))) {
          return localMethod;
        }
      }
      return null;
    }
  }
}
