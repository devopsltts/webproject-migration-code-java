package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAData
{
  private int pADataType;
  private byte[] pADataValue = null;
  private static final byte TAG_PATYPE = 1;
  private static final byte TAG_PAVALUE = 2;
  
  private PAData() {}
  
  public PAData(int paramInt, byte[] paramArrayOfByte)
  {
    this.pADataType = paramInt;
    if (paramArrayOfByte != null) {
      this.pADataValue = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public Object clone()
  {
    PAData localPAData = new PAData();
    localPAData.pADataType = this.pADataType;
    if (this.pADataValue != null)
    {
      localPAData.pADataValue = new byte[this.pADataValue.length];
      System.arraycopy(this.pADataValue, 0, localPAData.pADataValue, 0, this.pADataValue.length);
    }
    return localPAData;
  }
  
  public PAData(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    DerValue localDerValue = null;
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 1) {
      this.pADataType = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 2) {
      this.pADataValue = localDerValue.getData().getOctetString();
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(this.pADataType);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOctetString(this.pADataValue);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public int getType()
  {
    return this.pADataType;
  }
  
  public byte[] getValue()
  {
    return this.pADataValue == null ? null : (byte[])this.pADataValue.clone();
  }
  
  public static int getPreferredEType(PAData[] paramArrayOfPAData, int paramInt)
    throws IOException, Asn1Exception
  {
    if (paramArrayOfPAData == null) {
      return paramInt;
    }
    DerValue localDerValue1 = null;
    DerValue localDerValue2 = null;
    for (Object localObject3 : paramArrayOfPAData) {
      if (localObject3.getValue() != null) {
        switch (localObject3.getType())
        {
        case 11: 
          localDerValue1 = new DerValue(localObject3.getValue());
          break;
        case 19: 
          localDerValue2 = new DerValue(localObject3.getValue());
        }
      }
    }
    Object localObject2;
    if (localDerValue2 != null) {
      while (localDerValue2.data.available() > 0)
      {
        ??? = localDerValue2.data.getDerValue();
        localObject2 = new ETypeInfo2((DerValue)???);
        if (((ETypeInfo2)localObject2).getParams() == null) {
          return ((ETypeInfo2)localObject2).getEType();
        }
      }
    }
    if ((localDerValue1 != null) && (localDerValue1.data.available() > 0))
    {
      ??? = localDerValue1.data.getDerValue();
      localObject2 = new ETypeInfo((DerValue)???);
      return ((ETypeInfo)localObject2).getEType();
    }
    return paramInt;
  }
  
  public static SaltAndParams getSaltAndParams(int paramInt, PAData[] paramArrayOfPAData)
    throws Asn1Exception, IOException
  {
    if (paramArrayOfPAData == null) {
      return null;
    }
    DerValue localDerValue1 = null;
    DerValue localDerValue2 = null;
    String str = null;
    for (Object localObject3 : paramArrayOfPAData) {
      if (localObject3.getValue() != null) {
        switch (localObject3.getType())
        {
        case 3: 
          str = new String(localObject3.getValue(), KerberosString.MSNAME ? "UTF8" : "8859_1");
          break;
        case 11: 
          localDerValue1 = new DerValue(localObject3.getValue());
          break;
        case 19: 
          localDerValue2 = new DerValue(localObject3.getValue());
        }
      }
    }
    Object localObject2;
    if (localDerValue2 != null) {
      while (localDerValue2.data.available() > 0)
      {
        ??? = localDerValue2.data.getDerValue();
        localObject2 = new ETypeInfo2((DerValue)???);
        if ((((ETypeInfo2)localObject2).getParams() == null) && (((ETypeInfo2)localObject2).getEType() == paramInt)) {
          return new SaltAndParams(((ETypeInfo2)localObject2).getSalt(), ((ETypeInfo2)localObject2).getParams());
        }
      }
    }
    if (localDerValue1 != null) {
      while (localDerValue1.data.available() > 0)
      {
        ??? = localDerValue1.data.getDerValue();
        localObject2 = new ETypeInfo((DerValue)???);
        if (((ETypeInfo)localObject2).getEType() == paramInt) {
          return new SaltAndParams(((ETypeInfo)localObject2).getSalt(), null);
        }
      }
    }
    if (str != null) {
      return new SaltAndParams(str, null);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(">>>Pre-Authentication Data:\n\t PA-DATA type = ").append(this.pADataType).append('\n');
    DerValue localDerValue3;
    Object localObject;
    switch (this.pADataType)
    {
    case 2: 
      localStringBuilder.append("\t PA-ENC-TIMESTAMP");
      break;
    case 11: 
      if (this.pADataValue != null) {
        try
        {
          DerValue localDerValue1 = new DerValue(this.pADataValue);
          while (localDerValue1.data.available() > 0)
          {
            localDerValue3 = localDerValue1.data.getDerValue();
            localObject = new ETypeInfo(localDerValue3);
            localStringBuilder.append("\t PA-ETYPE-INFO etype = ").append(((ETypeInfo)localObject).getEType()).append(", salt = ").append(((ETypeInfo)localObject).getSalt()).append('\n');
          }
        }
        catch (IOException|Asn1Exception localIOException1)
        {
          localStringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
        }
      }
      break;
    case 19: 
      if (this.pADataValue != null) {
        try
        {
          DerValue localDerValue2 = new DerValue(this.pADataValue);
          while (localDerValue2.data.available() > 0)
          {
            localDerValue3 = localDerValue2.data.getDerValue();
            localObject = new ETypeInfo2(localDerValue3);
            localStringBuilder.append("\t PA-ETYPE-INFO2 etype = ").append(((ETypeInfo2)localObject).getEType()).append(", salt = ").append(((ETypeInfo2)localObject).getSalt()).append(", s2kparams = ");
            byte[] arrayOfByte = ((ETypeInfo2)localObject).getParams();
            if (arrayOfByte == null) {
              localStringBuilder.append("null\n");
            } else if (arrayOfByte.length == 0) {
              localStringBuilder.append("empty\n");
            } else {
              localStringBuilder.append(new HexDumpEncoder().encodeBuffer(arrayOfByte));
            }
          }
        }
        catch (IOException|Asn1Exception localIOException2)
        {
          localStringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
        }
      }
      break;
    case 129: 
      localStringBuilder.append("\t PA-FOR-USER\n");
      break;
    }
    return localStringBuilder.toString();
  }
  
  public static class SaltAndParams
  {
    public final String salt;
    public final byte[] params;
    
    public SaltAndParams(String paramString, byte[] paramArrayOfByte)
    {
      if ((paramString != null) && (paramString.isEmpty())) {
        paramString = null;
      }
      this.salt = paramString;
      this.params = paramArrayOfByte;
    }
  }
}
