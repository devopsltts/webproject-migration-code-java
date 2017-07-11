package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.TemplatesHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TemplatesHandlerImpl
  implements ContentHandler, TemplatesHandler, SourceLoader
{
  private String _systemId;
  private int _indentNumber;
  private URIResolver _uriResolver = null;
  private TransformerFactoryImpl _tfactory = null;
  private Parser _parser = null;
  private TemplatesImpl _templates = null;
  
  protected TemplatesHandlerImpl(int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl)
  {
    this._indentNumber = paramInt;
    this._tfactory = paramTransformerFactoryImpl;
    XSLTC localXSLTC = new XSLTC(paramTransformerFactoryImpl.useServicesMechnism(), paramTransformerFactoryImpl.getFeatureManager());
    if (paramTransformerFactoryImpl.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
      localXSLTC.setSecureProcessing(true);
    }
    localXSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet"));
    localXSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD"));
    localXSLTC.setProperty("http://apache.org/xml/properties/security-manager", paramTransformerFactoryImpl.getAttribute("http://apache.org/xml/properties/security-manager"));
    if ("true".equals(paramTransformerFactoryImpl.getAttribute("enable-inlining"))) {
      localXSLTC.setTemplateInlining(true);
    } else {
      localXSLTC.setTemplateInlining(false);
    }
    this._parser = localXSLTC.getParser();
  }
  
  public String getSystemId()
  {
    return this._systemId;
  }
  
  public void setSystemId(String paramString)
  {
    this._systemId = paramString;
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    this._uriResolver = paramURIResolver;
  }
  
  public Templates getTemplates()
  {
    return this._templates;
  }
  
  public InputSource loadSource(String paramString1, String paramString2, XSLTC paramXSLTC)
  {
    try
    {
      Source localSource = this._uriResolver.resolve(paramString1, paramString2);
      if (localSource != null) {
        return Util.getInputSource(paramXSLTC, localSource);
      }
    }
    catch (TransformerException localTransformerException) {}
    return null;
  }
  
  public void startDocument()
  {
    XSLTC localXSLTC = this._parser.getXSLTC();
    localXSLTC.init();
    localXSLTC.setOutputType(2);
    this._parser.startDocument();
  }
  
  public void endDocument()
    throws SAXException
  {
    this._parser.endDocument();
    try
    {
      XSLTC localXSLTC = this._parser.getXSLTC();
      if (this._systemId != null) {
        str = Util.baseName(this._systemId);
      } else {
        str = (String)this._tfactory.getAttribute("translet-name");
      }
      localXSLTC.setClassName(str);
      String str = localXSLTC.getClassName();
      Stylesheet localStylesheet = null;
      SyntaxTreeNode localSyntaxTreeNode = this._parser.getDocumentRoot();
      if ((!this._parser.errorsFound()) && (localSyntaxTreeNode != null))
      {
        localStylesheet = this._parser.makeStylesheet(localSyntaxTreeNode);
        localStylesheet.setSystemId(this._systemId);
        localStylesheet.setParentStylesheet(null);
        if (localXSLTC.getTemplateInlining()) {
          localStylesheet.setTemplateInlining(true);
        } else {
          localStylesheet.setTemplateInlining(false);
        }
        if (this._uriResolver != null) {
          localStylesheet.setSourceLoader(this);
        }
        this._parser.setCurrentStylesheet(localStylesheet);
        localXSLTC.setStylesheet(localStylesheet);
        this._parser.createAST(localStylesheet);
      }
      if ((!this._parser.errorsFound()) && (localStylesheet != null))
      {
        localStylesheet.setMultiDocument(localXSLTC.isMultiDocument());
        localStylesheet.setHasIdCall(localXSLTC.hasIdCall());
        synchronized (localXSLTC.getClass())
        {
          localStylesheet.translate();
        }
      }
      if (!this._parser.errorsFound())
      {
        ??? = localXSLTC.getBytecodes();
        if (??? != null)
        {
          this._templates = new TemplatesImpl(localXSLTC.getBytecodes(), str, this._parser.getOutputProperties(), this._indentNumber, this._tfactory);
          if (this._uriResolver != null) {
            this._templates.setURIResolver(this._uriResolver);
          }
        }
      }
      else
      {
        ??? = new StringBuffer();
        Vector localVector = this._parser.getErrors();
        int i = localVector.size();
        for (int j = 0; j < i; j++)
        {
          if (((StringBuffer)???).length() > 0) {
            ((StringBuffer)???).append('\n');
          }
          ((StringBuffer)???).append(localVector.elementAt(j).toString());
        }
        throw new SAXException("JAXP_COMPILE_ERR", new TransformerException(((StringBuffer)???).toString()));
      }
    }
    catch (CompilerException localCompilerException)
    {
      throw new SAXException("JAXP_COMPILE_ERR", localCompilerException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    this._parser.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
  {
    this._parser.endPrefixMapping(paramString);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    this._parser.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    this._parser.endElement(paramString1, paramString2, paramString3);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this._parser.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
  {
    this._parser.processingInstruction(paramString1, paramString2);
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this._parser.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void skippedEntity(String paramString)
  {
    this._parser.skippedEntity(paramString);
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    setSystemId(paramLocator.getSystemId());
    this._parser.setDocumentLocator(paramLocator);
  }
}
