package javax.security.auth.login;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import javax.security.auth.AuthPermission;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public abstract class Configuration
{
  private static Configuration configuration;
  private final AccessControlContext acc = AccessController.getContext();
  
  private static void checkPermission(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("createLoginConfiguration." + paramString));
    }
  }
  
  protected Configuration() {}
  
  public static Configuration getConfiguration()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("getLoginConfiguration"));
    }
    synchronized (Configuration.class)
    {
      if (configuration == null)
      {
        String str1 = null;
        str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
        {
          public String run()
          {
            return Security.getProperty("login.configuration.provider");
          }
        });
        if (str1 == null) {
          str1 = "sun.security.provider.ConfigFile";
        }
        try
        {
          String str2 = str1;
          localObject1 = (Configuration)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            public Configuration run()
              throws ClassNotFoundException, InstantiationException, IllegalAccessException
            {
              Class localClass = Class.forName(this.val$finalClass, false, Thread.currentThread().getContextClassLoader()).asSubclass(Configuration.class);
              return (Configuration)localClass.newInstance();
            }
          });
          AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            public Void run()
            {
              Configuration.setConfiguration(this.val$untrustedImpl);
              return null;
            }
          }, (AccessControlContext)Objects.requireNonNull(((Configuration)localObject1).acc));
        }
        catch (PrivilegedActionException localPrivilegedActionException)
        {
          Object localObject1 = localPrivilegedActionException.getException();
          if ((localObject1 instanceof InstantiationException)) {
            throw ((SecurityException)new SecurityException("Configuration error:" + ((Exception)localObject1).getCause().getMessage() + "\n").initCause(((Exception)localObject1).getCause()));
          }
          throw ((SecurityException)new SecurityException("Configuration error: " + ((Exception)localObject1).toString() + "\n").initCause((Throwable)localObject1));
        }
      }
      return configuration;
    }
  }
  
  public static void setConfiguration(Configuration paramConfiguration)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("setLoginConfiguration"));
    }
    configuration = paramConfiguration;
  }
  
  public static Configuration getInstance(String paramString, Parameters paramParameters)
    throws NoSuchAlgorithmException
  {
    checkPermission(paramString);
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString, paramParameters);
      return new ConfigDelegate((ConfigurationSpi)localInstance.impl, localInstance.provider, paramString, paramParameters, null);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  public static Configuration getInstance(String paramString1, Parameters paramParameters, String paramString2)
    throws NoSuchProviderException, NoSuchAlgorithmException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    checkPermission(paramString1);
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString1, paramParameters, paramString2);
      return new ConfigDelegate((ConfigurationSpi)localInstance.impl, localInstance.provider, paramString1, paramParameters, null);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  public static Configuration getInstance(String paramString, Parameters paramParameters, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    checkPermission(paramString);
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString, paramParameters, paramProvider);
      return new ConfigDelegate((ConfigurationSpi)localInstance.impl, localInstance.provider, paramString, paramParameters, null);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  private static Configuration handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException)
    throws NoSuchAlgorithmException
  {
    Throwable localThrowable = paramNoSuchAlgorithmException.getCause();
    if ((localThrowable instanceof IllegalArgumentException)) {
      throw ((IllegalArgumentException)localThrowable);
    }
    throw paramNoSuchAlgorithmException;
  }
  
  public Provider getProvider()
  {
    return null;
  }
  
  public String getType()
  {
    return null;
  }
  
  public Parameters getParameters()
  {
    return null;
  }
  
  public abstract AppConfigurationEntry[] getAppConfigurationEntry(String paramString);
  
  public void refresh() {}
  
  private static class ConfigDelegate
    extends Configuration
  {
    private ConfigurationSpi spi;
    private Provider p;
    private String type;
    private Configuration.Parameters params;
    
    private ConfigDelegate(ConfigurationSpi paramConfigurationSpi, Provider paramProvider, String paramString, Configuration.Parameters paramParameters)
    {
      this.spi = paramConfigurationSpi;
      this.p = paramProvider;
      this.type = paramString;
      this.params = paramParameters;
    }
    
    public String getType()
    {
      return this.type;
    }
    
    public Configuration.Parameters getParameters()
    {
      return this.params;
    }
    
    public Provider getProvider()
    {
      return this.p;
    }
    
    public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
    {
      return this.spi.engineGetAppConfigurationEntry(paramString);
    }
    
    public void refresh()
    {
      this.spi.engineRefresh();
    }
  }
  
  public static abstract interface Parameters {}
}
