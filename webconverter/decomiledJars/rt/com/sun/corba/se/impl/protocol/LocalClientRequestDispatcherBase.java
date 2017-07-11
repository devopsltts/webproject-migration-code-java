package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import org.omg.CORBA.portable.ServantObject;

public abstract class LocalClientRequestDispatcherBase
  implements LocalClientRequestDispatcher
{
  protected ORB orb;
  int scid;
  protected boolean servantIsLocal;
  protected ObjectAdapterFactory oaf;
  protected ObjectAdapterId oaid;
  protected byte[] objectId;
  private static final ThreadLocal isNextCallValid = new ThreadLocal()
  {
    protected synchronized Object initialValue()
    {
      return Boolean.TRUE;
    }
  };
  
  protected LocalClientRequestDispatcherBase(ORB paramORB, int paramInt, IOR paramIOR)
  {
    this.orb = paramORB;
    IIOPProfile localIIOPProfile = paramIOR.getProfile();
    this.servantIsLocal = ((paramORB.getORBData().isLocalOptimizationAllowed()) && (localIIOPProfile.isLocal()));
    ObjectKeyTemplate localObjectKeyTemplate = localIIOPProfile.getObjectKeyTemplate();
    this.scid = localObjectKeyTemplate.getSubcontractId();
    RequestDispatcherRegistry localRequestDispatcherRegistry = paramORB.getRequestDispatcherRegistry();
    this.oaf = localRequestDispatcherRegistry.getObjectAdapterFactory(paramInt);
    this.oaid = localObjectKeyTemplate.getObjectAdapterId();
    ObjectId localObjectId = localIIOPProfile.getObjectId();
    this.objectId = localObjectId.getId();
  }
  
  public byte[] getObjectId()
  {
    return this.objectId;
  }
  
  public boolean is_local(org.omg.CORBA.Object paramObject)
  {
    return false;
  }
  
  public boolean useLocalInvocation(org.omg.CORBA.Object paramObject)
  {
    if (isNextCallValid.get() == Boolean.TRUE) {
      return this.servantIsLocal;
    }
    isNextCallValid.set(Boolean.TRUE);
    return false;
  }
  
  protected boolean checkForCompatibleServant(ServantObject paramServantObject, Class paramClass)
  {
    if (paramServantObject == null) {
      return false;
    }
    if (!paramClass.isInstance(paramServantObject.servant))
    {
      isNextCallValid.set(Boolean.FALSE);
      return false;
    }
    return true;
  }
}
