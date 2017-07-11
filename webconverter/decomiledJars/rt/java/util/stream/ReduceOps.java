package java.util.stream;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

final class ReduceOps
{
  private ReduceOps() {}
  
  public static <T, U> TerminalOp<T, U> makeRef(final U paramU, final BiFunction<U, ? super T, U> paramBiFunction, final BinaryOperator<U> paramBinaryOperator)
  {
    Objects.requireNonNull(paramBiFunction);
    Objects.requireNonNull(paramBinaryOperator);
    new ReduceOp(StreamShape.REFERENCE)
    {
      public ReduceOps.1ReducingSink makeSink()
      {
        return new ReduceOps.1ReducingSink(paramU, paramBiFunction, paramBinaryOperator);
      }
    };
  }
  
  public static <T> TerminalOp<T, Optional<T>> makeRef(final BinaryOperator<T> paramBinaryOperator)
  {
    Objects.requireNonNull(paramBinaryOperator);
    new ReduceOp(StreamShape.REFERENCE)
    {
      public ReduceOps.2ReducingSink makeSink()
      {
        return new ReduceOps.2ReducingSink(paramBinaryOperator);
      }
    };
  }
  
  public static <T, I> TerminalOp<T, I> makeRef(final Collector<? super T, I, ?> paramCollector)
  {
    final Supplier localSupplier = ((Collector)Objects.requireNonNull(paramCollector)).supplier();
    final BiConsumer localBiConsumer = paramCollector.accumulator();
    final BinaryOperator localBinaryOperator = paramCollector.combiner();
    new ReduceOp(StreamShape.REFERENCE)
    {
      public ReduceOps.3ReducingSink makeSink()
      {
        return new ReduceOps.3ReducingSink(localSupplier, localBiConsumer, localBinaryOperator);
      }
      
      public int getOpFlags()
      {
        return paramCollector.characteristics().contains(Collector.Characteristics.UNORDERED) ? StreamOpFlag.NOT_ORDERED : 0;
      }
    };
  }
  
