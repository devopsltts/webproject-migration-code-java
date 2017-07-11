package sun.security.krb5.internal;

public abstract interface SeqNumber
{
  public abstract void randInit();
  
  public abstract void init(int paramInt);
  
  public abstract int current();
  
  public abstract int next();
  
  public abstract int step();
}
