package java.util.stream;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator.OfInt;
import java.util.Spliterator.OfInt;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

public abstract interface IntStream
  extends BaseStream<Integer, IntStream>
{
  public abstract IntStream filter(IntPredicate paramIntPredicate);
  
  public abstract IntStream map(IntUnaryOperator paramIntUnaryOperator);
  
  public abstract <U> Stream<U> mapToObj(IntFunction<? extends U> paramIntFunction);
  
  public abstract LongStream mapToLong(IntToLongFunction paramIntToLongFunction);
  
  public abstract DoubleStream mapToDouble(IntToDoubleFunction paramIntToDoubleFunction);
  
  public abstract IntStream flatMap(IntFunction<? extends IntStream> paramIntFunction);
  
  public abstract IntStream distinct();
  
  public abstract IntStream sorted();
  
  public abstract IntStream peek(IntConsumer paramIntConsumer);
  
  public abstract IntStream limit(long paramLong);
  
  public abstract IntStream skip(long paramLong);
  
  public abstract void forEach(IntConsumer paramIntConsumer);
  
  public abstract void forEachOrdered(IntConsumer paramIntConsumer);
  
  public abstract int[] toArray();
  
  public abstract int reduce(int paramInt, IntBinaryOperator paramIntBinaryOperator);
  
  public abstract OptionalInt reduce(IntBinaryOperator paramIntBinaryOperator);
  
  public abstract <R> R collect(Supplier<R> paramSupplier, ObjIntConsumer<R> paramObjIntConsumer, BiConsumer<R, R> paramBiConsumer);
  
  public abstract int sum();
  
  public abstract OptionalInt min();
  
  public abstract OptionalInt max();
  
  public abstract long count();
  
  public abstract OptionalDouble average();
  
  public abstract IntSummaryStatistics summaryStatistics();
  
  public abstract boolean anyMatch(IntPredicate paramIntPredicate);
  
  public abstract boolean allMatch(IntPredicate paramIntPredicate);
  
  public abstract boolean noneMatch(IntPredicate paramIntPredicate);
  
  public abstract OptionalInt findFirst();
  
  public abstract OptionalInt findAny();
  
  public abstract LongStream asLongStream();
  
  public abstract DoubleStream asDoubleStream();
  
  public abstract Stream<Integer> boxed();
  
  public abstract IntStream sequential();
  
  public abstract IntStream parallel();
  
  public abstract PrimitiveIterator.OfInt iterator();
  
  public abstract Spliterator.OfInt spliterator();
  
  public static Builder builder()
  {
    return new Streams.IntStreamBuilderImpl();
  }
  
  public static IntStream empty()
  {
    return StreamSupport.intStream(Spliterators.emptyIntSpliterator(), false);
  }
  
  public static IntStream of(int paramInt)
  {
    return StreamSupport.intStream(new Streams.IntStreamBuilderImpl(paramInt), false);
  }
  
  public static IntStream of(int... paramVarArgs)
  {
    return Arrays.stream(paramVarArgs);
  }
  
  public static IntStream iterate(int paramInt, final IntUnaryOperator paramIntUnaryOperator)
  {
    Objects.requireNonNull(paramIntUnaryOperator);
    PrimitiveIterator.OfInt local1 = new PrimitiveIterator.OfInt()
    {
      int t = this.val$seed;
      
      public boolean hasNext()
      {
        return true;
      }
      
      public int nextInt()
      {
        int i = this.t;
        this.t = paramIntUnaryOperator.applyAsInt(this.t);
        return i;
      }
    };
    return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(local1, 1296), false);
  }
  
  public static IntStream generate(IntSupplier paramIntSupplier)
  {
    Objects.requireNonNull(paramIntSupplier);
    return StreamSupport.intStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(Long.MAX_VALUE, paramIntSupplier), false);
  }
  
  public static IntStream range(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      return empty();
    }
    return StreamSupport.intStream(new Streams.RangeIntSpliterator(paramInt1, paramInt2, false), false);
  }
  
  public static IntStream rangeClosed(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      return empty();
    }
    return StreamSupport.intStream(new Streams.RangeIntSpliterator(paramInt1, paramInt2, true), false);
  }
  
  public static IntStream concat(IntStream paramIntStream1, IntStream paramIntStream2)
  {
    Objects.requireNonNull(paramIntStream1);
    Objects.requireNonNull(paramIntStream2);
    Streams.ConcatSpliterator.OfInt localOfInt = new Streams.ConcatSpliterator.OfInt(paramIntStream1.spliterator(), paramIntStream2.spliterator());
    IntStream localIntStream = StreamSupport.intStream(localOfInt, (paramIntStream1.isParallel()) || (paramIntStream2.isParallel()));
    return (IntStream)localIntStream.onClose(Streams.composedClose(paramIntStream1, paramIntStream2));
  }
  
  public static abstract interface Builder
    extends IntConsumer
  {
    public abstract void accept(int paramInt);
    
    public Builder add(int paramInt)
    {
      accept(paramInt);
      return this;
    }
    
    public abstract IntStream build();
  }
}
