package com.sun.jmx.snmp.IPAcl;

class JDMInformCommunity
  extends SimpleNode
{
  protected String community = "";
  
  JDMInformCommunity(int paramInt)
  {
    super(paramInt);
  }
  
  JDMInformCommunity(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMInformCommunity(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMInformCommunity(paramParser, paramInt);
  }
  
  public String getCommunity()
  {
    return this.community;
  }
}
