package javax.xml.crypto;

import java.security.Key;

public abstract interface KeySelectorResult
{
  public abstract Key getKey();
}
