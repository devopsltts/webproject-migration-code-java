package javax.tools;

import java.io.File;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolProvider
{
  private static final String propertyName = "sun.tools.ToolProvider";
  private static final String loggerName = "javax.tools";
  private static final String defaultJavaCompilerName = "com.sun.tools.javac.api.JavacTool";
  private static final String defaultDocumentationToolName = "com.sun.tools.javadoc.api.JavadocTool";
  private static ToolProvider instance;
  private Map<String, Reference<Class<?>>> toolClasses = new HashMap();
  private Reference<ClassLoader> refToolClassLoader = null;
  private static final String[] defaultToolsLocation = { "lib", "tools.jar" };
  
  static <T> T trace(Level paramLevel, Object paramObject)
  {
    try
    {
      if (System.getProperty("sun.tools.ToolProvider") != null)
      {
        StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
        String str1 = "???";
        String str2 = ToolProvider.class.getName();
        if (arrayOfStackTraceElement.length > 2)
        {
          localObject = arrayOfStackTraceElement[2];
          str1 = String.format((Locale)null, "%s(%s:%s)", new Object[] { ((StackTraceElement)localObject).getMethodName(), ((StackTraceElement)localObject).getFileName(), Integer.valueOf(((StackTraceElement)localObject).getLineNumber()) });
          str2 = ((StackTraceElement)localObject).getClassName();
        }
        Object localObject = Logger.getLogger("javax.tools");
        if ((paramObject instanceof Throwable)) {
          ((Logger)localObject).logp(paramLevel, str2, str1, paramObject.getClass().getName(), (Throwable)paramObject);
        } else {
          ((Logger)localObject).logp(paramLevel, str2, str1, String.valueOf(paramObject));
        }
      }
    }
    catch (SecurityException localSecurityException)
    {
      System.err.format((Locale)null, "%s: %s; %s%n", new Object[] { ToolProvider.class.getName(), paramObject, localSecurityException.getLocalizedMessage() });
    }
    return null;
  }
  
  public static JavaCompiler getSystemJavaCompiler()
  {
    return (JavaCompiler)instance().getSystemTool(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool");
  }
  
  public static DocumentationTool getSystemDocumentationTool()
  {
    return (DocumentationTool)instance().getSystemTool(DocumentationTool.class, "com.sun.tools.javadoc.api.JavadocTool");
  }
  
  public static ClassLoader getSystemToolClassLoader()
  {
    try
    {
      Class localClass = instance().getSystemToolClass(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool");
      return localClass.getClassLoader();
    }
    catch (Throwable localThrowable)
    {
      return (ClassLoader)trace(Level.WARNING, localThrowable);
    }
  }
  
  private static synchronized ToolProvider instance()
  {
    if (instance == null) {
      instance = new ToolProvider();
    }
    return instance;
  }
  
  private ToolProvider() {}
  
  private <T> T getSystemTool(Class<T> paramClass, String paramString)
  {
    Class localClass = getSystemToolClass(paramClass, paramString);
    try
    {
      return localClass.asSubclass(paramClass).newInstance();
    }
    catch (Throwable localThrowable)
    {
      trace(Level.WARNING, localThrowable);
    }
    return null;
  }
  
  private <T> Class<? extends T> getSystemToolClass(Class<T> paramClass, String paramString)
  {
    Reference localReference = (Reference)this.toolClasses.get(paramString);
    Class localClass = localReference == null ? null : (Class)localReference.get();
    if (localClass == null)
    {
      try
      {
        localClass = findSystemToolClass(paramString);
      }
      catch (Throwable localThrowable)
      {
        return (Class)trace(Level.WARNING, localThrowable);
      }
      this.toolClasses.put(paramString, new WeakReference(localClass));
    }
    return localClass.asSubclass(paramClass);
  }
  
  private Class<?> findSystemToolClass(String paramString)
    throws MalformedURLException, ClassNotFoundException
  {
    try
    {
      return Class.forName(paramString, false, null);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      trace(Level.FINE, localClassNotFoundException);
      Object localObject1 = this.refToolClassLoader == null ? null : (ClassLoader)this.refToolClassLoader.get();
      if (localObject1 == null)
      {
        File localFile = new File(System.getProperty("java.home"));
        if (localFile.getName().equalsIgnoreCase("jre")) {
          localFile = localFile.getParentFile();
        }
        for (String str : defaultToolsLocation) {
          localFile = new File(localFile, str);
        }
        if (!localFile.exists()) {
          throw localClassNotFoundException;
        }
        ??? = new URL[] { localFile.toURI().toURL() };
        trace(Level.FINE, ???[0].toString());
        localObject1 = URLClassLoader.newInstance((URL[])???);
        this.refToolClassLoader = new WeakReference(localObject1);
      }
      return Class.forName(paramString, false, (ClassLoader)localObject1);
    }
  }
}
