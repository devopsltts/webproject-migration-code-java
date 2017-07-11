package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class InfoOnlyServantCacheLocalCRDImpl
  extends ServantCacheLocalCRDBase
{
  public InfoOnlyServantCacheLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
  }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass)
  {
    OAInvocationInfo localOAInvocationInfo1 = getCachedInfo();
    if (!checkForCompatibleServant(localOAInvocationInfo1, paramClass)) {
      return null;
    }
    OAInvocationInfo localOAInvocationInfo2 = new OAInvocationInfo(localOAInvocationInfo1, paramString);
    this.orb.pushInvocationInfo(localOAInvocationInfo2);
    return localOAInvocationInfo2;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject)
  {
    this.orb.popInvocationInfo();
  }
}
