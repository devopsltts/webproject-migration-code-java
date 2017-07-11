package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;

public class SynthViewportUI
  extends ViewportUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthViewportUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthViewportUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    installDefaults(paramJComponent);
    installListeners(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    uninstallListeners(paramJComponent);
    uninstallDefaults(paramJComponent);
  }
  
  protected void installDefaults(JComponent paramJComponent)
  {
    updateStyle(paramJComponent);
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    SynthStyle localSynthStyle1 = SynthLookAndFeel.getStyle(localSynthContext.getComponent(), localSynthContext.getRegion());
    SynthStyle localSynthStyle2 = localSynthContext.getStyle();
    if (localSynthStyle1 != localSynthStyle2)
    {
      if (localSynthStyle2 != null) {
        localSynthStyle2.uninstallDefaults(localSynthContext);
      }
      localSynthContext.setStyle(localSynthStyle1);
      localSynthStyle1.installDefaults(localSynthContext);
    }
    this.style = localSynthStyle1;
    localSynthContext.dispose();
  }
  
  protected void installListeners(JComponent paramJComponent)
  {
    paramJComponent.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(this);
  }
  
  protected void uninstallDefaults(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    this.style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, this.style, paramInt);
  }
  
  private Region getRegion(JComponent paramJComponent)
  {
    return SynthLookAndFeel.getRegion(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintViewportBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JComponent)paramPropertyChangeEvent.getSource());
    }
  }
}
