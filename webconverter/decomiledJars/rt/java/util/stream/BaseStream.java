package java.util.stream;

import java.util.Iterator;
import java.util.Spliterator;

public abstract interface BaseStream<T, S extends BaseStream<T, S>>
  extends AutoCloseable
{
  public abstract Iterator<T> iterator();
  
  public abstract Spliterator<T> spliterator();
  
  public abstract boolean isParallel();
  
  public abstract S sequential();
  
  public abstract S parallel();
  
  public abstract S unordered();
  
  public abstract S onClose(Runnable paramRunnable);
  
  public abstract void close();
}
