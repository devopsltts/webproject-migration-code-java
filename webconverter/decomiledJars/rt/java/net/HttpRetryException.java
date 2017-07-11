package java.net;

import java.io.IOException;

public class HttpRetryException
  extends IOException
{
  private static final long serialVersionUID = -9186022286469111381L;
  private int responseCode;
  private String location;
  
  public HttpRetryException(String paramString, int paramInt)
  {
    super(paramString);
    this.responseCode = paramInt;
  }
  
  public HttpRetryException(String paramString1, int paramInt, String paramString2)
  {
    super(paramString1);
    this.responseCode = paramInt;
    this.location = paramString2;
  }
  
  public int responseCode()
  {
    return this.responseCode;
  }
  
  public String getReason()
  {
    return super.getMessage();
  }
  
  public String getLocation()
  {
    return this.location;
  }
}
