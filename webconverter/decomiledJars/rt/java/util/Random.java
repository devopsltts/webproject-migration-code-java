package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.misc.Unsafe;

public class Random
  implements Serializable
{
  static final long serialVersionUID = 3905348978240129619L;
  private final AtomicLong seed;
  private static final long multiplier = 25214903917L;
  private static final long addend = 11L;
  private static final long mask = 281474976710655L;
  private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
  static final String BadBound = "bound must be positive";
  static final String BadRange = "bound must be greater than origin";
  static final String BadSize = "size must be non-negative";
  private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
  private double nextNextGaussian;
  private boolean haveNextNextGaussian = false;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("seed", Long.TYPE), new ObjectStreamField("nextNextGaussian", Double.TYPE), new ObjectStreamField("haveNextNextGaussian", Boolean.TYPE) };
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  private static final long seedOffset;
  
  public Random()
  {
    this(seedUniquifier() ^ System.nanoTime());
  }
  
  private static long seedUniquifier()
  {
    for (;;)
    {
      long l1 = seedUniquifier.get();
      long l2 = l1 * 181783497276652981L;
      if (seedUniquifier.compareAndSet(l1, l2)) {
        return l2;
      }
    }
  }
  
  public Random(long paramLong)
  {
    if (getClass() == Random.class)
    {
      this.seed = new AtomicLong(initialScramble(paramLong));
    }
    else
    {
      this.seed = new AtomicLong();
      setSeed(paramLong);
    }
  }
  
  private static long initialScramble(long paramLong)
  {
    return (paramLong ^ 0x5DEECE66D) & 0xFFFFFFFFFFFF;
  }
  
  public synchronized void setSeed(long paramLong)
  {
    this.seed.set(initialScramble(paramLong));
    this.haveNextNextGaussian = false;
  }
  
  protected int next(int paramInt)
  {
    AtomicLong localAtomicLong = this.seed;
    long l1;
    long l2;
    do
    {
      l1 = localAtomicLong.get();
      l2 = l1 * 25214903917L + 11L & 0xFFFFFFFFFFFF;
    } while (!localAtomicLong.compareAndSet(l1, l2));
    return (int)(l2 >>> 48 - paramInt);
  }
  
  public void nextBytes(byte[] paramArrayOfByte)
  {
    int i = 0;
    int j = paramArrayOfByte.length;
    while (i < j)
    {
      int k = nextInt();
      int m = Math.min(j - i, 4);
      while (m-- > 0)
      {
        paramArrayOfByte[(i++)] = ((byte)k);
        k >>= 8;
      }
    }
  }
  
  final long internalNextLong(long paramLong1, long paramLong2)
  {
    long l1 = nextLong();
    if (paramLong1 < paramLong2)
    {
      long l2 = paramLong2 - paramLong1;
      long l3 = l2 - 1L;
      if ((l2 & l3) == 0L)
      {
        l1 = (l1 & l3) + paramLong1;
      }
      else if (l2 > 0L)
      {
        for (long l4 = l1 >>> 1; l4 + l3 - (l1 = l4 % l2) < 0L; l4 = nextLong() >>> 1) {}
        l1 += paramLong1;
      }
      else
      {
        while ((l1 < paramLong1) || (l1 >= paramLong2)) {
          l1 = nextLong();
        }
      }
    }
    return l1;
  }
  
  final int internalNextInt(int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2)
    {
      int i = paramInt2 - paramInt1;
      if (i > 0) {
        return nextInt(i) + paramInt1;
      }
      int j;
      do
      {
        j = nextInt();
      } while ((j < paramInt1) || (j >= paramInt2));
      return j;
    }
    return nextInt();
  }
  
  final double internalNextDouble(double paramDouble1, double paramDouble2)
  {
    double d = nextDouble();
    if (paramDouble1 < paramDouble2)
    {
      d = d * (paramDouble2 - paramDouble1) + paramDouble1;
      if (d >= paramDouble2) {
        d = Double.longBitsToDouble(Double.doubleToLongBits(paramDouble2) - 1L);
      }
    }
    return d;
  }
  
  public int nextInt()
  {
    return next(32);
  }
  
  public int nextInt(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("bound must be positive");
    }
    int i = next(31);
    int j = paramInt - 1;
    if ((paramInt & j) == 0) {
      i = (int)(paramInt * i >> 31);
    } else {
      for (int k = i; k - (i = k % paramInt) + j < 0; k = next(31)) {}
    }
    return i;
  }
  
  public long nextLong()
  {
    return (next(32) << 32) + next(32);
  }
  
  public boolean nextBoolean()
  {
    return next(1) != 0;
  }
  
  public float nextFloat()
  {
    return next(24) / 1.6777216E7F;
  }
  
  public double nextDouble()
  {
    return ((next(26) << 27) + next(27)) * 1.1102230246251565E-16D;
  }
  
  public synchronized double nextGaussian()
  {
    if (this.haveNextNextGaussian)
    {
      this.haveNextNextGaussian = false;
      return this.nextNextGaussian;
    }
    double d1;
    double d2;
    double d3;
    do
    {
      d1 = 2.0D * nextDouble() - 1.0D;
      d2 = 2.0D * nextDouble() - 1.0D;
      d3 = d1 * d1 + d2 * d2;
    } while ((d3 >= 1.0D) || (d3 == 0.0D));
    double d4 = StrictMath.sqrt(-2.0D * StrictMath.log(d3) / d3);
    this.nextNextGaussian = (d2 * d4);
    this.haveNextNextGaussian = true;
    return d1 * d4;
  }
  
  public IntStream ints(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, paramLong, Integer.MAX_VALUE, 0), false);
  }
  
  public IntStream ints()
  {
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false);
  }
  
  public IntStream ints(long paramLong, int paramInt1, int paramInt2)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, paramLong, paramInt1, paramInt2), false);
  }
  
  public IntStream ints(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, Long.MAX_VALUE, paramInt1, paramInt2), false);
  }
  
  public LongStream longs(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, paramLong, Long.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs()
  {
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    if (paramLong2 >= paramLong3) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, paramLong1, paramLong2, paramLong3), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2)
  {
    if (paramLong1 >= paramLong2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, Long.MAX_VALUE, paramLong1, paramLong2), false);
  }
  
  public DoubleStream doubles(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, paramLong, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles()
  {
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles(long paramLong, double paramDouble1, double paramDouble2)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    if (paramDouble1 >= paramDouble2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, paramLong, paramDouble1, paramDouble2), false);
  }
  
  public DoubleStream doubles(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 >= paramDouble2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, Long.MAX_VALUE, paramDouble1, paramDouble2), false);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    long l = localGetField.get("seed", -1L);
    if (l < 0L) {
      throw new StreamCorruptedException("Random: invalid seed");
    }
    resetSeed(l);
    this.nextNextGaussian = localGetField.get("nextNextGaussian", 0.0D);
    this.haveNextNextGaussian = localGetField.get("haveNextNextGaussian", false);
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("seed", this.seed.get());
    localPutField.put("nextNextGaussian", this.nextNextGaussian);
    localPutField.put("haveNextNextGaussian", this.haveNextNextGaussian);
    paramObjectOutputStream.writeFields();
  }
  
  private void resetSeed(long paramLong)
  {
    unsafe.putObjectVolatile(this, seedOffset, new AtomicLong(paramLong));
  }
  
  static
  {
    try
    {
      seedOffset = unsafe.objectFieldOffset(Random.class.getDeclaredField("seed"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class RandomDoublesSpliterator
    implements Spliterator.OfDouble
  {
    final Random rng;
    long index;
    final long fence;
    final double origin;
    final double bound;
    
    RandomDoublesSpliterator(Random paramRandom, long paramLong1, long paramLong2, double paramDouble1, double paramDouble2)
    {
      this.rng = paramRandom;
      this.index = paramLong1;
      this.fence = paramLong2;
      this.origin = paramDouble1;
      this.bound = paramDouble2;
    }
    
    public RandomDoublesSpliterator trySplit()
    {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> 1;
      return l2 <= l1 ? null : new RandomDoublesSpliterator(this.rng, l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return 17728;
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2)
      {
        paramDoubleConsumer.accept(this.rng.internalNextDouble(this.origin, this.bound));
        this.index = (l1 + 1L);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2)
      {
        this.index = l2;
        Random localRandom = this.rng;
        double d1 = this.origin;
        double d2 = this.bound;
        do
        {
          paramDoubleConsumer.accept(localRandom.internalNextDouble(d1, d2));
        } while (++l1 < l2);
      }
    }
  }
  
  static final class RandomIntsSpliterator
    implements Spliterator.OfInt
  {
    final Random rng;
    long index;
    final long fence;
    final int origin;
    final int bound;
    
    RandomIntsSpliterator(Random paramRandom, long paramLong1, long paramLong2, int paramInt1, int paramInt2)
    {
      this.rng = paramRandom;
      this.index = paramLong1;
      this.fence = paramLong2;
      this.origin = paramInt1;
      this.bound = paramInt2;
    }
    
    public RandomIntsSpliterator trySplit()
    {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> 1;
      return l2 <= l1 ? null : new RandomIntsSpliterator(this.rng, l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return 17728;
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2)
      {
        paramIntConsumer.accept(this.rng.internalNextInt(this.origin, this.bound));
        this.index = (l1 + 1L);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2)
      {
        this.index = l2;
        Random localRandom = this.rng;
        int i = this.origin;
        int j = this.bound;
        do
        {
          paramIntConsumer.accept(localRandom.internalNextInt(i, j));
        } while (++l1 < l2);
      }
    }
  }
  
  static final class RandomLongsSpliterator
    implements Spliterator.OfLong
  {
    final Random rng;
    long index;
    final long fence;
    final long origin;
    final long bound;
    
    RandomLongsSpliterator(Random paramRandom, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      this.rng = paramRandom;
      this.index = paramLong1;
      this.fence = paramLong2;
      this.origin = paramLong3;
      this.bound = paramLong4;
    }
    
    public RandomLongsSpliterator trySplit()
    {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> 1;
      return l2 <= l1 ? null : new RandomLongsSpliterator(this.rng, l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize()
    {
      return this.fence - this.index;
    }
    
    public int characteristics()
    {
      return 17728;
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2)
      {
        paramLongConsumer.accept(this.rng.internalNextLong(this.origin, this.bound));
        this.index = (l1 + 1L);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2)
      {
        this.index = l2;
        Random localRandom = this.rng;
        long l3 = this.origin;
        long l4 = this.bound;
        do
        {
          paramLongConsumer.accept(localRandom.internalNextLong(l3, l4));
        } while (++l1 < l2);
      }
    }
  }
}
