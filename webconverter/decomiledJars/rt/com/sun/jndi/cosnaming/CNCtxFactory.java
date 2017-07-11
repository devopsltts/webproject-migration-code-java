package com.sun.jndi.cosnaming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class CNCtxFactory
  implements InitialContextFactory
{
  public CNCtxFactory() {}
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    return new CNCtx(paramHashtable);
  }
}
