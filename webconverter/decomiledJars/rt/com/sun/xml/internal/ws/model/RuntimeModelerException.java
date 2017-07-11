package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class RuntimeModelerException
  extends JAXWSExceptionBase
{
  public RuntimeModelerException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public RuntimeModelerException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public RuntimeModelerException(Localizable paramLocalizable)
  {
    super("nestedModelerError", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.modeler";
  }
}
