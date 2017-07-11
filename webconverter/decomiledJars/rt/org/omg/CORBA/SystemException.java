package org.omg.CORBA;

public abstract class SystemException
  extends RuntimeException
{
  public int minor;
  public CompletionStatus completed;
  
  protected SystemException(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString);
    this.minor = paramInt;
    this.completed = paramCompletionStatus;
  }
  
  public String toString()
  {
    String str = super.toString();
    int i = this.minor & 0xF000;
    switch (i)
    {
    case 1330446336: 
      str = str + "  vmcid: OMG";
      break;
    case 1398079488: 
      str = str + "  vmcid: SUN";
      break;
    default: 
      str = str + "  vmcid: 0x" + Integer.toHexString(i);
    }
    int j = this.minor & 0xFFF;
    str = str + "  minor code: " + j;
    switch (this.completed.value())
    {
    case 0: 
      str = str + "  completed: Yes";
      break;
    case 1: 
      str = str + "  completed: No";
      break;
    case 2: 
    default: 
      str = str + " completed: Maybe";
    }
    return str;
  }
}
