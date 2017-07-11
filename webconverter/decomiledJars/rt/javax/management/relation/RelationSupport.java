package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class RelationSupport
  implements RelationSupportMBean, MBeanRegistration
{
  private String myRelId = null;
  private ObjectName myRelServiceName = null;
  private MBeanServer myRelServiceMBeanServer = null;
  private String myRelTypeName = null;
  private final Map<String, Role> myRoleName2ValueMap = new HashMap();
  private final AtomicBoolean myInRelServFlg = new AtomicBoolean();
  
  public RelationSupport(String paramString1, ObjectName paramObjectName, String paramString2, RoleList paramRoleList)
    throws InvalidRoleValueException, IllegalArgumentException
  {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
    initMembers(paramString1, paramObjectName, null, paramString2, paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
  }
  
  public RelationSupport(String paramString1, ObjectName paramObjectName, MBeanServer paramMBeanServer, String paramString2, RoleList paramRoleList)
    throws InvalidRoleValueException, IllegalArgumentException
  {
    if (paramMBeanServer == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
    initMembers(paramString1, paramObjectName, paramMBeanServer, paramString2, paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
  }
  
  public List<ObjectName> getRole(String paramString)
    throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRole", paramString);
    Object localObject = (List)Util.cast(getRoleInt(paramString, false, null, false));
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRole");
    return localObject;
  }
  
  public RoleResult getRoles(String[] paramArrayOfString)
    throws IllegalArgumentException, RelationServiceNotRegisteredException
  {
    if (paramArrayOfString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoles");
    Object localObject = getRolesInt(paramArrayOfString, false, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoles");
    return localObject;
  }
  
  public RoleResult getAllRoles()
    throws RelationServiceNotRegisteredException
  {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRoles");
    RoleResult localRoleResult = null;
    try
    {
      localRoleResult = getAllRolesInt(false, null);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRoles");
    return localRoleResult;
  }
  
  public RoleList retrieveAllRoles()
  {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "retrieveAllRoles");
    RoleList localRoleList;
    synchronized (this.myRoleName2ValueMap)
    {
      localRoleList = new RoleList(new ArrayList(this.myRoleName2ValueMap.values()));
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "retrieveAllRoles");
    return localRoleList;
  }
  
  public Integer getRoleCardinality(String paramString)
    throws IllegalArgumentException, RoleNotFoundException
  {
    Object localObject1;
    if (paramString == null)
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleCardinality", paramString);
    synchronized (this.myRoleName2ValueMap)
    {
      localObject1 = (Role)this.myRoleName2ValueMap.get(paramString);
    }
    if (localObject1 == null)
    {
      int i = 1;
      try
      {
        RelationService.throwRoleProblemException(i, paramString);
      }
      catch (InvalidRoleValueException localInvalidRoleValueException) {}
    }
    List localList = ((Role)localObject1).getRoleValue();
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleCardinality");
    return Integer.valueOf(localList.size());
  }
  
  public void setRole(Role paramRole)
    throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException
  {
    if (paramRole == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRole", paramRole);
    Object localObject = setRoleInt(paramRole, false, null, false);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRole");
  }
  
  public RoleResult setRoles(RoleList paramRoleList)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException
  {
    if (paramRoleList == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoles", paramRoleList);
    Object localObject = setRolesInt(paramRoleList, false, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoles");
    return localObject;
  }
  
  public void handleMBeanUnregistration(ObjectName paramObjectName, String paramString)
    throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException
  {
    if ((paramObjectName == null) || (paramString == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistration", new Object[] { paramObjectName, paramString });
    handleMBeanUnregistrationInt(paramObjectName, paramString, false, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistration");
  }
  
  public Map<ObjectName, List<String>> getReferencedMBeans()
  {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getReferencedMBeans");
    HashMap localHashMap = new HashMap();
    synchronized (this.myRoleName2ValueMap)
    {
      Iterator localIterator1 = this.myRoleName2ValueMap.values().iterator();
      while (localIterator1.hasNext())
      {
        Role localRole = (Role)localIterator1.next();
        String str = localRole.getRoleName();
        List localList = localRole.getRoleValue();
        Iterator localIterator2 = localList.iterator();
        while (localIterator2.hasNext())
        {
          ObjectName localObjectName = (ObjectName)localIterator2.next();
          Object localObject1 = (List)localHashMap.get(localObjectName);
          int i = 0;
          if (localObject1 == null)
          {
            i = 1;
            localObject1 = new ArrayList();
          }
          ((List)localObject1).add(str);
          if (i != 0) {
            localHashMap.put(localObjectName, localObject1);
          }
        }
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getReferencedMBeans");
    return localHashMap;
  }
  
  public String getRelationTypeName()
  {
    return this.myRelTypeName;
  }
  
  public ObjectName getRelationServiceName()
  {
    return this.myRelServiceName;
  }
  
  public String getRelationId()
  {
    return this.myRelId;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    this.myRelServiceMBeanServer = paramMBeanServer;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {}
  
  public void postDeregister() {}
  
  public Boolean isInRelationService()
  {
    return Boolean.valueOf(this.myInRelServFlg.get());
  }
  
  public void setRelationServiceManagementFlag(Boolean paramBoolean)
    throws IllegalArgumentException
  {
    if (paramBoolean == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    this.myInRelServFlg.set(paramBoolean.booleanValue());
  }
  
  Object getRoleInt(String paramString, boolean paramBoolean1, RelationService paramRelationService, boolean paramBoolean2)
    throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException
  {
    if ((paramString == null) || ((paramBoolean1) && (paramRelationService == null)))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleInt", paramString);
    int i = 0;
    Role localRole;
    synchronized (this.myRoleName2ValueMap)
    {
      localRole = (Role)this.myRoleName2ValueMap.get(paramString);
    }
    if (localRole == null)
    {
      i = 1;
    }
    else
    {
      if (paramBoolean1)
      {
        try
        {
          ??? = paramRelationService.checkRoleReading(paramString, this.myRelTypeName);
        }
        catch (RelationTypeNotFoundException localRelationTypeNotFoundException)
        {
          throw new RuntimeException(localRelationTypeNotFoundException.getMessage());
        }
      }
      else
      {
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramString;
        arrayOfObject[1] = this.myRelTypeName;
        String[] arrayOfString = new String[2];
        arrayOfString[0] = "java.lang.String";
        arrayOfString[1] = "java.lang.String";
        try
        {
          ??? = (Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleReading", arrayOfObject, arrayOfString);
        }
        catch (MBeanException localMBeanException)
        {
          throw new RuntimeException("incorrect relation type");
        }
        catch (ReflectionException localReflectionException)
        {
          throw new RuntimeException(localReflectionException.getMessage());
        }
        catch (InstanceNotFoundException localInstanceNotFoundException)
        {
          throw new RelationServiceNotRegisteredException(localInstanceNotFoundException.getMessage());
        }
      }
      i = ((Integer)???).intValue();
    }
    if (i == 0)
    {
      if (!paramBoolean2) {
        ??? = new ArrayList(localRole.getRoleValue());
      } else {
        ??? = (Role)localRole.clone();
      }
    }
    else
    {
      if (!paramBoolean2) {
        try
        {
          RelationService.throwRoleProblemException(i, paramString);
          return null;
        }
        catch (InvalidRoleValueException localInvalidRoleValueException)
        {
          throw new RuntimeException(localInvalidRoleValueException.getMessage());
        }
      }
      ??? = new RoleUnresolved(paramString, null, i);
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleInt");
    return ???;
  }
  
  RoleResult getRolesInt(String[] paramArrayOfString, boolean paramBoolean, RelationService paramRelationService)
    throws IllegalArgumentException, RelationServiceNotRegisteredException
  {
    if ((paramArrayOfString == null) || ((paramBoolean) && (paramRelationService == null)))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRolesInt");
    Object localObject1 = new RoleList();
    RoleUnresolvedList localRoleUnresolvedList = new RoleUnresolvedList();
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = paramArrayOfString[i];
      Object localObject2;
      try
      {
        localObject2 = getRoleInt(str, paramBoolean, paramRelationService, true);
      }
      catch (RoleNotFoundException localRoleNotFoundException)
      {
        return null;
      }
      if ((localObject2 instanceof Role)) {
        try
        {
          ((RoleList)localObject1).add((Role)localObject2);
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          throw new RuntimeException(localIllegalArgumentException1.getMessage());
        }
      } else if ((localObject2 instanceof RoleUnresolved)) {
        try
        {
          localRoleUnresolvedList.add((RoleUnresolved)localObject2);
        }
        catch (IllegalArgumentException localIllegalArgumentException2)
        {
          throw new RuntimeException(localIllegalArgumentException2.getMessage());
        }
      }
    }
    RoleResult localRoleResult = new RoleResult((RoleList)localObject1, localRoleUnresolvedList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRolesInt");
    return localRoleResult;
  }
  
  RoleResult getAllRolesInt(boolean paramBoolean, RelationService paramRelationService)
    throws IllegalArgumentException, RelationServiceNotRegisteredException
  {
    Object localObject1;
    if ((paramBoolean) && (paramRelationService == null))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRolesInt");
    synchronized (this.myRoleName2ValueMap)
    {
      localObject1 = new ArrayList(this.myRoleName2ValueMap.keySet());
    }
    ??? = new String[((List)localObject1).size()];
    ((List)localObject1).toArray((Object[])???);
    RoleResult localRoleResult = getRolesInt((String[])???, paramBoolean, paramRelationService);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRolesInt");
    return localRoleResult;
  }
  
  Object setRoleInt(Role paramRole, boolean paramBoolean1, RelationService paramRelationService, boolean paramBoolean2)
    throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException
  {
    if ((paramRole == null) || ((paramBoolean1) && (paramRelationService == null)))
    {
      str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoleInt", new Object[] { paramRole, Boolean.valueOf(paramBoolean1), paramRelationService, Boolean.valueOf(paramBoolean2) });
    String str = paramRole.getRoleName();
    int i = 0;
    Role localRole;
    synchronized (this.myRoleName2ValueMap)
    {
      localRole = (Role)this.myRoleName2ValueMap.get(str);
    }
    Boolean localBoolean;
    if (localRole == null)
    {
      localBoolean = Boolean.valueOf(true);
      ??? = new ArrayList();
    }
    else
    {
      localBoolean = Boolean.valueOf(false);
      ??? = localRole.getRoleValue();
    }
    try
    {
      Integer localInteger;
      if (paramBoolean1)
      {
        localInteger = paramRelationService.checkRoleWriting(paramRole, this.myRelTypeName, localBoolean);
      }
      else
      {
        localObject3 = new Object[3];
        localObject3[0] = paramRole;
        localObject3[1] = this.myRelTypeName;
        localObject3[2] = localBoolean;
        String[] arrayOfString = new String[3];
        arrayOfString[0] = "javax.management.relation.Role";
        arrayOfString[1] = "java.lang.String";
        arrayOfString[2] = "java.lang.Boolean";
        localInteger = (Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleWriting", (Object[])localObject3, arrayOfString);
      }
      i = localInteger.intValue();
    }
    catch (MBeanException localMBeanException)
    {
      Object localObject3 = localMBeanException.getTargetException();
      if ((localObject3 instanceof RelationTypeNotFoundException)) {
        throw ((RelationTypeNotFoundException)localObject3);
      }
      throw new RuntimeException(((Exception)localObject3).getMessage());
    }
    catch (ReflectionException localReflectionException)
    {
      throw new RuntimeException(localReflectionException.getMessage());
    }
    catch (RelationTypeNotFoundException localRelationTypeNotFoundException)
    {
      throw new RuntimeException(localRelationTypeNotFoundException.getMessage());
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new RelationServiceNotRegisteredException(localInstanceNotFoundException.getMessage());
    }
    Object localObject2 = null;
    if (i == 0)
    {
      if (!localBoolean.booleanValue())
      {
        sendRoleUpdateNotification(paramRole, (List)???, paramBoolean1, paramRelationService);
        updateRelationServiceMap(paramRole, (List)???, paramBoolean1, paramRelationService);
      }
      synchronized (this.myRoleName2ValueMap)
      {
        this.myRoleName2ValueMap.put(str, (Role)paramRole.clone());
      }
      if (paramBoolean2) {
        localObject2 = paramRole;
      }
    }
    else
    {
      if (!paramBoolean2)
      {
        RelationService.throwRoleProblemException(i, str);
        return null;
      }
      localObject2 = new RoleUnresolved(str, paramRole.getRoleValue(), i);
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoleInt");
    return localObject2;
  }
  
  private void sendRoleUpdateNotification(Role paramRole, List<ObjectName> paramList, boolean paramBoolean, RelationService paramRelationService)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException
  {
    if ((paramRole == null) || (paramList == null) || ((paramBoolean) && (paramRelationService == null)))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "sendRoleUpdateNotification", new Object[] { paramRole, paramList, Boolean.valueOf(paramBoolean), paramRelationService });
    if (paramBoolean)
    {
      try
      {
        paramRelationService.sendRoleUpdateNotification(this.myRelId, paramRole, paramList);
      }
      catch (RelationNotFoundException localRelationNotFoundException)
      {
        throw new RuntimeException(localRelationNotFoundException.getMessage());
      }
    }
    else
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = this.myRelId;
      arrayOfObject[1] = paramRole;
      arrayOfObject[2] = paramList;
      String[] arrayOfString = new String[3];
      arrayOfString[0] = "java.lang.String";
      arrayOfString[1] = "javax.management.relation.Role";
      arrayOfString[2] = "java.util.List";
      try
      {
        this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "sendRoleUpdateNotification", arrayOfObject, arrayOfString);
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RelationServiceNotRegisteredException(localInstanceNotFoundException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        Exception localException = localMBeanException.getTargetException();
        if ((localException instanceof RelationNotFoundException)) {
          throw ((RelationNotFoundException)localException);
        }
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "sendRoleUpdateNotification");
  }
  
  private void updateRelationServiceMap(Role paramRole, List<ObjectName> paramList, boolean paramBoolean, RelationService paramRelationService)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException
  {
    if ((paramRole == null) || (paramList == null) || ((paramBoolean) && (paramRelationService == null)))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "updateRelationServiceMap", new Object[] { paramRole, paramList, Boolean.valueOf(paramBoolean), paramRelationService });
    if (paramBoolean)
    {
      try
      {
        paramRelationService.updateRoleMap(this.myRelId, paramRole, paramList);
      }
      catch (RelationNotFoundException localRelationNotFoundException)
      {
        throw new RuntimeException(localRelationNotFoundException.getMessage());
      }
    }
    else
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = this.myRelId;
      arrayOfObject[1] = paramRole;
      arrayOfObject[2] = paramList;
      String[] arrayOfString = new String[3];
      arrayOfString[0] = "java.lang.String";
      arrayOfString[1] = "javax.management.relation.Role";
      arrayOfString[2] = "java.util.List";
      try
      {
        this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "updateRoleMap", arrayOfObject, arrayOfString);
      }
      catch (ReflectionException localReflectionException)
      {
        throw new RuntimeException(localReflectionException.getMessage());
      }
      catch (InstanceNotFoundException localInstanceNotFoundException)
      {
        throw new RelationServiceNotRegisteredException(localInstanceNotFoundException.getMessage());
      }
      catch (MBeanException localMBeanException)
      {
        Exception localException = localMBeanException.getTargetException();
        if ((localException instanceof RelationNotFoundException)) {
          throw ((RelationNotFoundException)localException);
        }
        throw new RuntimeException(localException.getMessage());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "updateRelationServiceMap");
  }
  
  RoleResult setRolesInt(RoleList paramRoleList, boolean paramBoolean, RelationService paramRelationService)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException
  {
    if ((paramRoleList == null) || ((paramBoolean) && (paramRelationService == null)))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRolesInt", new Object[] { paramRoleList, Boolean.valueOf(paramBoolean), paramRelationService });
    Object localObject1 = new RoleList();
    RoleUnresolvedList localRoleUnresolvedList = new RoleUnresolvedList();
    Object localObject2 = paramRoleList.asList().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Role localRole = (Role)((Iterator)localObject2).next();
      Object localObject3 = null;
      try
      {
        localObject3 = setRoleInt(localRole, paramBoolean, paramRelationService, true);
      }
      catch (RoleNotFoundException localRoleNotFoundException) {}catch (InvalidRoleValueException localInvalidRoleValueException) {}
      if ((localObject3 instanceof Role)) {
        try
        {
          ((RoleList)localObject1).add((Role)localObject3);
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          throw new RuntimeException(localIllegalArgumentException1.getMessage());
        }
      } else if ((localObject3 instanceof RoleUnresolved)) {
        try
        {
          localRoleUnresolvedList.add((RoleUnresolved)localObject3);
        }
        catch (IllegalArgumentException localIllegalArgumentException2)
        {
          throw new RuntimeException(localIllegalArgumentException2.getMessage());
        }
      }
    }
    localObject2 = new RoleResult((RoleList)localObject1, localRoleUnresolvedList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRolesInt");
    return localObject2;
  }
  
  private void initMembers(String paramString1, ObjectName paramObjectName, MBeanServer paramMBeanServer, String paramString2, RoleList paramRoleList)
    throws InvalidRoleValueException, IllegalArgumentException
  {
    if ((paramString1 == null) || (paramObjectName == null) || (paramString2 == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initMembers", new Object[] { paramString1, paramObjectName, paramMBeanServer, paramString2, paramRoleList });
    this.myRelId = paramString1;
    this.myRelServiceName = paramObjectName;
    this.myRelServiceMBeanServer = paramMBeanServer;
    this.myRelTypeName = paramString2;
    initRoleMap(paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initMembers");
  }
  
  private void initRoleMap(RoleList paramRoleList)
    throws InvalidRoleValueException
  {
    if (paramRoleList == null) {
      return;
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initRoleMap", paramRoleList);
    synchronized (this.myRoleName2ValueMap)
    {
      Iterator localIterator = paramRoleList.asList().iterator();
      while (localIterator.hasNext())
      {
        Role localRole = (Role)localIterator.next();
        String str = localRole.getRoleName();
        if (this.myRoleName2ValueMap.containsKey(str))
        {
          StringBuilder localStringBuilder = new StringBuilder("Role name ");
          localStringBuilder.append(str);
          localStringBuilder.append(" used for two roles.");
          throw new InvalidRoleValueException(localStringBuilder.toString());
        }
        this.myRoleName2ValueMap.put(str, (Role)localRole.clone());
      }
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initRoleMap");
  }
  
  void handleMBeanUnregistrationInt(ObjectName paramObjectName, String paramString, boolean paramBoolean, RelationService paramRelationService)
    throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException
  {
    Object localObject1;
    if ((paramObjectName == null) || (paramString == null) || ((paramBoolean) && (paramRelationService == null)))
    {
      localObject1 = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistrationInt", new Object[] { paramObjectName, paramString, Boolean.valueOf(paramBoolean), paramRelationService });
    synchronized (this.myRoleName2ValueMap)
    {
      localObject1 = (Role)this.myRoleName2ValueMap.get(paramString);
    }
    if (localObject1 == null)
    {
      ??? = new StringBuilder();
      localObject3 = "No role with name ";
      ((StringBuilder)???).append((String)localObject3);
      ((StringBuilder)???).append(paramString);
      throw new RoleNotFoundException(((StringBuilder)???).toString());
    }
    ??? = ((Role)localObject1).getRoleValue();
    Object localObject3 = new ArrayList((Collection)???);
    ((List)localObject3).remove(paramObjectName);
    Role localRole = new Role(paramString, (List)localObject3);
    Object localObject4 = setRoleInt(localRole, paramBoolean, paramRelationService, false);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistrationInt");
  }
}
