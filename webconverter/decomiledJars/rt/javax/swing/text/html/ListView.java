package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.Element;

public class ListView
  extends BlockView
{
  private StyleSheet.ListPainter listPainter;
  
  public ListView(Element paramElement)
  {
    super(paramElement, 1);
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 0.5F;
    case 1: 
      return 0.5F;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    super.paint(paramGraphics, paramShape);
    Rectangle localRectangle1 = paramShape.getBounds();
    Rectangle localRectangle2 = paramGraphics.getClipBounds();
    if (localRectangle2.x + localRectangle2.width < localRectangle1.x + getLeftInset())
    {
      Rectangle localRectangle3 = localRectangle1;
      localRectangle1 = getInsideAllocation(paramShape);
      int i = getViewCount();
      int j = localRectangle2.y + localRectangle2.height;
      for (int k = 0; k < i; k++)
      {
        localRectangle3.setBounds(localRectangle1);
        childAllocation(k, localRectangle3);
        if (localRectangle3.y >= j) {
          break;
        }
        if (localRectangle3.y + localRectangle3.height >= localRectangle2.y) {
          this.listPainter.paint(paramGraphics, localRectangle3.x, localRectangle3.y, localRectangle3.width, localRectangle3.height, this, k);
        }
      }
    }
  }
  
  protected void paintChild(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    this.listPainter.paint(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this, paramInt);
    super.paintChild(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void setPropertiesFromAttributes()
  {
    super.setPropertiesFromAttributes();
    this.listPainter = getStyleSheet().getListPainter(getAttributes());
  }
}
