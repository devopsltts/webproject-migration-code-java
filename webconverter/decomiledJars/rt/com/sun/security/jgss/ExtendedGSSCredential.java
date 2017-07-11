package com.sun.security.jgss;

import jdk.Exported;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

@Exported
public abstract interface ExtendedGSSCredential
  extends GSSCredential
{
  public abstract GSSCredential impersonate(GSSName paramGSSName)
    throws GSSException;
}
