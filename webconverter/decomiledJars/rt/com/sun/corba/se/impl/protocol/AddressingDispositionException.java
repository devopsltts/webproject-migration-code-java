package com.sun.corba.se.impl.protocol;

public class AddressingDispositionException
  extends RuntimeException
{
  private short expectedAddrDisp = 0;
  
  public AddressingDispositionException(short paramShort)
  {
    this.expectedAddrDisp = paramShort;
  }
  
  public short expectedAddrDisp()
  {
    return this.expectedAddrDisp;
  }
}
