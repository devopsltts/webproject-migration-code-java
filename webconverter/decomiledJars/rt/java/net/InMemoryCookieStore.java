package java.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

class InMemoryCookieStore
  implements CookieStore
{
  private List<HttpCookie> cookieJar = null;
  private Map<String, List<HttpCookie>> domainIndex = null;
  private Map<URI, List<HttpCookie>> uriIndex = null;
  private ReentrantLock lock = null;
  
  public InMemoryCookieStore() {}
  
  public void add(URI paramURI, HttpCookie paramHttpCookie)
  {
    if (paramHttpCookie == null) {
      throw new NullPointerException("cookie is null");
    }
    this.lock.lock();
    try
    {
      this.cookieJar.remove(paramHttpCookie);
      if (paramHttpCookie.getMaxAge() != 0L)
      {
        this.cookieJar.add(paramHttpCookie);
        if (paramHttpCookie.getDomain() != null) {
          addIndex(this.domainIndex, paramHttpCookie.getDomain(), paramHttpCookie);
        }
        if (paramURI != null) {
          addIndex(this.uriIndex, getEffectiveURI(paramURI), paramHttpCookie);
        }
      }
      this.lock.unlock();
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public List<HttpCookie> get(URI paramURI)
  {
    if (paramURI == null) {
      throw new NullPointerException("uri is null");
    }
    ArrayList localArrayList = new ArrayList();
    boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
    this.lock.lock();
    try
    {
      getInternal1(localArrayList, this.domainIndex, paramURI.getHost(), bool);
      getInternal2(localArrayList, this.uriIndex, getEffectiveURI(paramURI), bool);
    }
    finally
    {
      this.lock.unlock();
    }
    return localArrayList;
  }
  
  public List<HttpCookie> getCookies()
  {
    this.lock.lock();
    List localList;
    try
    {
      Iterator localIterator = this.cookieJar.iterator();
      while (localIterator.hasNext()) {
        if (((HttpCookie)localIterator.next()).hasExpired()) {
          localIterator.remove();
        }
      }
      localList = Collections.unmodifiableList(this.cookieJar);
      this.lock.unlock();
    }
    finally
    {
      localList = Collections.unmodifiableList(this.cookieJar);
      this.lock.unlock();
    }
    return localList;
  }
  
  public List<URI> getURIs()
  {
    ArrayList localArrayList = new ArrayList();
    this.lock.lock();
    try
    {
      Iterator localIterator = this.uriIndex.keySet().iterator();
      while (localIterator.hasNext())
      {
        URI localURI = (URI)localIterator.next();
        List localList = (List)this.uriIndex.get(localURI);
        if ((localList == null) || (localList.size() == 0)) {
          localIterator.remove();
        }
      }
    }
    finally
    {
      localArrayList.addAll(this.uriIndex.keySet());
      this.lock.unlock();
    }
    return localArrayList;
  }
  
  public boolean remove(URI paramURI, HttpCookie paramHttpCookie)
  {
    if (paramHttpCookie == null) {
      throw new NullPointerException("cookie is null");
    }
    boolean bool = false;
    this.lock.lock();
    try
    {
      bool = this.cookieJar.remove(paramHttpCookie);
    }
    finally
    {
      this.lock.unlock();
    }
    return bool;
  }
  
  public boolean removeAll()
  {
    this.lock.lock();
    try
    {
      if (this.cookieJar.isEmpty())
      {
        boolean bool = false;
        return bool;
      }
      this.cookieJar.clear();
      this.domainIndex.clear();
      this.uriIndex.clear();
    }
    finally
    {
      this.lock.unlock();
    }
    return true;
  }
  
  private boolean netscapeDomainMatches(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    boolean bool = ".local".equalsIgnoreCase(paramString1);
    int i = paramString1.indexOf('.');
    if (i == 0) {
      i = paramString1.indexOf('.', 1);
    }
    if ((!bool) && ((i == -1) || (i == paramString1.length() - 1))) {
      return false;
    }
    int j = paramString2.indexOf('.');
    if ((j == -1) && (bool)) {
      return true;
    }
    int k = paramString1.length();
    int m = paramString2.length() - k;
    if (m == 0) {
      return paramString2.equalsIgnoreCase(paramString1);
    }
    if (m > 0)
    {
      String str1 = paramString2.substring(0, m);
      String str2 = paramString2.substring(m);
      return str2.equalsIgnoreCase(paramString1);
    }
    if (m == -1) {
      return (paramString1.charAt(0) == '.') && (paramString2.equalsIgnoreCase(paramString1.substring(1)));
    }
    return false;
  }
  
  private void getInternal1(List<HttpCookie> paramList, Map<String, List<HttpCookie>> paramMap, String paramString, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      Iterator localIterator2 = localList.iterator();
      HttpCookie localHttpCookie;
      while (localIterator2.hasNext())
      {
        localHttpCookie = (HttpCookie)localIterator2.next();
        if (((localHttpCookie.getVersion() == 0) && (netscapeDomainMatches(str, paramString))) || ((localHttpCookie.getVersion() == 1) && (HttpCookie.domainMatches(str, paramString)))) {
          if (this.cookieJar.indexOf(localHttpCookie) != -1)
          {
            if (!localHttpCookie.hasExpired())
            {
              if (((paramBoolean) || (!localHttpCookie.getSecure())) && (!paramList.contains(localHttpCookie))) {
                paramList.add(localHttpCookie);
              }
            }
            else {
              localArrayList.add(localHttpCookie);
            }
          }
          else {
            localArrayList.add(localHttpCookie);
          }
        }
      }
      localIterator2 = localArrayList.iterator();
      while (localIterator2.hasNext())
      {
        localHttpCookie = (HttpCookie)localIterator2.next();
        localList.remove(localHttpCookie);
        this.cookieJar.remove(localHttpCookie);
      }
      localArrayList.clear();
    }
  }
  
  private <T> void getInternal2(List<HttpCookie> paramList, Map<T, List<HttpCookie>> paramMap, Comparable<T> paramComparable, boolean paramBoolean)
  {
    Iterator localIterator1 = paramMap.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = localIterator1.next();
      if (paramComparable.compareTo(localObject) == 0)
      {
        List localList = (List)paramMap.get(localObject);
        if (localList != null)
        {
          Iterator localIterator2 = localList.iterator();
          while (localIterator2.hasNext())
          {
            HttpCookie localHttpCookie = (HttpCookie)localIterator2.next();
            if (this.cookieJar.indexOf(localHttpCookie) != -1)
            {
              if (!localHttpCookie.hasExpired())
              {
                if (((paramBoolean) || (!localHttpCookie.getSecure())) && (!paramList.contains(localHttpCookie))) {
                  paramList.add(localHttpCookie);
                }
              }
              else
              {
                localIterator2.remove();
                this.cookieJar.remove(localHttpCookie);
              }
            }
            else {
              localIterator2.remove();
            }
          }
        }
      }
    }
  }
  
  private <T> void addIndex(Map<T, List<HttpCookie>> paramMap, T paramT, HttpCookie paramHttpCookie)
  {
    if (paramT != null)
    {
      Object localObject = (List)paramMap.get(paramT);
      if (localObject != null)
      {
        ((List)localObject).remove(paramHttpCookie);
        ((List)localObject).add(paramHttpCookie);
      }
      else
      {
        localObject = new ArrayList();
        ((List)localObject).add(paramHttpCookie);
        paramMap.put(paramT, localObject);
      }
    }
  }
  
  private URI getEffectiveURI(URI paramURI)
  {
    URI localURI = null;
    try
    {
      localURI = new URI("http", paramURI.getHost(), null, null, null);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      localURI = paramURI;
    }
    return localURI;
  }
}
