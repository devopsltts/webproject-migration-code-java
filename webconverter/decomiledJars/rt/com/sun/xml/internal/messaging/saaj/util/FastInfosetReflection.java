package com.sun.xml.internal.messaging.saaj.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FastInfosetReflection
{
  static Constructor fiDOMDocumentParser_new;
  static Method fiDOMDocumentParser_parse;
  static Constructor fiDOMDocumentSerializer_new;
  static Method fiDOMDocumentSerializer_serialize;
  static Method fiDOMDocumentSerializer_setOutputStream;
  static Class fiFastInfosetSource_class;
  static Constructor fiFastInfosetSource_new;
  static Method fiFastInfosetSource_getInputStream;
  static Method fiFastInfosetSource_setInputStream;
  static Constructor fiFastInfosetResult_new;
  static Method fiFastInfosetResult_getOutputStream;
  
  public FastInfosetReflection() {}
  
  public static Object DOMDocumentParser_new()
    throws Exception
  {
    if (fiDOMDocumentParser_new == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return fiDOMDocumentParser_new.newInstance((Object[])null);
  }
  
  public static void DOMDocumentParser_parse(Object paramObject, Document paramDocument, InputStream paramInputStream)
    throws Exception
  {
    if (fiDOMDocumentParser_parse == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    fiDOMDocumentParser_parse.invoke(paramObject, new Object[] { paramDocument, paramInputStream });
  }
  
  public static Object DOMDocumentSerializer_new()
    throws Exception
  {
    if (fiDOMDocumentSerializer_new == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return fiDOMDocumentSerializer_new.newInstance((Object[])null);
  }
  
  public static void DOMDocumentSerializer_serialize(Object paramObject, Node paramNode)
    throws Exception
  {
    if (fiDOMDocumentSerializer_serialize == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    fiDOMDocumentSerializer_serialize.invoke(paramObject, new Object[] { paramNode });
  }
  
  public static void DOMDocumentSerializer_setOutputStream(Object paramObject, OutputStream paramOutputStream)
    throws Exception
  {
    if (fiDOMDocumentSerializer_setOutputStream == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    fiDOMDocumentSerializer_setOutputStream.invoke(paramObject, new Object[] { paramOutputStream });
  }
  
  public static boolean isFastInfosetSource(Source paramSource)
  {
    return paramSource.getClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
  }
  
  public static Class getFastInfosetSource_class()
  {
    if (fiFastInfosetSource_class == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return fiFastInfosetSource_class;
  }
  
  public static Source FastInfosetSource_new(InputStream paramInputStream)
    throws Exception
  {
    if (fiFastInfosetSource_new == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return (Source)fiFastInfosetSource_new.newInstance(new Object[] { paramInputStream });
  }
  
  public static InputStream FastInfosetSource_getInputStream(Source paramSource)
    throws Exception
  {
    if (fiFastInfosetSource_getInputStream == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return (InputStream)fiFastInfosetSource_getInputStream.invoke(paramSource, (Object[])null);
  }
  
  public static void FastInfosetSource_setInputStream(Source paramSource, InputStream paramInputStream)
    throws Exception
  {
    if (fiFastInfosetSource_setInputStream == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    fiFastInfosetSource_setInputStream.invoke(paramSource, new Object[] { paramInputStream });
  }
  
  public static boolean isFastInfosetResult(Result paramResult)
  {
    return paramResult.getClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult");
  }
  
  public static Result FastInfosetResult_new(OutputStream paramOutputStream)
    throws Exception
  {
    if (fiFastInfosetResult_new == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return (Result)fiFastInfosetResult_new.newInstance(new Object[] { paramOutputStream });
  }
  
  public static OutputStream FastInfosetResult_getOutputStream(Result paramResult)
    throws Exception
  {
    if (fiFastInfosetResult_getOutputStream == null) {
      throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
    return (OutputStream)fiFastInfosetResult_getOutputStream.invoke(paramResult, (Object[])null);
  }
  
  static
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.fastinfoset.dom.DOMDocumentParser");
      fiDOMDocumentParser_new = localClass.getConstructor((Class[])null);
      fiDOMDocumentParser_parse = localClass.getMethod("parse", new Class[] { Document.class, InputStream.class });
      localClass = Class.forName("com.sun.xml.internal.fastinfoset.dom.DOMDocumentSerializer");
      fiDOMDocumentSerializer_new = localClass.getConstructor((Class[])null);
      fiDOMDocumentSerializer_serialize = localClass.getMethod("serialize", new Class[] { Node.class });
      fiDOMDocumentSerializer_setOutputStream = localClass.getMethod("setOutputStream", new Class[] { OutputStream.class });
      fiFastInfosetSource_class = localClass = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
      fiFastInfosetSource_new = localClass.getConstructor(new Class[] { InputStream.class });
      fiFastInfosetSource_getInputStream = localClass.getMethod("getInputStream", (Class[])null);
      fiFastInfosetSource_setInputStream = localClass.getMethod("setInputStream", new Class[] { InputStream.class });
      localClass = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult");
      fiFastInfosetResult_new = localClass.getConstructor(new Class[] { OutputStream.class });
      fiFastInfosetResult_getOutputStream = localClass.getMethod("getOutputStream", (Class[])null);
    }
    catch (Exception localException) {}
  }
}
