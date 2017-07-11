package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface ActivationMonitor
  extends Remote
{
  public abstract void inactiveObject(ActivationID paramActivationID)
    throws UnknownObjectException, RemoteException;
  
  public abstract void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
    throws UnknownObjectException, RemoteException;
  
  public abstract void inactiveGroup(ActivationGroupID paramActivationGroupID, long paramLong)
    throws UnknownGroupException, RemoteException;
}
