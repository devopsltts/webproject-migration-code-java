package javax.swing;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

class DebugGraphicsFilter
  extends RGBImageFilter
{
  Color color;
  
  DebugGraphicsFilter(Color paramColor)
  {
    this.canFilterIndexColorModel = true;
    this.color = paramColor;
  }
  
  public int filterRGB(int paramInt1, int paramInt2, int paramInt3)
  {
    return this.color.getRGB() | paramInt3 & 0xFF000000;
  }
}
