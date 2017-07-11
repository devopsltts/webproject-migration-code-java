package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;

public class SynthLabelUI
  extends BasicLabelUI
  implements SynthUI
{
  private SynthStyle style;
  
  public SynthLabelUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthLabelUI();
  }
  
  protected void installDefaults(JLabel paramJLabel)
  {
    updateStyle(paramJLabel);
  }
  
  void updateStyle(JLabel paramJLabel)
  {
    SynthContext localSynthContext = getContext(paramJLabel, 1);
    this.style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults(JLabel paramJLabel)
  {
    SynthContext localSynthContext = getContext(paramJLabel, 1);
    this.style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, this.style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if ((SynthLookAndFeel.getSelectedUI() == this) && (i == 1)) {
      i = SynthLookAndFeel.getSelectedUIState() | 0x1;
    }
    return i;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("Component must be non-null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Width and height must be >= 0");
    }
    JLabel localJLabel = (JLabel)paramJComponent;
    String str = localJLabel.getText();
    if ((str == null) || ("".equals(str))) {
      return -1;
    }
    Insets localInsets = localJLabel.getInsets();
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    localRectangle1.x = localInsets.left;
    localRectangle1.y = localInsets.top;
    localRectangle1.width = (paramInt1 - (localInsets.right + localRectangle1.x));
    localRectangle1.height = (paramInt2 - (localInsets.bottom + localRectangle1.y));
    SynthContext localSynthContext = getContext(localJLabel);
    FontMetrics localFontMetrics = localSynthContext.getComponent().getFontMetrics(localSynthContext.getStyle().getFont(localSynthContext));
    localSynthContext.getStyle().getGraphicsUtils(localSynthContext).layoutText(localSynthContext, localFontMetrics, localJLabel.getText(), localJLabel.getIcon(), localJLabel.getHorizontalAlignment(), localJLabel.getVerticalAlignment(), localJLabel.getHorizontalTextPosition(), localJLabel.getVerticalTextPosition(), localRectangle1, localRectangle3, localRectangle2, localJLabel.getIconTextGap());
    View localView = (View)localJLabel.getClientProperty("html");
    int i;
    if (localView != null)
    {
      i = BasicHTML.getHTMLBaseline(localView, localRectangle2.width, localRectangle2.height);
      if (i >= 0) {
        i += localRectangle2.y;
      }
    }
    else
    {
      i = localRectangle2.y + localFontMetrics.getAscent();
    }
    localSynthContext.dispose();
    return i;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintLabelBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    JLabel localJLabel = (JLabel)paramSynthContext.getComponent();
    Icon localIcon = localJLabel.isEnabled() ? localJLabel.getIcon() : localJLabel.getDisabledIcon();
    paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
    paramGraphics.setFont(this.style.getFont(paramSynthContext));
    paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, localJLabel.getText(), localIcon, localJLabel.getHorizontalAlignment(), localJLabel.getVerticalAlignment(), localJLabel.getHorizontalTextPosition(), localJLabel.getVerticalTextPosition(), localJLabel.getIconTextGap(), localJLabel.getDisplayedMnemonicIndex(), 0);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintLabelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    JLabel localJLabel = (JLabel)paramJComponent;
    Icon localIcon = localJLabel.isEnabled() ? localJLabel.getIcon() : localJLabel.getDisabledIcon();
    SynthContext localSynthContext = getContext(paramJComponent);
    Dimension localDimension = localSynthContext.getStyle().getGraphicsUtils(localSynthContext).getPreferredSize(localSynthContext, localSynthContext.getStyle().getFont(localSynthContext), localJLabel.getText(), localIcon, localJLabel.getHorizontalAlignment(), localJLabel.getVerticalAlignment(), localJLabel.getHorizontalTextPosition(), localJLabel.getVerticalTextPosition(), localJLabel.getIconTextGap(), localJLabel.getDisplayedMnemonicIndex());
    localSynthContext.dispose();
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    JLabel localJLabel = (JLabel)paramJComponent;
    Icon localIcon = localJLabel.isEnabled() ? localJLabel.getIcon() : localJLabel.getDisabledIcon();
    SynthContext localSynthContext = getContext(paramJComponent);
    Dimension localDimension = localSynthContext.getStyle().getGraphicsUtils(localSynthContext).getMinimumSize(localSynthContext, localSynthContext.getStyle().getFont(localSynthContext), localJLabel.getText(), localIcon, localJLabel.getHorizontalAlignment(), localJLabel.getVerticalAlignment(), localJLabel.getHorizontalTextPosition(), localJLabel.getVerticalTextPosition(), localJLabel.getIconTextGap(), localJLabel.getDisplayedMnemonicIndex());
    localSynthContext.dispose();
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    JLabel localJLabel = (JLabel)paramJComponent;
    Icon localIcon = localJLabel.isEnabled() ? localJLabel.getIcon() : localJLabel.getDisabledIcon();
    SynthContext localSynthContext = getContext(paramJComponent);
    Dimension localDimension = localSynthContext.getStyle().getGraphicsUtils(localSynthContext).getMaximumSize(localSynthContext, localSynthContext.getStyle().getFont(localSynthContext), localJLabel.getText(), localIcon, localJLabel.getHorizontalAlignment(), localJLabel.getVerticalAlignment(), localJLabel.getHorizontalTextPosition(), localJLabel.getVerticalTextPosition(), localJLabel.getIconTextGap(), localJLabel.getDisplayedMnemonicIndex());
    localSynthContext.dispose();
    return localDimension;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JLabel)paramPropertyChangeEvent.getSource());
    }
  }
}
