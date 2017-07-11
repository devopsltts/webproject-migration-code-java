package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMIpAddress
  extends Host
{
  private static final long serialVersionUID = 849729919486384484L;
  protected StringBuffer address = new StringBuffer();
  
  JDMIpAddress(int paramInt)
  {
    super(paramInt);
  }
  
  JDMIpAddress(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMIpAddress(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMIpAddress(paramParser, paramInt);
  }
  
  protected String getHname()
  {
    return this.address.toString();
  }
  
  protected PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException
  {
    return new PrincipalImpl(this.address.toString());
  }
}
