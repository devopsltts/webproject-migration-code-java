package sun.management;

import com.sun.management.DiagnosticCommandMBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

class DiagnosticCommandImpl
  extends NotificationEmitterSupport
  implements DiagnosticCommandMBean
{
  private final VMManagement jvm;
  private volatile Map<String, Wrapper> wrappers = null;
  private static final String strClassName = "".getClass().getName();
  private static final String strArrayClassName = [Ljava.lang.String.class.getName();
  private final boolean isSupported;
  private static final String notifName = "javax.management.Notification";
  private static final String[] diagFramNotifTypes = { "jmx.mbean.info.changed" };
  private MBeanNotificationInfo[] notifInfo = null;
  private static long seqNumber = 0L;
  
  public Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    throw new AttributeNotFoundException(paramString);
  }
  
  public void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    throw new AttributeNotFoundException(paramAttribute.getName());
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString)
  {
    return new AttributeList();
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList)
  {
    return new AttributeList();
  }
  
  DiagnosticCommandImpl(VMManagement paramVMManagement)
  {
    this.jvm = paramVMManagement;
    this.isSupported = paramVMManagement.isRemoteDiagnosticCommandsSupported();
  }
  
  public MBeanInfo getMBeanInfo()
  {
    TreeSet localTreeSet = new TreeSet(new OperationInfoComparator(null));
    Object localObject1;
    if (!this.isSupported) {
      localObject1 = Collections.EMPTY_MAP;
    } else {
      try
      {
        String[] arrayOfString = getDiagnosticCommands();
        localObject2 = getDiagnosticCommandInfo(arrayOfString);
        MBeanParameterInfo[] arrayOfMBeanParameterInfo = { new MBeanParameterInfo("arguments", strArrayClassName, "Array of Diagnostic Commands Arguments and Options") };
        localObject1 = new HashMap();
        for (int i = 0; i < arrayOfString.length; i++)
        {
          String str = transform(arrayOfString[i]);
          try
          {
            Wrapper localWrapper = new Wrapper(str, arrayOfString[i], localObject2[i]);
            ((Map)localObject1).put(str, localWrapper);
            localTreeSet.add(new MBeanOperationInfo(localWrapper.name, localWrapper.info.getDescription(), (localWrapper.info.getArgumentsInfo() == null) || (localWrapper.info.getArgumentsInfo().isEmpty()) ? null : arrayOfMBeanParameterInfo, strClassName, 2, commandDescriptor(localWrapper)));
          }
          catch (InstantiationException localInstantiationException) {}
        }
      }
      catch (IllegalArgumentException|UnsupportedOperationException localIllegalArgumentException)
      {
        localObject1 = Collections.EMPTY_MAP;
      }
    }
    this.wrappers = Collections.unmodifiableMap((Map)localObject1);
    HashMap localHashMap = new HashMap();
    localHashMap.put("immutableInfo", "false");
    localHashMap.put("interfaceClassName", "com.sun.management.DiagnosticCommandMBean");
    localHashMap.put("mxbean", "false");
    Object localObject2 = new ImmutableDescriptor(localHashMap);
    return new MBeanInfo(getClass().getName(), "Diagnostic Commands", null, null, (MBeanOperationInfo[])localTreeSet.toArray(new MBeanOperationInfo[localTreeSet.size()]), getNotificationInfo(), (Descriptor)localObject2);
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    if (!this.isSupported) {
      throw new UnsupportedOperationException();
    }
    if (this.wrappers == null) {
      getMBeanInfo();
    }
    Wrapper localWrapper = (Wrapper)this.wrappers.get(paramString);
    if (localWrapper != null)
    {
      if ((localWrapper.info.getArgumentsInfo().isEmpty()) && ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) && ((paramArrayOfString == null) || (paramArrayOfString.length == 0))) {
        return localWrapper.execute(null);
      }
      if ((paramArrayOfObject != null) && (paramArrayOfObject.length == 1) && (paramArrayOfString != null) && (paramArrayOfString.length == 1) && (paramArrayOfString[0] != null) && (paramArrayOfString[0].compareTo(strArrayClassName) == 0)) {
        return localWrapper.execute((String[])paramArrayOfObject[0]);
      }
    }
    throw new ReflectionException(new NoSuchMethodException(paramString));
  }
  
  private static String transform(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    int j = 0;
    for (int k = 0; k < paramString.length(); k++)
    {
      char c = paramString.charAt(k);
      if ((c == '.') || (c == '_'))
      {
        i = 0;
        j = 1;
      }
      else if (j != 0)
      {
        j = 0;
        localStringBuilder.append(Character.toUpperCase(c));
      }
      else if (i != 0)
      {
        localStringBuilder.append(Character.toLowerCase(c));
      }
      else
      {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  private Descriptor commandDescriptor(Wrapper paramWrapper)
    throws IllegalArgumentException
  {
    HashMap localHashMap1 = new HashMap();
    localHashMap1.put("dcmd.name", paramWrapper.info.getName());
    localHashMap1.put("dcmd.description", paramWrapper.info.getDescription());
    localHashMap1.put("dcmd.vmImpact", paramWrapper.info.getImpact());
    localHashMap1.put("dcmd.permissionClass", paramWrapper.info.getPermissionClass());
    localHashMap1.put("dcmd.permissionName", paramWrapper.info.getPermissionName());
    localHashMap1.put("dcmd.permissionAction", paramWrapper.info.getPermissionAction());
    localHashMap1.put("dcmd.enabled", Boolean.valueOf(paramWrapper.info.isEnabled()));
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("help ");
    localStringBuilder.append(paramWrapper.info.getName());
    localHashMap1.put("dcmd.help", executeDiagnosticCommand(localStringBuilder.toString()));
    if ((paramWrapper.info.getArgumentsInfo() != null) && (!paramWrapper.info.getArgumentsInfo().isEmpty()))
    {
      HashMap localHashMap2 = new HashMap();
      Iterator localIterator = paramWrapper.info.getArgumentsInfo().iterator();
      while (localIterator.hasNext())
      {
        DiagnosticCommandArgumentInfo localDiagnosticCommandArgumentInfo = (DiagnosticCommandArgumentInfo)localIterator.next();
        HashMap localHashMap3 = new HashMap();
        localHashMap3.put("dcmd.arg.name", localDiagnosticCommandArgumentInfo.getName());
        localHashMap3.put("dcmd.arg.type", localDiagnosticCommandArgumentInfo.getType());
        localHashMap3.put("dcmd.arg.description", localDiagnosticCommandArgumentInfo.getDescription());
        localHashMap3.put("dcmd.arg.isMandatory", Boolean.valueOf(localDiagnosticCommandArgumentInfo.isMandatory()));
        localHashMap3.put("dcmd.arg.isMultiple", Boolean.valueOf(localDiagnosticCommandArgumentInfo.isMultiple()));
        boolean bool = localDiagnosticCommandArgumentInfo.isOption();
        localHashMap3.put("dcmd.arg.isOption", Boolean.valueOf(bool));
        if (!bool) {
          localHashMap3.put("dcmd.arg.position", Integer.valueOf(localDiagnosticCommandArgumentInfo.getPosition()));
        } else {
          localHashMap3.put("dcmd.arg.position", Integer.valueOf(-1));
        }
        localHashMap2.put(localDiagnosticCommandArgumentInfo.getName(), new ImmutableDescriptor(localHashMap3));
      }
      localHashMap1.put("dcmd.arguments", new ImmutableDescriptor(localHashMap2));
    }
    return new ImmutableDescriptor(localHashMap1);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    synchronized (this)
    {
      if (this.notifInfo == null)
      {
        this.notifInfo = new MBeanNotificationInfo[1];
        this.notifInfo[0] = new MBeanNotificationInfo(diagFramNotifTypes, "javax.management.Notification", "Diagnostic Framework Notification");
      }
    }
    return this.notifInfo;
  }
  
  private static long getNextSeqNumber()
  {
    return ++seqNumber;
  }
  
  private void createDiagnosticFrameworkNotification()
  {
    if (!hasListeners()) {
      return;
    }
    ObjectName localObjectName = null;
    try
    {
      localObjectName = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
    }
    catch (MalformedObjectNameException localMalformedObjectNameException) {}
    Notification localNotification = new Notification("jmx.mbean.info.changed", localObjectName, getNextSeqNumber());
    localNotification.setUserData(getMBeanInfo());
    sendNotification(localNotification);
  }
  
  public synchronized void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    boolean bool1 = hasListeners();
    super.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if ((!bool1) && (bool2)) {
      setNotificationEnabled(true);
    }
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener);
    boolean bool2 = hasListeners();
    if ((bool1) && (!bool2)) {
      setNotificationEnabled(false);
    }
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if ((bool1) && (!bool2)) {
      setNotificationEnabled(false);
    }
  }
  
  private native void setNotificationEnabled(boolean paramBoolean);
  
  private native String[] getDiagnosticCommands();
  
  private native DiagnosticCommandInfo[] getDiagnosticCommandInfo(String[] paramArrayOfString);
  
  private native String executeDiagnosticCommand(String paramString);
  
  private static class OperationInfoComparator
    implements Comparator<MBeanOperationInfo>
  {
    private OperationInfoComparator() {}
    
    public int compare(MBeanOperationInfo paramMBeanOperationInfo1, MBeanOperationInfo paramMBeanOperationInfo2)
    {
      return paramMBeanOperationInfo1.getName().compareTo(paramMBeanOperationInfo2.getName());
    }
  }
  
  private class Wrapper
  {
    String name;
    String cmd;
    DiagnosticCommandInfo info;
    Permission permission;
    
    Wrapper(String paramString1, String paramString2, DiagnosticCommandInfo paramDiagnosticCommandInfo)
      throws InstantiationException
    {
      this.name = paramString1;
      this.cmd = paramString2;
      this.info = paramDiagnosticCommandInfo;
      this.permission = null;
      Object localObject = null;
      if (paramDiagnosticCommandInfo.getPermissionClass() != null)
      {
        try
        {
          Class localClass = Class.forName(paramDiagnosticCommandInfo.getPermissionClass());
          if (paramDiagnosticCommandInfo.getPermissionAction() == null) {
            try
            {
              Constructor localConstructor1 = localClass.getConstructor(new Class[] { String.class });
              this.permission = ((Permission)localConstructor1.newInstance(new Object[] { paramDiagnosticCommandInfo.getPermissionName() }));
            }
            catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException localInstantiationException2)
            {
              localObject = localInstantiationException2;
            }
          }
          if (this.permission == null) {
            try
            {
              Constructor localConstructor2 = localClass.getConstructor(new Class[] { String.class, String.class });
              this.permission = ((Permission)localConstructor2.newInstance(new Object[] { paramDiagnosticCommandInfo.getPermissionName(), paramDiagnosticCommandInfo.getPermissionAction() }));
            }
            catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException localInstantiationException3)
            {
              localObject = localInstantiationException3;
            }
          }
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
        if (this.permission == null)
        {
          InstantiationException localInstantiationException1 = new InstantiationException("Unable to instantiate required permission");
          localInstantiationException1.initCause(localObject);
        }
      }
    }
    
    public String execute(String[] paramArrayOfString)
    {
      if (this.permission != null)
      {
        localObject = System.getSecurityManager();
        if (localObject != null) {
          ((SecurityManager)localObject).checkPermission(this.permission);
        }
      }
      if (paramArrayOfString == null) {
        return DiagnosticCommandImpl.this.executeDiagnosticCommand(this.cmd);
      }
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append(this.cmd);
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        if (paramArrayOfString[i] == null) {
          throw new IllegalArgumentException("Invalid null argument");
        }
        ((StringBuilder)localObject).append(" ");
        ((StringBuilder)localObject).append(paramArrayOfString[i]);
      }
      return DiagnosticCommandImpl.this.executeDiagnosticCommand(((StringBuilder)localObject).toString());
    }
  }
}
