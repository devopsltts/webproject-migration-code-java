package java.security.spec;

import java.math.BigInteger;

public class ECPrivateKeySpec
  implements KeySpec
{
  private BigInteger s;
  private ECParameterSpec params;
  
  public ECPrivateKeySpec(BigInteger paramBigInteger, ECParameterSpec paramECParameterSpec)
  {
    if (paramBigInteger == null) {
      throw new NullPointerException("s is null");
    }
    if (paramECParameterSpec == null) {
      throw new NullPointerException("params is null");
    }
    this.s = paramBigInteger;
    this.params = paramECParameterSpec;
  }
  
  public BigInteger getS()
  {
    return this.s;
  }
  
  public ECParameterSpec getParams()
  {
    return this.params;
  }
}
