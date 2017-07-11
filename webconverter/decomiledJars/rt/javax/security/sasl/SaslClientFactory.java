package javax.security.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

public abstract interface SaslClientFactory
{
  public abstract SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException;
  
  public abstract String[] getMechanismNames(Map<String, ?> paramMap);
}
