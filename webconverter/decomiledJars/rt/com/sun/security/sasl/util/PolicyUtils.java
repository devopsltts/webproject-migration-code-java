package com.sun.security.sasl.util;

import java.util.Map;

public final class PolicyUtils
{
  public static final int NOPLAINTEXT = 1;
  public static final int NOACTIVE = 2;
  public static final int NODICTIONARY = 4;
  public static final int FORWARD_SECRECY = 8;
  public static final int NOANONYMOUS = 16;
  public static final int PASS_CREDENTIALS = 512;
  
  private PolicyUtils() {}
  
  public static boolean checkPolicy(int paramInt, Map<String, ?> paramMap)
  {
    if (paramMap == null) {
      return true;
    }
    if (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.noplaintext"))) && ((paramInt & 0x1) == 0)) {
      return false;
    }
    if (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.noactive"))) && ((paramInt & 0x2) == 0)) {
      return false;
    }
    if (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.nodictionary"))) && ((paramInt & 0x4) == 0)) {
      return false;
    }
    if (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.noanonymous"))) && ((paramInt & 0x10) == 0)) {
      return false;
    }
    if (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.forward"))) && ((paramInt & 0x8) == 0)) {
      return false;
    }
    return (!"true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.credentials"))) || ((paramInt & 0x200) != 0);
  }
  
  public static String[] filterMechs(String[] paramArrayOfString, int[] paramArrayOfInt, Map<String, ?> paramMap)
  {
    if (paramMap == null) {
      return (String[])paramArrayOfString.clone();
    }
    boolean[] arrayOfBoolean = new boolean[paramArrayOfString.length];
    int i = 0;
    for (int j = 0; j < paramArrayOfString.length; j++) {
      if ((arrayOfBoolean[j] = checkPolicy(paramArrayOfInt[j], paramMap))) {
        i++;
      }
    }
    String[] arrayOfString = new String[i];
    int k = 0;
    int m = 0;
    while (k < paramArrayOfString.length)
    {
      if (arrayOfBoolean[k] != 0) {
        arrayOfString[(m++)] = paramArrayOfString[k];
      }
      k++;
    }
    return arrayOfString;
  }
}
