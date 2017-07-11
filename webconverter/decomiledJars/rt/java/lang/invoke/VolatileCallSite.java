package java.lang.invoke;

public class VolatileCallSite
  extends CallSite
{
  public VolatileCallSite(MethodType paramMethodType)
  {
    super(paramMethodType);
  }
  
  public VolatileCallSite(MethodHandle paramMethodHandle)
  {
    super(paramMethodHandle);
  }
  
  public final MethodHandle getTarget()
  {
    return getTargetVolatile();
  }
  
  public void setTarget(MethodHandle paramMethodHandle)
  {
    checkTargetChange(getTargetVolatile(), paramMethodHandle);
    setTargetVolatile(paramMethodHandle);
  }
  
  public final MethodHandle dynamicInvoker()
  {
    return makeDynamicInvoker();
  }
}
