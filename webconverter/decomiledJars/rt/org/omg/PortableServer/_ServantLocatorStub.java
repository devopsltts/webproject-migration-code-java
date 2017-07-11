package org.omg.PortableServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class _ServantLocatorStub
  extends ObjectImpl
  implements ServantLocator
{
  public static final Class _opsClass = ServantLocatorOperations.class;
  private static String[] __ids = { "IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0" };
  
  public _ServantLocatorStub() {}
  
  public Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder)
    throws ForwardRequest
  {
    ServantObject localServantObject = _servant_preinvoke("preinvoke", _opsClass);
    ServantLocatorOperations localServantLocatorOperations = (ServantLocatorOperations)localServantObject.servant;
    try
    {
      Servant localServant = localServantLocatorOperations.preinvoke(paramArrayOfByte, paramPOA, paramString, paramCookieHolder);
      return localServant;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant)
  {
    ServantObject localServantObject = _servant_preinvoke("postinvoke", _opsClass);
    ServantLocatorOperations localServantLocatorOperations = (ServantLocatorOperations)localServantObject.servant;
    try
    {
      localServantLocatorOperations.postinvoke(paramArrayOfByte, paramPOA, paramString, paramObject, paramServant);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    String str = paramObjectInputStream.readUTF();
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      org.omg.CORBA.Object localObject = localORB.string_to_object(str);
      Delegate localDelegate = ((ObjectImpl)localObject)._get_delegate();
      _set_delegate(localDelegate);
    }
    finally
    {
      localORB.destroy();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      String str = localORB.object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    }
    finally
    {
      localORB.destroy();
    }
  }
}
