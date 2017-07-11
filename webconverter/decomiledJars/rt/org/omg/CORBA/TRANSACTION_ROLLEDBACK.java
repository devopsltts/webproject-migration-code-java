package org.omg.CORBA;

public final class TRANSACTION_ROLLEDBACK
  extends SystemException
{
  public TRANSACTION_ROLLEDBACK()
  {
    this("");
  }
  
  public TRANSACTION_ROLLEDBACK(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public TRANSACTION_ROLLEDBACK(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public TRANSACTION_ROLLEDBACK(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
