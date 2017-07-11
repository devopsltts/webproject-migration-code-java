package javax.imageio.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;

class FilterIterator<T>
  implements Iterator<T>
{
  private Iterator<T> iter;
  private ServiceRegistry.Filter filter;
  private T next = null;
  
  public FilterIterator(Iterator<T> paramIterator, ServiceRegistry.Filter paramFilter)
  {
    this.iter = paramIterator;
    this.filter = paramFilter;
    advance();
  }
  
  private void advance()
  {
    while (this.iter.hasNext())
    {
      Object localObject = this.iter.next();
      if (this.filter.filter(localObject))
      {
        this.next = localObject;
        return;
      }
    }
    this.next = null;
  }
  
  public boolean hasNext()
  {
    return this.next != null;
  }
  
  public T next()
  {
    if (this.next == null) {
      throw new NoSuchElementException();
    }
    Object localObject = this.next;
    advance();
    return localObject;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
