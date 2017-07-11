package sun.security.provider.certpath;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;

class ReverseState
  implements State
{
  private static final Debug debug = Debug.getInstance("certpath");
  X500Principal subjectDN;
  PublicKey pubKey;
  SubjectKeyIdentifierExtension subjKeyId;
  NameConstraintsExtension nc;
  int explicitPolicy;
  int policyMapping;
  int inhibitAnyPolicy;
  int certIndex;
  PolicyNodeImpl rootNode;
  int remainingCACerts;
  ArrayList<PKIXCertPathChecker> userCheckers;
  private boolean init = true;
  RevocationChecker revChecker;
  AlgorithmChecker algorithmChecker;
  UntrustedChecker untrustedChecker;
  TrustAnchor trustAnchor;
  boolean crlSign = true;
  
  ReverseState() {}
  
  public boolean isInitial()
  {
    return this.init;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("State [");
    localStringBuilder.append("\n  subjectDN of last cert: ").append(this.subjectDN);
    localStringBuilder.append("\n  subjectKeyIdentifier: ").append(String.valueOf(this.subjKeyId));
    localStringBuilder.append("\n  nameConstraints: ").append(String.valueOf(this.nc));
    localStringBuilder.append("\n  certIndex: ").append(this.certIndex);
    localStringBuilder.append("\n  explicitPolicy: ").append(this.explicitPolicy);
    localStringBuilder.append("\n  policyMapping:  ").append(this.policyMapping);
    localStringBuilder.append("\n  inhibitAnyPolicy:  ").append(this.inhibitAnyPolicy);
    localStringBuilder.append("\n  rootNode: ").append(this.rootNode);
    localStringBuilder.append("\n  remainingCACerts: ").append(this.remainingCACerts);
    localStringBuilder.append("\n  crlSign: ").append(this.crlSign);
    localStringBuilder.append("\n  init: ").append(this.init);
    localStringBuilder.append("\n]\n");
    return localStringBuilder.toString();
  }
  
  public void initState(PKIX.BuilderParams paramBuilderParams)
    throws CertPathValidatorException
  {
    int i = paramBuilderParams.maxPathLength();
    this.remainingCACerts = (i == -1 ? Integer.MAX_VALUE : i);
    if (paramBuilderParams.explicitPolicyRequired()) {
      this.explicitPolicy = 0;
    } else {
      this.explicitPolicy = (i == -1 ? i : i + 2);
    }
    if (paramBuilderParams.policyMappingInhibited()) {
      this.policyMapping = 0;
    } else {
      this.policyMapping = (i == -1 ? i : i + 2);
    }
    if (paramBuilderParams.anyPolicyInhibited()) {
      this.inhibitAnyPolicy = 0;
    } else {
      this.inhibitAnyPolicy = (i == -1 ? i : i + 2);
    }
    this.certIndex = 1;
    HashSet localHashSet = new HashSet(1);
    localHashSet.add("2.5.29.32.0");
    this.rootNode = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, localHashSet, false);
    this.userCheckers = new ArrayList(paramBuilderParams.certPathCheckers());
    Iterator localIterator = this.userCheckers.iterator();
    while (localIterator.hasNext())
    {
      PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
      localPKIXCertPathChecker.init(false);
    }
    this.crlSign = true;
    this.init = true;
  }
  
  public void updateState(TrustAnchor paramTrustAnchor, PKIX.BuilderParams paramBuilderParams)
    throws CertificateException, IOException, CertPathValidatorException
  {
    this.trustAnchor = paramTrustAnchor;
    X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
    if (localX509Certificate != null)
    {
      updateState(localX509Certificate);
    }
    else
    {
      X500Principal localX500Principal = paramTrustAnchor.getCA();
      updateState(paramTrustAnchor.getCAPublicKey(), localX500Principal);
    }
    int i = 0;
    Iterator localIterator = this.userCheckers.iterator();
    while (localIterator.hasNext())
    {
      PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
      if ((localPKIXCertPathChecker instanceof AlgorithmChecker))
      {
        ((AlgorithmChecker)localPKIXCertPathChecker).trySetTrustAnchor(paramTrustAnchor);
      }
      else if ((localPKIXCertPathChecker instanceof PKIXRevocationChecker))
      {
        if (i != 0) {
          throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
        }
        if ((localPKIXCertPathChecker instanceof RevocationChecker)) {
          ((RevocationChecker)localPKIXCertPathChecker).init(paramTrustAnchor, paramBuilderParams);
        }
        ((PKIXRevocationChecker)localPKIXCertPathChecker).init(false);
        i = 1;
      }
    }
    if ((paramBuilderParams.revocationEnabled()) && (i == 0))
    {
      this.revChecker = new RevocationChecker(paramTrustAnchor, paramBuilderParams);
      this.revChecker.init(false);
    }
    this.init = false;
  }
  
  private void updateState(PublicKey paramPublicKey, X500Principal paramX500Principal)
  {
    this.subjectDN = paramX500Principal;
    this.pubKey = paramPublicKey;
  }
  
  public void updateState(X509Certificate paramX509Certificate)
    throws CertificateException, IOException, CertPathValidatorException
  {
    if (paramX509Certificate == null) {
      return;
    }
    this.subjectDN = paramX509Certificate.getSubjectX500Principal();
    X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    PublicKey localPublicKey = paramX509Certificate.getPublicKey();
    if (PKIX.isDSAPublicKeyWithoutParams(localPublicKey)) {
      localPublicKey = BasicChecker.makeInheritedParamsKey(localPublicKey, this.pubKey);
    }
    this.pubKey = localPublicKey;
    if (this.init)
    {
      this.init = false;
      return;
    }
    this.subjKeyId = localX509CertImpl.getSubjectKeyIdentifierExtension();
    this.crlSign = RevocationChecker.certCanSignCrl(paramX509Certificate);
    if (this.nc != null)
    {
      this.nc.merge(localX509CertImpl.getNameConstraintsExtension());
    }
    else
    {
      this.nc = localX509CertImpl.getNameConstraintsExtension();
      if (this.nc != null) {
        this.nc = ((NameConstraintsExtension)this.nc.clone());
      }
    }
    this.explicitPolicy = PolicyChecker.mergeExplicitPolicy(this.explicitPolicy, localX509CertImpl, false);
    this.policyMapping = PolicyChecker.mergePolicyMapping(this.policyMapping, localX509CertImpl);
    this.inhibitAnyPolicy = PolicyChecker.mergeInhibitAnyPolicy(this.inhibitAnyPolicy, localX509CertImpl);
    this.certIndex += 1;
    this.remainingCACerts = ConstraintsChecker.mergeBasicConstraints(paramX509Certificate, this.remainingCACerts);
    this.init = false;
  }
  
  public boolean keyParamsNeeded()
  {
    return false;
  }
  
  public Object clone()
  {
    try
    {
      ReverseState localReverseState = (ReverseState)super.clone();
      localReverseState.userCheckers = ((ArrayList)this.userCheckers.clone());
      ListIterator localListIterator = localReverseState.userCheckers.listIterator();
      while (localListIterator.hasNext())
      {
        PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localListIterator.next();
        if ((localPKIXCertPathChecker instanceof Cloneable)) {
          localListIterator.set((PKIXCertPathChecker)localPKIXCertPathChecker.clone());
        }
      }
      if (this.nc != null) {
        localReverseState.nc = ((NameConstraintsExtension)this.nc.clone());
      }
      if (this.rootNode != null) {
        localReverseState.rootNode = this.rootNode.copyTree();
      }
      return localReverseState;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
}
