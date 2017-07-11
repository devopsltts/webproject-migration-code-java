package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

public abstract interface Group
  extends Principal
{
  public abstract boolean addMember(Principal paramPrincipal);
  
  public abstract boolean removeMember(Principal paramPrincipal);
  
  public abstract boolean isMember(Principal paramPrincipal);
  
  public abstract Enumeration<? extends Principal> members();
}
