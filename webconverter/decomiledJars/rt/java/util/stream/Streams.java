package java.util.stream;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

final class Streams
{
  static final Object NONE = new Object();
  
  private Streams()
  {
    throw new Error("no instances");
  }
  
  static Runnable composeWithExceptions(Runnable paramRunnable1, final Runnable paramRunnable2)
  {
    new Runnable()
    {
      public void run()
      {
        try
        {
          this.val$a.run();
        }
        catch (Throwable localThrowable1)
        {
          try
          {
            paramRunnable2.run();
          }
          catch (Throwable localThrowable2)
          {
            try
            {
              localThrowable1.addSuppressed(localThrowable2);
            }
            catch (Throwable localThrowable3) {}
          }
          throw localThrowable1;
        }
        paramRunnable2.run();
      }
    };
  }
  
  static Runnable composedClose(BaseStream<?, ?> paramBaseStream1, final BaseStream<?, ?> paramBaseStream2)
  {
    new Runnable()
    {
      public void run()
      {
        try
        {
          this.val$a.close();
        }
        catch (Throwable localThrowable1)
        {
          try
          {
            paramBaseStream2.close();
          }
          catch (Throwable localThrowable2)
          {
            try
            {
              localThrowable1.addSuppressed(localThrowable2);
            }
            catch (Throwable localThrowable3) {}
          }
          throw localThrowable1;
        }
        paramBaseStream2.close();
      }
    };
  }
  
  private static abstract class AbstractStreamBuilderImpl<T, S extends Spliterator<T>>
    implements Spliterator<T>
  {
    int count;
    
    private AbstractStreamBuilderImpl() {}
    
    public S trySplit()
    {
      return null;
    }
    
    public long estimateSize()
    {
      return -this.count - 1;
    }
    
    public int characteristics()
    {
      return 17488;
    }
  }
  
