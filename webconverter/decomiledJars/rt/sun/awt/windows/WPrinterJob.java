package sun.awt.windows;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.peer.ComponentPeer;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import sun.awt.Win32FontManager;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.DisposerTarget;
import sun.print.DialogOwner;
import sun.print.PeekGraphics;
import sun.print.PeekMetrics;
import sun.print.RasterPrinterJob;
import sun.print.ServiceDialog;
import sun.print.SunAlternateMedia;
import sun.print.SunPageSelection;
import sun.print.Win32MediaTray;
import sun.print.Win32PrintService;
import sun.print.Win32PrintServiceLookup;

public final class WPrinterJob
  extends RasterPrinterJob
  implements DisposerTarget
{
  protected static final long PS_ENDCAP_ROUND = 0L;
  protected static final long PS_ENDCAP_SQUARE = 256L;
  protected static final long PS_ENDCAP_FLAT = 512L;
  protected static final long PS_JOIN_ROUND = 0L;
  protected static final long PS_JOIN_BEVEL = 4096L;
  protected static final long PS_JOIN_MITER = 8192L;
  protected static final int POLYFILL_ALTERNATE = 1;
  protected static final int POLYFILL_WINDING = 2;
  private static final int MAX_WCOLOR = 255;
  private static final int SET_DUP_VERTICAL = 16;
  private static final int SET_DUP_HORIZONTAL = 32;
  private static final int SET_RES_HIGH = 64;
  private static final int SET_RES_LOW = 128;
  private static final int SET_COLOR = 512;
  private static final int SET_ORIENTATION = 16384;
  private static final int SET_COLLATED = 32768;
  private static final int PD_COLLATE = 16;
  private static final int PD_PRINTTOFILE = 32;
  private static final int DM_ORIENTATION = 1;
  private static final int DM_PAPERSIZE = 2;
  private static final int DM_COPIES = 256;
  private static final int DM_DEFAULTSOURCE = 512;
  private static final int DM_PRINTQUALITY = 1024;
  private static final int DM_COLOR = 2048;
  private static final int DM_DUPLEX = 4096;
  private static final int DM_YRESOLUTION = 8192;
  private static final int DM_COLLATE = 32768;
  private static final short DMCOLLATE_FALSE = 0;
  private static final short DMCOLLATE_TRUE = 1;
  private static final short DMORIENT_PORTRAIT = 1;
  private static final short DMORIENT_LANDSCAPE = 2;
  private static final short DMCOLOR_MONOCHROME = 1;
  private static final short DMCOLOR_COLOR = 2;
  private static final short DMRES_DRAFT = -1;
  private static final short DMRES_LOW = -2;
  private static final short DMRES_MEDIUM = -3;
  private static final short DMRES_HIGH = -4;
  private static final short DMDUP_SIMPLEX = 1;
  private static final short DMDUP_VERTICAL = 2;
  private static final short DMDUP_HORIZONTAL = 3;
  private static final int MAX_UNKNOWN_PAGES = 9999;
  private boolean driverDoesMultipleCopies = false;
  private boolean driverDoesCollation = false;
  private boolean userRequestedCollation = false;
  private boolean noDefaultPrinter = false;
  private HandleRecord handleRecord = new HandleRecord();
  private int mPrintPaperSize;
  private int mPrintXRes;
  private int mPrintYRes;
  private int mPrintPhysX;
  private int mPrintPhysY;
  private int mPrintWidth;
  private int mPrintHeight;
  private int mPageWidth;
  private int mPageHeight;
  private int mAttSides;
  private int mAttChromaticity;
  private int mAttXRes;
  private int mAttYRes;
  private int mAttQuality;
  private int mAttCollate;
  private int mAttCopies;
  private int mAttMediaSizeName;
  private int mAttMediaTray;
  private String mDestination = null;
  private Color mLastColor;
  private Color mLastTextColor;
  private String mLastFontFamily;
  private float mLastFontSize;
  private int mLastFontStyle;
  private int mLastRotation;
  private float mLastAwScale;
  private PrinterJob pjob;
  private ComponentPeer dialogOwnerPeer = null;
  private Object disposerReferent = new Object();
  private String lastNativeService = null;
  private boolean defaultCopies = true;
  
  public WPrinterJob()
  {
    Disposer.addRecord(this.disposerReferent, this.handleRecord = new HandleRecord());
    initAttributeMembers();
  }
  
  public Object getDisposerReferent()
  {
    return this.disposerReferent;
  }
  
  public PageFormat pageDialog(PageFormat paramPageFormat)
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (!(getPrintService() instanceof Win32PrintService)) {
      return super.pageDialog(paramPageFormat);
    }
    PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
    boolean bool = false;
    WPageDialog localWPageDialog = new WPageDialog((Frame)null, this, localPageFormat, null);
    localWPageDialog.setRetVal(false);
    localWPageDialog.setVisible(true);
    bool = localWPageDialog.getRetVal();
    localWPageDialog.dispose();
    if ((bool) && (this.myService != null))
    {
      String str = getNativePrintService();
      if (!this.myService.getName().equals(str)) {
        try
        {
          setPrintService(Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(str));
        }
        catch (PrinterException localPrinterException) {}
      }
      updatePageAttributes(this.myService, localPageFormat);
      return localPageFormat;
    }
    return paramPageFormat;
  }
  
  private boolean displayNativeDialog()
  {
    if (this.attributes == null) {
      return false;
    }
    DialogOwner localDialogOwner = (DialogOwner)this.attributes.get(DialogOwner.class);
    Frame localFrame = localDialogOwner != null ? localDialogOwner.getOwner() : null;
    WPrintDialog localWPrintDialog = new WPrintDialog(localFrame, this);
    localWPrintDialog.setRetVal(false);
    localWPrintDialog.setVisible(true);
    boolean bool = localWPrintDialog.getRetVal();
    localWPrintDialog.dispose();
    Destination localDestination = (Destination)this.attributes.get(Destination.class);
    if ((localDestination == null) || (!bool)) {
      return bool;
    }
    String str1 = null;
    String str2 = "sun.print.resources.serviceui";
    ResourceBundle localResourceBundle = ResourceBundle.getBundle(str2);
    try
    {
      str1 = localResourceBundle.getString("dialog.printtofile");
    }
    catch (MissingResourceException localMissingResourceException) {}
    FileDialog localFileDialog = new FileDialog(localFrame, str1, 1);
    URI localURI = localDestination.getURI();
    String str3 = localURI != null ? localURI.getSchemeSpecificPart() : null;
    if (str3 != null)
    {
      localObject1 = new File(str3);
      localFileDialog.setFile(((File)localObject1).getName());
      localObject2 = ((File)localObject1).getParentFile();
      if (localObject2 != null) {
        localFileDialog.setDirectory(((File)localObject2).getPath());
      }
    }
    else
    {
      localFileDialog.setFile("out.prn");
    }
    localFileDialog.setVisible(true);
    Object localObject1 = localFileDialog.getFile();
    if (localObject1 == null)
    {
      localFileDialog.dispose();
      return false;
    }
    Object localObject2 = localFileDialog.getDirectory() + (String)localObject1;
    File localFile1 = new File((String)localObject2);
    for (File localFile2 = localFile1.getParentFile(); ((localFile1.exists()) && ((!localFile1.isFile()) || (!localFile1.canWrite()))) || ((localFile2 != null) && ((!localFile2.exists()) || ((localFile2.exists()) && (!localFile2.canWrite())))); localFile2 = localFile1.getParentFile())
    {
      new PrintToFileErrorDialog(localFrame, ServiceDialog.getMsg("dialog.owtitle"), ServiceDialog.getMsg("dialog.writeerror") + " " + (String)localObject2, ServiceDialog.getMsg("button.ok")).setVisible(true);
      localFileDialog.setVisible(true);
      localObject1 = localFileDialog.getFile();
      if (localObject1 == null)
      {
        localFileDialog.dispose();
        return false;
      }
      localObject2 = localFileDialog.getDirectory() + (String)localObject1;
      localFile1 = new File((String)localObject2);
    }
    localFileDialog.dispose();
    this.attributes.add(new Destination(localFile1.toURI()));
    return true;
  }
  
  public boolean printDialog()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (this.attributes == null) {
      this.attributes = new HashPrintRequestAttributeSet();
    }
    if (!(getPrintService() instanceof Win32PrintService)) {
      return super.printDialog(this.attributes);
    }
    if (this.noDefaultPrinter == true) {
      return false;
    }
    return displayNativeDialog();
  }
  
  public void setPrintService(PrintService paramPrintService)
    throws PrinterException
  {
    super.setPrintService(paramPrintService);
    if (!(paramPrintService instanceof Win32PrintService)) {
      return;
    }
    this.driverDoesMultipleCopies = false;
    this.driverDoesCollation = false;
    setNativePrintServiceIfNeeded(paramPrintService.getName());
  }
  
  private native void setNativePrintService(String paramString)
    throws PrinterException;
  
  private void setNativePrintServiceIfNeeded(String paramString)
    throws PrinterException
  {
    if ((paramString != null) && (!paramString.equals(this.lastNativeService)))
    {
      setNativePrintService(paramString);
      this.lastNativeService = paramString;
    }
  }
  
  public PrintService getPrintService()
  {
    if (this.myService == null)
    {
      String str = getNativePrintService();
      if (str != null)
      {
        this.myService = Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(str);
        if (this.myService != null) {
          return this.myService;
        }
      }
      this.myService = PrintServiceLookup.lookupDefaultPrintService();
      if ((this.myService instanceof Win32PrintService)) {
        try
        {
          setNativePrintServiceIfNeeded(this.myService.getName());
        }
        catch (Exception localException)
        {
          this.myService = null;
        }
      }
    }
    return this.myService;
  }
  
  private native String getNativePrintService();
  
  private void initAttributeMembers()
  {
    this.mAttSides = 0;
    this.mAttChromaticity = 0;
    this.mAttXRes = 0;
    this.mAttYRes = 0;
    this.mAttQuality = 0;
    this.mAttCollate = -1;
    this.mAttCopies = 0;
    this.mAttMediaTray = 0;
    this.mAttMediaSizeName = 0;
    this.mDestination = null;
  }
  
  protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrinterException
  {
    initAttributeMembers();
    super.setAttributes(paramPrintRequestAttributeSet);
    this.mAttCopies = getCopiesInt();
    this.mDestination = this.destinationAttr;
    if (paramPrintRequestAttributeSet == null) {
      return;
    }
    Attribute[] arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
    for (int i = 0; i < arrayOfAttribute.length; i++)
    {
      Object localObject = arrayOfAttribute[i];
      try
      {
        if (((Attribute)localObject).getCategory() == Sides.class)
        {
          setSidesAttrib((Attribute)localObject);
        }
        else if (((Attribute)localObject).getCategory() == Chromaticity.class)
        {
          setColorAttrib((Attribute)localObject);
        }
        else if (((Attribute)localObject).getCategory() == PrinterResolution.class)
        {
          setResolutionAttrib((Attribute)localObject);
        }
        else if (((Attribute)localObject).getCategory() == PrintQuality.class)
        {
          setQualityAttrib((Attribute)localObject);
        }
        else if (((Attribute)localObject).getCategory() == SheetCollate.class)
        {
          setCollateAttrib((Attribute)localObject);
        }
        else if ((((Attribute)localObject).getCategory() == Media.class) || (((Attribute)localObject).getCategory() == SunAlternateMedia.class))
        {
          if (((Attribute)localObject).getCategory() == SunAlternateMedia.class)
          {
            Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
            if ((localMedia == null) || (!(localMedia instanceof MediaTray))) {
              localObject = ((SunAlternateMedia)localObject).getMedia();
            }
          }
          if ((localObject instanceof MediaSizeName)) {
            setWin32MediaAttrib((Attribute)localObject);
          }
          if ((localObject instanceof MediaTray)) {
            setMediaTrayAttrib((Attribute)localObject);
          }
        }
      }
      catch (ClassCastException localClassCastException) {}
    }
  }
  
  private native void getDefaultPage(PageFormat paramPageFormat);
  
  public PageFormat defaultPage(PageFormat paramPageFormat)
  {
    PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
    getDefaultPage(localPageFormat);
    return localPageFormat;
  }
  
  protected native void validatePaper(Paper paramPaper1, Paper paramPaper2);
  
  protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
  {
    PeekMetrics localPeekMetrics = paramPeekGraphics.getMetrics();
    WPathGraphics localWPathGraphics;
    if ((!forcePDL) && ((forceRaster == true) || (localPeekMetrics.hasNonSolidColors()) || (localPeekMetrics.hasCompositing())))
    {
      localWPathGraphics = null;
    }
    else
    {
      BufferedImage localBufferedImage = new BufferedImage(8, 8, 1);
      Graphics2D localGraphics2D = localBufferedImage.createGraphics();
      boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
      localWPathGraphics = new WPathGraphics(localGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
    }
    return localWPathGraphics;
  }
  
  protected double getXRes()
  {
    if (this.mAttXRes != 0) {
      return this.mAttXRes;
    }
    return this.mPrintXRes;
  }
  
  protected double getYRes()
  {
    if (this.mAttYRes != 0) {
      return this.mAttYRes;
    }
    return this.mPrintYRes;
  }
  
  protected double getPhysicalPrintableX(Paper paramPaper)
  {
    return this.mPrintPhysX;
  }
  
  protected double getPhysicalPrintableY(Paper paramPaper)
  {
    return this.mPrintPhysY;
  }
  
  protected double getPhysicalPrintableWidth(Paper paramPaper)
  {
    return this.mPrintWidth;
  }
  
  protected double getPhysicalPrintableHeight(Paper paramPaper)
  {
    return this.mPrintHeight;
  }
  
  protected double getPhysicalPageWidth(Paper paramPaper)
  {
    return this.mPageWidth;
  }
  
  protected double getPhysicalPageHeight(Paper paramPaper)
  {
    return this.mPageHeight;
  }
  
  protected boolean isCollated()
  {
    return this.userRequestedCollation;
  }
  
  protected int getCollatedCopies()
  {
    debug_println("driverDoesMultipleCopies=" + this.driverDoesMultipleCopies + " driverDoesCollation=" + this.driverDoesCollation);
    if ((super.isCollated()) && (!this.driverDoesCollation))
    {
      this.mAttCollate = 0;
      this.mAttCopies = 1;
      return getCopies();
    }
    return 1;
  }
  
  protected int getNoncollatedCopies()
  {
    if ((this.driverDoesMultipleCopies) || (super.isCollated())) {
      return 1;
    }
    return getCopies();
  }
  
  private long getPrintDC()
  {
    return this.handleRecord.mPrintDC;
  }
  
  private void setPrintDC(long paramLong)
  {
    this.handleRecord.mPrintDC = paramLong;
  }
  
  private long getDevMode()
  {
    return this.handleRecord.mPrintHDevMode;
  }
  
  private void setDevMode(long paramLong)
  {
    this.handleRecord.mPrintHDevMode = paramLong;
  }
  
  private long getDevNames()
  {
    return this.handleRecord.mPrintHDevNames;
  }
  
  private void setDevNames(long paramLong)
  {
    this.handleRecord.mPrintHDevNames = paramLong;
  }
  
  protected void beginPath()
  {
    beginPath(getPrintDC());
  }
  
  protected void endPath()
  {
    endPath(getPrintDC());
  }
  
  protected void closeFigure()
  {
    closeFigure(getPrintDC());
  }
  
  protected void fillPath()
  {
    fillPath(getPrintDC());
  }
  
  protected void moveTo(float paramFloat1, float paramFloat2)
  {
    moveTo(getPrintDC(), paramFloat1, paramFloat2);
  }
  
  protected void lineTo(float paramFloat1, float paramFloat2)
  {
    lineTo(getPrintDC(), paramFloat1, paramFloat2);
  }
  
  protected void polyBezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    polyBezierTo(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
  }
  
  protected void setPolyFillMode(int paramInt)
  {
    setPolyFillMode(getPrintDC(), paramInt);
  }
  
  protected void selectSolidBrush(Color paramColor)
  {
    if (!paramColor.equals(this.mLastColor))
    {
      this.mLastColor = paramColor;
      float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
      selectSolidBrush(getPrintDC(), (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
    }
  }
  
  protected int getPenX()
  {
    return getPenX(getPrintDC());
  }
  
  protected int getPenY()
  {
    return getPenY(getPrintDC());
  }
  
  protected void selectClipPath()
  {
    selectClipPath(getPrintDC());
  }
  
  protected void frameRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    frameRect(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }
  
  protected void fillRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Color paramColor)
  {
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    fillRect(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
  }
  
  protected void selectPen(float paramFloat, Color paramColor)
  {
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    selectPen(getPrintDC(), paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
  }
  
  protected boolean selectStylePen(int paramInt1, int paramInt2, float paramFloat, Color paramColor)
  {
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    long l1;
    switch (paramInt1)
    {
    case 0: 
      l1 = 512L;
      break;
    case 1: 
      l1 = 0L;
      break;
    case 2: 
    default: 
      l1 = 256L;
    }
    long l2;
    switch (paramInt2)
    {
    case 2: 
      l2 = 4096L;
      break;
    case 0: 
    default: 
      l2 = 8192L;
      break;
    case 1: 
      l2 = 0L;
    }
    return selectStylePen(getPrintDC(), l1, l2, paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
  }
  
  protected boolean setFont(String paramString, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2)
  {
    boolean bool = true;
    if ((!paramString.equals(this.mLastFontFamily)) || (paramFloat1 != this.mLastFontSize) || (paramInt1 != this.mLastFontStyle) || (paramInt2 != this.mLastRotation) || (paramFloat2 != this.mLastAwScale))
    {
      bool = setFont(getPrintDC(), paramString, paramFloat1, (paramInt1 & 0x1) != 0, (paramInt1 & 0x2) != 0, paramInt2, paramFloat2);
      if (bool)
      {
        this.mLastFontFamily = paramString;
        this.mLastFontSize = paramFloat1;
        this.mLastFontStyle = paramInt1;
        this.mLastRotation = paramInt2;
        this.mLastAwScale = paramFloat2;
      }
    }
    return bool;
  }
  
  protected void setTextColor(Color paramColor)
  {
    if (!paramColor.equals(this.mLastTextColor))
    {
      this.mLastTextColor = paramColor;
      float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
      setTextColor(getPrintDC(), (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
    }
  }
  
  protected String removeControlChars(String paramString)
  {
    return super.removeControlChars(paramString);
  }
  
  protected void textOut(String paramString, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat)
  {
    String str = removeControlChars(paramString);
    assert ((paramArrayOfFloat == null) || (str.length() == paramString.length()));
    if (str.length() == 0) {
      return;
    }
    textOut(getPrintDC(), str, str.length(), false, paramFloat1, paramFloat2, paramArrayOfFloat);
  }
  
  protected void glyphsOut(int[] paramArrayOfInt, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat)
  {
    char[] arrayOfChar = new char[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfChar[i] = ((char)(paramArrayOfInt[i] & 0xFFFF));
    }
    String str = new String(arrayOfChar);
    textOut(getPrintDC(), str, paramArrayOfInt.length, true, paramFloat1, paramFloat2, paramArrayOfFloat);
  }
  
  protected int getGDIAdvance(String paramString)
  {
    paramString = removeControlChars(paramString);
    if (paramString.length() == 0) {
      return 0;
    }
    return getGDIAdvance(getPrintDC(), paramString);
  }
  
  protected void drawImage3ByteBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8)
  {
    drawDIBImage(getPrintDC(), paramArrayOfByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, 24, null);
  }
  
  protected void drawDIBImage(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt, IndexColorModel paramIndexColorModel)
  {
    int i = 24;
    byte[] arrayOfByte = null;
    if (paramIndexColorModel != null)
    {
      i = paramInt;
      arrayOfByte = new byte[(1 << paramIndexColorModel.getPixelSize()) * 4];
      for (int j = 0; j < paramIndexColorModel.getMapSize(); j++)
      {
        arrayOfByte[(j * 4 + 0)] = ((byte)(paramIndexColorModel.getBlue(j) & 0xFF));
        arrayOfByte[(j * 4 + 1)] = ((byte)(paramIndexColorModel.getGreen(j) & 0xFF));
        arrayOfByte[(j * 4 + 2)] = ((byte)(paramIndexColorModel.getRed(j) & 0xFF));
      }
    }
    drawDIBImage(getPrintDC(), paramArrayOfByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, i, arrayOfByte);
  }
  
  protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
  {
    invalidateCachedState();
    deviceStartPage(paramPageFormat, paramPrintable, paramInt, paramBoolean);
  }
  
  protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
  {
    deviceEndPage(paramPageFormat, paramPrintable, paramInt);
  }
  
  private void invalidateCachedState()
  {
    this.mLastColor = null;
    this.mLastTextColor = null;
    this.mLastFontFamily = null;
  }
  
  public void setCopies(int paramInt)
  {
    super.setCopies(paramInt);
    this.defaultCopies = false;
    this.mAttCopies = paramInt;
    setNativeCopies(paramInt);
  }
  
  private native void setNativeCopies(int paramInt);
  
  private native boolean jobSetup(Pageable paramPageable, boolean paramBoolean);
  
  protected native void initPrinter();
  
  private native boolean _startDoc(String paramString1, String paramString2)
    throws PrinterException;
  
  protected void startDoc()
    throws PrinterException
  {
    if (!_startDoc(this.mDestination, getJobName())) {
      cancel();
    }
  }
  
  protected native void endDoc();
  
  protected native void abortDoc();
  
  private static native void deleteDC(long paramLong1, long paramLong2, long paramLong3);
  
  protected native void deviceStartPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean);
  
  protected native void deviceEndPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt);
  
  protected native void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  protected native void beginPath(long paramLong);
  
  protected native void endPath(long paramLong);
  
  protected native void closeFigure(long paramLong);
  
  protected native void fillPath(long paramLong);
  
  protected native void moveTo(long paramLong, float paramFloat1, float paramFloat2);
  
  protected native void lineTo(long paramLong, float paramFloat1, float paramFloat2);
  
  protected native void polyBezierTo(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
  
  protected native void setPolyFillMode(long paramLong, int paramInt);
  
  protected native void selectSolidBrush(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  protected native int getPenX(long paramLong);
  
  protected native int getPenY(long paramLong);
  
  protected native void selectClipPath(long paramLong);
  
  protected native void frameRect(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  protected native void fillRect(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3);
  
  protected native void selectPen(long paramLong, float paramFloat, int paramInt1, int paramInt2, int paramInt3);
  
  protected native boolean selectStylePen(long paramLong1, long paramLong2, long paramLong3, float paramFloat, int paramInt1, int paramInt2, int paramInt3);
  
  protected native boolean setFont(long paramLong, String paramString, float paramFloat1, boolean paramBoolean1, boolean paramBoolean2, int paramInt, float paramFloat2);
  
  protected native void setTextColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  protected native void textOut(long paramLong, String paramString, int paramInt, boolean paramBoolean, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat);
  
  private native int getGDIAdvance(long paramLong, String paramString);
  
  private native void drawDIBImage(long paramLong, byte[] paramArrayOfByte1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt, byte[] paramArrayOfByte2);
  
  private final String getPrinterAttrib()
  {
    PrintService localPrintService = getPrintService();
    String str = localPrintService != null ? localPrintService.getName() : null;
    return str;
  }
  
  private final int getCollateAttrib()
  {
    return this.mAttCollate;
  }
  
  private void setCollateAttrib(Attribute paramAttribute)
  {
    if (paramAttribute == SheetCollate.COLLATED) {
      this.mAttCollate = 1;
    } else {
      this.mAttCollate = 0;
    }
  }
  
  private void setCollateAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    setCollateAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int getOrientAttrib()
  {
    int i = 1;
    OrientationRequested localOrientationRequested = this.attributes == null ? null : (OrientationRequested)this.attributes.get(OrientationRequested.class);
    if (localOrientationRequested == null) {
      localOrientationRequested = (OrientationRequested)this.myService.getDefaultAttributeValue(OrientationRequested.class);
    }
    if (localOrientationRequested != null) {
      if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
        i = 2;
      } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
        i = 0;
      }
    }
    return i;
  }
  
  private void setOrientAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    if (paramPrintRequestAttributeSet != null) {
      paramPrintRequestAttributeSet.add(paramAttribute);
    }
  }
  
  private final int getCopiesAttrib()
  {
    if (this.defaultCopies) {
      return 0;
    }
    return getCopiesInt();
  }
  
  private final void setRangeCopiesAttribute(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (this.attributes != null)
    {
      if (paramBoolean)
      {
        this.attributes.add(new PageRanges(paramInt1, paramInt2));
        setPageRange(paramInt1, paramInt2);
      }
      this.defaultCopies = false;
      this.attributes.add(new Copies(paramInt3));
      super.setCopies(paramInt3);
      this.mAttCopies = paramInt3;
    }
  }
  
  private final boolean getDestAttrib()
  {
    return this.mDestination != null;
  }
  
  private final int getQualityAttrib()
  {
    return this.mAttQuality;
  }
  
  private void setQualityAttrib(Attribute paramAttribute)
  {
    if (paramAttribute == PrintQuality.HIGH) {
      this.mAttQuality = -4;
    } else if (paramAttribute == PrintQuality.NORMAL) {
      this.mAttQuality = -3;
    } else {
      this.mAttQuality = -2;
    }
  }
  
  private void setQualityAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    setQualityAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int getColorAttrib()
  {
    return this.mAttChromaticity;
  }
  
  private void setColorAttrib(Attribute paramAttribute)
  {
    if (paramAttribute == Chromaticity.COLOR) {
      this.mAttChromaticity = 2;
    } else {
      this.mAttChromaticity = 1;
    }
  }
  
  private void setColorAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    setColorAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int getSidesAttrib()
  {
    return this.mAttSides;
  }
  
  private void setSidesAttrib(Attribute paramAttribute)
  {
    if (paramAttribute == Sides.TWO_SIDED_LONG_EDGE) {
      this.mAttSides = 2;
    } else if (paramAttribute == Sides.TWO_SIDED_SHORT_EDGE) {
      this.mAttSides = 3;
    } else {
      this.mAttSides = 1;
    }
  }
  
  private void setSidesAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    setSidesAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int[] getWin32MediaAttrib()
  {
    int[] arrayOfInt = { 0, 0 };
    if (this.attributes != null)
    {
      Media localMedia = (Media)this.attributes.get(Media.class);
      if ((localMedia instanceof MediaSizeName))
      {
        MediaSizeName localMediaSizeName = (MediaSizeName)localMedia;
        MediaSize localMediaSize = MediaSize.getMediaSizeForName(localMediaSizeName);
        if (localMediaSize != null)
        {
          arrayOfInt[0] = ((int)(localMediaSize.getX(25400) * 72.0D));
          arrayOfInt[1] = ((int)(localMediaSize.getY(25400) * 72.0D));
        }
      }
    }
    return arrayOfInt;
  }
  
  private void setWin32MediaAttrib(Attribute paramAttribute)
  {
    if (!(paramAttribute instanceof MediaSizeName)) {
      return;
    }
    MediaSizeName localMediaSizeName = (MediaSizeName)paramAttribute;
    this.mAttMediaSizeName = ((Win32PrintService)this.myService).findPaperID(localMediaSizeName);
  }
  
  private void addPaperSize(PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramPrintRequestAttributeSet == null) {
      return;
    }
    MediaSizeName localMediaSizeName = ((Win32PrintService)this.myService).findWin32Media(paramInt1);
    if (localMediaSizeName == null) {
      localMediaSizeName = ((Win32PrintService)this.myService).findMatchingMediaSizeNameMM(paramInt2, paramInt3);
    }
    if (localMediaSizeName != null) {
      paramPrintRequestAttributeSet.add(localMediaSizeName);
    }
  }
  
  private void setWin32MediaAttrib(int paramInt1, int paramInt2, int paramInt3)
  {
    addPaperSize(this.attributes, paramInt1, paramInt2, paramInt3);
    this.mAttMediaSizeName = paramInt1;
  }
  
  private void setMediaTrayAttrib(Attribute paramAttribute)
  {
    if (paramAttribute == MediaTray.BOTTOM) {
      this.mAttMediaTray = 2;
    } else if (paramAttribute == MediaTray.ENVELOPE) {
      this.mAttMediaTray = 5;
    } else if (paramAttribute == MediaTray.LARGE_CAPACITY) {
      this.mAttMediaTray = 11;
    } else if (paramAttribute == MediaTray.MAIN) {
      this.mAttMediaTray = 1;
    } else if (paramAttribute == MediaTray.MANUAL) {
      this.mAttMediaTray = 4;
    } else if (paramAttribute == MediaTray.MIDDLE) {
      this.mAttMediaTray = 3;
    } else if (paramAttribute == MediaTray.SIDE) {
      this.mAttMediaTray = 7;
    } else if (paramAttribute == MediaTray.TOP) {
      this.mAttMediaTray = 1;
    } else if ((paramAttribute instanceof Win32MediaTray)) {
      this.mAttMediaTray = ((Win32MediaTray)paramAttribute).winID;
    } else {
      this.mAttMediaTray = 1;
    }
  }
  
  private void setMediaTrayAttrib(int paramInt)
  {
    this.mAttMediaTray = paramInt;
    MediaTray localMediaTray = ((Win32PrintService)this.myService).findMediaTray(paramInt);
  }
  
  private int getMediaTrayAttrib()
  {
    return this.mAttMediaTray;
  }
  
  private final boolean getPrintToFileEnabled()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      FilePermission localFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
      try
      {
        localSecurityManager.checkPermission(localFilePermission);
      }
      catch (SecurityException localSecurityException)
      {
        return false;
      }
    }
    return true;
  }
  
  private final void setNativeAttributes(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.attributes == null) {
      return;
    }
    Object localObject;
    if ((paramInt1 & 0x20) != 0)
    {
      localObject = (Destination)this.attributes.get(Destination.class);
      if (localObject == null) {
        try
        {
          this.attributes.add(new Destination(new File("./out.prn").toURI()));
        }
        catch (SecurityException localSecurityException)
        {
          try
          {
            this.attributes.add(new Destination(new URI("file:out.prn")));
          }
          catch (URISyntaxException localURISyntaxException) {}
        }
      }
    }
    else
    {
      this.attributes.remove(Destination.class);
    }
    if ((paramInt1 & 0x10) != 0) {
      setCollateAttrib(SheetCollate.COLLATED, this.attributes);
    } else {
      setCollateAttrib(SheetCollate.UNCOLLATED, this.attributes);
    }
    if ((paramInt1 & 0x2) != 0) {
      this.attributes.add(SunPageSelection.RANGE);
    } else if ((paramInt1 & 0x1) != 0) {
      this.attributes.add(SunPageSelection.SELECTION);
    } else {
      this.attributes.add(SunPageSelection.ALL);
    }
    if ((paramInt2 & 0x1) != 0) {
      if ((paramInt3 & 0x4000) != 0) {
        setOrientAttrib(OrientationRequested.LANDSCAPE, this.attributes);
      } else {
        setOrientAttrib(OrientationRequested.PORTRAIT, this.attributes);
      }
    }
    if ((paramInt2 & 0x800) != 0) {
      if ((paramInt3 & 0x200) != 0) {
        setColorAttrib(Chromaticity.COLOR, this.attributes);
      } else {
        setColorAttrib(Chromaticity.MONOCHROME, this.attributes);
      }
    }
    if ((paramInt2 & 0x400) != 0)
    {
      if ((paramInt3 & 0x80) != 0) {
        localObject = PrintQuality.DRAFT;
      } else if ((paramInt2 & 0x40) != 0) {
        localObject = PrintQuality.HIGH;
      } else {
        localObject = PrintQuality.NORMAL;
      }
      setQualityAttrib((Attribute)localObject, this.attributes);
    }
    if ((paramInt2 & 0x1000) != 0)
    {
      if ((paramInt3 & 0x10) != 0) {
        localObject = Sides.TWO_SIDED_LONG_EDGE;
      } else if ((paramInt3 & 0x20) != 0) {
        localObject = Sides.TWO_SIDED_SHORT_EDGE;
      } else {
        localObject = Sides.ONE_SIDED;
      }
      setSidesAttrib((Attribute)localObject, this.attributes);
    }
  }
  
  private void getDevModeValues(PrintRequestAttributeSet paramPrintRequestAttributeSet, DevModeValues paramDevModeValues)
  {
    Copies localCopies = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
    if (localCopies != null)
    {
      paramDevModeValues.dmFields |= 0x100;
      paramDevModeValues.copies = ((short)localCopies.getValue());
    }
    SheetCollate localSheetCollate = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
    if (localSheetCollate != null)
    {
      paramDevModeValues.dmFields |= 0x8000;
      paramDevModeValues.collate = (localSheetCollate == SheetCollate.COLLATED ? 1 : 0);
    }
    Chromaticity localChromaticity = (Chromaticity)paramPrintRequestAttributeSet.get(Chromaticity.class);
    if (localChromaticity != null)
    {
      paramDevModeValues.dmFields |= 0x800;
      if (localChromaticity == Chromaticity.COLOR) {
        paramDevModeValues.color = 2;
      } else {
        paramDevModeValues.color = 1;
      }
    }
    Sides localSides = (Sides)paramPrintRequestAttributeSet.get(Sides.class);
    if (localSides != null)
    {
      paramDevModeValues.dmFields |= 0x1000;
      if (localSides == Sides.TWO_SIDED_LONG_EDGE) {
        paramDevModeValues.duplex = 2;
      } else if (localSides == Sides.TWO_SIDED_SHORT_EDGE) {
        paramDevModeValues.duplex = 3;
      } else {
        paramDevModeValues.duplex = 1;
      }
    }
    OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    if (localOrientationRequested != null)
    {
      paramDevModeValues.dmFields |= 0x1;
      paramDevModeValues.orient = (localOrientationRequested == OrientationRequested.LANDSCAPE ? 2 : 1);
    }
    Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
    if ((localMedia instanceof MediaSizeName))
    {
      paramDevModeValues.dmFields |= 0x2;
      localObject1 = (MediaSizeName)localMedia;
      paramDevModeValues.paper = ((short)((Win32PrintService)this.myService).findPaperID((MediaSizeName)localObject1));
    }
    Object localObject1 = null;
    if ((localMedia instanceof MediaTray)) {
      localObject1 = (MediaTray)localMedia;
    }
    if (localObject1 == null)
    {
      localObject2 = (SunAlternateMedia)paramPrintRequestAttributeSet.get(SunAlternateMedia.class);
      if ((localObject2 != null) && ((((SunAlternateMedia)localObject2).getMedia() instanceof MediaTray))) {
        localObject1 = (MediaTray)((SunAlternateMedia)localObject2).getMedia();
      }
    }
    if (localObject1 != null)
    {
      paramDevModeValues.dmFields |= 0x200;
      paramDevModeValues.bin = ((short)((Win32PrintService)this.myService).findTrayID((MediaTray)localObject1));
    }
    Object localObject2 = (PrintQuality)paramPrintRequestAttributeSet.get(PrintQuality.class);
    if (localObject2 != null)
    {
      paramDevModeValues.dmFields |= 0x400;
      if (localObject2 == PrintQuality.DRAFT) {
        paramDevModeValues.xres_quality = -1;
      } else if (localObject2 == PrintQuality.HIGH) {
        paramDevModeValues.xres_quality = -4;
      } else {
        paramDevModeValues.xres_quality = -3;
      }
    }
    PrinterResolution localPrinterResolution = (PrinterResolution)paramPrintRequestAttributeSet.get(PrinterResolution.class);
    if (localPrinterResolution != null)
    {
      paramDevModeValues.dmFields |= 0x2400;
      paramDevModeValues.xres_quality = ((short)localPrinterResolution.getCrossFeedResolution(100));
      paramDevModeValues.yres = ((short)localPrinterResolution.getFeedResolution(100));
    }
  }
  
  private final void setJobAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt1, int paramInt2, short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5, short paramShort6, short paramShort7)
  {
    if (paramPrintRequestAttributeSet == null) {
      return;
    }
    if ((paramInt1 & 0x100) != 0) {
      paramPrintRequestAttributeSet.add(new Copies(paramShort1));
    }
    if ((paramInt1 & 0x8000) != 0) {
      if ((paramInt2 & 0x8000) != 0) {
        paramPrintRequestAttributeSet.add(SheetCollate.COLLATED);
      } else {
        paramPrintRequestAttributeSet.add(SheetCollate.UNCOLLATED);
      }
    }
    if ((paramInt1 & 0x1) != 0) {
      if ((paramInt2 & 0x4000) != 0) {
        paramPrintRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
      } else {
        paramPrintRequestAttributeSet.add(OrientationRequested.PORTRAIT);
      }
    }
    if ((paramInt1 & 0x800) != 0) {
      if ((paramInt2 & 0x200) != 0) {
        paramPrintRequestAttributeSet.add(Chromaticity.COLOR);
      } else {
        paramPrintRequestAttributeSet.add(Chromaticity.MONOCHROME);
      }
    }
    Object localObject;
    if ((paramInt1 & 0x400) != 0) {
      if (paramShort6 < 0)
      {
        if ((paramInt2 & 0x80) != 0) {
          localObject = PrintQuality.DRAFT;
        } else if ((paramInt1 & 0x40) != 0) {
          localObject = PrintQuality.HIGH;
        } else {
          localObject = PrintQuality.NORMAL;
        }
        paramPrintRequestAttributeSet.add((Attribute)localObject);
      }
      else if ((paramShort6 > 0) && (paramShort7 > 0))
      {
        paramPrintRequestAttributeSet.add(new PrinterResolution(paramShort6, paramShort7, 100));
      }
    }
    if ((paramInt1 & 0x1000) != 0)
    {
      if ((paramInt2 & 0x10) != 0) {
        localObject = Sides.TWO_SIDED_LONG_EDGE;
      } else if ((paramInt2 & 0x20) != 0) {
        localObject = Sides.TWO_SIDED_SHORT_EDGE;
      } else {
        localObject = Sides.ONE_SIDED;
      }
      paramPrintRequestAttributeSet.add((Attribute)localObject);
    }
    if ((paramInt1 & 0x2) != 0) {
      addPaperSize(paramPrintRequestAttributeSet, paramShort2, paramShort3, paramShort4);
    }
    if ((paramInt1 & 0x200) != 0)
    {
      localObject = ((Win32PrintService)this.myService).findMediaTray(paramShort5);
      paramPrintRequestAttributeSet.add(new SunAlternateMedia((Media)localObject));
    }
  }
  
  private native boolean showDocProperties(long paramLong, PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt, short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5, short paramShort6, short paramShort7, short paramShort8, short paramShort9);
  
  public PrintRequestAttributeSet showDocumentProperties(Window paramWindow, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    try
    {
      setNativePrintServiceIfNeeded(paramPrintService.getName());
    }
    catch (PrinterException localPrinterException) {}
    long l = ((WWindowPeer)paramWindow.getPeer()).getHWnd();
    DevModeValues localDevModeValues = new DevModeValues(null);
    getDevModeValues(paramPrintRequestAttributeSet, localDevModeValues);
    boolean bool = showDocProperties(l, paramPrintRequestAttributeSet, localDevModeValues.dmFields, localDevModeValues.copies, localDevModeValues.collate, localDevModeValues.color, localDevModeValues.duplex, localDevModeValues.orient, localDevModeValues.paper, localDevModeValues.bin, localDevModeValues.xres_quality, localDevModeValues.yres);
    if (bool) {
      return paramPrintRequestAttributeSet;
    }
    return null;
  }
  
  private final void setResolutionDPI(int paramInt1, int paramInt2)
  {
    if (this.attributes != null)
    {
      PrinterResolution localPrinterResolution = new PrinterResolution(paramInt1, paramInt2, 100);
      this.attributes.add(localPrinterResolution);
    }
    this.mAttXRes = paramInt1;
    this.mAttYRes = paramInt2;
  }
  
  private void setResolutionAttrib(Attribute paramAttribute)
  {
    PrinterResolution localPrinterResolution = (PrinterResolution)paramAttribute;
    this.mAttXRes = localPrinterResolution.getCrossFeedResolution(100);
    this.mAttYRes = localPrinterResolution.getFeedResolution(100);
  }
  
  private void setPrinterNameAttrib(String paramString)
  {
    PrintService localPrintService = getPrintService();
    if (paramString == null) {
      return;
    }
    if ((localPrintService != null) && (paramString.equals(localPrintService.getName()))) {
      return;
    }
    PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
    for (int i = 0; i < arrayOfPrintService.length; i++) {
      if (paramString.equals(arrayOfPrintService[i].getName()))
      {
        try
        {
          setPrintService(arrayOfPrintService[i]);
        }
        catch (PrinterException localPrinterException) {}
        return;
      }
    }
  }
  
  private static native void initIDs();
  
  static
  {
    Toolkit.getDefaultToolkit();
    initIDs();
    Win32FontManager.registerJREFontsForPrinting();
  }
  
  private static final class DevModeValues
  {
    int dmFields;
    short copies;
    short collate;
    short color;
    short duplex;
    short orient;
    short paper;
    short bin;
    short xres_quality;
    short yres;
    
    private DevModeValues() {}
  }
  
  static class HandleRecord
    implements DisposerRecord
  {
    private long mPrintDC;
    private long mPrintHDevMode;
    private long mPrintHDevNames;
    
    HandleRecord() {}
    
    public void dispose()
    {
      WPrinterJob.deleteDC(this.mPrintDC, this.mPrintHDevMode, this.mPrintHDevNames);
    }
  }
  
  class PrintToFileErrorDialog
    extends Dialog
    implements ActionListener
  {
    public PrintToFileErrorDialog(Frame paramFrame, String paramString1, String paramString2, String paramString3)
    {
      super(paramString1, true);
      init(paramFrame, paramString1, paramString2, paramString3);
    }
    
    public PrintToFileErrorDialog(Dialog paramDialog, String paramString1, String paramString2, String paramString3)
    {
      super(paramString1, true);
      init(paramDialog, paramString1, paramString2, paramString3);
    }
    
    private void init(Component paramComponent, String paramString1, String paramString2, String paramString3)
    {
      Panel localPanel = new Panel();
      add("Center", new Label(paramString2));
      Button localButton = new Button(paramString3);
      localButton.addActionListener(this);
      localPanel.add(localButton);
      add("South", localPanel);
      pack();
      Dimension localDimension = getSize();
      if (paramComponent != null)
      {
        Rectangle localRectangle = paramComponent.getBounds();
        setLocation(localRectangle.x + (localRectangle.width - localDimension.width) / 2, localRectangle.y + (localRectangle.height - localDimension.height) / 2);
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      setVisible(false);
      dispose();
    }
  }
}
