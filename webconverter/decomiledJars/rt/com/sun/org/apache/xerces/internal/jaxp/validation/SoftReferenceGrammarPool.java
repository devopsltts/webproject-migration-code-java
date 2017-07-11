package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

final class SoftReferenceGrammarPool
  implements XMLGrammarPool
{
  protected static final int TABLE_SIZE = 11;
  protected static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
  protected Entry[] fGrammars = null;
  protected boolean fPoolIsLocked;
  protected int fGrammarCount = 0;
  protected final ReferenceQueue fReferenceQueue = new ReferenceQueue();
  
  public SoftReferenceGrammarPool()
  {
    this.fGrammars = new Entry[11];
    this.fPoolIsLocked = false;
  }
  
  public SoftReferenceGrammarPool(int paramInt)
  {
    this.fGrammars = new Entry[paramInt];
    this.fPoolIsLocked = false;
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString)
  {
    synchronized (this.fGrammars)
    {
      clean();
      return ZERO_LENGTH_GRAMMAR_ARRAY;
    }
  }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar)
  {
    if (!this.fPoolIsLocked) {
      for (int i = 0; i < paramArrayOfGrammar.length; i++) {
        putGrammar(paramArrayOfGrammar[i]);
      }
    }
  }
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return getGrammar(paramXMLGrammarDescription);
  }
  
  public void putGrammar(Grammar paramGrammar)
  {
    if (!this.fPoolIsLocked) {
      synchronized (this.fGrammars)
      {
        clean();
        XMLGrammarDescription localXMLGrammarDescription = paramGrammar.getGrammarDescription();
        int i = hashCode(localXMLGrammarDescription);
        int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
        for (Entry localEntry = this.fGrammars[j]; localEntry != null; localEntry = localEntry.next) {
          if ((localEntry.hash == i) && (equals(localEntry.desc, localXMLGrammarDescription)))
          {
            if (localEntry.grammar.get() != paramGrammar) {
              localEntry.grammar = new SoftGrammarReference(localEntry, paramGrammar, this.fReferenceQueue);
            }
            return;
          }
        }
        localEntry = new Entry(i, j, localXMLGrammarDescription, paramGrammar, this.fGrammars[j], this.fReferenceQueue);
        this.fGrammars[j] = localEntry;
        this.fGrammarCount += 1;
      }
    }
  }
  
  public Grammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    synchronized (this.fGrammars)
    {
      clean();
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry localEntry = this.fGrammars[j]; localEntry != null; localEntry = localEntry.next)
      {
        Grammar localGrammar = (Grammar)localEntry.grammar.get();
        if (localGrammar == null) {
          removeEntry(localEntry);
        } else if ((localEntry.hash == i) && (equals(localEntry.desc, paramXMLGrammarDescription))) {
          return localGrammar;
        }
      }
      return null;
    }
  }
  
  public Grammar removeGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    synchronized (this.fGrammars)
    {
      clean();
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry localEntry = this.fGrammars[j]; localEntry != null; localEntry = localEntry.next) {
        if ((localEntry.hash == i) && (equals(localEntry.desc, paramXMLGrammarDescription))) {
          return removeEntry(localEntry);
        }
      }
      return null;
    }
  }
  
  public boolean containsGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    synchronized (this.fGrammars)
    {
      clean();
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry localEntry = this.fGrammars[j]; localEntry != null; localEntry = localEntry.next)
      {
        Grammar localGrammar = (Grammar)localEntry.grammar.get();
        if (localGrammar == null) {
          removeEntry(localEntry);
        } else if ((localEntry.hash == i) && (equals(localEntry.desc, paramXMLGrammarDescription))) {
          return true;
        }
      }
      return false;
    }
  }
  
  public void lockPool()
  {
    this.fPoolIsLocked = true;
  }
  
  public void unlockPool()
  {
    this.fPoolIsLocked = false;
  }
  
  public void clear()
  {
    for (int i = 0; i < this.fGrammars.length; i++) {
      if (this.fGrammars[i] != null)
      {
        this.fGrammars[i].clear();
        this.fGrammars[i] = null;
      }
    }
    this.fGrammarCount = 0;
  }
  
  public boolean equals(XMLGrammarDescription paramXMLGrammarDescription1, XMLGrammarDescription paramXMLGrammarDescription2)
  {
    if ((paramXMLGrammarDescription1 instanceof XMLSchemaDescription))
    {
      if (!(paramXMLGrammarDescription2 instanceof XMLSchemaDescription)) {
        return false;
      }
      XMLSchemaDescription localXMLSchemaDescription1 = (XMLSchemaDescription)paramXMLGrammarDescription1;
      XMLSchemaDescription localXMLSchemaDescription2 = (XMLSchemaDescription)paramXMLGrammarDescription2;
      String str1 = localXMLSchemaDescription1.getTargetNamespace();
      if (str1 != null)
      {
        if (!str1.equals(localXMLSchemaDescription2.getTargetNamespace())) {
          return false;
        }
      }
      else if (localXMLSchemaDescription2.getTargetNamespace() != null) {
        return false;
      }
      String str2 = localXMLSchemaDescription1.getExpandedSystemId();
      if (str2 != null)
      {
        if (!str2.equals(localXMLSchemaDescription2.getExpandedSystemId())) {
          return false;
        }
      }
      else if (localXMLSchemaDescription2.getExpandedSystemId() != null) {
        return false;
      }
      return true;
    }
    return paramXMLGrammarDescription1.equals(paramXMLGrammarDescription2);
  }
  
  public int hashCode(XMLGrammarDescription paramXMLGrammarDescription)
  {
    if ((paramXMLGrammarDescription instanceof XMLSchemaDescription))
    {
      XMLSchemaDescription localXMLSchemaDescription = (XMLSchemaDescription)paramXMLGrammarDescription;
      String str1 = localXMLSchemaDescription.getTargetNamespace();
      String str2 = localXMLSchemaDescription.getExpandedSystemId();
      int i = str1 != null ? str1.hashCode() : 0;
      i ^= (str2 != null ? str2.hashCode() : 0);
      return i;
    }
    return paramXMLGrammarDescription.hashCode();
  }
  
  private Grammar removeEntry(Entry paramEntry)
  {
    if (paramEntry.prev != null) {
      paramEntry.prev.next = paramEntry.next;
    } else {
      this.fGrammars[paramEntry.bucket] = paramEntry.next;
    }
    if (paramEntry.next != null) {
      paramEntry.next.prev = paramEntry.prev;
    }
    this.fGrammarCount -= 1;
    paramEntry.grammar.entry = null;
    return (Grammar)paramEntry.grammar.get();
  }
  
  private void clean()
  {
    for (Reference localReference = this.fReferenceQueue.poll(); localReference != null; localReference = this.fReferenceQueue.poll())
    {
      Entry localEntry = ((SoftGrammarReference)localReference).entry;
      if (localEntry != null) {
        removeEntry(localEntry);
      }
    }
  }
  
  static final class Entry
  {
    public int hash;
    public int bucket;
    public Entry prev;
    public Entry next;
    public XMLGrammarDescription desc;
    public SoftReferenceGrammarPool.SoftGrammarReference grammar;
    
    protected Entry(int paramInt1, int paramInt2, XMLGrammarDescription paramXMLGrammarDescription, Grammar paramGrammar, Entry paramEntry, ReferenceQueue paramReferenceQueue)
    {
      this.hash = paramInt1;
      this.bucket = paramInt2;
      this.prev = null;
      this.next = paramEntry;
      if (paramEntry != null) {
        paramEntry.prev = this;
      }
      this.desc = paramXMLGrammarDescription;
      this.grammar = new SoftReferenceGrammarPool.SoftGrammarReference(this, paramGrammar, paramReferenceQueue);
    }
    
    protected void clear()
    {
      this.desc = null;
      this.grammar = null;
      if (this.next != null)
      {
        this.next.clear();
        this.next = null;
      }
    }
  }
  
  static final class SoftGrammarReference
    extends SoftReference
  {
    public SoftReferenceGrammarPool.Entry entry;
    
    protected SoftGrammarReference(SoftReferenceGrammarPool.Entry paramEntry, Grammar paramGrammar, ReferenceQueue paramReferenceQueue)
    {
      super(paramReferenceQueue);
      this.entry = paramEntry;
    }
  }
}
