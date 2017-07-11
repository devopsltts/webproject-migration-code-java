package sun.applet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.NoSuchElementException;
import sun.awt.AppContext;
import sun.misc.IOUtils;
import sun.net.www.ParseUtil;

public class AppletClassLoader
  extends URLClassLoader
{
  private URL base;
  private CodeSource codesource;
  private AccessControlContext acc;
  private boolean exceptionStatus = false;
  private final Object threadGroupSynchronizer = new Object();
  private final Object grabReleaseSynchronizer = new Object();
  private boolean codebaseLookup = true;
  private volatile boolean allowRecursiveDirectoryRead = true;
  private Object syncResourceAsStream = new Object();
  private Object syncResourceAsStreamFromJar = new Object();
  private boolean resourceAsStreamInCall = false;
  private boolean resourceAsStreamFromJarInCall = false;
  private AppletThreadGroup threadGroup;
  private AppContext appContext;
  int usageCount = 0;
  private HashMap jdk11AppletInfo = new HashMap();
  private HashMap jdk12AppletInfo = new HashMap();
  private static AppletMessageHandler mh = new AppletMessageHandler("appletclassloader");
  
  protected AppletClassLoader(URL paramURL)
  {
    super(new URL[0]);
    this.base = paramURL;
    this.codesource = new CodeSource(paramURL, (Certificate[])null);
    this.acc = AccessController.getContext();
  }
  
  public void disableRecursiveDirectoryRead()
  {
    this.allowRecursiveDirectoryRead = false;
  }
  
  void setCodebaseLookup(boolean paramBoolean)
  {
    this.codebaseLookup = paramBoolean;
  }
  
  URL getBaseURL()
  {
    return this.base;
  }
  
  public URL[] getURLs()
  {
    URL[] arrayOfURL1 = super.getURLs();
    URL[] arrayOfURL2 = new URL[arrayOfURL1.length + 1];
    System.arraycopy(arrayOfURL1, 0, arrayOfURL2, 0, arrayOfURL1.length);
    arrayOfURL2[(arrayOfURL2.length - 1)] = this.base;
    return arrayOfURL2;
  }
  
  protected void addJar(String paramString)
    throws IOException
  {
    URL localURL;
    try
    {
      localURL = new URL(this.base, paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IllegalArgumentException("name");
    }
    addURL(localURL);
  }
  
  public synchronized Class loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    int i = paramString.lastIndexOf('.');
    if (i != -1)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPackageAccess(paramString.substring(0, i));
      }
    }
    try
    {
      return super.loadClass(paramString, paramBoolean);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw localClassNotFoundException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Error localError)
    {
      throw localError;
    }
  }
  
  protected Class findClass(String paramString)
    throws ClassNotFoundException
  {
    int i = paramString.indexOf(";");
    String str1 = "";
    if (i != -1)
    {
      str1 = paramString.substring(i, paramString.length());
      paramString = paramString.substring(0, i);
    }
    try
    {
      return super.findClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (!this.codebaseLookup) {
        throw new ClassNotFoundException(paramString);
      }
      String str2 = ParseUtil.encodePath(paramString.replace('.', '/'), false);
      final String str3 = str2 + ".class" + str1;
      try
      {
        byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws IOException
          {
            try
            {
              URL localURL = new URL(AppletClassLoader.this.base, str3);
              if ((AppletClassLoader.this.base.getProtocol().equals(localURL.getProtocol())) && (AppletClassLoader.this.base.getHost().equals(localURL.getHost())) && (AppletClassLoader.this.base.getPort() == localURL.getPort())) {
                return AppletClassLoader.getBytes(localURL);
              }
              return null;
            }
            catch (Exception localException) {}
            return null;
          }
        }, this.acc);
        if (arrayOfByte != null) {
          return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, this.codesource);
        }
        throw new ClassNotFoundException(paramString);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw new ClassNotFoundException(paramString, localPrivilegedActionException.getException());
      }
    }
  }
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    URL localURL = paramCodeSource.getLocation();
    String str1 = null;
    Permission localPermission1;
    try
    {
      localPermission1 = localURL.openConnection().getPermission();
    }
    catch (IOException localIOException1)
    {
      localPermission1 = null;
    }
    if ((localPermission1 instanceof FilePermission))
    {
      str1 = localPermission1.getName();
    }
    else if ((localPermission1 == null) && (localURL.getProtocol().equals("file")))
    {
      str1 = localURL.getFile().replace('/', File.separatorChar);
      str1 = ParseUtil.decode(str1);
    }
    if (str1 != null)
    {
      String str2 = str1;
      if (!str1.endsWith(File.separator))
      {
        int i = str1.lastIndexOf(File.separatorChar);
        if (i != -1)
        {
          str1 = str1.substring(0, i + 1) + "-";
          localPermissionCollection.add(new FilePermission(str1, "read"));
        }
      }
      File localFile = new File(str2);
      boolean bool = localFile.isDirectory();
      if ((this.allowRecursiveDirectoryRead) && ((bool) || (str2.toLowerCase().endsWith(".jar")) || (str2.toLowerCase().endsWith(".zip"))))
      {
        Permission localPermission2;
        try
        {
          localPermission2 = this.base.openConnection().getPermission();
        }
        catch (IOException localIOException2)
        {
          localPermission2 = null;
        }
        String str3;
        if ((localPermission2 instanceof FilePermission))
        {
          str3 = localPermission2.getName();
          if (str3.endsWith(File.separator)) {
            str3 = str3 + "-";
          }
          localPermissionCollection.add(new FilePermission(str3, "read"));
        }
        else if ((localPermission2 == null) && (this.base.getProtocol().equals("file")))
        {
          str3 = this.base.getFile().replace('/', File.separatorChar);
          str3 = ParseUtil.decode(str3);
          if (str3.endsWith(File.separator)) {
            str3 = str3 + "-";
          }
          localPermissionCollection.add(new FilePermission(str3, "read"));
        }
      }
    }
    return localPermissionCollection;
  }
  
  private static byte[] getBytes(URL paramURL)
    throws IOException
  {
    URLConnection localURLConnection = paramURL.openConnection();
    if ((localURLConnection instanceof HttpURLConnection))
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURLConnection;
      int j = localHttpURLConnection.getResponseCode();
      if (j >= 400) {
        throw new IOException("open HTTP connection failed.");
      }
    }
    int i = localURLConnection.getContentLength();
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream());
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = IOUtils.readFully(localBufferedInputStream, i, true);
    }
    finally
    {
      localBufferedInputStream.close();
    }
    return arrayOfByte;
  }
  
  public InputStream getResourceAsStream(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    try
    {
      InputStream localInputStream = null;
      synchronized (this.syncResourceAsStream)
      {
        this.resourceAsStreamInCall = true;
        localInputStream = super.getResourceAsStream(paramString);
        this.resourceAsStreamInCall = false;
      }
      if ((this.codebaseLookup == true) && (localInputStream == null))
      {
        ??? = new URL(this.base, ParseUtil.encodePath(paramString, false));
        localInputStream = ((URL)???).openStream();
      }
      return localInputStream;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public InputStream getResourceAsStreamFromJar(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    try
    {
      InputStream localInputStream = null;
      synchronized (this.syncResourceAsStreamFromJar)
      {
        this.resourceAsStreamFromJarInCall = true;
        localInputStream = super.getResourceAsStream(paramString);
        this.resourceAsStreamFromJarInCall = false;
      }
      return localInputStream;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public URL findResource(String paramString)
  {
    URL localURL = super.findResource(paramString);
    if (paramString.startsWith("META-INF/")) {
      return localURL;
    }
    if (!this.codebaseLookup) {
      return localURL;
    }
    if (localURL == null)
    {
      boolean bool1 = false;
      synchronized (this.syncResourceAsStreamFromJar)
      {
        bool1 = this.resourceAsStreamFromJarInCall;
      }
      if (bool1) {
        return null;
      }
      boolean bool2 = false;
      synchronized (this.syncResourceAsStream)
      {
        bool2 = this.resourceAsStreamInCall;
      }
      if (!bool2) {
        try
        {
          localURL = new URL(this.base, ParseUtil.encodePath(paramString, false));
          if (!resourceExists(localURL)) {
            localURL = null;
          }
        }
        catch (Exception localException)
        {
          localURL = null;
        }
      }
    }
    return localURL;
  }
  
  private boolean resourceExists(URL paramURL)
  {
    boolean bool = true;
    try
    {
      URLConnection localURLConnection = paramURL.openConnection();
      Object localObject;
      if ((localURLConnection instanceof HttpURLConnection))
      {
        localObject = (HttpURLConnection)localURLConnection;
        ((HttpURLConnection)localObject).setRequestMethod("HEAD");
        int i = ((HttpURLConnection)localObject).getResponseCode();
        if (i == 200) {
          return true;
        }
        if (i >= 400) {
          return false;
        }
      }
      else
      {
        localObject = localURLConnection.getInputStream();
        ((InputStream)localObject).close();
      }
    }
    catch (Exception localException)
    {
      bool = false;
    }
    return bool;
  }
  
  public Enumeration findResources(String paramString)
    throws IOException
  {
    final Enumeration localEnumeration = super.findResources(paramString);
    if (paramString.startsWith("META-INF/")) {
      return localEnumeration;
    }
    if (!this.codebaseLookup) {
      return localEnumeration;
    }
    URL localURL1 = new URL(this.base, ParseUtil.encodePath(paramString, false));
    if (!resourceExists(localURL1)) {
      localURL1 = null;
    }
    final URL localURL2 = localURL1;
    new Enumeration()
    {
      private boolean done;
      
      public Object nextElement()
      {
        if (!this.done)
        {
          if (localEnumeration.hasMoreElements()) {
            return localEnumeration.nextElement();
          }
          this.done = true;
          if (localURL2 != null) {
            return localURL2;
          }
        }
        throw new NoSuchElementException();
      }
      
      public boolean hasMoreElements()
      {
        return (!this.done) && ((localEnumeration.hasMoreElements()) || (localURL2 != null));
      }
    };
  }
  
  Class loadCode(String paramString)
    throws ClassNotFoundException
  {
    paramString = paramString.replace('/', '.');
    paramString = paramString.replace(File.separatorChar, '.');
    String str1 = null;
    int i = paramString.indexOf(";");
    if (i != -1)
    {
      str1 = paramString.substring(i, paramString.length());
      paramString = paramString.substring(0, i);
    }
    String str2 = paramString;
    if ((paramString.endsWith(".class")) || (paramString.endsWith(".java"))) {
      paramString = paramString.substring(0, paramString.lastIndexOf('.'));
    }
    try
    {
      if (str1 != null) {
        paramString = paramString + str1;
      }
      return loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (str1 != null) {
        str2 = str2 + str1;
      }
    }
    return loadClass(str2);
  }
  
  public ThreadGroup getThreadGroup()
  {
    synchronized (this.threadGroupSynchronizer)
    {
      if ((this.threadGroup == null) || (this.threadGroup.isDestroyed())) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            AppletClassLoader.this.threadGroup = new AppletThreadGroup(AppletClassLoader.this.base + "-threadGroup");
            AppContextCreator localAppContextCreator = new AppContextCreator(AppletClassLoader.this.threadGroup);
            localAppContextCreator.setContextClassLoader(AppletClassLoader.this);
            localAppContextCreator.start();
            try
            {
              synchronized (localAppContextCreator.syncObject)
              {
                while (!localAppContextCreator.created) {
                  localAppContextCreator.syncObject.wait();
                }
              }
            }
            catch (InterruptedException localInterruptedException) {}
            AppletClassLoader.this.appContext = localAppContextCreator.appContext;
            return null;
          }
        });
      }
      return this.threadGroup;
    }
  }
  
  public AppContext getAppContext()
  {
    return this.appContext;
  }
  
  public void grab()
  {
    synchronized (this.grabReleaseSynchronizer)
    {
      this.usageCount += 1;
    }
    getThreadGroup();
  }
  
  protected void setExceptionStatus()
  {
    this.exceptionStatus = true;
  }
  
  public boolean getExceptionStatus()
  {
    return this.exceptionStatus;
  }
  
  protected void release()
  {
    AppContext localAppContext = null;
    synchronized (this.grabReleaseSynchronizer)
    {
      if (this.usageCount > 1) {
        this.usageCount -= 1;
      } else {
        synchronized (this.threadGroupSynchronizer)
        {
          localAppContext = resetAppContext();
        }
      }
    }
    if (localAppContext != null) {
      try
      {
        localAppContext.dispose();
      }
      catch (IllegalThreadStateException localIllegalThreadStateException) {}
    }
  }
  
  protected AppContext resetAppContext()
  {
    AppContext localAppContext = null;
    synchronized (this.threadGroupSynchronizer)
    {
      localAppContext = this.appContext;
      this.usageCount = 0;
      this.appContext = null;
      this.threadGroup = null;
    }
    return localAppContext;
  }
  
  void setJDK11Target(Class paramClass, boolean paramBoolean)
  {
    this.jdk11AppletInfo.put(paramClass.toString(), Boolean.valueOf(paramBoolean));
  }
  
  void setJDK12Target(Class paramClass, boolean paramBoolean)
  {
    this.jdk12AppletInfo.put(paramClass.toString(), Boolean.valueOf(paramBoolean));
  }
  
  Boolean isJDK11Target(Class paramClass)
  {
    return (Boolean)this.jdk11AppletInfo.get(paramClass.toString());
  }
  
  Boolean isJDK12Target(Class paramClass)
  {
    return (Boolean)this.jdk12AppletInfo.get(paramClass.toString());
  }
  
  private static void printError(String paramString, Throwable paramThrowable)
  {
    String str = null;
    if (paramThrowable == null) {
      str = mh.getMessage("filenotfound", paramString);
    } else if ((paramThrowable instanceof IOException)) {
      str = mh.getMessage("fileioexception", paramString);
    } else if ((paramThrowable instanceof ClassFormatError)) {
      str = mh.getMessage("fileformat", paramString);
    } else if ((paramThrowable instanceof ThreadDeath)) {
      str = mh.getMessage("filedeath", paramString);
    } else if ((paramThrowable instanceof Error)) {
      str = mh.getMessage("fileerror", paramThrowable.toString(), paramString);
    }
    if (str != null) {
      System.err.println(str);
    }
  }
}
