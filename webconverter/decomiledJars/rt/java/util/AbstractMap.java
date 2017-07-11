package java.util;

import java.io.Serializable;

public abstract class AbstractMap<K, V>
  implements Map<K, V>
{
  volatile transient Set<K> keySet;
  volatile transient Collection<V> values;
  
  protected AbstractMap() {}
  
  public int size()
  {
    return entrySet().size();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean containsValue(Object paramObject)
  {
    Iterator localIterator = entrySet().iterator();
    Map.Entry localEntry;
    if (paramObject == null) {
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        if (localEntry.getValue() == null) {
          return true;
        }
      }
    }
    while (localIterator.hasNext())
    {
      localEntry = (Map.Entry)localIterator.next();
      if (paramObject.equals(localEntry.getValue())) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsKey(Object paramObject)
  {
    Iterator localIterator = entrySet().iterator();
    Map.Entry localEntry;
    if (paramObject == null) {
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        if (localEntry.getKey() == null) {
          return true;
        }
      }
    }
    while (localIterator.hasNext())
    {
      localEntry = (Map.Entry)localIterator.next();
      if (paramObject.equals(localEntry.getKey())) {
        return true;
      }
    }
    return false;
  }
  
  public V get(Object paramObject)
  {
    Iterator localIterator = entrySet().iterator();
    Map.Entry localEntry;
    if (paramObject == null) {
      while (localIterator.hasNext())
      {
        localEntry = (Map.Entry)localIterator.next();
        if (localEntry.getKey() == null) {
          return localEntry.getValue();
        }
      }
    }
    while (localIterator.hasNext())
    {
      localEntry = (Map.Entry)localIterator.next();
      if (paramObject.equals(localEntry.getKey())) {
        return localEntry.getValue();
      }
    }
    return null;
  }
  
  public V put(K paramK, V paramV)
  {
    throw new UnsupportedOperationException();
  }
  
  public V remove(Object paramObject)
  {
    Iterator localIterator = entrySet().iterator();
    Object localObject1 = null;
    if (paramObject == null) {
      while ((localObject1 == null) && (localIterator.hasNext()))
      {
        localObject2 = (Map.Entry)localIterator.next();
        if (((Map.Entry)localObject2).getKey() == null) {
          localObject1 = localObject2;
        }
      }
    }
    while ((localObject1 == null) && (localIterator.hasNext()))
    {
      localObject2 = (Map.Entry)localIterator.next();
      if (paramObject.equals(((Map.Entry)localObject2).getKey())) {
        localObject1 = localObject2;
      }
    }
    Object localObject2 = null;
    if (localObject1 != null)
    {
      localObject2 = localObject1.getValue();
      localIterator.remove();
    }
    return localObject2;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public void clear()
  {
    entrySet().clear();
  }
  
  public Set<K> keySet()
  {
    if (this.keySet == null) {
      this.keySet = new AbstractSet()
      {
        public Iterator<K> iterator()
        {
          new Iterator()
          {
            private Iterator<Map.Entry<K, V>> i = AbstractMap.this.entrySet().iterator();
            
            public boolean hasNext()
            {
              return this.i.hasNext();
            }
            
            public K next()
            {
              return ((Map.Entry)this.i.next()).getKey();
            }
            
            public void remove()
            {
              this.i.remove();
            }
          };
        }
        
        public int size()
        {
          return AbstractMap.this.size();
        }
        
        public boolean isEmpty()
        {
          return AbstractMap.this.isEmpty();
        }
        
        public void clear()
        {
          AbstractMap.this.clear();
        }
        
        public boolean contains(Object paramAnonymousObject)
        {
          return AbstractMap.this.containsKey(paramAnonymousObject);
        }
      };
    }
    return this.keySet;
  }
  
  public Collection<V> values()
  {
    if (this.values == null) {
      this.values = new AbstractCollection()
      {
        public Iterator<V> iterator()
        {
          new Iterator()
          {
            private Iterator<Map.Entry<K, V>> i = AbstractMap.this.entrySet().iterator();
            
            public boolean hasNext()
            {
              return this.i.hasNext();
            }
            
            public V next()
            {
              return ((Map.Entry)this.i.next()).getValue();
            }
            
            public void remove()
            {
              this.i.remove();
            }
          };
        }
        
        public int size()
        {
          return AbstractMap.this.size();
        }
        
        public boolean isEmpty()
        {
          return AbstractMap.this.isEmpty();
        }
        
        public void clear()
        {
          AbstractMap.this.clear();
        }
        
        public boolean contains(Object paramAnonymousObject)
        {
          return AbstractMap.this.containsValue(paramAnonymousObject);
        }
      };
    }
    return this.values;
  }
  
  public abstract Set<Map.Entry<K, V>> entrySet();
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Map)) {
      return false;
    }
    Map localMap = (Map)paramObject;
    if (localMap.size() != size()) {
      return false;
    }
    try
    {
      Iterator localIterator = entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        if (localObject2 == null)
        {
          if ((localMap.get(localObject1) != null) || (!localMap.containsKey(localObject1))) {
            return false;
          }
        }
        else if (!localObject2.equals(localMap.get(localObject1))) {
          return false;
        }
      }
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException)
    {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext()) {
      i += ((Map.Entry)localIterator.next()).hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    Iterator localIterator = entrySet().iterator();
    if (!localIterator.hasNext()) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    for (;;)
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject1 = localEntry.getKey();
      Object localObject2 = localEntry.getValue();
      localStringBuilder.append(localObject1 == this ? "(this Map)" : localObject1);
      localStringBuilder.append('=');
      localStringBuilder.append(localObject2 == this ? "(this Map)" : localObject2);
      if (!localIterator.hasNext()) {
        return '}';
      }
      localStringBuilder.append(',').append(' ');
    }
  }
  
  protected Object clone()
    throws CloneNotSupportedException
  {
    AbstractMap localAbstractMap = (AbstractMap)super.clone();
    localAbstractMap.keySet = null;
    localAbstractMap.values = null;
    return localAbstractMap;
  }
  
  private static boolean eq(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
  
  public static class SimpleEntry<K, V>
    implements Map.Entry<K, V>, Serializable
  {
    private static final long serialVersionUID = -8499721149061103585L;
    private final K key;
    private V value;
    
    public SimpleEntry(K paramK, V paramV)
    {
      this.key = paramK;
      this.value = paramV;
    }
    
    public SimpleEntry(Map.Entry<? extends K, ? extends V> paramEntry)
    {
      this.key = paramEntry.getKey();
      this.value = paramEntry.getValue();
    }
    
    public K getKey()
    {
      return this.key;
    }
    
    public V getValue()
    {
      return this.value;
    }
    
    public V setValue(V paramV)
    {
      Object localObject = this.value;
      this.value = paramV;
      return localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return (AbstractMap.eq(this.key, localEntry.getKey())) && (AbstractMap.eq(this.value, localEntry.getValue()));
    }
    
    public int hashCode()
    {
      return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
    }
    
    public String toString()
    {
      return this.key + "=" + this.value;
    }
  }
  
  public static class SimpleImmutableEntry<K, V>
    implements Map.Entry<K, V>, Serializable
  {
    private static final long serialVersionUID = 7138329143949025153L;
    private final K key;
    private final V value;
    
    public SimpleImmutableEntry(K paramK, V paramV)
    {
      this.key = paramK;
      this.value = paramV;
    }
    
    public SimpleImmutableEntry(Map.Entry<? extends K, ? extends V> paramEntry)
    {
      this.key = paramEntry.getKey();
      this.value = paramEntry.getValue();
    }
    
    public K getKey()
    {
      return this.key;
    }
    
    public V getValue()
    {
      return this.value;
    }
    
    public V setValue(V paramV)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return (AbstractMap.eq(this.key, localEntry.getKey())) && (AbstractMap.eq(this.value, localEntry.getValue()));
    }
    
    public int hashCode()
    {
      return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
    }
    
    public String toString()
    {
      return this.key + "=" + this.value;
    }
  }
}
