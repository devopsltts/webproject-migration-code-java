package org.omg.CORBA.portable;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

public abstract class Delegate
{
  public Delegate() {}
  
  public abstract org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object paramObject);
  
  public abstract org.omg.CORBA.Object duplicate(org.omg.CORBA.Object paramObject);
  
  public abstract void release(org.omg.CORBA.Object paramObject);
  
  public abstract boolean is_a(org.omg.CORBA.Object paramObject, String paramString);
  
  public abstract boolean non_existent(org.omg.CORBA.Object paramObject);
  
  public abstract boolean is_equivalent(org.omg.CORBA.Object paramObject1, org.omg.CORBA.Object paramObject2);
  
  public abstract int hash(org.omg.CORBA.Object paramObject, int paramInt);
  
  public abstract Request request(org.omg.CORBA.Object paramObject, String paramString);
  
  public abstract Request create_request(org.omg.CORBA.Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue);
  
  public abstract Request create_request(org.omg.CORBA.Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList);
  
  public ORB orb(org.omg.CORBA.Object paramObject)
  {
    throw new NO_IMPLEMENT();
  }
  
  public Policy get_policy(org.omg.CORBA.Object paramObject, int paramInt)
  {
    throw new NO_IMPLEMENT();
  }
  
  public DomainManager[] get_domain_managers(org.omg.CORBA.Object paramObject)
  {
    throw new NO_IMPLEMENT();
  }
  
  public org.omg.CORBA.Object set_policy_override(org.omg.CORBA.Object paramObject, Policy[] paramArrayOfPolicy, SetOverrideType paramSetOverrideType)
  {
    throw new NO_IMPLEMENT();
  }
  
  public boolean is_local(org.omg.CORBA.Object paramObject)
  {
    return false;
  }
  
  public ServantObject servant_preinvoke(org.omg.CORBA.Object paramObject, String paramString, Class paramClass)
  {
    return null;
  }
  
  public void servant_postinvoke(org.omg.CORBA.Object paramObject, ServantObject paramServantObject) {}
  
  public OutputStream request(org.omg.CORBA.Object paramObject, String paramString, boolean paramBoolean)
  {
    throw new NO_IMPLEMENT();
  }
  
  public InputStream invoke(org.omg.CORBA.Object paramObject, OutputStream paramOutputStream)
    throws ApplicationException, RemarshalException
  {
    throw new NO_IMPLEMENT();
  }
  
  public void releaseReply(org.omg.CORBA.Object paramObject, InputStream paramInputStream)
  {
    throw new NO_IMPLEMENT();
  }
  
  public String toString(org.omg.CORBA.Object paramObject)
  {
    return paramObject.getClass().getName() + ":" + toString();
  }
  
  public int hashCode(org.omg.CORBA.Object paramObject)
  {
    return System.identityHashCode(paramObject);
  }
  
  public boolean equals(org.omg.CORBA.Object paramObject, Object paramObject1)
  {
    return paramObject == paramObject1;
  }
}
