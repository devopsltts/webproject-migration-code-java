package com.sun.xml.internal.ws.addressing.model;

import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class InvalidAddressingHeaderException
  extends WebServiceException
{
  private QName problemHeader;
  private QName subsubcode;
  
  public InvalidAddressingHeaderException(QName paramQName1, QName paramQName2)
  {
    super(AddressingMessages.INVALID_ADDRESSING_HEADER_EXCEPTION(paramQName1, paramQName2));
    this.problemHeader = paramQName1;
    this.subsubcode = paramQName2;
  }
  
  public QName getProblemHeader()
  {
    return this.problemHeader;
  }
  
  public QName getSubsubcode()
  {
    return this.subsubcode;
  }
}
