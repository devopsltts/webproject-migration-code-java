package com.sun.corba.se.impl.encoding;

abstract interface RestorableInputStream
{
  public abstract Object createStreamMemento();
  
  public abstract void restoreInternalState(Object paramObject);
}
