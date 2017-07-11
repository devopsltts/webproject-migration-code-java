package java.lang.management;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.UnixOperatingSystemMXBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import sun.management.ManagementFactoryHelper;
import sun.management.Util;

 enum PlatformComponent
{
  CLASS_LOADING("java.lang.management.ClassLoadingMXBean", "java.lang", "ClassLoading", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  COMPILATION("java.lang.management.CompilationMXBean", "java.lang", "Compilation", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  MEMORY("java.lang.management.MemoryMXBean", "java.lang", "Memory", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  GARBAGE_COLLECTOR("java.lang.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", keyProperties(new String[] { "name" }), false, new MXBeanFetcher(), new PlatformComponent[0]),  MEMORY_MANAGER("java.lang.management.MemoryManagerMXBean", "java.lang", "MemoryManager", keyProperties(new String[] { "name" }), false, new MXBeanFetcher(), new PlatformComponent[] { GARBAGE_COLLECTOR }),  MEMORY_POOL("java.lang.management.MemoryPoolMXBean", "java.lang", "MemoryPool", keyProperties(new String[] { "name" }), false, new MXBeanFetcher(), new PlatformComponent[0]),  OPERATING_SYSTEM("java.lang.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  RUNTIME("java.lang.management.RuntimeMXBean", "java.lang", "Runtime", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  THREADING("java.lang.management.ThreadMXBean", "java.lang", "Threading", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  LOGGING("java.lang.management.PlatformLoggingMXBean", "java.util.logging", "Logging", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  BUFFER_POOL("java.lang.management.BufferPoolMXBean", "java.nio", "BufferPool", keyProperties(new String[] { "name" }), false, new MXBeanFetcher(), new PlatformComponent[0]),  SUN_GARBAGE_COLLECTOR("com.sun.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", keyProperties(new String[] { "name" }), false, new MXBeanFetcher(), new PlatformComponent[0]),  SUN_OPERATING_SYSTEM("com.sun.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  SUN_UNIX_OPERATING_SYSTEM("com.sun.management.UnixOperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]),  HOTSPOT_DIAGNOSTIC("com.sun.management.HotSpotDiagnosticMXBean", "com.sun.management", "HotSpotDiagnostic", defaultKeyProperties(), true, new MXBeanFetcher(), new PlatformComponent[0]);
  
  private final String mxbeanInterfaceName;
  private final String domain;
  private final String type;
  private final Set<String> keyProperties;
  private final MXBeanFetcher<?> fetcher;
  private final PlatformComponent[] subComponents;
  private final boolean singleton;
  private static Set<String> defaultKeyProps;
  private static Map<String, PlatformComponent> enumMap;
  private static final long serialVersionUID = 6992337162326171013L;
  
  private static <T extends GarbageCollectorMXBean> List<T> getGcMXBeanList(Class<T> paramClass)
  {
    List localList = ManagementFactoryHelper.getGarbageCollectorMXBeans();
    ArrayList localArrayList = new ArrayList(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      GarbageCollectorMXBean localGarbageCollectorMXBean = (GarbageCollectorMXBean)localIterator.next();
      if (paramClass.isInstance(localGarbageCollectorMXBean)) {
        localArrayList.add(paramClass.cast(localGarbageCollectorMXBean));
      }
    }
    return localArrayList;
  }
  
  private static <T extends OperatingSystemMXBean> List<T> getOSMXBeanList(Class<T> paramClass)
  {
    OperatingSystemMXBean localOperatingSystemMXBean = ManagementFactoryHelper.getOperatingSystemMXBean();
    if (paramClass.isInstance(localOperatingSystemMXBean)) {
      return Collections.singletonList(paramClass.cast(localOperatingSystemMXBean));
    }
    return Collections.emptyList();
  }
  
  private PlatformComponent(String paramString1, String paramString2, String paramString3, Set<String> paramSet, boolean paramBoolean, MXBeanFetcher<?> paramMXBeanFetcher, PlatformComponent... paramVarArgs)
  {
    this.mxbeanInterfaceName = paramString1;
    this.domain = paramString2;
    this.type = paramString3;
    this.keyProperties = paramSet;
    this.singleton = paramBoolean;
    this.fetcher = paramMXBeanFetcher;
    this.subComponents = paramVarArgs;
  }
  
  private static Set<String> defaultKeyProperties()
  {
    if (defaultKeyProps == null) {
      defaultKeyProps = Collections.singleton("type");
    }
    return defaultKeyProps;
  }
  
  private static Set<String> keyProperties(String... paramVarArgs)
  {
    HashSet localHashSet = new HashSet();
    localHashSet.add("type");
    for (String str : paramVarArgs) {
      localHashSet.add(str);
    }
    return localHashSet;
  }
  
  boolean isSingleton()
  {
    return this.singleton;
  }
  
  String getMXBeanInterfaceName()
  {
    return this.mxbeanInterfaceName;
  }
  
  Class<? extends PlatformManagedObject> getMXBeanInterface()
  {
    try
    {
      return Class.forName(this.mxbeanInterfaceName, false, PlatformManagedObject.class.getClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new AssertionError(localClassNotFoundException);
    }
  }
  
  <T extends PlatformManagedObject> List<T> getMXBeans(Class<T> paramClass)
  {
    return this.fetcher.getMXBeans();
  }
  
  <T extends PlatformManagedObject> T getSingletonMXBean(Class<T> paramClass)
  {
    if (!this.singleton) {
      throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances");
    }
    List localList = getMXBeans(paramClass);
    assert (localList.size() == 1);
    return localList.isEmpty() ? null : (PlatformManagedObject)localList.get(0);
  }
  
  <T extends PlatformManagedObject> T getSingletonMXBean(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass)
    throws IOException
  {
    if (!this.singleton) {
      throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances");
    }
    assert (this.keyProperties.size() == 1);
    String str = this.domain + ":type=" + this.type;
    return (PlatformManagedObject)ManagementFactory.newPlatformMXBeanProxy(paramMBeanServerConnection, str, paramClass);
  }
  
  <T extends PlatformManagedObject> List<T> getMXBeans(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = getObjectNames(paramMBeanServerConnection).iterator();
    while (localIterator.hasNext())
    {
      ObjectName localObjectName = (ObjectName)localIterator.next();
      localArrayList.add(ManagementFactory.newPlatformMXBeanProxy(paramMBeanServerConnection, localObjectName.getCanonicalName(), paramClass));
    }
    return localArrayList;
  }
  
  private Set<ObjectName> getObjectNames(MBeanServerConnection paramMBeanServerConnection)
    throws IOException
  {
    String str = this.domain + ":type=" + this.type;
    if (this.keyProperties.size() > 1) {
      str = str + ",*";
    }
    ObjectName localObjectName = Util.newObjectName(str);
    Set localSet = paramMBeanServerConnection.queryNames(localObjectName, null);
    for (PlatformComponent localPlatformComponent : this.subComponents) {
      localSet.addAll(localPlatformComponent.getObjectNames(paramMBeanServerConnection));
    }
    return localSet;
  }
  
  private static synchronized void ensureInitialized()
  {
    if (enumMap == null)
    {
      enumMap = new HashMap();
      for (PlatformComponent localPlatformComponent : values()) {
        enumMap.put(localPlatformComponent.getMXBeanInterfaceName(), localPlatformComponent);
      }
    }
  }
  
  static boolean isPlatformMXBean(String paramString)
  {
    ensureInitialized();
    return enumMap.containsKey(paramString);
  }
  
  static <T extends PlatformManagedObject> PlatformComponent getPlatformComponent(Class<T> paramClass)
  {
    ensureInitialized();
    String str = paramClass.getName();
    PlatformComponent localPlatformComponent = (PlatformComponent)enumMap.get(str);
    if ((localPlatformComponent != null) && (localPlatformComponent.getMXBeanInterface() == paramClass)) {
      return localPlatformComponent;
    }
    return null;
  }
  
  static abstract interface MXBeanFetcher<T extends PlatformManagedObject>
  {
    public abstract List<T> getMXBeans();
  }
}
