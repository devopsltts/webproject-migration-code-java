package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _InitialNameServiceStub
  extends ObjectImpl
  implements InitialNameService
{
  private static String[] __ids = { "IDL:activation/InitialNameService:1.0" };
  
  public _InitialNameServiceStub() {}
  
  public void bind(String paramString, org.omg.CORBA.Object paramObject, boolean paramBoolean)
    throws NameAlreadyBound
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("bind", true);
      localOutputStream.write_string(paramString);
      ObjectHelper.write(localOutputStream, paramObject);
      localOutputStream.write_boolean(paramBoolean);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/InitialNameService/NameAlreadyBound:1.0")) {
        throw NameAlreadyBoundHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      bind(paramString, paramObject, paramBoolean);
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
