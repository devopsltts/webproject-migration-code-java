package org.omg.CORBA;

public final class NO_PERMISSION
  extends SystemException
{
  public NO_PERMISSION()
  {
    this("");
  }
  
  public NO_PERMISSION(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public NO_PERMISSION(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public NO_PERMISSION(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
