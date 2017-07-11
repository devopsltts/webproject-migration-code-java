package java.util.logging;

@FunctionalInterface
public abstract interface Filter
{
  public abstract boolean isLoggable(LogRecord paramLogRecord);
}
