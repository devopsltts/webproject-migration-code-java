package java.util;

public abstract class AbstractQueue<E>
  extends AbstractCollection<E>
  implements Queue<E>
{
  protected AbstractQueue() {}
  
  public boolean add(E paramE)
  {
    if (offer(paramE)) {
      return true;
    }
    throw new IllegalStateException("Queue full");
  }
  
  public E remove()
  {
    Object localObject = poll();
    if (localObject != null) {
      return localObject;
    }
    throw new NoSuchElementException();
  }
  
  public E element()
  {
    Object localObject = peek();
    if (localObject != null) {
      return localObject;
    }
    throw new NoSuchElementException();
  }
  
  public void clear()
  {
    while (poll() != null) {}
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    boolean bool = false;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (add(localObject)) {
        bool = true;
      }
    }
    return bool;
  }
}
