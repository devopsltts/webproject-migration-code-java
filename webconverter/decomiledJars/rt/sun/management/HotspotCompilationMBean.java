package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public abstract interface HotspotCompilationMBean
{
  public abstract int getCompilerThreadCount();
  
  public abstract List<CompilerThreadStat> getCompilerThreadStats();
  
  public abstract long getTotalCompileCount();
  
  public abstract long getBailoutCompileCount();
  
  public abstract long getInvalidatedCompileCount();
  
  public abstract MethodInfo getLastCompile();
  
  public abstract MethodInfo getFailedCompile();
  
  public abstract MethodInfo getInvalidatedCompile();
  
  public abstract long getCompiledMethodCodeSize();
  
  public abstract long getCompiledMethodSize();
  
  public abstract List<Counter> getInternalCompilerCounters();
}
