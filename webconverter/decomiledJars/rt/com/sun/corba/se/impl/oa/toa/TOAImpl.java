package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientDelegate;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.portable.Delegate;
import org.omg.PortableInterceptor.ObjectReferenceFactory;

public class TOAImpl
  extends ObjectAdapterBase
  implements TOA
{
  private TransientObjectManager servants;
  
  public TOAImpl(ORB paramORB, TransientObjectManager paramTransientObjectManager, String paramString)
  {
    super(paramORB);
    this.servants = paramTransientObjectManager;
    int i = getORB().getTransientServerId();
    int j = 2;
    JIDLObjectKeyTemplate localJIDLObjectKeyTemplate = new JIDLObjectKeyTemplate(paramORB, j, i);
    Policies localPolicies = Policies.defaultPolicies;
    initializeTemplate(localJIDLObjectKeyTemplate, true, localPolicies, paramString, null, localJIDLObjectKeyTemplate.getObjectAdapterId());
  }
  
  public ObjectCopierFactory getObjectCopierFactory()
  {
    CopierManager localCopierManager = getORB().getCopierManager();
    return localCopierManager.getDefaultObjectCopierFactory();
  }
  
  public org.omg.CORBA.Object getLocalServant(byte[] paramArrayOfByte)
  {
    return (org.omg.CORBA.Object)this.servants.lookupServant(paramArrayOfByte);
  }
  
  public void getInvocationServant(OAInvocationInfo paramOAInvocationInfo)
  {
    Object localObject = this.servants.lookupServant(paramOAInvocationInfo.id());
    if (localObject == null) {
      localObject = new NullServantImpl(lifecycleWrapper().nullServant());
    }
    paramOAInvocationInfo.setServant(localObject);
  }
  
  public void returnServant() {}
  
  public String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte)
  {
    return StubAdapter.getTypeIds(paramObject);
  }
  
  public Policy getEffectivePolicy(int paramInt)
  {
    return null;
  }
  
  public int getManagerId()
  {
    return -1;
  }
  
  public short getState()
  {
    return 1;
  }
  
  public void enter()
    throws OADestroyed
  {}
  
  public void exit() {}
  
  public void connect(org.omg.CORBA.Object paramObject)
  {
    byte[] arrayOfByte = this.servants.storeServant(paramObject, null);
    String str = StubAdapter.getTypeIds(paramObject)[0];
    ObjectReferenceFactory localObjectReferenceFactory = getCurrentFactory();
    org.omg.CORBA.Object localObject = localObjectReferenceFactory.make_object(str, arrayOfByte);
    Delegate localDelegate = StubAdapter.getDelegate(localObject);
    CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)((ClientDelegate)localDelegate).getContactInfoList();
    LocalClientRequestDispatcher localLocalClientRequestDispatcher = localCorbaContactInfoList.getLocalClientRequestDispatcher();
    if ((localLocalClientRequestDispatcher instanceof JIDLLocalCRDImpl))
    {
      JIDLLocalCRDImpl localJIDLLocalCRDImpl = (JIDLLocalCRDImpl)localLocalClientRequestDispatcher;
      localJIDLLocalCRDImpl.setServant(paramObject);
    }
    else
    {
      throw new RuntimeException("TOAImpl.connect can not be called on " + localLocalClientRequestDispatcher);
    }
    StubAdapter.setDelegate(paramObject, localDelegate);
  }
  
  public void disconnect(org.omg.CORBA.Object paramObject)
  {
    Delegate localDelegate = StubAdapter.getDelegate(paramObject);
    CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)((ClientDelegate)localDelegate).getContactInfoList();
    LocalClientRequestDispatcher localLocalClientRequestDispatcher = localCorbaContactInfoList.getLocalClientRequestDispatcher();
    if ((localLocalClientRequestDispatcher instanceof JIDLLocalCRDImpl))
    {
      JIDLLocalCRDImpl localJIDLLocalCRDImpl = (JIDLLocalCRDImpl)localLocalClientRequestDispatcher;
      byte[] arrayOfByte = localJIDLLocalCRDImpl.getObjectId();
      this.servants.deleteServant(arrayOfByte);
      localJIDLLocalCRDImpl.unexport();
    }
    else
    {
      throw new RuntimeException("TOAImpl.disconnect can not be called on " + localLocalClientRequestDispatcher);
    }
  }
}
