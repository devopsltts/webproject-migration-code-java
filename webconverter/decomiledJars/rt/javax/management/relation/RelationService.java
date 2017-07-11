package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class RelationService
  extends NotificationBroadcasterSupport
  implements RelationServiceMBean, MBeanRegistration, NotificationListener
{
  private Map<String, Object> myRelId2ObjMap = new HashMap();
  private Map<String, String> myRelId2RelTypeMap = new HashMap();
  private Map<ObjectName, String> myRelMBeanObjName2RelIdMap = new HashMap();
  private Map<String, RelationType> myRelType2ObjMap = new HashMap();
  private Map<String, List<String>> myRelType2RelIdsMap = new HashMap();
  private final Map<ObjectName, Map<String, List<String>>> myRefedMBeanObjName2RelIdsMap = new HashMap();
  private boolean myPurgeFlag = true;
  private final AtomicLong atomicSeqNo = new AtomicLong();
  private ObjectName myObjName = null;
  private MBeanServer myMBeanServer = null;
  private MBeanServerNotificationFilter myUnregNtfFilter = null;
  private List<MBeanServerNotification> myUnregNtfList = new ArrayList();
  
  public RelationService(boolean paramBoolean)
  {
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "RelationService");
    setPurgeFlag(paramBoolean);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "RelationService");
  }
  
  public void isActive()
    throws RelationServiceNotRegisteredException
  {
    if (this.myMBeanServer == null)
    {
      String str = "Relation Service not registered in the MBean Server.";
      throw new RelationServiceNotRegisteredException(str);
    }
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    this.myMBeanServer = paramMBeanServer;
    this.myObjName = paramObjectName;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {}
  
  public void postDeregister() {}
  
  public boolean getPurgeFlag()
  {
    return this.myPurgeFlag;
  }
  
  public void setPurgeFlag(boolean paramBoolean)
  {
    this.myPurgeFlag = paramBoolean;
  }
  
  public void createRelationType(String paramString, RoleInfo[] paramArrayOfRoleInfo)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if ((paramString == null) || (paramArrayOfRoleInfo == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelationType", paramString);
    Object localObject = new RelationTypeSupport(paramString, paramArrayOfRoleInfo);
    addRelationTypeInt((RelationType)localObject);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelationType");
  }
  
  public void addRelationType(RelationType paramRelationType)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if (paramRelationType == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationType");
    Object localObject1 = paramRelationType.getRoleInfos();
    if (localObject1 == null)
    {
      localObject2 = "No role info provided.";
      throw new InvalidRelationTypeException((String)localObject2);
    }
    Object localObject2 = new RoleInfo[((List)localObject1).size()];
    int i = 0;
    Iterator localIterator = ((List)localObject1).iterator();
    while (localIterator.hasNext())
    {
      RoleInfo localRoleInfo = (RoleInfo)localIterator.next();
      localObject2[i] = localRoleInfo;
      i++;
    }
    RelationTypeSupport.checkRoleInfos((RoleInfo[])localObject2);
    addRelationTypeInt(paramRelationType);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationType");
  }
  
  public List<String> getAllRelationTypeNames()
  {
    ArrayList localArrayList;
    synchronized (this.myRelType2ObjMap)
    {
      localArrayList = new ArrayList(this.myRelType2ObjMap.keySet());
    }
    return localArrayList;
  }
  
  public List<RoleInfo> getRoleInfos(String paramString)
    throws IllegalArgumentException, RelationTypeNotFoundException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfos", paramString);
    Object localObject = getRelationType(paramString);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfos");
    return ((RelationType)localObject).getRoleInfos();
  }
  
  public RoleInfo getRoleInfo(String paramString1, String paramString2)
    throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfo", new Object[] { paramString1, paramString2 });
    Object localObject = getRelationType(paramString1);
    RoleInfo localRoleInfo = ((RelationType)localObject).getRoleInfo(paramString2);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfo");
    return localRoleInfo;
  }
  
  public void removeRelationType(String paramString)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException
  {
    isActive();
    if (paramString == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelationType", paramString);
    Object localObject1 = getRelationType(paramString);
    ArrayList localArrayList = null;
    Object localObject2;
    synchronized (this.myRelType2RelIdsMap)
    {
      localObject2 = (List)this.myRelType2RelIdsMap.get(paramString);
      if (localObject2 != null) {
        localArrayList = new ArrayList((Collection)localObject2);
      }
    }
    synchronized (this.myRelType2ObjMap)
    {
      this.myRelType2ObjMap.remove(paramString);
    }
    synchronized (this.myRelType2RelIdsMap)
    {
      this.myRelType2RelIdsMap.remove(paramString);
    }
    if (localArrayList != null)
    {
      ??? = localArrayList.iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = (String)((Iterator)???).next();
        try
        {
          removeRelation((String)localObject2);
        }
        catch (RelationNotFoundException localRelationNotFoundException)
        {
          throw new RuntimeException(localRelationNotFoundException.getMessage());
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelationType");
  }
  
  public void createRelation(String paramString1, String paramString2, RoleList paramRoleList)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException
  {
    isActive();
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelation", new Object[] { paramString1, paramString2, paramRoleList });
    Object localObject = new RelationSupport(paramString1, this.myObjName, paramString2, paramRoleList);
    addRelationInt(true, (RelationSupport)localObject, null, paramString1, paramString2, paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelation");
  }
  
  public void addRelation(ObjectName paramObjectName)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException
  {
    String str1;
    if (paramObjectName == null)
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelation", paramObjectName);
    isActive();
    if (!this.myMBeanServer.isInstanceOf(paramObjectName, "javax.management.relation.Relation"))
    {
      str1 = "This MBean does not implement the Relation interface.";
      throw new NoSuchMethodException(str1);
    }
    try
    {
      str1 = (String)this.myMBeanServer.getAttribute(paramObjectName, "RelationId");
    }
    catch (MBeanException localMBeanException1)
    {
      throw new RuntimeException(localMBeanException1.getTargetException().getMessage());
    }
    catch (ReflectionException localReflectionException1)
    {
      throw new RuntimeException(localReflectionException1.getMessage());
    }
    catch (AttributeNotFoundException localAttributeNotFoundException1)
    {
      throw new RuntimeException(localAttributeNotFoundException1.getMessage());
    }
    Object localObject1;
    if (str1 == null)
    {
      localObject1 = "This MBean does not provide a relation id.";
      throw new InvalidRelationIdException((String)localObject1);
    }
    try
    {
      localObject1 = (ObjectName)this.myMBeanServer.getAttribute(paramObjectName, "RelationServiceName");
    }
    catch (MBeanException localMBeanException2)
    {
      throw new RuntimeException(localMBeanException2.getTargetException().getMessage());
    }
    catch (ReflectionException localReflectionException2)
    {
      throw new RuntimeException(localReflectionException2.getMessage());
    }
    catch (AttributeNotFoundException localAttributeNotFoundException2)
    {
      throw new RuntimeException(localAttributeNotFoundException2.getMessage());
    }
    int i = 0;
    if (localObject1 == null) {
      i = 1;
    } else if (!((ObjectName)localObject1).equals(this.myObjName)) {
      i = 1;
    }
    String str2;
    if (i != 0)
    {
      str2 = "The Relation Service referenced in the MBean is not the current one.";
      throw new InvalidRelationServiceException(str2);
    }
    try
    {
      str2 = (String)this.myMBeanServer.getAttribute(paramObjectName, "RelationTypeName");
    }
    catch (MBeanException localMBeanException3)
    {
      throw new RuntimeException(localMBeanException3.getTargetException().getMessage());
    }
    catch (ReflectionException localReflectionException3)
    {
      throw new RuntimeException(localReflectionException3.getMessage());
    }
    catch (AttributeNotFoundException localAttributeNotFoundException3)
    {
      throw new RuntimeException(localAttributeNotFoundException3.getMessage());
    }
    Object localObject2;
    if (str2 == null)
    {
      localObject2 = "No relation type provided.";
      throw new RelationTypeNotFoundException((String)localObject2);
    }
    try
    {
      localObject2 = (RoleList)this.myMBeanServer.invoke(paramObjectName, "retrieveAllRoles", null, null);
    }
    catch (MBeanException localMBeanException4)
    {
      throw new RuntimeException(localMBeanException4.getTargetException().getMessage());
    }
    catch (ReflectionException localReflectionException4)
    {
      throw new RuntimeException(localReflectionException4.getMessage());
    }
    addRelationInt(false, null, paramObjectName, str1, str2, (RoleList)localObject2);
    synchronized (this.myRelMBeanObjName2RelIdMap)
    {
      this.myRelMBeanObjName2RelIdMap.put(paramObjectName, str1);
    }
    try
    {
      this.myMBeanServer.setAttribute(paramObjectName, new Attribute("RelationServiceManagementFlag", Boolean.TRUE));
    }
    catch (Exception localException) {}
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramObjectName);
    updateUnregistrationListener(localArrayList, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelation");
  }
  
  public ObjectName isRelationMBean(String paramString)
    throws IllegalArgumentException, RelationNotFoundException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelationMBean", paramString);
    Object localObject = getRelation(paramString);
    if ((localObject instanceof ObjectName)) {
      return (ObjectName)localObject;
    }
    return null;
  }
  
  public String isRelation(ObjectName paramObjectName)
    throws IllegalArgumentException
  {
    if (paramObjectName == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelation", paramObjectName);
    Object localObject1 = null;
    synchronized (this.myRelMBeanObjName2RelIdMap)
    {
      String str = (String)this.myRelMBeanObjName2RelIdMap.get(paramObjectName);
      if (str != null) {
        localObject1 = str;
      }
    }
    return localObject1;
  }
  
  public Boolean hasRelation(String paramString)
    throws IllegalArgumentException
  {
    Object localObject;
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "hasRelation", paramString);
    try
    {
      localObject = getRelation(paramString);
      return Boolean.valueOf(true);
    }
    catch (RelationNotFoundException localRelationNotFoundException) {}
    return Boolean.valueOf(false);
  }
  
  public List<String> getAllRelationIds()
  {
    ArrayList localArrayList;
    synchronized (this.myRelId2ObjMap)
    {
      localArrayList = new ArrayList(this.myRelId2ObjMap.keySet());
    }
    return localArrayList;
  }
  
  public Integer checkRoleReading(String paramString1, String paramString2)
    throws IllegalArgumentException, RelationTypeNotFoundException
  {
    Object localObject;
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleReading", new Object[] { paramString1, paramString2 });
    RelationType localRelationType = getRelationType(paramString2);
    try
    {
      RoleInfo localRoleInfo = localRelationType.getRoleInfo(paramString1);
      localObject = checkRoleInt(1, paramString1, null, localRoleInfo, false);
    }
    catch (RoleInfoNotFoundException localRoleInfoNotFoundException)
    {
      localObject = Integer.valueOf(1);
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleReading");
    return localObject;
  }
  
  public Integer checkRoleWriting(Role paramRole, String paramString, Boolean paramBoolean)
    throws IllegalArgumentException, RelationTypeNotFoundException
  {
    if ((paramRole == null) || (paramString == null) || (paramBoolean == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleWriting", new Object[] { paramRole, paramString, paramBoolean });
    Object localObject = getRelationType(paramString);
    String str = paramRole.getRoleName();
    List localList = paramRole.getRoleValue();
    boolean bool = true;
    if (paramBoolean.booleanValue()) {
      bool = false;
    }
    RoleInfo localRoleInfo;
    try
    {
      localRoleInfo = ((RelationType)localObject).getRoleInfo(str);
    }
    catch (RoleInfoNotFoundException localRoleInfoNotFoundException)
    {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
      return Integer.valueOf(1);
    }
    Integer localInteger = checkRoleInt(2, str, localList, localRoleInfo, bool);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
    return localInteger;
  }
  
  public void sendRelationCreationNotification(String paramString)
    throws IllegalArgumentException, RelationNotFoundException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationCreationNotification", paramString);
    Object localObject = new StringBuilder("Creation of relation ");
    ((StringBuilder)localObject).append(paramString);
    sendNotificationInt(1, ((StringBuilder)localObject).toString(), paramString, null, null, null, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationCreationNotification");
  }
  
  public void sendRoleUpdateNotification(String paramString, Role paramRole, List<ObjectName> paramList)
    throws IllegalArgumentException, RelationNotFoundException
  {
    if ((paramString == null) || (paramRole == null) || (paramList == null))
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    if (!(paramList instanceof ArrayList)) {
      paramList = new ArrayList(paramList);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRoleUpdateNotification", new Object[] { paramString, paramRole, paramList });
    String str1 = paramRole.getRoleName();
    List localList = paramRole.getRoleValue();
    String str2 = Role.roleValueToString(localList);
    String str3 = Role.roleValueToString(paramList);
    StringBuilder localStringBuilder = new StringBuilder("Value of role ");
    localStringBuilder.append(str1);
    localStringBuilder.append(" has changed\nOld value:\n");
    localStringBuilder.append(str3);
    localStringBuilder.append("\nNew value:\n");
    localStringBuilder.append(str2);
    sendNotificationInt(2, localStringBuilder.toString(), paramString, null, str1, localList, paramList);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRoleUpdateNotification");
  }
  
  public void sendRelationRemovalNotification(String paramString, List<ObjectName> paramList)
    throws IllegalArgumentException, RelationNotFoundException
  {
    if (paramString == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationRemovalNotification", new Object[] { paramString, paramList });
    sendNotificationInt(3, "Removal of relation " + paramString, paramString, paramList, null, null, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationRemovalNotification");
  }
  
  public void updateRoleMap(String paramString, Role paramRole, List<ObjectName> paramList)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException
  {
    if ((paramString == null) || (paramRole == null) || (paramList == null))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateRoleMap", new Object[] { paramString, paramRole, paramList });
    isActive();
    Object localObject1 = getRelation(paramString);
    String str = paramRole.getRoleName();
    List localList = paramRole.getRoleValue();
    ArrayList localArrayList1 = new ArrayList(paramList);
    ArrayList localArrayList2 = new ArrayList();
    Object localObject2 = localList.iterator();
    boolean bool;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (ObjectName)((Iterator)localObject2).next();
      int i = localArrayList1.indexOf(localObject3);
      if (i == -1)
      {
        bool = addNewMBeanReference((ObjectName)localObject3, paramString, str);
        if (bool) {
          localArrayList2.add(localObject3);
        }
      }
      else
      {
        localArrayList1.remove(i);
      }
    }
    localObject2 = new ArrayList();
    Object localObject3 = localArrayList1.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      ObjectName localObjectName = (ObjectName)((Iterator)localObject3).next();
      bool = removeMBeanReference(localObjectName, paramString, str, false);
      if (bool) {
        ((List)localObject2).add(localObjectName);
      }
    }
    updateUnregistrationListener(localArrayList2, (List)localObject2);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateRoleMap");
  }
  
  public void removeRelation(String paramString)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException
  {
    isActive();
    if (paramString == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelation", paramString);
    Object localObject1 = getRelation(paramString);
    if ((localObject1 instanceof ObjectName))
    {
      localArrayList1 = new ArrayList();
      localArrayList1.add((ObjectName)localObject1);
      updateUnregistrationListener(null, localArrayList1);
    }
    sendRelationRemovalNotification(paramString, null);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Object localObject2;
    synchronized (this.myRefedMBeanObjName2RelIdsMap)
    {
      Iterator localIterator = this.myRefedMBeanObjName2RelIdsMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (ObjectName)localIterator.next();
        Map localMap = (Map)this.myRefedMBeanObjName2RelIdsMap.get(localObject2);
        if (localMap.containsKey(paramString))
        {
          localMap.remove(paramString);
          localArrayList1.add(localObject2);
        }
        if (localMap.isEmpty()) {
          localArrayList2.add(localObject2);
        }
      }
      localIterator = localArrayList2.iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (ObjectName)localIterator.next();
        this.myRefedMBeanObjName2RelIdsMap.remove(localObject2);
      }
    }
    synchronized (this.myRelId2ObjMap)
    {
      this.myRelId2ObjMap.remove(paramString);
    }
    if ((localObject1 instanceof ObjectName)) {
      synchronized (this.myRelMBeanObjName2RelIdMap)
      {
        this.myRelMBeanObjName2RelIdMap.remove((ObjectName)localObject1);
      }
    }
    synchronized (this.myRelId2RelTypeMap)
    {
      ??? = (String)this.myRelId2RelTypeMap.get(paramString);
      this.myRelId2RelTypeMap.remove(paramString);
    }
    synchronized (this.myRelType2RelIdsMap)
    {
      localObject2 = (List)this.myRelType2RelIdsMap.get(???);
      if (localObject2 != null)
      {
        ((List)localObject2).remove(paramString);
        if (((List)localObject2).isEmpty()) {
          this.myRelType2RelIdsMap.remove(???);
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelation");
  }
  
  public void purgeRelations()
    throws RelationServiceNotRegisteredException
  {
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "purgeRelations");
    isActive();
    ArrayList localArrayList;
    synchronized (this.myRefedMBeanObjName2RelIdsMap)
    {
      localArrayList = new ArrayList(this.myUnregNtfList);
      this.myUnregNtfList = new ArrayList();
    }
    ??? = new ArrayList();
    HashMap localHashMap = new HashMap();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    synchronized (this.myRefedMBeanObjName2RelIdsMap)
    {
      localObject2 = localArrayList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (MBeanServerNotification)((Iterator)localObject2).next();
        localObject4 = ((MBeanServerNotification)localObject3).getMBeanName();
        ((List)???).add(localObject4);
        localObject5 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(localObject4);
        localHashMap.put(localObject4, localObject5);
        this.myRefedMBeanObjName2RelIdsMap.remove(localObject4);
      }
    }
    updateUnregistrationListener(null, (List)???);
    ??? = localArrayList.iterator();
    while (((Iterator)???).hasNext())
    {
      localObject2 = (MBeanServerNotification)((Iterator)???).next();
      localObject3 = ((MBeanServerNotification)localObject2).getMBeanName();
      localObject4 = (Map)localHashMap.get(localObject3);
      localObject5 = ((Map)localObject4).entrySet().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject5).next();
        String str = (String)localEntry.getKey();
        List localList = (List)localEntry.getValue();
        try
        {
          handleReferenceUnregistration(str, (ObjectName)localObject3, localList);
        }
        catch (RelationNotFoundException localRelationNotFoundException)
        {
          throw new RuntimeException(localRelationNotFoundException.getMessage());
        }
        catch (RoleNotFoundException localRoleNotFoundException)
        {
          throw new RuntimeException(localRoleNotFoundException.getMessage());
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "purgeRelations");
  }
  
  public Map<String, List<String>> findReferencingRelations(ObjectName paramObjectName, String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    if (paramObjectName == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findReferencingRelations", new Object[] { paramObjectName, paramString1, paramString2 });
    Object localObject1 = new HashMap();
    synchronized (this.myRefedMBeanObjName2RelIdsMap)
    {
      Map localMap = (Map)this.myRefedMBeanObjName2RelIdsMap.get(paramObjectName);
      if (localMap != null)
      {
        Set localSet = localMap.keySet();
        ArrayList localArrayList;
        String str;
        Object localObject2;
        if (paramString1 == null)
        {
          localArrayList = new ArrayList(localSet);
        }
        else
        {
          localArrayList = new ArrayList();
          localIterator = localSet.iterator();
          while (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            synchronized (this.myRelId2RelTypeMap)
            {
              localObject2 = (String)this.myRelId2RelTypeMap.get(str);
            }
            if (((String)localObject2).equals(paramString1)) {
              localArrayList.add(str);
            }
          }
        }
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          localObject2 = (List)localMap.get(str);
          if (paramString2 == null)
          {
            ((Map)localObject1).put(str, new ArrayList((Collection)localObject2));
          }
          else if (((List)localObject2).contains(paramString2))
          {
            ??? = new ArrayList();
            ((List)???).add(paramString2);
            ((Map)localObject1).put(str, ???);
          }
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findReferencingRelations");
    return localObject1;
  }
  
  public Map<ObjectName, List<String>> findAssociatedMBeans(ObjectName paramObjectName, String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    if (paramObjectName == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findAssociatedMBeans", new Object[] { paramObjectName, paramString1, paramString2 });
    Object localObject1 = findReferencingRelations(paramObjectName, paramString1, paramString2);
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = ((Map)localObject1).keySet().iterator();
    while (localIterator1.hasNext())
    {
      String str = (String)localIterator1.next();
      Map localMap;
      try
      {
        localMap = getReferencedMBeans(str);
      }
      catch (RelationNotFoundException localRelationNotFoundException)
      {
        throw new RuntimeException(localRelationNotFoundException.getMessage());
      }
      Iterator localIterator2 = localMap.keySet().iterator();
      while (localIterator2.hasNext())
      {
        ObjectName localObjectName = (ObjectName)localIterator2.next();
        if (!localObjectName.equals(paramObjectName))
        {
          Object localObject2 = (List)localHashMap.get(localObjectName);
          if (localObject2 == null)
          {
            localObject2 = new ArrayList();
            ((List)localObject2).add(str);
            localHashMap.put(localObjectName, localObject2);
          }
          else
          {
            ((List)localObject2).add(str);
          }
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findAssociatedMBeans");
    return localHashMap;
  }
  
  public List<String> findRelationsOfType(String paramString)
    throws IllegalArgumentException, RelationTypeNotFoundException
  {
    if (paramString == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findRelationsOfType");
    Object localObject1 = getRelationType(paramString);
    ArrayList localArrayList;
    synchronized (this.myRelType2RelIdsMap)
    {
      List localList = (List)this.myRelType2RelIdsMap.get(paramString);
      if (localList == null) {
        localArrayList = new ArrayList();
      } else {
        localArrayList = new ArrayList(localList);
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findRelationsOfType");
    return localArrayList;
  }
  
  public List<ObjectName> getRole(String paramString1, String paramString2)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRole", new Object[] { paramString1, paramString2 });
    isActive();
    Object localObject1 = getRelation(paramString1);
    Object localObject2;
    if ((localObject1 instanceof RelationSupport))
    {
      localObject2 = (List)Util.cast(((RelationSupport)localObject1).getRoleInt(paramString2, true, this, false));
    }
    else
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramString2;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "java.lang.String";
      try
      {
        List localList = (List)Util.cast(this.myMBeanServer.invoke((ObjectName)localObject1, "getRole", arrayOfObject, arrayOfString));
        if ((localList == null) || ((localList instanceof ArrayList))) {
          localObject2 = localList;
        } else {
          localObject2 = new ArrayList(localList);
        }
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RuntimeException(localInstanceNotFoundException.getMessage());
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        Exception localException = localMBeanException.getTargetException();
        if ((localException instanceof RoleNotFoundException)) {
          throw ((RoleNotFoundException)localException);
        }
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRole");
    return localObject2;
  }
  
  public RoleResult getRoles(String paramString, String[] paramArrayOfString)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException
  {
    if ((paramString == null) || (paramArrayOfString == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", paramString);
    isActive();
    Object localObject = getRelation(paramString);
    RoleResult localRoleResult;
    if ((localObject instanceof RelationSupport))
    {
      localRoleResult = ((RelationSupport)localObject).getRolesInt(paramArrayOfString, true, this);
    }
    else
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramArrayOfString;
      String[] arrayOfString = new String[1];
      try
      {
        arrayOfString[0] = paramArrayOfString.getClass().getName();
      }
      catch (Exception localException) {}
      try
      {
        localRoleResult = (RoleResult)this.myMBeanServer.invoke((ObjectName)localObject, "getRoles", arrayOfObject, arrayOfString);
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RuntimeException(localInstanceNotFoundException.getMessage());
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        throw new RuntimeException(localMBeanException.getTargetException().getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
    return localRoleResult;
  }
  
  public RoleResult getAllRoles(String paramString)
    throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", paramString);
    Object localObject = getRelation(paramString);
    RoleResult localRoleResult;
    if ((localObject instanceof RelationSupport)) {
      localRoleResult = ((RelationSupport)localObject).getAllRolesInt(true, this);
    } else {
      try
      {
        localRoleResult = (RoleResult)this.myMBeanServer.getAttribute((ObjectName)localObject, "AllRoles");
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
    return localRoleResult;
  }
  
  public Integer getRoleCardinality(String paramString1, String paramString2)
    throws IllegalArgumentException, RelationNotFoundException, RoleNotFoundException
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleCardinality", new Object[] { paramString1, paramString2 });
    Object localObject = getRelation(paramString1);
    Integer localInteger;
    if ((localObject instanceof RelationSupport))
    {
      localInteger = ((RelationSupport)localObject).getRoleCardinality(paramString2);
    }
    else
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramString2;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "java.lang.String";
      try
      {
        localInteger = (Integer)this.myMBeanServer.invoke((ObjectName)localObject, "getRoleCardinality", arrayOfObject, arrayOfString);
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RuntimeException(localInstanceNotFoundException.getMessage());
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        Exception localException = localMBeanException.getTargetException();
        if ((localException instanceof RoleNotFoundException)) {
          throw ((RoleNotFoundException)localException);
        }
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleCardinality");
    return localInteger;
  }
  
  public void setRole(String paramString, Role paramRole)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException
  {
    if ((paramString == null) || (paramRole == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRole", new Object[] { paramString, paramRole });
    isActive();
    Object localObject = getRelation(paramString);
    if ((localObject instanceof RelationSupport))
    {
      try
      {
        ((RelationSupport)localObject).setRoleInt(paramRole, true, this, false);
      }
      catch (RelationTypeNotFoundException localRelationTypeNotFoundException)
      {
        throw new RuntimeException(localRelationTypeNotFoundException.getMessage());
      }
    }
    else
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramRole;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "javax.management.relation.Role";
      try
      {
        this.myMBeanServer.setAttribute((ObjectName)localObject, new Attribute("Role", paramRole));
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RuntimeException(localInstanceNotFoundException.getMessage());
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        Exception localException = localMBeanException.getTargetException();
        if ((localException instanceof RoleNotFoundException)) {
          throw ((RoleNotFoundException)localException);
        }
        if ((localException instanceof InvalidRoleValueException)) {
          throw ((InvalidRoleValueException)localException);
        }
        throw new RuntimeException(localException.getMessage());
      }
      catch (AttributeNotFoundException localAttributeNotFoundException)
      {
        throw new RuntimeException(localAttributeNotFoundException.getMessage());
      }
      catch (InvalidAttributeValueException localInvalidAttributeValueException)
      {
        throw new RuntimeException(localInvalidAttributeValueException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRole");
  }
  
  public RoleResult setRoles(String paramString, RoleList paramRoleList)
    throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException
  {
    if ((paramString == null) || (paramRoleList == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRoles", new Object[] { paramString, paramRoleList });
    isActive();
    Object localObject = getRelation(paramString);
    RoleResult localRoleResult;
    if ((localObject instanceof RelationSupport))
    {
      try
      {
        localRoleResult = ((RelationSupport)localObject).setRolesInt(paramRoleList, true, this);
      }
      catch (RelationTypeNotFoundException localRelationTypeNotFoundException)
      {
        throw new RuntimeException(localRelationTypeNotFoundException.getMessage());
      }
    }
    else
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramRoleList;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "javax.management.relation.RoleList";
      try
      {
        localRoleResult = (RoleResult)this.myMBeanServer.invoke((ObjectName)localObject, "setRoles", arrayOfObject, arrayOfString);
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RuntimeException(localInstanceNotFoundException.getMessage());
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        throw new RuntimeException(localMBeanException.getTargetException().getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRoles");
    return localRoleResult;
  }
  
  public Map<ObjectName, List<String>> getReferencedMBeans(String paramString)
    throws IllegalArgumentException, RelationNotFoundException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getReferencedMBeans", paramString);
    Object localObject = getRelation(paramString);
    Map localMap;
    if ((localObject instanceof RelationSupport)) {
      localMap = ((RelationSupport)localObject).getReferencedMBeans();
    } else {
      try
      {
        localMap = (Map)Util.cast(this.myMBeanServer.getAttribute((ObjectName)localObject, "ReferencedMBeans"));
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getReferencedMBeans");
    return localMap;
  }
  
  public String getRelationTypeName(String paramString)
    throws IllegalArgumentException, RelationNotFoundException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationTypeName", paramString);
    Object localObject = getRelation(paramString);
    String str;
    if ((localObject instanceof RelationSupport)) {
      str = ((RelationSupport)localObject).getRelationTypeName();
    } else {
      try
      {
        str = (String)this.myMBeanServer.getAttribute((ObjectName)localObject, "RelationTypeName");
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationTypeName");
    return str;
  }
  
  public void handleNotification(Notification paramNotification, Object paramObject)
  {
    Object localObject1;
    if (paramNotification == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleNotification", paramNotification);
    if ((paramNotification instanceof MBeanServerNotification))
    {
      localObject1 = (MBeanServerNotification)paramNotification;
      String str = paramNotification.getType();
      if (str.equals("JMX.mbean.unregistered"))
      {
        ObjectName localObjectName = ((MBeanServerNotification)paramNotification).getMBeanName();
        int i = 0;
        synchronized (this.myRefedMBeanObjName2RelIdsMap)
        {
          if (this.myRefedMBeanObjName2RelIdsMap.containsKey(localObjectName))
          {
            synchronized (this.myUnregNtfList)
            {
              this.myUnregNtfList.add(localObject1);
            }
            i = 1;
          }
          if ((i != 0) && (this.myPurgeFlag)) {
            try
            {
              purgeRelations();
            }
            catch (Exception ???)
            {
              throw new RuntimeException(???.getMessage());
            }
          }
        }
        synchronized (this.myRelMBeanObjName2RelIdMap)
        {
          ??? = (String)this.myRelMBeanObjName2RelIdMap.get(localObjectName);
        }
        if (??? != null) {
          try
          {
            removeRelation((String)???);
          }
          catch (Exception localException)
          {
            throw new RuntimeException(localException.getMessage());
          }
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleNotification");
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getNotificationInfo");
    String str1 = "javax.management.relation.RelationNotification";
    String[] arrayOfString = { "jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.update.basic", "jmx.relation.update.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean" };
    String str2 = "Sent when a relation is created, updated or deleted.";
    MBeanNotificationInfo localMBeanNotificationInfo = new MBeanNotificationInfo(arrayOfString, str1, str2);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getNotificationInfo");
    return new MBeanNotificationInfo[] { localMBeanNotificationInfo };
  }
  
  private void addRelationTypeInt(RelationType paramRelationType)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if (paramRelationType == null)
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationTypeInt");
    String str1 = paramRelationType.getRelationTypeName();
    try
    {
      RelationType localRelationType = getRelationType(str1);
      if (localRelationType != null)
      {
        String str2 = "There is already a relation type in the Relation Service with name ";
        StringBuilder localStringBuilder = new StringBuilder(str2);
        localStringBuilder.append(str1);
        throw new InvalidRelationTypeException(localStringBuilder.toString());
      }
    }
    catch (RelationTypeNotFoundException localRelationTypeNotFoundException) {}
    synchronized (this.myRelType2ObjMap)
    {
      this.myRelType2ObjMap.put(str1, paramRelationType);
    }
    if ((paramRelationType instanceof RelationTypeSupport)) {
      ((RelationTypeSupport)paramRelationType).setRelationServiceFlag(true);
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationTypeInt");
  }
  
  RelationType getRelationType(String paramString)
    throws IllegalArgumentException, RelationTypeNotFoundException
  {
    Object localObject1;
    if (paramString == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationType", paramString);
    synchronized (this.myRelType2ObjMap)
    {
      localObject1 = (RelationType)this.myRelType2ObjMap.get(paramString);
    }
    if (localObject1 == null)
    {
      ??? = "No relation type created in the Relation Service with the name ";
      StringBuilder localStringBuilder = new StringBuilder((String)???);
      localStringBuilder.append(paramString);
      throw new RelationTypeNotFoundException(localStringBuilder.toString());
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationType");
    return localObject1;
  }
  
  Object getRelation(String paramString)
    throws IllegalArgumentException, RelationNotFoundException
  {
    Object localObject1;
    if (paramString == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelation", paramString);
    synchronized (this.myRelId2ObjMap)
    {
      localObject1 = this.myRelId2ObjMap.get(paramString);
    }
    if (localObject1 == null)
    {
      ??? = "No relation associated to relation id " + paramString;
      throw new RelationNotFoundException((String)???);
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelation");
    return localObject1;
  }
  
  private boolean addNewMBeanReference(ObjectName paramObjectName, String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    if ((paramObjectName == null) || (paramString1 == null) || (paramString2 == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addNewMBeanReference", new Object[] { paramObjectName, paramString1, paramString2 });
    boolean bool = false;
    synchronized (this.myRefedMBeanObjName2RelIdsMap)
    {
      Object localObject1 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(paramObjectName);
      Object localObject2;
      if (localObject1 == null)
      {
        bool = true;
        localObject2 = new ArrayList();
        ((List)localObject2).add(paramString2);
        localObject1 = new HashMap();
        ((Map)localObject1).put(paramString1, localObject2);
        this.myRefedMBeanObjName2RelIdsMap.put(paramObjectName, localObject1);
      }
      else
      {
        localObject2 = (List)((Map)localObject1).get(paramString1);
        if (localObject2 == null)
        {
          localObject2 = new ArrayList();
          ((List)localObject2).add(paramString2);
          ((Map)localObject1).put(paramString1, localObject2);
        }
        else
        {
          ((List)localObject2).add(paramString2);
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addNewMBeanReference");
    return bool;
  }
  
  private boolean removeMBeanReference(ObjectName paramObjectName, String paramString1, String paramString2, boolean paramBoolean)
    throws IllegalArgumentException
  {
    if ((paramObjectName == null) || (paramString1 == null) || (paramString2 == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeMBeanReference", new Object[] { paramObjectName, paramString1, paramString2, Boolean.valueOf(paramBoolean) });
    boolean bool = false;
    synchronized (this.myRefedMBeanObjName2RelIdsMap)
    {
      Map localMap = (Map)this.myRefedMBeanObjName2RelIdsMap.get(paramObjectName);
      if (localMap == null)
      {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
        return true;
      }
      List localList = null;
      if (!paramBoolean)
      {
        localList = (List)localMap.get(paramString1);
        int i = localList.indexOf(paramString2);
        if (i != -1) {
          localList.remove(i);
        }
      }
      if ((localList.isEmpty()) || (paramBoolean)) {
        localMap.remove(paramString1);
      }
      if (localMap.isEmpty())
      {
        this.myRefedMBeanObjName2RelIdsMap.remove(paramObjectName);
        bool = true;
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
    return bool;
  }
  
  private void updateUnregistrationListener(List<ObjectName> paramList1, List<ObjectName> paramList2)
    throws RelationServiceNotRegisteredException
  {
    if ((paramList1 != null) && (paramList2 != null) && (paramList1.isEmpty()) && (paramList2.isEmpty())) {
      return;
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateUnregistrationListener", new Object[] { paramList1, paramList2 });
    isActive();
    if ((paramList1 != null) || (paramList2 != null))
    {
      int i = 0;
      if (this.myUnregNtfFilter == null)
      {
        this.myUnregNtfFilter = new MBeanServerNotificationFilter();
        i = 1;
      }
      synchronized (this.myUnregNtfFilter)
      {
        Iterator localIterator;
        ObjectName localObjectName;
        if (paramList1 != null)
        {
          localIterator = paramList1.iterator();
          while (localIterator.hasNext())
          {
            localObjectName = (ObjectName)localIterator.next();
            this.myUnregNtfFilter.enableObjectName(localObjectName);
          }
        }
        if (paramList2 != null)
        {
          localIterator = paramList2.iterator();
          while (localIterator.hasNext())
          {
            localObjectName = (ObjectName)localIterator.next();
            this.myUnregNtfFilter.disableObjectName(localObjectName);
          }
        }
        if (i != 0) {
          try
          {
            this.myMBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this, this.myUnregNtfFilter, null);
          }
          catch (InstanceNotFoundException localInstanceNotFoundException)
          {
            throw new RelationServiceNotRegisteredException(localInstanceNotFoundException.getMessage());
          }
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateUnregistrationListener");
  }
  
  private void addRelationInt(boolean paramBoolean, RelationSupport paramRelationSupport, ObjectName paramObjectName, String paramString1, String paramString2, RoleList paramRoleList)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException
  {
    Object localObject1;
    if ((paramString1 == null) || (paramString2 == null) || ((paramBoolean) && ((paramRelationSupport == null) || (paramObjectName != null))) || ((!paramBoolean) && ((paramObjectName == null) || (paramRelationSupport != null))))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationInt", new Object[] { Boolean.valueOf(paramBoolean), paramRelationSupport, paramObjectName, paramString1, paramString2, paramRoleList });
    isActive();
    Object localObject3;
    try
    {
      localObject1 = getRelation(paramString1);
      if (localObject1 != null)
      {
        localObject2 = "There is already a relation with id ";
        localObject3 = new StringBuilder((String)localObject2);
        ((StringBuilder)localObject3).append(paramString1);
        throw new InvalidRelationIdException(((StringBuilder)localObject3).toString());
      }
    }
    catch (RelationNotFoundException localRelationNotFoundException1) {}
    RelationType localRelationType = getRelationType(paramString2);
    Object localObject2 = new ArrayList(localRelationType.getRoleInfos());
    Object localObject4;
    if (paramRoleList != null)
    {
      localObject3 = paramRoleList.asList().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (Role)((Iterator)localObject3).next();
        String str = ((Role)localObject4).getRoleName();
        List localList = ((Role)localObject4).getRoleValue();
        RoleInfo localRoleInfo;
        try
        {
          localRoleInfo = localRelationType.getRoleInfo(str);
        }
        catch (RoleInfoNotFoundException localRoleInfoNotFoundException)
        {
          throw new RoleNotFoundException(localRoleInfoNotFoundException.getMessage());
        }
        Integer localInteger = checkRoleInt(2, str, localList, localRoleInfo, false);
        int j = localInteger.intValue();
        if (j != 0) {
          throwRoleProblemException(j, str);
        }
        int k = ((List)localObject2).indexOf(localRoleInfo);
        ((List)localObject2).remove(k);
      }
    }
    initializeMissingRoles(paramBoolean, paramRelationSupport, paramObjectName, paramString1, paramString2, (List)localObject2);
    synchronized (this.myRelId2ObjMap)
    {
      if (paramBoolean) {
        this.myRelId2ObjMap.put(paramString1, paramRelationSupport);
      } else {
        this.myRelId2ObjMap.put(paramString1, paramObjectName);
      }
    }
    synchronized (this.myRelId2RelTypeMap)
    {
      this.myRelId2RelTypeMap.put(paramString1, paramString2);
    }
    synchronized (this.myRelType2RelIdsMap)
    {
      localObject4 = (List)this.myRelType2RelIdsMap.get(paramString2);
      int i = 0;
      if (localObject4 == null)
      {
        i = 1;
        localObject4 = new ArrayList();
      }
      ((List)localObject4).add(paramString1);
      if (i != 0) {
        this.myRelType2RelIdsMap.put(paramString2, localObject4);
      }
    }
    ??? = paramRoleList.asList().iterator();
    while (((Iterator)???).hasNext())
    {
      localObject4 = (Role)((Iterator)???).next();
      ArrayList localArrayList = new ArrayList();
      try
      {
        updateRoleMap(paramString1, (Role)localObject4, localArrayList);
      }
      catch (RelationNotFoundException localRelationNotFoundException3) {}
    }
    try
    {
      sendRelationCreationNotification(paramString1);
    }
    catch (RelationNotFoundException localRelationNotFoundException2) {}
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationInt");
  }
  
  private Integer checkRoleInt(int paramInt, String paramString, List<ObjectName> paramList, RoleInfo paramRoleInfo, boolean paramBoolean)
    throws IllegalArgumentException
  {
    if ((paramString == null) || (paramRoleInfo == null) || ((paramInt == 2) && (paramList == null)))
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleInt", new Object[] { Integer.valueOf(paramInt), paramString, paramList, paramRoleInfo, Boolean.valueOf(paramBoolean) });
    String str1 = paramRoleInfo.getName();
    if (!paramString.equals(str1))
    {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return Integer.valueOf(1);
    }
    boolean bool1;
    if (paramInt == 1)
    {
      bool1 = paramRoleInfo.isReadable();
      if (!bool1)
      {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return Integer.valueOf(2);
      }
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return new Integer(0);
    }
    if (paramBoolean)
    {
      bool1 = paramRoleInfo.isWritable();
      if (!bool1)
      {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return new Integer(3);
      }
    }
    int i = paramList.size();
    boolean bool2 = paramRoleInfo.checkMinDegree(i);
    if (!bool2)
    {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return new Integer(4);
    }
    boolean bool3 = paramRoleInfo.checkMaxDegree(i);
    if (!bool3)
    {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return new Integer(5);
    }
    String str2 = paramRoleInfo.getRefMBeanClassName();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ObjectName localObjectName = (ObjectName)localIterator.next();
      if (localObjectName == null)
      {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return new Integer(7);
      }
      try
      {
        boolean bool4 = this.myMBeanServer.isInstanceOf(localObjectName, str2);
        if (!bool4)
        {
          JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
          return new Integer(6);
        }
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return new Integer(7);
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
    return new Integer(0);
  }
  
  private void initializeMissingRoles(boolean paramBoolean, RelationSupport paramRelationSupport, ObjectName paramObjectName, String paramString1, String paramString2, List<RoleInfo> paramList)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, InvalidRoleValueException
  {
    if (((paramBoolean) && ((paramRelationSupport == null) || (paramObjectName != null))) || ((!paramBoolean) && ((paramObjectName == null) || (paramRelationSupport != null))) || (paramString1 == null) || (paramString2 == null) || (paramList == null))
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "initializeMissingRoles", new Object[] { Boolean.valueOf(paramBoolean), paramRelationSupport, paramObjectName, paramString1, paramString2, paramList });
    isActive();
    Object localObject = paramList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      RoleInfo localRoleInfo = (RoleInfo)((Iterator)localObject).next();
      String str = localRoleInfo.getName();
      ArrayList localArrayList = new ArrayList();
      Role localRole = new Role(str, localArrayList);
      if (paramBoolean)
      {
        try
        {
          paramRelationSupport.setRoleInt(localRole, true, this, false);
        }
        catch (RoleNotFoundException localRoleNotFoundException)
        {
          throw new RuntimeException(localRoleNotFoundException.getMessage());
        }
        catch (RelationNotFoundException localRelationNotFoundException)
        {
          throw new RuntimeException(localRelationNotFoundException.getMessage());
        }
        catch (RelationTypeNotFoundException localRelationTypeNotFoundException)
        {
          throw new RuntimeException(localRelationTypeNotFoundException.getMessage());
        }
      }
      else
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = localRole;
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "javax.management.relation.Role";
        try
        {
          this.myMBeanServer.setAttribute(paramObjectName, new Attribute("Role", localRole));
        }
        catch (InstanceNotFoundException localInstanceNotFoundException)
        {
          throw new RuntimeException(localInstanceNotFoundException.getMessage());
        }
        catch (ReflectionException localReflectionException)
        {
          throw new RuntimeException(localReflectionException.getMessage());
        }
        catch (MBeanException localMBeanException)
        {
          Exception localException = localMBeanException.getTargetException();
          if ((localException instanceof InvalidRoleValueException)) {
            throw ((InvalidRoleValueException)localException);
          }
          throw new RuntimeException(localException.getMessage());
        }
        catch (AttributeNotFoundException localAttributeNotFoundException)
        {
          throw new RuntimeException(localAttributeNotFoundException.getMessage());
        }
        catch (InvalidAttributeValueException localInvalidAttributeValueException)
        {
          throw new RuntimeException(localInvalidAttributeValueException.getMessage());
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "initializeMissingRoles");
  }
  
  static void throwRoleProblemException(int paramInt, String paramString)
    throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException
  {
    if (paramString == null)
    {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    int i = 0;
    String str2 = null;
    switch (paramInt)
    {
    case 1: 
      str2 = " does not exist in relation.";
      i = 1;
      break;
    case 2: 
      str2 = " is not readable.";
      i = 1;
      break;
    case 3: 
      str2 = " is not writable.";
      i = 1;
      break;
    case 4: 
      str2 = " has a number of MBean references less than the expected minimum degree.";
      i = 2;
      break;
    case 5: 
      str2 = " has a number of MBean references greater than the expected maximum degree.";
      i = 2;
      break;
    case 6: 
      str2 = " has an MBean reference to an MBean not of the expected class of references for that role.";
      i = 2;
      break;
    case 7: 
      str2 = " has a reference to null or to an MBean not registered.";
      i = 2;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    localStringBuilder.append(str2);
    String str3 = localStringBuilder.toString();
    if (i == 1) {
      throw new RoleNotFoundException(str3);
    }
    if (i == 2) {
      throw new InvalidRoleValueException(str3);
    }
  }
  
  private void sendNotificationInt(int paramInt, String paramString1, String paramString2, List<ObjectName> paramList1, String paramString3, List<ObjectName> paramList2, List<ObjectName> paramList3)
    throws IllegalArgumentException, RelationNotFoundException
  {
    String str1;
    if ((paramString1 == null) || (paramString2 == null) || ((paramInt != 3) && (paramList1 != null)) || ((paramInt == 2) && ((paramString3 == null) || (paramList2 == null) || (paramList3 == null))))
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendNotificationInt", new Object[] { Integer.valueOf(paramInt), paramString1, paramString2, paramList1, paramString3, paramList2, paramList3 });
    synchronized (this.myRelId2RelTypeMap)
    {
      str1 = (String)this.myRelId2RelTypeMap.get(paramString2);
    }
    ??? = isRelationMBean(paramString2);
    String str2 = null;
    if (??? != null) {
      switch (paramInt)
      {
      case 1: 
        str2 = "jmx.relation.creation.mbean";
        break;
      case 2: 
        str2 = "jmx.relation.update.mbean";
        break;
      case 3: 
        str2 = "jmx.relation.removal.mbean";
      }
    } else {
      switch (paramInt)
      {
      case 1: 
        str2 = "jmx.relation.creation.basic";
        break;
      case 2: 
        str2 = "jmx.relation.update.basic";
        break;
      case 3: 
        str2 = "jmx.relation.removal.basic";
      }
    }
    Long localLong = Long.valueOf(this.atomicSeqNo.incrementAndGet());
    Date localDate = new Date();
    long l = localDate.getTime();
    RelationNotification localRelationNotification = null;
    if ((str2.equals("jmx.relation.creation.basic")) || (str2.equals("jmx.relation.creation.mbean")) || (str2.equals("jmx.relation.removal.basic")) || (str2.equals("jmx.relation.removal.mbean"))) {
      localRelationNotification = new RelationNotification(str2, this, localLong.longValue(), l, paramString1, paramString2, str1, (ObjectName)???, paramList1);
    } else if ((str2.equals("jmx.relation.update.basic")) || (str2.equals("jmx.relation.update.mbean"))) {
      localRelationNotification = new RelationNotification(str2, this, localLong.longValue(), l, paramString1, paramString2, str1, (ObjectName)???, paramString3, paramList2, paramList3);
    }
    sendNotification(localRelationNotification);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendNotificationInt");
  }
  
  private void handleReferenceUnregistration(String paramString, ObjectName paramObjectName, List<String> paramList)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException, RoleNotFoundException
  {
    if ((paramString == null) || (paramList == null) || (paramObjectName == null))
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleReferenceUnregistration", new Object[] { paramString, paramObjectName, paramList });
    isActive();
    String str1 = getRelationTypeName(paramString);
    Object localObject = getRelation(paramString);
    int i = 0;
    Iterator localIterator = paramList.iterator();
    String str2;
    while (localIterator.hasNext())
    {
      str2 = (String)localIterator.next();
      if (i != 0) {
        break;
      }
      int j = getRoleCardinality(paramString, str2).intValue();
      int k = j - 1;
      RoleInfo localRoleInfo;
      try
      {
        localRoleInfo = getRoleInfo(str1, str2);
      }
      catch (RelationTypeNotFoundException localRelationTypeNotFoundException2)
      {
        throw new RuntimeException(localRelationTypeNotFoundException2.getMessage());
      }
      catch (RoleInfoNotFoundException localRoleInfoNotFoundException)
      {
        throw new RuntimeException(localRoleInfoNotFoundException.getMessage());
      }
      boolean bool = localRoleInfo.checkMinDegree(k);
      if (!bool) {
        i = 1;
      }
    }
    if (i != 0)
    {
      removeRelation(paramString);
    }
    else
    {
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        str2 = (String)localIterator.next();
        if ((localObject instanceof RelationSupport))
        {
          try
          {
            ((RelationSupport)localObject).handleMBeanUnregistrationInt(paramObjectName, str2, true, this);
          }
          catch (RelationTypeNotFoundException localRelationTypeNotFoundException1)
          {
            throw new RuntimeException(localRelationTypeNotFoundException1.getMessage());
          }
          catch (InvalidRoleValueException localInvalidRoleValueException)
          {
            throw new RuntimeException(localInvalidRoleValueException.getMessage());
          }
        }
        else
        {
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = paramObjectName;
          arrayOfObject[1] = str2;
          String[] arrayOfString = new String[2];
          arrayOfString[0] = "javax.management.ObjectName";
          arrayOfString[1] = "java.lang.String";
          try
          {
            this.myMBeanServer.invoke((ObjectName)localObject, "handleMBeanUnregistration", arrayOfObject, arrayOfString);
          }
          catch (InstanceNotFoundException localInstanceNotFoundException)
          {
            throw new RuntimeException(localInstanceNotFoundException.getMessage());
          }
          catch (ReflectionException localReflectionException)
          {
            throw new RuntimeException(localReflectionException.getMessage());
          }
          catch (MBeanException localMBeanException)
          {
            Exception localException = localMBeanException.getTargetException();
            throw new RuntimeException(localException.getMessage());
          }
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleReferenceUnregistration");
  }
}
