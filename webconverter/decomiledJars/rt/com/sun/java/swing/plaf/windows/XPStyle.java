package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.image.SunWritableRaster;
import sun.awt.windows.ThemeReader;
import sun.security.action.GetPropertyAction;
import sun.swing.CachedPainter;

class XPStyle
{
  private static XPStyle xp;
  private static SkinPainter skinPainter;
  private static Boolean themeActive;
  private HashMap<String, Border> borderMap = new HashMap();
  private HashMap<String, Color> colorMap = new HashMap();
  private boolean flatMenus = getSysBoolean(TMSchema.Prop.FLATMENUS);
  
  static synchronized void invalidateStyle()
  {
    xp = null;
    themeActive = null;
    skinPainter.flush();
  }
  
  static synchronized XPStyle getXP()
  {
    if (themeActive == null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      themeActive = (Boolean)localToolkit.getDesktopProperty("win.xpstyle.themeActive");
      if (themeActive == null) {
        themeActive = Boolean.FALSE;
      }
      if (themeActive.booleanValue())
      {
        GetPropertyAction localGetPropertyAction = new GetPropertyAction("swing.noxp");
        if ((AccessController.doPrivileged(localGetPropertyAction) == null) && (ThemeReader.isThemed()) && (!(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel))) {
          xp = new XPStyle();
        }
      }
    }
    return ThemeReader.isXPStyleEnabled() ? xp : null;
  }
  
  static boolean isVista()
  {
    XPStyle localXPStyle = getXP();
    return (localXPStyle != null) && (localXPStyle.isSkinDefined(null, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT));
  }
  
  String getString(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    return getTypeEnumName(paramComponent, paramPart, paramState, paramProp);
  }
  
  TMSchema.TypeEnum getTypeEnum(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return TMSchema.TypeEnum.getTypeEnum(paramProp, i);
  }
  
  private static String getTypeEnumName(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    if (i == -1) {
      return null;
    }
    return TMSchema.TypeEnum.getTypeEnum(paramProp, i).getName();
  }
  
  int getInt(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, int paramInt)
  {
    return ThemeReader.getInt(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
  }
  
  Dimension getDimension(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    Dimension localDimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return localDimension != null ? localDimension : new Dimension();
  }
  
  Point getPoint(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    Dimension localDimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return localDimension != null ? new Point(localDimension.width, localDimension.height) : new Point();
  }
  
  Insets getMargin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    Insets localInsets = ThemeReader.getThemeMargins(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return localInsets != null ? localInsets : new Insets(0, 0, 0, 0);
  }
  
  synchronized Color getColor(Skin paramSkin, TMSchema.Prop paramProp, Color paramColor)
  {
    String str = paramSkin.toString() + "." + paramProp.name();
    TMSchema.Part localPart = paramSkin.part;
    Object localObject = (Color)this.colorMap.get(str);
    if (localObject == null)
    {
      localObject = ThemeReader.getColor(localPart.getControlName(null), localPart.getValue(), TMSchema.State.getValue(localPart, paramSkin.state), paramProp.getValue());
      if (localObject != null)
      {
        localObject = new ColorUIResource((Color)localObject);
        this.colorMap.put(str, localObject);
      }
    }
    return localObject != null ? localObject : paramColor;
  }
  
  Color getColor(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, Color paramColor)
  {
    return getColor(new Skin(paramComponent, paramPart, paramState), paramProp, paramColor);
  }
  
  synchronized Border getBorder(Component paramComponent, TMSchema.Part paramPart)
  {
    if (paramPart == TMSchema.Part.MENU)
    {
      if (this.flatMenus) {
        return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1);
      }
      return null;
    }
    Skin localSkin = new Skin(paramComponent, paramPart, null);
    Object localObject = (Border)this.borderMap.get(localSkin.string);
    if (localObject == null)
    {
      String str = getTypeEnumName(paramComponent, paramPart, null, TMSchema.Prop.BGTYPE);
      if ("borderfill".equalsIgnoreCase(str))
      {
        int i = getInt(paramComponent, paramPart, null, TMSchema.Prop.BORDERSIZE, 1);
        Color localColor = getColor(localSkin, TMSchema.Prop.BORDERCOLOR, Color.black);
        localObject = new XPFillBorder(localColor, i);
        if (paramPart == TMSchema.Part.CP_COMBOBOX) {
          localObject = new XPStatefulFillBorder(localColor, i, paramPart, TMSchema.Prop.BORDERCOLOR);
        }
      }
      else if ("imagefile".equalsIgnoreCase(str))
      {
        Insets localInsets = getMargin(paramComponent, paramPart, null, TMSchema.Prop.SIZINGMARGINS);
        if (localInsets != null) {
          if (getBoolean(paramComponent, paramPart, null, TMSchema.Prop.BORDERONLY)) {
            localObject = new XPImageBorder(paramComponent, paramPart);
          } else if (paramPart == TMSchema.Part.CP_COMBOBOX) {
            localObject = new EmptyBorder(1, 1, 1, 1);
          } else if (paramPart == TMSchema.Part.TP_BUTTON) {
            localObject = new XPEmptyBorder(new Insets(3, 3, 3, 3));
          } else {
            localObject = new XPEmptyBorder(localInsets);
          }
        }
      }
      if (localObject != null) {
        this.borderMap.put(localSkin.string, localObject);
      }
    }
    return localObject;
  }
  
