package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class BasePropertySet
  implements PropertySet
{
  private Map<String, Object> mapView;
  
  protected BasePropertySet() {}
  
  protected abstract PropertyMap getPropertyMap();
  
  protected static PropertyMap parse(Class paramClass)
  {
    (PropertyMap)AccessController.doPrivileged(new PrivilegedAction()
    {
      public BasePropertySet.PropertyMap run()
      {
        BasePropertySet.PropertyMap localPropertyMap = new BasePropertySet.PropertyMap();
        for (Class localClass = this.val$clazz; localClass != null; localClass = localClass.getSuperclass())
        {
          Field localField;
          PropertySet.Property localProperty;
          for (localField : localClass.getDeclaredFields())
          {
            localProperty = (PropertySet.Property)localField.getAnnotation(PropertySet.Property.class);
            if (localProperty != null) {
              for (String str2 : localProperty.value()) {
                localPropertyMap.put(str2, new BasePropertySet.FieldAccessor(localField, str2));
              }
            }
          }
          for (localField : localClass.getDeclaredMethods())
          {
            localProperty = (PropertySet.Property)localField.getAnnotation(PropertySet.Property.class);
            if (localProperty != null)
            {
              ??? = localField.getName();
              assert ((((String)???).startsWith("get")) || (((String)???).startsWith("is")));
              String str1 = 's' + ((String)???).substring(1);
              Method localMethod;
              try
              {
                localMethod = this.val$clazz.getMethod(str1, new Class[] { localField.getReturnType() });
              }
              catch (NoSuchMethodException localNoSuchMethodException)
              {
                localMethod = null;
              }
              for (String str3 : localProperty.value()) {
                localPropertyMap.put(str3, new BasePropertySet.MethodAccessor(localField, localMethod, str3));
              }
            }
          }
        }
        return localPropertyMap;
      }
    });
  }
  
  public boolean containsKey(Object paramObject)
  {
    Accessor localAccessor = (Accessor)getPropertyMap().get(paramObject);
    if (localAccessor != null) {
      return localAccessor.get(this) != null;
    }
    return false;
  }
  
  public Object get(Object paramObject)
  {
    Accessor localAccessor = (Accessor)getPropertyMap().get(paramObject);
    if (localAccessor != null) {
      return localAccessor.get(this);
    }
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  public Object put(String paramString, Object paramObject)
  {
    Accessor localAccessor = (Accessor)getPropertyMap().get(paramString);
    if (localAccessor != null)
    {
      Object localObject = localAccessor.get(this);
      localAccessor.set(this, paramObject);
      return localObject;
    }
    throw new IllegalArgumentException("Undefined property " + paramString);
  }
  
  public boolean supports(Object paramObject)
  {
    return getPropertyMap().containsKey(paramObject);
  }
  
  public Object remove(Object paramObject)
  {
    Accessor localAccessor = (Accessor)getPropertyMap().get(paramObject);
    if (localAccessor != null)
    {
      Object localObject = localAccessor.get(this);
      localAccessor.set(this, null);
      return localObject;
    }
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  @Deprecated
  public final Map<String, Object> createMapView()
  {
    final HashSet localHashSet = new HashSet();
    createEntrySet(localHashSet);
    new AbstractMap()
    {
      public Set<Map.Entry<String, Object>> entrySet()
      {
        return localHashSet;
      }
    };
  }
  
  public Map<String, Object> asMap()
  {
    if (this.mapView == null) {
      this.mapView = createView();
    }
    return this.mapView;
  }
  
  protected Map<String, Object> createView()
  {
    return new MapView(mapAllowsAdditionalProperties());
  }
  
  protected boolean mapAllowsAdditionalProperties()
  {
    return false;
  }
  
  protected void createEntrySet(Set<Map.Entry<String, Object>> paramSet)
  {
    Iterator localIterator = getPropertyMap().entrySet().iterator();
    while (localIterator.hasNext())
    {
      final Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramSet.add(new Map.Entry()
      {
        public String getKey()
        {
          return (String)localEntry.getKey();
        }
        
        public Object getValue()
        {
          return ((BasePropertySet.Accessor)localEntry.getValue()).get(BasePropertySet.this);
        }
        
        public Object setValue(Object paramAnonymousObject)
        {
          BasePropertySet.Accessor localAccessor = (BasePropertySet.Accessor)localEntry.getValue();
          Object localObject = localAccessor.get(BasePropertySet.this);
          localAccessor.set(BasePropertySet.this, paramAnonymousObject);
          return localObject;
        }
      });
    }
  }
  
  protected static abstract interface Accessor
  {
    public abstract String getName();
    
    public abstract boolean hasValue(PropertySet paramPropertySet);
    
    public abstract Object get(PropertySet paramPropertySet);
    
    public abstract void set(PropertySet paramPropertySet, Object paramObject);
  }
  
  static final class FieldAccessor
    implements BasePropertySet.Accessor
  {
    private final Field f;
    private final String name;
    
    protected FieldAccessor(Field paramField, String paramString)
    {
      this.f = paramField;
      paramField.setAccessible(true);
      this.name = paramString;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public boolean hasValue(PropertySet paramPropertySet)
    {
      return get(paramPropertySet) != null;
    }
    
    public Object get(PropertySet paramPropertySet)
    {
      try
      {
        return this.f.get(paramPropertySet);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError();
      }
    }
    
    public void set(PropertySet paramPropertySet, Object paramObject)
    {
      try
      {
        this.f.set(paramPropertySet, paramObject);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError();
      }
    }
  }
  
  final class MapView
    extends HashMap<String, Object>
  {
    boolean extensible;
    
    MapView(boolean paramBoolean)
    {
      super();
      this.extensible = paramBoolean;
      initialize();
    }
    
    public void initialize()
    {
      BasePropertySet.PropertyMapEntry[] arrayOfPropertyMapEntry1 = BasePropertySet.this.getPropertyMap().getPropertyMapEntries();
      for (BasePropertySet.PropertyMapEntry localPropertyMapEntry : arrayOfPropertyMapEntry1) {
        super.put(localPropertyMapEntry.key, localPropertyMapEntry.value);
      }
    }
    
    public Object get(Object paramObject)
    {
      Object localObject = super.get(paramObject);
      if ((localObject instanceof BasePropertySet.Accessor)) {
        return ((BasePropertySet.Accessor)localObject).get(BasePropertySet.this);
      }
      return localObject;
    }
    
    public Set<Map.Entry<String, Object>> entrySet()
    {
      HashSet localHashSet = new HashSet();
      Iterator localIterator = keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localHashSet.add(new AbstractMap.SimpleImmutableEntry(str, get(str)));
      }
      return localHashSet;
    }
    
    public Object put(String paramString, Object paramObject)
    {
      Object localObject1 = super.get(paramString);
      if ((localObject1 != null) && ((localObject1 instanceof BasePropertySet.Accessor)))
      {
        Object localObject2 = ((BasePropertySet.Accessor)localObject1).get(BasePropertySet.this);
        ((BasePropertySet.Accessor)localObject1).set(BasePropertySet.this, paramObject);
        return localObject2;
      }
      if (this.extensible) {
        return super.put(paramString, paramObject);
      }
      throw new IllegalStateException("Unknown property [" + paramString + "] for PropertySet [" + BasePropertySet.this.getClass().getName() + "]");
    }
    
    public void clear()
    {
      Iterator localIterator = keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        remove(str);
      }
    }
    
    public Object remove(Object paramObject)
    {
      Object localObject = super.get(paramObject);
      if ((localObject instanceof BasePropertySet.Accessor)) {
        ((BasePropertySet.Accessor)localObject).set(BasePropertySet.this, null);
      }
      return super.remove(paramObject);
    }
  }
  
  static final class MethodAccessor
    implements BasePropertySet.Accessor
  {
    @NotNull
    private final Method getter;
    @Nullable
    private final Method setter;
    private final String name;
    
    protected MethodAccessor(Method paramMethod1, Method paramMethod2, String paramString)
    {
      this.getter = paramMethod1;
      this.setter = paramMethod2;
      this.name = paramString;
      paramMethod1.setAccessible(true);
      if (paramMethod2 != null) {
        paramMethod2.setAccessible(true);
      }
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public boolean hasValue(PropertySet paramPropertySet)
    {
      return get(paramPropertySet) != null;
    }
    
    public Object get(PropertySet paramPropertySet)
    {
      try
      {
        return this.getter.invoke(paramPropertySet, new Object[0]);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        handle(localInvocationTargetException);
      }
      return Integer.valueOf(0);
    }
    
    public void set(PropertySet paramPropertySet, Object paramObject)
    {
      if (this.setter == null) {
        throw new ReadOnlyPropertyException(getName());
      }
      try
      {
        this.setter.invoke(paramPropertySet, new Object[] { paramObject });
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        handle(localInvocationTargetException);
      }
    }
    
    private Exception handle(InvocationTargetException paramInvocationTargetException)
    {
      Throwable localThrowable = paramInvocationTargetException.getTargetException();
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      throw new Error(paramInvocationTargetException);
    }
  }
  
  protected static class PropertyMap
    extends HashMap<String, BasePropertySet.Accessor>
  {
    transient BasePropertySet.PropertyMapEntry[] cachedEntries = null;
    
    protected PropertyMap() {}
    
    BasePropertySet.PropertyMapEntry[] getPropertyMapEntries()
    {
      if (this.cachedEntries == null) {
        this.cachedEntries = createPropertyMapEntries();
      }
      return this.cachedEntries;
    }
    
    private BasePropertySet.PropertyMapEntry[] createPropertyMapEntries()
    {
      BasePropertySet.PropertyMapEntry[] arrayOfPropertyMapEntry = new BasePropertySet.PropertyMapEntry[size()];
      int i = 0;
      Iterator localIterator = entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        arrayOfPropertyMapEntry[(i++)] = new BasePropertySet.PropertyMapEntry((String)localEntry.getKey(), (BasePropertySet.Accessor)localEntry.getValue());
      }
      return arrayOfPropertyMapEntry;
    }
  }
  
  public static class PropertyMapEntry
  {
    String key;
    BasePropertySet.Accessor value;
    
    public PropertyMapEntry(String paramString, BasePropertySet.Accessor paramAccessor)
    {
      this.key = paramString;
      this.value = paramAccessor;
    }
  }
}
