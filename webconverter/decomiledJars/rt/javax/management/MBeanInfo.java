package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class MBeanInfo
  implements Cloneable, Serializable, DescriptorRead
{
  static final long serialVersionUID = -6451021435135161911L;
  private transient Descriptor descriptor;
  private final String description;
  private final String className;
  private final MBeanAttributeInfo[] attributes;
  private final MBeanOperationInfo[] operations;
  private final MBeanConstructorInfo[] constructors;
  private final MBeanNotificationInfo[] notifications;
  private transient int hashCode;
  private final transient boolean arrayGettersSafe;
  private static final Map<Class<?>, Boolean> arrayGettersSafeMap = new WeakHashMap();
  
  public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo)
    throws IllegalArgumentException
  {
    this(paramString1, paramString2, paramArrayOfMBeanAttributeInfo, paramArrayOfMBeanConstructorInfo, paramArrayOfMBeanOperationInfo, paramArrayOfMBeanNotificationInfo, null);
  }
  
  public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo, Descriptor paramDescriptor)
    throws IllegalArgumentException
  {
    this.className = paramString1;
    this.description = paramString2;
    if (paramArrayOfMBeanAttributeInfo == null) {
      paramArrayOfMBeanAttributeInfo = MBeanAttributeInfo.NO_ATTRIBUTES;
    }
    this.attributes = paramArrayOfMBeanAttributeInfo;
    if (paramArrayOfMBeanOperationInfo == null) {
      paramArrayOfMBeanOperationInfo = MBeanOperationInfo.NO_OPERATIONS;
    }
    this.operations = paramArrayOfMBeanOperationInfo;
    if (paramArrayOfMBeanConstructorInfo == null) {
      paramArrayOfMBeanConstructorInfo = MBeanConstructorInfo.NO_CONSTRUCTORS;
    }
    this.constructors = paramArrayOfMBeanConstructorInfo;
    if (paramArrayOfMBeanNotificationInfo == null) {
      paramArrayOfMBeanNotificationInfo = MBeanNotificationInfo.NO_NOTIFICATIONS;
    }
    this.notifications = paramArrayOfMBeanNotificationInfo;
    if (paramDescriptor == null) {
      paramDescriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }
    this.descriptor = paramDescriptor;
    this.arrayGettersSafe = arrayGettersSafe(getClass(), MBeanInfo.class);
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public String getClassName()
  {
    return this.className;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public MBeanAttributeInfo[] getAttributes()
  {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = nonNullAttributes();
    if (arrayOfMBeanAttributeInfo.length == 0) {
      return arrayOfMBeanAttributeInfo;
    }
    return (MBeanAttributeInfo[])arrayOfMBeanAttributeInfo.clone();
  }
  
  private MBeanAttributeInfo[] fastGetAttributes()
  {
    if (this.arrayGettersSafe) {
      return nonNullAttributes();
    }
    return getAttributes();
  }
  
  private MBeanAttributeInfo[] nonNullAttributes()
  {
    return this.attributes == null ? MBeanAttributeInfo.NO_ATTRIBUTES : this.attributes;
  }
  
  public MBeanOperationInfo[] getOperations()
  {
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = nonNullOperations();
    if (arrayOfMBeanOperationInfo.length == 0) {
      return arrayOfMBeanOperationInfo;
    }
    return (MBeanOperationInfo[])arrayOfMBeanOperationInfo.clone();
  }
  
  private MBeanOperationInfo[] fastGetOperations()
  {
    if (this.arrayGettersSafe) {
      return nonNullOperations();
    }
    return getOperations();
  }
  
  private MBeanOperationInfo[] nonNullOperations()
  {
    return this.operations == null ? MBeanOperationInfo.NO_OPERATIONS : this.operations;
  }
  
  public MBeanConstructorInfo[] getConstructors()
  {
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = nonNullConstructors();
    if (arrayOfMBeanConstructorInfo.length == 0) {
      return arrayOfMBeanConstructorInfo;
    }
    return (MBeanConstructorInfo[])arrayOfMBeanConstructorInfo.clone();
  }
  
  private MBeanConstructorInfo[] fastGetConstructors()
  {
    if (this.arrayGettersSafe) {
      return nonNullConstructors();
    }
    return getConstructors();
  }
  
  private MBeanConstructorInfo[] nonNullConstructors()
  {
    return this.constructors == null ? MBeanConstructorInfo.NO_CONSTRUCTORS : this.constructors;
  }
  
  public MBeanNotificationInfo[] getNotifications()
  {
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = nonNullNotifications();
    if (arrayOfMBeanNotificationInfo.length == 0) {
      return arrayOfMBeanNotificationInfo;
    }
    return (MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone();
  }
  
  private MBeanNotificationInfo[] fastGetNotifications()
  {
    if (this.arrayGettersSafe) {
      return nonNullNotifications();
    }
    return getNotifications();
  }
  
  private MBeanNotificationInfo[] nonNullNotifications()
  {
    return this.notifications == null ? MBeanNotificationInfo.NO_NOTIFICATIONS : this.notifications;
  }
  
  public Descriptor getDescriptor()
  {
    return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + "description=" + getDescription() + ", " + "attributes=" + Arrays.asList(fastGetAttributes()) + ", " + "constructors=" + Arrays.asList(fastGetConstructors()) + ", " + "operations=" + Arrays.asList(fastGetOperations()) + ", " + "notifications=" + Arrays.asList(fastGetNotifications()) + ", " + "descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanInfo)) {
      return false;
    }
    MBeanInfo localMBeanInfo = (MBeanInfo)paramObject;
    if ((!isEqual(getClassName(), localMBeanInfo.getClassName())) || (!isEqual(getDescription(), localMBeanInfo.getDescription())) || (!getDescriptor().equals(localMBeanInfo.getDescriptor()))) {
      return false;
    }
    return (Arrays.equals(localMBeanInfo.fastGetAttributes(), fastGetAttributes())) && (Arrays.equals(localMBeanInfo.fastGetOperations(), fastGetOperations())) && (Arrays.equals(localMBeanInfo.fastGetConstructors(), fastGetConstructors())) && (Arrays.equals(localMBeanInfo.fastGetNotifications(), fastGetNotifications()));
  }
  
  public int hashCode()
  {
    if (this.hashCode != 0) {
      return this.hashCode;
    }
    this.hashCode = (Objects.hash(new Object[] { getClassName(), getDescriptor() }) ^ Arrays.hashCode(fastGetAttributes()) ^ Arrays.hashCode(fastGetOperations()) ^ Arrays.hashCode(fastGetConstructors()) ^ Arrays.hashCode(fastGetNotifications()));
    return this.hashCode;
  }
  
  static boolean arrayGettersSafe(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass1 == paramClass2) {
      return true;
    }
    synchronized (arrayGettersSafeMap)
    {
      Boolean localBoolean = (Boolean)arrayGettersSafeMap.get(paramClass1);
      if (localBoolean == null)
      {
        try
        {
          ArrayGettersSafeAction localArrayGettersSafeAction = new ArrayGettersSafeAction(paramClass1, paramClass2);
          localBoolean = (Boolean)AccessController.doPrivileged(localArrayGettersSafeAction);
        }
        catch (Exception localException)
        {
          localBoolean = Boolean.valueOf(false);
        }
        arrayGettersSafeMap.put(paramClass1, localBoolean);
      }
      return localBoolean.booleanValue();
    }
  }
  
  private static boolean isEqual(String paramString1, String paramString2)
  {
    boolean bool;
    if (paramString1 == null) {
      bool = paramString2 == null;
    } else {
      bool = paramString1.equals(paramString2);
    }
    return bool;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (this.descriptor.getClass() == ImmutableDescriptor.class)
    {
      paramObjectOutputStream.write(1);
      String[] arrayOfString = this.descriptor.getFieldNames();
      paramObjectOutputStream.writeObject(arrayOfString);
      paramObjectOutputStream.writeObject(this.descriptor.getFieldValues(arrayOfString));
    }
    else
    {
      paramObjectOutputStream.write(0);
      paramObjectOutputStream.writeObject(this.descriptor);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    switch (paramObjectInputStream.read())
    {
    case 1: 
      String[] arrayOfString = (String[])paramObjectInputStream.readObject();
      Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
      this.descriptor = (arrayOfString.length == 0 ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(arrayOfString, arrayOfObject));
      break;
    case 0: 
      this.descriptor = ((Descriptor)paramObjectInputStream.readObject());
      if (this.descriptor == null) {
        this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
      }
      break;
    case -1: 
      this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
      break;
    default: 
      throw new StreamCorruptedException("Got unexpected byte.");
    }
  }
  
  private static class ArrayGettersSafeAction
    implements PrivilegedAction<Boolean>
  {
    private final Class<?> subclass;
    private final Class<?> immutableClass;
    
    ArrayGettersSafeAction(Class<?> paramClass1, Class<?> paramClass2)
    {
      this.subclass = paramClass1;
      this.immutableClass = paramClass2;
    }
    
    public Boolean run()
    {
      Method[] arrayOfMethod = this.immutableClass.getMethods();
      for (int i = 0; i < arrayOfMethod.length; i++)
      {
        Method localMethod1 = arrayOfMethod[i];
        String str = localMethod1.getName();
        if ((str.startsWith("get")) && (localMethod1.getParameterTypes().length == 0) && (localMethod1.getReturnType().isArray())) {
          try
          {
            Method localMethod2 = this.subclass.getMethod(str, new Class[0]);
            if (!localMethod2.equals(localMethod1)) {
              return Boolean.valueOf(false);
            }
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
            return Boolean.valueOf(false);
          }
        }
      }
      return Boolean.valueOf(true);
    }
  }
}
