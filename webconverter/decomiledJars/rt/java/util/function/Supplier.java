package java.util.function;

@FunctionalInterface
public abstract interface Supplier<T>
{
  public abstract T get();
}
