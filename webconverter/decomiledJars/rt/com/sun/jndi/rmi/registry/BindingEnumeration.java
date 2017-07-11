package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class BindingEnumeration
  implements NamingEnumeration<Binding>
{
  private RegistryContext ctx;
  private final String[] names;
  private int nextName;
  
  BindingEnumeration(RegistryContext paramRegistryContext, String[] paramArrayOfString)
  {
    this.ctx = new RegistryContext(paramRegistryContext);
    this.names = paramArrayOfString;
    this.nextName = 0;
  }
  
  protected void finalize()
  {
    this.ctx.close();
  }
  
  public boolean hasMore()
  {
    if (this.nextName >= this.names.length) {
      this.ctx.close();
    }
    return this.nextName < this.names.length;
  }
  
  public Binding next()
    throws NamingException
  {
    if (!hasMore()) {
      throw new NoSuchElementException();
    }
    String str1 = this.names[(this.nextName++)];
    Name localName = new CompositeName().add(str1);
    Object localObject = this.ctx.lookup(localName);
    String str2 = localName.toString();
    Binding localBinding = new Binding(str2, localObject);
    localBinding.setNameInNamespace(str2);
    return localBinding;
  }
  
  public boolean hasMoreElements()
  {
    return hasMore();
  }
  
  public Binding nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException("javax.naming.NamingException was thrown");
    }
  }
  
  public void close()
  {
    finalize();
  }
}
