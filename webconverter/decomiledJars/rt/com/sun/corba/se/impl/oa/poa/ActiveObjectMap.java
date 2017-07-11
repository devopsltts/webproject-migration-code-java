package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class ActiveObjectMap
{
  protected POAImpl poa;
  private Map keyToEntry = new HashMap();
  private Map entryToServant = new HashMap();
  private Map servantToEntry = new HashMap();
  
  protected ActiveObjectMap(POAImpl paramPOAImpl)
  {
    this.poa = paramPOAImpl;
  }
  
  public static ActiveObjectMap create(POAImpl paramPOAImpl, boolean paramBoolean)
  {
    if (paramBoolean) {
      return new MultipleObjectMap(paramPOAImpl);
    }
    return new SingleObjectMap(paramPOAImpl);
  }
  
  public final boolean contains(Servant paramServant)
  {
    return this.servantToEntry.containsKey(paramServant);
  }
  
  public final boolean containsKey(Key paramKey)
  {
    return this.keyToEntry.containsKey(paramKey);
  }
  
  public final AOMEntry get(Key paramKey)
  {
    AOMEntry localAOMEntry = (AOMEntry)this.keyToEntry.get(paramKey);
    if (localAOMEntry == null)
    {
      localAOMEntry = new AOMEntry(this.poa);
      putEntry(paramKey, localAOMEntry);
    }
    return localAOMEntry;
  }
  
  public final Servant getServant(AOMEntry paramAOMEntry)
  {
    return (Servant)this.entryToServant.get(paramAOMEntry);
  }
  
  public abstract Key getKey(AOMEntry paramAOMEntry)
    throws WrongPolicy;
  
  public Key getKey(Servant paramServant)
    throws WrongPolicy
  {
    AOMEntry localAOMEntry = (AOMEntry)this.servantToEntry.get(paramServant);
    return getKey(localAOMEntry);
  }
  
  protected void putEntry(Key paramKey, AOMEntry paramAOMEntry)
  {
    this.keyToEntry.put(paramKey, paramAOMEntry);
  }
  
  public final void putServant(Servant paramServant, AOMEntry paramAOMEntry)
  {
    this.entryToServant.put(paramAOMEntry, paramServant);
    this.servantToEntry.put(paramServant, paramAOMEntry);
  }
  
  protected abstract void removeEntry(AOMEntry paramAOMEntry, Key paramKey);
  
  public final void remove(Key paramKey)
  {
    AOMEntry localAOMEntry = (AOMEntry)this.keyToEntry.remove(paramKey);
    Servant localServant = (Servant)this.entryToServant.remove(localAOMEntry);
    if (localServant != null) {
      this.servantToEntry.remove(localServant);
    }
    removeEntry(localAOMEntry, paramKey);
  }
  
  public abstract boolean hasMultipleIDs(AOMEntry paramAOMEntry);
  
  protected void clear()
  {
    this.keyToEntry.clear();
  }
  
  public final Set keySet()
  {
    return this.keyToEntry.keySet();
  }
  
  public static class Key
  {
    public byte[] id;
    
    Key(byte[] paramArrayOfByte)
    {
      this.id = paramArrayOfByte;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < this.id.length; i++)
      {
        localStringBuffer.append(Integer.toString(this.id[i], 16));
        if (i != this.id.length - 1) {
          localStringBuffer.append(":");
        }
      }
      return localStringBuffer.toString();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Key)) {
        return false;
      }
      Key localKey = (Key)paramObject;
      if (localKey.id.length != this.id.length) {
        return false;
      }
      for (int i = 0; i < this.id.length; i++) {
        if (this.id[i] != localKey.id[i]) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      int i = 0;
      for (int j = 0; j < this.id.length; j++) {
        i = 31 * i + this.id[j];
      }
      return i;
    }
  }
}
