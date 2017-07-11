package com.sun.beans.decoder;

final class VoidElementHandler
  extends ObjectElementHandler
{
  VoidElementHandler() {}
  
  protected boolean isArgument()
  {
    return false;
  }
}
