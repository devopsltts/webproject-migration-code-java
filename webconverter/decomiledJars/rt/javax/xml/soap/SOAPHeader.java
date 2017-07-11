package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;

public abstract interface SOAPHeader
  extends SOAPElement
{
  public abstract SOAPHeaderElement addHeaderElement(Name paramName)
    throws SOAPException;
  
  public abstract SOAPHeaderElement addHeaderElement(QName paramQName)
    throws SOAPException;
  
  public abstract Iterator examineMustUnderstandHeaderElements(String paramString);
  
  public abstract Iterator examineHeaderElements(String paramString);
  
  public abstract Iterator extractHeaderElements(String paramString);
  
  public abstract SOAPHeaderElement addNotUnderstoodHeaderElement(QName paramQName)
    throws SOAPException;
  
  public abstract SOAPHeaderElement addUpgradeHeaderElement(Iterator paramIterator)
    throws SOAPException;
  
  public abstract SOAPHeaderElement addUpgradeHeaderElement(String[] paramArrayOfString)
    throws SOAPException;
  
  public abstract SOAPHeaderElement addUpgradeHeaderElement(String paramString)
    throws SOAPException;
  
  public abstract Iterator examineAllHeaderElements();
  
  public abstract Iterator extractAllHeaderElements();
}
