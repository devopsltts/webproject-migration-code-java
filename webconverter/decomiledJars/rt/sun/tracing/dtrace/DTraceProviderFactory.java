package sun.tracing.dtrace;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class DTraceProviderFactory
  extends ProviderFactory
{
  public DTraceProviderFactory() {}
  
  public <T extends Provider> T createProvider(Class<T> paramClass)
  {
    DTraceProvider localDTraceProvider = new DTraceProvider(paramClass);
    Provider localProvider = localDTraceProvider.newProxyInstance();
    localDTraceProvider.setProxy(localProvider);
    localDTraceProvider.init();
    new Activation(localDTraceProvider.getModuleName(), new DTraceProvider[] { localDTraceProvider });
    return localProvider;
  }
  
  public Map<Class<? extends Provider>, Provider> createProviders(Set<Class<? extends Provider>> paramSet, String paramString)
  {
    HashMap localHashMap = new HashMap();
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      DTraceProvider localDTraceProvider = new DTraceProvider(localClass);
      localHashSet.add(localDTraceProvider);
      localHashMap.put(localClass, localDTraceProvider.newProxyInstance());
    }
    new Activation(paramString, (DTraceProvider[])localHashSet.toArray(new DTraceProvider[0]));
    return localHashMap;
  }
  
  public static boolean isSupported()
  {
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        RuntimePermission localRuntimePermission = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
        localSecurityManager.checkPermission(localRuntimePermission);
      }
      return JVM.isSupported();
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
}
