package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Enumeration;

public abstract interface InetAddressAcl
{
  public abstract String getName();
  
  public abstract boolean checkReadPermission(InetAddress paramInetAddress);
  
  public abstract boolean checkReadPermission(InetAddress paramInetAddress, String paramString);
  
  public abstract boolean checkCommunity(String paramString);
  
  public abstract boolean checkWritePermission(InetAddress paramInetAddress);
  
  public abstract boolean checkWritePermission(InetAddress paramInetAddress, String paramString);
  
  public abstract Enumeration<InetAddress> getTrapDestinations();
  
  public abstract Enumeration<String> getTrapCommunities(InetAddress paramInetAddress);
  
  public abstract Enumeration<InetAddress> getInformDestinations();
  
  public abstract Enumeration<String> getInformCommunities(InetAddress paramInetAddress);
}
