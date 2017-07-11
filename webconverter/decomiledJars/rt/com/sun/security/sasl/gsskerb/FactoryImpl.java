package com.sun.security.sasl.gsskerb;

import com.sun.security.sasl.util.PolicyUtils;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public final class FactoryImpl
  implements SaslClientFactory, SaslServerFactory
{
  private static final String[] myMechs = { "GSSAPI" };
  private static final int[] mechPolicies = { 19 };
  private static final int GSS_KERB_V5 = 0;
  
  public FactoryImpl() {}
  
  public SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i].equals(myMechs[0])) && (PolicyUtils.checkPolicy(mechPolicies[0], paramMap))) {
        return new GssKrb5Client(paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
      }
    }
    return null;
  }
  
  public SaslServer createSaslServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    if ((paramString1.equals(myMechs[0])) && (PolicyUtils.checkPolicy(mechPolicies[0], paramMap)))
    {
      if (paramCallbackHandler == null) {
        throw new SaslException("Callback handler with support for AuthorizeCallback required");
      }
      return new GssKrb5Server(paramString2, paramString3, paramMap, paramCallbackHandler);
    }
    return null;
  }
  
  public String[] getMechanismNames(Map<String, ?> paramMap)
  {
    return PolicyUtils.filterMechs(myMechs, mechPolicies, paramMap);
  }
}
