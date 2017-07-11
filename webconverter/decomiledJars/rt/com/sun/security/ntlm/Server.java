package com.sun.security.ntlm;

import java.util.Arrays;
import java.util.Locale;

public abstract class Server
  extends NTLM
{
  private final String domain;
  private final boolean allVersion;
  
  public Server(String paramString1, String paramString2)
    throws NTLMException
  {
    super(paramString1);
    if (paramString2 == null) {
      throw new NTLMException(6, "domain cannot be null");
    }
    this.allVersion = (paramString1 == null);
    this.domain = paramString2;
    debug("NTLM Server: (t,version) = (%s,%s)\n", new Object[] { paramString2, paramString1 });
  }
  
  public byte[] type2(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NTLMException
  {
    if (paramArrayOfByte2 == null) {
      throw new NTLMException(6, "nonce cannot be null");
    }
    debug("NTLM Server: Type 1 received\n", new Object[0]);
    if (paramArrayOfByte1 != null) {
      debug(paramArrayOfByte1);
    }
    NTLM.Writer localWriter = new NTLM.Writer(2, 32);
    int i = 590341;
    localWriter.writeSecurityBuffer(12, this.domain, true);
    localWriter.writeInt(20, i);
    localWriter.writeBytes(24, paramArrayOfByte2);
    debug("NTLM Server: Type 2 created\n", new Object[0]);
    debug(localWriter.getBytes());
    return localWriter.getBytes();
  }
  
  public String[] verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NTLMException
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      throw new NTLMException(6, "type1 or nonce cannot be null");
    }
    debug("NTLM Server: Type 3 received\n", new Object[0]);
    if (paramArrayOfByte1 != null) {
      debug(paramArrayOfByte1);
    }
    NTLM.Reader localReader = new NTLM.Reader(paramArrayOfByte1);
    String str1 = localReader.readSecurityBuffer(36, true);
    String str2 = localReader.readSecurityBuffer(44, true);
    String str3 = localReader.readSecurityBuffer(28, true);
    boolean bool = false;
    char[] arrayOfChar = getPassword(str3, str1);
    if (arrayOfChar == null) {
      throw new NTLMException(3, "Unknown user");
    }
    byte[] arrayOfByte1 = localReader.readSecurityBuffer(12);
    byte[] arrayOfByte2 = localReader.readSecurityBuffer(20);
    byte[] arrayOfByte3;
    byte[] arrayOfByte4;
    byte[] arrayOfByte5;
    if ((!bool) && ((this.allVersion) || (this.v == Version.NTLM)))
    {
      if (arrayOfByte1.length > 0)
      {
        arrayOfByte3 = getP1(arrayOfChar);
        arrayOfByte4 = calcLMHash(arrayOfByte3);
        arrayOfByte5 = calcResponse(arrayOfByte4, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte5, arrayOfByte1)) {
          bool = true;
        }
      }
      if (arrayOfByte2.length > 0)
      {
        arrayOfByte3 = getP2(arrayOfChar);
        arrayOfByte4 = calcNTHash(arrayOfByte3);
        arrayOfByte5 = calcResponse(arrayOfByte4, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte5, arrayOfByte2)) {
          bool = true;
        }
      }
      debug("NTLM Server: verify using NTLM: " + bool + "\n", new Object[0]);
    }
    byte[] arrayOfByte6;
    if ((!bool) && ((this.allVersion) || (this.v == Version.NTLM2)))
    {
      arrayOfByte3 = getP2(arrayOfChar);
      arrayOfByte4 = calcNTHash(arrayOfByte3);
      arrayOfByte5 = Arrays.copyOf(arrayOfByte1, 8);
      arrayOfByte6 = ntlm2NTLM(arrayOfByte4, arrayOfByte5, paramArrayOfByte2);
      if (Arrays.equals(arrayOfByte2, arrayOfByte6)) {
        bool = true;
      }
      debug("NTLM Server: verify using NTLM2: " + bool + "\n", new Object[0]);
    }
    if ((!bool) && ((this.allVersion) || (this.v == Version.NTLMv2)))
    {
      arrayOfByte3 = getP2(arrayOfChar);
      arrayOfByte4 = calcNTHash(arrayOfByte3);
      if (arrayOfByte1.length > 0)
      {
        arrayOfByte5 = Arrays.copyOfRange(arrayOfByte1, 16, arrayOfByte1.length);
        arrayOfByte6 = calcV2(arrayOfByte4, str1.toUpperCase(Locale.US) + str3, arrayOfByte5, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte6, arrayOfByte1)) {
          bool = true;
        }
      }
      if (arrayOfByte2.length > 0)
      {
        arrayOfByte5 = Arrays.copyOfRange(arrayOfByte2, 16, arrayOfByte2.length);
        arrayOfByte6 = calcV2(arrayOfByte4, str1.toUpperCase(Locale.US) + str3, arrayOfByte5, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte6, arrayOfByte2)) {
          bool = true;
        }
      }
      debug("NTLM Server: verify using NTLMv2: " + bool + "\n", new Object[0]);
    }
    if (!bool) {
      throw new NTLMException(4, "None of LM and NTLM verified");
    }
    return new String[] { str1, str2, str3 };
  }
  
  public abstract char[] getPassword(String paramString1, String paramString2);
}
