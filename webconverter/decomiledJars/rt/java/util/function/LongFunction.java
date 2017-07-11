package java.util.function;

@FunctionalInterface
public abstract interface LongFunction<R>
{
  public abstract R apply(long paramLong);
}
