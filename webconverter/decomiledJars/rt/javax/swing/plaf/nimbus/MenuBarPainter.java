package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;
import javax.swing.JComponent;

final class MenuBarPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int BORDER_ENABLED = 2;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  private Color color2 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.10255819F, 0.23921567F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.10654225F, 0.23921567F, -29);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 65281);
  private Color color5 = decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
  private Object[] componentColors;
  
  public MenuBarPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
  {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    this.componentColors = paramArrayOfObject;
    switch (this.state)
    {
    case 1: 
      paintBackgroundEnabled(paramGraphics2D);
      break;
    case 2: 
      paintBorderEnabled(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return this.ctx;
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBorderEnabled(Graphics2D paramGraphics2D)
  {
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1()
  {
    this.rect.setRect(decodeX(1.0F), decodeY(0.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(1.9523809F) - decodeY(0.0F));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    this.rect.setRect(decodeX(1.0F), decodeY(0.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(0.0F));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3()
  {
    this.rect.setRect(decodeX(1.0F), decodeY(2.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(3.0F) - decodeY(2.0F));
    return this.rect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(1.0F * f3 + f1, 0.0F * f4 + f2, 1.0F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.015F, 0.03F, 0.23354445F, 0.7569444F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4 });
  }
}
