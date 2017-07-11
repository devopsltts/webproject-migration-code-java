package sun.awt.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class OffScreenImage
  extends BufferedImage
{
  protected Component c;
  private OffScreenImageSource osis;
  private Font defaultFont;
  
  public OffScreenImage(Component paramComponent, ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean)
  {
    super(paramColorModel, paramWritableRaster, paramBoolean, null);
    this.c = paramComponent;
    initSurface(paramWritableRaster.getWidth(), paramWritableRaster.getHeight());
  }
  
  public Graphics getGraphics()
  {
    return createGraphics();
  }
  
  public Graphics2D createGraphics()
  {
    if (this.c == null)
    {
      localObject1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return ((GraphicsEnvironment)localObject1).createGraphics(this);
    }
    Object localObject1 = this.c.getBackground();
    if (localObject1 == null) {
      localObject1 = SystemColor.window;
    }
    Object localObject2 = this.c.getForeground();
    if (localObject2 == null) {
      localObject2 = SystemColor.windowText;
    }
    Font localFont = this.c.getFont();
    if (localFont == null)
    {
      if (this.defaultFont == null) {
        this.defaultFont = new Font("Dialog", 0, 12);
      }
      localFont = this.defaultFont;
    }
    return new SunGraphics2D(SurfaceData.getPrimarySurfaceData(this), (Color)localObject2, (Color)localObject1, localFont);
  }
  
  private void initSurface(int paramInt1, int paramInt2)
  {
    Graphics2D localGraphics2D = createGraphics();
    try
    {
      localGraphics2D.clearRect(0, 0, paramInt1, paramInt2);
    }
    finally
    {
      localGraphics2D.dispose();
    }
  }
  
  public ImageProducer getSource()
  {
    if (this.osis == null) {
      this.osis = new OffScreenImageSource(this);
    }
    return this.osis;
  }
}
