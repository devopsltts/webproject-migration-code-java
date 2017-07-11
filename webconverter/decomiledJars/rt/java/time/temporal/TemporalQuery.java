package java.time.temporal;

@FunctionalInterface
public abstract interface TemporalQuery<R>
{
  public abstract R queryFrom(TemporalAccessor paramTemporalAccessor);
}
