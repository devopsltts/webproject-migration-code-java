package javax.management.relation;

import java.io.Serializable;
import java.util.List;

public abstract interface RelationType
  extends Serializable
{
  public abstract String getRelationTypeName();
  
  public abstract List<RoleInfo> getRoleInfos();
  
  public abstract RoleInfo getRoleInfo(String paramString)
    throws IllegalArgumentException, RoleInfoNotFoundException;
}
