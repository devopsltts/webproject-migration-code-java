package javax.xml.soap;

import javax.xml.transform.dom.DOMResult;

public class SAAJResult
  extends DOMResult
{
  public SAAJResult()
    throws SOAPException
  {
    this(MessageFactory.newInstance().createMessage());
  }
  
  public SAAJResult(String paramString)
    throws SOAPException
  {
    this(MessageFactory.newInstance(paramString).createMessage());
  }
  
  public SAAJResult(SOAPMessage paramSOAPMessage)
  {
    super(paramSOAPMessage.getSOAPPart());
  }
  
  public SAAJResult(SOAPElement paramSOAPElement)
  {
    super(paramSOAPElement);
  }
  
  public Node getResult()
  {
    return (Node)super.getNode().getFirstChild();
  }
}
