package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14N
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  
  public TransformC14N() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws CanonicalizationException
  {
    Canonicalizer20010315OmitComments localCanonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
    if (paramOutputStream != null) {
      localCanonicalizer20010315OmitComments.setWriter(paramOutputStream);
    }
    byte[] arrayOfByte = null;
    arrayOfByte = localCanonicalizer20010315OmitComments.engineCanonicalize(paramXMLSignatureInput);
    XMLSignatureInput localXMLSignatureInput = new XMLSignatureInput(arrayOfByte);
    if (paramOutputStream != null) {
      localXMLSignatureInput.setOutputStream(paramOutputStream);
    }
    return localXMLSignatureInput;
  }
}
