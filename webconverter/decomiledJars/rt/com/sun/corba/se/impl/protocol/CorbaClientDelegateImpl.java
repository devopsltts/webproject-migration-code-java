package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class CorbaClientDelegateImpl
  extends CorbaClientDelegate
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private ORBUtilSystemException wrapper;
  private CorbaContactInfoList contactInfoList;
  
  public CorbaClientDelegateImpl(com.sun.corba.se.spi.orb.ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList)
  {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.contactInfoList = paramCorbaContactInfoList;
  }
  
  public Broker getBroker()
  {
    return this.orb;
  }
  
  public ContactInfoList getContactInfoList()
  {
    return this.contactInfoList;
  }
  
  public OutputStream request(org.omg.CORBA.Object paramObject, String paramString, boolean paramBoolean)
  {
    ClientInvocationInfo localClientInvocationInfo = this.orb.createOrIncrementInvocationInfo();
    Iterator localIterator = localClientInvocationInfo.getContactInfoListIterator();
    if (localIterator == null)
    {
      localIterator = this.contactInfoList.iterator();
      localClientInvocationInfo.setContactInfoListIterator(localIterator);
    }
    if (!localIterator.hasNext()) {
      throw ((CorbaContactInfoListIterator)localIterator).getFailureException();
    }
    CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)localIterator.next();
    ClientRequestDispatcher localClientRequestDispatcher = localCorbaContactInfo.getClientRequestDispatcher();
    localClientInvocationInfo.setClientRequestDispatcher(localClientRequestDispatcher);
    return (OutputStream)localClientRequestDispatcher.beginRequest(paramObject, paramString, !paramBoolean, localCorbaContactInfo);
  }
  
  public InputStream invoke(org.omg.CORBA.Object paramObject, OutputStream paramOutputStream)
    throws ApplicationException, RemarshalException
  {
    ClientRequestDispatcher localClientRequestDispatcher = getClientRequestDispatcher();
    return (InputStream)localClientRequestDispatcher.marshalingComplete(paramObject, (OutputObject)paramOutputStream);
  }
  
  public void releaseReply(org.omg.CORBA.Object paramObject, InputStream paramInputStream)
  {
    ClientRequestDispatcher localClientRequestDispatcher = getClientRequestDispatcher();
    localClientRequestDispatcher.endRequest(this.orb, paramObject, (InputObject)paramInputStream);
    this.orb.releaseOrDecrementInvocationInfo();
  }
  
  private ClientRequestDispatcher getClientRequestDispatcher()
  {
    return ((CorbaInvocationInfo)this.orb.getInvocationInfo()).getClientRequestDispatcher();
  }
  
  public org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object paramObject)
  {
    InputStream localInputStream = null;
    org.omg.CORBA.Object localObject1 = null;
    try
    {
      OutputStream localOutputStream = request(null, "_interface", true);
      localInputStream = invoke((org.omg.CORBA.Object)null, localOutputStream);
      localObject2 = localInputStream.read_Object();
      if (!localObject2._is_a("IDL:omg.org/CORBA/InterfaceDef:1.0")) {
        throw this.wrapper.wrongInterfaceDef(CompletionStatus.COMPLETED_MAYBE);
      }
      try
      {
        localObject1 = (org.omg.CORBA.Object)JDKBridge.loadClass("org.omg.CORBA._InterfaceDefStub").newInstance();
      }
      catch (Exception localException)
      {
        throw this.wrapper.noInterfaceDefStub(localException);
      }
      Delegate localDelegate = StubAdapter.getDelegate(localObject2);
      StubAdapter.setDelegate(localObject1, localDelegate);
    }
    catch (ApplicationException localApplicationException)
    {
      throw this.wrapper.applicationExceptionInSpecialMethod(localApplicationException);
    }
    catch (RemarshalException localRemarshalException)
    {
      org.omg.CORBA.Object localObject2 = get_interface_def(paramObject);
      return localObject2;
    }
    finally
    {
      releaseReply((org.omg.CORBA.Object)null, localInputStream);
    }
    return localObject1;
  }
  
  public boolean is_a(org.omg.CORBA.Object paramObject, String paramString)
  {
    String[] arrayOfString = StubAdapter.getTypeIds(paramObject);
    String str = this.contactInfoList.getTargetIOR().getTypeId();
    if (paramString.equals(str)) {
      return true;
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i])) {
        return true;
      }
    }
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = request(null, "_is_a", true);
      localOutputStream.write_string(paramString);
      localInputStream = invoke((org.omg.CORBA.Object)null, localOutputStream);
      bool = localInputStream.read_boolean();
      return bool;
    }
    catch (ApplicationException localApplicationException)
    {
      throw this.wrapper.applicationExceptionInSpecialMethod(localApplicationException);
    }
    catch (RemarshalException localRemarshalException)
    {
      boolean bool = is_a(paramObject, paramString);
      return bool;
    }
    finally
    {
      releaseReply((org.omg.CORBA.Object)null, localInputStream);
    }
  }
  
  public boolean non_existent(org.omg.CORBA.Object paramObject)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = request(null, "_non_existent", true);
      localInputStream = invoke((org.omg.CORBA.Object)null, localOutputStream);
      bool = localInputStream.read_boolean();
      return bool;
    }
    catch (ApplicationException localApplicationException)
    {
      throw this.wrapper.applicationExceptionInSpecialMethod(localApplicationException);
    }
    catch (RemarshalException localRemarshalException)
    {
      boolean bool = non_existent(paramObject);
      return bool;
    }
    finally
    {
      releaseReply((org.omg.CORBA.Object)null, localInputStream);
    }
  }
  
  public org.omg.CORBA.Object duplicate(org.omg.CORBA.Object paramObject)
  {
    return paramObject;
  }
  
  public void release(org.omg.CORBA.Object paramObject) {}
  
  public boolean is_equivalent(org.omg.CORBA.Object paramObject1, org.omg.CORBA.Object paramObject2)
  {
    if (paramObject2 == null) {
      return false;
    }
    if (!StubAdapter.isStub(paramObject2)) {
      return false;
    }
    Delegate localDelegate = StubAdapter.getDelegate(paramObject2);
    if (localDelegate == null) {
      return false;
    }
    if (localDelegate == this) {
      return true;
    }
    if (!(localDelegate instanceof CorbaClientDelegateImpl)) {
      return false;
    }
    CorbaClientDelegateImpl localCorbaClientDelegateImpl = (CorbaClientDelegateImpl)localDelegate;
    CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)localCorbaClientDelegateImpl.getContactInfoList();
    return this.contactInfoList.getTargetIOR().isEquivalent(localCorbaContactInfoList.getTargetIOR());
  }
  
  public boolean equals(org.omg.CORBA.Object paramObject, Object paramObject1)
  {
    if (paramObject1 == null) {
      return false;
    }
    if (!StubAdapter.isStub(paramObject1)) {
      return false;
    }
    Delegate localDelegate = StubAdapter.getDelegate(paramObject1);
    if (localDelegate == null) {
      return false;
    }
    if ((localDelegate instanceof CorbaClientDelegateImpl))
    {
      CorbaClientDelegateImpl localCorbaClientDelegateImpl = (CorbaClientDelegateImpl)localDelegate;
      IOR localIOR = localCorbaClientDelegateImpl.contactInfoList.getTargetIOR();
      return this.contactInfoList.getTargetIOR().equals(localIOR);
    }
    return false;
  }
  
  public int hashCode(org.omg.CORBA.Object paramObject)
  {
    return hashCode();
  }
  
  public int hash(org.omg.CORBA.Object paramObject, int paramInt)
  {
    int i = hashCode();
    if (i > paramInt) {
      return 0;
    }
    return i;
  }
  
  public Request request(org.omg.CORBA.Object paramObject, String paramString)
  {
    return new RequestImpl(this.orb, paramObject, null, paramString, null, null, null, null);
  }
  
  public Request create_request(org.omg.CORBA.Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue)
  {
    return new RequestImpl(this.orb, paramObject, paramContext, paramString, paramNVList, paramNamedValue, null, null);
  }
  
  public Request create_request(org.omg.CORBA.Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList)
  {
    return new RequestImpl(this.orb, paramObject, paramContext, paramString, paramNVList, paramNamedValue, paramExceptionList, paramContextList);
  }
  
  public org.omg.CORBA.ORB orb(org.omg.CORBA.Object paramObject)
  {
    return this.orb;
  }
  
  public boolean is_local(org.omg.CORBA.Object paramObject)
  {
    return this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal();
  }
  
  public ServantObject servant_preinvoke(org.omg.CORBA.Object paramObject, String paramString, Class paramClass)
  {
    return this.contactInfoList.getLocalClientRequestDispatcher().servant_preinvoke(paramObject, paramString, paramClass);
  }
  
  public void servant_postinvoke(org.omg.CORBA.Object paramObject, ServantObject paramServantObject)
  {
    this.contactInfoList.getLocalClientRequestDispatcher().servant_postinvoke(paramObject, paramServantObject);
  }
  
  public String get_codebase(org.omg.CORBA.Object paramObject)
  {
    if (this.contactInfoList.getTargetIOR() != null) {
      return this.contactInfoList.getTargetIOR().getProfile().getCodebase();
    }
    return null;
  }
  
  public String toString(org.omg.CORBA.Object paramObject)
  {
    return this.contactInfoList.getTargetIOR().stringify();
  }
  
  public int hashCode()
  {
    return this.contactInfoList.hashCode();
  }
}
