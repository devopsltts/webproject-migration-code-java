package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter.LayerPainter;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public abstract class WindowsTextUI
  extends BasicTextUI
{
  static LayeredHighlighter.LayerPainter WindowsPainter = new WindowsHighlightPainter(null);
  
  public WindowsTextUI() {}
  
  protected Caret createCaret()
  {
    return new WindowsCaret();
  }
  
  static class WindowsCaret
    extends DefaultCaret
    implements UIResource
  {
    WindowsCaret() {}
    
    protected Highlighter.HighlightPainter getSelectionPainter()
    {
      return WindowsTextUI.WindowsPainter;
    }
  }
  
  static class WindowsHighlightPainter
    extends DefaultHighlighter.DefaultHighlightPainter
  {
    WindowsHighlightPainter(Color paramColor)
    {
      super();
    }
    
    public void paint(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent)
    {
      Rectangle localRectangle1 = paramShape.getBounds();
      try
      {
        TextUI localTextUI = paramJTextComponent.getUI();
        Rectangle localRectangle2 = localTextUI.modelToView(paramJTextComponent, paramInt1);
        Rectangle localRectangle3 = localTextUI.modelToView(paramJTextComponent, paramInt2);
        Color localColor = getColor();
        if (localColor == null) {
          paramGraphics.setColor(paramJTextComponent.getSelectionColor());
        } else {
          paramGraphics.setColor(localColor);
        }
        int i = 0;
        int j = 0;
        if (paramJTextComponent.isEditable())
        {
          int k = paramJTextComponent.getCaretPosition();
          i = paramInt1 == k ? 1 : 0;
          j = paramInt2 == k ? 1 : 0;
        }
        if (localRectangle2.y == localRectangle3.y)
        {
          Rectangle localRectangle4 = localRectangle2.union(localRectangle3);
          if (localRectangle4.width > 0) {
            if (i != 0)
            {
              localRectangle4.x += 1;
              localRectangle4.width -= 1;
            }
            else if (j != 0)
            {
              localRectangle4.width -= 1;
            }
          }
          paramGraphics.fillRect(localRectangle4.x, localRectangle4.y, localRectangle4.width, localRectangle4.height);
        }
        else
        {
          int m = localRectangle1.x + localRectangle1.width - localRectangle2.x;
          if ((i != 0) && (m > 0))
          {
            localRectangle2.x += 1;
            m--;
          }
          paramGraphics.fillRect(localRectangle2.x, localRectangle2.y, m, localRectangle2.height);
          if (localRectangle2.y + localRectangle2.height != localRectangle3.y) {
            paramGraphics.fillRect(localRectangle1.x, localRectangle2.y + localRectangle2.height, localRectangle1.width, localRectangle3.y - (localRectangle2.y + localRectangle2.height));
          }
          if ((j != 0) && (localRectangle3.x > localRectangle1.x)) {
            localRectangle3.x -= 1;
          }
          paramGraphics.fillRect(localRectangle1.x, localRectangle3.y, localRectangle3.x - localRectangle1.x, localRectangle3.height);
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
    
    public Shape paintLayer(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView)
    {
      Color localColor = getColor();
      if (localColor == null) {
        paramGraphics.setColor(paramJTextComponent.getSelectionColor());
      } else {
        paramGraphics.setColor(localColor);
      }
      int i = 0;
      int j = 0;
      if (paramJTextComponent.isEditable())
      {
        int k = paramJTextComponent.getCaretPosition();
        i = paramInt1 == k ? 1 : 0;
        j = paramInt2 == k ? 1 : 0;
      }
      Object localObject;
      if ((paramInt1 == paramView.getStartOffset()) && (paramInt2 == paramView.getEndOffset()))
      {
        if ((paramShape instanceof Rectangle)) {
          localObject = (Rectangle)paramShape;
        } else {
          localObject = paramShape.getBounds();
        }
        if ((i != 0) && (((Rectangle)localObject).width > 0)) {
          paramGraphics.fillRect(((Rectangle)localObject).x + 1, ((Rectangle)localObject).y, ((Rectangle)localObject).width - 1, ((Rectangle)localObject).height);
        } else if ((j != 0) && (((Rectangle)localObject).width > 0)) {
          paramGraphics.fillRect(((Rectangle)localObject).x, ((Rectangle)localObject).y, ((Rectangle)localObject).width - 1, ((Rectangle)localObject).height);
        } else {
          paramGraphics.fillRect(((Rectangle)localObject).x, ((Rectangle)localObject).y, ((Rectangle)localObject).width, ((Rectangle)localObject).height);
        }
        return localObject;
      }
      try
      {
        localObject = paramView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, paramShape);
        Rectangle localRectangle = (localObject instanceof Rectangle) ? (Rectangle)localObject : ((Shape)localObject).getBounds();
        if ((i != 0) && (localRectangle.width > 0)) {
          paramGraphics.fillRect(localRectangle.x + 1, localRectangle.y, localRectangle.width - 1, localRectangle.height);
        } else if ((j != 0) && (localRectangle.width > 0)) {
          paramGraphics.fillRect(localRectangle.x, localRectangle.y, localRectangle.width - 1, localRectangle.height);
        } else {
          paramGraphics.fillRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
        }
        return localRectangle;
      }
      catch (BadLocationException localBadLocationException) {}
      return null;
    }
  }
}
