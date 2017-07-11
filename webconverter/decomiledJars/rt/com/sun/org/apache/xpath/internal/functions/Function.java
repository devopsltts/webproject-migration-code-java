package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.PrintStream;
import javax.xml.transform.TransformerException;

public abstract class Function
  extends Expression
{
  static final long serialVersionUID = 6927661240854599768L;
  
  public Function() {}
  
  public void setArg(Expression paramExpression, int paramInt)
    throws WrongNumberArgsException
  {
    reportWrongNumberArgs();
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt != 0) {
      reportWrongNumberArgs();
    }
  }
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("zero", null));
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    System.out.println("Error! Function.execute should not be called!");
    return null;
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor) {}
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if (paramXPathVisitor.visitFunction(paramExpressionOwner, this)) {
      callArgVisitors(paramXPathVisitor);
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    return isSameClass(paramExpression);
  }
  
  public void postCompileStep(Compiler paramCompiler) {}
}
