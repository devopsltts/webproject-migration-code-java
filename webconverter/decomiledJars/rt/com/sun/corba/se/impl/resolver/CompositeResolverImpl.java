package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;

public class CompositeResolverImpl
  implements Resolver
{
  private Resolver first;
  private Resolver second;
  
  public CompositeResolverImpl(Resolver paramResolver1, Resolver paramResolver2)
  {
    this.first = paramResolver1;
    this.second = paramResolver2;
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    org.omg.CORBA.Object localObject = this.first.resolve(paramString);
    if (localObject == null) {
      localObject = this.second.resolve(paramString);
    }
    return localObject;
  }
  
  public Set list()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(this.first.list());
    localHashSet.addAll(this.second.list());
    return localHashSet;
  }
}
