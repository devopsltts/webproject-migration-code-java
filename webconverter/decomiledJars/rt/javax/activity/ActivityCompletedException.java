package javax.activity;

import java.rmi.RemoteException;

public class ActivityCompletedException
  extends RemoteException
{
  public ActivityCompletedException() {}
  
  public ActivityCompletedException(String paramString)
  {
    super(paramString);
  }
  
  public ActivityCompletedException(Throwable paramThrowable)
  {
    this("", paramThrowable);
  }
  
  public ActivityCompletedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
