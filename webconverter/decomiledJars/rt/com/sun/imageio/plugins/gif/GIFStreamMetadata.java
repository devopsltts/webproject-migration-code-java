package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFStreamMetadata
  extends GIFMetadata
{
  static final String nativeMetadataFormatName = "javax_imageio_gif_stream_1.0";
  static final String[] versionStrings = { "87a", "89a" };
  public String version;
  public int logicalScreenWidth;
  public int logicalScreenHeight;
  public int colorResolution;
  public int pixelAspectRatio;
  public int backgroundColorIndex;
  public boolean sortFlag;
  static final String[] colorTableSizes = { "2", "4", "8", "16", "32", "64", "128", "256" };
  public byte[] globalColorTable = null;
  
  protected GIFStreamMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    super(paramBoolean, paramString1, paramString2, paramArrayOfString1, paramArrayOfString2);
  }
  
  public GIFStreamMetadata()
  {
    this(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null);
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString.equals("javax_imageio_gif_stream_1.0")) {
      return getNativeTree();
    }
    if (paramString.equals("javax_imageio_1.0")) {
      return getStandardTree();
    }
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private Node getNativeTree()
  {
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("javax_imageio_gif_stream_1.0");
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Version");
    localIIOMetadataNode1.setAttribute("value", this.version);
    localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    localIIOMetadataNode1 = new IIOMetadataNode("LogicalScreenDescriptor");
    localIIOMetadataNode1.setAttribute("logicalScreenWidth", this.logicalScreenWidth == -1 ? "" : Integer.toString(this.logicalScreenWidth));
    localIIOMetadataNode1.setAttribute("logicalScreenHeight", this.logicalScreenHeight == -1 ? "" : Integer.toString(this.logicalScreenHeight));
    localIIOMetadataNode1.setAttribute("colorResolution", this.colorResolution == -1 ? "" : Integer.toString(this.colorResolution));
    localIIOMetadataNode1.setAttribute("pixelAspectRatio", Integer.toString(this.pixelAspectRatio));
    localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    if (this.globalColorTable != null)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("GlobalColorTable");
      int i = this.globalColorTable.length / 3;
      localIIOMetadataNode1.setAttribute("sizeOfGlobalColorTable", Integer.toString(i));
      localIIOMetadataNode1.setAttribute("backgroundColorIndex", Integer.toString(this.backgroundColorIndex));
      localIIOMetadataNode1.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
      for (int j = 0; j < i; j++)
      {
        IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("ColorTableEntry");
        localIIOMetadataNode3.setAttribute("index", Integer.toString(j));
        int k = this.globalColorTable[(3 * j)] & 0xFF;
        int m = this.globalColorTable[(3 * j + 1)] & 0xFF;
        int n = this.globalColorTable[(3 * j + 2)] & 0xFF;
        localIIOMetadataNode3.setAttribute("red", Integer.toString(k));
        localIIOMetadataNode3.setAttribute("green", Integer.toString(m));
        localIIOMetadataNode3.setAttribute("blue", Integer.toString(n));
        localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    }
    return localIIOMetadataNode2;
  }
  
  public IIOMetadataNode getStandardChromaNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    localIIOMetadataNode2.setAttribute("name", "RGB");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (this.globalColorTable != null)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("Palette");
      int i = this.globalColorTable.length / 3;
      for (int j = 0; j < i; j++)
      {
        IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
        localIIOMetadataNode3.setAttribute("index", Integer.toString(j));
        localIIOMetadataNode3.setAttribute("red", Integer.toString(this.globalColorTable[(3 * j)] & 0xFF));
        localIIOMetadataNode3.setAttribute("green", Integer.toString(this.globalColorTable[(3 * j + 1)] & 0xFF));
        localIIOMetadataNode3.setAttribute("blue", Integer.toString(this.globalColorTable[(3 * j + 2)] & 0xFF));
        localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      localIIOMetadataNode2 = new IIOMetadataNode("BackgroundIndex");
      localIIOMetadataNode2.setAttribute("value", Integer.toString(this.backgroundColorIndex));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardCompressionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    localIIOMetadataNode2.setAttribute("value", "lzw");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("Lossless");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDataNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Data");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("SampleFormat");
    localIIOMetadataNode2.setAttribute("value", "Index");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    localIIOMetadataNode2.setAttribute("value", this.colorResolution == -1 ? "" : Integer.toString(this.colorResolution));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
    float f = 1.0F;
    if (this.pixelAspectRatio != 0) {
      f = (this.pixelAspectRatio + 15) / 64.0F;
    }
    localIIOMetadataNode2.setAttribute("value", Float.toString(f));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    localIIOMetadataNode2.setAttribute("value", "Normal");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("HorizontalScreenSize");
    localIIOMetadataNode2.setAttribute("value", this.logicalScreenWidth == -1 ? "" : Integer.toString(this.logicalScreenWidth));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("VerticalScreenSize");
    localIIOMetadataNode2.setAttribute("value", this.logicalScreenHeight == -1 ? "" : Integer.toString(this.logicalScreenHeight));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDocumentNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Document");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("FormatVersion");
    localIIOMetadataNode2.setAttribute("value", this.version);
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode()
  {
    return null;
  }
  
  public IIOMetadataNode getStandardTransparencyNode()
  {
    return null;
  }
  
  public void setFromTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    throw new IllegalStateException("Metadata is read-only!");
  }
  
  protected void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    throw new IllegalStateException("Metadata is read-only!");
  }
  
  protected void mergeStandardTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    throw new IllegalStateException("Metadata is read-only!");
  }
  
  public void reset()
  {
    throw new IllegalStateException("Metadata is read-only!");
  }
}
