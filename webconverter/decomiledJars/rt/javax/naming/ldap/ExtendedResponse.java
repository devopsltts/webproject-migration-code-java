package javax.naming.ldap;

import java.io.Serializable;

public abstract interface ExtendedResponse
  extends Serializable
{
  public abstract String getID();
  
  public abstract byte[] getEncodedValue();
}
