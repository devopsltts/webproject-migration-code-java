package com.sun.jmx.snmp.IPAcl;

class JDMAccess
  extends SimpleNode
{
  protected int access = -1;
  
  JDMAccess(int paramInt)
  {
    super(paramInt);
  }
  
  JDMAccess(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMAccess(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMAccess(paramParser, paramInt);
  }
  
  protected void putPermission(AclEntryImpl paramAclEntryImpl)
  {
    if (this.access == 17) {
      paramAclEntryImpl.addPermission(SnmpAcl.getREAD());
    }
    if (this.access == 18)
    {
      paramAclEntryImpl.addPermission(SnmpAcl.getREAD());
      paramAclEntryImpl.addPermission(SnmpAcl.getWRITE());
    }
  }
}
