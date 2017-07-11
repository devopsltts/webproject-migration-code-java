package org.omg.CORBA;

public final class COMM_FAILURE
  extends SystemException
{
  public COMM_FAILURE()
  {
    this("");
  }
  
  public COMM_FAILURE(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public COMM_FAILURE(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public COMM_FAILURE(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
