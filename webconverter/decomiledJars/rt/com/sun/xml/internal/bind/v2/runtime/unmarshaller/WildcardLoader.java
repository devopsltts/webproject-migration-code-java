package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import javax.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

public final class WildcardLoader
  extends ProxyLoader
{
  private final DomLoader dom;
  private final WildcardMode mode;
  
  public WildcardLoader(DomHandler paramDomHandler, WildcardMode paramWildcardMode)
  {
    this.dom = new DomLoader(paramDomHandler);
    this.mode = paramWildcardMode;
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    if (this.mode.allowTypedObject)
    {
      Loader localLoader = localUnmarshallingContext.selectRootLoader(paramState, paramTagName);
      if (localLoader != null) {
        return localLoader;
      }
    }
    if (this.mode.allowDom) {
      return this.dom;
    }
    return Discarder.INSTANCE;
  }
}
