package java.util;

public abstract interface Queue<E>
  extends Collection<E>
{
  public abstract boolean add(E paramE);
  
  public abstract boolean offer(E paramE);
  
  public abstract E remove();
  
  public abstract E poll();
  
  public abstract E element();
  
  public abstract E peek();
}
