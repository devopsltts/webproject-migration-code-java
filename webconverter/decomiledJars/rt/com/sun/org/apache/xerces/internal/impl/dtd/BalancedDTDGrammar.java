package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XNIException;

final class BalancedDTDGrammar
  extends DTDGrammar
{
  private boolean fMixed;
  private int fDepth = 0;
  private short[] fOpStack = null;
  private int[][] fGroupIndexStack;
  private int[] fGroupIndexStackSizes;
  
  public BalancedDTDGrammar(SymbolTable paramSymbolTable, XMLDTDDescription paramXMLDTDDescription)
  {
    super(paramSymbolTable, paramXMLDTDDescription);
  }
  
  public final void startContentModel(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fDepth = 0;
    initializeContentModelStacks();
    super.startContentModel(paramString, paramAugmentations);
  }
  
  public final void startGroup(Augmentations paramAugmentations)
    throws XNIException
  {
    this.fDepth += 1;
    initializeContentModelStacks();
    this.fMixed = false;
  }
  
  public final void pcdata(Augmentations paramAugmentations)
    throws XNIException
  {
    this.fMixed = true;
  }
  
  public final void element(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    addToCurrentGroup(addUniqueLeafNode(paramString));
  }
  
  public final void separator(short paramShort, Augmentations paramAugmentations)
    throws XNIException
  {
    if (paramShort == 0) {
      this.fOpStack[this.fDepth] = 4;
    } else if (paramShort == 1) {
      this.fOpStack[this.fDepth] = 5;
    }
  }
  
  public final void occurrence(short paramShort, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fMixed)
    {
      int i = this.fGroupIndexStackSizes[this.fDepth] - 1;
      if (paramShort == 2) {
        this.fGroupIndexStack[this.fDepth][i] = addContentSpecNode(1, this.fGroupIndexStack[this.fDepth][i], -1);
      } else if (paramShort == 3) {
        this.fGroupIndexStack[this.fDepth][i] = addContentSpecNode(2, this.fGroupIndexStack[this.fDepth][i], -1);
      } else if (paramShort == 4) {
        this.fGroupIndexStack[this.fDepth][i] = addContentSpecNode(3, this.fGroupIndexStack[this.fDepth][i], -1);
      }
    }
  }
  
  public final void endGroup(Augmentations paramAugmentations)
    throws XNIException
  {
    int i = this.fGroupIndexStackSizes[this.fDepth];
    int j = i > 0 ? addContentSpecNodes(0, i - 1) : addUniqueLeafNode(null);
    this.fDepth -= 1;
    addToCurrentGroup(j);
  }
  
  public final void endDTD(Augmentations paramAugmentations)
    throws XNIException
  {
    super.endDTD(paramAugmentations);
    this.fOpStack = null;
    this.fGroupIndexStack = ((int[][])null);
    this.fGroupIndexStackSizes = null;
  }
  
  protected final void addContentSpecToElement(XMLElementDecl paramXMLElementDecl)
  {
    int i = this.fGroupIndexStackSizes[0] > 0 ? this.fGroupIndexStack[0][0] : -1;
    setContentSpecIndex(this.fCurrentElementIndex, i);
  }
  
  private int addContentSpecNodes(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return this.fGroupIndexStack[this.fDepth][paramInt1];
    }
    int i = paramInt1 + paramInt2 >>> 1;
    return addContentSpecNode(this.fOpStack[this.fDepth], addContentSpecNodes(paramInt1, i), addContentSpecNodes(i + 1, paramInt2));
  }
  
  private void initializeContentModelStacks()
  {
    if (this.fOpStack == null)
    {
      this.fOpStack = new short[8];
      this.fGroupIndexStack = new int[8][];
      this.fGroupIndexStackSizes = new int[8];
    }
    else if (this.fDepth == this.fOpStack.length)
    {
      short[] arrayOfShort = new short[this.fDepth * 2];
      System.arraycopy(this.fOpStack, 0, arrayOfShort, 0, this.fDepth);
      this.fOpStack = arrayOfShort;
      int[][] arrayOfInt = new int[this.fDepth * 2][];
      System.arraycopy(this.fGroupIndexStack, 0, arrayOfInt, 0, this.fDepth);
      this.fGroupIndexStack = arrayOfInt;
      int[] arrayOfInt1 = new int[this.fDepth * 2];
      System.arraycopy(this.fGroupIndexStackSizes, 0, arrayOfInt1, 0, this.fDepth);
      this.fGroupIndexStackSizes = arrayOfInt1;
    }
    this.fOpStack[this.fDepth] = -1;
    this.fGroupIndexStackSizes[this.fDepth] = 0;
  }
  
  private void addToCurrentGroup(int paramInt)
  {
    Object localObject = this.fGroupIndexStack[this.fDepth];
    int tmp18_15 = this.fDepth;
    int[] tmp18_11 = this.fGroupIndexStackSizes;
    int tmp20_19 = tmp18_11[tmp18_15];
    tmp18_11[tmp18_15] = (tmp20_19 + 1);
    int i = tmp20_19;
    if (localObject == null)
    {
      localObject = new int[8];
      this.fGroupIndexStack[this.fDepth] = localObject;
    }
    else if (i == localObject.length)
    {
      int[] arrayOfInt = new int[localObject.length * 2];
      System.arraycopy(localObject, 0, arrayOfInt, 0, localObject.length);
      localObject = arrayOfInt;
      this.fGroupIndexStack[this.fDepth] = localObject;
    }
    localObject[i] = paramInt;
  }
}
