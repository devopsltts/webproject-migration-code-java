package javax.imageio;

import java.awt.Dimension;
import java.util.Locale;

public class ImageWriteParam
  extends IIOParam
{
  public static final int MODE_DISABLED = 0;
  public static final int MODE_DEFAULT = 1;
  public static final int MODE_EXPLICIT = 2;
  public static final int MODE_COPY_FROM_METADATA = 3;
  private static final int MAX_MODE = 3;
  protected boolean canWriteTiles = false;
  protected int tilingMode = 3;
  protected Dimension[] preferredTileSizes = null;
  protected boolean tilingSet = false;
  protected int tileWidth = 0;
  protected int tileHeight = 0;
  protected boolean canOffsetTiles = false;
  protected int tileGridXOffset = 0;
  protected int tileGridYOffset = 0;
  protected boolean canWriteProgressive = false;
  protected int progressiveMode = 3;
  protected boolean canWriteCompressed = false;
  protected int compressionMode = 3;
  protected String[] compressionTypes = null;
  protected String compressionType = null;
  protected float compressionQuality = 1.0F;
  protected Locale locale = null;
  
  protected ImageWriteParam() {}
  
  public ImageWriteParam(Locale paramLocale)
  {
    this.locale = paramLocale;
  }
  
  private static Dimension[] clonePreferredTileSizes(Dimension[] paramArrayOfDimension)
  {
    if (paramArrayOfDimension == null) {
      return null;
    }
    Dimension[] arrayOfDimension = new Dimension[paramArrayOfDimension.length];
    for (int i = 0; i < paramArrayOfDimension.length; i++) {
      arrayOfDimension[i] = new Dimension(paramArrayOfDimension[i]);
    }
    return arrayOfDimension;
  }
  
  public Locale getLocale()
  {
    return this.locale;
  }
  
  public boolean canWriteTiles()
  {
    return this.canWriteTiles;
  }
  
  public boolean canOffsetTiles()
  {
    return this.canOffsetTiles;
  }
  
  public void setTilingMode(int paramInt)
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("Illegal value for mode!");
    }
    this.tilingMode = paramInt;
    if (paramInt == 2) {
      unsetTiling();
    }
  }
  
  public int getTilingMode()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported");
    }
    return this.tilingMode;
  }
  
  public Dimension[] getPreferredTileSizes()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported");
    }
    return clonePreferredTileSizes(this.preferredTileSizes);
  }
  
  public void setTiling(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if (getTilingMode() != 2) {
      throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
    }
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("tile dimensions are non-positive!");
    }
    int i = (paramInt3 != 0) || (paramInt4 != 0) ? 1 : 0;
    if ((!canOffsetTiles()) && (i != 0)) {
      throw new UnsupportedOperationException("Can't offset tiles!");
    }
    if (this.preferredTileSizes != null)
    {
      int j = 1;
      for (int k = 0; k < this.preferredTileSizes.length; k += 2)
      {
        Dimension localDimension1 = this.preferredTileSizes[k];
        Dimension localDimension2 = this.preferredTileSizes[(k + 1)];
        if ((paramInt1 < localDimension1.width) || (paramInt1 > localDimension2.width) || (paramInt2 < localDimension1.height) || (paramInt2 > localDimension2.height))
        {
          j = 0;
          break;
        }
      }
      if (j == 0) {
        throw new IllegalArgumentException("Illegal tile size!");
      }
    }
    this.tilingSet = true;
    this.tileWidth = paramInt1;
    this.tileHeight = paramInt2;
    this.tileGridXOffset = paramInt3;
    this.tileGridYOffset = paramInt4;
  }
  
  public void unsetTiling()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if (getTilingMode() != 2) {
      throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
    }
    this.tilingSet = false;
    this.tileWidth = 0;
    this.tileHeight = 0;
    this.tileGridXOffset = 0;
    this.tileGridYOffset = 0;
  }
  
  public int getTileWidth()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if (getTilingMode() != 2) {
      throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
    }
    if (!this.tilingSet) {
      throw new IllegalStateException("Tiling parameters not set!");
    }
    return this.tileWidth;
  }
  
  public int getTileHeight()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if (getTilingMode() != 2) {
      throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
    }
    if (!this.tilingSet) {
      throw new IllegalStateException("Tiling parameters not set!");
    }
    return this.tileHeight;
  }
  
  public int getTileGridXOffset()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if (getTilingMode() != 2) {
      throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
    }
    if (!this.tilingSet) {
      throw new IllegalStateException("Tiling parameters not set!");
    }
    return this.tileGridXOffset;
  }
  
  public int getTileGridYOffset()
  {
    if (!canWriteTiles()) {
      throw new UnsupportedOperationException("Tiling not supported!");
    }
    if (getTilingMode() != 2) {
      throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
    }
    if (!this.tilingSet) {
      throw new IllegalStateException("Tiling parameters not set!");
    }
    return this.tileGridYOffset;
  }
  
  public boolean canWriteProgressive()
  {
    return this.canWriteProgressive;
  }
  
  public void setProgressiveMode(int paramInt)
  {
    if (!canWriteProgressive()) {
      throw new UnsupportedOperationException("Progressive output not supported");
    }
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("Illegal value for mode!");
    }
    if (paramInt == 2) {
      throw new IllegalArgumentException("MODE_EXPLICIT not supported for progressive output");
    }
    this.progressiveMode = paramInt;
  }
  
  public int getProgressiveMode()
  {
    if (!canWriteProgressive()) {
      throw new UnsupportedOperationException("Progressive output not supported");
    }
    return this.progressiveMode;
  }
  
  public boolean canWriteCompressed()
  {
    return this.canWriteCompressed;
  }
  
  public void setCompressionMode(int paramInt)
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("Illegal value for mode!");
    }
    this.compressionMode = paramInt;
    if (paramInt == 2) {
      unsetCompression();
    }
  }
  
  public int getCompressionMode()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    return this.compressionMode;
  }
  
  public String[] getCompressionTypes()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported");
    }
    if (this.compressionTypes == null) {
      return null;
    }
    return (String[])this.compressionTypes.clone();
  }
  
  public void setCompressionType(String paramString)
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    String[] arrayOfString = getCompressionTypes();
    if (arrayOfString == null) {
      throw new UnsupportedOperationException("No settable compression types");
    }
    if (paramString != null)
    {
      int i = 0;
      if (arrayOfString != null) {
        for (int j = 0; j < arrayOfString.length; j++) {
          if (paramString.equals(arrayOfString[j]))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0) {
        throw new IllegalArgumentException("Unknown compression type!");
      }
    }
    this.compressionType = paramString;
  }
  
  public String getCompressionType()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    return this.compressionType;
  }
  
  public void unsetCompression()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    this.compressionType = null;
    this.compressionQuality = 1.0F;
  }
  
  public String getLocalizedCompressionTypeName()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if (getCompressionType() == null) {
      throw new IllegalStateException("No compression type set!");
    }
    return getCompressionType();
  }
  
  public boolean isCompressionLossless()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    return true;
  }
  
  public void setCompressionQuality(float paramFloat)
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("Quality out-of-bounds!");
    }
    this.compressionQuality = paramFloat;
  }
  
  public float getCompressionQuality()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    return this.compressionQuality;
  }
  
  public float getBitRate(float paramFloat)
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("Quality out-of-bounds!");
    }
    return -1.0F;
  }
  
  public String[] getCompressionQualityDescriptions()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    return null;
  }
  
  public float[] getCompressionQualityValues()
  {
    if (!canWriteCompressed()) {
      throw new UnsupportedOperationException("Compression not supported.");
    }
    if (getCompressionMode() != 2) {
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
    }
    if ((getCompressionTypes() != null) && (getCompressionType() == null)) {
      throw new IllegalStateException("No compression type set!");
    }
    return null;
  }
}
