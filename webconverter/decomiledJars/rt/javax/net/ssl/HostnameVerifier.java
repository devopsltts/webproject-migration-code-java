package javax.net.ssl;

public abstract interface HostnameVerifier
{
  public abstract boolean verify(String paramString, SSLSession paramSSLSession);
}
