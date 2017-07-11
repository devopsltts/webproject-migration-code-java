package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerDouble<BeanT>
  extends Lister<BeanT, double[], Double, DoubleArrayPack>
{
  private PrimitiveArrayListerDouble() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Double.TYPE, new PrimitiveArrayListerDouble());
  }
  
  public ListIterator<Double> iterator(final double[] paramArrayOfDouble, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return this.idx < paramArrayOfDouble.length;
      }
      
      public Double next()
      {
        return Double.valueOf(paramArrayOfDouble[(this.idx++)]);
      }
    };
  }
  
  public DoubleArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor)
  {
    return new DoubleArrayPack();
  }
  
  public void addToPack(DoubleArrayPack paramDoubleArrayPack, Double paramDouble)
  {
    paramDoubleArrayPack.add(paramDouble);
  }
  
  public void endPacking(DoubleArrayPack paramDoubleArrayPack, BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramDoubleArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new double[0]);
  }
  
  static final class DoubleArrayPack
  {
    double[] buf = new double[16];
    int size;
    
    DoubleArrayPack() {}
    
    void add(Double paramDouble)
    {
      if (this.buf.length == this.size)
      {
        double[] arrayOfDouble = new double[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfDouble, 0, this.buf.length);
        this.buf = arrayOfDouble;
      }
      if (paramDouble != null) {
        this.buf[(this.size++)] = paramDouble.doubleValue();
      }
    }
    
    double[] build()
    {
      if (this.buf.length == this.size) {
        return this.buf;
      }
      double[] arrayOfDouble = new double[this.size];
      System.arraycopy(this.buf, 0, arrayOfDouble, 0, this.size);
      return arrayOfDouble;
    }
  }
}
