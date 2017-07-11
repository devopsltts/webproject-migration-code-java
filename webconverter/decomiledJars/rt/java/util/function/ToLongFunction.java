package java.util.function;

@FunctionalInterface
public abstract interface ToLongFunction<T>
{
  public abstract long applyAsLong(T paramT);
}
