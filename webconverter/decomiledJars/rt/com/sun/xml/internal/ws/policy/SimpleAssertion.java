package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class SimpleAssertion
  extends PolicyAssertion
{
  protected SimpleAssertion() {}
  
  protected SimpleAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection)
  {
    super(paramAssertionData, paramCollection);
  }
  
  public final boolean hasNestedPolicy()
  {
    return false;
  }
  
  public final NestedPolicy getNestedPolicy()
  {
    return null;
  }
}
