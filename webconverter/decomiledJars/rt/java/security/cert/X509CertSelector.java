package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificatePoliciesExtension;
import sun.security.x509.CertificatePolicyId;
import sun.security.x509.CertificatePolicySet;
import sun.security.x509.DNSName;
import sun.security.x509.EDIPartyName;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralSubtree;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.IPAddressName;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.OIDName;
import sun.security.x509.OtherName;
import sun.security.x509.PolicyInformation;
import sun.security.x509.PrivateKeyUsageExtension;
import sun.security.x509.RFC822Name;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.URIName;
import sun.security.x509.X400Address;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509Key;

public class X509CertSelector
  implements CertSelector
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final ObjectIdentifier ANY_EXTENDED_KEY_USAGE = ObjectIdentifier.newInternal(new int[] { 2, 5, 29, 37, 0 });
  private BigInteger serialNumber;
  private X500Principal issuer;
  private X500Principal subject;
  private byte[] subjectKeyID;
  private byte[] authorityKeyID;
  private Date certificateValid;
  private Date privateKeyValid;
  private ObjectIdentifier subjectPublicKeyAlgID;
  private PublicKey subjectPublicKey;
  private byte[] subjectPublicKeyBytes;
  private boolean[] keyUsage;
  private Set<String> keyPurposeSet;
  private Set<ObjectIdentifier> keyPurposeOIDSet;
  private Set<List<?>> subjectAlternativeNames;
  private Set<GeneralNameInterface> subjectAlternativeGeneralNames;
  private CertificatePolicySet policy;
  private Set<String> policySet;
  private Set<List<?>> pathToNames;
  private Set<GeneralNameInterface> pathToGeneralNames;
  private NameConstraintsExtension nc;
  private byte[] ncBytes;
  private int basicConstraints = -1;
  private X509Certificate x509Cert;
  private boolean matchAllSubjectAltNames = true;
  private static final Boolean FALSE;
  private static final int PRIVATE_KEY_USAGE_ID = 0;
  private static final int SUBJECT_ALT_NAME_ID = 1;
  private static final int NAME_CONSTRAINTS_ID = 2;
  private static final int CERT_POLICIES_ID = 3;
  private static final int EXTENDED_KEY_USAGE_ID = 4;
  private static final int NUM_OF_EXTENSIONS = 5;
  private static final String[] EXTENSION_OIDS;
  static final int NAME_ANY = 0;
  static final int NAME_RFC822 = 1;
  static final int NAME_DNS = 2;
  static final int NAME_X400 = 3;
  static final int NAME_DIRECTORY = 4;
  static final int NAME_EDI = 5;
  static final int NAME_URI = 6;
  static final int NAME_IP = 7;
  static final int NAME_OID = 8;
  
  public X509CertSelector() {}
  
  public void setCertificate(X509Certificate paramX509Certificate)
  {
    this.x509Cert = paramX509Certificate;
  }
  
  public void setSerialNumber(BigInteger paramBigInteger)
  {
    this.serialNumber = paramBigInteger;
  }
  
  public void setIssuer(X500Principal paramX500Principal)
  {
    this.issuer = paramX500Principal;
  }
  
  public void setIssuer(String paramString)
    throws IOException
  {
    if (paramString == null) {
      this.issuer = null;
    } else {
      this.issuer = new X500Name(paramString).asX500Principal();
    }
  }
  
  public void setIssuer(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      this.issuer = (paramArrayOfByte == null ? null : new X500Principal(paramArrayOfByte));
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IOException("Invalid name", localIllegalArgumentException);
    }
  }
  
  public void setSubject(X500Principal paramX500Principal)
  {
    this.subject = paramX500Principal;
  }
  
  public void setSubject(String paramString)
    throws IOException
  {
    if (paramString == null) {
      this.subject = null;
    } else {
      this.subject = new X500Name(paramString).asX500Principal();
    }
  }
  
  public void setSubject(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      this.subject = (paramArrayOfByte == null ? null : new X500Principal(paramArrayOfByte));
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IOException("Invalid name", localIllegalArgumentException);
    }
  }
  
  public void setSubjectKeyIdentifier(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      this.subjectKeyID = null;
    } else {
      this.subjectKeyID = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public void setAuthorityKeyIdentifier(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      this.authorityKeyID = null;
    } else {
      this.authorityKeyID = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public void setCertificateValid(Date paramDate)
  {
    if (paramDate == null) {
      this.certificateValid = null;
    } else {
      this.certificateValid = ((Date)paramDate.clone());
    }
  }
  
  public void setPrivateKeyValid(Date paramDate)
  {
    if (paramDate == null) {
      this.privateKeyValid = null;
    } else {
      this.privateKeyValid = ((Date)paramDate.clone());
    }
  }
  
  public void setSubjectPublicKeyAlgID(String paramString)
    throws IOException
  {
    if (paramString == null) {
      this.subjectPublicKeyAlgID = null;
    } else {
      this.subjectPublicKeyAlgID = new ObjectIdentifier(paramString);
    }
  }
  
  public void setSubjectPublicKey(PublicKey paramPublicKey)
  {
    if (paramPublicKey == null)
    {
      this.subjectPublicKey = null;
      this.subjectPublicKeyBytes = null;
    }
    else
    {
      this.subjectPublicKey = paramPublicKey;
      this.subjectPublicKeyBytes = paramPublicKey.getEncoded();
    }
  }
  
  public void setSubjectPublicKey(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null)
    {
      this.subjectPublicKey = null;
      this.subjectPublicKeyBytes = null;
    }
    else
    {
      this.subjectPublicKeyBytes = ((byte[])paramArrayOfByte.clone());
      this.subjectPublicKey = X509Key.parse(new DerValue(this.subjectPublicKeyBytes));
    }
  }
  
  public void setKeyUsage(boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean == null) {
      this.keyUsage = null;
    } else {
      this.keyUsage = ((boolean[])paramArrayOfBoolean.clone());
    }
  }
  
  public void setExtendedKeyUsage(Set<String> paramSet)
    throws IOException
  {
    if ((paramSet == null) || (paramSet.isEmpty()))
    {
      this.keyPurposeSet = null;
      this.keyPurposeOIDSet = null;
    }
    else
    {
      this.keyPurposeSet = Collections.unmodifiableSet(new HashSet(paramSet));
      this.keyPurposeOIDSet = new HashSet();
      Iterator localIterator = this.keyPurposeSet.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.keyPurposeOIDSet.add(new ObjectIdentifier(str));
      }
    }
  }
  
  public void setMatchAllSubjectAltNames(boolean paramBoolean)
  {
    this.matchAllSubjectAltNames = paramBoolean;
  }
  
  public void setSubjectAlternativeNames(Collection<List<?>> paramCollection)
    throws IOException
  {
    if (paramCollection == null)
    {
      this.subjectAlternativeNames = null;
      this.subjectAlternativeGeneralNames = null;
    }
    else
    {
      if (paramCollection.isEmpty())
      {
        this.subjectAlternativeNames = null;
        this.subjectAlternativeGeneralNames = null;
        return;
      }
      Set localSet = cloneAndCheckNames(paramCollection);
      this.subjectAlternativeGeneralNames = parseNames(localSet);
      this.subjectAlternativeNames = localSet;
    }
  }
  
  public void addSubjectAlternativeName(int paramInt, String paramString)
    throws IOException
  {
    addSubjectAlternativeNameInternal(paramInt, paramString);
  }
  
  public void addSubjectAlternativeName(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    addSubjectAlternativeNameInternal(paramInt, paramArrayOfByte.clone());
  }
  
  private void addSubjectAlternativeNameInternal(int paramInt, Object paramObject)
    throws IOException
  {
    GeneralNameInterface localGeneralNameInterface = makeGeneralNameInterface(paramInt, paramObject);
    if (this.subjectAlternativeNames == null) {
      this.subjectAlternativeNames = new HashSet();
    }
    if (this.subjectAlternativeGeneralNames == null) {
      this.subjectAlternativeGeneralNames = new HashSet();
    }
    ArrayList localArrayList = new ArrayList(2);
    localArrayList.add(Integer.valueOf(paramInt));
    localArrayList.add(paramObject);
    this.subjectAlternativeNames.add(localArrayList);
    this.subjectAlternativeGeneralNames.add(localGeneralNameInterface);
  }
  
  private static Set<GeneralNameInterface> parseNames(Collection<List<?>> paramCollection)
    throws IOException
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      List localList = (List)localIterator.next();
      if (localList.size() != 2) {
        throw new IOException("name list size not 2");
      }
      Object localObject = localList.get(0);
      if (!(localObject instanceof Integer)) {
        throw new IOException("expected an Integer");
      }
      int i = ((Integer)localObject).intValue();
      localObject = localList.get(1);
      localHashSet.add(makeGeneralNameInterface(i, localObject));
    }
    return localHashSet;
  }
  
  static boolean equalNames(Collection<?> paramCollection1, Collection<?> paramCollection2)
  {
    if ((paramCollection1 == null) || (paramCollection2 == null)) {
      return paramCollection1 == paramCollection2;
    }
    return paramCollection1.equals(paramCollection2);
  }
  
  static GeneralNameInterface makeGeneralNameInterface(int paramInt, Object paramObject)
    throws IOException
  {
    if (debug != null) {
      debug.println("X509CertSelector.makeGeneralNameInterface(" + paramInt + ")...");
    }
    Object localObject;
    if ((paramObject instanceof String))
    {
      if (debug != null) {
        debug.println("X509CertSelector.makeGeneralNameInterface() name is String: " + paramObject);
      }
      switch (paramInt)
      {
      case 1: 
        localObject = new RFC822Name((String)paramObject);
        break;
      case 2: 
        localObject = new DNSName((String)paramObject);
        break;
      case 4: 
        localObject = new X500Name((String)paramObject);
        break;
      case 6: 
        localObject = new URIName((String)paramObject);
        break;
      case 7: 
        localObject = new IPAddressName((String)paramObject);
        break;
      case 8: 
        localObject = new OIDName((String)paramObject);
        break;
      case 3: 
      case 5: 
      default: 
        throw new IOException("unable to parse String names of type " + paramInt);
      }
      if (debug != null) {
        debug.println("X509CertSelector.makeGeneralNameInterface() result: " + localObject.toString());
      }
    }
    else if ((paramObject instanceof byte[]))
    {
      DerValue localDerValue = new DerValue((byte[])paramObject);
      if (debug != null) {
        debug.println("X509CertSelector.makeGeneralNameInterface() is byte[]");
      }
      switch (paramInt)
      {
      case 0: 
        localObject = new OtherName(localDerValue);
        break;
      case 1: 
        localObject = new RFC822Name(localDerValue);
        break;
      case 2: 
        localObject = new DNSName(localDerValue);
        break;
      case 3: 
        localObject = new X400Address(localDerValue);
        break;
      case 4: 
        localObject = new X500Name(localDerValue);
        break;
      case 5: 
        localObject = new EDIPartyName(localDerValue);
        break;
      case 6: 
        localObject = new URIName(localDerValue);
        break;
      case 7: 
        localObject = new IPAddressName(localDerValue);
        break;
      case 8: 
        localObject = new OIDName(localDerValue);
        break;
      default: 
        throw new IOException("unable to parse byte array names of type " + paramInt);
      }
      if (debug != null) {
        debug.println("X509CertSelector.makeGeneralNameInterface() result: " + localObject.toString());
      }
    }
    else
    {
      if (debug != null) {
        debug.println("X509CertSelector.makeGeneralName() input name not String or byte array");
      }
      throw new IOException("name not String or byte array");
    }
    return localObject;
  }
  
  public void setNameConstraints(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null)
    {
      this.ncBytes = null;
      this.nc = null;
    }
    else
    {
      this.ncBytes = ((byte[])paramArrayOfByte.clone());
      this.nc = new NameConstraintsExtension(FALSE, paramArrayOfByte);
    }
  }
  
  public void setBasicConstraints(int paramInt)
  {
    if (paramInt < -2) {
      throw new IllegalArgumentException("basic constraints less than -2");
    }
    this.basicConstraints = paramInt;
  }
  
  public void setPolicy(Set<String> paramSet)
    throws IOException
  {
    if (paramSet == null)
    {
      this.policySet = null;
      this.policy = null;
    }
    else
    {
      Set localSet = Collections.unmodifiableSet(new HashSet(paramSet));
      Iterator localIterator = localSet.iterator();
      Vector localVector = new Vector();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (!(localObject instanceof String)) {
          throw new IOException("non String in certPolicySet");
        }
        localVector.add(new CertificatePolicyId(new ObjectIdentifier((String)localObject)));
      }
      this.policySet = localSet;
      this.policy = new CertificatePolicySet(localVector);
    }
  }
  
  public void setPathToNames(Collection<List<?>> paramCollection)
    throws IOException
  {
    if ((paramCollection == null) || (paramCollection.isEmpty()))
    {
      this.pathToNames = null;
      this.pathToGeneralNames = null;
    }
    else
    {
      Set localSet = cloneAndCheckNames(paramCollection);
      this.pathToGeneralNames = parseNames(localSet);
      this.pathToNames = localSet;
    }
  }
  
  void setPathToNamesInternal(Set<GeneralNameInterface> paramSet)
  {
    this.pathToNames = Collections.emptySet();
    this.pathToGeneralNames = paramSet;
  }
  
  public void addPathToName(int paramInt, String paramString)
    throws IOException
  {
    addPathToNameInternal(paramInt, paramString);
  }
  
  public void addPathToName(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    addPathToNameInternal(paramInt, paramArrayOfByte.clone());
  }
  
  private void addPathToNameInternal(int paramInt, Object paramObject)
    throws IOException
  {
    GeneralNameInterface localGeneralNameInterface = makeGeneralNameInterface(paramInt, paramObject);
    if (this.pathToGeneralNames == null)
    {
      this.pathToNames = new HashSet();
      this.pathToGeneralNames = new HashSet();
    }
    ArrayList localArrayList = new ArrayList(2);
    localArrayList.add(Integer.valueOf(paramInt));
    localArrayList.add(paramObject);
    this.pathToNames.add(localArrayList);
    this.pathToGeneralNames.add(localGeneralNameInterface);
  }
  
  public X509Certificate getCertificate()
  {
    return this.x509Cert;
  }
  
  public BigInteger getSerialNumber()
  {
    return this.serialNumber;
  }
  
  public X500Principal getIssuer()
  {
    return this.issuer;
  }
  
  public String getIssuerAsString()
  {
    return this.issuer == null ? null : this.issuer.getName();
  }
  
  public byte[] getIssuerAsBytes()
    throws IOException
  {
    return this.issuer == null ? null : this.issuer.getEncoded();
  }
  
  public X500Principal getSubject()
  {
    return this.subject;
  }
  
  public String getSubjectAsString()
  {
    return this.subject == null ? null : this.subject.getName();
  }
  
  public byte[] getSubjectAsBytes()
    throws IOException
  {
    return this.subject == null ? null : this.subject.getEncoded();
  }
  
  public byte[] getSubjectKeyIdentifier()
  {
    if (this.subjectKeyID == null) {
      return null;
    }
    return (byte[])this.subjectKeyID.clone();
  }
  
  public byte[] getAuthorityKeyIdentifier()
  {
    if (this.authorityKeyID == null) {
      return null;
    }
    return (byte[])this.authorityKeyID.clone();
  }
  
  public Date getCertificateValid()
  {
    if (this.certificateValid == null) {
      return null;
    }
    return (Date)this.certificateValid.clone();
  }
  
  public Date getPrivateKeyValid()
  {
    if (this.privateKeyValid == null) {
      return null;
    }
    return (Date)this.privateKeyValid.clone();
  }
  
  public String getSubjectPublicKeyAlgID()
  {
    if (this.subjectPublicKeyAlgID == null) {
      return null;
    }
    return this.subjectPublicKeyAlgID.toString();
  }
  
  public PublicKey getSubjectPublicKey()
  {
    return this.subjectPublicKey;
  }
  
  public boolean[] getKeyUsage()
  {
    if (this.keyUsage == null) {
      return null;
    }
    return (boolean[])this.keyUsage.clone();
  }
  
  public Set<String> getExtendedKeyUsage()
  {
    return this.keyPurposeSet;
  }
  
  public boolean getMatchAllSubjectAltNames()
  {
    return this.matchAllSubjectAltNames;
  }
  
  public Collection<List<?>> getSubjectAlternativeNames()
  {
    if (this.subjectAlternativeNames == null) {
      return null;
    }
    return cloneNames(this.subjectAlternativeNames);
  }
  
  private static Set<List<?>> cloneNames(Collection<List<?>> paramCollection)
  {
    try
    {
      return cloneAndCheckNames(paramCollection);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("cloneNames encountered IOException: " + localIOException.getMessage());
    }
  }
  
  private static Set<List<?>> cloneAndCheckNames(Collection<List<?>> paramCollection)
    throws IOException
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramCollection.iterator();
    List localList1;
    while (localIterator.hasNext())
    {
      localList1 = (List)localIterator.next();
      localHashSet.add(new ArrayList(localList1));
    }
    localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      localList1 = (List)localIterator.next();
      List localList2 = localList1;
      if (localList2.size() != 2) {
        throw new IOException("name list size not 2");
      }
      Object localObject1 = localList2.get(0);
      if (!(localObject1 instanceof Integer)) {
        throw new IOException("expected an Integer");
      }
      int i = ((Integer)localObject1).intValue();
      if ((i < 0) || (i > 8)) {
        throw new IOException("name type not 0-8");
      }
      Object localObject2 = localList2.get(1);
      if ((!(localObject2 instanceof byte[])) && (!(localObject2 instanceof String)))
      {
        if (debug != null) {
          debug.println("X509CertSelector.cloneAndCheckNames() name not byte array");
        }
        throw new IOException("name not byte array or String");
      }
      if ((localObject2 instanceof byte[])) {
        localList2.set(1, ((byte[])localObject2).clone());
      }
    }
    return localHashSet;
  }
  
  public byte[] getNameConstraints()
  {
    if (this.ncBytes == null) {
      return null;
    }
    return (byte[])this.ncBytes.clone();
  }
  
  public int getBasicConstraints()
  {
    return this.basicConstraints;
  }
  
  public Set<String> getPolicy()
  {
    return this.policySet;
  }
  
  public Collection<List<?>> getPathToNames()
  {
    if (this.pathToNames == null) {
      return null;
    }
    return cloneNames(this.pathToNames);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("X509CertSelector: [\n");
    if (this.x509Cert != null) {
      localStringBuffer.append("  Certificate: " + this.x509Cert.toString() + "\n");
    }
    if (this.serialNumber != null) {
      localStringBuffer.append("  Serial Number: " + this.serialNumber.toString() + "\n");
    }
    if (this.issuer != null) {
      localStringBuffer.append("  Issuer: " + getIssuerAsString() + "\n");
    }
    if (this.subject != null) {
      localStringBuffer.append("  Subject: " + getSubjectAsString() + "\n");
    }
    localStringBuffer.append("  matchAllSubjectAltNames flag: " + String.valueOf(this.matchAllSubjectAltNames) + "\n");
    Object localObject;
    if (this.subjectAlternativeNames != null)
    {
      localStringBuffer.append("  SubjectAlternativeNames:\n");
      localObject = this.subjectAlternativeNames.iterator();
      while (((Iterator)localObject).hasNext())
      {
        List localList = (List)((Iterator)localObject).next();
        localStringBuffer.append("    type " + localList.get(0) + ", name " + localList.get(1) + "\n");
      }
    }
    if (this.subjectKeyID != null)
    {
      localObject = new HexDumpEncoder();
      localStringBuffer.append("  Subject Key Identifier: " + ((HexDumpEncoder)localObject).encodeBuffer(this.subjectKeyID) + "\n");
    }
    if (this.authorityKeyID != null)
    {
      localObject = new HexDumpEncoder();
      localStringBuffer.append("  Authority Key Identifier: " + ((HexDumpEncoder)localObject).encodeBuffer(this.authorityKeyID) + "\n");
    }
    if (this.certificateValid != null) {
      localStringBuffer.append("  Certificate Valid: " + this.certificateValid.toString() + "\n");
    }
    if (this.privateKeyValid != null) {
      localStringBuffer.append("  Private Key Valid: " + this.privateKeyValid.toString() + "\n");
    }
    if (this.subjectPublicKeyAlgID != null) {
      localStringBuffer.append("  Subject Public Key AlgID: " + this.subjectPublicKeyAlgID.toString() + "\n");
    }
    if (this.subjectPublicKey != null) {
      localStringBuffer.append("  Subject Public Key: " + this.subjectPublicKey.toString() + "\n");
    }
    if (this.keyUsage != null) {
      localStringBuffer.append("  Key Usage: " + keyUsageToString(this.keyUsage) + "\n");
    }
    if (this.keyPurposeSet != null) {
      localStringBuffer.append("  Extended Key Usage: " + this.keyPurposeSet.toString() + "\n");
    }
    if (this.policy != null) {
      localStringBuffer.append("  Policy: " + this.policy.toString() + "\n");
    }
    if (this.pathToGeneralNames != null)
    {
      localStringBuffer.append("  Path to names:\n");
      localObject = this.pathToGeneralNames.iterator();
      while (((Iterator)localObject).hasNext()) {
        localStringBuffer.append("    " + ((Iterator)localObject).next() + "\n");
      }
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  private static String keyUsageToString(boolean[] paramArrayOfBoolean)
  {
    String str = "KeyUsage [\n";
    try
    {
      if (paramArrayOfBoolean[0] != 0) {
        str = str + "  DigitalSignature\n";
      }
      if (paramArrayOfBoolean[1] != 0) {
        str = str + "  Non_repudiation\n";
      }
      if (paramArrayOfBoolean[2] != 0) {
        str = str + "  Key_Encipherment\n";
      }
      if (paramArrayOfBoolean[3] != 0) {
        str = str + "  Data_Encipherment\n";
      }
      if (paramArrayOfBoolean[4] != 0) {
        str = str + "  Key_Agreement\n";
      }
      if (paramArrayOfBoolean[5] != 0) {
        str = str + "  Key_CertSign\n";
      }
      if (paramArrayOfBoolean[6] != 0) {
        str = str + "  Crl_Sign\n";
      }
      if (paramArrayOfBoolean[7] != 0) {
        str = str + "  Encipher_Only\n";
      }
      if (paramArrayOfBoolean[8] != 0) {
        str = str + "  Decipher_Only\n";
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    str = str + "]\n";
    return str;
  }
  
  private static Extension getExtensionObject(X509Certificate paramX509Certificate, int paramInt)
    throws IOException
  {
    if ((paramX509Certificate instanceof X509CertImpl))
    {
      localObject = (X509CertImpl)paramX509Certificate;
      switch (paramInt)
      {
      case 0: 
        return ((X509CertImpl)localObject).getPrivateKeyUsageExtension();
      case 1: 
        return ((X509CertImpl)localObject).getSubjectAlternativeNameExtension();
      case 2: 
        return ((X509CertImpl)localObject).getNameConstraintsExtension();
      case 3: 
        return ((X509CertImpl)localObject).getCertificatePoliciesExtension();
      case 4: 
        return ((X509CertImpl)localObject).getExtendedKeyUsageExtension();
      }
      return null;
    }
    Object localObject = paramX509Certificate.getExtensionValue(EXTENSION_OIDS[paramInt]);
    if (localObject == null) {
      return null;
    }
    DerInputStream localDerInputStream = new DerInputStream((byte[])localObject);
    byte[] arrayOfByte = localDerInputStream.getOctetString();
    switch (paramInt)
    {
    case 0: 
      try
      {
        return new PrivateKeyUsageExtension(FALSE, arrayOfByte);
      }
      catch (CertificateException localCertificateException)
      {
        throw new IOException(localCertificateException.getMessage());
      }
    case 1: 
      return new SubjectAlternativeNameExtension(FALSE, arrayOfByte);
    case 2: 
      return new NameConstraintsExtension(FALSE, arrayOfByte);
    case 3: 
      return new CertificatePoliciesExtension(FALSE, arrayOfByte);
    case 4: 
      return new ExtendedKeyUsageExtension(FALSE, arrayOfByte);
    }
    return null;
  }
  
  public boolean match(Certificate paramCertificate)
  {
    if (!(paramCertificate instanceof X509Certificate)) {
      return false;
    }
    X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
    if (debug != null) {
      debug.println("X509CertSelector.match(SN: " + localX509Certificate.getSerialNumber().toString(16) + "\n  Issuer: " + localX509Certificate.getIssuerDN() + "\n  Subject: " + localX509Certificate.getSubjectDN() + ")");
    }
    if ((this.x509Cert != null) && (!this.x509Cert.equals(localX509Certificate)))
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: certs don't match");
      }
      return false;
    }
    if ((this.serialNumber != null) && (!this.serialNumber.equals(localX509Certificate.getSerialNumber())))
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: serial numbers don't match");
      }
      return false;
    }
    if ((this.issuer != null) && (!this.issuer.equals(localX509Certificate.getIssuerX500Principal())))
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: issuer DNs don't match");
      }
      return false;
    }
    if ((this.subject != null) && (!this.subject.equals(localX509Certificate.getSubjectX500Principal())))
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: subject DNs don't match");
      }
      return false;
    }
    if (this.certificateValid != null) {
      try
      {
        localX509Certificate.checkValidity(this.certificateValid);
      }
      catch (CertificateException localCertificateException)
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: certificate not within validity period");
        }
        return false;
      }
    }
    if (this.subjectPublicKeyBytes != null)
    {
      byte[] arrayOfByte = localX509Certificate.getPublicKey().getEncoded();
      if (!Arrays.equals(this.subjectPublicKeyBytes, arrayOfByte))
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: subject public keys don't match");
        }
        return false;
      }
    }
    boolean bool = (matchBasicConstraints(localX509Certificate)) && (matchKeyUsage(localX509Certificate)) && (matchExtendedKeyUsage(localX509Certificate)) && (matchSubjectKeyID(localX509Certificate)) && (matchAuthorityKeyID(localX509Certificate)) && (matchPrivateKeyValid(localX509Certificate)) && (matchSubjectPublicKeyAlgID(localX509Certificate)) && (matchPolicy(localX509Certificate)) && (matchSubjectAlternativeNames(localX509Certificate)) && (matchPathToNames(localX509Certificate)) && (matchNameConstraints(localX509Certificate));
    if ((bool) && (debug != null)) {
      debug.println("X509CertSelector.match returning: true");
    }
    return bool;
  }
  
  private boolean matchSubjectKeyID(X509Certificate paramX509Certificate)
  {
    if (this.subjectKeyID == null) {
      return true;
    }
    try
    {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.14");
      if (arrayOfByte1 == null)
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: no subject key ID extension");
        }
        return false;
      }
      DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = localDerInputStream.getOctetString();
      if ((arrayOfByte2 == null) || (!Arrays.equals(this.subjectKeyID, arrayOfByte2)))
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: subject key IDs don't match");
        }
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: exception in subject key ID check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchAuthorityKeyID(X509Certificate paramX509Certificate)
  {
    if (this.authorityKeyID == null) {
      return true;
    }
    try
    {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.35");
      if (arrayOfByte1 == null)
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: no authority key ID extension");
        }
        return false;
      }
      DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = localDerInputStream.getOctetString();
      if ((arrayOfByte2 == null) || (!Arrays.equals(this.authorityKeyID, arrayOfByte2)))
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: authority key IDs don't match");
        }
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: exception in authority key ID check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchPrivateKeyValid(X509Certificate paramX509Certificate)
  {
    if (this.privateKeyValid == null) {
      return true;
    }
    PrivateKeyUsageExtension localPrivateKeyUsageExtension = null;
    try
    {
      localPrivateKeyUsageExtension = (PrivateKeyUsageExtension)getExtensionObject(paramX509Certificate, 0);
      if (localPrivateKeyUsageExtension != null) {
        localPrivateKeyUsageExtension.valid(this.privateKeyValid);
      }
    }
    catch (CertificateExpiredException localCertificateExpiredException)
    {
      if (debug != null)
      {
        str = "n/a";
        try
        {
          Date localDate1 = localPrivateKeyUsageExtension.get("not_after");
          str = localDate1.toString();
        }
        catch (CertificateException localCertificateException1) {}
        debug.println("X509CertSelector.match: private key usage not within validity date; ext.NOT_After: " + str + "; X509CertSelector: " + toString());
        localCertificateExpiredException.printStackTrace();
      }
      return false;
    }
    catch (CertificateNotYetValidException localCertificateNotYetValidException)
    {
      String str;
      if (debug != null)
      {
        str = "n/a";
        try
        {
          Date localDate2 = localPrivateKeyUsageExtension.get("not_before");
          str = localDate2.toString();
        }
        catch (CertificateException localCertificateException2) {}
        debug.println("X509CertSelector.match: private key usage not within validity date; ext.NOT_BEFORE: " + str + "; X509CertSelector: " + toString());
        localCertificateNotYetValidException.printStackTrace();
      }
      return false;
    }
    catch (IOException localIOException)
    {
      if (debug != null)
      {
        debug.println("X509CertSelector.match: IOException in private key usage check; X509CertSelector: " + toString());
        localIOException.printStackTrace();
      }
      return false;
    }
    return true;
  }
  
  private boolean matchSubjectPublicKeyAlgID(X509Certificate paramX509Certificate)
  {
    if (this.subjectPublicKeyAlgID == null) {
      return true;
    }
    try
    {
      byte[] arrayOfByte = paramX509Certificate.getPublicKey().getEncoded();
      DerValue localDerValue = new DerValue(arrayOfByte);
      if (localDerValue.tag != 48) {
        throw new IOException("invalid key format");
      }
      AlgorithmId localAlgorithmId = AlgorithmId.parse(localDerValue.data.getDerValue());
      if (debug != null) {
        debug.println("X509CertSelector.match: subjectPublicKeyAlgID = " + this.subjectPublicKeyAlgID + ", xcert subjectPublicKeyAlgID = " + localAlgorithmId.getOID());
      }
      if (!this.subjectPublicKeyAlgID.equals(localAlgorithmId.getOID()))
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: subject public key alg IDs don't match");
        }
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in subject public key algorithm OID check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchKeyUsage(X509Certificate paramX509Certificate)
  {
    if (this.keyUsage == null) {
      return true;
    }
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    if (arrayOfBoolean != null) {
      for (int i = 0; i < this.keyUsage.length; i++) {
        if ((this.keyUsage[i] != 0) && ((i >= arrayOfBoolean.length) || (arrayOfBoolean[i] == 0)))
        {
          if (debug != null) {
            debug.println("X509CertSelector.match: key usage bits don't match");
          }
          return false;
        }
      }
    }
    return true;
  }
  
  private boolean matchExtendedKeyUsage(X509Certificate paramX509Certificate)
  {
    if ((this.keyPurposeSet == null) || (this.keyPurposeSet.isEmpty())) {
      return true;
    }
    try
    {
      ExtendedKeyUsageExtension localExtendedKeyUsageExtension = (ExtendedKeyUsageExtension)getExtensionObject(paramX509Certificate, 4);
      if (localExtendedKeyUsageExtension != null)
      {
        Vector localVector = localExtendedKeyUsageExtension.get("usages");
        if ((!localVector.contains(ANY_EXTENDED_KEY_USAGE)) && (!localVector.containsAll(this.keyPurposeOIDSet)))
        {
          if (debug != null) {
            debug.println("X509CertSelector.match: cert failed extendedKeyUsage criterion");
          }
          return false;
        }
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in extended key usage check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchSubjectAlternativeNames(X509Certificate paramX509Certificate)
  {
    if ((this.subjectAlternativeNames == null) || (this.subjectAlternativeNames.isEmpty())) {
      return true;
    }
    try
    {
      SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = (SubjectAlternativeNameExtension)getExtensionObject(paramX509Certificate, 1);
      if (localSubjectAlternativeNameExtension == null)
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: no subject alternative name extension");
        }
        return false;
      }
      GeneralNames localGeneralNames = localSubjectAlternativeNameExtension.get("subject_name");
      Iterator localIterator1 = this.subjectAlternativeGeneralNames.iterator();
      while (localIterator1.hasNext())
      {
        GeneralNameInterface localGeneralNameInterface1 = (GeneralNameInterface)localIterator1.next();
        boolean bool = false;
        Iterator localIterator2 = localGeneralNames.iterator();
        while ((localIterator2.hasNext()) && (!bool))
        {
          GeneralNameInterface localGeneralNameInterface2 = ((GeneralName)localIterator2.next()).getName();
          bool = localGeneralNameInterface2.equals(localGeneralNameInterface1);
        }
        if ((!bool) && ((this.matchAllSubjectAltNames) || (!localIterator1.hasNext())))
        {
          if (debug != null) {
            debug.println("X509CertSelector.match: subject alternative name " + localGeneralNameInterface1 + " not found");
          }
          return false;
        }
        if ((bool) && (!this.matchAllSubjectAltNames)) {
          break;
        }
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in subject alternative name check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchNameConstraints(X509Certificate paramX509Certificate)
  {
    if (this.nc == null) {
      return true;
    }
    try
    {
      if (!this.nc.verify(paramX509Certificate))
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: name constraints not satisfied");
        }
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in name constraints check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchPolicy(X509Certificate paramX509Certificate)
  {
    if (this.policy == null) {
      return true;
    }
    try
    {
      CertificatePoliciesExtension localCertificatePoliciesExtension = (CertificatePoliciesExtension)getExtensionObject(paramX509Certificate, 3);
      if (localCertificatePoliciesExtension == null)
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: no certificate policy extension");
        }
        return false;
      }
      List localList = localCertificatePoliciesExtension.get("policies");
      ArrayList localArrayList = new ArrayList(localList.size());
      Iterator localIterator = localList.iterator();
      Object localObject;
      while (localIterator.hasNext())
      {
        localObject = (PolicyInformation)localIterator.next();
        localArrayList.add(((PolicyInformation)localObject).getPolicyIdentifier());
      }
      if (this.policy != null)
      {
        int i = 0;
        if (this.policy.getCertPolicyIds().isEmpty())
        {
          if (localArrayList.isEmpty())
          {
            if (debug != null) {
              debug.println("X509CertSelector.match: cert failed policyAny criterion");
            }
            return false;
          }
        }
        else
        {
          localObject = this.policy.getCertPolicyIds().iterator();
          while (((Iterator)localObject).hasNext())
          {
            CertificatePolicyId localCertificatePolicyId = (CertificatePolicyId)((Iterator)localObject).next();
            if (localArrayList.contains(localCertificatePolicyId))
            {
              i = 1;
              break;
            }
          }
          if (i == 0)
          {
            if (debug != null) {
              debug.println("X509CertSelector.match: cert failed policyAny criterion");
            }
            return false;
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in certificate policy ID check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchPathToNames(X509Certificate paramX509Certificate)
  {
    if (this.pathToGeneralNames == null) {
      return true;
    }
    try
    {
      NameConstraintsExtension localNameConstraintsExtension = (NameConstraintsExtension)getExtensionObject(paramX509Certificate, 2);
      if (localNameConstraintsExtension == null) {
        return true;
      }
      if ((debug != null) && (Debug.isOn("certpath")))
      {
        debug.println("X509CertSelector.match pathToNames:\n");
        localObject = this.pathToGeneralNames.iterator();
        while (((Iterator)localObject).hasNext()) {
          debug.println("    " + ((Iterator)localObject).next() + "\n");
        }
      }
      Object localObject = localNameConstraintsExtension.get("permitted_subtrees");
      GeneralSubtrees localGeneralSubtrees = localNameConstraintsExtension.get("excluded_subtrees");
      if ((localGeneralSubtrees != null) && (!matchExcluded(localGeneralSubtrees))) {
        return false;
      }
      if ((localObject != null) && (!matchPermitted((GeneralSubtrees)localObject))) {
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in name constraints check");
      }
      return false;
    }
    return true;
  }
  
  private boolean matchExcluded(GeneralSubtrees paramGeneralSubtrees)
  {
    Iterator localIterator1 = paramGeneralSubtrees.iterator();
    while (localIterator1.hasNext())
    {
      GeneralSubtree localGeneralSubtree = (GeneralSubtree)localIterator1.next();
      GeneralNameInterface localGeneralNameInterface1 = localGeneralSubtree.getName().getName();
      Iterator localIterator2 = this.pathToGeneralNames.iterator();
      while (localIterator2.hasNext())
      {
        GeneralNameInterface localGeneralNameInterface2 = (GeneralNameInterface)localIterator2.next();
        if (localGeneralNameInterface1.getType() == localGeneralNameInterface2.getType()) {
          switch (localGeneralNameInterface2.constrains(localGeneralNameInterface1))
          {
          case 0: 
          case 2: 
            if (debug != null)
            {
              debug.println("X509CertSelector.match: name constraints inhibit path to specified name");
              debug.println("X509CertSelector.match: excluded name: " + localGeneralNameInterface2);
            }
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private boolean matchPermitted(GeneralSubtrees paramGeneralSubtrees)
  {
    Iterator localIterator1 = this.pathToGeneralNames.iterator();
    while (localIterator1.hasNext())
    {
      GeneralNameInterface localGeneralNameInterface1 = (GeneralNameInterface)localIterator1.next();
      Iterator localIterator2 = paramGeneralSubtrees.iterator();
      int i = 0;
      int j = 0;
      String str = "";
      while ((localIterator2.hasNext()) && (i == 0))
      {
        GeneralSubtree localGeneralSubtree = (GeneralSubtree)localIterator2.next();
        GeneralNameInterface localGeneralNameInterface2 = localGeneralSubtree.getName().getName();
        if (localGeneralNameInterface2.getType() == localGeneralNameInterface1.getType())
        {
          j = 1;
          str = str + "  " + localGeneralNameInterface2;
          switch (localGeneralNameInterface1.constrains(localGeneralNameInterface2))
          {
          case 0: 
          case 2: 
            i = 1;
          }
        }
      }
      if ((i == 0) && (j != 0))
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: name constraints inhibit path to specified name; permitted names of type " + localGeneralNameInterface1.getType() + ": " + str);
        }
        return false;
      }
    }
    return true;
  }
  
  private boolean matchBasicConstraints(X509Certificate paramX509Certificate)
  {
    if (this.basicConstraints == -1) {
      return true;
    }
    int i = paramX509Certificate.getBasicConstraints();
    if (this.basicConstraints == -2)
    {
      if (i != -1)
      {
        if (debug != null) {
          debug.println("X509CertSelector.match: not an EE cert");
        }
        return false;
      }
    }
    else if (i < this.basicConstraints)
    {
      if (debug != null) {
        debug.println("X509CertSelector.match: cert's maxPathLen is less than the min maxPathLen set by basicConstraints. (" + i + " < " + this.basicConstraints + ")");
      }
      return false;
    }
    return true;
  }
  
  private static <T> Set<T> cloneSet(Set<T> paramSet)
  {
    if ((paramSet instanceof HashSet))
    {
      Object localObject = ((HashSet)paramSet).clone();
      return (Set)localObject;
    }
    return new HashSet(paramSet);
  }
  
  public Object clone()
  {
    try
    {
      X509CertSelector localX509CertSelector = (X509CertSelector)super.clone();
      if (this.subjectAlternativeNames != null)
      {
        localX509CertSelector.subjectAlternativeNames = cloneSet(this.subjectAlternativeNames);
        localX509CertSelector.subjectAlternativeGeneralNames = cloneSet(this.subjectAlternativeGeneralNames);
      }
      if (this.pathToGeneralNames != null)
      {
        localX509CertSelector.pathToNames = cloneSet(this.pathToNames);
        localX509CertSelector.pathToGeneralNames = cloneSet(this.pathToGeneralNames);
      }
      return localX509CertSelector;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  static
  {
    CertPathHelperImpl.initialize();
    FALSE = Boolean.FALSE;
    EXTENSION_OIDS = new String[5];
    EXTENSION_OIDS[0] = "2.5.29.16";
    EXTENSION_OIDS[1] = "2.5.29.17";
    EXTENSION_OIDS[2] = "2.5.29.30";
    EXTENSION_OIDS[3] = "2.5.29.32";
    EXTENSION_OIDS[4] = "2.5.29.37";
  }
}
