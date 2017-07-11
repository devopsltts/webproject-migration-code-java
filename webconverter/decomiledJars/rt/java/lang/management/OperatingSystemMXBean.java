package java.lang.management;

public abstract interface OperatingSystemMXBean
  extends PlatformManagedObject
{
  public abstract String getName();
  
  public abstract String getArch();
  
  public abstract String getVersion();
  
  public abstract int getAvailableProcessors();
  
  public abstract double getSystemLoadAverage();
}
