package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

public class DnsUrl
  extends Uri
{
  private String domain;
  
  public static DnsUrl[] fromList(String paramString)
    throws MalformedURLException
  {
    DnsUrl[] arrayOfDnsUrl1 = new DnsUrl[(paramString.length() + 1) / 2];
    int i = 0;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    while (localStringTokenizer.hasMoreTokens()) {
      arrayOfDnsUrl1[(i++)] = new DnsUrl(localStringTokenizer.nextToken());
    }
    DnsUrl[] arrayOfDnsUrl2 = new DnsUrl[i];
    System.arraycopy(arrayOfDnsUrl1, 0, arrayOfDnsUrl2, 0, i);
    return arrayOfDnsUrl2;
  }
  
  public DnsUrl(String paramString)
    throws MalformedURLException
  {
    super(paramString);
    if (!this.scheme.equals("dns")) {
      throw new MalformedURLException(paramString + " is not a valid DNS pseudo-URL");
    }
    this.domain = (this.path.startsWith("/") ? this.path.substring(1) : this.path);
    this.domain = (this.domain.equals("") ? "." : UrlUtil.decode(this.domain));
  }
  
  public String getDomain()
  {
    return this.domain;
  }
}
