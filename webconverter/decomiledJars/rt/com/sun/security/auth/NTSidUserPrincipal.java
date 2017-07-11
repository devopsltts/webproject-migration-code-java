package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidUserPrincipal
  extends NTSid
{
  private static final long serialVersionUID = -5573239889517749525L;
  
  public NTSidUserPrincipal(String paramString)
  {
    super(paramString);
  }
  
  public String toString()
  {
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("NTSidUserPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { getName() };
    return localMessageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof NTSidUserPrincipal)) {
      return false;
    }
    return super.equals(paramObject);
  }
}
