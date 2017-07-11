package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public abstract class KeyStoreSpi
{
  public KeyStoreSpi() {}
  
  public abstract Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException;
  
  public abstract Certificate[] engineGetCertificateChain(String paramString);
  
  public abstract Certificate engineGetCertificate(String paramString);
  
  public abstract Date engineGetCreationDate(String paramString);
  
  public abstract void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException;
  
  public abstract void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException;
  
  public abstract void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException;
  
  public abstract void engineDeleteEntry(String paramString)
    throws KeyStoreException;
  
  public abstract Enumeration<String> engineAliases();
  
  public abstract boolean engineContainsAlias(String paramString);
  
  public abstract int engineSize();
  
  public abstract boolean engineIsKeyEntry(String paramString);
  
  public abstract boolean engineIsCertificateEntry(String paramString);
  
  public abstract String engineGetCertificateAlias(Certificate paramCertificate);
  
  public abstract void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException;
  
  public void engineStore(KeyStore.LoadStoreParameter paramLoadStoreParameter)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException;
  
  public void engineLoad(KeyStore.LoadStoreParameter paramLoadStoreParameter)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if (paramLoadStoreParameter == null)
    {
      engineLoad((InputStream)null, (char[])null);
      return;
    }
    if ((paramLoadStoreParameter instanceof KeyStore.SimpleLoadStoreParameter))
    {
      KeyStore.ProtectionParameter localProtectionParameter = paramLoadStoreParameter.getProtectionParameter();
      char[] arrayOfChar;
      if ((localProtectionParameter instanceof KeyStore.PasswordProtection))
      {
        arrayOfChar = ((KeyStore.PasswordProtection)localProtectionParameter).getPassword();
      }
      else if ((localProtectionParameter instanceof KeyStore.CallbackHandlerProtection))
      {
        CallbackHandler localCallbackHandler = ((KeyStore.CallbackHandlerProtection)localProtectionParameter).getCallbackHandler();
        PasswordCallback localPasswordCallback = new PasswordCallback("Password: ", false);
        try
        {
          localCallbackHandler.handle(new Callback[] { localPasswordCallback });
        }
        catch (UnsupportedCallbackException localUnsupportedCallbackException)
        {
          throw new NoSuchAlgorithmException("Could not obtain password", localUnsupportedCallbackException);
        }
        arrayOfChar = localPasswordCallback.getPassword();
        localPasswordCallback.clearPassword();
        if (arrayOfChar == null) {
          throw new NoSuchAlgorithmException("No password provided");
        }
      }
      else
      {
        throw new NoSuchAlgorithmException("ProtectionParameter must be PasswordProtection or CallbackHandlerProtection");
      }
      engineLoad(null, arrayOfChar);
      return;
    }
    throw new UnsupportedOperationException();
  }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
  {
    if (!engineContainsAlias(paramString)) {
      return null;
    }
    if (paramProtectionParameter == null)
    {
      if (engineIsCertificateEntry(paramString)) {
        return new KeyStore.TrustedCertificateEntry(engineGetCertificate(paramString));
      }
      throw new UnrecoverableKeyException("requested entry requires a password");
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
          return new KeyStore.PrivateKeyEntry((PrivateKey)localKey, arrayOfCertificate);
        }
        if ((localKey instanceof SecretKey)) {
          return new KeyStore.SecretKeyEntry((SecretKey)localKey);
        }
      }
    }
    throw new UnsupportedOperationException();
  }
  
  public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    if ((paramProtectionParameter != null) && (!(paramProtectionParameter instanceof KeyStore.PasswordProtection))) {
      throw new KeyStoreException("unsupported protection parameter");
    }
    KeyStore.PasswordProtection localPasswordProtection = null;
    if (paramProtectionParameter != null) {
      localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
    }
    if ((paramEntry instanceof KeyStore.TrustedCertificateEntry))
    {
      if ((paramProtectionParameter != null) && (localPasswordProtection.getPassword() != null)) {
        throw new KeyStoreException("trusted certificate entries are not password-protected");
      }
      KeyStore.TrustedCertificateEntry localTrustedCertificateEntry = (KeyStore.TrustedCertificateEntry)paramEntry;
      engineSetCertificateEntry(paramString, localTrustedCertificateEntry.getTrustedCertificate());
      return;
    }
    if ((paramEntry instanceof KeyStore.PrivateKeyEntry))
    {
      if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null)) {
        throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
      }
      engineSetKeyEntry(paramString, ((KeyStore.PrivateKeyEntry)paramEntry).getPrivateKey(), localPasswordProtection.getPassword(), ((KeyStore.PrivateKeyEntry)paramEntry).getCertificateChain());
      return;
    }
    if ((paramEntry instanceof KeyStore.SecretKeyEntry))
    {
      if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null)) {
        throw new KeyStoreException("non-null password required to create SecretKeyEntry");
      }
      engineSetKeyEntry(paramString, ((KeyStore.SecretKeyEntry)paramEntry).getSecretKey(), localPasswordProtection.getPassword(), (Certificate[])null);
      return;
    }
    throw new KeyStoreException("unsupported entry type: " + paramEntry.getClass().getName());
  }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass)
  {
    if (paramClass == KeyStore.TrustedCertificateEntry.class) {
      return engineIsCertificateEntry(paramString);
    }
    if (paramClass == KeyStore.PrivateKeyEntry.class) {
      return (engineIsKeyEntry(paramString)) && (engineGetCertificate(paramString) != null);
    }
    if (paramClass == KeyStore.SecretKeyEntry.class) {
      return (engineIsKeyEntry(paramString)) && (engineGetCertificate(paramString) == null);
    }
    return false;
  }
}
