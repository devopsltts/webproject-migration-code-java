package com.sun.imageio.plugins.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;

public class PaletteBuilder
{
  protected static final int MAXLEVEL = 8;
  protected RenderedImage src;
  protected ColorModel srcColorModel;
  protected Raster srcRaster;
  protected int requiredSize;
  protected ColorNode root;
  protected int numNodes;
  protected int maxNodes;
  protected int currLevel;
  protected int currSize;
  protected ColorNode[] reduceList;
  protected ColorNode[] palette;
  protected int transparency;
  protected ColorNode transColor;
  
  public static RenderedImage createIndexedImage(RenderedImage paramRenderedImage)
  {
    PaletteBuilder localPaletteBuilder = new PaletteBuilder(paramRenderedImage);
    localPaletteBuilder.buildPalette();
    return localPaletteBuilder.getIndexedImage();
  }
  
  public static IndexColorModel createIndexColorModel(RenderedImage paramRenderedImage)
  {
    PaletteBuilder localPaletteBuilder = new PaletteBuilder(paramRenderedImage);
    localPaletteBuilder.buildPalette();
    return localPaletteBuilder.getIndexColorModel();
  }
  
  public static boolean canCreatePalette(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    if (paramImageTypeSpecifier == null) {
      throw new IllegalArgumentException("type == null");
    }
    return true;
  }
  
  public static boolean canCreatePalette(RenderedImage paramRenderedImage)
  {
    if (paramRenderedImage == null) {
      throw new IllegalArgumentException("image == null");
    }
    ImageTypeSpecifier localImageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
    return canCreatePalette(localImageTypeSpecifier);
  }
  
  protected RenderedImage getIndexedImage()
  {
    IndexColorModel localIndexColorModel = getIndexColorModel();
    BufferedImage localBufferedImage = new BufferedImage(this.src.getWidth(), this.src.getHeight(), 13, localIndexColorModel);
    WritableRaster localWritableRaster = localBufferedImage.getRaster();
    for (int i = 0; i < localBufferedImage.getHeight(); i++) {
      for (int j = 0; j < localBufferedImage.getWidth(); j++)
      {
        Color localColor = getSrcColor(j, i);
        localWritableRaster.setSample(j, i, 0, findColorIndex(this.root, localColor));
      }
    }
    return localBufferedImage;
  }
  
  protected PaletteBuilder(RenderedImage paramRenderedImage)
  {
    this(paramRenderedImage, 256);
  }
  
  protected PaletteBuilder(RenderedImage paramRenderedImage, int paramInt)
  {
    this.src = paramRenderedImage;
    this.srcColorModel = paramRenderedImage.getColorModel();
    this.srcRaster = paramRenderedImage.getData();
    this.transparency = this.srcColorModel.getTransparency();
    this.requiredSize = paramInt;
  }
  
  private Color getSrcColor(int paramInt1, int paramInt2)
  {
    int i = this.srcColorModel.getRGB(this.srcRaster.getDataElements(paramInt1, paramInt2, null));
    return new Color(i, this.transparency != 1);
  }
  
  protected int findColorIndex(ColorNode paramColorNode, Color paramColor)
  {
    if ((this.transparency != 1) && (paramColor.getAlpha() != 255)) {
      return 0;
    }
    if (paramColorNode.isLeaf) {
      return paramColorNode.paletteIndex;
    }
    int i = getBranchIndex(paramColor, paramColorNode.level);
    return findColorIndex(paramColorNode.children[i], paramColor);
  }
  
  protected void buildPalette()
  {
    this.reduceList = new ColorNode[9];
    for (int i = 0; i < this.reduceList.length; i++) {
      this.reduceList[i] = null;
    }
    this.numNodes = 0;
    this.maxNodes = 0;
    this.root = null;
    this.currSize = 0;
    this.currLevel = 8;
    i = this.src.getWidth();
    int j = this.src.getHeight();
    for (int k = 0; k < j; k++) {
      for (int m = 0; m < i; m++)
      {
        Color localColor = getSrcColor(i - m - 1, j - k - 1);
        if ((this.transparency != 1) && (localColor.getAlpha() != 255))
        {
          if (this.transColor == null)
          {
            this.requiredSize -= 1;
            this.transColor = new ColorNode();
            this.transColor.isLeaf = true;
          }
          this.transColor = insertNode(this.transColor, localColor, 0);
        }
        else
        {
          this.root = insertNode(this.root, localColor, 0);
        }
        if (this.currSize > this.requiredSize) {
          reduceTree();
        }
      }
    }
  }
  
  protected ColorNode insertNode(ColorNode paramColorNode, Color paramColor, int paramInt)
  {
    if (paramColorNode == null)
    {
      paramColorNode = new ColorNode();
      this.numNodes += 1;
      if (this.numNodes > this.maxNodes) {
        this.maxNodes = this.numNodes;
      }
      paramColorNode.level = paramInt;
      paramColorNode.isLeaf = (paramInt > 8);
      if (paramColorNode.isLeaf) {
        this.currSize += 1;
      }
    }
    paramColorNode.colorCount += 1;
    paramColorNode.red += paramColor.getRed();
    paramColorNode.green += paramColor.getGreen();
    paramColorNode.blue += paramColor.getBlue();
    if (!paramColorNode.isLeaf)
    {
      int i = getBranchIndex(paramColor, paramInt);
      if (paramColorNode.children[i] == null)
      {
        paramColorNode.childCount += 1;
        if (paramColorNode.childCount == 2)
        {
          paramColorNode.nextReducible = this.reduceList[paramInt];
          this.reduceList[paramInt] = paramColorNode;
        }
      }
      paramColorNode.children[i] = insertNode(paramColorNode.children[i], paramColor, paramInt + 1);
    }
    return paramColorNode;
  }
  
