package com.sun.security.auth.module;

import com.sun.security.auth.UnixNumericGroupPrincipal;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class JndiLoginModule
  implements LoginModule
{
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  public final String USER_PROVIDER = "user.provider.url";
  public final String GROUP_PROVIDER = "group.provider.url";
  private boolean debug = false;
  private boolean strongDebug = false;
  private String userProvider;
  private String groupProvider;
  private boolean useFirstPass = false;
  private boolean tryFirstPass = false;
  private boolean storePass = false;
  private boolean clearPass = false;
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  private String username;
  private char[] password;
  DirContext ctx;
  private UnixPrincipal userPrincipal;
  private UnixNumericUserPrincipal UIDPrincipal;
  private UnixNumericGroupPrincipal GIDPrincipal;
  private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups = new LinkedList();
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, Object> sharedState;
  private Map<String, ?> options;
  private static final String CRYPT = "{crypt}";
  private static final String USER_PWD = "userPassword";
  private static final String USER_UID = "uidNumber";
  private static final String USER_GID = "gidNumber";
  private static final String GROUP_ID = "gidNumber";
  private static final String NAME = "javax.security.auth.login.name";
  private static final String PWD = "javax.security.auth.login.password";
  
  public JndiLoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = paramMap1;
    this.options = paramMap2;
    this.debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    this.strongDebug = "true".equalsIgnoreCase((String)paramMap2.get("strongDebug"));
    this.userProvider = ((String)paramMap2.get("user.provider.url"));
    this.groupProvider = ((String)paramMap2.get("group.provider.url"));
    this.tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    this.useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    this.storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    this.clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
  }
  
  public boolean login()
    throws LoginException
  {
    if (this.userProvider == null) {
      throw new LoginException("Error: Unable to locate JNDI user provider");
    }
    if (this.groupProvider == null) {
      throw new LoginException("Error: Unable to locate JNDI group provider");
    }
    if (this.debug)
    {
      System.out.println("\t\t[JndiLoginModule] user provider: " + this.userProvider);
      System.out.println("\t\t[JndiLoginModule] group provider: " + this.groupProvider);
    }
    if (this.tryFirstPass) {
      try
      {
        attemptAuthentication(true);
        this.succeeded = true;
        if (this.debug) {
          System.out.println("\t\t[JndiLoginModule] tryFirstPass succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException1)
      {
        cleanState();
        if (this.debug) {
          System.out.println("\t\t[JndiLoginModule] tryFirstPass failed with:" + localLoginException1.toString());
        }
      }
    } else if (this.useFirstPass) {
      try
      {
        attemptAuthentication(true);
        this.succeeded = true;
        if (this.debug) {
          System.out.println("\t\t[JndiLoginModule] useFirstPass succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException2)
      {
        cleanState();
        if (this.debug) {
          System.out.println("\t\t[JndiLoginModule] useFirstPass failed");
        }
        throw localLoginException2;
      }
    }
    try
    {
      attemptAuthentication(false);
      this.succeeded = true;
      if (this.debug) {
        System.out.println("\t\t[JndiLoginModule] regular authentication succeeded");
      }
      return true;
    }
    catch (LoginException localLoginException3)
    {
      cleanState();
      if (this.debug) {
        System.out.println("\t\t[JndiLoginModule] regular authentication failed");
      }
      throw localLoginException3;
    }
  }
  
  public boolean commit()
    throws LoginException
  {
    if (!this.succeeded) {
      return false;
    }
    if (this.subject.isReadOnly())
    {
      cleanState();
      throw new LoginException("Subject is Readonly");
    }
    if (!this.subject.getPrincipals().contains(this.userPrincipal)) {
      this.subject.getPrincipals().add(this.userPrincipal);
    }
    if (!this.subject.getPrincipals().contains(this.UIDPrincipal)) {
      this.subject.getPrincipals().add(this.UIDPrincipal);
    }
    if (!this.subject.getPrincipals().contains(this.GIDPrincipal)) {
      this.subject.getPrincipals().add(this.GIDPrincipal);
    }
    for (int i = 0; i < this.supplementaryGroups.size(); i++) {
      if (!this.subject.getPrincipals().contains(this.supplementaryGroups.get(i))) {
        this.subject.getPrincipals().add(this.supplementaryGroups.get(i));
      }
    }
    if (this.debug)
    {
      System.out.println("\t\t[JndiLoginModule]: added UnixPrincipal,");
      System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
      System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
      System.out.println("\t\t\t to Subject");
    }
    cleanState();
    this.commitSucceeded = true;
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    if (this.debug) {
      System.out.println("\t\t[JndiLoginModule]: aborted authentication failed");
    }
    if (!this.succeeded) {
      return false;
    }
    if ((this.succeeded == true) && (!this.commitSucceeded))
    {
      this.succeeded = false;
      cleanState();
      this.userPrincipal = null;
      this.UIDPrincipal = null;
      this.GIDPrincipal = null;
      this.supplementaryGroups = new LinkedList();
    }
    else
    {
      logout();
    }
    return true;
  }
  
  public boolean logout()
    throws LoginException
  {
    if (this.subject.isReadOnly())
    {
      cleanState();
      throw new LoginException("Subject is Readonly");
    }
    this.subject.getPrincipals().remove(this.userPrincipal);
    this.subject.getPrincipals().remove(this.UIDPrincipal);
    this.subject.getPrincipals().remove(this.GIDPrincipal);
    for (int i = 0; i < this.supplementaryGroups.size(); i++) {
      this.subject.getPrincipals().remove(this.supplementaryGroups.get(i));
    }
    cleanState();
    this.succeeded = false;
    this.commitSucceeded = false;
    this.userPrincipal = null;
    this.UIDPrincipal = null;
    this.GIDPrincipal = null;
    this.supplementaryGroups = new LinkedList();
    if (this.debug) {
      System.out.println("\t\t[JndiLoginModule]: logged out Subject");
    }
    return true;
  }
  
  private void attemptAuthentication(boolean paramBoolean)
    throws LoginException
  {
    String str1 = null;
    getUsernamePassword(paramBoolean);
    try
    {
      InitialContext localInitialContext = new InitialContext();
      this.ctx = ((DirContext)localInitialContext.lookup(this.userProvider));
      SearchControls localSearchControls = new SearchControls();
      NamingEnumeration localNamingEnumeration = this.ctx.search("", "(uid=" + this.username + ")", localSearchControls);
      if (localNamingEnumeration.hasMore())
      {
        SearchResult localSearchResult = (SearchResult)localNamingEnumeration.next();
        Attributes localAttributes = localSearchResult.getAttributes();
        Attribute localAttribute1 = localAttributes.get("userPassword");
        String str2 = new String((byte[])localAttribute1.get(), "UTF8");
        str1 = str2.substring("{crypt}".length());
        if (verifyPassword(str1, new String(this.password)) == true)
        {
          if (this.debug) {
            System.out.println("\t\t[JndiLoginModule] attemptAuthentication() succeeded");
          }
        }
        else
        {
          if (this.debug) {
            System.out.println("\t\t[JndiLoginModule] attemptAuthentication() failed");
          }
          throw new FailedLoginException("Login incorrect");
        }
        if ((this.storePass) && (!this.sharedState.containsKey("javax.security.auth.login.name")) && (!this.sharedState.containsKey("javax.security.auth.login.password")))
        {
          this.sharedState.put("javax.security.auth.login.name", this.username);
          this.sharedState.put("javax.security.auth.login.password", this.password);
        }
        this.userPrincipal = new UnixPrincipal(this.username);
        Attribute localAttribute2 = localAttributes.get("uidNumber");
        String str3 = (String)localAttribute2.get();
        this.UIDPrincipal = new UnixNumericUserPrincipal(str3);
        if ((this.debug) && (str3 != null)) {
          System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has UID: " + str3);
        }
        Attribute localAttribute3 = localAttributes.get("gidNumber");
        String str4 = (String)localAttribute3.get();
        this.GIDPrincipal = new UnixNumericGroupPrincipal(str4, true);
        if ((this.debug) && (str4 != null)) {
          System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has GID: " + str4);
        }
        this.ctx = ((DirContext)localInitialContext.lookup(this.groupProvider));
        localNamingEnumeration = this.ctx.search("", new BasicAttributes("memberUid", this.username));
        while (localNamingEnumeration.hasMore())
        {
          localSearchResult = (SearchResult)localNamingEnumeration.next();
          localAttributes = localSearchResult.getAttributes();
          localAttribute3 = localAttributes.get("gidNumber");
          String str5 = (String)localAttribute3.get();
          if (!str4.equals(str5))
          {
            UnixNumericGroupPrincipal localUnixNumericGroupPrincipal = new UnixNumericGroupPrincipal(str5, false);
            this.supplementaryGroups.add(localUnixNumericGroupPrincipal);
            if ((this.debug) && (str5 != null)) {
              System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has Supplementary Group: " + str5);
            }
          }
        }
      }
      else
      {
        if (this.debug) {
          System.out.println("\t\t[JndiLoginModule]: User not found");
        }
        throw new FailedLoginException("User not found");
      }
    }
    catch (NamingException localNamingException)
    {
      if (this.debug)
      {
        System.out.println("\t\t[JndiLoginModule]:  User not found");
        localNamingException.printStackTrace();
      }
      throw new FailedLoginException("User not found");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (this.debug)
      {
        System.out.println("\t\t[JndiLoginModule]:  password incorrectly encoded");
        localUnsupportedEncodingException.printStackTrace();
      }
      throw new LoginException("Login failure due to incorrect password encoding in the password database");
    }
  }
  
  private void getUsernamePassword(boolean paramBoolean)
    throws LoginException
  {
    if (paramBoolean)
    {
      this.username = ((String)this.sharedState.get("javax.security.auth.login.name"));
      this.password = ((char[])this.sharedState.get("javax.security.auth.login.password"));
      return;
    }
    if (this.callbackHandler == null) {
      throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
    }
    String str = this.userProvider.substring(0, this.userProvider.indexOf(":"));
    Callback[] arrayOfCallback = new Callback[2];
    arrayOfCallback[0] = new NameCallback(str + " " + rb.getString("username."));
    arrayOfCallback[1] = new PasswordCallback(str + " " + rb.getString("password."), false);
    try
    {
      this.callbackHandler.handle(arrayOfCallback);
      this.username = ((NameCallback)arrayOfCallback[0]).getName();
      char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[1]).getPassword();
      this.password = new char[arrayOfChar.length];
      System.arraycopy(arrayOfChar, 0, this.password, 0, arrayOfChar.length);
      ((PasswordCallback)arrayOfCallback[1]).clearPassword();
    }
    catch (IOException localIOException)
    {
      throw new LoginException(localIOException.toString());
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new LoginException("Error: " + localUnsupportedCallbackException.getCallback().toString() + " not available to garner authentication information " + "from the user");
    }
    if (this.strongDebug)
    {
      System.out.println("\t\t[JndiLoginModule] user entered username: " + this.username);
      System.out.print("\t\t[JndiLoginModule] user entered password: ");
      for (int i = 0; i < this.password.length; i++) {
        System.out.print(this.password[i]);
      }
      System.out.println();
    }
  }
  
  private boolean verifyPassword(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return false;
    }
    Crypt localCrypt = new Crypt();
    try
    {
      byte[] arrayOfByte1 = paramString1.getBytes("UTF8");
      byte[] arrayOfByte2 = localCrypt.crypt(paramString2.getBytes("UTF8"), arrayOfByte1);
      if (arrayOfByte2.length != arrayOfByte1.length) {
        return false;
      }
      for (int i = 0; i < arrayOfByte2.length; i++) {
        if (arrayOfByte1[i] != arrayOfByte2[i]) {
          return false;
        }
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      return false;
    }
    return true;
  }
  
  private void cleanState()
  {
    this.username = null;
    if (this.password != null)
    {
      for (int i = 0; i < this.password.length; i++) {
        this.password[i] = ' ';
      }
      this.password = null;
    }
    this.ctx = null;
    if (this.clearPass)
    {
      this.sharedState.remove("javax.security.auth.login.name");
      this.sharedState.remove("javax.security.auth.login.password");
    }
  }
}
