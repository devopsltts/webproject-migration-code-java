package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public final class DefaultValueLoaderDecorator
  extends Loader
{
  private final Loader l;
  private final String defaultValue;
  
  public DefaultValueLoaderDecorator(Loader paramLoader, String paramString)
  {
    this.l = paramLoader;
    this.defaultValue = paramString;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    if (paramState.getElementDefaultValue() == null) {
      paramState.setElementDefaultValue(this.defaultValue);
    }
    paramState.setLoader(this.l);
    this.l.startElement(paramState, paramTagName);
  }
}
