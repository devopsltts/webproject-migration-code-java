package sun.net.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLPermission;
import java.security.Permission;

public class URLUtil
{
  public URLUtil() {}
  
  public static String urlNoFragString(URL paramURL)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str1 = paramURL.getProtocol();
    if (str1 != null)
    {
      str1 = str1.toLowerCase();
      localStringBuilder.append(str1);
      localStringBuilder.append("://");
    }
    String str2 = paramURL.getHost();
    if (str2 != null)
    {
      str2 = str2.toLowerCase();
      localStringBuilder.append(str2);
      int i = paramURL.getPort();
      if (i == -1) {
        i = paramURL.getDefaultPort();
      }
      if (i != -1) {
        localStringBuilder.append(":").append(i);
      }
    }
    String str3 = paramURL.getFile();
    if (str3 != null) {
      localStringBuilder.append(str3);
    }
    return localStringBuilder.toString();
  }
  
  public static Permission getConnectPermission(URL paramURL)
    throws IOException
  {
    String str1 = paramURL.toString().toLowerCase();
    if ((str1.startsWith("http:")) || (str1.startsWith("https:"))) {
      return getURLConnectPermission(paramURL);
    }
    if ((str1.startsWith("jar:http:")) || (str1.startsWith("jar:https:")))
    {
      String str2 = paramURL.toString();
      int i = str2.indexOf("!/");
      str2 = str2.substring(4, i > -1 ? i : str2.length());
      URL localURL = new URL(str2);
      return getURLConnectPermission(localURL);
    }
    return paramURL.openConnection().getPermission();
  }
  
  private static Permission getURLConnectPermission(URL paramURL)
  {
    String str = paramURL.getProtocol() + "://" + paramURL.getAuthority() + paramURL.getPath();
    return new URLPermission(str);
  }
}
