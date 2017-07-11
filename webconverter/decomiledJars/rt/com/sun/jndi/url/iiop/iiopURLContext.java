package com.sun.jndi.url.iiop;

import com.sun.jndi.cosnaming.CorbanameUrl;
import com.sun.jndi.cosnaming.IiopUrl;
import com.sun.jndi.toolkit.url.GenericURLContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class iiopURLContext
  extends GenericURLContext
{
  iiopURLContext(Hashtable<?, ?> paramHashtable)
  {
    super(paramHashtable);
  }
  
  protected ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    return iiopURLContextFactory.getUsingURLIgnoreRest(paramString, paramHashtable);
  }
  
  protected Name getURLSuffix(String paramString1, String paramString2)
    throws NamingException
  {
    try
    {
      Object localObject;
      if ((paramString2.startsWith("iiop://")) || (paramString2.startsWith("iiopname://")))
      {
        localObject = new IiopUrl(paramString2);
        return ((IiopUrl)localObject).getCosName();
      }
      if (paramString2.startsWith("corbaname:"))
      {
        localObject = new CorbanameUrl(paramString2);
        return ((CorbanameUrl)localObject).getCosName();
      }
      throw new MalformedURLException("Not a valid URL: " + paramString2);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new InvalidNameException(localMalformedURLException.getMessage());
    }
  }
}
