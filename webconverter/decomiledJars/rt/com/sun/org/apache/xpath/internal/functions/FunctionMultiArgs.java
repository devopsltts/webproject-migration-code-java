package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class FunctionMultiArgs
  extends Function3Args
{
  static final long serialVersionUID = 7117257746138417181L;
  Expression[] m_args;
  
  public FunctionMultiArgs() {}
  
  public Expression[] getArgs()
  {
    return this.m_args;
  }
  
  public void setArg(Expression paramExpression, int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt < 3)
    {
      super.setArg(paramExpression, paramInt);
    }
    else
    {
      if (null == this.m_args)
      {
        this.m_args = new Expression[1];
        this.m_args[0] = paramExpression;
      }
      else
      {
        Expression[] arrayOfExpression = new Expression[this.m_args.length + 1];
        System.arraycopy(this.m_args, 0, arrayOfExpression, 0, this.m_args.length);
        arrayOfExpression[this.m_args.length] = paramExpression;
        this.m_args = arrayOfExpression;
      }
      paramExpression.exprSetParent(this);
    }
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    if (null != this.m_args) {
      for (int i = 0; i < this.m_args.length; i++) {
        this.m_args[i].fixupVariables(paramVector, paramInt);
      }
    }
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {}
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });
    throw new RuntimeException(str);
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    if (super.canTraverseOutsideSubtree()) {
      return true;
    }
    int i = this.m_args.length;
    for (int j = 0; j < i; j++) {
      if (this.m_args[j].canTraverseOutsideSubtree()) {
        return true;
      }
    }
    return false;
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor)
  {
    super.callArgVisitors(paramXPathVisitor);
    if (null != this.m_args)
    {
      int i = this.m_args.length;
      for (int j = 0; j < i; j++) {
        this.m_args[j].callVisitors(new ArgMultiOwner(j), paramXPathVisitor);
      }
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    FunctionMultiArgs localFunctionMultiArgs = (FunctionMultiArgs)paramExpression;
    if (null != this.m_args)
    {
      int i = this.m_args.length;
      if ((null == localFunctionMultiArgs) || (localFunctionMultiArgs.m_args.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!this.m_args[j].deepEquals(localFunctionMultiArgs.m_args[j])) {
          return false;
        }
      }
    }
    else if (null != localFunctionMultiArgs.m_args)
    {
      return false;
    }
    return true;
  }
  
  class ArgMultiOwner
    implements ExpressionOwner
  {
    int m_argIndex;
    
    ArgMultiOwner(int paramInt)
    {
      this.m_argIndex = paramInt;
    }
    
    public Expression getExpression()
    {
      return FunctionMultiArgs.this.m_args[this.m_argIndex];
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(FunctionMultiArgs.this);
      FunctionMultiArgs.this.m_args[this.m_argIndex] = paramExpression;
    }
  }
}
