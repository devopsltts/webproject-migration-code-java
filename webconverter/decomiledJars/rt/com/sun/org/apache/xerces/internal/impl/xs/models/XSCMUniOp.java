package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSCMUniOp
  extends CMNode
{
  private CMNode fChild;
  
  public XSCMUniOp(int paramInt, CMNode paramCMNode)
  {
    super(paramInt);
    if ((type() != 5) && (type() != 4) && (type() != 6)) {
      throw new RuntimeException("ImplementationMessages.VAL_UST");
    }
    this.fChild = paramCMNode;
  }
  
  final CMNode getChild()
  {
    return this.fChild;
  }
  
  public boolean isNullable()
  {
    if (type() == 6) {
      return this.fChild.isNullable();
    }
    return true;
  }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet)
  {
    paramCMStateSet.setTo(this.fChild.firstPos());
  }
  
  protected void calcLastPos(CMStateSet paramCMStateSet)
  {
    paramCMStateSet.setTo(this.fChild.lastPos());
  }
  
  public void setUserData(Object paramObject)
  {
    super.setUserData(paramObject);
    this.fChild.setUserData(paramObject);
  }
}
