package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class MetalComboBoxEditor
  extends BasicComboBoxEditor
{
  protected static Insets editorBorderInsets = new Insets(2, 2, 2, 0);
  
  public MetalComboBoxEditor()
  {
    this.editor = new JTextField("", 9)
    {
      public void setText(String paramAnonymousString)
      {
        if (getText().equals(paramAnonymousString)) {
          return;
        }
        super.setText(paramAnonymousString);
      }
      
      public Dimension getPreferredSize()
      {
        Dimension localDimension = super.getPreferredSize();
        localDimension.height += 4;
        return localDimension;
      }
      
      public Dimension getMinimumSize()
      {
        Dimension localDimension = super.getMinimumSize();
        localDimension.height += 4;
        return localDimension;
      }
    };
    this.editor.setBorder(new EditorBorder());
  }
  
  class EditorBorder
    extends AbstractBorder
  {
    EditorBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      if (MetalLookAndFeel.usingOcean())
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        paramGraphics.drawRect(0, 0, paramInt3, paramInt4 - 1);
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        paramGraphics.drawRect(1, 1, paramInt3 - 2, paramInt4 - 3);
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        paramGraphics.drawLine(0, 0, paramInt3 - 1, 0);
        paramGraphics.drawLine(0, 0, 0, paramInt4 - 2);
        paramGraphics.drawLine(0, paramInt4 - 2, paramInt3 - 1, paramInt4 - 2);
        paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
        paramGraphics.drawLine(1, 1, paramInt3 - 1, 1);
        paramGraphics.drawLine(1, 1, 1, paramInt4 - 1);
        paramGraphics.drawLine(1, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
        paramGraphics.setColor(MetalLookAndFeel.getControl());
        paramGraphics.drawLine(1, paramInt4 - 2, 1, paramInt4 - 2);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 2, 0);
      return paramInsets;
    }
  }
  
  public static class UIResource
    extends MetalComboBoxEditor
    implements UIResource
  {
    public UIResource() {}
  }
}
