package sun.net.ftp;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceConfigurationError;
import sun.net.ftp.impl.DefaultFtpClientProvider;

public abstract class FtpClientProvider
{
  private static final Object lock = new Object();
  private static FtpClientProvider provider = null;
  
  public abstract FtpClient createFtpClient();
  
  protected FtpClientProvider()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("ftpClientProvider"));
    }
  }
  
  private static boolean loadProviderFromProperty()
  {
    String str = System.getProperty("sun.net.ftpClientProvider");
    if (str == null) {
      return false;
    }
    try
    {
      Class localClass = Class.forName(str, true, null);
      provider = (FtpClientProvider)localClass.newInstance();
      return true;
    }
    catch (ClassNotFoundException|IllegalAccessException|InstantiationException|SecurityException localClassNotFoundException)
    {
      throw new ServiceConfigurationError(localClassNotFoundException.toString());
    }
  }
  
  private static boolean loadProviderAsService()
  {
    return false;
  }
  
  public static FtpClientProvider provider()
  {
    synchronized (lock)
    {
      if (provider != null) {
        return provider;
      }
      (FtpClientProvider)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          if (FtpClientProvider.access$000()) {
            return FtpClientProvider.provider;
          }
          if (FtpClientProvider.access$200()) {
            return FtpClientProvider.provider;
          }
          FtpClientProvider.access$102(new DefaultFtpClientProvider());
          return FtpClientProvider.provider;
        }
      });
    }
  }
}
