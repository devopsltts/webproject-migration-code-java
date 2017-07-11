package com.sun.jndi.rmi.registry;

import com.sun.jndi.url.rmi.rmiURLContextFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public class RegistryContextFactory
  implements ObjectFactory, InitialContextFactory
{
  public static final String ADDRESS_TYPE = "URL";
  
  public RegistryContextFactory() {}
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramHashtable != null) {
      paramHashtable = (Hashtable)paramHashtable.clone();
    }
    return URLToContext(getInitCtxURL(paramHashtable), paramHashtable);
  }
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (!isRegistryRef(paramObject)) {
      return null;
    }
    Object localObject = URLsToObject(getURLs((Reference)paramObject), paramHashtable);
    if ((localObject instanceof RegistryContext))
    {
      RegistryContext localRegistryContext = (RegistryContext)localObject;
      localRegistryContext.reference = ((Reference)paramObject);
    }
    return localObject;
  }
  
  private static Context URLToContext(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    rmiURLContextFactory localRmiURLContextFactory = new rmiURLContextFactory();
    Object localObject = localRmiURLContextFactory.getObjectInstance(paramString, null, null, paramHashtable);
    if ((localObject instanceof Context)) {
      return (Context)localObject;
    }
    throw new NotContextException(paramString);
  }
  
  private static Object URLsToObject(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    rmiURLContextFactory localRmiURLContextFactory = new rmiURLContextFactory();
    return localRmiURLContextFactory.getObjectInstance(paramArrayOfString, null, null, paramHashtable);
  }
  
  private static String getInitCtxURL(Hashtable<?, ?> paramHashtable)
  {
    String str = null;
    if (paramHashtable != null) {
      str = (String)paramHashtable.get("java.naming.provider.url");
    }
    return str != null ? str : "rmi:";
  }
  
  private static boolean isRegistryRef(Object paramObject)
  {
    if (!(paramObject instanceof Reference)) {
      return false;
    }
    String str = RegistryContextFactory.class.getName();
    Reference localReference = (Reference)paramObject;
    return str.equals(localReference.getFactoryClassName());
  }
  
  private static String[] getURLs(Reference paramReference)
    throws NamingException
  {
    int i = 0;
    String[] arrayOfString = new String[paramReference.size()];
    Enumeration localEnumeration = paramReference.getAll();
    while (localEnumeration.hasMoreElements())
    {
      localObject = (RefAddr)localEnumeration.nextElement();
      if (((localObject instanceof StringRefAddr)) && (((RefAddr)localObject).getType().equals("URL"))) {
        arrayOfString[(i++)] = ((String)((RefAddr)localObject).getContent());
      }
    }
    if (i == 0) {
      throw new ConfigurationException("Reference contains no valid addresses");
    }
    if (i == paramReference.size()) {
      return arrayOfString;
    }
    Object localObject = new String[i];
    System.arraycopy(arrayOfString, 0, localObject, 0, i);
    return localObject;
  }
}
