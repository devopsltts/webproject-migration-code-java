package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;

abstract class MBeanIntrospector<M>
{
  MBeanIntrospector() {}
  
  abstract PerInterfaceMap<M> getPerInterfaceMap();
  
  abstract MBeanInfoMap getMBeanInfoMap();
  
  abstract MBeanAnalyzer<M> getAnalyzer(Class<?> paramClass)
    throws NotCompliantMBeanException;
  
  abstract boolean isMXBean();
  
  abstract M mFrom(Method paramMethod);
  
  abstract String getName(M paramM);
  
  abstract Type getGenericReturnType(M paramM);
  
  abstract Type[] getGenericParameterTypes(M paramM);
  
  abstract String[] getSignature(M paramM);
  
  abstract void checkMethod(M paramM);
  
  abstract Object invokeM2(M paramM, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
    throws InvocationTargetException, IllegalAccessException, MBeanException;
  
  abstract boolean validParameter(M paramM, Object paramObject1, int paramInt, Object paramObject2);
  
  abstract MBeanAttributeInfo getMBeanAttributeInfo(String paramString, M paramM1, M paramM2);
  
  abstract MBeanOperationInfo getMBeanOperationInfo(String paramString, M paramM);
  
  abstract Descriptor getBasicMBeanDescriptor();
  
  abstract Descriptor getMBeanDescriptor(Class<?> paramClass);
  
  final List<Method> getMethods(Class<?> paramClass)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return Arrays.asList(paramClass.getMethods());
  }
  
  final PerInterface<M> getPerInterface(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    PerInterfaceMap localPerInterfaceMap = getPerInterfaceMap();
    synchronized (localPerInterfaceMap)
    {
      WeakReference localWeakReference = (WeakReference)localPerInterfaceMap.get(paramClass);
      PerInterface localPerInterface = localWeakReference == null ? null : (PerInterface)localWeakReference.get();
      if (localPerInterface == null) {
        try
        {
          MBeanAnalyzer localMBeanAnalyzer = getAnalyzer(paramClass);
          MBeanInfo localMBeanInfo = makeInterfaceMBeanInfo(paramClass, localMBeanAnalyzer);
          localPerInterface = new PerInterface(paramClass, this, localMBeanAnalyzer, localMBeanInfo);
          localWeakReference = new WeakReference(localPerInterface);
          localPerInterfaceMap.put(paramClass, localWeakReference);
        }
        catch (Exception localException)
        {
          throw Introspector.throwException(paramClass, localException);
        }
      }
      return localPerInterface;
    }
  }
  
  private MBeanInfo makeInterfaceMBeanInfo(Class<?> paramClass, MBeanAnalyzer<M> paramMBeanAnalyzer)
  {
    MBeanInfoMaker localMBeanInfoMaker = new MBeanInfoMaker(null);
    paramMBeanAnalyzer.visit(localMBeanInfoMaker);
    return localMBeanInfoMaker.makeMBeanInfo(paramClass, "Information on the management interface of the MBean");
  }
  
  final boolean consistent(M paramM1, M paramM2)
  {
    return (paramM1 == null) || (paramM2 == null) || (getGenericReturnType(paramM1).equals(getGenericParameterTypes(paramM2)[0]));
  }
  
