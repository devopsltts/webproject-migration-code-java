package java.util;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;

class ArrayPrefixHelpers
{
  static final int CUMULATE = 1;
  static final int SUMMED = 2;
  static final int FINISHED = 4;
  static final int MIN_PARTITION = 16;
  
  private ArrayPrefixHelpers() {}
  
  static final class CumulateTask<T>
    extends CountedCompleter<Void>
  {
    final T[] array;
    final BinaryOperator<T> function;
    CumulateTask<T> left;
    CumulateTask<T> right;
    T in;
    T out;
    final int lo;
    final int hi;
    final int origin;
    final int fence;
    final int threshold;
    
    public CumulateTask(CumulateTask<T> paramCumulateTask, BinaryOperator<T> paramBinaryOperator, T[] paramArrayOfT, int paramInt1, int paramInt2)
    {
      super();
      this.function = paramBinaryOperator;
      this.array = paramArrayOfT;
      this.lo = (this.origin = paramInt1);
      this.hi = (this.fence = paramInt2);
      int i;
      this.threshold = ((i = (paramInt2 - paramInt1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : i);
    }
    
    CumulateTask(CumulateTask<T> paramCumulateTask, BinaryOperator<T> paramBinaryOperator, T[] paramArrayOfT, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super();
      this.function = paramBinaryOperator;
      this.array = paramArrayOfT;
      this.origin = paramInt1;
      this.fence = paramInt2;
      this.threshold = paramInt3;
      this.lo = paramInt4;
      this.hi = paramInt5;
    }
    
    public final void compute()
    {
      BinaryOperator localBinaryOperator;
      Object[] arrayOfObject;
      if (((localBinaryOperator = this.function) == null) || ((arrayOfObject = this.array) == null)) {
        throw new NullPointerException();
      }
      int i = this.threshold;
      int j = this.origin;
      int k = this.fence;
      Object localObject2 = this;
      int m;
      Object localObject1;
      while (((m = ((CumulateTask)localObject2).lo) >= 0) && ((localObject1 = ((CumulateTask)localObject2).hi) <= arrayOfObject.length))
      {
        Object localObject6;
        int i4;
        if (localObject1 - m > i)
        {
          CumulateTask localCumulateTask1 = ((CumulateTask)localObject2).left;
          CumulateTask localCumulateTask2 = ((CumulateTask)localObject2).right;
          Object localObject4;
          if (localCumulateTask1 == null)
          {
            int i2 = m + localObject1 >>> 1;
            localObject4 = localCumulateTask2 = ((CumulateTask)localObject2).right = new CumulateTask((CumulateTask)localObject2, localBinaryOperator, arrayOfObject, j, k, i, i2, localObject1);
            localObject2 = localCumulateTask1 = ((CumulateTask)localObject2).left = new CumulateTask((CumulateTask)localObject2, localBinaryOperator, arrayOfObject, j, k, i, m, i2);
          }
          else
          {
            localObject6 = ((CumulateTask)localObject2).in;
            localCumulateTask1.in = localObject6;
            localObject4 = localObject2 = null;
            if (localCumulateTask2 != null)
            {
              Object localObject7 = localCumulateTask1.out;
              localCumulateTask2.in = (m == j ? localObject7 : localBinaryOperator.apply(localObject6, localObject7));
              int i5;
              while (((i5 = localCumulateTask2.getPendingCount()) & 0x1) == 0) {
                if (localCumulateTask2.compareAndSetPendingCount(i5, i5 | 0x1)) {
                  localObject2 = localCumulateTask2;
                }
              }
            }
            while (((i4 = localCumulateTask1.getPendingCount()) & 0x1) == 0) {
              if (localCumulateTask1.compareAndSetPendingCount(i4, i4 | 0x1))
              {
                if (localObject2 != null) {
                  localObject4 = localObject2;
                }
                localObject2 = localCumulateTask1;
              }
            }
            if (localObject2 == null) {
              break;
            }
          }
          if (localObject4 != null) {
            ((CumulateTask)localObject4).fork();
          }
        }
        else
        {
          int i1;
          while (((i1 = ((CumulateTask)localObject2).getPendingCount()) & 0x4) == 0)
          {
            int n = m > j ? 2 : (i1 & 0x1) != 0 ? 4 : 6;
            if (((CumulateTask)localObject2).compareAndSetPendingCount(i1, i1 | n))
            {
              Object localObject3;
              Object localObject5;
              if (n != 2)
              {
                if (m == j)
                {
                  localObject3 = arrayOfObject[j];
                  localObject5 = j + 1;
                }
                else
                {
                  localObject3 = ((CumulateTask)localObject2).in;
                  localObject5 = m;
                }
                for (localObject6 = localObject5; localObject6 < localObject1; localObject6++)
                {
                  Object tmp461_456 = localBinaryOperator.apply(localObject3, arrayOfObject[localObject6]);
                  localObject3 = tmp461_456;
                  arrayOfObject[localObject6] = tmp461_456;
                }
              }
              else if (localObject1 < k)
              {
                localObject3 = arrayOfObject[m];
                for (localObject5 = m + 1; localObject5 < localObject1; localObject5++) {
                  localObject3 = localBinaryOperator.apply(localObject3, arrayOfObject[localObject5]);
                }
              }
              else
              {
                localObject3 = ((CumulateTask)localObject2).in;
              }
              ((CumulateTask)localObject2).out = localObject3;
              for (;;)
              {
                if ((localObject5 = (CumulateTask)((CumulateTask)localObject2).getCompleter()) == null)
                {
                  if ((n & 0x4) != 0) {
                    ((CumulateTask)localObject2).quietlyComplete();
                  }
                }
                else
                {
                  int i3 = ((CumulateTask)localObject5).getPendingCount();
                  if ((i3 & n & 0x4) != 0)
                  {
                    localObject2 = localObject5;
                  }
                  else if ((i3 & n & 0x2) != 0)
                  {
                    CumulateTask localCumulateTask3;
                    CumulateTask localCumulateTask4;
                    if (((localCumulateTask3 = ((CumulateTask)localObject5).left) != null) && ((localCumulateTask4 = ((CumulateTask)localObject5).right) != null))
                    {
                      Object localObject8 = localCumulateTask3.out;
                      ((CumulateTask)localObject5).out = (localCumulateTask4.hi == k ? localObject8 : localBinaryOperator.apply(localObject8, localCumulateTask4.out));
                    }
                    int i6 = ((i3 & 0x1) == 0) && (((CumulateTask)localObject5).lo == j) ? 1 : 0;
                    if (((i4 = i3 | n | i6) == i3) || (((CumulateTask)localObject5).compareAndSetPendingCount(i3, i4)))
                    {
                      n = 2;
                      localObject2 = localObject5;
                      if (i6 != 0) {
                        ((CumulateTask)localObject5).fork();
                      }
                    }
                  }
                  else
                  {
                    if (((CumulateTask)localObject5).compareAndSetPendingCount(i3, i3 | n)) {
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  static final class DoubleCumulateTask
    extends CountedCompleter<Void>
  {
    final double[] array;
    final DoubleBinaryOperator function;
    DoubleCumulateTask left;
    DoubleCumulateTask right;
    double in;
    double out;
    final int lo;
    final int hi;
    final int origin;
    final int fence;
    final int threshold;
    
    public DoubleCumulateTask(DoubleCumulateTask paramDoubleCumulateTask, DoubleBinaryOperator paramDoubleBinaryOperator, double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    {
      super();
      this.function = paramDoubleBinaryOperator;
      this.array = paramArrayOfDouble;
      this.lo = (this.origin = paramInt1);
      this.hi = (this.fence = paramInt2);
      int i;
      this.threshold = ((i = (paramInt2 - paramInt1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : i);
    }
    
    DoubleCumulateTask(DoubleCumulateTask paramDoubleCumulateTask, DoubleBinaryOperator paramDoubleBinaryOperator, double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super();
      this.function = paramDoubleBinaryOperator;
      this.array = paramArrayOfDouble;
      this.origin = paramInt1;
      this.fence = paramInt2;
      this.threshold = paramInt3;
      this.lo = paramInt4;
      this.hi = paramInt5;
    }
    
    public final void compute()
    {
      DoubleBinaryOperator localDoubleBinaryOperator;
      double[] arrayOfDouble;
      if (((localDoubleBinaryOperator = this.function) == null) || ((arrayOfDouble = this.array) == null)) {
        throw new NullPointerException();
      }
      int i = this.threshold;
      int j = this.origin;
      int k = this.fence;
      Object localObject1 = this;
      int m;
      int n;
      while (((m = ((DoubleCumulateTask)localObject1).lo) >= 0) && ((n = ((DoubleCumulateTask)localObject1).hi) <= arrayOfDouble.length))
      {
        int i6;
        if (n - m > i)
        {
          DoubleCumulateTask localDoubleCumulateTask1 = ((DoubleCumulateTask)localObject1).left;
          DoubleCumulateTask localDoubleCumulateTask2 = ((DoubleCumulateTask)localObject1).right;
          Object localObject2;
          if (localDoubleCumulateTask1 == null)
          {
            int i3 = m + n >>> 1;
            localObject2 = localDoubleCumulateTask2 = ((DoubleCumulateTask)localObject1).right = new DoubleCumulateTask((DoubleCumulateTask)localObject1, localDoubleBinaryOperator, arrayOfDouble, j, k, i, i3, n);
            localObject1 = localDoubleCumulateTask1 = ((DoubleCumulateTask)localObject1).left = new DoubleCumulateTask((DoubleCumulateTask)localObject1, localDoubleBinaryOperator, arrayOfDouble, j, k, i, m, i3);
          }
          else
          {
            double d2 = ((DoubleCumulateTask)localObject1).in;
            localDoubleCumulateTask1.in = d2;
            localObject2 = localObject1 = null;
            if (localDoubleCumulateTask2 != null)
            {
              double d3 = localDoubleCumulateTask1.out;
              localDoubleCumulateTask2.in = (m == j ? d3 : localDoubleBinaryOperator.applyAsDouble(d2, d3));
              int i7;
              while (((i7 = localDoubleCumulateTask2.getPendingCount()) & 0x1) == 0) {
                if (localDoubleCumulateTask2.compareAndSetPendingCount(i7, i7 | 0x1)) {
                  localObject1 = localDoubleCumulateTask2;
                }
              }
            }
            while (((i6 = localDoubleCumulateTask1.getPendingCount()) & 0x1) == 0) {
              if (localDoubleCumulateTask1.compareAndSetPendingCount(i6, i6 | 0x1))
              {
                if (localObject1 != null) {
                  localObject2 = localObject1;
                }
                localObject1 = localDoubleCumulateTask1;
              }
            }
            if (localObject1 == null) {
              break;
            }
          }
          if (localObject2 != null) {
            ((DoubleCumulateTask)localObject2).fork();
          }
        }
        else
        {
          int i2;
          while (((i2 = ((DoubleCumulateTask)localObject1).getPendingCount()) & 0x4) == 0)
          {
            int i1 = m > j ? 2 : (i2 & 0x1) != 0 ? 4 : 6;
            if (((DoubleCumulateTask)localObject1).compareAndSetPendingCount(i2, i2 | i1))
            {
              double d1;
              int i4;
              int i5;
              if (i1 != 2)
              {
                if (m == j)
                {
                  d1 = arrayOfDouble[j];
                  i4 = j + 1;
                }
                else
                {
                  d1 = ((DoubleCumulateTask)localObject1).in;
                  i4 = m;
                }
                for (i5 = i4; i5 < n; i5++)
                {
                  double tmp461_456 = localDoubleBinaryOperator.applyAsDouble(d1, arrayOfDouble[i5]);
                  d1 = tmp461_456;
                  arrayOfDouble[i5] = tmp461_456;
                }
              }
              else if (n < k)
              {
                d1 = arrayOfDouble[m];
                for (i4 = m + 1; i4 < n; i4++) {
                  d1 = localDoubleBinaryOperator.applyAsDouble(d1, arrayOfDouble[i4]);
                }
              }
              else
              {
                d1 = ((DoubleCumulateTask)localObject1).in;
              }
              ((DoubleCumulateTask)localObject1).out = d1;
              for (;;)
              {
                DoubleCumulateTask localDoubleCumulateTask3;
                if ((localDoubleCumulateTask3 = (DoubleCumulateTask)((DoubleCumulateTask)localObject1).getCompleter()) == null)
                {
                  if ((i1 & 0x4) != 0) {
                    ((DoubleCumulateTask)localObject1).quietlyComplete();
                  }
                }
                else
                {
                  i5 = localDoubleCumulateTask3.getPendingCount();
                  if ((i5 & i1 & 0x4) != 0)
                  {
                    localObject1 = localDoubleCumulateTask3;
                  }
                  else if ((i5 & i1 & 0x2) != 0)
                  {
                    DoubleCumulateTask localDoubleCumulateTask4;
                    DoubleCumulateTask localDoubleCumulateTask5;
                    if (((localDoubleCumulateTask4 = localDoubleCumulateTask3.left) != null) && ((localDoubleCumulateTask5 = localDoubleCumulateTask3.right) != null))
                    {
                      double d4 = localDoubleCumulateTask4.out;
                      localDoubleCumulateTask3.out = (localDoubleCumulateTask5.hi == k ? d4 : localDoubleBinaryOperator.applyAsDouble(d4, localDoubleCumulateTask5.out));
                    }
                    int i8 = ((i5 & 0x1) == 0) && (localDoubleCumulateTask3.lo == j) ? 1 : 0;
                    if (((i6 = i5 | i1 | i8) == i5) || (localDoubleCumulateTask3.compareAndSetPendingCount(i5, i6)))
                    {
                      i1 = 2;
                      localObject1 = localDoubleCumulateTask3;
                      if (i8 != 0) {
                        localDoubleCumulateTask3.fork();
                      }
                    }
                  }
                  else
                  {
                    if (localDoubleCumulateTask3.compareAndSetPendingCount(i5, i5 | i1)) {
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  static final class IntCumulateTask
    extends CountedCompleter<Void>
  {
    final int[] array;
    final IntBinaryOperator function;
    IntCumulateTask left;
    IntCumulateTask right;
    int in;
    int out;
    final int lo;
    final int hi;
    final int origin;
    final int fence;
    final int threshold;
    
    public IntCumulateTask(IntCumulateTask paramIntCumulateTask, IntBinaryOperator paramIntBinaryOperator, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      super();
      this.function = paramIntBinaryOperator;
      this.array = paramArrayOfInt;
      this.lo = (this.origin = paramInt1);
      this.hi = (this.fence = paramInt2);
      int i;
      this.threshold = ((i = (paramInt2 - paramInt1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : i);
    }
    
    IntCumulateTask(IntCumulateTask paramIntCumulateTask, IntBinaryOperator paramIntBinaryOperator, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super();
      this.function = paramIntBinaryOperator;
      this.array = paramArrayOfInt;
      this.origin = paramInt1;
      this.fence = paramInt2;
      this.threshold = paramInt3;
      this.lo = paramInt4;
      this.hi = paramInt5;
    }
    
    public final void compute()
    {
      IntBinaryOperator localIntBinaryOperator;
      int[] arrayOfInt;
      if (((localIntBinaryOperator = this.function) == null) || ((arrayOfInt = this.array) == null)) {
        throw new NullPointerException();
      }
      int i = this.threshold;
      int j = this.origin;
      int k = this.fence;
      Object localObject1 = this;
      int m;
      int n;
      while (((m = ((IntCumulateTask)localObject1).lo) >= 0) && ((n = ((IntCumulateTask)localObject1).hi) <= arrayOfInt.length))
      {
        int i4;
        int i5;
        if (n - m > i)
        {
          IntCumulateTask localIntCumulateTask1 = ((IntCumulateTask)localObject1).left;
          IntCumulateTask localIntCumulateTask2 = ((IntCumulateTask)localObject1).right;
          Object localObject2;
          if (localIntCumulateTask1 == null)
          {
            i4 = m + n >>> 1;
            localObject2 = localIntCumulateTask2 = ((IntCumulateTask)localObject1).right = new IntCumulateTask((IntCumulateTask)localObject1, localIntBinaryOperator, arrayOfInt, j, k, i, i4, n);
            localObject1 = localIntCumulateTask1 = ((IntCumulateTask)localObject1).left = new IntCumulateTask((IntCumulateTask)localObject1, localIntBinaryOperator, arrayOfInt, j, k, i, m, i4);
          }
          else
          {
            i4 = ((IntCumulateTask)localObject1).in;
            localIntCumulateTask1.in = i4;
            localObject2 = localObject1 = null;
            if (localIntCumulateTask2 != null)
            {
              i5 = localIntCumulateTask1.out;
              localIntCumulateTask2.in = (m == j ? i5 : localIntBinaryOperator.applyAsInt(i4, i5));
              int i6;
              while (((i6 = localIntCumulateTask2.getPendingCount()) & 0x1) == 0) {
                if (localIntCumulateTask2.compareAndSetPendingCount(i6, i6 | 0x1)) {
                  localObject1 = localIntCumulateTask2;
                }
              }
            }
            while (((i5 = localIntCumulateTask1.getPendingCount()) & 0x1) == 0) {
              if (localIntCumulateTask1.compareAndSetPendingCount(i5, i5 | 0x1))
              {
                if (localObject1 != null) {
                  localObject2 = localObject1;
                }
                localObject1 = localIntCumulateTask1;
              }
            }
            if (localObject1 == null) {
              break;
            }
          }
          if (localObject2 != null) {
            ((IntCumulateTask)localObject2).fork();
          }
        }
        else
        {
          int i2;
          while (((i2 = ((IntCumulateTask)localObject1).getPendingCount()) & 0x4) == 0)
          {
            int i1 = m > j ? 2 : (i2 & 0x1) != 0 ? 4 : 6;
            if (((IntCumulateTask)localObject1).compareAndSetPendingCount(i2, i2 | i1))
            {
              int i3;
              if (i1 != 2)
              {
                if (m == j)
                {
                  i2 = arrayOfInt[j];
                  i3 = j + 1;
                }
                else
                {
                  i2 = ((IntCumulateTask)localObject1).in;
                  i3 = m;
                }
                for (i4 = i3; i4 < n; i4++)
                {
                  int tmp461_456 = localIntBinaryOperator.applyAsInt(i2, arrayOfInt[i4]);
                  i2 = tmp461_456;
                  arrayOfInt[i4] = tmp461_456;
                }
              }
              else if (n < k)
              {
                i2 = arrayOfInt[m];
                for (i3 = m + 1; i3 < n; i3++) {
                  i2 = localIntBinaryOperator.applyAsInt(i2, arrayOfInt[i3]);
                }
              }
              else
              {
                i2 = ((IntCumulateTask)localObject1).in;
              }
              ((IntCumulateTask)localObject1).out = i2;
              for (;;)
              {
                IntCumulateTask localIntCumulateTask3;
                if ((localIntCumulateTask3 = (IntCumulateTask)((IntCumulateTask)localObject1).getCompleter()) == null)
                {
                  if ((i1 & 0x4) != 0) {
                    ((IntCumulateTask)localObject1).quietlyComplete();
                  }
                }
                else
                {
                  i4 = localIntCumulateTask3.getPendingCount();
                  if ((i4 & i1 & 0x4) != 0)
                  {
                    localObject1 = localIntCumulateTask3;
                  }
                  else if ((i4 & i1 & 0x2) != 0)
                  {
                    IntCumulateTask localIntCumulateTask4;
                    IntCumulateTask localIntCumulateTask5;
                    if (((localIntCumulateTask4 = localIntCumulateTask3.left) != null) && ((localIntCumulateTask5 = localIntCumulateTask3.right) != null))
                    {
                      i7 = localIntCumulateTask4.out;
                      localIntCumulateTask3.out = (localIntCumulateTask5.hi == k ? i7 : localIntBinaryOperator.applyAsInt(i7, localIntCumulateTask5.out));
                    }
                    int i7 = ((i4 & 0x1) == 0) && (localIntCumulateTask3.lo == j) ? 1 : 0;
                    if (((i5 = i4 | i1 | i7) == i4) || (localIntCumulateTask3.compareAndSetPendingCount(i4, i5)))
                    {
                      i1 = 2;
                      localObject1 = localIntCumulateTask3;
                      if (i7 != 0) {
                        localIntCumulateTask3.fork();
                      }
                    }
                  }
                  else
                  {
                    if (localIntCumulateTask3.compareAndSetPendingCount(i4, i4 | i1)) {
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  static final class LongCumulateTask
    extends CountedCompleter<Void>
  {
    final long[] array;
    final LongBinaryOperator function;
    LongCumulateTask left;
    LongCumulateTask right;
    long in;
    long out;
    final int lo;
    final int hi;
    final int origin;
    final int fence;
    final int threshold;
    
    public LongCumulateTask(LongCumulateTask paramLongCumulateTask, LongBinaryOperator paramLongBinaryOperator, long[] paramArrayOfLong, int paramInt1, int paramInt2)
    {
      super();
      this.function = paramLongBinaryOperator;
      this.array = paramArrayOfLong;
      this.lo = (this.origin = paramInt1);
      this.hi = (this.fence = paramInt2);
      int i;
      this.threshold = ((i = (paramInt2 - paramInt1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : i);
    }
    
    LongCumulateTask(LongCumulateTask paramLongCumulateTask, LongBinaryOperator paramLongBinaryOperator, long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super();
      this.function = paramLongBinaryOperator;
      this.array = paramArrayOfLong;
      this.origin = paramInt1;
      this.fence = paramInt2;
      this.threshold = paramInt3;
      this.lo = paramInt4;
      this.hi = paramInt5;
    }
    
    public final void compute()
    {
      LongBinaryOperator localLongBinaryOperator;
      long[] arrayOfLong;
      if (((localLongBinaryOperator = this.function) == null) || ((arrayOfLong = this.array) == null)) {
        throw new NullPointerException();
      }
      int i = this.threshold;
      int j = this.origin;
      int k = this.fence;
      Object localObject1 = this;
      int m;
      int n;
      while (((m = ((LongCumulateTask)localObject1).lo) >= 0) && ((n = ((LongCumulateTask)localObject1).hi) <= arrayOfLong.length))
      {
        int i6;
        if (n - m > i)
        {
          LongCumulateTask localLongCumulateTask1 = ((LongCumulateTask)localObject1).left;
          LongCumulateTask localLongCumulateTask2 = ((LongCumulateTask)localObject1).right;
          Object localObject2;
          if (localLongCumulateTask1 == null)
          {
            int i3 = m + n >>> 1;
            localObject2 = localLongCumulateTask2 = ((LongCumulateTask)localObject1).right = new LongCumulateTask((LongCumulateTask)localObject1, localLongBinaryOperator, arrayOfLong, j, k, i, i3, n);
            localObject1 = localLongCumulateTask1 = ((LongCumulateTask)localObject1).left = new LongCumulateTask((LongCumulateTask)localObject1, localLongBinaryOperator, arrayOfLong, j, k, i, m, i3);
          }
          else
          {
            long l2 = ((LongCumulateTask)localObject1).in;
            localLongCumulateTask1.in = l2;
            localObject2 = localObject1 = null;
            if (localLongCumulateTask2 != null)
            {
              long l3 = localLongCumulateTask1.out;
              localLongCumulateTask2.in = (m == j ? l3 : localLongBinaryOperator.applyAsLong(l2, l3));
              int i7;
              while (((i7 = localLongCumulateTask2.getPendingCount()) & 0x1) == 0) {
                if (localLongCumulateTask2.compareAndSetPendingCount(i7, i7 | 0x1)) {
                  localObject1 = localLongCumulateTask2;
                }
              }
            }
            while (((i6 = localLongCumulateTask1.getPendingCount()) & 0x1) == 0) {
              if (localLongCumulateTask1.compareAndSetPendingCount(i6, i6 | 0x1))
              {
                if (localObject1 != null) {
                  localObject2 = localObject1;
                }
                localObject1 = localLongCumulateTask1;
              }
            }
            if (localObject1 == null) {
              break;
            }
          }
          if (localObject2 != null) {
            ((LongCumulateTask)localObject2).fork();
          }
        }
        else
        {
          int i2;
          while (((i2 = ((LongCumulateTask)localObject1).getPendingCount()) & 0x4) == 0)
          {
            int i1 = m > j ? 2 : (i2 & 0x1) != 0 ? 4 : 6;
            if (((LongCumulateTask)localObject1).compareAndSetPendingCount(i2, i2 | i1))
            {
              long l1;
              int i4;
              int i5;
              if (i1 != 2)
              {
                if (m == j)
                {
                  l1 = arrayOfLong[j];
                  i4 = j + 1;
                }
                else
                {
                  l1 = ((LongCumulateTask)localObject1).in;
                  i4 = m;
                }
                for (i5 = i4; i5 < n; i5++)
                {
                  long tmp461_456 = localLongBinaryOperator.applyAsLong(l1, arrayOfLong[i5]);
                  l1 = tmp461_456;
                  arrayOfLong[i5] = tmp461_456;
                }
              }
              else if (n < k)
              {
                l1 = arrayOfLong[m];
                for (i4 = m + 1; i4 < n; i4++) {
                  l1 = localLongBinaryOperator.applyAsLong(l1, arrayOfLong[i4]);
                }
              }
              else
              {
                l1 = ((LongCumulateTask)localObject1).in;
              }
              ((LongCumulateTask)localObject1).out = l1;
              for (;;)
              {
                LongCumulateTask localLongCumulateTask3;
                if ((localLongCumulateTask3 = (LongCumulateTask)((LongCumulateTask)localObject1).getCompleter()) == null)
                {
                  if ((i1 & 0x4) != 0) {
                    ((LongCumulateTask)localObject1).quietlyComplete();
                  }
                }
                else
                {
                  i5 = localLongCumulateTask3.getPendingCount();
                  if ((i5 & i1 & 0x4) != 0)
                  {
                    localObject1 = localLongCumulateTask3;
                  }
                  else if ((i5 & i1 & 0x2) != 0)
                  {
                    LongCumulateTask localLongCumulateTask4;
                    LongCumulateTask localLongCumulateTask5;
                    if (((localLongCumulateTask4 = localLongCumulateTask3.left) != null) && ((localLongCumulateTask5 = localLongCumulateTask3.right) != null))
                    {
                      long l4 = localLongCumulateTask4.out;
                      localLongCumulateTask3.out = (localLongCumulateTask5.hi == k ? l4 : localLongBinaryOperator.applyAsLong(l4, localLongCumulateTask5.out));
                    }
                    int i8 = ((i5 & 0x1) == 0) && (localLongCumulateTask3.lo == j) ? 1 : 0;
                    if (((i6 = i5 | i1 | i8) == i5) || (localLongCumulateTask3.compareAndSetPendingCount(i5, i6)))
                    {
                      i1 = 2;
                      localObject1 = localLongCumulateTask3;
                      if (i8 != 0) {
                        localLongCumulateTask3.fork();
                      }
                    }
                  }
                  else
                  {
                    if (localLongCumulateTask3.compareAndSetPendingCount(i5, i5 | i1)) {
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
