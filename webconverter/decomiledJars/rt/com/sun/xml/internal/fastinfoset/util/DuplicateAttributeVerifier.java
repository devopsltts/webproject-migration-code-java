package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;

public class DuplicateAttributeVerifier
{
  public static final int MAP_SIZE = 256;
  public int _currentIteration;
  private Entry[] _map;
  public final Entry _poolHead;
  public Entry _poolCurrent;
  private Entry _poolTail = this._poolHead = new Entry();
  
  public DuplicateAttributeVerifier() {}
  
  public final void clear()
  {
    this._currentIteration = 0;
    for (Entry localEntry = this._poolHead; localEntry != null; localEntry = localEntry.poolNext) {
      localEntry.iteration = 0;
    }
    reset();
  }
  
  public final void reset()
  {
    this._poolCurrent = this._poolHead;
    if (this._map == null) {
      this._map = new Entry['Ā'];
    }
  }
  
  private final void increasePool(int paramInt)
  {
    if (this._map == null)
    {
      this._map = new Entry['Ā'];
      this._poolCurrent = this._poolHead;
    }
    else
    {
      Entry localEntry1 = this._poolTail;
      for (int i = 0; i < paramInt; i++)
      {
        Entry localEntry2 = new Entry();
        this._poolTail.poolNext = localEntry2;
        this._poolTail = localEntry2;
      }
      this._poolCurrent = localEntry1.poolNext;
    }
  }
  
  public final void checkForDuplicateAttribute(int paramInt1, int paramInt2)
    throws FastInfosetException
  {
    if (this._poolCurrent == null) {
      increasePool(16);
    }
    Entry localEntry1 = this._poolCurrent;
    this._poolCurrent = this._poolCurrent.poolNext;
    Entry localEntry2 = this._map[paramInt1];
    if ((localEntry2 == null) || (localEntry2.iteration < this._currentIteration))
    {
      localEntry1.hashNext = null;
      this._map[paramInt1] = localEntry1;
      localEntry1.iteration = this._currentIteration;
      localEntry1.value = paramInt2;
    }
    else
    {
      Entry localEntry3 = localEntry2;
      do
      {
        if (localEntry3.value == paramInt2)
        {
          reset();
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateAttribute"));
        }
      } while ((localEntry3 = localEntry3.hashNext) != null);
      localEntry1.hashNext = localEntry2;
      this._map[paramInt1] = localEntry1;
      localEntry1.iteration = this._currentIteration;
      localEntry1.value = paramInt2;
    }
  }
  
  public static class Entry
  {
    private int iteration;
    private int value;
    private Entry hashNext;
    private Entry poolNext;
    
    public Entry() {}
  }
}
