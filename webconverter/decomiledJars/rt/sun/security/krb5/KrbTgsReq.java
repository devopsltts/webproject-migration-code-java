package sun.security.krb5;

import java.io.IOException;
import java.net.UnknownHostException;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.crypto.Nonce;

public class KrbTgsReq
{
  private PrincipalName princName;
  private PrincipalName servName;
  private TGSReq tgsReqMessg;
  private KerberosTime ctime;
  private Ticket secondTicket = null;
  private boolean useSubkey = false;
  EncryptionKey tgsReqKey;
  private static final boolean DEBUG = Krb5.DEBUG;
  private byte[] obuf;
  private byte[] ibuf;
  
  public KrbTgsReq(Credentials paramCredentials, PrincipalName paramPrincipalName)
    throws KrbException, IOException
  {
    this(new KDCOptions(), paramCredentials, paramPrincipalName, null, null, null, null, null, null, null, null);
  }
  
  public KrbTgsReq(Credentials paramCredentials, Ticket paramTicket, PrincipalName paramPrincipalName)
    throws KrbException, IOException
  {
    this(KDCOptions.with(new int[] { 14, 1 }), paramCredentials, paramPrincipalName, null, null, null, null, null, null, new Ticket[] { paramTicket }, null);
  }
  
  public KrbTgsReq(Credentials paramCredentials, PrincipalName paramPrincipalName, PAData paramPAData)
    throws KrbException, IOException
  {
    this(KDCOptions.with(new int[] { 1 }), paramCredentials, paramCredentials.getClient(), paramPrincipalName, null, null, null, null, null, null, null, null, paramPAData);
  }
  
  KrbTgsReq(KDCOptions paramKDCOptions, Credentials paramCredentials, PrincipalName paramPrincipalName, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int[] paramArrayOfInt, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket[] paramArrayOfTicket, EncryptionKey paramEncryptionKey)
    throws KrbException, IOException
  {
    this(paramKDCOptions, paramCredentials, paramCredentials.getClient(), paramPrincipalName, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramArrayOfInt, paramHostAddresses, paramAuthorizationData, paramArrayOfTicket, paramEncryptionKey, null);
  }
  
  private KrbTgsReq(KDCOptions paramKDCOptions, Credentials paramCredentials, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int[] paramArrayOfInt, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket[] paramArrayOfTicket, EncryptionKey paramEncryptionKey, PAData paramPAData)
    throws KrbException, IOException
  {
    this.princName = paramPrincipalName1;
    this.servName = paramPrincipalName2;
    this.ctime = KerberosTime.now();
    if ((paramKDCOptions.get(1)) && (!paramCredentials.flags.get(1))) {
      throw new KrbException(101);
    }
    if ((paramKDCOptions.get(2)) && (!paramCredentials.flags.get(1))) {
      throw new KrbException(101);
    }
    if ((paramKDCOptions.get(3)) && (!paramCredentials.flags.get(3))) {
      throw new KrbException(101);
    }
    if ((paramKDCOptions.get(4)) && (!paramCredentials.flags.get(3))) {
      throw new KrbException(101);
    }
    if ((paramKDCOptions.get(5)) && (!paramCredentials.flags.get(5))) {
      throw new KrbException(101);
    }
    if ((paramKDCOptions.get(8)) && (!paramCredentials.flags.get(8))) {
      throw new KrbException(101);
    }
    if (paramKDCOptions.get(6))
    {
      if (!paramCredentials.flags.get(6)) {
        throw new KrbException(101);
      }
    }
    else if (paramKerberosTime1 != null) {
      paramKerberosTime1 = null;
    }
    if (paramKDCOptions.get(8))
    {
      if (!paramCredentials.flags.get(8)) {
        throw new KrbException(101);
      }
    }
    else if (paramKerberosTime3 != null) {
      paramKerberosTime3 = null;
    }
    if ((paramKDCOptions.get(28)) || (paramKDCOptions.get(14)))
    {
      if (paramArrayOfTicket == null) {
        throw new KrbException(101);
      }
      this.secondTicket = paramArrayOfTicket[0];
    }
    else if (paramArrayOfTicket != null)
    {
      paramArrayOfTicket = null;
    }
    this.tgsReqMessg = createRequest(paramKDCOptions, paramCredentials.ticket, paramCredentials.key, this.ctime, this.princName, this.servName, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramArrayOfInt, paramHostAddresses, paramAuthorizationData, paramArrayOfTicket, paramEncryptionKey, paramPAData);
    this.obuf = this.tgsReqMessg.asn1Encode();
    if (paramCredentials.flags.get(2)) {
      paramKDCOptions.set(2, true);
    }
  }
  
