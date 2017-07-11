package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanInfoSupport
  extends MBeanInfo
  implements ModelMBeanInfo
{
  private static final long oldSerialVersionUID = -3944083498453227709L;
  private static final long newSerialVersionUID = -1935722590756516193L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("mmbAttributes", [Ljavax.management.MBeanAttributeInfo.class), new ObjectStreamField("mmbConstructors", [Ljavax.management.MBeanConstructorInfo.class), new ObjectStreamField("mmbNotifications", [Ljavax.management.MBeanNotificationInfo.class), new ObjectStreamField("mmbOperations", [Ljavax.management.MBeanOperationInfo.class), new ObjectStreamField("currClass", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("modelMBeanAttributes", [Ljavax.management.MBeanAttributeInfo.class), new ObjectStreamField("modelMBeanConstructors", [Ljavax.management.MBeanConstructorInfo.class), new ObjectStreamField("modelMBeanNotifications", [Ljavax.management.MBeanNotificationInfo.class), new ObjectStreamField("modelMBeanOperations", [Ljavax.management.MBeanOperationInfo.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private Descriptor modelMBeanDescriptor = null;
  private MBeanAttributeInfo[] modelMBeanAttributes;
  private MBeanConstructorInfo[] modelMBeanConstructors;
  private MBeanNotificationInfo[] modelMBeanNotifications;
  private MBeanOperationInfo[] modelMBeanOperations;
  private static final String ATTR = "attribute";
  private static final String OPER = "operation";
  private static final String NOTF = "notification";
  private static final String CONS = "constructor";
  private static final String MMB = "mbean";
  private static final String ALL = "all";
  private static final String currClass = "ModelMBeanInfoSupport";
  private static final ModelMBeanAttributeInfo[] NO_ATTRIBUTES = new ModelMBeanAttributeInfo[0];
  private static final ModelMBeanConstructorInfo[] NO_CONSTRUCTORS = new ModelMBeanConstructorInfo[0];
  private static final ModelMBeanNotificationInfo[] NO_NOTIFICATIONS = new ModelMBeanNotificationInfo[0];
  private static final ModelMBeanOperationInfo[] NO_OPERATIONS = new ModelMBeanOperationInfo[0];
  
  public ModelMBeanInfoSupport(ModelMBeanInfo paramModelMBeanInfo)
  {
    super(paramModelMBeanInfo.getClassName(), paramModelMBeanInfo.getDescription(), paramModelMBeanInfo.getAttributes(), paramModelMBeanInfo.getConstructors(), paramModelMBeanInfo.getOperations(), paramModelMBeanInfo.getNotifications());
    this.modelMBeanAttributes = paramModelMBeanInfo.getAttributes();
    this.modelMBeanConstructors = paramModelMBeanInfo.getConstructors();
    this.modelMBeanOperations = paramModelMBeanInfo.getOperations();
    this.modelMBeanNotifications = paramModelMBeanInfo.getNotifications();
    try
    {
      Descriptor localDescriptor = paramModelMBeanInfo.getMBeanDescriptor();
      this.modelMBeanDescriptor = validDescriptor(localDescriptor);
    }
    catch (MBeanException localMBeanException)
    {
      this.modelMBeanDescriptor = validDescriptor(null);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Could not get a valid modelMBeanDescriptor, setting a default Descriptor");
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Exit");
    }
  }
  
  public ModelMBeanInfoSupport(String paramString1, String paramString2, ModelMBeanAttributeInfo[] paramArrayOfModelMBeanAttributeInfo, ModelMBeanConstructorInfo[] paramArrayOfModelMBeanConstructorInfo, ModelMBeanOperationInfo[] paramArrayOfModelMBeanOperationInfo, ModelMBeanNotificationInfo[] paramArrayOfModelMBeanNotificationInfo)
  {
    this(paramString1, paramString2, paramArrayOfModelMBeanAttributeInfo, paramArrayOfModelMBeanConstructorInfo, paramArrayOfModelMBeanOperationInfo, paramArrayOfModelMBeanNotificationInfo, null);
  }
  
  public ModelMBeanInfoSupport(String paramString1, String paramString2, ModelMBeanAttributeInfo[] paramArrayOfModelMBeanAttributeInfo, ModelMBeanConstructorInfo[] paramArrayOfModelMBeanConstructorInfo, ModelMBeanOperationInfo[] paramArrayOfModelMBeanOperationInfo, ModelMBeanNotificationInfo[] paramArrayOfModelMBeanNotificationInfo, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramArrayOfModelMBeanAttributeInfo != null ? paramArrayOfModelMBeanAttributeInfo : NO_ATTRIBUTES, paramArrayOfModelMBeanConstructorInfo != null ? paramArrayOfModelMBeanConstructorInfo : NO_CONSTRUCTORS, paramArrayOfModelMBeanOperationInfo != null ? paramArrayOfModelMBeanOperationInfo : NO_OPERATIONS, paramArrayOfModelMBeanNotificationInfo != null ? paramArrayOfModelMBeanNotificationInfo : NO_NOTIFICATIONS);
    this.modelMBeanAttributes = paramArrayOfModelMBeanAttributeInfo;
    this.modelMBeanConstructors = paramArrayOfModelMBeanConstructorInfo;
    this.modelMBeanOperations = paramArrayOfModelMBeanOperationInfo;
    this.modelMBeanNotifications = paramArrayOfModelMBeanNotificationInfo;
    this.modelMBeanDescriptor = validDescriptor(paramDescriptor);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfoSupport(String,String,ModelMBeanAttributeInfo[],ModelMBeanConstructorInfo[],ModelMBeanOperationInfo[],ModelMBeanNotificationInfo[],Descriptor)", "Exit");
    }
  }
  
  public Object clone()
  {
    return new ModelMBeanInfoSupport(this);
  }
  
  public Descriptor[] getDescriptors(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Entry");
    }
    if ((paramString == null) || (paramString.equals(""))) {
      paramString = "all";
    }
    Descriptor[] arrayOfDescriptor;
    if (paramString.equalsIgnoreCase("mbean"))
    {
      arrayOfDescriptor = new Descriptor[] { this.modelMBeanDescriptor };
    }
    else
    {
      Object localObject;
      int i;
      int j;
      if (paramString.equalsIgnoreCase("attribute"))
      {
        localObject = this.modelMBeanAttributes;
        i = 0;
        if (localObject != null) {
          i = localObject.length;
        }
        arrayOfDescriptor = new Descriptor[i];
        for (j = 0; j < i; j++) {
          arrayOfDescriptor[j] = ((ModelMBeanAttributeInfo)localObject[j]).getDescriptor();
        }
      }
      else if (paramString.equalsIgnoreCase("operation"))
      {
        localObject = this.modelMBeanOperations;
        i = 0;
        if (localObject != null) {
          i = localObject.length;
        }
        arrayOfDescriptor = new Descriptor[i];
        for (j = 0; j < i; j++) {
          arrayOfDescriptor[j] = ((ModelMBeanOperationInfo)localObject[j]).getDescriptor();
        }
      }
      else if (paramString.equalsIgnoreCase("constructor"))
      {
        localObject = this.modelMBeanConstructors;
        i = 0;
        if (localObject != null) {
          i = localObject.length;
        }
        arrayOfDescriptor = new Descriptor[i];
        for (j = 0; j < i; j++) {
          arrayOfDescriptor[j] = ((ModelMBeanConstructorInfo)localObject[j]).getDescriptor();
        }
      }
      else if (paramString.equalsIgnoreCase("notification"))
      {
        localObject = this.modelMBeanNotifications;
        i = 0;
        if (localObject != null) {
          i = localObject.length;
        }
        arrayOfDescriptor = new Descriptor[i];
        for (j = 0; j < i; j++) {
          arrayOfDescriptor[j] = ((ModelMBeanNotificationInfo)localObject[j]).getDescriptor();
        }
      }
      else if (paramString.equalsIgnoreCase("all"))
      {
        localObject = this.modelMBeanAttributes;
        i = 0;
        if (localObject != null) {
          i = localObject.length;
        }
        MBeanOperationInfo[] arrayOfMBeanOperationInfo = this.modelMBeanOperations;
        int k = 0;
        if (arrayOfMBeanOperationInfo != null) {
          k = arrayOfMBeanOperationInfo.length;
        }
        MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = this.modelMBeanConstructors;
        int m = 0;
        if (arrayOfMBeanConstructorInfo != null) {
          m = arrayOfMBeanConstructorInfo.length;
        }
        MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = this.modelMBeanNotifications;
        int n = 0;
        if (arrayOfMBeanNotificationInfo != null) {
          n = arrayOfMBeanNotificationInfo.length;
        }
        int i1 = i + m + k + n + 1;
        arrayOfDescriptor = new Descriptor[i1];
        arrayOfDescriptor[(i1 - 1)] = this.modelMBeanDescriptor;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++)
        {
          arrayOfDescriptor[i2] = ((ModelMBeanAttributeInfo)localObject[i3]).getDescriptor();
          i2++;
        }
        for (i3 = 0; i3 < m; i3++)
        {
          arrayOfDescriptor[i2] = ((ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[i3]).getDescriptor();
          i2++;
        }
        for (i3 = 0; i3 < k; i3++)
        {
          arrayOfDescriptor[i2] = ((ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[i3]).getDescriptor();
          i2++;
        }
        for (i3 = 0; i3 < n; i3++)
        {
          arrayOfDescriptor[i2] = ((ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[i3]).getDescriptor();
          i2++;
        }
      }
      else
      {
        localObject = new IllegalArgumentException("Descriptor Type is invalid");
        throw new RuntimeOperationsException((RuntimeException)localObject, "Exception occurred trying to find the descriptors of the MBean");
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Exit");
    }
    return arrayOfDescriptor;
  }
  
  public void setDescriptors(Descriptor[] paramArrayOfDescriptor)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Entry");
    }
    if (paramArrayOfDescriptor == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor list is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo");
    }
    if (paramArrayOfDescriptor.length == 0) {
      return;
    }
    for (int i = 0; i < paramArrayOfDescriptor.length; i++) {
      setDescriptor(paramArrayOfDescriptor[i], null);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Exit");
    }
  }
  
  public Descriptor getDescriptor(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptor(String)", "Entry");
    }
    return getDescriptor(paramString, null);
  }
  
  public Descriptor getDescriptor(String paramString1, String paramString2)
    throws MBeanException, RuntimeOperationsException
  {
    if (paramString1 == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo");
    }
    if ("mbean".equalsIgnoreCase(paramString2)) {
      return (Descriptor)this.modelMBeanDescriptor.clone();
    }
    Object localObject;
    if (("attribute".equalsIgnoreCase(paramString2)) || (paramString2 == null))
    {
      localObject = getAttribute(paramString1);
      if (localObject != null) {
        return ((ModelMBeanAttributeInfo)localObject).getDescriptor();
      }
      if (paramString2 != null) {
        return null;
      }
    }
    if (("operation".equalsIgnoreCase(paramString2)) || (paramString2 == null))
    {
      localObject = getOperation(paramString1);
      if (localObject != null) {
        return ((ModelMBeanOperationInfo)localObject).getDescriptor();
      }
      if (paramString2 != null) {
        return null;
      }
    }
    if (("constructor".equalsIgnoreCase(paramString2)) || (paramString2 == null))
    {
      localObject = getConstructor(paramString1);
      if (localObject != null) {
        return ((ModelMBeanConstructorInfo)localObject).getDescriptor();
      }
      if (paramString2 != null) {
        return null;
      }
    }
    if (("notification".equalsIgnoreCase(paramString2)) || (paramString2 == null))
    {
      localObject = getNotification(paramString1);
      if (localObject != null) {
        return ((ModelMBeanNotificationInfo)localObject).getDescriptor();
      }
      if (paramString2 != null) {
        return null;
      }
    }
    if (paramString2 == null) {
      return null;
    }
    throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor Type is invalid"), "Exception occurred trying to find the descriptors of the MBean");
  }
  
  public void setDescriptor(Descriptor paramDescriptor, String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Entry");
    }
    if (paramDescriptor == null) {
      paramDescriptor = new DescriptorSupport();
    }
    if ((paramString == null) || (paramString.equals("")))
    {
      paramString = (String)paramDescriptor.getFieldValue("descriptorType");
      if (paramString == null)
      {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptorType null in both String parameter and Descriptor, defaulting to mbean");
        paramString = "mbean";
      }
    }
    String str = (String)paramDescriptor.getFieldValue("name");
    if (str == null)
    {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptor name null, defaulting to " + getClassName());
      str = getClassName();
    }
    int i = 0;
    Object localObject1;
    if (paramString.equalsIgnoreCase("mbean"))
    {
      setMBeanDescriptor(paramDescriptor);
      i = 1;
    }
    else
    {
      int j;
      int k;
      Object localObject2;
      if (paramString.equalsIgnoreCase("attribute"))
      {
        localObject1 = this.modelMBeanAttributes;
        j = 0;
        if (localObject1 != null) {
          j = localObject1.length;
        }
        for (k = 0; k < j; k++) {
          if (str.equals(localObject1[k].getName()))
          {
            i = 1;
            localObject2 = (ModelMBeanAttributeInfo)localObject1[k];
            ((ModelMBeanAttributeInfo)localObject2).setDescriptor(paramDescriptor);
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
            {
              StringBuilder localStringBuilder = new StringBuilder().append("Setting descriptor to ").append(paramDescriptor).append("\t\n local: AttributeInfo descriptor is ").append(((ModelMBeanAttributeInfo)localObject2).getDescriptor()).append("\t\n modelMBeanInfo: AttributeInfo descriptor is ").append(getDescriptor(str, "attribute"));
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", localStringBuilder.toString());
            }
          }
        }
      }
      else if (paramString.equalsIgnoreCase("operation"))
      {
        localObject1 = this.modelMBeanOperations;
        j = 0;
        if (localObject1 != null) {
          j = localObject1.length;
        }
        for (k = 0; k < j; k++) {
          if (str.equals(localObject1[k].getName()))
          {
            i = 1;
            localObject2 = (ModelMBeanOperationInfo)localObject1[k];
            ((ModelMBeanOperationInfo)localObject2).setDescriptor(paramDescriptor);
          }
        }
      }
      else if (paramString.equalsIgnoreCase("constructor"))
      {
        localObject1 = this.modelMBeanConstructors;
        j = 0;
        if (localObject1 != null) {
          j = localObject1.length;
        }
        for (k = 0; k < j; k++) {
          if (str.equals(localObject1[k].getName()))
          {
            i = 1;
            localObject2 = (ModelMBeanConstructorInfo)localObject1[k];
            ((ModelMBeanConstructorInfo)localObject2).setDescriptor(paramDescriptor);
          }
        }
      }
      else if (paramString.equalsIgnoreCase("notification"))
      {
        localObject1 = this.modelMBeanNotifications;
        j = 0;
        if (localObject1 != null) {
          j = localObject1.length;
        }
        for (k = 0; k < j; k++) {
          if (str.equals(localObject1[k].getName()))
          {
            i = 1;
            localObject2 = (ModelMBeanNotificationInfo)localObject1[k];
            ((ModelMBeanNotificationInfo)localObject2).setDescriptor(paramDescriptor);
          }
        }
      }
      else
      {
        localObject1 = new IllegalArgumentException("Invalid descriptor type: " + paramString);
        throw new RuntimeOperationsException((RuntimeException)localObject1, "Exception occurred trying to set the descriptors of the MBean");
      }
    }
    if (i == 0)
    {
      localObject1 = new IllegalArgumentException("Descriptor name is invalid: type=" + paramString + "; name=" + str);
      throw new RuntimeOperationsException((RuntimeException)localObject1, "Exception occurred trying to set the descriptors of the MBean");
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Exit");
    }
  }
  
  public ModelMBeanAttributeInfo getAttribute(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    ModelMBeanAttributeInfo localModelMBeanAttributeInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Entry");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute Name is null"), "Exception occurred trying to get the ModelMBeanAttributeInfo of the MBean");
    }
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanAttributes;
    int i = 0;
    if (arrayOfMBeanAttributeInfo != null) {
      i = arrayOfMBeanAttributeInfo.length;
    }
    for (int j = 0; (j < i) && (localModelMBeanAttributeInfo == null); j++)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      {
        StringBuilder localStringBuilder = new StringBuilder().append("\t\n this.getAttributes() MBeanAttributeInfo Array ").append(j).append(":").append(((ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[j]).getDescriptor()).append("\t\n this.modelMBeanAttributes MBeanAttributeInfo Array ").append(j).append(":").append(((ModelMBeanAttributeInfo)this.modelMBeanAttributes[j]).getDescriptor());
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", localStringBuilder.toString());
      }
      if (paramString.equals(arrayOfMBeanAttributeInfo[j].getName())) {
        localModelMBeanAttributeInfo = (ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[j].clone();
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Exit");
    }
    return localModelMBeanAttributeInfo;
  }
  
  public ModelMBeanOperationInfo getOperation(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    ModelMBeanOperationInfo localModelMBeanOperationInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Entry");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("inName is null"), "Exception occurred trying to get the ModelMBeanOperationInfo of the MBean");
    }
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = this.modelMBeanOperations;
    int i = 0;
    if (arrayOfMBeanOperationInfo != null) {
      i = arrayOfMBeanOperationInfo.length;
    }
    for (int j = 0; (j < i) && (localModelMBeanOperationInfo == null); j++) {
      if (paramString.equals(arrayOfMBeanOperationInfo[j].getName())) {
        localModelMBeanOperationInfo = (ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[j].clone();
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Exit");
    }
    return localModelMBeanOperationInfo;
  }
  
  public ModelMBeanConstructorInfo getConstructor(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    ModelMBeanConstructorInfo localModelMBeanConstructorInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Entry");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Constructor name is null"), "Exception occurred trying to get the ModelMBeanConstructorInfo of the MBean");
    }
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = this.modelMBeanConstructors;
    int i = 0;
    if (arrayOfMBeanConstructorInfo != null) {
      i = arrayOfMBeanConstructorInfo.length;
    }
    for (int j = 0; (j < i) && (localModelMBeanConstructorInfo == null); j++) {
      if (paramString.equals(arrayOfMBeanConstructorInfo[j].getName())) {
        localModelMBeanConstructorInfo = (ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[j].clone();
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Exit");
    }
    return localModelMBeanConstructorInfo;
  }
  
  public ModelMBeanNotificationInfo getNotification(String paramString)
    throws MBeanException, RuntimeOperationsException
  {
    ModelMBeanNotificationInfo localModelMBeanNotificationInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Entry");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Notification name is null"), "Exception occurred trying to get the ModelMBeanNotificationInfo of the MBean");
    }
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = this.modelMBeanNotifications;
    int i = 0;
    if (arrayOfMBeanNotificationInfo != null) {
      i = arrayOfMBeanNotificationInfo.length;
    }
    for (int j = 0; (j < i) && (localModelMBeanNotificationInfo == null); j++) {
      if (paramString.equals(arrayOfMBeanNotificationInfo[j].getName())) {
        localModelMBeanNotificationInfo = (ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[j].clone();
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Exit");
    }
    return localModelMBeanNotificationInfo;
  }
  
  public Descriptor getDescriptor()
  {
    return getMBeanDescriptorNoException();
  }
  
  public Descriptor getMBeanDescriptor()
    throws MBeanException
  {
    return getMBeanDescriptorNoException();
  }
  
  private Descriptor getMBeanDescriptorNoException()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Entry");
    }
    if (this.modelMBeanDescriptor == null) {
      this.modelMBeanDescriptor = validDescriptor(null);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Exit, returning: " + this.modelMBeanDescriptor);
    }
    return (Descriptor)this.modelMBeanDescriptor.clone();
  }
  
  public void setMBeanDescriptor(Descriptor paramDescriptor)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setMBeanDescriptor(Descriptor)", "Entry");
    }
    this.modelMBeanDescriptor = validDescriptor(paramDescriptor);
  }
  
  private Descriptor validDescriptor(Descriptor paramDescriptor)
    throws RuntimeOperationsException
  {
    int i = paramDescriptor == null ? 1 : 0;
    Object localObject;
    if (i != 0)
    {
      localObject = new DescriptorSupport();
      JmxProperties.MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
    }
    else
    {
      localObject = (Descriptor)paramDescriptor.clone();
    }
    if ((i != 0) && (((Descriptor)localObject).getFieldValue("name") == null))
    {
      ((Descriptor)localObject).setField("name", getClassName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + getClassName());
    }
    if ((i != 0) && (((Descriptor)localObject).getFieldValue("descriptorType") == null))
    {
      ((Descriptor)localObject).setField("descriptorType", "mbean");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"mbean\"");
    }
    if (((Descriptor)localObject).getFieldValue("displayName") == null)
    {
      ((Descriptor)localObject).setField("displayName", getClassName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getClassName());
    }
    if (((Descriptor)localObject).getFieldValue("persistPolicy") == null)
    {
      ((Descriptor)localObject).setField("persistPolicy", "never");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor persistPolicy to \"never\"");
    }
    if (((Descriptor)localObject).getFieldValue("log") == null)
    {
      ((Descriptor)localObject).setField("log", "F");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor \"log\" field to \"F\"");
    }
    if (((Descriptor)localObject).getFieldValue("visibility") == null)
    {
      ((Descriptor)localObject).setField("visibility", "1");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor visibility to 1");
    }
    if (!((Descriptor)localObject).isValid()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + localObject.toString());
    }
    if (!((String)((Descriptor)localObject).getFieldValue("descriptorType")).equalsIgnoreCase("mbean")) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: mbean , was: " + ((Descriptor)localObject).getFieldValue("descriptorType"));
    }
    return localObject;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      this.modelMBeanDescriptor = ((Descriptor)localGetField.get("modelMBeanDescriptor", null));
      if (localGetField.defaulted("modelMBeanDescriptor")) {
        throw new NullPointerException("modelMBeanDescriptor");
      }
      this.modelMBeanAttributes = ((MBeanAttributeInfo[])localGetField.get("mmbAttributes", null));
      if (localGetField.defaulted("mmbAttributes")) {
        throw new NullPointerException("mmbAttributes");
      }
      this.modelMBeanConstructors = ((MBeanConstructorInfo[])localGetField.get("mmbConstructors", null));
      if (localGetField.defaulted("mmbConstructors")) {
        throw new NullPointerException("mmbConstructors");
      }
      this.modelMBeanNotifications = ((MBeanNotificationInfo[])localGetField.get("mmbNotifications", null));
      if (localGetField.defaulted("mmbNotifications")) {
        throw new NullPointerException("mmbNotifications");
      }
      this.modelMBeanOperations = ((MBeanOperationInfo[])localGetField.get("mmbOperations", null));
      if (localGetField.defaulted("mmbOperations")) {
        throw new NullPointerException("mmbOperations");
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
      localPutField.put("modelMBeanDescriptor", this.modelMBeanDescriptor);
      localPutField.put("mmbAttributes", this.modelMBeanAttributes);
      localPutField.put("mmbConstructors", this.modelMBeanConstructors);
      localPutField.put("mmbNotifications", this.modelMBeanNotifications);
      localPutField.put("mmbOperations", this.modelMBeanOperations);
      localPutField.put("currClass", "ModelMBeanInfoSupport");
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
      serialVersionUID = -3944083498453227709L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -1935722590756516193L;
    }
  }
}
