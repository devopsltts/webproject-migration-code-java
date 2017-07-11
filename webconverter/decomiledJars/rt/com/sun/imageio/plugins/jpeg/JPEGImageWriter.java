package com.sun.imageio.plugins.jpeg;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageWriter
  extends ImageWriter
{
  private boolean debug = false;
  private long structPointer = 0L;
  private ImageOutputStream ios = null;
  private Raster srcRas = null;
  private WritableRaster raster = null;
  private boolean indexed = false;
  private IndexColorModel indexCM = null;
  private boolean convertTosRGB = false;
  private WritableRaster converted = null;
  private boolean isAlphaPremultiplied = false;
  private ColorModel srcCM = null;
  private List thumbnails = null;
  private ICC_Profile iccProfile = null;
  private int sourceXOffset = 0;
  private int sourceYOffset = 0;
  private int sourceWidth = 0;
  private int[] srcBands = null;
  private int sourceHeight = 0;
  private int currentImage = 0;
  private ColorConvertOp convertOp = null;
  private JPEGQTable[] streamQTables = null;
  private JPEGHuffmanTable[] streamDCHuffmanTables = null;
  private JPEGHuffmanTable[] streamACHuffmanTables = null;
  private boolean ignoreJFIF = false;
  private boolean forceJFIF = false;
  private boolean ignoreAdobe = false;
  private int newAdobeTransform = -1;
  private boolean writeDefaultJFIF = false;
  private boolean writeAdobe = false;
  private JPEGMetadata metadata = null;
  private boolean sequencePrepared = false;
  private int numScans = 0;
  private Object disposerReferent = new Object();
  private DisposerRecord disposerRecord = new JPEGWriterDisposerRecord(this.structPointer);
  protected static final int WARNING_DEST_IGNORED = 0;
  protected static final int WARNING_STREAM_METADATA_IGNORED = 1;
  protected static final int WARNING_DEST_METADATA_COMP_MISMATCH = 2;
  protected static final int WARNING_DEST_METADATA_JFIF_MISMATCH = 3;
  protected static final int WARNING_DEST_METADATA_ADOBE_MISMATCH = 4;
  protected static final int WARNING_IMAGE_METADATA_JFIF_MISMATCH = 5;
  protected static final int WARNING_IMAGE_METADATA_ADOBE_MISMATCH = 6;
  protected static final int WARNING_METADATA_NOT_JPEG_FOR_RASTER = 7;
  protected static final int WARNING_NO_BANDS_ON_INDEXED = 8;
  protected static final int WARNING_ILLEGAL_THUMBNAIL = 9;
  protected static final int WARNING_IGNORING_THUMBS = 10;
  protected static final int WARNING_FORCING_JFIF = 11;
  protected static final int WARNING_THUMB_CLIPPED = 12;
  protected static final int WARNING_METADATA_ADJUSTED_FOR_THUMB = 13;
  protected static final int WARNING_NO_RGB_THUMB_AS_INDEXED = 14;
  protected static final int WARNING_NO_GRAY_THUMB_AS_INDEXED = 15;
  private static final int MAX_WARNING = 15;
  static final Dimension[] preferredThumbSizes = { new Dimension(1, 1), new Dimension(255, 255) };
  private Thread theThread = null;
  private int theLockCount = 0;
  private CallBackLock cbLock = new CallBackLock();
  
  public JPEGImageWriter(ImageWriterSpi paramImageWriterSpi)
  {
    super(paramImageWriterSpi);
    Disposer.addRecord(this.disposerReferent, this.disposerRecord);
  }
  
  public void setOutput(Object paramObject)
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      super.setOutput(paramObject);
      resetInternalState();
      this.ios = ((ImageOutputStream)paramObject);
      setDest(this.structPointer);
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public ImageWriteParam getDefaultWriteParam()
  {
    return new JPEGImageWriteParam(null);
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam)
  {
    setThreadLock();
    try
    {
      JPEGMetadata localJPEGMetadata = new JPEGMetadata(paramImageWriteParam, this);
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    setThreadLock();
    try
    {
      JPEGMetadata localJPEGMetadata = new JPEGMetadata(paramImageTypeSpecifier, paramImageWriteParam, this);
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam)
  {
    if ((paramIIOMetadata instanceof JPEGMetadata))
    {
      JPEGMetadata localJPEGMetadata = (JPEGMetadata)paramIIOMetadata;
      if (localJPEGMetadata.isStream) {
        return paramIIOMetadata;
      }
    }
    return null;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    setThreadLock();
    try
    {
      IIOMetadata localIIOMetadata = convertImageMetadataOnThread(paramIIOMetadata, paramImageTypeSpecifier, paramImageWriteParam);
      return localIIOMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private IIOMetadata convertImageMetadataOnThread(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    Object localObject;
    if ((paramIIOMetadata instanceof JPEGMetadata))
    {
      localObject = (JPEGMetadata)paramIIOMetadata;
      if (!((JPEGMetadata)localObject).isStream) {
        return paramIIOMetadata;
      }
      return null;
    }
    if (paramIIOMetadata.isStandardMetadataFormatSupported())
    {
      localObject = "javax_imageio_1.0";
      Node localNode = paramIIOMetadata.getAsTree((String)localObject);
      if (localNode != null)
      {
        JPEGMetadata localJPEGMetadata = new JPEGMetadata(paramImageTypeSpecifier, paramImageWriteParam, this);
        try
        {
          localJPEGMetadata.setFromTree((String)localObject, localNode);
        }
        catch (IIOInvalidTreeException localIIOInvalidTreeException)
        {
          return null;
        }
        return localJPEGMetadata;
      }
    }
    return null;
  }
  
  public int getNumThumbnailsSupported(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    if (jfifOK(paramImageTypeSpecifier, paramImageWriteParam, paramIIOMetadata1, paramIIOMetadata2)) {
      return Integer.MAX_VALUE;
    }
    return 0;
  }
  
  public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    if (jfifOK(paramImageTypeSpecifier, paramImageWriteParam, paramIIOMetadata1, paramIIOMetadata2)) {
      return (Dimension[])preferredThumbSizes.clone();
    }
    return null;
  }
  
  private boolean jfifOK(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    if ((paramImageTypeSpecifier != null) && (!JPEG.isJFIFcompliant(paramImageTypeSpecifier, true))) {
      return false;
    }
    if (paramIIOMetadata2 != null)
    {
      JPEGMetadata localJPEGMetadata = null;
      if ((paramIIOMetadata2 instanceof JPEGMetadata)) {
        localJPEGMetadata = (JPEGMetadata)paramIIOMetadata2;
      } else {
        localJPEGMetadata = (JPEGMetadata)convertImageMetadata(paramIIOMetadata2, paramImageTypeSpecifier, paramImageWriteParam);
      }
      if (localJPEGMetadata.findMarkerSegment(JFIFMarkerSegment.class, true) == null) {
        return false;
      }
    }
    return true;
  }
  
  public boolean canWriteRasters()
  {
    return true;
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      writeOnThread(paramIIOMetadata, paramIIOImage, paramImageWriteParam);
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private void writeOnThread(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    if (this.ios == null) {
      throw new IllegalStateException("Output has not been set!");
    }
    if (paramIIOImage == null) {
      throw new IllegalArgumentException("image is null!");
    }
    if (paramIIOMetadata != null) {
      warningOccurred(1);
    }
    boolean bool1 = paramIIOImage.hasRaster();
    RenderedImage localRenderedImage = null;
    if (bool1)
    {
      this.srcRas = paramIIOImage.getRaster();
    }
    else
    {
      localRenderedImage = paramIIOImage.getRenderedImage();
      if ((localRenderedImage instanceof BufferedImage))
      {
        this.srcRas = ((BufferedImage)localRenderedImage).getRaster();
      }
      else if ((localRenderedImage.getNumXTiles() == 1) && (localRenderedImage.getNumYTiles() == 1))
      {
        this.srcRas = localRenderedImage.getTile(localRenderedImage.getMinTileX(), localRenderedImage.getMinTileY());
        if ((this.srcRas.getWidth() != localRenderedImage.getWidth()) || (this.srcRas.getHeight() != localRenderedImage.getHeight())) {
          this.srcRas = this.srcRas.createChild(this.srcRas.getMinX(), this.srcRas.getMinY(), localRenderedImage.getWidth(), localRenderedImage.getHeight(), this.srcRas.getMinX(), this.srcRas.getMinY(), null);
        }
      }
      else
      {
        this.srcRas = localRenderedImage.getData();
      }
    }
    int i = this.srcRas.getNumBands();
    this.indexed = false;
    this.indexCM = null;
    ColorModel localColorModel = null;
    ColorSpace localColorSpace = null;
    this.isAlphaPremultiplied = false;
    this.srcCM = null;
    if (!bool1)
    {
      localColorModel = localRenderedImage.getColorModel();
      if (localColorModel != null)
      {
        localColorSpace = localColorModel.getColorSpace();
        if ((localColorModel instanceof IndexColorModel))
        {
          this.indexed = true;
          this.indexCM = ((IndexColorModel)localColorModel);
          i = localColorModel.getNumComponents();
        }
        if (localColorModel.isAlphaPremultiplied())
        {
          this.isAlphaPremultiplied = true;
          this.srcCM = localColorModel;
        }
      }
    }
    this.srcBands = JPEG.bandOffsets[(i - 1)];
    int j = i;
    if (paramImageWriteParam != null)
    {
      int[] arrayOfInt1 = paramImageWriteParam.getSourceBands();
      if (arrayOfInt1 != null) {
        if (this.indexed)
        {
          warningOccurred(8);
        }
        else
        {
          this.srcBands = arrayOfInt1;
          j = this.srcBands.length;
          if (j > i) {
            throw new IIOException("ImageWriteParam specifies too many source bands");
          }
        }
      }
    }
    boolean bool2 = j != i;
    boolean bool3 = (!bool1) && (!bool2);
    Object localObject1 = null;
    int[] arrayOfInt2;
    if (!this.indexed)
    {
      localObject1 = this.srcRas.getSampleModel().getSampleSize();
      if (bool2)
      {
        arrayOfInt2 = new int[j];
        for (m = 0; m < j; m++) {
          arrayOfInt2[m] = localObject1[this.srcBands[m]];
        }
        localObject1 = arrayOfInt2;
      }
    }
    else
    {
      arrayOfInt2 = this.srcRas.getSampleModel().getSampleSize();
      localObject1 = new int[i];
      for (m = 0; m < i; m++) {
        localObject1[m] = arrayOfInt2[0];
      }
    }
    for (int k = 0; k < localObject1.length; k++)
    {
      if ((localObject1[k] <= 0) || (localObject1[k] > 8)) {
        throw new IIOException("Illegal band size: should be 0 < size <= 8");
      }
      if (this.indexed) {
        localObject1[k] = 8;
      }
    }
    if (this.debug)
    {
      System.out.println("numSrcBands is " + i);
      System.out.println("numBandsUsed is " + j);
      System.out.println("usingBandSubset is " + bool2);
      System.out.println("fullImage is " + bool3);
      System.out.print("Band sizes:");
      for (k = 0; k < localObject1.length; k++) {
        System.out.print(" " + localObject1[k]);
      }
      System.out.println();
    }
    ImageTypeSpecifier localImageTypeSpecifier1 = null;
    if (paramImageWriteParam != null)
    {
      localImageTypeSpecifier1 = paramImageWriteParam.getDestinationType();
      if ((bool3) && (localImageTypeSpecifier1 != null))
      {
        warningOccurred(0);
        localImageTypeSpecifier1 = null;
      }
    }
    this.sourceXOffset = this.srcRas.getMinX();
    this.sourceYOffset = this.srcRas.getMinY();
    int m = this.srcRas.getWidth();
    int n = this.srcRas.getHeight();
    this.sourceWidth = m;
    this.sourceHeight = n;
    int i1 = 1;
    int i2 = 1;
    int i3 = 0;
    int i4 = 0;
    JPEGQTable[] arrayOfJPEGQTable = null;
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable1 = null;
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable2 = null;
    boolean bool4 = false;
    JPEGImageWriteParam localJPEGImageWriteParam = null;
    int i5 = 0;
    if (paramImageWriteParam != null)
    {
      localObject2 = paramImageWriteParam.getSourceRegion();
      if (localObject2 != null)
      {
        Rectangle localRectangle = new Rectangle(this.sourceXOffset, this.sourceYOffset, this.sourceWidth, this.sourceHeight);
        localObject2 = ((Rectangle)localObject2).intersection(localRectangle);
        this.sourceXOffset = ((Rectangle)localObject2).x;
        this.sourceYOffset = ((Rectangle)localObject2).y;
        this.sourceWidth = ((Rectangle)localObject2).width;
        this.sourceHeight = ((Rectangle)localObject2).height;
      }
      if (this.sourceWidth + this.sourceXOffset > m) {
        this.sourceWidth = (m - this.sourceXOffset);
      }
      if (this.sourceHeight + this.sourceYOffset > n) {
        this.sourceHeight = (n - this.sourceYOffset);
      }
      i1 = paramImageWriteParam.getSourceXSubsampling();
      i2 = paramImageWriteParam.getSourceYSubsampling();
      i3 = paramImageWriteParam.getSubsamplingXOffset();
      i4 = paramImageWriteParam.getSubsamplingYOffset();
      switch (paramImageWriteParam.getCompressionMode())
      {
      case 0: 
        throw new IIOException("JPEG compression cannot be disabled");
      case 2: 
        float f = paramImageWriteParam.getCompressionQuality();
        f = JPEG.convertToLinearQuality(f);
        arrayOfJPEGQTable = new JPEGQTable[2];
        arrayOfJPEGQTable[0] = JPEGQTable.K1Luminance.getScaledInstance(f, true);
        arrayOfJPEGQTable[1] = JPEGQTable.K2Chrominance.getScaledInstance(f, true);
        break;
      case 1: 
        arrayOfJPEGQTable = new JPEGQTable[2];
        arrayOfJPEGQTable[0] = JPEGQTable.K1Div2Luminance;
        arrayOfJPEGQTable[1] = JPEGQTable.K2Div2Chrominance;
      }
      i5 = paramImageWriteParam.getProgressiveMode();
      if ((paramImageWriteParam instanceof JPEGImageWriteParam))
      {
        localJPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
        bool4 = localJPEGImageWriteParam.getOptimizeHuffmanTables();
      }
    }
    Object localObject2 = paramIIOImage.getMetadata();
    if (localObject2 != null) {
      if ((localObject2 instanceof JPEGMetadata))
      {
        this.metadata = ((JPEGMetadata)localObject2);
        if (this.debug) {
          System.out.println("We have metadata, and it's JPEG metadata");
        }
      }
      else if (!bool1)
      {
        ImageTypeSpecifier localImageTypeSpecifier2 = localImageTypeSpecifier1;
        if (localImageTypeSpecifier2 == null) {
          localImageTypeSpecifier2 = new ImageTypeSpecifier(localRenderedImage);
        }
        this.metadata = ((JPEGMetadata)convertImageMetadata((IIOMetadata)localObject2, localImageTypeSpecifier2, paramImageWriteParam));
      }
      else
      {
        warningOccurred(7);
      }
    }
    this.ignoreJFIF = false;
    this.ignoreAdobe = false;
    this.newAdobeTransform = -1;
    this.writeDefaultJFIF = false;
    this.writeAdobe = false;
    int i6 = 0;
    int i7 = 0;
    JFIFMarkerSegment localJFIFMarkerSegment = null;
    AdobeMarkerSegment localAdobeMarkerSegment = null;
    SOFMarkerSegment localSOFMarkerSegment = null;
    if (this.metadata != null)
    {
      localJFIFMarkerSegment = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      localAdobeMarkerSegment = (AdobeMarkerSegment)this.metadata.findMarkerSegment(AdobeMarkerSegment.class, true);
      localSOFMarkerSegment = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
    }
    this.iccProfile = null;
    this.convertTosRGB = false;
    this.converted = null;
    if (localImageTypeSpecifier1 != null)
    {
      if (j != localImageTypeSpecifier1.getNumBands()) {
        throw new IIOException("Number of source bands != number of destination bands");
      }
      localColorSpace = localImageTypeSpecifier1.getColorModel().getColorSpace();
      if (this.metadata != null)
      {
        checkSOFBands(localSOFMarkerSegment, j);
        checkJFIF(localJFIFMarkerSegment, localImageTypeSpecifier1, false);
        if ((localJFIFMarkerSegment != null) && (!this.ignoreJFIF) && (JPEG.isNonStandardICC(localColorSpace))) {
          this.iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
        }
        checkAdobe(localAdobeMarkerSegment, localImageTypeSpecifier1, false);
      }
      else
      {
        if (JPEG.isJFIFcompliant(localImageTypeSpecifier1, false))
        {
          this.writeDefaultJFIF = true;
          if (JPEG.isNonStandardICC(localColorSpace)) {
            this.iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
          }
        }
        else
        {
          int i8 = JPEG.transformForType(localImageTypeSpecifier1, false);
          if (i8 != -1)
          {
            this.writeAdobe = true;
            this.newAdobeTransform = i8;
          }
        }
        this.metadata = new JPEGMetadata(localImageTypeSpecifier1, null, this);
      }
      i6 = getSrcCSType(localImageTypeSpecifier1);
      i7 = getDefaultDestCSType(localImageTypeSpecifier1);
    }
    else if (this.metadata == null)
    {
      if (bool3)
      {
        this.metadata = new JPEGMetadata(new ImageTypeSpecifier(localRenderedImage), paramImageWriteParam, this);
        if (this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true) != null)
        {
          localColorSpace = localRenderedImage.getColorModel().getColorSpace();
          if (JPEG.isNonStandardICC(localColorSpace)) {
            this.iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
          }
        }
        i6 = getSrcCSType(localRenderedImage);
        i7 = getDefaultDestCSType(localRenderedImage);
      }
    }
    else
    {
      checkSOFBands(localSOFMarkerSegment, j);
      if (bool3)
      {
        ImageTypeSpecifier localImageTypeSpecifier3 = new ImageTypeSpecifier(localRenderedImage);
        i6 = getSrcCSType(localRenderedImage);
        if (localColorModel != null)
        {
          boolean bool5 = localColorModel.hasAlpha();
          switch (localColorSpace.getType())
          {
          case 6: 
            if (!bool5)
            {
              i7 = 1;
            }
            else if (localJFIFMarkerSegment != null)
            {
              this.ignoreJFIF = true;
              warningOccurred(5);
            }
            if ((localAdobeMarkerSegment != null) && (localAdobeMarkerSegment.transform != 0))
            {
              this.newAdobeTransform = 0;
              warningOccurred(6);
            }
            break;
          case 5: 
            if (!bool5)
            {
              if (localJFIFMarkerSegment != null)
              {
                i7 = 3;
                if ((JPEG.isNonStandardICC(localColorSpace)) || (((localColorSpace instanceof ICC_ColorSpace)) && (localJFIFMarkerSegment.iccSegment != null))) {
                  this.iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
                }
              }
              else if (localAdobeMarkerSegment != null)
              {
                switch (localAdobeMarkerSegment.transform)
                {
                case 0: 
                  i7 = 2;
                  break;
                case 1: 
                  i7 = 3;
                  break;
                default: 
                  warningOccurred(6);
                  this.newAdobeTransform = 0;
                  i7 = 2;
                  break;
                }
              }
              else
              {
                i10 = localSOFMarkerSegment.getIDencodedCSType();
                if (i10 != 0)
                {
                  i7 = i10;
                }
                else
                {
                  bool6 = isSubsampled(localSOFMarkerSegment.componentSpecs);
                  if (bool6) {
                    i7 = 3;
                  } else {
                    i7 = 2;
                  }
                }
              }
            }
            else
            {
              if (localJFIFMarkerSegment != null)
              {
                this.ignoreJFIF = true;
                warningOccurred(5);
              }
              if (localAdobeMarkerSegment != null)
              {
                if (localAdobeMarkerSegment.transform != 0)
                {
                  this.newAdobeTransform = 0;
                  warningOccurred(6);
                }
                i7 = 6;
              }
              else
              {
                i10 = localSOFMarkerSegment.getIDencodedCSType();
                if (i10 != 0)
                {
                  i7 = i10;
                }
                else
                {
                  bool6 = isSubsampled(localSOFMarkerSegment.componentSpecs);
                  i7 = bool6 ? 7 : 6;
                }
              }
            }
            break;
          case 13: 
            if (localColorSpace == JPEG.JCS.getYCC()) {
              if (!bool5)
              {
                if (localJFIFMarkerSegment != null)
                {
                  this.convertTosRGB = true;
                  this.convertOp = new ColorConvertOp(localColorSpace, JPEG.JCS.sRGB, null);
                  i7 = 3;
                }
                else if (localAdobeMarkerSegment != null)
                {
                  if (localAdobeMarkerSegment.transform != 1)
                  {
                    this.newAdobeTransform = 1;
                    warningOccurred(6);
                  }
                  i7 = 5;
                }
                else
                {
                  i7 = 5;
                }
              }
              else
              {
                if (localJFIFMarkerSegment != null)
                {
                  this.ignoreJFIF = true;
                  warningOccurred(5);
                }
                else if ((localAdobeMarkerSegment != null) && (localAdobeMarkerSegment.transform != 0))
                {
                  this.newAdobeTransform = 0;
                  warningOccurred(6);
                }
                i7 = 10;
              }
            }
            break;
          }
        }
      }
    }
    int i9 = 0;
    int[] arrayOfInt3 = null;
    if (this.metadata != null)
    {
      if (localSOFMarkerSegment == null) {
        localSOFMarkerSegment = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
      }
      if ((localSOFMarkerSegment != null) && (localSOFMarkerSegment.tag == 194))
      {
        i9 = 1;
        if (i5 == 3) {
          arrayOfInt3 = collectScans(this.metadata, localSOFMarkerSegment);
        } else {
          this.numScans = 0;
        }
      }
      if (localJFIFMarkerSegment == null) {
        localJFIFMarkerSegment = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      }
    }
    this.thumbnails = paramIIOImage.getThumbnails();
    int i10 = paramIIOImage.getNumThumbnails();
    this.forceJFIF = false;
    if (!this.writeDefaultJFIF) {
      if (this.metadata == null)
      {
        this.thumbnails = null;
        if (i10 != 0) {
          warningOccurred(10);
        }
      }
      else if (!bool3)
      {
        if (localJFIFMarkerSegment == null)
        {
          this.thumbnails = null;
          if (i10 != 0) {
            warningOccurred(10);
          }
        }
      }
      else if (localJFIFMarkerSegment == null)
      {
        if ((i7 == 1) || (i7 == 3))
        {
          if (i10 != 0)
          {
            this.forceJFIF = true;
            warningOccurred(11);
          }
        }
        else
        {
          this.thumbnails = null;
          if (i10 != 0) {
            warningOccurred(10);
          }
        }
      }
    }
    boolean bool6 = (this.metadata != null) || (this.writeDefaultJFIF) || (this.writeAdobe);
    boolean bool7 = true;
    boolean bool8 = true;
    DQTMarkerSegment localDQTMarkerSegment = null;
    DHTMarkerSegment localDHTMarkerSegment = null;
    int i11 = 0;
    if (this.metadata != null)
    {
      localDQTMarkerSegment = (DQTMarkerSegment)this.metadata.findMarkerSegment(DQTMarkerSegment.class, true);
      localDHTMarkerSegment = (DHTMarkerSegment)this.metadata.findMarkerSegment(DHTMarkerSegment.class, true);
      localObject3 = (DRIMarkerSegment)this.metadata.findMarkerSegment(DRIMarkerSegment.class, true);
      if (localObject3 != null) {
        i11 = ((DRIMarkerSegment)localObject3).restartInterval;
      }
      if (localDQTMarkerSegment == null) {
        bool7 = false;
      }
      if (localDHTMarkerSegment == null) {
        bool8 = false;
      }
    }
    if (arrayOfJPEGQTable == null) {
      if (localDQTMarkerSegment != null) {
        arrayOfJPEGQTable = collectQTablesFromMetadata(this.metadata);
      } else if (this.streamQTables != null) {
        arrayOfJPEGQTable = this.streamQTables;
      } else if ((localJPEGImageWriteParam != null) && (localJPEGImageWriteParam.areTablesSet())) {
        arrayOfJPEGQTable = localJPEGImageWriteParam.getQTables();
      } else {
        arrayOfJPEGQTable = JPEG.getDefaultQTables();
      }
    }
    if (!bool4) {
      if ((localDHTMarkerSegment != null) && (i9 == 0))
      {
        arrayOfJPEGHuffmanTable1 = collectHTablesFromMetadata(this.metadata, true);
        arrayOfJPEGHuffmanTable2 = collectHTablesFromMetadata(this.metadata, false);
      }
      else if (this.streamDCHuffmanTables != null)
      {
        arrayOfJPEGHuffmanTable1 = this.streamDCHuffmanTables;
        arrayOfJPEGHuffmanTable2 = this.streamACHuffmanTables;
      }
      else if ((localJPEGImageWriteParam != null) && (localJPEGImageWriteParam.areTablesSet()))
      {
        arrayOfJPEGHuffmanTable1 = localJPEGImageWriteParam.getDCHuffmanTables();
        arrayOfJPEGHuffmanTable2 = localJPEGImageWriteParam.getACHuffmanTables();
      }
      else
      {
        arrayOfJPEGHuffmanTable1 = JPEG.getDefaultHuffmanTables(true);
        arrayOfJPEGHuffmanTable2 = JPEG.getDefaultHuffmanTables(false);
      }
    }
    Object localObject3 = new int[j];
    int[] arrayOfInt4 = new int[j];
    int[] arrayOfInt5 = new int[j];
    int[] arrayOfInt6 = new int[j];
    for (int i12 = 0; i12 < j; i12++)
    {
      localObject3[i12] = (i12 + 1);
      arrayOfInt4[i12] = 1;
      arrayOfInt5[i12] = 1;
      arrayOfInt6[i12] = 0;
    }
    if (localSOFMarkerSegment != null) {
      for (i12 = 0; i12 < j; i12++)
      {
        if (!this.forceJFIF) {
          localObject3[i12] = localSOFMarkerSegment.componentSpecs[i12].componentId;
        }
        arrayOfInt4[i12] = localSOFMarkerSegment.componentSpecs[i12].HsamplingFactor;
        arrayOfInt5[i12] = localSOFMarkerSegment.componentSpecs[i12].VsamplingFactor;
        arrayOfInt6[i12] = localSOFMarkerSegment.componentSpecs[i12].QtableSelector;
      }
    }
    this.sourceXOffset += i3;
    this.sourceWidth -= i3;
    this.sourceYOffset += i4;
    this.sourceHeight -= i4;
    i12 = (this.sourceWidth + i1 - 1) / i1;
    int i13 = (this.sourceHeight + i2 - 1) / i2;
    int i14 = this.sourceWidth * j;
    DataBufferByte localDataBufferByte = new DataBufferByte(i14);
    int[] arrayOfInt7 = JPEG.bandOffsets[(j - 1)];
    this.raster = Raster.createInterleavedRaster(localDataBufferByte, this.sourceWidth, 1, i14, j, arrayOfInt7, null);
    clearAbortRequest();
    this.cbLock.lock();
    try
    {
      processImageStarted(this.currentImage);
    }
    finally
    {
      this.cbLock.unlock();
    }
    boolean bool9 = false;
    if (this.debug)
    {
      System.out.println("inCsType: " + i6);
      System.out.println("outCsType: " + i7);
    }
    bool9 = writeImage(this.structPointer, localDataBufferByte.getData(), i6, i7, j, (int[])localObject1, this.sourceWidth, i12, i13, i1, i2, arrayOfJPEGQTable, bool7, arrayOfJPEGHuffmanTable1, arrayOfJPEGHuffmanTable2, bool8, bool4, i5 != 0, this.numScans, arrayOfInt3, (int[])localObject3, arrayOfInt4, arrayOfInt5, arrayOfInt6, bool6, i11);
    this.cbLock.lock();
    try
    {
      if (bool9) {
        processWriteAborted();
      } else {
        processImageComplete();
      }
      this.ios.flush();
    }
    finally
    {
      this.cbLock.unlock();
    }
    this.currentImage += 1;
  }
  
  public boolean canWriteSequence()
  {
    return true;
  }
  
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      prepareWriteSequenceOnThread(paramIIOMetadata);
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private void prepareWriteSequenceOnThread(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    if (this.ios == null) {
      throw new IllegalStateException("Output has not been set!");
    }
    if (paramIIOMetadata != null) {
      if ((paramIIOMetadata instanceof JPEGMetadata))
      {
        JPEGMetadata localJPEGMetadata = (JPEGMetadata)paramIIOMetadata;
        if (!localJPEGMetadata.isStream) {
          throw new IllegalArgumentException("Invalid stream metadata object.");
        }
        if (this.currentImage != 0) {
          throw new IIOException("JPEG Stream metadata must precede all images");
        }
        if (this.sequencePrepared == true) {
          throw new IIOException("Stream metadata already written!");
        }
        this.streamQTables = collectQTablesFromMetadata(localJPEGMetadata);
        if (this.debug) {
          System.out.println("after collecting from stream metadata, streamQTables.length is " + this.streamQTables.length);
        }
        if (this.streamQTables == null) {
          this.streamQTables = JPEG.getDefaultQTables();
        }
        this.streamDCHuffmanTables = collectHTablesFromMetadata(localJPEGMetadata, true);
        if (this.streamDCHuffmanTables == null) {
          this.streamDCHuffmanTables = JPEG.getDefaultHuffmanTables(true);
        }
        this.streamACHuffmanTables = collectHTablesFromMetadata(localJPEGMetadata, false);
        if (this.streamACHuffmanTables == null) {
          this.streamACHuffmanTables = JPEG.getDefaultHuffmanTables(false);
        }
        writeTables(this.structPointer, this.streamQTables, this.streamDCHuffmanTables, this.streamACHuffmanTables);
      }
      else
      {
        throw new IIOException("Stream metadata must be JPEG metadata");
      }
    }
    this.sequencePrepared = true;
  }
  
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if (!this.sequencePrepared) {
        throw new IllegalStateException("sequencePrepared not called!");
      }
      write(null, paramIIOImage, paramImageWriteParam);
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public void endWriteSequence()
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if (!this.sequencePrepared) {
        throw new IllegalStateException("sequencePrepared not called!");
      }
      this.sequencePrepared = false;
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public synchronized void abort()
  {
    setThreadLock();
    try
    {
      super.abort();
      abortWrite(this.structPointer);
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  protected synchronized void clearAbortRequest()
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if (abortRequested())
      {
        super.clearAbortRequest();
        resetWriter(this.structPointer);
        setDest(this.structPointer);
      }
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private void resetInternalState()
  {
    resetWriter(this.structPointer);
    this.srcRas = null;
    this.raster = null;
    this.convertTosRGB = false;
    this.currentImage = 0;
    this.numScans = 0;
    this.metadata = null;
  }
  
  public void reset()
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      super.reset();
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public void dispose()
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if (this.structPointer != 0L)
      {
        this.disposerRecord.dispose();
        this.structPointer = 0L;
      }
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  void warningOccurred(int paramInt)
  {
    this.cbLock.lock();
    try
    {
      if ((paramInt < 0) || (paramInt > 15)) {
        throw new InternalError("Invalid warning index");
      }
      processWarningOccurred(this.currentImage, "com.sun.imageio.plugins.jpeg.JPEGImageWriterResources", Integer.toString(paramInt));
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  void warningWithMessage(String paramString)
  {
    this.cbLock.lock();
    try
    {
      processWarningOccurred(this.currentImage, paramString);
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  void thumbnailStarted(int paramInt)
  {
    this.cbLock.lock();
    try
    {
      processThumbnailStarted(this.currentImage, paramInt);
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  void thumbnailProgress(float paramFloat)
  {
    this.cbLock.lock();
    try
    {
      processThumbnailProgress(paramFloat);
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  void thumbnailComplete()
  {
    this.cbLock.lock();
    try
    {
      processThumbnailComplete();
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private void checkSOFBands(SOFMarkerSegment paramSOFMarkerSegment, int paramInt)
    throws IIOException
  {
    if ((paramSOFMarkerSegment != null) && (paramSOFMarkerSegment.componentSpecs.length != paramInt)) {
      throw new IIOException("Metadata components != number of destination bands");
    }
  }
  
  private void checkJFIF(JFIFMarkerSegment paramJFIFMarkerSegment, ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean)
  {
    if ((paramJFIFMarkerSegment != null) && (!JPEG.isJFIFcompliant(paramImageTypeSpecifier, paramBoolean)))
    {
      this.ignoreJFIF = true;
      warningOccurred(paramBoolean ? 5 : 3);
    }
  }
  
  private void checkAdobe(AdobeMarkerSegment paramAdobeMarkerSegment, ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean)
  {
    if (paramAdobeMarkerSegment != null)
    {
      int i = JPEG.transformForType(paramImageTypeSpecifier, paramBoolean);
      if (paramAdobeMarkerSegment.transform != i)
      {
        warningOccurred(paramBoolean ? 6 : 4);
        if (i == -1) {
          this.ignoreAdobe = true;
        } else {
          this.newAdobeTransform = i;
        }
      }
    }
  }
  
  private int[] collectScans(JPEGMetadata paramJPEGMetadata, SOFMarkerSegment paramSOFMarkerSegment)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 9;
    int j = 4;
    Object localObject = paramJPEGMetadata.markerSequence.iterator();
    while (((Iterator)localObject).hasNext())
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)((Iterator)localObject).next();
      if ((localMarkerSegment instanceof SOSMarkerSegment)) {
        localArrayList.add(localMarkerSegment);
      }
    }
    localObject = null;
    this.numScans = 0;
    if (!localArrayList.isEmpty())
    {
      this.numScans = localArrayList.size();
      localObject = new int[this.numScans * i];
      int k = 0;
      for (int m = 0; m < this.numScans; m++)
      {
        SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)localArrayList.get(m);
        localObject[(k++)] = localSOSMarkerSegment.componentSpecs.length;
        for (int n = 0; n < j; n++) {
          if (n < localSOSMarkerSegment.componentSpecs.length)
          {
            int i1 = localSOSMarkerSegment.componentSpecs[n].componentSelector;
            for (int i2 = 0; i2 < paramSOFMarkerSegment.componentSpecs.length; i2++) {
              if (i1 == paramSOFMarkerSegment.componentSpecs[i2].componentId)
              {
                localObject[(k++)] = i2;
                break;
              }
            }
          }
          else
          {
            localObject[(k++)] = 0;
          }
        }
        localObject[(k++)] = localSOSMarkerSegment.startSpectralSelection;
        localObject[(k++)] = localSOSMarkerSegment.endSpectralSelection;
        localObject[(k++)] = localSOSMarkerSegment.approxHigh;
        localObject[(k++)] = localSOSMarkerSegment.approxLow;
      }
    }
    return localObject;
  }
  
  private JPEGQTable[] collectQTablesFromMetadata(JPEGMetadata paramJPEGMetadata)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramJPEGMetadata.markerSequence.iterator();
    while (localIterator.hasNext())
    {
      localObject = (MarkerSegment)localIterator.next();
      if ((localObject instanceof DQTMarkerSegment))
      {
        DQTMarkerSegment localDQTMarkerSegment = (DQTMarkerSegment)localObject;
        localArrayList.addAll(localDQTMarkerSegment.tables);
      }
    }
    Object localObject = null;
    if (localArrayList.size() != 0)
    {
      localObject = new JPEGQTable[localArrayList.size()];
      for (int i = 0; i < localObject.length; i++) {
        localObject[i] = new JPEGQTable(((DQTMarkerSegment.Qtable)localArrayList.get(i)).data);
      }
    }
    return localObject;
  }
  
  private JPEGHuffmanTable[] collectHTablesFromMetadata(JPEGMetadata paramJPEGMetadata, boolean paramBoolean)
    throws IIOException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramJPEGMetadata.markerSequence.iterator();
    Object localObject2;
    int i;
    while (localIterator.hasNext())
    {
      localObject1 = (MarkerSegment)localIterator.next();
      if ((localObject1 instanceof DHTMarkerSegment))
      {
        localObject2 = (DHTMarkerSegment)localObject1;
        for (i = 0; i < ((DHTMarkerSegment)localObject2).tables.size(); i++)
        {
          DHTMarkerSegment.Htable localHtable = (DHTMarkerSegment.Htable)((DHTMarkerSegment)localObject2).tables.get(i);
          if (localHtable.tableClass == (paramBoolean ? 0 : 1)) {
            localArrayList.add(localHtable);
          }
        }
      }
    }
    Object localObject1 = null;
    if (localArrayList.size() != 0)
    {
      localObject2 = new DHTMarkerSegment.Htable[localArrayList.size()];
      localArrayList.toArray((Object[])localObject2);
      localObject1 = new JPEGHuffmanTable[localArrayList.size()];
      for (i = 0; i < localObject1.length; i++)
      {
        localObject1[i] = null;
        for (int j = 0; j < localArrayList.size(); j++) {
          if (localObject2[j].tableID == i)
          {
            if (localObject1[i] != null) {
              throw new IIOException("Metadata has duplicate Htables!");
            }
            localObject1[i] = new JPEGHuffmanTable(localObject2[j].numCodes, localObject2[j].values);
          }
        }
      }
    }
    return localObject1;
  }
  
  private int getSrcCSType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    return getSrcCSType(paramImageTypeSpecifier.getColorModel());
  }
  
  private int getSrcCSType(RenderedImage paramRenderedImage)
  {
    return getSrcCSType(paramRenderedImage.getColorModel());
  }
  
  private int getSrcCSType(ColorModel paramColorModel)
  {
    int i = 0;
    if (paramColorModel != null)
    {
      boolean bool = paramColorModel.hasAlpha();
      ColorSpace localColorSpace = paramColorModel.getColorSpace();
      switch (localColorSpace.getType())
      {
      case 6: 
        i = 1;
        break;
      case 5: 
        if (bool) {
          i = 6;
        } else {
          i = 2;
        }
        break;
      case 3: 
        if (bool) {
          i = 7;
        } else {
          i = 3;
        }
        break;
      case 13: 
        if (localColorSpace == JPEG.JCS.getYCC()) {
          if (bool) {
            i = 10;
          } else {
            i = 5;
          }
        }
      case 9: 
        i = 4;
      }
    }
    return i;
  }
  
  private int getDestCSType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    boolean bool = localColorModel.hasAlpha();
    ColorSpace localColorSpace = localColorModel.getColorSpace();
    int i = 0;
    switch (localColorSpace.getType())
    {
    case 6: 
      i = 1;
      break;
    case 5: 
      if (bool) {
        i = 6;
      } else {
        i = 2;
      }
      break;
    case 3: 
      if (bool) {
        i = 7;
      } else {
        i = 3;
      }
      break;
    case 13: 
      if (localColorSpace == JPEG.JCS.getYCC()) {
        if (bool) {
          i = 10;
        } else {
          i = 5;
        }
      }
    case 9: 
      i = 4;
    }
    return i;
  }
  
  private int getDefaultDestCSType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    return getDefaultDestCSType(paramImageTypeSpecifier.getColorModel());
  }
  
  private int getDefaultDestCSType(RenderedImage paramRenderedImage)
  {
    return getDefaultDestCSType(paramRenderedImage.getColorModel());
  }
  
  private int getDefaultDestCSType(ColorModel paramColorModel)
  {
    int i = 0;
    if (paramColorModel != null)
    {
      boolean bool = paramColorModel.hasAlpha();
      ColorSpace localColorSpace = paramColorModel.getColorSpace();
      switch (localColorSpace.getType())
      {
      case 6: 
        i = 1;
        break;
      case 5: 
        if (bool) {
          i = 7;
        } else {
          i = 3;
        }
        break;
      case 3: 
        if (bool) {
          i = 7;
        } else {
          i = 3;
        }
        break;
      case 13: 
        if (localColorSpace == JPEG.JCS.getYCC()) {
          if (bool) {
            i = 10;
          } else {
            i = 5;
          }
        }
      case 9: 
        i = 11;
      }
    }
    return i;
  }
  
  private boolean isSubsampled(SOFMarkerSegment.ComponentSpec[] paramArrayOfComponentSpec)
  {
    int i = paramArrayOfComponentSpec[0].HsamplingFactor;
    int j = paramArrayOfComponentSpec[0].VsamplingFactor;
    for (int k = 1; k < paramArrayOfComponentSpec.length; k++) {
      if ((paramArrayOfComponentSpec[k].HsamplingFactor != i) || (paramArrayOfComponentSpec[k].HsamplingFactor != i)) {
        return true;
      }
    }
    return false;
  }
  
  private static native void initWriterIDs(Class paramClass1, Class paramClass2);
  
  private native long initJPEGImageWriter();
  
  private native void setDest(long paramLong);
  
  private native boolean writeImage(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, JPEGQTable[] paramArrayOfJPEGQTable, boolean paramBoolean1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt9, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, int[] paramArrayOfInt5, int[] paramArrayOfInt6, boolean paramBoolean5, int paramInt10);
  
  private void writeMetadata()
    throws IOException
  {
    if (this.metadata == null)
    {
      if (this.writeDefaultJFIF) {
        JFIFMarkerSegment.writeDefaultJFIF(this.ios, this.thumbnails, this.iccProfile, this);
      }
      if (this.writeAdobe) {
        AdobeMarkerSegment.writeAdobeSegment(this.ios, this.newAdobeTransform);
      }
    }
    else
    {
      this.metadata.writeToStream(this.ios, this.ignoreJFIF, this.forceJFIF, this.thumbnails, this.iccProfile, this.ignoreAdobe, this.newAdobeTransform, this);
    }
  }
  
  private native void writeTables(long paramLong, JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2);
  
  private void grabPixels(int paramInt)
  {
    Object localObject1 = null;
    Object localObject2;
    if (this.indexed)
    {
      localObject1 = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + paramInt, this.sourceWidth, 1, 0, 0, new int[] { 0 });
      boolean bool = this.indexCM.getTransparency() != 1;
      localObject2 = this.indexCM.convertToIntDiscrete((Raster)localObject1, bool);
      localObject1 = ((BufferedImage)localObject2).getRaster();
    }
    else
    {
      localObject1 = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + paramInt, this.sourceWidth, 1, 0, 0, this.srcBands);
    }
    if (this.convertTosRGB)
    {
      if (this.debug) {
        System.out.println("Converting to sRGB");
      }
      this.converted = this.convertOp.filter((Raster)localObject1, this.converted);
      localObject1 = this.converted;
    }
    if (this.isAlphaPremultiplied)
    {
      WritableRaster localWritableRaster = ((Raster)localObject1).createCompatibleWritableRaster();
      localObject2 = null;
      localObject2 = ((Raster)localObject1).getPixels(((Raster)localObject1).getMinX(), ((Raster)localObject1).getMinY(), ((Raster)localObject1).getWidth(), ((Raster)localObject1).getHeight(), (int[])localObject2);
      localWritableRaster.setPixels(((Raster)localObject1).getMinX(), ((Raster)localObject1).getMinY(), ((Raster)localObject1).getWidth(), ((Raster)localObject1).getHeight(), (int[])localObject2);
      this.srcCM.coerceData(localWritableRaster, false);
      localObject1 = localWritableRaster.createChild(localWritableRaster.getMinX(), localWritableRaster.getMinY(), localWritableRaster.getWidth(), localWritableRaster.getHeight(), 0, 0, this.srcBands);
    }
    this.raster.setRect((Raster)localObject1);
    if ((paramInt > 7) && (paramInt % 8 == 0))
    {
      this.cbLock.lock();
      try
      {
        processImageProgress(paramInt / this.sourceHeight * 100.0F);
      }
      finally
      {
        this.cbLock.unlock();
      }
    }
  }
  
  private native void abortWrite(long paramLong);
  
  private native void resetWriter(long paramLong);
  
  private static native void disposeWriter(long paramLong);
  
  private void writeOutputData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.cbLock.lock();
    try
    {
      this.ios.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private synchronized void setThreadLock()
  {
    Thread localThread = Thread.currentThread();
    if (this.theThread != null)
    {
      if (this.theThread != localThread) {
        throw new IllegalStateException("Attempt to use instance of " + this + " locked on thread " + this.theThread + " from thread " + localThread);
      }
      this.theLockCount += 1;
    }
    else
    {
      this.theThread = localThread;
      this.theLockCount = 1;
    }
  }
  
  private synchronized void clearThreadLock()
  {
    Thread localThread = Thread.currentThread();
    if ((this.theThread == null) || (this.theThread != localThread)) {
      throw new IllegalStateException("Attempt to clear thread lock form wrong thread. Locked thread: " + this.theThread + "; current thread: " + localThread);
    }
    this.theLockCount -= 1;
    if (this.theLockCount == 0) {
      this.theThread = null;
    }
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("jpeg");
        return null;
      }
    });
    initWriterIDs(JPEGQTable.class, JPEGHuffmanTable.class);
  }
  
  private static class CallBackLock
  {
    private State lockState = State.Unlocked;
    
    CallBackLock() {}
    
    void check()
    {
      if (this.lockState != State.Unlocked) {
        throw new IllegalStateException("Access to the writer is not allowed");
      }
    }
    
    private void lock()
    {
      this.lockState = State.Locked;
    }
    
    private void unlock()
    {
      this.lockState = State.Unlocked;
    }
    
    private static enum State
    {
      Unlocked,  Locked;
      
      private State() {}
    }
  }
  
  private static class JPEGWriterDisposerRecord
    implements DisposerRecord
  {
    private long pData;
    
    public JPEGWriterDisposerRecord(long paramLong)
    {
      this.pData = paramLong;
    }
    
    public synchronized void dispose()
    {
      if (this.pData != 0L)
      {
        JPEGImageWriter.disposeWriter(this.pData);
        this.pData = 0L;
      }
    }
  }
}