  public void send()
    throws IOException, KrbException
  {
    String str = null;
    if (this.servName != null) {
      str = this.servName.getRealmString();
    }
    KdcComm localKdcComm = new KdcComm(str);
    this.ibuf = localKdcComm.send(this.obuf);
  }
  
  public KrbTgsRep getReply()
    throws KrbException, IOException
  {
    return new KrbTgsRep(this.ibuf, this);
  }
  
  public Credentials sendAndGetCreds()
    throws IOException, KrbException
  {
    KrbTgsRep localKrbTgsRep = null;
    Object localObject = null;
    send();
    localKrbTgsRep = getReply();
    return localKrbTgsRep.getCreds();
  }
  
  KerberosTime getCtime()
  {
    return this.ctime;
  }
  
  private TGSReq createRequest(KDCOptions paramKDCOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, KerberosTime paramKerberosTime1, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, int[] paramArrayOfInt, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket[] paramArrayOfTicket, EncryptionKey paramEncryptionKey2, PAData paramPAData)
    throws IOException, KrbException, UnknownHostException
  {
    KerberosTime localKerberosTime = null;
    if (paramKerberosTime3 == null) {
      localKerberosTime = new KerberosTime(0L);
    } else {
      localKerberosTime = paramKerberosTime3;
    }
    this.tgsReqKey = paramEncryptionKey1;
    int[] arrayOfInt = null;
    if (paramArrayOfInt == null) {
      arrayOfInt = EType.getDefaults("default_tgs_enctypes");
    } else {
      arrayOfInt = paramArrayOfInt;
    }
    EncryptionKey localEncryptionKey = null;
    EncryptedData localEncryptedData = null;
    if (paramAuthorizationData != null)
    {
      localObject = paramAuthorizationData.asn1Encode();
      if (paramEncryptionKey2 != null)
      {
        localEncryptionKey = paramEncryptionKey2;
        this.tgsReqKey = paramEncryptionKey2;
        this.useSubkey = true;
        localEncryptedData = new EncryptedData(localEncryptionKey, (byte[])localObject, 5);
      }
      else
      {
        localEncryptedData = new EncryptedData(paramEncryptionKey1, (byte[])localObject, 4);
      }
    }
    Object localObject = new KDCReqBody(paramKDCOptions, paramPrincipalName1, paramPrincipalName2, paramKerberosTime2, localKerberosTime, paramKerberosTime4, Nonce.value(), arrayOfInt, paramHostAddresses, localEncryptedData, paramArrayOfTicket);
    byte[] arrayOfByte1 = ((KDCReqBody)localObject).asn1Encode(12);
    Checksum localChecksum;
    switch (Checksum.CKSUMTYPE_DEFAULT)
    {
    case -138: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 8: 
    case 12: 
    case 15: 
    case 16: 
      localChecksum = new Checksum(Checksum.CKSUMTYPE_DEFAULT, arrayOfByte1, paramEncryptionKey1, 6);
      break;
    case 1: 
    case 2: 
    case 7: 
    default: 
      localChecksum = new Checksum(Checksum.CKSUMTYPE_DEFAULT, arrayOfByte1);
    }
    byte[] arrayOfByte2 = new KrbApReq(new APOptions(), paramTicket, paramEncryptionKey1, paramPrincipalName1, localChecksum, paramKerberosTime1, localEncryptionKey, null, null).getMessage();
    PAData localPAData = new PAData(1, arrayOfByte2);
    return new TGSReq(new PAData[] { paramPAData != null ? new PAData[] { paramPAData, localPAData } : localPAData }, (KDCReqBody)localObject);
  }
  
  TGSReq getMessage()
  {
    return this.tgsReqMessg;
  }
  
  Ticket getSecondTicket()
  {
    return this.secondTicket;
  }
  
  private static void debug(String paramString) {}
  
  boolean usedSubkey()
  {
    return this.useSubkey;
  }
}
