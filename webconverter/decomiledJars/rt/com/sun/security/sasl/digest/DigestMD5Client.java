package com.sun.security.sasl.digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class DigestMD5Client
  extends DigestMD5Base
  implements SaslClient
{
  private static final String MY_CLASS_NAME = DigestMD5Client.class.getName();
  private static final String CIPHER_PROPERTY = "com.sun.security.sasl.digest.cipher";
  private static final String[] DIRECTIVE_KEY = { "realm", "qop", "algorithm", "nonce", "maxbuf", "charset", "cipher", "rspauth", "stale" };
  private static final int REALM = 0;
  private static final int QOP = 1;
  private static final int ALGORITHM = 2;
  private static final int NONCE = 3;
  private static final int MAXBUF = 4;
  private static final int CHARSET = 5;
  private static final int CIPHER = 6;
  private static final int RESPONSE_AUTH = 7;
  private static final int STALE = 8;
  private int nonceCount;
  private String specifiedCipher;
  private byte[] cnonce;
  private String username;
  private char[] passwd;
  private byte[] authzidBytes;
  
  DigestMD5Client(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    super(paramMap, MY_CLASS_NAME, 2, paramString2 + "/" + paramString3, paramCallbackHandler);
    if (paramString1 != null)
    {
      this.authzid = paramString1;
      try
      {
        this.authzidBytes = paramString1.getBytes("UTF8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new SaslException("DIGEST-MD5: Error encoding authzid value into UTF-8", localUnsupportedEncodingException);
      }
    }
    if (paramMap != null)
    {
      this.specifiedCipher = ((String)paramMap.get("com.sun.security.sasl.digest.cipher"));
      logger.log(Level.FINE, "DIGEST60:Explicitly specified cipher: {0}", this.specifiedCipher);
    }
  }
  
  public boolean hasInitialResponse()
  {
    return false;
  }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (paramArrayOfByte.length > 2048) {
      throw new SaslException("DIGEST-MD5: Invalid digest-challenge length. Got:  " + paramArrayOfByte.length + " Expected < " + 2048);
    }
    byte[][] arrayOfByte;
    switch (this.step)
    {
    case 2: 
      ArrayList localArrayList = new ArrayList(3);
      arrayOfByte = parseDirectives(paramArrayOfByte, DIRECTIVE_KEY, localArrayList, 0);
      try
      {
        processChallenge(arrayOfByte, localArrayList);
        checkQopSupport(arrayOfByte[1], arrayOfByte[6]);
        this.step += 1;
        return generateClientResponse(arrayOfByte[5]);
      }
      catch (SaslException localSaslException)
      {
        this.step = 0;
        clearPassword();
        throw localSaslException;
      }
      catch (IOException localIOException)
      {
        this.step = 0;
        clearPassword();
        throw new SaslException("DIGEST-MD5: Error generating digest response-value", localIOException);
      }
    case 3: 
      try
      {
        arrayOfByte = parseDirectives(paramArrayOfByte, DIRECTIVE_KEY, null, 0);
        validateResponseValue(arrayOfByte[7]);
        if ((this.integrity) && (this.privacy)) {
          this.secCtx = new DigestMD5Base.DigestPrivacy(this, true);
        } else if (this.integrity) {
          this.secCtx = new DigestMD5Base.DigestIntegrity(this, true);
        }
        byte[] arrayOfByte1 = null;
        return arrayOfByte1;
      }
      finally
      {
        clearPassword();
        this.step = 0;
        this.completed = true;
      }
    }
    throw new SaslException("DIGEST-MD5: Client at illegal state");
  }
  
  private void processChallenge(byte[][] paramArrayOfByte, List<byte[]> paramList)
    throws SaslException, UnsupportedEncodingException
  {
    if (paramArrayOfByte[5] != null)
    {
      if (!"utf-8".equals(new String(paramArrayOfByte[5], this.encoding))) {
        throw new SaslException("DIGEST-MD5: digest-challenge format violation. Unrecognised charset value: " + new String(paramArrayOfByte[5]));
      }
      this.encoding = "UTF8";
      this.useUTF8 = true;
    }
    if (paramArrayOfByte[2] == null) {
      throw new SaslException("DIGEST-MD5: Digest-challenge format violation: algorithm directive missing");
    }
    if (!"md5-sess".equals(new String(paramArrayOfByte[2], this.encoding))) {
      throw new SaslException("DIGEST-MD5: Digest-challenge format violation. Invalid value for 'algorithm' directive: " + paramArrayOfByte[2]);
    }
    if (paramArrayOfByte[3] == null) {
      throw new SaslException("DIGEST-MD5: Digest-challenge format violation: nonce directive missing");
    }
    this.nonce = paramArrayOfByte[3];
    try
    {
      String[] arrayOfString = null;
      if (paramArrayOfByte[0] != null) {
        if ((paramList == null) || (paramList.size() <= 1))
        {
          this.negotiatedRealm = new String(paramArrayOfByte[0], this.encoding);
        }
        else
        {
          arrayOfString = new String[paramList.size()];
          for (int j = 0; j < arrayOfString.length; j++) {
            arrayOfString[j] = new String((byte[])paramList.get(j), this.encoding);
          }
        }
      }
      NameCallback localNameCallback = this.authzid == null ? new NameCallback("DIGEST-MD5 authentication ID: ") : new NameCallback("DIGEST-MD5 authentication ID: ", this.authzid);
      PasswordCallback localPasswordCallback = new PasswordCallback("DIGEST-MD5 password: ", false);
      Object localObject;
      if (arrayOfString == null)
      {
        localObject = this.negotiatedRealm == null ? new RealmCallback("DIGEST-MD5 realm: ") : new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
        this.cbh.handle(new Callback[] { localObject, localNameCallback, localPasswordCallback });
        this.negotiatedRealm = ((RealmCallback)localObject).getText();
        if (this.negotiatedRealm == null) {
          this.negotiatedRealm = "";
        }
      }
      else
      {
        localObject = new RealmChoiceCallback("DIGEST-MD5 realm: ", arrayOfString, 0, false);
        this.cbh.handle(new Callback[] { localObject, localNameCallback, localPasswordCallback });
        int[] arrayOfInt = ((RealmChoiceCallback)localObject).getSelectedIndexes();
        if ((arrayOfInt == null) || (arrayOfInt[0] < 0) || (arrayOfInt[0] >= arrayOfString.length)) {
          throw new SaslException("DIGEST-MD5: Invalid realm chosen");
        }
        this.negotiatedRealm = arrayOfString[arrayOfInt[0]];
      }
      this.passwd = localPasswordCallback.getPassword();
      localPasswordCallback.clearPassword();
      this.username = localNameCallback.getName();
    }
    catch (SaslException localSaslException)
    {
      throw localSaslException;
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire realm, authentication ID or password", localUnsupportedCallbackException);
    }
    catch (IOException localIOException)
    {
      throw new SaslException("DIGEST-MD5: Error acquiring realm, authentication ID or password", localIOException);
    }
    if ((this.username == null) || (this.passwd == null)) {
      throw new SaslException("DIGEST-MD5: authentication ID and password must be specified");
    }
    int i = paramArrayOfByte[4] == null ? 65536 : Integer.parseInt(new String(paramArrayOfByte[4], this.encoding));
    this.sendMaxBufSize = (this.sendMaxBufSize == 0 ? i : Math.min(this.sendMaxBufSize, i));
  }
  
  private void checkQopSupport(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws IOException
  {
    String str;
    if (paramArrayOfByte1 == null) {
      str = "auth";
    } else {
      str = new String(paramArrayOfByte1, this.encoding);
    }
    String[] arrayOfString = new String[3];
    byte[] arrayOfByte = parseQop(str, arrayOfString, true);
    byte b = combineMasks(arrayOfByte);
    switch (findPreferredMask(b, this.qop))
    {
    case 0: 
      throw new SaslException("DIGEST-MD5: No common protection layer between client and server");
    case 1: 
      this.negotiatedQop = "auth";
      break;
    case 2: 
      this.negotiatedQop = "auth-int";
      this.integrity = true;
      this.rawSendSize = (this.sendMaxBufSize - 16);
      break;
    case 4: 
      this.negotiatedQop = "auth-conf";
      this.privacy = (this.integrity = 1);
      this.rawSendSize = (this.sendMaxBufSize - 26);
      checkStrengthSupport(paramArrayOfByte2);
    }
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "DIGEST61:Raw send size: {0}", new Integer(this.rawSendSize));
    }
  }
  
  private void checkStrengthSupport(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new SaslException("DIGEST-MD5: server did not specify cipher to use for 'auth-conf'");
    }
    String str1 = new String(paramArrayOfByte, this.encoding);
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, ", \t\n");
    int i = localStringTokenizer.countTokens();
    String str2 = null;
    byte[] arrayOfByte1 = { 0, 0, 0, 0, 0 };
    String[] arrayOfString = new String[arrayOfByte1.length];
    for (int j = 0; j < i; j++)
    {
      str2 = localStringTokenizer.nextToken();
      for (k = 0; k < CIPHER_TOKENS.length; k++) {
        if (str2.equals(CIPHER_TOKENS[k]))
        {
          int tmp126_124 = k;
          byte[] tmp126_122 = arrayOfByte1;
          tmp126_122[tmp126_124] = ((byte)(tmp126_122[tmp126_124] | CIPHER_MASKS[k]));
          arrayOfString[k] = str2;
          logger.log(Level.FINE, "DIGEST62:Server supports {0}", str2);
        }
      }
    }
    byte[] arrayOfByte2 = getPlatformCiphers();
    int k = 0;
    for (int m = 0; m < arrayOfByte1.length; m++)
    {
      int tmp192_190 = m;
      byte[] tmp192_188 = arrayOfByte1;
      tmp192_188[tmp192_190] = ((byte)(tmp192_188[tmp192_190] & arrayOfByte2[m]));
      k = (byte)(k | arrayOfByte1[m]);
    }
    if (k == 0) {
      throw new SaslException("DIGEST-MD5: Client supports none of these cipher suites: " + str1);
    }
    this.negotiatedCipher = findCipherAndStrength(arrayOfByte1, arrayOfString);
    if (this.negotiatedCipher == null) {
      throw new SaslException("DIGEST-MD5: Unable to negotiate a strength level for 'auth-conf'");
    }
    logger.log(Level.FINE, "DIGEST63:Cipher suite: {0}", this.negotiatedCipher);
  }
  
  private String findCipherAndStrength(byte[] paramArrayOfByte, String[] paramArrayOfString)
  {
    for (int j = 0; j < this.strength.length; j++)
    {
      int i;
      if ((i = this.strength[j]) != 0) {
        for (int k = 0; k < paramArrayOfByte.length; k++) {
          if ((i == paramArrayOfByte[k]) && ((this.specifiedCipher == null) || (this.specifiedCipher.equals(paramArrayOfString[k]))))
          {
            switch (i)
            {
            case 4: 
              this.negotiatedStrength = "high";
              break;
            case 2: 
              this.negotiatedStrength = "medium";
              break;
            case 1: 
              this.negotiatedStrength = "low";
            }
            return paramArrayOfString[k];
          }
        }
      }
    }
    return null;
  }
  
  private byte[] generateClientResponse(byte[] paramArrayOfByte)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    if (this.useUTF8)
    {
      localByteArrayOutputStream.write("charset=".getBytes(this.encoding));
      localByteArrayOutputStream.write(paramArrayOfByte);
      localByteArrayOutputStream.write(44);
    }
    localByteArrayOutputStream.write(("username=\"" + quotedStringValue(this.username) + "\",").getBytes(this.encoding));
    if (this.negotiatedRealm.length() > 0) {
      localByteArrayOutputStream.write(("realm=\"" + quotedStringValue(this.negotiatedRealm) + "\",").getBytes(this.encoding));
    }
    localByteArrayOutputStream.write("nonce=\"".getBytes(this.encoding));
    writeQuotedStringValue(localByteArrayOutputStream, this.nonce);
    localByteArrayOutputStream.write(34);
    localByteArrayOutputStream.write(44);
    this.nonceCount = getNonceCount(this.nonce);
    localByteArrayOutputStream.write(("nc=" + nonceCountToHex(this.nonceCount) + ",").getBytes(this.encoding));
    this.cnonce = generateNonce();
    localByteArrayOutputStream.write("cnonce=\"".getBytes(this.encoding));
    writeQuotedStringValue(localByteArrayOutputStream, this.cnonce);
    localByteArrayOutputStream.write("\",".getBytes(this.encoding));
    localByteArrayOutputStream.write(("digest-uri=\"" + this.digestUri + "\",").getBytes(this.encoding));
    localByteArrayOutputStream.write("maxbuf=".getBytes(this.encoding));
    localByteArrayOutputStream.write(String.valueOf(this.recvMaxBufSize).getBytes(this.encoding));
    localByteArrayOutputStream.write(44);
    try
    {
      localByteArrayOutputStream.write("response=".getBytes(this.encoding));
      localByteArrayOutputStream.write(generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes));
      localByteArrayOutputStream.write(44);
    }
    catch (Exception localException)
    {
      throw new SaslException("DIGEST-MD5: Error generating response value", localException);
    }
    localByteArrayOutputStream.write(("qop=" + this.negotiatedQop).getBytes(this.encoding));
    if (this.negotiatedCipher != null) {
      localByteArrayOutputStream.write((",cipher=\"" + this.negotiatedCipher + "\"").getBytes(this.encoding));
    }
    if (this.authzidBytes != null)
    {
      localByteArrayOutputStream.write(",authzid=\"".getBytes(this.encoding));
      writeQuotedStringValue(localByteArrayOutputStream, this.authzidBytes);
      localByteArrayOutputStream.write("\"".getBytes(this.encoding));
    }
    if (localByteArrayOutputStream.size() > 4096) {
      throw new SaslException("DIGEST-MD5: digest-response size too large. Length: " + localByteArrayOutputStream.size());
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  private void validateResponseValue(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (paramArrayOfByte == null) {
      throw new SaslException("DIGEST-MD5: Authenication failed. Expecting 'rspauth' authentication success message");
    }
    try
    {
      byte[] arrayOfByte = generateResponseValue("", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes);
      if (!Arrays.equals(arrayOfByte, paramArrayOfByte)) {
        throw new SaslException("Server's rspauth value does not match what client expects");
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new SaslException("Problem generating response value for verification", localNoSuchAlgorithmException);
    }
    catch (IOException localIOException)
    {
      throw new SaslException("Problem generating response value for verification", localIOException);
    }
  }
  
  private static int getNonceCount(byte[] paramArrayOfByte)
  {
    return 1;
  }
  
  private void clearPassword()
  {
    if (this.passwd != null)
    {
      for (int i = 0; i < this.passwd.length; i++) {
        this.passwd[i] = '\000';
      }
      this.passwd = null;
    }
  }
}
