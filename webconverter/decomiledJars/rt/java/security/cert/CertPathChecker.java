package java.security.cert;

public abstract interface CertPathChecker
{
  public abstract void init(boolean paramBoolean)
    throws CertPathValidatorException;
  
  public abstract boolean isForwardCheckingSupported();
  
  public abstract void check(Certificate paramCertificate)
    throws CertPathValidatorException;
}
