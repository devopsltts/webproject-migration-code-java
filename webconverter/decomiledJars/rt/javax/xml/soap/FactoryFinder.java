package javax.xml.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

class FactoryFinder
{
  FactoryFinder() {}
  
  private static Object newInstance(String paramString, ClassLoader paramClassLoader)
    throws SOAPException
  {
    try
    {
      Class localClass = safeLoadClass(paramString, paramClassLoader);
      return localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new SOAPException("Provider " + paramString + " not found", localClassNotFoundException);
    }
    catch (Exception localException)
    {
      throw new SOAPException("Provider " + paramString + " could not be instantiated: " + localException, localException);
    }
  }
  
  static Object find(String paramString)
    throws SOAPException
  {
    return find(paramString, null, false);
  }
  
  static Object find(String paramString1, String paramString2)
    throws SOAPException
  {
    return find(paramString1, paramString2, true);
  }
  
  static Object find(String paramString1, String paramString2, boolean paramBoolean)
    throws SOAPException
  {
    ClassLoader localClassLoader;
    try
    {
      localClassLoader = Thread.currentThread().getContextClassLoader();
    }
    catch (Exception localException1)
    {
      throw new SOAPException(localException1.toString(), localException1);
    }
    try
    {
      String str1 = System.getProperty(paramString1);
      if (str1 != null) {
        return newInstance(str1, localClassLoader);
      }
    }
    catch (SecurityException localSecurityException) {}
    Object localObject1;
    Object localObject2;
    Object localObject3;
    try
    {
      String str2 = System.getProperty("java.home");
      localObject1 = str2 + File.separator + "lib" + File.separator + "jaxm.properties";
      localObject2 = new File((String)localObject1);
      if (((File)localObject2).exists())
      {
        localObject3 = new Properties();
        ((Properties)localObject3).load(new FileInputStream((File)localObject2));
        String str4 = ((Properties)localObject3).getProperty(paramString1);
        return newInstance(str4, localClassLoader);
      }
    }
    catch (Exception localException2) {}
    String str3 = "META-INF/services/" + paramString1;
    try
    {
      localObject1 = null;
      if (localClassLoader == null) {
        localObject1 = ClassLoader.getSystemResourceAsStream(str3);
      } else {
        localObject1 = localClassLoader.getResourceAsStream(str3);
      }
      if (localObject1 != null)
      {
        localObject2 = new BufferedReader(new InputStreamReader((InputStream)localObject1, "UTF-8"));
        localObject3 = ((BufferedReader)localObject2).readLine();
        ((BufferedReader)localObject2).close();
        if ((localObject3 != null) && (!"".equals(localObject3))) {
          return newInstance((String)localObject3, localClassLoader);
        }
      }
    }
    catch (Exception localException3) {}
    if (!paramBoolean) {
      return null;
    }
    if (paramString2 == null) {
      throw new SOAPException("Provider for " + paramString1 + " cannot be found", null);
    }
    return newInstance(paramString2, localClassLoader);
  }
  
  private static Class safeLoadClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        int i = paramString.lastIndexOf('.');
        if (i != -1) {
          localSecurityManager.checkPackageAccess(paramString.substring(0, i));
        }
      }
      if (paramClassLoader == null) {
        return Class.forName(paramString);
      }
      return paramClassLoader.loadClass(paramString);
    }
    catch (SecurityException localSecurityException)
    {
      if (isDefaultImplementation(paramString)) {
        return Class.forName(paramString);
      }
      throw localSecurityException;
    }
  }
  
  private static boolean isDefaultImplementation(String paramString)
  {
    return ("com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl".equals(paramString)) || ("com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl".equals(paramString)) || ("com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory".equals(paramString)) || ("com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl".equals(paramString));
  }
}
