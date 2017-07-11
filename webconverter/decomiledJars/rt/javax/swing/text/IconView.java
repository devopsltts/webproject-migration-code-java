package javax.swing.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.Icon;

public class IconView
  extends View
{
  private Icon c;
  
  public IconView(Element paramElement)
  {
    super(paramElement);
    AttributeSet localAttributeSet = paramElement.getAttributes();
    this.c = StyleConstants.getIcon(localAttributeSet);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = paramShape.getBounds();
    this.c.paintIcon(getContainer(), paramGraphics, localRectangle.x, localRectangle.y);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return this.c.getIconWidth();
    case 1: 
      return this.c.getIconHeight();
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return 1.0F;
    }
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    int i = getStartOffset();
    int j = getEndOffset();
    if ((paramInt >= i) && (paramInt <= j))
    {
      Rectangle localRectangle = paramShape.getBounds();
      if (paramInt == j) {
        localRectangle.x += localRectangle.width;
      }
      localRectangle.width = 0;
      return localRectangle;
    }
    throw new BadLocationException(paramInt + " not in range " + i + "," + j, paramInt);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    if (paramFloat1 < localRectangle.x + localRectangle.width / 2)
    {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    }
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
}
