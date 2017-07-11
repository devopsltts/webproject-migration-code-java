package org.omg.CORBA;

public final class INITIALIZE
  extends SystemException
{
  public INITIALIZE()
  {
    this("");
  }
  
  public INITIALIZE(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INITIALIZE(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INITIALIZE(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
