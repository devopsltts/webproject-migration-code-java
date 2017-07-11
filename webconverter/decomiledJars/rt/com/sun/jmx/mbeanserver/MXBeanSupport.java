package com.sun.jmx.mbeanserver;

import java.util.Iterator;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MXBeanSupport
  extends MBeanSupport<ConvertingMethod>
{
  private final Object lock = new Object();
  private MXBeanLookup mxbeanLookup;
  private ObjectName objectName;
  
  public <T> MXBeanSupport(T paramT, Class<T> paramClass)
    throws NotCompliantMBeanException
  {
    super(paramT, paramClass);
  }
  
  MBeanIntrospector<ConvertingMethod> getMBeanIntrospector()
  {
    return MXBeanIntrospector.getInstance();
  }
  
  Object getCookie()
  {
    return this.mxbeanLookup;
  }
  
  static <T> Class<? super T> findMXBeanInterface(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("Null resource class");
    }
    Set localSet1 = transitiveInterfaces(paramClass);
    Set localSet2 = Util.newSet();
    Object localObject = localSet1.iterator();
    Class localClass1;
    while (((Iterator)localObject).hasNext())
    {
      localClass1 = (Class)((Iterator)localObject).next();
      if (JMX.isMXBeanInterface(localClass1)) {
        localSet2.add(localClass1);
      }
    }
    if (localSet2.size() > 1)
    {
      localObject = localSet2.iterator();
      label167:
      for (;;)
      {
        if (!((Iterator)localObject).hasNext()) {
          break label170;
        }
        localClass1 = (Class)((Iterator)localObject).next();
        Iterator localIterator = localSet2.iterator();
        for (;;)
        {
          if (!localIterator.hasNext()) {
            break label167;
          }
          Class localClass2 = (Class)localIterator.next();
          if ((localClass1 != localClass2) && (localClass2.isAssignableFrom(localClass1)))
          {
            localIterator.remove();
            break;
          }
        }
      }
      label170:
      localObject = "Class " + paramClass.getName() + " implements more than " + "one MXBean interface: " + localSet2;
      throw new IllegalArgumentException((String)localObject);
    }
    if (localSet2.iterator().hasNext()) {
      return (Class)Util.cast(localSet2.iterator().next());
    }
    localObject = "Class " + paramClass.getName() + " is not a JMX compliant MXBean";
    throw new IllegalArgumentException((String)localObject);
  }
  
  private static Set<Class<?>> transitiveInterfaces(Class<?> paramClass)
  {
    Set localSet = Util.newSet();
    transitiveInterfaces(paramClass, localSet);
    return localSet;
  }
  
  private static void transitiveInterfaces(Class<?> paramClass, Set<Class<?>> paramSet)
  {
    if (paramClass == null) {
      return;
    }
    if (paramClass.isInterface()) {
      paramSet.add(paramClass);
    }
    transitiveInterfaces(paramClass.getSuperclass(), paramSet);
    for (Class localClass : paramClass.getInterfaces()) {
      transitiveInterfaces(localClass, paramSet);
    }
  }
  
  public void register(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException
  {
    if (paramObjectName == null) {
      throw new IllegalArgumentException("Null object name");
    }
    synchronized (this.lock)
    {
      this.mxbeanLookup = MXBeanLookup.lookupFor(paramMBeanServer);
      this.mxbeanLookup.addReference(paramObjectName, getResource());
      this.objectName = paramObjectName;
    }
  }
  
  public void unregister()
  {
    synchronized (this.lock)
    {
      if ((this.mxbeanLookup != null) && (this.mxbeanLookup.removeReference(this.objectName, getResource()))) {
        this.objectName = null;
      }
    }
  }
}
