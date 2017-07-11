package sun.security.pkcs12;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore.Entry;
import java.security.KeyStore.Entry.Attribute;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PKCS12Attribute;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.x500.X500Principal;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public final class PKCS12KeyStore
  extends KeyStoreSpi
{
  public static final int VERSION_3 = 3;
  private static final String[] KEY_PROTECTION_ALGORITHM = { "keystore.pkcs12.keyProtectionAlgorithm", "keystore.PKCS12.keyProtectionAlgorithm" };
  private static final String[] CORE_ATTRIBUTES = { "1.2.840.113549.1.9.20", "1.2.840.113549.1.9.21", "2.16.840.1.113894.746875.1.1" };
  private static final Debug debug = Debug.getInstance("pkcs12");
  private static final int[] keyBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 2 };
  private static final int[] certBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 3 };
  private static final int[] secretBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 5 };
  private static final int[] pkcs9Name = { 1, 2, 840, 113549, 1, 9, 20 };
  private static final int[] pkcs9KeyId = { 1, 2, 840, 113549, 1, 9, 21 };
  private static final int[] pkcs9certType = { 1, 2, 840, 113549, 1, 9, 22, 1 };
  private static final int[] pbeWithSHAAnd40BitRC2CBC = { 1, 2, 840, 113549, 1, 12, 1, 6 };
  private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = { 1, 2, 840, 113549, 1, 12, 1, 3 };
  private static final int[] pbes2 = { 1, 2, 840, 113549, 1, 5, 13 };
  private static final int[] TrustedKeyUsage = { 2, 16, 840, 1, 113894, 746875, 1, 1 };
  private static final int[] AnyExtendedKeyUsage = { 2, 5, 29, 37, 0 };
  private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
  private static ObjectIdentifier CertBag_OID;
  private static ObjectIdentifier SecretBag_OID;
  private static ObjectIdentifier PKCS9FriendlyName_OID;
  private static ObjectIdentifier PKCS9LocalKeyId_OID;
  private static ObjectIdentifier PKCS9CertType_OID;
  private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
  private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
  private static ObjectIdentifier pbes2_OID;
  private static ObjectIdentifier TrustedKeyUsage_OID;
  private static ObjectIdentifier[] AnyUsage;
  private int counter = 0;
  private static final int iterationCount = 1024;
  private static final int SALT_LEN = 20;
  private int privateKeyCount = 0;
  private int secretKeyCount = 0;
  private int certificateCount = 0;
  private SecureRandom random;
  private Map<String, Entry> entries = Collections.synchronizedMap(new LinkedHashMap());
  private ArrayList<KeyEntry> keyList = new ArrayList();
  private LinkedHashMap<X500Principal, X509Certificate> certsMap = new LinkedHashMap();
  private ArrayList<CertEntry> certEntries = new ArrayList();
  
  public PKCS12KeyStore() {}
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    Object localObject1 = null;
    if ((localEntry == null) || (!(localEntry instanceof KeyEntry))) {
      return null;
    }
    byte[] arrayOfByte1 = null;
    if ((localEntry instanceof PrivateKeyEntry)) {
      arrayOfByte1 = ((PrivateKeyEntry)localEntry).protectedPrivKey;
    } else if ((localEntry instanceof SecretKeyEntry)) {
      arrayOfByte1 = ((SecretKeyEntry)localEntry).protectedSecretKey;
    } else {
      throw new UnrecoverableKeyException("Error locating key");
    }
    byte[] arrayOfByte2;
    Object localObject2;
    Object localObject4;
    ObjectIdentifier localObjectIdentifier;
    AlgorithmParameters localAlgorithmParameters;
    try
    {
      EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
      arrayOfByte2 = localEncryptedPrivateKeyInfo.getEncryptedData();
      localObject2 = new DerValue(localEncryptedPrivateKeyInfo.getAlgorithm().encode());
      localObject4 = ((DerValue)localObject2).toDerInputStream();
      localObjectIdentifier = ((DerInputStream)localObject4).getOID();
      localAlgorithmParameters = parseAlgParameters(localObjectIdentifier, (DerInputStream)localObject4);
    }
    catch (IOException localIOException)
    {
      localObject2 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException);
      ((UnrecoverableKeyException)localObject2).initCause(localIOException);
      throw ((Throwable)localObject2);
    }
    try
    {
      byte[] arrayOfByte3;
      try
      {
        localObject2 = getPBEKey(paramArrayOfChar);
        localObject4 = Cipher.getInstance(mapPBEParamsToAlgorithm(localObjectIdentifier, localAlgorithmParameters));
        ((Cipher)localObject4).init(2, (Key)localObject2, localAlgorithmParameters);
        arrayOfByte3 = ((Cipher)localObject4).doFinal(arrayOfByte2);
      }
      catch (Exception localException2)
      {
        while (paramArrayOfChar.length == 0) {
          paramArrayOfChar = new char[1];
        }
        throw localException2;
      }
      localObject3 = new DerValue(arrayOfByte3);
      localObject4 = ((DerValue)localObject3).toDerInputStream();
      int i = ((DerInputStream)localObject4).getInteger();
      DerValue[] arrayOfDerValue = ((DerInputStream)localObject4).getSequence(2);
      AlgorithmId localAlgorithmId = new AlgorithmId(arrayOfDerValue[0].getOID());
      String str = localAlgorithmId.getName();
      Object localObject5;
      Object localObject6;
      if ((localEntry instanceof PrivateKeyEntry))
      {
        localObject5 = KeyFactory.getInstance(str);
        localObject6 = new PKCS8EncodedKeySpec(arrayOfByte3);
        localObject1 = ((KeyFactory)localObject5).generatePrivate((KeySpec)localObject6);
        if (debug != null) {
          debug.println("Retrieved a protected private key (" + localObject1.getClass().getName() + ") at alias '" + paramString + "'");
        }
      }
      else
      {
        localObject5 = SecretKeyFactory.getInstance(str);
        localObject6 = ((DerInputStream)localObject4).getOctetString();
        SecretKeySpec localSecretKeySpec = new SecretKeySpec((byte[])localObject6, str);
        if (str.startsWith("PBE"))
        {
          KeySpec localKeySpec = ((SecretKeyFactory)localObject5).getKeySpec(localSecretKeySpec, PBEKeySpec.class);
          localObject1 = ((SecretKeyFactory)localObject5).generateSecret(localKeySpec);
        }
        else
        {
          localObject1 = ((SecretKeyFactory)localObject5).generateSecret(localSecretKeySpec);
        }
        if (debug != null) {
          debug.println("Retrieved a protected secret key (" + localObject1.getClass().getName() + ") at alias '" + paramString + "'");
        }
      }
    }
    catch (Exception localException1)
    {
      Object localObject3 = new UnrecoverableKeyException("Get Key failed: " + localException1.getMessage());
      ((UnrecoverableKeyException)localObject3).initCause(localException1);
      throw ((Throwable)localObject3);
    }
    return localObject1;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString)
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if ((localEntry != null) && ((localEntry instanceof PrivateKeyEntry)))
    {
      if (((PrivateKeyEntry)localEntry).chain == null) {
        return null;
      }
      if (debug != null) {
        debug.println("Retrieved a " + ((PrivateKeyEntry)localEntry).chain.length + "-certificate chain at alias '" + paramString + "'");
      }
      return (Certificate[])((PrivateKeyEntry)localEntry).chain.clone();
    }
    return null;
  }
  
  public Certificate engineGetCertificate(String paramString)
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (localEntry == null) {
      return null;
    }
    if (((localEntry instanceof CertEntry)) && (((CertEntry)localEntry).trustedKeyUsage != null))
    {
      if (debug != null) {
        if (Arrays.equals(AnyUsage, ((CertEntry)localEntry).trustedKeyUsage)) {
          debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for any purpose)");
        } else {
          debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for limited purposes)");
        }
      }
      return ((CertEntry)localEntry).cert;
    }
    if ((localEntry instanceof PrivateKeyEntry))
    {
      if (((PrivateKeyEntry)localEntry).chain == null) {
        return null;
      }
      if (debug != null) {
        debug.println("Retrieved a certificate at alias '" + paramString + "'");
      }
      return ((PrivateKeyEntry)localEntry).chain[0];
    }
    return null;
  }
  
  public Date engineGetCreationDate(String paramString)
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (localEntry != null) {
      return new Date(localEntry.date.getTime());
    }
    return null;
  }
  
  public synchronized void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    KeyStore.PasswordProtection localPasswordProtection = new KeyStore.PasswordProtection(paramArrayOfChar);
    try
    {
      setKeyEntry(paramString, paramKey, localPasswordProtection, paramArrayOfCertificate, null);
      return;
    }
    finally
    {
      try
      {
        localPasswordProtection.destroy();
      }
      catch (DestroyFailedException localDestroyFailedException2) {}
    }
  }
  
  private void setKeyEntry(String paramString, Key paramKey, KeyStore.PasswordProtection paramPasswordProtection, Certificate[] paramArrayOfCertificate, Set<KeyStore.Entry.Attribute> paramSet)
    throws KeyStoreException
  {
    try
    {
      Object localObject2;
      Object localObject1;
      if ((paramKey instanceof PrivateKey))
      {
        localObject2 = new PrivateKeyEntry(null);
        ((PrivateKeyEntry)localObject2).date = new Date();
        if ((paramKey.getFormat().equals("PKCS#8")) || (paramKey.getFormat().equals("PKCS8")))
        {
          if (debug != null) {
            debug.println("Setting a protected private key (" + paramKey.getClass().getName() + ") at alias '" + paramString + "'");
          }
          ((PrivateKeyEntry)localObject2).protectedPrivKey = encryptPrivateKey(paramKey.getEncoded(), paramPasswordProtection);
        }
        else
        {
          throw new KeyStoreException("Private key is not encodedas PKCS#8");
        }
        if (paramArrayOfCertificate != null)
        {
          if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
            throw new KeyStoreException("Certificate chain is not valid");
          }
          ((PrivateKeyEntry)localObject2).chain = ((Certificate[])paramArrayOfCertificate.clone());
          this.certificateCount += paramArrayOfCertificate.length;
          if (debug != null) {
            debug.println("Setting a " + paramArrayOfCertificate.length + "-certificate chain at alias '" + paramString + "'");
          }
        }
        this.privateKeyCount += 1;
        localObject1 = localObject2;
      }
      else if ((paramKey instanceof SecretKey))
      {
        localObject2 = new SecretKeyEntry(null);
        ((SecretKeyEntry)localObject2).date = new Date();
        DerOutputStream localDerOutputStream1 = new DerOutputStream();
        DerOutputStream localDerOutputStream2 = new DerOutputStream();
        localDerOutputStream2.putInteger(0);
        AlgorithmId localAlgorithmId = AlgorithmId.get(paramKey.getAlgorithm());
        localAlgorithmId.encode(localDerOutputStream2);
        localDerOutputStream2.putOctetString(paramKey.getEncoded());
        localDerOutputStream1.write((byte)48, localDerOutputStream2);
        ((SecretKeyEntry)localObject2).protectedSecretKey = encryptPrivateKey(localDerOutputStream1.toByteArray(), paramPasswordProtection);
        if (debug != null) {
          debug.println("Setting a protected secret key (" + paramKey.getClass().getName() + ") at alias '" + paramString + "'");
        }
        this.secretKeyCount += 1;
        localObject1 = localObject2;
      }
      else
      {
        throw new KeyStoreException("Unsupported Key type");
      }
      localObject1.attributes = new HashSet();
      if (paramSet != null) {
        localObject1.attributes.addAll(paramSet);
      }
      localObject1.keyId = ("Time " + localObject1.date.getTime()).getBytes("UTF8");
      localObject1.alias = paramString.toLowerCase(Locale.ENGLISH);
      this.entries.put(paramString.toLowerCase(Locale.ENGLISH), localObject1);
    }
    catch (Exception localException)
    {
      throw new KeyStoreException("Key protection  algorithm not found: " + localException, localException);
    }
  }
  
  public synchronized void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    try
    {
      new EncryptedPrivateKeyInfo(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException, localIOException);
    }
    PrivateKeyEntry localPrivateKeyEntry = new PrivateKeyEntry(null);
    localPrivateKeyEntry.date = new Date();
    if (debug != null) {
      debug.println("Setting a protected private key at alias '" + paramString + "'");
    }
    try
    {
      localPrivateKeyEntry.keyId = ("Time " + localPrivateKeyEntry.date.getTime()).getBytes("UTF8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    localPrivateKeyEntry.alias = paramString.toLowerCase(Locale.ENGLISH);
    localPrivateKeyEntry.protectedPrivKey = ((byte[])paramArrayOfByte.clone());
    if (paramArrayOfCertificate != null)
    {
      if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
        throw new KeyStoreException("Certificate chain is not valid");
      }
      localPrivateKeyEntry.chain = ((Certificate[])paramArrayOfCertificate.clone());
      this.certificateCount += paramArrayOfCertificate.length;
      if (debug != null) {
        debug.println("Setting a " + localPrivateKeyEntry.chain.length + "-certificate chain at alias '" + paramString + "'");
      }
    }
    this.privateKeyCount += 1;
    this.entries.put(paramString.toLowerCase(Locale.ENGLISH), localPrivateKeyEntry);
  }
  
  private byte[] getSalt()
  {
    byte[] arrayOfByte = new byte[20];
    if (this.random == null) {
      this.random = new SecureRandom();
    }
    this.random.nextBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  private AlgorithmParameters getAlgorithmParameters(String paramString)
    throws IOException
  {
    AlgorithmParameters localAlgorithmParameters = null;
    PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(getSalt(), 1024);
    try
    {
      localAlgorithmParameters = AlgorithmParameters.getInstance(paramString);
      localAlgorithmParameters.init(localPBEParameterSpec);
    }
    catch (Exception localException)
    {
      throw new IOException("getAlgorithmParameters failed: " + localException.getMessage(), localException);
    }
    return localAlgorithmParameters;
  }
  
  private AlgorithmParameters parseAlgParameters(ObjectIdentifier paramObjectIdentifier, DerInputStream paramDerInputStream)
    throws IOException
  {
    AlgorithmParameters localAlgorithmParameters = null;
    try
    {
      DerValue localDerValue;
      if (paramDerInputStream.available() == 0)
      {
        localDerValue = null;
      }
      else
      {
        localDerValue = paramDerInputStream.getDerValue();
        if (localDerValue.tag == 5) {
          localDerValue = null;
        }
      }
      if (localDerValue != null)
      {
        if (paramObjectIdentifier.equals(pbes2_OID)) {
          localAlgorithmParameters = AlgorithmParameters.getInstance("PBES2");
        } else {
          localAlgorithmParameters = AlgorithmParameters.getInstance("PBE");
        }
        localAlgorithmParameters.init(localDerValue.toByteArray());
      }
    }
    catch (Exception localException)
    {
      throw new IOException("parseAlgParameters failed: " + localException.getMessage(), localException);
    }
    return localAlgorithmParameters;
  }
  
  private SecretKey getPBEKey(char[] paramArrayOfChar)
    throws IOException
  {
    SecretKey localSecretKey = null;
    try
    {
      PBEKeySpec localPBEKeySpec = new PBEKeySpec(paramArrayOfChar);
      SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("PBE");
      localSecretKey = localSecretKeyFactory.generateSecret(localPBEKeySpec);
      localPBEKeySpec.clearPassword();
    }
    catch (Exception localException)
    {
      throw new IOException("getSecretKey failed: " + localException.getMessage(), localException);
    }
    return localSecretKey;
  }
  
  private byte[] encryptPrivateKey(byte[] paramArrayOfByte, KeyStore.PasswordProtection paramPasswordProtection)
    throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    byte[] arrayOfByte1 = null;
    try
    {
      String str = paramPasswordProtection.getProtectionAlgorithm();
      if (str != null)
      {
        localObject2 = paramPasswordProtection.getProtectionParameters();
        if (localObject2 != null)
        {
          localObject1 = AlgorithmParameters.getInstance(str);
          ((AlgorithmParameters)localObject1).init((AlgorithmParameterSpec)localObject2);
        }
        else
        {
          localObject1 = getAlgorithmParameters(str);
        }
      }
      else
      {
        str = (String)AccessController.doPrivileged(new PrivilegedAction()
        {
          public String run()
          {
            String str = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[0]);
            if (str == null) {
              str = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[1]);
            }
            return str;
          }
        });
        if ((str == null) || (str.isEmpty())) {
          str = "PBEWithSHA1AndDESede";
        }
        localObject1 = getAlgorithmParameters(str);
      }
      Object localObject2 = mapPBEAlgorithmToOID(str);
      if (localObject2 == null) {
        throw new IOException("PBE algorithm '" + str + " 'is not supported for key entry protection");
      }
      SecretKey localSecretKey = getPBEKey(paramPasswordProtection.getPassword());
      Cipher localCipher = Cipher.getInstance(str);
      localCipher.init(1, localSecretKey, (AlgorithmParameters)localObject1);
      byte[] arrayOfByte2 = localCipher.doFinal(paramArrayOfByte);
      AlgorithmId localAlgorithmId = new AlgorithmId((ObjectIdentifier)localObject2, localCipher.getParameters());
      if (debug != null) {
        debug.println("  (Cipher algorithm: " + localCipher.getAlgorithm() + ")");
      }
      EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(localAlgorithmId, arrayOfByte2);
      arrayOfByte1 = localEncryptedPrivateKeyInfo.getEncoded();
    }
    catch (Exception localException)
    {
      Object localObject1 = new UnrecoverableKeyException("Encrypt Private Key failed: " + localException.getMessage());
      ((UnrecoverableKeyException)localObject1).initCause(localException);
      throw ((Throwable)localObject1);
    }
    return arrayOfByte1;
  }
  
  private static ObjectIdentifier mapPBEAlgorithmToOID(String paramString)
    throws NoSuchAlgorithmException
  {
    if (paramString.toLowerCase(Locale.ENGLISH).startsWith("pbewithhmacsha")) {
      return pbes2_OID;
    }
    return AlgorithmId.get(paramString).getOID();
  }
  
  private static String mapPBEParamsToAlgorithm(ObjectIdentifier paramObjectIdentifier, AlgorithmParameters paramAlgorithmParameters)
    throws NoSuchAlgorithmException
  {
    if ((paramObjectIdentifier.equals(pbes2_OID)) && (paramAlgorithmParameters != null)) {
      return paramAlgorithmParameters.toString();
    }
    return paramObjectIdentifier.toString();
  }
  
  public synchronized void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    setCertEntry(paramString, paramCertificate, null);
  }
  
  private void setCertEntry(String paramString, Certificate paramCertificate, Set<KeyStore.Entry.Attribute> paramSet)
    throws KeyStoreException
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if ((localEntry != null) && ((localEntry instanceof KeyEntry))) {
      throw new KeyStoreException("Cannot overwrite own certificate");
    }
    CertEntry localCertEntry = new CertEntry((X509Certificate)paramCertificate, null, paramString, AnyUsage, paramSet);
    this.certificateCount += 1;
    this.entries.put(paramString, localCertEntry);
    if (debug != null) {
      debug.println("Setting a trusted certificate at alias '" + paramString + "'");
    }
  }
  
  public synchronized void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    if (debug != null) {
      debug.println("Removing entry at alias '" + paramString + "'");
    }
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if ((localEntry instanceof PrivateKeyEntry))
    {
      PrivateKeyEntry localPrivateKeyEntry = (PrivateKeyEntry)localEntry;
      if (localPrivateKeyEntry.chain != null) {
        this.certificateCount -= localPrivateKeyEntry.chain.length;
      }
      this.privateKeyCount -= 1;
    }
    else if ((localEntry instanceof CertEntry))
    {
      this.certificateCount -= 1;
    }
    else if ((localEntry instanceof SecretKeyEntry))
    {
      this.secretKeyCount -= 1;
    }
    this.entries.remove(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public Enumeration<String> engineAliases()
  {
    return Collections.enumeration(this.entries.keySet());
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    return this.entries.containsKey(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public int engineSize()
  {
    return this.entries.size();
  }
  
  public boolean engineIsKeyEntry(String paramString)
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (localEntry != null) && ((localEntry instanceof KeyEntry));
  }
  
  public boolean engineIsCertificateEntry(String paramString)
  {
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (localEntry != null) && ((localEntry instanceof CertEntry)) && (((CertEntry)localEntry).trustedKeyUsage != null);
  }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass)
  {
    if (paramClass == KeyStore.TrustedCertificateEntry.class) {
      return engineIsCertificateEntry(paramString);
    }
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (paramClass == KeyStore.PrivateKeyEntry.class) {
      return (localEntry != null) && ((localEntry instanceof PrivateKeyEntry));
    }
    if (paramClass == KeyStore.SecretKeyEntry.class) {
      return (localEntry != null) && ((localEntry instanceof SecretKeyEntry));
    }
    return false;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate)
  {
    Object localObject = null;
    Enumeration localEnumeration = engineAliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Entry localEntry = (Entry)this.entries.get(str);
      if ((localEntry instanceof PrivateKeyEntry))
      {
        if (((PrivateKeyEntry)localEntry).chain != null) {
          localObject = ((PrivateKeyEntry)localEntry).chain[0];
        }
      }
      else
      {
        if ((!(localEntry instanceof CertEntry)) || (((CertEntry)localEntry).trustedKeyUsage == null)) {
          continue;
        }
        localObject = ((CertEntry)localEntry).cert;
      }
      if ((localObject != null) && (((Certificate)localObject).equals(paramCertificate))) {
        return str;
      }
    }
    return null;
  }
  
  public synchronized void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if (paramArrayOfChar == null) {
      throw new IllegalArgumentException("password can't be null");
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(3);
    byte[] arrayOfByte1 = localDerOutputStream2.toByteArray();
    localDerOutputStream1.write(arrayOfByte1);
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    DerOutputStream localDerOutputStream4 = new DerOutputStream();
    if ((this.privateKeyCount > 0) || (this.secretKeyCount > 0))
    {
      if (debug != null) {
        debug.println("Storing " + (this.privateKeyCount + this.secretKeyCount) + " protected key(s) in a PKCS#7 data content-type");
      }
      localObject1 = createSafeContent();
      localObject2 = new ContentInfo((byte[])localObject1);
      ((ContentInfo)localObject2).encode(localDerOutputStream4);
    }
    if (this.certificateCount > 0)
    {
      if (debug != null) {
        debug.println("Storing " + this.certificateCount + " certificate(s) in a PKCS#7 encryptedData content-type");
      }
      localObject1 = createEncryptedData(paramArrayOfChar);
      localObject2 = new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue((byte[])localObject1));
      ((ContentInfo)localObject2).encode(localDerOutputStream4);
    }
    Object localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream4);
    Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
    ContentInfo localContentInfo = new ContentInfo((byte[])localObject2);
    localContentInfo.encode(localDerOutputStream3);
    byte[] arrayOfByte2 = localDerOutputStream3.toByteArray();
    localDerOutputStream1.write(arrayOfByte2);
    byte[] arrayOfByte3 = calculateMac(paramArrayOfChar, (byte[])localObject2);
    localDerOutputStream1.write(arrayOfByte3);
    DerOutputStream localDerOutputStream5 = new DerOutputStream();
    localDerOutputStream5.write((byte)48, localDerOutputStream1);
    byte[] arrayOfByte4 = localDerOutputStream5.toByteArray();
    paramOutputStream.write(arrayOfByte4);
    paramOutputStream.flush();
  }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
  {
    if (!engineContainsAlias(paramString)) {
      return null;
    }
    Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (paramProtectionParameter == null) {
      if (engineIsCertificateEntry(paramString))
      {
        if (((localEntry instanceof CertEntry)) && (((CertEntry)localEntry).trustedKeyUsage != null))
        {
          if (debug != null) {
            debug.println("Retrieved a trusted certificate at alias '" + paramString + "'");
          }
          return new KeyStore.TrustedCertificateEntry(((CertEntry)localEntry).cert, getAttributes(localEntry));
        }
      }
      else {
        throw new UnrecoverableKeyException("requested entry requires a password");
      }
    }
    if ((paramProtectionParameter instanceof KeyStore.PasswordProtection))
    {
      if (engineIsCertificateEntry(paramString)) {
        throw new UnsupportedOperationException("trusted certificate entries are not password-protected");
      }
      if (engineIsKeyEntry(paramString))
      {
        KeyStore.PasswordProtection localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
        char[] arrayOfChar = localPasswordProtection.getPassword();
        Key localKey = engineGetKey(paramString, arrayOfChar);
        if ((localKey instanceof PrivateKey))
        {
          Certificate[] arrayOfCertificate = engineGetCertificateChain(paramString);
          return new KeyStore.PrivateKeyEntry((PrivateKey)localKey, arrayOfCertificate, getAttributes(localEntry));
        }
        if ((localKey instanceof SecretKey)) {
          return new KeyStore.SecretKeyEntry((SecretKey)localKey, getAttributes(localEntry));
        }
      }
      else if (!engineIsKeyEntry(paramString))
      {
        throw new UnsupportedOperationException("untrusted certificate entries are not password-protected");
      }
    }
    throw new UnsupportedOperationException();
  }
  
  public synchronized void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    if ((paramProtectionParameter != null) && (!(paramProtectionParameter instanceof KeyStore.PasswordProtection))) {
      throw new KeyStoreException("unsupported protection parameter");
    }
    KeyStore.PasswordProtection localPasswordProtection = null;
    if (paramProtectionParameter != null) {
      localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
    }
    Object localObject;
    if ((paramEntry instanceof KeyStore.TrustedCertificateEntry))
    {
      if ((paramProtectionParameter != null) && (localPasswordProtection.getPassword() != null)) {
        throw new KeyStoreException("trusted certificate entries are not password-protected");
      }
      localObject = (KeyStore.TrustedCertificateEntry)paramEntry;
      setCertEntry(paramString, ((KeyStore.TrustedCertificateEntry)localObject).getTrustedCertificate(), ((KeyStore.TrustedCertificateEntry)localObject).getAttributes());
      return;
    }
    if ((paramEntry instanceof KeyStore.PrivateKeyEntry))
    {
      if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null)) {
        throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
      }
      localObject = (KeyStore.PrivateKeyEntry)paramEntry;
      setKeyEntry(paramString, ((KeyStore.PrivateKeyEntry)localObject).getPrivateKey(), localPasswordProtection, ((KeyStore.PrivateKeyEntry)localObject).getCertificateChain(), ((KeyStore.PrivateKeyEntry)localObject).getAttributes());
      return;
    }
    if ((paramEntry instanceof KeyStore.SecretKeyEntry))
    {
      if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null)) {
        throw new KeyStoreException("non-null password required to create SecretKeyEntry");
      }
      localObject = (KeyStore.SecretKeyEntry)paramEntry;
      setKeyEntry(paramString, ((KeyStore.SecretKeyEntry)localObject).getSecretKey(), localPasswordProtection, (Certificate[])null, ((KeyStore.SecretKeyEntry)localObject).getAttributes());
      return;
    }
    throw new KeyStoreException("unsupported entry type: " + paramEntry.getClass().getName());
  }
  
  private Set<KeyStore.Entry.Attribute> getAttributes(Entry paramEntry)
  {
    if (paramEntry.attributes == null) {
      paramEntry.attributes = new HashSet();
    }
    paramEntry.attributes.add(new PKCS12Attribute(PKCS9FriendlyName_OID.toString(), paramEntry.alias));
    byte[] arrayOfByte = paramEntry.keyId;
    if (arrayOfByte != null) {
      paramEntry.attributes.add(new PKCS12Attribute(PKCS9LocalKeyId_OID.toString(), Debug.toString(arrayOfByte)));
    }
    if ((paramEntry instanceof CertEntry))
    {
      ObjectIdentifier[] arrayOfObjectIdentifier = ((CertEntry)paramEntry).trustedKeyUsage;
      if (arrayOfObjectIdentifier != null) {
        if (arrayOfObjectIdentifier.length == 1) {
          paramEntry.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), arrayOfObjectIdentifier[0].toString()));
        } else {
          paramEntry.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), Arrays.toString(arrayOfObjectIdentifier)));
        }
      }
    }
    return paramEntry.attributes;
  }
  
  private byte[] generateHash(byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte = null;
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
      localMessageDigest.update(paramArrayOfByte);
      arrayOfByte = localMessageDigest.digest();
    }
    catch (Exception localException)
    {
      throw new IOException("generateHash failed: " + localException, localException);
    }
    return arrayOfByte;
  }
  
  private byte[] calculateMac(char[] paramArrayOfChar, byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    String str = "SHA1";
    try
    {
      byte[] arrayOfByte2 = getSalt();
      Mac localMac = Mac.getInstance("HmacPBESHA1");
      PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(arrayOfByte2, 1024);
      SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
      localMac.init(localSecretKey, localPBEParameterSpec);
      localMac.update(paramArrayOfByte);
      byte[] arrayOfByte3 = localMac.doFinal();
      MacData localMacData = new MacData(str, arrayOfByte3, arrayOfByte2, 1024);
      DerOutputStream localDerOutputStream = new DerOutputStream();
      localDerOutputStream.write(localMacData.getEncoded());
      arrayOfByte1 = localDerOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
      throw new IOException("calculateMac failed: " + localException, localException);
    }
    return arrayOfByte1;
  }
  
  private boolean validateChain(Certificate[] paramArrayOfCertificate)
  {
    for (int i = 0; i < paramArrayOfCertificate.length - 1; i++)
    {
      X500Principal localX500Principal1 = ((X509Certificate)paramArrayOfCertificate[i]).getIssuerX500Principal();
      X500Principal localX500Principal2 = ((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectX500Principal();
      if (!localX500Principal1.equals(localX500Principal2)) {
        return false;
      }
    }
    HashSet localHashSet = new HashSet(Arrays.asList(paramArrayOfCertificate));
    return localHashSet.size() == paramArrayOfCertificate.length;
  }
  
  private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, Set<KeyStore.Entry.Attribute> paramSet)
    throws IOException
  {
    return getBagAttributes(paramString, paramArrayOfByte, null, paramSet);
  }
  
  private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<KeyStore.Entry.Attribute> paramSet)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    if ((paramString == null) && (paramArrayOfByte == null) && (arrayOfByte3 == null)) {
      return null;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    Object localObject1;
    Object localObject2;
    if (paramString != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOID(PKCS9FriendlyName_OID);
      localObject1 = new DerOutputStream();
      localObject2 = new DerOutputStream();
      ((DerOutputStream)localObject1).putBMPString(paramString);
      localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
      ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
      arrayOfByte2 = ((DerOutputStream)localObject2).toByteArray();
    }
    if (paramArrayOfByte != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOID(PKCS9LocalKeyId_OID);
      localObject1 = new DerOutputStream();
      localObject2 = new DerOutputStream();
      ((DerOutputStream)localObject1).putOctetString(paramArrayOfByte);
      localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
      ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
      arrayOfByte1 = ((DerOutputStream)localObject2).toByteArray();
    }
    if (paramArrayOfObjectIdentifier != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOID(TrustedKeyUsage_OID);
      localObject1 = new DerOutputStream();
      localObject2 = new DerOutputStream();
      for (ObjectIdentifier localObjectIdentifier : paramArrayOfObjectIdentifier) {
        ((DerOutputStream)localObject1).putOID(localObjectIdentifier);
      }
      localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
      ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
      arrayOfByte3 = ((DerOutputStream)localObject2).toByteArray();
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    if (arrayOfByte2 != null) {
      localDerOutputStream2.write(arrayOfByte2);
    }
    if (arrayOfByte1 != null) {
      localDerOutputStream2.write(arrayOfByte1);
    }
    if (arrayOfByte3 != null) {
      localDerOutputStream2.write(arrayOfByte3);
    }
    if (paramSet != null)
    {
      localObject1 = paramSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (KeyStore.Entry.Attribute)((Iterator)localObject1).next();
        ??? = ((KeyStore.Entry.Attribute)localObject2).getName();
        if ((!CORE_ATTRIBUTES[0].equals(???)) && (!CORE_ATTRIBUTES[1].equals(???)) && (!CORE_ATTRIBUTES[2].equals(???))) {
          localDerOutputStream2.write(((PKCS12Attribute)localObject2).getEncoded());
        }
      }
    }
    localDerOutputStream1.write((byte)49, localDerOutputStream2);
    return localDerOutputStream1.toByteArray();
  }
  
  private byte[] createEncryptedData(char[] paramArrayOfChar)
    throws CertificateException, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    Object localObject1 = engineAliases();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      localObject2 = (String)((Enumeration)localObject1).nextElement();
      localObject3 = (Entry)this.entries.get(localObject2);
      if ((localObject3 instanceof PrivateKeyEntry))
      {
        PrivateKeyEntry localPrivateKeyEntry = (PrivateKeyEntry)localObject3;
        if (localPrivateKeyEntry.chain != null) {
          localObject4 = localPrivateKeyEntry.chain;
        } else {
          localObject4 = new Certificate[0];
        }
      }
      else if ((localObject3 instanceof CertEntry))
      {
        localObject4 = new Certificate[] { ((CertEntry)localObject3).cert };
      }
      else
      {
        localObject4 = new Certificate[0];
      }
      for (int i = 0; i < localObject4.length; i++)
      {
        DerOutputStream localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOID(CertBag_OID);
        DerOutputStream localDerOutputStream4 = new DerOutputStream();
        localDerOutputStream4.putOID(PKCS9CertType_OID);
        DerOutputStream localDerOutputStream5 = new DerOutputStream();
        X509Certificate localX509Certificate = (X509Certificate)localObject4[i];
        localDerOutputStream5.putOctetString(localX509Certificate.getEncoded());
        localDerOutputStream4.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream5);
        DerOutputStream localDerOutputStream6 = new DerOutputStream();
        localDerOutputStream6.write((byte)48, localDerOutputStream4);
        byte[] arrayOfByte1 = localDerOutputStream6.toByteArray();
        DerOutputStream localDerOutputStream7 = new DerOutputStream();
        localDerOutputStream7.write(arrayOfByte1);
        localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream7);
        byte[] arrayOfByte2 = null;
        if (i == 0)
        {
          Object localObject5;
          if ((localObject3 instanceof KeyEntry))
          {
            localObject5 = (KeyEntry)localObject3;
            arrayOfByte2 = getBagAttributes(((KeyEntry)localObject5).alias, ((KeyEntry)localObject5).keyId, ((KeyEntry)localObject5).attributes);
          }
          else
          {
            localObject5 = (CertEntry)localObject3;
            arrayOfByte2 = getBagAttributes(((CertEntry)localObject5).alias, ((CertEntry)localObject5).keyId, ((CertEntry)localObject5).trustedKeyUsage, ((CertEntry)localObject5).attributes);
          }
        }
        else
        {
          arrayOfByte2 = getBagAttributes(localX509Certificate.getSubjectX500Principal().getName(), null, ((Entry)localObject3).attributes);
        }
        if (arrayOfByte2 != null) {
          localDerOutputStream3.write(arrayOfByte2);
        }
        localDerOutputStream1.write((byte)48, localDerOutputStream3);
      }
    }
    localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
    Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
    Object localObject3 = encryptContent((byte[])localObject2, paramArrayOfChar);
    Object localObject4 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    ((DerOutputStream)localObject4).putInteger(0);
    ((DerOutputStream)localObject4).write((byte[])localObject3);
    localDerOutputStream2.write((byte)48, (DerOutputStream)localObject4);
    return localDerOutputStream2.toByteArray();
  }
  
  private byte[] createSafeContent()
    throws CertificateException, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    Object localObject1 = engineAliases();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      String str = (String)((Enumeration)localObject1).nextElement();
      Entry localEntry = (Entry)this.entries.get(str);
      if ((localEntry != null) && ((localEntry instanceof KeyEntry)))
      {
        DerOutputStream localDerOutputStream2 = new DerOutputStream();
        KeyEntry localKeyEntry = (KeyEntry)localEntry;
        Object localObject3;
        DerOutputStream localDerOutputStream3;
        if ((localKeyEntry instanceof PrivateKeyEntry))
        {
          localDerOutputStream2.putOID(PKCS8ShroudedKeyBag_OID);
          localObject2 = ((PrivateKeyEntry)localKeyEntry).protectedPrivKey;
          localObject3 = null;
          try
          {
            localObject3 = new EncryptedPrivateKeyInfo((byte[])localObject2);
          }
          catch (IOException localIOException)
          {
            throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + localIOException.getMessage());
          }
          localDerOutputStream3 = new DerOutputStream();
          localDerOutputStream3.write(((EncryptedPrivateKeyInfo)localObject3).getEncoded());
          localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream3);
        }
        else
        {
          if (!(localKeyEntry instanceof SecretKeyEntry)) {
            continue;
          }
          localDerOutputStream2.putOID(SecretBag_OID);
          localObject2 = new DerOutputStream();
          ((DerOutputStream)localObject2).putOID(PKCS8ShroudedKeyBag_OID);
          localObject3 = new DerOutputStream();
          ((DerOutputStream)localObject3).putOctetString(((SecretKeyEntry)localKeyEntry).protectedSecretKey);
          ((DerOutputStream)localObject2).write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), (DerOutputStream)localObject3);
          localDerOutputStream3 = new DerOutputStream();
          localDerOutputStream3.write((byte)48, (DerOutputStream)localObject2);
          byte[] arrayOfByte = localDerOutputStream3.toByteArray();
          DerOutputStream localDerOutputStream4 = new DerOutputStream();
          localDerOutputStream4.write(arrayOfByte);
          localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream4);
        }
        Object localObject2 = getBagAttributes(str, localEntry.keyId, localEntry.attributes);
        localDerOutputStream2.write((byte[])localObject2);
        localDerOutputStream1.write((byte)48, localDerOutputStream2);
      }
    }
    localObject1 = new DerOutputStream();
    ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
    return ((DerOutputStream)localObject1).toByteArray();
  }
  
  private byte[] encryptContent(byte[] paramArrayOfByte, char[] paramArrayOfChar)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    AlgorithmParameters localAlgorithmParameters = getAlgorithmParameters("PBEWithSHA1AndRC2_40");
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    AlgorithmId localAlgorithmId = new AlgorithmId(pbeWithSHAAnd40BitRC2CBC_OID, localAlgorithmParameters);
    localAlgorithmId.encode(localDerOutputStream1);
    byte[] arrayOfByte2 = localDerOutputStream1.toByteArray();
    try
    {
      SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
      localObject = Cipher.getInstance("PBEWithSHA1AndRC2_40");
      ((Cipher)localObject).init(1, localSecretKey, localAlgorithmParameters);
      arrayOfByte1 = ((Cipher)localObject).doFinal(paramArrayOfByte);
      if (debug != null) {
        debug.println("  (Cipher algorithm: " + ((Cipher)localObject).getAlgorithm() + ")");
      }
    }
    catch (Exception localException)
    {
      throw new IOException("Failed to encrypt safe contents entry: " + localException, localException);
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOID(ContentInfo.DATA_OID);
    localDerOutputStream2.write(arrayOfByte2);
    Object localObject = new DerOutputStream();
    ((DerOutputStream)localObject).putOctetString(arrayOfByte1);
    localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), (DerOutputStream)localObject);
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write((byte)48, localDerOutputStream2);
    return localDerOutputStream3.toByteArray();
  }
  
  public synchronized void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    if (paramInputStream == null) {
      return;
    }
    this.counter = 0;
    DerValue localDerValue = new DerValue(paramInputStream);
    DerInputStream localDerInputStream1 = localDerValue.toDerInputStream();
    int i = localDerInputStream1.getInteger();
    if (i != 3) {
      throw new IOException("PKCS12 keystore not in version 3 format");
    }
    this.entries.clear();
    ContentInfo localContentInfo = new ContentInfo(localDerInputStream1);
    ObjectIdentifier localObjectIdentifier1 = localContentInfo.getContentType();
    byte[] arrayOfByte;
    if (localObjectIdentifier1.equals(ContentInfo.DATA_OID)) {
      arrayOfByte = localContentInfo.getData();
    } else {
      throw new IOException("public key protected PKCS12 not supported");
    }
    DerInputStream localDerInputStream2 = new DerInputStream(arrayOfByte);
    DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
    int j = arrayOfDerValue1.length;
    this.privateKeyCount = 0;
    this.secretKeyCount = 0;
    this.certificateCount = 0;
    Object localObject8;
    Object localObject7;
    Object localObject6;
    Object localObject5;
    Object localObject9;
    for (int k = 0; k < j; k++)
    {
      localObject8 = null;
      localObject7 = new DerInputStream(arrayOfDerValue1[k].toByteArray());
      localObject6 = new ContentInfo((DerInputStream)localObject7);
      localObjectIdentifier1 = ((ContentInfo)localObject6).getContentType();
      localObject5 = null;
      if (localObjectIdentifier1.equals(ContentInfo.DATA_OID))
      {
        if (debug != null) {
          debug.println("Loading PKCS#7 data content-type");
        }
        localObject5 = ((ContentInfo)localObject6).getData();
      }
      else if (localObjectIdentifier1.equals(ContentInfo.ENCRYPTED_DATA_OID))
      {
        if (paramArrayOfChar == null)
        {
          if (debug == null) {
            continue;
          }
          debug.println("Warning: skipping PKCS#7 encryptedData content-type - no password was supplied");
          continue;
        }
        if (debug != null) {
          debug.println("Loading PKCS#7 encryptedData content-type");
        }
        localObject9 = ((ContentInfo)localObject6).getContent().toDerInputStream();
        int n = ((DerInputStream)localObject9).getInteger();
        DerValue[] arrayOfDerValue2 = ((DerInputStream)localObject9).getSequence(2);
        ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue2[0].getOID();
        localObject8 = arrayOfDerValue2[1].toByteArray();
        if (!arrayOfDerValue2[2].isContextSpecific((byte)0)) {
          throw new IOException("encrypted content not present!");
        }
        byte b = 4;
        if (arrayOfDerValue2[2].isConstructed()) {
          b = (byte)(b | 0x20);
        }
        arrayOfDerValue2[2].resetTag(b);
        localObject5 = arrayOfDerValue2[2].getOctetString();
        DerInputStream localDerInputStream3 = arrayOfDerValue2[1].toDerInputStream();
        ObjectIdentifier localObjectIdentifier3 = localDerInputStream3.getOID();
        AlgorithmParameters localAlgorithmParameters = parseAlgParameters(localObjectIdentifier3, localDerInputStream3);
        try
        {
          SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
          Cipher localCipher = Cipher.getInstance(localObjectIdentifier3.toString());
          localCipher.init(2, localSecretKey, localAlgorithmParameters);
          localObject5 = localCipher.doFinal((byte[])localObject5);
        }
        catch (Exception localException2)
        {
          while (paramArrayOfChar.length == 0) {
            paramArrayOfChar = new char[1];
          }
          throw new IOException("keystore password was incorrect", new UnrecoverableKeyException("failed to decrypt safe contents entry: " + localException2));
        }
      }
      else
      {
        throw new IOException("public key protected PKCS12 not supported");
      }
      localObject9 = new DerInputStream((byte[])localObject5);
      loadSafeContents((DerInputStream)localObject9, paramArrayOfChar);
    }
    if ((paramArrayOfChar != null) && (localDerInputStream1.available() > 0))
    {
      localObject4 = new MacData(localDerInputStream1);
      try
      {
        localObject5 = ((MacData)localObject4).getDigestAlgName().toUpperCase(Locale.ENGLISH);
        localObject5 = ((String)localObject5).replace("-", "");
        localObject6 = Mac.getInstance("HmacPBE" + (String)localObject5);
        localObject7 = new PBEParameterSpec(((MacData)localObject4).getSalt(), ((MacData)localObject4).getIterations());
        localObject8 = getPBEKey(paramArrayOfChar);
        ((Mac)localObject6).init((Key)localObject8, (AlgorithmParameterSpec)localObject7);
        ((Mac)localObject6).update(arrayOfByte);
        localObject9 = ((Mac)localObject6).doFinal();
        if (debug != null) {
          debug.println("Checking keystore integrity (MAC algorithm: " + ((Mac)localObject6).getAlgorithm() + ")");
        }
        if (!MessageDigest.isEqual(((MacData)localObject4).getDigest(), (byte[])localObject9)) {
          throw new SecurityException("Failed PKCS12 integrity checking");
        }
      }
      catch (Exception localException1)
      {
        throw new IOException("Integrity check failed: " + localException1, localException1);
      }
    }
    Object localObject4 = (PrivateKeyEntry[])this.keyList.toArray(new PrivateKeyEntry[this.keyList.size()]);
    for (int m = 0; m < localObject4.length; m++)
    {
      localObject6 = localObject4[m];
      if (((PrivateKeyEntry)localObject6).keyId != null)
      {
        localObject7 = new ArrayList();
        for (localObject8 = findMatchedCertificate((PrivateKeyEntry)localObject6); localObject8 != null; localObject8 = (X509Certificate)this.certsMap.get(localObject9))
        {
          if (!((ArrayList)localObject7).isEmpty())
          {
            localObject9 = ((ArrayList)localObject7).iterator();
            while (((Iterator)localObject9).hasNext())
            {
              X509Certificate localX509Certificate = (X509Certificate)((Iterator)localObject9).next();
              if (((X509Certificate)localObject8).equals(localX509Certificate))
              {
                if (debug == null) {
                  break label988;
                }
                debug.println("Loop detected in certificate chain. Skip adding repeated cert to chain. Subject: " + ((X509Certificate)localObject8).getSubjectX500Principal().toString());
                break label988;
              }
            }
          }
          ((ArrayList)localObject7).add(localObject8);
          localObject9 = ((X509Certificate)localObject8).getIssuerX500Principal();
          if (((X500Principal)localObject9).equals(((X509Certificate)localObject8).getSubjectX500Principal())) {
            break;
          }
        }
        label988:
        if (((ArrayList)localObject7).size() > 0) {
          ((PrivateKeyEntry)localObject6).chain = ((Certificate[])((ArrayList)localObject7).toArray(new Certificate[((ArrayList)localObject7).size()]));
        }
      }
    }
    if (debug != null)
    {
      if (this.privateKeyCount > 0) {
        debug.println("Loaded " + this.privateKeyCount + " protected private key(s)");
      }
      if (this.secretKeyCount > 0) {
        debug.println("Loaded " + this.secretKeyCount + " protected secret key(s)");
      }
      if (this.certificateCount > 0) {
        debug.println("Loaded " + this.certificateCount + " certificate(s)");
      }
    }
    this.certEntries.clear();
    this.certsMap.clear();
    this.keyList.clear();
  }
  
  private X509Certificate findMatchedCertificate(PrivateKeyEntry paramPrivateKeyEntry)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Iterator localIterator = this.certEntries.iterator();
    while (localIterator.hasNext())
    {
      CertEntry localCertEntry = (CertEntry)localIterator.next();
      if (Arrays.equals(paramPrivateKeyEntry.keyId, localCertEntry.keyId))
      {
        localObject1 = localCertEntry;
        if (paramPrivateKeyEntry.alias.equalsIgnoreCase(localCertEntry.alias)) {
          return localCertEntry.cert;
        }
      }
      else if (paramPrivateKeyEntry.alias.equalsIgnoreCase(localCertEntry.alias))
      {
        localObject2 = localCertEntry;
      }
    }
    if (localObject1 != null) {
      return localObject1.cert;
    }
    if (localObject2 != null) {
      return localObject2.cert;
    }
    return null;
  }
  
  private void loadSafeContents(DerInputStream paramDerInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    DerValue[] arrayOfDerValue1 = paramDerInputStream.getSequence(2);
    int i = arrayOfDerValue1.length;
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = null;
      DerInputStream localDerInputStream1 = arrayOfDerValue1[j].toDerInputStream();
      ObjectIdentifier localObjectIdentifier1 = localDerInputStream1.getOID();
      DerValue localDerValue = localDerInputStream1.getDerValue();
      if (!localDerValue.isContextSpecific((byte)0)) {
        throw new IOException("unsupported PKCS12 bag value type " + localDerValue.tag);
      }
      localDerValue = localDerValue.data.getDerValue();
      Object localObject2;
      if (localObjectIdentifier1.equals(PKCS8ShroudedKeyBag_OID))
      {
        localObject2 = new PrivateKeyEntry(null);
        ((PrivateKeyEntry)localObject2).protectedPrivKey = localDerValue.toByteArray();
        localObject1 = localObject2;
        this.privateKeyCount += 1;
      }
      else
      {
        DerValue[] arrayOfDerValue2;
        if (localObjectIdentifier1.equals(CertBag_OID))
        {
          localObject2 = new DerInputStream(localDerValue.toByteArray());
          arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
          localObject3 = arrayOfDerValue2[0].getOID();
          if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
            throw new IOException("unsupported PKCS12 cert value type " + arrayOfDerValue2[1].tag);
          }
          localObject4 = arrayOfDerValue2[1].data.getDerValue();
          localObject5 = CertificateFactory.getInstance("X509");
          X509Certificate localX509Certificate = (X509Certificate)((CertificateFactory)localObject5).generateCertificate(new ByteArrayInputStream(((DerValue)localObject4).getOctetString()));
          localObject1 = localX509Certificate;
          this.certificateCount += 1;
        }
        else if (localObjectIdentifier1.equals(SecretBag_OID))
        {
          localObject2 = new DerInputStream(localDerValue.toByteArray());
          arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
          localObject3 = arrayOfDerValue2[0].getOID();
          if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
            throw new IOException("unsupported PKCS12 secret value type " + arrayOfDerValue2[1].tag);
          }
          localObject4 = arrayOfDerValue2[1].data.getDerValue();
          localObject5 = new SecretKeyEntry(null);
          ((SecretKeyEntry)localObject5).protectedSecretKey = ((DerValue)localObject4).getOctetString();
          localObject1 = localObject5;
          this.secretKeyCount += 1;
        }
        else if (debug != null)
        {
          debug.println("Unsupported PKCS12 bag type: " + localObjectIdentifier1);
        }
      }
      try
      {
        localObject2 = localDerInputStream1.getSet(3);
      }
      catch (IOException localIOException1)
      {
        localObject2 = null;
      }
      String str = null;
      Object localObject3 = null;
      Object localObject4 = null;
      Object localObject5 = new HashSet();
      Object localObject7;
      Object localObject8;
      if (localObject2 != null) {
        for (int k = 0; k < localObject2.length; k++)
        {
          localObject7 = localObject2[k].toByteArray();
          localObject8 = new DerInputStream((byte[])localObject7);
          DerValue[] arrayOfDerValue3 = ((DerInputStream)localObject8).getSequence(2);
          ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue3[0].getOID();
          DerInputStream localDerInputStream2 = new DerInputStream(arrayOfDerValue3[1].toByteArray());
          DerValue[] arrayOfDerValue4;
          try
          {
            arrayOfDerValue4 = localDerInputStream2.getSet(1);
          }
          catch (IOException localIOException2)
          {
            throw new IOException("Attribute " + localObjectIdentifier2 + " should have a value " + localIOException2.getMessage());
          }
          if (localObjectIdentifier2.equals(PKCS9FriendlyName_OID))
          {
            str = arrayOfDerValue4[0].getBMPString();
          }
          else if (localObjectIdentifier2.equals(PKCS9LocalKeyId_OID))
          {
            localObject3 = arrayOfDerValue4[0].getOctetString();
          }
          else if (localObjectIdentifier2.equals(TrustedKeyUsage_OID))
          {
            localObject4 = new ObjectIdentifier[arrayOfDerValue4.length];
            for (int m = 0; m < arrayOfDerValue4.length; m++) {
              localObject4[m] = arrayOfDerValue4[m].getOID();
            }
          }
          else
          {
            ((Set)localObject5).add(new PKCS12Attribute((byte[])localObject7));
          }
        }
      }
      Object localObject6;
      if ((localObject1 instanceof KeyEntry))
      {
        localObject6 = (KeyEntry)localObject1;
        if (((localObject1 instanceof PrivateKeyEntry)) && (localObject3 == null))
        {
          if (this.privateKeyCount == 1) {
            localObject3 = "01".getBytes("UTF8");
          }
        }
        else
        {
          ((KeyEntry)localObject6).keyId = ((byte[])localObject3);
          localObject7 = new String((byte[])localObject3, "UTF8");
          localObject8 = null;
          if (((String)localObject7).startsWith("Time ")) {
            try
            {
              localObject8 = new Date(Long.parseLong(((String)localObject7).substring(5)));
            }
            catch (Exception localException)
            {
              localObject8 = null;
            }
          }
          if (localObject8 == null) {
            localObject8 = new Date();
          }
          ((KeyEntry)localObject6).date = ((Date)localObject8);
          if ((localObject1 instanceof PrivateKeyEntry)) {
            this.keyList.add((PrivateKeyEntry)localObject6);
          }
          if (((KeyEntry)localObject6).attributes == null) {
            ((KeyEntry)localObject6).attributes = new HashSet();
          }
          ((KeyEntry)localObject6).attributes.addAll((Collection)localObject5);
          if (str == null) {
            str = getUnfriendlyName();
          }
          ((KeyEntry)localObject6).alias = str;
          this.entries.put(str.toLowerCase(Locale.ENGLISH), localObject6);
        }
      }
      else if ((localObject1 instanceof X509Certificate))
      {
        localObject6 = (X509Certificate)localObject1;
        if ((localObject3 == null) && (this.privateKeyCount == 1) && (j == 0)) {
          localObject3 = "01".getBytes("UTF8");
        }
        if (localObject4 != null)
        {
          if (str == null) {
            str = getUnfriendlyName();
          }
          localObject7 = new CertEntry((X509Certificate)localObject6, (byte[])localObject3, str, (ObjectIdentifier[])localObject4, (Set)localObject5);
          this.entries.put(str.toLowerCase(Locale.ENGLISH), localObject7);
        }
        else
        {
          this.certEntries.add(new CertEntry((X509Certificate)localObject6, (byte[])localObject3, str));
        }
        localObject7 = ((X509Certificate)localObject6).getSubjectX500Principal();
        if ((localObject7 != null) && (!this.certsMap.containsKey(localObject7))) {
          this.certsMap.put(localObject7, localObject6);
        }
      }
    }
  }
  
  private String getUnfriendlyName()
  {
    this.counter += 1;
    return String.valueOf(this.counter);
  }
  
  static
  {
    try
    {
      PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
      CertBag_OID = new ObjectIdentifier(certBag);
      SecretBag_OID = new ObjectIdentifier(secretBag);
      PKCS9FriendlyName_OID = new ObjectIdentifier(pkcs9Name);
      PKCS9LocalKeyId_OID = new ObjectIdentifier(pkcs9KeyId);
      PKCS9CertType_OID = new ObjectIdentifier(pkcs9certType);
      pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(pbeWithSHAAnd40BitRC2CBC);
      pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
      pbes2_OID = new ObjectIdentifier(pbes2);
      TrustedKeyUsage_OID = new ObjectIdentifier(TrustedKeyUsage);
      AnyUsage = new ObjectIdentifier[] { new ObjectIdentifier(AnyExtendedKeyUsage) };
    }
    catch (IOException localIOException) {}
  }
  
  private static class CertEntry
    extends PKCS12KeyStore.Entry
  {
    final X509Certificate cert;
    ObjectIdentifier[] trustedKeyUsage;
    
    CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString)
    {
      this(paramX509Certificate, paramArrayOfByte, paramString, null, null);
    }
    
    CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<? extends KeyStore.Entry.Attribute> paramSet)
    {
      super();
      this.date = new Date();
      this.cert = paramX509Certificate;
      this.keyId = paramArrayOfByte;
      this.alias = paramString;
      this.trustedKeyUsage = paramArrayOfObjectIdentifier;
      this.attributes = new HashSet();
      if (paramSet != null) {
        this.attributes.addAll(paramSet);
      }
    }
  }
  
  private static class Entry
  {
    Date date;
    String alias;
    byte[] keyId;
    Set<KeyStore.Entry.Attribute> attributes;
    
    private Entry() {}
  }
  
  private static class KeyEntry
    extends PKCS12KeyStore.Entry
  {
    private KeyEntry()
    {
      super();
    }
  }
  
  private static class PrivateKeyEntry
    extends PKCS12KeyStore.KeyEntry
  {
    byte[] protectedPrivKey;
    Certificate[] chain;
    
    private PrivateKeyEntry()
    {
      super();
    }
  }
  
  private static class SecretKeyEntry
    extends PKCS12KeyStore.KeyEntry
  {
    byte[] protectedSecretKey;
    
    private SecretKeyEntry()
    {
      super();
    }
  }
}
