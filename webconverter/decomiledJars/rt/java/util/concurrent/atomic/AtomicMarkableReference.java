package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicMarkableReference<V>
{
  private volatile Pair<V> pair;
  private static final Unsafe UNSAFE = ;
  private static final long pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
  
  public AtomicMarkableReference(V paramV, boolean paramBoolean)
  {
    this.pair = Pair.of(paramV, paramBoolean);
  }
  
  public V getReference()
  {
    return this.pair.reference;
  }
  
  public boolean isMarked()
  {
    return this.pair.mark;
  }
  
  public V get(boolean[] paramArrayOfBoolean)
  {
    Pair localPair = this.pair;
    paramArrayOfBoolean[0] = localPair.mark;
    return localPair.reference;
  }
  
  public boolean weakCompareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2)
  {
    return compareAndSet(paramV1, paramV2, paramBoolean1, paramBoolean2);
  }
  
  public boolean compareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2)
  {
    Pair localPair = this.pair;
    return (paramV1 == localPair.reference) && (paramBoolean1 == localPair.mark) && (((paramV2 == localPair.reference) && (paramBoolean2 == localPair.mark)) || (casPair(localPair, Pair.of(paramV2, paramBoolean2))));
  }
  
  public void set(V paramV, boolean paramBoolean)
  {
    Pair localPair = this.pair;
    if ((paramV != localPair.reference) || (paramBoolean != localPair.mark)) {
      this.pair = Pair.of(paramV, paramBoolean);
    }
  }
  
  public boolean attemptMark(V paramV, boolean paramBoolean)
  {
    Pair localPair = this.pair;
    return (paramV == localPair.reference) && ((paramBoolean == localPair.mark) || (casPair(localPair, Pair.of(paramV, paramBoolean))));
  }
  
  private boolean casPair(Pair<V> paramPair1, Pair<V> paramPair2)
  {
    return UNSAFE.compareAndSwapObject(this, pairOffset, paramPair1, paramPair2);
  }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass)
  {
    try
    {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      NoSuchFieldError localNoSuchFieldError = new NoSuchFieldError(paramString);
      localNoSuchFieldError.initCause(localNoSuchFieldException);
      throw localNoSuchFieldError;
    }
  }
  
  private static class Pair<T>
  {
    final T reference;
    final boolean mark;
    
    private Pair(T paramT, boolean paramBoolean)
    {
      this.reference = paramT;
      this.mark = paramBoolean;
    }
    
    static <T> Pair<T> of(T paramT, boolean paramBoolean)
    {
      return new Pair(paramT, paramBoolean);
    }
  }
}
