package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class ErrorHandlerAdaptor
  implements XMLErrorHandler
{
  private boolean hadError = false;
  
  public ErrorHandlerAdaptor() {}
  
  public boolean hadError()
  {
    return this.hadError;
  }
  
  public void reset()
  {
    this.hadError = false;
  }
  
  protected abstract ErrorHandler getErrorHandler();
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException)
  {
    try
    {
      this.hadError = true;
      getErrorHandler().fatalError(Util.toSAXParseException(paramXMLParseException));
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedSAXException(localSAXException);
    }
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException)
  {
    try
    {
      this.hadError = true;
      getErrorHandler().error(Util.toSAXParseException(paramXMLParseException));
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedSAXException(localSAXException);
    }
  }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException)
  {
    try
    {
      getErrorHandler().warning(Util.toSAXParseException(paramXMLParseException));
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedSAXException(localSAXException);
    }
  }
}
