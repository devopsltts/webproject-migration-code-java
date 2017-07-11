package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

class BumpBuffer
{
  static final int IMAGE_SIZE = 64;
  transient Image image;
  Color topColor;
  Color shadowColor;
  Color backColor;
  private GraphicsConfiguration gc;
  
  public BumpBuffer(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3)
  {
    this.gc = paramGraphicsConfiguration;
    this.topColor = paramColor1;
    this.shadowColor = paramColor2;
    this.backColor = paramColor3;
    createImage();
    fillBumpBuffer();
  }
  
  public boolean hasSameConfiguration(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3)
  {
    if (this.gc != null)
    {
      if (!this.gc.equals(paramGraphicsConfiguration)) {
        return false;
      }
    }
    else if (paramGraphicsConfiguration != null) {
      return false;
    }
    return (this.topColor.equals(paramColor1)) && (this.shadowColor.equals(paramColor2)) && (this.backColor.equals(paramColor3));
  }
  
  public Image getImage()
  {
    return this.image;
  }
  
  private void fillBumpBuffer()
  {
    Graphics localGraphics = this.image.getGraphics();
    localGraphics.setColor(this.backColor);
    localGraphics.fillRect(0, 0, 64, 64);
    localGraphics.setColor(this.topColor);
    int j;
    for (int i = 0; i < 64; i += 4) {
      for (j = 0; j < 64; j += 4)
      {
        localGraphics.drawLine(i, j, i, j);
        localGraphics.drawLine(i + 2, j + 2, i + 2, j + 2);
      }
    }
    localGraphics.setColor(this.shadowColor);
    for (i = 0; i < 64; i += 4) {
      for (j = 0; j < 64; j += 4)
      {
        localGraphics.drawLine(i + 1, j + 1, i + 1, j + 1);
        localGraphics.drawLine(i + 3, j + 3, i + 3, j + 3);
      }
    }
    localGraphics.dispose();
  }
  
  private void createImage()
  {
    if (this.gc != null)
    {
      this.image = this.gc.createCompatibleImage(64, 64, this.backColor != MetalBumps.ALPHA ? 1 : 2);
    }
    else
    {
      int[] arrayOfInt = { this.backColor.getRGB(), this.topColor.getRGB(), this.shadowColor.getRGB() };
      IndexColorModel localIndexColorModel = new IndexColorModel(8, 3, arrayOfInt, 0, false, this.backColor == MetalBumps.ALPHA ? 0 : -1, 0);
      this.image = new BufferedImage(64, 64, 13, localIndexColorModel);
    }
  }
}
