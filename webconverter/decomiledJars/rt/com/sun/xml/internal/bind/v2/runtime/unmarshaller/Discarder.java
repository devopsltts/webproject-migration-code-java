package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class Discarder
  extends Loader
{
  public static final Loader INSTANCE = new Discarder();
  
  private Discarder()
  {
    super(false);
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
  {
    paramState.setTarget(null);
    paramState.setLoader(this);
  }
}
