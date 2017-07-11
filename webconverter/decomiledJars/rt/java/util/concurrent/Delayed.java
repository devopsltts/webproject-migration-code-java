package java.util.concurrent;

public abstract interface Delayed
  extends Comparable<Delayed>
{
  public abstract long getDelay(TimeUnit paramTimeUnit);
}
