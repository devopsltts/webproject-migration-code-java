package java.util.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

public abstract interface Stream<T>
  extends BaseStream<T, Stream<T>>
{
  public abstract Stream<T> filter(Predicate<? super T> paramPredicate);
  
  public abstract <R> Stream<R> map(Function<? super T, ? extends R> paramFunction);
  
  public abstract IntStream mapToInt(ToIntFunction<? super T> paramToIntFunction);
  
  public abstract LongStream mapToLong(ToLongFunction<? super T> paramToLongFunction);
  
  public abstract DoubleStream mapToDouble(ToDoubleFunction<? super T> paramToDoubleFunction);
  
  public abstract <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> paramFunction);
  
  public abstract IntStream flatMapToInt(Function<? super T, ? extends IntStream> paramFunction);
  
  public abstract LongStream flatMapToLong(Function<? super T, ? extends LongStream> paramFunction);
  
  public abstract DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> paramFunction);
  
  public abstract Stream<T> distinct();
  
  public abstract Stream<T> sorted();
  
  public abstract Stream<T> sorted(Comparator<? super T> paramComparator);
  
  public abstract Stream<T> peek(Consumer<? super T> paramConsumer);
  
  public abstract Stream<T> limit(long paramLong);
  
  public abstract Stream<T> skip(long paramLong);
  
  public abstract void forEach(Consumer<? super T> paramConsumer);
  
  public abstract void forEachOrdered(Consumer<? super T> paramConsumer);
  
  public abstract Object[] toArray();
  
  public abstract <A> A[] toArray(IntFunction<A[]> paramIntFunction);
  
  public abstract T reduce(T paramT, BinaryOperator<T> paramBinaryOperator);
  
  public abstract Optional<T> reduce(BinaryOperator<T> paramBinaryOperator);
  
  public abstract <U> U reduce(U paramU, BiFunction<U, ? super T, U> paramBiFunction, BinaryOperator<U> paramBinaryOperator);
  
  public abstract <R> R collect(Supplier<R> paramSupplier, BiConsumer<R, ? super T> paramBiConsumer, BiConsumer<R, R> paramBiConsumer1);
  
  public abstract <R, A> R collect(Collector<? super T, A, R> paramCollector);
  
  public abstract Optional<T> min(Comparator<? super T> paramComparator);
  
  public abstract Optional<T> max(Comparator<? super T> paramComparator);
  
  public abstract long count();
  
  public abstract boolean anyMatch(Predicate<? super T> paramPredicate);
  
  public abstract boolean allMatch(Predicate<? super T> paramPredicate);
  
  public abstract boolean noneMatch(Predicate<? super T> paramPredicate);
  
  public abstract Optional<T> findFirst();
  
  public abstract Optional<T> findAny();
  
  public static <T> Builder<T> builder()
  {
    return new Streams.StreamBuilderImpl();
  }
  
  public static <T> Stream<T> empty()
  {
    return StreamSupport.stream(Spliterators.emptySpliterator(), false);
  }
  
  public static <T> Stream<T> of(T paramT)
  {
    return StreamSupport.stream(new Streams.StreamBuilderImpl(paramT), false);
  }
  
  @SafeVarargs
  public static <T> Stream<T> of(T... paramVarArgs)
  {
    return Arrays.stream(paramVarArgs);
  }
  
  public static <T> Stream<T> iterate(T paramT, final UnaryOperator<T> paramUnaryOperator)
  {
    Objects.requireNonNull(paramUnaryOperator);
    Iterator local1 = new Iterator()
    {
      T t = Streams.NONE;
      
      public boolean hasNext()
      {
        return true;
      }
      
      public T next()
      {
        return this.t = this.t == Streams.NONE ? this.val$seed : paramUnaryOperator.apply(this.t);
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(local1, 1040), false);
  }
  
  public static <T> Stream<T> generate(Supplier<T> paramSupplier)
  {
    Objects.requireNonNull(paramSupplier);
    return StreamSupport.stream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef(Long.MAX_VALUE, paramSupplier), false);
  }
  
  public static <T> Stream<T> concat(Stream<? extends T> paramStream1, Stream<? extends T> paramStream2)
  {
    Objects.requireNonNull(paramStream1);
    Objects.requireNonNull(paramStream2);
    Streams.ConcatSpliterator.OfRef localOfRef = new Streams.ConcatSpliterator.OfRef(paramStream1.spliterator(), paramStream2.spliterator());
    Stream localStream = StreamSupport.stream(localOfRef, (paramStream1.isParallel()) || (paramStream2.isParallel()));
    return (Stream)localStream.onClose(Streams.composedClose(paramStream1, paramStream2));
  }
  
  public static abstract interface Builder<T>
    extends Consumer<T>
  {
    public abstract void accept(T paramT);
    
    public Builder<T> add(T paramT)
    {
      accept(paramT);
      return this;
    }
    
    public abstract Stream<T> build();
  }
}
