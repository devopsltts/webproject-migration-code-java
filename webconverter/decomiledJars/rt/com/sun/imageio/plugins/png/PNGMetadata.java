package com.sun.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PNGMetadata
  extends IIOMetadata
  implements Cloneable
{
  public static final String nativeMetadataFormatName = "javax_imageio_png_1.0";
  protected static final String nativeMetadataFormatClassName = "com.sun.imageio.plugins.png.PNGMetadataFormat";
  static final String[] IHDR_colorTypeNames = { "Grayscale", null, "RGB", "Palette", "GrayAlpha", null, "RGBAlpha" };
  static final int[] IHDR_numChannels = { 1, 0, 3, 3, 2, 0, 4 };
  static final String[] IHDR_bitDepths = { "1", "2", "4", "8", "16" };
  static final String[] IHDR_compressionMethodNames = { "deflate" };
  static final String[] IHDR_filterMethodNames = { "adaptive" };
  static final String[] IHDR_interlaceMethodNames = { "none", "adam7" };
  static final String[] iCCP_compressionMethodNames = { "deflate" };
  static final String[] zTXt_compressionMethodNames = { "deflate" };
  public static final int PHYS_UNIT_UNKNOWN = 0;
  public static final int PHYS_UNIT_METER = 1;
  static final String[] unitSpecifierNames = { "unknown", "meter" };
  static final String[] renderingIntentNames = { "Perceptual", "Relative colorimetric", "Saturation", "Absolute colorimetric" };
  static final String[] colorSpaceTypeNames = { "GRAY", null, "RGB", "RGB", "GRAY", null, "RGB" };
  public boolean IHDR_present;
  public int IHDR_width;
  public int IHDR_height;
  public int IHDR_bitDepth;
  public int IHDR_colorType;
  public int IHDR_compressionMethod;
  public int IHDR_filterMethod;
  public int IHDR_interlaceMethod;
  public boolean PLTE_present;
  public byte[] PLTE_red;
  public byte[] PLTE_green;
  public byte[] PLTE_blue;
  public int[] PLTE_order = null;
  public boolean bKGD_present;
  public int bKGD_colorType;
  public int bKGD_index;
  public int bKGD_gray;
  public int bKGD_red;
  public int bKGD_green;
  public int bKGD_blue;
  public boolean cHRM_present;
  public int cHRM_whitePointX;
  public int cHRM_whitePointY;
  public int cHRM_redX;
  public int cHRM_redY;
  public int cHRM_greenX;
  public int cHRM_greenY;
  public int cHRM_blueX;
  public int cHRM_blueY;
  public boolean gAMA_present;
  public int gAMA_gamma;
  public boolean hIST_present;
  public char[] hIST_histogram;
  public boolean iCCP_present;
  public String iCCP_profileName;
  public int iCCP_compressionMethod;
  public byte[] iCCP_compressedProfile;
  public ArrayList<String> iTXt_keyword = new ArrayList();
  public ArrayList<Boolean> iTXt_compressionFlag = new ArrayList();
  public ArrayList<Integer> iTXt_compressionMethod = new ArrayList();
  public ArrayList<String> iTXt_languageTag = new ArrayList();
  public ArrayList<String> iTXt_translatedKeyword = new ArrayList();
  public ArrayList<String> iTXt_text = new ArrayList();
  public boolean pHYs_present;
  public int pHYs_pixelsPerUnitXAxis;
  public int pHYs_pixelsPerUnitYAxis;
  public int pHYs_unitSpecifier;
  public boolean sBIT_present;
  public int sBIT_colorType;
  public int sBIT_grayBits;
  public int sBIT_redBits;
  public int sBIT_greenBits;
  public int sBIT_blueBits;
  public int sBIT_alphaBits;
  public boolean sPLT_present;
  public String sPLT_paletteName;
  public int sPLT_sampleDepth;
  public int[] sPLT_red;
  public int[] sPLT_green;
  public int[] sPLT_blue;
  public int[] sPLT_alpha;
  public int[] sPLT_frequency;
  public boolean sRGB_present;
  public int sRGB_renderingIntent;
  public ArrayList<String> tEXt_keyword = new ArrayList();
  public ArrayList<String> tEXt_text = new ArrayList();
  public boolean tIME_present;
  public int tIME_year;
  public int tIME_month;
  public int tIME_day;
  public int tIME_hour;
  public int tIME_minute;
  public int tIME_second;
  public boolean tRNS_present;
  public int tRNS_colorType;
  public byte[] tRNS_alpha;
  public int tRNS_gray;
  public int tRNS_red;
  public int tRNS_green;
  public int tRNS_blue;
  public ArrayList<String> zTXt_keyword = new ArrayList();
  public ArrayList<Integer> zTXt_compressionMethod = new ArrayList();
  public ArrayList<String> zTXt_text = new ArrayList();
  public ArrayList<String> unknownChunkType = new ArrayList();
  public ArrayList<byte[]> unknownChunkData = new ArrayList();
  
  public PNGMetadata()
  {
    super(true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null);
  }
  
  public PNGMetadata(IIOMetadata paramIIOMetadata) {}
  
  public void initialize(ImageTypeSpecifier paramImageTypeSpecifier, int paramInt)
  {
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    SampleModel localSampleModel = paramImageTypeSpecifier.getSampleModel();
    int[] arrayOfInt = localSampleModel.getSampleSize();
    int i = arrayOfInt[0];
    for (int j = 1; j < arrayOfInt.length; j++) {
      if (arrayOfInt[j] > i) {
        i = arrayOfInt[j];
      }
    }
    if ((arrayOfInt.length > 1) && (i < 8)) {
      i = 8;
    }
    if ((i > 2) && (i < 4)) {
      i = 4;
    } else if ((i > 4) && (i < 8)) {
      i = 8;
    } else if ((i > 8) && (i < 16)) {
      i = 16;
    } else if (i > 16) {
      throw new RuntimeException("bitDepth > 16!");
    }
    this.IHDR_bitDepth = i;
    if ((localColorModel instanceof IndexColorModel))
    {
      IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
      int k = localIndexColorModel.getMapSize();
      byte[] arrayOfByte1 = new byte[k];
      localIndexColorModel.getReds(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[k];
      localIndexColorModel.getGreens(arrayOfByte2);
      byte[] arrayOfByte3 = new byte[k];
      localIndexColorModel.getBlues(arrayOfByte3);
      int m = 0;
      if ((!this.IHDR_present) || (this.IHDR_colorType != 3))
      {
        m = 1;
        int n = 255 / ((1 << this.IHDR_bitDepth) - 1);
        for (int i1 = 0; i1 < k; i1++)
        {
          int i2 = arrayOfByte1[i1];
          if ((i2 != (byte)(i1 * n)) || (i2 != arrayOfByte2[i1]) || (i2 != arrayOfByte3[i1]))
          {
            m = 0;
            break;
          }
        }
      }
      boolean bool = localColorModel.hasAlpha();
      byte[] arrayOfByte4 = null;
      if (bool)
      {
        arrayOfByte4 = new byte[k];
        localIndexColorModel.getAlphas(arrayOfByte4);
      }
      if ((m != 0) && (bool) && ((i == 8) || (i == 16)))
      {
        this.IHDR_colorType = 4;
      }
      else if ((m != 0) && (!bool))
      {
        this.IHDR_colorType = 0;
      }
      else
      {
        this.IHDR_colorType = 3;
        this.PLTE_present = true;
        this.PLTE_order = null;
        this.PLTE_red = ((byte[])arrayOfByte1.clone());
        this.PLTE_green = ((byte[])arrayOfByte2.clone());
        this.PLTE_blue = ((byte[])arrayOfByte3.clone());
        if (bool)
        {
          this.tRNS_present = true;
          this.tRNS_colorType = 3;
          this.PLTE_order = new int[arrayOfByte4.length];
          byte[] arrayOfByte5 = new byte[arrayOfByte4.length];
          int i3 = 0;
          for (int i4 = 0; i4 < arrayOfByte4.length; i4++) {
            if (arrayOfByte4[i4] != -1)
            {
              this.PLTE_order[i4] = i3;
              arrayOfByte5[i3] = arrayOfByte4[i4];
              i3++;
            }
          }
          i4 = i3;
          for (int i5 = 0; i5 < arrayOfByte4.length; i5++) {
            if (arrayOfByte4[i5] == -1) {
              this.PLTE_order[i5] = (i3++);
            }
          }
          byte[] arrayOfByte6 = this.PLTE_red;
          byte[] arrayOfByte7 = this.PLTE_green;
          byte[] arrayOfByte8 = this.PLTE_blue;
          int i6 = arrayOfByte6.length;
          this.PLTE_red = new byte[i6];
          this.PLTE_green = new byte[i6];
          this.PLTE_blue = new byte[i6];
          for (int i7 = 0; i7 < i6; i7++)
          {
            this.PLTE_red[this.PLTE_order[i7]] = arrayOfByte6[i7];
            this.PLTE_green[this.PLTE_order[i7]] = arrayOfByte7[i7];
            this.PLTE_blue[this.PLTE_order[i7]] = arrayOfByte8[i7];
          }
          this.tRNS_alpha = new byte[i4];
          System.arraycopy(arrayOfByte5, 0, this.tRNS_alpha, 0, i4);
        }
      }
    }
    else if (paramInt == 1)
    {
      this.IHDR_colorType = 0;
    }
    else if (paramInt == 2)
    {
      this.IHDR_colorType = 4;
    }
    else if (paramInt == 3)
    {
      this.IHDR_colorType = 2;
    }
    else if (paramInt == 4)
    {
      this.IHDR_colorType = 6;
    }
    else
    {
      throw new RuntimeException("Number of bands not 1-4!");
    }
    this.IHDR_present = true;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  private ArrayList<byte[]> cloneBytesArrayList(ArrayList<byte[]> paramArrayList)
  {
    if (paramArrayList == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramArrayList.size());
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      byte[] arrayOfByte = (byte[])localIterator.next();
      localArrayList.add(arrayOfByte == null ? null : (byte[])arrayOfByte.clone());
    }
    return localArrayList;
  }
  
  public Object clone()
  {
    PNGMetadata localPNGMetadata;
    try
    {
      localPNGMetadata = (PNGMetadata)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      return null;
    }
    localPNGMetadata.unknownChunkData = cloneBytesArrayList(this.unknownChunkData);
    return localPNGMetadata;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString.equals("javax_imageio_png_1.0")) {
      return getNativeTree();
    }
    if (paramString.equals("javax_imageio_1.0")) {
      return getStandardTree();
    }
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private Node getNativeTree()
  {
    IIOMetadataNode localIIOMetadataNode1 = null;
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("javax_imageio_png_1.0");
    IIOMetadataNode localIIOMetadataNode3;
    if (this.IHDR_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("IHDR");
      localIIOMetadataNode3.setAttribute("width", Integer.toString(this.IHDR_width));
      localIIOMetadataNode3.setAttribute("height", Integer.toString(this.IHDR_height));
      localIIOMetadataNode3.setAttribute("bitDepth", Integer.toString(this.IHDR_bitDepth));
      localIIOMetadataNode3.setAttribute("colorType", IHDR_colorTypeNames[this.IHDR_colorType]);
      localIIOMetadataNode3.setAttribute("compressionMethod", IHDR_compressionMethodNames[this.IHDR_compressionMethod]);
      localIIOMetadataNode3.setAttribute("filterMethod", IHDR_filterMethodNames[this.IHDR_filterMethod]);
      localIIOMetadataNode3.setAttribute("interlaceMethod", IHDR_interlaceMethodNames[this.IHDR_interlaceMethod]);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    int i;
    IIOMetadataNode localIIOMetadataNode6;
    if (this.PLTE_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("PLTE");
      i = this.PLTE_red.length;
      for (int k = 0; k < i; k++)
      {
        localIIOMetadataNode6 = new IIOMetadataNode("PLTEEntry");
        localIIOMetadataNode6.setAttribute("index", Integer.toString(k));
        localIIOMetadataNode6.setAttribute("red", Integer.toString(this.PLTE_red[k] & 0xFF));
        localIIOMetadataNode6.setAttribute("green", Integer.toString(this.PLTE_green[k] & 0xFF));
        localIIOMetadataNode6.setAttribute("blue", Integer.toString(this.PLTE_blue[k] & 0xFF));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode6);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.bKGD_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("bKGD");
      if (this.bKGD_colorType == 3)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("bKGD_Palette");
        localIIOMetadataNode1.setAttribute("index", Integer.toString(this.bKGD_index));
      }
      else if (this.bKGD_colorType == 0)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("bKGD_Grayscale");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(this.bKGD_gray));
      }
      else if (this.bKGD_colorType == 2)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("bKGD_RGB");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(this.bKGD_red));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(this.bKGD_green));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(this.bKGD_blue));
      }
      localIIOMetadataNode3.appendChild(localIIOMetadataNode1);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.cHRM_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("cHRM");
      localIIOMetadataNode3.setAttribute("whitePointX", Integer.toString(this.cHRM_whitePointX));
      localIIOMetadataNode3.setAttribute("whitePointY", Integer.toString(this.cHRM_whitePointY));
      localIIOMetadataNode3.setAttribute("redX", Integer.toString(this.cHRM_redX));
      localIIOMetadataNode3.setAttribute("redY", Integer.toString(this.cHRM_redY));
      localIIOMetadataNode3.setAttribute("greenX", Integer.toString(this.cHRM_greenX));
      localIIOMetadataNode3.setAttribute("greenY", Integer.toString(this.cHRM_greenY));
      localIIOMetadataNode3.setAttribute("blueX", Integer.toString(this.cHRM_blueX));
      localIIOMetadataNode3.setAttribute("blueY", Integer.toString(this.cHRM_blueY));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.gAMA_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("gAMA");
      localIIOMetadataNode3.setAttribute("value", Integer.toString(this.gAMA_gamma));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    IIOMetadataNode localIIOMetadataNode4;
    if (this.hIST_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("hIST");
      for (i = 0; i < this.hIST_histogram.length; i++)
      {
        localIIOMetadataNode4 = new IIOMetadataNode("hISTEntry");
        localIIOMetadataNode4.setAttribute("index", Integer.toString(i));
        localIIOMetadataNode4.setAttribute("value", Integer.toString(this.hIST_histogram[i]));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode4);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.iCCP_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("iCCP");
      localIIOMetadataNode3.setAttribute("profileName", this.iCCP_profileName);
      localIIOMetadataNode3.setAttribute("compressionMethod", iCCP_compressionMethodNames[this.iCCP_compressionMethod]);
      Object localObject = this.iCCP_compressedProfile;
      if (localObject != null) {
        localObject = ((byte[])localObject).clone();
      }
      localIIOMetadataNode3.setUserObject(localObject);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    int j;
    if (this.iTXt_keyword.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("iTXt");
      for (j = 0; j < this.iTXt_keyword.size(); j++)
      {
        localIIOMetadataNode4 = new IIOMetadataNode("iTXtEntry");
        localIIOMetadataNode4.setAttribute("keyword", (String)this.iTXt_keyword.get(j));
        localIIOMetadataNode4.setAttribute("compressionFlag", ((Boolean)this.iTXt_compressionFlag.get(j)).booleanValue() ? "TRUE" : "FALSE");
        localIIOMetadataNode4.setAttribute("compressionMethod", ((Integer)this.iTXt_compressionMethod.get(j)).toString());
        localIIOMetadataNode4.setAttribute("languageTag", (String)this.iTXt_languageTag.get(j));
        localIIOMetadataNode4.setAttribute("translatedKeyword", (String)this.iTXt_translatedKeyword.get(j));
        localIIOMetadataNode4.setAttribute("text", (String)this.iTXt_text.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode4);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.pHYs_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("pHYs");
      localIIOMetadataNode3.setAttribute("pixelsPerUnitXAxis", Integer.toString(this.pHYs_pixelsPerUnitXAxis));
      localIIOMetadataNode3.setAttribute("pixelsPerUnitYAxis", Integer.toString(this.pHYs_pixelsPerUnitYAxis));
      localIIOMetadataNode3.setAttribute("unitSpecifier", unitSpecifierNames[this.pHYs_unitSpecifier]);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.sBIT_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("sBIT");
      if (this.sBIT_colorType == 0)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_Grayscale");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
      }
      else if (this.sBIT_colorType == 4)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_GrayAlpha");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
        localIIOMetadataNode1.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
      }
      else if (this.sBIT_colorType == 2)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_RGB");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(this.sBIT_redBits));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
      }
      else if (this.sBIT_colorType == 6)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_RGBAlpha");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(this.sBIT_redBits));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
        localIIOMetadataNode1.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
      }
      else if (this.sBIT_colorType == 3)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_Palette");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(this.sBIT_redBits));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
      }
      localIIOMetadataNode3.appendChild(localIIOMetadataNode1);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.sPLT_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("sPLT");
      localIIOMetadataNode3.setAttribute("name", this.sPLT_paletteName);
      localIIOMetadataNode3.setAttribute("sampleDepth", Integer.toString(this.sPLT_sampleDepth));
      j = this.sPLT_red.length;
      for (int m = 0; m < j; m++)
      {
        localIIOMetadataNode6 = new IIOMetadataNode("sPLTEntry");
        localIIOMetadataNode6.setAttribute("index", Integer.toString(m));
        localIIOMetadataNode6.setAttribute("red", Integer.toString(this.sPLT_red[m]));
        localIIOMetadataNode6.setAttribute("green", Integer.toString(this.sPLT_green[m]));
        localIIOMetadataNode6.setAttribute("blue", Integer.toString(this.sPLT_blue[m]));
        localIIOMetadataNode6.setAttribute("alpha", Integer.toString(this.sPLT_alpha[m]));
        localIIOMetadataNode6.setAttribute("frequency", Integer.toString(this.sPLT_frequency[m]));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode6);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.sRGB_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("sRGB");
      localIIOMetadataNode3.setAttribute("renderingIntent", renderingIntentNames[this.sRGB_renderingIntent]);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    IIOMetadataNode localIIOMetadataNode5;
    if (this.tEXt_keyword.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("tEXt");
      for (j = 0; j < this.tEXt_keyword.size(); j++)
      {
        localIIOMetadataNode5 = new IIOMetadataNode("tEXtEntry");
        localIIOMetadataNode5.setAttribute("keyword", (String)this.tEXt_keyword.get(j));
        localIIOMetadataNode5.setAttribute("value", (String)this.tEXt_text.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode5);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.tIME_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("tIME");
      localIIOMetadataNode3.setAttribute("year", Integer.toString(this.tIME_year));
      localIIOMetadataNode3.setAttribute("month", Integer.toString(this.tIME_month));
      localIIOMetadataNode3.setAttribute("day", Integer.toString(this.tIME_day));
      localIIOMetadataNode3.setAttribute("hour", Integer.toString(this.tIME_hour));
      localIIOMetadataNode3.setAttribute("minute", Integer.toString(this.tIME_minute));
      localIIOMetadataNode3.setAttribute("second", Integer.toString(this.tIME_second));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.tRNS_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("tRNS");
      if (this.tRNS_colorType == 3)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("tRNS_Palette");
        for (j = 0; j < this.tRNS_alpha.length; j++)
        {
          localIIOMetadataNode5 = new IIOMetadataNode("tRNS_PaletteEntry");
          localIIOMetadataNode5.setAttribute("index", Integer.toString(j));
          localIIOMetadataNode5.setAttribute("alpha", Integer.toString(this.tRNS_alpha[j] & 0xFF));
          localIIOMetadataNode1.appendChild(localIIOMetadataNode5);
        }
      }
      else if (this.tRNS_colorType == 0)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("tRNS_Grayscale");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(this.tRNS_gray));
      }
      else if (this.tRNS_colorType == 2)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("tRNS_RGB");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(this.tRNS_red));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(this.tRNS_green));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(this.tRNS_blue));
      }
      localIIOMetadataNode3.appendChild(localIIOMetadataNode1);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.zTXt_keyword.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("zTXt");
      for (j = 0; j < this.zTXt_keyword.size(); j++)
      {
        localIIOMetadataNode5 = new IIOMetadataNode("zTXtEntry");
        localIIOMetadataNode5.setAttribute("keyword", (String)this.zTXt_keyword.get(j));
        int n = ((Integer)this.zTXt_compressionMethod.get(j)).intValue();
        localIIOMetadataNode5.setAttribute("compressionMethod", zTXt_compressionMethodNames[n]);
        localIIOMetadataNode5.setAttribute("text", (String)this.zTXt_text.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode5);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (this.unknownChunkType.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("UnknownChunks");
      for (j = 0; j < this.unknownChunkType.size(); j++)
      {
        localIIOMetadataNode5 = new IIOMetadataNode("UnknownChunk");
        localIIOMetadataNode5.setAttribute("type", (String)this.unknownChunkType.get(j));
        localIIOMetadataNode5.setUserObject((byte[])this.unknownChunkData.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode5);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    return localIIOMetadataNode2;
  }
  
  private int getNumChannels()
  {
    int i = IHDR_numChannels[this.IHDR_colorType];
    if ((this.IHDR_colorType == 3) && (this.tRNS_present) && (this.tRNS_colorType == this.IHDR_colorType)) {
      i = 4;
    }
    return i;
  }
  
  public IIOMetadataNode getStandardChromaNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    localIIOMetadataNode2.setAttribute("name", colorSpaceTypeNames[this.IHDR_colorType]);
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("NumChannels");
    localIIOMetadataNode2.setAttribute("value", Integer.toString(getNumChannels()));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (this.gAMA_present)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("Gamma");
      localIIOMetadataNode2.setAttribute("value", Float.toString(this.gAMA_gamma * 1.0E-5F));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    localIIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    int i;
    int j;
    if (this.PLTE_present)
    {
      i = (this.tRNS_present) && (this.tRNS_colorType == 3) ? 1 : 0;
      localIIOMetadataNode2 = new IIOMetadataNode("Palette");
      for (j = 0; j < this.PLTE_red.length; j++)
      {
        IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
        localIIOMetadataNode3.setAttribute("index", Integer.toString(j));
        localIIOMetadataNode3.setAttribute("red", Integer.toString(this.PLTE_red[j] & 0xFF));
        localIIOMetadataNode3.setAttribute("green", Integer.toString(this.PLTE_green[j] & 0xFF));
        localIIOMetadataNode3.setAttribute("blue", Integer.toString(this.PLTE_blue[j] & 0xFF));
        if (i != 0)
        {
          int m = j < this.tRNS_alpha.length ? this.tRNS_alpha[j] & 0xFF : 255;
          localIIOMetadataNode3.setAttribute("alpha", Integer.toString(m));
        }
        localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    if (this.bKGD_present)
    {
      if (this.bKGD_colorType == 3)
      {
        localIIOMetadataNode2 = new IIOMetadataNode("BackgroundIndex");
        localIIOMetadataNode2.setAttribute("value", Integer.toString(this.bKGD_index));
      }
      else
      {
        localIIOMetadataNode2 = new IIOMetadataNode("BackgroundColor");
        int k;
        if (this.bKGD_colorType == 0)
        {
          i = j = k = this.bKGD_gray;
        }
        else
        {
          i = this.bKGD_red;
          j = this.bKGD_green;
          k = this.bKGD_blue;
        }
        localIIOMetadataNode2.setAttribute("red", Integer.toString(i));
        localIIOMetadataNode2.setAttribute("green", Integer.toString(j));
        localIIOMetadataNode2.setAttribute("blue", Integer.toString(k));
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardCompressionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    localIIOMetadataNode2.setAttribute("value", "deflate");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("Lossless");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("NumProgressiveScans");
    localIIOMetadataNode2.setAttribute("value", this.IHDR_interlaceMethod == 0 ? "1" : "7");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  private String repeat(String paramString, int paramInt)
  {
    if (paramInt == 1) {
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer((paramString.length() + 1) * paramInt - 1);
    localStringBuffer.append(paramString);
    for (int i = 1; i < paramInt; i++)
    {
      localStringBuffer.append(" ");
      localStringBuffer.append(paramString);
    }
    return localStringBuffer.toString();
  }
  
  public IIOMetadataNode getStandardDataNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Data");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("PlanarConfiguration");
    localIIOMetadataNode2.setAttribute("value", "PixelInterleaved");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("SampleFormat");
    localIIOMetadataNode2.setAttribute("value", this.IHDR_colorType == 3 ? "Index" : "UnsignedIntegral");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    String str1 = Integer.toString(this.IHDR_bitDepth);
    localIIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    localIIOMetadataNode2.setAttribute("value", repeat(str1, getNumChannels()));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (this.sBIT_present)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("SignificantBitsPerSample");
      String str2;
      if ((this.sBIT_colorType == 0) || (this.sBIT_colorType == 4)) {
        str2 = Integer.toString(this.sBIT_grayBits);
      } else {
        str2 = Integer.toString(this.sBIT_redBits) + " " + Integer.toString(this.sBIT_greenBits) + " " + Integer.toString(this.sBIT_blueBits);
      }
      if ((this.sBIT_colorType == 4) || (this.sBIT_colorType == 6)) {
        str2 = str2 + " " + Integer.toString(this.sBIT_alphaBits);
      }
      localIIOMetadataNode2.setAttribute("value", str2);
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
    float f = this.pHYs_present ? this.pHYs_pixelsPerUnitXAxis / this.pHYs_pixelsPerUnitYAxis : 1.0F;
    localIIOMetadataNode2.setAttribute("value", Float.toString(f));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    localIIOMetadataNode2.setAttribute("value", "Normal");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if ((this.pHYs_present) && (this.pHYs_unitSpecifier == 1))
    {
      localIIOMetadataNode2 = new IIOMetadataNode("HorizontalPixelSize");
      localIIOMetadataNode2.setAttribute("value", Float.toString(1000.0F / this.pHYs_pixelsPerUnitXAxis));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      localIIOMetadataNode2 = new IIOMetadataNode("VerticalPixelSize");
      localIIOMetadataNode2.setAttribute("value", Float.toString(1000.0F / this.pHYs_pixelsPerUnitYAxis));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDocumentNode()
  {
    if (!this.tIME_present) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Document");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ImageModificationTime");
    localIIOMetadataNode2.setAttribute("year", Integer.toString(this.tIME_year));
    localIIOMetadataNode2.setAttribute("month", Integer.toString(this.tIME_month));
    localIIOMetadataNode2.setAttribute("day", Integer.toString(this.tIME_day));
    localIIOMetadataNode2.setAttribute("hour", Integer.toString(this.tIME_hour));
    localIIOMetadataNode2.setAttribute("minute", Integer.toString(this.tIME_minute));
    localIIOMetadataNode2.setAttribute("second", Integer.toString(this.tIME_second));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode()
  {
    int i = this.tEXt_keyword.size() + this.iTXt_keyword.size() + this.zTXt_keyword.size();
    if (i == 0) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Text");
    IIOMetadataNode localIIOMetadataNode2 = null;
    for (int j = 0; j < this.tEXt_keyword.size(); j++)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("keyword", (String)this.tEXt_keyword.get(j));
      localIIOMetadataNode2.setAttribute("value", (String)this.tEXt_text.get(j));
      localIIOMetadataNode2.setAttribute("encoding", "ISO-8859-1");
      localIIOMetadataNode2.setAttribute("compression", "none");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    for (j = 0; j < this.iTXt_keyword.size(); j++)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("keyword", (String)this.iTXt_keyword.get(j));
      localIIOMetadataNode2.setAttribute("value", (String)this.iTXt_text.get(j));
      localIIOMetadataNode2.setAttribute("language", (String)this.iTXt_languageTag.get(j));
      if (((Boolean)this.iTXt_compressionFlag.get(j)).booleanValue()) {
        localIIOMetadataNode2.setAttribute("compression", "zip");
      } else {
        localIIOMetadataNode2.setAttribute("compression", "none");
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    for (j = 0; j < this.zTXt_keyword.size(); j++)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("keyword", (String)this.zTXt_keyword.get(j));
      localIIOMetadataNode2.setAttribute("value", (String)this.zTXt_text.get(j));
      localIIOMetadataNode2.setAttribute("compression", "zip");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTransparencyNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Transparency");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("Alpha");
    int i = (this.IHDR_colorType == 6) || (this.IHDR_colorType == 4) || ((this.IHDR_colorType == 3) && (this.tRNS_present) && (this.tRNS_colorType == this.IHDR_colorType) && (this.tRNS_alpha != null)) ? 1 : 0;
    localIIOMetadataNode2.setAttribute("value", i != 0 ? "nonpremultipled" : "none");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (this.tRNS_present)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TransparentColor");
      if (this.tRNS_colorType == 2) {
        localIIOMetadataNode2.setAttribute("value", Integer.toString(this.tRNS_red) + " " + Integer.toString(this.tRNS_green) + " " + Integer.toString(this.tRNS_blue));
      } else if (this.tRNS_colorType == 0) {
        localIIOMetadataNode2.setAttribute("value", Integer.toString(this.tRNS_gray));
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  private void fatal(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    throw new IIOInvalidTreeException(paramString, paramNode);
  }
  
  private String getStringAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString1);
    if (localNode == null)
    {
      if (!paramBoolean) {
        return paramString2;
      }
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    }
    return localNode.getNodeValue();
  }
  
  private int getIntAttribute(Node paramNode, String paramString, int paramInt, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean);
    if (str == null) {
      return paramInt;
    }
    return Integer.parseInt(str);
  }
  
  private float getFloatAttribute(Node paramNode, String paramString, float paramFloat, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean);
    if (str == null) {
      return paramFloat;
    }
    return Float.parseFloat(str);
  }
  
  private int getIntAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getIntAttribute(paramNode, paramString, -1, true);
  }
  
  private float getFloatAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getFloatAttribute(paramNode, paramString, -1.0F, true);
  }
  
  private boolean getBooleanAttribute(Node paramNode, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString);
    if (localNode == null)
    {
      if (!paramBoolean2) {
        return paramBoolean1;
      }
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    }
    String str = localNode.getNodeValue();
    if ((str.equals("TRUE")) || (str.equals("true"))) {
      return true;
    }
    if ((str.equals("FALSE")) || (str.equals("false"))) {
      return false;
    }
    fatal(paramNode, "Attribute " + paramString + " must be 'TRUE' or 'FALSE'!");
    return false;
  }
  
  private boolean getBooleanAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getBooleanAttribute(paramNode, paramString, false, true);
  }
  
  private int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString);
    if (localNode == null)
    {
      if (!paramBoolean) {
        return paramInt;
      }
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    }
    String str = localNode.getNodeValue();
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (str.equals(paramArrayOfString[i])) {
        return i;
      }
    }
    fatal(paramNode, "Illegal value for attribute " + paramString + "!");
    return -1;
  }
  
  private int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString)
    throws IIOInvalidTreeException
  {
    return getEnumeratedAttribute(paramNode, paramString, paramArrayOfString, -1, true);
  }
  
  private String getAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString1);
    if (localNode == null)
    {
      if (!paramBoolean) {
        return paramString2;
      }
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    }
    return localNode.getNodeValue();
  }
  
  private String getAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getAttribute(paramNode, paramString, null, true);
  }
  
  public void mergeTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramString.equals("javax_imageio_png_1.0"))
    {
      if (paramNode == null) {
        throw new IllegalArgumentException("root == null!");
      }
      mergeNativeTree(paramNode);
    }
    else if (paramString.equals("javax_imageio_1.0"))
    {
      if (paramNode == null) {
        throw new IllegalArgumentException("root == null!");
      }
      mergeStandardTree(paramNode);
    }
    else
    {
      throw new IllegalArgumentException("Not a recognized format!");
    }
  }
  
  private void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode;
    if (!localNode.getNodeName().equals("javax_imageio_png_1.0")) {
      fatal(localNode, "Root must be javax_imageio_png_1.0");
    }
    for (localNode = localNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
    {
      String str1 = localNode.getNodeName();
      if (str1.equals("IHDR"))
      {
        this.IHDR_width = getIntAttribute(localNode, "width");
        this.IHDR_height = getIntAttribute(localNode, "height");
        this.IHDR_bitDepth = Integer.valueOf(IHDR_bitDepths[getEnumeratedAttribute(localNode, "bitDepth", IHDR_bitDepths)]).intValue();
        this.IHDR_colorType = getEnumeratedAttribute(localNode, "colorType", IHDR_colorTypeNames);
        this.IHDR_compressionMethod = getEnumeratedAttribute(localNode, "compressionMethod", IHDR_compressionMethodNames);
        this.IHDR_filterMethod = getEnumeratedAttribute(localNode, "filterMethod", IHDR_filterMethodNames);
        this.IHDR_interlaceMethod = getEnumeratedAttribute(localNode, "interlaceMethod", IHDR_interlaceMethodNames);
        this.IHDR_present = true;
      }
      else
      {
        Object localObject1;
        Object localObject2;
        Object localObject4;
        int k;
        Object localObject8;
        if (str1.equals("PLTE"))
        {
          localObject1 = new byte['Ā'];
          localObject2 = new byte['Ā'];
          localObject4 = new byte['Ā'];
          k = -1;
          localObject8 = localNode.getFirstChild();
          if (localObject8 == null) {
            fatal(localNode, "Palette has no entries!");
          }
          while (localObject8 != null)
          {
            if (!((Node)localObject8).getNodeName().equals("PLTEEntry")) {
              fatal(localNode, "Only a PLTEEntry may be a child of a PLTE!");
            }
            n = getIntAttribute((Node)localObject8, "index");
            if ((n < 0) || (n > 255)) {
              fatal(localNode, "Bad value for PLTEEntry attribute index!");
            }
            if (n > k) {
              k = n;
            }
            localObject1[n] = ((byte)getIntAttribute((Node)localObject8, "red"));
            localObject2[n] = ((byte)getIntAttribute((Node)localObject8, "green"));
            localObject4[n] = ((byte)getIntAttribute((Node)localObject8, "blue"));
            localObject8 = ((Node)localObject8).getNextSibling();
          }
          int n = k + 1;
          this.PLTE_red = new byte[n];
          this.PLTE_green = new byte[n];
          this.PLTE_blue = new byte[n];
          System.arraycopy(localObject1, 0, this.PLTE_red, 0, n);
          System.arraycopy(localObject2, 0, this.PLTE_green, 0, n);
          System.arraycopy(localObject4, 0, this.PLTE_blue, 0, n);
          this.PLTE_present = true;
        }
        else if (str1.equals("bKGD"))
        {
          this.bKGD_present = false;
          localObject1 = localNode.getFirstChild();
          if (localObject1 == null) {
            fatal(localNode, "bKGD node has no children!");
          }
          localObject2 = ((Node)localObject1).getNodeName();
          if (((String)localObject2).equals("bKGD_Palette"))
          {
            this.bKGD_index = getIntAttribute((Node)localObject1, "index");
            this.bKGD_colorType = 3;
          }
          else if (((String)localObject2).equals("bKGD_Grayscale"))
          {
            this.bKGD_gray = getIntAttribute((Node)localObject1, "gray");
            this.bKGD_colorType = 0;
          }
          else if (((String)localObject2).equals("bKGD_RGB"))
          {
            this.bKGD_red = getIntAttribute((Node)localObject1, "red");
            this.bKGD_green = getIntAttribute((Node)localObject1, "green");
            this.bKGD_blue = getIntAttribute((Node)localObject1, "blue");
            this.bKGD_colorType = 2;
          }
          else
          {
            fatal(localNode, "Bad child of a bKGD node!");
          }
          if (((Node)localObject1).getNextSibling() != null) {
            fatal(localNode, "bKGD node has more than one child!");
          }
          this.bKGD_present = true;
        }
        else if (str1.equals("cHRM"))
        {
          this.cHRM_whitePointX = getIntAttribute(localNode, "whitePointX");
          this.cHRM_whitePointY = getIntAttribute(localNode, "whitePointY");
          this.cHRM_redX = getIntAttribute(localNode, "redX");
          this.cHRM_redY = getIntAttribute(localNode, "redY");
          this.cHRM_greenX = getIntAttribute(localNode, "greenX");
          this.cHRM_greenY = getIntAttribute(localNode, "greenY");
          this.cHRM_blueX = getIntAttribute(localNode, "blueX");
          this.cHRM_blueY = getIntAttribute(localNode, "blueY");
          this.cHRM_present = true;
        }
        else if (str1.equals("gAMA"))
        {
          this.gAMA_gamma = getIntAttribute(localNode, "value");
          this.gAMA_present = true;
        }
        else if (str1.equals("hIST"))
        {
          localObject1 = new char['Ā'];
          int i = -1;
          localObject4 = localNode.getFirstChild();
          if (localObject4 == null) {
            fatal(localNode, "hIST node has no children!");
          }
          while (localObject4 != null)
          {
            if (!((Node)localObject4).getNodeName().equals("hISTEntry")) {
              fatal(localNode, "Only a hISTEntry may be a child of a hIST!");
            }
            k = getIntAttribute((Node)localObject4, "index");
            if ((k < 0) || (k > 255)) {
              fatal(localNode, "Bad value for histEntry attribute index!");
            }
            if (k > i) {
              i = k;
            }
            localObject1[k] = ((char)getIntAttribute((Node)localObject4, "value"));
            localObject4 = ((Node)localObject4).getNextSibling();
          }
          k = i + 1;
          this.hIST_histogram = new char[k];
          System.arraycopy(localObject1, 0, this.hIST_histogram, 0, k);
          this.hIST_present = true;
        }
        else if (str1.equals("iCCP"))
        {
          this.iCCP_profileName = getAttribute(localNode, "profileName");
          this.iCCP_compressionMethod = getEnumeratedAttribute(localNode, "compressionMethod", iCCP_compressionMethodNames);
          localObject1 = ((IIOMetadataNode)localNode).getUserObject();
          if (localObject1 == null) {
            fatal(localNode, "No ICCP profile present in user object!");
          }
          if (!(localObject1 instanceof byte[])) {
            fatal(localNode, "User object not a byte array!");
          }
          this.iCCP_compressedProfile = ((byte[])((byte[])localObject1).clone());
          this.iCCP_present = true;
        }
        else
        {
          Object localObject3;
          Object localObject7;
          Object localObject9;
          if (str1.equals("iTXt"))
          {
            for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
            {
              if (!((Node)localObject1).getNodeName().equals("iTXtEntry")) {
                fatal(localNode, "Only an iTXtEntry may be a child of an iTXt!");
              }
              localObject3 = getAttribute((Node)localObject1, "keyword");
              if (isValidKeyword((String)localObject3))
              {
                this.iTXt_keyword.add(localObject3);
                boolean bool = getBooleanAttribute((Node)localObject1, "compressionFlag");
                this.iTXt_compressionFlag.add(Boolean.valueOf(bool));
                localObject7 = getAttribute((Node)localObject1, "compressionMethod");
                this.iTXt_compressionMethod.add(Integer.valueOf((String)localObject7));
                localObject8 = getAttribute((Node)localObject1, "languageTag");
                this.iTXt_languageTag.add(localObject8);
                String str3 = getAttribute((Node)localObject1, "translatedKeyword");
                this.iTXt_translatedKeyword.add(str3);
                localObject9 = getAttribute((Node)localObject1, "text");
                this.iTXt_text.add(localObject9);
              }
            }
          }
          else if (str1.equals("pHYs"))
          {
            this.pHYs_pixelsPerUnitXAxis = getIntAttribute(localNode, "pixelsPerUnitXAxis");
            this.pHYs_pixelsPerUnitYAxis = getIntAttribute(localNode, "pixelsPerUnitYAxis");
            this.pHYs_unitSpecifier = getEnumeratedAttribute(localNode, "unitSpecifier", unitSpecifierNames);
            this.pHYs_present = true;
          }
          else if (str1.equals("sBIT"))
          {
            this.sBIT_present = false;
            localObject1 = localNode.getFirstChild();
            if (localObject1 == null) {
              fatal(localNode, "sBIT node has no children!");
            }
            localObject3 = ((Node)localObject1).getNodeName();
            if (((String)localObject3).equals("sBIT_Grayscale"))
            {
              this.sBIT_grayBits = getIntAttribute((Node)localObject1, "gray");
              this.sBIT_colorType = 0;
            }
            else if (((String)localObject3).equals("sBIT_GrayAlpha"))
            {
              this.sBIT_grayBits = getIntAttribute((Node)localObject1, "gray");
              this.sBIT_alphaBits = getIntAttribute((Node)localObject1, "alpha");
              this.sBIT_colorType = 4;
            }
            else if (((String)localObject3).equals("sBIT_RGB"))
            {
              this.sBIT_redBits = getIntAttribute((Node)localObject1, "red");
              this.sBIT_greenBits = getIntAttribute((Node)localObject1, "green");
              this.sBIT_blueBits = getIntAttribute((Node)localObject1, "blue");
              this.sBIT_colorType = 2;
            }
            else if (((String)localObject3).equals("sBIT_RGBAlpha"))
            {
              this.sBIT_redBits = getIntAttribute((Node)localObject1, "red");
              this.sBIT_greenBits = getIntAttribute((Node)localObject1, "green");
              this.sBIT_blueBits = getIntAttribute((Node)localObject1, "blue");
              this.sBIT_alphaBits = getIntAttribute((Node)localObject1, "alpha");
              this.sBIT_colorType = 6;
            }
            else if (((String)localObject3).equals("sBIT_Palette"))
            {
              this.sBIT_redBits = getIntAttribute((Node)localObject1, "red");
              this.sBIT_greenBits = getIntAttribute((Node)localObject1, "green");
              this.sBIT_blueBits = getIntAttribute((Node)localObject1, "blue");
              this.sBIT_colorType = 3;
            }
            else
            {
              fatal(localNode, "Bad child of an sBIT node!");
            }
            if (((Node)localObject1).getNextSibling() != null) {
              fatal(localNode, "sBIT node has more than one child!");
            }
            this.sBIT_present = true;
          }
          else
          {
            Object localObject5;
            int i1;
            if (str1.equals("sPLT"))
            {
              this.sPLT_paletteName = getAttribute(localNode, "name");
              this.sPLT_sampleDepth = getIntAttribute(localNode, "sampleDepth");
              localObject1 = new int['Ā'];
              localObject3 = new int['Ā'];
              localObject5 = new int['Ā'];
              localObject7 = new int['Ā'];
              localObject8 = new int['Ā'];
              i1 = -1;
              localObject9 = localNode.getFirstChild();
              if (localObject9 == null) {
                fatal(localNode, "sPLT node has no children!");
              }
              while (localObject9 != null)
              {
                if (!((Node)localObject9).getNodeName().equals("sPLTEntry")) {
                  fatal(localNode, "Only an sPLTEntry may be a child of an sPLT!");
                }
                i2 = getIntAttribute((Node)localObject9, "index");
                if ((i2 < 0) || (i2 > 255)) {
                  fatal(localNode, "Bad value for PLTEEntry attribute index!");
                }
                if (i2 > i1) {
                  i1 = i2;
                }
                localObject1[i2] = getIntAttribute((Node)localObject9, "red");
                localObject3[i2] = getIntAttribute((Node)localObject9, "green");
                localObject5[i2] = getIntAttribute((Node)localObject9, "blue");
                localObject7[i2] = getIntAttribute((Node)localObject9, "alpha");
                localObject8[i2] = getIntAttribute((Node)localObject9, "frequency");
                localObject9 = ((Node)localObject9).getNextSibling();
              }
              int i2 = i1 + 1;
              this.sPLT_red = new int[i2];
              this.sPLT_green = new int[i2];
              this.sPLT_blue = new int[i2];
              this.sPLT_alpha = new int[i2];
              this.sPLT_frequency = new int[i2];
              System.arraycopy(localObject1, 0, this.sPLT_red, 0, i2);
              System.arraycopy(localObject3, 0, this.sPLT_green, 0, i2);
              System.arraycopy(localObject5, 0, this.sPLT_blue, 0, i2);
              System.arraycopy(localObject7, 0, this.sPLT_alpha, 0, i2);
              System.arraycopy(localObject8, 0, this.sPLT_frequency, 0, i2);
              this.sPLT_present = true;
            }
            else if (str1.equals("sRGB"))
            {
              this.sRGB_renderingIntent = getEnumeratedAttribute(localNode, "renderingIntent", renderingIntentNames);
              this.sRGB_present = true;
            }
            else if (str1.equals("tEXt"))
            {
              for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
              {
                if (!((Node)localObject1).getNodeName().equals("tEXtEntry")) {
                  fatal(localNode, "Only an tEXtEntry may be a child of an tEXt!");
                }
                localObject3 = getAttribute((Node)localObject1, "keyword");
                this.tEXt_keyword.add(localObject3);
                localObject5 = getAttribute((Node)localObject1, "value");
                this.tEXt_text.add(localObject5);
              }
            }
            else if (str1.equals("tIME"))
            {
              this.tIME_year = getIntAttribute(localNode, "year");
              this.tIME_month = getIntAttribute(localNode, "month");
              this.tIME_day = getIntAttribute(localNode, "day");
              this.tIME_hour = getIntAttribute(localNode, "hour");
              this.tIME_minute = getIntAttribute(localNode, "minute");
              this.tIME_second = getIntAttribute(localNode, "second");
              this.tIME_present = true;
            }
            else if (str1.equals("tRNS"))
            {
              this.tRNS_present = false;
              localObject1 = localNode.getFirstChild();
              if (localObject1 == null) {
                fatal(localNode, "tRNS node has no children!");
              }
              localObject3 = ((Node)localObject1).getNodeName();
              if (((String)localObject3).equals("tRNS_Palette"))
              {
                localObject5 = new byte['Ā'];
                int m = -1;
                localObject8 = ((Node)localObject1).getFirstChild();
                if (localObject8 == null) {
                  fatal(localNode, "tRNS_Palette node has no children!");
                }
                while (localObject8 != null)
                {
                  if (!((Node)localObject8).getNodeName().equals("tRNS_PaletteEntry")) {
                    fatal(localNode, "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!");
                  }
                  i1 = getIntAttribute((Node)localObject8, "index");
                  if ((i1 < 0) || (i1 > 255)) {
                    fatal(localNode, "Bad value for tRNS_PaletteEntry attribute index!");
                  }
                  if (i1 > m) {
                    m = i1;
                  }
                  localObject5[i1] = ((byte)getIntAttribute((Node)localObject8, "alpha"));
                  localObject8 = ((Node)localObject8).getNextSibling();
                }
                i1 = m + 1;
                this.tRNS_alpha = new byte[i1];
                this.tRNS_colorType = 3;
                System.arraycopy(localObject5, 0, this.tRNS_alpha, 0, i1);
              }
              else if (((String)localObject3).equals("tRNS_Grayscale"))
              {
                this.tRNS_gray = getIntAttribute((Node)localObject1, "gray");
                this.tRNS_colorType = 0;
              }
              else if (((String)localObject3).equals("tRNS_RGB"))
              {
                this.tRNS_red = getIntAttribute((Node)localObject1, "red");
                this.tRNS_green = getIntAttribute((Node)localObject1, "green");
                this.tRNS_blue = getIntAttribute((Node)localObject1, "blue");
                this.tRNS_colorType = 2;
              }
              else
              {
                fatal(localNode, "Bad child of a tRNS node!");
              }
              if (((Node)localObject1).getNextSibling() != null) {
                fatal(localNode, "tRNS node has more than one child!");
              }
              this.tRNS_present = true;
            }
            else if (str1.equals("zTXt"))
            {
              for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
              {
                if (!((Node)localObject1).getNodeName().equals("zTXtEntry")) {
                  fatal(localNode, "Only an zTXtEntry may be a child of an zTXt!");
                }
                localObject3 = getAttribute((Node)localObject1, "keyword");
                this.zTXt_keyword.add(localObject3);
                int j = getEnumeratedAttribute((Node)localObject1, "compressionMethod", zTXt_compressionMethodNames);
                this.zTXt_compressionMethod.add(new Integer(j));
                String str2 = getAttribute((Node)localObject1, "text");
                this.zTXt_text.add(str2);
              }
            }
            else if (str1.equals("UnknownChunks"))
            {
              for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
              {
                if (!((Node)localObject1).getNodeName().equals("UnknownChunk")) {
                  fatal(localNode, "Only an UnknownChunk may be a child of an UnknownChunks!");
                }
                localObject3 = getAttribute((Node)localObject1, "type");
                Object localObject6 = ((IIOMetadataNode)localObject1).getUserObject();
                if (((String)localObject3).length() != 4) {
                  fatal((Node)localObject1, "Chunk type must be 4 characters!");
                }
                if (localObject6 == null) {
                  fatal((Node)localObject1, "No chunk data present in user object!");
                }
                if (!(localObject6 instanceof byte[])) {
                  fatal((Node)localObject1, "User object not a byte array!");
                }
                this.unknownChunkType.add(localObject3);
                this.unknownChunkData.add(((byte[])localObject6).clone());
              }
            }
            else
            {
              fatal(localNode, "Unknown child of root node!");
            }
          }
        }
      }
    }
  }
  
  private boolean isValidKeyword(String paramString)
  {
    int i = paramString.length();
    if ((i < 1) || (i >= 80)) {
      return false;
    }
    if ((paramString.startsWith(" ")) || (paramString.endsWith(" ")) || (paramString.contains("  "))) {
      return false;
    }
    return isISOLatin(paramString, false);
  }
  
  private boolean isISOLatin(String paramString, boolean paramBoolean)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if (((k < 32) || (k > 255) || ((k > 126) && (k < 161))) && ((!paramBoolean) || (k != 16))) {
        return false;
      }
    }
    return true;
  }
  
  private void mergeStandardTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    Node localNode1 = paramNode;
    if (!localNode1.getNodeName().equals("javax_imageio_1.0")) {
      fatal(localNode1, "Root must be javax_imageio_1.0");
    }
    for (localNode1 = localNode1.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling())
    {
      String str1 = localNode1.getNodeName();
      Node localNode2;
      String str2;
      int i2;
      Node localNode4;
      int k;
      int i1;
      if (str1.equals("Chroma"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("Gamma"))
          {
            float f1 = getFloatAttribute(localNode2, "value");
            this.gAMA_present = true;
            this.gAMA_gamma = ((int)(f1 * 100000.0F + 0.5D));
          }
          else if (str2.equals("Palette"))
          {
            byte[] arrayOfByte1 = new byte['Ā'];
            byte[] arrayOfByte2 = new byte['Ā'];
            byte[] arrayOfByte3 = new byte['Ā'];
            i2 = -1;
            for (localNode4 = localNode2.getFirstChild(); localNode4 != null; localNode4 = localNode4.getNextSibling())
            {
              i3 = getIntAttribute(localNode4, "index");
              if ((i3 >= 0) && (i3 <= 255))
              {
                arrayOfByte1[i3] = ((byte)getIntAttribute(localNode4, "red"));
                arrayOfByte2[i3] = ((byte)getIntAttribute(localNode4, "green"));
                arrayOfByte3[i3] = ((byte)getIntAttribute(localNode4, "blue"));
                if (i3 > i2) {
                  i2 = i3;
                }
              }
            }
            int i3 = i2 + 1;
            this.PLTE_red = new byte[i3];
            this.PLTE_green = new byte[i3];
            this.PLTE_blue = new byte[i3];
            System.arraycopy(arrayOfByte1, 0, this.PLTE_red, 0, i3);
            System.arraycopy(arrayOfByte2, 0, this.PLTE_green, 0, i3);
            System.arraycopy(arrayOfByte3, 0, this.PLTE_blue, 0, i3);
            this.PLTE_present = true;
          }
          else if (str2.equals("BackgroundIndex"))
          {
            this.bKGD_present = true;
            this.bKGD_colorType = 3;
            this.bKGD_index = getIntAttribute(localNode2, "value");
          }
          else if (str2.equals("BackgroundColor"))
          {
            k = getIntAttribute(localNode2, "red");
            int n = getIntAttribute(localNode2, "green");
            i1 = getIntAttribute(localNode2, "blue");
            if ((k == n) && (k == i1))
            {
              this.bKGD_colorType = 0;
              this.bKGD_gray = k;
            }
            else
            {
              this.bKGD_red = k;
              this.bKGD_green = n;
              this.bKGD_blue = i1;
            }
            this.bKGD_present = true;
          }
        }
      }
      else if (str1.equals("Compression"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("NumProgressiveScans"))
          {
            k = getIntAttribute(localNode2, "value");
            this.IHDR_interlaceMethod = (k > 1 ? 1 : 0);
          }
        }
      }
      else if (str1.equals("Data"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          String str4;
          StringTokenizer localStringTokenizer;
          if (str2.equals("BitsPerSample"))
          {
            str4 = getAttribute(localNode2, "value");
            localStringTokenizer = new StringTokenizer(str4);
            i1 = -1;
            while (localStringTokenizer.hasMoreTokens())
            {
              i2 = Integer.parseInt(localStringTokenizer.nextToken());
              if (i2 > i1) {
                i1 = i2;
              }
            }
            if (i1 < 1) {
              i1 = 1;
            }
            if (i1 == 3) {
              i1 = 4;
            }
            if ((i1 > 4) || (i1 < 8)) {
              i1 = 8;
            }
            if (i1 > 8) {
              i1 = 16;
            }
            this.IHDR_bitDepth = i1;
          }
          else if (str2.equals("SignificantBitsPerSample"))
          {
            str4 = getAttribute(localNode2, "value");
            localStringTokenizer = new StringTokenizer(str4);
            i1 = localStringTokenizer.countTokens();
            if (i1 == 1)
            {
              this.sBIT_colorType = 0;
              this.sBIT_grayBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            else if (i1 == 2)
            {
              this.sBIT_colorType = 4;
              this.sBIT_grayBits = Integer.parseInt(localStringTokenizer.nextToken());
              this.sBIT_alphaBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            else if (i1 == 3)
            {
              this.sBIT_colorType = 2;
              this.sBIT_redBits = Integer.parseInt(localStringTokenizer.nextToken());
              this.sBIT_greenBits = Integer.parseInt(localStringTokenizer.nextToken());
              this.sBIT_blueBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            else if (i1 == 4)
            {
              this.sBIT_colorType = 6;
              this.sBIT_redBits = Integer.parseInt(localStringTokenizer.nextToken());
              this.sBIT_greenBits = Integer.parseInt(localStringTokenizer.nextToken());
              this.sBIT_blueBits = Integer.parseInt(localStringTokenizer.nextToken());
              this.sBIT_alphaBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            if ((i1 >= 1) && (i1 <= 4)) {
              this.sBIT_present = true;
            }
          }
        }
      }
      else if (str1.equals("Dimension"))
      {
        int i = 0;
        int j = 0;
        int m = 0;
        float f2 = -1.0F;
        float f3 = -1.0F;
        float f4 = -1.0F;
        for (localNode4 = localNode1.getFirstChild(); localNode4 != null; localNode4 = localNode4.getNextSibling())
        {
          String str9 = localNode4.getNodeName();
          if (str9.equals("PixelAspectRatio"))
          {
            f4 = getFloatAttribute(localNode4, "value");
            m = 1;
          }
          else if (str9.equals("HorizontalPixelSize"))
          {
            f2 = getFloatAttribute(localNode4, "value");
            i = 1;
          }
          else if (str9.equals("VerticalPixelSize"))
          {
            f3 = getFloatAttribute(localNode4, "value");
            j = 1;
          }
        }
        if ((i != 0) && (j != 0))
        {
          this.pHYs_present = true;
          this.pHYs_unitSpecifier = 1;
          this.pHYs_pixelsPerUnitXAxis = ((int)(f2 * 1000.0F + 0.5F));
          this.pHYs_pixelsPerUnitYAxis = ((int)(f3 * 1000.0F + 0.5F));
        }
        else if (m != 0)
        {
          this.pHYs_present = true;
          this.pHYs_unitSpecifier = 0;
          for (int i4 = 1; i4 < 100; i4++)
          {
            int i5 = (int)(f4 * i4);
            if (Math.abs(i5 / i4 - f4) < 0.001D) {
              break;
            }
          }
          this.pHYs_pixelsPerUnitXAxis = ((int)(f4 * i4));
          this.pHYs_pixelsPerUnitYAxis = i4;
        }
      }
      else
      {
        Node localNode3;
        String str3;
        if (str1.equals("Document")) {
          for (localNode3 = localNode1.getFirstChild(); localNode3 != null; localNode3 = localNode3.getNextSibling())
          {
            str3 = localNode3.getNodeName();
            if (str3.equals("ImageModificationTime"))
            {
              this.tIME_present = true;
              this.tIME_year = getIntAttribute(localNode3, "year");
              this.tIME_month = getIntAttribute(localNode3, "month");
              this.tIME_day = getIntAttribute(localNode3, "day");
              this.tIME_hour = getIntAttribute(localNode3, "hour", 0, false);
              this.tIME_minute = getIntAttribute(localNode3, "minute", 0, false);
              this.tIME_second = getIntAttribute(localNode3, "second", 0, false);
            }
          }
        } else if (str1.equals("Text")) {
          for (localNode3 = localNode1.getFirstChild(); localNode3 != null; localNode3 = localNode3.getNextSibling())
          {
            str3 = localNode3.getNodeName();
            if (str3.equals("TextEntry"))
            {
              String str5 = getAttribute(localNode3, "keyword", "", false);
              String str6 = getAttribute(localNode3, "value");
              String str7 = getAttribute(localNode3, "language", "", false);
              String str8 = getAttribute(localNode3, "compression", "none", false);
              if (isValidKeyword(str5)) {
                if (isISOLatin(str6, true))
                {
                  if (str8.equals("zip"))
                  {
                    this.zTXt_keyword.add(str5);
                    this.zTXt_text.add(str6);
                    this.zTXt_compressionMethod.add(Integer.valueOf(0));
                  }
                  else
                  {
                    this.tEXt_keyword.add(str5);
                    this.tEXt_text.add(str6);
                  }
                }
                else
                {
                  this.iTXt_keyword.add(str5);
                  this.iTXt_compressionFlag.add(Boolean.valueOf(str8.equals("zip")));
                  this.iTXt_compressionMethod.add(Integer.valueOf(0));
                  this.iTXt_languageTag.add(str7);
                  this.iTXt_translatedKeyword.add(str5);
                  this.iTXt_text.add(str6);
                }
              }
            }
          }
        }
      }
    }
  }
  
  public void reset()
  {
    this.IHDR_present = false;
    this.PLTE_present = false;
    this.bKGD_present = false;
    this.cHRM_present = false;
    this.gAMA_present = false;
    this.hIST_present = false;
    this.iCCP_present = false;
    this.iTXt_keyword = new ArrayList();
    this.iTXt_compressionFlag = new ArrayList();
    this.iTXt_compressionMethod = new ArrayList();
    this.iTXt_languageTag = new ArrayList();
    this.iTXt_translatedKeyword = new ArrayList();
    this.iTXt_text = new ArrayList();
    this.pHYs_present = false;
    this.sBIT_present = false;
    this.sPLT_present = false;
    this.sRGB_present = false;
    this.tEXt_keyword = new ArrayList();
    this.tEXt_text = new ArrayList();
    this.tIME_present = false;
    this.tRNS_present = false;
    this.zTXt_keyword = new ArrayList();
    this.zTXt_compressionMethod = new ArrayList();
    this.zTXt_text = new ArrayList();
    this.unknownChunkType = new ArrayList();
    this.unknownChunkData = new ArrayList();
  }
}
