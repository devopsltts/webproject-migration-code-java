package com.sun.org.omg.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.DefinitionKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _IDLTypeStub
  extends ObjectImpl
  implements IDLType
{
  private static String[] __ids = { "IDL:omg.org/CORBA/IDLType:1.0", "IDL:omg.org/CORBA/IRObject:1.0" };
  
  public _IDLTypeStub() {}
  
  public _IDLTypeStub(Delegate paramDelegate)
  {
    _set_delegate(paramDelegate);
  }
  
  public TypeCode type()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("_get_type", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = localInputStream.read_TypeCode();
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
      Object localObject1 = type();
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public DefinitionKind def_kind()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("_get_def_kind", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = DefinitionKindHelper.read(localInputStream);
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
      Object localObject1 = def_kind();
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void destroy()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("destroy", true);
      localInputStream = _invoke(localOutputStream);
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      destroy();
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
  {
    try
    {
      String str = paramObjectInputStream.readUTF();
      org.omg.CORBA.Object localObject = ORB.init().string_to_object(str);
      Delegate localDelegate = ((ObjectImpl)localObject)._get_delegate();
      _set_delegate(localDelegate);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
  {
    try
    {
      String str = ORB.init().object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    }
    catch (IOException localIOException) {}
  }
}
