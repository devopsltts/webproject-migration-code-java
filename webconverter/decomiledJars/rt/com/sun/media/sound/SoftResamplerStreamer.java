package com.sun.media.sound;

import java.io.IOException;

public abstract interface SoftResamplerStreamer
  extends ModelOscillatorStream
{
  public abstract void open(ModelWavetable paramModelWavetable, float paramFloat)
    throws IOException;
}