  static abstract class ConcatSpliterator<T, T_SPLITR extends Spliterator<T>>
    implements Spliterator<T>
  {
    protected final T_SPLITR aSpliterator;
    protected final T_SPLITR bSpliterator;
    boolean beforeSplit;
    final boolean unsized;
    
    public ConcatSpliterator(T_SPLITR paramT_SPLITR1, T_SPLITR paramT_SPLITR2)
    {
      this.aSpliterator = paramT_SPLITR1;
      this.bSpliterator = paramT_SPLITR2;
      this.beforeSplit = true;
      this.unsized = (paramT_SPLITR1.estimateSize() + paramT_SPLITR2.estimateSize() < 0L);
    }
    
    public T_SPLITR trySplit()
    {
      Spliterator localSpliterator = this.beforeSplit ? this.aSpliterator : this.bSpliterator.trySplit();
      this.beforeSplit = false;
      return localSpliterator;
    }
    
    public boolean tryAdvance(Consumer<? super T> paramConsumer)
    {
      boolean bool;
      if (this.beforeSplit)
      {
        bool = this.aSpliterator.tryAdvance(paramConsumer);
        if (!bool)
        {
          this.beforeSplit = false;
          bool = this.bSpliterator.tryAdvance(paramConsumer);
        }
      }
      else
      {
        bool = this.bSpliterator.tryAdvance(paramConsumer);
      }
      return bool;
    }
    
    public void forEachRemaining(Consumer<? super T> paramConsumer)
    {
      if (this.beforeSplit) {
        this.aSpliterator.forEachRemaining(paramConsumer);
      }
      this.bSpliterator.forEachRemaining(paramConsumer);
    }
    
    public long estimateSize()
    {
      if (this.beforeSplit)
      {
        long l = this.aSpliterator.estimateSize() + this.bSpliterator.estimateSize();
        return l >= 0L ? l : Long.MAX_VALUE;
      }
      return this.bSpliterator.estimateSize();
    }
    
    public int characteristics()
    {
      if (this.beforeSplit) {
        return this.aSpliterator.characteristics() & this.bSpliterator.characteristics() & ((0x5 | (this.unsized ? 16448 : 0)) ^ 0xFFFFFFFF);
      }
      return this.bSpliterator.characteristics();
    }
    
    public Comparator<? super T> getComparator()
    {
      if (this.beforeSplit) {
        throw new IllegalStateException();
      }
      return this.bSpliterator.getComparator();
    }
    
    static class OfDouble
      extends Streams.ConcatSpliterator.OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble>
      implements Spliterator.OfDouble
    {
      OfDouble(Spliterator.OfDouble paramOfDouble1, Spliterator.OfDouble paramOfDouble2)
      {
        super(paramOfDouble2, null);
      }
    }
    
    static class OfInt
      extends Streams.ConcatSpliterator.OfPrimitive<Integer, IntConsumer, Spliterator.OfInt>
      implements Spliterator.OfInt
    {
      OfInt(Spliterator.OfInt paramOfInt1, Spliterator.OfInt paramOfInt2)
      {
        super(paramOfInt2, null);
      }
    }
    
    static class OfLong
      extends Streams.ConcatSpliterator.OfPrimitive<Long, LongConsumer, Spliterator.OfLong>
      implements Spliterator.OfLong
    {
      OfLong(Spliterator.OfLong paramOfLong1, Spliterator.OfLong paramOfLong2)
      {
        super(paramOfLong2, null);
      }
    }
    
    private static abstract class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>>
      extends Streams.ConcatSpliterator<T, T_SPLITR>
      implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>
    {
      private OfPrimitive(T_SPLITR paramT_SPLITR1, T_SPLITR paramT_SPLITR2)
      {
        super(paramT_SPLITR2);
      }
      
      public boolean tryAdvance(T_CONS paramT_CONS)
      {
        boolean bool;
        if (this.beforeSplit)
        {
          bool = ((Spliterator.OfPrimitive)this.aSpliterator).tryAdvance(paramT_CONS);
          if (!bool)
          {
            this.beforeSplit = false;
            bool = ((Spliterator.OfPrimitive)this.bSpliterator).tryAdvance(paramT_CONS);
          }
        }
        else
        {
          bool = ((Spliterator.OfPrimitive)this.bSpliterator).tryAdvance(paramT_CONS);
        }
        return bool;
      }
      
      public void forEachRemaining(T_CONS paramT_CONS)
      {
        if (this.beforeSplit) {
          ((Spliterator.OfPrimitive)this.aSpliterator).forEachRemaining(paramT_CONS);
        }
        ((Spliterator.OfPrimitive)this.bSpliterator).forEachRemaining(paramT_CONS);
      }
    }
    
    static class OfRef<T>
      extends Streams.ConcatSpliterator<T, Spliterator<T>>
    {
      OfRef(Spliterator<T> paramSpliterator1, Spliterator<T> paramSpliterator2)
      {
        super(paramSpliterator2);
      }
    }
  }
  
