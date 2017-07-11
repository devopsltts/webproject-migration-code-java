package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import javax.management.NotCompliantMBeanException;

public class RoleInfo
  implements Serializable
{
  private static final long oldSerialVersionUID = 7227256952085334351L;
  private static final long newSerialVersionUID = 2504952983494636987L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myName", String.class), new ObjectStreamField("myIsReadableFlg", Boolean.TYPE), new ObjectStreamField("myIsWritableFlg", Boolean.TYPE), new ObjectStreamField("myDescription", String.class), new ObjectStreamField("myMinDegree", Integer.TYPE), new ObjectStreamField("myMaxDegree", Integer.TYPE), new ObjectStreamField("myRefMBeanClassName", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("name", String.class), new ObjectStreamField("isReadable", Boolean.TYPE), new ObjectStreamField("isWritable", Boolean.TYPE), new ObjectStreamField("description", String.class), new ObjectStreamField("minDegree", Integer.TYPE), new ObjectStreamField("maxDegree", Integer.TYPE), new ObjectStreamField("referencedMBeanClassName", String.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  public static final int ROLE_CARDINALITY_INFINITY = -1;
  private String name = null;
  private boolean isReadable;
  private boolean isWritable;
  private String description = null;
  private int minDegree;
  private int maxDegree;
  private String referencedMBeanClassName = null;
  
  public RoleInfo(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, String paramString3)
    throws IllegalArgumentException, InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException
  {
    init(paramString1, paramString2, paramBoolean1, paramBoolean2, paramInt1, paramInt2, paramString3);
  }
  
  public RoleInfo(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException
  {
    try
    {
      init(paramString1, paramString2, paramBoolean1, paramBoolean2, 1, 1, null);
    }
    catch (InvalidRoleInfoException localInvalidRoleInfoException) {}
  }
  
  public RoleInfo(String paramString1, String paramString2)
    throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException
  {
    try
    {
      init(paramString1, paramString2, true, true, 1, 1, null);
    }
    catch (InvalidRoleInfoException localInvalidRoleInfoException) {}
  }
  
  public RoleInfo(RoleInfo paramRoleInfo)
    throws IllegalArgumentException
  {
    if (paramRoleInfo == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    try
    {
      init(paramRoleInfo.getName(), paramRoleInfo.getRefMBeanClassName(), paramRoleInfo.isReadable(), paramRoleInfo.isWritable(), paramRoleInfo.getMinDegree(), paramRoleInfo.getMaxDegree(), paramRoleInfo.getDescription());
    }
    catch (InvalidRoleInfoException localInvalidRoleInfoException) {}
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public boolean isReadable()
  {
    return this.isReadable;
  }
  
  public boolean isWritable()
  {
    return this.isWritable;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public int getMinDegree()
  {
    return this.minDegree;
  }
  
  public int getMaxDegree()
  {
    return this.maxDegree;
  }
  
  public String getRefMBeanClassName()
  {
    return this.referencedMBeanClassName;
  }
  
  public boolean checkMinDegree(int paramInt)
  {
    return (paramInt >= -1) && ((this.minDegree == -1) || (paramInt >= this.minDegree));
  }
  
  public boolean checkMaxDegree(int paramInt)
  {
    return (paramInt >= -1) && ((this.maxDegree == -1) || ((paramInt != -1) && (paramInt <= this.maxDegree)));
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("role info name: " + this.name);
    localStringBuilder.append("; isReadable: " + this.isReadable);
    localStringBuilder.append("; isWritable: " + this.isWritable);
    localStringBuilder.append("; description: " + this.description);
    localStringBuilder.append("; minimum degree: " + this.minDegree);
    localStringBuilder.append("; maximum degree: " + this.maxDegree);
    localStringBuilder.append("; MBean class: " + this.referencedMBeanClassName);
    return localStringBuilder.toString();
  }
  
  private void init(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, String paramString3)
    throws IllegalArgumentException, InvalidRoleInfoException
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    this.name = paramString1;
    this.isReadable = paramBoolean1;
    this.isWritable = paramBoolean2;
    if (paramString3 != null) {
      this.description = paramString3;
    }
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt2 != -1) && ((paramInt1 == -1) || (paramInt1 > paramInt2)))
    {
      localStringBuilder.append("Minimum degree ");
      localStringBuilder.append(paramInt1);
      localStringBuilder.append(" is greater than maximum degree ");
      localStringBuilder.append(paramInt2);
      i = 1;
    }
    else if ((paramInt1 < -1) || (paramInt2 < -1))
    {
      localStringBuilder.append("Minimum or maximum degree has an illegal value, must be [0, ROLE_CARDINALITY_INFINITY].");
      i = 1;
    }
    if (i != 0) {
      throw new InvalidRoleInfoException(localStringBuilder.toString());
    }
    this.minDegree = paramInt1;
    this.maxDegree = paramInt2;
    this.referencedMBeanClassName = paramString2;
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
      this.isReadable = localGetField.get("myIsReadableFlg", false);
      if (localGetField.defaulted("myIsReadableFlg")) {
        throw new NullPointerException("myIsReadableFlg");
      }
      this.isWritable = localGetField.get("myIsWritableFlg", false);
      if (localGetField.defaulted("myIsWritableFlg")) {
        throw new NullPointerException("myIsWritableFlg");
      }
      this.description = ((String)localGetField.get("myDescription", null));
      if (localGetField.defaulted("myDescription")) {
        throw new NullPointerException("myDescription");
      }
      this.minDegree = localGetField.get("myMinDegree", 0);
      if (localGetField.defaulted("myMinDegree")) {
        throw new NullPointerException("myMinDegree");
      }
      this.maxDegree = localGetField.get("myMaxDegree", 0);
      if (localGetField.defaulted("myMaxDegree")) {
        throw new NullPointerException("myMaxDegree");
      }
      this.referencedMBeanClassName = ((String)localGetField.get("myRefMBeanClassName", null));
      if (localGetField.defaulted("myRefMBeanClassName")) {
        throw new NullPointerException("myRefMBeanClassName");
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
      localPutField.put("myIsReadableFlg", this.isReadable);
      localPutField.put("myIsWritableFlg", this.isWritable);
      localPutField.put("myDescription", this.description);
      localPutField.put("myMinDegree", this.minDegree);
      localPutField.put("myMaxDegree", this.maxDegree);
      localPutField.put("myRefMBeanClassName", this.referencedMBeanClassName);
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
      serialVersionUID = 7227256952085334351L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 2504952983494636987L;
    }
  }
}
