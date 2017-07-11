package com.sun.xml.internal.bind;

import javax.xml.bind.ValidationEventLocator;

public abstract interface ValidationEventLocatorEx
  extends ValidationEventLocator
{
  public abstract String getFieldName();
}
