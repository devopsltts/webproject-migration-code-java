package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import javax.security.auth.kerberos.KeyTab;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.internal.ASRep;
import sun.security.krb5.internal.ASReq;
import sun.security.krb5.internal.EncASRepPart;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.crypto.EType;
import sun.security.util.DerValue;

class KrbAsRep
  extends KrbKdcRep
{
  private ASRep rep;
  private Credentials creds;
  private boolean DEBUG = Krb5.DEBUG;
  
  KrbAsRep(byte[] paramArrayOfByte)
    throws KrbException, Asn1Exception, IOException
  {
    DerValue localDerValue = new DerValue(paramArrayOfByte);
    try
    {
      this.rep = new ASRep(localDerValue);
    }
    catch (Asn1Exception localAsn1Exception)
    {
      this.rep = null;
      KRBError localKRBError = new KRBError(localDerValue);
      String str1 = localKRBError.getErrorString();
      String str2 = null;
      if ((str1 != null) && (str1.length() > 0)) {
        if (str1.charAt(str1.length() - 1) == 0) {
          str2 = str1.substring(0, str1.length() - 1);
        } else {
          str2 = str1;
        }
      }
      KrbException localKrbException;
      if (str2 == null)
      {
        localKrbException = new KrbException(localKRBError);
      }
      else
      {
        if (this.DEBUG) {
          System.out.println("KRBError received: " + str2);
        }
        localKrbException = new KrbException(localKRBError, str2);
      }
      localKrbException.initCause(localAsn1Exception);
      throw localKrbException;
    }
  }
  
  PAData[] getPA()
  {
    return this.rep.pAData;
  }
  
  void decryptUsingKeyTab(KeyTab paramKeyTab, KrbAsReq paramKrbAsReq, PrincipalName paramPrincipalName)
    throws KrbException, Asn1Exception, IOException
  {
    EncryptionKey localEncryptionKey = null;
    int i = this.rep.encPart.getEType();
    Integer localInteger = this.rep.encPart.kvno;
    try
    {
      localEncryptionKey = EncryptionKey.findKey(i, localInteger, Krb5Util.keysFromJavaxKeyTab(paramKeyTab, paramPrincipalName));
    }
    catch (KrbException localKrbException)
    {
      if (localKrbException.returnCode() == 44) {
        localEncryptionKey = EncryptionKey.findKey(i, Krb5Util.keysFromJavaxKeyTab(paramKeyTab, paramPrincipalName));
      }
    }
    if (localEncryptionKey == null) {
      throw new KrbException(400, "Cannot find key for type/kvno to decrypt AS REP - " + EType.toString(i) + "/" + localInteger);
    }
    decrypt(localEncryptionKey, paramKrbAsReq);
  }
  
  void decryptUsingPassword(char[] paramArrayOfChar, KrbAsReq paramKrbAsReq, PrincipalName paramPrincipalName)
    throws KrbException, Asn1Exception, IOException
  {
    int i = this.rep.encPart.getEType();
    EncryptionKey localEncryptionKey = EncryptionKey.acquireSecretKey(paramPrincipalName, paramArrayOfChar, i, PAData.getSaltAndParams(i, this.rep.pAData));
    decrypt(localEncryptionKey, paramKrbAsReq);
  }
  
  private void decrypt(EncryptionKey paramEncryptionKey, KrbAsReq paramKrbAsReq)
    throws KrbException, Asn1Exception, IOException
  {
    byte[] arrayOfByte1 = this.rep.encPart.decrypt(paramEncryptionKey, 3);
    byte[] arrayOfByte2 = this.rep.encPart.reset(arrayOfByte1);
    DerValue localDerValue = new DerValue(arrayOfByte2);
    EncASRepPart localEncASRepPart = new EncASRepPart(localDerValue);
    this.rep.encKDCRepPart = localEncASRepPart;
    ASReq localASReq = paramKrbAsReq.getMessage();
    check(true, localASReq, this.rep);
    this.creds = new Credentials(this.rep.ticket, localASReq.reqBody.cname, this.rep.ticket.sname, localEncASRepPart.key, localEncASRepPart.flags, localEncASRepPart.authtime, localEncASRepPart.starttime, localEncASRepPart.endtime, localEncASRepPart.renewTill, localEncASRepPart.caddr);
    if (this.DEBUG) {
      System.out.println(">>> KrbAsRep cons in KrbAsReq.getReply " + localASReq.reqBody.cname.getNameString());
    }
  }
  
  Credentials getCreds()
  {
    return (Credentials)Objects.requireNonNull(this.creds, "Creds not available yet.");
  }
  
  sun.security.krb5.internal.ccache.Credentials getCCreds()
  {
    return new sun.security.krb5.internal.ccache.Credentials(this.rep);
  }
}
