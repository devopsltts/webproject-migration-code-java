package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.NamingException;

public abstract interface InitialContextFactoryBuilder
{
  public abstract InitialContextFactory createInitialContextFactory(Hashtable<?, ?> paramHashtable)
    throws NamingException;
}
