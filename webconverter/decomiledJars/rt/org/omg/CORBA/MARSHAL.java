package org.omg.CORBA;

public final class MARSHAL
  extends SystemException
{
  public MARSHAL()
  {
    this("");
  }
  
  public MARSHAL(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public MARSHAL(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public MARSHAL(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}
