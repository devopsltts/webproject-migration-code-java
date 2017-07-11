package java.lang.management;

public abstract interface CompilationMXBean
  extends PlatformManagedObject
{
  public abstract String getName();
  
  public abstract boolean isCompilationTimeMonitoringSupported();
  
  public abstract long getTotalCompilationTime();
}
