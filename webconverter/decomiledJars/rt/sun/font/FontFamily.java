package sun.font;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import sun.util.logging.PlatformLogger;

public class FontFamily
{
  private static ConcurrentHashMap<String, FontFamily> familyNameMap = new ConcurrentHashMap();
  private static HashMap<String, FontFamily> allLocaleNames;
  protected String familyName;
  protected Font2D plain;
  protected Font2D bold;
  protected Font2D italic;
  protected Font2D bolditalic;
  protected boolean logicalFont = false;
  protected int familyRank;
  private int familyWidth = 0;
  
  public static FontFamily getFamily(String paramString)
  {
    return (FontFamily)familyNameMap.get(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public static String[] getAllFamilyNames()
  {
    return null;
  }
  
  static void remove(Font2D paramFont2D)
  {
    String str = paramFont2D.getFamilyName(Locale.ENGLISH);
    FontFamily localFontFamily = getFamily(str);
    if (localFontFamily == null) {
      return;
    }
    if (localFontFamily.plain == paramFont2D) {
      localFontFamily.plain = null;
    }
    if (localFontFamily.bold == paramFont2D) {
      localFontFamily.bold = null;
    }
    if (localFontFamily.italic == paramFont2D) {
      localFontFamily.italic = null;
    }
    if (localFontFamily.bolditalic == paramFont2D) {
      localFontFamily.bolditalic = null;
    }
    if ((localFontFamily.plain == null) && (localFontFamily.bold == null) && (localFontFamily.plain == null) && (localFontFamily.bold == null)) {
      familyNameMap.remove(str);
    }
  }
  
  public FontFamily(String paramString, boolean paramBoolean, int paramInt)
  {
    this.logicalFont = paramBoolean;
    this.familyName = paramString;
    this.familyRank = paramInt;
    familyNameMap.put(paramString.toLowerCase(Locale.ENGLISH), this);
  }
  
  FontFamily(String paramString)
  {
    this.logicalFont = false;
    this.familyName = paramString;
    this.familyRank = 4;
  }
  
  public String getFamilyName()
  {
    return this.familyName;
  }
  
  public int getRank()
  {
    return this.familyRank;
  }
  
  private boolean isFromSameSource(Font2D paramFont2D)
  {
    if (!(paramFont2D instanceof FileFont)) {
      return false;
    }
    FileFont localFileFont1 = null;
    if ((this.plain instanceof FileFont)) {
      localFileFont1 = (FileFont)this.plain;
    } else if ((this.bold instanceof FileFont)) {
      localFileFont1 = (FileFont)this.bold;
    } else if ((this.italic instanceof FileFont)) {
      localFileFont1 = (FileFont)this.italic;
    } else if ((this.bolditalic instanceof FileFont)) {
      localFileFont1 = (FileFont)this.bolditalic;
    }
    if (localFileFont1 == null) {
      return false;
    }
    File localFile1 = new File(localFileFont1.platName).getParentFile();
    FileFont localFileFont2 = (FileFont)paramFont2D;
    File localFile2 = new File(localFileFont2.platName).getParentFile();
    return Objects.equals(localFile2, localFile1);
  }
  
  private boolean preferredWidth(Font2D paramFont2D)
  {
    int i = paramFont2D.getWidth();
    if (this.familyWidth == 0)
    {
      this.familyWidth = i;
      return true;
    }
    if (i == this.familyWidth) {
      return true;
    }
    if (Math.abs(5 - i) < Math.abs(5 - this.familyWidth))
    {
      if (FontUtilities.debugFonts()) {
        FontUtilities.getLogger().info("Found more preferred width. New width = " + i + " Old width = " + this.familyWidth + " in font " + paramFont2D + " nulling out fonts plain: " + this.plain + " bold: " + this.bold + " italic: " + this.italic + " bolditalic: " + this.bolditalic);
      }
      this.familyWidth = i;
      this.plain = (this.bold = this.italic = this.bolditalic = null);
      return true;
    }
    if (FontUtilities.debugFonts()) {
      FontUtilities.getLogger().info("Family rejecting font " + paramFont2D + " of less preferred width " + i);
    }
    return false;
  }
  
  private boolean closerWeight(Font2D paramFont2D1, Font2D paramFont2D2, int paramInt)
  {
    if (this.familyWidth != paramFont2D2.getWidth()) {
      return false;
    }
    if (paramFont2D1 == null) {
      return true;
    }
    if (FontUtilities.debugFonts()) {
      FontUtilities.getLogger().info("New weight for style " + paramInt + ". Curr.font=" + paramFont2D1 + " New font=" + paramFont2D2 + " Curr.weight=" + paramFont2D1.getWeight() + " New weight=" + paramFont2D2.getWeight());
    }
    int i = paramFont2D2.getWeight();
    switch (paramInt)
    {
    case 0: 
    case 2: 
      return (i <= 400) && (i > paramFont2D1.getWeight());
    case 1: 
    case 3: 
      return Math.abs(i - 700) < Math.abs(paramFont2D1.getWeight() - 700);
    }
    return false;
  }
  
  public void setFont(Font2D paramFont2D, int paramInt)
  {
    if (FontUtilities.isLogging())
    {
      String str;
      if ((paramFont2D instanceof CompositeFont)) {
        str = "Request to add " + paramFont2D.getFamilyName(null) + " with style " + paramInt + " to family " + this.familyName;
      } else {
        str = "Request to add " + paramFont2D + " with style " + paramInt + " to family " + this;
      }
      FontUtilities.getLogger().info(str);
    }
    if ((paramFont2D.getRank() > this.familyRank) && (!isFromSameSource(paramFont2D)))
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().warning("Rejecting adding " + paramFont2D + " of lower rank " + paramFont2D.getRank() + " to family " + this + " of rank " + this.familyRank);
      }
      return;
    }
    switch (paramInt)
    {
    case 0: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(this.plain, paramFont2D, paramInt))) {
        this.plain = paramFont2D;
      }
      break;
    case 1: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(this.bold, paramFont2D, paramInt))) {
        this.bold = paramFont2D;
      }
      break;
    case 2: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(this.italic, paramFont2D, paramInt))) {
        this.italic = paramFont2D;
      }
      break;
    case 3: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(this.bolditalic, paramFont2D, paramInt))) {
        this.bolditalic = paramFont2D;
      }
      break;
    }
  }
  
  public Font2D getFontWithExactStyleMatch(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return this.plain;
    case 1: 
      return this.bold;
    case 2: 
      return this.italic;
    case 3: 
      return this.bolditalic;
    }
    return null;
  }
  
  public Font2D getFont(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return this.plain;
    case 1: 
      if (this.bold != null) {
        return this.bold;
      }
      if ((this.plain != null) && (this.plain.canDoStyle(paramInt))) {
        return this.plain;
      }
      return null;
    case 2: 
      if (this.italic != null) {
        return this.italic;
      }
      if ((this.plain != null) && (this.plain.canDoStyle(paramInt))) {
        return this.plain;
      }
      return null;
    case 3: 
      if (this.bolditalic != null) {
        return this.bolditalic;
      }
      if ((this.italic != null) && (this.italic.canDoStyle(paramInt))) {
        return this.italic;
      }
      if ((this.bold != null) && (this.bold.canDoStyle(paramInt))) {
        return this.italic;
      }
      if ((this.plain != null) && (this.plain.canDoStyle(paramInt))) {
        return this.plain;
      }
      return null;
    }
    return null;
  }
  
  Font2D getClosestStyle(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      if (this.bold != null) {
        return this.bold;
      }
      if (this.italic != null) {
        return this.italic;
      }
      return this.bolditalic;
    case 1: 
      if (this.plain != null) {
        return this.plain;
      }
      if (this.bolditalic != null) {
        return this.bolditalic;
      }
      return this.italic;
    case 2: 
      if (this.bolditalic != null) {
        return this.bolditalic;
      }
      if (this.plain != null) {
        return this.plain;
      }
      return this.bold;
    case 3: 
      if (this.italic != null) {
        return this.italic;
      }
      if (this.bold != null) {
        return this.bold;
      }
      return this.plain;
    }
    return null;
  }
  
  static synchronized void addLocaleNames(FontFamily paramFontFamily, String[] paramArrayOfString)
  {
    if (allLocaleNames == null) {
      allLocaleNames = new HashMap();
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      allLocaleNames.put(paramArrayOfString[i].toLowerCase(), paramFontFamily);
    }
  }
  
  public static synchronized FontFamily getLocaleFamily(String paramString)
  {
    if (allLocaleNames == null) {
      return null;
    }
    return (FontFamily)allLocaleNames.get(paramString.toLowerCase());
  }
  
  public static FontFamily[] getAllFontFamilies()
  {
    Collection localCollection = familyNameMap.values();
    return (FontFamily[])localCollection.toArray(new FontFamily[0]);
  }
  
  public String toString()
  {
    return "Font family: " + this.familyName + " plain=" + this.plain + " bold=" + this.bold + " italic=" + this.italic + " bolditalic=" + this.bolditalic;
  }
}
