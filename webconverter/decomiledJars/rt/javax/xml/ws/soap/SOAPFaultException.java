package javax.xml.ws.soap;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;

public class SOAPFaultException
  extends ProtocolException
{
  private SOAPFault fault;
  
  public SOAPFaultException(SOAPFault paramSOAPFault)
  {
    super(paramSOAPFault.getFaultString());
    this.fault = paramSOAPFault;
  }
  
  public SOAPFault getFault()
  {
    return this.fault;
  }
}
