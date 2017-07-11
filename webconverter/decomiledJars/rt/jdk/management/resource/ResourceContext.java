package jdk.management.resource;

import java.util.stream.Stream;
import jdk.management.resource.internal.SimpleResourceContext;

public abstract interface ResourceContext
  extends AutoCloseable
{
  public abstract void close();
  
  public abstract String getName();
  
  public ResourceContext bindThreadContext()
  {
    throw new UnsupportedOperationException("bind not supported by " + getName());
  }
  
  public static ResourceContext unbindThreadContext()
  {
    return SimpleResourceContext.unbindThreadContext();
  }
  
  public Stream<Thread> boundThreads()
  {
    throw new UnsupportedOperationException("boundThreads not supported by " + getName());
  }
  
  public abstract ResourceRequest getResourceRequest(ResourceType paramResourceType);
  
  public void addResourceMeter(ResourceMeter paramResourceMeter)
  {
    throw new UnsupportedOperationException("addResourceMeter not supported by " + getName());
  }
  
  public boolean removeResourceMeter(ResourceMeter paramResourceMeter)
  {
    throw new UnsupportedOperationException("removeResourceMeter not supported by " + getName());
  }
  
  public abstract ResourceMeter getMeter(ResourceType paramResourceType);
  
  public abstract Stream<ResourceMeter> meters();
  
  public void requestAccurateUpdate(ResourceAccuracy paramResourceAccuracy)
  {
    throw new UnsupportedOperationException("requestAccurateUpdate not supported by " + getName());
  }
}
