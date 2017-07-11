package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class NameClassPairEnumeration
  implements NamingEnumeration<NameClassPair>
{
  private final String[] names;
  private int nextName;
  
  NameClassPairEnumeration(String[] paramArrayOfString)
  {
    this.names = paramArrayOfString;
    this.nextName = 0;
  }
  
  public boolean hasMore()
  {
    return this.nextName < this.names.length;
  }
  
  public NameClassPair next()
    throws NamingException
  {
    if (!hasMore()) {
      throw new NoSuchElementException();
    }
    String str = this.names[(this.nextName++)];
    Name localName = new CompositeName().add(str);
    NameClassPair localNameClassPair = new NameClassPair(localName.toString(), "java.lang.Object");
    localNameClassPair.setNameInNamespace(str);
    return localNameClassPair;
  }
  
  public boolean hasMoreElements()
  {
    return hasMore();
  }
  
  public NameClassPair nextElement()
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
    this.nextName = this.names.length;
  }
}
