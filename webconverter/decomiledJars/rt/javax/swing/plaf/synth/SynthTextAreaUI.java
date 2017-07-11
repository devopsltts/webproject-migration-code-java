package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

public class SynthTextAreaUI
  extends BasicTextAreaUI
  implements SynthUI
{
  private Handler handler = new Handler(null);
  private SynthStyle style;
  
  public SynthTextAreaUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthTextAreaUI();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    updateStyle(getComponent());
    getComponent().addFocusListener(this.handler);
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(getComponent(), 1);
    getComponent().putClientProperty("caretAspectRatio", null);
    getComponent().removeFocusListener(this.handler);
    this.style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    this.style = null;
    super.uninstallDefaults();
  }
  
  private void updateStyle(JTextComponent paramJTextComponent)
  {
    SynthContext localSynthContext = getContext(paramJTextComponent, 1);
    SynthStyle localSynthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (this.style != localSynthStyle)
    {
      SynthTextFieldUI.updateStyle(paramJTextComponent, localSynthContext, getPropertyPrefix());
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, this.style, paramInt);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintTextAreaBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    super.paint(paramGraphics, getComponent());
  }
  
  protected void paintBackground(Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintTextAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JTextComponent)paramPropertyChangeEvent.getSource());
    }
    super.propertyChange(paramPropertyChangeEvent);
  }
  
  private final class Handler
    implements FocusListener
  {
    private Handler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      SynthTextAreaUI.this.getComponent().repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      SynthTextAreaUI.this.getComponent().repaint();
    }
  }
}
