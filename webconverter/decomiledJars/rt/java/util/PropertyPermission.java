package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

public final class PropertyPermission
  extends BasicPermission
{
  private static final int READ = 1;
  private static final int WRITE = 2;
  private static final int ALL = 3;
  private static final int NONE = 0;
  private transient int mask;
  private String actions;
  private static final long serialVersionUID = 885438825399942851L;
  
  private void init(int paramInt)
  {
    if ((paramInt & 0x3) != paramInt) {
      throw new IllegalArgumentException("invalid actions mask");
    }
    if (paramInt == 0) {
      throw new IllegalArgumentException("invalid actions mask");
    }
    if (getName() == null) {
      throw new NullPointerException("name can't be null");
    }
    this.mask = paramInt;
  }
  
  public PropertyPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    init(getMask(paramString2));
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof PropertyPermission)) {
      return false;
    }
    PropertyPermission localPropertyPermission = (PropertyPermission)paramPermission;
    return ((this.mask & localPropertyPermission.mask) == localPropertyPermission.mask) && (super.implies(localPropertyPermission));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof PropertyPermission)) {
      return false;
    }
    PropertyPermission localPropertyPermission = (PropertyPermission)paramObject;
    return (this.mask == localPropertyPermission.mask) && (getName().equals(localPropertyPermission.getName()));
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  private static int getMask(String paramString)
  {
    int i = 0;
    if (paramString == null) {
      return i;
    }
    if (paramString == "read") {
      return 1;
    }
    if (paramString == "write") {
      return 2;
    }
    if (paramString == "read,write") {
      return 3;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length - 1;
    if (j < 0) {
      return i;
    }
    while (j != -1)
    {
      int k;
      while ((j != -1) && (((k = arrayOfChar[j]) == ' ') || (k == 13) || (k == 10) || (k == 12) || (k == 9))) {
        j--;
      }
      int m;
      if ((j >= 3) && ((arrayOfChar[(j - 3)] == 'r') || (arrayOfChar[(j - 3)] == 'R')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'a') || (arrayOfChar[(j - 1)] == 'A')) && ((arrayOfChar[j] == 'd') || (arrayOfChar[j] == 'D')))
      {
        m = 4;
        i |= 0x1;
      }
      else if ((j >= 4) && ((arrayOfChar[(j - 4)] == 'w') || (arrayOfChar[(j - 4)] == 'W')) && ((arrayOfChar[(j - 3)] == 'r') || (arrayOfChar[(j - 3)] == 'R')) && ((arrayOfChar[(j - 2)] == 'i') || (arrayOfChar[(j - 2)] == 'I')) && ((arrayOfChar[(j - 1)] == 't') || (arrayOfChar[(j - 1)] == 'T')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
      {
        m = 5;
        i |= 0x2;
      }
      else
      {
        throw new IllegalArgumentException("invalid permission: " + paramString);
      }
      int n = 0;
      while ((j >= m) && (n == 0))
      {
        switch (arrayOfChar[(j - m)])
        {
        case ',': 
          n = 1;
          break;
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
          break;
        default: 
          throw new IllegalArgumentException("invalid permission: " + paramString);
        }
        j--;
      }
      j -= m;
    }
    return i;
  }
  
  static String getActions(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if ((paramInt & 0x1) == 1)
    {
      i = 1;
      localStringBuilder.append("read");
    }
    if ((paramInt & 0x2) == 2)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("write");
    }
    return localStringBuilder.toString();
  }
  
  public String getActions()
  {
    if (this.actions == null) {
      this.actions = getActions(this.mask);
    }
    return this.actions;
  }
  
  int getMask()
  {
    return this.mask;
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new PropertyPermissionCollection();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (this.actions == null) {
      getActions();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getMask(this.actions));
  }
}
