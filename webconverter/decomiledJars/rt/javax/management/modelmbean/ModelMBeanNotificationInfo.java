package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanNotificationInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanNotificationInfo
  extends MBeanNotificationInfo
  implements DescriptorAccess
{
  private static final long oldSerialVersionUID = -5211564525059047097L;
  private static final long newSerialVersionUID = -7445681389570207141L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("notificationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("notificationDescriptor", Descriptor.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private Descriptor notificationDescriptor;
  private static final String currClass = "ModelMBeanNotificationInfo";
  
  public ModelMBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    this(paramArrayOfString, paramString1, paramString2, null);
  }
  
  public ModelMBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2, Descriptor paramDescriptor)
  {
    super(paramArrayOfString, paramString1, paramString2);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "ModelMBeanNotificationInfo", "Entry");
    }
    this.notificationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanNotificationInfo(ModelMBeanNotificationInfo paramModelMBeanNotificationInfo)
  {
    this(paramModelMBeanNotificationInfo.getNotifTypes(), paramModelMBeanNotificationInfo.getName(), paramModelMBeanNotificationInfo.getDescription(), paramModelMBeanNotificationInfo.getDescriptor());
  }
  
  public Object clone()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "clone()", "Entry");
    }
    return new ModelMBeanNotificationInfo(this);
  }
  
  public Descriptor getDescriptor()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Entry");
    }
    if (this.notificationDescriptor == null)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Descriptor value is null, setting descriptor to default values");
      }
      this.notificationDescriptor = validDescriptor(null);
    }
    return (Descriptor)this.notificationDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry");
    }
    this.notificationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public String toString()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "toString()", "Entry");
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ModelMBeanNotificationInfo: ").append(getName());
    localStringBuilder.append(" ; Description: ").append(getDescription());
    localStringBuilder.append(" ; Descriptor: ").append(getDescriptor());
    localStringBuilder.append(" ; Types: ");
    String[] arrayOfString = getNotifTypes();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      if (i > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(arrayOfString[i]);
    }
    return localStringBuilder.toString();
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
      ((Descriptor)localObject).setField("name", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + getName());
    }
    if ((i != 0) && (((Descriptor)localObject).getFieldValue("descriptorType") == null))
    {
      ((Descriptor)localObject).setField("descriptorType", "notification");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"notification\"");
    }
    if (((Descriptor)localObject).getFieldValue("displayName") == null)
    {
      ((Descriptor)localObject).setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    }
    if (((Descriptor)localObject).getFieldValue("severity") == null)
    {
      ((Descriptor)localObject).setField("severity", "6");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor severity field to 6");
    }
    if (!((Descriptor)localObject).isValid()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + localObject.toString());
    }
    if (!getName().equalsIgnoreCase((String)((Descriptor)localObject).getFieldValue("name"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + ((Descriptor)localObject).getFieldValue("name"));
    }
    if (!"notification".equalsIgnoreCase((String)((Descriptor)localObject).getFieldValue("descriptorType"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"notification\" , was: " + ((Descriptor)localObject).getFieldValue("descriptorType"));
    }
    return localObject;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("notificationDescriptor", this.notificationDescriptor);
      localPutField.put("currClass", "ModelMBeanNotificationInfo");
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
      serialVersionUID = -5211564525059047097L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -7445681389570207141L;
    }
  }
}