  protected IndexColorModel getIndexColorModel()
  {
    int i = this.currSize;
    if (this.transColor != null) {
      i++;
    }
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    int j = 0;
    this.palette = new ColorNode[i];
    if (this.transColor != null) {
      j++;
    }
    if (this.root != null) {
      findPaletteEntry(this.root, j, arrayOfByte1, arrayOfByte2, arrayOfByte3);
    }
    IndexColorModel localIndexColorModel = null;
    if (this.transColor != null) {
      localIndexColorModel = new IndexColorModel(8, i, arrayOfByte1, arrayOfByte2, arrayOfByte3, 0);
    } else {
      localIndexColorModel = new IndexColorModel(8, this.currSize, arrayOfByte1, arrayOfByte2, arrayOfByte3);
    }
    return localIndexColorModel;
  }
  
  protected int findPaletteEntry(ColorNode paramColorNode, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    if (paramColorNode.isLeaf)
    {
      paramArrayOfByte1[paramInt] = ((byte)(int)(paramColorNode.red / paramColorNode.colorCount));
      paramArrayOfByte2[paramInt] = ((byte)(int)(paramColorNode.green / paramColorNode.colorCount));
      paramArrayOfByte3[paramInt] = ((byte)(int)(paramColorNode.blue / paramColorNode.colorCount));
      paramColorNode.paletteIndex = paramInt;
      this.palette[paramInt] = paramColorNode;
      paramInt++;
    }
    else
    {
      for (int i = 0; i < 8; i++) {
        if (paramColorNode.children[i] != null) {
          paramInt = findPaletteEntry(paramColorNode.children[i], paramInt, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
        }
      }
    }
    return paramInt;
  }
  
  protected int getBranchIndex(Color paramColor, int paramInt)
  {
    if ((paramInt > 8) || (paramInt < 0)) {
      throw new IllegalArgumentException("Invalid octree node depth: " + paramInt);
    }
    int i = 8 - paramInt;
    int j = 0x1 & (0xFF & paramColor.getRed()) >> i;
    int k = 0x1 & (0xFF & paramColor.getGreen()) >> i;
    int m = 0x1 & (0xFF & paramColor.getBlue()) >> i;
    int n = j << 2 | k << 1 | m;
    return n;
  }
  
  protected void reduceTree()
  {
    for (int i = this.reduceList.length - 1; (this.reduceList[i] == null) && (i >= 0); i--) {}
    Object localObject1 = this.reduceList[i];
    if (localObject1 == null) {
      return;
    }
    Object localObject2 = localObject1;
    int j = ((ColorNode)localObject2).colorCount;
    for (int k = 1; ((ColorNode)localObject2).nextReducible != null; k++)
    {
      if (j > ((ColorNode)localObject2).nextReducible.colorCount)
      {
        localObject1 = localObject2;
        j = ((ColorNode)localObject2).colorCount;
      }
      localObject2 = ((ColorNode)localObject2).nextReducible;
    }
    if (localObject1 == this.reduceList[i])
    {
      this.reduceList[i] = ((ColorNode)localObject1).nextReducible;
    }
    else
    {
      localObject2 = ((ColorNode)localObject1).nextReducible;
      ((ColorNode)localObject1).nextReducible = ((ColorNode)localObject2).nextReducible;
      localObject1 = localObject2;
    }
    if (((ColorNode)localObject1).isLeaf) {
      return;
    }
    int m = ((ColorNode)localObject1).getLeafChildCount();
    ((ColorNode)localObject1).isLeaf = true;
    this.currSize -= m - 1;
    int n = ((ColorNode)localObject1).level;
    for (int i1 = 0; i1 < 8; i1++) {
      ((ColorNode)localObject1).children[i1] = freeTree(localObject1.children[i1]);
    }
    ((ColorNode)localObject1).childCount = 0;
  }
  
  protected ColorNode freeTree(ColorNode paramColorNode)
  {
    if (paramColorNode == null) {
      return null;
    }
    for (int i = 0; i < 8; i++) {
      paramColorNode.children[i] = freeTree(paramColorNode.children[i]);
    }
    this.numNodes -= 1;
    return null;
  }
  
  protected class ColorNode
  {
    public boolean isLeaf = false;
    public int childCount = 0;
    ColorNode[] children = new ColorNode[8];
    public int colorCount;
    public long red;
    public long blue;
    public long green;
    public int paletteIndex;
    public int level = 0;
    ColorNode nextReducible;
    
    public ColorNode()
    {
      for (int i = 0; i < 8; i++) {
        this.children[i] = null;
      }
      this.colorCount = 0;
      this.red = (this.green = this.blue = 0L);
      this.paletteIndex = 0;
    }
    
    public int getLeafChildCount()
    {
      if (this.isLeaf) {
        return 0;
      }
      int i = 0;
      for (int j = 0; j < this.children.length; j++) {
        if (this.children[j] != null) {
          if (this.children[j].isLeaf) {
            i++;
          } else {
            i += this.children[j].getLeafChildCount();
          }
        }
      }
      return i;
    }
    
    public int getRGB()
    {
      int i = (int)this.red / this.colorCount;
      int j = (int)this.green / this.colorCount;
      int k = (int)this.blue / this.colorCount;
      int m = 0xFF000000 | (0xFF & i) << 16 | (0xFF & j) << 8 | 0xFF & k;
      return m;
    }
  }
}
