package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public abstract interface LdapContext
  extends DirContext
{
  public static final String CONTROL_FACTORIES = "java.naming.factory.control";
  
  public abstract ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest)
    throws NamingException;
  
  public abstract LdapContext newInstance(Control[] paramArrayOfControl)
    throws NamingException;
  
  public abstract void reconnect(Control[] paramArrayOfControl)
    throws NamingException;
  
  public abstract Control[] getConnectControls()
    throws NamingException;
  
  public abstract void setRequestControls(Control[] paramArrayOfControl)
    throws NamingException;
  
  public abstract Control[] getRequestControls()
    throws NamingException;
  
  public abstract Control[] getResponseControls()
    throws NamingException;
}
