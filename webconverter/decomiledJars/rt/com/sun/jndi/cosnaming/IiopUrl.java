package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Name;
import javax.naming.NamingException;

public final class IiopUrl
{
  private static final int DEFAULT_IIOPNAME_PORT = 9999;
  private static final int DEFAULT_IIOP_PORT = 900;
  private static final String DEFAULT_HOST = "localhost";
  private Vector<Address> addresses;
  private String stringName;
  
  public Vector<Address> getAddresses()
  {
    return this.addresses;
  }
  
  public String getStringName()
  {
    return this.stringName;
  }
  
  public Name getCosName()
    throws NamingException
  {
    return CNCtx.parser.parse(this.stringName);
  }
  
  public IiopUrl(String paramString)
    throws MalformedURLException
  {
    boolean bool;
    int i;
    if (paramString.startsWith("iiopname://"))
    {
      bool = false;
      i = 11;
    }
    else if (paramString.startsWith("iiop://"))
    {
      bool = true;
      i = 7;
    }
    else
    {
      throw new MalformedURLException("Invalid iiop/iiopname URL: " + paramString);
    }
    int j = paramString.indexOf('/', i);
    if (j < 0)
    {
      j = paramString.length();
      this.stringName = "";
    }
    else
    {
      this.stringName = UrlUtil.decode(paramString.substring(j + 1));
    }
    this.addresses = new Vector(3);
    if (bool)
    {
      this.addresses.addElement(new Address(paramString.substring(i, j), bool));
    }
    else
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString.substring(i, j), ",");
      while (localStringTokenizer.hasMoreTokens()) {
        this.addresses.addElement(new Address(localStringTokenizer.nextToken(), bool));
      }
      if (this.addresses.size() == 0) {
        this.addresses.addElement(new Address("", bool));
      }
    }
  }
  
  public static class Address
  {
    public int port = -1;
    public int major;
    public int minor;
    public String host;
    
    public Address(String paramString, boolean paramBoolean)
      throws MalformedURLException
    {
      int j;
      if ((paramBoolean) || ((j = paramString.indexOf('@')) < 0))
      {
        this.major = 1;
        this.minor = 0;
        i = 0;
      }
      else
      {
        k = paramString.indexOf('.');
        if (k < 0) {
          throw new MalformedURLException("invalid version: " + paramString);
        }
        try
        {
          this.major = Integer.parseInt(paramString.substring(0, k));
          this.minor = Integer.parseInt(paramString.substring(k + 1, j));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new MalformedURLException("Nonnumeric version: " + paramString);
        }
        i = j + 1;
      }
      int k = paramString.indexOf('/', i);
      if (k < 0) {
        k = paramString.length();
      }
      int m;
      if (paramString.startsWith("[", i))
      {
        m = paramString.indexOf(']', i + 1);
        if ((m < 0) || (m > k)) {
          throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + paramString);
        }
        this.host = paramString.substring(i, m + 1);
        i = m + 1;
      }
      else
      {
        m = paramString.indexOf(':', i);
        int n = (m < 0) || (m > k) ? k : m;
        if (i < n) {
          this.host = paramString.substring(i, n);
        }
        i = n;
      }
      if (i + 1 < k) {
        if (paramString.startsWith(":", i))
        {
          i++;
          this.port = Integer.parseInt(paramString.substring(i, k));
        }
        else
        {
          throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + paramString);
        }
      }
      int i = k;
      if (("".equals(this.host)) || (this.host == null)) {
        this.host = "localhost";
      }
      if (this.port == -1) {
        this.port = (paramBoolean ? 900 : 9999);
      }
    }
  }
}
