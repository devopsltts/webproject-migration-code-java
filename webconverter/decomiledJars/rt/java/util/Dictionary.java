package java.util;

public abstract class Dictionary<K, V>
{
  public Dictionary() {}
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract Enumeration<K> keys();
  
  public abstract Enumeration<V> elements();
  
  public abstract V get(Object paramObject);
  
  public abstract V put(K paramK, V paramV);
  
  public abstract V remove(Object paramObject);
}
