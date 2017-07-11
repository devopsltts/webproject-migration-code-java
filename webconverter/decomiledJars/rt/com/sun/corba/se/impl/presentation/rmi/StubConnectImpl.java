package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class StubConnectImpl
{
  static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");
  
  public StubConnectImpl() {}
  
  public static StubIORImpl connect(StubIORImpl paramStubIORImpl, org.omg.CORBA.Object paramObject, ObjectImpl paramObjectImpl, ORB paramORB)
    throws RemoteException
  {
    Delegate localDelegate = null;
    try
    {
      try
      {
        localDelegate = StubAdapter.getDelegate(paramObjectImpl);
        if (localDelegate.orb(paramObjectImpl) != paramORB) {
          throw wrapper.connectWrongOrb();
        }
      }
      catch (BAD_OPERATION localBAD_OPERATION1)
      {
        if (paramStubIORImpl == null)
        {
          Tie localTie = Utility.getAndForgetTie(paramObject);
          if (localTie == null) {
            throw wrapper.connectNoTie();
          }
          ORB localORB = paramORB;
          try
          {
            localORB = localTie.orb();
          }
          catch (BAD_OPERATION localBAD_OPERATION2)
          {
            localTie.orb(paramORB);
          }
          catch (BAD_INV_ORDER localBAD_INV_ORDER)
          {
            localTie.orb(paramORB);
          }
          if (localORB != paramORB) {
            throw wrapper.connectTieWrongOrb();
          }
          localDelegate = StubAdapter.getDelegate(localTie);
          CORBAObjectImpl localCORBAObjectImpl = new CORBAObjectImpl();
          localCORBAObjectImpl._set_delegate(localDelegate);
          paramStubIORImpl = new StubIORImpl(localCORBAObjectImpl);
        }
        else
        {
          localDelegate = paramStubIORImpl.getDelegate(paramORB);
        }
        StubAdapter.setDelegate(paramObjectImpl, localDelegate);
      }
    }
    catch (SystemException localSystemException)
    {
      throw new RemoteException("CORBA SystemException", localSystemException);
    }
    return paramStubIORImpl;
  }
}
