package java.net;

import java.security.Permission;

public abstract class Authenticator
{
  private static Authenticator theAuthenticator;
  private String requestingHost;
  private InetAddress requestingSite;
  private int requestingPort;
  private String requestingProtocol;
  private String requestingPrompt;
  private String requestingScheme;
  private URL requestingURL;
  private RequestorType requestingAuthType;
  
  public Authenticator() {}
  
  private void reset()
  {
    this.requestingHost = null;
    this.requestingSite = null;
    this.requestingPort = -1;
    this.requestingProtocol = null;
    this.requestingPrompt = null;
    this.requestingScheme = null;
    this.requestingURL = null;
    this.requestingAuthType = RequestorType.SERVER;
  }
  
  public static synchronized void setDefault(Authenticator paramAuthenticator)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      NetPermission localNetPermission = new NetPermission("setDefaultAuthenticator");
      localSecurityManager.checkPermission(localNetPermission);
    }
    theAuthenticator = paramAuthenticator;
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject1 = new NetPermission("requestPasswordAuthentication");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    Object localObject1 = theAuthenticator;
    if (localObject1 == null) {
      return null;
    }
    synchronized (localObject1)
    {
      ((Authenticator)localObject1).reset();
      ((Authenticator)localObject1).requestingSite = paramInetAddress;
      ((Authenticator)localObject1).requestingPort = paramInt;
      ((Authenticator)localObject1).requestingProtocol = paramString1;
      ((Authenticator)localObject1).requestingPrompt = paramString2;
      ((Authenticator)localObject1).requestingScheme = paramString3;
      return ((Authenticator)localObject1).getPasswordAuthentication();
    }
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject1 = new NetPermission("requestPasswordAuthentication");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    Object localObject1 = theAuthenticator;
    if (localObject1 == null) {
      return null;
    }
    synchronized (localObject1)
    {
      ((Authenticator)localObject1).reset();
      ((Authenticator)localObject1).requestingHost = paramString1;
      ((Authenticator)localObject1).requestingSite = paramInetAddress;
      ((Authenticator)localObject1).requestingPort = paramInt;
      ((Authenticator)localObject1).requestingProtocol = paramString2;
      ((Authenticator)localObject1).requestingPrompt = paramString3;
      ((Authenticator)localObject1).requestingScheme = paramString4;
      return ((Authenticator)localObject1).getPasswordAuthentication();
    }
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4, URL paramURL, RequestorType paramRequestorType)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject1 = new NetPermission("requestPasswordAuthentication");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    Object localObject1 = theAuthenticator;
    if (localObject1 == null) {
      return null;
    }
    synchronized (localObject1)
    {
      ((Authenticator)localObject1).reset();
      ((Authenticator)localObject1).requestingHost = paramString1;
      ((Authenticator)localObject1).requestingSite = paramInetAddress;
      ((Authenticator)localObject1).requestingPort = paramInt;
      ((Authenticator)localObject1).requestingProtocol = paramString2;
      ((Authenticator)localObject1).requestingPrompt = paramString3;
      ((Authenticator)localObject1).requestingScheme = paramString4;
      ((Authenticator)localObject1).requestingURL = paramURL;
      ((Authenticator)localObject1).requestingAuthType = paramRequestorType;
      return ((Authenticator)localObject1).getPasswordAuthentication();
    }
  }
  
  protected final String getRequestingHost()
  {
    return this.requestingHost;
  }
  
  protected final InetAddress getRequestingSite()
  {
    return this.requestingSite;
  }
  
  protected final int getRequestingPort()
  {
    return this.requestingPort;
  }
  
  protected final String getRequestingProtocol()
  {
    return this.requestingProtocol;
  }
  
  protected final String getRequestingPrompt()
  {
    return this.requestingPrompt;
  }
  
  protected final String getRequestingScheme()
  {
    return this.requestingScheme;
  }
  
  protected PasswordAuthentication getPasswordAuthentication()
  {
    return null;
  }
  
  protected URL getRequestingURL()
  {
    return this.requestingURL;
  }
  
  protected RequestorType getRequestorType()
  {
    return this.requestingAuthType;
  }
  
  public static enum RequestorType
  {
    PROXY,  SERVER;
    
    private RequestorType() {}
  }
}
