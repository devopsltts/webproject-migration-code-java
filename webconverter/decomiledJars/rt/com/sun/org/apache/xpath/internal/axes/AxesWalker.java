package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class AxesWalker
  extends PredicatedNodeTest
  implements Cloneable, PathComponent, ExpressionOwner
{
  static final long serialVersionUID = -2966031951306601247L;
  private DTM m_dtm;
  transient int m_root = -1;
  private transient int m_currentNode = -1;
  transient boolean m_isFresh;
  protected AxesWalker m_nextWalker;
  AxesWalker m_prevWalker;
  protected int m_axis = -1;
  protected DTMAxisTraverser m_traverser;
  
  public AxesWalker(LocPathIterator paramLocPathIterator, int paramInt)
  {
    super(paramLocPathIterator);
    this.m_axis = paramInt;
  }
  
  public final WalkingIterator wi()
  {
    return (WalkingIterator)this.m_lpi;
  }
  
  public void init(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    initPredicateInfo(paramCompiler, paramInt1);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    AxesWalker localAxesWalker = (AxesWalker)super.clone();
    return localAxesWalker;
  }
  
  AxesWalker cloneDeep(WalkingIterator paramWalkingIterator, Vector paramVector)
    throws CloneNotSupportedException
  {
    AxesWalker localAxesWalker = findClone(this, paramVector);
    if (null != localAxesWalker) {
      return localAxesWalker;
    }
    localAxesWalker = (AxesWalker)clone();
    localAxesWalker.setLocPathIterator(paramWalkingIterator);
    if (null != paramVector)
    {
      paramVector.addElement(this);
      paramVector.addElement(localAxesWalker);
    }
    if (wi().m_lastUsedWalker == this) {
      paramWalkingIterator.m_lastUsedWalker = localAxesWalker;
    }
    if (null != this.m_nextWalker) {
      localAxesWalker.m_nextWalker = this.m_nextWalker.cloneDeep(paramWalkingIterator, paramVector);
    }
    if (null != paramVector)
    {
      if (null != this.m_prevWalker) {
        localAxesWalker.m_prevWalker = this.m_prevWalker.cloneDeep(paramWalkingIterator, paramVector);
      }
    }
    else if (null != this.m_nextWalker) {
      localAxesWalker.m_nextWalker.m_prevWalker = localAxesWalker;
    }
    return localAxesWalker;
  }
  
  static AxesWalker findClone(AxesWalker paramAxesWalker, Vector paramVector)
  {
    if (null != paramVector)
    {
      int i = paramVector.size();
      for (int j = 0; j < i; j += 2) {
        if (paramAxesWalker == paramVector.elementAt(j)) {
          return (AxesWalker)paramVector.elementAt(j + 1);
        }
      }
    }
    return null;
  }
  
  public void detach()
  {
    this.m_currentNode = -1;
    this.m_dtm = null;
    this.m_traverser = null;
    this.m_isFresh = true;
    this.m_root = -1;
  }
  
  public int getRoot()
  {
    return this.m_root;
  }
  
  public int getAnalysisBits()
  {
    int i = getAxis();
    int j = WalkerFactory.getAnalysisBitFromAxes(i);
    return j;
  }
  
  public void setRoot(int paramInt)
  {
    XPathContext localXPathContext = wi().getXPathContext();
    this.m_dtm = localXPathContext.getDTM(paramInt);
    this.m_traverser = this.m_dtm.getAxisTraverser(this.m_axis);
    this.m_isFresh = true;
    this.m_foundLast = false;
    this.m_root = paramInt;
    this.m_currentNode = paramInt;
    if (-1 == paramInt) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_SETTING_WALKER_ROOT_TO_NULL", null));
    }
    resetProximityPositions();
  }
  
  public final int getCurrentNode()
  {
    return this.m_currentNode;
  }
  
  public void setNextWalker(AxesWalker paramAxesWalker)
  {
    this.m_nextWalker = paramAxesWalker;
  }
  
  public AxesWalker getNextWalker()
  {
    return this.m_nextWalker;
  }
  
  public void setPrevWalker(AxesWalker paramAxesWalker)
  {
    this.m_prevWalker = paramAxesWalker;
  }
  
  public AxesWalker getPrevWalker()
  {
    return this.m_prevWalker;
  }
  
  private int returnNextNode(int paramInt)
  {
    return paramInt;
  }
  
  protected int getNextNode()
  {
    if (this.m_foundLast) {
      return -1;
    }
    if (this.m_isFresh)
    {
      this.m_currentNode = this.m_traverser.first(this.m_root);
      this.m_isFresh = false;
    }
    else if (-1 != this.m_currentNode)
    {
      this.m_currentNode = this.m_traverser.next(this.m_root, this.m_currentNode);
    }
    if (-1 == this.m_currentNode) {
      this.m_foundLast = true;
    }
    return this.m_currentNode;
  }
  
  public int nextNode()
  {
    int i = -1;
    AxesWalker localAxesWalker1 = wi().getLastUsedWalker();
    while (null != localAxesWalker1)
    {
      i = localAxesWalker1.getNextNode();
      if (-1 == i)
      {
        localAxesWalker1 = localAxesWalker1.m_prevWalker;
      }
      else if (localAxesWalker1.acceptNode(i) == 1)
      {
        if (null == localAxesWalker1.m_nextWalker)
        {
          wi().setLastUsedWalker(localAxesWalker1);
          break;
        }
        AxesWalker localAxesWalker2 = localAxesWalker1;
        localAxesWalker1 = localAxesWalker1.m_nextWalker;
        localAxesWalker1.setRoot(i);
        localAxesWalker1.m_prevWalker = localAxesWalker2;
      }
    }
    return i;
  }
  
  public int getLastPos(XPathContext paramXPathContext)
  {
    int i = getProximityPosition();
    AxesWalker localAxesWalker1;
    try
    {
      localAxesWalker1 = (AxesWalker)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      return -1;
    }
    localAxesWalker1.setPredicateCount(this.m_predicateIndex);
    localAxesWalker1.setNextWalker(null);
    localAxesWalker1.setPrevWalker(null);
    WalkingIterator localWalkingIterator = wi();
    AxesWalker localAxesWalker2 = localWalkingIterator.getLastUsedWalker();
    try
    {
      localWalkingIterator.setLastUsedWalker(localAxesWalker1);
      int j;
      while (-1 != (j = localAxesWalker1.nextNode())) {
        i++;
      }
    }
    finally
    {
      localWalkingIterator.setLastUsedWalker(localAxesWalker2);
    }
    return i;
  }
  
  public void setDefaultDTM(DTM paramDTM)
  {
    this.m_dtm = paramDTM;
  }
  
  public DTM getDTM(int paramInt)
  {
    return wi().getXPathContext().getDTM(paramInt);
  }
  
  public boolean isDocOrdered()
  {
    return true;
  }
  
  public int getAxis()
  {
    return this.m_axis;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if (paramXPathVisitor.visitStep(paramExpressionOwner, this))
    {
      callPredicateVisitors(paramXPathVisitor);
      if (null != this.m_nextWalker) {
        this.m_nextWalker.callVisitors(this, paramXPathVisitor);
      }
    }
  }
  
  public Expression getExpression()
  {
    return this.m_nextWalker;
  }
  
  public void setExpression(Expression paramExpression)
  {
    paramExpression.exprSetParent(this);
    this.m_nextWalker = ((AxesWalker)paramExpression);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    AxesWalker localAxesWalker = (AxesWalker)paramExpression;
    return this.m_axis == localAxesWalker.m_axis;
  }
}
