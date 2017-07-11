package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;

public class Uri
{
  protected String uri;
  protected String scheme;
  protected String host = null;
  protected int port = -1;
  protected boolean hasAuthority;
  protected String path;
  protected String query = null;
  
  public Uri(String paramString)
    throws MalformedURLException
  {
    init(paramString);
  }
  
  protected Uri() {}
  
  protected void init(String paramString)
    throws MalformedURLException
  {
    this.uri = paramString;
    parse(paramString);
  }
  
  public String getScheme()
  {
    return this.scheme;
  }
  
  public String getHost()
  {
    return this.host;
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public String getPath()
  {
    return this.path;
  }
  
  public String getQuery()
  {
    return this.query;
  }
  
  public String toString()
  {
    return this.uri;
  }
  
  private void parse(String paramString)
    throws MalformedURLException
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      throw new MalformedURLException("Invalid URI: " + paramString);
    }
    this.scheme = paramString.substring(0, i);
    i++;
    this.hasAuthority = paramString.startsWith("//", i);
    if (this.hasAuthority)
    {
      i += 2;
      j = paramString.indexOf('/', i);
      if (j < 0) {
        j = paramString.length();
      }
      int k;
      if (paramString.startsWith("[", i))
      {
        k = paramString.indexOf(']', i + 1);
        if ((k < 0) || (k > j)) {
          throw new MalformedURLException("Invalid URI: " + paramString);
        }
        this.host = paramString.substring(i, k + 1);
        i = k + 1;
      }
      else
      {
        k = paramString.indexOf(':', i);
        int m = (k < 0) || (k > j) ? j : k;
        if (i < m) {
          this.host = paramString.substring(i, m);
        }
        i = m;
      }
      if ((i + 1 < j) && (paramString.startsWith(":", i)))
      {
        i++;
        this.port = Integer.parseInt(paramString.substring(i, j));
      }
      i = j;
    }
    int j = paramString.indexOf('?', i);
    if (j < 0)
    {
      this.path = paramString.substring(i);
    }
    else
    {
      this.path = paramString.substring(i, j);
      this.query = paramString.substring(j);
    }
  }
}
