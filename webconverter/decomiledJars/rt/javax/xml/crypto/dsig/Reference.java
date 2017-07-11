package javax.xml.crypto.dsig;

import java.io.InputStream;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLStructure;

public abstract interface Reference
  extends URIReference, XMLStructure
{
  public abstract List getTransforms();
  
  public abstract DigestMethod getDigestMethod();
  
  public abstract String getId();
  
  public abstract byte[] getDigestValue();
  
  public abstract byte[] getCalculatedDigestValue();
  
  public abstract boolean validate(XMLValidateContext paramXMLValidateContext)
    throws XMLSignatureException;
  
  public abstract Data getDereferencedData();
  
  public abstract InputStream getDigestInputStream();
}
