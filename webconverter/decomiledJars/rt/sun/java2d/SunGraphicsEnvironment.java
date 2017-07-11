package sun.java2d;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import sun.awt.DisplayChangedListener;
import sun.awt.SunDisplayChanger;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.FontManagerForSGE;

public abstract class SunGraphicsEnvironment
  extends GraphicsEnvironment
  implements DisplayChangedListener
{
  public static boolean isOpenSolaris;
  private static Font defaultFont;
  protected GraphicsDevice[] screens;
  protected SunDisplayChanger displayChanger = new SunDisplayChanger();
  
  public SunGraphicsEnvironment()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        String str1 = System.getProperty("os.version", "0.0");
        try
        {
          float f = Float.parseFloat(str1);
          if (f > 5.1F)
          {
            File localFile1 = new File("/etc/release");
            FileInputStream localFileInputStream = new FileInputStream(localFile1);
            InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream, "ISO-8859-1");
            BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
            String str2 = localBufferedReader.readLine();
            if (str2.indexOf("OpenSolaris") >= 0)
            {
              SunGraphicsEnvironment.isOpenSolaris = true;
            }
            else
            {
              String str3 = "/usr/openwin/lib/X11/fonts/TrueType/CourierNew.ttf";
              File localFile2 = new File(str3);
              SunGraphicsEnvironment.isOpenSolaris = !localFile2.exists();
            }
            localFileInputStream.close();
          }
        }
        catch (Exception localException) {}
        SunGraphicsEnvironment.access$002(new Font("Dialog", 0, 12));
        return null;
      }
    });
  }
  
  public synchronized GraphicsDevice[] getScreenDevices()
  {
    GraphicsDevice[] arrayOfGraphicsDevice = this.screens;
    if (arrayOfGraphicsDevice == null)
    {
      int i = getNumScreens();
      arrayOfGraphicsDevice = new GraphicsDevice[i];
      for (int j = 0; j < i; j++) {
        arrayOfGraphicsDevice[j] = makeScreenDevice(j);
      }
      this.screens = arrayOfGraphicsDevice;
    }
    return arrayOfGraphicsDevice;
  }
  
  protected abstract int getNumScreens();
  
  protected abstract GraphicsDevice makeScreenDevice(int paramInt);
  
  public GraphicsDevice getDefaultScreenDevice()
  {
    GraphicsDevice[] arrayOfGraphicsDevice = getScreenDevices();
    if (arrayOfGraphicsDevice.length == 0) {
      throw new AWTError("no screen devices");
    }
    return arrayOfGraphicsDevice[0];
  }
  
  public Graphics2D createGraphics(BufferedImage paramBufferedImage)
  {
    if (paramBufferedImage == null) {
      throw new NullPointerException("BufferedImage cannot be null");
    }
    SurfaceData localSurfaceData = SurfaceData.getPrimarySurfaceData(paramBufferedImage);
    return new SunGraphics2D(localSurfaceData, Color.white, Color.black, defaultFont);
  }
  
  public static FontManagerForSGE getFontManagerForSGE()
  {
    FontManager localFontManager = FontManagerFactory.getInstance();
    return (FontManagerForSGE)localFontManager;
  }
  
  public static void useAlternateFontforJALocales()
  {
    getFontManagerForSGE().useAlternateFontforJALocales();
  }
  
  public Font[] getAllFonts()
  {
    FontManagerForSGE localFontManagerForSGE = getFontManagerForSGE();
    Font[] arrayOfFont1 = localFontManagerForSGE.getAllInstalledFonts();
    Font[] arrayOfFont2 = localFontManagerForSGE.getCreatedFonts();
    if ((arrayOfFont2 == null) || (arrayOfFont2.length == 0)) {
      return arrayOfFont1;
    }
    int i = arrayOfFont1.length + arrayOfFont2.length;
    Font[] arrayOfFont3 = (Font[])Arrays.copyOf(arrayOfFont1, i);
    System.arraycopy(arrayOfFont2, 0, arrayOfFont3, arrayOfFont1.length, arrayOfFont2.length);
    return arrayOfFont3;
  }
  
  public String[] getAvailableFontFamilyNames(Locale paramLocale)
  {
    FontManagerForSGE localFontManagerForSGE = getFontManagerForSGE();
    String[] arrayOfString1 = localFontManagerForSGE.getInstalledFontFamilyNames(paramLocale);
    TreeMap localTreeMap = localFontManagerForSGE.getCreatedFontFamilyNames();
    if ((localTreeMap == null) || (localTreeMap.size() == 0)) {
      return arrayOfString1;
    }
    for (int i = 0; i < arrayOfString1.length; i++) {
      localTreeMap.put(arrayOfString1[i].toLowerCase(paramLocale), arrayOfString1[i]);
    }
    String[] arrayOfString2 = new String[localTreeMap.size()];
    Object[] arrayOfObject = localTreeMap.keySet().toArray();
    for (int j = 0; j < arrayOfObject.length; j++) {
      arrayOfString2[j] = ((String)localTreeMap.get(arrayOfObject[j]));
    }
    return arrayOfString2;
  }
  
  public String[] getAvailableFontFamilyNames()
  {
    return getAvailableFontFamilyNames(Locale.getDefault());
  }
  
  public static Rectangle getUsableBounds(GraphicsDevice paramGraphicsDevice)
  {
    GraphicsConfiguration localGraphicsConfiguration = paramGraphicsDevice.getDefaultConfiguration();
    Insets localInsets = Toolkit.getDefaultToolkit().getScreenInsets(localGraphicsConfiguration);
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    localRectangle.x += localInsets.left;
    localRectangle.y += localInsets.top;
    localRectangle.width -= localInsets.left + localInsets.right;
    localRectangle.height -= localInsets.top + localInsets.bottom;
    return localRectangle;
  }
  
  public void displayChanged()
  {
    for (GraphicsDevice localGraphicsDevice : getScreenDevices()) {
      if ((localGraphicsDevice instanceof DisplayChangedListener)) {
        ((DisplayChangedListener)localGraphicsDevice).displayChanged();
      }
    }
    this.displayChanger.notifyListeners();
  }
  
  public void paletteChanged()
  {
    this.displayChanger.notifyPaletteChanged();
  }
  
  public abstract boolean isDisplayLocal();
  
  public void addDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
  {
    this.displayChanger.add(paramDisplayChangedListener);
  }
  
  public void removeDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
  {
    this.displayChanger.remove(paramDisplayChangedListener);
  }
  
  public boolean isFlipStrategyPreferred(ComponentPeer paramComponentPeer)
  {
    return false;
  }
}
