package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMNetMaskV6
  extends JDMNetMask
{
  private static final long serialVersionUID = 4505256777680576645L;
  
  public JDMNetMaskV6(int paramInt)
  {
    super(paramInt);
  }
  
  public JDMNetMaskV6(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  protected PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException
  {
    return new NetMaskImpl(this.address.toString(), Integer.parseInt(this.mask));
  }
}
