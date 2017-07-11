package java.util.function;

@FunctionalInterface
public abstract interface ToLongBiFunction<T, U>
{
  public abstract long applyAsLong(T paramT, U paramU);
}
