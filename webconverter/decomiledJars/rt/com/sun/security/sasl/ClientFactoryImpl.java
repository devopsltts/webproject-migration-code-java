package com.sun.security.sasl;

import com.sun.security.sasl.util.PolicyUtils;
import java.io.IOException;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

public final class ClientFactoryImpl
  implements SaslClientFactory
{
  private static final String[] myMechs = { "EXTERNAL", "CRAM-MD5", "PLAIN" };
  private static final int[] mechPolicies = { 7, 17, 16 };
  private static final int EXTERNAL = 0;
  private static final int CRAMMD5 = 1;
  private static final int PLAIN = 2;
  
  public ClientFactoryImpl() {}
  
  public SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if ((paramArrayOfString[i].equals(myMechs[0])) && (PolicyUtils.checkPolicy(mechPolicies[0], paramMap))) {
        return new ExternalClient(paramString1);
      }
      Object[] arrayOfObject;
      if ((paramArrayOfString[i].equals(myMechs[1])) && (PolicyUtils.checkPolicy(mechPolicies[1], paramMap)))
      {
        arrayOfObject = getUserInfo("CRAM-MD5", paramString1, paramCallbackHandler);
        return new CramMD5Client((String)arrayOfObject[0], (byte[])arrayOfObject[1]);
      }
      if ((paramArrayOfString[i].equals(myMechs[2])) && (PolicyUtils.checkPolicy(mechPolicies[2], paramMap)))
      {
        arrayOfObject = getUserInfo("PLAIN", paramString1, paramCallbackHandler);
        return new PlainClient(paramString1, (String)arrayOfObject[0], (byte[])arrayOfObject[1]);
      }
    }
    return null;
  }
  
  public String[] getMechanismNames(Map<String, ?> paramMap)
  {
    return PolicyUtils.filterMechs(myMechs, mechPolicies, paramMap);
  }
  
  private Object[] getUserInfo(String paramString1, String paramString2, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    if (paramCallbackHandler == null) {
      throw new SaslException("Callback handler to get username/password required");
    }
    try
    {
      String str1 = paramString1 + " authentication id: ";
      String str2 = paramString1 + " password: ";
      NameCallback localNameCallback = paramString2 == null ? new NameCallback(str1) : new NameCallback(str1, paramString2);
      PasswordCallback localPasswordCallback = new PasswordCallback(str2, false);
      paramCallbackHandler.handle(new Callback[] { localNameCallback, localPasswordCallback });
      char[] arrayOfChar = localPasswordCallback.getPassword();
      byte[] arrayOfByte;
      if (arrayOfChar != null)
      {
        arrayOfByte = new String(arrayOfChar).getBytes("UTF8");
        localPasswordCallback.clearPassword();
      }
      else
      {
        arrayOfByte = null;
      }
      String str3 = localNameCallback.getName();
      return new Object[] { str3, arrayOfByte };
    }
    catch (IOException localIOException)
    {
      throw new SaslException("Cannot get password", localIOException);
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new SaslException("Cannot get userid/password", localUnsupportedCallbackException);
    }
  }
}
