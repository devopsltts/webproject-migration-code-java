package com.sun.jmx.snmp.IPAcl;

class JDMTrapCommunity
  extends SimpleNode
{
  protected String community = "";
  
  JDMTrapCommunity(int paramInt)
  {
    super(paramInt);
  }
  
  JDMTrapCommunity(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMTrapCommunity(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMTrapCommunity(paramParser, paramInt);
  }
  
  public String getCommunity()
  {
    return this.community;
  }
}
