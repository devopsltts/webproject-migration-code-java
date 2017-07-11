package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

/**
 * @deprecated
 */
public abstract class PipeCloner
  extends TubeCloner
{
  public static Pipe clone(Pipe paramPipe)
  {
    return new PipeClonerImpl().copy(paramPipe);
  }
  
  PipeCloner(Map<Object, Object> paramMap)
  {
    super(paramMap);
  }
  
  public abstract <T extends Pipe> T copy(T paramT);
  
  public abstract void add(Pipe paramPipe1, Pipe paramPipe2);
}
