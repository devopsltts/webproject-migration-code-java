package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.PolicyNode;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.CertificatePoliciesExtension;
import sun.security.x509.CertificatePolicyId;
import sun.security.x509.CertificatePolicyMap;
import sun.security.x509.InhibitAnyPolicyExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.PolicyConstraintsExtension;
import sun.security.x509.PolicyInformation;
import sun.security.x509.PolicyMappingsExtension;
import sun.security.x509.X509CertImpl;

class PolicyChecker
  extends PKIXCertPathChecker
{
  private final Set<String> initPolicies;
  private final int certPathLen;
  private final boolean expPolicyRequired;
  private final boolean polMappingInhibited;
  private final boolean anyPolicyInhibited;
  private final boolean rejectPolicyQualifiers;
  private PolicyNodeImpl rootNode;
  private int explicitPolicy;
  private int policyMapping;
  private int inhibitAnyPolicy;
  private int certIndex;
  private Set<String> supportedExts;
  private static final Debug debug = Debug.getInstance("certpath");
  static final String ANY_POLICY = "2.5.29.32.0";
  
  PolicyChecker(Set<String> paramSet, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, PolicyNodeImpl paramPolicyNodeImpl)
  {
    if (paramSet.isEmpty())
    {
      this.initPolicies = new HashSet(1);
      this.initPolicies.add("2.5.29.32.0");
    }
    else
    {
      this.initPolicies = new HashSet(paramSet);
    }
    this.certPathLen = paramInt;
    this.expPolicyRequired = paramBoolean1;
    this.polMappingInhibited = paramBoolean2;
    this.anyPolicyInhibited = paramBoolean3;
    this.rejectPolicyQualifiers = paramBoolean4;
    this.rootNode = paramPolicyNodeImpl;
  }
  
  public void init(boolean paramBoolean)
    throws CertPathValidatorException
  {
    if (paramBoolean) {
      throw new CertPathValidatorException("forward checking not supported");
    }
    this.certIndex = 1;
    this.explicitPolicy = (this.expPolicyRequired ? 0 : this.certPathLen + 1);
    this.policyMapping = (this.polMappingInhibited ? 0 : this.certPathLen + 1);
    this.inhibitAnyPolicy = (this.anyPolicyInhibited ? 0 : this.certPathLen + 1);
  }
  
  public boolean isForwardCheckingSupported()
  {
    return false;
  }
  
  public Set<String> getSupportedExtensions()
  {
    if (this.supportedExts == null)
    {
      this.supportedExts = new HashSet(4);
      this.supportedExts.add(PKIXExtensions.CertificatePolicies_Id.toString());
      this.supportedExts.add(PKIXExtensions.PolicyMappings_Id.toString());
      this.supportedExts.add(PKIXExtensions.PolicyConstraints_Id.toString());
      this.supportedExts.add(PKIXExtensions.InhibitAnyPolicy_Id.toString());
      this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
    }
    return this.supportedExts;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection)
    throws CertPathValidatorException
  {
    checkPolicy((X509Certificate)paramCertificate);
    if ((paramCollection != null) && (!paramCollection.isEmpty()))
    {
      paramCollection.remove(PKIXExtensions.CertificatePolicies_Id.toString());
      paramCollection.remove(PKIXExtensions.PolicyMappings_Id.toString());
      paramCollection.remove(PKIXExtensions.PolicyConstraints_Id.toString());
      paramCollection.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
    }
  }
  
  private void checkPolicy(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    String str = "certificate policies";
    if (debug != null)
    {
      debug.println("PolicyChecker.checkPolicy() ---checking " + str + "...");
      debug.println("PolicyChecker.checkPolicy() certIndex = " + this.certIndex);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: explicitPolicy = " + this.explicitPolicy);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyMapping = " + this.policyMapping);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyTree = " + this.rootNode);
    }
    X509CertImpl localX509CertImpl = null;
    try
    {
      localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    }
    catch (CertificateException localCertificateException)
    {
      throw new CertPathValidatorException(localCertificateException);
    }
    boolean bool = this.certIndex == this.certPathLen;
    this.rootNode = processPolicies(this.certIndex, this.initPolicies, this.explicitPolicy, this.policyMapping, this.inhibitAnyPolicy, this.rejectPolicyQualifiers, this.rootNode, localX509CertImpl, bool);
    if (!bool)
    {
      this.explicitPolicy = mergeExplicitPolicy(this.explicitPolicy, localX509CertImpl, bool);
      this.policyMapping = mergePolicyMapping(this.policyMapping, localX509CertImpl);
      this.inhibitAnyPolicy = mergeInhibitAnyPolicy(this.inhibitAnyPolicy, localX509CertImpl);
    }
    this.certIndex += 1;
    if (debug != null)
    {
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: explicitPolicy = " + this.explicitPolicy);
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyMapping = " + this.policyMapping);
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyTree = " + this.rootNode);
      debug.println("PolicyChecker.checkPolicy() " + str + " verified");
    }
  }
  
  static int mergeExplicitPolicy(int paramInt, X509CertImpl paramX509CertImpl, boolean paramBoolean)
    throws CertPathValidatorException
  {
    if ((paramInt > 0) && (!X509CertImpl.isSelfIssued(paramX509CertImpl))) {
      paramInt--;
    }
    try
    {
      PolicyConstraintsExtension localPolicyConstraintsExtension = paramX509CertImpl.getPolicyConstraintsExtension();
      if (localPolicyConstraintsExtension == null) {
        return paramInt;
      }
      int i = localPolicyConstraintsExtension.get("require").intValue();
      if (debug != null) {
        debug.println("PolicyChecker.mergeExplicitPolicy() require Index from cert = " + i);
      }
      if (!paramBoolean)
      {
        if ((i != -1) && ((paramInt == -1) || (i < paramInt))) {
          paramInt = i;
        }
      }
      else if (i == 0) {
        paramInt = i;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null)
      {
        debug.println("PolicyChecker.mergeExplicitPolicy unexpected exception");
        localIOException.printStackTrace();
      }
      throw new CertPathValidatorException(localIOException);
    }
    return paramInt;
  }
  
  static int mergePolicyMapping(int paramInt, X509CertImpl paramX509CertImpl)
    throws CertPathValidatorException
  {
    if ((paramInt > 0) && (!X509CertImpl.isSelfIssued(paramX509CertImpl))) {
      paramInt--;
    }
    try
    {
      PolicyConstraintsExtension localPolicyConstraintsExtension = paramX509CertImpl.getPolicyConstraintsExtension();
      if (localPolicyConstraintsExtension == null) {
        return paramInt;
      }
      int i = localPolicyConstraintsExtension.get("inhibit").intValue();
      if (debug != null) {
        debug.println("PolicyChecker.mergePolicyMapping() inhibit Index from cert = " + i);
      }
      if ((i != -1) && ((paramInt == -1) || (i < paramInt))) {
        paramInt = i;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null)
      {
        debug.println("PolicyChecker.mergePolicyMapping unexpected exception");
        localIOException.printStackTrace();
      }
      throw new CertPathValidatorException(localIOException);
    }
    return paramInt;
  }
  
  static int mergeInhibitAnyPolicy(int paramInt, X509CertImpl paramX509CertImpl)
    throws CertPathValidatorException
  {
    if ((paramInt > 0) && (!X509CertImpl.isSelfIssued(paramX509CertImpl))) {
      paramInt--;
    }
    try
    {
      InhibitAnyPolicyExtension localInhibitAnyPolicyExtension = (InhibitAnyPolicyExtension)paramX509CertImpl.getExtension(PKIXExtensions.InhibitAnyPolicy_Id);
      if (localInhibitAnyPolicyExtension == null) {
        return paramInt;
      }
      int i = localInhibitAnyPolicyExtension.get("skip_certs").intValue();
      if (debug != null) {
        debug.println("PolicyChecker.mergeInhibitAnyPolicy() skipCerts Index from cert = " + i);
      }
      if ((i != -1) && (i < paramInt)) {
        paramInt = i;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null)
      {
        debug.println("PolicyChecker.mergeInhibitAnyPolicy unexpected exception");
        localIOException.printStackTrace();
      }
      throw new CertPathValidatorException(localIOException);
    }
    return paramInt;
  }
  
  static PolicyNodeImpl processPolicies(int paramInt1, Set<String> paramSet, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, PolicyNodeImpl paramPolicyNodeImpl, X509CertImpl paramX509CertImpl, boolean paramBoolean2)
    throws CertPathValidatorException
  {
    boolean bool1 = false;
    PolicyNodeImpl localPolicyNodeImpl = null;
    Object localObject = new HashSet();
    if (paramPolicyNodeImpl == null) {
      localPolicyNodeImpl = null;
    } else {
      localPolicyNodeImpl = paramPolicyNodeImpl.copyTree();
    }
    CertificatePoliciesExtension localCertificatePoliciesExtension = paramX509CertImpl.getCertificatePoliciesExtension();
    if ((localCertificatePoliciesExtension != null) && (localPolicyNodeImpl != null))
    {
      bool1 = localCertificatePoliciesExtension.isCritical();
      if (debug != null) {
        debug.println("PolicyChecker.processPolicies() policiesCritical = " + bool1);
      }
      List localList;
      try
      {
        localList = localCertificatePoliciesExtension.get("policies");
      }
      catch (IOException localIOException)
      {
        throw new CertPathValidatorException("Exception while retrieving policyOIDs", localIOException);
      }
      if (debug != null) {
        debug.println("PolicyChecker.processPolicies() rejectPolicyQualifiers = " + paramBoolean1);
      }
      int i = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        PolicyInformation localPolicyInformation = (PolicyInformation)localIterator.next();
        String str = localPolicyInformation.getPolicyIdentifier().getIdentifier().toString();
        if (str.equals("2.5.29.32.0"))
        {
          i = 1;
          localObject = localPolicyInformation.getPolicyQualifiers();
        }
        else
        {
          if (debug != null) {
            debug.println("PolicyChecker.processPolicies() processing policy: " + str);
          }
          Set localSet = localPolicyInformation.getPolicyQualifiers();
          if ((!localSet.isEmpty()) && (paramBoolean1) && (bool1)) {
            throw new CertPathValidatorException("critical policy qualifiers present in certificate", null, null, -1, PKIXReason.INVALID_POLICY);
          }
          boolean bool2 = processParents(paramInt1, bool1, paramBoolean1, localPolicyNodeImpl, str, localSet, false);
          if (!bool2) {
            processParents(paramInt1, bool1, paramBoolean1, localPolicyNodeImpl, str, localSet, true);
          }
        }
      }
      if ((i != 0) && ((paramInt4 > 0) || ((!paramBoolean2) && (X509CertImpl.isSelfIssued(paramX509CertImpl)))))
      {
        if (debug != null) {
          debug.println("PolicyChecker.processPolicies() processing policy: 2.5.29.32.0");
        }
        processParents(paramInt1, bool1, paramBoolean1, localPolicyNodeImpl, "2.5.29.32.0", (Set)localObject, true);
      }
      localPolicyNodeImpl.prune(paramInt1);
      if (!localPolicyNodeImpl.getChildren().hasNext()) {
        localPolicyNodeImpl = null;
      }
    }
    else if (localCertificatePoliciesExtension == null)
    {
      if (debug != null) {
        debug.println("PolicyChecker.processPolicies() no policies present in cert");
      }
      localPolicyNodeImpl = null;
    }
    if ((localPolicyNodeImpl != null) && (!paramBoolean2)) {
      localPolicyNodeImpl = processPolicyMappings(paramX509CertImpl, paramInt1, paramInt3, localPolicyNodeImpl, bool1, (Set)localObject);
    }
    if ((localPolicyNodeImpl != null) && (!paramSet.contains("2.5.29.32.0")) && (localCertificatePoliciesExtension != null))
    {
      localPolicyNodeImpl = removeInvalidNodes(localPolicyNodeImpl, paramInt1, paramSet, localCertificatePoliciesExtension);
      if ((localPolicyNodeImpl != null) && (paramBoolean2)) {
        localPolicyNodeImpl = rewriteLeafNodes(paramInt1, paramSet, localPolicyNodeImpl);
      }
    }
    if (paramBoolean2) {
      paramInt2 = mergeExplicitPolicy(paramInt2, paramX509CertImpl, paramBoolean2);
    }
    if ((paramInt2 == 0) && (localPolicyNodeImpl == null)) {
      throw new CertPathValidatorException("non-null policy tree required and policy tree is null", null, null, -1, PKIXReason.INVALID_POLICY);
    }
    return localPolicyNodeImpl;
  }
  
  private static PolicyNodeImpl rewriteLeafNodes(int paramInt, Set<String> paramSet, PolicyNodeImpl paramPolicyNodeImpl)
  {
    Set localSet1 = paramPolicyNodeImpl.getPolicyNodesValid(paramInt, "2.5.29.32.0");
    if (localSet1.isEmpty()) {
      return paramPolicyNodeImpl;
    }
    PolicyNodeImpl localPolicyNodeImpl1 = (PolicyNodeImpl)localSet1.iterator().next();
    PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localPolicyNodeImpl1.getParent();
    localPolicyNodeImpl2.deleteChild(localPolicyNodeImpl1);
    HashSet localHashSet = new HashSet(paramSet);
    Iterator localIterator1 = paramPolicyNodeImpl.getPolicyNodes(paramInt).iterator();
    Object localObject;
    while (localIterator1.hasNext())
    {
      localObject = (PolicyNodeImpl)localIterator1.next();
      localHashSet.remove(((PolicyNodeImpl)localObject).getValidPolicy());
    }
    if (localHashSet.isEmpty())
    {
      paramPolicyNodeImpl.prune(paramInt);
      if (!paramPolicyNodeImpl.getChildren().hasNext()) {
        paramPolicyNodeImpl = null;
      }
    }
    else
    {
      boolean bool = localPolicyNodeImpl1.isCritical();
      localObject = localPolicyNodeImpl1.getPolicyQualifiers();
      Iterator localIterator2 = localHashSet.iterator();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        Set localSet2 = Collections.singleton(str);
        PolicyNodeImpl localPolicyNodeImpl3 = new PolicyNodeImpl(localPolicyNodeImpl2, str, (Set)localObject, bool, localSet2, false);
      }
    }
    return paramPolicyNodeImpl;
  }
  
  private static boolean processParents(int paramInt, boolean paramBoolean1, boolean paramBoolean2, PolicyNodeImpl paramPolicyNodeImpl, String paramString, Set<PolicyQualifierInfo> paramSet, boolean paramBoolean3)
    throws CertPathValidatorException
  {
    boolean bool = false;
    if (debug != null) {
      debug.println("PolicyChecker.processParents(): matchAny = " + paramBoolean3);
    }
    Set localSet1 = paramPolicyNodeImpl.getPolicyNodesExpected(paramInt - 1, paramString, paramBoolean3);
    Iterator localIterator1 = localSet1.iterator();
    while (localIterator1.hasNext())
    {
      PolicyNodeImpl localPolicyNodeImpl1 = (PolicyNodeImpl)localIterator1.next();
      if (debug != null) {
        debug.println("PolicyChecker.processParents() found parent:\n" + localPolicyNodeImpl1.asString());
      }
      bool = true;
      String str1 = localPolicyNodeImpl1.getValidPolicy();
      PolicyNodeImpl localPolicyNodeImpl2 = null;
      HashSet localHashSet = null;
      if (paramString.equals("2.5.29.32.0"))
      {
        Set localSet2 = localPolicyNodeImpl1.getExpectedPolicies();
        Iterator localIterator2 = localSet2.iterator();
        while (localIterator2.hasNext())
        {
          String str2 = (String)localIterator2.next();
          Iterator localIterator3 = localPolicyNodeImpl1.getChildren();
          for (;;)
          {
            if (!localIterator3.hasNext()) {
              break label272;
            }
            localObject = (PolicyNodeImpl)localIterator3.next();
            String str3 = ((PolicyNodeImpl)localObject).getValidPolicy();
            if (str2.equals(str3))
            {
              if (debug == null) {
                break;
              }
              debug.println(str3 + " in parent's " + "expected policy set already appears in " + "child node");
              break;
            }
          }
          label272:
          Object localObject = new HashSet();
          ((Set)localObject).add(str2);
          localPolicyNodeImpl2 = new PolicyNodeImpl(localPolicyNodeImpl1, str2, paramSet, paramBoolean1, (Set)localObject, false);
        }
      }
      else
      {
        localHashSet = new HashSet();
        localHashSet.add(paramString);
        localPolicyNodeImpl2 = new PolicyNodeImpl(localPolicyNodeImpl1, paramString, paramSet, paramBoolean1, localHashSet, false);
      }
    }
    return bool;
  }
  
  private static PolicyNodeImpl processPolicyMappings(X509CertImpl paramX509CertImpl, int paramInt1, int paramInt2, PolicyNodeImpl paramPolicyNodeImpl, boolean paramBoolean, Set<PolicyQualifierInfo> paramSet)
    throws CertPathValidatorException
  {
    PolicyMappingsExtension localPolicyMappingsExtension = paramX509CertImpl.getPolicyMappingsExtension();
    if (localPolicyMappingsExtension == null) {
      return paramPolicyNodeImpl;
    }
    if (debug != null) {
      debug.println("PolicyChecker.processPolicyMappings() inside policyMapping check");
    }
    List localList = null;
    try
    {
      localList = localPolicyMappingsExtension.get("map");
    }
    catch (IOException localIOException)
    {
      if (debug != null)
      {
        debug.println("PolicyChecker.processPolicyMappings() mapping exception");
        localIOException.printStackTrace();
      }
      throw new CertPathValidatorException("Exception while checking mapping", localIOException);
    }
    int i = 0;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      CertificatePolicyMap localCertificatePolicyMap = (CertificatePolicyMap)localIterator.next();
      String str1 = localCertificatePolicyMap.getIssuerIdentifier().getIdentifier().toString();
      String str2 = localCertificatePolicyMap.getSubjectIdentifier().getIdentifier().toString();
      if (debug != null)
      {
        debug.println("PolicyChecker.processPolicyMappings() issuerDomain = " + str1);
        debug.println("PolicyChecker.processPolicyMappings() subjectDomain = " + str2);
      }
      if (str1.equals("2.5.29.32.0")) {
        throw new CertPathValidatorException("encountered an issuerDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY);
      }
      if (str2.equals("2.5.29.32.0")) {
        throw new CertPathValidatorException("encountered a subjectDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY);
      }
      Set localSet = paramPolicyNodeImpl.getPolicyNodesValid(paramInt1, str1);
      Object localObject1;
      Object localObject2;
      PolicyNodeImpl localPolicyNodeImpl1;
      if (!localSet.isEmpty())
      {
        localObject1 = localSet.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (PolicyNodeImpl)((Iterator)localObject1).next();
          if ((paramInt2 > 0) || (paramInt2 == -1))
          {
            ((PolicyNodeImpl)localObject2).addExpectedPolicy(str2);
          }
          else if (paramInt2 == 0)
          {
            localPolicyNodeImpl1 = (PolicyNodeImpl)((PolicyNodeImpl)localObject2).getParent();
            if (debug != null) {
              debug.println("PolicyChecker.processPolicyMappings() before deleting: policy tree = " + paramPolicyNodeImpl);
            }
            localPolicyNodeImpl1.deleteChild((PolicyNode)localObject2);
            i = 1;
            if (debug != null) {
              debug.println("PolicyChecker.processPolicyMappings() after deleting: policy tree = " + paramPolicyNodeImpl);
            }
          }
        }
      }
      else if ((paramInt2 > 0) || (paramInt2 == -1))
      {
        localObject1 = paramPolicyNodeImpl.getPolicyNodesValid(paramInt1, "2.5.29.32.0");
        localObject2 = ((Set)localObject1).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localPolicyNodeImpl1 = (PolicyNodeImpl)((Iterator)localObject2).next();
          PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localPolicyNodeImpl1.getParent();
          HashSet localHashSet = new HashSet();
          localHashSet.add(str2);
          PolicyNodeImpl localPolicyNodeImpl3 = new PolicyNodeImpl(localPolicyNodeImpl2, str1, paramSet, paramBoolean, localHashSet, true);
        }
      }
    }
    if (i != 0)
    {
      paramPolicyNodeImpl.prune(paramInt1);
      if (!paramPolicyNodeImpl.getChildren().hasNext())
      {
        if (debug != null) {
          debug.println("setting rootNode to null");
        }
        paramPolicyNodeImpl = null;
      }
    }
    return paramPolicyNodeImpl;
  }
  
  private static PolicyNodeImpl removeInvalidNodes(PolicyNodeImpl paramPolicyNodeImpl, int paramInt, Set<String> paramSet, CertificatePoliciesExtension paramCertificatePoliciesExtension)
    throws CertPathValidatorException
  {
    List localList = null;
    try
    {
      localList = paramCertificatePoliciesExtension.get("policies");
    }
    catch (IOException localIOException)
    {
      throw new CertPathValidatorException("Exception while retrieving policyOIDs", localIOException);
    }
    int i = 0;
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      PolicyInformation localPolicyInformation = (PolicyInformation)localIterator1.next();
      String str = localPolicyInformation.getPolicyIdentifier().getIdentifier().toString();
      if (debug != null) {
        debug.println("PolicyChecker.processPolicies() processing policy second time: " + str);
      }
      Set localSet = paramPolicyNodeImpl.getPolicyNodesValid(paramInt, str);
      Iterator localIterator2 = localSet.iterator();
      while (localIterator2.hasNext())
      {
        PolicyNodeImpl localPolicyNodeImpl1 = (PolicyNodeImpl)localIterator2.next();
        PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localPolicyNodeImpl1.getParent();
        if ((localPolicyNodeImpl2.getValidPolicy().equals("2.5.29.32.0")) && (!paramSet.contains(str)) && (!str.equals("2.5.29.32.0")))
        {
          if (debug != null) {
            debug.println("PolicyChecker.processPolicies() before deleting: policy tree = " + paramPolicyNodeImpl);
          }
          localPolicyNodeImpl2.deleteChild(localPolicyNodeImpl1);
          i = 1;
          if (debug != null) {
            debug.println("PolicyChecker.processPolicies() after deleting: policy tree = " + paramPolicyNodeImpl);
          }
        }
      }
    }
    if (i != 0)
    {
      paramPolicyNodeImpl.prune(paramInt);
      if (!paramPolicyNodeImpl.getChildren().hasNext()) {
        paramPolicyNodeImpl = null;
      }
    }
    return paramPolicyNodeImpl;
  }
  
  PolicyNode getPolicyTree()
  {
    if (this.rootNode == null) {
      return null;
    }
    PolicyNodeImpl localPolicyNodeImpl = this.rootNode.copyTree();
    localPolicyNodeImpl.setImmutable();
    return localPolicyNodeImpl;
  }
}
