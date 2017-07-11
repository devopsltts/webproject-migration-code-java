package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.management.Notification;
import javax.management.ObjectName;

public class RelationNotification
  extends Notification
{
  private static final long oldSerialVersionUID = -2126464566505527147L;
  private static final long newSerialVersionUID = -6871117877523310399L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myNewRoleValue", ArrayList.class), new ObjectStreamField("myOldRoleValue", ArrayList.class), new ObjectStreamField("myRelId", String.class), new ObjectStreamField("myRelObjName", ObjectName.class), new ObjectStreamField("myRelTypeName", String.class), new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myUnregMBeanList", ArrayList.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("newRoleValue", List.class), new ObjectStreamField("oldRoleValue", List.class), new ObjectStreamField("relationId", String.class), new ObjectStreamField("relationObjName", ObjectName.class), new ObjectStreamField("relationTypeName", String.class), new ObjectStreamField("roleName", String.class), new ObjectStreamField("unregisterMBeanList", List.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
  public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
  public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
  public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
  public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
  public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";
  private String relationId = null;
  private String relationTypeName = null;
  private ObjectName relationObjName = null;
  private List<ObjectName> unregisterMBeanList = null;
  private String roleName = null;
  private List<ObjectName> oldRoleValue = null;
  private List<ObjectName> newRoleValue = null;
  
  public RelationNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, List<ObjectName> paramList)
    throws IllegalArgumentException
  {
    super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
    if ((!isValidBasicStrict(paramString1, paramObject, paramString3, paramString4)) || (!isValidCreate(paramString1))) {
      throw new IllegalArgumentException("Invalid parameter.");
    }
    this.relationId = paramString3;
    this.relationTypeName = paramString4;
    this.relationObjName = safeGetObjectName(paramObjectName);
    this.unregisterMBeanList = safeGetObjectNameList(paramList);
  }
  
  public RelationNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, String paramString5, List<ObjectName> paramList1, List<ObjectName> paramList2)
    throws IllegalArgumentException
  {
    super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
    if ((!isValidBasicStrict(paramString1, paramObject, paramString3, paramString4)) || (!isValidUpdate(paramString1, paramString5, paramList1, paramList2))) {
      throw new IllegalArgumentException("Invalid parameter.");
    }
    this.relationId = paramString3;
    this.relationTypeName = paramString4;
    this.relationObjName = safeGetObjectName(paramObjectName);
    this.roleName = paramString5;
    this.oldRoleValue = safeGetObjectNameList(paramList2);
    this.newRoleValue = safeGetObjectNameList(paramList1);
  }
  
  public String getRelationId()
  {
    return this.relationId;
  }
  
  public String getRelationTypeName()
  {
    return this.relationTypeName;
  }
  
  public ObjectName getObjectName()
  {
    return this.relationObjName;
  }
  
  public List<ObjectName> getMBeansToUnregister()
  {
    Object localObject;
    if (this.unregisterMBeanList != null) {
      localObject = new ArrayList(this.unregisterMBeanList);
    } else {
      localObject = Collections.emptyList();
    }
    return localObject;
  }
  
  public String getRoleName()
  {
    String str = null;
    if (this.roleName != null) {
      str = this.roleName;
    }
    return str;
  }
  
  public List<ObjectName> getOldRoleValue()
  {
    Object localObject;
    if (this.oldRoleValue != null) {
      localObject = new ArrayList(this.oldRoleValue);
    } else {
      localObject = Collections.emptyList();
    }
    return localObject;
  }
  
  public List<ObjectName> getNewRoleValue()
  {
    Object localObject;
    if (this.newRoleValue != null) {
      localObject = new ArrayList(this.newRoleValue);
    } else {
      localObject = Collections.emptyList();
    }
    return localObject;
  }
  
  private boolean isValidBasicStrict(String paramString1, Object paramObject, String paramString2, String paramString3)
  {
    if (paramObject == null) {
      return false;
    }
    return isValidBasic(paramString1, paramObject, paramString2, paramString3);
  }
  
  private boolean isValidBasic(String paramString1, Object paramObject, String paramString2, String paramString3)
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null)) {
      return false;
    }
    return (paramObject == null) || ((paramObject instanceof RelationService)) || ((paramObject instanceof ObjectName));
  }
  
  private boolean isValidCreate(String paramString)
  {
    String[] arrayOfString = { "jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean" };
    HashSet localHashSet = new HashSet(Arrays.asList(arrayOfString));
    return localHashSet.contains(paramString);
  }
  
  private boolean isValidUpdate(String paramString1, String paramString2, List<ObjectName> paramList1, List<ObjectName> paramList2)
  {
    if ((!paramString1.equals("jmx.relation.update.basic")) && (!paramString1.equals("jmx.relation.update.mbean"))) {
      return false;
    }
    return (paramString2 != null) && (paramList2 != null) && (paramList1 != null);
  }
  
  private ArrayList<ObjectName> safeGetObjectNameList(List<ObjectName> paramList)
  {
    ArrayList localArrayList = null;
    if (paramList != null)
    {
      localArrayList = new ArrayList();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        ObjectName localObjectName = (ObjectName)localIterator.next();
        localArrayList.add(ObjectName.getInstance(localObjectName));
      }
    }
    return localArrayList;
  }
  
  private ObjectName safeGetObjectName(ObjectName paramObjectName)
  {
    ObjectName localObjectName = null;
    if (paramObjectName != null) {
      localObjectName = ObjectName.getInstance(paramObjectName);
    }
    return localObjectName;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str1;
    String str2;
    String str3;
    ObjectName localObjectName;
    List localList1;
    List localList2;
    List localList3;
    if (compat)
    {
      str1 = (String)localGetField.get("myRelId", null);
      str2 = (String)localGetField.get("myRelTypeName", null);
      str3 = (String)localGetField.get("myRoleName", null);
      localObjectName = (ObjectName)localGetField.get("myRelObjName", null);
      localList1 = (List)Util.cast(localGetField.get("myNewRoleValue", null));
      localList2 = (List)Util.cast(localGetField.get("myOldRoleValue", null));
      localList3 = (List)Util.cast(localGetField.get("myUnregMBeanList", null));
    }
    else
    {
      str1 = (String)localGetField.get("relationId", null);
      str2 = (String)localGetField.get("relationTypeName", null);
      str3 = (String)localGetField.get("roleName", null);
      localObjectName = (ObjectName)localGetField.get("relationObjName", null);
      localList1 = (List)Util.cast(localGetField.get("newRoleValue", null));
      localList2 = (List)Util.cast(localGetField.get("oldRoleValue", null));
      localList3 = (List)Util.cast(localGetField.get("unregisterMBeanList", null));
    }
    String str4 = super.getType();
    if ((!isValidBasic(str4, super.getSource(), str1, str2)) || ((!isValidCreate(str4)) && (!isValidUpdate(str4, str3, localList1, localList2))))
    {
      super.setSource(null);
      throw new InvalidObjectException("Invalid object read");
    }
    this.relationObjName = safeGetObjectName(localObjectName);
    this.newRoleValue = safeGetObjectNameList(localList1);
    this.oldRoleValue = safeGetObjectNameList(localList2);
    this.unregisterMBeanList = safeGetObjectNameList(localList3);
    this.relationId = str1;
    this.relationTypeName = str2;
    this.roleName = str3;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("myNewRoleValue", this.newRoleValue);
      localPutField.put("myOldRoleValue", this.oldRoleValue);
      localPutField.put("myRelId", this.relationId);
      localPutField.put("myRelObjName", this.relationObjName);
      localPutField.put("myRelTypeName", this.relationTypeName);
      localPutField.put("myRoleName", this.roleName);
      localPutField.put("myUnregMBeanList", this.unregisterMBeanList);
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
      serialVersionUID = -2126464566505527147L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -6871117877523310399L;
    }
  }
}
