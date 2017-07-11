package com.sun.jmx.remote.protocol.iiop;

import com.sun.jmx.remote.internal.IIOPProxy;
import java.io.SerializablePermission;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Properties;
import javax.rmi.CORBA.Stub;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;

public class IIOPProxyImpl
  implements IIOPProxy
{
  private static final AccessControlContext STUB_ACC;
  
  public IIOPProxyImpl() {}
  
  public boolean isStub(Object paramObject)
  {
    return paramObject instanceof Stub;
  }
  
  public Object getDelegate(Object paramObject)
  {
    return ((Stub)paramObject)._get_delegate();
  }
  
  public void setDelegate(Object paramObject1, Object paramObject2)
  {
    ((Stub)paramObject1)._set_delegate((Delegate)paramObject2);
  }
  
  public Object getOrb(Object paramObject)
  {
    try
    {
      return ((Stub)paramObject)._orb();
    }
    catch (BAD_OPERATION localBAD_OPERATION)
    {
      throw new UnsupportedOperationException(localBAD_OPERATION);
    }
  }
  
  public void connect(Object paramObject1, Object paramObject2)
    throws RemoteException
  {
    ((Stub)paramObject1).connect((ORB)paramObject2);
  }
  
  public boolean isOrb(Object paramObject)
  {
    return paramObject instanceof ORB;
  }
  
  public Object createOrb(String[] paramArrayOfString, Properties paramProperties)
  {
    return ORB.init(paramArrayOfString, paramProperties);
  }
  
  public Object stringToObject(Object paramObject, String paramString)
  {
    return ((ORB)paramObject).string_to_object(paramString);
  }
  
  public String objectToString(Object paramObject1, Object paramObject2)
  {
    return ((ORB)paramObject1).object_to_string((org.omg.CORBA.Object)paramObject2);
  }
  
  public <T> T narrow(Object paramObject, Class<T> paramClass)
  {
    return PortableRemoteObject.narrow(paramObject, paramClass);
  }
  
  public void exportObject(Remote paramRemote)
    throws RemoteException
  {
    PortableRemoteObject.exportObject(paramRemote);
  }
  
  public void unexportObject(Remote paramRemote)
    throws NoSuchObjectException
  {
    PortableRemoteObject.unexportObject(paramRemote);
  }
  
  public Remote toStub(final Remote paramRemote)
    throws NoSuchObjectException
  {
    if (System.getSecurityManager() == null) {
      return PortableRemoteObject.toStub(paramRemote);
    }
    try
    {
      (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Remote run()
          throws Exception
        {
          return PortableRemoteObject.toStub(paramRemote);
        }
      }, STUB_ACC);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      if ((localPrivilegedActionException.getException() instanceof NoSuchObjectException)) {
        throw ((NoSuchObjectException)localPrivilegedActionException.getException());
      }
      throw new RuntimeException("Unexpected exception type", localPrivilegedActionException.getException());
    }
  }
  
  static
  {
    Permissions localPermissions = new Permissions();
    localPermissions.add(new SerializablePermission("enableSubclassImplementation"));
    STUB_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, localPermissions) });
  }
}
