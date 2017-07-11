package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.util.Iterator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

final class Include
  extends TopLevelElement
{
  private Stylesheet _included = null;
  
  Include() {}
  
  public Stylesheet getIncludedStylesheet()
  {
    return this._included;
  }
  
  public void parseContents(Parser paramParser)
  {
    XSLTC localXSLTC = paramParser.getXSLTC();
    Stylesheet localStylesheet1 = paramParser.getCurrentStylesheet();
    String str1 = getAttribute("href");
    try
    {
      if (localStylesheet1.checkForLoop(str1))
      {
        localObject1 = new ErrorMsg("CIRCULAR_INCLUDE_ERR", str1, this);
        paramParser.reportError(2, (ErrorMsg)localObject1);
        return;
      }
      Object localObject1 = null;
      XMLReader localXMLReader = null;
      String str2 = localStylesheet1.getSystemId();
      SourceLoader localSourceLoader = localStylesheet1.getSourceLoader();
      if (localSourceLoader != null)
      {
        localObject1 = localSourceLoader.loadSource(str1, str2, localXSLTC);
        if (localObject1 != null)
        {
          str1 = ((InputSource)localObject1).getSystemId();
          localXMLReader = localXSLTC.getXMLReader();
        }
        else if (paramParser.errorsFound())
        {
          return;
        }
      }
      Object localObject2;
      if (localObject1 == null)
      {
        str1 = SystemIDResolver.getAbsoluteURI(str1, str2);
        localObject2 = SecuritySupport.checkAccess(str1, (String)localXSLTC.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
        if (localObject2 != null)
        {
          ErrorMsg localErrorMsg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(str1), localObject2, this);
          paramParser.reportError(2, localErrorMsg);
          return;
        }
        localObject1 = new InputSource(str1);
      }
      if (localObject1 == null)
      {
        localObject2 = new ErrorMsg("FILE_NOT_FOUND_ERR", str1, this);
        paramParser.reportError(2, (ErrorMsg)localObject2);
        return;
      }
      if (localXMLReader != null) {
        localObject2 = paramParser.parse(localXMLReader, (InputSource)localObject1);
      } else {
        localObject2 = paramParser.parse((InputSource)localObject1);
      }
      if (localObject2 == null) {
        return;
      }
      this._included = paramParser.makeStylesheet((SyntaxTreeNode)localObject2);
      if (this._included == null) {
        return;
      }
      this._included.setSourceLoader(localSourceLoader);
      this._included.setSystemId(str1);
      this._included.setParentStylesheet(localStylesheet1);
      this._included.setIncludingStylesheet(localStylesheet1);
      this._included.setTemplateInlining(localStylesheet1.getTemplateInlining());
      int i = localStylesheet1.getImportPrecedence();
      this._included.setImportPrecedence(i);
      paramParser.setCurrentStylesheet(this._included);
      this._included.parseContents(paramParser);
      Iterator localIterator = this._included.elements();
      Stylesheet localStylesheet2 = paramParser.getTopLevelStylesheet();
      while (localIterator.hasNext())
      {
        SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
        if ((localSyntaxTreeNode instanceof TopLevelElement)) {
          if ((localSyntaxTreeNode instanceof Variable)) {
            localStylesheet2.addVariable((Variable)localSyntaxTreeNode);
          } else if ((localSyntaxTreeNode instanceof Param)) {
            localStylesheet2.addParam((Param)localSyntaxTreeNode);
          } else {
            localStylesheet2.addElement((TopLevelElement)localSyntaxTreeNode);
          }
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    finally
    {
      paramParser.setCurrentStylesheet(localStylesheet1);
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
}
