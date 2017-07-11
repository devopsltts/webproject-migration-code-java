package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FilterExprIteratorSimple
  extends LocPathIterator
{
  static final long serialVersionUID = -6978977187025375579L;
  private Expression m_expr;
  private transient XNodeSet m_exprObj;
  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;
  
  public FilterExprIteratorSimple()
  {
    super(null);
  }
  
  public FilterExprIteratorSimple(Expression paramExpression)
  {
    super(null);
    this.m_expr = paramExpression;
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    this.m_exprObj = executeFilterExpr(paramInt, this.m_execContext, getPrefixResolver(), getIsTopLevel(), this.m_stackFrame, this.m_expr);
  }
  
  public static XNodeSet executeFilterExpr(int paramInt1, XPathContext paramXPathContext, PrefixResolver paramPrefixResolver, boolean paramBoolean, int paramInt2, Expression paramExpression)
    throws WrappedRuntimeException
  {
    PrefixResolver localPrefixResolver = paramXPathContext.getNamespaceContext();
    XNodeSet localXNodeSet = null;
    try
    {
      paramXPathContext.pushCurrentNode(paramInt1);
      paramXPathContext.setNamespaceContext(paramPrefixResolver);
      if (paramBoolean)
      {
        VariableStack localVariableStack = paramXPathContext.getVarStack();
        int i = localVariableStack.getStackFrame();
        localVariableStack.setStackFrame(paramInt2);
        localXNodeSet = (XNodeSet)paramExpression.execute(paramXPathContext);
        localXNodeSet.setShouldCacheNodes(true);
        localVariableStack.setStackFrame(i);
      }
      else
      {
        localXNodeSet = (XNodeSet)paramExpression.execute(paramXPathContext);
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new WrappedRuntimeException(localTransformerException);
    }
    finally
    {
      paramXPathContext.popCurrentNode();
      paramXPathContext.setNamespaceContext(localPrefixResolver);
    }
    return localXNodeSet;
  }
  
  public int nextNode()
  {
    if (this.m_foundLast) {
      return -1;
    }
    int i;
    if (null != this.m_exprObj) {
      this.m_lastFetched = (i = this.m_exprObj.nextNode());
    } else {
      this.m_lastFetched = (i = -1);
    }
    if (-1 != i)
    {
      this.m_pos += 1;
      return i;
    }
    this.m_foundLast = true;
    return -1;
  }
  
  public void detach()
  {
    if (this.m_allowDetach)
    {
      super.detach();
      this.m_exprObj.detach();
      this.m_exprObj = null;
    }
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    this.m_expr.fixupVariables(paramVector, paramInt);
  }
  
  public Expression getInnerExpression()
  {
    return this.m_expr;
  }
  
  public void setInnerExpression(Expression paramExpression)
  {
    paramExpression.exprSetParent(this);
    this.m_expr = paramExpression;
  }
  
  public int getAnalysisBits()
  {
    if ((null != this.m_expr) && ((this.m_expr instanceof PathComponent))) {
      return ((PathComponent)this.m_expr).getAnalysisBits();
    }
    return 67108864;
  }
  
  public boolean isDocOrdered()
  {
    return this.m_exprObj.isDocOrdered();
  }
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor)
  {
    this.m_expr.callVisitors(new filterExprOwner(), paramXPathVisitor);
    super.callPredicateVisitors(paramXPathVisitor);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    FilterExprIteratorSimple localFilterExprIteratorSimple = (FilterExprIteratorSimple)paramExpression;
    return this.m_expr.deepEquals(localFilterExprIteratorSimple.m_expr);
  }
  
  public int getAxis()
  {
    if (null != this.m_exprObj) {
      return this.m_exprObj.getAxis();
    }
    return 20;
  }
  
  class filterExprOwner
    implements ExpressionOwner
  {
    filterExprOwner() {}
    
    public Expression getExpression()
    {
      return FilterExprIteratorSimple.this.m_expr;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(FilterExprIteratorSimple.this);
      FilterExprIteratorSimple.this.m_expr = paramExpression;
    }
  }
}
