package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class BasicPermission
  extends Permission
  implements Serializable
{
  private static final long serialVersionUID = 6279438298436773498L;
  private transient boolean wildcard;
  private transient String path;
  private transient boolean exitVM;
  
  private void init(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name can't be null");
    }
    int i = paramString.length();
    if (i == 0) {
      throw new IllegalArgumentException("name can't be empty");
    }
    int j = paramString.charAt(i - 1);
    if ((j == 42) && ((i == 1) || (paramString.charAt(i - 2) == '.')))
    {
      this.wildcard = true;
      if (i == 1) {
        this.path = "";
      } else {
        this.path = paramString.substring(0, i - 1);
      }
    }
    else if (paramString.equals("exitVM"))
    {
      this.wildcard = true;
      this.path = "exitVM.";
      this.exitVM = true;
    }
    else
    {
      this.path = paramString;
    }
  }
  
  public BasicPermission(String paramString)
  {
    super(paramString);
    init(paramString);
  }
  
  public BasicPermission(String paramString1, String paramString2)
  {
    super(paramString1);
    init(paramString1);
  }
  
  public boolean implies(Permission paramPermission)
  {
    if ((paramPermission == null) || (paramPermission.getClass() != getClass())) {
      return false;
    }
    BasicPermission localBasicPermission = (BasicPermission)paramPermission;
    if (this.wildcard)
    {
      if (localBasicPermission.wildcard) {
        return localBasicPermission.path.startsWith(this.path);
      }
      return (localBasicPermission.path.length() > this.path.length()) && (localBasicPermission.path.startsWith(this.path));
    }
    if (localBasicPermission.wildcard) {
      return false;
    }
    return this.path.equals(localBasicPermission.path);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (paramObject.getClass() != getClass())) {
      return false;
    }
    BasicPermission localBasicPermission = (BasicPermission)paramObject;
    return getName().equals(localBasicPermission.getName());
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public String getActions()
  {
    return "";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new BasicPermissionCollection(getClass());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
  
  final String getCanonicalName()
  {
    return this.exitVM ? "exitVM.*" : getName();
  }
}
