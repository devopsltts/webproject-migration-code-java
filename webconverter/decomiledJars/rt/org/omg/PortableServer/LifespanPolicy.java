package org.omg.PortableServer;

import org.omg.CORBA.Policy;
import org.omg.CORBA.portable.IDLEntity;

public abstract interface LifespanPolicy
  extends LifespanPolicyOperations, Policy, IDLEntity
{}
