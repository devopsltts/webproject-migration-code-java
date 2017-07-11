package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.JMX;

public class DescriptorCache
{
  private static final DescriptorCache instance = new DescriptorCache();
  private final WeakHashMap<ImmutableDescriptor, WeakReference<ImmutableDescriptor>> map = new WeakHashMap();
  
  private DescriptorCache() {}
  
  static DescriptorCache getInstance()
  {
    return instance;
  }
  
  public static DescriptorCache getInstance(JMX paramJMX)
  {
    if (paramJMX != null) {
      return instance;
    }
    return null;
  }
  
  public ImmutableDescriptor get(ImmutableDescriptor paramImmutableDescriptor)
  {
    WeakReference localWeakReference = (WeakReference)this.map.get(paramImmutableDescriptor);
    ImmutableDescriptor localImmutableDescriptor = localWeakReference == null ? null : (ImmutableDescriptor)localWeakReference.get();
    if (localImmutableDescriptor != null) {
      return localImmutableDescriptor;
    }
    this.map.put(paramImmutableDescriptor, new WeakReference(paramImmutableDescriptor));
    return paramImmutableDescriptor;
  }
  
  public ImmutableDescriptor union(Descriptor... paramVarArgs)
  {
    return get(ImmutableDescriptor.union(paramVarArgs));
  }
}
