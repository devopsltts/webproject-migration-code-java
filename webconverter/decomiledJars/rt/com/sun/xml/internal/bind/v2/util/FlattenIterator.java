package com.sun.xml.internal.bind.v2.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class FlattenIterator<T>
  implements Iterator<T>
{
  private final Iterator<? extends Map<?, ? extends T>> parent;
  private Iterator<? extends T> child = null;
  private T next;
  
  public FlattenIterator(Iterable<? extends Map<?, ? extends T>> paramIterable)
  {
    this.parent = paramIterable.iterator();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasNext()
  {
    getNext();
    return this.next != null;
  }
  
  public T next()
  {
    Object localObject = this.next;
    this.next = null;
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return localObject;
  }
  
  private void getNext()
  {
    if (this.next != null) {
      return;
    }
    if ((this.child != null) && (this.child.hasNext()))
    {
      this.next = this.child.next();
      return;
    }
    if (this.parent.hasNext())
    {
      this.child = ((Map)this.parent.next()).values().iterator();
      getNext();
    }
  }
}
