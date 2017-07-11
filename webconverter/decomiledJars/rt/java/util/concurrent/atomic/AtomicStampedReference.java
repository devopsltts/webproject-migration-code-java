package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicStampedReference<V>
{
  private volatile Pair<V> pair;
  private static final Unsafe UNSAFE = ;
  private static final long pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicStampedReference.class);
  
  public AtomicStampedReference(V paramV, int paramInt)
  {
    this.pair = Pair.of(paramV, paramInt);
  }
  
  public V getReference()
  {
    return this.pair.reference;
  }
  
  public int getStamp()
  {
    return this.pair.stamp;
  }
  
  public V get(int[] paramArrayOfInt)
  {
    Pair localPair = this.pair;
    paramArrayOfInt[0] = localPair.stamp;
    return localPair.reference;
  }
  
  public boolean weakCompareAndSet(V paramV1, V paramV2, int paramInt1, int paramInt2)
  {
    return compareAndSet(paramV1, paramV2, paramInt1, paramInt2);
  }
  
  public boolean compareAndSet(V paramV1, V paramV2, int paramInt1, int paramInt2)
  {
    Pair localPair = this.pair;
    return (paramV1 == localPair.reference) && (paramInt1 == localPair.stamp) && (((paramV2 == localPair.reference) && (paramInt2 == localPair.stamp)) || (casPair(localPair, Pair.of(paramV2, paramInt2))));
  }
  
  public void set(V paramV, int paramInt)
  {
    Pair localPair = this.pair;
    if ((paramV != localPair.reference) || (paramInt != localPair.stamp)) {
      this.pair = Pair.of(paramV, paramInt);
    }
  }
  
  public boolean attemptStamp(V paramV, int paramInt)
  {
    Pair localPair = this.pair;
    return (paramV == localPair.reference) && ((paramInt == localPair.stamp) || (casPair(localPair, Pair.of(paramV, paramInt))));
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
    final int stamp;
    
    private Pair(T paramT, int paramInt)
    {
      this.reference = paramT;
      this.stamp = paramInt;
    }
    
    static <T> Pair<T> of(T paramT, int paramInt)
    {
      return new Pair(paramT, paramInt);
    }
  }
}
