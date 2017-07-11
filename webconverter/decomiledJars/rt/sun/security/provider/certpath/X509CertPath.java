package sun.security.provider.certpath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class X509CertPath
  extends CertPath
{
  private static final long serialVersionUID = 4989800333263052980L;
  private List<X509Certificate> certs;
  private static final String COUNT_ENCODING = "count";
  private static final String PKCS7_ENCODING = "PKCS7";
  private static final String PKIPATH_ENCODING = "PkiPath";
  private static final Collection<String> encodingList;
  
  public X509CertPath(List<? extends Certificate> paramList)
    throws CertificateException
  {
    super("X.509");
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (!(localObject instanceof X509Certificate)) {
        throw new CertificateException("List is not all X509Certificates: " + localObject.getClass().getName());
      }
    }
    this.certs = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public X509CertPath(InputStream paramInputStream)
    throws CertificateException
  {
    this(paramInputStream, "PkiPath");
  }
  
  public X509CertPath(InputStream paramInputStream, String paramString)
    throws CertificateException
  {
    super("X.509");
    switch (paramString)
    {
    case "PkiPath": 
      this.certs = parsePKIPATH(paramInputStream);
      break;
    case "PKCS7": 
      this.certs = parsePKCS7(paramInputStream);
      break;
    default: 
      throw new CertificateException("unsupported encoding");
    }
  }
  
  private static List<X509Certificate> parsePKIPATH(InputStream paramInputStream)
    throws CertificateException
  {
    ArrayList localArrayList = null;
    CertificateFactory localCertificateFactory = null;
    if (paramInputStream == null) {
      throw new CertificateException("input stream is null");
    }
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(readAllBytes(paramInputStream));
      DerValue[] arrayOfDerValue = localDerInputStream.getSequence(3);
      if (arrayOfDerValue.length == 0) {
        return Collections.emptyList();
      }
      localCertificateFactory = CertificateFactory.getInstance("X.509");
      localArrayList = new ArrayList(arrayOfDerValue.length);
      for (int i = arrayOfDerValue.length - 1; i >= 0; i--) {
        localArrayList.add((X509Certificate)localCertificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfDerValue[i].toByteArray())));
      }
      return Collections.unmodifiableList(localArrayList);
    }
    catch (IOException localIOException)
    {
      throw new CertificateException("IOException parsing PkiPath data: " + localIOException, localIOException);
    }
  }
  
  private static List<X509Certificate> parsePKCS7(InputStream paramInputStream)
    throws CertificateException
  {
    if (paramInputStream == null) {
      throw new CertificateException("input stream is null");
    }
    Object localObject;
    try
    {
      if (!paramInputStream.markSupported()) {
        paramInputStream = new ByteArrayInputStream(readAllBytes(paramInputStream));
      }
      PKCS7 localPKCS7 = new PKCS7(paramInputStream);
      X509Certificate[] arrayOfX509Certificate = localPKCS7.getCertificates();
      if (arrayOfX509Certificate != null) {
        localObject = Arrays.asList(arrayOfX509Certificate);
      } else {
        localObject = new ArrayList(0);
      }
    }
    catch (IOException localIOException)
    {
      throw new CertificateException("IOException parsing PKCS7 data: " + localIOException);
    }
    return Collections.unmodifiableList((List)localObject);
  }
  
  private static byte[] readAllBytes(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[' '];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) != -1) {
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public byte[] getEncoded()
    throws CertificateEncodingException
  {
    return encodePKIPATH();
  }
  
  private byte[] encodePKIPATH()
    throws CertificateEncodingException
  {
    ListIterator localListIterator = this.certs.listIterator(this.certs.size());
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      while (localListIterator.hasPrevious())
      {
        localObject = (X509Certificate)localListIterator.previous();
        if (this.certs.lastIndexOf(localObject) != this.certs.indexOf(localObject)) {
          throw new CertificateEncodingException("Duplicate Certificate");
        }
        byte[] arrayOfByte = ((X509Certificate)localObject).getEncoded();
        localDerOutputStream.write(arrayOfByte);
      }
      Object localObject = new DerOutputStream();
      ((DerOutputStream)localObject).write((byte)48, localDerOutputStream);
      return ((DerOutputStream)localObject).toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new CertificateEncodingException("IOException encoding PkiPath data: " + localIOException, localIOException);
    }
  }
  
  private byte[] encodePKCS7()
    throws CertificateEncodingException
  {
    PKCS7 localPKCS7 = new PKCS7(new AlgorithmId[0], new ContentInfo(ContentInfo.DATA_OID, null), (X509Certificate[])this.certs.toArray(new X509Certificate[this.certs.size()]), new SignerInfo[0]);
    DerOutputStream localDerOutputStream = new DerOutputStream();
    try
    {
      localPKCS7.encodeSignedData(localDerOutputStream);
    }
    catch (IOException localIOException)
    {
      throw new CertificateEncodingException(localIOException.getMessage());
    }
    return localDerOutputStream.toByteArray();
  }
  
  public byte[] getEncoded(String paramString)
    throws CertificateEncodingException
  {
    switch (paramString)
    {
    case "PkiPath": 
      return encodePKIPATH();
    case "PKCS7": 
      return encodePKCS7();
    }
    throw new CertificateEncodingException("unsupported encoding");
  }
  
  public static Iterator<String> getEncodingsStatic()
  {
    return encodingList.iterator();
  }
  
  public Iterator<String> getEncodings()
  {
    return getEncodingsStatic();
  }
  
  public List<X509Certificate> getCertificates()
  {
    return this.certs;
  }
  
  static
  {
    ArrayList localArrayList = new ArrayList(2);
    localArrayList.add("PkiPath");
    localArrayList.add("PKCS7");
    encodingList = Collections.unmodifiableCollection(localArrayList);
  }
}
