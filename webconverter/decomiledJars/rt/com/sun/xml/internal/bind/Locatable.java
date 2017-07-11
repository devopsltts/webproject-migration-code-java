package com.sun.xml.internal.bind;

import org.xml.sax.Locator;

public abstract interface Locatable
{
  public abstract Locator sourceLocation();
}
