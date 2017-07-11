package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

abstract class GIFMetadata
  extends IIOMetadata
{
  static final int UNDEFINED_INTEGER_VALUE = -1;
  
  protected static void fatal(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    throw new IIOInvalidTreeException(paramString, paramNode);
  }
  
  protected static String getStringAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean, String[] paramArrayOfString)
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
    String str = localNode.getNodeValue();
    if (paramArrayOfString != null)
    {
      if (str == null) {
        fatal(paramNode, "Null value for " + paramNode.getNodeName() + " attribute " + paramString1 + "!");
      }
      int i = 0;
      int j = paramArrayOfString.length;
      for (int k = 0; k < j; k++) {
        if (str.equals(paramArrayOfString[k]))
        {
          i = 1;
          break;
        }
      }
      if (i == 0) {
        fatal(paramNode, "Bad value for " + paramNode.getNodeName() + " attribute " + paramString1 + "!");
      }
    }
    return str;
  }
  
  protected static int getIntAttribute(Node paramNode, String paramString, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
    throws IIOInvalidTreeException
  {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean1, null);
    if ((str == null) || ("".equals(str))) {
      return paramInt1;
    }
    int i = paramInt1;
    try
    {
      i = Integer.parseInt(str);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      fatal(paramNode, "Bad value for " + paramNode.getNodeName() + " attribute " + paramString + "!");
    }
    if ((paramBoolean2) && ((i < paramInt2) || (i > paramInt3))) {
      fatal(paramNode, "Bad value for " + paramNode.getNodeName() + " attribute " + paramString + "!");
    }
    return i;
  }
  
  protected static float getFloatAttribute(Node paramNode, String paramString, float paramFloat, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean, null);
    if (str == null) {
      return paramFloat;
    }
    return Float.parseFloat(str);
  }
  
  protected static int getIntAttribute(Node paramNode, String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
    throws IIOInvalidTreeException
  {
    return getIntAttribute(paramNode, paramString, -1, true, paramBoolean, paramInt1, paramInt2);
  }
  
  protected static float getFloatAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getFloatAttribute(paramNode, paramString, -1.0F, true);
  }
  
  protected static boolean getBooleanAttribute(Node paramNode, String paramString, boolean paramBoolean1, boolean paramBoolean2)
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
  
  protected static boolean getBooleanAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getBooleanAttribute(paramNode, paramString, false, true);
  }
  
  protected static int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
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
  
  protected static int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString)
    throws IIOInvalidTreeException
  {
    return getEnumeratedAttribute(paramNode, paramString, paramArrayOfString, -1, true);
  }
  
  protected static String getAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean)
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
  
  protected static String getAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getAttribute(paramNode, paramString, null, true);
  }
  
  protected GIFMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    super(paramBoolean, paramString1, paramString2, paramArrayOfString1, paramArrayOfString2);
  }
  
  public void mergeTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramString.equals(this.nativeMetadataFormatName))
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
  
  protected byte[] getColorTable(Node paramNode, String paramString, boolean paramBoolean, int paramInt)
    throws IIOInvalidTreeException
  {
    byte[] arrayOfByte1 = new byte['Ā'];
    byte[] arrayOfByte2 = new byte['Ā'];
    byte[] arrayOfByte3 = new byte['Ā'];
    int i = -1;
    Node localNode = paramNode.getFirstChild();
    if (localNode == null) {
      fatal(paramNode, "Palette has no entries!");
    }
    while (localNode != null)
    {
      if (!localNode.getNodeName().equals(paramString)) {
        fatal(paramNode, "Only a " + paramString + " may be a child of a " + localNode.getNodeName() + "!");
      }
      j = getIntAttribute(localNode, "index", true, 0, 255);
      if (j > i) {
        i = j;
      }
      arrayOfByte1[j] = ((byte)getIntAttribute(localNode, "red", true, 0, 255));
      arrayOfByte2[j] = ((byte)getIntAttribute(localNode, "green", true, 0, 255));
      arrayOfByte3[j] = ((byte)getIntAttribute(localNode, "blue", true, 0, 255));
      localNode = localNode.getNextSibling();
    }
    int j = i + 1;
    if ((paramBoolean) && (j != paramInt)) {
      fatal(paramNode, "Unexpected length for palette!");
    }
    byte[] arrayOfByte4 = new byte[3 * j];
    int k = 0;
    int m = 0;
    while (k < j)
    {
      arrayOfByte4[(m++)] = arrayOfByte1[k];
      arrayOfByte4[(m++)] = arrayOfByte2[k];
      arrayOfByte4[(m++)] = arrayOfByte3[k];
      k++;
    }
    return arrayOfByte4;
  }
  
  protected abstract void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException;
  
  protected abstract void mergeStandardTree(Node paramNode)
    throws IIOInvalidTreeException;
}
