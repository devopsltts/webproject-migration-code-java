package org.omg.PortableServer;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class ServantLocatorPOA
  extends Servant
  implements ServantLocatorOperations, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0" };
  
  public ServantLocatorPOA() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    throw new BAD_OPERATION();
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte)
  {
    return (String[])__ids.clone();
  }
  
  public ServantLocator _this()
  {
    return ServantLocatorHelper.narrow(super._this_object());
  }
  
  public ServantLocator _this(ORB paramORB)
  {
    return ServantLocatorHelper.narrow(super._this_object(paramORB));
  }
  
  static
  {
    _methods.put("preinvoke", new Integer(0));
    _methods.put("postinvoke", new Integer(1));
  }
}
