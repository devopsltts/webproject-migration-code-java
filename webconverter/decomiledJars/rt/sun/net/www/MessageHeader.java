package sun.net.www;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringJoiner;

public class MessageHeader
{
  private String[] keys;
  private String[] values;
  private int nkeys;
  
  public MessageHeader()
  {
    grow();
  }
  
  public MessageHeader(InputStream paramInputStream)
    throws IOException
  {
    parseHeader(paramInputStream);
  }
  
  public synchronized String getHeaderNamesInList()
  {
    StringJoiner localStringJoiner = new StringJoiner(",");
    for (int i = 0; i < this.nkeys; i++) {
      localStringJoiner.add(this.keys[i]);
    }
    return localStringJoiner.toString();
  }
  
  public synchronized void reset()
  {
    this.keys = null;
    this.values = null;
    this.nkeys = 0;
    grow();
  }
  
  public synchronized String findValue(String paramString)
  {
    int i;
    if (paramString == null)
    {
      i = this.nkeys;
      do
      {
        i--;
        if (i < 0) {
          break;
        }
      } while (this.keys[i] != null);
      return this.values[i];
    }
    else
    {
      i = this.nkeys;
      do
      {
        i--;
        if (i < 0) {
          break;
        }
      } while (!paramString.equalsIgnoreCase(this.keys[i]));
      return this.values[i];
    }
    return null;
  }
  
  public synchronized int getKey(String paramString)
  {
    int i = this.nkeys;
    do
    {
      i--;
      if (i < 0) {
        break;
      }
    } while ((this.keys[i] != paramString) && ((paramString == null) || (!paramString.equalsIgnoreCase(this.keys[i]))));
    return i;
    return -1;
  }
  
