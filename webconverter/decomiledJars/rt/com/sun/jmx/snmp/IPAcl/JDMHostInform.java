package com.sun.jmx.snmp.IPAcl;

class JDMHostInform
  extends SimpleNode
{
  protected String name = "";
  
  JDMHostInform(int paramInt)
  {
    super(paramInt);
  }
  
  JDMHostInform(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMHostInform(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMHostInform(paramParser, paramInt);
  }
}
