package java.security.interfaces;

import java.security.spec.ECParameterSpec;

public abstract interface ECKey
{
  public abstract ECParameterSpec getParams();
}
