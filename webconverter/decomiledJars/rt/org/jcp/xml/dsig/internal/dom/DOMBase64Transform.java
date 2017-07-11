package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMBase64Transform
  extends ApacheTransform
{
  public DOMBase64Transform() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec != null) {
      throw new InvalidAlgorithmParameterException("params must be null");
    }
  }
}
