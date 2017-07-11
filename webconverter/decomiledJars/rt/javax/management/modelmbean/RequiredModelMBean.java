package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;
import javax.management.ServiceNotFoundException;
import javax.management.loading.ClassLoaderRepository;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class RequiredModelMBean
  implements ModelMBean, MBeanRegistration, NotificationEmitter
{
  ModelMBeanInfo modelMBeanInfo;
  private NotificationBroadcasterSupport generalBroadcaster = null;
  private NotificationBroadcasterSupport attributeBroadcaster = null;
  private Object managedResource = null;
  private boolean registered = false;
  private transient MBeanServer server = null;
  private static final JavaSecurityAccess javaSecurityAccess = ;
  private final AccessControlContext acc = AccessController.getContext();
  private static final Class<?>[] primitiveClasses = { Integer.TYPE, Long.TYPE, Boolean.TYPE, Double.TYPE, Float.TYPE, Short.TYPE, Byte.TYPE, Character.TYPE };
  private static final Map<String, Class<?>> primitiveClassMap = new HashMap();
  private static Set<String> rmmbMethodNames;
  private static final String[] primitiveTypes = { Boolean.TYPE.getName(), Byte.TYPE.getName(), Character.TYPE.getName(), Short.TYPE.getName(), Integer.TYPE.getName(), Long.TYPE.getName(), Float.TYPE.getName(), Double.TYPE.getName(), Void.TYPE.getName() };
  private static final String[] primitiveWrappers = { Boolean.class.getName(), Byte.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), Void.class.getName() };
  
  public RequiredModelMBean()
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Entry");
    }
    this.modelMBeanInfo = createDefaultModelMBeanInfo();
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Exit");
    }
  }
  
  public RequiredModelMBean(ModelMBeanInfo paramModelMBeanInfo)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Entry");
    }
    setModelMBeanInfo(paramModelMBeanInfo);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Exit");
    }
  }
  
  public void setModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Entry");
    }
    if (paramModelMBeanInfo == null)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo is null: Raising exception.");
      }
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("ModelMBeanInfo must not be null");
      throw new RuntimeOperationsException(localIllegalArgumentException, "Exception occurred trying to initialize the ModelMBeanInfo of the RequiredModelMBean");
    }
    if (this.registered)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "RequiredMBean is registered: Raising exception.");
      }
      IllegalStateException localIllegalStateException = new IllegalStateException("cannot call setModelMBeanInfo while ModelMBean is registered");
      throw new RuntimeOperationsException(localIllegalStateException, "Exception occurred trying to set the ModelMBeanInfo of the RequiredModelMBean");
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Setting ModelMBeanInfo to " + printModelMBeanInfo(paramModelMBeanInfo));
      int i = 0;
      if (paramModelMBeanInfo.getNotifications() != null) {
        i = paramModelMBeanInfo.getNotifications().length;
      }
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo notifications has " + i + " elements");
    }
    this.modelMBeanInfo = ((ModelMBeanInfo)paramModelMBeanInfo.clone());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "set mbeanInfo to: " + printModelMBeanInfo(this.modelMBeanInfo));
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Exit");
    }
  }
  
  public void setManagedResource(Object paramObject, String paramString)
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Entry");
    }
    if ((paramString == null) || (!paramString.equalsIgnoreCase("objectReference")))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource Type is not supported: " + paramString);
      }
      throw new InvalidTargetObjectTypeException(paramString);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource is valid");
    }
    this.managedResource = paramObject;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object, String)", "Exit");
    }
  }
  
  public void load()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException
  {
    ServiceNotFoundException localServiceNotFoundException = new ServiceNotFoundException("Persistence not supported for this MBean");
    throw new MBeanException(localServiceNotFoundException, localServiceNotFoundException.getMessage());
  }
  
  public void store()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException
  {
    ServiceNotFoundException localServiceNotFoundException = new ServiceNotFoundException("Persistence not supported for this MBean");
    throw new MBeanException(localServiceNotFoundException, localServiceNotFoundException.getMessage());
  }
  
  private Object resolveForCacheValue(Descriptor paramDescriptor)
    throws MBeanException, RuntimeOperationsException
  {
    boolean bool1 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool1) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Entry");
    }
    Object localObject1 = null;
    boolean bool2 = false;
    boolean bool3 = true;
    long l1 = 0L;
    if (paramDescriptor == null)
    {
      if (bool1) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Input Descriptor is null");
      }
      return localObject1;
    }
    if (bool1) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "descriptor is " + paramDescriptor);
    }
    Descriptor localDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
    if ((localDescriptor == null) && (bool1)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "MBean Descriptor is null");
    }
    Object localObject2 = paramDescriptor.getFieldValue("currencyTimeLimit");
    String str1;
    if (localObject2 != null) {
      str1 = localObject2.toString();
    } else {
      str1 = null;
    }
    if ((str1 == null) && (localDescriptor != null))
    {
      localObject2 = localDescriptor.getFieldValue("currencyTimeLimit");
      if (localObject2 != null) {
        str1 = localObject2.toString();
      } else {
        str1 = null;
      }
    }
    if (str1 != null)
    {
      if (bool1) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyTimeLimit: " + str1);
      }
      l1 = new Long(str1).longValue() * 1000L;
      Object localObject3;
      if (l1 < 0L)
      {
        bool3 = false;
        bool2 = true;
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", l1 + ": never Cached");
        }
      }
      else if (l1 == 0L)
      {
        bool3 = true;
        bool2 = false;
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "always valid Cache");
        }
      }
      else
      {
        localObject3 = paramDescriptor.getFieldValue("lastUpdatedTimeStamp");
        String str2;
        if (localObject3 != null) {
          str2 = localObject3.toString();
        } else {
          str2 = null;
        }
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "lastUpdatedTimeStamp: " + str2);
        }
        if (str2 == null) {
          str2 = "0";
        }
        long l2 = new Long(str2).longValue();
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyPeriod:" + l1 + " lastUpdatedTimeStamp:" + l2);
        }
        long l3 = new Date().getTime();
        if (l3 < l2 + l1)
        {
          bool3 = true;
          bool2 = false;
          if (bool1) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", " timed valid Cache for " + l3 + " < " + (l2 + l1));
          }
        }
        else
        {
          bool3 = false;
          bool2 = true;
          if (bool1) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "timed expired cache for " + l3 + " > " + (l2 + l1));
          }
        }
      }
      if (bool1) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "returnCachedValue:" + bool3 + " resetValue: " + bool2);
      }
      if (bool3 == true)
      {
        localObject3 = paramDescriptor.getFieldValue("value");
        if (localObject3 != null)
        {
          localObject1 = localObject3;
          if (bool1) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "valid Cache value: " + localObject3);
          }
        }
        else
        {
          localObject1 = null;
          if (bool1) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "no Cached value");
          }
        }
      }
      if (bool2 == true)
      {
        paramDescriptor.removeField("lastUpdatedTimeStamp");
        paramDescriptor.removeField("value");
        localObject1 = null;
        this.modelMBeanInfo.setDescriptor(paramDescriptor, null);
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "reset cached value to null");
        }
      }
    }
    if (bool1) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Exit");
    }
    return localObject1;
  }
  
  public MBeanInfo getMBeanInfo()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "Entry");
    }
    if (this.modelMBeanInfo == null)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "modelMBeanInfo is null");
      }
      this.modelMBeanInfo = createDefaultModelMBeanInfo();
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "ModelMBeanInfo is " + this.modelMBeanInfo.getClassName() + " for " + this.modelMBeanInfo.getDescription());
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", printModelMBeanInfo(this.modelMBeanInfo));
    }
    return (MBeanInfo)this.modelMBeanInfo.clone();
  }
  
  private String printModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramModelMBeanInfo == null)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "printModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo to print is null, printing local ModelMBeanInfo");
      }
      paramModelMBeanInfo = this.modelMBeanInfo;
    }
    localStringBuilder.append("\nMBeanInfo for ModelMBean is:");
    localStringBuilder.append("\nCLASSNAME: \t" + paramModelMBeanInfo.getClassName());
    localStringBuilder.append("\nDESCRIPTION: \t" + paramModelMBeanInfo.getDescription());
    try
    {
      localStringBuilder.append("\nMBEAN DESCRIPTOR: \t" + paramModelMBeanInfo.getMBeanDescriptor());
    }
    catch (Exception localException)
    {
      localStringBuilder.append("\nMBEAN DESCRIPTOR: \t is invalid");
    }
    localStringBuilder.append("\nATTRIBUTES");
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = paramModelMBeanInfo.getAttributes();
    if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
      for (int i = 0; i < arrayOfMBeanAttributeInfo.length; i++)
      {
        ModelMBeanAttributeInfo localModelMBeanAttributeInfo = (ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[i];
        localStringBuilder.append(" ** NAME: \t" + localModelMBeanAttributeInfo.getName());
        localStringBuilder.append("    DESCR: \t" + localModelMBeanAttributeInfo.getDescription());
        localStringBuilder.append("    TYPE: \t" + localModelMBeanAttributeInfo.getType() + "    READ: \t" + localModelMBeanAttributeInfo.isReadable() + "    WRITE: \t" + localModelMBeanAttributeInfo.isWritable());
        localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanAttributeInfo.getDescriptor().toString());
      }
    } else {
      localStringBuilder.append(" ** No attributes **");
    }
    localStringBuilder.append("\nCONSTRUCTORS");
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = paramModelMBeanInfo.getConstructors();
    if ((arrayOfMBeanConstructorInfo != null) && (arrayOfMBeanConstructorInfo.length > 0)) {
      for (int j = 0; j < arrayOfMBeanConstructorInfo.length; j++)
      {
        ModelMBeanConstructorInfo localModelMBeanConstructorInfo = (ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[j];
        localStringBuilder.append(" ** NAME: \t" + localModelMBeanConstructorInfo.getName());
        localStringBuilder.append("    DESCR: \t" + localModelMBeanConstructorInfo.getDescription());
        localStringBuilder.append("    PARAM: \t" + localModelMBeanConstructorInfo.getSignature().length + " parameter(s)");
        localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanConstructorInfo.getDescriptor().toString());
      }
    } else {
      localStringBuilder.append(" ** No Constructors **");
    }
    localStringBuilder.append("\nOPERATIONS");
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = paramModelMBeanInfo.getOperations();
    if ((arrayOfMBeanOperationInfo != null) && (arrayOfMBeanOperationInfo.length > 0)) {
      for (int k = 0; k < arrayOfMBeanOperationInfo.length; k++)
      {
        ModelMBeanOperationInfo localModelMBeanOperationInfo = (ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[k];
        localStringBuilder.append(" ** NAME: \t" + localModelMBeanOperationInfo.getName());
        localStringBuilder.append("    DESCR: \t" + localModelMBeanOperationInfo.getDescription());
        localStringBuilder.append("    PARAM: \t" + localModelMBeanOperationInfo.getSignature().length + " parameter(s)");
        localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanOperationInfo.getDescriptor().toString());
      }
    } else {
      localStringBuilder.append(" ** No operations ** ");
    }
    localStringBuilder.append("\nNOTIFICATIONS");
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramModelMBeanInfo.getNotifications();
    if ((arrayOfMBeanNotificationInfo != null) && (arrayOfMBeanNotificationInfo.length > 0)) {
      for (int m = 0; m < arrayOfMBeanNotificationInfo.length; m++)
      {
        ModelMBeanNotificationInfo localModelMBeanNotificationInfo = (ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[m];
        localStringBuilder.append(" ** NAME: \t" + localModelMBeanNotificationInfo.getName());
        localStringBuilder.append("    DESCR: \t" + localModelMBeanNotificationInfo.getDescription());
        localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanNotificationInfo.getDescriptor().toString());
      }
    } else {
      localStringBuilder.append(" ** No notifications **");
    }
    localStringBuilder.append(" ** ModelMBean: End of MBeanInfo ** ");
    return localStringBuilder.toString();
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Entry");
    }
    if (paramString == null)
    {
      localObject1 = new IllegalArgumentException("Method name must not be null");
      throw new RuntimeOperationsException((RuntimeException)localObject1, "An exception occurred while trying to invoke a method on a RequiredModelMBean");
    }
    Object localObject1 = null;
    int i = paramString.lastIndexOf(".");
    if (i > 0)
    {
      localObject1 = paramString.substring(0, i);
      str1 = paramString.substring(i + 1);
    }
    else
    {
      str1 = paramString;
    }
    i = str1.indexOf("(");
    if (i > 0) {
      str1 = str1.substring(0, i);
    }
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Finding operation " + paramString + " as " + str1);
    }
    ModelMBeanOperationInfo localModelMBeanOperationInfo = this.modelMBeanInfo.getOperation(str1);
    if (localModelMBeanOperationInfo == null)
    {
      localObject2 = "Operation " + paramString + " not in ModelMBeanInfo";
      throw new MBeanException(new ServiceNotFoundException((String)localObject2), (String)localObject2);
    }
    Object localObject2 = localModelMBeanOperationInfo.getDescriptor();
    if (localObject2 == null) {
      throw new MBeanException(new ServiceNotFoundException("Operation descriptor null"), "Operation descriptor null");
    }
    Object localObject3 = resolveForCacheValue((Descriptor)localObject2);
    if (localObject3 != null)
    {
      if (bool) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Returning cached value");
      }
      return localObject3;
    }
    if (localObject1 == null) {
      localObject1 = (String)((Descriptor)localObject2).getFieldValue("class");
    }
    String str1 = (String)((Descriptor)localObject2).getFieldValue("name");
    if (str1 == null) {
      throw new MBeanException(new ServiceNotFoundException("Method descriptor must include `name' field"), "Method descriptor must include `name' field");
    }
    String str2 = (String)((Descriptor)localObject2).getFieldValue("targetType");
    if ((str2 != null) && (!str2.equalsIgnoreCase("objectReference")))
    {
      localObject4 = "Target type must be objectReference: " + str2;
      throw new MBeanException(new InvalidTargetObjectTypeException((String)localObject4), (String)localObject4);
    }
    Object localObject4 = ((Descriptor)localObject2).getFieldValue("targetObject");
    if ((bool) && (localObject4 != null)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Found target object in descriptor");
    }
    Method localMethod = findRMMBMethod(str1, localObject4, (String)localObject1, paramArrayOfString);
    Object localObject5;
    if (localMethod != null)
    {
      localObject5 = this;
    }
    else
    {
      if (bool) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in managedResource class");
      }
      Object localObject7;
      if (localObject4 != null)
      {
        localObject5 = localObject4;
      }
      else
      {
        localObject5 = this.managedResource;
        if (localObject5 == null)
        {
          localObject6 = "managedResource for invoke " + paramString + " is null";
          localObject7 = new ServiceNotFoundException((String)localObject6);
          throw new MBeanException((Exception)localObject7);
        }
      }
      if (localObject1 != null) {
        try
        {
          localObject7 = AccessController.getContext();
          localObject8 = localObject5;
          final Object localObject9 = localObject1;
          final ClassNotFoundException[] arrayOfClassNotFoundException = new ClassNotFoundException[1];
          localObject6 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
          {
            public Class<?> run()
            {
              try
              {
                ReflectUtil.checkPackageAccess(localObject9);
                ClassLoader localClassLoader = localObject8.getClass().getClassLoader();
                return Class.forName(localObject9, false, localClassLoader);
              }
              catch (ClassNotFoundException localClassNotFoundException)
              {
                arrayOfClassNotFoundException[0] = localClassNotFoundException;
              }
              return null;
            }
          }, (AccessControlContext)localObject7, this.acc);
          if (arrayOfClassNotFoundException[0] != null) {
            throw arrayOfClassNotFoundException[0];
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          final Object localObject8 = "class for invoke " + paramString + " not found";
          throw new ReflectionException(localClassNotFoundException, (String)localObject8);
        }
      } else {
        localObject6 = localObject5.getClass();
      }
      localMethod = resolveMethod((Class)localObject6, str1, paramArrayOfString);
    }
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "found " + str1 + ", now invoking");
    }
    Object localObject6 = invokeMethod(paramString, localMethod, localObject5, paramArrayOfObject);
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "successfully invoked method");
    }
    if (localObject6 != null) {
      cacheResult(localModelMBeanOperationInfo, (Descriptor)localObject2, localObject6);
    }
    return localObject6;
  }
  
  private Method resolveMethod(Class<?> paramClass, String paramString, final String[] paramArrayOfString)
    throws ReflectionException
  {
    final boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolving " + paramClass.getName() + "." + paramString);
    }
    final Class[] arrayOfClass;
    Object localObject;
    if (paramArrayOfString == null)
    {
      arrayOfClass = null;
    }
    else
    {
      AccessControlContext localAccessControlContext = AccessController.getContext();
      localObject = new ReflectionException[1];
      final ClassLoader localClassLoader = paramClass.getClassLoader();
      arrayOfClass = new Class[paramArrayOfString.length];
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
      {
        public Void run()
        {
          for (int i = 0; i < paramArrayOfString.length; i++)
          {
            if (bool) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolve type " + paramArrayOfString[i]);
            }
            arrayOfClass[i] = ((Class)RequiredModelMBean.primitiveClassMap.get(paramArrayOfString[i]));
            if (arrayOfClass[i] == null) {
              try
              {
                ReflectUtil.checkPackageAccess(paramArrayOfString[i]);
                arrayOfClass[i] = Class.forName(paramArrayOfString[i], false, localClassLoader);
              }
              catch (ClassNotFoundException localClassNotFoundException)
              {
                if (bool) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "class not found");
                }
                this.val$caughtException[0] = new ReflectionException(localClassNotFoundException, "Parameter class not found");
              }
            }
          }
          return null;
        }
      }, localAccessControlContext, this.acc);
      if (localObject[0] != null) {
        throw localObject[0];
      }
    }
    try
    {
      return paramClass.getMethod(paramString, arrayOfClass);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localObject = "Target method not found: " + paramClass.getName() + "." + paramString;
      throw new ReflectionException(localNoSuchMethodException, (String)localObject);
    }
  }
  
  private Method findRMMBMethod(String paramString1, Object paramObject, String paramString2, String[] paramArrayOfString)
  {
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in RequiredModelMBean class");
    }
    if (!isRMMBMethodName(paramString1)) {
      return null;
    }
    if (paramObject != null) {
      return null;
    }
    final RequiredModelMBean localRequiredModelMBean = RequiredModelMBean.class;
    Object localObject;
    if (paramString2 == null)
    {
      localObject = localRequiredModelMBean;
    }
    else
    {
      AccessControlContext localAccessControlContext = AccessController.getContext();
      final String str = paramString2;
      localObject = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
      {
        public Class<?> run()
        {
          try
          {
            ReflectUtil.checkPackageAccess(str);
            ClassLoader localClassLoader = localRequiredModelMBean.getClassLoader();
            Class localClass = Class.forName(str, false, localClassLoader);
            if (!localRequiredModelMBean.isAssignableFrom(localClass)) {
              return null;
            }
            return localClass;
          }
          catch (ClassNotFoundException localClassNotFoundException) {}
          return null;
        }
      }, localAccessControlContext, this.acc);
    }
    try
    {
      return localObject != null ? resolveMethod((Class)localObject, paramString1, paramArrayOfString) : null;
    }
    catch (ReflectionException localReflectionException) {}
    return null;
  }
  
  private Object invokeMethod(String paramString, final Method paramMethod, final Object paramObject, final Object[] paramArrayOfObject)
    throws MBeanException, ReflectionException
  {
    try
    {
      final Throwable[] arrayOfThrowable = new Throwable[1];
      localObject1 = AccessController.getContext();
      Object localObject2 = javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            ReflectUtil.checkPackageAccess(paramMethod.getDeclaringClass());
            return MethodUtil.invoke(paramMethod, paramObject, paramArrayOfObject);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            arrayOfThrowable[0] = localInvocationTargetException;
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            arrayOfThrowable[0] = localIllegalAccessException;
          }
          return null;
        }
      }, (AccessControlContext)localObject1, this.acc);
      if (arrayOfThrowable[0] != null)
      {
        if ((arrayOfThrowable[0] instanceof Exception)) {
          throw ((Exception)arrayOfThrowable[0]);
        }
        if ((arrayOfThrowable[0] instanceof Error)) {
          throw ((Error)arrayOfThrowable[0]);
        }
      }
      return localObject2;
    }
    catch (RuntimeErrorException localRuntimeErrorException)
    {
      throw new RuntimeOperationsException(localRuntimeErrorException, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new RuntimeOperationsException(localRuntimeException, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionException(localIllegalAccessException, "IllegalAccessException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject1 = localInvocationTargetException.getTargetException();
      if ((localObject1 instanceof RuntimeException)) {
        throw new MBeanException((RuntimeException)localObject1, "RuntimeException thrown in RequiredModelMBean while trying to invoke operation " + paramString);
      }
      if ((localObject1 instanceof Error)) {
        throw new RuntimeErrorException((Error)localObject1, "Error occurred in RequiredModelMBean while trying to invoke operation " + paramString);
      }
      if ((localObject1 instanceof ReflectionException)) {
        throw ((ReflectionException)localObject1);
      }
      throw new MBeanException((Exception)localObject1, "Exception thrown in RequiredModelMBean while trying to invoke operation " + paramString);
    }
    catch (Error localError)
    {
      throw new RuntimeErrorException(localError, "Error occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    }
    catch (Exception localException)
    {
      throw new ReflectionException(localException, "Exception occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    }
  }
  
  private void cacheResult(ModelMBeanOperationInfo paramModelMBeanOperationInfo, Descriptor paramDescriptor, Object paramObject)
    throws MBeanException
  {
    Descriptor localDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
    Object localObject = paramDescriptor.getFieldValue("currencyTimeLimit");
    String str;
    if (localObject != null) {
      str = localObject.toString();
    } else {
      str = null;
    }
    if ((str == null) && (localDescriptor != null))
    {
      localObject = localDescriptor.getFieldValue("currencyTimeLimit");
      if (localObject != null) {
        str = localObject.toString();
      } else {
        str = null;
      }
    }
    if ((str != null) && (!str.equals("-1")))
    {
      paramDescriptor.setField("value", paramObject);
      paramDescriptor.setField("lastUpdatedTimeStamp", String.valueOf(new Date().getTime()));
      this.modelMBeanInfo.setDescriptor(paramDescriptor, "operation");
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String,Object[],Object[])", "new descriptor is " + paramDescriptor);
      }
    }
  }
  
  private static synchronized boolean isRMMBMethodName(String paramString)
  {
    if (rmmbMethodNames == null) {
      try
      {
        HashSet localHashSet = new HashSet();
        Method[] arrayOfMethod = RequiredModelMBean.class.getMethods();
        for (int i = 0; i < arrayOfMethod.length; i++) {
          localHashSet.add(arrayOfMethod[i].getName());
        }
        rmmbMethodNames = localHashSet;
      }
      catch (Exception localException)
      {
        return true;
      }
    }
    return rmmbMethodNames.contains(paramString);
  }
  
  public Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a RequiredModelMBean");
    }
    boolean bool1 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool1) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Entry with " + paramString);
    }
    Object localObject1;
    try
    {
      if (this.modelMBeanInfo == null) {
        throw new AttributeNotFoundException("getAttribute failed: ModelMBeanInfo not found for " + paramString);
      }
      ModelMBeanAttributeInfo localModelMBeanAttributeInfo = this.modelMBeanInfo.getAttribute(paramString);
      Descriptor localDescriptor1 = this.modelMBeanInfo.getMBeanDescriptor();
      if (localModelMBeanAttributeInfo == null) {
        throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + paramString);
      }
      Descriptor localDescriptor2 = localModelMBeanAttributeInfo.getDescriptor();
      if (localDescriptor2 != null)
      {
        if (!localModelMBeanAttributeInfo.isReadable()) {
          throw new AttributeNotFoundException("getAttribute failed: " + paramString + " is not readable ");
        }
        localObject1 = resolveForCacheValue(localDescriptor2);
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "*** cached value is " + localObject1);
        }
        Object localObject2;
        if (localObject1 == null)
        {
          if (bool1) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "**** cached value is null - getting getMethod");
          }
          str1 = (String)localDescriptor2.getFieldValue("getMethod");
          if (str1 != null)
          {
            if (bool1) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "invoking a getMethod for " + paramString);
            }
            localObject2 = invoke(str1, new Object[0], new String[0]);
            if (localObject2 != null)
            {
              if (bool1) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a non-null response from getMethod\n");
              }
              localObject1 = localObject2;
              Object localObject3 = localDescriptor2.getFieldValue("currencyTimeLimit");
              String str2;
              if (localObject3 != null) {
                str2 = localObject3.toString();
              } else {
                str2 = null;
              }
              if ((str2 == null) && (localDescriptor1 != null))
              {
                localObject3 = localDescriptor1.getFieldValue("currencyTimeLimit");
                if (localObject3 != null) {
                  str2 = localObject3.toString();
                } else {
                  str2 = null;
                }
              }
              if ((str2 != null) && (!str2.equals("-1")))
              {
                if (bool1) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "setting cached value and lastUpdatedTime in descriptor");
                }
                localDescriptor2.setField("value", localObject1);
                String str3 = String.valueOf(new Date().getTime());
                localDescriptor2.setField("lastUpdatedTimeStamp", str3);
                localModelMBeanAttributeInfo.setDescriptor(localDescriptor2);
                this.modelMBeanInfo.setDescriptor(localDescriptor2, "attribute");
                if (bool1)
                {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "new descriptor is " + localDescriptor2);
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "AttributeInfo descriptor is " + localModelMBeanAttributeInfo.getDescriptor());
                  String str4 = this.modelMBeanInfo.getDescriptor(paramString, "attribute").toString();
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "modelMBeanInfo: AttributeInfo descriptor is " + str4);
                }
              }
            }
            else
            {
              if (bool1) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a null response from getMethod\n");
              }
              localObject1 = null;
            }
          }
          else
          {
            localObject2 = "";
            localObject1 = localDescriptor2.getFieldValue("value");
            if (localObject1 == null)
            {
              localObject2 = "default ";
              localObject1 = localDescriptor2.getFieldValue("default");
            }
            if (bool1) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "could not find getMethod for " + paramString + ", returning descriptor " + (String)localObject2 + "value");
            }
          }
        }
        final String str1 = localModelMBeanAttributeInfo.getType();
        if (localObject1 != null)
        {
          localObject2 = localObject1.getClass().getName();
          if (!str1.equals(localObject2))
          {
            int i = 0;
            int j = 0;
            int k = 0;
            for (int m = 0; m < primitiveTypes.length; m++) {
              if (str1.equals(primitiveTypes[m]))
              {
                j = 1;
                if (!((String)localObject2).equals(primitiveWrappers[m])) {
                  break;
                }
                k = 1;
                break;
              }
            }
            if (j != 0)
            {
              if (k == 0) {
                i = 1;
              }
            }
            else
            {
              boolean bool2;
              try
              {
                final Class localClass1 = localObject1.getClass();
                final Exception[] arrayOfException = new Exception[1];
                AccessControlContext localAccessControlContext = AccessController.getContext();
                Class localClass2 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
                {
                  public Class<?> run()
                  {
                    try
                    {
                      ReflectUtil.checkPackageAccess(str1);
                      ClassLoader localClassLoader = localClass1.getClassLoader();
                      return Class.forName(str1, true, localClassLoader);
                    }
                    catch (Exception localException)
                    {
                      arrayOfException[0] = localException;
                    }
                    return null;
                  }
                }, localAccessControlContext, this.acc);
                if (arrayOfException[0] != null) {
                  throw arrayOfException[0];
                }
                bool2 = localClass2.isInstance(localObject1);
              }
              catch (Exception localException2)
              {
                bool2 = false;
                if (bool1) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exception: ", localException2);
                }
              }
              if (!bool2) {
                i = 1;
              }
            }
            if (i != 0)
            {
              if (bool1) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Wrong response type '" + str1 + "'");
              }
              throw new MBeanException(new InvalidAttributeValueException("Wrong value type received for get attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
            }
          }
        }
      }
      else
      {
        if (bool1) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed " + paramString + " not in attributeDescriptor\n");
        }
        throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, no getMethod defined in descriptor for attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
      }
    }
    catch (MBeanException localMBeanException)
    {
      throw localMBeanException;
    }
    catch (AttributeNotFoundException localAttributeNotFoundException)
    {
      throw localAttributeNotFoundException;
    }
    catch (Exception localException1)
    {
      if (bool1) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed with " + localException1.getMessage() + " exception type " + localException1.getClass().toString());
      }
      throw new MBeanException(localException1, "An exception occurred while trying to get an attribute value: " + localException1.getMessage());
    }
    if (bool1) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exit");
    }
    return localObject1;
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Entry");
    }
    if (paramArrayOfString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"), "Exception occurred trying to get attributes of a RequiredModelMBean");
    }
    AttributeList localAttributeList = new AttributeList();
    for (int i = 0; i < paramArrayOfString.length; i++) {
      try
      {
        localAttributeList.add(new Attribute(paramArrayOfString[i], getAttribute(paramArrayOfString[i])));
      }
      catch (Exception localException)
      {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Failed to get \"" + paramArrayOfString[i] + "\": ", localException);
        }
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Exit");
    }
    return localAttributeList;
  }
  
  public void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute()", "Entry");
    }
    if (paramAttribute == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"), "Exception occurred trying to set an attribute of a RequiredModelMBean");
    }
    String str1 = paramAttribute.getName();
    Object localObject1 = paramAttribute.getValue();
    int i = 0;
    ModelMBeanAttributeInfo localModelMBeanAttributeInfo = this.modelMBeanInfo.getAttribute(str1);
    if (localModelMBeanAttributeInfo == null) {
      throw new AttributeNotFoundException("setAttribute failed: " + str1 + " is not found ");
    }
    Descriptor localDescriptor1 = this.modelMBeanInfo.getMBeanDescriptor();
    Descriptor localDescriptor2 = localModelMBeanAttributeInfo.getDescriptor();
    if (localDescriptor2 != null)
    {
      if (!localModelMBeanAttributeInfo.isWritable()) {
        throw new AttributeNotFoundException("setAttribute failed: " + str1 + " is not writable ");
      }
      String str2 = (String)localDescriptor2.getFieldValue("setMethod");
      String str3 = (String)localDescriptor2.getFieldValue("getMethod");
      String str4 = localModelMBeanAttributeInfo.getType();
      Object localObject2 = "Unknown";
      try
      {
        localObject2 = getAttribute(str1);
      }
      catch (Throwable localThrowable) {}
      Attribute localAttribute = new Attribute(str1, localObject2);
      if (str2 == null)
      {
        if (localObject1 != null) {
          try
          {
            Class localClass = loadClass(str4);
            if (!localClass.isInstance(localObject1)) {
              throw new InvalidAttributeValueException(localClass.getName() + " expected, " + localObject1.getClass().getName() + " received.");
            }
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Class " + str4 + " for attribute " + str1 + " not found: ", localClassNotFoundException);
            }
          }
        }
        i = 1;
      }
      else
      {
        invoke(str2, new Object[] { localObject1 }, new String[] { str4 });
      }
      Object localObject3 = localDescriptor2.getFieldValue("currencyTimeLimit");
      String str5;
      if (localObject3 != null) {
        str5 = localObject3.toString();
      } else {
        str5 = null;
      }
      if ((str5 == null) && (localDescriptor1 != null))
      {
        localObject3 = localDescriptor1.getFieldValue("currencyTimeLimit");
        if (localObject3 != null) {
          str5 = localObject3.toString();
        } else {
          str5 = null;
        }
      }
      int j = (str5 != null) && (!str5.equals("-1")) ? 1 : 0;
      if ((str2 == null) && (j == 0) && (str3 != null)) {
        throw new MBeanException(new ServiceNotFoundException("No setMethod field is defined in the descriptor for " + str1 + " attribute and caching is not enabled " + "for it"));
      }
      if ((j != 0) || (i != 0))
      {
        if (bool) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setting cached value of " + str1 + " to " + localObject1);
        }
        localDescriptor2.setField("value", localObject1);
        Object localObject4;
        if (j != 0)
        {
          localObject4 = String.valueOf(new Date().getTime());
          localDescriptor2.setField("lastUpdatedTimeStamp", localObject4);
        }
        localModelMBeanAttributeInfo.setDescriptor(localDescriptor2);
        this.modelMBeanInfo.setDescriptor(localDescriptor2, "attribute");
        if (bool)
        {
          localObject4 = new StringBuilder().append("new descriptor is ").append(localDescriptor2).append(". AttributeInfo descriptor is ").append(localModelMBeanAttributeInfo.getDescriptor()).append(". AttributeInfo descriptor is ").append(this.modelMBeanInfo.getDescriptor(str1, "attribute"));
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", ((StringBuilder)localObject4).toString());
        }
      }
      if (bool) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "sending sendAttributeNotification");
      }
      sendAttributeChangeNotification(localAttribute, paramAttribute);
    }
    else
    {
      if (bool) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setMethod failed " + str1 + " not in attributeDescriptor\n");
      }
      throw new InvalidAttributeValueException("Unable to resolve attribute value, no defined in descriptor for attribute");
    }
    if (bool) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Exit");
    }
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Entry");
    }
    if (paramAttributeList == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"), "Exception occurred trying to set attributes of a RequiredModelMBean");
    }
    AttributeList localAttributeList = new AttributeList();
    Iterator localIterator = paramAttributeList.asList().iterator();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      try
      {
        setAttribute(localAttribute);
        localAttributeList.add(localAttribute);
      }
      catch (Exception localException)
      {
        localAttributeList.remove(localAttribute);
      }
    }
    return localAttributeList;
  }
  
  private ModelMBeanInfo createDefaultModelMBeanInfo()
  {
    return new ModelMBeanInfoSupport(getClass().getName(), "Default ModelMBean", null, null, null, null);
  }
  
  private synchronized void writeToLog(String paramString1, String paramString2)
    throws Exception
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Notification Logging to " + paramString1 + ": " + paramString2);
    }
    if ((paramString1 == null) || (paramString2 == null))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Bad input parameters, will not log this entry.");
      }
      return;
    }
    FileOutputStream localFileOutputStream = new FileOutputStream(paramString1, true);
    try
    {
      PrintStream localPrintStream = new PrintStream(localFileOutputStream);
      localPrintStream.println(paramString2);
      localPrintStream.close();
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Successfully opened log " + paramString1);
      }
    }
    catch (Exception localException)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Exception " + localException.toString() + " trying to write to the Notification log file " + paramString1);
      }
      throw localException;
    }
    finally
    {
      localFileOutputStream.close();
    }
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws IllegalArgumentException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
    }
    if (paramNotificationListener == null) {
      throw new IllegalArgumentException("notification listener must not be null");
    }
    if (this.generalBroadcaster == null) {
      this.generalBroadcaster = new NotificationBroadcasterSupport();
    }
    this.generalBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "NotificationListener added");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
    }
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    if (paramNotificationListener == null) {
      throw new ListenerNotFoundException("Notification listener is null");
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Entry");
    }
    if (this.generalBroadcaster == null) {
      throw new ListenerNotFoundException("No notification listeners registered");
    }
    this.generalBroadcaster.removeNotificationListener(paramNotificationListener);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Exit");
    }
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    if (paramNotificationListener == null) {
      throw new ListenerNotFoundException("Notification listener is null");
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
    }
    if (this.generalBroadcaster == null) {
      throw new ListenerNotFoundException("No notification listeners registered");
    }
    this.generalBroadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
    }
  }
  
  public void sendNotification(Notification paramNotification)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Entry");
    }
    if (paramNotification == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("notification object must not be null"), "Exception occurred trying to send a notification from a RequiredModelMBean");
    }
    Descriptor localDescriptor1 = this.modelMBeanInfo.getDescriptor(paramNotification.getType(), "notification");
    Descriptor localDescriptor2 = this.modelMBeanInfo.getMBeanDescriptor();
    if (localDescriptor1 != null)
    {
      String str1 = (String)localDescriptor1.getFieldValue("log");
      if ((str1 == null) && (localDescriptor2 != null)) {
        str1 = (String)localDescriptor2.getFieldValue("log");
      }
      if ((str1 != null) && ((str1.equalsIgnoreCase("t")) || (str1.equalsIgnoreCase("true"))))
      {
        String str2 = (String)localDescriptor1.getFieldValue("logfile");
        if ((str2 == null) && (localDescriptor2 != null)) {
          str2 = (String)localDescriptor2.getFieldValue("logfile");
        }
        if (str2 != null) {
          try
          {
            writeToLog(str2, "LogMsg: " + new Date(paramNotification.getTimeStamp()).toString() + " " + paramNotification.getType() + " " + paramNotification.getMessage() + " Severity = " + (String)localDescriptor1.getFieldValue("severity"));
          }
          catch (Exception localException)
          {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Failed to log " + paramNotification.getType() + " notification: ", localException);
            }
          }
        }
      }
    }
    if (this.generalBroadcaster != null) {
      this.generalBroadcaster.sendNotification(paramNotification);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "sendNotification sent provided notification object");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", " Exit");
    }
  }
  
  public void sendNotification(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Entry");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("notification message must not be null"), "Exception occurred trying to send a text notification from a ModelMBean");
    }
    Notification localNotification = new Notification("jmx.modelmbean.generic", this, 1L, paramString);
    sendNotification(localNotification);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Notification sent");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Exit");
    }
  }
  
  private static final boolean hasNotification(ModelMBeanInfo paramModelMBeanInfo, String paramString)
  {
    try
    {
      if (paramModelMBeanInfo == null) {
        return false;
      }
      return paramModelMBeanInfo.getNotification(paramString) != null;
    }
    catch (MBeanException localMBeanException)
    {
      return false;
    }
    catch (RuntimeOperationsException localRuntimeOperationsException) {}
    return false;
  }
  
  private static final ModelMBeanNotificationInfo makeGenericInfo()
  {
    DescriptorSupport localDescriptorSupport = new DescriptorSupport(new String[] { "name=GENERIC", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.modelmbean.generic" });
    return new ModelMBeanNotificationInfo(new String[] { "jmx.modelmbean.generic" }, "GENERIC", "A text notification has been issued by the managed resource", localDescriptorSupport);
  }
  
  private static final ModelMBeanNotificationInfo makeAttributeChangeInfo()
  {
    DescriptorSupport localDescriptorSupport = new DescriptorSupport(new String[] { "name=ATTRIBUTE_CHANGE", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.attribute.change" });
    return new ModelMBeanNotificationInfo(new String[] { "jmx.attribute.change" }, "ATTRIBUTE_CHANGE", "Signifies that an observed MBean attribute value has changed", localDescriptorSupport);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Entry");
    }
    boolean bool1 = hasNotification(this.modelMBeanInfo, "GENERIC");
    boolean bool2 = hasNotification(this.modelMBeanInfo, "ATTRIBUTE_CHANGE");
    ModelMBeanNotificationInfo[] arrayOfModelMBeanNotificationInfo1 = (ModelMBeanNotificationInfo[])this.modelMBeanInfo.getNotifications();
    int i = (arrayOfModelMBeanNotificationInfo1 == null ? 0 : arrayOfModelMBeanNotificationInfo1.length) + (bool1 ? 0 : 1) + (bool2 ? 0 : 1);
    ModelMBeanNotificationInfo[] arrayOfModelMBeanNotificationInfo2 = new ModelMBeanNotificationInfo[i];
    int j = 0;
    if (!bool1) {
      arrayOfModelMBeanNotificationInfo2[(j++)] = makeGenericInfo();
    }
    if (!bool2) {
      arrayOfModelMBeanNotificationInfo2[(j++)] = makeAttributeChangeInfo();
    }
    int k = arrayOfModelMBeanNotificationInfo1.length;
    int m = j;
    for (int n = 0; n < k; n++) {
      arrayOfModelMBeanNotificationInfo2[(m + n)] = arrayOfModelMBeanNotificationInfo1[n];
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Exit");
    }
    return arrayOfModelMBeanNotificationInfo2;
  }
  
  public void addAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString, Object paramObject)
    throws MBeanException, RuntimeOperationsException, IllegalArgumentException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Entry");
    }
    if (paramNotificationListener == null) {
      throw new IllegalArgumentException("Listener to be registered must not be null");
    }
    if (this.attributeBroadcaster == null) {
      this.attributeBroadcaster = new NotificationBroadcasterSupport();
    }
    AttributeChangeNotificationFilter localAttributeChangeNotificationFilter = new AttributeChangeNotificationFilter();
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanInfo.getAttributes();
    int i = 0;
    int j;
    if (paramString == null)
    {
      if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
        for (j = 0; j < arrayOfMBeanAttributeInfo.length; j++) {
          localAttributeChangeNotificationFilter.enableAttribute(arrayOfMBeanAttributeInfo[j].getName());
        }
      }
    }
    else
    {
      if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
        for (j = 0; j < arrayOfMBeanAttributeInfo.length; j++) {
          if (paramString.equals(arrayOfMBeanAttributeInfo[j].getName()))
          {
            i = 1;
            localAttributeChangeNotificationFilter.enableAttribute(paramString);
            break;
          }
        }
      }
      if (i == 0) {
        throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name does not exist"), "Exception occurred trying to add an AttributeChangeNotification listener");
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      Vector localVector = localAttributeChangeNotificationFilter.getEnabledAttributes();
      String str = localVector.size() > 1 ? "[" + (String)localVector.firstElement() + ", ...]" : localVector.toString();
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Set attribute change filter to " + str);
    }
    this.attributeBroadcaster.addNotificationListener(paramNotificationListener, localAttributeChangeNotificationFilter, paramObject);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Notification listener added for " + paramString);
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Exit");
    }
  }
  
  public void removeAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString)
    throws MBeanException, RuntimeOperationsException, ListenerNotFoundException
  {
    if (paramNotificationListener == null) {
      throw new ListenerNotFoundException("Notification listener is null");
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Entry");
    }
    if (this.attributeBroadcaster == null) {
      throw new ListenerNotFoundException("No attribute change notification listeners registered");
    }
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanInfo.getAttributes();
    int i = 0;
    if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
      for (int j = 0; j < arrayOfMBeanAttributeInfo.length; j++) {
        if (arrayOfMBeanAttributeInfo[j].getName().equals(paramString))
        {
          i = 1;
          break;
        }
      }
    }
    if ((i == 0) && (paramString != null)) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid attribute name"), "Exception occurred trying to remove attribute change notification listener");
    }
    this.attributeBroadcaster.removeNotificationListener(paramNotificationListener);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Exit");
    }
  }
  
  public void sendAttributeChangeNotification(AttributeChangeNotification paramAttributeChangeNotification)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Entry");
    }
    if (paramAttributeChangeNotification == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attribute change notification object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
    }
    Object localObject1 = paramAttributeChangeNotification.getOldValue();
    Object localObject2 = paramAttributeChangeNotification.getNewValue();
    if (localObject1 == null) {
      localObject1 = "null";
    }
    if (localObject2 == null) {
      localObject2 = "null";
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Sending AttributeChangeNotification with " + paramAttributeChangeNotification.getAttributeName() + paramAttributeChangeNotification.getAttributeType() + paramAttributeChangeNotification.getNewValue() + paramAttributeChangeNotification.getOldValue());
    }
    Descriptor localDescriptor1 = this.modelMBeanInfo.getDescriptor(paramAttributeChangeNotification.getType(), "notification");
    Descriptor localDescriptor2 = this.modelMBeanInfo.getMBeanDescriptor();
    String str1;
    String str2;
    if (localDescriptor1 != null)
    {
      str1 = (String)localDescriptor1.getFieldValue("log");
      if ((str1 == null) && (localDescriptor2 != null)) {
        str1 = (String)localDescriptor2.getFieldValue("log");
      }
      if ((str1 != null) && ((str1.equalsIgnoreCase("t")) || (str1.equalsIgnoreCase("true"))))
      {
        str2 = (String)localDescriptor1.getFieldValue("logfile");
        if ((str2 == null) && (localDescriptor2 != null)) {
          str2 = (String)localDescriptor2.getFieldValue("logfile");
        }
        if (str2 != null) {
          try
          {
            writeToLog(str2, "LogMsg: " + new Date(paramAttributeChangeNotification.getTimeStamp()).toString() + " " + paramAttributeChangeNotification.getType() + " " + paramAttributeChangeNotification.getMessage() + " Name = " + paramAttributeChangeNotification.getAttributeName() + " Old value = " + localObject1 + " New value = " + localObject2);
          }
          catch (Exception localException1)
          {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + paramAttributeChangeNotification.getType() + " notification: ", localException1);
            }
          }
        }
      }
    }
    else if (localDescriptor2 != null)
    {
      str1 = (String)localDescriptor2.getFieldValue("log");
      if ((str1 != null) && ((str1.equalsIgnoreCase("t")) || (str1.equalsIgnoreCase("true"))))
      {
        str2 = (String)localDescriptor2.getFieldValue("logfile");
        if (str2 != null) {
          try
          {
            writeToLog(str2, "LogMsg: " + new Date(paramAttributeChangeNotification.getTimeStamp()).toString() + " " + paramAttributeChangeNotification.getType() + " " + paramAttributeChangeNotification.getMessage() + " Name = " + paramAttributeChangeNotification.getAttributeName() + " Old value = " + localObject1 + " New value = " + localObject2);
          }
          catch (Exception localException2)
          {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + paramAttributeChangeNotification.getType() + " notification: ", localException2);
            }
          }
        }
      }
    }
    if (this.attributeBroadcaster != null) {
      this.attributeBroadcaster.sendNotification(paramAttributeChangeNotification);
    }
    if (this.generalBroadcaster != null) {
      this.generalBroadcaster.sendNotification(paramAttributeChangeNotification);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "sent notification");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Exit");
    }
  }
  
  public void sendAttributeChangeNotification(Attribute paramAttribute1, Attribute paramAttribute2)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Entry");
    }
    if ((paramAttribute1 == null) || (paramAttribute2 == null)) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
    }
    if (!paramAttribute1.getName().equals(paramAttribute2.getName())) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute names are not the same"), "Exception occurred trying to send attribute change notification of a ModelMBean");
    }
    Object localObject1 = paramAttribute2.getValue();
    Object localObject2 = paramAttribute1.getValue();
    String str = "unknown";
    if (localObject1 != null) {
      str = localObject1.getClass().getName();
    }
    if (localObject2 != null) {
      str = localObject2.getClass().getName();
    }
    AttributeChangeNotification localAttributeChangeNotification = new AttributeChangeNotification(this, 1L, new Date().getTime(), "AttributeChangeDetected", paramAttribute1.getName(), str, paramAttribute1.getValue(), paramAttribute2.getValue());
    sendAttributeChangeNotification(localAttributeChangeNotification);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Exit");
    }
  }
  
  protected ClassLoaderRepository getClassLoaderRepository()
  {
    return MBeanServerFactory.getClassLoaderRepository(this.server);
  }
  
  private Class<?> loadClass(final String paramString)
    throws ClassNotFoundException
  {
    AccessControlContext localAccessControlContext = AccessController.getContext();
    final ClassNotFoundException[] arrayOfClassNotFoundException = new ClassNotFoundException[1];
    Class localClass = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
    {
      public Class<?> run()
      {
        try
        {
          ReflectUtil.checkPackageAccess(paramString);
          return Class.forName(paramString);
        }
        catch (ClassNotFoundException localClassNotFoundException1)
        {
          ClassLoaderRepository localClassLoaderRepository = RequiredModelMBean.this.getClassLoaderRepository();
          try
          {
            if (localClassLoaderRepository == null) {
              throw new ClassNotFoundException(paramString);
            }
            return localClassLoaderRepository.loadClass(paramString);
          }
          catch (ClassNotFoundException localClassNotFoundException2)
          {
            arrayOfClassNotFoundException[0] = localClassNotFoundException2;
          }
        }
        return null;
      }
    }, localAccessControlContext, this.acc);
    if (arrayOfClassNotFoundException[0] != null) {
      throw arrayOfClassNotFoundException[0];
    }
    return localClass;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    if (paramObjectName == null) {
      throw new NullPointerException("name of RequiredModelMBean to registered is null");
    }
    this.server = paramMBeanServer;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean)
  {
    this.registered = paramBoolean.booleanValue();
  }
  
  public void preDeregister()
    throws Exception
  {}
  
  public void postDeregister()
  {
    this.registered = false;
    this.server = null;
  }
  
  static
  {
    for (int i = 0; i < primitiveClasses.length; i++)
    {
      Class localClass = primitiveClasses[i];
      primitiveClassMap.put(localClass.getName(), localClass);
    }
  }
}
