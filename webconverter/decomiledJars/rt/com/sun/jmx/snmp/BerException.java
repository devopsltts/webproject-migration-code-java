package com.sun.jmx.snmp;

public class BerException
  extends Exception
{
  private static final long serialVersionUID = 494709767137042951L;
  public static final int BAD_VERSION = 1;
  private int errorType = 0;
  
  public BerException()
  {
    this.errorType = 0;
  }
  
  public BerException(int paramInt)
  {
    this.errorType = paramInt;
  }
  
  public boolean isInvalidSnmpVersion()
  {
    return this.errorType == 1;
  }
}
