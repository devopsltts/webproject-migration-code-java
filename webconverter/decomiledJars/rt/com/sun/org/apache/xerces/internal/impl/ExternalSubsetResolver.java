package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public abstract interface ExternalSubsetResolver
  extends XMLEntityResolver
{
  public abstract XMLInputSource getExternalSubset(XMLDTDDescription paramXMLDTDDescription)
    throws XNIException, IOException;
}
