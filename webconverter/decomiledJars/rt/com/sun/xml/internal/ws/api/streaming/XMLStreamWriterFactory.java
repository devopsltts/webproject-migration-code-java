package com.sun.xml.internal.ws.api.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;

public abstract class XMLStreamWriterFactory
{
  private static final Logger LOGGER = Logger.getLogger(XMLStreamWriterFactory.class.getName());
  private static volatile ContextClassloaderLocal<XMLStreamWriterFactory> writerFactory = new ContextClassloaderLocal()
  {
    protected XMLStreamWriterFactory initialValue()
    {
      XMLOutputFactory localXMLOutputFactory = null;
      if (Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".woodstox")) {
        try
        {
          localXMLOutputFactory = (XMLOutputFactory)Class.forName("com.ctc.wstx.stax.WstxOutputFactory").newInstance();
        }
        catch (Exception localException) {}
      }
      if (localXMLOutputFactory == null) {
        localXMLOutputFactory = XMLOutputFactory.newInstance();
      }
      Object localObject = null;
      if (!Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".noPool")) {
        try
        {
          Class localClass = localXMLOutputFactory.createXMLStreamWriter(new StringWriter()).getClass();
          if (localClass.getName().startsWith("com.sun.xml.internal.stream.")) {
            localObject = new XMLStreamWriterFactory.Zephyr(localXMLOutputFactory, localClass, null);
          }
        }
        catch (XMLStreamException localXMLStreamException)
        {
          Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, null, localXMLStreamException);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, null, localNoSuchMethodException);
        }
      }
      if ((localObject == null) && (localXMLOutputFactory.getClass().getName().equals("com.ctc.wstx.stax.WstxOutputFactory"))) {
        localObject = new XMLStreamWriterFactory.NoLock(localXMLOutputFactory);
      }
      if (localObject == null) {
        localObject = new XMLStreamWriterFactory.Default(localXMLOutputFactory);
      }
      if (XMLStreamWriterFactory.LOGGER.isLoggable(Level.FINE)) {
        XMLStreamWriterFactory.LOGGER.log(Level.FINE, "XMLStreamWriterFactory instance is = {0}", localObject);
      }
      return localObject;
    }
  };
  
  public XMLStreamWriterFactory() {}
  
  public abstract XMLStreamWriter doCreate(OutputStream paramOutputStream);
  
  public abstract XMLStreamWriter doCreate(OutputStream paramOutputStream, String paramString);
  
  public abstract void doRecycle(XMLStreamWriter paramXMLStreamWriter);
  
  public static void recycle(XMLStreamWriter paramXMLStreamWriter)
  {
    get().doRecycle(paramXMLStreamWriter);
  }
  
  @NotNull
  public static XMLStreamWriterFactory get()
  {
    return (XMLStreamWriterFactory)writerFactory.get();
  }
  
  public static void set(@NotNull XMLStreamWriterFactory paramXMLStreamWriterFactory)
  {
    if (paramXMLStreamWriterFactory == null) {
      throw new IllegalArgumentException();
    }
    writerFactory.set(paramXMLStreamWriterFactory);
  }
  
  public static XMLStreamWriter create(OutputStream paramOutputStream)
  {
    return get().doCreate(paramOutputStream);
  }
  
  public static XMLStreamWriter create(OutputStream paramOutputStream, String paramString)
  {
    return get().doCreate(paramOutputStream, paramString);
  }
  
  /**
   * @deprecated
   */
  public static XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream)
  {
    return create(paramOutputStream);
  }
  
  /**
   * @deprecated
   */
  public static XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString)
  {
    return create(paramOutputStream, paramString);
  }
  
  /**
   * @deprecated
   */
  public static XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString, boolean paramBoolean)
  {
    return create(paramOutputStream, paramString);
  }
  
  public static final class Default
    extends XMLStreamWriterFactory
  {
    private final XMLOutputFactory xof;
    
    public Default(XMLOutputFactory paramXMLOutputFactory)
    {
      this.xof = paramXMLOutputFactory;
    }
    
    public XMLStreamWriter doCreate(OutputStream paramOutputStream)
    {
      return doCreate(paramOutputStream, "UTF-8");
    }
    
    public synchronized XMLStreamWriter doCreate(OutputStream paramOutputStream, String paramString)
    {
      try
      {
        XMLStreamWriter localXMLStreamWriter = this.xof.createXMLStreamWriter(paramOutputStream, paramString);
        return new XMLStreamWriterFactory.HasEncodingWriter(localXMLStreamWriter, paramString);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public void doRecycle(XMLStreamWriter paramXMLStreamWriter) {}
  }
  
  private static class HasEncodingWriter
    extends XMLStreamWriterFilter
    implements HasEncoding
  {
    private final String encoding;
    
    HasEncodingWriter(XMLStreamWriter paramXMLStreamWriter, String paramString)
    {
      super();
      this.encoding = paramString;
    }
    
    public String getEncoding()
    {
      return this.encoding;
    }
    
    XMLStreamWriter getWriter()
    {
      return this.writer;
    }
  }
  
  public static final class NoLock
    extends XMLStreamWriterFactory
  {
    private final XMLOutputFactory xof;
    
    public NoLock(XMLOutputFactory paramXMLOutputFactory)
    {
      this.xof = paramXMLOutputFactory;
    }
    
    public XMLStreamWriter doCreate(OutputStream paramOutputStream)
    {
      return doCreate(paramOutputStream, "utf-8");
    }
    
    public XMLStreamWriter doCreate(OutputStream paramOutputStream, String paramString)
    {
      try
      {
        XMLStreamWriter localXMLStreamWriter = this.xof.createXMLStreamWriter(paramOutputStream, paramString);
        return new XMLStreamWriterFactory.HasEncodingWriter(localXMLStreamWriter, paramString);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public void doRecycle(XMLStreamWriter paramXMLStreamWriter) {}
  }
  
  public static abstract interface RecycleAware
  {
    public abstract void onRecycled();
  }
  
  public static final class Zephyr
    extends XMLStreamWriterFactory
  {
    private final XMLOutputFactory xof;
    private final ThreadLocal<XMLStreamWriter> pool = new ThreadLocal();
    private final Method resetMethod;
    private final Method setOutputMethod;
    private final Class zephyrClass;
    
    public static XMLStreamWriterFactory newInstance(XMLOutputFactory paramXMLOutputFactory)
    {
      try
      {
        Class localClass = paramXMLOutputFactory.createXMLStreamWriter(new StringWriter()).getClass();
        if (!localClass.getName().startsWith("com.sun.xml.internal.stream.")) {
          return null;
        }
        return new Zephyr(paramXMLOutputFactory, localClass);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        return null;
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
      return null;
    }
    
    private Zephyr(XMLOutputFactory paramXMLOutputFactory, Class paramClass)
      throws NoSuchMethodException
    {
      this.xof = paramXMLOutputFactory;
      this.zephyrClass = paramClass;
      this.setOutputMethod = paramClass.getMethod("setOutput", new Class[] { StreamResult.class, String.class });
      this.resetMethod = paramClass.getMethod("reset", new Class[0]);
    }
    
    @Nullable
    private XMLStreamWriter fetch()
    {
      XMLStreamWriter localXMLStreamWriter = (XMLStreamWriter)this.pool.get();
      if (localXMLStreamWriter == null) {
        return null;
      }
      this.pool.set(null);
      return localXMLStreamWriter;
    }
    
    public XMLStreamWriter doCreate(OutputStream paramOutputStream)
    {
      return doCreate(paramOutputStream, "UTF-8");
    }
    
    public XMLStreamWriter doCreate(OutputStream paramOutputStream, String paramString)
    {
      XMLStreamWriter localXMLStreamWriter = fetch();
      if (localXMLStreamWriter != null) {
        try
        {
          this.resetMethod.invoke(localXMLStreamWriter, new Object[0]);
          this.setOutputMethod.invoke(localXMLStreamWriter, new Object[] { new StreamResult(paramOutputStream), paramString });
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new XMLReaderException("stax.cantCreate", new Object[] { localIllegalAccessException });
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new XMLReaderException("stax.cantCreate", new Object[] { localInvocationTargetException });
        }
      } else {
        try
        {
          localXMLStreamWriter = this.xof.createXMLStreamWriter(paramOutputStream, paramString);
        }
        catch (XMLStreamException localXMLStreamException)
        {
          throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
        }
      }
      return new XMLStreamWriterFactory.HasEncodingWriter(localXMLStreamWriter, paramString);
    }
    
    public void doRecycle(XMLStreamWriter paramXMLStreamWriter)
    {
      if ((paramXMLStreamWriter instanceof XMLStreamWriterFactory.HasEncodingWriter)) {
        paramXMLStreamWriter = ((XMLStreamWriterFactory.HasEncodingWriter)paramXMLStreamWriter).getWriter();
      }
      if (this.zephyrClass.isInstance(paramXMLStreamWriter))
      {
        try
        {
          paramXMLStreamWriter.close();
        }
        catch (XMLStreamException localXMLStreamException)
        {
          throw new WebServiceException(localXMLStreamException);
        }
        this.pool.set(paramXMLStreamWriter);
      }
      if ((paramXMLStreamWriter instanceof XMLStreamWriterFactory.RecycleAware)) {
        ((XMLStreamWriterFactory.RecycleAware)paramXMLStreamWriter).onRecycled();
      }
    }
  }
}
