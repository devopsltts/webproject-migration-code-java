package sun.reflect.misc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import sun.misc.IOUtils;

public final class MethodUtil
  extends SecureClassLoader
{
  private static final String MISC_PKG = "sun.reflect.misc.";
  private static final String TRAMPOLINE = "sun.reflect.misc.Trampoline";
  private static final Method bounce = ;
  
  private MethodUtil() {}
  
  public static Method getMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getMethod(paramString, paramArrayOfClass);
  }
  
  public static Method[] getMethods(Class<?> paramClass)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getMethods();
  }
  
  public static Method[] getPublicMethods(Class<?> paramClass)
  {
    if (System.getSecurityManager() == null) {
      return paramClass.getMethods();
    }
    HashMap localHashMap = new HashMap();
    while (paramClass != null)
    {
      boolean bool = getInternalPublicMethods(paramClass, localHashMap);
      if (bool) {
        break;
      }
      getInterfaceMethods(paramClass, localHashMap);
      paramClass = paramClass.getSuperclass();
    }
    return (Method[])localHashMap.values().toArray(new Method[localHashMap.size()]);
  }
  
  private static void getInterfaceMethods(Class<?> paramClass, Map<Signature, Method> paramMap)
  {
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      boolean bool = getInternalPublicMethods(localClass, paramMap);
      if (!bool) {
        getInterfaceMethods(localClass, paramMap);
      }
    }
  }
  
  private static boolean getInternalPublicMethods(Class<?> paramClass, Map<Signature, Method> paramMap)
  {
    Method[] arrayOfMethod = null;
    try
    {
      if (!Modifier.isPublic(paramClass.getModifiers())) {
        return false;
      }
      if (!ReflectUtil.isPackageAccessible(paramClass)) {
        return false;
      }
      arrayOfMethod = paramClass.getMethods();
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    boolean bool = true;
    Class localClass;
    for (int i = 0; i < arrayOfMethod.length; i++)
    {
      localClass = arrayOfMethod[i].getDeclaringClass();
      if (!Modifier.isPublic(localClass.getModifiers()))
      {
        bool = false;
        break;
      }
    }
    if (bool) {
      for (i = 0; i < arrayOfMethod.length; i++) {
        addMethod(paramMap, arrayOfMethod[i]);
      }
    } else {
      for (i = 0; i < arrayOfMethod.length; i++)
      {
        localClass = arrayOfMethod[i].getDeclaringClass();
        if (paramClass.equals(localClass)) {
          addMethod(paramMap, arrayOfMethod[i]);
        }
      }
    }
    return bool;
  }
  
  private static void addMethod(Map<Signature, Method> paramMap, Method paramMethod)
  {
    Signature localSignature = new Signature(paramMethod);
    if (!paramMap.containsKey(localSignature))
    {
      paramMap.put(localSignature, paramMethod);
    }
    else if (!paramMethod.getDeclaringClass().isInterface())
    {
      Method localMethod = (Method)paramMap.get(localSignature);
      if (localMethod.getDeclaringClass().isInterface()) {
        paramMap.put(localSignature, paramMethod);
      }
    }
  }
  
  public static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException
  {
    try
    {
      return bounce.invoke(null, new Object[] { paramMethod, paramObject, paramArrayOfObject });
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getCause();
      if ((localThrowable instanceof InvocationTargetException)) {
        throw ((InvocationTargetException)localThrowable);
      }
      if ((localThrowable instanceof IllegalAccessException)) {
        throw ((IllegalAccessException)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new Error("Unexpected invocation error", localThrowable);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Error("Unexpected invocation error", localIllegalAccessException);
    }
  }
  
  private static Method getTrampoline()
  {
    try
    {
      (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Method run()
          throws Exception
        {
          Class localClass = MethodUtil.access$000();
          Class[] arrayOfClass = { Method.class, Object.class, [Ljava.lang.Object.class };
          Method localMethod = localClass.getDeclaredMethod("invoke", arrayOfClass);
          localMethod.setAccessible(true);
          return localMethod;
        }
      });
    }
    catch (Exception localException)
    {
      throw new InternalError("bouncer cannot be found", localException);
    }
  }
  
  protected synchronized Class<?> loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    Class localClass = findLoadedClass(paramString);
    if (localClass == null)
    {
      try
      {
        localClass = findClass(paramString);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      if (localClass == null) {
        localClass = getParent().loadClass(paramString);
      }
    }
    if (paramBoolean) {
      resolveClass(localClass);
    }
    return localClass;
  }
  
  protected Class<?> findClass(String paramString)
    throws ClassNotFoundException
  {
    if (!paramString.startsWith("sun.reflect.misc.")) {
      throw new ClassNotFoundException(paramString);
    }
    String str = paramString.replace('.', '/').concat(".class");
    URL localURL = getResource(str);
    if (localURL != null) {
      try
      {
        return defineClass(paramString, localURL);
      }
      catch (IOException localIOException)
      {
        throw new ClassNotFoundException(paramString, localIOException);
      }
    }
    throw new ClassNotFoundException(paramString);
  }
  
  private Class<?> defineClass(String paramString, URL paramURL)
    throws IOException
  {
    byte[] arrayOfByte = getBytes(paramURL);
    CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
    if (!paramString.equals("sun.reflect.misc.Trampoline")) {
      throw new IOException("MethodUtil: bad name " + paramString);
    }
    return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, localCodeSource);
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
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    localPermissionCollection.add(new AllPermission());
    return localPermissionCollection;
  }
  
  private static Class<?> getTrampolineClass()
  {
    try
    {
      return Class.forName("sun.reflect.misc.Trampoline", true, new MethodUtil());
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return null;
  }
  
  private static class Signature
  {
    private String methodName;
    private Class<?>[] argClasses;
    private volatile int hashCode = 0;
    
    Signature(Method paramMethod)
    {
      this.methodName = paramMethod.getName();
      this.argClasses = paramMethod.getParameterTypes();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      Signature localSignature = (Signature)paramObject;
      if (!this.methodName.equals(localSignature.methodName)) {
        return false;
      }
      if (this.argClasses.length != localSignature.argClasses.length) {
        return false;
      }
      for (int i = 0; i < this.argClasses.length; i++) {
        if (this.argClasses[i] != localSignature.argClasses[i]) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      if (this.hashCode == 0)
      {
        int i = 17;
        i = 37 * i + this.methodName.hashCode();
        if (this.argClasses != null) {
          for (int j = 0; j < this.argClasses.length; j++) {
            i = 37 * i + (this.argClasses[j] == null ? 0 : this.argClasses[j].hashCode());
          }
        }
        this.hashCode = i;
      }
      return this.hashCode;
    }
  }
}
