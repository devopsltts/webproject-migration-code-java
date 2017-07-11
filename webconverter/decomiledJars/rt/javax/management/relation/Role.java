package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;

public class Role
  implements Serializable
{
  private static final long oldSerialVersionUID = -1959486389343113026L;
  private static final long newSerialVersionUID = -279985518429862552L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myName", String.class), new ObjectStreamField("myObjNameList", ArrayList.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("name", String.class), new ObjectStreamField("objectNameList", List.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private String name = null;
  private List<ObjectName> objectNameList = new ArrayList();
  
  public Role(String paramString, List<ObjectName> paramList)
    throws IllegalArgumentException
  {
    if ((paramString == null) || (paramList == null))
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    setRoleName(paramString);
    setRoleValue(paramList);
  }
  
  public String getRoleName()
  {
    return this.name;
  }
  
  public List<ObjectName> getRoleValue()
  {
    return this.objectNameList;
  }
  
  public void setRoleName(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    this.name = paramString;
  }
  
  public void setRoleValue(List<ObjectName> paramList)
    throws IllegalArgumentException
  {
    if (paramList == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    this.objectNameList = new ArrayList(paramList);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("role name: " + this.name + "; role value: ");
    Iterator localIterator = this.objectNameList.iterator();
    while (localIterator.hasNext())
    {
      ObjectName localObjectName = (ObjectName)localIterator.next();
      localStringBuilder.append(localObjectName.toString());
      if (localIterator.hasNext()) {
        localStringBuilder.append(", ");
      }
    }
    return localStringBuilder.toString();
  }
  
  public Object clone()
  {
    try
    {
      return new Role(this.name, this.objectNameList);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  public static String roleValueToString(List<ObjectName> paramList)
    throws IllegalArgumentException
  {
    if (paramList == null)
    {
      localObject = "Invalid parameter";
      throw new IllegalArgumentException((String)localObject);
    }
    Object localObject = new StringBuilder();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ObjectName localObjectName = (ObjectName)localIterator.next();
      if (((StringBuilder)localObject).length() > 0) {
        ((StringBuilder)localObject).append("\n");
      }
      ((StringBuilder)localObject).append(localObjectName.toString());
    }
    return ((StringBuilder)localObject).toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      this.name = ((String)localGetField.get("myName", null));
      if (localGetField.defaulted("myName")) {
        throw new NullPointerException("myName");
      }
      this.objectNameList = ((List)Util.cast(localGetField.get("myObjNameList", null)));
      if (localGetField.defaulted("myObjNameList")) {
        throw new NullPointerException("myObjNameList");
      }
    }
    else
    {
      paramObjectInputStream.defaultReadObject();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("myName", this.name);
      localPutField.put("myObjNameList", this.objectNameList);
      paramObjectOutputStream.writeFields();
    }
    else
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  static
  {
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      compat = (str != null) && (str.equals("1.0"));
    }
    catch (Exception localException) {}
    if (compat)
    {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = -1959486389343113026L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -279985518429862552L;
    }
  }
}
