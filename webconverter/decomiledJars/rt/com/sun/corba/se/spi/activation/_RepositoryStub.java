package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _RepositoryStub
  extends ObjectImpl
  implements Repository
{
  private static String[] __ids = { "IDL:activation/Repository:1.0" };
  
  public _RepositoryStub() {}
  
  public int registerServer(ServerDef paramServerDef)
    throws ServerAlreadyRegistered, BadServerDefinition
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("registerServer", true);
      ServerDefHelper.write(localOutputStream, paramServerDef);
      localInputStream = _invoke(localOutputStream);
      int i = ServerIdHelper.read(localInputStream);
      int k = i;
      return k;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerAlreadyRegistered:1.0")) {
        throw ServerAlreadyRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/BadServerDefinition:1.0")) {
        throw BadServerDefinitionHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      int j = registerServer(paramServerDef);
      return j;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void unregisterServer(int paramInt)
    throws ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("unregisterServer", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      unregisterServer(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public ServerDef getServer(int paramInt)
    throws ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getServer", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ServerDefHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = getServer(paramInt);
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public boolean isInstalled(int paramInt)
    throws ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("isInstalled", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      boolean bool1 = localInputStream.read_boolean();
      boolean bool3 = bool1;
      return bool3;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      boolean bool2 = isInstalled(paramInt);
      return bool2;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void install(int paramInt)
    throws ServerNotRegistered, ServerAlreadyInstalled
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("install", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerAlreadyInstalled:1.0")) {
        throw ServerAlreadyInstalledHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      install(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void uninstall(int paramInt)
    throws ServerNotRegistered, ServerAlreadyUninstalled
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("uninstall", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerAlreadyUninstalled:1.0")) {
        throw ServerAlreadyUninstalledHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      uninstall(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public int[] listRegisteredServers()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("listRegisteredServers", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ServerIdsHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = listRegisteredServers();
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String[] getApplicationNames()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getApplicationNames", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = StringSeqHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = getApplicationNames();
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public int getServerID(String paramString)
    throws ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getServerID", true);
      localOutputStream.write_string(paramString);
      localInputStream = _invoke(localOutputStream);
      int i = ServerIdHelper.read(localInputStream);
      int k = i;
      return k;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      int j = getServerID(paramString);
      return j;
    }
    finally
    {
      _releaseReply(localInputStream);
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
