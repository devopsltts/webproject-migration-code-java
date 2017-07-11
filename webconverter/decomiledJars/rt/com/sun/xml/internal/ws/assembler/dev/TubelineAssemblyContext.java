package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;

public abstract interface TubelineAssemblyContext
{
  public abstract Pipe getAdaptedTubelineHead();
  
  public abstract <T> T getImplementation(Class<T> paramClass);
  
  public abstract Tube getTubelineHead();
}
