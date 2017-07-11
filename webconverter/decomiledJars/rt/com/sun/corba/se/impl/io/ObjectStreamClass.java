package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.util.RepositoryId;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import org.omg.CORBA.ValueMember;
import sun.corba.Bridge;

public class ObjectStreamClass
  implements Serializable
{
  private static final boolean DEBUG_SVUID = false;
  public static final long kDefaultUID = -1L;
  private static Object[] noArgsList = new Object[0];
  private static Class<?>[] noTypesList = new Class[0];
  private boolean isEnum;
  private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Bridge run()
    {
      return Bridge.get();
    }
  });
  private static final PersistentFieldsValue persistentFieldsValue = new PersistentFieldsValue();
  public static final int CLASS_MASK = 1553;
  public static final int FIELD_MASK = 223;
  public static final int METHOD_MASK = 3391;
  private static ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];
  private String name;
  private ObjectStreamClass superclass;
  private boolean serializable;
  private boolean externalizable;
  private ObjectStreamField[] fields;
  private Class<?> ofClass;
  boolean forProxyClass;
  private long suid = -1L;
  private String suidStr = null;
  private long actualSuid = -1L;
  private String actualSuidStr = null;
  int primBytes;
  int objFields;
  private boolean initialized = false;
  private Object lock = new Object();
  private boolean hasExternalizableBlockData;
  Method writeObjectMethod;
  Method readObjectMethod;
  private transient Method writeReplaceObjectMethod;
  private transient Method readResolveObjectMethod;
  private Constructor cons;
  private String rmiiiopOptionalDataRepId = null;
  private ObjectStreamClass localClassDesc;
  private static Method hasStaticInitializerMethod = null;
  private static final long serialVersionUID = -6120832682080437368L;
  public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
  private static Comparator compareClassByName = new CompareClassByName(null);
  private static final Comparator compareObjStrFieldsByName = new CompareObjStrFieldsByName(null);
  private static Comparator compareMemberByName = new CompareMemberByName(null);
  
  static final ObjectStreamClass lookup(Class<?> paramClass)
  {
    ObjectStreamClass localObjectStreamClass = lookupInternal(paramClass);
    if ((localObjectStreamClass.isSerializable()) || (localObjectStreamClass.isExternalizable())) {
      return localObjectStreamClass;
    }
    return null;
  }
  
  static ObjectStreamClass lookupInternal(Class<?> paramClass)
  {
    ObjectStreamClass localObjectStreamClass1 = null;
    synchronized (descriptorFor)
    {
      localObjectStreamClass1 = findDescriptorFor(paramClass);
      if (localObjectStreamClass1 == null)
      {
        boolean bool1 = Serializable.class.isAssignableFrom(paramClass);
        ObjectStreamClass localObjectStreamClass2 = null;
        if (bool1)
        {
          Class localClass = paramClass.getSuperclass();
          if (localClass != null) {
            localObjectStreamClass2 = lookup(localClass);
          }
        }
        boolean bool2 = false;
        if (bool1)
        {
          bool2 = ((localObjectStreamClass2 != null) && (localObjectStreamClass2.isExternalizable())) || (Externalizable.class.isAssignableFrom(paramClass));
          if (bool2) {
            bool1 = false;
          }
        }
        localObjectStreamClass1 = new ObjectStreamClass(paramClass, localObjectStreamClass2, bool1, bool2);
      }
      localObjectStreamClass1.init();
    }
    return localObjectStreamClass1;
  }
  
  public final String getName()
  {
    return this.name;
  }
  
  public static final long getSerialVersionUID(Class<?> paramClass)
  {
    ObjectStreamClass localObjectStreamClass = lookup(paramClass);
    if (localObjectStreamClass != null) {
      return localObjectStreamClass.getSerialVersionUID();
    }
    return 0L;
  }
  
  public final long getSerialVersionUID()
  {
    return this.suid;
  }
  
  public final String getSerialVersionUIDStr()
  {
    if (this.suidStr == null) {
      this.suidStr = Long.toHexString(this.suid).toUpperCase();
    }
    return this.suidStr;
  }
  
  public static final long getActualSerialVersionUID(Class<?> paramClass)
  {
    ObjectStreamClass localObjectStreamClass = lookup(paramClass);
    if (localObjectStreamClass != null) {
      return localObjectStreamClass.getActualSerialVersionUID();
    }
    return 0L;
  }
  
  public final long getActualSerialVersionUID()
  {
    return this.actualSuid;
  }
  
  public final String getActualSerialVersionUIDStr()
  {
    if (this.actualSuidStr == null) {
      this.actualSuidStr = Long.toHexString(this.actualSuid).toUpperCase();
    }
    return this.actualSuidStr;
  }
  
  public final Class<?> forClass()
  {
    return this.ofClass;
  }
  
  public ObjectStreamField[] getFields()
  {
    if (this.fields.length > 0)
    {
      ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[this.fields.length];
      System.arraycopy(this.fields, 0, arrayOfObjectStreamField, 0, this.fields.length);
      return arrayOfObjectStreamField;
    }
    return this.fields;
  }
  
  public boolean hasField(ValueMember paramValueMember)
  {
    try
    {
      for (int i = 0; i < this.fields.length; i++) {
        if ((this.fields[i].getName().equals(paramValueMember.name)) && (this.fields[i].getSignature().equals(ValueUtility.getSignature(paramValueMember)))) {
          return true;
        }
      }
    }
    catch (Exception localException) {}
    return false;
  }
  
  final ObjectStreamField[] getFieldsNoCopy()
  {
    return this.fields;
  }
  
  public final ObjectStreamField getField(String paramString)
  {
    for (int i = this.fields.length - 1; i >= 0; i--) {
      if (paramString.equals(this.fields[i].getName())) {
        return this.fields[i];
      }
    }
    return null;
  }
  
  public Serializable writeReplace(Serializable paramSerializable)
  {
    if (this.writeReplaceObjectMethod != null) {
      try
      {
        return (Serializable)this.writeReplaceObjectMethod.invoke(paramSerializable, noArgsList);
      }
      catch (Throwable localThrowable)
      {
        throw new RuntimeException(localThrowable);
      }
    }
    return paramSerializable;
  }
  
  public Object readResolve(Object paramObject)
  {
    if (this.readResolveObjectMethod != null) {
      try
      {
        return this.readResolveObjectMethod.invoke(paramObject, noArgsList);
      }
      catch (Throwable localThrowable)
      {
        throw new RuntimeException(localThrowable);
      }
    }
    return paramObject;
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.name);
    localStringBuffer.append(": static final long serialVersionUID = ");
    localStringBuffer.append(Long.toString(this.suid));
    localStringBuffer.append("L;");
    return localStringBuffer.toString();
  }
  
  private ObjectStreamClass(Class<?> paramClass, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.ofClass = paramClass;
    if (Proxy.isProxyClass(paramClass)) {
      this.forProxyClass = true;
    }
    this.name = paramClass.getName();
    this.isEnum = Enum.class.isAssignableFrom(paramClass);
    this.superclass = paramObjectStreamClass;
    this.serializable = paramBoolean1;
    if (!this.forProxyClass) {
      this.externalizable = paramBoolean2;
    }
    insertDescriptorFor(this);
  }
  
  private void init()
  {
    synchronized (this.lock)
    {
      if (this.initialized) {
        return;
      }
      final Class localClass = this.ofClass;
      if ((!this.serializable) || (this.externalizable) || (this.forProxyClass) || (this.name.equals("java.lang.String")))
      {
        this.fields = NO_FIELDS;
      }
      else if (this.serializable)
      {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            ObjectStreamClass.this.fields = ((ObjectStreamField[])ObjectStreamClass.persistentFieldsValue.get(localClass));
            if (ObjectStreamClass.this.fields == null)
            {
              Field[] arrayOfField = localClass.getDeclaredFields();
              int j = 0;
              ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[arrayOfField.length];
              for (int k = 0; k < arrayOfField.length; k++)
              {
                Field localField2 = arrayOfField[k];
                int m = localField2.getModifiers();
                if ((!Modifier.isStatic(m)) && (!Modifier.isTransient(m)))
                {
                  localField2.setAccessible(true);
                  arrayOfObjectStreamField[(j++)] = new ObjectStreamField(localField2);
                }
              }
              ObjectStreamClass.this.fields = new ObjectStreamField[j];
              System.arraycopy(arrayOfObjectStreamField, 0, ObjectStreamClass.this.fields, 0, j);
            }
            else
            {
              for (int i = ObjectStreamClass.this.fields.length - 1; i >= 0; i--) {
                try
                {
                  Field localField1 = localClass.getDeclaredField(ObjectStreamClass.this.fields[i].getName());
                  if (ObjectStreamClass.this.fields[i].getType() == localField1.getType())
                  {
                    localField1.setAccessible(true);
                    ObjectStreamClass.this.fields[i].setField(localField1);
                  }
                }
                catch (NoSuchFieldException localNoSuchFieldException) {}
              }
            }
            return null;
          }
        });
        if (this.fields.length > 1) {
          Arrays.sort(this.fields);
        }
        computeFieldInfo();
      }
      if ((isNonSerializable()) || (this.isEnum)) {
        this.suid = 0L;
      } else {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            if (ObjectStreamClass.this.forProxyClass) {
              ObjectStreamClass.this.suid = 0L;
            } else {
              try
              {
                Field localField = localClass.getDeclaredField("serialVersionUID");
                int i = localField.getModifiers();
                if ((Modifier.isStatic(i)) && (Modifier.isFinal(i)))
                {
                  localField.setAccessible(true);
                  ObjectStreamClass.this.suid = localField.getLong(localClass);
                }
                else
                {
                  ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(localClass);
                }
              }
              catch (NoSuchFieldException localNoSuchFieldException)
              {
                ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(localClass);
              }
              catch (IllegalAccessException localIllegalAccessException)
              {
                ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(localClass);
              }
            }
            ObjectStreamClass.this.writeReplaceObjectMethod = ObjectStreamClass.getInheritableMethod(localClass, "writeReplace", ObjectStreamClass.noTypesList, Object.class);
            ObjectStreamClass.this.readResolveObjectMethod = ObjectStreamClass.getInheritableMethod(localClass, "readResolve", ObjectStreamClass.noTypesList, Object.class);
            if (ObjectStreamClass.this.externalizable) {
              ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(localClass);
            } else {
              ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(localClass);
            }
            if ((ObjectStreamClass.this.serializable) && (!ObjectStreamClass.this.forProxyClass))
            {
              ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(localClass, "writeObject", new Class[] { ObjectOutputStream.class }, Void.TYPE);
              ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(localClass, "readObject", new Class[] { ObjectInputStream.class }, Void.TYPE);
            }
            return null;
          }
        });
      }
      this.actualSuid = computeStructuralUID(this, localClass);
      if (hasWriteObject()) {
        this.rmiiiopOptionalDataRepId = computeRMIIIOPOptionalDataRepId();
      }
      this.initialized = true;
    }
  }
  
  private static Method getPrivateMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass, Class<?> paramClass2)
  {
    try
    {
      Method localMethod = paramClass1.getDeclaredMethod(paramString, paramArrayOfClass);
      localMethod.setAccessible(true);
      int i = localMethod.getModifiers();
      return (localMethod.getReturnType() == paramClass2) && ((i & 0x8) == 0) && ((i & 0x2) != 0) ? localMethod : null;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
  private String computeRMIIIOPOptionalDataRepId()
  {
    StringBuffer localStringBuffer = new StringBuffer("RMI:org.omg.custom.");
    localStringBuffer.append(RepositoryId.convertToISOLatin1(getName()));
    localStringBuffer.append(':');
    localStringBuffer.append(getActualSerialVersionUIDStr());
    localStringBuffer.append(':');
    localStringBuffer.append(getSerialVersionUIDStr());
    return localStringBuffer.toString();
  }
  
  public final String getRMIIIOPOptionalDataRepId()
  {
    return this.rmiiiopOptionalDataRepId;
  }
  
  ObjectStreamClass(String paramString, long paramLong)
  {
    this.name = paramString;
    this.suid = paramLong;
    this.superclass = null;
  }
  
  final void setClass(Class<?> paramClass)
    throws InvalidClassException
  {
    if (paramClass == null)
    {
      this.localClassDesc = null;
      this.ofClass = null;
      computeFieldInfo();
      return;
    }
    this.localClassDesc = lookupInternal(paramClass);
    if (this.localClassDesc == null) {
      throw new InvalidClassException(paramClass.getName(), "Local class not compatible");
    }
    if (this.suid != this.localClassDesc.suid)
    {
      int i = (isNonSerializable()) || (this.localClassDesc.isNonSerializable()) ? 1 : 0;
      int j = (paramClass.isArray()) && (!paramClass.getName().equals(this.name)) ? 1 : 0;
      if ((j == 0) && (i == 0)) {
        throw new InvalidClassException(paramClass.getName(), "Local class not compatible: stream classdesc serialVersionUID=" + this.suid + " local class serialVersionUID=" + this.localClassDesc.suid);
      }
    }
    if (!compareClassNames(this.name, paramClass.getName(), '.')) {
      throw new InvalidClassException(paramClass.getName(), "Incompatible local class name. Expected class name compatible with " + this.name);
    }
    if ((this.serializable != this.localClassDesc.serializable) || (this.externalizable != this.localClassDesc.externalizable) || ((!this.serializable) && (!this.externalizable))) {
      throw new InvalidClassException(paramClass.getName(), "Serialization incompatible with Externalization");
    }
    ObjectStreamField[] arrayOfObjectStreamField1 = (ObjectStreamField[])this.localClassDesc.fields;
    ObjectStreamField[] arrayOfObjectStreamField2 = (ObjectStreamField[])this.fields;
    int k = 0;
    for (int m = 0; m < arrayOfObjectStreamField2.length; m++) {
      for (int n = k; n < arrayOfObjectStreamField1.length; n++) {
        if (arrayOfObjectStreamField2[m].getName().equals(arrayOfObjectStreamField1[n].getName()))
        {
          if ((arrayOfObjectStreamField2[m].isPrimitive()) && (!arrayOfObjectStreamField2[m].typeEquals(arrayOfObjectStreamField1[n]))) {
            throw new InvalidClassException(paramClass.getName(), "The type of field " + arrayOfObjectStreamField2[m].getName() + " of class " + this.name + " is incompatible.");
          }
          k = n;
          arrayOfObjectStreamField2[m].setField(arrayOfObjectStreamField1[k].getField());
          break;
        }
      }
    }
    computeFieldInfo();
    this.ofClass = paramClass;
    this.readObjectMethod = this.localClassDesc.readObjectMethod;
    this.readResolveObjectMethod = this.localClassDesc.readResolveObjectMethod;
  }
  
  static boolean compareClassNames(String paramString1, String paramString2, char paramChar)
  {
    int i = paramString1.lastIndexOf(paramChar);
    if (i < 0) {
      i = 0;
    }
    int j = paramString2.lastIndexOf(paramChar);
    if (j < 0) {
      j = 0;
    }
    return paramString1.regionMatches(false, i, paramString2, j, paramString1.length() - i);
  }
  
  final boolean typeEquals(ObjectStreamClass paramObjectStreamClass)
  {
    return (this.suid == paramObjectStreamClass.suid) && (compareClassNames(this.name, paramObjectStreamClass.name, '.'));
  }
  
  final void setSuperclass(ObjectStreamClass paramObjectStreamClass)
  {
    this.superclass = paramObjectStreamClass;
  }
  
  final ObjectStreamClass getSuperclass()
  {
    return this.superclass;
  }
  
  final boolean hasReadObject()
  {
    return this.readObjectMethod != null;
  }
  
  final boolean hasWriteObject()
  {
    return this.writeObjectMethod != null;
  }
  
  final boolean isCustomMarshaled()
  {
    return (hasWriteObject()) || (isExternalizable()) || ((this.superclass != null) && (this.superclass.isCustomMarshaled()));
  }
  
  boolean hasExternalizableBlockDataMode()
  {
    return this.hasExternalizableBlockData;
  }
  
  Object newInstance()
    throws InstantiationException, InvocationTargetException, UnsupportedOperationException
  {
    if (this.cons != null) {
      try
      {
        return this.cons.newInstance(new Object[0]);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        InternalError localInternalError = new InternalError();
        localInternalError.initCause(localIllegalAccessException);
        throw localInternalError;
      }
    }
    throw new UnsupportedOperationException();
  }
  
  private static Constructor getExternalizableConstructor(Class<?> paramClass)
  {
    try
    {
      Constructor localConstructor = paramClass.getDeclaredConstructor(new Class[0]);
      localConstructor.setAccessible(true);
      return (localConstructor.getModifiers() & 0x1) != 0 ? localConstructor : null;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
  private static Constructor getSerializableConstructor(Class<?> paramClass)
  {
    Object localObject = paramClass;
    while (Serializable.class.isAssignableFrom((Class)localObject)) {
      if ((localObject = ((Class)localObject).getSuperclass()) == null) {
        return null;
      }
    }
    try
    {
      Constructor localConstructor = ((Class)localObject).getDeclaredConstructor(new Class[0]);
      int i = localConstructor.getModifiers();
      if (((i & 0x2) != 0) || (((i & 0x5) == 0) && (!packageEquals(paramClass, (Class)localObject)))) {
        return null;
      }
      localConstructor = bridge.newConstructorForSerialization(paramClass, localConstructor);
      localConstructor.setAccessible(true);
      return localConstructor;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
  final ObjectStreamClass localClassDescriptor()
  {
    return this.localClassDesc;
  }
  
  boolean isSerializable()
  {
    return this.serializable;
  }
  
  boolean isExternalizable()
  {
    return this.externalizable;
  }
  
  boolean isNonSerializable()
  {
    return (!this.externalizable) && (!this.serializable);
  }
  
  private void computeFieldInfo()
  {
    this.primBytes = 0;
    this.objFields = 0;
    for (int i = 0; i < this.fields.length; i++) {
      switch (this.fields[i].getTypeCode())
      {
      case 'B': 
      case 'Z': 
        this.primBytes += 1;
        break;
      case 'C': 
      case 'S': 
        this.primBytes += 2;
        break;
      case 'F': 
      case 'I': 
        this.primBytes += 4;
        break;
      case 'D': 
      case 'J': 
        this.primBytes += 8;
        break;
      case 'L': 
      case '[': 
        this.objFields += 1;
      }
    }
  }
  
  private static void msg(String paramString)
  {
    System.out.println(paramString);
  }
  
  private static long _computeSerialVersionUID(Class<?> paramClass)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
      localObject1 = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
      DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)localObject1);
      localDataOutputStream.writeUTF(paramClass.getName());
      int i = paramClass.getModifiers();
      i &= 0x611;
      Method[] arrayOfMethod = paramClass.getDeclaredMethods();
      if ((i & 0x200) != 0)
      {
        i &= 0xFBFF;
        if (arrayOfMethod.length > 0) {
          i |= 0x400;
        }
      }
      i &= 0x611;
      localDataOutputStream.writeInt(i);
      if (!paramClass.isArray())
      {
        localObject2 = paramClass.getInterfaces();
        Arrays.sort((Object[])localObject2, compareClassByName);
        for (j = 0; j < localObject2.length; j++) {
          localDataOutputStream.writeUTF(localObject2[j].getName());
        }
      }
      Object localObject2 = paramClass.getDeclaredFields();
      Arrays.sort((Object[])localObject2, compareMemberByName);
      for (int j = 0; j < localObject2.length; j++)
      {
        Object localObject3 = localObject2[j];
        int m = localObject3.getModifiers();
        if ((!Modifier.isPrivate(m)) || ((!Modifier.isTransient(m)) && (!Modifier.isStatic(m))))
        {
          localDataOutputStream.writeUTF(localObject3.getName());
          m &= 0xDF;
          localDataOutputStream.writeInt(m);
          localDataOutputStream.writeUTF(getSignature(localObject3.getType()));
        }
      }
      if (hasStaticInitializer(paramClass))
      {
        localDataOutputStream.writeUTF("<clinit>");
        localDataOutputStream.writeInt(8);
        localDataOutputStream.writeUTF("()V");
      }
      MethodSignature[] arrayOfMethodSignature1 = MethodSignature.removePrivateAndSort(paramClass.getDeclaredConstructors());
      Object localObject4;
      String str;
      int i2;
      for (int k = 0; k < arrayOfMethodSignature1.length; k++)
      {
        MethodSignature localMethodSignature = arrayOfMethodSignature1[k];
        localObject4 = "<init>";
        str = localMethodSignature.signature;
        str = str.replace('/', '.');
        localDataOutputStream.writeUTF((String)localObject4);
        i2 = localMethodSignature.member.getModifiers() & 0xD3F;
        localDataOutputStream.writeInt(i2);
        localDataOutputStream.writeUTF(str);
      }
      MethodSignature[] arrayOfMethodSignature2 = MethodSignature.removePrivateAndSort(arrayOfMethod);
      for (int n = 0; n < arrayOfMethodSignature2.length; n++)
      {
        localObject4 = arrayOfMethodSignature2[n];
        str = ((MethodSignature)localObject4).signature;
        str = str.replace('/', '.');
        localDataOutputStream.writeUTF(((MethodSignature)localObject4).member.getName());
        i2 = ((MethodSignature)localObject4).member.getModifiers() & 0xD3F;
        localDataOutputStream.writeInt(i2);
        localDataOutputStream.writeUTF(str);
      }
      localDataOutputStream.flush();
      byte[] arrayOfByte = localMessageDigest.digest();
      for (int i1 = 0; i1 < Math.min(8, arrayOfByte.length); i1++) {
        l += ((arrayOfByte[i1] & 0xFF) << i1 * 8);
      }
    }
    catch (IOException localIOException)
    {
      l = -1L;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      Object localObject1 = new SecurityException();
      ((SecurityException)localObject1).initCause(localNoSuchAlgorithmException);
      throw ((Throwable)localObject1);
    }
    return l;
  }
  
  private static long computeStructuralUID(ObjectStreamClass paramObjectStreamClass, Class<?> paramClass)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try
    {
      if ((!Serializable.class.isAssignableFrom(paramClass)) || (paramClass.isInterface())) {
        return 0L;
      }
      if (Externalizable.class.isAssignableFrom(paramClass)) {
        return 1L;
      }
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
      localObject = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
      DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)localObject);
      Class localClass = paramClass.getSuperclass();
      if (localClass != null) {
        localDataOutputStream.writeLong(computeStructuralUID(lookup(localClass), localClass));
      }
      if (paramObjectStreamClass.hasWriteObject()) {
        localDataOutputStream.writeInt(2);
      } else {
        localDataOutputStream.writeInt(1);
      }
      ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields();
      if (arrayOfObjectStreamField.length > 1) {
        Arrays.sort(arrayOfObjectStreamField, compareObjStrFieldsByName);
      }
      for (int i = 0; i < arrayOfObjectStreamField.length; i++)
      {
        localDataOutputStream.writeUTF(arrayOfObjectStreamField[i].getName());
        localDataOutputStream.writeUTF(arrayOfObjectStreamField[i].getSignature());
      }
      localDataOutputStream.flush();
      byte[] arrayOfByte = localMessageDigest.digest();
      for (int j = 0; j < Math.min(8, arrayOfByte.length); j++) {
        l += ((arrayOfByte[j] & 0xFF) << j * 8);
      }
    }
    catch (IOException localIOException)
    {
      l = -1L;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      Object localObject = new SecurityException();
      ((SecurityException)localObject).initCause(localNoSuchAlgorithmException);
      throw ((Throwable)localObject);
    }
    return l;
  }
  
  static String getSignature(Class<?> paramClass)
  {
    String str = null;
    if (paramClass.isArray())
    {
      Object localObject = paramClass;
      int i = 0;
      while (((Class)localObject).isArray())
      {
        i++;
        localObject = ((Class)localObject).getComponentType();
      }
      StringBuffer localStringBuffer = new StringBuffer();
      for (int j = 0; j < i; j++) {
        localStringBuffer.append("[");
      }
      localStringBuffer.append(getSignature((Class)localObject));
      str = localStringBuffer.toString();
    }
    else if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        str = "I";
      } else if (paramClass == Byte.TYPE) {
        str = "B";
      } else if (paramClass == Long.TYPE) {
        str = "J";
      } else if (paramClass == Float.TYPE) {
        str = "F";
      } else if (paramClass == Double.TYPE) {
        str = "D";
      } else if (paramClass == Short.TYPE) {
        str = "S";
      } else if (paramClass == Character.TYPE) {
        str = "C";
      } else if (paramClass == Boolean.TYPE) {
        str = "Z";
      } else if (paramClass == Void.TYPE) {
        str = "V";
      }
    }
    else
    {
      str = "L" + paramClass.getName().replace('.', '/') + ";";
    }
    return str;
  }
  
  static String getSignature(Method paramMethod)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getSignature(arrayOfClass[i]));
    }
    localStringBuffer.append(")");
    localStringBuffer.append(getSignature(paramMethod.getReturnType()));
    return localStringBuffer.toString();
  }
  
  static String getSignature(Constructor paramConstructor)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getSignature(arrayOfClass[i]));
    }
    localStringBuffer.append(")V");
    return localStringBuffer.toString();
  }
  
  private static ObjectStreamClass findDescriptorFor(Class<?> paramClass)
  {
    int i = paramClass.hashCode();
    int j = (i & 0x7FFFFFFF) % descriptorFor.length;
    ObjectStreamClassEntry localObjectStreamClassEntry1;
    while (((localObjectStreamClassEntry1 = descriptorFor[j]) != null) && (localObjectStreamClassEntry1.get() == null)) {
      descriptorFor[j] = localObjectStreamClassEntry1.next;
    }
    ObjectStreamClassEntry localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
    while (localObjectStreamClassEntry1 != null)
    {
      ObjectStreamClass localObjectStreamClass = (ObjectStreamClass)localObjectStreamClassEntry1.get();
      if (localObjectStreamClass == null)
      {
        localObjectStreamClassEntry2.next = localObjectStreamClassEntry1.next;
      }
      else
      {
        if (localObjectStreamClass.ofClass == paramClass) {
          return localObjectStreamClass;
        }
        localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
      }
      localObjectStreamClassEntry1 = localObjectStreamClassEntry1.next;
    }
    return null;
  }
  
  private static void insertDescriptorFor(ObjectStreamClass paramObjectStreamClass)
  {
    if (findDescriptorFor(paramObjectStreamClass.ofClass) != null) {
      return;
    }
    int i = paramObjectStreamClass.ofClass.hashCode();
    int j = (i & 0x7FFFFFFF) % descriptorFor.length;
    ObjectStreamClassEntry localObjectStreamClassEntry = new ObjectStreamClassEntry(paramObjectStreamClass);
    localObjectStreamClassEntry.next = descriptorFor[j];
    descriptorFor[j] = localObjectStreamClassEntry;
  }
  
  private static Field[] getDeclaredFields(Class<?> paramClass)
  {
    (Field[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return this.val$clz.getDeclaredFields();
      }
    });
  }
  
  private static boolean hasStaticInitializer(Class<?> paramClass)
  {
    Object localObject;
    if (hasStaticInitializerMethod == null)
    {
      localObject = null;
      try
      {
        if (localObject == null) {
          localObject = java.io.ObjectStreamClass.class;
        }
        hasStaticInitializerMethod = ((Class)localObject).getDeclaredMethod("hasStaticInitializer", new Class[] { Class.class });
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
      if (hasStaticInitializerMethod == null) {
        throw new InternalError("Can't find hasStaticInitializer method on " + ((Class)localObject).getName());
      }
      hasStaticInitializerMethod.setAccessible(true);
    }
    try
    {
      localObject = (Boolean)hasStaticInitializerMethod.invoke(null, new Object[] { paramClass });
      return ((Boolean)localObject).booleanValue();
    }
    catch (Exception localException)
    {
      InternalError localInternalError = new InternalError("Error invoking hasStaticInitializer");
      localInternalError.initCause(localException);
      throw localInternalError;
    }
  }
  
  private static Method getInheritableMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass, Class<?> paramClass2)
  {
    Method localMethod = null;
    Object localObject = paramClass1;
    while (localObject != null) {
      try
      {
        localMethod = ((Class)localObject).getDeclaredMethod(paramString, paramArrayOfClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localObject = ((Class)localObject).getSuperclass();
      }
    }
    if ((localMethod == null) || (localMethod.getReturnType() != paramClass2)) {
      return null;
    }
    localMethod.setAccessible(true);
    int i = localMethod.getModifiers();
    if ((i & 0x408) != 0) {
      return null;
    }
    if ((i & 0x5) != 0) {
      return localMethod;
    }
    if ((i & 0x2) != 0) {
      return paramClass1 == localObject ? localMethod : null;
    }
    return packageEquals(paramClass1, (Class)localObject) ? localMethod : null;
  }
  
  private static boolean packageEquals(Class<?> paramClass1, Class<?> paramClass2)
  {
    Package localPackage1 = paramClass1.getPackage();
    Package localPackage2 = paramClass2.getPackage();
    return (localPackage1 == localPackage2) || ((localPackage1 != null) && (localPackage1.equals(localPackage2)));
  }
  
  private static class CompareClassByName
    implements Comparator
  {
    private CompareClassByName() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      Class localClass1 = (Class)paramObject1;
      Class localClass2 = (Class)paramObject2;
      return localClass1.getName().compareTo(localClass2.getName());
    }
  }
  
  private static class CompareMemberByName
    implements Comparator
  {
    private CompareMemberByName() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      String str1 = ((Member)paramObject1).getName();
      String str2 = ((Member)paramObject2).getName();
      if ((paramObject1 instanceof Method))
      {
        str1 = str1 + ObjectStreamClass.getSignature((Method)paramObject1);
        str2 = str2 + ObjectStreamClass.getSignature((Method)paramObject2);
      }
      else if ((paramObject1 instanceof Constructor))
      {
        str1 = str1 + ObjectStreamClass.getSignature((Constructor)paramObject1);
        str2 = str2 + ObjectStreamClass.getSignature((Constructor)paramObject2);
      }
      return str1.compareTo(str2);
    }
  }
  
  private static class CompareObjStrFieldsByName
    implements Comparator
  {
    private CompareObjStrFieldsByName() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      ObjectStreamField localObjectStreamField1 = (ObjectStreamField)paramObject1;
      ObjectStreamField localObjectStreamField2 = (ObjectStreamField)paramObject2;
      return localObjectStreamField1.getName().compareTo(localObjectStreamField2.getName());
    }
  }
  
  private static class MethodSignature
    implements Comparator
  {
    Member member;
    String signature;
    
    static MethodSignature[] removePrivateAndSort(Member[] paramArrayOfMember)
    {
      int i = 0;
      for (int j = 0; j < paramArrayOfMember.length; j++) {
        if (!Modifier.isPrivate(paramArrayOfMember[j].getModifiers())) {
          i++;
        }
      }
      MethodSignature[] arrayOfMethodSignature = new MethodSignature[i];
      int k = 0;
      for (int m = 0; m < paramArrayOfMember.length; m++) {
        if (!Modifier.isPrivate(paramArrayOfMember[m].getModifiers()))
        {
          arrayOfMethodSignature[k] = new MethodSignature(paramArrayOfMember[m]);
          k++;
        }
      }
      if (k > 0) {
        Arrays.sort(arrayOfMethodSignature, arrayOfMethodSignature[0]);
      }
      return arrayOfMethodSignature;
    }
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      if (paramObject1 == paramObject2) {
        return 0;
      }
      MethodSignature localMethodSignature1 = (MethodSignature)paramObject1;
      MethodSignature localMethodSignature2 = (MethodSignature)paramObject2;
      int i;
      if (isConstructor())
      {
        i = localMethodSignature1.signature.compareTo(localMethodSignature2.signature);
      }
      else
      {
        i = localMethodSignature1.member.getName().compareTo(localMethodSignature2.member.getName());
        if (i == 0) {
          i = localMethodSignature1.signature.compareTo(localMethodSignature2.signature);
        }
      }
      return i;
    }
    
    private final boolean isConstructor()
    {
      return this.member instanceof Constructor;
    }
    
    private MethodSignature(Member paramMember)
    {
      this.member = paramMember;
      if (isConstructor()) {
        this.signature = ObjectStreamClass.getSignature((Constructor)paramMember);
      } else {
        this.signature = ObjectStreamClass.getSignature((Method)paramMember);
      }
    }
  }
  
  private static class ObjectStreamClassEntry
  {
    ObjectStreamClassEntry next;
    private ObjectStreamClass c;
    
    ObjectStreamClassEntry(ObjectStreamClass paramObjectStreamClass)
    {
      this.c = paramObjectStreamClass;
    }
    
    public Object get()
    {
      return this.c;
    }
  }
  
  private static final class PersistentFieldsValue
    extends ClassValue<ObjectStreamField[]>
  {
    PersistentFieldsValue() {}
    
    protected ObjectStreamField[] computeValue(Class<?> paramClass)
    {
      try
      {
        Field localField = paramClass.getDeclaredField("serialPersistentFields");
        int i = localField.getModifiers();
        if ((Modifier.isPrivate(i)) && (Modifier.isStatic(i)) && (Modifier.isFinal(i)))
        {
          localField.setAccessible(true);
          java.io.ObjectStreamField[] arrayOfObjectStreamField = (java.io.ObjectStreamField[])localField.get(paramClass);
          return translateFields(arrayOfObjectStreamField);
        }
      }
      catch (NoSuchFieldException|IllegalAccessException|IllegalArgumentException|ClassCastException localNoSuchFieldException) {}
      return null;
    }
    
    private static ObjectStreamField[] translateFields(java.io.ObjectStreamField[] paramArrayOfObjectStreamField)
    {
      ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[paramArrayOfObjectStreamField.length];
      for (int i = 0; i < paramArrayOfObjectStreamField.length; i++) {
        arrayOfObjectStreamField[i] = new ObjectStreamField(paramArrayOfObjectStreamField[i].getName(), paramArrayOfObjectStreamField[i].getType());
      }
      return arrayOfObjectStreamField;
    }
  }
}
