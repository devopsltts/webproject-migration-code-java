package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.LongBinaryOperator;

public class LongAccumulator
  extends Striped64
  implements Serializable
{
  private static final long serialVersionUID = 7249069246863182397L;
  private final LongBinaryOperator function;
  private final long identity;
  
  public LongAccumulator(LongBinaryOperator paramLongBinaryOperator, long paramLong)
  {
    this.function = paramLongBinaryOperator;
    this.base = (this.identity = paramLong);
  }
  
  public void accumulate(long paramLong)
  {
    Striped64.Cell[] arrayOfCell;
    long l1;
    long l3;
    if (((arrayOfCell = this.cells) != null) || (((l3 = this.function.applyAsLong(l1 = this.base, paramLong)) != l1) && (!casBase(l1, l3))))
    {
      boolean bool = true;
      int i;
      Striped64.Cell localCell;
      if ((arrayOfCell != null) && ((i = arrayOfCell.length - 1) >= 0) && ((localCell = arrayOfCell[(getProbe() & i)]) != null))
      {
        long l2;
        if ((bool = ((l3 = this.function.applyAsLong(l2 = localCell.value, paramLong)) == l2) || (localCell.cas(l2, l3)) ? 1 : 0) != 0) {}
      }
      else
      {
        longAccumulate(paramLong, this.function, bool);
      }
    }
  }
  
  public long get()
  {
    Striped64.Cell[] arrayOfCell = this.cells;
    long l = this.base;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          l = this.function.applyAsLong(l, localCell.value);
        }
      }
    }
    return l;
  }
  
  public void reset()
  {
    Striped64.Cell[] arrayOfCell = this.cells;
    this.base = this.identity;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          localCell.value = this.identity;
        }
      }
    }
  }
  
  public long getThenReset()
  {
    Striped64.Cell[] arrayOfCell = this.cells;
    long l1 = this.base;
    this.base = this.identity;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null)
        {
          long l2 = localCell.value;
          localCell.value = this.identity;
          l1 = this.function.applyAsLong(l1, l2);
        }
      }
    }
    return l1;
  }
  
  public String toString()
  {
    return Long.toString(get());
  }
  
  public long longValue()
  {
    return get();
  }
  
  public int intValue()
  {
    return (int)get();
  }
  
  public float floatValue()
  {
    return (float)get();
  }
  
  public double doubleValue()
  {
    return get();
  }
  
  private Object writeReplace()
  {
    return new SerializationProxy(this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Proxy required");
  }
  
  private static class SerializationProxy
    implements Serializable
  {
    private static final long serialVersionUID = 7249069246863182397L;
    private final long value;
    private final LongBinaryOperator function;
    private final long identity;
    
    SerializationProxy(LongAccumulator paramLongAccumulator)
    {
      this.function = paramLongAccumulator.function;
      this.identity = paramLongAccumulator.identity;
      this.value = paramLongAccumulator.get();
    }
    
    private Object readResolve()
    {
      LongAccumulator localLongAccumulator = new LongAccumulator(this.function, this.identity);
      localLongAccumulator.base = this.value;
      return localLongAccumulator;
    }
  }
}