  static final class DoubleStreamBuilderImpl
    extends Streams.AbstractStreamBuilderImpl<Double, Spliterator.OfDouble>
    implements DoubleStream.Builder, Spliterator.OfDouble
  {
    double first;
    SpinedBuffer.OfDouble buffer;
    
    DoubleStreamBuilderImpl()
    {
      super();
    }
    
    DoubleStreamBuilderImpl(double paramDouble)
    {
      super();
      this.first = paramDouble;
      this.count = -2;
    }
    
    public void accept(double paramDouble)
    {
      if (this.count == 0)
      {
        this.first = paramDouble;
        this.count += 1;
      }
      else if (this.count > 0)
      {
        if (this.buffer == null)
        {
          this.buffer = new SpinedBuffer.OfDouble();
          this.buffer.accept(this.first);
          this.count += 1;
        }
        this.buffer.accept(paramDouble);
      }
      else
      {
        throw new IllegalStateException();
      }
    }
    
    public DoubleStream build()
    {
      int i = this.count;
      if (i >= 0)
      {
        this.count = (-this.count - 1);
        return i < 2 ? StreamSupport.doubleStream(this, false) : StreamSupport.doubleStream(this.buffer.spliterator(), false);
      }
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      Objects.requireNonNull(paramDoubleConsumer);
      if (this.count == -2)
      {
        paramDoubleConsumer.accept(this.first);
        this.count = -1;
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      Objects.requireNonNull(paramDoubleConsumer);
      if (this.count == -2)
      {
        paramDoubleConsumer.accept(this.first);
        this.count = -1;
      }
    }
  }
  
  static final class IntStreamBuilderImpl
    extends Streams.AbstractStreamBuilderImpl<Integer, Spliterator.OfInt>
    implements IntStream.Builder, Spliterator.OfInt
  {
    int first;
    SpinedBuffer.OfInt buffer;
    
    IntStreamBuilderImpl()
    {
      super();
    }
    
    IntStreamBuilderImpl(int paramInt)
    {
      super();
      this.first = paramInt;
      this.count = -2;
    }
    
    public void accept(int paramInt)
    {
      if (this.count == 0)
      {
        this.first = paramInt;
        this.count += 1;
      }
      else if (this.count > 0)
      {
        if (this.buffer == null)
        {
          this.buffer = new SpinedBuffer.OfInt();
          this.buffer.accept(this.first);
          this.count += 1;
        }
        this.buffer.accept(paramInt);
      }
      else
      {
        throw new IllegalStateException();
      }
    }
    
    public IntStream build()
    {
      int i = this.count;
      if (i >= 0)
      {
        this.count = (-this.count - 1);
        return i < 2 ? StreamSupport.intStream(this, false) : StreamSupport.intStream(this.buffer.spliterator(), false);
      }
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      Objects.requireNonNull(paramIntConsumer);
      if (this.count == -2)
      {
        paramIntConsumer.accept(this.first);
        this.count = -1;
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      Objects.requireNonNull(paramIntConsumer);
      if (this.count == -2)
      {
        paramIntConsumer.accept(this.first);
        this.count = -1;
      }
    }
  }
  
  static final class LongStreamBuilderImpl
    extends Streams.AbstractStreamBuilderImpl<Long, Spliterator.OfLong>
    implements LongStream.Builder, Spliterator.OfLong
  {
    long first;
    SpinedBuffer.OfLong buffer;
    
    LongStreamBuilderImpl()
    {
      super();
    }
    
    LongStreamBuilderImpl(long paramLong)
    {
      super();
      this.first = paramLong;
      this.count = -2;
    }
    
    public void accept(long paramLong)
    {
      if (this.count == 0)
      {
        this.first = paramLong;
        this.count += 1;
      }
      else if (this.count > 0)
      {
        if (this.buffer == null)
        {
          this.buffer = new SpinedBuffer.OfLong();
          this.buffer.accept(this.first);
          this.count += 1;
        }
        this.buffer.accept(paramLong);
      }
      else
      {
        throw new IllegalStateException();
      }
    }
    
    public LongStream build()
    {
      int i = this.count;
      if (i >= 0)
      {
        this.count = (-this.count - 1);
        return i < 2 ? StreamSupport.longStream(this, false) : StreamSupport.longStream(this.buffer.spliterator(), false);
      }
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      Objects.requireNonNull(paramLongConsumer);
      if (this.count == -2)
      {
        paramLongConsumer.accept(this.first);
        this.count = -1;
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      Objects.requireNonNull(paramLongConsumer);
      if (this.count == -2)
      {
        paramLongConsumer.accept(this.first);
        this.count = -1;
      }
    }
  }
  
  static final class RangeIntSpliterator
    implements Spliterator.OfInt
  {
    private int from;
    private final int upTo;
    private int last;
    private static final int BALANCED_SPLIT_THRESHOLD = 16777216;
    private static final int RIGHT_BALANCED_SPLIT_RATIO = 8;
    
    RangeIntSpliterator(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this(paramInt1, paramInt2, paramBoolean ? 1 : 0);
    }
    
    private RangeIntSpliterator(int paramInt1, int paramInt2, int paramInt3)
    {
      this.from = paramInt1;
      this.upTo = paramInt2;
      this.last = paramInt3;
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      Objects.requireNonNull(paramIntConsumer);
      int i = this.from;
      if (i < this.upTo)
      {
        this.from += 1;
        paramIntConsumer.accept(i);
        return true;
      }
      if (this.last > 0)
      {
        this.last = 0;
        paramIntConsumer.accept(i);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      Objects.requireNonNull(paramIntConsumer);
      int i = this.from;
      int j = this.upTo;
      int k = this.last;
      this.from = this.upTo;
      this.last = 0;
      while (i < j) {
        paramIntConsumer.accept(i++);
      }
      if (k > 0) {
        paramIntConsumer.accept(i);
      }
    }
    
    public long estimateSize()
    {
      return this.upTo - this.from + this.last;
    }
    
    public int characteristics()
    {
      return 17749;
    }
    
    public Comparator<? super Integer> getComparator()
    {
      return null;
    }
    
    public Spliterator.OfInt trySplit()
    {
      long l = estimateSize();
      return l <= 1L ? null : new RangeIntSpliterator(this.from, this.from += splitPoint(l), 0);
    }
    
    private int splitPoint(long paramLong)
    {
      int i = paramLong < 16777216L ? 2 : 8;
      return (int)(paramLong / i);
    }
  }
  
  static final class RangeLongSpliterator
    implements Spliterator.OfLong
  {
    private long from;
    private final long upTo;
    private int last;
    private static final long BALANCED_SPLIT_THRESHOLD = 16777216L;
    private static final long RIGHT_BALANCED_SPLIT_RATIO = 8L;
    
    RangeLongSpliterator(long paramLong1, long paramLong2, boolean paramBoolean)
    {
      this(paramLong1, paramLong2, paramBoolean ? 1 : 0);
    }
    
    private RangeLongSpliterator(long paramLong1, long paramLong2, int paramInt)
    {
      assert (paramLong2 - paramLong1 + paramInt > 0L);
      this.from = paramLong1;
      this.upTo = paramLong2;
      this.last = paramInt;
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      Objects.requireNonNull(paramLongConsumer);
      long l = this.from;
      if (l < this.upTo)
      {
        this.from += 1L;
        paramLongConsumer.accept(l);
        return true;
      }
      if (this.last > 0)
      {
        this.last = 0;
        paramLongConsumer.accept(l);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      Objects.requireNonNull(paramLongConsumer);
      long l1 = this.from;
      long l2 = this.upTo;
      int i = this.last;
      this.from = this.upTo;
      this.last = 0;
      while (l1 < l2) {
        paramLongConsumer.accept(l1++);
      }
      if (i > 0) {
        paramLongConsumer.accept(l1);
      }
    }
    
    public long estimateSize()
    {
      return this.upTo - this.from + this.last;
    }
    
    public int characteristics()
    {
      return 17749;
    }
    
    public Comparator<? super Long> getComparator()
    {
      return null;
    }
    
    public Spliterator.OfLong trySplit()
    {
      long l = estimateSize();
      return l <= 1L ? null : new RangeLongSpliterator(this.from, this.from += splitPoint(l), 0);
    }
    
    private long splitPoint(long paramLong)
    {
      long l = paramLong < 16777216L ? 2L : 8L;
      return paramLong / l;
    }
  }
  
  static final class StreamBuilderImpl<T>
    extends Streams.AbstractStreamBuilderImpl<T, Spliterator<T>>
    implements Stream.Builder<T>
  {
    T first;
    SpinedBuffer<T> buffer;
    
    StreamBuilderImpl()
    {
      super();
    }
    
    StreamBuilderImpl(T paramT)
    {
      super();
      this.first = paramT;
      this.count = -2;
    }
    
    public void accept(T paramT)
    {
      if (this.count == 0)
      {
        this.first = paramT;
        this.count += 1;
      }
      else if (this.count > 0)
      {
        if (this.buffer == null)
        {
          this.buffer = new SpinedBuffer();
          this.buffer.accept(this.first);
          this.count += 1;
        }
        this.buffer.accept(paramT);
      }
      else
      {
        throw new IllegalStateException();
      }
    }
    
    public Stream.Builder<T> add(T paramT)
    {
      accept(paramT);
      return this;
    }
    
    public Stream<T> build()
    {
      int i = this.count;
      if (i >= 0)
      {
        this.count = (-this.count - 1);
        return i < 2 ? StreamSupport.stream(this, false) : StreamSupport.stream(this.buffer.spliterator(), false);
      }
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(Consumer<? super T> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      if (this.count == -2)
      {
        paramConsumer.accept(this.first);
        this.count = -1;
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(Consumer<? super T> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      if (this.count == -2)
      {
        paramConsumer.accept(this.first);
        this.count = -1;
      }
    }
  }
}
