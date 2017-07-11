package com.sun.xml.internal.ws.developer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class HttpConfigFeature
  extends WebServiceFeature
{
  public static final String ID = "http://jax-ws.java.net/features/http-config";
  private static final Constructor cookieManagerConstructor;
  private static final Object cookiePolicy;
  private final CookieHandler cookieJar;
  
  public HttpConfigFeature()
  {
    this(getInternalCookieHandler());
  }
  
  public HttpConfigFeature(CookieHandler paramCookieHandler)
  {
    this.enabled = true;
    this.cookieJar = paramCookieHandler;
  }
  
  private static CookieHandler getInternalCookieHandler()
  {
    try
    {
      return (CookieHandler)cookieManagerConstructor.newInstance(new Object[] { null, cookiePolicy });
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  public String getID()
  {
    return "http://jax-ws.java.net/features/http-config";
  }
  
  public CookieHandler getCookieHandler()
  {
    return this.cookieJar;
  }
  
  static
  {
    Constructor localConstructor;
    Object localObject;
    try
    {
      Class localClass1 = Class.forName("java.net.CookiePolicy");
      localClass2 = Class.forName("java.net.CookieStore");
      localConstructor = Class.forName("java.net.CookieManager").getConstructor(new Class[] { localClass2, localClass1 });
      localObject = localClass1.getField("ACCEPT_ALL").get(null);
    }
    catch (Exception localException1)
    {
      try
      {
        Class localClass2 = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookiePolicy");
        Class localClass3 = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookieStore");
        localConstructor = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookieManager").getConstructor(new Class[] { localClass3, localClass2 });
        localObject = localClass2.getField("ACCEPT_ALL").get(null);
      }
      catch (Exception localException2)
      {
        throw new WebServiceException(localException2);
      }
    }
    cookieManagerConstructor = localConstructor;
    cookiePolicy = localObject;
  }
}
