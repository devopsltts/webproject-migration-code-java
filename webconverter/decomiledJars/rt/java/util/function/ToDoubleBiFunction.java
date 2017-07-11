package java.util.function;

@FunctionalInterface
public abstract interface ToDoubleBiFunction<T, U>
{
  public abstract double applyAsDouble(T paramT, U paramU);
}
