package com.oracle.webservices.internal.api.message;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public abstract interface MessageContext
  extends DistributedPropertySet
{
  public abstract SOAPMessage getAsSOAPMessage()
    throws SOAPException;
  
  /**
   * @deprecated
   */
  public abstract SOAPMessage getSOAPMessage()
    throws SOAPException;
  
  public abstract ContentType writeTo(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract ContentType getContentType();
}
