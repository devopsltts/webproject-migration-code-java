package java.security.cert;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PolicyQualifierInfo
{
  private byte[] mEncoded;
  private String mId;
  private byte[] mData;
  private String pqiString;
  
  public PolicyQualifierInfo(byte[] paramArrayOfByte)
    throws IOException
  {
    this.mEncoded = ((byte[])paramArrayOfByte.clone());
    DerValue localDerValue = new DerValue(this.mEncoded);
    if (localDerValue.tag != 48) {
      throw new IOException("Invalid encoding for PolicyQualifierInfo");
    }
    this.mId = localDerValue.data.getDerValue().getOID().toString();
    byte[] arrayOfByte = localDerValue.data.toByteArray();
    if (arrayOfByte == null)
    {
      this.mData = null;
    }
    else
    {
      this.mData = new byte[arrayOfByte.length];
      System.arraycopy(arrayOfByte, 0, this.mData, 0, arrayOfByte.length);
    }
  }
  
  public final String getPolicyQualifierId()
  {
    return this.mId;
  }
  
  public final byte[] getEncoded()
  {
    return (byte[])this.mEncoded.clone();
  }
  
  public final byte[] getPolicyQualifier()
  {
    return this.mData == null ? null : (byte[])this.mData.clone();
  }
  
  public String toString()
  {
    if (this.pqiString != null) {
      return this.pqiString;
    }
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("PolicyQualifierInfo: [\n");
    localStringBuffer.append("  qualifierID: " + this.mId + "\n");
    localStringBuffer.append("  qualifier: " + (this.mData == null ? "null" : localHexDumpEncoder.encodeBuffer(this.mData)) + "\n");
    localStringBuffer.append("]");
    this.pqiString = localStringBuffer.toString();
    return this.pqiString;
  }
}
