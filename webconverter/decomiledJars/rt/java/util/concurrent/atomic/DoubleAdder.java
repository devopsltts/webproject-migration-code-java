package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class DoubleAdder
  extends Striped64
  implements Serializable
{
  private static final long serialVersionUID = 7249069246863182397L;
  
  public DoubleAdder() {}
  
  public void add(double paramDouble)
  {
    Striped64.Cell[] arrayOfCell;
    long l1;
    if (((arrayOfCell = this.cells) != null) || (!casBase(l1 = this.base, Double.doubleToRawLongBits(Double.longBitsToDouble(l1) + paramDouble))))
    {
      boolean bool = true;
      int i;
      Striped64.Cell localCell;
      long l2;
      if ((arrayOfCell == null) || ((i = arrayOfCell.length - 1) < 0) || ((localCell = arrayOfCell[(getProbe() & i)]) == null) || (!(bool = localCell.cas(l2 = localCell.value, Double.doubleToRawLongBits(Double.longBitsToDouble(l2) + paramDouble))))) {
        doubleAccumulate(paramDouble, null, bool);
      }
    }
  }
  
  public double sum()
  {
    Striped64.Cell[] arrayOfCell = this.cells;
    double d = Double.longBitsToDouble(this.base);
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          d += Double.longBitsToDouble(localCell.value);
        }
      }
    }
    return d;
  }
  
  public void reset()
  {
    Striped64.Cell[] arrayOfCell = this.cells;
    this.base = 0L;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          localCell.value = 0L;
        }
      }
    }
  }
  
  public double sumThenReset()
  {
    Striped64.Cell[] arrayOfCell = this.cells;
    double d = Double.longBitsToDouble(this.base);
    this.base = 0L;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null)
        {
          long l = localCell.value;
          localCell.value = 0L;
          d += Double.longBitsToDouble(l);
        }
      }
    }
    return d;
  }
  
  public String toString()
  {
    return Double.toString(sum());
  }
  
  public double doubleValue()
  {
    return sum();
  }
  
  public long longValue()
  {
    return sum();
  }
  
  public int intValue()
  {
    return (int)sum();
  }
  
  public float floatValue()
  {
    return (float)sum();
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
    private final double value;
    
    SerializationProxy(DoubleAdder paramDoubleAdder)
    {
      this.value = paramDoubleAdder.sum();
    }
    
    private Object readResolve()
    {
      DoubleAdder localDoubleAdder = new DoubleAdder();
      localDoubleAdder.base = Double.doubleToRawLongBits(this.value);
      return localDoubleAdder;
    }
  }
}