  public synchronized String getKey(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.nkeys)) {
      return null;
    }
    return this.keys[paramInt];
  }
  
  public synchronized String getValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.nkeys)) {
      return null;
    }
    return this.values[paramInt];
  }
  
  public synchronized String findNextValue(String paramString1, String paramString2)
  {
    int i = 0;
    int j;
    if (paramString1 == null)
    {
      j = this.nkeys;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        if (this.keys[j] == null)
        {
          if (i != 0) {
            return this.values[j];
          }
          if (this.values[j] == paramString2) {
            i = 1;
          }
        }
      }
    }
    else
    {
      j = this.nkeys;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        if (paramString1.equalsIgnoreCase(this.keys[j]))
        {
          if (i != 0) {
            return this.values[j];
          }
          if (this.values[j] == paramString2) {
            i = 1;
          }
        }
      }
    }
    return null;
  }
  
  public boolean filterNTLMResponses(String paramString)
  {
    int i = 0;
    for (int j = 0; j < this.nkeys; j++) {
      if ((paramString.equalsIgnoreCase(this.keys[j])) && (this.values[j] != null) && (this.values[j].length() > 5) && (this.values[j].substring(0, 5).equalsIgnoreCase("NTLM ")))
      {
        i = 1;
        break;
      }
    }
    if (i != 0)
    {
      j = 0;
      for (int k = 0; k < this.nkeys; k++) {
        if ((!paramString.equalsIgnoreCase(this.keys[k])) || ((!"Negotiate".equalsIgnoreCase(this.values[k])) && (!"Kerberos".equalsIgnoreCase(this.values[k]))))
        {
          if (k != j)
          {
            this.keys[j] = this.keys[k];
            this.values[j] = this.values[k];
          }
          j++;
        }
      }
      if (j != this.nkeys)
      {
        this.nkeys = j;
        return true;
      }
    }
    return false;
  }
  
  public Iterator<String> multiValueIterator(String paramString)
  {
    return new HeaderIterator(paramString, this);
  }
  
  public synchronized Map<String, List<String>> getHeaders()
  {
    return getHeaders(null);
  }
  
  public synchronized Map<String, List<String>> getHeaders(String[] paramArrayOfString)
  {
    return filterAndAddHeaders(paramArrayOfString, null);
  }
  
  public synchronized Map<String, List<String>> filterAndAddHeaders(String[] paramArrayOfString, Map<String, List<String>> paramMap)
  {
    int i = 0;
    HashMap localHashMap = new HashMap();
    int j = this.nkeys;
    Object localObject1;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      if (paramArrayOfString != null) {
        for (int k = 0; k < paramArrayOfString.length; k++) {
          if ((paramArrayOfString[k] != null) && (paramArrayOfString[k].equalsIgnoreCase(this.keys[j])))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0)
      {
        localObject1 = (List)localHashMap.get(this.keys[j]);
        if (localObject1 == null)
        {
          localObject1 = new ArrayList();
          localHashMap.put(this.keys[j], localObject1);
        }
        ((List)localObject1).add(this.values[j]);
      }
      else
      {
        i = 0;
      }
    }
    if (paramMap != null)
    {
      localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Map.Entry)localIterator.next();
        Object localObject2 = (List)localHashMap.get(((Map.Entry)localObject1).getKey());
        if (localObject2 == null)
        {
          localObject2 = new ArrayList();
          localHashMap.put(((Map.Entry)localObject1).getKey(), localObject2);
        }
        ((List)localObject2).addAll((Collection)((Map.Entry)localObject1).getValue());
      }
    }
    Iterator localIterator = localHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (String)localIterator.next();
      localHashMap.put(localObject1, Collections.unmodifiableList((List)localHashMap.get(localObject1)));
    }
    return Collections.unmodifiableMap(localHashMap);
  }
  
  public synchronized void print(PrintStream paramPrintStream)
  {
    for (int i = 0; i < this.nkeys; i++) {
      if (this.keys[i] != null) {
        paramPrintStream.print(this.keys[i] + (this.values[i] != null ? ": " + this.values[i] : "") + "\r\n");
      }
    }
    paramPrintStream.print("\r\n");
    paramPrintStream.flush();
  }
  
  public synchronized void add(String paramString1, String paramString2)
  {
    grow();
    this.keys[this.nkeys] = paramString1;
    this.values[this.nkeys] = paramString2;
    this.nkeys += 1;
  }
  
  public synchronized void prepend(String paramString1, String paramString2)
  {
    grow();
    for (int i = this.nkeys; i > 0; i--)
    {
      this.keys[i] = this.keys[(i - 1)];
      this.values[i] = this.values[(i - 1)];
    }
    this.keys[0] = paramString1;
    this.values[0] = paramString2;
    this.nkeys += 1;
  }
  
  public synchronized void set(int paramInt, String paramString1, String paramString2)
  {
    grow();
    if (paramInt < 0) {
      return;
    }
    if (paramInt >= this.nkeys)
    {
      add(paramString1, paramString2);
    }
    else
    {
      this.keys[paramInt] = paramString1;
      this.values[paramInt] = paramString2;
    }
  }
  
  private void grow()
  {
    if ((this.keys == null) || (this.nkeys >= this.keys.length))
    {
      String[] arrayOfString1 = new String[this.nkeys + 4];
      String[] arrayOfString2 = new String[this.nkeys + 4];
      if (this.keys != null) {
        System.arraycopy(this.keys, 0, arrayOfString1, 0, this.nkeys);
      }
      if (this.values != null) {
        System.arraycopy(this.values, 0, arrayOfString2, 0, this.nkeys);
      }
      this.keys = arrayOfString1;
      this.values = arrayOfString2;
    }
  }
  
  public synchronized void remove(String paramString)
  {
    int i;
    int j;
    if (paramString == null) {
      for (i = 0; i < this.nkeys; i++) {
        while ((this.keys[i] == null) && (i < this.nkeys))
        {
          for (j = i; j < this.nkeys - 1; j++)
          {
            this.keys[j] = this.keys[(j + 1)];
            this.values[j] = this.values[(j + 1)];
          }
          this.nkeys -= 1;
        }
      }
    } else {
      for (i = 0; i < this.nkeys; i++) {
        while ((paramString.equalsIgnoreCase(this.keys[i])) && (i < this.nkeys))
        {
          for (j = i; j < this.nkeys - 1; j++)
          {
            this.keys[j] = this.keys[(j + 1)];
            this.values[j] = this.values[(j + 1)];
          }
          this.nkeys -= 1;
        }
      }
    }
  }
  
  public synchronized void set(String paramString1, String paramString2)
  {
    int i = this.nkeys;
    do
    {
      i--;
      if (i < 0) {
        break;
      }
    } while (!paramString1.equalsIgnoreCase(this.keys[i]));
    this.values[i] = paramString2;
    return;
    add(paramString1, paramString2);
  }
  
  public synchronized void setIfNotSet(String paramString1, String paramString2)
  {
    if (findValue(paramString1) == null) {
      add(paramString1, paramString2);
    }
  }
  
  public static String canonicalID(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    int i = 0;
    int j = paramString.length();
    int m;
    for (int k = 0; (i < j) && (((m = paramString.charAt(i)) == '<') || (m <= 32)); k = 1) {
      i++;
    }
    while ((i < j) && (((m = paramString.charAt(j - 1)) == '>') || (m <= 32)))
    {
      j--;
      k = 1;
    }
    return k != 0 ? paramString.substring(i, j) : paramString;
  }
  
  public void parseHeader(InputStream paramInputStream)
    throws IOException
  {
    synchronized (this)
    {
      this.nkeys = 0;
    }
    mergeHeader(paramInputStream);
  }
  
  public void mergeHeader(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      return;
    }
    Object localObject1 = new char[10];
    int i = paramInputStream.read();
    while ((i != 10) && (i != 13) && (i >= 0))
    {
      int j = 0;
      int k = -1;
      int n = i > 32 ? 1 : 0;
      localObject1[(j++)] = ((char)i);
      int m;
      Object localObject2;
      while ((m = paramInputStream.read()) >= 0)
      {
        switch (m)
        {
        case 58: 
          if ((n != 0) && (j > 0)) {
            k = j;
          }
          n = 0;
          break;
        case 9: 
          m = 32;
        case 32: 
          n = 0;
          break;
        case 10: 
        case 13: 
          i = paramInputStream.read();
          if ((m == 13) && (i == 10))
          {
            i = paramInputStream.read();
            if (i == 13) {
              i = paramInputStream.read();
            }
          }
          if ((i == 10) || (i == 13) || (i > 32)) {
            break label252;
          }
          m = 32;
        }
        if (j >= localObject1.length)
        {
          localObject2 = new char[localObject1.length * 2];
          System.arraycopy(localObject1, 0, localObject2, 0, j);
          localObject1 = localObject2;
        }
        localObject1[(j++)] = ((char)m);
      }
      i = -1;
      label252:
      while ((j > 0) && (localObject1[(j - 1)] <= ' ')) {
        j--;
      }
      if (k <= 0)
      {
        localObject2 = null;
        k = 0;
      }
      else
      {
        localObject2 = String.copyValueOf((char[])localObject1, 0, k);
        if ((k < j) && (localObject1[k] == ':')) {
          k++;
        }
        while ((k < j) && (localObject1[k] <= ' ')) {
          k++;
        }
      }
      String str;
      if (k >= j) {
        str = new String();
      } else {
        str = String.copyValueOf((char[])localObject1, k, j - k);
      }
      add((String)localObject2, str);
    }
  }
  
  public synchronized String toString()
  {
    String str = super.toString() + this.nkeys + " pairs: ";
    for (int i = 0; (i < this.keys.length) && (i < this.nkeys); i++) {
      str = str + "{" + this.keys[i] + ": " + this.values[i] + "}";
    }
    return str;
  }
  
  class HeaderIterator
    implements Iterator<String>
  {
    int index = 0;
    int next = -1;
    String key;
    boolean haveNext = false;
    Object lock;
    
    public HeaderIterator(String paramString, Object paramObject)
    {
      this.key = paramString;
      this.lock = paramObject;
    }
    
    public boolean hasNext()
    {
      synchronized (this.lock)
      {
        if (this.haveNext) {
          return true;
        }
        while (this.index < MessageHeader.this.nkeys)
        {
          if (this.key.equalsIgnoreCase(MessageHeader.this.keys[this.index]))
          {
            this.haveNext = true;
            this.next = (this.index++);
            return true;
          }
          this.index += 1;
        }
        return false;
      }
    }
    
    public String next()
    {
      synchronized (this.lock)
      {
        if (this.haveNext)
        {
          this.haveNext = false;
          return MessageHeader.this.values[this.next];
        }
        if (hasNext()) {
          return next();
        }
        throw new NoSuchElementException("No more elements");
      }
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("remove not allowed");
    }
  }
}
