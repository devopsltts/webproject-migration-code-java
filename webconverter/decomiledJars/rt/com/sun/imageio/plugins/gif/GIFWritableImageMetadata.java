package com.sun.imageio.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

class GIFWritableImageMetadata
  extends GIFImageMetadata
{
  static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_image_1.0";
  
  GIFWritableImageMetadata()
  {
    super(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public void reset()
  {
    this.imageLeftPosition = 0;
    this.imageTopPosition = 0;
    this.imageWidth = 0;
    this.imageHeight = 0;
    this.interlaceFlag = false;
    this.sortFlag = false;
    this.localColorTable = null;
    this.disposalMethod = 0;
    this.userInputFlag = false;
    this.transparentColorFlag = false;
    this.delayTime = 0;
    this.transparentColorIndex = 0;
    this.hasPlainTextExtension = false;
    this.textGridLeft = 0;
    this.textGridTop = 0;
    this.textGridWidth = 0;
    this.textGridHeight = 0;
    this.characterCellWidth = 0;
    this.characterCellHeight = 0;
    this.textForegroundColor = 0;
    this.textBackgroundColor = 0;
    this.text = null;
    this.applicationIDs = null;
    this.authenticationCodes = null;
    this.applicationData = null;
    this.comments = null;
  }
  
  private byte[] fromISO8859(String paramString)
  {
    try
    {
      return paramString.getBytes("ISO-8859-1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return "".getBytes();
  }
  
  protected void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode;
    if (!localNode.getNodeName().equals("javax_imageio_gif_image_1.0")) {
      fatal(localNode, "Root must be javax_imageio_gif_image_1.0");
    }
    for (localNode = localNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
    {
      String str1 = localNode.getNodeName();
      if (str1.equals("ImageDescriptor"))
      {
        this.imageLeftPosition = getIntAttribute(localNode, "imageLeftPosition", -1, true, true, 0, 65535);
        this.imageTopPosition = getIntAttribute(localNode, "imageTopPosition", -1, true, true, 0, 65535);
        this.imageWidth = getIntAttribute(localNode, "imageWidth", -1, true, true, 1, 65535);
        this.imageHeight = getIntAttribute(localNode, "imageHeight", -1, true, true, 1, 65535);
        this.interlaceFlag = getBooleanAttribute(localNode, "interlaceFlag", false, true);
      }
      else if (str1.equals("LocalColorTable"))
      {
        int i = getIntAttribute(localNode, "sizeOfLocalColorTable", true, 2, 256);
        if ((i != 2) && (i != 4) && (i != 8) && (i != 16) && (i != 32) && (i != 64) && (i != 128) && (i != 256)) {
          fatal(localNode, "Bad value for LocalColorTable attribute sizeOfLocalColorTable!");
        }
        this.sortFlag = getBooleanAttribute(localNode, "sortFlag", false, true);
        this.localColorTable = getColorTable(localNode, "ColorTableEntry", true, i);
      }
      else
      {
        Object localObject1;
        if (str1.equals("GraphicControlExtension"))
        {
          localObject1 = getStringAttribute(localNode, "disposalMethod", null, true, disposalMethodNames);
          for (this.disposalMethod = 0; !((String)localObject1).equals(disposalMethodNames[this.disposalMethod]); this.disposalMethod += 1) {}
          this.userInputFlag = getBooleanAttribute(localNode, "userInputFlag", false, true);
          this.transparentColorFlag = getBooleanAttribute(localNode, "transparentColorFlag", false, true);
          this.delayTime = getIntAttribute(localNode, "delayTime", -1, true, true, 0, 65535);
          this.transparentColorIndex = getIntAttribute(localNode, "transparentColorIndex", -1, true, true, 0, 65535);
        }
        else if (str1.equals("PlainTextExtension"))
        {
          this.hasPlainTextExtension = true;
          this.textGridLeft = getIntAttribute(localNode, "textGridLeft", -1, true, true, 0, 65535);
          this.textGridTop = getIntAttribute(localNode, "textGridTop", -1, true, true, 0, 65535);
          this.textGridWidth = getIntAttribute(localNode, "textGridWidth", -1, true, true, 1, 65535);
          this.textGridHeight = getIntAttribute(localNode, "textGridHeight", -1, true, true, 1, 65535);
          this.characterCellWidth = getIntAttribute(localNode, "characterCellWidth", -1, true, true, 1, 65535);
          this.characterCellHeight = getIntAttribute(localNode, "characterCellHeight", -1, true, true, 1, 65535);
          this.textForegroundColor = getIntAttribute(localNode, "textForegroundColor", -1, true, true, 0, 255);
          this.textBackgroundColor = getIntAttribute(localNode, "textBackgroundColor", -1, true, true, 0, 255);
          localObject1 = getStringAttribute(localNode, "text", "", false, null);
          this.text = fromISO8859((String)localObject1);
        }
        else
        {
          String str2;
          if (str1.equals("ApplicationExtensions"))
          {
            localObject1 = (IIOMetadataNode)localNode.getFirstChild();
            if (!((IIOMetadataNode)localObject1).getNodeName().equals("ApplicationExtension")) {
              fatal(localNode, "Only a ApplicationExtension may be a child of a ApplicationExtensions!");
            }
            str2 = getStringAttribute((Node)localObject1, "applicationID", null, true, null);
            String str3 = getStringAttribute((Node)localObject1, "authenticationCode", null, true, null);
            Object localObject2 = ((IIOMetadataNode)localObject1).getUserObject();
            if ((localObject2 == null) || (!(localObject2 instanceof byte[]))) {
              fatal((Node)localObject1, "Bad user object in ApplicationExtension!");
            }
            if (this.applicationIDs == null)
            {
              this.applicationIDs = new ArrayList();
              this.authenticationCodes = new ArrayList();
              this.applicationData = new ArrayList();
            }
            this.applicationIDs.add(fromISO8859(str2));
            this.authenticationCodes.add(fromISO8859(str3));
            this.applicationData.add(localObject2);
          }
          else if (str1.equals("CommentExtensions"))
          {
            localObject1 = localNode.getFirstChild();
            if (localObject1 != null) {
              while (localObject1 != null)
              {
                if (!((Node)localObject1).getNodeName().equals("CommentExtension")) {
                  fatal(localNode, "Only a CommentExtension may be a child of a CommentExtensions!");
                }
                if (this.comments == null) {
                  this.comments = new ArrayList();
                }
                str2 = getStringAttribute((Node)localObject1, "value", null, true, null);
                this.comments.add(fromISO8859(str2));
                localObject1 = ((Node)localObject1).getNextSibling();
              }
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
  
  protected void mergeStandardTree(Node paramNode)
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
      if (str1.equals("Chroma")) {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("Palette"))
          {
            this.localColorTable = getColorTable(localNode2, "PaletteEntry", false, -1);
            break;
          }
        }
      } else if (str1.equals("Compression")) {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("NumProgressiveScans"))
          {
            int i = getIntAttribute(localNode2, "value", 4, false, true, 1, Integer.MAX_VALUE);
            if (i <= 1) {
              break;
            }
            this.interlaceFlag = true;
            break;
          }
        }
      } else if (str1.equals("Dimension")) {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("HorizontalPixelOffset")) {
            this.imageLeftPosition = getIntAttribute(localNode2, "value", -1, true, true, 0, 65535);
          } else if (str2.equals("VerticalPixelOffset")) {
            this.imageTopPosition = getIntAttribute(localNode2, "value", -1, true, true, 0, 65535);
          }
        }
      } else if (str1.equals("Text")) {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if ((str2.equals("TextEntry")) && (getAttribute(localNode2, "compression", "none", false).equals("none")) && (Charset.isSupported(getAttribute(localNode2, "encoding", "ISO-8859-1", false))))
          {
            String str3 = getAttribute(localNode2, "value");
            byte[] arrayOfByte = fromISO8859(str3);
            if (this.comments == null) {
              this.comments = new ArrayList();
            }
            this.comments.add(arrayOfByte);
          }
        }
      } else if (str1.equals("Transparency")) {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("TransparentIndex"))
          {
            this.transparentColorIndex = getIntAttribute(localNode2, "value", -1, true, true, 0, 255);
            this.transparentColorFlag = true;
            break;
          }
        }
      }
    }
  }
  
  public void setFromTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    reset();
    mergeTree(paramString, paramNode);
  }
}
