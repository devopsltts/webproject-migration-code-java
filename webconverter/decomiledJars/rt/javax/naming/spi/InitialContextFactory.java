package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract interface InitialContextFactory
{
  public abstract Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException;
}
