package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_NR_UDS
  extends POAPolicyMediatorBase
{
  private Servant defaultServant;
  
  POAPolicyMediatorImpl_NR_UDS(Policies paramPolicies, POAImpl paramPOAImpl)
  {
    super(paramPolicies, paramPOAImpl);
    if (paramPolicies.retainServants()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
    if (!paramPolicies.useDefaultServant()) {
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
    }
    this.defaultServant = null;
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString)
    throws ForwardRequest
  {
    if (this.defaultServant == null) {
      throw this.poa.invocationWrapper().poaNoDefaultServant();
    }
    return this.defaultServant;
  }
  
  public void returnServant() {}
  
  public void etherealizeAll() {}
  
  public void clearAOM() {}
  
  public ServantManager getServantManager()
    throws WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public void setServantManager(ServantManager paramServantManager)
    throws WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public Servant getDefaultServant()
    throws NoServant, WrongPolicy
  {
    if (this.defaultServant == null) {
      throw new NoServant();
    }
    return this.defaultServant;
  }
  
  public void setDefaultServant(Servant paramServant)
    throws WrongPolicy
  {
    this.defaultServant = paramServant;
    setDelegate(this.defaultServant, "DefaultServant".getBytes());
  }
  
  public final void activateObject(byte[] paramArrayOfByte, Servant paramServant)
    throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive
  {
    throw new WrongPolicy();
  }
  
  public Servant deactivateObject(byte[] paramArrayOfByte)
    throws ObjectNotActive, WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public byte[] servantToId(Servant paramServant)
    throws ServantNotActive, WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  public Servant idToServant(byte[] paramArrayOfByte)
    throws WrongPolicy, ObjectNotActive
  {
    if (this.defaultServant != null) {
      return this.defaultServant;
    }
    throw new ObjectNotActive();
  }
}
