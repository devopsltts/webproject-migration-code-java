package com.sun.corba.se.impl.util;

import java.util.Hashtable;

public class RepositoryIdCache
  extends Hashtable
{
  private RepositoryIdPool pool = new RepositoryIdPool();
  
  public RepositoryIdCache()
  {
    this.pool.setCaches(this);
  }
  
  public final synchronized RepositoryId getId(String paramString)
  {
    RepositoryId localRepositoryId = (RepositoryId)super.get(paramString);
    if (localRepositoryId != null) {
      return localRepositoryId;
    }
    localRepositoryId = new RepositoryId(paramString);
    put(paramString, localRepositoryId);
    return localRepositoryId;
  }
}
