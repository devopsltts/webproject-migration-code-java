package com.sun.net.httpserver;

import jdk.Exported;

@Exported
public abstract class Authenticator
{
  public Authenticator() {}
  
  public abstract Result authenticate(HttpExchange paramHttpExchange);
  
  @Exported
  public static class Failure
    extends Authenticator.Result
  {
    private int responseCode;
    
    public Failure(int paramInt)
    {
      this.responseCode = paramInt;
    }
    
    public int getResponseCode()
    {
      return this.responseCode;
    }
  }
  
  public static abstract class Result
  {
    public Result() {}
  }
  
  @Exported
  public static class Retry
    extends Authenticator.Result
  {
    private int responseCode;
    
    public Retry(int paramInt)
    {
      this.responseCode = paramInt;
    }
    
    public int getResponseCode()
    {
      return this.responseCode;
    }
  }
  
  @Exported
  public static class Success
    extends Authenticator.Result
  {
    private HttpPrincipal principal;
    
    public Success(HttpPrincipal paramHttpPrincipal)
    {
      this.principal = paramHttpPrincipal;
    }
    
    public HttpPrincipal getPrincipal()
    {
      return this.principal;
    }
  }
}
