package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public abstract interface DirStateFactory
  extends StateFactory
{
  public abstract Result getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes)
    throws NamingException;
  
  public static class Result
  {
    private Object obj;
    private Attributes attrs;
    
    public Result(Object paramObject, Attributes paramAttributes)
    {
      this.obj = paramObject;
      this.attrs = paramAttributes;
    }
    
    public Object getObject()
    {
      return this.obj;
    }
    
    public Attributes getAttributes()
    {
      return this.attrs;
    }
  }
}
