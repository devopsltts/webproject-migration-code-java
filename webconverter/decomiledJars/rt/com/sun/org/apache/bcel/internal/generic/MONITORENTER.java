package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class MONITORENTER
  extends Instruction
  implements ExceptionThrower, StackConsumer
{
  public MONITORENTER()
  {
    super((short)194, (short)1);
  }
  
  public Class[] getExceptions()
  {
    return new Class[] { ExceptionConstants.NULL_POINTER_EXCEPTION };
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitMONITORENTER(this);
  }
}
