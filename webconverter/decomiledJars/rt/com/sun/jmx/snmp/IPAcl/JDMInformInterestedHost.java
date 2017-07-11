package com.sun.jmx.snmp.IPAcl;

class JDMInformInterestedHost
  extends SimpleNode
{
  JDMInformInterestedHost(int paramInt)
  {
    super(paramInt);
  }
  
  JDMInformInterestedHost(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMInformInterestedHost(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMInformInterestedHost(paramParser, paramInt);
  }
}
