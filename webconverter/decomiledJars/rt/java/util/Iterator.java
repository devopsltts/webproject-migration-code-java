package java.util;

import java.util.function.Consumer;

public abstract interface Iterator<E>
{
  public abstract boolean hasNext();
  
  public abstract E next();
  
  public void remove()
  {
    throw new UnsupportedOperationException("remove");
  }
  
  public void forEachRemaining(Consumer<? super E> paramConsumer)
  {
    Objects.requireNonNull(paramConsumer);
    while (hasNext()) {
      paramConsumer.accept(next());
    }
  }
}
