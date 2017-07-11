package java.security;

public abstract class PolicySpi
{
  public PolicySpi() {}
  
  protected abstract boolean engineImplies(ProtectionDomain paramProtectionDomain, Permission paramPermission);
  
  protected void engineRefresh() {}
  
  protected PermissionCollection engineGetPermissions(CodeSource paramCodeSource)
  {
    return Policy.UNSUPPORTED_EMPTY_COLLECTION;
  }
  
  protected PermissionCollection engineGetPermissions(ProtectionDomain paramProtectionDomain)
  {
    return Policy.UNSUPPORTED_EMPTY_COLLECTION;
  }
}
