package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;

public class StyleConstants
{
  public static final String ComponentElementName = "component";
  public static final String IconElementName = "icon";
  public static final Object NameAttribute = new StyleConstants("name");
  public static final Object ResolveAttribute = new StyleConstants("resolver");
  public static final Object ModelAttribute = new StyleConstants("model");
  public static final Object BidiLevel = new CharacterConstants("bidiLevel", null);
  public static final Object FontFamily = new FontConstants("family", null);
  public static final Object Family = FontFamily;
  public static final Object FontSize = new FontConstants("size", null);
  public static final Object Size = FontSize;
  public static final Object Bold = new FontConstants("bold", null);
  public static final Object Italic = new FontConstants("italic", null);
  public static final Object Underline = new CharacterConstants("underline", null);
  public static final Object StrikeThrough = new CharacterConstants("strikethrough", null);
  public static final Object Superscript = new CharacterConstants("superscript", null);
  public static final Object Subscript = new CharacterConstants("subscript", null);
  public static final Object Foreground = new ColorConstants("foreground", null);
  public static final Object Background = new ColorConstants("background", null);
  public static final Object ComponentAttribute = new CharacterConstants("component", null);
  public static final Object IconAttribute = new CharacterConstants("icon", null);
  public static final Object ComposedTextAttribute = new StyleConstants("composed text");
  public static final Object FirstLineIndent = new ParagraphConstants("FirstLineIndent", null);
  public static final Object LeftIndent = new ParagraphConstants("LeftIndent", null);
  public static final Object RightIndent = new ParagraphConstants("RightIndent", null);
  public static final Object LineSpacing = new ParagraphConstants("LineSpacing", null);
  public static final Object SpaceAbove = new ParagraphConstants("SpaceAbove", null);
  public static final Object SpaceBelow = new ParagraphConstants("SpaceBelow", null);
  public static final Object Alignment = new ParagraphConstants("Alignment", null);
  public static final Object TabSet = new ParagraphConstants("TabSet", null);
  public static final Object Orientation = new ParagraphConstants("Orientation", null);
  public static final int ALIGN_LEFT = 0;
  public static final int ALIGN_CENTER = 1;
  public static final int ALIGN_RIGHT = 2;
  public static final int ALIGN_JUSTIFIED = 3;
  static Object[] keys = { NameAttribute, ResolveAttribute, BidiLevel, FontFamily, FontSize, Bold, Italic, Underline, StrikeThrough, Superscript, Subscript, Foreground, Background, ComponentAttribute, IconAttribute, FirstLineIndent, LeftIndent, RightIndent, LineSpacing, SpaceAbove, SpaceBelow, Alignment, TabSet, Orientation, ModelAttribute, ComposedTextAttribute };
  private String representation;
  
  public String toString()
  {
    return this.representation;
  }
  
  public static int getBidiLevel(AttributeSet paramAttributeSet)
  {
    Integer localInteger = (Integer)paramAttributeSet.getAttribute(BidiLevel);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    return 0;
  }
  
  public static void setBidiLevel(MutableAttributeSet paramMutableAttributeSet, int paramInt)
  {
    paramMutableAttributeSet.addAttribute(BidiLevel, Integer.valueOf(paramInt));
  }
  
  public static Component getComponent(AttributeSet paramAttributeSet)
  {
    return (Component)paramAttributeSet.getAttribute(ComponentAttribute);
  }
  
  public static void setComponent(MutableAttributeSet paramMutableAttributeSet, Component paramComponent)
  {
    paramMutableAttributeSet.addAttribute("$ename", "component");
    paramMutableAttributeSet.addAttribute(ComponentAttribute, paramComponent);
  }
  
  public static Icon getIcon(AttributeSet paramAttributeSet)
  {
    return (Icon)paramAttributeSet.getAttribute(IconAttribute);
  }
  
  public static void setIcon(MutableAttributeSet paramMutableAttributeSet, Icon paramIcon)
  {
    paramMutableAttributeSet.addAttribute("$ename", "icon");
    paramMutableAttributeSet.addAttribute(IconAttribute, paramIcon);
  }
  