  final Object invokeM(M paramM, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
    throws MBeanException, ReflectionException
  {
    try
    {
      return invokeM2(paramM, paramObject1, paramArrayOfObject, paramObject2);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      unwrapInvocationTargetException(localInvocationTargetException);
      throw new RuntimeException(localInvocationTargetException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionException(localIllegalAccessException, localIllegalAccessException.toString());
    }
  }
  
  final void invokeSetter(String paramString, M paramM, Object paramObject1, Object paramObject2, Object paramObject3)
    throws MBeanException, ReflectionException, InvalidAttributeValueException
  {
    try
    {
      invokeM2(paramM, paramObject1, new Object[] { paramObject2 }, paramObject3);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionException(localIllegalAccessException, localIllegalAccessException.toString());
    }
    catch (RuntimeException localRuntimeException)
    {
      maybeInvalidParameter(paramString, paramM, paramObject2, paramObject3);
      throw localRuntimeException;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      maybeInvalidParameter(paramString, paramM, paramObject2, paramObject3);
      unwrapInvocationTargetException(localInvocationTargetException);
    }
  }
  
  private void maybeInvalidParameter(String paramString, M paramM, Object paramObject1, Object paramObject2)
    throws InvalidAttributeValueException
  {
    if (!validParameter(paramM, paramObject1, 0, paramObject2))
    {
      String str = "Invalid value for attribute " + paramString + ": " + paramObject1;
      throw new InvalidAttributeValueException(str);
    }
  }
  
  static boolean isValidParameter(Method paramMethod, Object paramObject, int paramInt)
  {
    Class localClass = paramMethod.getParameterTypes()[paramInt];
    try
    {
      Object localObject = Array.newInstance(localClass, 1);
      Array.set(localObject, 0, paramObject);
      return true;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return false;
  }
  
  private static void unwrapInvocationTargetException(InvocationTargetException paramInvocationTargetException)
    throws MBeanException
  {
    Throwable localThrowable = paramInvocationTargetException.getCause();
    if ((localThrowable instanceof RuntimeException)) {
      throw ((RuntimeException)localThrowable);
    }
    if ((localThrowable instanceof Error)) {
      throw ((Error)localThrowable);
    }
    throw new MBeanException((Exception)localThrowable, localThrowable == null ? null : localThrowable.toString());
  }
  
  final MBeanInfo getMBeanInfo(Object paramObject, PerInterface<M> paramPerInterface)
  {
    MBeanInfo localMBeanInfo = getClassMBeanInfo(paramObject.getClass(), paramPerInterface);
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = findNotifications(paramObject);
    if ((arrayOfMBeanNotificationInfo == null) || (arrayOfMBeanNotificationInfo.length == 0)) {
      return localMBeanInfo;
    }
    return new MBeanInfo(localMBeanInfo.getClassName(), localMBeanInfo.getDescription(), localMBeanInfo.getAttributes(), localMBeanInfo.getConstructors(), localMBeanInfo.getOperations(), arrayOfMBeanNotificationInfo, localMBeanInfo.getDescriptor());
  }
  
  final MBeanInfo getClassMBeanInfo(Class<?> paramClass, PerInterface<M> paramPerInterface)
  {
    MBeanInfoMap localMBeanInfoMap = getMBeanInfoMap();
    synchronized (localMBeanInfoMap)
    {
      WeakHashMap localWeakHashMap = (WeakHashMap)localMBeanInfoMap.get(paramClass);
      if (localWeakHashMap == null)
      {
        localWeakHashMap = new WeakHashMap();
        localMBeanInfoMap.put(paramClass, localWeakHashMap);
      }
      Class localClass = paramPerInterface.getMBeanInterface();
      MBeanInfo localMBeanInfo1 = (MBeanInfo)localWeakHashMap.get(localClass);
      if (localMBeanInfo1 == null)
      {
        MBeanInfo localMBeanInfo2 = paramPerInterface.getMBeanInfo();
        ImmutableDescriptor localImmutableDescriptor = ImmutableDescriptor.union(new Descriptor[] { localMBeanInfo2.getDescriptor(), getMBeanDescriptor(paramClass) });
        localMBeanInfo1 = new MBeanInfo(paramClass.getName(), localMBeanInfo2.getDescription(), localMBeanInfo2.getAttributes(), findConstructors(paramClass), localMBeanInfo2.getOperations(), (MBeanNotificationInfo[])null, localImmutableDescriptor);
        localWeakHashMap.put(localClass, localMBeanInfo1);
      }
      return localMBeanInfo1;
    }
  }
  
  static MBeanNotificationInfo[] findNotifications(Object paramObject)
  {
    if (!(paramObject instanceof NotificationBroadcaster)) {
      return null;
    }
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo1 = ((NotificationBroadcaster)paramObject).getNotificationInfo();
    if (arrayOfMBeanNotificationInfo1 == null) {
      return null;
    }
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo2 = new MBeanNotificationInfo[arrayOfMBeanNotificationInfo1.length];
    for (int i = 0; i < arrayOfMBeanNotificationInfo1.length; i++)
    {
      MBeanNotificationInfo localMBeanNotificationInfo = arrayOfMBeanNotificationInfo1[i];
      if (localMBeanNotificationInfo.getClass() != MBeanNotificationInfo.class) {
        localMBeanNotificationInfo = (MBeanNotificationInfo)localMBeanNotificationInfo.clone();
      }
      arrayOfMBeanNotificationInfo2[i] = localMBeanNotificationInfo;
    }
    return arrayOfMBeanNotificationInfo2;
  }
  
  private static MBeanConstructorInfo[] findConstructors(Class<?> paramClass)
  {
    Constructor[] arrayOfConstructor = paramClass.getConstructors();
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = new MBeanConstructorInfo[arrayOfConstructor.length];
    for (int i = 0; i < arrayOfConstructor.length; i++) {
      arrayOfMBeanConstructorInfo[i] = new MBeanConstructorInfo("Public constructor of the MBean", arrayOfConstructor[i]);
    }
    return arrayOfMBeanConstructorInfo;
  }
  
  private class MBeanInfoMaker
    implements MBeanAnalyzer.MBeanVisitor<M>
  {
    private final List<MBeanAttributeInfo> attrs = Util.newList();
    private final List<MBeanOperationInfo> ops = Util.newList();
    
    private MBeanInfoMaker() {}
    
    public void visitAttribute(String paramString, M paramM1, M paramM2)
    {
      MBeanAttributeInfo localMBeanAttributeInfo = MBeanIntrospector.this.getMBeanAttributeInfo(paramString, paramM1, paramM2);
      this.attrs.add(localMBeanAttributeInfo);
    }
    
    public void visitOperation(String paramString, M paramM)
    {
      MBeanOperationInfo localMBeanOperationInfo = MBeanIntrospector.this.getMBeanOperationInfo(paramString, paramM);
      this.ops.add(localMBeanOperationInfo);
    }
    
    MBeanInfo makeMBeanInfo(Class<?> paramClass, String paramString)
    {
      MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = (MBeanAttributeInfo[])this.attrs.toArray(new MBeanAttributeInfo[0]);
      MBeanOperationInfo[] arrayOfMBeanOperationInfo = (MBeanOperationInfo[])this.ops.toArray(new MBeanOperationInfo[0]);
      String str = "interfaceClassName=" + paramClass.getName();
      ImmutableDescriptor localImmutableDescriptor1 = new ImmutableDescriptor(new String[] { str });
      Descriptor localDescriptor1 = MBeanIntrospector.this.getBasicMBeanDescriptor();
      Descriptor localDescriptor2 = Introspector.descriptorForElement(paramClass);
      ImmutableDescriptor localImmutableDescriptor2 = DescriptorCache.getInstance().union(new Descriptor[] { localImmutableDescriptor1, localDescriptor1, localDescriptor2 });
      return new MBeanInfo(paramClass.getName(), paramString, arrayOfMBeanAttributeInfo, null, arrayOfMBeanOperationInfo, null, localImmutableDescriptor2);
    }
  }
  
  static class MBeanInfoMap
    extends WeakHashMap<Class<?>, WeakHashMap<Class<?>, MBeanInfo>>
  {
    MBeanInfoMap() {}
  }
  
  static final class PerInterfaceMap<M>
    extends WeakHashMap<Class<?>, WeakReference<PerInterface<M>>>
  {
    PerInterfaceMap() {}
  }
}
