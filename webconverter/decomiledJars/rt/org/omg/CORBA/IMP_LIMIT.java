package org.omg.CORBA;

public final class IMP_LIMIT
  extends SystemException
{
  public IMP_LIMIT()
  {
    this("");
  }
  
  public IMP_LIMIT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public IMP_LIMIT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public IMP_LIMIT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
