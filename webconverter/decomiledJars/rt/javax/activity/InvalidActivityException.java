package javax.activity;

import java.rmi.RemoteException;

public class InvalidActivityException
  extends RemoteException
{
  public InvalidActivityException() {}
  
  public InvalidActivityException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidActivityException(Throwable paramThrowable)
  {
    this("", paramThrowable);
  }
  
  public InvalidActivityException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
