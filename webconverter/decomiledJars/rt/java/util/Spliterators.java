package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public final class Spliterators
{
  private static final Spliterator<Object> EMPTY_SPLITERATOR = new Spliterators.EmptySpliterator.OfRef();
  private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR = new Spliterators.EmptySpliterator.OfInt();
  private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR = new Spliterators.EmptySpliterator.OfLong();
  private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR = new Spliterators.EmptySpliterator.OfDouble();
  
  private Spliterators() {}
  
  public static <T> Spliterator<T> emptySpliterator()
  {
    return EMPTY_SPLITERATOR;
  }
  
  public static Spliterator.OfInt emptyIntSpliterator()
  {
    return EMPTY_INT_SPLITERATOR;
  }
  
  public static Spliterator.OfLong emptyLongSpliterator()
  {
    return EMPTY_LONG_SPLITERATOR;
  }
  
  public static Spliterator.OfDouble emptyDoubleSpliterator()
  {
    return EMPTY_DOUBLE_SPLITERATOR;
  }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt)
  {
    return new ArraySpliterator((Object[])Objects.requireNonNull(paramArrayOfObject), paramInt);
  }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((Object[])Objects.requireNonNull(paramArrayOfObject)).length, paramInt1, paramInt2);
    return new ArraySpliterator(paramArrayOfObject, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt)
  {
    return new IntArraySpliterator((int[])Objects.requireNonNull(paramArrayOfInt), paramInt);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((int[])Objects.requireNonNull(paramArrayOfInt)).length, paramInt1, paramInt2);
    return new IntArraySpliterator(paramArrayOfInt, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt)
  {
    return new LongArraySpliterator((long[])Objects.requireNonNull(paramArrayOfLong), paramInt);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((long[])Objects.requireNonNull(paramArrayOfLong)).length, paramInt1, paramInt2);
    return new LongArraySpliterator(paramArrayOfLong, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt)
  {
    return new DoubleArraySpliterator((double[])Objects.requireNonNull(paramArrayOfDouble), paramInt);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((double[])Objects.requireNonNull(paramArrayOfDouble)).length, paramInt1, paramInt2);
    return new DoubleArraySpliterator(paramArrayOfDouble, paramInt1, paramInt2, paramInt3);
  }
  
  private static void checkFromToBounds(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 > paramInt3) {
      throw new ArrayIndexOutOfBoundsException("origin(" + paramInt2 + ") > fence(" + paramInt3 + ")");
    }
    if (paramInt2 < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt3 > paramInt1) {
      throw new ArrayIndexOutOfBoundsException(paramInt3);
    }
  }
  
  public static <T> Spliterator<T> spliterator(Collection<? extends T> paramCollection, int paramInt)
  {
    return new IteratorSpliterator((Collection)Objects.requireNonNull(paramCollection), paramInt);
  }
  
  public static <T> Spliterator<T> spliterator(Iterator<? extends T> paramIterator, long paramLong, int paramInt)
  {
    return new IteratorSpliterator((Iterator)Objects.requireNonNull(paramIterator), paramLong, paramInt);
  }
  
  public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> paramIterator, int paramInt)
  {
    return new IteratorSpliterator((Iterator)Objects.requireNonNull(paramIterator), paramInt);
  }
  
  public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt paramOfInt, long paramLong, int paramInt)
  {
    return new IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(paramOfInt), paramLong, paramInt);
  }
  
  public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt paramOfInt, int paramInt)
  {
    return new IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(paramOfInt), paramInt);
  }
  
  public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong paramOfLong, long paramLong, int paramInt)
  {
    return new LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(paramOfLong), paramLong, paramInt);
  }
  
  public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong paramOfLong, int paramInt)
  {
    return new LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(paramOfLong), paramInt);
  }
  
  public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble paramOfDouble, long paramLong, int paramInt)
  {
    return new DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(paramOfDouble), paramLong, paramInt);
  }
  
  public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble paramOfDouble, int paramInt)
  {
    return new DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(paramOfDouble), paramInt);
  }
  
  public static <T> Iterator<T> iterator(Spliterator<? extends T> paramSpliterator)
  {
    Objects.requireNonNull(paramSpliterator);
    new Iterator()
    {
      boolean valueReady = false;
      T nextElement;
      
      public void accept(T paramAnonymousT)
      {
        this.valueReady = true;
        this.nextElement = paramAnonymousT;
      }
      
      public boolean hasNext()
      {
        if (!this.valueReady) {
          this.val$spliterator.tryAdvance(this);
        }
        return this.valueReady;
      }
      
      public T next()
      {
        if ((!this.valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        this.valueReady = false;
        return this.nextElement;
      }
    };
  }
  
  public static PrimitiveIterator.OfInt iterator(Spliterator.OfInt paramOfInt)
  {
    Objects.requireNonNull(paramOfInt);
    new PrimitiveIterator.OfInt()
    {
      boolean valueReady = false;
      int nextElement;
      
      public void accept(int paramAnonymousInt)
      {
        this.valueReady = true;
        this.nextElement = paramAnonymousInt;
      }
      
      public boolean hasNext()
      {
        if (!this.valueReady) {
          this.val$spliterator.tryAdvance(this);
        }
        return this.valueReady;
      }
      
      public int nextInt()
      {
        if ((!this.valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        this.valueReady = false;
        return this.nextElement;
      }
    };
  }
  
  public static PrimitiveIterator.OfLong iterator(Spliterator.OfLong paramOfLong)
  {
    Objects.requireNonNull(paramOfLong);
    new PrimitiveIterator.OfLong()
    {
      boolean valueReady = false;
      long nextElement;
      
      public void accept(long paramAnonymousLong)
      {
        this.valueReady = true;
        this.nextElement = paramAnonymousLong;
      }
      
      public boolean hasNext()
      {
        if (!this.valueReady) {
          this.val$spliterator.tryAdvance(this);
        }
        return this.valueReady;
      }
      
      public long nextLong()
      {
        if ((!this.valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        this.valueReady = false;
        return this.nextElement;
      }
    };
  }
  
  public static PrimitiveIterator.OfDouble iterator(Spliterator.OfDouble paramOfDouble)
  {
    Objects.requireNonNull(paramOfDouble);
    new PrimitiveIterator.OfDouble()
    {
      boolean valueReady = false;
      double nextElement;
      
      public void accept(double paramAnonymousDouble)
      {
        this.valueReady = true;
        this.nextElement = paramAnonymousDouble;
      }
      
      public boolean hasNext()
      {
        if (!this.valueReady) {
          this.val$spliterator.tryAdvance(this);
        }
        return this.valueReady;
      }
      
      public double nextDouble()
      {
        if ((!this.valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        this.valueReady = false;
        return this.nextElement;
      }
    };
  }
  
  public static abstract class AbstractDoubleSpliterator
    implements Spliterator.OfDouble
  {
    static final int MAX_BATCH = 33554432;
    static final int BATCH_UNIT = 1024;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractDoubleSpliterator(long paramLong, int paramInt)
    {
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator.OfDouble trySplit()
    {
      HoldingDoubleConsumer localHoldingDoubleConsumer = new HoldingDoubleConsumer();
      long l = this.est;
      if ((l > 1L) && (tryAdvance(localHoldingDoubleConsumer)))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        double[] arrayOfDouble = new double[i];
        int j = 0;
        do
        {
          arrayOfDouble[j] = localHoldingDoubleConsumer.value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingDoubleConsumer)));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.DoubleArraySpliterator(arrayOfDouble, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    static final class HoldingDoubleConsumer
      implements DoubleConsumer
    {
      double value;
      
      HoldingDoubleConsumer() {}
      
      public void accept(double paramDouble)
      {
        this.value = paramDouble;
      }
    }
  }
  
  public static abstract class AbstractIntSpliterator
    implements Spliterator.OfInt
  {
    static final int MAX_BATCH = 33554432;
    static final int BATCH_UNIT = 1024;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractIntSpliterator(long paramLong, int paramInt)
    {
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator.OfInt trySplit()
    {
      HoldingIntConsumer localHoldingIntConsumer = new HoldingIntConsumer();
      long l = this.est;
      if ((l > 1L) && (tryAdvance(localHoldingIntConsumer)))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        int[] arrayOfInt = new int[i];
        int j = 0;
        do
        {
          arrayOfInt[j] = localHoldingIntConsumer.value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingIntConsumer)));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.IntArraySpliterator(arrayOfInt, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    static final class HoldingIntConsumer
      implements IntConsumer
    {
      int value;
      
      HoldingIntConsumer() {}
      
      public void accept(int paramInt)
      {
        this.value = paramInt;
      }
    }
  }
  
  public static abstract class AbstractLongSpliterator
    implements Spliterator.OfLong
  {
    static final int MAX_BATCH = 33554432;
    static final int BATCH_UNIT = 1024;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractLongSpliterator(long paramLong, int paramInt)
    {
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator.OfLong trySplit()
    {
      HoldingLongConsumer localHoldingLongConsumer = new HoldingLongConsumer();
      long l = this.est;
      if ((l > 1L) && (tryAdvance(localHoldingLongConsumer)))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        long[] arrayOfLong = new long[i];
        int j = 0;
        do
        {
          arrayOfLong[j] = localHoldingLongConsumer.value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingLongConsumer)));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.LongArraySpliterator(arrayOfLong, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    static final class HoldingLongConsumer
      implements LongConsumer
    {
      long value;
      
      HoldingLongConsumer() {}
      
      public void accept(long paramLong)
      {
        this.value = paramLong;
      }
    }
  }
  
  public static abstract class AbstractSpliterator<T>
    implements Spliterator<T>
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractSpliterator(long paramLong, int paramInt)
    {
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator<T> trySplit()
    {
      HoldingConsumer localHoldingConsumer = new HoldingConsumer();
      long l = this.est;
      if ((l > 1L) && (tryAdvance(localHoldingConsumer)))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        Object[] arrayOfObject = new Object[i];
        int j = 0;
        do
        {
          arrayOfObject[j] = localHoldingConsumer.value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingConsumer)));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.ArraySpliterator(arrayOfObject, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    static final class HoldingConsumer<T>
      implements Consumer<T>
    {
      Object value;
      
      HoldingConsumer() {}
      
      public void accept(T paramT)
      {
        this.value = paramT;
      }
    }
  }
  
  static final class ArraySpliterator<T>
    implements Spliterator<T>
  {
    private final Object[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public ArraySpliterator(Object[] paramArrayOfObject, int paramInt)
    {
      this(paramArrayOfObject, 0, paramArrayOfObject.length, paramInt);
    }
    
    public ArraySpliterator(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
    {
      this.array = paramArrayOfObject;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator<T> trySplit()
    {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return i >= j ? null : new ArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject;
      int j;
      int i;
      if (((arrayOfObject = this.array).length >= (j = this.fence)) && ((i = this.index) >= 0) && (i < (this.index = j))) {
        do
        {
          paramConsumer.accept(arrayOfObject[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      if ((this.index >= 0) && (this.index < this.fence))
      {
        Object localObject = this.array[(this.index++)];
        paramConsumer.accept(localObject);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super T> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class DoubleArraySpliterator
    implements Spliterator.OfDouble
  {
    private final double[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public DoubleArraySpliterator(double[] paramArrayOfDouble, int paramInt)
    {
      this(paramArrayOfDouble, 0, paramArrayOfDouble.length, paramInt);
    }
    
    public DoubleArraySpliterator(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
    {
      this.array = paramArrayOfDouble;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator.OfDouble trySplit()
    {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return i >= j ? null : new DoubleArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      double[] arrayOfDouble;
      int j;
      int i;
      if (((arrayOfDouble = this.array).length >= (j = this.fence)) && ((i = this.index) >= 0) && (i < (this.index = j))) {
        do
        {
          paramDoubleConsumer.accept(arrayOfDouble[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      if ((this.index >= 0) && (this.index < this.fence))
      {
        paramDoubleConsumer.accept(this.array[(this.index++)]);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super Double> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class DoubleIteratorSpliterator
    implements Spliterator.OfDouble
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private PrimitiveIterator.OfDouble it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble paramOfDouble, long paramLong, int paramInt)
    {
      this.it = paramOfDouble;
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble paramOfDouble, int paramInt)
    {
      this.it = paramOfDouble;
      this.est = Long.MAX_VALUE;
      this.characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator.OfDouble trySplit()
    {
      PrimitiveIterator.OfDouble localOfDouble = this.it;
      long l = this.est;
      if ((l > 1L) && (localOfDouble.hasNext()))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        double[] arrayOfDouble = new double[i];
        int j = 0;
        do
        {
          arrayOfDouble[j] = localOfDouble.nextDouble();
          j++;
        } while ((j < i) && (localOfDouble.hasNext()));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.DoubleArraySpliterator(arrayOfDouble, 0, j, this.characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      this.it.forEachRemaining(paramDoubleConsumer);
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      if (this.it.hasNext())
      {
        paramDoubleConsumer.accept(this.it.nextDouble());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super Double> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C>
  {
    EmptySpliterator() {}
    
    public S trySplit()
    {
      return null;
    }
    
    public boolean tryAdvance(C paramC)
    {
      Objects.requireNonNull(paramC);
      return false;
    }
    
    public void forEachRemaining(C paramC)
    {
      Objects.requireNonNull(paramC);
    }
    
    public long estimateSize()
    {
      return 0L;
    }
    
    public int characteristics()
    {
      return 16448;
    }
    
    private static final class OfDouble
      extends Spliterators.EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer>
      implements Spliterator.OfDouble
    {
      OfDouble() {}
    }
    
    private static final class OfInt
      extends Spliterators.EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer>
      implements Spliterator.OfInt
    {
      OfInt() {}
    }
    
    private static final class OfLong
      extends Spliterators.EmptySpliterator<Long, Spliterator.OfLong, LongConsumer>
      implements Spliterator.OfLong
    {
      OfLong() {}
    }
    
    private static final class OfRef<T>
      extends Spliterators.EmptySpliterator<T, Spliterator<T>, Consumer<? super T>>
      implements Spliterator<T>
    {
      OfRef() {}
    }
  }
  
  static final class IntArraySpliterator
    implements Spliterator.OfInt
  {
    private final int[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public IntArraySpliterator(int[] paramArrayOfInt, int paramInt)
    {
      this(paramArrayOfInt, 0, paramArrayOfInt.length, paramInt);
    }
    
    public IntArraySpliterator(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
    {
      this.array = paramArrayOfInt;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator.OfInt trySplit()
    {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return i >= j ? null : new IntArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      int[] arrayOfInt;
      int j;
      int i;
      if (((arrayOfInt = this.array).length >= (j = this.fence)) && ((i = this.index) >= 0) && (i < (this.index = j))) {
        do
        {
          paramIntConsumer.accept(arrayOfInt[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      if ((this.index >= 0) && (this.index < this.fence))
      {
        paramIntConsumer.accept(this.array[(this.index++)]);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super Integer> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class IntIteratorSpliterator
    implements Spliterator.OfInt
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private PrimitiveIterator.OfInt it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public IntIteratorSpliterator(PrimitiveIterator.OfInt paramOfInt, long paramLong, int paramInt)
    {
      this.it = paramOfInt;
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public IntIteratorSpliterator(PrimitiveIterator.OfInt paramOfInt, int paramInt)
    {
      this.it = paramOfInt;
      this.est = Long.MAX_VALUE;
      this.characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator.OfInt trySplit()
    {
      PrimitiveIterator.OfInt localOfInt = this.it;
      long l = this.est;
      if ((l > 1L) && (localOfInt.hasNext()))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        int[] arrayOfInt = new int[i];
        int j = 0;
        do
        {
          arrayOfInt[j] = localOfInt.nextInt();
          j++;
        } while ((j < i) && (localOfInt.hasNext()));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.IntArraySpliterator(arrayOfInt, 0, j, this.characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      this.it.forEachRemaining(paramIntConsumer);
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      if (this.it.hasNext())
      {
        paramIntConsumer.accept(this.it.nextInt());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super Integer> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static class IteratorSpliterator<T>
    implements Spliterator<T>
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private final Collection<? extends T> collection;
    private Iterator<? extends T> it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public IteratorSpliterator(Collection<? extends T> paramCollection, int paramInt)
    {
      this.collection = paramCollection;
      this.it = null;
      this.characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public IteratorSpliterator(Iterator<? extends T> paramIterator, long paramLong, int paramInt)
    {
      this.collection = null;
      this.it = paramIterator;
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public IteratorSpliterator(Iterator<? extends T> paramIterator, int paramInt)
    {
      this.collection = null;
      this.it = paramIterator;
      this.est = Long.MAX_VALUE;
      this.characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator<T> trySplit()
    {
      Iterator localIterator;
      long l;
      if ((localIterator = this.it) == null)
      {
        localIterator = this.it = this.collection.iterator();
        l = this.est = this.collection.size();
      }
      else
      {
        l = this.est;
      }
      if ((l > 1L) && (localIterator.hasNext()))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        Object[] arrayOfObject = new Object[i];
        int j = 0;
        do
        {
          arrayOfObject[j] = localIterator.next();
          j++;
        } while ((j < i) && (localIterator.hasNext()));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.ArraySpliterator(arrayOfObject, 0, j, this.characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Iterator localIterator;
      if ((localIterator = this.it) == null)
      {
        localIterator = this.it = this.collection.iterator();
        this.est = this.collection.size();
      }
      localIterator.forEachRemaining(paramConsumer);
    }
    
    public boolean tryAdvance(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      if (this.it == null)
      {
        this.it = this.collection.iterator();
        this.est = this.collection.size();
      }
      if (this.it.hasNext())
      {
        paramConsumer.accept(this.it.next());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      if (this.it == null)
      {
        this.it = this.collection.iterator();
        return this.est = this.collection.size();
      }
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super T> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class LongArraySpliterator
    implements Spliterator.OfLong
  {
    private final long[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public LongArraySpliterator(long[] paramArrayOfLong, int paramInt)
    {
      this(paramArrayOfLong, 0, paramArrayOfLong.length, paramInt);
    }
    
    public LongArraySpliterator(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3)
    {
      this.array = paramArrayOfLong;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator.OfLong trySplit()
    {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return i >= j ? null : new LongArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      long[] arrayOfLong;
      int j;
      int i;
      if (((arrayOfLong = this.array).length >= (j = this.fence)) && ((i = this.index) >= 0) && (i < (this.index = j))) {
        do
        {
          paramLongConsumer.accept(arrayOfLong[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      if ((this.index >= 0) && (this.index < this.fence))
      {
        paramLongConsumer.accept(this.array[(this.index++)]);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super Long> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class LongIteratorSpliterator
    implements Spliterator.OfLong
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private PrimitiveIterator.OfLong it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public LongIteratorSpliterator(PrimitiveIterator.OfLong paramOfLong, long paramLong, int paramInt)
    {
      this.it = paramOfLong;
      this.est = paramLong;
      this.characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public LongIteratorSpliterator(PrimitiveIterator.OfLong paramOfLong, int paramInt)
    {
      this.it = paramOfLong;
      this.est = Long.MAX_VALUE;
      this.characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator.OfLong trySplit()
    {
      PrimitiveIterator.OfLong localOfLong = this.it;
      long l = this.est;
      if ((l > 1L) && (localOfLong.hasNext()))
      {
        int i = this.batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        long[] arrayOfLong = new long[i];
        int j = 0;
        do
        {
          arrayOfLong[j] = localOfLong.nextLong();
          j++;
        } while ((j < i) && (localOfLong.hasNext()));
        this.batch = j;
        if (this.est != Long.MAX_VALUE) {
          this.est -= j;
        }
        return new Spliterators.LongArraySpliterator(arrayOfLong, 0, j, this.characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      this.it.forEachRemaining(paramLongConsumer);
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      if (this.it.hasNext())
      {
        paramLongConsumer.accept(this.it.nextLong());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return this.est;
    }
    
    public int characteristics()
    {
      return this.characteristics;
    }
    
    public Comparator<? super Long> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
}
