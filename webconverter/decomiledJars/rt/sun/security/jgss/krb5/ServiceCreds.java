package sun.security.jgss.krb5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public final class ServiceCreds
{
  private KerberosPrincipal kp;
  private Set<KerberosPrincipal> allPrincs;
  private List<javax.security.auth.kerberos.KeyTab> ktabs;
  private List<KerberosKey> kk;
  private KerberosTicket tgt;
  private boolean destroyed;
  
  private ServiceCreds() {}
  
  public static ServiceCreds getInstance(Subject paramSubject, String paramString)
  {
    ServiceCreds localServiceCreds = new ServiceCreds();
    localServiceCreds.allPrincs = paramSubject.getPrincipals(KerberosPrincipal.class);
    Iterator localIterator = SubjectComber.findMany(paramSubject, paramString, null, KerberosKey.class).iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (KerberosKey)localIterator.next();
      localServiceCreds.allPrincs.add(((KerberosKey)localObject).getPrincipal());
    }
    if (paramString != null)
    {
      localServiceCreds.kp = new KerberosPrincipal(paramString);
    }
    else if (localServiceCreds.allPrincs.size() == 1)
    {
      int i = 0;
      localObject = SubjectComber.findMany(paramSubject, null, null, javax.security.auth.kerberos.KeyTab.class).iterator();
      while (((Iterator)localObject).hasNext())
      {
        javax.security.auth.kerberos.KeyTab localKeyTab = (javax.security.auth.kerberos.KeyTab)((Iterator)localObject).next();
        if (!localKeyTab.isBound())
        {
          i = 1;
          break;
        }
      }
      if (i == 0)
      {
        localServiceCreds.kp = ((KerberosPrincipal)localServiceCreds.allPrincs.iterator().next());
        paramString = localServiceCreds.kp.getName();
      }
    }
    localServiceCreds.ktabs = SubjectComber.findMany(paramSubject, paramString, null, javax.security.auth.kerberos.KeyTab.class);
    localServiceCreds.kk = SubjectComber.findMany(paramSubject, paramString, null, KerberosKey.class);
    localServiceCreds.tgt = ((KerberosTicket)SubjectComber.find(paramSubject, null, paramString, KerberosTicket.class));
    if ((localServiceCreds.ktabs.isEmpty()) && (localServiceCreds.kk.isEmpty()) && (localServiceCreds.tgt == null)) {
      return null;
    }
    localServiceCreds.destroyed = false;
    return localServiceCreds;
  }
  
  public String getName()
  {
    if (this.destroyed) {
      throw new IllegalStateException("This object is destroyed");
    }
    return this.kp == null ? null : this.kp.getName();
  }
  
  public KerberosKey[] getKKeys()
  {
    if (this.destroyed) {
      throw new IllegalStateException("This object is destroyed");
    }
    KerberosPrincipal localKerberosPrincipal = this.kp;
    if ((localKerberosPrincipal == null) && (!this.allPrincs.isEmpty())) {
      localKerberosPrincipal = (KerberosPrincipal)this.allPrincs.iterator().next();
    }
    if (localKerberosPrincipal == null)
    {
      Iterator localIterator = this.ktabs.iterator();
      while (localIterator.hasNext())
      {
        javax.security.auth.kerberos.KeyTab localKeyTab = (javax.security.auth.kerberos.KeyTab)localIterator.next();
        PrincipalName localPrincipalName = Krb5Util.snapshotFromJavaxKeyTab(localKeyTab).getOneName();
        if (localPrincipalName != null)
        {
          localKerberosPrincipal = new KerberosPrincipal(localPrincipalName.getName());
          break;
        }
      }
    }
    if (localKerberosPrincipal != null) {
      return getKKeys(localKerberosPrincipal);
    }
    return new KerberosKey[0];
  }
  
  public KerberosKey[] getKKeys(KerberosPrincipal paramKerberosPrincipal)
  {
    if (this.destroyed) {
      throw new IllegalStateException("This object is destroyed");
    }
    ArrayList localArrayList = new ArrayList();
    if ((this.kp != null) && (!paramKerberosPrincipal.equals(this.kp))) {
      return new KerberosKey[0];
    }
    Iterator localIterator = this.kk.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (KerberosKey)localIterator.next();
      if (((KerberosKey)localObject).getPrincipal().equals(paramKerberosPrincipal)) {
        localArrayList.add(localObject);
      }
    }
    localIterator = this.ktabs.iterator();
    while (localIterator.hasNext())
    {
      localObject = (javax.security.auth.kerberos.KeyTab)localIterator.next();
      if ((((javax.security.auth.kerberos.KeyTab)localObject).getPrincipal() != null) || (!((javax.security.auth.kerberos.KeyTab)localObject).isBound()) || (this.allPrincs.contains(paramKerberosPrincipal))) {
        for (KerberosKey localKerberosKey : ((javax.security.auth.kerberos.KeyTab)localObject).getKeys(paramKerberosPrincipal)) {
          localArrayList.add(localKerberosKey);
        }
      }
    }
    return (KerberosKey[])localArrayList.toArray(new KerberosKey[localArrayList.size()]);
  }
  
  public EncryptionKey[] getEKeys(PrincipalName paramPrincipalName)
  {
    if (this.destroyed) {
      throw new IllegalStateException("This object is destroyed");
    }
    KerberosKey[] arrayOfKerberosKey = getKKeys(new KerberosPrincipal(paramPrincipalName.getName()));
    if (arrayOfKerberosKey.length == 0) {
      arrayOfKerberosKey = getKKeys();
    }
    EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfKerberosKey.length];
    for (int i = 0; i < arrayOfEncryptionKey.length; i++) {
      arrayOfEncryptionKey[i] = new EncryptionKey(arrayOfKerberosKey[i].getEncoded(), arrayOfKerberosKey[i].getKeyType(), new Integer(arrayOfKerberosKey[i].getVersionNumber()));
    }
    return arrayOfEncryptionKey;
  }
  
  public Credentials getInitCred()
  {
    if (this.destroyed) {
      throw new IllegalStateException("This object is destroyed");
    }
    if (this.tgt == null) {
      return null;
    }
    try
    {
      return Krb5Util.ticketToCreds(this.tgt);
    }
    catch (KrbException|IOException localKrbException) {}
    return null;
  }
  
  public void destroy()
  {
    this.destroyed = true;
    this.kp = null;
    this.ktabs.clear();
    this.kk.clear();
    this.tgt = null;
  }
}
