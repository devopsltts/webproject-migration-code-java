package javax.xml.crypto.dsig;

import java.io.InputStream;
import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface SignedInfo
  extends XMLStructure
{
  public abstract CanonicalizationMethod getCanonicalizationMethod();
  
  public abstract SignatureMethod getSignatureMethod();
  
  public abstract List getReferences();
  
  public abstract String getId();
  
  public abstract InputStream getCanonicalizedData();
}
