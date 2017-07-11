package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public class NamespaceSupport
  implements NamespaceContext
{
  protected String[] fNamespace = new String[32];
  protected int fNamespaceSize;
  protected int[] fContext = new int[8];
  protected int fCurrentContext;
  protected String[] fPrefixes = new String[16];
  
  public NamespaceSupport() {}
  
  public NamespaceSupport(NamespaceContext paramNamespaceContext)
  {
    pushContext();
    Enumeration localEnumeration = paramNamespaceContext.getAllPrefixes();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = paramNamespaceContext.getURI(str1);
      declarePrefix(str1, str2);
    }
  }
  
  public void reset()
  {
    this.fNamespaceSize = 0;
    this.fCurrentContext = 0;
    this.fNamespace[(this.fNamespaceSize++)] = XMLSymbols.PREFIX_XML;
    this.fNamespace[(this.fNamespaceSize++)] = NamespaceContext.XML_URI;
    this.fNamespace[(this.fNamespaceSize++)] = XMLSymbols.PREFIX_XMLNS;
    this.fNamespace[(this.fNamespaceSize++)] = NamespaceContext.XMLNS_URI;
    this.fContext[this.fCurrentContext] = this.fNamespaceSize;
  }
  
  public void pushContext()
  {
    if (this.fCurrentContext + 1 == this.fContext.length)
    {
      int[] arrayOfInt = new int[this.fContext.length * 2];
      System.arraycopy(this.fContext, 0, arrayOfInt, 0, this.fContext.length);
      this.fContext = arrayOfInt;
    }
    this.fContext[(++this.fCurrentContext)] = this.fNamespaceSize;
  }
  
  public void popContext()
  {
    this.fNamespaceSize = this.fContext[(this.fCurrentContext--)];
  }
  
  public boolean declarePrefix(String paramString1, String paramString2)
  {
    if ((paramString1 == XMLSymbols.PREFIX_XML) || (paramString1 == XMLSymbols.PREFIX_XMLNS)) {
      return false;
    }
    for (int i = this.fNamespaceSize; i > this.fContext[this.fCurrentContext]; i -= 2) {
      if (this.fNamespace[(i - 2)] == paramString1)
      {
        this.fNamespace[(i - 1)] = paramString2;
        return true;
      }
    }
    if (this.fNamespaceSize == this.fNamespace.length)
    {
      String[] arrayOfString = new String[this.fNamespaceSize * 2];
      System.arraycopy(this.fNamespace, 0, arrayOfString, 0, this.fNamespaceSize);
      this.fNamespace = arrayOfString;
    }
    this.fNamespace[(this.fNamespaceSize++)] = paramString1;
    this.fNamespace[(this.fNamespaceSize++)] = paramString2;
    return true;
  }
  
  public String getURI(String paramString)
  {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[(i - 2)] == paramString) {
        return this.fNamespace[(i - 1)];
      }
    }
    return null;
  }
  
  public String getPrefix(String paramString)
  {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if ((this.fNamespace[(i - 1)] == paramString) && (getURI(this.fNamespace[(i - 2)]) == paramString)) {
        return this.fNamespace[(i - 2)];
      }
    }
    return null;
  }
  
  public int getDeclaredPrefixCount()
  {
    return (this.fNamespaceSize - this.fContext[this.fCurrentContext]) / 2;
  }
  
  public String getDeclaredPrefixAt(int paramInt)
  {
    return this.fNamespace[(this.fContext[this.fCurrentContext] + paramInt * 2)];
  }
  
  public Iterator getPrefixes()
  {
    int i = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2)
    {
      localObject = new String[this.fNamespaceSize];
      this.fPrefixes = ((String[])localObject);
    }
    Object localObject = null;
    int j = 1;
    for (int k = 2; k < this.fNamespaceSize - 2; k += 2)
    {
      localObject = this.fNamespace[(k + 2)];
      for (int m = 0; m < i; m++) {
        if (this.fPrefixes[m] == localObject)
        {
          j = 0;
          break;
        }
      }
      if (j != 0) {
        this.fPrefixes[(i++)] = localObject;
      }
      j = 1;
    }
    return new IteratorPrefixes(this.fPrefixes, i);
  }
  
  public Enumeration getAllPrefixes()
  {
    int i = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2)
    {
      localObject = new String[this.fNamespaceSize];
      this.fPrefixes = ((String[])localObject);
    }
    Object localObject = null;
    int j = 1;
    for (int k = 2; k < this.fNamespaceSize - 2; k += 2)
    {
      localObject = this.fNamespace[(k + 2)];
      for (int m = 0; m < i; m++) {
        if (this.fPrefixes[m] == localObject)
        {
          j = 0;
          break;
        }
      }
      if (j != 0) {
        this.fPrefixes[(i++)] = localObject;
      }
      j = 1;
    }
    return new Prefixes(this.fPrefixes, i);
  }
  
  public Vector getPrefixes(String paramString)
  {
    int i = 0;
    Object localObject = null;
    int j = 1;
    Vector localVector = new Vector();
    for (int k = this.fNamespaceSize; k > 0; k -= 2) {
      if ((this.fNamespace[(k - 1)] == paramString) && (!localVector.contains(this.fNamespace[(k - 2)]))) {
        localVector.add(this.fNamespace[(k - 2)]);
      }
    }
    return localVector;
  }
  
  public boolean containsPrefix(String paramString)
  {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[(i - 2)] == paramString) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsPrefixInCurrentContext(String paramString)
  {
    for (int i = this.fContext[this.fCurrentContext]; i < this.fNamespaceSize; i += 2) {
      if (this.fNamespace[i] == paramString) {
        return true;
      }
    }
    return false;
  }
  
  protected final class IteratorPrefixes
    implements Iterator
  {
    private String[] prefixes;
    private int counter = 0;
    private int size = 0;
    
    public IteratorPrefixes(String[] paramArrayOfString, int paramInt)
    {
      this.prefixes = paramArrayOfString;
      this.size = paramInt;
    }
    
    public boolean hasNext()
    {
      return this.counter < this.size;
    }
    
    public Object next()
    {
      if (this.counter < this.size) {
        return NamespaceSupport.this.fPrefixes[(this.counter++)];
      }
      throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < this.size; i++)
      {
        localStringBuffer.append(this.prefixes[i]);
        localStringBuffer.append(" ");
      }
      return localStringBuffer.toString();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  protected final class Prefixes
    implements Enumeration
  {
    private String[] prefixes;
    private int counter = 0;
    private int size = 0;
    
    public Prefixes(String[] paramArrayOfString, int paramInt)
    {
      this.prefixes = paramArrayOfString;
      this.size = paramInt;
    }
    
    public boolean hasMoreElements()
    {
      return this.counter < this.size;
    }
    
    public Object nextElement()
    {
      if (this.counter < this.size) {
        return NamespaceSupport.this.fPrefixes[(this.counter++)];
      }
      throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < this.size; i++)
      {
        localStringBuffer.append(this.prefixes[i]);
        localStringBuffer.append(" ");
      }
      return localStringBuffer.toString();
    }
  }
}
