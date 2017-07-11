package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

class SwatchPanel
  extends JPanel
{
  protected Color[] colors;
  protected Dimension swatchSize;
  protected Dimension numSwatches;
  protected Dimension gap;
  private int selRow;
  private int selCol;
  
  public SwatchPanel()
  {
    initValues();
    initColors();
    setToolTipText("");
    setOpaque(true);
    setBackground(Color.white);
    setFocusable(true);
    setInheritsPopupMenu(true);
    addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        SwatchPanel.this.repaint();
      }
      
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        SwatchPanel.this.repaint();
      }
    });
    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent paramAnonymousKeyEvent)
      {
        int i = paramAnonymousKeyEvent.getKeyCode();
        switch (i)
        {
        case 38: 
          if (SwatchPanel.this.selRow > 0)
          {
            SwatchPanel.access$010(SwatchPanel.this);
            SwatchPanel.this.repaint();
          }
          break;
        case 40: 
          if (SwatchPanel.this.selRow < SwatchPanel.this.numSwatches.height - 1)
          {
            SwatchPanel.access$008(SwatchPanel.this);
            SwatchPanel.this.repaint();
          }
          break;
        case 37: 
          if ((SwatchPanel.this.selCol > 0) && (SwatchPanel.this.getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$110(SwatchPanel.this);
            SwatchPanel.this.repaint();
          }
          else if ((SwatchPanel.this.selCol < SwatchPanel.this.numSwatches.width - 1) && (!SwatchPanel.this.getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$108(SwatchPanel.this);
            SwatchPanel.this.repaint();
          }
          break;
        case 39: 
          if ((SwatchPanel.this.selCol < SwatchPanel.this.numSwatches.width - 1) && (SwatchPanel.this.getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$108(SwatchPanel.this);
            SwatchPanel.this.repaint();
          }
          else if ((SwatchPanel.this.selCol > 0) && (!SwatchPanel.this.getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$110(SwatchPanel.this);
            SwatchPanel.this.repaint();
          }
          break;
        case 36: 
          SwatchPanel.this.selCol = 0;
          SwatchPanel.this.selRow = 0;
          SwatchPanel.this.repaint();
          break;
        case 35: 
          SwatchPanel.this.selCol = (SwatchPanel.this.numSwatches.width - 1);
          SwatchPanel.this.selRow = (SwatchPanel.this.numSwatches.height - 1);
          SwatchPanel.this.repaint();
        }
      }
    });
  }
  
  public Color getSelectedColor()
  {
    return getColorForCell(this.selCol, this.selRow);
  }
  
  protected void initValues() {}
  
  public void paintComponent(Graphics paramGraphics)
  {
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
    for (int i = 0; i < this.numSwatches.height; i++)
    {
      int j = i * (this.swatchSize.height + this.gap.height);
      for (int k = 0; k < this.numSwatches.width; k++)
      {
        Color localColor1 = getColorForCell(k, i);
        paramGraphics.setColor(localColor1);
        int m;
        if (!getComponentOrientation().isLeftToRight()) {
          m = (this.numSwatches.width - k - 1) * (this.swatchSize.width + this.gap.width);
        } else {
          m = k * (this.swatchSize.width + this.gap.width);
        }
        paramGraphics.fillRect(m, j, this.swatchSize.width, this.swatchSize.height);
        paramGraphics.setColor(Color.black);
        paramGraphics.drawLine(m + this.swatchSize.width - 1, j, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
        paramGraphics.drawLine(m, j + this.swatchSize.height - 1, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
        if ((this.selRow == i) && (this.selCol == k) && (isFocusOwner()))
        {
          Color localColor2 = new Color(localColor1.getRed() < 125 ? 255 : 0, localColor1.getGreen() < 125 ? 255 : 0, localColor1.getBlue() < 125 ? 255 : 0);
          paramGraphics.setColor(localColor2);
          paramGraphics.drawLine(m, j, m + this.swatchSize.width - 1, j);
          paramGraphics.drawLine(m, j, m, j + this.swatchSize.height - 1);
          paramGraphics.drawLine(m + this.swatchSize.width - 1, j, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
          paramGraphics.drawLine(m, j + this.swatchSize.height - 1, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
          paramGraphics.drawLine(m, j, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
          paramGraphics.drawLine(m, j + this.swatchSize.height - 1, m + this.swatchSize.width - 1, j);
        }
      }
    }
  }
  
  public Dimension getPreferredSize()
  {
    int i = this.numSwatches.width * (this.swatchSize.width + this.gap.width) - 1;
    int j = this.numSwatches.height * (this.swatchSize.height + this.gap.height) - 1;
    return new Dimension(i, j);
  }
  
  protected void initColors() {}
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    Color localColor = getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
    return localColor.getRed() + ", " + localColor.getGreen() + ", " + localColor.getBlue();
  }
  
  public void setSelectedColorFromLocation(int paramInt1, int paramInt2)
  {
    if (!getComponentOrientation().isLeftToRight()) {
      this.selCol = (this.numSwatches.width - paramInt1 / (this.swatchSize.width + this.gap.width) - 1);
    } else {
      this.selCol = (paramInt1 / (this.swatchSize.width + this.gap.width));
    }
    this.selRow = (paramInt2 / (this.swatchSize.height + this.gap.height));
    repaint();
  }
  
  public Color getColorForLocation(int paramInt1, int paramInt2)
  {
    int i;
    if (!getComponentOrientation().isLeftToRight()) {
      i = this.numSwatches.width - paramInt1 / (this.swatchSize.width + this.gap.width) - 1;
    } else {
      i = paramInt1 / (this.swatchSize.width + this.gap.width);
    }
    int j = paramInt2 / (this.swatchSize.height + this.gap.height);
    return getColorForCell(i, j);
  }
  
  private Color getColorForCell(int paramInt1, int paramInt2)
  {
    return this.colors[(paramInt2 * this.numSwatches.width + paramInt1)];
  }
}
