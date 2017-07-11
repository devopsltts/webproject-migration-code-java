package com.sun.xml.internal.ws.client;

public enum ContentNegotiation
{
  none,  pessimistic,  optimistic;
  
  public static final String PROPERTY = "com.sun.xml.internal.ws.client.ContentNegotiation";
  
  private ContentNegotiation() {}
  
  public static ContentNegotiation obtainFromSystemProperty()
  {
    try
    {
      String str = System.getProperty("com.sun.xml.internal.ws.client.ContentNegotiation");
      if (str == null) {
        return none;
      }
      return valueOf(str);
    }
    catch (Exception localException) {}
    return none;
  }
}
