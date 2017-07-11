package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class CacheEnumerator
  implements Enumeration
{
  boolean keys;
  int index;
  CacheEntry[] table;
  CacheEntry entry;
  
  CacheEnumerator(CacheEntry[] paramArrayOfCacheEntry, boolean paramBoolean)
  {
    this.table = paramArrayOfCacheEntry;
    this.keys = paramBoolean;
    this.index = paramArrayOfCacheEntry.length;
  }
  
  public boolean hasMoreElements()
  {
    if (this.index >= 0)
    {
      while (this.entry != null)
      {
        if (this.entry.check() != null) {
          return true;
        }
        this.entry = this.entry.next;
      }
      while ((--this.index >= 0) && ((this.entry = this.table[this.index]) == null)) {}
    }
    return false;
  }
  
  public Object nextElement()
  {
    while (this.index >= 0)
    {
      while ((this.entry == null) && (--this.index >= 0) && ((this.entry = this.table[this.index]) == null)) {}
      if (this.entry != null)
      {
        CacheEntry localCacheEntry = this.entry;
        this.entry = localCacheEntry.next;
        if (localCacheEntry.check() != null) {
          return this.keys ? localCacheEntry.key : localCacheEntry.check();
        }
      }
    }
    throw new NoSuchElementException("CacheEnumerator");
  }
}
