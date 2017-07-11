package java.security;

import java.io.Serializable;

public abstract interface Key
  extends Serializable
{
  public static final long serialVersionUID = 6603384152749567654L;
  
  public abstract String getAlgorithm();
  
  public abstract String getFormat();
  
  public abstract byte[] getEncoded();
}
