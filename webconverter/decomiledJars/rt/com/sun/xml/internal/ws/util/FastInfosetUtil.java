package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.xml.stream.XMLStreamReader;

public class FastInfosetUtil
{
  public FastInfosetUtil() {}
  
  public static XMLStreamReader createFIStreamReader(InputStream paramInputStream)
  {
    if (FastInfosetReflection.fiStAXDocumentParser_new == null) {
      throw new XMLReaderException("fastinfoset.noImplementation", new Object[0]);
    }
    try
    {
      Object localObject = FastInfosetReflection.fiStAXDocumentParser_new.newInstance(new Object[0]);
      FastInfosetReflection.fiStAXDocumentParser_setStringInterning.invoke(localObject, new Object[] { Boolean.TRUE });
      FastInfosetReflection.fiStAXDocumentParser_setInputStream.invoke(localObject, new Object[] { paramInputStream });
      return (XMLStreamReader)localObject;
    }
    catch (Exception localException)
    {
      throw new XMLStreamReaderException(localException);
    }
  }
}
