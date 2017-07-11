package javax.xml.xpath;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class XPathFactoryFinder
{
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xpath.internal";
  private static final SecuritySupport ss;
  private static boolean debug;
  private static final Properties cacheProps = new Properties();
  private static volatile boolean firstTime = true;
  private final ClassLoader classLoader;
  private static final Class<XPathFactory> SERVICE_CLASS = XPathFactory.class;
  
  private static void debugPrintln(String paramString)
  {
    if (debug) {
      System.err.println("JAXP: " + paramString);
    }
  }
  
  public XPathFactoryFinder(ClassLoader paramClassLoader)
  {
    this.classLoader = paramClassLoader;
    if (debug) {
      debugDisplayClassLoader();
    }
  }
  
  private void debugDisplayClassLoader()
  {
    try
    {
      if (this.classLoader == ss.getContextClassLoader())
      {
        debugPrintln("using thread context class loader (" + this.classLoader + ") for search");
        return;
      }
    }
    catch (Throwable localThrowable) {}
    if (this.classLoader == ClassLoader.getSystemClassLoader())
    {
      debugPrintln("using system class loader (" + this.classLoader + ") for search");
      return;
    }
    debugPrintln("using class loader (" + this.classLoader + ") for search");
  }
  
  public XPathFactory newFactory(String paramString)
    throws XPathFactoryConfigurationException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    XPathFactory localXPathFactory = _newFactory(paramString);
    if (localXPathFactory != null) {
      debugPrintln("factory '" + localXPathFactory.getClass().getName() + "' was found for " + paramString);
    } else {
      debugPrintln("unable to find a factory for " + paramString);
    }
    return localXPathFactory;
  }
  
  private XPathFactory _newFactory(String paramString)
    throws XPathFactoryConfigurationException
  {
    XPathFactory localXPathFactory = null;
    String str1 = SERVICE_CLASS.getName() + ":" + paramString;
    try
    {
      debugPrintln("Looking up system property '" + str1 + "'");
      String str2 = ss.getSystemProperty(str1);
      if (str2 != null)
      {
        debugPrintln("The value is '" + str2 + "'");
        localXPathFactory = createInstance(str2, true);
        if (localXPathFactory != null) {
          return localXPathFactory;
        }
      }
      else
      {
        debugPrintln("The property is undefined.");
      }
    }
    catch (Throwable localThrowable)
    {
      if (debug)
      {
        debugPrintln("failed to look up system property '" + str1 + "'");
        localThrowable.printStackTrace();
      }
    }
    String str3 = ss.getSystemProperty("java.home");
    String str4 = str3 + File.separator + "lib" + File.separator + "jaxp.properties";
    try
    {
      if (firstTime) {
        synchronized (cacheProps)
        {
          if (firstTime)
          {
            File localFile = new File(str4);
            firstTime = false;
            if (ss.doesFileExist(localFile))
            {
              debugPrintln("Read properties file " + localFile);
              cacheProps.load(ss.getFileInputStream(localFile));
            }
          }
        }
      }
      ??? = cacheProps.getProperty(str1);
      debugPrintln("found " + (String)??? + " in $java.home/jaxp.properties");
      if (??? != null)
      {
        localXPathFactory = createInstance((String)???, true);
        if (localXPathFactory != null) {
          return localXPathFactory;
        }
      }
    }
    catch (Exception localException)
    {
      if (debug) {
        localException.printStackTrace();
      }
    }
    assert (localXPathFactory == null);
    localXPathFactory = findServiceProvider(paramString);
    if (localXPathFactory != null) {
      return localXPathFactory;
    }
    if (paramString.equals("http://java.sun.com/jaxp/xpath/dom"))
    {
      debugPrintln("attempting to use the platform default W3C DOM XPath lib");
      return createInstance("com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl", true);
    }
    debugPrintln("all things were tried, but none was found. bailing out.");
    return null;
  }
  
  private Class<?> createClass(String paramString)
  {
    int i = 0;
    if ((System.getSecurityManager() != null) && (paramString != null) && (paramString.startsWith("com.sun.org.apache.xpath.internal"))) {
      i = 1;
    }
    Class localClass;
    try
    {
      if ((this.classLoader != null) && (i == 0)) {
        localClass = Class.forName(paramString, false, this.classLoader);
      } else {
        localClass = Class.forName(paramString);
      }
    }
    catch (Throwable localThrowable)
    {
      if (debug) {
        localThrowable.printStackTrace();
      }
      return null;
    }
    return localClass;
  }
  
  XPathFactory createInstance(String paramString)
    throws XPathFactoryConfigurationException
  {
    return createInstance(paramString, false);
  }
  
  XPathFactory createInstance(String paramString, boolean paramBoolean)
    throws XPathFactoryConfigurationException
  {
    XPathFactory localXPathFactory = null;
    debugPrintln("createInstance(" + paramString + ")");
    Class localClass = createClass(paramString);
    if (localClass == null)
    {
      debugPrintln("failed to getClass(" + paramString + ")");
      return null;
    }
    debugPrintln("loaded " + paramString + " from " + which(localClass));
    try
    {
      if (!paramBoolean) {
        localXPathFactory = newInstanceNoServiceLoader(localClass);
      }
      if (localXPathFactory == null) {
        localXPathFactory = (XPathFactory)localClass.newInstance();
      }
    }
    catch (ClassCastException localClassCastException)
    {
      debugPrintln("could not instantiate " + localClass.getName());
      if (debug) {
        localClassCastException.printStackTrace();
      }
      return null;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      debugPrintln("could not instantiate " + localClass.getName());
      if (debug) {
        localIllegalAccessException.printStackTrace();
      }
      return null;
    }
    catch (InstantiationException localInstantiationException)
    {
      debugPrintln("could not instantiate " + localClass.getName());
      if (debug) {
        localInstantiationException.printStackTrace();
      }
      return null;
    }
    return localXPathFactory;
  }
  
  private static XPathFactory newInstanceNoServiceLoader(Class<?> paramClass)
    throws XPathFactoryConfigurationException
  {
    if (System.getSecurityManager() == null) {
      return null;
    }
    try
    {
      Method localMethod = paramClass.getDeclaredMethod("newXPathFactoryNoServiceLoader", new Class[0]);
      int i = localMethod.getModifiers();
      if ((!Modifier.isStatic(i)) || (!Modifier.isPublic(i))) {
        return null;
      }
      Class localClass = localMethod.getReturnType();
      if (SERVICE_CLASS.isAssignableFrom(localClass)) {
        return (XPathFactory)SERVICE_CLASS.cast(localMethod.invoke(null, (Object[])null));
      }
      throw new ClassCastException(localClass + " cannot be cast to " + SERVICE_CLASS);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new XPathFactoryConfigurationException(localClassCastException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    catch (Exception localException) {}
    return null;
  }
  
  private boolean isObjectModelSupportedBy(final XPathFactory paramXPathFactory, final String paramString, AccessControlContext paramAccessControlContext)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(paramXPathFactory.isObjectModelSupported(paramString));
      }
    }, paramAccessControlContext)).booleanValue();
  }
  
  private XPathFactory findServiceProvider(final String paramString)
    throws XPathFactoryConfigurationException
  {
    assert (paramString != null);
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    try
    {
      (XPathFactory)AccessController.doPrivileged(new PrivilegedAction()
      {
        public XPathFactory run()
        {
          ServiceLoader localServiceLoader = ServiceLoader.load(XPathFactoryFinder.SERVICE_CLASS);
          Iterator localIterator = localServiceLoader.iterator();
          while (localIterator.hasNext())
          {
            XPathFactory localXPathFactory = (XPathFactory)localIterator.next();
            if (XPathFactoryFinder.this.isObjectModelSupportedBy(localXPathFactory, paramString, localAccessControlContext)) {
              return localXPathFactory;
            }
          }
          return null;
        }
      });
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      throw new XPathFactoryConfigurationException(localServiceConfigurationError);
    }
  }
  
  private static String which(Class paramClass)
  {
    return which(paramClass.getName(), paramClass.getClassLoader());
  }
  
  private static String which(String paramString, ClassLoader paramClassLoader)
  {
    String str = paramString.replace('.', '/') + ".class";
    if (paramClassLoader == null) {
      paramClassLoader = ClassLoader.getSystemClassLoader();
    }
    URL localURL = ss.getResourceAsURL(paramClassLoader, str);
    if (localURL != null) {
      return localURL.toString();
    }
    return null;
  }
  
  static
  {
    ss = new SecuritySupport();
    debug = false;
    try
    {
      debug = ss.getSystemProperty("jaxp.debug") != null;
    }
    catch (Exception localException)
    {
      debug = false;
    }
  }
}
