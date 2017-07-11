package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Control;

public final class LdapResult
{
  int msgId;
  public int status;
  String matchedDN;
  String errorMessage;
  Vector<Vector<String>> referrals = null;
  LdapReferralException refEx = null;
  Vector<LdapEntry> entries = null;
  Vector<Control> resControls = null;
  public byte[] serverCreds = null;
  String extensionId = null;
  byte[] extensionValue = null;
  
  public LdapResult() {}
  
  boolean compareToSearchResult(String paramString)
  {
    boolean bool = false;
    switch (this.status)
    {
    case 6: 
      this.status = 0;
      this.entries = new Vector(1, 1);
      BasicAttributes localBasicAttributes = new BasicAttributes(true);
      LdapEntry localLdapEntry = new LdapEntry(paramString, localBasicAttributes);
      this.entries.addElement(localLdapEntry);
      bool = true;
      break;
    case 5: 
      this.status = 0;
      this.entries = new Vector(0);
      bool = true;
      break;
    default: 
      bool = false;
    }
    return bool;
  }
}
