package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import sun.security.util.Debug;

public final class UnresolvedPermission
  extends Permission
  implements Serializable
{
  private static final long serialVersionUID = -4821973115467008846L;
  private static final Debug debug = Debug.getInstance("policy,access", "UnresolvedPermission");
  private String type;
  private String name;
  private String actions;
  private transient Certificate[] certs;
  private static final Class[] PARAMS0 = new Class[0];
  private static final Class[] PARAMS1 = { String.class };
  private static final Class[] PARAMS2 = { String.class, String.class };
  
  public UnresolvedPermission(String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate)
  {
    super(paramString1);
    if (paramString1 == null) {
      throw new NullPointerException("type can't be null");
    }
    this.type = paramString1;
    this.name = paramString2;
    this.actions = paramString3;
    if (paramArrayOfCertificate != null)
    {
      for (int i = 0; i < paramArrayOfCertificate.length; i++) {
        if (!(paramArrayOfCertificate[i] instanceof X509Certificate))
        {
          this.certs = ((Certificate[])paramArrayOfCertificate.clone());
          break;
        }
      }
      if (this.certs == null)
      {
        i = 0;
        int j = 0;
        while (i < paramArrayOfCertificate.length)
        {
          j++;
          while ((i + 1 < paramArrayOfCertificate.length) && (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectDN()))) {
            i++;
          }
          i++;
        }
        if (j == paramArrayOfCertificate.length) {
          this.certs = ((Certificate[])paramArrayOfCertificate.clone());
        }
        if (this.certs == null)
        {
          ArrayList localArrayList = new ArrayList();
          for (i = 0; i < paramArrayOfCertificate.length; i++)
          {
            localArrayList.add(paramArrayOfCertificate[i]);
            while ((i + 1 < paramArrayOfCertificate.length) && (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectDN()))) {
              i++;
            }
          }
          this.certs = new Certificate[localArrayList.size()];
          localArrayList.toArray(this.certs);
        }
      }
    }
  }
  
  Permission resolve(Permission paramPermission, Certificate[] paramArrayOfCertificate)
  {
    if (this.certs != null)
    {
      if (paramArrayOfCertificate == null) {
        return null;
      }
      for (int j = 0; j < this.certs.length; j++)
      {
        int i = 0;
        for (int k = 0; k < paramArrayOfCertificate.length; k++) {
          if (this.certs[j].equals(paramArrayOfCertificate[k]))
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          return null;
        }
      }
    }
    try
    {
      Class localClass = paramPermission.getClass();
      if ((this.name == null) && (this.actions == null)) {
        try
        {
          Constructor localConstructor1 = localClass.getConstructor(PARAMS0);
          return (Permission)localConstructor1.newInstance(new Object[0]);
        }
        catch (NoSuchMethodException localNoSuchMethodException2)
        {
          try
          {
            Constructor localConstructor4 = localClass.getConstructor(PARAMS1);
            return (Permission)localConstructor4.newInstance(new Object[] { this.name });
          }
          catch (NoSuchMethodException localNoSuchMethodException4)
          {
            Constructor localConstructor6 = localClass.getConstructor(PARAMS2);
            return (Permission)localConstructor6.newInstance(new Object[] { this.name, this.actions });
          }
        }
      }
      if ((this.name != null) && (this.actions == null)) {
        try
        {
          Constructor localConstructor2 = localClass.getConstructor(PARAMS1);
          return (Permission)localConstructor2.newInstance(new Object[] { this.name });
        }
        catch (NoSuchMethodException localNoSuchMethodException3)
        {
          Constructor localConstructor5 = localClass.getConstructor(PARAMS2);
          return (Permission)localConstructor5.newInstance(new Object[] { this.name, this.actions });
        }
      }
      Constructor localConstructor3 = localClass.getConstructor(PARAMS2);
      return (Permission)localConstructor3.newInstance(new Object[] { this.name, this.actions });
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      if (debug != null)
      {
        debug.println("NoSuchMethodException:\n  could not find proper constructor for " + this.type);
        localNoSuchMethodException1.printStackTrace();
      }
      return null;
    }
    catch (Exception localException)
    {
      if (debug != null)
      {
        debug.println("unable to instantiate " + this.name);
        localException.printStackTrace();
      }
    }
    return null;
  }
  
  public boolean implies(Permission paramPermission)
  {
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof UnresolvedPermission)) {
      return false;
    }
    UnresolvedPermission localUnresolvedPermission = (UnresolvedPermission)paramObject;
    if (!this.type.equals(localUnresolvedPermission.type)) {
      return false;
    }
    if (this.name == null)
    {
      if (localUnresolvedPermission.name != null) {
        return false;
      }
    }
    else if (!this.name.equals(localUnresolvedPermission.name)) {
      return false;
    }
    if (this.actions == null)
    {
      if (localUnresolvedPermission.actions != null) {
        return false;
      }
    }
    else if (!this.actions.equals(localUnresolvedPermission.actions)) {
      return false;
    }
    if (((this.certs == null) && (localUnresolvedPermission.certs != null)) || ((this.certs != null) && (localUnresolvedPermission.certs == null)) || ((this.certs != null) && (localUnresolvedPermission.certs != null) && (this.certs.length != localUnresolvedPermission.certs.length))) {
      return false;
    }
    int k;
    int j;
    for (int i = 0; (this.certs != null) && (i < this.certs.length); i++)
    {
      k = 0;
      for (j = 0; j < localUnresolvedPermission.certs.length; j++) {
        if (this.certs[i].equals(localUnresolvedPermission.certs[j]))
        {
          k = 1;
          break;
        }
      }
      if (k == 0) {
        return false;
      }
    }
    for (i = 0; (localUnresolvedPermission.certs != null) && (i < localUnresolvedPermission.certs.length); i++)
    {
      k = 0;
      for (j = 0; j < this.certs.length; j++) {
        if (localUnresolvedPermission.certs[i].equals(this.certs[j]))
        {
          k = 1;
          break;
        }
      }
      if (k == 0) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = this.type.hashCode();
    if (this.name != null) {
      i ^= this.name.hashCode();
    }
    if (this.actions != null) {
      i ^= this.actions.hashCode();
    }
    return i;
  }
  
  public String getActions()
  {
    return "";
  }
  
  public String getUnresolvedType()
  {
    return this.type;
  }
  
  public String getUnresolvedName()
  {
    return this.name;
  }
  
  public String getUnresolvedActions()
  {
    return this.actions;
  }
  
  public Certificate[] getUnresolvedCerts()
  {
    return this.certs == null ? null : (Certificate[])this.certs.clone();
  }
  
  public String toString()
  {
    return "(unresolved " + this.type + " " + this.name + " " + this.actions + ")";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new UnresolvedPermissionCollection();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((this.certs == null) || (this.certs.length == 0))
    {
      paramObjectOutputStream.writeInt(0);
    }
    else
    {
      paramObjectOutputStream.writeInt(this.certs.length);
      for (int i = 0; i < this.certs.length; i++)
      {
        Certificate localCertificate = this.certs[i];
        try
        {
          paramObjectOutputStream.writeUTF(localCertificate.getType());
          byte[] arrayOfByte = localCertificate.getEncoded();
          paramObjectOutputStream.writeInt(arrayOfByte.length);
          paramObjectOutputStream.write(arrayOfByte);
        }
        catch (CertificateEncodingException localCertificateEncodingException)
        {
          throw new IOException(localCertificateEncodingException.getMessage());
        }
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    Hashtable localHashtable = null;
    paramObjectInputStream.defaultReadObject();
    if (this.type == null) {
      throw new NullPointerException("type can't be null");
    }
    int i = paramObjectInputStream.readInt();
    if (i > 0)
    {
      localHashtable = new Hashtable(3);
      this.certs = new Certificate[i];
    }
    for (int j = 0; j < i; j++)
    {
      String str = paramObjectInputStream.readUTF();
      CertificateFactory localCertificateFactory;
      if (localHashtable.containsKey(str))
      {
        localCertificateFactory = (CertificateFactory)localHashtable.get(str);
      }
      else
      {
        try
        {
          localCertificateFactory = CertificateFactory.getInstance(str);
        }
        catch (CertificateException localCertificateException1)
        {
          throw new ClassNotFoundException("Certificate factory for " + str + " not found");
        }
        localHashtable.put(str, localCertificateFactory);
      }
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = new byte[paramObjectInputStream.readInt()];
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        throw new IOException("Certificate too big");
      }
      paramObjectInputStream.readFully(arrayOfByte);
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      try
      {
        this.certs[j] = localCertificateFactory.generateCertificate(localByteArrayInputStream);
      }
      catch (CertificateException localCertificateException2)
      {
        throw new IOException(localCertificateException2.getMessage());
      }
      localByteArrayInputStream.close();
    }
  }
}
