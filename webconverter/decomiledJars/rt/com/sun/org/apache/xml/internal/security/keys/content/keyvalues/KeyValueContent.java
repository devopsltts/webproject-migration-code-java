package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.security.PublicKey;

public abstract interface KeyValueContent
{
  public abstract PublicKey getPublicKey()
    throws XMLSecurityException;
}
