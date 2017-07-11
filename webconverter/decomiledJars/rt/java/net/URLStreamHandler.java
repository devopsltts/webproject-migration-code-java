package java.net;

import java.io.IOException;
import sun.net.util.IPAddressUtil;

public abstract class URLStreamHandler
{
  public URLStreamHandler() {}
  
  protected abstract URLConnection openConnection(URL paramURL)
    throws IOException;
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy)
    throws IOException
  {
    throw new UnsupportedOperationException("Method not implemented.");
  }
  
  protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2)
  {
    String str1 = paramURL.getProtocol();
    String str2 = paramURL.getAuthority();
    String str3 = paramURL.getUserInfo();
    String str4 = paramURL.getHost();
    int i = paramURL.getPort();
    String str5 = paramURL.getPath();
    String str6 = paramURL.getQuery();
    String str7 = paramURL.getRef();
    int j = 0;
    int k = 0;
    if (paramInt1 < paramInt2)
    {
      m = paramString.indexOf('?');
      k = m == paramInt1 ? 1 : 0;
      if ((m != -1) && (m < paramInt2))
      {
        str6 = paramString.substring(m + 1, paramInt2);
        if (paramInt2 > m) {
          paramInt2 = m;
        }
        paramString = paramString.substring(0, m);
      }
    }
    int m = 0;
    int n = (paramInt1 <= paramInt2 - 4) && (paramString.charAt(paramInt1) == '/') && (paramString.charAt(paramInt1 + 1) == '/') && (paramString.charAt(paramInt1 + 2) == '/') && (paramString.charAt(paramInt1 + 3) == '/') ? 1 : 0;
    int i1;
    String str9;
    if ((n == 0) && (paramInt1 <= paramInt2 - 2) && (paramString.charAt(paramInt1) == '/') && (paramString.charAt(paramInt1 + 1) == '/'))
    {
      paramInt1 += 2;
      m = paramString.indexOf('/', paramInt1);
      if (m < 0)
      {
        m = paramString.indexOf('?', paramInt1);
        if (m < 0) {
          m = paramInt2;
        }
      }
      str4 = str2 = paramString.substring(paramInt1, m);
      i1 = str2.indexOf('@');
      if (i1 != -1)
      {
        str3 = str2.substring(0, i1);
        str4 = str2.substring(i1 + 1);
      }
      else
      {
        str3 = null;
      }
      if (str4 != null)
      {
        if ((str4.length() > 0) && (str4.charAt(0) == '['))
        {
          if ((i1 = str4.indexOf(']')) > 2)
          {
            str9 = str4;
            str4 = str9.substring(0, i1 + 1);
            if (!IPAddressUtil.isIPv6LiteralAddress(str4.substring(1, i1))) {
              throw new IllegalArgumentException("Invalid host: " + str4);
            }
            i = -1;
            if (str9.length() > i1 + 1) {
              if (str9.charAt(i1 + 1) == ':')
              {
                i1++;
                if (str9.length() > i1 + 1) {
                  i = Integer.parseInt(str9.substring(i1 + 1));
                }
              }
              else
              {
                throw new IllegalArgumentException("Invalid authority field: " + str2);
              }
            }
          }
          else
          {
            throw new IllegalArgumentException("Invalid authority field: " + str2);
          }
        }
        else
        {
          i1 = str4.indexOf(':');
          i = -1;
          if (i1 >= 0)
          {
            if (str4.length() > i1 + 1) {
              i = Integer.parseInt(str4.substring(i1 + 1));
            }
            str4 = str4.substring(0, i1);
          }
        }
      }
      else {
        str4 = "";
      }
      if (i < -1) {
        throw new IllegalArgumentException("Invalid port number :" + i);
      }
      paramInt1 = m;
      if ((str2 != null) && (str2.length() > 0)) {
        str5 = "";
      }
    }
    if (str4 == null) {
      str4 = "";
    }
    if (paramInt1 < paramInt2)
    {
      if (paramString.charAt(paramInt1) == '/')
      {
        str5 = paramString.substring(paramInt1, paramInt2);
      }
      else if ((str5 != null) && (str5.length() > 0))
      {
        j = 1;
        i1 = str5.lastIndexOf('/');
        str9 = "";
        if ((i1 == -1) && (str2 != null)) {
          str9 = "/";
        }
        str5 = str5.substring(0, i1 + 1) + str9 + paramString.substring(paramInt1, paramInt2);
      }
      else
      {
        String str8 = str2 != null ? "/" : "";
        str5 = str8 + paramString.substring(paramInt1, paramInt2);
      }
    }
    else if ((k != 0) && (str5 != null))
    {
      int i2 = str5.lastIndexOf('/');
      if (i2 < 0) {
        i2 = 0;
      }
      str5 = str5.substring(0, i2) + "/";
    }
    if (str5 == null) {
      str5 = "";
    }
    if (j != 0)
    {
      while ((m = str5.indexOf("/./")) >= 0) {
        str5 = str5.substring(0, m) + str5.substring(m + 2);
      }
      m = 0;
      while ((m = str5.indexOf("/../", m)) >= 0) {
        if ((m > 0) && ((paramInt2 = str5.lastIndexOf('/', m - 1)) >= 0) && (str5.indexOf("/../", paramInt2) != 0))
        {
          str5 = str5.substring(0, paramInt2) + str5.substring(m + 3);
          m = 0;
        }
        else
        {
          m += 3;
        }
      }
      while (str5.endsWith("/.."))
      {
        m = str5.indexOf("/..");
        if ((paramInt2 = str5.lastIndexOf('/', m - 1)) < 0) {
          break;
        }
        str5 = str5.substring(0, paramInt2 + 1);
      }
      if ((str5.startsWith("./")) && (str5.length() > 2)) {
        str5 = str5.substring(2);
      }
      if (str5.endsWith("/.")) {
        str5 = str5.substring(0, str5.length() - 1);
      }
    }
    setURL(paramURL, str1, str4, i, str2, str3, str5, str6, str7);
  }
  
  protected int getDefaultPort()
  {
    return -1;
  }
  
  protected boolean equals(URL paramURL1, URL paramURL2)
  {
    String str1 = paramURL1.getRef();
    String str2 = paramURL2.getRef();
    return ((str1 == str2) || ((str1 != null) && (str1.equals(str2)))) && (sameFile(paramURL1, paramURL2));
  }
  
  protected int hashCode(URL paramURL)
  {
    int i = 0;
    String str1 = paramURL.getProtocol();
    if (str1 != null) {
      i += str1.hashCode();
    }
    InetAddress localInetAddress = getHostAddress(paramURL);
    if (localInetAddress != null)
    {
      i += localInetAddress.hashCode();
    }
    else
    {
      str2 = paramURL.getHost();
      if (str2 != null) {
        i += str2.toLowerCase().hashCode();
      }
    }
    String str2 = paramURL.getFile();
    if (str2 != null) {
      i += str2.hashCode();
    }
    if (paramURL.getPort() == -1) {
      i += getDefaultPort();
    } else {
      i += paramURL.getPort();
    }
    String str3 = paramURL.getRef();
    if (str3 != null) {
      i += str3.hashCode();
    }
    return i;
  }
  
  protected boolean sameFile(URL paramURL1, URL paramURL2)
  {
    if ((paramURL1.getProtocol() != paramURL2.getProtocol()) && ((paramURL1.getProtocol() == null) || (!paramURL1.getProtocol().equalsIgnoreCase(paramURL2.getProtocol())))) {
      return false;
    }
    if ((paramURL1.getFile() != paramURL2.getFile()) && ((paramURL1.getFile() == null) || (!paramURL1.getFile().equals(paramURL2.getFile())))) {
      return false;
    }
    int i = paramURL1.getPort() != -1 ? paramURL1.getPort() : paramURL1.handler.getDefaultPort();
    int j = paramURL2.getPort() != -1 ? paramURL2.getPort() : paramURL2.handler.getDefaultPort();
    if (i != j) {
      return false;
    }
    return hostsEqual(paramURL1, paramURL2);
  }
  
  protected synchronized InetAddress getHostAddress(URL paramURL)
  {
    if (paramURL.hostAddress != null) {
      return paramURL.hostAddress;
    }
    String str = paramURL.getHost();
    if ((str == null) || (str.equals(""))) {
      return null;
    }
    try
    {
      paramURL.hostAddress = InetAddress.getByName(str);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      return null;
    }
    catch (SecurityException localSecurityException)
    {
      return null;
    }
    return paramURL.hostAddress;
  }
  
  protected boolean hostsEqual(URL paramURL1, URL paramURL2)
  {
    InetAddress localInetAddress1 = getHostAddress(paramURL1);
    InetAddress localInetAddress2 = getHostAddress(paramURL2);
    if ((localInetAddress1 != null) && (localInetAddress2 != null)) {
      return localInetAddress1.equals(localInetAddress2);
    }
    if ((paramURL1.getHost() != null) && (paramURL2.getHost() != null)) {
      return paramURL1.getHost().equalsIgnoreCase(paramURL2.getHost());
    }
    return (paramURL1.getHost() == null) && (paramURL2.getHost() == null);
  }
  
  protected String toExternalForm(URL paramURL)
  {
    int i = paramURL.getProtocol().length() + 1;
    if ((paramURL.getAuthority() != null) && (paramURL.getAuthority().length() > 0)) {
      i += 2 + paramURL.getAuthority().length();
    }
    if (paramURL.getPath() != null) {
      i += paramURL.getPath().length();
    }
    if (paramURL.getQuery() != null) {
      i += 1 + paramURL.getQuery().length();
    }
    if (paramURL.getRef() != null) {
      i += 1 + paramURL.getRef().length();
    }
    StringBuffer localStringBuffer = new StringBuffer(i);
    localStringBuffer.append(paramURL.getProtocol());
    localStringBuffer.append(":");
    if ((paramURL.getAuthority() != null) && (paramURL.getAuthority().length() > 0))
    {
      localStringBuffer.append("//");
      localStringBuffer.append(paramURL.getAuthority());
    }
    if (paramURL.getPath() != null) {
      localStringBuffer.append(paramURL.getPath());
    }
    if (paramURL.getQuery() != null)
    {
      localStringBuffer.append('?');
      localStringBuffer.append(paramURL.getQuery());
    }
    if (paramURL.getRef() != null)
    {
      localStringBuffer.append("#");
      localStringBuffer.append(paramURL.getRef());
    }
    return localStringBuffer.toString();
  }
  
  protected void setURL(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    if (this != paramURL.handler) {
      throw new SecurityException("handler for url different from this handler");
    }
    paramURL.set(paramURL.getProtocol(), paramString2, paramInt, paramString3, paramString4, paramString5, paramString6, paramString7);
  }
  
  @Deprecated
  protected void setURL(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4)
  {
    String str1 = null;
    String str2 = null;
    if ((paramString2 != null) && (paramString2.length() != 0))
    {
      str1 = paramString2 + ":" + paramInt;
      int i = paramString2.lastIndexOf('@');
      if (i != -1)
      {
        str2 = paramString2.substring(0, i);
        paramString2 = paramString2.substring(i + 1);
      }
    }
    String str3 = null;
    String str4 = null;
    if (paramString3 != null)
    {
      int j = paramString3.lastIndexOf('?');
      if (j != -1)
      {
        str4 = paramString3.substring(j + 1);
        str3 = paramString3.substring(0, j);
      }
      else
      {
        str3 = paramString3;
      }
    }
    setURL(paramURL, paramString1, paramString2, paramInt, str1, str2, str3, str4, paramString4);
  }
}
