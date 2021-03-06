package java.util;

public abstract interface Set<E>
  extends Collection<E>
{
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract boolean contains(Object paramObject);
  
  public abstract Iterator<E> iterator();
  
  public abstract Object[] toArray();
  
  public abstract <T> T[] toArray(T[] paramArrayOfT);
  
  public abstract boolean add(E paramE);
  
  public abstract boolean remove(Object paramObject);
  
  public abstract boolean containsAll(Collection<?> paramCollection);
  
  public abstract boolean addAll(Collection<? extends E> paramCollection);
  
  public abstract boolean retainAll(Collection<?> paramCollection);
  
  public abstract boolean removeAll(Collection<?> paramCollection);
  
  public abstract void clear();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public Spliterator<E> spliterator()
  {
    return Spliterators.spliterator(this, 1);
  }
}