  boolean isSkinDefined(Component paramComponent, TMSchema.Part paramPart)
  {
    return (paramPart.getValue() == 0) || (ThemeReader.isThemePartDefined(paramPart.getControlName(paramComponent), paramPart.getValue(), 0));
  }
  
  synchronized Skin getSkin(Component paramComponent, TMSchema.Part paramPart)
  {
    assert (isSkinDefined(paramComponent, paramPart)) : ("part " + paramPart + " is not defined");
    return new Skin(paramComponent, paramPart, null);
  }
  
  long getThemeTransitionDuration(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, TMSchema.Prop paramProp)
  {
    return ThemeReader.getThemeTransitionDuration(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState1), TMSchema.State.getValue(paramPart, paramState2), paramProp != null ? paramProp.getValue() : 0);
  }
  
  private XPStyle() {}
  
  private boolean getBoolean(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    return ThemeReader.getBoolean(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
  }
  
  static Dimension getPartSize(TMSchema.Part paramPart, TMSchema.State paramState)
  {
    return ThemeReader.getPartSize(paramPart.getControlName(null), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState));
  }
  
  private static boolean getSysBoolean(TMSchema.Prop paramProp)
  {
    return ThemeReader.getSysBoolean("window", paramProp.getValue());
  }
  
  static
  {
    skinPainter = new SkinPainter();
    themeActive = null;
    invalidateStyle();
  }
  
  static class GlyphButton
    extends JButton
  {
    private XPStyle.Skin skin;
    
    public GlyphButton(Component paramComponent, TMSchema.Part paramPart)
    {
      XPStyle localXPStyle = XPStyle.getXP();
      this.skin = (localXPStyle != null ? localXPStyle.getSkin(paramComponent, paramPart) : null);
      setBorder(null);
      setContentAreaFilled(false);
      setMinimumSize(new Dimension(5, 5));
      setPreferredSize(new Dimension(16, 16));
      setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    protected TMSchema.State getState()
    {
      TMSchema.State localState = TMSchema.State.NORMAL;
      if (!isEnabled()) {
        localState = TMSchema.State.DISABLED;
      } else if (getModel().isPressed()) {
        localState = TMSchema.State.PRESSED;
      } else if (getModel().isRollover()) {
        localState = TMSchema.State.HOT;
      }
      return localState;
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      if ((XPStyle.getXP() == null) || (this.skin == null)) {
        return;
      }
      Dimension localDimension = getSize();
      this.skin.paintSkin(paramGraphics, 0, 0, localDimension.width, localDimension.height, getState());
    }
    
    public void setPart(Component paramComponent, TMSchema.Part paramPart)
    {
      XPStyle localXPStyle = XPStyle.getXP();
      this.skin = (localXPStyle != null ? localXPStyle.getSkin(paramComponent, paramPart) : null);
      revalidate();
      repaint();
    }
    
    protected void paintBorder(Graphics paramGraphics) {}
  }
  
  static class Skin
  {
    final Component component;
    final TMSchema.Part part;
    final TMSchema.State state;
    private final String string;
    private Dimension size = null;
    
    Skin(Component paramComponent, TMSchema.Part paramPart)
    {
      this(paramComponent, paramPart, null);
    }
    
    Skin(TMSchema.Part paramPart, TMSchema.State paramState)
    {
      this(null, paramPart, paramState);
    }
    
    Skin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState)
    {
      this.component = paramComponent;
      this.part = paramPart;
      this.state = paramState;
      String str = paramPart.getControlName(paramComponent) + "." + paramPart.name();
      if (paramState != null) {
        str = str + "(" + paramState.name() + ")";
      }
      this.string = str;
    }
    
    Insets getContentMargin()
    {
      int i = 100;
      int j = 100;
      Insets localInsets = ThemeReader.getThemeBackgroundContentMargins(this.part.getControlName(null), this.part.getValue(), 0, i, j);
      return localInsets != null ? localInsets : new Insets(0, 0, 0, 0);
    }
    
    private int getWidth(TMSchema.State paramState)
    {
      if (this.size == null) {
        this.size = XPStyle.getPartSize(this.part, paramState);
      }
      return this.size != null ? this.size.width : 0;
    }
    
    int getWidth()
    {
      return getWidth(this.state != null ? this.state : TMSchema.State.NORMAL);
    }
    
    private int getHeight(TMSchema.State paramState)
    {
      if (this.size == null) {
        this.size = XPStyle.getPartSize(this.part, paramState);
      }
      return this.size != null ? this.size.height : 0;
    }
    
    int getHeight()
    {
      return getHeight(this.state != null ? this.state : TMSchema.State.NORMAL);
    }
    
    public String toString()
    {
      return this.string;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Skin)) && (((Skin)paramObject).string.equals(this.string));
    }
    
    public int hashCode()
    {
      return this.string.hashCode();
    }
    
    void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, TMSchema.State paramState)
    {
      if (paramState == null) {
        paramState = this.state;
      }
      paintSkin(paramGraphics, paramInt1, paramInt2, getWidth(paramState), getHeight(paramState), paramState);
    }
    
    void paintSkin(Graphics paramGraphics, Rectangle paramRectangle, TMSchema.State paramState)
    {
      paintSkin(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, paramState);
    }
    
    void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
    {
      if (XPStyle.getXP() == null) {
        return;
      }
      if ((ThemeReader.isGetThemeTransitionDurationDefined()) && ((this.component instanceof JComponent)) && (SwingUtilities.getAncestorOfClass(CellRendererPane.class, this.component) == null)) {
        AnimationController.paintSkin((JComponent)this.component, this, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      } else {
        paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      }
    }
    
    void paintSkinRaw(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
    {
      if (XPStyle.getXP() == null) {
        return;
      }
      XPStyle.skinPainter.paint(null, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { this, paramState });
    }
    
    void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState, boolean paramBoolean)
    {
      if (XPStyle.getXP() == null) {
        return;
      }
      if ((paramBoolean) && ("borderfill".equals(XPStyle.getTypeEnumName(this.component, this.part, paramState, TMSchema.Prop.BGTYPE)))) {
        return;
      }
      XPStyle.skinPainter.paint(null, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { this, paramState });
    }
  }
  
  private static class SkinPainter
    extends CachedPainter
  {
    SkinPainter()
    {
      super();
      flush();
    }
    
    public void flush()
    {
      super.flush();
    }
    
    protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      int i = 0;
      XPStyle.Skin localSkin = (XPStyle.Skin)paramArrayOfObject[0];
      TMSchema.Part localPart = localSkin.part;
      TMSchema.State localState = (TMSchema.State)paramArrayOfObject[1];
      if (localState == null) {
        localState = localSkin.state;
      }
      if (paramComponent == null) {
        paramComponent = localSkin.component;
      }
      BufferedImage localBufferedImage = (BufferedImage)paramImage;
      WritableRaster localWritableRaster = localBufferedImage.getRaster();
      DataBufferInt localDataBufferInt = (DataBufferInt)localWritableRaster.getDataBuffer();
      ThemeReader.paintBackground(SunWritableRaster.stealData(localDataBufferInt, 0), localPart.getControlName(paramComponent), localPart.getValue(), TMSchema.State.getValue(localPart, localState), 0, 0, paramInt1, paramInt2, paramInt1);
      SunWritableRaster.markDirty(localDataBufferInt);
    }
    
    protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject)
    {
      return new BufferedImage(paramInt1, paramInt2, 2);
    }
  }
  
  private class XPEmptyBorder
    extends EmptyBorder
    implements UIResource
  {
    XPEmptyBorder(Insets paramInsets)
    {
      super(paramInsets.left + 2, paramInsets.bottom + 2, paramInsets.right + 2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets = super.getBorderInsets(paramComponent, paramInsets);
      Object localObject = null;
      if ((paramComponent instanceof AbstractButton))
      {
        Insets localInsets = ((AbstractButton)paramComponent).getMargin();
        if (((paramComponent.getParent() instanceof JToolBar)) && (!(paramComponent instanceof JRadioButton)) && (!(paramComponent instanceof JCheckBox)) && ((localInsets instanceof InsetsUIResource)))
        {
          paramInsets.top -= 2;
          paramInsets.left -= 2;
          paramInsets.bottom -= 2;
          paramInsets.right -= 2;
        }
        else
        {
          localObject = localInsets;
        }
      }
      else if ((paramComponent instanceof JToolBar))
      {
        localObject = ((JToolBar)paramComponent).getMargin();
      }
      else if ((paramComponent instanceof JTextComponent))
      {
        localObject = ((JTextComponent)paramComponent).getMargin();
      }
      if (localObject != null)
      {
        paramInsets.top = (((Insets)localObject).top + 2);
        paramInsets.left = (((Insets)localObject).left + 2);
        paramInsets.bottom = (((Insets)localObject).bottom + 2);
        paramInsets.right = (((Insets)localObject).right + 2);
      }
      return paramInsets;
    }
  }
  
  private class XPFillBorder
    extends LineBorder
    implements UIResource
  {
    XPFillBorder(Color paramColor, int paramInt)
    {
      super(paramInt);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets = null;
      if ((paramComponent instanceof AbstractButton)) {
        localInsets = ((AbstractButton)paramComponent).getMargin();
      } else if ((paramComponent instanceof JToolBar)) {
        localInsets = ((JToolBar)paramComponent).getMargin();
      } else if ((paramComponent instanceof JTextComponent)) {
        localInsets = ((JTextComponent)paramComponent).getMargin();
      }
      paramInsets.top = ((localInsets != null ? localInsets.top : 0) + this.thickness);
      paramInsets.left = ((localInsets != null ? localInsets.left : 0) + this.thickness);
      paramInsets.bottom = ((localInsets != null ? localInsets.bottom : 0) + this.thickness);
      paramInsets.right = ((localInsets != null ? localInsets.right : 0) + this.thickness);
      return paramInsets;
    }
  }
  
  private class XPImageBorder
    extends AbstractBorder
    implements UIResource
  {
    XPStyle.Skin skin;
    
    XPImageBorder(Component paramComponent, TMSchema.Part paramPart)
    {
      this.skin = XPStyle.this.getSkin(paramComponent, paramPart);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.skin.paintSkin(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets1 = null;
      Insets localInsets2 = this.skin.getContentMargin();
      if (localInsets2 == null) {
        localInsets2 = new Insets(0, 0, 0, 0);
      }
      if ((paramComponent instanceof AbstractButton)) {
        localInsets1 = ((AbstractButton)paramComponent).getMargin();
      } else if ((paramComponent instanceof JToolBar)) {
        localInsets1 = ((JToolBar)paramComponent).getMargin();
      } else if ((paramComponent instanceof JTextComponent)) {
        localInsets1 = ((JTextComponent)paramComponent).getMargin();
      }
      paramInsets.top = ((localInsets1 != null ? localInsets1.top : 0) + localInsets2.top);
      paramInsets.left = ((localInsets1 != null ? localInsets1.left : 0) + localInsets2.left);
      paramInsets.bottom = ((localInsets1 != null ? localInsets1.bottom : 0) + localInsets2.bottom);
      paramInsets.right = ((localInsets1 != null ? localInsets1.right : 0) + localInsets2.right);
      return paramInsets;
    }
  }
  
  private class XPStatefulFillBorder
    extends XPStyle.XPFillBorder
  {
    private final TMSchema.Part part;
    private final TMSchema.Prop prop;
    
    XPStatefulFillBorder(Color paramColor, int paramInt, TMSchema.Part paramPart, TMSchema.Prop paramProp)
    {
      super(paramColor, paramInt);
      this.part = paramPart;
      this.prop = paramProp;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      TMSchema.State localState = TMSchema.State.NORMAL;
      if ((paramComponent instanceof JComboBox))
      {
        JComboBox localJComboBox = (JComboBox)paramComponent;
        if ((localJComboBox.getUI() instanceof WindowsComboBoxUI))
        {
          WindowsComboBoxUI localWindowsComboBoxUI = (WindowsComboBoxUI)localJComboBox.getUI();
          localState = localWindowsComboBoxUI.getXPComboBoxState(localJComboBox);
        }
      }
      this.lineColor = XPStyle.this.getColor(paramComponent, this.part, localState, this.prop, Color.black);
      super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
}
