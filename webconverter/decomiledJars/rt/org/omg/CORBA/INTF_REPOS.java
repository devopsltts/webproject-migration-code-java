package org.omg.CORBA;

public final class INTF_REPOS
  extends SystemException
{
  public INTF_REPOS()
  {
    this("");
  }
  
  public INTF_REPOS(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INTF_REPOS(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INTF_REPOS(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
