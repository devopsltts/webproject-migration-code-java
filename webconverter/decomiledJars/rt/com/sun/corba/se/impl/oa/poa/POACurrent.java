package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import java.util.EmptyStackException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.PortableServer.Current;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class POACurrent
  extends ObjectImpl
  implements Current
{
  private ORB orb;
  private POASystemException wrapper;
  
  public POACurrent(ORB paramORB)
  {
    this.orb = paramORB;
    this.wrapper = POASystemException.get(paramORB, "oa.invocation");
  }
  
  public String[] _ids()
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = "IDL:omg.org/PortableServer/Current:1.0";
    return arrayOfString;
  }
  
  public POA get_POA()
    throws NoContext
  {
    POA localPOA = (POA)peekThrowNoContext().oa();
    throwNoContextIfNull(localPOA);
    return localPOA;
  }
  
  public byte[] get_object_id()
    throws NoContext
  {
    byte[] arrayOfByte = peekThrowNoContext().id();
    throwNoContextIfNull(arrayOfByte);
    return arrayOfByte;
  }
  
  public ObjectAdapter getOA()
  {
    ObjectAdapter localObjectAdapter = peekThrowInternal().oa();
    throwInternalIfNull(localObjectAdapter);
    return localObjectAdapter;
  }
  
  public byte[] getObjectId()
  {
    byte[] arrayOfByte = peekThrowInternal().id();
    throwInternalIfNull(arrayOfByte);
    return arrayOfByte;
  }
  
  Servant getServant()
  {
    Servant localServant = (Servant)peekThrowInternal().getServantContainer();
    return localServant;
  }
  
  CookieHolder getCookieHolder()
  {
    CookieHolder localCookieHolder = peekThrowInternal().getCookieHolder();
    throwInternalIfNull(localCookieHolder);
    return localCookieHolder;
  }
  
  public String getOperation()
  {
    String str = peekThrowInternal().getOperation();
    throwInternalIfNull(str);
    return str;
  }
  
  void setServant(Servant paramServant)
  {
    peekThrowInternal().setServant(paramServant);
  }
  
  private OAInvocationInfo peekThrowNoContext()
    throws NoContext
  {
    OAInvocationInfo localOAInvocationInfo = null;
    try
    {
      localOAInvocationInfo = this.orb.peekInvocationInfo();
    }
    catch (EmptyStackException localEmptyStackException)
    {
      throw new NoContext();
    }
    return localOAInvocationInfo;
  }
  
  private OAInvocationInfo peekThrowInternal()
  {
    OAInvocationInfo localOAInvocationInfo = null;
    try
    {
      localOAInvocationInfo = this.orb.peekInvocationInfo();
    }
    catch (EmptyStackException localEmptyStackException)
    {
      throw this.wrapper.poacurrentUnbalancedStack(localEmptyStackException);
    }
    return localOAInvocationInfo;
  }
  
  private void throwNoContextIfNull(Object paramObject)
    throws NoContext
  {
    if (paramObject == null) {
      throw new NoContext();
    }
  }
  
  private void throwInternalIfNull(Object paramObject)
  {
    if (paramObject == null) {
      throw this.wrapper.poacurrentNullField(CompletionStatus.COMPLETED_MAYBE);
    }
  }
}
