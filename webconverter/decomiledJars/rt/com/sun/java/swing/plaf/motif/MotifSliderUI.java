package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import sun.swing.SwingUtilities2;

public class MotifSliderUI
  extends BasicSliderUI
{
  static final Dimension PREFERRED_HORIZONTAL_SIZE = new Dimension(164, 15);
  static final Dimension PREFERRED_VERTICAL_SIZE = new Dimension(15, 164);
  static final Dimension MINIMUM_HORIZONTAL_SIZE = new Dimension(43, 15);
  static final Dimension MINIMUM_VERTICAL_SIZE = new Dimension(15, 43);
  
  public MotifSliderUI(JSlider paramJSlider)
  {
    super(paramJSlider);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifSliderUI((JSlider)paramJComponent);
  }
  
  public Dimension getPreferredHorizontalSize()
  {
    return PREFERRED_HORIZONTAL_SIZE;
  }
  
  public Dimension getPreferredVerticalSize()
  {
    return PREFERRED_VERTICAL_SIZE;
  }
  
  public Dimension getMinimumHorizontalSize()
  {
    return MINIMUM_HORIZONTAL_SIZE;
  }
  
  public Dimension getMinimumVerticalSize()
  {
    return MINIMUM_VERTICAL_SIZE;
  }
  
  protected Dimension getThumbSize()
  {
    if (this.slider.getOrientation() == 0) {
      return new Dimension(30, 15);
    }
    return new Dimension(15, 30);
  }
  
  public void paintFocus(Graphics paramGraphics) {}
  
  public void paintTrack(Graphics paramGraphics) {}
  
  public void paintThumb(Graphics paramGraphics)
  {
    Rectangle localRectangle = this.thumbRect;
    int i = localRectangle.x;
    int j = localRectangle.y;
    int k = localRectangle.width;
    int m = localRectangle.height;
    if (this.slider.isEnabled()) {
      paramGraphics.setColor(this.slider.getForeground());
    } else {
      paramGraphics.setColor(this.slider.getForeground().darker());
    }
    if (this.slider.getOrientation() == 0)
    {
      paramGraphics.translate(i, localRectangle.y - 1);
      paramGraphics.fillRect(0, 1, k, m - 1);
      paramGraphics.setColor(getHighlightColor());
      SwingUtilities2.drawHLine(paramGraphics, 0, k - 1, 1);
      SwingUtilities2.drawVLine(paramGraphics, 0, 1, m);
      SwingUtilities2.drawVLine(paramGraphics, k / 2, 2, m - 1);
      paramGraphics.setColor(getShadowColor());
      SwingUtilities2.drawHLine(paramGraphics, 0, k - 1, m);
      SwingUtilities2.drawVLine(paramGraphics, k - 1, 1, m);
      SwingUtilities2.drawVLine(paramGraphics, k / 2 - 1, 2, m);
      paramGraphics.translate(-i, -(localRectangle.y - 1));
    }
    else
    {
      paramGraphics.translate(localRectangle.x - 1, 0);
      paramGraphics.fillRect(1, j, k - 1, m);
      paramGraphics.setColor(getHighlightColor());
      SwingUtilities2.drawHLine(paramGraphics, 1, k, j);
      SwingUtilities2.drawVLine(paramGraphics, 1, j + 1, j + m - 1);
      SwingUtilities2.drawHLine(paramGraphics, 2, k - 1, j + m / 2);
      paramGraphics.setColor(getShadowColor());
      SwingUtilities2.drawHLine(paramGraphics, 2, k, j + m - 1);
      SwingUtilities2.drawVLine(paramGraphics, k, j + m - 1, j);
      SwingUtilities2.drawHLine(paramGraphics, 2, k - 1, j + m / 2 - 1);
      paramGraphics.translate(-(localRectangle.x - 1), 0);
    }
  }
}
