package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;

public abstract class LayeredHighlighter
  implements Highlighter
{
  public LayeredHighlighter() {}
  
  public abstract void paintLayeredHighlights(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView);
  
  public static abstract class LayerPainter
    implements Highlighter.HighlightPainter
  {
    public LayerPainter() {}
    
    public abstract Shape paintLayer(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView);
  }
}
