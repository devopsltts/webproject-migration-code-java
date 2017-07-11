package sun.security.x509;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DistributionPoint
{
  public static final int KEY_COMPROMISE = 1;
  public static final int CA_COMPROMISE = 2;
  public static final int AFFILIATION_CHANGED = 3;
  public static final int SUPERSEDED = 4;
  public static final int CESSATION_OF_OPERATION = 5;
  public static final int CERTIFICATE_HOLD = 6;
  public static final int PRIVILEGE_WITHDRAWN = 7;
  public static final int AA_COMPROMISE = 8;
  private static final String[] REASON_STRINGS = { null, "key compromise", "CA compromise", "affiliation changed", "superseded", "cessation of operation", "certificate hold", "privilege withdrawn", "AA compromise" };
  private static final byte TAG_DIST_PT = 0;
  private static final byte TAG_REASONS = 1;
  private static final byte TAG_ISSUER = 2;
  private static final byte TAG_FULL_NAME = 0;
  private static final byte TAG_REL_NAME = 1;
  private GeneralNames fullName;
  private RDN relativeName;
  private boolean[] reasonFlags;
  private GeneralNames crlIssuer;
  private volatile int hashCode;
  
  public DistributionPoint(GeneralNames paramGeneralNames1, boolean[] paramArrayOfBoolean, GeneralNames paramGeneralNames2)
  {
    if ((paramGeneralNames1 == null) && (paramGeneralNames2 == null)) {
      throw new IllegalArgumentException("fullName and crlIssuer may not both be null");
    }
    this.fullName = paramGeneralNames1;
    this.reasonFlags = paramArrayOfBoolean;
    this.crlIssuer = paramGeneralNames2;
  }
  
  public DistributionPoint(RDN paramRDN, boolean[] paramArrayOfBoolean, GeneralNames paramGeneralNames)
  {
    if ((paramRDN == null) && (paramGeneralNames == null)) {
      throw new IllegalArgumentException("relativeName and crlIssuer may not both be null");
    }
    this.relativeName = paramRDN;
    this.reasonFlags = paramArrayOfBoolean;
    this.crlIssuer = paramGeneralNames;
  }
  
  public DistributionPoint(DerValue paramDerValue)
    throws IOException
  {
    if (paramDerValue.tag != 48) {
      throw new IOException("Invalid encoding of DistributionPoint.");
    }
    while ((paramDerValue.data != null) && (paramDerValue.data.available() != 0))
    {
      DerValue localDerValue1 = paramDerValue.data.getDerValue();
      if ((localDerValue1.isContextSpecific((byte)0)) && (localDerValue1.isConstructed()))
      {
        if ((this.fullName != null) || (this.relativeName != null)) {
          throw new IOException("Duplicate DistributionPointName in DistributionPoint.");
        }
        DerValue localDerValue2 = localDerValue1.data.getDerValue();
        if ((localDerValue2.isContextSpecific((byte)0)) && (localDerValue2.isConstructed()))
        {
          localDerValue2.resetTag((byte)48);
          this.fullName = new GeneralNames(localDerValue2);
        }
        else if ((localDerValue2.isContextSpecific((byte)1)) && (localDerValue2.isConstructed()))
        {
          localDerValue2.resetTag((byte)49);
          this.relativeName = new RDN(localDerValue2);
        }
        else
        {
          throw new IOException("Invalid DistributionPointName in DistributionPoint");
        }
      }
      else if ((localDerValue1.isContextSpecific((byte)1)) && (!localDerValue1.isConstructed()))
      {
        if (this.reasonFlags != null) {
          throw new IOException("Duplicate Reasons in DistributionPoint.");
        }
        localDerValue1.resetTag((byte)3);
        this.reasonFlags = localDerValue1.getUnalignedBitString().toBooleanArray();
      }
      else if ((localDerValue1.isContextSpecific((byte)2)) && (localDerValue1.isConstructed()))
      {
        if (this.crlIssuer != null) {
          throw new IOException("Duplicate CRLIssuer in DistributionPoint.");
        }
        localDerValue1.resetTag((byte)48);
        this.crlIssuer = new GeneralNames(localDerValue1);
      }
      else
      {
        throw new IOException("Invalid encoding of DistributionPoint.");
      }
    }
    if ((this.crlIssuer == null) && (this.fullName == null) && (this.relativeName == null)) {
      throw new IOException("One of fullName, relativeName,  and crlIssuer has to be set");
    }
  }
  
  public GeneralNames getFullName()
  {
    return this.fullName;
  }
  
  public RDN getRelativeName()
  {
    return this.relativeName;
  }
  
  public boolean[] getReasonFlags()
  {
    return this.reasonFlags;
  }
  
  public GeneralNames getCRLIssuer()
  {
    return this.crlIssuer;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2;
    Object localObject;
    if ((this.fullName != null) || (this.relativeName != null))
    {
      localDerOutputStream2 = new DerOutputStream();
      if (this.fullName != null)
      {
        localObject = new DerOutputStream();
        this.fullName.encode((DerOutputStream)localObject);
        localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), (DerOutputStream)localObject);
      }
      else if (this.relativeName != null)
      {
        localObject = new DerOutputStream();
        this.relativeName.encode((DerOutputStream)localObject);
        localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), (DerOutputStream)localObject);
      }
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    }
    if (this.reasonFlags != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localObject = new BitArray(this.reasonFlags);
      localDerOutputStream2.putTruncatedUnalignedBitString((BitArray)localObject);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream2);
    }
    if (this.crlIssuer != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      this.crlIssuer.encode(localDerOutputStream2);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream1);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DistributionPoint)) {
      return false;
    }
    DistributionPoint localDistributionPoint = (DistributionPoint)paramObject;
    boolean bool = (Objects.equals(this.fullName, localDistributionPoint.fullName)) && (Objects.equals(this.relativeName, localDistributionPoint.relativeName)) && (Objects.equals(this.crlIssuer, localDistributionPoint.crlIssuer)) && (Arrays.equals(this.reasonFlags, localDistributionPoint.reasonFlags));
    return bool;
  }
  
  public int hashCode()
  {
    int i = this.hashCode;
    if (i == 0)
    {
      i = 1;
      if (this.fullName != null) {
        i += this.fullName.hashCode();
      }
      if (this.relativeName != null) {
        i += this.relativeName.hashCode();
      }
      if (this.crlIssuer != null) {
        i += this.crlIssuer.hashCode();
      }
      if (this.reasonFlags != null) {
        for (int j = 0; j < this.reasonFlags.length; j++) {
          if (this.reasonFlags[j] != 0) {
            i += j;
          }
        }
      }
      this.hashCode = i;
    }
    return i;
  }
  
  private static String reasonToString(int paramInt)
  {
    if ((paramInt > 0) && (paramInt < REASON_STRINGS.length)) {
      return REASON_STRINGS[paramInt];
    }
    return "Unknown reason " + paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.fullName != null) {
      localStringBuilder.append("DistributionPoint:\n     " + this.fullName + "\n");
    }
    if (this.relativeName != null) {
      localStringBuilder.append("DistributionPoint:\n     " + this.relativeName + "\n");
    }
    if (this.reasonFlags != null)
    {
      localStringBuilder.append("   ReasonFlags:\n");
      for (int i = 0; i < this.reasonFlags.length; i++) {
        if (this.reasonFlags[i] != 0) {
          localStringBuilder.append("    " + reasonToString(i) + "\n");
        }
      }
    }
    if (this.crlIssuer != null) {
      localStringBuilder.append("   CRLIssuer:" + this.crlIssuer + "\n");
    }
    return localStringBuilder.toString();
  }
}
