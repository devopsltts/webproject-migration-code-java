package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXSLTTransform
  extends ApacheTransform
{
  public DOMXSLTTransform() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec == null) {
      throw new InvalidAlgorithmParameterException("params are required");
    }
    if (!(paramTransformParameterSpec instanceof XSLTTransformParameterSpec)) {
      throw new InvalidAlgorithmParameterException("unrecognized params");
    }
    this.params = paramTransformParameterSpec;
  }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws InvalidAlgorithmParameterException
  {
    super.init(paramXMLStructure, paramXMLCryptoContext);
    unmarshalParams(DOMUtils.getFirstChildElement(this.transformElem));
  }
  
  private void unmarshalParams(Element paramElement)
  {
    this.params = new XSLTTransformParameterSpec(new DOMStructure(paramElement));
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    super.marshalParams(paramXMLStructure, paramXMLCryptoContext);
    XSLTTransformParameterSpec localXSLTTransformParameterSpec = (XSLTTransformParameterSpec)getParameterSpec();
    Node localNode = ((DOMStructure)localXSLTTransformParameterSpec.getStylesheet()).getNode();
    DOMUtils.appendChild(this.transformElem, localNode);
  }
}
