package sun.awt;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sun.awt.windows.WFontConfiguration;
import sun.font.SunFontManager;
import sun.font.SunFontManager.FamilyDescription;
import sun.font.TrueTypeFont;

public final class Win32FontManager
  extends SunFontManager
{
  private static TrueTypeFont eudcFont;
  static String fontsForPrinting = null;
  
  private static native String getEUDCFontFile();
  
  public TrueTypeFont getEUDCFont()
  {
    return eudcFont;
  }
  
  public Win32FontManager()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Win32FontManager.this.registerJREFontsWithPlatform(SunFontManager.jreFontDirName);
        return null;
      }
    });
  }
  
  protected boolean useAbsoluteFontFileNames()
  {
    return false;
  }
  
  protected void registerFontFile(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
  {
    if (this.registeredFontFiles.contains(paramString)) {
      return;
    }
    this.registeredFontFiles.add(paramString);
    int i;
    if (getTrueTypeFilter().accept(null, paramString)) {
      i = 0;
    } else if (getType1Filter().accept(null, paramString)) {
      i = 1;
    } else {
      return;
    }
    if (this.fontPath == null) {
      this.fontPath = getPlatformFontPath(noType1Font);
    }
    String str1 = jreFontDirName + File.pathSeparator + this.fontPath;
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
    int j = 0;
    try
    {
      while ((j == 0) && (localStringTokenizer.hasMoreTokens()))
      {
        String str2 = localStringTokenizer.nextToken();
        boolean bool = str2.equals(jreFontDirName);
        File localFile = new File(str2, paramString);
        if (localFile.canRead())
        {
          j = 1;
          String str3 = localFile.getAbsolutePath();
          if (paramBoolean)
          {
            registerDeferredFont(paramString, str3, paramArrayOfString, i, bool, paramInt);
            break;
          }
          registerFontFile(str3, paramArrayOfString, i, bool, paramInt);
          break;
        }
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      System.err.println(localNoSuchElementException);
    }
    if (j == 0) {
      addToMissingFontFileList(paramString);
    }
  }
  
  protected FontConfiguration createFontConfiguration()
  {
    WFontConfiguration localWFontConfiguration = new WFontConfiguration(this);
    localWFontConfiguration.init();
    return localWFontConfiguration;
  }
  
  public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2)
  {
    return new WFontConfiguration(this, paramBoolean1, paramBoolean2);
  }
  
  protected void populateFontFileNameMap(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale)
  {
    populateFontFileNameMap0(paramHashMap1, paramHashMap2, paramHashMap, paramLocale);
  }
  
  private static native void populateFontFileNameMap0(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale);
  
  protected synchronized native String getFontPath(boolean paramBoolean);
  
  protected String[] getDefaultPlatformFont()
  {
    String[] arrayOfString1 = new String[2];
    arrayOfString1[0] = "Arial";
    arrayOfString1[1] = "c:\\windows\\fonts";
    final String[] arrayOfString2 = getPlatformFontDirs(true);
    if (arrayOfString2.length > 1)
    {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          for (int i = 0; i < arrayOfString2.length; i++)
          {
            String str = arrayOfString2[i] + File.separator + "arial.ttf";
            File localFile = new File(str);
            if (localFile.exists()) {
              return arrayOfString2[i];
            }
          }
          return null;
        }
      });
      if (str != null) {
        arrayOfString1[1] = str;
      }
    }
    else
    {
      arrayOfString1[1] = arrayOfString2[0];
    }
    arrayOfString1[1] = (arrayOfString1[1] + File.separator + "arial.ttf");
    return arrayOfString1;
  }
  
  protected void registerJREFontsWithPlatform(String paramString)
  {
    fontsForPrinting = paramString;
  }
  
  public static void registerJREFontsForPrinting()
  {
    String str;
    synchronized (Win32GraphicsEnvironment.class)
    {
      GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (fontsForPrinting == null) {
        return;
      }
      str = fontsForPrinting;
      fontsForPrinting = null;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        File localFile1 = new File(this.val$pathName);
        String[] arrayOfString = localFile1.list(SunFontManager.getInstance().getTrueTypeFilter());
        if (arrayOfString == null) {
          return null;
        }
        for (int i = 0; i < arrayOfString.length; i++)
        {
          File localFile2 = new File(localFile1, arrayOfString[i]);
          Win32FontManager.registerFontWithPlatform(localFile2.getAbsolutePath());
        }
        return null;
      }
    });
  }
  
  protected static native void registerFontWithPlatform(String paramString);
  
  protected static native void deRegisterFontWithPlatform(String paramString);
  
  public HashMap<String, SunFontManager.FamilyDescription> populateHardcodedFileNameMap()
  {
    HashMap localHashMap = new HashMap();
    SunFontManager.FamilyDescription localFamilyDescription = new SunFontManager.FamilyDescription();
    localFamilyDescription.familyName = "Segoe UI";
    localFamilyDescription.plainFullName = "Segoe UI";
    localFamilyDescription.plainFileName = "segoeui.ttf";
    localFamilyDescription.boldFullName = "Segoe UI Bold";
    localFamilyDescription.boldFileName = "segoeuib.ttf";
    localFamilyDescription.italicFullName = "Segoe UI Italic";
    localFamilyDescription.italicFileName = "segoeuii.ttf";
    localFamilyDescription.boldItalicFullName = "Segoe UI Bold Italic";
    localFamilyDescription.boldItalicFileName = "segoeuiz.ttf";
    localHashMap.put("segoe", localFamilyDescription);
    localFamilyDescription = new SunFontManager.FamilyDescription();
    localFamilyDescription.familyName = "Tahoma";
    localFamilyDescription.plainFullName = "Tahoma";
    localFamilyDescription.plainFileName = "tahoma.ttf";
    localFamilyDescription.boldFullName = "Tahoma Bold";
    localFamilyDescription.boldFileName = "tahomabd.ttf";
    localHashMap.put("tahoma", localFamilyDescription);
    localFamilyDescription = new SunFontManager.FamilyDescription();
    localFamilyDescription.familyName = "Verdana";
    localFamilyDescription.plainFullName = "Verdana";
    localFamilyDescription.plainFileName = "verdana.TTF";
    localFamilyDescription.boldFullName = "Verdana Bold";
    localFamilyDescription.boldFileName = "verdanab.TTF";
    localFamilyDescription.italicFullName = "Verdana Italic";
    localFamilyDescription.italicFileName = "verdanai.TTF";
    localFamilyDescription.boldItalicFullName = "Verdana Bold Italic";
    localFamilyDescription.boldItalicFileName = "verdanaz.TTF";
    localHashMap.put("verdana", localFamilyDescription);
    localFamilyDescription = new SunFontManager.FamilyDescription();
    localFamilyDescription.familyName = "Arial";
    localFamilyDescription.plainFullName = "Arial";
    localFamilyDescription.plainFileName = "ARIAL.TTF";
    localFamilyDescription.boldFullName = "Arial Bold";
    localFamilyDescription.boldFileName = "ARIALBD.TTF";
    localFamilyDescription.italicFullName = "Arial Italic";
    localFamilyDescription.italicFileName = "ARIALI.TTF";
    localFamilyDescription.boldItalicFullName = "Arial Bold Italic";
    localFamilyDescription.boldItalicFileName = "ARIALBI.TTF";
    localHashMap.put("arial", localFamilyDescription);
    localFamilyDescription = new SunFontManager.FamilyDescription();
    localFamilyDescription.familyName = "Symbol";
    localFamilyDescription.plainFullName = "Symbol";
    localFamilyDescription.plainFileName = "Symbol.TTF";
    localHashMap.put("symbol", localFamilyDescription);
    localFamilyDescription = new SunFontManager.FamilyDescription();
    localFamilyDescription.familyName = "WingDings";
    localFamilyDescription.plainFullName = "WingDings";
    localFamilyDescription.plainFileName = "WINGDING.TTF";
    localHashMap.put("wingdings", localFamilyDescription);
    return localHashMap;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        String str = Win32FontManager.access$000();
        if (str != null) {
          try
          {
            Win32FontManager.access$102(new TrueTypeFont(str, null, 0, true));
          }
          catch (FontFormatException localFontFormatException) {}
        }
        return null;
      }
    });
  }
}
