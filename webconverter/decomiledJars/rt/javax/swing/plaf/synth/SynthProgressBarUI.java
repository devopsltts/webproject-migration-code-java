package javax.swing.plaf.synth;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import sun.swing.SwingUtilities2;

public class SynthProgressBarUI
  extends BasicProgressBarUI
  implements SynthUI, PropertyChangeListener
{
  private SynthStyle style;
  private int progressPadding;
  private boolean rotateText;
  private boolean paintOutsideClip;
  private boolean tileWhenIndeterminate;
  private int tileWidth;
  
  public SynthProgressBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthProgressBarUI();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    this.progressBar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    this.progressBar.removePropertyChangeListener(this);
  }
  
  protected void installDefaults()
  {
    updateStyle(this.progressBar);
  }
  
  private void updateStyle(JProgressBar paramJProgressBar)
  {
    SynthContext localSynthContext = getContext(paramJProgressBar, 1);
    SynthStyle localSynthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    setCellLength(this.style.getInt(localSynthContext, "ProgressBar.cellLength", 1));
    setCellSpacing(this.style.getInt(localSynthContext, "ProgressBar.cellSpacing", 0));
    this.progressPadding = this.style.getInt(localSynthContext, "ProgressBar.progressPadding", 0);
    this.paintOutsideClip = this.style.getBoolean(localSynthContext, "ProgressBar.paintOutsideClip", false);
    this.rotateText = this.style.getBoolean(localSynthContext, "ProgressBar.rotateText", false);
    this.tileWhenIndeterminate = this.style.getBoolean(localSynthContext, "ProgressBar.tileWhenIndeterminate", false);
    this.tileWidth = this.style.getInt(localSynthContext, "ProgressBar.tileWidth", 15);
    String str = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
    if (str != null) {
      if ("large".equals(str)) {
        this.tileWidth = ((int)(this.tileWidth * 1.15D));
      } else if ("small".equals(str)) {
        this.tileWidth = ((int)(this.tileWidth * 0.857D));
      } else if ("mini".equals(str)) {
        this.tileWidth = ((int)(this.tileWidth * 0.784D));
      }
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(this.progressBar, 1);
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
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if ((this.progressBar.isStringPainted()) && (this.progressBar.getOrientation() == 0))
    {
      SynthContext localSynthContext = getContext(paramJComponent);
      Font localFont = localSynthContext.getStyle().getFont(localSynthContext);
      FontMetrics localFontMetrics = this.progressBar.getFontMetrics(localFont);
      localSynthContext.dispose();
      return (paramInt2 - localFontMetrics.getAscent() - localFontMetrics.getDescent()) / 2 + localFontMetrics.getAscent();
    }
    return -1;
  }
  
  protected Rectangle getBox(Rectangle paramRectangle)
  {
    if (this.tileWhenIndeterminate) {
      return SwingUtilities.calculateInnerArea(this.progressBar, paramRectangle);
    }
    return super.getBox(paramRectangle);
  }
  
  protected void setAnimationIndex(int paramInt)
  {
    if (this.paintOutsideClip)
    {
      if (getAnimationIndex() == paramInt) {
        return;
      }
      super.setAnimationIndex(paramInt);
      this.progressBar.repaint();
    }
    else
    {
      super.setAnimationIndex(paramInt);
    }
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintProgressBarBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), this.progressBar.getOrientation());
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
    JProgressBar localJProgressBar = (JProgressBar)paramSynthContext.getComponent();
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (!localJProgressBar.isIndeterminate())
    {
      Insets localInsets = localJProgressBar.getInsets();
      double d2 = localJProgressBar.getPercentComplete();
      if (d2 != 0.0D) {
        if (localJProgressBar.getOrientation() == 0)
        {
          i = localInsets.left + this.progressPadding;
          j = localInsets.top + this.progressPadding;
          k = (int)(d2 * (localJProgressBar.getWidth() - (localInsets.left + this.progressPadding + localInsets.right + this.progressPadding)));
          m = localJProgressBar.getHeight() - (localInsets.top + this.progressPadding + localInsets.bottom + this.progressPadding);
          if (!SynthLookAndFeel.isLeftToRight(localJProgressBar)) {
            i = localJProgressBar.getWidth() - localInsets.right - k - this.progressPadding;
          }
        }
        else
        {
          i = localInsets.left + this.progressPadding;
          k = localJProgressBar.getWidth() - (localInsets.left + this.progressPadding + localInsets.right + this.progressPadding);
          m = (int)(d2 * (localJProgressBar.getHeight() - (localInsets.top + this.progressPadding + localInsets.bottom + this.progressPadding)));
          j = localJProgressBar.getHeight() - localInsets.bottom - m - this.progressPadding;
        }
      }
    }
    else
    {
      this.boxRect = getBox(this.boxRect);
      i = this.boxRect.x + this.progressPadding;
      j = this.boxRect.y + this.progressPadding;
      k = this.boxRect.width - this.progressPadding - this.progressPadding;
      m = this.boxRect.height - this.progressPadding - this.progressPadding;
    }
    if ((this.tileWhenIndeterminate) && (localJProgressBar.isIndeterminate()))
    {
      double d1 = getAnimationIndex() / getFrameCount();
      int n = (int)(d1 * this.tileWidth);
      Shape localShape = paramGraphics.getClip();
      paramGraphics.clipRect(i, j, k, m);
      int i1;
      if (localJProgressBar.getOrientation() == 0)
      {
        i1 = i - this.tileWidth + n;
        while (i1 <= k)
        {
          paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i1, j, this.tileWidth, m, localJProgressBar.getOrientation());
          i1 += this.tileWidth;
        }
      }
      else
      {
        i1 = j - n;
        while (i1 < m + this.tileWidth)
        {
          paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i, i1, k, this.tileWidth, localJProgressBar.getOrientation());
          i1 += this.tileWidth;
        }
      }
      paramGraphics.setClip(localShape);
    }
    else
    {
      paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i, j, k, m, localJProgressBar.getOrientation());
    }
    if (localJProgressBar.isStringPainted()) {
      paintText(paramSynthContext, paramGraphics, localJProgressBar.getString());
    }
  }
  
  protected void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString)
  {
    if (this.progressBar.isStringPainted())
    {
      SynthStyle localSynthStyle = paramSynthContext.getStyle();
      Font localFont = localSynthStyle.getFont(paramSynthContext);
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(this.progressBar, paramGraphics, localFont);
      int i = localSynthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, localFont, localFontMetrics, paramString);
      Rectangle localRectangle = this.progressBar.getBounds();
      Object localObject;
      if ((this.rotateText) && (this.progressBar.getOrientation() == 1))
      {
        localObject = (Graphics2D)paramGraphics;
        AffineTransform localAffineTransform;
        Point localPoint;
        if (this.progressBar.getComponentOrientation().isLeftToRight())
        {
          localAffineTransform = AffineTransform.getRotateInstance(-1.5707963267948966D);
          localPoint = new Point((localRectangle.width + localFontMetrics.getAscent() - localFontMetrics.getDescent()) / 2, (localRectangle.height + i) / 2);
        }
        else
        {
          localAffineTransform = AffineTransform.getRotateInstance(1.5707963267948966D);
          localPoint = new Point((localRectangle.width - localFontMetrics.getAscent() + localFontMetrics.getDescent()) / 2, (localRectangle.height - i) / 2);
        }
        if (localPoint.x < 0) {
          return;
        }
        localFont = localFont.deriveFont(localAffineTransform);
        ((Graphics2D)localObject).setFont(localFont);
        ((Graphics2D)localObject).setColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
        localSynthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, localPoint.x, localPoint.y, -1);
      }
      else
      {
        localObject = new Rectangle(localRectangle.width / 2 - i / 2, (localRectangle.height - (localFontMetrics.getAscent() + localFontMetrics.getDescent())) / 2, 0, 0);
        if (((Rectangle)localObject).y < 0) {
          return;
        }
        paramGraphics.setColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
        paramGraphics.setFont(localFont);
        localSynthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, ((Rectangle)localObject).x, ((Rectangle)localObject).y, -1);
      }
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.progressBar.getOrientation());
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if ((SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) || ("indeterminate".equals(paramPropertyChangeEvent.getPropertyName()))) {
      updateStyle((JProgressBar)paramPropertyChangeEvent.getSource());
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = null;
    Insets localInsets = this.progressBar.getInsets();
    FontMetrics localFontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
    String str1 = this.progressBar.getString();
    int i = localFontMetrics.getHeight() + localFontMetrics.getDescent();
    int j;
    if (this.progressBar.getOrientation() == 0)
    {
      localDimension = new Dimension(getPreferredInnerHorizontal());
      if (this.progressBar.isStringPainted())
      {
        if (i > localDimension.height) {
          localDimension.height = i;
        }
        j = SwingUtilities2.stringWidth(this.progressBar, localFontMetrics, str1);
        if (j > localDimension.width) {
          localDimension.width = j;
        }
      }
    }
    else
    {
      localDimension = new Dimension(getPreferredInnerVertical());
      if (this.progressBar.isStringPainted())
      {
        if (i > localDimension.width) {
          localDimension.width = i;
        }
        j = SwingUtilities2.stringWidth(this.progressBar, localFontMetrics, str1);
        if (j > localDimension.height) {
          localDimension.height = j;
        }
      }
    }
    String str2 = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
    if (str2 != null) {
      if ("large".equals(str2))
      {
        Dimension tmp221_220 = localDimension;
        tmp221_220.width = ((int)(tmp221_220.width * 1.15F));
        Dimension tmp234_233 = localDimension;
        tmp234_233.height = ((int)(tmp234_233.height * 1.15F));
      }
      else if ("small".equals(str2))
      {
        Dimension tmp260_259 = localDimension;
        tmp260_259.width = ((int)(tmp260_259.width * 0.9F));
        Dimension tmp273_272 = localDimension;
        tmp273_272.height = ((int)(tmp273_272.height * 0.9F));
      }
      else if ("mini".equals(str2))
      {
        Dimension tmp299_298 = localDimension;
        tmp299_298.width = ((int)(tmp299_298.width * 0.784F));
        Dimension tmp312_311 = localDimension;
        tmp312_311.height = ((int)(tmp312_311.height * 0.784F));
      }
    }
    localDimension.width += localInsets.left + localInsets.right;
    localDimension.height += localInsets.top + localInsets.bottom;
    return localDimension;
  }
}
