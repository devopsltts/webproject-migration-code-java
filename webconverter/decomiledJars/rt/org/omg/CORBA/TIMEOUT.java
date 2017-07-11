package org.omg.CORBA;

public final class TIMEOUT
  extends SystemException
{
  public TIMEOUT()
  {
    this("");
  }
  
  public TIMEOUT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public TIMEOUT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public TIMEOUT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
