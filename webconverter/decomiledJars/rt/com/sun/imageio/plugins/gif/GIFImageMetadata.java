package com.sun.imageio.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFImageMetadata
  extends GIFMetadata
{
  static final String nativeMetadataFormatName = "javax_imageio_gif_image_1.0";
  static final String[] disposalMethodNames = { "none", "doNotDispose", "restoreToBackgroundColor", "restoreToPrevious", "undefinedDisposalMethod4", "undefinedDisposalMethod5", "undefinedDisposalMethod6", "undefinedDisposalMethod7" };
  public int imageLeftPosition;
  public int imageTopPosition;
  public int imageWidth;
  public int imageHeight;
  public boolean interlaceFlag = false;
  public boolean sortFlag = false;
  public byte[] localColorTable = null;
  public int disposalMethod = 0;
  public boolean userInputFlag = false;
  public boolean transparentColorFlag = false;
  public int delayTime = 0;
  public int transparentColorIndex = 0;
  public boolean hasPlainTextExtension = false;
  public int textGridLeft;
  public int textGridTop;
  public int textGridWidth;
  public int textGridHeight;
  public int characterCellWidth;
  public int characterCellHeight;
  public int textForegroundColor;
  public int textBackgroundColor;
  public byte[] text;
  public List applicationIDs = null;
  public List authenticationCodes = null;
  public List applicationData = null;
  public List comments = null;
  
  protected GIFImageMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    super(paramBoolean, paramString1, paramString2, paramArrayOfString1, paramArrayOfString2);
  }
  
  public GIFImageMetadata()
  {
    this(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString.equals("javax_imageio_gif_image_1.0")) {
      return getNativeTree();
    }
    if (paramString.equals("javax_imageio_1.0")) {
      return getStandardTree();
    }
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private String toISO8859(byte[] paramArrayOfByte)
  {
    try
    {
      return new String(paramArrayOfByte, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return "";
  }
  
  private Node getNativeTree()
  {
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("javax_imageio_gif_image_1.0");
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("ImageDescriptor");
    localIIOMetadataNode1.setAttribute("imageLeftPosition", Integer.toString(this.imageLeftPosition));
    localIIOMetadataNode1.setAttribute("imageTopPosition", Integer.toString(this.imageTopPosition));
    localIIOMetadataNode1.setAttribute("imageWidth", Integer.toString(this.imageWidth));
    localIIOMetadataNode1.setAttribute("imageHeight", Integer.toString(this.imageHeight));
    localIIOMetadataNode1.setAttribute("interlaceFlag", this.interlaceFlag ? "TRUE" : "FALSE");
    localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    IIOMetadataNode localIIOMetadataNode3;
    if (this.localColorTable != null)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("LocalColorTable");
      i = this.localColorTable.length / 3;
      localIIOMetadataNode1.setAttribute("sizeOfLocalColorTable", Integer.toString(i));
      localIIOMetadataNode1.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
      for (j = 0; j < i; j++)
      {
        localIIOMetadataNode3 = new IIOMetadataNode("ColorTableEntry");
        localIIOMetadataNode3.setAttribute("index", Integer.toString(j));
        int m = this.localColorTable[(3 * j)] & 0xFF;
        int n = this.localColorTable[(3 * j + 1)] & 0xFF;
        int i1 = this.localColorTable[(3 * j + 2)] & 0xFF;
        localIIOMetadataNode3.setAttribute("red", Integer.toString(m));
        localIIOMetadataNode3.setAttribute("green", Integer.toString(n));
        localIIOMetadataNode3.setAttribute("blue", Integer.toString(i1));
        localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    }
    localIIOMetadataNode1 = new IIOMetadataNode("GraphicControlExtension");
    localIIOMetadataNode1.setAttribute("disposalMethod", disposalMethodNames[this.disposalMethod]);
    localIIOMetadataNode1.setAttribute("userInputFlag", this.userInputFlag ? "TRUE" : "FALSE");
    localIIOMetadataNode1.setAttribute("transparentColorFlag", this.transparentColorFlag ? "TRUE" : "FALSE");
    localIIOMetadataNode1.setAttribute("delayTime", Integer.toString(this.delayTime));
    localIIOMetadataNode1.setAttribute("transparentColorIndex", Integer.toString(this.transparentColorIndex));
    localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    if (this.hasPlainTextExtension)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("PlainTextExtension");
      localIIOMetadataNode1.setAttribute("textGridLeft", Integer.toString(this.textGridLeft));
      localIIOMetadataNode1.setAttribute("textGridTop", Integer.toString(this.textGridTop));
      localIIOMetadataNode1.setAttribute("textGridWidth", Integer.toString(this.textGridWidth));
      localIIOMetadataNode1.setAttribute("textGridHeight", Integer.toString(this.textGridHeight));
      localIIOMetadataNode1.setAttribute("characterCellWidth", Integer.toString(this.characterCellWidth));
      localIIOMetadataNode1.setAttribute("characterCellHeight", Integer.toString(this.characterCellHeight));
      localIIOMetadataNode1.setAttribute("textForegroundColor", Integer.toString(this.textForegroundColor));
      localIIOMetadataNode1.setAttribute("textBackgroundColor", Integer.toString(this.textBackgroundColor));
      localIIOMetadataNode1.setAttribute("text", toISO8859(this.text));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    }
    int i = this.applicationIDs == null ? 0 : this.applicationIDs.size();
    Object localObject;
    byte[] arrayOfByte1;
    if (i > 0)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("ApplicationExtensions");
      for (j = 0; j < i; j++)
      {
        localIIOMetadataNode3 = new IIOMetadataNode("ApplicationExtension");
        localObject = (byte[])this.applicationIDs.get(j);
        localIIOMetadataNode3.setAttribute("applicationID", toISO8859((byte[])localObject));
        arrayOfByte1 = (byte[])this.authenticationCodes.get(j);
        localIIOMetadataNode3.setAttribute("authenticationCode", toISO8859(arrayOfByte1));
        byte[] arrayOfByte2 = (byte[])this.applicationData.get(j);
        localIIOMetadataNode3.setUserObject((byte[])arrayOfByte2.clone());
        localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode1);
    }
    int j = this.comments == null ? 0 : this.comments.size();
    if (j > 0)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("CommentExtensions");
      for (int k = 0; k < j; k++)
      {
        localObject = new IIOMetadataNode("CommentExtension");
        arrayOfByte1 = (byte[])this.comments.get(k);
        ((IIOMetadataNode)localObject).setAttribute("value", toISO8859(arrayOfByte1));
        localIIOMetadataNode1.appendChild((Node)localObject);
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
    localIIOMetadataNode2 = new IIOMetadataNode("NumChannels");
    localIIOMetadataNode2.setAttribute("value", this.transparentColorFlag ? "4" : "3");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (this.localColorTable != null)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("Palette");
      int i = this.localColorTable.length / 3;
      for (int j = 0; j < i; j++)
      {
        IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
        localIIOMetadataNode3.setAttribute("index", Integer.toString(j));
        localIIOMetadataNode3.setAttribute("red", Integer.toString(this.localColorTable[(3 * j)] & 0xFF));
        localIIOMetadataNode3.setAttribute("green", Integer.toString(this.localColorTable[(3 * j + 1)] & 0xFF));
        localIIOMetadataNode3.setAttribute("blue", Integer.toString(this.localColorTable[(3 * j + 2)] & 0xFF));
        localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
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
    localIIOMetadataNode2.setAttribute("value", "lzw");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("Lossless");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("NumProgressiveScans");
    localIIOMetadataNode2.setAttribute("value", this.interlaceFlag ? "4" : "1");
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
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    localIIOMetadataNode2.setAttribute("value", "Normal");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("HorizontalPixelOffset");
    localIIOMetadataNode2.setAttribute("value", Integer.toString(this.imageLeftPosition));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("VerticalPixelOffset");
    localIIOMetadataNode2.setAttribute("value", Integer.toString(this.imageTopPosition));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode()
  {
    if (this.comments == null) {
      return null;
    }
    Iterator localIterator = this.comments.iterator();
    if (!localIterator.hasNext()) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Text");
    IIOMetadataNode localIIOMetadataNode2 = null;
    while (localIterator.hasNext())
    {
      byte[] arrayOfByte = (byte[])localIterator.next();
      String str = null;
      try
      {
        str = new String(arrayOfByte, "ISO-8859-1");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new RuntimeException("Encoding ISO-8859-1 unknown!");
      }
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("value", str);
      localIIOMetadataNode2.setAttribute("encoding", "ISO-8859-1");
      localIIOMetadataNode2.setAttribute("compression", "none");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTransparencyNode()
  {
    if (!this.transparentColorFlag) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Transparency");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("TransparentIndex");
    localIIOMetadataNode2.setAttribute("value", Integer.toString(this.transparentColorIndex));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
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
