package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class CompoundBorder
  extends AbstractBorder
{
  protected Border outsideBorder;
  protected Border insideBorder;
  
  public CompoundBorder()
  {
    this.outsideBorder = null;
    this.insideBorder = null;
  }
  
  @ConstructorProperties({"outsideBorder", "insideBorder"})
  public CompoundBorder(Border paramBorder1, Border paramBorder2)
  {
    this.outsideBorder = paramBorder1;
    this.insideBorder = paramBorder2;
  }
  
  public boolean isBorderOpaque()
  {
    return ((this.outsideBorder == null) || (this.outsideBorder.isBorderOpaque())) && ((this.insideBorder == null) || (this.insideBorder.isBorderOpaque()));
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt1;
    int j = paramInt2;
    int k = paramInt3;
    int m = paramInt4;
    if (this.outsideBorder != null)
    {
      this.outsideBorder.paintBorder(paramComponent, paramGraphics, i, j, k, m);
      Insets localInsets = this.outsideBorder.getBorderInsets(paramComponent);
      i += localInsets.left;
      j += localInsets.top;
      k = k - localInsets.right - localInsets.left;
      m = m - localInsets.bottom - localInsets.top;
    }
    if (this.insideBorder != null) {
      this.insideBorder.paintBorder(paramComponent, paramGraphics, i, j, k, m);
    }
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    paramInsets.top = (paramInsets.left = paramInsets.right = paramInsets.bottom = 0);
    Insets localInsets;
    if (this.outsideBorder != null)
    {
      localInsets = this.outsideBorder.getBorderInsets(paramComponent);
      paramInsets.top += localInsets.top;
      paramInsets.left += localInsets.left;
      paramInsets.right += localInsets.right;
      paramInsets.bottom += localInsets.bottom;
    }
    if (this.insideBorder != null)
    {
      localInsets = this.insideBorder.getBorderInsets(paramComponent);
      paramInsets.top += localInsets.top;
      paramInsets.left += localInsets.left;
      paramInsets.right += localInsets.right;
      paramInsets.bottom += localInsets.bottom;
    }
    return paramInsets;
  }
  
  public Border getOutsideBorder()
  {
    return this.outsideBorder;
  }
  
  public Border getInsideBorder()
  {
    return this.insideBorder;
  }
}
