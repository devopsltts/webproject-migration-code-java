package com.sun.java.browser.net;

public abstract interface ProxyInfo
{
  public abstract String getHost();
  
  public abstract int getPort();
  
  public abstract boolean isSocks();
}
