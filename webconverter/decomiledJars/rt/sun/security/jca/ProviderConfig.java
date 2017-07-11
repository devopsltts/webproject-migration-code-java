package sun.security.jca;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

final class ProviderConfig
{
  private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
  private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
  private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
  private static final int MAX_LOAD_TRIES = 30;
  private static final Class[] CL_STRING = { String.class };
  private final String className;
  private final String argument;
  private int tries;
  private volatile Provider provider;
  private boolean isLoading;
  
  ProviderConfig(String paramString1, String paramString2)
  {
    if ((paramString1.equals("sun.security.pkcs11.SunPKCS11")) && (paramString2.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg"))) {
      checkSunPKCS11Solaris();
    }
    this.className = paramString1;
    this.argument = expand(paramString2);
  }
  
  ProviderConfig(String paramString)
  {
    this(paramString, "");
  }
  
  ProviderConfig(Provider paramProvider)
  {
    this.className = paramProvider.getClass().getName();
    this.argument = "";
    this.provider = paramProvider;
  }
  
  private void checkSunPKCS11Solaris()
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        File localFile = new File("/usr/lib/libpkcs11.so");
        if (!localFile.exists()) {
          return Boolean.FALSE;
        }
        if ("false".equalsIgnoreCase(System.getProperty("sun.security.pkcs11.enable-solaris"))) {
          return Boolean.FALSE;
        }
        return Boolean.TRUE;
      }
    });
    if (localBoolean == Boolean.FALSE) {
      this.tries = 30;
    }
  }
  
  private boolean hasArgument()
  {
    return this.argument.length() != 0;
  }
  
  private boolean shouldLoad()
  {
    return this.tries < 30;
  }
  
  private void disableLoad()
  {
    this.tries = 30;
  }
  
  boolean isLoaded()
  {
    return this.provider != null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ProviderConfig)) {
      return false;
    }
    ProviderConfig localProviderConfig = (ProviderConfig)paramObject;
    return (this.className.equals(localProviderConfig.className)) && (this.argument.equals(localProviderConfig.argument));
  }
  
  public int hashCode()
  {
    return this.className.hashCode() + this.argument.hashCode();
  }
  
  public String toString()
  {
    if (hasArgument()) {
      return this.className + "('" + this.argument + "')";
    }
    return this.className;
  }
  
  synchronized Provider getProvider()
  {
    Provider localProvider = this.provider;
    if (localProvider != null) {
      return localProvider;
    }
    if (!shouldLoad()) {
      return null;
    }
    if (this.isLoading)
    {
      if (debug != null)
      {
        debug.println("Recursion loading provider: " + this);
        new Exception("Call trace").printStackTrace();
      }
      return null;
    }
    try
    {
      this.isLoading = true;
      this.tries += 1;
      localProvider = doLoadProvider();
      this.isLoading = false;
    }
    finally
    {
      this.isLoading = false;
    }
    return localProvider;
  }
  
  private Provider doLoadProvider()
  {
    (Provider)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Provider run()
      {
        if (ProviderConfig.debug != null) {
          ProviderConfig.debug.println("Loading provider: " + ProviderConfig.this);
        }
        try
        {
          ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
          if (localClassLoader != null) {
            localObject1 = localClassLoader.loadClass(ProviderConfig.this.className);
          } else {
            localObject1 = Class.forName(ProviderConfig.this.className);
          }
          Object localObject2;
          if (!ProviderConfig.this.hasArgument())
          {
            localObject2 = ((Class)localObject1).newInstance();
          }
          else
          {
            Constructor localConstructor = ((Class)localObject1).getConstructor(ProviderConfig.CL_STRING);
            localObject2 = localConstructor.newInstance(new Object[] { ProviderConfig.this.argument });
          }
          if ((localObject2 instanceof Provider))
          {
            if (ProviderConfig.debug != null) {
              ProviderConfig.debug.println("Loaded provider " + localObject2);
            }
            return (Provider)localObject2;
          }
          if (ProviderConfig.debug != null) {
            ProviderConfig.debug.println(ProviderConfig.this.className + " is not a provider");
          }
          ProviderConfig.this.disableLoad();
          return null;
        }
        catch (Exception localException)
        {
          Object localObject1;
          if ((localException instanceof InvocationTargetException)) {
            localObject1 = ((InvocationTargetException)localException).getCause();
          } else {
            localObject1 = localException;
          }
          if (ProviderConfig.debug != null)
          {
            ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
            ((Throwable)localObject1).printStackTrace();
          }
          if ((localObject1 instanceof ProviderException)) {
            throw ((ProviderException)localObject1);
          }
          if ((localObject1 instanceof UnsupportedOperationException)) {
            ProviderConfig.this.disableLoad();
          }
        }
        return null;
      }
    });
  }
  
  private static String expand(String paramString)
  {
    if (!paramString.contains("${")) {
      return paramString;
    }
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          return PropertyExpander.expand(this.val$value);
        }
        catch (GeneralSecurityException localGeneralSecurityException)
        {
          throw new ProviderException(localGeneralSecurityException);
        }
      }
    });
  }
}
