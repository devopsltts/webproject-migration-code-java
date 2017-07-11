package org.omg.CORBA;

public final class NO_IMPLEMENT
  extends SystemException
{
  public NO_IMPLEMENT()
  {
    this("");
  }
  
  public NO_IMPLEMENT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public NO_IMPLEMENT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public NO_IMPLEMENT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
