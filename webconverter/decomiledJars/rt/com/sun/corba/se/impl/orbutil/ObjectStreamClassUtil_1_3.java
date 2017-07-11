package com.sun.corba.se.impl.orbutil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

public final class ObjectStreamClassUtil_1_3
{
  private static Comparator compareClassByName = new CompareClassByName(null);
  private static Comparator compareMemberByName = new CompareMemberByName(null);
  private static Method hasStaticInitializerMethod = null;
  
  public ObjectStreamClassUtil_1_3() {}
  
  public static long computeSerialVersionUID(Class paramClass)
  {
    long l = com.sun.corba.se.impl.io.ObjectStreamClass.getSerialVersionUID(paramClass);
    if (l == 0L) {
      return l;
    }
    l = getSerialVersion(l, paramClass).longValue();
    return l;
  }
  
  private static Long getSerialVersion(final long paramLong, Class paramClass)
  {
    (Long)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        long l;
        try
        {
          Field localField = this.val$cl.getDeclaredField("serialVersionUID");
          int i = localField.getModifiers();
          if ((Modifier.isStatic(i)) && (Modifier.isFinal(i)) && (Modifier.isPrivate(i))) {
            l = paramLong;
          } else {
            l = ObjectStreamClassUtil_1_3._computeSerialVersionUID(this.val$cl);
          }
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          l = ObjectStreamClassUtil_1_3._computeSerialVersionUID(this.val$cl);
        }
        return new Long(l);
      }
    });
  }
  
  public static long computeStructuralUID(boolean paramBoolean, Class<?> paramClass)
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
      DigestOutputStream localDigestOutputStream = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
      DataOutputStream localDataOutputStream = new DataOutputStream(localDigestOutputStream);
      Class localClass = paramClass.getSuperclass();
      Object localObject;
      if ((localClass != null) && (localClass != Object.class))
      {
        boolean bool = false;
        Class[] arrayOfClass = { ObjectOutputStream.class };
        localObject = getDeclaredMethod(localClass, "writeObject", arrayOfClass, 2, 8);
        if (localObject != null) {
          bool = true;
        }
        localDataOutputStream.writeLong(computeStructuralUID(bool, localClass));
      }
      if (paramBoolean) {
        localDataOutputStream.writeInt(2);
      } else {
        localDataOutputStream.writeInt(1);
      }
      Field[] arrayOfField = getDeclaredFields(paramClass);
      Arrays.sort(arrayOfField, compareMemberByName);
      for (int i = 0; i < arrayOfField.length; i++)
      {
        localObject = arrayOfField[i];
        k = ((Field)localObject).getModifiers();
        if ((!Modifier.isTransient(k)) && (!Modifier.isStatic(k)))
        {
          localDataOutputStream.writeUTF(((Field)localObject).getName());
          localDataOutputStream.writeUTF(getSignature(((Field)localObject).getType()));
        }
      }
      localDataOutputStream.flush();
      byte[] arrayOfByte = localMessageDigest.digest();
      int j = Math.min(8, arrayOfByte.length);
      for (int k = j; k > 0; k--) {
        l += ((arrayOfByte[k] & 0xFF) << k * 8);
      }
    }
    catch (IOException localIOException)
    {
      l = -1L;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new SecurityException(localNoSuchAlgorithmException.getMessage());
    }
    return l;
  }
  
  private static long _computeSerialVersionUID(Class paramClass)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
      DigestOutputStream localDigestOutputStream = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
      DataOutputStream localDataOutputStream = new DataOutputStream(localDigestOutputStream);
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
      localDataOutputStream.writeInt(i);
      if (!paramClass.isArray())
      {
        localObject1 = paramClass.getInterfaces();
        Arrays.sort((Object[])localObject1, compareClassByName);
        for (j = 0; j < localObject1.length; j++) {
          localDataOutputStream.writeUTF(localObject1[j].getName());
        }
      }
      Object localObject1 = paramClass.getDeclaredFields();
      Arrays.sort((Object[])localObject1, compareMemberByName);
      for (int j = 0; j < localObject1.length; j++)
      {
        Object localObject2 = localObject1[j];
        int m = localObject2.getModifiers();
        if ((!Modifier.isPrivate(m)) || ((!Modifier.isTransient(m)) && (!Modifier.isStatic(m))))
        {
          localDataOutputStream.writeUTF(localObject2.getName());
          localDataOutputStream.writeInt(m);
          localDataOutputStream.writeUTF(getSignature(localObject2.getType()));
        }
      }
      if (hasStaticInitializer(paramClass))
      {
        localDataOutputStream.writeUTF("<clinit>");
        localDataOutputStream.writeInt(8);
        localDataOutputStream.writeUTF("()V");
      }
      MethodSignature[] arrayOfMethodSignature1 = MethodSignature.removePrivateAndSort(paramClass.getDeclaredConstructors());
      Object localObject3;
      String str;
      for (int k = 0; k < arrayOfMethodSignature1.length; k++)
      {
        MethodSignature localMethodSignature = arrayOfMethodSignature1[k];
        localObject3 = "<init>";
        str = localMethodSignature.signature;
        str = str.replace('/', '.');
        localDataOutputStream.writeUTF((String)localObject3);
        localDataOutputStream.writeInt(localMethodSignature.member.getModifiers());
        localDataOutputStream.writeUTF(str);
      }
      MethodSignature[] arrayOfMethodSignature2 = MethodSignature.removePrivateAndSort(arrayOfMethod);
      for (int n = 0; n < arrayOfMethodSignature2.length; n++)
      {
        localObject3 = arrayOfMethodSignature2[n];
        str = ((MethodSignature)localObject3).signature;
        str = str.replace('/', '.');
        localDataOutputStream.writeUTF(((MethodSignature)localObject3).member.getName());
        localDataOutputStream.writeInt(((MethodSignature)localObject3).member.getModifiers());
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
      throw new SecurityException(localNoSuchAlgorithmException.getMessage());
    }
    return l;
  }
  
  private static String getSignature(Class paramClass)
  {
    String str = null;
    if (paramClass.isArray())
    {
      Class localClass = paramClass;
      int i = 0;
      while (localClass.isArray())
      {
        i++;
        localClass = localClass.getComponentType();
      }
      StringBuffer localStringBuffer = new StringBuffer();
      for (int j = 0; j < i; j++) {
        localStringBuffer.append("[");
      }
      localStringBuffer.append(getSignature(localClass));
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
  
  private static String getSignature(Method paramMethod)
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
  
  private static String getSignature(Constructor paramConstructor)
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
  
  private static Field[] getDeclaredFields(Class paramClass)
  {
    (Field[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return this.val$clz.getDeclaredFields();
      }
    });
  }
  
  private static boolean hasStaticInitializer(Class paramClass)
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
      throw new InternalError("Error invoking hasStaticInitializer: " + localException);
    }
  }
  
  private static Method getDeclaredMethod(Class paramClass, final String paramString, final Class[] paramArrayOfClass, final int paramInt1, final int paramInt2)
  {
    (Method)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Method localMethod = null;
        try
        {
          localMethod = this.val$cl.getDeclaredMethod(paramString, paramArrayOfClass);
          int i = localMethod.getModifiers();
          if (((i & paramInt2) != 0) || ((i & paramInt1) != paramInt1)) {
            localMethod = null;
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException) {}
        return localMethod;
      }
    });
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
        str1 = str1 + ObjectStreamClassUtil_1_3.getSignature((Method)paramObject1);
        str2 = str2 + ObjectStreamClassUtil_1_3.getSignature((Method)paramObject2);
      }
      else if ((paramObject1 instanceof Constructor))
      {
        str1 = str1 + ObjectStreamClassUtil_1_3.getSignature((Constructor)paramObject1);
        str2 = str2 + ObjectStreamClassUtil_1_3.getSignature((Constructor)paramObject2);
      }
      return str1.compareTo(str2);
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
        this.signature = ObjectStreamClassUtil_1_3.getSignature((Constructor)paramMember);
      } else {
        this.signature = ObjectStreamClassUtil_1_3.getSignature((Method)paramMember);
      }
    }
  }
}