  public static <T, R> TerminalOp<T, R> makeRef(final Supplier<R> paramSupplier, final BiConsumer<R, ? super T> paramBiConsumer, final BiConsumer<R, R> paramBiConsumer1)
  {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramBiConsumer);
    Objects.requireNonNull(paramBiConsumer1);
    new ReduceOp(StreamShape.REFERENCE)
    {
      public ReduceOps.4ReducingSink makeSink()
      {
        return new ReduceOps.4ReducingSink(paramSupplier, paramBiConsumer, paramBiConsumer1);
      }
    };
  }
  
  public static TerminalOp<Integer, Integer> makeInt(final int paramInt, final IntBinaryOperator paramIntBinaryOperator)
  {
    Objects.requireNonNull(paramIntBinaryOperator);
    new ReduceOp(StreamShape.INT_VALUE)
    {
      public ReduceOps.5ReducingSink makeSink()
      {
        return new ReduceOps.5ReducingSink(paramInt, paramIntBinaryOperator);
      }
    };
  }
  
  public static TerminalOp<Integer, OptionalInt> makeInt(final IntBinaryOperator paramIntBinaryOperator)
  {
    Objects.requireNonNull(paramIntBinaryOperator);
    new ReduceOp(StreamShape.INT_VALUE)
    {
      public ReduceOps.6ReducingSink makeSink()
      {
        return new ReduceOps.6ReducingSink(paramIntBinaryOperator);
      }
    };
  }
  
  public static <R> TerminalOp<Integer, R> makeInt(final Supplier<R> paramSupplier, final ObjIntConsumer<R> paramObjIntConsumer, final BinaryOperator<R> paramBinaryOperator)
  {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramObjIntConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    new ReduceOp(StreamShape.INT_VALUE)
    {
      public ReduceOps.7ReducingSink makeSink()
      {
        return new ReduceOps.7ReducingSink(paramSupplier, paramObjIntConsumer, paramBinaryOperator);
      }
    };
  }
  
  public static TerminalOp<Long, Long> makeLong(final long paramLong, final LongBinaryOperator paramLongBinaryOperator)
  {
    Objects.requireNonNull(paramLongBinaryOperator);
    new ReduceOp(StreamShape.LONG_VALUE)
    {
      public ReduceOps.8ReducingSink makeSink()
      {
        return new ReduceOps.8ReducingSink(paramLong, paramLongBinaryOperator);
      }
    };
  }
  
  public static TerminalOp<Long, OptionalLong> makeLong(final LongBinaryOperator paramLongBinaryOperator)
  {
    Objects.requireNonNull(paramLongBinaryOperator);
    new ReduceOp(StreamShape.LONG_VALUE)
    {
      public ReduceOps.9ReducingSink makeSink()
      {
        return new ReduceOps.9ReducingSink(paramLongBinaryOperator);
      }
    };
  }
  
  public static <R> TerminalOp<Long, R> makeLong(final Supplier<R> paramSupplier, final ObjLongConsumer<R> paramObjLongConsumer, final BinaryOperator<R> paramBinaryOperator)
  {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramObjLongConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    new ReduceOp(StreamShape.LONG_VALUE)
    {
      public ReduceOps.10ReducingSink makeSink()
      {
        return new ReduceOps.10ReducingSink(paramSupplier, paramObjLongConsumer, paramBinaryOperator);
      }
    };
  }
  
  public static TerminalOp<Double, Double> makeDouble(final double paramDouble, final DoubleBinaryOperator paramDoubleBinaryOperator)
  {
    Objects.requireNonNull(paramDoubleBinaryOperator);
    new ReduceOp(StreamShape.DOUBLE_VALUE)
    {
      public ReduceOps.11ReducingSink makeSink()
      {
        return new ReduceOps.11ReducingSink(paramDouble, paramDoubleBinaryOperator);
      }
    };
  }
  
  public static TerminalOp<Double, OptionalDouble> makeDouble(final DoubleBinaryOperator paramDoubleBinaryOperator)
  {
    Objects.requireNonNull(paramDoubleBinaryOperator);
    new ReduceOp(StreamShape.DOUBLE_VALUE)
    {
      public ReduceOps.12ReducingSink makeSink()
      {
        return new ReduceOps.12ReducingSink(paramDoubleBinaryOperator);
      }
    };
  }
  
  public static <R> TerminalOp<Double, R> makeDouble(final Supplier<R> paramSupplier, final ObjDoubleConsumer<R> paramObjDoubleConsumer, final BinaryOperator<R> paramBinaryOperator)
  {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramObjDoubleConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    new ReduceOp(StreamShape.DOUBLE_VALUE)
    {
      public ReduceOps.13ReducingSink makeSink()
      {
        return new ReduceOps.13ReducingSink(paramSupplier, paramObjDoubleConsumer, paramBinaryOperator);
      }
    };
  }
  
  private static abstract interface AccumulatingSink<T, R, K extends AccumulatingSink<T, R, K>>
    extends TerminalSink<T, R>
  {
    public abstract void combine(K paramK);
  }
  
  private static abstract class Box<U>
  {
    U state;
    
    Box() {}
    
    public U get()
    {
      return this.state;
    }
  }
  
  private static abstract class ReduceOp<T, R, S extends ReduceOps.AccumulatingSink<T, R, S>>
    implements TerminalOp<T, R>
  {
    private final StreamShape inputShape;
    
    ReduceOp(StreamShape paramStreamShape)
    {
      this.inputShape = paramStreamShape;
    }
    
    public abstract S makeSink();
    
    public StreamShape inputShape()
    {
      return this.inputShape;
    }
    
    public <P_IN> R evaluateSequential(PipelineHelper<T> paramPipelineHelper, Spliterator<P_IN> paramSpliterator)
    {
      return ((ReduceOps.AccumulatingSink)paramPipelineHelper.wrapAndCopyInto(makeSink(), paramSpliterator)).get();
    }
    
    public <P_IN> R evaluateParallel(PipelineHelper<T> paramPipelineHelper, Spliterator<P_IN> paramSpliterator)
    {
      return ((ReduceOps.AccumulatingSink)new ReduceOps.ReduceTask(this, paramPipelineHelper, paramSpliterator).invoke()).get();
    }
  }
  
  private static final class ReduceTask<P_IN, P_OUT, R, S extends ReduceOps.AccumulatingSink<P_OUT, R, S>>
    extends AbstractTask<P_IN, P_OUT, S, ReduceTask<P_IN, P_OUT, R, S>>
  {
    private final ReduceOps.ReduceOp<P_OUT, R, S> op;
    
    ReduceTask(ReduceOps.ReduceOp<P_OUT, R, S> paramReduceOp, PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator)
    {
      super(paramSpliterator);
      this.op = paramReduceOp;
    }
    
    ReduceTask(ReduceTask<P_IN, P_OUT, R, S> paramReduceTask, Spliterator<P_IN> paramSpliterator)
    {
      super(paramSpliterator);
      this.op = paramReduceTask.op;
    }
    
    protected ReduceTask<P_IN, P_OUT, R, S> makeChild(Spliterator<P_IN> paramSpliterator)
    {
      return new ReduceTask(this, paramSpliterator);
    }
    
    protected S doLeaf()
    {
      return (ReduceOps.AccumulatingSink)this.helper.wrapAndCopyInto(this.op.makeSink(), this.spliterator);
    }
    
    public void onCompletion(CountedCompleter<?> paramCountedCompleter)
    {
      if (!isLeaf())
      {
        ReduceOps.AccumulatingSink localAccumulatingSink = (ReduceOps.AccumulatingSink)((ReduceTask)this.leftChild).getLocalResult();
        localAccumulatingSink.combine((ReduceOps.AccumulatingSink)((ReduceTask)this.rightChild).getLocalResult());
        setLocalResult(localAccumulatingSink);
      }
      super.onCompletion(paramCountedCompleter);
    }
  }
}
