package com.sun.jndi.ldap.sasl;

import com.sun.jndi.ldap.Connection;
import com.sun.jndi.ldap.LdapClient;
import com.sun.jndi.ldap.LdapResult;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public final class LdapSasl
{
  private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
  private static final String SASL_AUTHZ_ID = "java.naming.security.sasl.authorizationId";
  private static final String SASL_REALM = "java.naming.security.sasl.realm";
  private static final int LDAP_SUCCESS = 0;
  private static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
  private static final byte[] NO_BYTES = new byte[0];
  
  private LdapSasl() {}
  
  public static LdapResult saslBind(LdapClient paramLdapClient, Connection paramConnection, String paramString1, String paramString2, Object paramObject, String paramString3, Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl)
    throws IOException, NamingException
  {
    SaslClient localSaslClient = null;
    int i = 0;
    DefaultCallbackHandler localDefaultCallbackHandler = paramHashtable != null ? (CallbackHandler)paramHashtable.get("java.naming.security.sasl.callback") : null;
    if (localDefaultCallbackHandler == null)
    {
      localDefaultCallbackHandler = new DefaultCallbackHandler(paramString2, paramObject, (String)paramHashtable.get("java.naming.security.sasl.realm"));
      i = 1;
    }
    String str = paramHashtable != null ? (String)paramHashtable.get("java.naming.security.sasl.authorizationId") : null;
    String[] arrayOfString = getSaslMechanismNames(paramString3);
    try
    {
      localSaslClient = Sasl.createSaslClient(arrayOfString, str, "ldap", paramString1, paramHashtable, localDefaultCallbackHandler);
      if (localSaslClient == null) {
        throw new AuthenticationNotSupportedException(paramString3);
      }
      localObject1 = localSaslClient.getMechanismName();
      byte[] arrayOfByte = localSaslClient.hasInitialResponse() ? localSaslClient.evaluateChallenge(NO_BYTES) : null;
      for (LdapResult localLdapResult = paramLdapClient.ldapBind(null, arrayOfByte, paramArrayOfControl, (String)localObject1, true); (!localSaslClient.isComplete()) && ((localLdapResult.status == 14) || (localLdapResult.status == 0)); localLdapResult = paramLdapClient.ldapBind(null, arrayOfByte, paramArrayOfControl, (String)localObject1, true))
      {
        arrayOfByte = localSaslClient.evaluateChallenge(localLdapResult.serverCreds != null ? localLdapResult.serverCreds : NO_BYTES);
        if (localLdapResult.status == 0)
        {
          if (arrayOfByte == null) {
            break;
          }
          throw new AuthenticationException("SASL client generated response after success");
        }
      }
      if (localLdapResult.status == 0)
      {
        if (!localSaslClient.isComplete()) {
          throw new AuthenticationException("SASL authentication not complete despite server claims");
        }
        localObject2 = (String)localSaslClient.getNegotiatedProperty("javax.security.sasl.qop");
        if ((localObject2 != null) && ((((String)localObject2).equalsIgnoreCase("auth-int")) || (((String)localObject2).equalsIgnoreCase("auth-conf"))))
        {
          SaslInputStream localSaslInputStream = new SaslInputStream(localSaslClient, paramConnection.inStream);
          SaslOutputStream localSaslOutputStream = new SaslOutputStream(localSaslClient, paramConnection.outStream);
          paramConnection.replaceStreams(localSaslInputStream, localSaslOutputStream);
        }
        else
        {
          localSaslClient.dispose();
        }
      }
      Object localObject2 = localLdapResult;
      return localObject2;
    }
    catch (SaslException localSaslException)
    {
      Object localObject1 = new AuthenticationException(paramString3);
      ((NamingException)localObject1).setRootCause(localSaslException);
      throw ((Throwable)localObject1);
    }
    finally
    {
      if (i != 0) {
        ((DefaultCallbackHandler)localDefaultCallbackHandler).clearPassword();
      }
    }
  }
  
  private static String[] getSaslMechanismNames(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    Vector localVector = new Vector(10);
    while (localStringTokenizer.hasMoreTokens()) {
      localVector.addElement(localStringTokenizer.nextToken());
    }
    String[] arrayOfString = new String[localVector.size()];
    for (int i = 0; i < localVector.size(); i++) {
      arrayOfString[i] = ((String)localVector.elementAt(i));
    }
    return arrayOfString;
  }
}
