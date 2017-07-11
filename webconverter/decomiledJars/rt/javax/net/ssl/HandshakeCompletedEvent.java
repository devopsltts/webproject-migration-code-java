package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.EventObject;

public class HandshakeCompletedEvent
  extends EventObject
{
  private static final long serialVersionUID = 7914963744257769778L;
  private transient SSLSession session;
  
  public HandshakeCompletedEvent(SSLSocket paramSSLSocket, SSLSession paramSSLSession)
  {
    super(paramSSLSocket);
    this.session = paramSSLSession;
  }
  
  public SSLSession getSession()
  {
    return this.session;
  }
  
  public String getCipherSuite()
  {
    return this.session.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates()
  {
    return this.session.getLocalCertificates();
  }
  
  public Certificate[] getPeerCertificates()
    throws SSLPeerUnverifiedException
  {
    return this.session.getPeerCertificates();
  }
  
  public javax.security.cert.X509Certificate[] getPeerCertificateChain()
    throws SSLPeerUnverifiedException
  {
    return this.session.getPeerCertificateChain();
  }
  
  public Principal getPeerPrincipal()
    throws SSLPeerUnverifiedException
  {
    Object localObject;
    try
    {
      localObject = this.session.getPeerPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      Certificate[] arrayOfCertificate = getPeerCertificates();
      localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
    }
    return localObject;
  }
  
  public Principal getLocalPrincipal()
  {
    Object localObject;
    try
    {
      localObject = this.session.getLocalPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      localObject = null;
      Certificate[] arrayOfCertificate = getLocalCertificates();
      if (arrayOfCertificate != null) {
        localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
      }
    }
    return localObject;
  }
  
  public SSLSocket getSocket()
  {
    return (SSLSocket)getSource();
  }
}
