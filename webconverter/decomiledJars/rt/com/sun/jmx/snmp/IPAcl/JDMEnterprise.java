package com.sun.jmx.snmp.IPAcl;

class JDMEnterprise
  extends SimpleNode
{
  protected String enterprise = "";
  
  JDMEnterprise(int paramInt)
  {
    super(paramInt);
  }
  
  JDMEnterprise(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMEnterprise(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMEnterprise(paramParser, paramInt);
  }
}