  public static String getFontFamily(AttributeSet paramAttributeSet)
  {
    String str = (String)paramAttributeSet.getAttribute(FontFamily);
    if (str == null) {
      str = "Monospaced";
    }
    return str;
  }
  
  public static void setFontFamily(MutableAttributeSet paramMutableAttributeSet, String paramString)
  {
    paramMutableAttributeSet.addAttribute(FontFamily, paramString);
  }
  
  public static int getFontSize(AttributeSet paramAttributeSet)
  {
    Integer localInteger = (Integer)paramAttributeSet.getAttribute(FontSize);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    return 12;
  }
  
  public static void setFontSize(MutableAttributeSet paramMutableAttributeSet, int paramInt)
  {
    paramMutableAttributeSet.addAttribute(FontSize, Integer.valueOf(paramInt));
  }
  
  public static boolean isBold(AttributeSet paramAttributeSet)
  {
    Boolean localBoolean = (Boolean)paramAttributeSet.getAttribute(Bold);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  public static void setBold(MutableAttributeSet paramMutableAttributeSet, boolean paramBoolean)
  {
    paramMutableAttributeSet.addAttribute(Bold, Boolean.valueOf(paramBoolean));
  }
  
  public static boolean isItalic(AttributeSet paramAttributeSet)
  {
    Boolean localBoolean = (Boolean)paramAttributeSet.getAttribute(Italic);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  public static void setItalic(MutableAttributeSet paramMutableAttributeSet, boolean paramBoolean)
  {
    paramMutableAttributeSet.addAttribute(Italic, Boolean.valueOf(paramBoolean));
  }
  
  public static boolean isUnderline(AttributeSet paramAttributeSet)
  {
    Boolean localBoolean = (Boolean)paramAttributeSet.getAttribute(Underline);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  public static boolean isStrikeThrough(AttributeSet paramAttributeSet)
  {
    Boolean localBoolean = (Boolean)paramAttributeSet.getAttribute(StrikeThrough);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  public static boolean isSuperscript(AttributeSet paramAttributeSet)
  {
    Boolean localBoolean = (Boolean)paramAttributeSet.getAttribute(Superscript);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  public static boolean isSubscript(AttributeSet paramAttributeSet)
  {
    Boolean localBoolean = (Boolean)paramAttributeSet.getAttribute(Subscript);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  public static void setUnderline(MutableAttributeSet paramMutableAttributeSet, boolean paramBoolean)
  {
    paramMutableAttributeSet.addAttribute(Underline, Boolean.valueOf(paramBoolean));
  }
  
  public static void setStrikeThrough(MutableAttributeSet paramMutableAttributeSet, boolean paramBoolean)
  {
    paramMutableAttributeSet.addAttribute(StrikeThrough, Boolean.valueOf(paramBoolean));
  }
  
  public static void setSuperscript(MutableAttributeSet paramMutableAttributeSet, boolean paramBoolean)
  {
    paramMutableAttributeSet.addAttribute(Superscript, Boolean.valueOf(paramBoolean));
  }
  
  public static void setSubscript(MutableAttributeSet paramMutableAttributeSet, boolean paramBoolean)
  {
    paramMutableAttributeSet.addAttribute(Subscript, Boolean.valueOf(paramBoolean));
  }
  
  public static Color getForeground(AttributeSet paramAttributeSet)
  {
    Color localColor = (Color)paramAttributeSet.getAttribute(Foreground);
    if (localColor == null) {
      localColor = Color.black;
    }
    return localColor;
  }
  
  public static void setForeground(MutableAttributeSet paramMutableAttributeSet, Color paramColor)
  {
    paramMutableAttributeSet.addAttribute(Foreground, paramColor);
  }
  
  public static Color getBackground(AttributeSet paramAttributeSet)
  {
    Color localColor = (Color)paramAttributeSet.getAttribute(Background);
    if (localColor == null) {
      localColor = Color.black;
    }
    return localColor;
  }
  
  public static void setBackground(MutableAttributeSet paramMutableAttributeSet, Color paramColor)
  {
    paramMutableAttributeSet.addAttribute(Background, paramColor);
  }
  
  public static float getFirstLineIndent(AttributeSet paramAttributeSet)
  {
    Float localFloat = (Float)paramAttributeSet.getAttribute(FirstLineIndent);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    return 0.0F;
  }
  
  public static void setFirstLineIndent(MutableAttributeSet paramMutableAttributeSet, float paramFloat)
  {
    paramMutableAttributeSet.addAttribute(FirstLineIndent, new Float(paramFloat));
  }
  
  public static float getRightIndent(AttributeSet paramAttributeSet)
  {
    Float localFloat = (Float)paramAttributeSet.getAttribute(RightIndent);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    return 0.0F;
  }
  
  public static void setRightIndent(MutableAttributeSet paramMutableAttributeSet, float paramFloat)
  {
    paramMutableAttributeSet.addAttribute(RightIndent, new Float(paramFloat));
  }
  
  public static float getLeftIndent(AttributeSet paramAttributeSet)
  {
    Float localFloat = (Float)paramAttributeSet.getAttribute(LeftIndent);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    return 0.0F;
  }
  
  public static void setLeftIndent(MutableAttributeSet paramMutableAttributeSet, float paramFloat)
  {
    paramMutableAttributeSet.addAttribute(LeftIndent, new Float(paramFloat));
  }
  
  public static float getLineSpacing(AttributeSet paramAttributeSet)
  {
    Float localFloat = (Float)paramAttributeSet.getAttribute(LineSpacing);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    return 0.0F;
  }
  
  public static void setLineSpacing(MutableAttributeSet paramMutableAttributeSet, float paramFloat)
  {
    paramMutableAttributeSet.addAttribute(LineSpacing, new Float(paramFloat));
  }
  
  public static float getSpaceAbove(AttributeSet paramAttributeSet)
  {
    Float localFloat = (Float)paramAttributeSet.getAttribute(SpaceAbove);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    return 0.0F;
  }
  
  public static void setSpaceAbove(MutableAttributeSet paramMutableAttributeSet, float paramFloat)
  {
    paramMutableAttributeSet.addAttribute(SpaceAbove, new Float(paramFloat));
  }
  
  public static float getSpaceBelow(AttributeSet paramAttributeSet)
  {
    Float localFloat = (Float)paramAttributeSet.getAttribute(SpaceBelow);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    return 0.0F;
  }
  
  public static void setSpaceBelow(MutableAttributeSet paramMutableAttributeSet, float paramFloat)
  {
    paramMutableAttributeSet.addAttribute(SpaceBelow, new Float(paramFloat));
  }
  
  public static int getAlignment(AttributeSet paramAttributeSet)
  {
    Integer localInteger = (Integer)paramAttributeSet.getAttribute(Alignment);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    return 0;
  }
  
  public static void setAlignment(MutableAttributeSet paramMutableAttributeSet, int paramInt)
  {
    paramMutableAttributeSet.addAttribute(Alignment, Integer.valueOf(paramInt));
  }
  
  public static TabSet getTabSet(AttributeSet paramAttributeSet)
  {
    TabSet localTabSet = (TabSet)paramAttributeSet.getAttribute(TabSet);
    return localTabSet;
  }
  
  public static void setTabSet(MutableAttributeSet paramMutableAttributeSet, TabSet paramTabSet)
  {
    paramMutableAttributeSet.addAttribute(TabSet, paramTabSet);
  }
  
  StyleConstants(String paramString)
  {
    this.representation = paramString;
  }
  
  public static class CharacterConstants
    extends StyleConstants
    implements AttributeSet.CharacterAttribute
  {
    private CharacterConstants(String paramString)
    {
      super();
    }
  }
  
  public static class ColorConstants
    extends StyleConstants
    implements AttributeSet.ColorAttribute, AttributeSet.CharacterAttribute
  {
    private ColorConstants(String paramString)
    {
      super();
    }
  }
  
  public static class FontConstants
    extends StyleConstants
    implements AttributeSet.FontAttribute, AttributeSet.CharacterAttribute
  {
    private FontConstants(String paramString)
    {
      super();
    }
  }
  
  public static class ParagraphConstants
    extends StyleConstants
    implements AttributeSet.ParagraphAttribute
  {
    private ParagraphConstants(String paramString)
    {
      super();
    }
  }
}
