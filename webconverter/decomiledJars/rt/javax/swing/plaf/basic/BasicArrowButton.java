package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class BasicArrowButton
  extends JButton
  implements SwingConstants
{
  protected int direction;
  private Color shadow;
  private Color darkShadow;
  private Color highlight;
  
  public BasicArrowButton(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
  {
    setRequestFocusEnabled(false);
    setDirection(paramInt);
    setBackground(paramColor1);
    this.shadow = paramColor2;
    this.darkShadow = paramColor3;
    this.highlight = paramColor4;
  }
  
  public BasicArrowButton(int paramInt)
  {
    this(paramInt, UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"));
  }
  
  public int getDirection()
  {
    return this.direction;
  }
  
  public void setDirection(int paramInt)
  {
    this.direction = paramInt;
  }
  
  public void paint(Graphics paramGraphics)
  {
    int i = getSize().width;
    int j = getSize().height;
    Color localColor = paramGraphics.getColor();
    boolean bool1 = getModel().isPressed();
    boolean bool2 = isEnabled();
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(1, 1, i - 2, j - 2);
    if ((getBorder() != null) && (!(getBorder() instanceof UIResource)))
    {
      paintBorder(paramGraphics);
    }
    else if (bool1)
    {
      paramGraphics.setColor(this.shadow);
      paramGraphics.drawRect(0, 0, i - 1, j - 1);
    }
    else
    {
      paramGraphics.drawLine(0, 0, 0, j - 1);
      paramGraphics.drawLine(1, 0, i - 2, 0);
      paramGraphics.setColor(this.highlight);
      paramGraphics.drawLine(1, 1, 1, j - 3);
      paramGraphics.drawLine(2, 1, i - 3, 1);
      paramGraphics.setColor(this.shadow);
      paramGraphics.drawLine(1, j - 2, i - 2, j - 2);
      paramGraphics.drawLine(i - 2, 1, i - 2, j - 3);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(0, j - 1, i - 1, j - 1);
      paramGraphics.drawLine(i - 1, j - 1, i - 1, 0);
    }
    if ((j < 5) || (i < 5))
    {
      paramGraphics.setColor(localColor);
      return;
    }
    if (bool1) {
      paramGraphics.translate(1, 1);
    }
    int k = Math.min((j - 4) / 3, (i - 4) / 3);
    k = Math.max(k, 2);
    paintTriangle(paramGraphics, (i - k) / 2, (j - k) / 2, k, this.direction, bool2);
    if (bool1) {
      paramGraphics.translate(-1, -1);
    }
    paramGraphics.setColor(localColor);
  }
  
  public Dimension getPreferredSize()
  {
    return new Dimension(16, 16);
  }
  
  public Dimension getMinimumSize()
  {
    return new Dimension(5, 5);
  }
  
  public Dimension getMaximumSize()
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public boolean isFocusTraversable()
  {
    return false;
  }
  
  public void paintTriangle(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    Color localColor = paramGraphics.getColor();
    int k = 0;
    paramInt3 = Math.max(paramInt3, 2);
    int i = paramInt3 / 2 - 1;
    paramGraphics.translate(paramInt1, paramInt2);
    if (paramBoolean) {
      paramGraphics.setColor(this.darkShadow);
    } else {
      paramGraphics.setColor(this.shadow);
    }
    int j;
    switch (paramInt4)
    {
    case 1: 
      for (j = 0; j < paramInt3; j++) {
        paramGraphics.drawLine(i - j, j, i + j, j);
      }
      if (!paramBoolean)
      {
        paramGraphics.setColor(this.highlight);
        paramGraphics.drawLine(i - j + 2, j, i + j, j);
      }
      break;
    case 5: 
      if (!paramBoolean)
      {
        paramGraphics.translate(1, 1);
        paramGraphics.setColor(this.highlight);
        for (j = paramInt3 - 1; j >= 0; j--)
        {
          paramGraphics.drawLine(i - j, k, i + j, k);
          k++;
        }
        paramGraphics.translate(-1, -1);
        paramGraphics.setColor(this.shadow);
      }
      k = 0;
      j = paramInt3 - 1;
    case 7: 
    case 3: 
      while (j >= 0)
      {
        paramGraphics.drawLine(i - j, k, i + j, k);
        k++;
        j--;
        continue;
        for (j = 0; j < paramInt3; j++) {
          paramGraphics.drawLine(j, i - j, j, i + j);
        }
        if (!paramBoolean)
        {
          paramGraphics.setColor(this.highlight);
          paramGraphics.drawLine(j, i - j + 2, j, i + j);
          break;
          if (!paramBoolean)
          {
            paramGraphics.translate(1, 1);
            paramGraphics.setColor(this.highlight);
            for (j = paramInt3 - 1; j >= 0; j--)
            {
              paramGraphics.drawLine(k, i - j, k, i + j);
              k++;
            }
            paramGraphics.translate(-1, -1);
            paramGraphics.setColor(this.shadow);
          }
          k = 0;
          for (j = paramInt3 - 1; j >= 0; j--)
          {
            paramGraphics.drawLine(k, i - j, k, i + j);
            k++;
          }
        }
      }
    }
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(localColor);
  }
}
