package com.sun.xml.internal.ws.util;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class FastInfosetReflection
{
  public static final Constructor fiStAXDocumentParser_new;
  public static final Method fiStAXDocumentParser_setInputStream;
  public static final Method fiStAXDocumentParser_setStringInterning;
  
  public FastInfosetReflection() {}
  
  static
  {
    Constructor localConstructor = null;
    Method localMethod1 = null;
    Method localMethod2 = null;
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
      localConstructor = localClass.getConstructor(new Class[0]);
      localMethod1 = localClass.getMethod("setInputStream", new Class[] { InputStream.class });
      localMethod2 = localClass.getMethod("setStringInterning", new Class[] { Boolean.TYPE });
    }
    catch (Exception localException) {}
    fiStAXDocumentParser_new = localConstructor;
    fiStAXDocumentParser_setInputStream = localMethod1;
    fiStAXDocumentParser_setStringInterning = localMethod2;
  }
}
