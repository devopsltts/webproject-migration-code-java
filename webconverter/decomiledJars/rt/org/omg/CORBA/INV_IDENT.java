package org.omg.CORBA;

public final class INV_IDENT
  extends SystemException
{
  public INV_IDENT()
  {
    this("");
  }
  
  public INV_IDENT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INV_IDENT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INV_IDENT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
