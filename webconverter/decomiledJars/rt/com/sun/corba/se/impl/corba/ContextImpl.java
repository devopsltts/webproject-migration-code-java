package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;

public final class ContextImpl
  extends Context
{
  private org.omg.CORBA.ORB _orb;
  private ORBUtilSystemException wrapper;
  
  public ContextImpl(org.omg.CORBA.ORB paramORB)
  {
    this._orb = paramORB;
    this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.presentation");
  }
  
  public ContextImpl(Context paramContext)
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public String context_name()
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public Context parent()
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public Context create_child(String paramString)
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public void set_one_value(String paramString, Any paramAny)
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public void set_values(NVList paramNVList)
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public void delete_values(String paramString)
  {
    throw this.wrapper.contextNotImplemented();
  }
  
  public NVList get_values(String paramString1, int paramInt, String paramString2)
  {
    throw this.wrapper.contextNotImplemented();
  }
}
