package javax.xml.transform;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder
{
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xalan.internal.";
  private static boolean debug;
  private static final Properties cacheProps;
  static volatile boolean firstTime;
  private static final SecuritySupport ss;
  
  FactoryFinder() {}
  
  private static void dPrint(String paramString)
  {
    if (debug) {
      System.err.println("JAXP: " + paramString);
    }
  }
  
  private static Class<?> getProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2)
    throws ClassNotFoundException
  {
    try
    {
      if (paramClassLoader == null)
      {
        if (paramBoolean2) {
          return Class.forName(paramString, false, FactoryFinder.class.getClassLoader());
        }
        paramClassLoader = ss.getContextClassLoader();
        if (paramClassLoader == null) {
          throw new ClassNotFoundException();
        }
        return Class.forName(paramString, false, paramClassLoader);
      }
      return Class.forName(paramString, false, paramClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (paramBoolean1) {
        return Class.forName(paramString, false, FactoryFinder.class.getClassLoader());
      }
      throw localClassNotFoundException;
    }
  }
  
  static <T> T newInstance(Class<T> paramClass, String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2)
    throws TransformerFactoryConfigurationError
  {
    assert (paramClass != null);
    boolean bool = false;
    if ((System.getSecurityManager() != null) && (paramString != null) && (paramString.startsWith("com.sun.org.apache.xalan.internal.")))
    {
      paramClassLoader = null;
      bool = true;
    }
    try
    {
      Class localClass = getProviderClass(paramString, paramClassLoader, paramBoolean1, bool);
      if (!paramClass.isAssignableFrom(localClass)) {
        throw new ClassCastException(paramString + " cannot be cast to " + paramClass.getName());
      }
      Object localObject = null;
      if (!paramBoolean2) {
        localObject = newInstanceNoServiceLoader(paramClass, localClass);
      }
      if (localObject == null) {
        localObject = localClass.newInstance();
      }
      if (debug) {
        dPrint("created new instance of " + localClass + " using ClassLoader: " + paramClassLoader);
      }
      return paramClass.cast(localObject);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new TransformerFactoryConfigurationError(localClassNotFoundException, "Provider " + paramString + " not found");
    }
    catch (Exception localException)
    {
      throw new TransformerFactoryConfigurationError(localException, "Provider " + paramString + " could not be instantiated: " + localException);
    }
  }
  
  private static <T> T newInstanceNoServiceLoader(Class<T> paramClass, Class<?> paramClass1)
  {
    if (System.getSecurityManager() == null) {
      return null;
    }
    try
    {
      Method localMethod = paramClass1.getDeclaredMethod("newTransformerFactoryNoServiceLoader", new Class[0]);
      int i = localMethod.getModifiers();
      if ((!Modifier.isPublic(i)) || (!Modifier.isStatic(i))) {
        return null;
      }
      Class localClass = localMethod.getReturnType();
      if (paramClass.isAssignableFrom(localClass))
      {
        Object localObject = localMethod.invoke(null, (Object[])null);
        return paramClass.cast(localObject);
      }
      throw new ClassCastException(localClass + " cannot be cast to " + paramClass);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new TransformerFactoryConfigurationError(localClassCastException, localClassCastException.getMessage());
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    catch (Exception localException) {}
    return null;
  }
  
  static <T> T find(Class<T> paramClass, String paramString)
    throws TransformerFactoryConfigurationError
  {
    assert (paramClass != null);
    String str1 = paramClass.getName();
    dPrint("find factoryId =" + str1);
    try
    {
      String str2 = ss.getSystemProperty(str1);
      if (str2 != null)
      {
        dPrint("found system property, value=" + str2);
        return newInstance(paramClass, str2, null, true, true);
      }
    }
    catch (SecurityException localSecurityException)
    {
      if (debug) {
        localSecurityException.printStackTrace();
      }
    }
    try
    {
      if (firstTime) {
        synchronized (cacheProps)
        {
          if (firstTime)
          {
            String str3 = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File localFile = new File(str3);
            firstTime = false;
            if (ss.doesFileExist(localFile))
            {
              dPrint("Read properties file " + localFile);
              cacheProps.load(ss.getFileInputStream(localFile));
            }
          }
        }
      }
      ??? = cacheProps.getProperty(str1);
      if (??? != null)
      {
        dPrint("found in $java.home/jaxp.properties, value=" + (String)???);
        return newInstance(paramClass, (String)???, null, true, true);
      }
    }
    catch (Exception localException)
    {
      if (debug) {
        localException.printStackTrace();
      }
    }
    Object localObject1 = findServiceProvider(paramClass);
    if (localObject1 != null) {
      return localObject1;
    }
    if (paramString == null) {
      throw new TransformerFactoryConfigurationError(null, "Provider for " + str1 + " cannot be found");
    }
    dPrint("loaded from fallback value: " + paramString);
    return newInstance(paramClass, paramString, null, true, true);
  }
  
  private static <T> T findServiceProvider(Class<T> paramClass)
    throws TransformerFactoryConfigurationError
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public T run()
        {
          ServiceLoader localServiceLoader = ServiceLoader.load(this.val$type);
          Iterator localIterator = localServiceLoader.iterator();
          if (localIterator.hasNext()) {
            return localIterator.next();
          }
          return null;
        }
      });
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      RuntimeException localRuntimeException = new RuntimeException("Provider for " + paramClass + " cannot be created", localServiceConfigurationError);
      TransformerFactoryConfigurationError localTransformerFactoryConfigurationError = new TransformerFactoryConfigurationError(localRuntimeException, localRuntimeException.getMessage());
      throw localTransformerFactoryConfigurationError;
    }
  }
  
  static
  {
    debug = false;
    cacheProps = new Properties();
    firstTime = true;
    ss = new SecuritySupport();
    try
    {
      String str = ss.getSystemProperty("jaxp.debug");
      debug = (str != null) && (!"false".equals(str));
    }
    catch (SecurityException localSecurityException)
    {
      debug = false;
    }
  }
}
