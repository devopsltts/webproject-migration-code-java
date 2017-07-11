package java.rmi.server;

public abstract interface RMIFailureHandler
{
  public abstract boolean failure(Exception paramException);
}
