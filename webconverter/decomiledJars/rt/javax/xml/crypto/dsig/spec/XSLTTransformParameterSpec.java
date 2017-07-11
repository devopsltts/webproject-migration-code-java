package javax.xml.crypto.dsig.spec;

import javax.xml.crypto.XMLStructure;

public final class XSLTTransformParameterSpec
  implements TransformParameterSpec
{
  private XMLStructure stylesheet;
  
  public XSLTTransformParameterSpec(XMLStructure paramXMLStructure)
  {
    if (paramXMLStructure == null) {
      throw new NullPointerException();
    }
    this.stylesheet = paramXMLStructure;
  }
  
  public XMLStructure getStylesheet()
  {
    return this.stylesheet;
  }
}
