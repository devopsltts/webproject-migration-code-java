package org.omg.CORBA;

public final class OBJ_ADAPTER
  extends SystemException
{
  public OBJ_ADAPTER()
  {
    this("");
  }
  
  public OBJ_ADAPTER(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public OBJ_ADAPTER(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public OBJ_ADAPTER(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
