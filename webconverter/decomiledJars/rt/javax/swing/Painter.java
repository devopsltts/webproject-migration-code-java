package javax.swing;

import java.awt.Graphics2D;

public abstract interface Painter<T>
{
  public abstract void paint(Graphics2D paramGraphics2D, T paramT, int paramInt1, int paramInt2);
}
