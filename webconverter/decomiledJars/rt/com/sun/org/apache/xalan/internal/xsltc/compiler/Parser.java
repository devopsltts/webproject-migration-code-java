package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.java_cup.internal.runtime.Symbol;
import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

public class Parser
  implements Constants, ContentHandler
{
  private static final String XSL = "xsl";
  private static final String TRANSLET = "translet";
  private Locator _locator = null;
  private XSLTC _xsltc;
  private XPathParser _xpathParser;
  private Vector _errors;
  private Vector _warnings;
  private Map<String, String> _instructionClasses;
  private Map<String, String[]> _instructionAttrs;
  private Map<String, QName> _qNames;
  private Map<String, Map> _namespaces;
  private QName _useAttributeSets;
  private QName _excludeResultPrefixes;
  private QName _extensionElementPrefixes;
  private Map<String, Object> _variableScope;
  private Stylesheet _currentStylesheet;
  private SymbolTable _symbolTable;
  private Output _output;
  private Template _template;
  private boolean _rootNamespaceDef;
  private SyntaxTreeNode _root;
  private String _target;
  private int _currentImportPrecedence;
  private boolean _useServicesMechanism = true;
  private String _PImedia = null;
  private String _PItitle = null;
  private String _PIcharset = null;
  private int _templateIndex = 0;
  private boolean versionIsOne = true;
  private Stack _parentStack = null;
  private Map<String, String> _prefixMapping = null;
  
  public Parser(XSLTC paramXSLTC, boolean paramBoolean)
  {
    this._xsltc = paramXSLTC;
    this._useServicesMechanism = paramBoolean;
  }
  
  public void init()
  {
    this._qNames = new HashMap(512);
    this._namespaces = new HashMap();
    this._instructionClasses = new HashMap();
    this._instructionAttrs = new HashMap();
    this._variableScope = new HashMap();
    this._template = null;
    this._errors = new Vector();
    this._warnings = new Vector();
    this._symbolTable = new SymbolTable();
    this._xpathParser = new XPathParser(this);
    this._currentStylesheet = null;
    this._output = null;
    this._root = null;
    this._rootNamespaceDef = false;
    this._currentImportPrecedence = 1;
    initStdClasses();
    initInstructionAttrs();
    initExtClasses();
    initSymbolTable();
    this._useAttributeSets = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "use-attribute-sets");
    this._excludeResultPrefixes = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "exclude-result-prefixes");
    this._extensionElementPrefixes = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "extension-element-prefixes");
  }
  
  public void setOutput(Output paramOutput)
  {
    if (this._output != null)
    {
      if (this._output.getImportPrecedence() <= paramOutput.getImportPrecedence())
      {
        String str = this._output.getCdata();
        paramOutput.mergeOutput(this._output);
        this._output.disable();
        this._output = paramOutput;
      }
      else
      {
        paramOutput.disable();
      }
    }
    else {
      this._output = paramOutput;
    }
  }
  
  public Output getOutput()
  {
    return this._output;
  }
  
  public Properties getOutputProperties()
  {
    return getTopLevelStylesheet().getOutputProperties();
  }
  
  public void addVariable(Variable paramVariable)
  {
    addVariableOrParam(paramVariable);
  }
  
  public void addParameter(Param paramParam)
  {
    addVariableOrParam(paramParam);
  }
  
  private void addVariableOrParam(VariableBase paramVariableBase)
  {
    Object localObject = this._variableScope.get(paramVariableBase.getName().getStringRep());
    if (localObject != null)
    {
      Stack localStack;
      if ((localObject instanceof Stack))
      {
        localStack = (Stack)localObject;
        localStack.push(paramVariableBase);
      }
      else if ((localObject instanceof VariableBase))
      {
        localStack = new Stack();
        localStack.push(localObject);
        localStack.push(paramVariableBase);
        this._variableScope.put(paramVariableBase.getName().getStringRep(), localStack);
      }
    }
    else
    {
      this._variableScope.put(paramVariableBase.getName().getStringRep(), paramVariableBase);
    }
  }
  
  public void removeVariable(QName paramQName)
  {
    Object localObject = this._variableScope.get(paramQName.getStringRep());
    if ((localObject instanceof Stack))
    {
      Stack localStack = (Stack)localObject;
      if (!localStack.isEmpty()) {
        localStack.pop();
      }
      if (!localStack.isEmpty()) {
        return;
      }
    }
    this._variableScope.remove(paramQName.getStringRep());
  }
  
  public VariableBase lookupVariable(QName paramQName)
  {
    Object localObject = this._variableScope.get(paramQName.getStringRep());
    if ((localObject instanceof VariableBase)) {
      return (VariableBase)localObject;
    }
    if ((localObject instanceof Stack))
    {
      Stack localStack = (Stack)localObject;
      return (VariableBase)localStack.peek();
    }
    return null;
  }
  
  public void setXSLTC(XSLTC paramXSLTC)
  {
    this._xsltc = paramXSLTC;
  }
  
  public XSLTC getXSLTC()
  {
    return this._xsltc;
  }
  
  public int getCurrentImportPrecedence()
  {
    return this._currentImportPrecedence;
  }
  
  public int getNextImportPrecedence()
  {
    return ++this._currentImportPrecedence;
  }
  
  public void setCurrentStylesheet(Stylesheet paramStylesheet)
  {
    this._currentStylesheet = paramStylesheet;
  }
  
  public Stylesheet getCurrentStylesheet()
  {
    return this._currentStylesheet;
  }
  
  public Stylesheet getTopLevelStylesheet()
  {
    return this._xsltc.getStylesheet();
  }
  
  public QName getQNameSafe(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    if (i != -1)
    {
      str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      String str3 = null;
      if (!str1.equals("xmlns"))
      {
        str3 = this._symbolTable.lookupNamespace(str1);
        if (str3 == null) {
          str3 = "";
        }
      }
      return getQName(str3, str1, str2);
    }
    String str1 = paramString.equals("xmlns") ? null : this._symbolTable.lookupNamespace("");
    return getQName(str1, null, paramString);
  }
  
  public QName getQName(String paramString)
  {
    return getQName(paramString, true, false);
  }
  
  public QName getQNameIgnoreDefaultNs(String paramString)
  {
    return getQName(paramString, true, true);
  }
  
  public QName getQName(String paramString, boolean paramBoolean)
  {
    return getQName(paramString, paramBoolean, false);
  }
  
  private QName getQName(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramString.lastIndexOf(':');
    if (i != -1)
    {
      str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      String str3 = null;
      if (!str1.equals("xmlns"))
      {
        str3 = this._symbolTable.lookupNamespace(str1);
        if ((str3 == null) && (paramBoolean1))
        {
          int j = getLineNumber();
          ErrorMsg localErrorMsg = new ErrorMsg("NAMESPACE_UNDEF_ERR", j, str1);
          reportError(3, localErrorMsg);
        }
      }
      return getQName(str3, str1, str2);
    }
    if (paramString.equals("xmlns")) {
      paramBoolean2 = true;
    }
    String str1 = paramBoolean2 ? null : this._symbolTable.lookupNamespace("");
    return getQName(str1, null, paramString);
  }
  
  public QName getQName(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 == null) || (paramString1.equals("")))
    {
      localObject = (QName)this._qNames.get(paramString3);
      if (localObject == null)
      {
        localObject = new QName(null, paramString2, paramString3);
        this._qNames.put(paramString3, localObject);
      }
      return localObject;
    }
    Object localObject = (Map)this._namespaces.get(paramString1);
    String str = paramString2 + ':' + paramString3;
    if (localObject == null)
    {
      localQName = new QName(paramString1, paramString2, paramString3);
      this._namespaces.put(paramString1, localObject = new HashMap());
      ((Map)localObject).put(str, localQName);
      return localQName;
    }
    QName localQName = (QName)((Map)localObject).get(str);
    if (localQName == null)
    {
      localQName = new QName(paramString1, paramString2, paramString3);
      ((Map)localObject).put(str, localQName);
    }
    return localQName;
  }
  
  public QName getQName(String paramString1, String paramString2)
  {
    return getQName(paramString1 + paramString2);
  }
  
  public QName getQName(QName paramQName1, QName paramQName2)
  {
    return getQName(paramQName1.toString() + paramQName2.toString());
  }
  
  public QName getUseAttributeSets()
  {
    return this._useAttributeSets;
  }
  
  public QName getExtensionElementPrefixes()
  {
    return this._extensionElementPrefixes;
  }
  
  public QName getExcludeResultPrefixes()
  {
    return this._excludeResultPrefixes;
  }
  
  public Stylesheet makeStylesheet(SyntaxTreeNode paramSyntaxTreeNode)
    throws CompilerException
  {
    try
    {
      Stylesheet localStylesheet;
      if ((paramSyntaxTreeNode instanceof Stylesheet))
      {
        localStylesheet = (Stylesheet)paramSyntaxTreeNode;
      }
      else
      {
        localStylesheet = new Stylesheet();
        localStylesheet.setSimplified();
        localStylesheet.addElement(paramSyntaxTreeNode);
        localStylesheet.setAttributes((AttributesImpl)paramSyntaxTreeNode.getAttributes());
        if (paramSyntaxTreeNode.lookupNamespace("") == null) {
          paramSyntaxTreeNode.addPrefixMapping("", "");
        }
      }
      localStylesheet.setParser(this);
      return localStylesheet;
    }
    catch (ClassCastException localClassCastException)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("NOT_STYLESHEET_ERR", paramSyntaxTreeNode);
      throw new CompilerException(localErrorMsg.toString());
    }
  }
  
  public void createAST(Stylesheet paramStylesheet)
  {
    try
    {
      if (paramStylesheet != null)
      {
        paramStylesheet.parseContents(this);
        int i = paramStylesheet.getImportPrecedence();
        Iterator localIterator = paramStylesheet.elements();
        while (localIterator.hasNext())
        {
          SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
          if ((localSyntaxTreeNode instanceof Text))
          {
            int j = getLineNumber();
            ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_TEXT_NODE_ERR", j, null);
            reportError(3, localErrorMsg);
          }
        }
        if (!errorsFound()) {
          paramStylesheet.typeCheck(this._symbolTable);
        }
      }
    }
    catch (TypeCheckError localTypeCheckError)
    {
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", localTypeCheckError));
    }
  }
  
  public SyntaxTreeNode parse(XMLReader paramXMLReader, InputSource paramInputSource)
  {
    try
    {
      paramXMLReader.setContentHandler(this);
      paramXMLReader.parse(paramInputSource);
      return getStylesheet(this._root);
    }
    catch (IOException localIOException)
    {
      if (this._xsltc.debug()) {
        localIOException.printStackTrace();
      }
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", localIOException));
    }
    catch (SAXException localSAXException)
    {
      Exception localException2 = localSAXException.getException();
      if (this._xsltc.debug())
      {
        localSAXException.printStackTrace();
        if (localException2 != null) {
          localException2.printStackTrace();
        }
      }
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", localSAXException));
    }
    catch (CompilerException localCompilerException)
    {
      if (this._xsltc.debug()) {
        localCompilerException.printStackTrace();
      }
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", localCompilerException));
    }
    catch (Exception localException1)
    {
      if (this._xsltc.debug()) {
        localException1.printStackTrace();
      }
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", localException1));
    }
    return null;
  }
  
  public SyntaxTreeNode parse(InputSource paramInputSource)
  {
    try
    {
      SAXParserFactory localSAXParserFactory = FactoryImpl.getSAXFactory(this._useServicesMechanism);
      if (this._xsltc.isSecureProcessing()) {
        try
        {
          localSAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (SAXException localSAXException2) {}
      }
      try
      {
        localSAXParserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
      }
      catch (Exception localException)
      {
        localSAXParserFactory.setNamespaceAware(true);
      }
      localObject1 = localSAXParserFactory.newSAXParser();
      Object localObject2;
      try
      {
        ((SAXParser)localObject1).setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"));
      }
      catch (SAXNotRecognizedException localSAXNotRecognizedException)
      {
        localObject2 = new ErrorMsg("WARNING_MSG", localObject1.getClass().getName() + ": " + localSAXNotRecognizedException.getMessage());
        reportError(4, (ErrorMsg)localObject2);
      }
      XMLReader localXMLReader = ((SAXParser)localObject1).getXMLReader();
      try
      {
        localObject2 = (XMLSecurityManager)this._xsltc.getProperty("http://apache.org/xml/properties/security-manager");
        for (XMLSecurityManager.Limit localLimit : XMLSecurityManager.Limit.values()) {
          localXMLReader.setProperty(localLimit.apiProperty(), ((XMLSecurityManager)localObject2).getLimitValueAsString(localLimit));
        }
        if (((XMLSecurityManager)localObject2).printEntityCountInfo()) {
          ((SAXParser)localObject1).setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
        }
      }
      catch (SAXException localSAXException3)
      {
        System.err.println("Warning:  " + localXMLReader.getClass().getName() + ": " + localSAXException3.getMessage());
      }
      return parse(localXMLReader, paramInputSource);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      Object localObject1 = new ErrorMsg("SAX_PARSER_CONFIG_ERR");
      reportError(3, (ErrorMsg)localObject1);
    }
    catch (SAXParseException localSAXParseException)
    {
      reportError(3, new ErrorMsg(localSAXParseException.getMessage(), localSAXParseException.getLineNumber()));
    }
    catch (SAXException localSAXException1)
    {
      reportError(3, new ErrorMsg(localSAXException1.getMessage()));
    }
    return null;
  }
  
  public SyntaxTreeNode getDocumentRoot()
  {
    return this._root;
  }
  
  protected void setPIParameters(String paramString1, String paramString2, String paramString3)
  {
    this._PImedia = paramString1;
    this._PItitle = paramString2;
    this._PIcharset = paramString3;
  }
  
  private SyntaxTreeNode getStylesheet(SyntaxTreeNode paramSyntaxTreeNode)
    throws CompilerException
  {
    Object localObject1;
    if (this._target == null)
    {
      if (!this._rootNamespaceDef)
      {
        localObject1 = new ErrorMsg("MISSING_XSLT_URI_ERR");
        throw new CompilerException(((ErrorMsg)localObject1).toString());
      }
      return paramSyntaxTreeNode;
    }
    Object localObject2;
    if (this._target.charAt(0) == '#')
    {
      localObject1 = findStylesheet(paramSyntaxTreeNode, this._target.substring(1));
      if (localObject1 == null)
      {
        localObject2 = new ErrorMsg("MISSING_XSLT_TARGET_ERR", this._target, paramSyntaxTreeNode);
        throw new CompilerException(((ErrorMsg)localObject2).toString());
      }
      return localObject1;
    }
    try
    {
      localObject1 = this._target;
      if (((String)localObject1).indexOf(":") == -1) {
        localObject1 = "file:" + (String)localObject1;
      }
      localObject1 = SystemIDResolver.getAbsoluteURI((String)localObject1);
      localObject2 = SecuritySupport.checkAccess((String)localObject1, (String)this._xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
      if (localObject2 != null)
      {
        ErrorMsg localErrorMsg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(this._target), localObject2, paramSyntaxTreeNode);
        throw new CompilerException(localErrorMsg.toString());
      }
    }
    catch (IOException localIOException)
    {
      throw new CompilerException(localIOException);
    }
    return loadExternalStylesheet(this._target);
  }
  
  private SyntaxTreeNode findStylesheet(SyntaxTreeNode paramSyntaxTreeNode, String paramString)
  {
    if (paramSyntaxTreeNode == null) {
      return null;
    }
    if ((paramSyntaxTreeNode instanceof Stylesheet))
    {
      localObject = paramSyntaxTreeNode.getAttribute("id");
      if (((String)localObject).equals(paramString)) {
        return paramSyntaxTreeNode;
      }
    }
    Object localObject = paramSyntaxTreeNode.getContents();
    if (localObject != null)
    {
      int i = ((List)localObject).size();
      for (int j = 0; j < i; j++)
      {
        SyntaxTreeNode localSyntaxTreeNode1 = (SyntaxTreeNode)((List)localObject).get(j);
        SyntaxTreeNode localSyntaxTreeNode2 = findStylesheet(localSyntaxTreeNode1, paramString);
        if (localSyntaxTreeNode2 != null) {
          return localSyntaxTreeNode2;
        }
      }
    }
    return null;
  }
  
  private SyntaxTreeNode loadExternalStylesheet(String paramString)
    throws CompilerException
  {
    InputSource localInputSource;
    if (new File(paramString).exists()) {
      localInputSource = new InputSource("file:" + paramString);
    } else {
      localInputSource = new InputSource(paramString);
    }
    SyntaxTreeNode localSyntaxTreeNode = parse(localInputSource);
    return localSyntaxTreeNode;
  }
  
  private void initAttrTable(String paramString, String[] paramArrayOfString)
  {
    this._instructionAttrs.put(getQName("http://www.w3.org/1999/XSL/Transform", "xsl", paramString).getStringRep(), paramArrayOfString);
  }
  
  private void initInstructionAttrs()
  {
    initAttrTable("template", new String[] { "match", "name", "priority", "mode" });
    initAttrTable("stylesheet", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
    initAttrTable("transform", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
    initAttrTable("text", new String[] { "disable-output-escaping" });
    initAttrTable("if", new String[] { "test" });
    initAttrTable("choose", new String[0]);
    initAttrTable("when", new String[] { "test" });
    initAttrTable("otherwise", new String[0]);
    initAttrTable("for-each", new String[] { "select" });
    initAttrTable("message", new String[] { "terminate" });
    initAttrTable("number", new String[] { "level", "count", "from", "value", "format", "lang", "letter-value", "grouping-separator", "grouping-size" });
    initAttrTable("comment", new String[0]);
    initAttrTable("copy", new String[] { "use-attribute-sets" });
    initAttrTable("copy-of", new String[] { "select" });
    initAttrTable("param", new String[] { "name", "select" });
    initAttrTable("with-param", new String[] { "name", "select" });
    initAttrTable("variable", new String[] { "name", "select" });
    initAttrTable("output", new String[] { "method", "version", "encoding", "omit-xml-declaration", "standalone", "doctype-public", "doctype-system", "cdata-section-elements", "indent", "media-type" });
    initAttrTable("sort", new String[] { "select", "order", "case-order", "lang", "data-type" });
    initAttrTable("key", new String[] { "name", "match", "use" });
    initAttrTable("fallback", new String[0]);
    initAttrTable("attribute", new String[] { "name", "namespace" });
    initAttrTable("attribute-set", new String[] { "name", "use-attribute-sets" });
    initAttrTable("value-of", new String[] { "select", "disable-output-escaping" });
    initAttrTable("element", new String[] { "name", "namespace", "use-attribute-sets" });
    initAttrTable("call-template", new String[] { "name" });
    initAttrTable("apply-templates", new String[] { "select", "mode" });
    initAttrTable("apply-imports", new String[0]);
    initAttrTable("decimal-format", new String[] { "name", "decimal-separator", "grouping-separator", "infinity", "minus-sign", "NaN", "percent", "per-mille", "zero-digit", "digit", "pattern-separator" });
    initAttrTable("import", new String[] { "href" });
    initAttrTable("include", new String[] { "href" });
    initAttrTable("strip-space", new String[] { "elements" });
    initAttrTable("preserve-space", new String[] { "elements" });
    initAttrTable("processing-instruction", new String[] { "name" });
    initAttrTable("namespace-alias", new String[] { "stylesheet-prefix", "result-prefix" });
  }
  
  private void initStdClasses()
  {
    initStdClass("template", "Template");
    initStdClass("stylesheet", "Stylesheet");
    initStdClass("transform", "Stylesheet");
    initStdClass("text", "Text");
    initStdClass("if", "If");
    initStdClass("choose", "Choose");
    initStdClass("when", "When");
    initStdClass("otherwise", "Otherwise");
    initStdClass("for-each", "ForEach");
    initStdClass("message", "Message");
    initStdClass("number", "Number");
    initStdClass("comment", "Comment");
    initStdClass("copy", "Copy");
    initStdClass("copy-of", "CopyOf");
    initStdClass("param", "Param");
    initStdClass("with-param", "WithParam");
    initStdClass("variable", "Variable");
    initStdClass("output", "Output");
    initStdClass("sort", "Sort");
    initStdClass("key", "Key");
    initStdClass("fallback", "Fallback");
    initStdClass("attribute", "XslAttribute");
    initStdClass("attribute-set", "AttributeSet");
    initStdClass("value-of", "ValueOf");
    initStdClass("element", "XslElement");
    initStdClass("call-template", "CallTemplate");
    initStdClass("apply-templates", "ApplyTemplates");
    initStdClass("apply-imports", "ApplyImports");
    initStdClass("decimal-format", "DecimalFormatting");
    initStdClass("import", "Import");
    initStdClass("include", "Include");
    initStdClass("strip-space", "Whitespace");
    initStdClass("preserve-space", "Whitespace");
    initStdClass("processing-instruction", "ProcessingInstruction");
    initStdClass("namespace-alias", "NamespaceAlias");
  }
  
  private void initStdClass(String paramString1, String paramString2)
  {
    this._instructionClasses.put(getQName("http://www.w3.org/1999/XSL/Transform", "xsl", paramString1).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + paramString2);
  }
  
  public boolean elementSupported(String paramString1, String paramString2)
  {
    return this._instructionClasses.get(getQName(paramString1, "xsl", paramString2).getStringRep()) != null;
  }
  
  public boolean functionSupported(String paramString)
  {
    return this._symbolTable.lookupPrimop(paramString) != null;
  }
  
  private void initExtClasses()
  {
    initExtClass("output", "TransletOutput");
    initExtClass("http://xml.apache.org/xalan/redirect", "write", "TransletOutput");
  }
  
  private void initExtClass(String paramString1, String paramString2)
  {
    this._instructionClasses.put(getQName("http://xml.apache.org/xalan/xsltc", "translet", paramString1).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + paramString2);
  }
  
  private void initExtClass(String paramString1, String paramString2, String paramString3)
  {
    this._instructionClasses.put(getQName(paramString1, "translet", paramString2).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + paramString3);
  }
  
  private void initSymbolTable()
  {
    MethodType localMethodType1 = new MethodType(Type.Int, Type.Void);
    MethodType localMethodType2 = new MethodType(Type.Int, Type.Real);
    MethodType localMethodType3 = new MethodType(Type.Int, Type.String);
    MethodType localMethodType4 = new MethodType(Type.Int, Type.NodeSet);
    MethodType localMethodType5 = new MethodType(Type.Real, Type.Int);
    MethodType localMethodType6 = new MethodType(Type.Real, Type.Void);
    MethodType localMethodType7 = new MethodType(Type.Real, Type.Real);
    MethodType localMethodType8 = new MethodType(Type.Real, Type.NodeSet);
    MethodType localMethodType9 = new MethodType(Type.Real, Type.Reference);
    MethodType localMethodType10 = new MethodType(Type.Int, Type.Int);
    MethodType localMethodType11 = new MethodType(Type.NodeSet, Type.Reference);
    MethodType localMethodType12 = new MethodType(Type.NodeSet, Type.Void);
    MethodType localMethodType13 = new MethodType(Type.NodeSet, Type.String);
    MethodType localMethodType14 = new MethodType(Type.NodeSet, Type.NodeSet);
    MethodType localMethodType15 = new MethodType(Type.Node, Type.Void);
    MethodType localMethodType16 = new MethodType(Type.String, Type.Void);
    MethodType localMethodType17 = new MethodType(Type.String, Type.String);
    MethodType localMethodType18 = new MethodType(Type.String, Type.Node);
    MethodType localMethodType19 = new MethodType(Type.String, Type.NodeSet);
    MethodType localMethodType20 = new MethodType(Type.String, Type.Reference);
    MethodType localMethodType21 = new MethodType(Type.Boolean, Type.Reference);
    MethodType localMethodType22 = new MethodType(Type.Boolean, Type.Void);
    MethodType localMethodType23 = new MethodType(Type.Boolean, Type.Boolean);
    MethodType localMethodType24 = new MethodType(Type.Boolean, Type.String);
    MethodType localMethodType25 = new MethodType(Type.NodeSet, Type.Object);
    MethodType localMethodType26 = new MethodType(Type.Real, Type.Real, Type.Real);
    MethodType localMethodType27 = new MethodType(Type.Int, Type.Int, Type.Int);
    MethodType localMethodType28 = new MethodType(Type.Boolean, Type.Real, Type.Real);
    MethodType localMethodType29 = new MethodType(Type.Boolean, Type.Int, Type.Int);
    MethodType localMethodType30 = new MethodType(Type.String, Type.String, Type.String);
    MethodType localMethodType31 = new MethodType(Type.String, Type.Real, Type.String);
    MethodType localMethodType32 = new MethodType(Type.String, Type.String, Type.Real);
    MethodType localMethodType33 = new MethodType(Type.Reference, Type.String, Type.Reference);
    MethodType localMethodType34 = new MethodType(Type.NodeSet, Type.String, Type.String);
    MethodType localMethodType35 = new MethodType(Type.NodeSet, Type.String, Type.NodeSet);
    MethodType localMethodType36 = new MethodType(Type.Boolean, Type.Boolean, Type.Boolean);
    MethodType localMethodType37 = new MethodType(Type.Boolean, Type.String, Type.String);
    MethodType localMethodType38 = new MethodType(Type.String, Type.String, Type.NodeSet);
    MethodType localMethodType39 = new MethodType(Type.String, Type.Real, Type.String, Type.String);
    MethodType localMethodType40 = new MethodType(Type.String, Type.String, Type.Real, Type.Real);
    MethodType localMethodType41 = new MethodType(Type.String, Type.String, Type.String, Type.String);
    this._symbolTable.addPrimop("current", localMethodType15);
    this._symbolTable.addPrimop("last", localMethodType1);
    this._symbolTable.addPrimop("position", localMethodType1);
    this._symbolTable.addPrimop("true", localMethodType22);
    this._symbolTable.addPrimop("false", localMethodType22);
    this._symbolTable.addPrimop("not", localMethodType23);
    this._symbolTable.addPrimop("name", localMethodType16);
    this._symbolTable.addPrimop("name", localMethodType18);
    this._symbolTable.addPrimop("generate-id", localMethodType16);
    this._symbolTable.addPrimop("generate-id", localMethodType18);
    this._symbolTable.addPrimop("ceiling", localMethodType7);
    this._symbolTable.addPrimop("floor", localMethodType7);
    this._symbolTable.addPrimop("round", localMethodType7);
    this._symbolTable.addPrimop("contains", localMethodType37);
    this._symbolTable.addPrimop("number", localMethodType9);
    this._symbolTable.addPrimop("number", localMethodType6);
    this._symbolTable.addPrimop("boolean", localMethodType21);
    this._symbolTable.addPrimop("string", localMethodType20);
    this._symbolTable.addPrimop("string", localMethodType16);
    this._symbolTable.addPrimop("translate", localMethodType41);
    this._symbolTable.addPrimop("string-length", localMethodType1);
    this._symbolTable.addPrimop("string-length", localMethodType3);
    this._symbolTable.addPrimop("starts-with", localMethodType37);
    this._symbolTable.addPrimop("format-number", localMethodType31);
    this._symbolTable.addPrimop("format-number", localMethodType39);
    this._symbolTable.addPrimop("unparsed-entity-uri", localMethodType17);
    this._symbolTable.addPrimop("key", localMethodType34);
    this._symbolTable.addPrimop("key", localMethodType35);
    this._symbolTable.addPrimop("id", localMethodType13);
    this._symbolTable.addPrimop("id", localMethodType14);
    this._symbolTable.addPrimop("namespace-uri", localMethodType16);
    this._symbolTable.addPrimop("function-available", localMethodType24);
    this._symbolTable.addPrimop("element-available", localMethodType24);
    this._symbolTable.addPrimop("document", localMethodType13);
    this._symbolTable.addPrimop("document", localMethodType12);
    this._symbolTable.addPrimop("count", localMethodType4);
    this._symbolTable.addPrimop("sum", localMethodType8);
    this._symbolTable.addPrimop("local-name", localMethodType16);
    this._symbolTable.addPrimop("local-name", localMethodType19);
    this._symbolTable.addPrimop("namespace-uri", localMethodType16);
    this._symbolTable.addPrimop("namespace-uri", localMethodType19);
    this._symbolTable.addPrimop("substring", localMethodType32);
    this._symbolTable.addPrimop("substring", localMethodType40);
    this._symbolTable.addPrimop("substring-after", localMethodType30);
    this._symbolTable.addPrimop("substring-before", localMethodType30);
    this._symbolTable.addPrimop("normalize-space", localMethodType16);
    this._symbolTable.addPrimop("normalize-space", localMethodType17);
    this._symbolTable.addPrimop("system-property", localMethodType17);
    this._symbolTable.addPrimop("nodeset", localMethodType11);
    this._symbolTable.addPrimop("objectType", localMethodType20);
    this._symbolTable.addPrimop("cast", localMethodType33);
    this._symbolTable.addPrimop("+", localMethodType26);
    this._symbolTable.addPrimop("-", localMethodType26);
    this._symbolTable.addPrimop("*", localMethodType26);
    this._symbolTable.addPrimop("/", localMethodType26);
    this._symbolTable.addPrimop("%", localMethodType26);
    this._symbolTable.addPrimop("+", localMethodType27);
    this._symbolTable.addPrimop("-", localMethodType27);
    this._symbolTable.addPrimop("*", localMethodType27);
    this._symbolTable.addPrimop("<", localMethodType28);
    this._symbolTable.addPrimop("<=", localMethodType28);
    this._symbolTable.addPrimop(">", localMethodType28);
    this._symbolTable.addPrimop(">=", localMethodType28);
    this._symbolTable.addPrimop("<", localMethodType29);
    this._symbolTable.addPrimop("<=", localMethodType29);
    this._symbolTable.addPrimop(">", localMethodType29);
    this._symbolTable.addPrimop(">=", localMethodType29);
    this._symbolTable.addPrimop("<", localMethodType36);
    this._symbolTable.addPrimop("<=", localMethodType36);
    this._symbolTable.addPrimop(">", localMethodType36);
    this._symbolTable.addPrimop(">=", localMethodType36);
    this._symbolTable.addPrimop("or", localMethodType36);
    this._symbolTable.addPrimop("and", localMethodType36);
    this._symbolTable.addPrimop("u-", localMethodType7);
    this._symbolTable.addPrimop("u-", localMethodType10);
  }
  
  public SymbolTable getSymbolTable()
  {
    return this._symbolTable;
  }
  
  public Template getTemplate()
  {
    return this._template;
  }
  
  public void setTemplate(Template paramTemplate)
  {
    this._template = paramTemplate;
  }
  
  public int getTemplateIndex()
  {
    return this._templateIndex++;
  }
  
  public SyntaxTreeNode makeInstance(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    Object localObject1 = null;
    QName localQName = getQName(paramString1, paramString2, paramString3);
    String str = (String)this._instructionClasses.get(localQName.getStringRep());
    Object localObject3;
    if (str != null)
    {
      try
      {
        Class localClass = ObjectFactory.findProviderClass(str, true);
        localObject1 = (SyntaxTreeNode)localClass.newInstance();
        ((SyntaxTreeNode)localObject1).setQName(localQName);
        ((SyntaxTreeNode)localObject1).setParser(this);
        if (this._locator != null) {
          ((SyntaxTreeNode)localObject1).setLineNumber(getLineNumber());
        }
        if ((localObject1 instanceof Stylesheet)) {
          this._xsltc.setStylesheet((Stylesheet)localObject1);
        }
        checkForSuperfluousAttributes((SyntaxTreeNode)localObject1, paramAttributes);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localObject3 = new ErrorMsg("CLASS_NOT_FOUND_ERR", (SyntaxTreeNode)localObject1);
        reportError(3, (ErrorMsg)localObject3);
      }
      catch (Exception localException)
      {
        localObject3 = new ErrorMsg("INTERNAL_ERR", localException.getMessage(), (SyntaxTreeNode)localObject1);
        reportError(2, (ErrorMsg)localObject3);
      }
    }
    else
    {
      if (paramString1 != null)
      {
        Object localObject2;
        if (paramString1.equals("http://www.w3.org/1999/XSL/Transform"))
        {
          localObject1 = new UnsupportedElement(paramString1, paramString2, paramString3, false);
          localObject2 = (UnsupportedElement)localObject1;
          localObject3 = new ErrorMsg("UNSUPPORTED_XSL_ERR", getLineNumber(), paramString3);
          ((UnsupportedElement)localObject2).setErrorMessage((ErrorMsg)localObject3);
          if (this.versionIsOne) {
            reportError(1, (ErrorMsg)localObject3);
          }
        }
        else if (paramString1.equals("http://xml.apache.org/xalan/xsltc"))
        {
          localObject1 = new UnsupportedElement(paramString1, paramString2, paramString3, true);
          localObject2 = (UnsupportedElement)localObject1;
          localObject3 = new ErrorMsg("UNSUPPORTED_EXT_ERR", getLineNumber(), paramString3);
          ((UnsupportedElement)localObject2).setErrorMessage((ErrorMsg)localObject3);
        }
        else
        {
          localObject2 = this._xsltc.getStylesheet();
          if ((localObject2 != null) && (((Stylesheet)localObject2).isExtension(paramString1)) && (localObject2 != (SyntaxTreeNode)this._parentStack.peek()))
          {
            localObject1 = new UnsupportedElement(paramString1, paramString2, paramString3, true);
            localObject3 = (UnsupportedElement)localObject1;
            ErrorMsg localErrorMsg = new ErrorMsg("UNSUPPORTED_EXT_ERR", getLineNumber(), paramString2 + ":" + paramString3);
            ((UnsupportedElement)localObject3).setErrorMessage(localErrorMsg);
          }
        }
      }
      if (localObject1 == null)
      {
        localObject1 = new LiteralElement();
        ((SyntaxTreeNode)localObject1).setLineNumber(getLineNumber());
      }
    }
    if ((localObject1 != null) && ((localObject1 instanceof LiteralElement))) {
      ((LiteralElement)localObject1).setQName(localQName);
    }
    return localObject1;
  }
  
  private void checkForSuperfluousAttributes(SyntaxTreeNode paramSyntaxTreeNode, Attributes paramAttributes)
  {
    QName localQName = paramSyntaxTreeNode.getQName();
    boolean bool = paramSyntaxTreeNode instanceof Stylesheet;
    String[] arrayOfString = (String[])this._instructionAttrs.get(localQName.getStringRep());
    if ((this.versionIsOne) && (arrayOfString != null))
    {
      int j = paramAttributes.getLength();
      for (int k = 0; k < j; k++)
      {
        String str = paramAttributes.getQName(k);
        if ((bool) && (str.equals("version"))) {
          this.versionIsOne = paramAttributes.getValue(k).equals("1.0");
        }
        if ((!str.startsWith("xml")) && (str.indexOf(':') <= 0))
        {
          for (int i = 0; (i < arrayOfString.length) && (!str.equalsIgnoreCase(arrayOfString[i])); i++) {}
          if (i == arrayOfString.length)
          {
            ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_ATTRIBUTE_ERR", str, paramSyntaxTreeNode);
            localErrorMsg.setWarningError(true);
            reportError(4, localErrorMsg);
          }
        }
      }
    }
  }
  
  public Expression parseExpression(SyntaxTreeNode paramSyntaxTreeNode, String paramString)
  {
    return (Expression)parseTopLevel(paramSyntaxTreeNode, "<EXPRESSION>" + paramString, null);
  }
  
  public Expression parseExpression(SyntaxTreeNode paramSyntaxTreeNode, String paramString1, String paramString2)
  {
    String str = paramSyntaxTreeNode.getAttribute(paramString1);
    if ((str.length() == 0) && (paramString2 != null)) {
      str = paramString2;
    }
    return (Expression)parseTopLevel(paramSyntaxTreeNode, "<EXPRESSION>" + str, str);
  }
  
  public Pattern parsePattern(SyntaxTreeNode paramSyntaxTreeNode, String paramString)
  {
    return (Pattern)parseTopLevel(paramSyntaxTreeNode, "<PATTERN>" + paramString, paramString);
  }
  
  public Pattern parsePattern(SyntaxTreeNode paramSyntaxTreeNode, String paramString1, String paramString2)
  {
    String str = paramSyntaxTreeNode.getAttribute(paramString1);
    if ((str.length() == 0) && (paramString2 != null)) {
      str = paramString2;
    }
    return (Pattern)parseTopLevel(paramSyntaxTreeNode, "<PATTERN>" + str, str);
  }
  
  private SyntaxTreeNode parseTopLevel(SyntaxTreeNode paramSyntaxTreeNode, String paramString1, String paramString2)
  {
    int i = getLineNumber();
    try
    {
      this._xpathParser.setScanner(new XPathLexer(new StringReader(paramString1)));
      Symbol localSymbol = this._xpathParser.parse(paramString2, i);
      if (localSymbol != null)
      {
        SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localSymbol.value;
        if (localSyntaxTreeNode != null)
        {
          localSyntaxTreeNode.setParser(this);
          localSyntaxTreeNode.setParent(paramSyntaxTreeNode);
          localSyntaxTreeNode.setLineNumber(i);
          return localSyntaxTreeNode;
        }
      }
      reportError(3, new ErrorMsg("XPATH_PARSER_ERR", paramString2, paramSyntaxTreeNode));
    }
    catch (Exception localException)
    {
      if (this._xsltc.debug()) {
        localException.printStackTrace();
      }
      reportError(3, new ErrorMsg("XPATH_PARSER_ERR", paramString2, paramSyntaxTreeNode));
    }
    SyntaxTreeNode.Dummy.setParser(this);
    return SyntaxTreeNode.Dummy;
  }
  
  public boolean errorsFound()
  {
    return this._errors.size() > 0;
  }
  
  public void printErrors()
  {
    int i = this._errors.size();
    if (i > 0)
    {
      System.err.println(new ErrorMsg("COMPILER_ERROR_KEY"));
      for (int j = 0; j < i; j++) {
        System.err.println("  " + this._errors.elementAt(j));
      }
    }
  }
  
  public void printWarnings()
  {
    int i = this._warnings.size();
    if (i > 0)
    {
      System.err.println(new ErrorMsg("COMPILER_WARNING_KEY"));
      for (int j = 0; j < i; j++) {
        System.err.println("  " + this._warnings.elementAt(j));
      }
    }
  }
  
  public void reportError(int paramInt, ErrorMsg paramErrorMsg)
  {
    switch (paramInt)
    {
    case 0: 
      this._errors.addElement(paramErrorMsg);
      break;
    case 1: 
      this._errors.addElement(paramErrorMsg);
      break;
    case 2: 
      this._errors.addElement(paramErrorMsg);
      break;
    case 3: 
      this._errors.addElement(paramErrorMsg);
      break;
    case 4: 
      this._warnings.addElement(paramErrorMsg);
    }
  }
  
  public Vector getErrors()
  {
    return this._errors;
  }
  
  public Vector getWarnings()
  {
    return this._warnings;
  }
  
  public void startDocument()
  {
    this._root = null;
    this._target = null;
    this._prefixMapping = null;
    this._parentStack = new Stack();
  }
  
  public void endDocument() {}
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    if (this._prefixMapping == null) {
      this._prefixMapping = new HashMap();
    }
    this._prefixMapping.put(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    int i = paramString3.lastIndexOf(':');
    String str = i == -1 ? null : paramString3.substring(0, i);
    SyntaxTreeNode localSyntaxTreeNode = makeInstance(paramString1, str, paramString2, paramAttributes);
    Object localObject;
    if (localSyntaxTreeNode == null)
    {
      localObject = new ErrorMsg("ELEMENT_PARSE_ERR", str + ':' + paramString2);
      throw new SAXException(((ErrorMsg)localObject).toString());
    }
    if (this._root == null)
    {
      if ((this._prefixMapping == null) || (!this._prefixMapping.containsValue("http://www.w3.org/1999/XSL/Transform"))) {
        this._rootNamespaceDef = false;
      } else {
        this._rootNamespaceDef = true;
      }
      this._root = localSyntaxTreeNode;
    }
    else
    {
      localObject = (SyntaxTreeNode)this._parentStack.peek();
      ((SyntaxTreeNode)localObject).addElement(localSyntaxTreeNode);
      localSyntaxTreeNode.setParent((SyntaxTreeNode)localObject);
    }
    localSyntaxTreeNode.setAttributes(new AttributesImpl(paramAttributes));
    localSyntaxTreeNode.setPrefixMapping(this._prefixMapping);
    if ((localSyntaxTreeNode instanceof Stylesheet))
    {
      getSymbolTable().setCurrentNode(localSyntaxTreeNode);
      ((Stylesheet)localSyntaxTreeNode).declareExtensionPrefixes(this);
    }
    this._prefixMapping = null;
    this._parentStack.push(localSyntaxTreeNode);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    this._parentStack.pop();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    String str = new String(paramArrayOfChar, paramInt1, paramInt2);
    SyntaxTreeNode localSyntaxTreeNode1 = (SyntaxTreeNode)this._parentStack.peek();
    if (str.length() == 0) {
      return;
    }
    if ((localSyntaxTreeNode1 instanceof Text))
    {
      ((Text)localSyntaxTreeNode1).setText(str);
      return;
    }
    if ((localSyntaxTreeNode1 instanceof Stylesheet)) {
      return;
    }
    SyntaxTreeNode localSyntaxTreeNode2 = localSyntaxTreeNode1.lastChild();
    if ((localSyntaxTreeNode2 != null) && ((localSyntaxTreeNode2 instanceof Text)))
    {
      Text localText = (Text)localSyntaxTreeNode2;
      if ((!localText.isTextElement()) && ((paramInt2 > 1) || (paramArrayOfChar[0] < 'Ā')))
      {
        localText.setText(str);
        return;
      }
    }
    localSyntaxTreeNode1.addElement(new Text(str));
  }
  
  private String getTokenValue(String paramString)
  {
    int i = paramString.indexOf('"');
    int j = paramString.lastIndexOf('"');
    return paramString.substring(i + 1, j);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
  {
    if ((this._target == null) && (paramString1.equals("xml-stylesheet")))
    {
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString2);
      while (localStringTokenizer.hasMoreElements())
      {
        String str5 = (String)localStringTokenizer.nextElement();
        if (str5.startsWith("href")) {
          str1 = getTokenValue(str5);
        } else if (str5.startsWith("media")) {
          str2 = getTokenValue(str5);
        } else if (str5.startsWith("title")) {
          str3 = getTokenValue(str5);
        } else if (str5.startsWith("charset")) {
          str4 = getTokenValue(str5);
        }
      }
      if (((this._PImedia == null) || (this._PImedia.equals(str2))) && ((this._PItitle == null) || (this._PImedia.equals(str3))) && ((this._PIcharset == null) || (this._PImedia.equals(str4)))) {
        this._target = str1;
      }
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void skippedEntity(String paramString) {}
  
  public void setDocumentLocator(Locator paramLocator)
  {
    this._locator = paramLocator;
  }
  
  private int getLineNumber()
  {
    int i = 0;
    if (this._locator != null) {
      i = this._locator.getLineNumber();
    }
    return i;
  }
}
