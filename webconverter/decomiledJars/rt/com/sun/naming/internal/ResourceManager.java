package com.sun.naming.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public final class ResourceManager
{
  private static final String PROVIDER_RESOURCE_FILE_NAME = "jndiprovider.properties";
  private static final String APP_RESOURCE_FILE_NAME = "jndi.properties";
  private static final String JRELIB_PROPERTY_FILE_NAME = "jndi.properties";
  private static final String DISABLE_APP_RESOURCE_FILES = "com.sun.naming.disable.app.resource.files";
  private static final String[] listProperties = { "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.factory.control" };
  private static final VersionHelper helper = VersionHelper.getVersionHelper();
  private static final WeakHashMap<Object, Hashtable<? super String, Object>> propertiesCache = new WeakHashMap(11);
  private static final WeakHashMap<ClassLoader, Map<String, List<NamedWeakReference<Object>>>> factoryCache = new WeakHashMap(11);
  private static final WeakHashMap<ClassLoader, Map<String, WeakReference<Object>>> urlFactoryCache = new WeakHashMap(11);
  private static final WeakReference<Object> NO_FACTORY = new WeakReference(null);
  
  private ResourceManager() {}
  
  public static Hashtable<?, ?> getInitialEnvironment(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    String[] arrayOfString1 = VersionHelper.PROPS;
    if (paramHashtable == null) {
      paramHashtable = new Hashtable(11);
    }
    Object localObject1 = paramHashtable.get("java.naming.applet");
    String[] arrayOfString2 = helper.getJndiProperties();
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      Object localObject2 = paramHashtable.get(arrayOfString1[i]);
      if (localObject2 == null)
      {
        if (localObject1 != null) {
          localObject2 = AppletParameter.get(localObject1, arrayOfString1[i]);
        }
        if (localObject2 == null) {
          localObject2 = arrayOfString2 != null ? arrayOfString2[i] : helper.getJndiProperty(i);
        }
        if (localObject2 != null) {
          paramHashtable.put(arrayOfString1[i], localObject2);
        }
      }
    }
    String str = (String)paramHashtable.get("com.sun.naming.disable.app.resource.files");
    if ((str != null) && (str.equalsIgnoreCase("true"))) {
      return paramHashtable;
    }
    mergeTables(paramHashtable, getApplicationResources());
    return paramHashtable;
  }
  
  public static String getProperty(String paramString, Hashtable<?, ?> paramHashtable, Context paramContext, boolean paramBoolean)
    throws NamingException
  {
    String str1 = paramHashtable != null ? (String)paramHashtable.get(paramString) : null;
    if ((paramContext == null) || ((str1 != null) && (!paramBoolean))) {
      return str1;
    }
    String str2 = (String)getProviderResource(paramContext).get(paramString);
    if (str1 == null) {
      return str2;
    }
    if ((str2 == null) || (!paramBoolean)) {
      return str1;
    }
    return str1 + ":" + str2;
  }
  
  public static FactoryEnumeration getFactories(String paramString, Hashtable<?, ?> paramHashtable, Context paramContext)
    throws NamingException
  {
    String str1 = getProperty(paramString, paramHashtable, paramContext, true);
    if (str1 == null) {
      return null;
    }
    ClassLoader localClassLoader = helper.getContextClassLoader();
    Object localObject1 = null;
    synchronized (factoryCache)
    {
      localObject1 = (Map)factoryCache.get(localClassLoader);
      if (localObject1 == null)
      {
        localObject1 = new HashMap(11);
        factoryCache.put(localClassLoader, localObject1);
      }
    }
    synchronized (localObject1)
    {
      Object localObject3 = (List)((Map)localObject1).get(str1);
      if (localObject3 != null) {
        return ((List)localObject3).size() == 0 ? null : new FactoryEnumeration((List)localObject3, localClassLoader);
      }
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, ":");
      localObject3 = new ArrayList(5);
      while (localStringTokenizer.hasMoreTokens()) {
        try
        {
          String str2 = localStringTokenizer.nextToken();
          Class localClass = helper.loadClass(str2, localClassLoader);
          ((List)localObject3).add(new NamedWeakReference(localClass, str2));
        }
        catch (Exception localException) {}
      }
      ((Map)localObject1).put(str1, localObject3);
      return new FactoryEnumeration((List)localObject3, localClassLoader);
    }
  }
  
  public static Object getFactory(String paramString1, Hashtable<?, ?> paramHashtable, Context paramContext, String paramString2, String paramString3)
    throws NamingException
  {
    String str1 = getProperty(paramString1, paramHashtable, paramContext, true);
    if (str1 != null) {
      str1 = str1 + ":" + paramString3;
    } else {
      str1 = paramString3;
    }
    ClassLoader localClassLoader = helper.getContextClassLoader();
    String str2 = paramString2 + " " + str1;
    Object localObject1 = null;
    synchronized (urlFactoryCache)
    {
      localObject1 = (Map)urlFactoryCache.get(localClassLoader);
      if (localObject1 == null)
      {
        localObject1 = new HashMap(11);
        urlFactoryCache.put(localClassLoader, localObject1);
      }
    }
    synchronized (localObject1)
    {
      Object localObject3 = null;
      WeakReference localWeakReference = (WeakReference)((Map)localObject1).get(str2);
      if (localWeakReference == NO_FACTORY) {
        return null;
      }
      if (localWeakReference != null)
      {
        localObject3 = localWeakReference.get();
        if (localObject3 != null) {
          return localObject3;
        }
      }
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, ":");
      while ((localObject3 == null) && (localStringTokenizer.hasMoreTokens()))
      {
        String str3 = localStringTokenizer.nextToken() + paramString2;
        try
        {
          localObject3 = helper.loadClass(str3, localClassLoader).newInstance();
        }
        catch (InstantiationException localInstantiationException)
        {
          localNamingException = new NamingException("Cannot instantiate " + str3);
          localNamingException.setRootCause(localInstantiationException);
          throw localNamingException;
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          NamingException localNamingException = new NamingException("Cannot access " + str3);
          localNamingException.setRootCause(localIllegalAccessException);
          throw localNamingException;
        }
        catch (Exception localException) {}
      }
      ((Map)localObject1).put(str2, localObject3 != null ? new WeakReference(localObject3) : NO_FACTORY);
      return localObject3;
    }
  }
  
  private static Hashtable<? super String, Object> getProviderResource(Object paramObject)
    throws NamingException
  {
    if (paramObject == null) {
      return new Hashtable(1);
    }
    synchronized (propertiesCache)
    {
      Class localClass = paramObject.getClass();
      Object localObject1 = (Hashtable)propertiesCache.get(localClass);
      if (localObject1 != null) {
        return localObject1;
      }
      localObject1 = new Properties();
      InputStream localInputStream = helper.getResourceAsStream(localClass, "jndiprovider.properties");
      if (localInputStream != null) {
        try
        {
          ((Properties)localObject1).load(localInputStream);
        }
        catch (IOException localIOException)
        {
          ConfigurationException localConfigurationException = new ConfigurationException("Error reading provider resource file for " + localClass);
          localConfigurationException.setRootCause(localIOException);
          throw localConfigurationException;
        }
      }
      propertiesCache.put(localClass, localObject1);
      return localObject1;
    }
  }
  
  private static Hashtable<? super String, Object> getApplicationResources()
    throws NamingException
  {
    ClassLoader localClassLoader = helper.getContextClassLoader();
    synchronized (propertiesCache)
    {
      Object localObject1 = (Hashtable)propertiesCache.get(localClassLoader);
      if (localObject1 != null) {
        return localObject1;
      }
      try
      {
        NamingEnumeration localNamingEnumeration = helper.getResources(localClassLoader, "jndi.properties");
        Object localObject3;
        try
        {
          while (localNamingEnumeration.hasMore())
          {
            localObject2 = new Properties();
            localObject3 = (InputStream)localNamingEnumeration.next();
            try
            {
              ((Properties)localObject2).load((InputStream)localObject3);
            }
            finally
            {
              ((InputStream)localObject3).close();
            }
            if (localObject1 == null) {
              localObject1 = localObject2;
            } else {
              mergeTables((Hashtable)localObject1, (Hashtable)localObject2);
            }
          }
          while (localNamingEnumeration.hasMore()) {
            ((InputStream)localNamingEnumeration.next()).close();
          }
          localObject2 = helper.getJavaHomeLibStream("jndi.properties");
        }
        finally
        {
          while (localNamingEnumeration.hasMore()) {
            ((InputStream)localNamingEnumeration.next()).close();
          }
        }
        if (localObject2 != null) {
          try
          {
            localObject3 = new Properties();
            ((Properties)localObject3).load((InputStream)localObject2);
            if (localObject1 == null) {
              localObject1 = localObject3;
            } else {
              mergeTables((Hashtable)localObject1, (Hashtable)localObject3);
            }
          }
          finally
          {
            ((InputStream)localObject2).close();
          }
        }
      }
      catch (IOException localIOException)
      {
        Object localObject2 = new ConfigurationException("Error reading application resource file");
        ((NamingException)localObject2).setRootCause(localIOException);
        throw ((Throwable)localObject2);
      }
      if (localObject1 == null) {
        localObject1 = new Hashtable(11);
      }
      propertiesCache.put(localClassLoader, localObject1);
      return localObject1;
    }
  }
  
  private static void mergeTables(Hashtable<? super String, Object> paramHashtable1, Hashtable<? super String, Object> paramHashtable2)
  {
    Iterator localIterator = paramHashtable2.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = localIterator.next();
      String str1 = (String)localObject1;
      Object localObject2 = paramHashtable1.get(str1);
      if (localObject2 == null)
      {
        paramHashtable1.put(str1, paramHashtable2.get(str1));
      }
      else if (isListProperty(str1))
      {
        String str2 = (String)paramHashtable2.get(str1);
        paramHashtable1.put(str1, (String)localObject2 + ":" + str2);
      }
    }
  }
  
  private static boolean isListProperty(String paramString)
  {
    paramString = paramString.intern();
    for (int i = 0; i < listProperties.length; i++) {
      if (paramString == listProperties[i]) {
        return true;
      }
    }
    return false;
  }
  
  private static class AppletParameter
  {
    private static final Class<?> clazz = getClass("java.applet.Applet");
    private static final Method getMethod = getMethod(clazz, "getParameter", new Class[] { String.class });
    
    private AppletParameter() {}
    
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
    
    static Object get(Object paramObject, String paramString)
    {
      if ((clazz == null) || (!clazz.isInstance(paramObject))) {
        throw new ClassCastException(paramObject.getClass().getName());
      }
      try
      {
        return getMethod.invoke(paramObject, new Object[] { paramString });
      }
      catch (InvocationTargetException|IllegalAccessException localInvocationTargetException)
      {
        throw new AssertionError(localInvocationTargetException);
      }
    }
  }
}
