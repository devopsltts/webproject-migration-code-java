package javax.imageio;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageTypeSpecifier
{
  protected ColorModel colorModel;
  protected SampleModel sampleModel;
  private static ImageTypeSpecifier[] BISpecifier = new ImageTypeSpecifier[14];
  private static ColorSpace sRGB = ColorSpace.getInstance(1000);
  
  private ImageTypeSpecifier() {}
  
  public ImageTypeSpecifier(ColorModel paramColorModel, SampleModel paramSampleModel)
  {
    if (paramColorModel == null) {
      throw new IllegalArgumentException("colorModel == null!");
    }
    if (paramSampleModel == null) {
      throw new IllegalArgumentException("sampleModel == null!");
    }
    if (!paramColorModel.isCompatibleSampleModel(paramSampleModel)) {
      throw new IllegalArgumentException("sampleModel is incompatible with colorModel!");
    }
    this.colorModel = paramColorModel;
    this.sampleModel = paramSampleModel;
  }
  
  public ImageTypeSpecifier(RenderedImage paramRenderedImage)
  {
    if (paramRenderedImage == null) {
      throw new IllegalArgumentException("image == null!");
    }
    this.colorModel = paramRenderedImage.getColorModel();
    this.sampleModel = paramRenderedImage.getSampleModel();
  }
  
  public static ImageTypeSpecifier createPacked(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    return new Packed(paramColorSpace, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean);
  }
  
  static ColorModel createComponentCM(ColorSpace paramColorSpace, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramBoolean1 ? 3 : 1;
    int[] arrayOfInt = new int[paramInt1];
    int j = DataBuffer.getDataTypeSize(paramInt2);
    for (int k = 0; k < paramInt1; k++) {
      arrayOfInt[k] = j;
    }
    return new ComponentColorModel(paramColorSpace, arrayOfInt, paramBoolean1, paramBoolean2, i, paramInt2);
  }
  
  public static ImageTypeSpecifier createInterleaved(ColorSpace paramColorSpace, int[] paramArrayOfInt, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new Interleaved(paramColorSpace, paramArrayOfInt, paramInt, paramBoolean1, paramBoolean2);
  }
  
  public static ImageTypeSpecifier createBanded(ColorSpace paramColorSpace, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new Banded(paramColorSpace, paramArrayOfInt1, paramArrayOfInt2, paramInt, paramBoolean1, paramBoolean2);
  }
  
  public static ImageTypeSpecifier createGrayscale(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return new Grayscale(paramInt1, paramInt2, paramBoolean, false, false);
  }
  
  public static ImageTypeSpecifier createGrayscale(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new Grayscale(paramInt1, paramInt2, paramBoolean1, true, paramBoolean2);
  }
  
  public static ImageTypeSpecifier createIndexed(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt1, int paramInt2)
  {
    return new Indexed(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramArrayOfByte4, paramInt1, paramInt2);
  }
  
  public static ImageTypeSpecifier createFromBufferedImageType(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= 13)) {
      return getSpecifier(paramInt);
    }
    if (paramInt == 0) {
      throw new IllegalArgumentException("Cannot create from TYPE_CUSTOM!");
    }
    throw new IllegalArgumentException("Invalid BufferedImage type!");
  }
  
  public static ImageTypeSpecifier createFromRenderedImage(RenderedImage paramRenderedImage)
  {
    if (paramRenderedImage == null) {
      throw new IllegalArgumentException("image == null!");
    }
    if ((paramRenderedImage instanceof BufferedImage))
    {
      int i = ((BufferedImage)paramRenderedImage).getType();
      if (i != 0) {
        return getSpecifier(i);
      }
    }
    return new ImageTypeSpecifier(paramRenderedImage);
  }
  
  public int getBufferedImageType()
  {
    BufferedImage localBufferedImage = createBufferedImage(1, 1);
    return localBufferedImage.getType();
  }
  
  public int getNumComponents()
  {
    return this.colorModel.getNumComponents();
  }
  
  public int getNumBands()
  {
    return this.sampleModel.getNumBands();
  }
  
  public int getBitsPerBand(int paramInt)
  {
    if (((paramInt < 0 ? 1 : 0) | (paramInt >= getNumBands() ? 1 : 0)) != 0) {
      throw new IllegalArgumentException("band out of range!");
    }
    return this.sampleModel.getSampleSize(paramInt);
  }
  
  public SampleModel getSampleModel()
  {
    return this.sampleModel;
  }
  
  public SampleModel getSampleModel(int paramInt1, int paramInt2)
  {
    if (paramInt1 * paramInt2 > 2147483647L) {
      throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
    }
    return this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
  }
  
  public ColorModel getColorModel()
  {
    return this.colorModel;
  }
  
  public BufferedImage createBufferedImage(int paramInt1, int paramInt2)
  {
    try
    {
      SampleModel localSampleModel = getSampleModel(paramInt1, paramInt2);
      WritableRaster localWritableRaster = Raster.createWritableRaster(localSampleModel, new Point(0, 0));
      return new BufferedImage(this.colorModel, localWritableRaster, this.colorModel.isAlphaPremultiplied(), new Hashtable());
    }
    catch (NegativeArraySizeException localNegativeArraySizeException)
    {
      throw new IllegalArgumentException("Array size > Integer.MAX_VALUE!");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof ImageTypeSpecifier))) {
      return false;
    }
    ImageTypeSpecifier localImageTypeSpecifier = (ImageTypeSpecifier)paramObject;
    return (this.colorModel.equals(localImageTypeSpecifier.colorModel)) && (this.sampleModel.equals(localImageTypeSpecifier.sampleModel));
  }
  
  public int hashCode()
  {
    return 9 * this.colorModel.hashCode() + 14 * this.sampleModel.hashCode();
  }
  
  private static ImageTypeSpecifier getSpecifier(int paramInt)
  {
    if (BISpecifier[paramInt] == null) {
      BISpecifier[paramInt] = createSpecifier(paramInt);
    }
    return BISpecifier[paramInt];
  }
  
  private static ImageTypeSpecifier createSpecifier(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return createPacked(sRGB, 16711680, 65280, 255, 0, 3, false);
    case 2: 
      return createPacked(sRGB, 16711680, 65280, 255, -16777216, 3, false);
    case 3: 
      return createPacked(sRGB, 16711680, 65280, 255, -16777216, 3, true);
    case 4: 
      return createPacked(sRGB, 255, 65280, 16711680, 0, 3, false);
    case 5: 
      return createInterleaved(sRGB, new int[] { 2, 1, 0 }, 0, false, false);
    case 6: 
      return createInterleaved(sRGB, new int[] { 3, 2, 1, 0 }, 0, true, false);
    case 7: 
      return createInterleaved(sRGB, new int[] { 3, 2, 1, 0 }, 0, true, true);
    case 8: 
      return createPacked(sRGB, 63488, 2016, 31, 0, 1, false);
    case 9: 
      return createPacked(sRGB, 31744, 992, 31, 0, 1, false);
    case 10: 
      return createGrayscale(8, 0, false);
    case 11: 
      return createGrayscale(16, 1, false);
    case 12: 
      return createGrayscale(1, 0, false);
    case 13: 
      BufferedImage localBufferedImage = new BufferedImage(1, 1, 13);
      IndexColorModel localIndexColorModel = (IndexColorModel)localBufferedImage.getColorModel();
      int i = localIndexColorModel.getMapSize();
      byte[] arrayOfByte1 = new byte[i];
      byte[] arrayOfByte2 = new byte[i];
      byte[] arrayOfByte3 = new byte[i];
      byte[] arrayOfByte4 = new byte[i];
      localIndexColorModel.getReds(arrayOfByte1);
      localIndexColorModel.getGreens(arrayOfByte2);
      localIndexColorModel.getBlues(arrayOfByte3);
      localIndexColorModel.getAlphas(arrayOfByte4);
      return createIndexed(arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4, 8, 0);
    }
    throw new IllegalArgumentException("Invalid BufferedImage type!");
  }
  
  static class Banded
    extends ImageTypeSpecifier
  {
    ColorSpace colorSpace;
    int[] bankIndices;
    int[] bandOffsets;
    int dataType;
    boolean hasAlpha;
    boolean isAlphaPremultiplied;
    
    public Banded(ColorSpace paramColorSpace, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      if (paramColorSpace == null) {
        throw new IllegalArgumentException("colorSpace == null!");
      }
      if (paramArrayOfInt1 == null) {
        throw new IllegalArgumentException("bankIndices == null!");
      }
      if (paramArrayOfInt2 == null) {
        throw new IllegalArgumentException("bandOffsets == null!");
      }
      if (paramArrayOfInt1.length != paramArrayOfInt2.length) {
        throw new IllegalArgumentException("bankIndices.length != bandOffsets.length!");
      }
      if ((paramInt != 0) && (paramInt != 2) && (paramInt != 1) && (paramInt != 3) && (paramInt != 4) && (paramInt != 5)) {
        throw new IllegalArgumentException("Bad value for dataType!");
      }
      int i = paramColorSpace.getNumComponents() + (paramBoolean1 ? 1 : 0);
      if (paramArrayOfInt2.length != i) {
        throw new IllegalArgumentException("bandOffsets.length is wrong!");
      }
      this.colorSpace = paramColorSpace;
      this.bankIndices = ((int[])paramArrayOfInt1.clone());
      this.bandOffsets = ((int[])paramArrayOfInt2.clone());
      this.dataType = paramInt;
      this.hasAlpha = paramBoolean1;
      this.isAlphaPremultiplied = paramBoolean2;
      this.colorModel = ImageTypeSpecifier.createComponentCM(paramColorSpace, paramArrayOfInt1.length, paramInt, paramBoolean1, paramBoolean2);
      int j = 1;
      int k = 1;
      this.sampleModel = new BandedSampleModel(paramInt, j, k, j, paramArrayOfInt1, paramArrayOfInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (!(paramObject instanceof Banded))) {
        return false;
      }
      Banded localBanded = (Banded)paramObject;
      if ((!this.colorSpace.equals(localBanded.colorSpace)) || (this.dataType != localBanded.dataType) || (this.hasAlpha != localBanded.hasAlpha) || (this.isAlphaPremultiplied != localBanded.isAlphaPremultiplied) || (this.bankIndices.length != localBanded.bankIndices.length) || (this.bandOffsets.length != localBanded.bandOffsets.length)) {
        return false;
      }
      for (int i = 0; i < this.bankIndices.length; i++) {
        if (this.bankIndices[i] != localBanded.bankIndices[i]) {
          return false;
        }
      }
      for (i = 0; i < this.bandOffsets.length; i++) {
        if (this.bandOffsets[i] != localBanded.bandOffsets[i]) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      return super.hashCode() + 3 * this.bandOffsets.length + 7 * this.bankIndices.length + 21 * this.dataType + (this.hasAlpha ? 19 : 29);
    }
  }
  
  static class Grayscale
    extends ImageTypeSpecifier
  {
    int bits;
    int dataType;
    boolean isSigned;
    boolean hasAlpha;
    boolean isAlphaPremultiplied;
    
    public Grayscale(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      super();
      if ((paramInt1 != 1) && (paramInt1 != 2) && (paramInt1 != 4) && (paramInt1 != 8) && (paramInt1 != 16)) {
        throw new IllegalArgumentException("Bad value for bits!");
      }
      if ((paramInt2 != 0) && (paramInt2 != 2) && (paramInt2 != 1)) {
        throw new IllegalArgumentException("Bad value for dataType!");
      }
      if ((paramInt1 > 8) && (paramInt2 == 0)) {
        throw new IllegalArgumentException("Too many bits for dataType!");
      }
      this.bits = paramInt1;
      this.dataType = paramInt2;
      this.isSigned = paramBoolean1;
      this.hasAlpha = paramBoolean2;
      this.isAlphaPremultiplied = paramBoolean3;
      ColorSpace localColorSpace = ColorSpace.getInstance(1003);
      int i;
      if (((paramInt1 == 8) && (paramInt2 == 0)) || ((paramInt1 == 16) && ((paramInt2 == 2) || (paramInt2 == 1))))
      {
        i = paramBoolean2 ? 2 : 1;
        int j = paramBoolean2 ? 3 : 1;
        int[] arrayOfInt1 = new int[i];
        arrayOfInt1[0] = paramInt1;
        if (i == 2) {
          arrayOfInt1[1] = paramInt1;
        }
        this.colorModel = new ComponentColorModel(localColorSpace, arrayOfInt1, paramBoolean2, paramBoolean3, j, paramInt2);
        int[] arrayOfInt2 = new int[i];
        arrayOfInt2[0] = 0;
        if (i == 2) {
          arrayOfInt2[1] = 1;
        }
        int m = 1;
        int n = 1;
        this.sampleModel = new PixelInterleavedSampleModel(paramInt2, m, n, i, m * i, arrayOfInt2);
      }
      else
      {
        i = 1 << paramInt1;
        byte[] arrayOfByte = new byte[i];
        for (int k = 0; k < i; k++) {
          arrayOfByte[k] = ((byte)(k * 255 / (i - 1)));
        }
        this.colorModel = new IndexColorModel(paramInt1, i, arrayOfByte, arrayOfByte, arrayOfByte);
        this.sampleModel = new MultiPixelPackedSampleModel(paramInt2, 1, 1, paramInt1);
      }
    }
  }
  
  static class Indexed
    extends ImageTypeSpecifier
  {
    byte[] redLUT;
    byte[] greenLUT;
    byte[] blueLUT;
    byte[] alphaLUT = null;
    int bits;
    int dataType;
    
    public Indexed(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt1, int paramInt2)
    {
      super();
      if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null) || (paramArrayOfByte3 == null)) {
        throw new IllegalArgumentException("LUT is null!");
      }
      if ((paramInt1 != 1) && (paramInt1 != 2) && (paramInt1 != 4) && (paramInt1 != 8) && (paramInt1 != 16)) {
        throw new IllegalArgumentException("Bad value for bits!");
      }
      if ((paramInt2 != 0) && (paramInt2 != 2) && (paramInt2 != 1) && (paramInt2 != 3)) {
        throw new IllegalArgumentException("Bad value for dataType!");
      }
      if (((paramInt1 > 8) && (paramInt2 == 0)) || ((paramInt1 > 16) && (paramInt2 != 3))) {
        throw new IllegalArgumentException("Too many bits for dataType!");
      }
      int i = 1 << paramInt1;
      if ((paramArrayOfByte1.length != i) || (paramArrayOfByte2.length != i) || (paramArrayOfByte3.length != i) || ((paramArrayOfByte4 != null) && (paramArrayOfByte4.length != i))) {
        throw new IllegalArgumentException("LUT has improper length!");
      }
      this.redLUT = ((byte[])paramArrayOfByte1.clone());
      this.greenLUT = ((byte[])paramArrayOfByte2.clone());
      this.blueLUT = ((byte[])paramArrayOfByte3.clone());
      if (paramArrayOfByte4 != null) {
        this.alphaLUT = ((byte[])paramArrayOfByte4.clone());
      }
      this.bits = paramInt1;
      this.dataType = paramInt2;
      if (paramArrayOfByte4 == null) {
        this.colorModel = new IndexColorModel(paramInt1, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
      } else {
        this.colorModel = new IndexColorModel(paramInt1, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramArrayOfByte4);
      }
      if (((paramInt1 == 8) && (paramInt2 == 0)) || ((paramInt1 == 16) && ((paramInt2 == 2) || (paramInt2 == 1))))
      {
        int[] arrayOfInt = { 0 };
        this.sampleModel = new PixelInterleavedSampleModel(paramInt2, 1, 1, 1, 1, arrayOfInt);
      }
      else
      {
        this.sampleModel = new MultiPixelPackedSampleModel(paramInt2, 1, 1, paramInt1);
      }
    }
  }
  
  static class Interleaved
    extends ImageTypeSpecifier
  {
    ColorSpace colorSpace;
    int[] bandOffsets;
    int dataType;
    boolean hasAlpha;
    boolean isAlphaPremultiplied;
    
    public Interleaved(ColorSpace paramColorSpace, int[] paramArrayOfInt, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      if (paramColorSpace == null) {
        throw new IllegalArgumentException("colorSpace == null!");
      }
      if (paramArrayOfInt == null) {
        throw new IllegalArgumentException("bandOffsets == null!");
      }
      int i = paramColorSpace.getNumComponents() + (paramBoolean1 ? 1 : 0);
      if (paramArrayOfInt.length != i) {
        throw new IllegalArgumentException("bandOffsets.length is wrong!");
      }
      if ((paramInt != 0) && (paramInt != 2) && (paramInt != 1) && (paramInt != 3) && (paramInt != 4) && (paramInt != 5)) {
        throw new IllegalArgumentException("Bad value for dataType!");
      }
      this.colorSpace = paramColorSpace;
      this.bandOffsets = ((int[])paramArrayOfInt.clone());
      this.dataType = paramInt;
      this.hasAlpha = paramBoolean1;
      this.isAlphaPremultiplied = paramBoolean2;
      this.colorModel = ImageTypeSpecifier.createComponentCM(paramColorSpace, paramArrayOfInt.length, paramInt, paramBoolean1, paramBoolean2);
      int j = paramArrayOfInt[0];
      int k = j;
      for (int m = 0; m < paramArrayOfInt.length; m++)
      {
        n = paramArrayOfInt[m];
        j = Math.min(n, j);
        k = Math.max(n, k);
      }
      m = k - j + 1;
      int n = 1;
      int i1 = 1;
      this.sampleModel = new PixelInterleavedSampleModel(paramInt, n, i1, m, n * m, paramArrayOfInt);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (!(paramObject instanceof Interleaved))) {
        return false;
      }
      Interleaved localInterleaved = (Interleaved)paramObject;
      if ((!this.colorSpace.equals(localInterleaved.colorSpace)) || (this.dataType != localInterleaved.dataType) || (this.hasAlpha != localInterleaved.hasAlpha) || (this.isAlphaPremultiplied != localInterleaved.isAlphaPremultiplied) || (this.bandOffsets.length != localInterleaved.bandOffsets.length)) {
        return false;
      }
      for (int i = 0; i < this.bandOffsets.length; i++) {
        if (this.bandOffsets[i] != localInterleaved.bandOffsets[i]) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      return super.hashCode() + 4 * this.bandOffsets.length + 25 * this.dataType + (this.hasAlpha ? 17 : 18);
    }
  }
  
  static class Packed
    extends ImageTypeSpecifier
  {
    ColorSpace colorSpace;
    int redMask;
    int greenMask;
    int blueMask;
    int alphaMask;
    int transferType;
    boolean isAlphaPremultiplied;
    
    public Packed(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
    {
      super();
      if (paramColorSpace == null) {
        throw new IllegalArgumentException("colorSpace == null!");
      }
      if (paramColorSpace.getType() != 5) {
        throw new IllegalArgumentException("colorSpace is not of type TYPE_RGB!");
      }
      if ((paramInt5 != 0) && (paramInt5 != 1) && (paramInt5 != 3)) {
        throw new IllegalArgumentException("Bad value for transferType!");
      }
      if ((paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == 0) && (paramInt4 == 0)) {
        throw new IllegalArgumentException("No mask has at least 1 bit set!");
      }
      this.colorSpace = paramColorSpace;
      this.redMask = paramInt1;
      this.greenMask = paramInt2;
      this.blueMask = paramInt3;
      this.alphaMask = paramInt4;
      this.transferType = paramInt5;
      this.isAlphaPremultiplied = paramBoolean;
      int i = 32;
      this.colorModel = new DirectColorModel(paramColorSpace, i, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean, paramInt5);
      this.sampleModel = this.colorModel.createCompatibleSampleModel(1, 1);
    }
  }
}
