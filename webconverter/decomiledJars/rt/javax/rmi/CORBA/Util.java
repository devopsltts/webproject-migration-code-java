package javax.rmi.CORBA;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Util
{
  private static final UtilDelegate utilDelegate = (UtilDelegate)createDelegate("javax.rmi.CORBA.UtilClass");
  private static final String UtilClassKey = "javax.rmi.CORBA.UtilClass";
  
  private Util() {}
  
  public static RemoteException mapSystemException(SystemException paramSystemException)
  {
    if (utilDelegate != null) {
      return utilDelegate.mapSystemException(paramSystemException);
    }
    return null;
  }
  
  public static void writeAny(OutputStream paramOutputStream, Object paramObject)
  {
    if (utilDelegate != null) {
      utilDelegate.writeAny(paramOutputStream, paramObject);
    }
  }
  
  public static Object readAny(InputStream paramInputStream)
  {
    if (utilDelegate != null) {
      return utilDelegate.readAny(paramInputStream);
    }
    return null;
  }
  
  public static void writeRemoteObject(OutputStream paramOutputStream, Object paramObject)
  {
    if (utilDelegate != null) {
      utilDelegate.writeRemoteObject(paramOutputStream, paramObject);
    }
  }
  
  public static void writeAbstractObject(OutputStream paramOutputStream, Object paramObject)
  {
    if (utilDelegate != null) {
      utilDelegate.writeAbstractObject(paramOutputStream, paramObject);
    }
  }
  
  public static void registerTarget(Tie paramTie, Remote paramRemote)
  {
    if (utilDelegate != null) {
      utilDelegate.registerTarget(paramTie, paramRemote);
    }
  }
  
  public static void unexportObject(Remote paramRemote)
    throws NoSuchObjectException
  {
    if (utilDelegate != null) {
      utilDelegate.unexportObject(paramRemote);
    }
  }
  
  public static Tie getTie(Remote paramRemote)
  {
    if (utilDelegate != null) {
      return utilDelegate.getTie(paramRemote);
    }
    return null;
  }
  
  public static ValueHandler createValueHandler()
  {
    if (utilDelegate != null) {
      return utilDelegate.createValueHandler();
    }
    return null;
  }
  
  public static String getCodebase(Class paramClass)
  {
    if (utilDelegate != null) {
      return utilDelegate.getCodebase(paramClass);
    }
    return null;
  }
  
  public static Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    if (utilDelegate != null) {
      return utilDelegate.loadClass(paramString1, paramString2, paramClassLoader);
    }
    return null;
  }
  
  public static boolean isLocal(Stub paramStub)
    throws RemoteException
  {
    if (utilDelegate != null) {
      return utilDelegate.isLocal(paramStub);
    }
    return false;
  }
  
  public static RemoteException wrapException(Throwable paramThrowable)
  {
    if (utilDelegate != null) {
      return utilDelegate.wrapException(paramThrowable);
    }
    return null;
  }
  
  public static Object[] copyObjects(Object[] paramArrayOfObject, ORB paramORB)
    throws RemoteException
  {
    if (utilDelegate != null) {
      return utilDelegate.copyObjects(paramArrayOfObject, paramORB);
    }
    return null;
  }
  
  public static Object copyObject(Object paramObject, ORB paramORB)
    throws RemoteException
  {
    if (utilDelegate != null) {
      return utilDelegate.copyObject(paramObject, paramORB);
    }
    return null;
  }
  
  private static Object createDelegate(String paramString)
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null)
    {
      Properties localProperties = getORBPropertiesFile();
      if (localProperties != null) {
        str = localProperties.getProperty(paramString);
      }
    }
    if (str == null) {
      return new com.sun.corba.se.impl.javax.rmi.CORBA.Util();
    }
    try
    {
      return loadDelegateClass(str).newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localINITIALIZE = new INITIALIZE("Cannot instantiate " + str);
      localINITIALIZE.initCause(localClassNotFoundException);
      throw localINITIALIZE;
    }
    catch (Exception localException)
    {
      INITIALIZE localINITIALIZE = new INITIALIZE("Error while instantiating" + str);
      localINITIALIZE.initCause(localException);
      throw localINITIALIZE;
    }
  }
  
  private static Class loadDelegateClass(String paramString)
    throws ClassNotFoundException
  {
    try
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      return Class.forName(paramString, false, localClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      try
      {
        return RMIClassLoader.loadClass(paramString);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        String str = "Could not load " + paramString + ": " + localMalformedURLException.toString();
        ClassNotFoundException localClassNotFoundException2 = new ClassNotFoundException(str);
        throw localClassNotFoundException2;
      }
    }
  }
  
  private static Properties getORBPropertiesFile()
  {
    return (Properties)AccessController.doPrivileged(new GetORBPropertiesFileAction());
  }
}
