package jdk.management.resource.internal.inst;

import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;

@InstrumentationTarget("jdk.management.resource.internal.WrapInstrumentation")
public class WrapInstrumentationRMHooks
{
  public WrapInstrumentationRMHooks() {}
  
  @InstrumentationMethod
  public boolean wrapComplete()
  {
    wrapComplete();
    return true;
  }
}
