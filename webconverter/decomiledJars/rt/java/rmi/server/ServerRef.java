package java.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

@Deprecated
public abstract interface ServerRef
  extends RemoteRef
{
  public static final long serialVersionUID = -4557750989390278438L;
  
  public abstract RemoteStub exportObject(Remote paramRemote, Object paramObject)
    throws RemoteException;
  
  public abstract String getClientHost()
    throws ServerNotActiveException;
}
