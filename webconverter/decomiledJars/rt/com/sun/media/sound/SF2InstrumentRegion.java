package com.sun.media.sound;

public final class SF2InstrumentRegion
  extends SF2Region
{
  SF2Layer layer;
  
  public SF2InstrumentRegion() {}
  
  public SF2Layer getLayer()
  {
    return this.layer;
  }
  
  public void setLayer(SF2Layer paramSF2Layer)
  {
    this.layer = paramSF2Layer;
  }
}
