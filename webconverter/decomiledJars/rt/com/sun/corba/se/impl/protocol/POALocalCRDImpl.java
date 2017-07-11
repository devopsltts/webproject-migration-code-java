package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ServantObject;

public class POALocalCRDImpl
  extends LocalClientRequestDispatcherBase
{
  private ORBUtilSystemException wrapper;
  private POASystemException poaWrapper;
  
  public POALocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.poaWrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  private OAInvocationInfo servantEnter(ObjectAdapter paramObjectAdapter)
    throws OADestroyed
  {
    paramObjectAdapter.enter();
    OAInvocationInfo localOAInvocationInfo = paramObjectAdapter.makeInvocationInfo(this.objectId);
    this.orb.pushInvocationInfo(localOAInvocationInfo);
    return localOAInvocationInfo;
  }
  
  private void servantExit(ObjectAdapter paramObjectAdapter)
  {
    try
    {
      paramObjectAdapter.returnServant();
      paramObjectAdapter.exit();
      this.orb.popInvocationInfo();
    }
    finally
    {
      paramObjectAdapter.exit();
      this.orb.popInvocationInfo();
    }
  }
  
  public ServantObject servant_preinvoke(org.omg.CORBA.Object paramObject, String paramString, Class paramClass)
  {
    ObjectAdapter localObjectAdapter = this.oaf.find(this.oaid);
    OAInvocationInfo localOAInvocationInfo = null;
    try
    {
      localOAInvocationInfo = servantEnter(localObjectAdapter);
      localOAInvocationInfo.setOperation(paramString);
    }
    catch (OADestroyed localOADestroyed)
    {
      return servant_preinvoke(paramObject, paramString, paramClass);
    }
    try
    {
      try
      {
        localObjectAdapter.getInvocationServant(localOAInvocationInfo);
        if (!checkForCompatibleServant(localOAInvocationInfo, paramClass)) {
          return null;
        }
      }
      catch (Throwable localThrowable1)
      {
        servantExit(localObjectAdapter);
        throw localThrowable1;
      }
    }
    catch (ForwardException localForwardException)
    {
      RuntimeException localRuntimeException = new RuntimeException("deal with this.");
      localRuntimeException.initCause(localForwardException);
      throw localRuntimeException;
    }
    catch (ThreadDeath localThreadDeath)
    {
      throw this.wrapper.runtimeexception(localThreadDeath);
    }
    catch (Throwable localThrowable2)
    {
      if ((localThrowable2 instanceof SystemException)) {
        throw ((SystemException)localThrowable2);
      }
      throw this.poaWrapper.localServantLookup(localThrowable2);
    }
    if (!checkForCompatibleServant(localOAInvocationInfo, paramClass))
    {
      servantExit(localObjectAdapter);
      return null;
    }
    return localOAInvocationInfo;
  }
  
  public void servant_postinvoke(org.omg.CORBA.Object paramObject, ServantObject paramServantObject)
  {
    ObjectAdapter localObjectAdapter = this.orb.peekInvocationInfo().oa();
    servantExit(localObjectAdapter);
  }
}
