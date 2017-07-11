package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class FullServantCacheLocalCRDImpl
  extends ServantCacheLocalCRDBase
{
  public FullServantCacheLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
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
    try
    {
      localOAInvocationInfo2.oa().enter();
    }
    catch (OADestroyed localOADestroyed)
    {
      throw this.wrapper.preinvokePoaDestroyed(localOADestroyed);
    }
    return localOAInvocationInfo2;
  }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject)
  {
    OAInvocationInfo localOAInvocationInfo = getCachedInfo();
    localOAInvocationInfo.oa().exit();
    this.orb.popInvocationInfo();
  }
}
