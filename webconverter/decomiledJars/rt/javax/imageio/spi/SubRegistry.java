package javax.imageio.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class SubRegistry
{
  ServiceRegistry registry;
  Class category;
  PartiallyOrderedSet poset = new PartiallyOrderedSet();
  Map<Class<?>, Object> map = new HashMap();
  
  public SubRegistry(ServiceRegistry paramServiceRegistry, Class paramClass)
  {
    this.registry = paramServiceRegistry;
    this.category = paramClass;
  }
  
  public boolean registerServiceProvider(Object paramObject)
  {
    Object localObject = this.map.get(paramObject.getClass());
    int i = localObject != null ? 1 : 0;
    if (i != 0) {
      deregisterServiceProvider(localObject);
    }
    this.map.put(paramObject.getClass(), paramObject);
    this.poset.add(paramObject);
    if ((paramObject instanceof RegisterableService))
    {
      RegisterableService localRegisterableService = (RegisterableService)paramObject;
      localRegisterableService.onRegistration(this.registry, this.category);
    }
    return i == 0;
  }
  
  public boolean deregisterServiceProvider(Object paramObject)
  {
    Object localObject = this.map.get(paramObject.getClass());
    if (paramObject == localObject)
    {
      this.map.remove(paramObject.getClass());
      this.poset.remove(paramObject);
      if ((paramObject instanceof RegisterableService))
      {
        RegisterableService localRegisterableService = (RegisterableService)paramObject;
        localRegisterableService.onDeregistration(this.registry, this.category);
      }
      return true;
    }
    return false;
  }
  
  public boolean contains(Object paramObject)
  {
    Object localObject = this.map.get(paramObject.getClass());
    return localObject == paramObject;
  }
  
  public boolean setOrdering(Object paramObject1, Object paramObject2)
  {
    return this.poset.setOrdering(paramObject1, paramObject2);
  }
  
  public boolean unsetOrdering(Object paramObject1, Object paramObject2)
  {
    return this.poset.unsetOrdering(paramObject1, paramObject2);
  }
  
  public Iterator getServiceProviders(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.poset.iterator();
    }
    return this.map.values().iterator();
  }
  
  public <T> T getServiceProviderByClass(Class<T> paramClass)
  {
    return this.map.get(paramClass);
  }
  
  public void clear()
  {
    Iterator localIterator = this.map.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localIterator.remove();
      if ((localObject instanceof RegisterableService))
      {
        RegisterableService localRegisterableService = (RegisterableService)localObject;
        localRegisterableService.onDeregistration(this.registry, this.category);
      }
    }
    this.poset.clear();
  }
  
  public void finalize()
  {
    clear();
  }
}
