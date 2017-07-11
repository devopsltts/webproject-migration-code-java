package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DQTMarkerSegment
  extends MarkerSegment
{
  List tables = new ArrayList();
  
  DQTMarkerSegment(float paramFloat, boolean paramBoolean)
  {
    super(219);
    this.tables.add(new Qtable(true, paramFloat));
    if (paramBoolean) {
      this.tables.add(new Qtable(false, paramFloat));
    }
  }
  
  DQTMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    int i = this.length;
    while (i > 0)
    {
      Qtable localQtable = new Qtable(paramJPEGBuffer);
      this.tables.add(localQtable);
      i -= localQtable.data.length + 1;
    }
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  DQTMarkerSegment(JPEGQTable[] paramArrayOfJPEGQTable)
  {
    super(219);
    for (int i = 0; i < paramArrayOfJPEGQTable.length; i++) {
      this.tables.add(new Qtable(paramArrayOfJPEGQTable[i], i));
    }
  }
  
  DQTMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(219);
    NodeList localNodeList = paramNode.getChildNodes();
    int i = localNodeList.getLength();
    if ((i < 1) || (i > 4)) {
      throw new IIOInvalidTreeException("Invalid DQT node", paramNode);
    }
    for (int j = 0; j < i; j++) {
      this.tables.add(new Qtable(localNodeList.item(j)));
    }
  }
  
  protected Object clone()
  {
    DQTMarkerSegment localDQTMarkerSegment = (DQTMarkerSegment)super.clone();
    localDQTMarkerSegment.tables = new ArrayList(this.tables.size());
    Iterator localIterator = this.tables.iterator();
    while (localIterator.hasNext())
    {
      Qtable localQtable = (Qtable)localIterator.next();
      localDQTMarkerSegment.tables.add(localQtable.clone());
    }
    return localDQTMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dqt");
    for (int i = 0; i < this.tables.size(); i++)
    {
      Qtable localQtable = (Qtable)this.tables.get(i);
      localIIOMetadataNode.appendChild(localQtable.getNativeNode());
    }
    return localIIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {}
  
  void print()
  {
    printTag("DQT");
    System.out.println("Num tables: " + Integer.toString(this.tables.size()));
    for (int i = 0; i < this.tables.size(); i++)
    {
      Qtable localQtable = (Qtable)this.tables.get(i);
      localQtable.print();
    }
    System.out.println();
  }
  
  Qtable getChromaForLuma(Qtable paramQtable)
  {
    Qtable localQtable = null;
    int i = 1;
    for (int j = 1;; j++)
    {
      paramQtable.getClass();
      if (j >= 64) {
        break;
      }
      if (paramQtable.data[j] != paramQtable.data[(j - 1)])
      {
        i = 0;
        break;
      }
    }
    if (i != 0)
    {
      localQtable = (Qtable)paramQtable.clone();
      localQtable.tableID = 1;
    }
    else
    {
      j = 0;
      for (int k = 1;; k++)
      {
        paramQtable.getClass();
        if (k >= 64) {
          break;
        }
        if (paramQtable.data[k] > paramQtable.data[j]) {
          j = k;
        }
      }
      float f = paramQtable.data[j] / JPEGQTable.K1Div2Luminance.getTable()[j];
      JPEGQTable localJPEGQTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(f, true);
      localQtable = new Qtable(localJPEGQTable, 1);
    }
    return localQtable;
  }
  
  Qtable getQtableFromNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    return new Qtable(paramNode);
  }
  
  class Qtable
    implements Cloneable
  {
    int elementPrecision;
    int tableID;
    final int QTABLE_SIZE = 64;
    int[] data;
    private final int[] zigzag = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
    
    Qtable(boolean paramBoolean, float paramFloat)
    {
      this.elementPrecision = 0;
      JPEGQTable localJPEGQTable = null;
      if (paramBoolean)
      {
        this.tableID = 0;
        localJPEGQTable = JPEGQTable.K1Div2Luminance;
      }
      else
      {
        this.tableID = 1;
        localJPEGQTable = JPEGQTable.K2Div2Chrominance;
      }
      if (paramFloat != 0.75F)
      {
        paramFloat = JPEG.convertToLinearQuality(paramFloat);
        if (paramBoolean) {
          localJPEGQTable = JPEGQTable.K1Luminance.getScaledInstance(paramFloat, true);
        } else {
          localJPEGQTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(paramFloat, true);
        }
      }
      this.data = localJPEGQTable.getTable();
    }
    
    Qtable(JPEGBuffer paramJPEGBuffer)
      throws IIOException
    {
      this.elementPrecision = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr] >>> 4);
      this.tableID = (paramJPEGBuffer.buf[(paramJPEGBuffer.bufPtr++)] & 0xF);
      if (this.elementPrecision != 0) {
        throw new IIOException("Unsupported element precision");
      }
      this.data = new int[64];
      for (int i = 0; i < 64; i++) {
        this.data[i] = (paramJPEGBuffer.buf[(paramJPEGBuffer.bufPtr + this.zigzag[i])] & 0xFF);
      }
      paramJPEGBuffer.bufPtr += 64;
    }
    
    Qtable(JPEGQTable paramJPEGQTable, int paramInt)
    {
      this.elementPrecision = 0;
      this.tableID = paramInt;
      this.data = paramJPEGQTable.getTable();
    }
    
    Qtable(Node paramNode)
      throws IIOInvalidTreeException
    {
      if (paramNode.getNodeName().equals("dqtable"))
      {
        NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
        int i = localNamedNodeMap.getLength();
        if ((i < 1) || (i > 2)) {
          throw new IIOInvalidTreeException("dqtable node must have 1 or 2 attributes", paramNode);
        }
        this.elementPrecision = 0;
        this.tableID = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "qtableId", 0, 3, true);
        if ((paramNode instanceof IIOMetadataNode))
        {
          IIOMetadataNode localIIOMetadataNode = (IIOMetadataNode)paramNode;
          JPEGQTable localJPEGQTable = (JPEGQTable)localIIOMetadataNode.getUserObject();
          if (localJPEGQTable == null) {
            throw new IIOInvalidTreeException("dqtable node must have user object", paramNode);
          }
          this.data = localJPEGQTable.getTable();
        }
        else
        {
          throw new IIOInvalidTreeException("dqtable node must have user object", paramNode);
        }
      }
      else
      {
        throw new IIOInvalidTreeException("Invalid node, expected dqtable", paramNode);
      }
    }
    
    protected Object clone()
    {
      Qtable localQtable = null;
      try
      {
        localQtable = (Qtable)super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      if (this.data != null) {
        localQtable.data = ((int[])this.data.clone());
      }
      return localQtable;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dqtable");
      localIIOMetadataNode.setAttribute("elementPrecision", Integer.toString(this.elementPrecision));
      localIIOMetadataNode.setAttribute("qtableId", Integer.toString(this.tableID));
      localIIOMetadataNode.setUserObject(new JPEGQTable(this.data));
      return localIIOMetadataNode;
    }
    
    void print()
    {
      System.out.println("Table id: " + Integer.toString(this.tableID));
      System.out.println("Element precision: " + Integer.toString(this.elementPrecision));
      new JPEGQTable(this.data).toString();
    }
  }
}
