package javax.swing.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class BorderUIResource
  implements Border, UIResource, Serializable
{
  static Border etched;
  static Border loweredBevel;
  static Border raisedBevel;
  static Border blackLine;
  private Border delegate;
  
  public static Border getEtchedBorderUIResource()
  {
    if (etched == null) {
      etched = new EtchedBorderUIResource();
    }
    return etched;
  }
  
  public static Border getLoweredBevelBorderUIResource()
  {
    if (loweredBevel == null) {
      loweredBevel = new BevelBorderUIResource(1);
    }
    return loweredBevel;
  }
  
  public static Border getRaisedBevelBorderUIResource()
  {
    if (raisedBevel == null) {
      raisedBevel = new BevelBorderUIResource(0);
    }
    return raisedBevel;
  }
  
  public static Border getBlackLineBorderUIResource()
  {
    if (blackLine == null) {
      blackLine = new LineBorderUIResource(Color.black);
    }
    return blackLine;
  }
  
  public BorderUIResource(Border paramBorder)
  {
    if (paramBorder == null) {
      throw new IllegalArgumentException("null border delegate argument");
    }
    this.delegate = paramBorder;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.delegate.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Insets getBorderInsets(Component paramComponent)
  {
    return this.delegate.getBorderInsets(paramComponent);
  }
  
  public boolean isBorderOpaque()
  {
    return this.delegate.isBorderOpaque();
  }
  
  public static class BevelBorderUIResource
    extends BevelBorder
    implements UIResource
  {
    public BevelBorderUIResource(int paramInt)
    {
      super();
    }
    
    public BevelBorderUIResource(int paramInt, Color paramColor1, Color paramColor2)
    {
      super(paramColor1, paramColor2);
    }
    
    @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
    public BevelBorderUIResource(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      super(paramColor1, paramColor2, paramColor3, paramColor4);
    }
  }
  
  public static class CompoundBorderUIResource
    extends CompoundBorder
    implements UIResource
  {
    @ConstructorProperties({"outsideBorder", "insideBorder"})
    public CompoundBorderUIResource(Border paramBorder1, Border paramBorder2)
    {
      super(paramBorder2);
    }
  }
  
  public static class EmptyBorderUIResource
    extends EmptyBorder
    implements UIResource
  {
    public EmptyBorderUIResource(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt2, paramInt3, paramInt4);
    }
    
    @ConstructorProperties({"borderInsets"})
    public EmptyBorderUIResource(Insets paramInsets)
    {
      super();
    }
  }
  
  public static class EtchedBorderUIResource
    extends EtchedBorder
    implements UIResource
  {
    public EtchedBorderUIResource() {}
    
    public EtchedBorderUIResource(int paramInt)
    {
      super();
    }
    
    public EtchedBorderUIResource(Color paramColor1, Color paramColor2)
    {
      super(paramColor2);
    }
    
    @ConstructorProperties({"etchType", "highlightColor", "shadowColor"})
    public EtchedBorderUIResource(int paramInt, Color paramColor1, Color paramColor2)
    {
      super(paramColor1, paramColor2);
    }
  }
  
  public static class LineBorderUIResource
    extends LineBorder
    implements UIResource
  {
    public LineBorderUIResource(Color paramColor)
    {
      super();
    }
    
    @ConstructorProperties({"lineColor", "thickness"})
    public LineBorderUIResource(Color paramColor, int paramInt)
    {
      super(paramInt);
    }
  }
  
  public static class MatteBorderUIResource
    extends MatteBorder
    implements UIResource
  {
    public MatteBorderUIResource(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
    {
      super(paramInt2, paramInt3, paramInt4, paramColor);
    }
    
    public MatteBorderUIResource(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Icon paramIcon)
    {
      super(paramInt2, paramInt3, paramInt4, paramIcon);
    }
    
    public MatteBorderUIResource(Icon paramIcon)
    {
      super();
    }
  }
  
  public static class TitledBorderUIResource
    extends TitledBorder
    implements UIResource
  {
    public TitledBorderUIResource(String paramString)
    {
      super();
    }
    
    public TitledBorderUIResource(Border paramBorder)
    {
      super();
    }
    
    public TitledBorderUIResource(Border paramBorder, String paramString)
    {
      super(paramString);
    }
    
    public TitledBorderUIResource(Border paramBorder, String paramString, int paramInt1, int paramInt2)
    {
      super(paramString, paramInt1, paramInt2);
    }
    
    public TitledBorderUIResource(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont)
    {
      super(paramString, paramInt1, paramInt2, paramFont);
    }
    
    @ConstructorProperties({"border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor"})
    public TitledBorderUIResource(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont, Color paramColor)
    {
      super(paramString, paramInt1, paramInt2, paramFont, paramColor);
    }
  }
}
