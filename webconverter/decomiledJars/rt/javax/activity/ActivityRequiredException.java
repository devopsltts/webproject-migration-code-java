package javax.activity;

import java.rmi.RemoteException;

public class ActivityRequiredException
  extends RemoteException
{
  public ActivityRequiredException() {}
  
  public ActivityRequiredException(String paramString)
  {
    super(paramString);
  }
  
  public ActivityRequiredException(Throwable paramThrowable)
  {
    this("", paramThrowable);
  }
  
  public ActivityRequiredException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
