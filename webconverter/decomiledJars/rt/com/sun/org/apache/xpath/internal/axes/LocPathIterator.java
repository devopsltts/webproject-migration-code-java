package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class LocPathIterator
  extends PredicatedNodeTest
  implements Cloneable, DTMIterator, Serializable, PathComponent
{
  static final long serialVersionUID = -4602476357268405754L;
  protected boolean m_allowDetach = true;
  protected transient IteratorPool m_clones = new IteratorPool(this);
  protected transient DTM m_cdtm;
  transient int m_stackFrame = -1;
  private boolean m_isTopLevel = false;
  public transient int m_lastFetched = -1;
  protected transient int m_context = -1;
  protected transient int m_currentContextNode = -1;
  protected transient int m_pos = 0;
  protected transient int m_length = -1;
  private PrefixResolver m_prefixResolver;
  protected transient XPathContext m_execContext;
  
  protected LocPathIterator() {}
  
  protected LocPathIterator(PrefixResolver paramPrefixResolver)
  {
    setLocPathIterator(this);
    this.m_prefixResolver = paramPrefixResolver;
  }
  
  protected LocPathIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    this(paramCompiler, paramInt1, paramInt2, true);
  }
  
  protected LocPathIterator(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean)
    throws TransformerException
  {
    setLocPathIterator(this);
  }
  
  public int getAnalysisBits()
  {
    int i = getAxis();
    int j = WalkerFactory.getAnalysisBitFromAxes(i);
    return j;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, TransformerException
  {
    try
    {
      paramObjectInputStream.defaultReadObject();
      this.m_clones = new IteratorPool(this);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new TransformerException(localClassNotFoundException);
    }
  }
  
  public void setEnvironment(Object paramObject) {}
  
  public DTM getDTM(int paramInt)
  {
    return this.m_execContext.getDTM(paramInt);
  }
  
  public DTMManager getDTMManager()
  {
    return this.m_execContext.getDTMManager();
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XNodeSet localXNodeSet = new XNodeSet((LocPathIterator)this.m_clones.getInstance());
    localXNodeSet.setRoot(paramXPathContext.getCurrentNode(), paramXPathContext);
    return localXNodeSet;
  }
  
  public void executeCharsToContentHandler(XPathContext paramXPathContext, ContentHandler paramContentHandler)
    throws TransformerException, SAXException
  {
    LocPathIterator localLocPathIterator = (LocPathIterator)this.m_clones.getInstance();
    int i = paramXPathContext.getCurrentNode();
    localLocPathIterator.setRoot(i, paramXPathContext);
    int j = localLocPathIterator.nextNode();
    DTM localDTM = localLocPathIterator.getDTM(j);
    localLocPathIterator.detach();
    if (j != -1) {
      localDTM.dispatchCharactersEvents(j, paramContentHandler, false);
    }
  }
  
  public DTMIterator asIterator(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    XNodeSet localXNodeSet = new XNodeSet((LocPathIterator)this.m_clones.getInstance());
    localXNodeSet.setRoot(paramInt, paramXPathContext);
    return localXNodeSet;
  }
  
  public boolean isNodesetExpr()
  {
    return true;
  }
  
  public int asNode(XPathContext paramXPathContext)
    throws TransformerException
  {
    DTMIterator localDTMIterator = this.m_clones.getInstance();
    int i = paramXPathContext.getCurrentNode();
    localDTMIterator.setRoot(i, paramXPathContext);
    int j = localDTMIterator.nextNode();
    localDTMIterator.detach();
    return j;
  }
  
  public boolean bool(XPathContext paramXPathContext)
    throws TransformerException
  {
    return asNode(paramXPathContext) != -1;
  }
  
  public void setIsTopLevel(boolean paramBoolean)
  {
    this.m_isTopLevel = paramBoolean;
  }
  
  public boolean getIsTopLevel()
  {
    return this.m_isTopLevel;
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    this.m_context = paramInt;
    XPathContext localXPathContext = (XPathContext)paramObject;
    this.m_execContext = localXPathContext;
    this.m_cdtm = localXPathContext.getDTM(paramInt);
    this.m_currentContextNode = paramInt;
    if (null == this.m_prefixResolver) {
      this.m_prefixResolver = localXPathContext.getNamespaceContext();
    }
    this.m_lastFetched = -1;
    this.m_foundLast = false;
    this.m_pos = 0;
    this.m_length = -1;
    if (this.m_isTopLevel) {
      this.m_stackFrame = localXPathContext.getVarStack().getStackFrame();
    }
  }
  
  protected void setNextPosition(int paramInt)
  {
    assertion(false, "setNextPosition not supported in this iterator!");
  }
  
  public final int getCurrentPos()
  {
    return this.m_pos;
  }
  
  public void setShouldCacheNodes(boolean paramBoolean)
  {
    assertion(false, "setShouldCacheNodes not supported by this iterater!");
  }
  
  public boolean isMutable()
  {
    return false;
  }
  
  public void setCurrentPos(int paramInt)
  {
    assertion(false, "setCurrentPos not supported by this iterator!");
  }
  
  public void incrementCurrentPos()
  {
    this.m_pos += 1;
  }
  
  public int size()
  {
    assertion(false, "size() not supported by this iterator!");
    return 0;
  }
  
  public int item(int paramInt)
  {
    assertion(false, "item(int index) not supported by this iterator!");
    return 0;
  }
  
  public void setItem(int paramInt1, int paramInt2)
  {
    assertion(false, "setItem not supported by this iterator!");
  }
  
  public int getLength()
  {
    int i = this == this.m_execContext.getSubContextList() ? 1 : 0;
    int j = getPredicateCount();
    if ((-1 != this.m_length) && (i != 0) && (this.m_predicateIndex < 1)) {
      return this.m_length;
    }
    if (this.m_foundLast) {
      return this.m_pos;
    }
    int k = this.m_predicateIndex >= 0 ? getProximityPosition() : this.m_pos;
    LocPathIterator localLocPathIterator;
    try
    {
      localLocPathIterator = (LocPathIterator)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      return -1;
    }
    if ((j > 0) && (i != 0)) {
      localLocPathIterator.m_predCount = this.m_predicateIndex;
    }
    int m;
    while (-1 != (m = localLocPathIterator.nextNode())) {
      k++;
    }
    if ((i != 0) && (this.m_predicateIndex < 1)) {
      this.m_length = k;
    }
    return k;
  }
  
  public boolean isFresh()
  {
    return this.m_pos == 0;
  }
  
  public int previousNode()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null));
  }
  
  public int getWhatToShow()
  {
    return -17;
  }
  
  public DTMFilter getFilter()
  {
    return null;
  }
  
  public int getRoot()
  {
    return this.m_context;
  }
  
  public boolean getExpandEntityReferences()
  {
    return true;
  }
  
  public void allowDetachToRelease(boolean paramBoolean)
  {
    this.m_allowDetach = paramBoolean;
  }
  
  public void detach()
  {
    if (this.m_allowDetach)
    {
      this.m_execContext = null;
      this.m_cdtm = null;
      this.m_length = -1;
      this.m_pos = 0;
      this.m_lastFetched = -1;
      this.m_context = -1;
      this.m_currentContextNode = -1;
      this.m_clones.freeInstance(this);
    }
  }
  
  public void reset()
  {
    assertion(false, "This iterator can not reset!");
  }
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    LocPathIterator localLocPathIterator = (LocPathIterator)this.m_clones.getInstanceOrThrow();
    localLocPathIterator.m_execContext = this.m_execContext;
    localLocPathIterator.m_cdtm = this.m_cdtm;
    localLocPathIterator.m_context = this.m_context;
    localLocPathIterator.m_currentContextNode = this.m_currentContextNode;
    localLocPathIterator.m_stackFrame = this.m_stackFrame;
    return localLocPathIterator;
  }
  
  public abstract int nextNode();
  
  protected int returnNextNode(int paramInt)
  {
    if (-1 != paramInt) {
      this.m_pos += 1;
    }
    this.m_lastFetched = paramInt;
    if (-1 == paramInt) {
      this.m_foundLast = true;
    }
    return paramInt;
  }
  
  public int getCurrentNode()
  {
    return this.m_lastFetched;
  }
  
  public void runTo(int paramInt)
  {
    if ((this.m_foundLast) || ((paramInt >= 0) && (paramInt <= getCurrentPos()))) {
      return;
    }
    int i;
    if (-1 == paramInt) {
      while (-1 != (i = nextNode())) {}
    }
    while (-1 != (i = nextNode())) {
      if (getCurrentPos() >= paramInt) {
        break;
      }
    }
  }
  
  public final boolean getFoundLast()
  {
    return this.m_foundLast;
  }
  
  public final XPathContext getXPathContext()
  {
    return this.m_execContext;
  }
  
  public final int getContext()
  {
    return this.m_context;
  }
  
  public final int getCurrentContextNode()
  {
    return this.m_currentContextNode;
  }
  
  public final void setCurrentContextNode(int paramInt)
  {
    this.m_currentContextNode = paramInt;
  }
  
  public final PrefixResolver getPrefixResolver()
  {
    if (null == this.m_prefixResolver) {
      this.m_prefixResolver = ((PrefixResolver)getExpressionOwner());
    }
    return this.m_prefixResolver;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if (paramXPathVisitor.visitLocationPath(paramExpressionOwner, this))
    {
      paramXPathVisitor.visitStep(paramExpressionOwner, this);
      callPredicateVisitors(paramXPathVisitor);
    }
  }
  
  public boolean isDocOrdered()
  {
    return true;
  }
  
  public int getAxis()
  {
    return -1;
  }
  
  public int getLastPos(XPathContext paramXPathContext)
  {
    return getLength();
  }
}
