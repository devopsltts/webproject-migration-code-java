package java.security;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.jca.JCAUtil;

public abstract class SignatureSpi
{
  protected SecureRandom appRandom = null;
  
  public SignatureSpi() {}
  
  protected abstract void engineInitVerify(PublicKey paramPublicKey)
    throws InvalidKeyException;
  
  protected abstract void engineInitSign(PrivateKey paramPrivateKey)
    throws InvalidKeyException;
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    this.appRandom = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected abstract void engineUpdate(byte paramByte)
    throws SignatureException;
  
  protected abstract void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException;
  
  protected void engineUpdate(ByteBuffer paramByteBuffer)
  {
    if (!paramByteBuffer.hasRemaining()) {
      return;
    }
    try
    {
      int k;
      if (paramByteBuffer.hasArray())
      {
        byte[] arrayOfByte1 = paramByteBuffer.array();
        int j = paramByteBuffer.arrayOffset();
        k = paramByteBuffer.position();
        int m = paramByteBuffer.limit();
        engineUpdate(arrayOfByte1, j + k, m - k);
        paramByteBuffer.position(m);
      }
      else
      {
        int i = paramByteBuffer.remaining();
        byte[] arrayOfByte2 = new byte[JCAUtil.getTempArraySize(i)];
        while (i > 0)
        {
          k = Math.min(i, arrayOfByte2.length);
          paramByteBuffer.get(arrayOfByte2, 0, k);
          engineUpdate(arrayOfByte2, 0, k);
          i -= k;
        }
      }
    }
    catch (SignatureException localSignatureException)
    {
      throw new ProviderException("update() failed", localSignatureException);
    }
  }
  
  protected abstract byte[] engineSign()
    throws SignatureException;
  
  protected int engineSign(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    byte[] arrayOfByte = engineSign();
    if (paramInt2 < arrayOfByte.length) {
      throw new SignatureException("partial signatures not returned");
    }
    if (paramArrayOfByte.length - paramInt1 < arrayOfByte.length) {
      throw new SignatureException("insufficient space in the output buffer to store the signature");
    }
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt1, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected abstract boolean engineVerify(byte[] paramArrayOfByte)
    throws SignatureException;
  
  protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    return engineVerify(arrayOfByte);
  }
  
  @Deprecated
  protected abstract void engineSetParameter(String paramString, Object paramObject)
    throws InvalidParameterException;
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    throw new UnsupportedOperationException();
  }
  
  protected AlgorithmParameters engineGetParameters()
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  protected abstract Object engineGetParameter(String paramString)
    throws InvalidParameterException;
  
  public Object clone()
    throws CloneNotSupportedException
  {
    if ((this instanceof Cloneable)) {
      return super.clone();
    }
    throw new CloneNotSupportedException();
  }
}
