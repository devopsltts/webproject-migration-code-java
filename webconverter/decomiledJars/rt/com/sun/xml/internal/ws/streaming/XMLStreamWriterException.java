package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLStreamWriterException
  extends JAXWSExceptionBase
{
  public XMLStreamWriterException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public XMLStreamWriterException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public XMLStreamWriterException(Localizable paramLocalizable)
  {
    super("xmlwriter.nestedError", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.streaming";
  }
}
