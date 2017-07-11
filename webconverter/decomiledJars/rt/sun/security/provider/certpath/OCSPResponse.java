package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

public final class OCSPResponse
{
  private static ResponseStatus[] rsvalues = ;
  private static final Debug debug = Debug.getInstance("certpath");
  private static final boolean dump = (debug != null) && (Debug.isOn("ocsp"));
  private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 1 });
  private static final int CERT_STATUS_GOOD = 0;
  private static final int CERT_STATUS_REVOKED = 1;
  private static final int CERT_STATUS_UNKNOWN = 2;
  private static final int NAME_TAG = 1;
  private static final int KEY_TAG = 2;
  private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
  private static final int DEFAULT_MAX_CLOCK_SKEW = 900000;
  private static final int MAX_CLOCK_SKEW = initializeClockSkew();
  private static CRLReason[] values = CRLReason.values();
  private final ResponseStatus responseStatus;
  private final Map<CertId, SingleResponse> singleResponseMap;
  private final AlgorithmId sigAlgId;
  private final byte[] signature;
  private final byte[] tbsResponseData;
  private final byte[] responseNonce;
  private List<X509CertImpl> certs;
  private X509CertImpl signerCert = null;
  private X500Principal responderName = null;
  private KeyIdentifier responderKeyId = null;
  
  private static int initializeClockSkew()
  {
    Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.clockSkew"));
    if ((localInteger == null) || (localInteger.intValue() < 0)) {
      return 900000;
    }
    return localInteger.intValue() * 1000;
  }
  
  OCSPResponse(byte[] paramArrayOfByte)
    throws IOException
  {
    if (dump)
    {
      localObject1 = new HexDumpEncoder();
      debug.println("OCSPResponse bytes...\n\n" + ((HexDumpEncoder)localObject1).encode(paramArrayOfByte) + "\n");
    }
    Object localObject1 = new DerValue(paramArrayOfByte);
    if (((DerValue)localObject1).tag != 48) {
      throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag.");
    }
    DerInputStream localDerInputStream1 = ((DerValue)localObject1).getData();
    int i = localDerInputStream1.getEnumerated();
    if ((i >= 0) && (i < rsvalues.length)) {
      this.responseStatus = rsvalues[i];
    } else {
      throw new IOException("Unknown OCSPResponse status: " + i);
    }
    if (debug != null) {
      debug.println("OCSP response status: " + this.responseStatus);
    }
    if (this.responseStatus != ResponseStatus.SUCCESSFUL)
    {
      this.singleResponseMap = Collections.emptyMap();
      this.certs = new ArrayList();
      this.sigAlgId = null;
      this.signature = null;
      this.tbsResponseData = null;
      this.responseNonce = null;
      return;
    }
    localObject1 = localDerInputStream1.getDerValue();
    if (!((DerValue)localObject1).isContextSpecific((byte)0)) {
      throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0.");
    }
    DerValue localDerValue1 = ((DerValue)localObject1).data.getDerValue();
    if (localDerValue1.tag != 48) {
      throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag.");
    }
    localDerInputStream1 = localDerValue1.data;
    ObjectIdentifier localObjectIdentifier = localDerInputStream1.getOID();
    if (localObjectIdentifier.equals(OCSP_BASIC_RESPONSE_OID))
    {
      if (debug != null) {
        debug.println("OCSP response type: basic");
      }
    }
    else
    {
      if (debug != null) {
        debug.println("OCSP response type: " + localObjectIdentifier);
      }
      throw new IOException("Unsupported OCSP response type: " + localObjectIdentifier);
    }
    DerInputStream localDerInputStream2 = new DerInputStream(localDerInputStream1.getOctetString());
    DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
    if (arrayOfDerValue1.length < 3) {
      throw new IOException("Unexpected BasicOCSPResponse value");
    }
    DerValue localDerValue2 = arrayOfDerValue1[0];
    this.tbsResponseData = arrayOfDerValue1[0].toByteArray();
    if (localDerValue2.tag != 48) {
      throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag.");
    }
    DerInputStream localDerInputStream3 = localDerValue2.data;
    DerValue localDerValue3 = localDerInputStream3.getDerValue();
    if ((localDerValue3.isContextSpecific((byte)0)) && (localDerValue3.isConstructed()) && (localDerValue3.isContextSpecific()))
    {
      localDerValue3 = localDerValue3.data.getDerValue();
      j = localDerValue3.getInteger();
      if (localDerValue3.data.available() != 0) {
        throw new IOException("Bad encoding in version  element of OCSP response: bad format");
      }
      localDerValue3 = localDerInputStream3.getDerValue();
    }
    int j = (short)(byte)(localDerValue3.tag & 0x1F);
    if (j == 1)
    {
      this.responderName = new X500Principal(localDerValue3.getData().toByteArray());
      if (debug != null) {
        debug.println("Responder's name: " + this.responderName);
      }
    }
    else if (j == 2)
    {
      this.responderKeyId = new KeyIdentifier(localDerValue3.getData().getOctetString());
      if (debug != null) {
        debug.println("Responder's key ID: " + Debug.toString(this.responderKeyId.getIdentifier()));
      }
    }
    else
    {
      throw new IOException("Bad encoding in responderID element of OCSP response: expected ASN.1 context specific tag 0 or 1");
    }
    localDerValue3 = localDerInputStream3.getDerValue();
    if (debug != null)
    {
      localObject2 = localDerValue3.getGeneralizedTime();
      debug.println("OCSP response produced at: " + localObject2);
    }
    Object localObject2 = localDerInputStream3.getSequence(1);
    this.singleResponseMap = new HashMap(localObject2.length);
    if (debug != null) {
      debug.println("OCSP number of SingleResponses: " + localObject2.length);
    }
    Object localObject3;
    for (int k = 0; k < localObject2.length; k++)
    {
      localObject3 = new SingleResponse(localObject2[k], null);
      this.singleResponseMap.put(((SingleResponse)localObject3).getCertId(), localObject3);
    }
    byte[] arrayOfByte = null;
    if (localDerInputStream3.available() > 0)
    {
      localDerValue3 = localDerInputStream3.getDerValue();
      if (localDerValue3.isContextSpecific((byte)1))
      {
        localObject3 = localDerValue3.data.getSequence(3);
        for (int m = 0; m < localObject3.length; m++)
        {
          sun.security.x509.Extension localExtension = new sun.security.x509.Extension(localObject3[m]);
          if (debug != null) {
            debug.println("OCSP extension: " + localExtension);
          }
          if (localExtension.getExtensionId().equals(OCSP.NONCE_EXTENSION_OID)) {
            arrayOfByte = localExtension.getExtensionValue();
          } else if (localExtension.isCritical()) {
            throw new IOException("Unsupported OCSP critical extension: " + localExtension.getExtensionId());
          }
        }
      }
    }
    this.responseNonce = arrayOfByte;
    this.sigAlgId = AlgorithmId.parse(arrayOfDerValue1[1]);
    this.signature = arrayOfDerValue1[2].getBitString();
    if (arrayOfDerValue1.length > 3)
    {
      localObject3 = arrayOfDerValue1[3];
      if (!((DerValue)localObject3).isContextSpecific((byte)0)) {
        throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0.");
      }
      DerValue[] arrayOfDerValue2 = ((DerValue)localObject3).getData().getSequence(3);
      this.certs = new ArrayList(arrayOfDerValue2.length);
      try
      {
        for (int n = 0; n < arrayOfDerValue2.length; n++)
        {
          X509CertImpl localX509CertImpl = new X509CertImpl(arrayOfDerValue2[n].toByteArray());
          this.certs.add(localX509CertImpl);
          if (debug != null) {
            debug.println("OCSP response cert #" + (n + 1) + ": " + localX509CertImpl.getSubjectX500Principal());
          }
        }
      }
      catch (CertificateException localCertificateException)
      {
        throw new IOException("Bad encoding in X509 Certificate", localCertificateException);
      }
    }
    else
    {
      this.certs = new ArrayList();
    }
  }
  
  void verify(List<CertId> paramList, X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, Date paramDate, byte[] paramArrayOfByte)
    throws CertPathValidatorException
  {
    switch (1.$SwitchMap$sun$security$provider$certpath$OCSPResponse$ResponseStatus[this.responseStatus.ordinal()])
    {
    case 1: 
      break;
    case 2: 
    case 3: 
      throw new CertPathValidatorException("OCSP response error: " + this.responseStatus, null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    case 4: 
    default: 
      throw new CertPathValidatorException("OCSP response error: " + this.responseStatus);
    }
    Iterator localIterator1 = paramList.iterator();
    Object localObject2;
    Object localObject3;
    while (localIterator1.hasNext())
    {
      localObject2 = (CertId)localIterator1.next();
      localObject3 = getSingleResponse((CertId)localObject2);
      if (localObject3 == null)
      {
        if (debug != null) {
          debug.println("No response found for CertId: " + localObject2);
        }
        throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
      }
      if (debug != null) {
        debug.println("Status of certificate (with serial number " + ((CertId)localObject2).getSerialNumber() + ") is: " + ((SingleResponse)localObject3).getCertStatus());
      }
    }
    Object localObject1;
    if (this.signerCert == null)
    {
      try
      {
        this.certs.add(X509CertImpl.toImpl(paramX509Certificate1));
        if (paramX509Certificate2 != null) {
          this.certs.add(X509CertImpl.toImpl(paramX509Certificate2));
        }
      }
      catch (CertificateException localCertificateException1)
      {
        throw new CertPathValidatorException("Invalid issuer or trusted responder certificate", localCertificateException1);
      }
      if (this.responderName != null)
      {
        localObject1 = this.certs.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (X509CertImpl)((Iterator)localObject1).next();
          if (((X509CertImpl)localObject2).getSubjectX500Principal().equals(this.responderName))
          {
            this.signerCert = ((X509CertImpl)localObject2);
            break;
          }
        }
      }
      else if (this.responderKeyId != null)
      {
        localObject1 = this.certs.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (X509CertImpl)((Iterator)localObject1).next();
          localObject3 = ((X509CertImpl)localObject2).getSubjectKeyId();
          if ((localObject3 != null) && (this.responderKeyId.equals(localObject3)))
          {
            this.signerCert = ((X509CertImpl)localObject2);
            break;
          }
          try
          {
            localObject3 = new KeyIdentifier(((X509CertImpl)localObject2).getPublicKey());
          }
          catch (IOException localIOException) {}
          if (this.responderKeyId.equals(localObject3))
          {
            this.signerCert = ((X509CertImpl)localObject2);
            break;
          }
        }
      }
    }
    if (this.signerCert != null) {
      if (this.signerCert.equals(paramX509Certificate1))
      {
        if (debug != null) {
          debug.println("OCSP response is signed by the target's Issuing CA");
        }
      }
      else if (this.signerCert.equals(paramX509Certificate2))
      {
        if (debug != null) {
          debug.println("OCSP response is signed by a Trusted Responder");
        }
      }
      else if (this.signerCert.getIssuerX500Principal().equals(paramX509Certificate1.getSubjectX500Principal()))
      {
        try
        {
          localObject1 = this.signerCert.getExtendedKeyUsage();
          if ((localObject1 == null) || (!((List)localObject1).contains("1.3.6.1.5.5.7.3.9"))) {
            throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses");
          }
        }
        catch (CertificateParsingException localCertificateParsingException)
        {
          throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses", localCertificateParsingException);
        }
        AlgorithmChecker localAlgorithmChecker = new AlgorithmChecker(new TrustAnchor(paramX509Certificate1, null));
        localAlgorithmChecker.init(false);
        localAlgorithmChecker.check(this.signerCert, Collections.emptySet());
        try
        {
          if (paramDate == null) {
            this.signerCert.checkValidity();
          } else {
            this.signerCert.checkValidity(paramDate);
          }
        }
        catch (CertificateException localCertificateException2)
        {
          throw new CertPathValidatorException("Responder's certificate not within the validity period", localCertificateException2);
        }
        sun.security.x509.Extension localExtension = this.signerCert.getExtension(PKIXExtensions.OCSPNoCheck_Id);
        if ((localExtension != null) && (debug != null)) {
          debug.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck.");
        }
        try
        {
          this.signerCert.verify(paramX509Certificate1.getPublicKey());
          if (debug != null) {
            debug.println("OCSP response is signed by an Authorized Responder");
          }
        }
        catch (GeneralSecurityException localGeneralSecurityException)
        {
          this.signerCert = null;
        }
      }
      else
      {
        throw new CertPathValidatorException("Responder's certificate is not authorized to sign OCSP responses");
      }
    }
    if (this.signerCert != null)
    {
      AlgorithmChecker.check(this.signerCert.getPublicKey(), this.sigAlgId);
      if (!verifySignature(this.signerCert)) {
        throw new CertPathValidatorException("Error verifying OCSP Response's signature");
      }
    }
    else
    {
      throw new CertPathValidatorException("Unable to verify OCSP Response's signature");
    }
    if ((paramArrayOfByte != null) && (this.responseNonce != null) && (!Arrays.equals(paramArrayOfByte, this.responseNonce))) {
      throw new CertPathValidatorException("Nonces don't match");
    }
    long l = paramDate == null ? System.currentTimeMillis() : paramDate.getTime();
    Date localDate1 = new Date(l + MAX_CLOCK_SKEW);
    Date localDate2 = new Date(l - MAX_CLOCK_SKEW);
    Iterator localIterator2 = this.singleResponseMap.values().iterator();
    while (localIterator2.hasNext())
    {
      SingleResponse localSingleResponse = (SingleResponse)localIterator2.next();
      if (debug != null)
      {
        String str = "";
        if (localSingleResponse.nextUpdate != null) {
          str = " until " + localSingleResponse.nextUpdate;
        }
        debug.println("OCSP response validity interval is from " + localSingleResponse.thisUpdate + str);
        debug.println("Checking validity of OCSP response on: " + new Date(l));
      }
      if (!localDate1.before(localSingleResponse.thisUpdate))
      {
        if (!localDate2.after(localSingleResponse.nextUpdate != null ? localSingleResponse.nextUpdate : localSingleResponse.thisUpdate)) {}
      }
      else {
        throw new CertPathValidatorException("Response is unreliable: its validity interval is out-of-date");
      }
    }
  }
  
  ResponseStatus getResponseStatus()
  {
    return this.responseStatus;
  }
  
  private boolean verifySignature(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    try
    {
      Signature localSignature = Signature.getInstance(this.sigAlgId.getName());
      localSignature.initVerify(paramX509Certificate.getPublicKey());
      localSignature.update(this.tbsResponseData);
      if (localSignature.verify(this.signature))
      {
        if (debug != null) {
          debug.println("Verified signature of OCSP Response");
        }
        return true;
      }
      if (debug != null) {
        debug.println("Error verifying signature of OCSP Response");
      }
      return false;
    }
    catch (InvalidKeyException|NoSuchAlgorithmException|SignatureException localInvalidKeyException)
    {
      throw new CertPathValidatorException(localInvalidKeyException);
    }
  }
  
  SingleResponse getSingleResponse(CertId paramCertId)
  {
    return (SingleResponse)this.singleResponseMap.get(paramCertId);
  }
  
  X509Certificate getSignerCertificate()
  {
    return this.signerCert;
  }
  
  public static enum ResponseStatus
  {
    SUCCESSFUL,  MALFORMED_REQUEST,  INTERNAL_ERROR,  TRY_LATER,  UNUSED,  SIG_REQUIRED,  UNAUTHORIZED;
    
    private ResponseStatus() {}
  }
  
  static final class SingleResponse
    implements OCSP.RevocationStatus
  {
    private final CertId certId;
    private final OCSP.RevocationStatus.CertStatus certStatus;
    private final Date thisUpdate;
    private final Date nextUpdate;
    private final Date revocationTime;
    private final CRLReason revocationReason;
    private final Map<String, java.security.cert.Extension> singleExtensions;
    
    private SingleResponse(DerValue paramDerValue)
      throws IOException
    {
      if (paramDerValue.tag != 48) {
        throw new IOException("Bad ASN.1 encoding in SingleResponse");
      }
      DerInputStream localDerInputStream = paramDerValue.data;
      this.certId = new CertId(localDerInputStream.getDerValue().data);
      DerValue localDerValue = localDerInputStream.getDerValue();
      int i = (short)(byte)(localDerValue.tag & 0x1F);
      Object localObject;
      int j;
      if (i == 1)
      {
        this.certStatus = OCSP.RevocationStatus.CertStatus.REVOKED;
        this.revocationTime = localDerValue.data.getGeneralizedTime();
        if (localDerValue.data.available() != 0)
        {
          localObject = localDerValue.data.getDerValue();
          i = (short)(byte)(((DerValue)localObject).tag & 0x1F);
          if (i == 0)
          {
            j = ((DerValue)localObject).data.getEnumerated();
            if ((j >= 0) && (j < OCSPResponse.values.length)) {
              this.revocationReason = OCSPResponse.values[j];
            } else {
              this.revocationReason = CRLReason.UNSPECIFIED;
            }
          }
          else
          {
            this.revocationReason = CRLReason.UNSPECIFIED;
          }
        }
        else
        {
          this.revocationReason = CRLReason.UNSPECIFIED;
        }
        if (OCSPResponse.debug != null)
        {
          OCSPResponse.debug.println("Revocation time: " + this.revocationTime);
          OCSPResponse.debug.println("Revocation reason: " + this.revocationReason);
        }
      }
      else
      {
        this.revocationTime = null;
        this.revocationReason = CRLReason.UNSPECIFIED;
        if (i == 0) {
          this.certStatus = OCSP.RevocationStatus.CertStatus.GOOD;
        } else if (i == 2) {
          this.certStatus = OCSP.RevocationStatus.CertStatus.UNKNOWN;
        } else {
          throw new IOException("Invalid certificate status");
        }
      }
      this.thisUpdate = localDerInputStream.getGeneralizedTime();
      if (localDerInputStream.available() == 0)
      {
        this.nextUpdate = null;
      }
      else
      {
        localDerValue = localDerInputStream.getDerValue();
        i = (short)(byte)(localDerValue.tag & 0x1F);
        if (i == 0)
        {
          this.nextUpdate = localDerValue.data.getGeneralizedTime();
          if (localDerInputStream.available() != 0)
          {
            localDerValue = localDerInputStream.getDerValue();
            i = (short)(byte)(localDerValue.tag & 0x1F);
          }
        }
        else
        {
          this.nextUpdate = null;
        }
      }
      if (localDerInputStream.available() > 0)
      {
        localDerValue = localDerInputStream.getDerValue();
        if (localDerValue.isContextSpecific((byte)1))
        {
          localObject = localDerValue.data.getSequence(3);
          this.singleExtensions = new HashMap(localObject.length);
          for (j = 0; j < localObject.length; j++)
          {
            sun.security.x509.Extension localExtension = new sun.security.x509.Extension(localObject[j]);
            if (OCSPResponse.debug != null) {
              OCSPResponse.debug.println("OCSP single extension: " + localExtension);
            }
            if (localExtension.isCritical()) {
              throw new IOException("Unsupported OCSP critical extension: " + localExtension.getExtensionId());
            }
            this.singleExtensions.put(localExtension.getId(), localExtension);
          }
        }
        else
        {
          this.singleExtensions = Collections.emptyMap();
        }
      }
      else
      {
        this.singleExtensions = Collections.emptyMap();
      }
    }
    
    public OCSP.RevocationStatus.CertStatus getCertStatus()
    {
      return this.certStatus;
    }
    
    private CertId getCertId()
    {
      return this.certId;
    }
    
    public Date getRevocationTime()
    {
      return (Date)this.revocationTime.clone();
    }
    
    public CRLReason getRevocationReason()
    {
      return this.revocationReason;
    }
    
    public Map<String, java.security.cert.Extension> getSingleExtensions()
    {
      return Collections.unmodifiableMap(this.singleExtensions);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SingleResponse:  \n");
      localStringBuilder.append(this.certId);
      localStringBuilder.append("\nCertStatus: " + this.certStatus + "\n");
      if (this.certStatus == OCSP.RevocationStatus.CertStatus.REVOKED)
      {
        localStringBuilder.append("revocationTime is " + this.revocationTime + "\n");
        localStringBuilder.append("revocationReason is " + this.revocationReason + "\n");
      }
      localStringBuilder.append("thisUpdate is " + this.thisUpdate + "\n");
      if (this.nextUpdate != null) {
        localStringBuilder.append("nextUpdate is " + this.nextUpdate + "\n");
      }
      return localStringBuilder.toString();
    }
  }
}
