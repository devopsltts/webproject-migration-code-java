package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class StepPattern
  extends NodeTest
  implements SubContextList, ExpressionOwner
{
  static final long serialVersionUID = 9071668960168152644L;
  protected int m_axis;
  String m_targetString;
  StepPattern m_relativePathPattern;
  Expression[] m_predicates;
  private static final boolean DEBUG_MATCHES = false;
  
  public StepPattern(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3)
  {
    super(paramInt1, paramString1, paramString2);
    this.m_axis = paramInt2;
  }
  
  public StepPattern(int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramInt1);
    this.m_axis = paramInt2;
  }
  
  public void calcTargetString()
  {
    int i = getWhatToShow();
    switch (i)
    {
    case 128: 
      this.m_targetString = "#comment";
      break;
    case 4: 
    case 8: 
    case 12: 
      this.m_targetString = "#text";
      break;
    case -1: 
      this.m_targetString = "*";
      break;
    case 256: 
    case 1280: 
      this.m_targetString = "/";
      break;
    case 1: 
      if ("*" == this.m_name) {
        this.m_targetString = "*";
      } else {
        this.m_targetString = this.m_name;
      }
      break;
    default: 
      this.m_targetString = "*";
    }
  }
  
  public String getTargetString()
  {
    return this.m_targetString;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    if (null != this.m_predicates) {
      for (int i = 0; i < this.m_predicates.length; i++) {
        this.m_predicates[i].fixupVariables(paramVector, paramInt);
      }
    }
    if (null != this.m_relativePathPattern) {
      this.m_relativePathPattern.fixupVariables(paramVector, paramInt);
    }
  }
  
  public void setRelativePathPattern(StepPattern paramStepPattern)
  {
    this.m_relativePathPattern = paramStepPattern;
    paramStepPattern.exprSetParent(this);
    calcScore();
  }
  
  public StepPattern getRelativePathPattern()
  {
    return this.m_relativePathPattern;
  }
  
  public Expression[] getPredicates()
  {
    return this.m_predicates;
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
  
  public Expression getPredicate(int paramInt)
  {
    return this.m_predicates[paramInt];
  }
  
  public final int getPredicateCount()
  {
    return null == this.m_predicates ? 0 : this.m_predicates.length;
  }
  
  public void setPredicates(Expression[] paramArrayOfExpression)
  {
    this.m_predicates = paramArrayOfExpression;
    if (null != paramArrayOfExpression) {
      for (int i = 0; i < paramArrayOfExpression.length; i++) {
        paramArrayOfExpression[i].exprSetParent(this);
      }
    }
    calcScore();
  }
  
  public void calcScore()
  {
    if ((getPredicateCount() > 0) || (null != this.m_relativePathPattern)) {
      this.m_score = SCORE_OTHER;
    } else {
      super.calcScore();
    }
    if (null == this.m_targetString) {
      calcTargetString();
    }
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    DTM localDTM = paramXPathContext.getDTM(paramInt);
    if (localDTM != null)
    {
      int i = localDTM.getExpandedTypeID(paramInt);
      return execute(paramXPathContext, paramInt, localDTM, i);
    }
    return NodeTest.SCORE_NONE;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return execute(paramXPathContext, paramXPathContext.getCurrentNode());
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2)
    throws TransformerException
  {
    if (this.m_whatToShow == 65536)
    {
      if (null != this.m_relativePathPattern) {
        return this.m_relativePathPattern.execute(paramXPathContext);
      }
      return NodeTest.SCORE_NONE;
    }
    XObject localXObject = super.execute(paramXPathContext, paramInt1, paramDTM, paramInt2);
    if (localXObject == NodeTest.SCORE_NONE) {
      return NodeTest.SCORE_NONE;
    }
    if ((getPredicateCount() != 0) && (!executePredicates(paramXPathContext, paramDTM, paramInt1))) {
      return NodeTest.SCORE_NONE;
    }
    if (null != this.m_relativePathPattern) {
      return this.m_relativePathPattern.executeRelativePathPattern(paramXPathContext, paramDTM, paramInt1);
    }
    return localXObject;
  }
  
  private final boolean checkProximityPosition(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2, int paramInt3)
  {
    try
    {
      DTMAxisTraverser localDTMAxisTraverser = paramDTM.getAxisTraverser(12);
      for (int i = localDTMAxisTraverser.first(paramInt2); -1 != i; i = localDTMAxisTraverser.next(paramInt2, i)) {
        try
        {
          paramXPathContext.pushCurrentNode(i);
          if (NodeTest.SCORE_NONE != super.execute(paramXPathContext, i))
          {
            int j = 1;
            int k;
            try
            {
              paramXPathContext.pushSubContextList(this);
              for (k = 0; k < paramInt1; k++)
              {
                paramXPathContext.pushPredicatePos(k);
                try
                {
                  XObject localXObject = this.m_predicates[k].execute(paramXPathContext);
                  try
                  {
                    if (2 == localXObject.getType()) {
                      throw new Error("Why: Should never have been called");
                    }
                    if (!localXObject.boolWithSideEffects())
                    {
                      j = 0;
                      localXObject.detach();
                      paramXPathContext.popPredicatePos();
                      break;
                    }
                  }
                  finally {}
                }
                finally {}
              }
            }
            finally {}
            if (j != 0) {
              paramInt3--;
            }
            if (paramInt3 < 1)
            {
              k = 0;
              return k;
            }
          }
        }
        finally
        {
          paramXPathContext.popCurrentNode();
        }
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
    return paramInt3 == 1;
  }
  
  private final int getProximityPosition(XPathContext paramXPathContext, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    int j = paramXPathContext.getCurrentNode();
    DTM localDTM = paramXPathContext.getDTM(j);
    int k = localDTM.getParent(j);
    try
    {
      DTMAxisTraverser localDTMAxisTraverser = localDTM.getAxisTraverser(3);
      for (int m = localDTMAxisTraverser.first(k); -1 != m; m = localDTMAxisTraverser.next(k, m)) {
        try
        {
          paramXPathContext.pushCurrentNode(m);
          if (NodeTest.SCORE_NONE != super.execute(paramXPathContext, m))
          {
            int n = 1;
            int i1;
            try
            {
              paramXPathContext.pushSubContextList(this);
              for (i1 = 0; i1 < paramInt; i1++)
              {
                paramXPathContext.pushPredicatePos(i1);
                try
                {
                  XObject localXObject = this.m_predicates[i1].execute(paramXPathContext);
                  try
                  {
                    if (2 == localXObject.getType())
                    {
                      if (i + 1 != (int)localXObject.numWithSideEffects())
                      {
                        n = 0;
                        localXObject.detach();
                        paramXPathContext.popPredicatePos();
                        break;
                      }
                    }
                    else if (!localXObject.boolWithSideEffects())
                    {
                      n = 0;
                      localXObject.detach();
                      paramXPathContext.popPredicatePos();
                      break;
                    }
                  }
                  finally {}
                }
                finally {}
              }
            }
            finally {}
            if (n != 0) {
              i++;
            }
            if ((!paramBoolean) && (m == j))
            {
              i1 = i;
              return i1;
            }
          }
        }
        finally
        {
          paramXPathContext.popCurrentNode();
        }
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
    return i;
  }
  
  public int getProximityPosition(XPathContext paramXPathContext)
  {
    return getProximityPosition(paramXPathContext, paramXPathContext.getPredicatePos(), false);
  }
  
  public int getLastPos(XPathContext paramXPathContext)
  {
    return getProximityPosition(paramXPathContext, paramXPathContext.getPredicatePos(), true);
  }
  
  protected final XObject executeRelativePathPattern(XPathContext paramXPathContext, DTM paramDTM, int paramInt)
    throws TransformerException
  {
    Object localObject1 = NodeTest.SCORE_NONE;
    int i = paramInt;
    DTMAxisTraverser localDTMAxisTraverser = paramDTM.getAxisTraverser(this.m_axis);
    for (int j = localDTMAxisTraverser.first(i); -1 != j; j = localDTMAxisTraverser.next(i, j)) {
      try
      {
        paramXPathContext.pushCurrentNode(j);
        localObject1 = execute(paramXPathContext);
        if (localObject1 != NodeTest.SCORE_NONE)
        {
          paramXPathContext.popCurrentNode();
          break;
        }
      }
      finally
      {
        paramXPathContext.popCurrentNode();
      }
    }
    return localObject1;
  }
  
  protected final boolean executePredicates(XPathContext paramXPathContext, DTM paramDTM, int paramInt)
    throws TransformerException
  {
    boolean bool = true;
    int i = 0;
    int j = getPredicateCount();
    try
    {
      paramXPathContext.pushSubContextList(this);
      for (int k = 0; k < j; k++)
      {
        paramXPathContext.pushPredicatePos(k);
        try
        {
          XObject localXObject = this.m_predicates[k].execute(paramXPathContext);
          try
          {
            if (2 == localXObject.getType())
            {
              int m = (int)localXObject.num();
              if (i != 0)
              {
                bool = m == 1;
                localXObject.detach();
                paramXPathContext.popPredicatePos();
                break;
              }
              i = 1;
              if (!checkProximityPosition(paramXPathContext, k, paramDTM, paramInt, m))
              {
                bool = false;
                localXObject.detach();
                paramXPathContext.popPredicatePos();
                break;
              }
            }
            else if (!localXObject.boolWithSideEffects())
            {
              bool = false;
              localXObject.detach();
              paramXPathContext.popPredicatePos();
              break;
            }
          }
          finally {}
        }
        finally {}
      }
    }
    finally
    {
      paramXPathContext.popSubContextList();
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (StepPattern localStepPattern = this; localStepPattern != null; localStepPattern = localStepPattern.m_relativePathPattern)
    {
      if (localStepPattern != this) {
        localStringBuffer.append("/");
      }
      localStringBuffer.append(Axis.getNames(localStepPattern.m_axis));
      localStringBuffer.append("::");
      if (20480 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("doc()");
      }
      else if (65536 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("function()");
      }
      else if (-1 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("node()");
      }
      else if (4 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("text()");
      }
      else if (64 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("processing-instruction(");
        if (null != localStepPattern.m_name) {
          localStringBuffer.append(localStepPattern.m_name);
        }
        localStringBuffer.append(")");
      }
      else if (128 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("comment()");
      }
      else if (null != localStepPattern.m_name)
      {
        if (2 == localStepPattern.m_whatToShow) {
          localStringBuffer.append("@");
        }
        if (null != localStepPattern.m_namespace)
        {
          localStringBuffer.append("{");
          localStringBuffer.append(localStepPattern.m_namespace);
          localStringBuffer.append("}");
        }
        localStringBuffer.append(localStepPattern.m_name);
      }
      else if (2 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("@");
      }
      else if (1280 == localStepPattern.m_whatToShow)
      {
        localStringBuffer.append("doc-root()");
      }
      else
      {
        localStringBuffer.append('?').append(Integer.toHexString(localStepPattern.m_whatToShow));
      }
      if (null != localStepPattern.m_predicates) {
        for (int i = 0; i < localStepPattern.m_predicates.length; i++)
        {
          localStringBuffer.append("[");
          localStringBuffer.append(localStepPattern.m_predicates[i]);
          localStringBuffer.append("]");
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  public double getMatchScore(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    paramXPathContext.pushCurrentNode(paramInt);
    paramXPathContext.pushCurrentExpressionNode(paramInt);
    try
    {
      XObject localXObject = execute(paramXPathContext);
      double d = localXObject.num();
      return d;
    }
    finally
    {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popCurrentExpressionNode();
    }
  }
  
  public void setAxis(int paramInt)
  {
    this.m_axis = paramInt;
  }
  
  public int getAxis()
  {
    return this.m_axis;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if (paramXPathVisitor.visitMatchPattern(paramExpressionOwner, this)) {
      callSubtreeVisitors(paramXPathVisitor);
    }
  }
  
  protected void callSubtreeVisitors(XPathVisitor paramXPathVisitor)
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
    if (null != this.m_relativePathPattern) {
      this.m_relativePathPattern.callVisitors(this, paramXPathVisitor);
    }
  }
  
  public Expression getExpression()
  {
    return this.m_relativePathPattern;
  }
  
  public void setExpression(Expression paramExpression)
  {
    paramExpression.exprSetParent(this);
    this.m_relativePathPattern = ((StepPattern)paramExpression);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    StepPattern localStepPattern = (StepPattern)paramExpression;
    if (null != this.m_predicates)
    {
      int i = this.m_predicates.length;
      if ((null == localStepPattern.m_predicates) || (localStepPattern.m_predicates.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!this.m_predicates[j].deepEquals(localStepPattern.m_predicates[j])) {
          return false;
        }
      }
    }
    else if (null != localStepPattern.m_predicates)
    {
      return false;
    }
    if (null != this.m_relativePathPattern)
    {
      if (!this.m_relativePathPattern.deepEquals(localStepPattern.m_relativePathPattern)) {
        return false;
      }
    }
    else if (localStepPattern.m_relativePathPattern != null) {
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
      return StepPattern.this.m_predicates[this.m_index];
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(StepPattern.this);
      StepPattern.this.m_predicates[this.m_index] = paramExpression;
    }
  }
}
