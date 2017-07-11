package sun.misc;

import java.net.HttpCookie;
import java.util.List;

public abstract interface JavaNetHttpCookieAccess
{
  public abstract List<HttpCookie> parse(String paramString);
  
  public abstract String header(HttpCookie paramHttpCookie);
}
