package org.omg.PortableServer;

import org.omg.CORBA.PolicyOperations;

public abstract interface LifespanPolicyOperations
  extends PolicyOperations
{
  public abstract LifespanPolicyValue value();
}
