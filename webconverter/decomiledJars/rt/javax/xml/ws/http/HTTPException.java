package javax.xml.ws.http;

import javax.xml.ws.ProtocolException;

public class HTTPException
  extends ProtocolException
{
  private int statusCode;
  
  public HTTPException(int paramInt)
  {
    this.statusCode = paramInt;
  }
  
  public int getStatusCode()
  {
    return this.statusCode;
  }
}
