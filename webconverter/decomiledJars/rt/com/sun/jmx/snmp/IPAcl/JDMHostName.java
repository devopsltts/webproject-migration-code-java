package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMHostName
  extends Host
{
  private static final long serialVersionUID = -9120082068923591122L;
  protected StringBuffer name = new StringBuffer();
  
  JDMHostName(int paramInt)
  {
    super(paramInt);
  }
  
  JDMHostName(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMHostName(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMHostName(paramParser, paramInt);
  }
  
  protected String getHname()
  {
    return this.name.toString();
  }
  
  protected PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException
  {
    return new PrincipalImpl(this.name.toString());
  }
}
