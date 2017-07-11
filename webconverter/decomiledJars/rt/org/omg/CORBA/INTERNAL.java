package org.omg.CORBA;

public final class INTERNAL
  extends SystemException
{
  public INTERNAL()
  {
    this("");
  }
  
  public INTERNAL(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INTERNAL(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INTERNAL(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
