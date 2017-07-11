package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import javax.naming.ldap.Control;

class DigestClientId
  extends SimpleClientId
{
  private static final String[] SASL_PROPS = { "java.naming.security.sasl.authorizationId", "java.naming.security.sasl.realm", "javax.security.sasl.qop", "javax.security.sasl.strength", "javax.security.sasl.reuse", "javax.security.sasl.server.authentication", "javax.security.sasl.maxbuffer", "javax.security.sasl.policy.noplaintext", "javax.security.sasl.policy.noactive", "javax.security.sasl.policy.nodictionary", "javax.security.sasl.policy.noanonymous", "javax.security.sasl.policy.forward", "javax.security.sasl.policy.credentials" };
  private final String[] propvals;
  private final int myHash;
  private int pHash = 0;
  
  DigestClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3, String paramString4, Object paramObject, Hashtable<?, ?> paramHashtable)
  {
    super(paramInt1, paramString1, paramInt2, paramString2, paramArrayOfControl, paramOutputStream, paramString3, paramString4, paramObject);
    if (paramHashtable == null)
    {
      this.propvals = null;
    }
    else
    {
      this.propvals = new String[SASL_PROPS.length];
      for (int i = 0; i < SASL_PROPS.length; i++)
      {
        this.propvals[i] = ((String)paramHashtable.get(SASL_PROPS[i]));
        if (this.propvals[i] != null) {
          this.pHash = (this.pHash * 31 + this.propvals[i].hashCode());
        }
      }
    }
    this.myHash = (super.hashCode() + this.pHash);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof DigestClientId)) {
      return false;
    }
    DigestClientId localDigestClientId = (DigestClientId)paramObject;
    return (this.myHash == localDigestClientId.myHash) && (this.pHash == localDigestClientId.pHash) && (super.equals(paramObject)) && (Arrays.equals(this.propvals, localDigestClientId.propvals));
  }
  
  public int hashCode()
  {
    return this.myHash;
  }
  
  public String toString()
  {
    if (this.propvals != null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < this.propvals.length; i++)
      {
        localStringBuffer.append(':');
        if (this.propvals[i] != null) {
          localStringBuffer.append(this.propvals[i]);
        }
      }
      return super.toString() + localStringBuffer.toString();
    }
    return super.toString();
  }
}
