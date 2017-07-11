package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class NameSpaceSymbTable
{
  private static final String XMLNS = "xmlns";
  private static final SymbMap initialMap = new SymbMap();
  private SymbMap symb = (SymbMap)initialMap.clone();
  private List<SymbMap> level = new ArrayList();
  private boolean cloned = true;
  
  public NameSpaceSymbTable() {}
  
  public void getUnrenderedNodes(Collection<Attr> paramCollection)
  {
    Iterator localIterator = this.symb.entrySet().iterator();
    while (localIterator.hasNext())
    {
      NameSpaceSymbEntry localNameSpaceSymbEntry = (NameSpaceSymbEntry)localIterator.next();
      if ((!localNameSpaceSymbEntry.rendered) && (localNameSpaceSymbEntry.n != null))
      {
        localNameSpaceSymbEntry = (NameSpaceSymbEntry)localNameSpaceSymbEntry.clone();
        needsClone();
        this.symb.put(localNameSpaceSymbEntry.prefix, localNameSpaceSymbEntry);
        localNameSpaceSymbEntry.lastrendered = localNameSpaceSymbEntry.uri;
        localNameSpaceSymbEntry.rendered = true;
        paramCollection.add(localNameSpaceSymbEntry.n);
      }
    }
  }
  
  public void outputNodePush()
  {
    push();
  }
  
  public void outputNodePop()
  {
    pop();
  }
  
  public void push()
  {
    this.level.add(null);
    this.cloned = false;
  }
  
  public void pop()
  {
    int i = this.level.size() - 1;
    Object localObject = this.level.remove(i);
    if (localObject != null)
    {
      this.symb = ((SymbMap)localObject);
      if (i == 0) {
        this.cloned = false;
      } else {
        this.cloned = (this.level.get(i - 1) != this.symb);
      }
    }
    else
    {
      this.cloned = false;
    }
  }
  
  final void needsClone()
  {
    if (!this.cloned)
    {
      this.level.set(this.level.size() - 1, this.symb);
      this.symb = ((SymbMap)this.symb.clone());
      this.cloned = true;
    }
  }
  
  public Attr getMapping(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = this.symb.get(paramString);
    if (localNameSpaceSymbEntry == null) {
      return null;
    }
    if (localNameSpaceSymbEntry.rendered) {
      return null;
    }
    localNameSpaceSymbEntry = (NameSpaceSymbEntry)localNameSpaceSymbEntry.clone();
    needsClone();
    this.symb.put(paramString, localNameSpaceSymbEntry);
    localNameSpaceSymbEntry.rendered = true;
    localNameSpaceSymbEntry.lastrendered = localNameSpaceSymbEntry.uri;
    return localNameSpaceSymbEntry.n;
  }
  
  public Attr getMappingWithoutRendered(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = this.symb.get(paramString);
    if (localNameSpaceSymbEntry == null) {
      return null;
    }
    if (localNameSpaceSymbEntry.rendered) {
      return null;
    }
    return localNameSpaceSymbEntry.n;
  }
  
  public boolean addMapping(String paramString1, String paramString2, Attr paramAttr)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry1 = this.symb.get(paramString1);
    if ((localNameSpaceSymbEntry1 != null) && (paramString2.equals(localNameSpaceSymbEntry1.uri))) {
      return false;
    }
    NameSpaceSymbEntry localNameSpaceSymbEntry2 = new NameSpaceSymbEntry(paramString2, paramAttr, false, paramString1);
    needsClone();
    this.symb.put(paramString1, localNameSpaceSymbEntry2);
    if (localNameSpaceSymbEntry1 != null)
    {
      localNameSpaceSymbEntry2.lastrendered = localNameSpaceSymbEntry1.lastrendered;
      if ((localNameSpaceSymbEntry1.lastrendered != null) && (localNameSpaceSymbEntry1.lastrendered.equals(paramString2))) {
        localNameSpaceSymbEntry2.rendered = true;
      }
    }
    return true;
  }
  
  public Node addMappingAndRender(String paramString1, String paramString2, Attr paramAttr)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry1 = this.symb.get(paramString1);
    if ((localNameSpaceSymbEntry1 != null) && (paramString2.equals(localNameSpaceSymbEntry1.uri)))
    {
      if (!localNameSpaceSymbEntry1.rendered)
      {
        localNameSpaceSymbEntry1 = (NameSpaceSymbEntry)localNameSpaceSymbEntry1.clone();
        needsClone();
        this.symb.put(paramString1, localNameSpaceSymbEntry1);
        localNameSpaceSymbEntry1.lastrendered = paramString2;
        localNameSpaceSymbEntry1.rendered = true;
        return localNameSpaceSymbEntry1.n;
      }
      return null;
    }
    NameSpaceSymbEntry localNameSpaceSymbEntry2 = new NameSpaceSymbEntry(paramString2, paramAttr, true, paramString1);
    localNameSpaceSymbEntry2.lastrendered = paramString2;
    needsClone();
    this.symb.put(paramString1, localNameSpaceSymbEntry2);
    if ((localNameSpaceSymbEntry1 != null) && (localNameSpaceSymbEntry1.lastrendered != null) && (localNameSpaceSymbEntry1.lastrendered.equals(paramString2)))
    {
      localNameSpaceSymbEntry2.rendered = true;
      return null;
    }
    return localNameSpaceSymbEntry2.n;
  }
  
  public int getLevel()
  {
    return this.level.size();
  }
  
  public void removeMapping(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = this.symb.get(paramString);
    if (localNameSpaceSymbEntry != null)
    {
      needsClone();
      this.symb.put(paramString, null);
    }
  }
  
  public void removeMappingIfNotRender(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = this.symb.get(paramString);
    if ((localNameSpaceSymbEntry != null) && (!localNameSpaceSymbEntry.rendered))
    {
      needsClone();
      this.symb.put(paramString, null);
    }
  }
  
  public boolean removeMappingIfRender(String paramString)
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = this.symb.get(paramString);
    if ((localNameSpaceSymbEntry != null) && (localNameSpaceSymbEntry.rendered))
    {
      needsClone();
      this.symb.put(paramString, null);
    }
    return false;
  }
  
  static
  {
    NameSpaceSymbEntry localNameSpaceSymbEntry = new NameSpaceSymbEntry("", null, true, "xmlns");
    localNameSpaceSymbEntry.lastrendered = "";
    initialMap.put("xmlns", localNameSpaceSymbEntry);
  }
}
