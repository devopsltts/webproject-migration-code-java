package org.omg.CORBA;

public final class BAD_INV_ORDER
  extends SystemException
{
  public BAD_INV_ORDER()
  {
    this("");
  }
  
  public BAD_INV_ORDER(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public BAD_INV_ORDER(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public BAD_INV_ORDER(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
