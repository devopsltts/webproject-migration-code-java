package com.sun.jmx.snmp;

import com.sun.jmx.snmp.internal.SnmpTools;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SnmpEngineId
  implements Serializable
{
  private static final long serialVersionUID = 5434729655830763317L;
  byte[] engineId = null;
  String hexString = null;
  String humanString = null;
  
  SnmpEngineId(String paramString)
  {
    this.engineId = SnmpTools.ascii2binary(paramString);
    this.hexString = paramString.toLowerCase();
  }
  
  SnmpEngineId(byte[] paramArrayOfByte)
  {
    this.engineId = paramArrayOfByte;
    this.hexString = SnmpTools.binary2ascii(paramArrayOfByte).toLowerCase();
  }
  
  public String getReadableId()
  {
    return this.humanString;
  }
  
  public String toString()
  {
    return this.hexString;
  }
  
  public byte[] getBytes()
  {
    return this.engineId;
  }
  
  void setStringValue(String paramString)
  {
    this.humanString = paramString;
  }
  
  static void validateId(String paramString)
    throws IllegalArgumentException
  {
    byte[] arrayOfByte = SnmpTools.ascii2binary(paramString);
    validateId(arrayOfByte);
  }
  
  static void validateId(byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    if (paramArrayOfByte.length < 5) {
      throw new IllegalArgumentException("Id size lower than 5 bytes.");
    }
    if (paramArrayOfByte.length > 32) {
      throw new IllegalArgumentException("Id size greater than 32 bytes.");
    }
    if (((paramArrayOfByte[0] & 0x80) == 0) && (paramArrayOfByte.length != 12)) {
      throw new IllegalArgumentException("Very first bit = 0 and length != 12 octets");
    }
    byte[] arrayOfByte1 = new byte[paramArrayOfByte.length];
    if (Arrays.equals(arrayOfByte1, paramArrayOfByte)) {
      throw new IllegalArgumentException("Zeroed Id.");
    }
    byte[] arrayOfByte2 = new byte[paramArrayOfByte.length];
    Arrays.fill(arrayOfByte2, (byte)-1);
    if (Arrays.equals(arrayOfByte2, paramArrayOfByte)) {
      throw new IllegalArgumentException("0xFF Id.");
    }
  }
  
  public static SnmpEngineId createEngineId(byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    validateId(paramArrayOfByte);
    return new SnmpEngineId(paramArrayOfByte);
  }
  
  public static SnmpEngineId createEngineId()
  {
    Object localObject = null;
    byte[] arrayOfByte = new byte[13];
    int i = 42;
    long l1 = 255L;
    long l2 = System.currentTimeMillis();
    arrayOfByte[0] = ((byte)((i & 0xFF000000) >> 24));
    int tmp32_31 = 0;
    byte[] tmp32_30 = arrayOfByte;
    tmp32_30[tmp32_31] = ((byte)(tmp32_30[tmp32_31] | 0x80));
    arrayOfByte[1] = ((byte)((i & 0xFF0000) >> 16));
    arrayOfByte[2] = ((byte)((i & 0xFF00) >> 8));
    arrayOfByte[3] = ((byte)(i & 0xFF));
    arrayOfByte[4] = 5;
    arrayOfByte[5] = ((byte)(int)((tmp32_31 & l1 << 56) >>> 56));
    arrayOfByte[6] = ((byte)(int)((tmp32_31 & l1 << 48) >>> 48));
    arrayOfByte[7] = ((byte)(int)((tmp32_31 & l1 << 40) >>> 40));
    arrayOfByte[8] = ((byte)(int)((tmp32_31 & l1 << 32) >>> 32));
    arrayOfByte[9] = ((byte)(int)((tmp32_31 & l1 << 24) >>> 24));
    arrayOfByte[10] = ((byte)(int)((tmp32_31 & l1 << 16) >>> 16));
    arrayOfByte[11] = ((byte)(int)((tmp32_31 & l1 << 8) >>> 8));
    arrayOfByte[12] = ((byte)(int)(tmp32_31 & l1));
    return new SnmpEngineId(arrayOfByte);
  }
  
  public SnmpOid toOid()
  {
    long[] arrayOfLong = new long[this.engineId.length + 1];
    arrayOfLong[0] = this.engineId.length;
    for (int i = 1; i <= this.engineId.length; i++) {
      arrayOfLong[i] = (this.engineId[(i - 1)] & 0xFF);
    }
    return new SnmpOid(arrayOfLong);
  }
  
  public static SnmpEngineId createEngineId(String paramString)
    throws IllegalArgumentException, UnknownHostException
  {
    return createEngineId(paramString, null);
  }
  
  public static SnmpEngineId createEngineId(String paramString1, String paramString2)
    throws IllegalArgumentException, UnknownHostException
  {
    if (paramString1 == null) {
      return null;
    }
    if ((paramString1.startsWith("0x")) || (paramString1.startsWith("0X")))
    {
      validateId(paramString1);
      return new SnmpEngineId(paramString1);
    }
    paramString2 = paramString2 == null ? ":" : paramString2;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, paramString2, true);
    String str1 = null;
    String str2 = null;
    String str3 = null;
    int i = 161;
    int j = 42;
    InetAddress localInetAddress = null;
    SnmpEngineId localSnmpEngineId = null;
    try
    {
      try
      {
        str1 = localStringTokenizer.nextToken();
      }
      catch (NoSuchElementException localNoSuchElementException1)
      {
        throw new IllegalArgumentException("Passed string is invalid : [" + paramString1 + "]");
      }
      if (!str1.equals(paramString2))
      {
        localInetAddress = InetAddress.getByName(str1);
        try
        {
          localStringTokenizer.nextToken();
        }
        catch (NoSuchElementException localNoSuchElementException2)
        {
          localSnmpEngineId = createEngineId(localInetAddress, i, j);
          localSnmpEngineId.setStringValue(paramString1);
          return localSnmpEngineId;
        }
      }
      else
      {
        localInetAddress = InetAddress.getLocalHost();
      }
      try
      {
        str2 = localStringTokenizer.nextToken();
      }
      catch (NoSuchElementException localNoSuchElementException3)
      {
        localSnmpEngineId = createEngineId(localInetAddress, i, j);
        localSnmpEngineId.setStringValue(paramString1);
        return localSnmpEngineId;
      }
      if (!str2.equals(paramString2))
      {
        i = Integer.parseInt(str2);
        try
        {
          localStringTokenizer.nextToken();
        }
        catch (NoSuchElementException localNoSuchElementException4)
        {
          localSnmpEngineId = createEngineId(localInetAddress, i, j);
          localSnmpEngineId.setStringValue(paramString1);
          return localSnmpEngineId;
        }
      }
      try
      {
        str3 = localStringTokenizer.nextToken();
      }
      catch (NoSuchElementException localNoSuchElementException5)
      {
        localSnmpEngineId = createEngineId(localInetAddress, i, j);
        localSnmpEngineId.setStringValue(paramString1);
        return localSnmpEngineId;
      }
      if (!str3.equals(paramString2)) {
        j = Integer.parseInt(str3);
      }
      localSnmpEngineId = createEngineId(localInetAddress, i, j);
      localSnmpEngineId.setStringValue(paramString1);
      return localSnmpEngineId;
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException("Passed string is invalid : [" + paramString1 + "]. Check that the used separator [" + paramString2 + "] is compatible with IPv6 address format.");
    }
  }
  
  public static SnmpEngineId createEngineId(int paramInt)
    throws UnknownHostException
  {
    int i = 42;
    InetAddress localInetAddress = null;
    localInetAddress = InetAddress.getLocalHost();
    return createEngineId(localInetAddress, paramInt, i);
  }
  
  public static SnmpEngineId createEngineId(InetAddress paramInetAddress, int paramInt)
    throws IllegalArgumentException
  {
    int i = 42;
    if (paramInetAddress == null) {
      throw new IllegalArgumentException("InetAddress is null.");
    }
    return createEngineId(paramInetAddress, paramInt, i);
  }
  
  public static SnmpEngineId createEngineId(int paramInt1, int paramInt2)
    throws UnknownHostException
  {
    InetAddress localInetAddress = null;
    localInetAddress = InetAddress.getLocalHost();
    return createEngineId(localInetAddress, paramInt1, paramInt2);
  }
  
  public static SnmpEngineId createEngineId(InetAddress paramInetAddress, int paramInt1, int paramInt2)
  {
    if (paramInetAddress == null) {
      throw new IllegalArgumentException("InetAddress is null.");
    }
    byte[] arrayOfByte1 = paramInetAddress.getAddress();
    byte[] arrayOfByte2 = new byte[9 + arrayOfByte1.length];
    arrayOfByte2[0] = ((byte)((paramInt2 & 0xFF000000) >> 24));
    int tmp43_42 = 0;
    byte[] tmp43_40 = arrayOfByte2;
    tmp43_40[tmp43_42] = ((byte)(tmp43_40[tmp43_42] | 0x80));
    arrayOfByte2[1] = ((byte)((paramInt2 & 0xFF0000) >> 16));
    arrayOfByte2[2] = ((byte)((paramInt2 & 0xFF00) >> 8));
    arrayOfByte2[3] = ((byte)(paramInt2 & 0xFF));
    arrayOfByte2[4] = 5;
    if (arrayOfByte1.length == 4) {
      arrayOfByte2[4] = 1;
    }
    if (arrayOfByte1.length == 16) {
      arrayOfByte2[4] = 2;
    }
    for (int i = 0; i < arrayOfByte1.length; i++) {
      arrayOfByte2[(i + 5)] = arrayOfByte1[i];
    }
    arrayOfByte2[(5 + arrayOfByte1.length)] = ((byte)((paramInt1 & 0xFF000000) >> 24));
    arrayOfByte2[(6 + arrayOfByte1.length)] = ((byte)((paramInt1 & 0xFF0000) >> 16));
    arrayOfByte2[(7 + arrayOfByte1.length)] = ((byte)((paramInt1 & 0xFF00) >> 8));
    arrayOfByte2[(8 + arrayOfByte1.length)] = ((byte)(paramInt1 & 0xFF));
    return new SnmpEngineId(arrayOfByte2);
  }
  
  public static SnmpEngineId createEngineId(int paramInt, InetAddress paramInetAddress)
  {
    if (paramInetAddress == null) {
      throw new IllegalArgumentException("InetAddress is null.");
    }
    byte[] arrayOfByte1 = paramInetAddress.getAddress();
    byte[] arrayOfByte2 = new byte[5 + arrayOfByte1.length];
    arrayOfByte2[0] = ((byte)((paramInt & 0xFF000000) >> 24));
    int tmp39_38 = 0;
    byte[] tmp39_37 = arrayOfByte2;
    tmp39_37[tmp39_38] = ((byte)(tmp39_37[tmp39_38] | 0x80));
    arrayOfByte2[1] = ((byte)((paramInt & 0xFF0000) >> 16));
    arrayOfByte2[2] = ((byte)((paramInt & 0xFF00) >> 8));
    arrayOfByte2[3] = ((byte)(paramInt & 0xFF));
    if (arrayOfByte1.length == 4) {
      arrayOfByte2[4] = 1;
    }
    if (arrayOfByte1.length == 16) {
      arrayOfByte2[4] = 2;
    }
    for (int i = 0; i < arrayOfByte1.length; i++) {
      arrayOfByte2[(i + 5)] = arrayOfByte1[i];
    }
    return new SnmpEngineId(arrayOfByte2);
  }
  
  public static SnmpEngineId createEngineId(InetAddress paramInetAddress)
  {
    return createEngineId(42, paramInetAddress);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SnmpEngineId)) {
      return false;
    }
    return this.hexString.equals(((SnmpEngineId)paramObject).toString());
  }
  
  public int hashCode()
  {
    return this.hexString.hashCode();
  }
}
