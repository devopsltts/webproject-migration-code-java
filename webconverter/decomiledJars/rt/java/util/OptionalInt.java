package java.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class OptionalInt
{
  private static final OptionalInt EMPTY = new OptionalInt();
  private final boolean isPresent;
  private final int value;
  
  private OptionalInt()
  {
    this.isPresent = false;
    this.value = 0;
  }
  
  public static OptionalInt empty()
  {
    return EMPTY;
  }
  
  private OptionalInt(int paramInt)
  {
    this.isPresent = true;
    this.value = paramInt;
  }
  
  public static OptionalInt of(int paramInt)
  {
    return new OptionalInt(paramInt);
  }
  
  public int getAsInt()
  {
    if (!this.isPresent) {
      throw new NoSuchElementException("No value present");
    }
    return this.value;
  }
  
  public boolean isPresent()
  {
    return this.isPresent;
  }
  
  public void ifPresent(IntConsumer paramIntConsumer)
  {
    if (this.isPresent) {
      paramIntConsumer.accept(this.value);
    }
  }
  
  public int orElse(int paramInt)
  {
    return this.isPresent ? this.value : paramInt;
  }
  
  public int orElseGet(IntSupplier paramIntSupplier)
  {
    return this.isPresent ? this.value : paramIntSupplier.getAsInt();
  }
  
  public <X extends Throwable> int orElseThrow(Supplier<X> paramSupplier)
    throws Throwable
  {
    if (this.isPresent) {
      return this.value;
    }
    throw ((Throwable)paramSupplier.get());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof OptionalInt)) {
      return false;
    }
    OptionalInt localOptionalInt = (OptionalInt)paramObject;
    return this.value == localOptionalInt.value;
  }
  
  public int hashCode()
  {
    return this.isPresent ? Integer.hashCode(this.value) : 0;
  }
  
  public String toString()
  {
    return this.isPresent ? String.format("OptionalInt[%s]", new Object[] { Integer.valueOf(this.value) }) : "OptionalInt.empty";
  }
}
