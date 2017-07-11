package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.SAXSourceLocator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.compiler.XPathParser;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

public class XPath
  implements Serializable, ExpressionOwner
{
  static final long serialVersionUID = 3976493477939110553L;
  private Expression m_mainExp;
  private transient FunctionTable m_funcTable = null;
  String m_patternString;
  public static final int SELECT = 0;
  public static final int MATCH = 1;
  private static final boolean DEBUG_MATCHES = false;
  public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;
  public static final double MATCH_SCORE_QNAME = 0.0D;
  public static final double MATCH_SCORE_NSWILD = -0.25D;
  public static final double MATCH_SCORE_NODETEST = -0.5D;
  public static final double MATCH_SCORE_OTHER = 0.5D;
  
  private void initFunctionTable()
  {
    this.m_funcTable = new FunctionTable();
  }
  
  public Expression getExpression()
  {
    return this.m_mainExp;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    this.m_mainExp.fixupVariables(paramVector, paramInt);
  }
  
  public void setExpression(Expression paramExpression)
  {
    if (null != this.m_mainExp) {
      paramExpression.exprSetParent(this.m_mainExp.exprGetParent());
    }
    this.m_mainExp = paramExpression;
  }
  
  public SourceLocator getLocator()
  {
    return this.m_mainExp;
  }
  
  public String getPatternString()
  {
    return this.m_patternString;
  }
  
  public XPath(String paramString, SourceLocator paramSourceLocator, PrefixResolver paramPrefixResolver, int paramInt, ErrorListener paramErrorListener)
    throws TransformerException
  {
    initFunctionTable();
    if (null == paramErrorListener) {
      paramErrorListener = new DefaultErrorHandler();
    }
    this.m_patternString = paramString;
    XPathParser localXPathParser = new XPathParser(paramErrorListener, paramSourceLocator);
    Compiler localCompiler = new Compiler(paramErrorListener, paramSourceLocator, this.m_funcTable);
    if (0 == paramInt) {
      localXPathParser.initXPath(localCompiler, paramString, paramPrefixResolver);
    } else if (1 == paramInt) {
      localXPathParser.initMatchPattern(localCompiler, paramString, paramPrefixResolver);
    } else {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_CANNOT_DEAL_XPATH_TYPE", new Object[] { Integer.toString(paramInt) }));
    }
    Expression localExpression = localCompiler.compile(0);
    setExpression(localExpression);
    if ((null != paramSourceLocator) && ((paramSourceLocator instanceof ExpressionNode))) {
      localExpression.exprSetParent((ExpressionNode)paramSourceLocator);
    }
  }
  
  public XPath(String paramString, SourceLocator paramSourceLocator, PrefixResolver paramPrefixResolver, int paramInt, ErrorListener paramErrorListener, FunctionTable paramFunctionTable)
    throws TransformerException
  {
    this.m_funcTable = paramFunctionTable;
    if (null == paramErrorListener) {
      paramErrorListener = new DefaultErrorHandler();
    }
    this.m_patternString = paramString;
    XPathParser localXPathParser = new XPathParser(paramErrorListener, paramSourceLocator);
    Compiler localCompiler = new Compiler(paramErrorListener, paramSourceLocator, this.m_funcTable);
    if (0 == paramInt) {
      localXPathParser.initXPath(localCompiler, paramString, paramPrefixResolver);
    } else if (1 == paramInt) {
      localXPathParser.initMatchPattern(localCompiler, paramString, paramPrefixResolver);
    } else {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_CANNOT_DEAL_XPATH_TYPE", new Object[] { Integer.toString(paramInt) }));
    }
    Expression localExpression = localCompiler.compile(0);
    setExpression(localExpression);
    if ((null != paramSourceLocator) && ((paramSourceLocator instanceof ExpressionNode))) {
      localExpression.exprSetParent((ExpressionNode)paramSourceLocator);
    }
  }
  
  public XPath(String paramString, SourceLocator paramSourceLocator, PrefixResolver paramPrefixResolver, int paramInt)
    throws TransformerException
  {
    this(paramString, paramSourceLocator, paramPrefixResolver, paramInt, null);
  }
  
  public XPath(Expression paramExpression)
  {
    setExpression(paramExpression);
    initFunctionTable();
  }
  
  public XObject execute(XPathContext paramXPathContext, Node paramNode, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    return execute(paramXPathContext, paramXPathContext.getDTMHandleFromNode(paramNode), paramPrefixResolver);
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    paramXPathContext.pushNamespaceContext(paramPrefixResolver);
    paramXPathContext.pushCurrentNodeAndExpression(paramInt, paramInt);
    XObject localXObject = null;
    try
    {
      localXObject = this.m_mainExp.execute(paramXPathContext);
    }
    catch (TransformerException localTransformerException1)
    {
      localTransformerException1.setLocator(getLocator());
      localObject1 = paramXPathContext.getErrorListener();
      if (null != localObject1) {
        ((ErrorListener)localObject1).error(localTransformerException1);
      } else {
        throw localTransformerException1;
      }
    }
    catch (Exception localException1)
    {
      Exception localException2;
      while ((localException1 instanceof WrappedRuntimeException)) {
        localException2 = ((WrappedRuntimeException)localException1).getException();
      }
      Object localObject1 = localException2.getMessage();
      if ((localObject1 == null) || (((String)localObject1).length() == 0)) {
        localObject1 = XSLMessages.createXPATHMessage("ER_XPATH_ERROR", null);
      }
      TransformerException localTransformerException2 = new TransformerException((String)localObject1, getLocator(), localException2);
      ErrorListener localErrorListener = paramXPathContext.getErrorListener();
      if (null != localErrorListener) {
        localErrorListener.fatalError(localTransformerException2);
      } else {
        throw localTransformerException2;
      }
    }
    finally
    {
      paramXPathContext.popNamespaceContext();
      paramXPathContext.popCurrentNodeAndExpression();
    }
    return localXObject;
  }
  
  public boolean bool(XPathContext paramXPathContext, int paramInt, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    paramXPathContext.pushNamespaceContext(paramPrefixResolver);
    paramXPathContext.pushCurrentNodeAndExpression(paramInt, paramInt);
    try
    {
      boolean bool = this.m_mainExp.bool(paramXPathContext);
      return bool;
    }
    catch (TransformerException localTransformerException1)
    {
      localTransformerException1.setLocator(getLocator());
      localObject1 = paramXPathContext.getErrorListener();
      if (null != localObject1) {
        ((ErrorListener)localObject1).error(localTransformerException1);
      } else {
        throw localTransformerException1;
      }
    }
    catch (Exception localException1)
    {
      Exception localException2;
      while ((localException1 instanceof WrappedRuntimeException)) {
        localException2 = ((WrappedRuntimeException)localException1).getException();
      }
      Object localObject1 = localException2.getMessage();
      if ((localObject1 == null) || (((String)localObject1).length() == 0)) {
        localObject1 = XSLMessages.createXPATHMessage("ER_XPATH_ERROR", null);
      }
      TransformerException localTransformerException2 = new TransformerException((String)localObject1, getLocator(), localException2);
      ErrorListener localErrorListener = paramXPathContext.getErrorListener();
      if (null != localErrorListener) {
        localErrorListener.fatalError(localTransformerException2);
      } else {
        throw localTransformerException2;
      }
    }
    finally
    {
      paramXPathContext.popNamespaceContext();
      paramXPathContext.popCurrentNodeAndExpression();
    }
    return false;
  }
  
  public double getMatchScore(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    paramXPathContext.pushCurrentNode(paramInt);
    paramXPathContext.pushCurrentExpressionNode(paramInt);
    try
    {
      XObject localXObject = this.m_mainExp.execute(paramXPathContext);
      double d = localXObject.num();
      return d;
    }
    finally
    {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popCurrentExpressionNode();
    }
  }
  
  public void warn(XPathContext paramXPathContext, int paramInt, String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    ErrorListener localErrorListener = paramXPathContext.getErrorListener();
    if (null != localErrorListener) {
      localErrorListener.warning(new TransformerException(str, (SAXSourceLocator)paramXPathContext.getSAXLocator()));
    }
  }
  
  public void assertion(boolean paramBoolean, String paramString)
  {
    if (!paramBoolean)
    {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    }
  }
  
  public void error(XPathContext paramXPathContext, int paramInt, String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    ErrorListener localErrorListener = paramXPathContext.getErrorListener();
    if (null != localErrorListener)
    {
      localErrorListener.fatalError(new TransformerException(str, (SAXSourceLocator)paramXPathContext.getSAXLocator()));
    }
    else
    {
      SourceLocator localSourceLocator = paramXPathContext.getSAXLocator();
      System.out.println(str + "; file " + localSourceLocator.getSystemId() + "; line " + localSourceLocator.getLineNumber() + "; column " + localSourceLocator.getColumnNumber());
    }
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    this.m_mainExp.callVisitors(this, paramXPathVisitor);
  }
}
