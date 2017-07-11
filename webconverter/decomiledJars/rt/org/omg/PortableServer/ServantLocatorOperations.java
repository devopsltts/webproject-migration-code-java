package org.omg.PortableServer;

import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public abstract interface ServantLocatorOperations
  extends ServantManagerOperations
{
  public abstract Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder)
    throws ForwardRequest;
  
  public abstract void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant);
}
