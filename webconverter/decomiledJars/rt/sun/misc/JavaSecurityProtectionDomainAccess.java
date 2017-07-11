package sun.misc;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public abstract interface JavaSecurityProtectionDomainAccess
{
  public abstract ProtectionDomainCache getProtectionDomainCache();
  
  public static abstract interface ProtectionDomainCache
  {
    public abstract void put(ProtectionDomain paramProtectionDomain, PermissionCollection paramPermissionCollection);
    
    public abstract PermissionCollection get(ProtectionDomain paramProtectionDomain);
  }
}
