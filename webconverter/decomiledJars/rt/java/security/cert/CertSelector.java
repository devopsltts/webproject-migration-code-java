package java.security.cert;

public abstract interface CertSelector
  extends Cloneable
{
  public abstract boolean match(Certificate paramCertificate);
  
  public abstract Object clone();
}
