package sun.security.util;

class NullCache<K, V>
  extends Cache<K, V>
{
  static final Cache<Object, Object> INSTANCE = new NullCache();
  
  private NullCache() {}
  
  public int size()
  {
    return 0;
  }
  
  public void clear() {}
  
  public void put(K paramK, V paramV) {}
  
  public V get(Object paramObject)
  {
    return null;
  }
  
  public void remove(Object paramObject) {}
  
  public void setCapacity(int paramInt) {}
  
  public void setTimeout(int paramInt) {}
  
  public void accept(Cache.CacheVisitor<K, V> paramCacheVisitor) {}
}
