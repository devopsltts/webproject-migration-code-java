package org.omg.PortableServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;

public class _ServantActivatorStub
  extends ObjectImpl
  implements ServantActivator
{
  public static final Class _opsClass = ServantActivatorOperations.class;
  private static String[] __ids = { "IDL:omg.org/PortableServer/ServantActivator:2.3", "IDL:omg.org/PortableServer/ServantManager:1.0" };
  
  public _ServantActivatorStub() {}
  
  public Servant incarnate(byte[] paramArrayOfByte, POA paramPOA)
    throws ForwardRequest
  {
    ServantObject localServantObject = _servant_preinvoke("incarnate", _opsClass);
    ServantActivatorOperations localServantActivatorOperations = (ServantActivatorOperations)localServantObject.servant;
    try
    {
      Servant localServant = localServantActivatorOperations.incarnate(paramArrayOfByte, paramPOA);
      return localServant;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void etherealize(byte[] paramArrayOfByte, POA paramPOA, Servant paramServant, boolean paramBoolean1, boolean paramBoolean2)
  {
    ServantObject localServantObject = _servant_preinvoke("etherealize", _opsClass);
    ServantActivatorOperations localServantActivatorOperations = (ServantActivatorOperations)localServantObject.servant;
    try
    {
      localServantActivatorOperations.etherealize(paramArrayOfByte, paramPOA, paramServant, paramBoolean1, paramBoolean2);
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
