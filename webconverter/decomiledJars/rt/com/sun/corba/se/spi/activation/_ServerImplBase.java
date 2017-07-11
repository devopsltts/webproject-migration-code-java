package com.sun.corba.se.spi.activation;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ServerImplBase
  extends ObjectImpl
  implements Server, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:activation/Server:1.0" };
  
  public _ServerImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    switch (localInteger.intValue())
    {
    case 0: 
      shutdown();
      localOutputStream = paramResponseHandler.createReply();
      break;
    case 1: 
      install();
      localOutputStream = paramResponseHandler.createReply();
      break;
    case 2: 
      uninstall();
      localOutputStream = paramResponseHandler.createReply();
      break;
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    return localOutputStream;
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  static
  {
    _methods.put("shutdown", new Integer(0));
    _methods.put("install", new Integer(1));
    _methods.put("uninstall", new Integer(2));
  }
}
