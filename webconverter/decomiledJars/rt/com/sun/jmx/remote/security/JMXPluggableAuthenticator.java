package com.sun.jmx.remote.security;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXAuthenticator;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public final class JMXPluggableAuthenticator
  implements JMXAuthenticator
{
  private LoginContext loginContext;
  private String username;
  private String password;
  private static final String LOGIN_CONFIG_PROP = "jmx.remote.x.login.config";
  private static final String LOGIN_CONFIG_NAME = "JMXPluggableAuthenticator";
  private static final String PASSWORD_FILE_PROP = "jmx.remote.x.password.file";
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXPluggableAuthenticator");
  
  public JMXPluggableAuthenticator(Map<?, ?> paramMap)
  {
    String str1 = null;
    String str2 = null;
    if (paramMap != null)
    {
      str1 = (String)paramMap.get("jmx.remote.x.login.config");
      str2 = (String)paramMap.get("jmx.remote.x.password.file");
    }
    try
    {
      if (str1 != null)
      {
        this.loginContext = new LoginContext(str1, new JMXCallbackHandler(null));
      }
      else
      {
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          localSecurityManager.checkPermission(new AuthPermission("createLoginContext.JMXPluggableAuthenticator"));
        }
        final String str3 = str2;
        try
        {
          this.loginContext = ((LoginContext)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            public LoginContext run()
              throws LoginException
            {
              return new LoginContext("JMXPluggableAuthenticator", null, new JMXPluggableAuthenticator.JMXCallbackHandler(JMXPluggableAuthenticator.this, null), new JMXPluggableAuthenticator.FileLoginConfig(str3));
            }
          }));
        }
        catch (PrivilegedActionException localPrivilegedActionException)
        {
          throw ((LoginException)localPrivilegedActionException.getException());
        }
      }
    }
    catch (LoginException localLoginException)
    {
      authenticationFailure("authenticate", localLoginException);
    }
    catch (SecurityException localSecurityException)
    {
      authenticationFailure("authenticate", localSecurityException);
    }
  }
  
  public Subject authenticate(Object paramObject)
  {
    if (!(paramObject instanceof String[]))
    {
      if (paramObject == null) {
        authenticationFailure("authenticate", "Credentials required");
      }
      localObject1 = "Credentials should be String[] instead of " + paramObject.getClass().getName();
      authenticationFailure("authenticate", (String)localObject1);
    }
    Object localObject1 = (String[])paramObject;
    Object localObject2;
    if (localObject1.length != 2)
    {
      localObject2 = "Credentials should have 2 elements not " + localObject1.length;
      authenticationFailure("authenticate", (String)localObject2);
    }
    this.username = localObject1[0];
    this.password = localObject1[1];
    if ((this.username == null) || (this.password == null)) {
      authenticationFailure("authenticate", "Username or password is null");
    }
    try
    {
      this.loginContext.login();
      localObject2 = this.loginContext.getSubject();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          this.val$subject.setReadOnly();
          return null;
        }
      });
      return localObject2;
    }
    catch (LoginException localLoginException)
    {
      authenticationFailure("authenticate", localLoginException);
    }
    return null;
  }
  
  private static void authenticationFailure(String paramString1, String paramString2)
    throws SecurityException
  {
    String str = "Authentication failed! " + paramString2;
    SecurityException localSecurityException = new SecurityException(str);
    logException(paramString1, str, localSecurityException);
    throw localSecurityException;
  }
  
  private static void authenticationFailure(String paramString, Exception paramException)
    throws SecurityException
  {
    String str;
    Object localObject;
    if ((paramException instanceof SecurityException))
    {
      str = paramException.getMessage();
      localObject = (SecurityException)paramException;
    }
    else
    {
      str = "Authentication failed! " + paramException.getMessage();
      SecurityException localSecurityException = new SecurityException(str);
      EnvHelp.initCause(localSecurityException, paramException);
      localObject = localSecurityException;
    }
    logException(paramString, str, (Exception)localObject);
    throw ((Throwable)localObject);
  }
  
  private static void logException(String paramString1, String paramString2, Exception paramException)
  {
    if (logger.traceOn()) {
      logger.trace(paramString1, paramString2);
    }
    if (logger.debugOn()) {
      logger.debug(paramString1, paramException);
    }
  }
  
  private static class FileLoginConfig
    extends Configuration
  {
    private AppConfigurationEntry[] entries;
    private static final String FILE_LOGIN_MODULE = FileLoginModule.class.getName();
    private static final String PASSWORD_FILE_OPTION = "passwordFile";
    
    public FileLoginConfig(String paramString)
    {
      Object localObject;
      if (paramString != null)
      {
        localObject = new HashMap(1);
        ((Map)localObject).put("passwordFile", paramString);
      }
      else
      {
        localObject = Collections.emptyMap();
      }
      this.entries = new AppConfigurationEntry[] { new AppConfigurationEntry(FILE_LOGIN_MODULE, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, (Map)localObject) };
    }
    
    public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
    {
      return paramString.equals("JMXPluggableAuthenticator") ? this.entries : null;
    }
    
    public void refresh() {}
  }
  
  private final class JMXCallbackHandler
    implements CallbackHandler
  {
    private JMXCallbackHandler() {}
    
    public void handle(Callback[] paramArrayOfCallback)
      throws IOException, UnsupportedCallbackException
    {
      for (int i = 0; i < paramArrayOfCallback.length; i++) {
        if ((paramArrayOfCallback[i] instanceof NameCallback)) {
          ((NameCallback)paramArrayOfCallback[i]).setName(JMXPluggableAuthenticator.this.username);
        } else if ((paramArrayOfCallback[i] instanceof PasswordCallback)) {
          ((PasswordCallback)paramArrayOfCallback[i]).setPassword(JMXPluggableAuthenticator.this.password.toCharArray());
        } else {
          throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized Callback");
        }
      }
    }
  }
}
