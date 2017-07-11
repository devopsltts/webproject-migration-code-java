package com.sun.xml.internal.messaging.saaj.soap;

public abstract interface SOAPDocument
{
  public abstract SOAPPartImpl getSOAPPart();
  
  public abstract SOAPDocumentImpl getDocument();
}
