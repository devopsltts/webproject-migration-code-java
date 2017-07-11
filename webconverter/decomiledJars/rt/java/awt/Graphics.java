package java.awt;

import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public abstract class Graphics
{
  protected Graphics() {}
  
  public abstract Graphics create();
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Graphics localGraphics = create();
    if (localGraphics == null) {
      return null;
    }
    localGraphics.translate(paramInt1, paramInt2);
    localGraphics.clipRect(0, 0, paramInt3, paramInt4);
    return localGraphics;
  }
  
  public abstract void translate(int paramInt1, int paramInt2);
  
  public abstract Color getColor();
  
  public abstract void setColor(Color paramColor);
  
  public abstract void setPaintMode();
  
  public abstract void setXORMode(Color paramColor);
  
  public abstract Font getFont();
  
  public abstract void setFont(Font paramFont);
  
  public FontMetrics getFontMetrics()
  {
    return getFontMetrics(getFont());
  }
  
  public abstract FontMetrics getFontMetrics(Font paramFont);
  
  public abstract Rectangle getClipBounds();
  
  public abstract void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract Shape getClip();
  
  public abstract void setClip(Shape paramShape);
  
  public abstract void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      return;
    }
    if ((paramInt4 == 0) || (paramInt3 == 0))
    {
      drawLine(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
    }
    else
    {
      drawLine(paramInt1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
      drawLine(paramInt1 + paramInt3, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4 - 1);
      drawLine(paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1 + 1, paramInt2 + paramInt4);
      drawLine(paramInt1, paramInt2 + paramInt4, paramInt1, paramInt2 + 1);
    }
  }
  
  public abstract void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    Color localColor1 = getColor();
    Color localColor2 = localColor1.brighter();
    Color localColor3 = localColor1.darker();
    setColor(paramBoolean ? localColor2 : localColor3);
    drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4);
    drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
    setColor(paramBoolean ? localColor3 : localColor2);
    drawLine(paramInt1 + 1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
    drawLine(paramInt1 + paramInt3, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4 - 1);
    setColor(localColor1);
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    Color localColor1 = getColor();
    Color localColor2 = localColor1.brighter();
    Color localColor3 = localColor1.darker();
    if (!paramBoolean) {
      setColor(localColor3);
    }
    fillRect(paramInt1 + 1, paramInt2 + 1, paramInt3 - 2, paramInt4 - 2);
    setColor(paramBoolean ? localColor2 : localColor3);
    drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4 - 1);
    drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 2, paramInt2);
    setColor(paramBoolean ? localColor3 : localColor2);
    drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
    drawLine(paramInt1 + paramInt3 - 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 2);
    setColor(localColor1);
  }
  
  public abstract void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public abstract void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public void drawPolygon(Polygon paramPolygon)
  {
    drawPolygon(paramPolygon.xpoints, paramPolygon.ypoints, paramPolygon.npoints);
  }
  
  public abstract void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public void fillPolygon(Polygon paramPolygon)
  {
    fillPolygon(paramPolygon.xpoints, paramPolygon.ypoints, paramPolygon.npoints);
  }
  
  public abstract void drawString(String paramString, int paramInt1, int paramInt2);
  
  public abstract void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2);
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    drawString(new String(paramArrayOfChar, paramInt1, paramInt2), paramInt3, paramInt4);
  }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    drawString(new String(paramArrayOfByte, 0, paramInt1, paramInt2), paramInt3, paramInt4);
  }
  
  public abstract boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  public abstract boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver);
  
  public abstract boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver);
  
  public abstract boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver);
  
  public abstract boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver);
  
  public abstract boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver);
  
  public abstract void dispose();
  
  public void finalize()
  {
    dispose();
  }
  
  public String toString()
  {
    return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
  }
  
  @Deprecated
  public Rectangle getClipRect()
  {
    return getClipBounds();
  }
  
  public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle localRectangle = getClipBounds();
    if (localRectangle == null) {
      return true;
    }
    return localRectangle.intersects(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Rectangle getClipBounds(Rectangle paramRectangle)
  {
    Rectangle localRectangle = getClipBounds();
    if (localRectangle != null)
    {
      paramRectangle.x = localRectangle.x;
      paramRectangle.y = localRectangle.y;
      paramRectangle.width = localRectangle.width;
      paramRectangle.height = localRectangle.height;
    }
    else if (paramRectangle == null)
    {
      throw new NullPointerException("null rectangle parameter");
    }
    return paramRectangle;
  }
}
