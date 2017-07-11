package com.sun.jmx.snmp;

public class SnmpOpaque
  extends SnmpString
{
  private static final long serialVersionUID = 380952213936036664L;
  static final String name = "Opaque";
  
  public SnmpOpaque(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  public SnmpOpaque(Byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  public SnmpOpaque(String paramString)
  {
    super(paramString);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.value.length; i++)
    {
      int j = this.value[i];
      int k = j >= 0 ? j : j + 256;
      localStringBuffer.append(Character.forDigit(k / 16, 16));
      localStringBuffer.append(Character.forDigit(k % 16, 16));
    }
    return localStringBuffer.toString();
  }
  
  public final String getTypeName()
  {
    return "Opaque";
  }
}
