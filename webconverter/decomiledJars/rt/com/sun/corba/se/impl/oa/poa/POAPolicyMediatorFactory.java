package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;

abstract class POAPolicyMediatorFactory
{
  POAPolicyMediatorFactory() {}
  
  static POAPolicyMediator create(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    if (paramPolicies.retainServants())
    {
      if (paramPolicies.useActiveMapOnly()) {
        return new POAPolicyMediatorImpl_R_AOM(paramPolicies, paramPOAImpl);
      }
      if (paramPolicies.useDefaultServant()) {
        return new POAPolicyMediatorImpl_R_UDS(paramPolicies, paramPOAImpl);
      }
      if (paramPolicies.useServantManager()) {
        return new POAPolicyMediatorImpl_R_USM(paramPolicies, paramPOAImpl);
      }
      throw paramPOAImpl.invocationWrapper().pmfCreateRetain();
    }
    if (paramPolicies.useDefaultServant()) {
      return new POAPolicyMediatorImpl_NR_UDS(paramPolicies, paramPOAImpl);
    }
    if (paramPolicies.useServantManager()) {
      return new POAPolicyMediatorImpl_NR_USM(paramPolicies, paramPOAImpl);
    }
    throw paramPOAImpl.invocationWrapper().pmfCreateNonRetain();
  }
}
