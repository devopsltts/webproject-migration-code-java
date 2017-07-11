package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageReader
  extends ImageReader
{
  private boolean debug = false;
  private long structPointer = 0L;
  private ImageInputStream iis = null;
  private List imagePositions = null;
  private int numImages = 0;
  protected static final int WARNING_NO_EOI = 0;
  protected static final int WARNING_NO_JFIF_IN_THUMB = 1;
  protected static final int WARNING_IGNORE_INVALID_ICC = 2;
  private static final int MAX_WARNING = 2;
  private int currentImage = -1;
  private int width;
  private int height;
  private int colorSpaceCode;
  private int outColorSpaceCode;
  private int numComponents;
  private ColorSpace iccCS = null;
  private ColorConvertOp convert = null;
  private BufferedImage image = null;
  private WritableRaster raster = null;
  private WritableRaster target = null;
  private DataBufferByte buffer = null;
  private Rectangle destROI = null;
  private int[] destinationBands = null;
  private JPEGMetadata streamMetadata = null;
  private JPEGMetadata imageMetadata = null;
  private int imageMetadataIndex = -1;
  private boolean haveSeeked = false;
  private JPEGQTable[] abbrevQTables = null;
  private JPEGHuffmanTable[] abbrevDCHuffmanTables = null;
  private JPEGHuffmanTable[] abbrevACHuffmanTables = null;
  private int minProgressivePass = 0;
  private int maxProgressivePass = Integer.MAX_VALUE;
  private static final int UNKNOWN = -1;
  private static final int MIN_ESTIMATED_PASSES = 10;
  private int knownPassCount = -1;
  private int pass = 0;
  private float percentToDate = 0.0F;
  private float previousPassPercentage = 0.0F;
  private int progInterval = 0;
  private boolean tablesOnlyChecked = false;
  private Object disposerReferent = new Object();
  private DisposerRecord disposerRecord = new JPEGReaderDisposerRecord(this.structPointer);
  private Thread theThread = null;
  private int theLockCount = 0;
  private CallBackLock cbLock = new CallBackLock();
  
  private static native void initReaderIDs(Class paramClass1, Class paramClass2, Class paramClass3);
  
  public JPEGImageReader(ImageReaderSpi paramImageReaderSpi)
  {
    super(paramImageReaderSpi);
    Disposer.addRecord(this.disposerReferent, this.disposerRecord);
  }
  
  private native long initJPEGImageReader();
  
  protected void warningOccurred(int paramInt)
  {
    this.cbLock.lock();
    try
    {
      if ((paramInt < 0) || (paramInt > 2)) {
        throw new InternalError("Invalid warning index");
      }
      processWarningOccurred("com.sun.imageio.plugins.jpeg.JPEGImageReaderResources", Integer.toString(paramInt));
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  protected void warningWithMessage(String paramString)
  {
    this.cbLock.lock();
    try
    {
      processWarningOccurred(paramString);
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      super.setInput(paramObject, paramBoolean1, paramBoolean2);
      this.ignoreMetadata = paramBoolean2;
      resetInternalState();
      this.iis = ((ImageInputStream)paramObject);
      setSource(this.structPointer);
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private int readInputData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.cbLock.lock();
    try
    {
      int i = this.iis.read(paramArrayOfByte, paramInt1, paramInt2);
      return i;
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private long skipInputBytes(long paramLong)
    throws IOException
  {
    this.cbLock.lock();
    try
    {
      long l = this.iis.skipBytes(paramLong);
      return l;
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private native void setSource(long paramLong);
  
  private void checkTablesOnly()
    throws IOException
  {
    if (this.debug) {
      System.out.println("Checking for tables-only image");
    }
    long l1 = this.iis.getStreamPosition();
    if (this.debug)
    {
      System.out.println("saved pos is " + l1);
      System.out.println("length is " + this.iis.length());
    }
    boolean bool = readNativeHeader(true);
    if (bool)
    {
      long l2;
      if (this.debug)
      {
        System.out.println("tables-only image found");
        l2 = this.iis.getStreamPosition();
        System.out.println("pos after return from native is " + l2);
      }
      if (!this.ignoreMetadata)
      {
        this.iis.seek(l1);
        this.haveSeeked = true;
        this.streamMetadata = new JPEGMetadata(true, false, this.iis, this);
        l2 = this.iis.getStreamPosition();
        if (this.debug) {
          System.out.println("pos after constructing stream metadata is " + l2);
        }
      }
      if (hasNextImage()) {
        this.imagePositions.add(new Long(this.iis.getStreamPosition()));
      }
    }
    else
    {
      this.imagePositions.add(new Long(l1));
      this.currentImage = 0;
    }
    if (this.seekForwardOnly)
    {
      Long localLong = (Long)this.imagePositions.get(this.imagePositions.size() - 1);
      this.iis.flushBefore(localLong.longValue());
    }
    this.tablesOnlyChecked = true;
  }
  
  public int getNumImages(boolean paramBoolean)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      int i = getNumImagesOnThread(paramBoolean);
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private int getNumImagesOnThread(boolean paramBoolean)
    throws IOException
  {
    if (this.numImages != 0) {
      return this.numImages;
    }
    if (this.iis == null) {
      throw new IllegalStateException("Input not set");
    }
    if (paramBoolean == true)
    {
      if (this.seekForwardOnly) {
        throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
      }
      if (!this.tablesOnlyChecked) {
        checkTablesOnly();
      }
      this.iis.mark();
      gotoImage(0);
      JPEGBuffer localJPEGBuffer = new JPEGBuffer(this.iis);
      localJPEGBuffer.loadBuf(0);
      boolean bool = false;
      while (!bool)
      {
        bool = localJPEGBuffer.scanForFF(this);
        switch (localJPEGBuffer.buf[localJPEGBuffer.bufPtr] & 0xFF)
        {
        case 216: 
          this.numImages += 1;
        case 0: 
        case 208: 
        case 209: 
        case 210: 
        case 211: 
        case 212: 
        case 213: 
        case 214: 
        case 215: 
        case 217: 
          localJPEGBuffer.bufAvail -= 1;
          localJPEGBuffer.bufPtr += 1;
          break;
        default: 
          localJPEGBuffer.bufAvail -= 1;
          localJPEGBuffer.bufPtr += 1;
          localJPEGBuffer.loadBuf(2);
          int i = (localJPEGBuffer.buf[(localJPEGBuffer.bufPtr++)] & 0xFF) << 8 | localJPEGBuffer.buf[(localJPEGBuffer.bufPtr++)] & 0xFF;
          localJPEGBuffer.bufAvail -= 2;
          i -= 2;
          localJPEGBuffer.skipData(i);
        }
      }
      this.iis.reset();
      return this.numImages;
    }
    return -1;
  }
  
  private void gotoImage(int paramInt)
    throws IOException
  {
    if (this.iis == null) {
      throw new IllegalStateException("Input not set");
    }
    if (paramInt < this.minIndex) {
      throw new IndexOutOfBoundsException();
    }
    if (!this.tablesOnlyChecked) {
      checkTablesOnly();
    }
    if (paramInt < this.imagePositions.size())
    {
      this.iis.seek(((Long)this.imagePositions.get(paramInt)).longValue());
    }
    else
    {
      Long localLong = (Long)this.imagePositions.get(this.imagePositions.size() - 1);
      this.iis.seek(localLong.longValue());
      skipImage();
      for (int i = this.imagePositions.size(); i <= paramInt; i++)
      {
        if (!hasNextImage()) {
          throw new IndexOutOfBoundsException();
        }
        localLong = new Long(this.iis.getStreamPosition());
        this.imagePositions.add(localLong);
        if (this.seekForwardOnly) {
          this.iis.flushBefore(localLong.longValue());
        }
        if (i < paramInt) {
          skipImage();
        }
      }
    }
    if (this.seekForwardOnly) {
      this.minIndex = paramInt;
    }
    this.haveSeeked = true;
  }
  
  private void skipImage()
    throws IOException
  {
    if (this.debug) {
      System.out.println("skipImage called");
    }
    int i = 0;
    for (int j = this.iis.read(); j != -1; j = this.iis.read())
    {
      if ((i == 1) && (j == 217)) {
        return;
      }
      i = j == 255 ? 1 : 0;
    }
    throw new IndexOutOfBoundsException();
  }
  
  private boolean hasNextImage()
    throws IOException
  {
    if (this.debug) {
      System.out.print("hasNextImage called; returning ");
    }
    this.iis.mark();
    int i = 0;
    for (int j = this.iis.read(); j != -1; j = this.iis.read())
    {
      if ((i == 1) && (j == 216))
      {
        this.iis.reset();
        if (this.debug) {
          System.out.println("true");
        }
        return true;
      }
      i = j == 255 ? 1 : 0;
    }
    this.iis.reset();
    if (this.debug) {
      System.out.println("false");
    }
    return false;
  }
  
  private void pushBack(int paramInt)
    throws IOException
  {
    if (this.debug) {
      System.out.println("pushing back " + paramInt + " bytes");
    }
    this.cbLock.lock();
    try
    {
      this.iis.seek(this.iis.getStreamPosition() - paramInt);
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private void readHeader(int paramInt, boolean paramBoolean)
    throws IOException
  {
    gotoImage(paramInt);
    readNativeHeader(paramBoolean);
    this.currentImage = paramInt;
  }
  
  private boolean readNativeHeader(boolean paramBoolean)
    throws IOException
  {
    boolean bool = false;
    bool = readImageHeader(this.structPointer, this.haveSeeked, paramBoolean);
    this.haveSeeked = false;
    return bool;
  }
  
  private native boolean readImageHeader(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  private void setImageData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
  {
    this.width = paramInt1;
    this.height = paramInt2;
    this.colorSpaceCode = paramInt3;
    this.outColorSpaceCode = paramInt4;
    this.numComponents = paramInt5;
    if (paramArrayOfByte == null)
    {
      this.iccCS = null;
      return;
    }
    ICC_Profile localICC_Profile1 = null;
    try
    {
      localICC_Profile1 = ICC_Profile.getInstance(paramArrayOfByte);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      this.iccCS = null;
      warningOccurred(2);
      return;
    }
    byte[] arrayOfByte1 = localICC_Profile1.getData();
    ICC_Profile localICC_Profile2 = null;
    if ((this.iccCS instanceof ICC_ColorSpace)) {
      localICC_Profile2 = ((ICC_ColorSpace)this.iccCS).getProfile();
    }
    byte[] arrayOfByte2 = null;
    if (localICC_Profile2 != null) {
      arrayOfByte2 = localICC_Profile2.getData();
    }
    if ((arrayOfByte2 == null) || (!Arrays.equals(arrayOfByte2, arrayOfByte1)))
    {
      this.iccCS = new ICC_ColorSpace(localICC_Profile1);
      try
      {
        float[] arrayOfFloat = this.iccCS.fromRGB(new float[] { 1.0F, 0.0F, 0.0F });
      }
      catch (CMMException localCMMException)
      {
        this.iccCS = null;
        this.cbLock.lock();
        try
        {
          warningOccurred(2);
        }
        finally
        {
          this.cbLock.unlock();
        }
      }
    }
  }
  
  public int getWidth(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if (this.currentImage != paramInt)
      {
        this.cbLock.check();
        readHeader(paramInt, true);
      }
      int i = this.width;
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public int getHeight(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if (this.currentImage != paramInt)
      {
        this.cbLock.check();
        readHeader(paramInt, true);
      }
      int i = this.height;
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private ImageTypeProducer getImageType(int paramInt)
  {
    ImageTypeProducer localImageTypeProducer = null;
    if ((paramInt > 0) && (paramInt < 12)) {
      localImageTypeProducer = ImageTypeProducer.getTypeProducer(paramInt);
    }
    return localImageTypeProducer;
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if (this.currentImage != paramInt)
      {
        this.cbLock.check();
        readHeader(paramInt, true);
      }
      ImageTypeSpecifier localImageTypeSpecifier = getImageType(this.colorSpaceCode).getType();
      return localImageTypeSpecifier;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public Iterator getImageTypes(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      Iterator localIterator = getImageTypesOnThread(paramInt);
      return localIterator;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private Iterator getImageTypesOnThread(int paramInt)
    throws IOException
  {
    if (this.currentImage != paramInt)
    {
      this.cbLock.check();
      readHeader(paramInt, true);
    }
    ImageTypeProducer localImageTypeProducer = getImageType(this.colorSpaceCode);
    ArrayList localArrayList = new ArrayList(1);
    switch (this.colorSpaceCode)
    {
    case 1: 
      localArrayList.add(localImageTypeProducer);
      localArrayList.add(getImageType(2));
      break;
    case 2: 
      localArrayList.add(localImageTypeProducer);
      localArrayList.add(getImageType(1));
      localArrayList.add(getImageType(5));
      break;
    case 6: 
      localArrayList.add(localImageTypeProducer);
      break;
    case 5: 
      if (localImageTypeProducer != null)
      {
        localArrayList.add(localImageTypeProducer);
        localArrayList.add(getImageType(2));
      }
      break;
    case 10: 
      if (localImageTypeProducer != null) {
        localArrayList.add(localImageTypeProducer);
      }
      break;
    case 3: 
      localArrayList.add(getImageType(2));
      if (this.iccCS != null) {
        localArrayList.add(new ImageTypeProducer()
        {
          protected ImageTypeSpecifier produce()
          {
            return ImageTypeSpecifier.createInterleaved(JPEGImageReader.this.iccCS, JPEG.bOffsRGB, 0, false, false);
          }
        });
      }
      localArrayList.add(getImageType(1));
      localArrayList.add(getImageType(5));
      break;
    case 7: 
      localArrayList.add(getImageType(6));
    }
    return new ImageTypeIterator(localArrayList.iterator());
  }
  
  private void checkColorConversion(BufferedImage paramBufferedImage, ImageReadParam paramImageReadParam)
    throws IIOException
  {
    if ((paramImageReadParam != null) && ((paramImageReadParam.getSourceBands() != null) || (paramImageReadParam.getDestinationBands() != null))) {
      return;
    }
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    if ((localColorModel instanceof IndexColorModel)) {
      throw new IIOException("IndexColorModel not supported");
    }
    ColorSpace localColorSpace1 = localColorModel.getColorSpace();
    int i = localColorSpace1.getType();
    this.convert = null;
    ColorSpace localColorSpace2;
    switch (this.outColorSpaceCode)
    {
    case 1: 
      if (i == 5)
      {
        setOutColorSpace(this.structPointer, 2);
        this.outColorSpaceCode = 2;
        this.numComponents = 3;
      }
      else if (i != 6)
      {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 2: 
      if (i == 6)
      {
        if (this.colorSpaceCode == 3)
        {
          setOutColorSpace(this.structPointer, 1);
          this.outColorSpaceCode = 1;
          this.numComponents = 1;
        }
      }
      else if ((this.iccCS != null) && (localColorModel.getNumComponents() == this.numComponents) && (localColorSpace1 != this.iccCS)) {
        this.convert = new ColorConvertOp(this.iccCS, localColorSpace1, null);
      } else if ((this.iccCS == null) && (!localColorSpace1.isCS_sRGB()) && (localColorModel.getNumComponents() == this.numComponents)) {
        this.convert = new ColorConvertOp(JPEG.JCS.sRGB, localColorSpace1, null);
      } else if (i != 5) {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 6: 
      if ((i != 5) || (localColorModel.getNumComponents() != this.numComponents)) {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 5: 
      localColorSpace2 = JPEG.JCS.getYCC();
      if (localColorSpace2 == null) {
        throw new IIOException("Incompatible color conversion");
      }
      if ((localColorSpace1 != localColorSpace2) && (localColorModel.getNumComponents() == this.numComponents)) {
        this.convert = new ColorConvertOp(localColorSpace2, localColorSpace1, null);
      }
      break;
    case 10: 
      localColorSpace2 = JPEG.JCS.getYCC();
      if ((localColorSpace2 == null) || (localColorSpace1 != localColorSpace2) || (localColorModel.getNumComponents() != this.numComponents)) {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 3: 
    case 4: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      throw new IIOException("Incompatible color conversion");
    }
  }
  
  private native void setOutColorSpace(long paramLong, int paramInt);
  
  public ImageReadParam getDefaultReadParam()
  {
    return new JPEGImageReadParam();
  }
  
  public IIOMetadata getStreamMetadata()
    throws IOException
  {
    setThreadLock();
    try
    {
      if (!this.tablesOnlyChecked)
      {
        this.cbLock.check();
        checkTablesOnly();
      }
      JPEGMetadata localJPEGMetadata = this.streamMetadata;
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public IIOMetadata getImageMetadata(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if ((this.imageMetadataIndex == paramInt) && (this.imageMetadata != null))
      {
        localJPEGMetadata = this.imageMetadata;
        return localJPEGMetadata;
      }
      this.cbLock.check();
      gotoImage(paramInt);
      this.imageMetadata = new JPEGMetadata(false, false, this.iis, this);
      this.imageMetadataIndex = paramInt;
      JPEGMetadata localJPEGMetadata = this.imageMetadata;
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      try
      {
        readInternal(paramInt, paramImageReadParam, false);
      }
      catch (RuntimeException localRuntimeException)
      {
        resetLibraryState(this.structPointer);
        throw localRuntimeException;
      }
      catch (IOException localIOException)
      {
        resetLibraryState(this.structPointer);
        throw localIOException;
      }
      BufferedImage localBufferedImage1 = this.image;
      this.image = null;
      BufferedImage localBufferedImage2 = localBufferedImage1;
      return localBufferedImage2;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private Raster readInternal(int paramInt, ImageReadParam paramImageReadParam, boolean paramBoolean)
    throws IOException
  {
    readHeader(paramInt, false);
    WritableRaster localWritableRaster = null;
    int i = 0;
    if (!paramBoolean)
    {
      localObject1 = getImageTypes(paramInt);
      if (!((Iterator)localObject1).hasNext()) {
        throw new IIOException("Unsupported Image Type");
      }
      this.image = getDestination(paramImageReadParam, (Iterator)localObject1, this.width, this.height);
      localWritableRaster = this.image.getRaster();
      i = this.image.getSampleModel().getNumBands();
      checkColorConversion(this.image, paramImageReadParam);
      checkReadParamBandSettings(paramImageReadParam, this.numComponents, i);
    }
    else
    {
      setOutColorSpace(this.structPointer, this.colorSpaceCode);
      this.image = null;
    }
    Object localObject1 = JPEG.bandOffsets[(this.numComponents - 1)];
    int j = paramBoolean ? this.numComponents : i;
    this.destinationBands = null;
    Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
    this.destROI = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, this.width, this.height, this.image, localRectangle, this.destROI);
    int k = 1;
    int m = 1;
    this.minProgressivePass = 0;
    this.maxProgressivePass = Integer.MAX_VALUE;
    if (paramImageReadParam != null)
    {
      k = paramImageReadParam.getSourceXSubsampling();
      m = paramImageReadParam.getSourceYSubsampling();
      int[] arrayOfInt1 = paramImageReadParam.getSourceBands();
      if (arrayOfInt1 != null)
      {
        localObject1 = arrayOfInt1;
        j = localObject1.length;
      }
      if (!paramBoolean) {
        this.destinationBands = paramImageReadParam.getDestinationBands();
      }
      this.minProgressivePass = paramImageReadParam.getSourceMinProgressivePass();
      this.maxProgressivePass = paramImageReadParam.getSourceMaxProgressivePass();
      if ((paramImageReadParam instanceof JPEGImageReadParam))
      {
        localObject2 = (JPEGImageReadParam)paramImageReadParam;
        if (((JPEGImageReadParam)localObject2).areTablesSet())
        {
          this.abbrevQTables = ((JPEGImageReadParam)localObject2).getQTables();
          this.abbrevDCHuffmanTables = ((JPEGImageReadParam)localObject2).getDCHuffmanTables();
          this.abbrevACHuffmanTables = ((JPEGImageReadParam)localObject2).getACHuffmanTables();
        }
      }
    }
    int n = this.destROI.width * j;
    this.buffer = new DataBufferByte(n);
    Object localObject2 = JPEG.bandOffsets[(j - 1)];
    this.raster = Raster.createInterleavedRaster(this.buffer, this.destROI.width, 1, n, j, (int[])localObject2, null);
    if (paramBoolean) {
      this.target = Raster.createInterleavedRaster(0, this.destROI.width, this.destROI.height, n, j, (int[])localObject2, null);
    } else {
      this.target = localWritableRaster;
    }
    int[] arrayOfInt2 = this.target.getSampleModel().getSampleSize();
    for (int i1 = 0; i1 < arrayOfInt2.length; i1++) {
      if ((arrayOfInt2[i1] <= 0) || (arrayOfInt2[i1] > 8)) {
        throw new IIOException("Illegal band size: should be 0 < size <= 8");
      }
    }
    i1 = (this.updateListeners != null) || (this.progressListeners != null) ? 1 : 0;
    initProgressData();
    if (paramInt == this.imageMetadataIndex)
    {
      this.knownPassCount = 0;
      Iterator localIterator = this.imageMetadata.markerSequence.iterator();
      while (localIterator.hasNext()) {
        if ((localIterator.next() instanceof SOSMarkerSegment)) {
          this.knownPassCount += 1;
        }
      }
    }
    this.progInterval = Math.max((this.target.getHeight() - 1) / 20, 1);
    if (this.knownPassCount > 0) {
      this.progInterval *= this.knownPassCount;
    } else if (this.maxProgressivePass != Integer.MAX_VALUE) {
      this.progInterval *= (this.maxProgressivePass - this.minProgressivePass + 1);
    }
    if (this.debug)
    {
      System.out.println("**** Read Data *****");
      System.out.println("numRasterBands is " + j);
      System.out.print("srcBands:");
      for (i2 = 0; i2 < localObject1.length; i2++) {
        System.out.print(" " + localObject1[i2]);
      }
      System.out.println();
      System.out.println("destination bands is " + this.destinationBands);
      if (this.destinationBands != null)
      {
        for (i2 = 0; i2 < this.destinationBands.length; i2++) {
          System.out.print(" " + this.destinationBands[i2]);
        }
        System.out.println();
      }
      System.out.println("sourceROI is " + localRectangle);
      System.out.println("destROI is " + this.destROI);
      System.out.println("periodX is " + k);
      System.out.println("periodY is " + m);
      System.out.println("minProgressivePass is " + this.minProgressivePass);
      System.out.println("maxProgressivePass is " + this.maxProgressivePass);
      System.out.println("callbackUpdates is " + i1);
    }
    processImageStarted(this.currentImage);
    int i2 = 0;
    boolean bool = readImage(this.structPointer, this.buffer.getData(), j, (int[])localObject1, arrayOfInt2, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, k, m, this.abbrevQTables, this.abbrevDCHuffmanTables, this.abbrevACHuffmanTables, this.minProgressivePass, this.maxProgressivePass, i1);
    if (bool) {
      processReadAborted();
    } else {
      processImageComplete();
    }
    return this.target;
  }
  
  private void acceptPixels(int paramInt, boolean paramBoolean)
  {
    if (this.convert != null) {
      this.convert.filter(this.raster, this.raster);
    }
    this.target.setRect(this.destROI.x, this.destROI.y + paramInt, this.raster);
    this.cbLock.lock();
    try
    {
      processImageUpdate(this.image, this.destROI.x, this.destROI.y + paramInt, this.raster.getWidth(), 1, 1, 1, this.destinationBands);
      if ((paramInt > 0) && (paramInt % this.progInterval == 0))
      {
        int i = this.target.getHeight() - 1;
        float f = paramInt / i;
        if (paramBoolean)
        {
          if (this.knownPassCount != -1)
          {
            processImageProgress((this.pass + f) * 100.0F / this.knownPassCount);
          }
          else if (this.maxProgressivePass != Integer.MAX_VALUE)
          {
            processImageProgress((this.pass + f) * 100.0F / (this.maxProgressivePass - this.minProgressivePass + 1));
          }
          else
          {
            int j = Math.max(2, 10 - this.pass);
            int k = this.pass + j - 1;
            this.progInterval = Math.max(i / 20 * k, k);
            if (paramInt % this.progInterval == 0)
            {
              this.percentToDate = (this.previousPassPercentage + (1.0F - this.previousPassPercentage) * f / j);
              if (this.debug)
              {
                System.out.print("pass= " + this.pass);
                System.out.print(", y= " + paramInt);
                System.out.print(", progInt= " + this.progInterval);
                System.out.print(", % of pass: " + f);
                System.out.print(", rem. passes: " + j);
                System.out.print(", prev%: " + this.previousPassPercentage);
                System.out.print(", %ToDate: " + this.percentToDate);
                System.out.print(" ");
              }
              processImageProgress(this.percentToDate * 100.0F);
            }
          }
        }
        else {
          processImageProgress(f * 100.0F);
        }
      }
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private void initProgressData()
  {
    this.knownPassCount = -1;
    this.pass = 0;
    this.percentToDate = 0.0F;
    this.previousPassPercentage = 0.0F;
    this.progInterval = 0;
  }
  
  private void passStarted(int paramInt)
  {
    this.cbLock.lock();
    try
    {
      this.pass = paramInt;
      this.previousPassPercentage = this.percentToDate;
      processPassStarted(this.image, paramInt, this.minProgressivePass, this.maxProgressivePass, 0, 0, 1, 1, this.destinationBands);
      this.cbLock.unlock();
    }
    finally
    {
      this.cbLock.unlock();
    }
  }
  
  private void passComplete()
  {
    this.cbLock.lock();
    try
    {
      processPassComplete(this.image);
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
  
  private native boolean readImage(long paramLong, byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2, int paramInt8, int paramInt9, boolean paramBoolean);
  
  public void abort()
  {
    setThreadLock();
    try
    {
      super.abort();
      abortRead(this.structPointer);
      clearThreadLock();
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private native void abortRead(long paramLong);
  
  private native void resetLibraryState(long paramLong);
  
  public boolean canReadRaster()
  {
    return true;
  }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    setThreadLock();
    Raster localRaster = null;
    try
    {
      this.cbLock.check();
      Point localPoint = null;
      if (paramImageReadParam != null)
      {
        localPoint = paramImageReadParam.getDestinationOffset();
        paramImageReadParam.setDestinationOffset(new Point(0, 0));
      }
      localRaster = readInternal(paramInt, paramImageReadParam, true);
      if (localPoint != null) {
        this.target = this.target.createWritableTranslatedChild(localPoint.x, localPoint.y);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      resetLibraryState(this.structPointer);
      throw localRuntimeException;
    }
    catch (IOException localIOException)
    {
      resetLibraryState(this.structPointer);
      throw localIOException;
    }
    finally
    {
      clearThreadLock();
    }
    return localRaster;
  }
  
  public boolean readerSupportsThumbnails()
  {
    return true;
  }
  
  public int getNumThumbnails(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      getImageMetadata(paramInt);
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = 0;
      if (localJFIFMarkerSegment != null)
      {
        i = localJFIFMarkerSegment.thumb == null ? 0 : 1;
        i += localJFIFMarkerSegment.extSegments.size();
      }
      int j = i;
      return j;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public int getThumbnailWidth(int paramInt1, int paramInt2)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if ((paramInt2 < 0) || (paramInt2 >= getNumThumbnails(paramInt1))) {
        throw new IndexOutOfBoundsException("No such thumbnail");
      }
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = localJFIFMarkerSegment.getThumbnailWidth(paramInt2);
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public int getThumbnailHeight(int paramInt1, int paramInt2)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if ((paramInt2 < 0) || (paramInt2 >= getNumThumbnails(paramInt1))) {
        throw new IndexOutOfBoundsException("No such thumbnail");
      }
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = localJFIFMarkerSegment.getThumbnailHeight(paramInt2);
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public BufferedImage readThumbnail(int paramInt1, int paramInt2)
    throws IOException
  {
    setThreadLock();
    try
    {
      this.cbLock.check();
      if ((paramInt2 < 0) || (paramInt2 >= getNumThumbnails(paramInt1))) {
        throw new IndexOutOfBoundsException("No such thumbnail");
      }
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      BufferedImage localBufferedImage = localJFIFMarkerSegment.getThumbnail(this.iis, paramInt2, this);
      return localBufferedImage;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private void resetInternalState()
  {
    resetReader(this.structPointer);
    this.numImages = 0;
    this.imagePositions = new ArrayList();
    this.currentImage = -1;
    this.image = null;
    this.raster = null;
    this.target = null;
    this.buffer = null;
    this.destROI = null;
    this.destinationBands = null;
    this.streamMetadata = null;
    this.imageMetadata = null;
    this.imageMetadataIndex = -1;
    this.haveSeeked = false;
    this.tablesOnlyChecked = false;
    this.iccCS = null;
    initProgressData();
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
  
  private native void resetReader(long paramLong);
  
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
  
  private static native void disposeReader(long paramLong);
  
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
      throw new IllegalStateException("Attempt to clear thread lock  form wrong thread. Locked thread: " + this.theThread + "; current thread: " + localThread);
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
    initReaderIDs(ImageInputStream.class, JPEGQTable.class, JPEGHuffmanTable.class);
  }
  
  private static class CallBackLock
  {
    private State lockState = State.Unlocked;
    
    CallBackLock() {}
    
    void check()
    {
      if (this.lockState != State.Unlocked) {
        throw new IllegalStateException("Access to the reader is not allowed");
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
  
  private static class JPEGReaderDisposerRecord
    implements DisposerRecord
  {
    private long pData;
    
    public JPEGReaderDisposerRecord(long paramLong)
    {
      this.pData = paramLong;
    }
    
    public synchronized void dispose()
    {
      if (this.pData != 0L)
      {
        JPEGImageReader.disposeReader(this.pData);
        this.pData = 0L;
      }
    }
  }
}
