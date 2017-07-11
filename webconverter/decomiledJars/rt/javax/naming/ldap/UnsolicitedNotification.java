package javax.naming.ldap;

import javax.naming.NamingException;

public abstract interface UnsolicitedNotification
  extends ExtendedResponse, HasControls
{
  public abstract String[] getReferrals();
  
  public abstract NamingException getException();
}
