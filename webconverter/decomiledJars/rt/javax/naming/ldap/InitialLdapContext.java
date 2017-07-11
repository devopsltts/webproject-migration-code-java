package javax.naming.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;
import javax.naming.directory.InitialDirContext;

public class InitialLdapContext
  extends InitialDirContext
  implements LdapContext
{
  private static final String BIND_CONTROLS_PROPERTY = "java.naming.ldap.control.connect";
  
  public InitialLdapContext()
    throws NamingException
  {
    super(null);
  }
  
  public InitialLdapContext(Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl)
    throws NamingException
  {
    super(true);
    Hashtable localHashtable = paramHashtable == null ? new Hashtable(11) : (Hashtable)paramHashtable.clone();
    if (paramArrayOfControl != null)
    {
      Control[] arrayOfControl = new Control[paramArrayOfControl.length];
      System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, paramArrayOfControl.length);
      localHashtable.put("java.naming.ldap.control.connect", arrayOfControl);
    }
    localHashtable.put("java.naming.ldap.version", "3");
    init(localHashtable);
  }
  
  private LdapContext getDefaultLdapInitCtx()
    throws NamingException
  {
    Context localContext = getDefaultInitCtx();
    if (!(localContext instanceof LdapContext))
    {
      if (localContext == null) {
        throw new NoInitialContextException();
      }
      throw new NotContextException("Not an instance of LdapContext");
    }
    return (LdapContext)localContext;
  }
  
  public ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest)
    throws NamingException
  {
    return getDefaultLdapInitCtx().extendedOperation(paramExtendedRequest);
  }
  
  public LdapContext newInstance(Control[] paramArrayOfControl)
    throws NamingException
  {
    return getDefaultLdapInitCtx().newInstance(paramArrayOfControl);
  }
  
  public void reconnect(Control[] paramArrayOfControl)
    throws NamingException
  {
    getDefaultLdapInitCtx().reconnect(paramArrayOfControl);
  }
  
  public Control[] getConnectControls()
    throws NamingException
  {
    return getDefaultLdapInitCtx().getConnectControls();
  }
  
  public void setRequestControls(Control[] paramArrayOfControl)
    throws NamingException
  {
    getDefaultLdapInitCtx().setRequestControls(paramArrayOfControl);
  }
  
  public Control[] getRequestControls()
    throws NamingException
  {
    return getDefaultLdapInitCtx().getRequestControls();
  }
  
  public Control[] getResponseControls()
    throws NamingException
  {
    return getDefaultLdapInitCtx().getResponseControls();
  }
}
