package org.omg.CosNaming;

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
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddressHelper;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.omg.CosNaming.NamingContextExtPackage.URLStringHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;

public class _NamingContextExtStub
  extends ObjectImpl
  implements NamingContextExt
{
  private static String[] __ids = { "IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0" };
  
  public _NamingContextExtStub() {}
  
  public String to_string(NameComponent[] paramArrayOfNameComponent)
    throws InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("to_string", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      localInputStream = _invoke(localOutputStream);
      str1 = StringNameHelper.read(localInputStream);
      String str2 = str1;
      return str2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      str1 = localApplicationException.getId();
      if (str1.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL(str1);
    }
    catch (RemarshalException localRemarshalException)
    {
      String str1 = to_string(paramArrayOfNameComponent);
      return str1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public NameComponent[] to_name(String paramString)
    throws InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("to_name", true);
      StringNameHelper.write(localOutputStream, paramString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = NameHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = to_name(paramString);
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String to_url(String paramString1, String paramString2)
    throws InvalidAddress, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("to_url", true);
      AddressHelper.write(localOutputStream, paramString1);
      StringNameHelper.write(localOutputStream, paramString2);
      localInputStream = _invoke(localOutputStream);
      str1 = URLStringHelper.read(localInputStream);
      String str2 = str1;
      return str2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      str1 = localApplicationException.getId();
      if (str1.equals("IDL:omg.org/CosNaming/NamingContextExt/InvalidAddress:1.0")) {
        throw InvalidAddressHelper.read(localInputStream);
      }
      if (str1.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL(str1);
    }
    catch (RemarshalException localRemarshalException)
    {
      String str1 = to_url(paramString1, paramString2);
      return str1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public org.omg.CORBA.Object resolve_str(String paramString)
    throws NotFound, CannotProceed, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("resolve_str", true);
      StringNameHelper.write(localOutputStream, paramString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ObjectHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = resolve_str(paramString);
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void bind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("bind", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      ObjectHelper.write(localOutputStream, paramObject);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
        throw AlreadyBoundHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      bind(paramArrayOfNameComponent, paramObject);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("bind_context", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      NamingContextHelper.write(localOutputStream, paramNamingContext);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
        throw AlreadyBoundHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      bind_context(paramArrayOfNameComponent, paramNamingContext);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void rebind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("rebind", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      ObjectHelper.write(localOutputStream, paramObject);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      rebind(paramArrayOfNameComponent, paramObject);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("rebind_context", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      NamingContextHelper.write(localOutputStream, paramNamingContext);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      rebind_context(paramArrayOfNameComponent, paramNamingContext);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public org.omg.CORBA.Object resolve(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("resolve", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ObjectHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = resolve(paramArrayOfNameComponent);
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void unbind(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("unbind", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      unbind(paramArrayOfNameComponent);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("list", true);
      localOutputStream.write_ulong(paramInt);
      localInputStream = _invoke(localOutputStream);
      paramBindingListHolder.value = BindingListHelper.read(localInputStream);
      paramBindingIteratorHolder.value = BindingIteratorHelper.read(localInputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      list(paramInt, paramBindingListHolder, paramBindingIteratorHolder);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public NamingContext new_context()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("new_context", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = NamingContextHelper.read(localInputStream);
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
      Object localObject1 = new_context();
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, AlreadyBound, CannotProceed, InvalidName
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("bind_new_context", true);
      NameHelper.write(localOutputStream, paramArrayOfNameComponent);
      localInputStream = _invoke(localOutputStream);
      localObject1 = NamingContextHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
        throw NotFoundHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
        throw AlreadyBoundHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
        throw CannotProceedHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
        throw InvalidNameHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = bind_new_context(paramArrayOfNameComponent);
      return localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void destroy()
    throws NotEmpty
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("destroy", true);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotEmpty:1.0")) {
        throw NotEmptyHelper.read(localInputStream);
      }
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
