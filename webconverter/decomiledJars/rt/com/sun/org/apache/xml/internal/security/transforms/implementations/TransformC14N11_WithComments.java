package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14N11_WithComments
  extends TransformSpi
{
  public TransformC14N11_WithComments() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2006/12/xml-c14n11#WithComments";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws CanonicalizationException
  {
    Canonicalizer11_WithComments localCanonicalizer11_WithComments = new Canonicalizer11_WithComments();
    if (paramOutputStream != null) {
      localCanonicalizer11_WithComments.setWriter(paramOutputStream);
    }
    byte[] arrayOfByte = null;
    arrayOfByte = localCanonicalizer11_WithComments.engineCanonicalize(paramXMLSignatureInput);
    XMLSignatureInput localXMLSignatureInput = new XMLSignatureInput(arrayOfByte);
    if (paramOutputStream != null) {
      localXMLSignatureInput.setOutputStream(paramOutputStream);
    }
    return localXMLSignatureInput;
  }
}
