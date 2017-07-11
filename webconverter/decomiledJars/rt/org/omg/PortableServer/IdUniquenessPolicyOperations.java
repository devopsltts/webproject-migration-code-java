package org.omg.PortableServer;

import org.omg.CORBA.PolicyOperations;

public abstract interface IdUniquenessPolicyOperations
  extends PolicyOperations
{
  public abstract IdUniquenessPolicyValue value();
}
