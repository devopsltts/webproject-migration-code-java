package java.rmi.activation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import sun.rmi.server.ActivationGroupImpl;
import sun.security.action.GetIntegerAction;

public abstract class ActivationGroup
  extends UnicastRemoteObject
  implements ActivationInstantiator
{
  private ActivationGroupID groupID;
  private ActivationMonitor monitor;
  private long incarnation;
  private static ActivationGroup currGroup;
  private static ActivationGroupID currGroupID;
  private static ActivationSystem currSystem;
  private static boolean canCreate = true;
  private static final long serialVersionUID = -7696947875314805420L;
  
  protected ActivationGroup(ActivationGroupID paramActivationGroupID)
    throws RemoteException
  {
    this.groupID = paramActivationGroupID;
  }
  
  public boolean inactiveObject(ActivationID paramActivationID)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    getMonitor().inactiveObject(paramActivationID);
    return true;
  }
  
  public abstract void activeObject(ActivationID paramActivationID, Remote paramRemote)
    throws ActivationException, UnknownObjectException, RemoteException;
  
  public static synchronized ActivationGroup createGroup(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc, long paramLong)
    throws ActivationException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    if (currGroup != null) {
      throw new ActivationException("group already exists");
    }
    if (!canCreate) {
      throw new ActivationException("group deactivated and cannot be recreated");
    }
    try
    {
      String str = paramActivationGroupDesc.getClassName();
      ActivationGroupImpl localActivationGroupImpl = ActivationGroupImpl.class;
      Object localObject1;
      if ((str == null) || (str.equals(localActivationGroupImpl.getName())))
      {
        localObject1 = localActivationGroupImpl;
      }
      else
      {
        try
        {
          localObject2 = RMIClassLoader.loadClass(paramActivationGroupDesc.getLocation(), str);
        }
        catch (Exception localException2)
        {
          throw new ActivationException("Could not load group implementation class", localException2);
        }
        if (ActivationGroup.class.isAssignableFrom((Class)localObject2)) {
          localObject1 = ((Class)localObject2).asSubclass(ActivationGroup.class);
        } else {
          throw new ActivationException("group not correct class: " + ((Class)localObject2).getName());
        }
      }
      Object localObject2 = ((Class)localObject1).getConstructor(new Class[] { ActivationGroupID.class, MarshalledObject.class });
      ActivationGroup localActivationGroup = (ActivationGroup)((Constructor)localObject2).newInstance(new Object[] { paramActivationGroupID, paramActivationGroupDesc.getData() });
      currSystem = paramActivationGroupID.getSystem();
      localActivationGroup.incarnation = paramLong;
      localActivationGroup.monitor = currSystem.activeGroup(paramActivationGroupID, localActivationGroup, paramLong);
      currGroup = localActivationGroup;
      currGroupID = paramActivationGroupID;
      canCreate = false;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localInvocationTargetException.getTargetException().printStackTrace();
      throw new ActivationException("exception in group constructor", localInvocationTargetException.getTargetException());
    }
    catch (ActivationException localActivationException)
    {
      throw localActivationException;
    }
    catch (Exception localException1)
    {
      throw new ActivationException("exception creating group", localException1);
    }
    return currGroup;
  }
  
  public static synchronized ActivationGroupID currentGroupID()
  {
    return currGroupID;
  }
  
  static synchronized ActivationGroupID internalCurrentGroupID()
    throws ActivationException
  {
    if (currGroupID == null) {
      throw new ActivationException("nonexistent group");
    }
    return currGroupID;
  }
  
  public static synchronized void setSystem(ActivationSystem paramActivationSystem)
    throws ActivationException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    if (currSystem != null) {
      throw new ActivationException("activation system already set");
    }
    currSystem = paramActivationSystem;
  }
  
  public static synchronized ActivationSystem getSystem()
    throws ActivationException
  {
    if (currSystem == null) {
      try
      {
        int i = ((Integer)AccessController.doPrivileged(new GetIntegerAction("java.rmi.activation.port", 1098))).intValue();
        currSystem = (ActivationSystem)Naming.lookup("//:" + i + "/java.rmi.activation.ActivationSystem");
      }
      catch (Exception localException)
      {
        throw new ActivationException("unable to obtain ActivationSystem", localException);
      }
    }
    return currSystem;
  }
  
  protected void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    getMonitor().activeObject(paramActivationID, paramMarshalledObject);
  }
  
  protected void inactiveGroup()
    throws UnknownGroupException, RemoteException
  {
    try
    {
      getMonitor().inactiveGroup(this.groupID, this.incarnation);
      destroyGroup();
    }
    finally
    {
      destroyGroup();
    }
  }
  
  private ActivationMonitor getMonitor()
    throws RemoteException
  {
    synchronized (ActivationGroup.class)
    {
      if (this.monitor != null) {
        return this.monitor;
      }
    }
    throw new RemoteException("monitor not received");
  }
  
  private static synchronized void destroyGroup()
  {
    currGroup = null;
    currGroupID = null;
  }
  
  static synchronized ActivationGroup currentGroup()
    throws ActivationException
  {
    if (currGroup == null) {
      throw new ActivationException("group is not active");
    }
    return currGroup;
  }
}
