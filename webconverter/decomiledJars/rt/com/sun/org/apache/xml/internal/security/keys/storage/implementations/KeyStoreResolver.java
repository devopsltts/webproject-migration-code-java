package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class KeyStoreResolver
  extends StorageResolverSpi
{
  private KeyStore keyStore = null;
  
  public KeyStoreResolver(KeyStore paramKeyStore)
    throws StorageResolverException
  {
    this.keyStore = paramKeyStore;
    try
    {
      paramKeyStore.aliases();
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new StorageResolverException("generic.EmptyMessage", localKeyStoreException);
    }
  }
  
  public Iterator<Certificate> getIterator()
  {
    return new KeyStoreIterator(this.keyStore);
  }
  
  static class KeyStoreIterator
    implements Iterator<Certificate>
  {
    KeyStore keyStore = null;
    Enumeration<String> aliases = null;
    Certificate nextCert = null;
    
    public KeyStoreIterator(KeyStore paramKeyStore)
    {
      try
      {
        this.keyStore = paramKeyStore;
        this.aliases = this.keyStore.aliases();
      }
      catch (KeyStoreException localKeyStoreException)
      {
        this.aliases = new Enumeration()
        {
          public boolean hasMoreElements()
          {
            return false;
          }
          
          public String nextElement()
          {
            return null;
          }
        };
      }
    }
    
    public boolean hasNext()
    {
      if (this.nextCert == null) {
        this.nextCert = findNextCert();
      }
      return this.nextCert != null;
    }
    
    public Certificate next()
    {
      if (this.nextCert == null)
      {
        this.nextCert = findNextCert();
        if (this.nextCert == null) {
          throw new NoSuchElementException();
        }
      }
      Certificate localCertificate = this.nextCert;
      this.nextCert = null;
      return localCertificate;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Can't remove keys from KeyStore");
    }
    
    private Certificate findNextCert()
    {
      while (this.aliases.hasMoreElements())
      {
        String str = (String)this.aliases.nextElement();
        try
        {
          Certificate localCertificate = this.keyStore.getCertificate(str);
          if (localCertificate != null) {
            return localCertificate;
          }
        }
        catch (KeyStoreException localKeyStoreException)
        {
          return null;
        }
      }
      return null;
    }
  }
}
