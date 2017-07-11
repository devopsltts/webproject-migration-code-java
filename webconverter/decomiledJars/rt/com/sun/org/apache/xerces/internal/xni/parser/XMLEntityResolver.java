package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLEntityResolver
{
  public abstract XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws XNIException, IOException;
}
