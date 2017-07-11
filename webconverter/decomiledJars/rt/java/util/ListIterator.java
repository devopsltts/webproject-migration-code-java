package java.util;

public abstract interface ListIterator<E>
  extends Iterator<E>
{
  public abstract boolean hasNext();
  
  public abstract E next();
  
  public abstract boolean hasPrevious();
  
  public abstract E previous();
  
  public abstract int nextIndex();
  
  public abstract int previousIndex();
  
  public abstract void remove();
  
  public abstract void set(E paramE);
  
  public abstract void add(E paramE);
}
