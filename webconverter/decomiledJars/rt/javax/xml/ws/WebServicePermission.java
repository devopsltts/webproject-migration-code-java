package javax.xml.ws;

import java.security.BasicPermission;

public final class WebServicePermission
  extends BasicPermission
{
  private static final long serialVersionUID = -146474640053770988L;
  
  public WebServicePermission(String paramString)
  {
    super(paramString);
  }
  
  public WebServicePermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}
