package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import javax.swing.KeyStroke;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.KeyBinding;

public class MotifTextUI
{
  static final JTextComponent.KeyBinding[] defaultBindings = { new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 2), "copy-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 1), "paste-from-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(127, 1), "cut-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(37, 1), "selection-backward"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(39, 1), "selection-forward") };
  
  public MotifTextUI() {}
  
  public static Caret createCaret()
  {
    return new MotifCaret();
  }
  
  public static class MotifCaret
    extends DefaultCaret
    implements UIResource
  {
    static final int IBeamOverhang = 2;
    
    public MotifCaret() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      super.focusGained(paramFocusEvent);
      getComponent().repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      super.focusLost(paramFocusEvent);
      getComponent().repaint();
    }
    
    protected void damage(Rectangle paramRectangle)
    {
      if (paramRectangle != null)
      {
        this.x = (paramRectangle.x - 2 - 1);
        this.y = paramRectangle.y;
        this.width = (paramRectangle.width + 4 + 3);
        this.height = paramRectangle.height;
        repaint();
      }
    }
    
    public void paint(Graphics paramGraphics)
    {
      if (isVisible()) {
        try
        {
          JTextComponent localJTextComponent = getComponent();
          Color localColor = localJTextComponent.hasFocus() ? localJTextComponent.getCaretColor() : localJTextComponent.getDisabledTextColor();
          TextUI localTextUI = localJTextComponent.getUI();
          int i = getDot();
          Rectangle localRectangle = localTextUI.modelToView(localJTextComponent, i);
          int j = localRectangle.x - 2;
          int k = localRectangle.x + 2;
          int m = localRectangle.y + 1;
          int n = localRectangle.y + localRectangle.height - 2;
          paramGraphics.setColor(localColor);
          paramGraphics.drawLine(localRectangle.x, m, localRectangle.x, n);
          paramGraphics.drawLine(j, m, k, m);
          paramGraphics.drawLine(j, n, k, n);
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
  }
}
