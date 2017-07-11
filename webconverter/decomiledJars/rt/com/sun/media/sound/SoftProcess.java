package com.sun.media.sound;

public abstract interface SoftProcess
  extends SoftControl
{
  public abstract void init(SoftSynthesizer paramSoftSynthesizer);
  
  public abstract double[] get(int paramInt, String paramString);
  
  public abstract void processControlLogic();
  
  public abstract void reset();
}
