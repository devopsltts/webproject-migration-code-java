package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public abstract class Graphics2D
  extends Graphics
{
  protected Graphics2D() {}
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    Paint localPaint = getPaint();
    Color localColor1 = getColor();
    Color localColor2 = localColor1.brighter();
    Color localColor3 = localColor1.darker();
    setColor(paramBoolean ? localColor2 : localColor3);
    fillRect(paramInt1, paramInt2, 1, paramInt4 + 1);
    fillRect(paramInt1 + 1, paramInt2, paramInt3 - 1, 1);
    setColor(paramBoolean ? localColor3 : localColor2);
    fillRect(paramInt1 + 1, paramInt2 + paramInt4, paramInt3, 1);
    fillRect(paramInt1 + paramInt3, paramInt2, 1, paramInt4);
    setPaint(localPaint);
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    Paint localPaint = getPaint();
    Color localColor1 = getColor();
    Color localColor2 = localColor1.brighter();
    Color localColor3 = localColor1.darker();
    if (!paramBoolean) {
      setColor(localColor3);
    } else if (localPaint != localColor1) {
      setColor(localColor1);
    }
    fillRect(paramInt1 + 1, paramInt2 + 1, paramInt3 - 2, paramInt4 - 2);
    setColor(paramBoolean ? localColor2 : localColor3);
    fillRect(paramInt1, paramInt2, 1, paramInt4);
    fillRect(paramInt1 + 1, paramInt2, paramInt3 - 2, 1);
    setColor(paramBoolean ? localColor3 : localColor2);
    fillRect(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt3 - 1, 1);
    fillRect(paramInt1 + paramInt3 - 1, paramInt2, 1, paramInt4 - 1);
    setPaint(localPaint);
  }
  
  public abstract void draw(Shape paramShape);
  
  public abstract boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver);
  
  public abstract void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2);
  
  public abstract void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform);
  
  public abstract void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform);
  
  public abstract void drawString(String paramString, int paramInt1, int paramInt2);
  
  public abstract void drawString(String paramString, float paramFloat1, float paramFloat2);
  
  public abstract void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2);
  
  public abstract void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2);
  
  public abstract void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2);
  
  public abstract void fill(Shape paramShape);
  
  public abstract boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean);
  
  public abstract GraphicsConfiguration getDeviceConfiguration();
  
  public abstract void setComposite(Composite paramComposite);
  
  public abstract void setPaint(Paint paramPaint);
  
  public abstract void setStroke(Stroke paramStroke);
  
  public abstract void setRenderingHint(RenderingHints.Key paramKey, Object paramObject);
  
  public abstract Object getRenderingHint(RenderingHints.Key paramKey);
  
  public abstract void setRenderingHints(Map<?, ?> paramMap);
  
  public abstract void addRenderingHints(Map<?, ?> paramMap);
  
  public abstract RenderingHints getRenderingHints();
  
  public abstract void translate(int paramInt1, int paramInt2);
  
  public abstract void translate(double paramDouble1, double paramDouble2);
  
  public abstract void rotate(double paramDouble);
  
  public abstract void rotate(double paramDouble1, double paramDouble2, double paramDouble3);
  
  public abstract void scale(double paramDouble1, double paramDouble2);
  
  public abstract void shear(double paramDouble1, double paramDouble2);
  
  public abstract void transform(AffineTransform paramAffineTransform);
  
  public abstract void setTransform(AffineTransform paramAffineTransform);
  
  public abstract AffineTransform getTransform();
  
  public abstract Paint getPaint();
  
  public abstract Composite getComposite();
  
  public abstract void setBackground(Color paramColor);
  
  public abstract Color getBackground();
  
  public abstract Stroke getStroke();
  
  public abstract void clip(Shape paramShape);
  
  public abstract FontRenderContext getFontRenderContext();
}
