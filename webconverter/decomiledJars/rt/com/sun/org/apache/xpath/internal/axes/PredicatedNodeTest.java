package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract class PredicatedNodeTest
  extends NodeTest
  implements SubContextList
{
  static final long serialVersionUID = -6193530757296377351L;
  protected int m_predCount = -1;
  protected transient boolean m_foundLast = false;
  protected LocPathIterator m_lpi;
  transient int m_predicateIndex = -1;
  private Expression[] m_predicates;
  protected transient int[] m_proximityPositions;
  static final boolean DEBUG_PREDICATECOUNTING = false;
  
  PredicatedNodeTest(LocPathIterator paramLocPathIterator)
  {
    this.m_lpi = paramLocPathIterator;
  }
  
  PredicatedNodeTest() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, TransformerException
  {
    try
    {
      paramObjectInputStream.defaultReadObject();
      this.m_predicateIndex = -1;
      resetProximityPositions();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new TransformerException(localClassNotFoundException);
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    PredicatedNodeTest localPredicatedNodeTest = (PredicatedNodeTest)super.clone();
    if ((null != this.m_proximityPositions) && (this.m_proximityPositions == localPredicatedNodeTest.m_proximityPositions))
    {
      localPredicatedNodeTest.m_proximityPositions = new int[this.m_proximityPositions.length];
      System.arraycopy(this.m_proximityPositions, 0, localPredicatedNodeTest.m_proximityPositions, 0, this.m_proximityPositions.length);
    }
    if (localPredicatedNodeTest.m_lpi == this) {
      localPredicatedNodeTest.m_lpi = ((LocPathIterator)localPredicatedNodeTest);
    }
    return localPredicatedNodeTest;
  }
  
  public int getPredicateCount()
  {
    if (-1 == this.m_predCount) {
      return null == this.m_predicates ? 0 : this.m_predicates.length;
    }
    return this.m_predCount;
  }
  
  public void setPredicateCount(int paramInt)
  {
    if (paramInt > 0)
    {
      Expression[] arrayOfExpression = new Expression[paramInt];
      for (int i = 0; i < paramInt; i++) {
        arrayOfExpression[i] = this.m_predicates[i];
      }
      this.m_predicates = arrayOfExpression;
    }
    else
    {
      this.m_predicates = null;
    }
  }
  
  protected void initPredicateInfo(Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    int i = paramCompiler.getFirstPredicateOpPos(paramInt);
    if (i > 0)
    {
      this.m_predicates = paramCompiler.getCompiledPredicates(i);
      if (null != this.m_predicates) {
        for (int j = 0; j < this.m_predicates.length; j++) {
          this.m_predicates[j].exprSetParent(this);
        }
      }
    }
  }
  
  public Expression getPredicate(int paramInt)
  {
    return this.m_predicates[paramInt];
  }
  
  public int getProximityPosition()
  {
    return getProximityPosition(this.m_predicateIndex);
  }
  
  public int getProximityPosition(XPathContext paramXPathContext)
  {
    return getProximityPosition();
  }
  
  public abstract int getLastPos(XPathContext paramXPathContext);
  
  protected int getProximityPosition(int paramInt)
  {
    return paramInt >= 0 ? this.m_proximityPositions[paramInt] : 0;
  }
  
  public void resetProximityPositions()
  {
    int i = getPredicateCount();
    if (i > 0)
    {
      if (null == this.m_proximityPositions) {
        this.m_proximityPositions = new int[i];
      }
      for (int j = 0; j < i; j++) {
        try
        {
          initProximityPosition(j);
        }
        catch (Exception localException)
        {
          throw new WrappedRuntimeException(localException);
        }
      }
    }
  }
  
  public void initProximityPosition(int paramInt)
    throws TransformerException
  {
    this.m_proximityPositions[paramInt] = 0;
  }
  
  protected void countProximityPosition(int paramInt)
  {
    int[] arrayOfInt = this.m_proximityPositions;
    if ((null != arrayOfInt) && (paramInt < arrayOfInt.length)) {
      arrayOfInt[paramInt] += 1;
    }
  }
  
  public boolean isReverseAxes()
  {
    return false;
  }
  
  public int getPredicateIndex()
  {
    return this.m_predicateIndex;
  }
  
  boolean executePredicates(int paramInt, XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getPredicateCount();
    if (i == 0) {
      return true;
    }
    PrefixResolver localPrefixResolver = paramXPathContext.getNamespaceContext();
    try
    {
      this.m_predicateIndex = 0;
      paramXPathContext.pushSubContextList(this);
      paramXPathContext.pushNamespaceContext(this.m_lpi.getPrefixResolver());
      paramXPathContext.pushCurrentNode(paramInt);
      for (int j = 0; j < i; j++)
      {
        XObject localXObject = this.m_predicates[j].execute(paramXPathContext);
        int k;
        if (2 == localXObject.getType())
        {
          k = getProximityPosition(this.m_predicateIndex);
          int m = (int)localXObject.num();
          if (k != m)
          {
            boolean bool = false;
            return bool;
          }
          if ((this.m_predicates[j].isStableNumber()) && (j == i - 1)) {
            this.m_foundLast = true;
          }
        }
        else if (!localXObject.bool())
        {
          k = 0;
          return k;
        }
        countProximityPosition(++this.m_predicateIndex);
      }
    }
    finally
    {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popNamespaceContext();
      paramXPathContext.popSubContextList();
      this.m_predicateIndex = -1;
    }
    return true;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    int i = getPredicateCount();
    for (int j = 0; j < i; j++) {
      this.m_predicates[j].fixupVariables(paramVector, paramInt);
    }
  }
  
  protected String nodeToString(int paramInt)
  {
    if (-1 != paramInt)
    {
      DTM localDTM = this.m_lpi.getXPathContext().getDTM(paramInt);
      return localDTM.getNodeName(paramInt) + "{" + (paramInt + 1) + "}";
    }
    return "null";
  }
  
  public short acceptNode(int paramInt)
  {
    XPathContext localXPathContext = this.m_lpi.getXPathContext();
    try
    {
      localXPathContext.pushCurrentNode(paramInt);
      XObject localXObject = execute(localXPathContext, paramInt);
      if (localXObject != NodeTest.SCORE_NONE)
      {
        if (getPredicateCount() > 0)
        {
          countProximityPosition(0);
          if (!executePredicates(paramInt, localXPathContext))
          {
            s = 3;
            return s;
          }
        }
        short s = 1;
        return s;
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
    finally
    {
      localXPathContext.popCurrentNode();
    }
    return 3;
  }
  
  public LocPathIterator getLocPathIterator()
  {
    return this.m_lpi;
  }
  
  public void setLocPathIterator(LocPathIterator paramLocPathIterator)
  {
    this.m_lpi = paramLocPathIterator;
    if (this != paramLocPathIterator) {
      paramLocPathIterator.exprSetParent(this);
    }
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    int i = getPredicateCount();
    for (int j = 0; j < i; j++) {
      if (getPredicate(j).canTraverseOutsideSubtree()) {
        return true;
      }
    }
    return false;
  }
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor)
  {
    if (null != this.m_predicates)
    {
      int i = this.m_predicates.length;
      for (int j = 0; j < i; j++)
      {
        PredOwner localPredOwner = new PredOwner(j);
        if (paramXPathVisitor.visitPredicate(localPredOwner, this.m_predicates[j])) {
          this.m_predicates[j].callVisitors(localPredOwner, paramXPathVisitor);
        }
      }
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    PredicatedNodeTest localPredicatedNodeTest = (PredicatedNodeTest)paramExpression;
    if (null != this.m_predicates)
    {
      int i = this.m_predicates.length;
      if ((null == localPredicatedNodeTest.m_predicates) || (localPredicatedNodeTest.m_predicates.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!this.m_predicates[j].deepEquals(localPredicatedNodeTest.m_predicates[j])) {
          return false;
        }
      }
    }
    else if (null != localPredicatedNodeTest.m_predicates)
    {
      return false;
    }
    return true;
  }
  
  class PredOwner
    implements ExpressionOwner
  {
    int m_index;
    
    PredOwner(int paramInt)
    {
      this.m_index = paramInt;
    }
    
    public Expression getExpression()
    {
      return PredicatedNodeTest.this.m_predicates[this.m_index];
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(PredicatedNodeTest.this);
      PredicatedNodeTest.this.m_predicates[this.m_index] = paramExpression;
    }
  }
}
