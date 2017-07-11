package com.sun.xml.internal.ws.commons.xmlutil;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class Converter
{
  public static final String UTF_8 = "UTF-8";
  private static final Logger LOGGER = Logger.getLogger(Converter.class);
  private static final ContextClassloaderLocal<XMLOutputFactory> xmlOutputFactory = new ContextClassloaderLocal()
  {
    protected XMLOutputFactory initialValue()
      throws Exception
    {
      return XMLOutputFactory.newInstance();
    }
  };
  private static final AtomicBoolean logMissingStaxUtilsWarning = new AtomicBoolean(false);
  
  private Converter() {}
  
  public static String toString(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return "[ No exception ]";
    }
    StringWriter localStringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(localStringWriter));
    return localStringWriter.toString();
  }
  
  public static String toString(Packet paramPacket)
  {
    if (paramPacket == null) {
      return "[ Null packet ]";
    }
    if (paramPacket.getMessage() == null) {
      return "[ Empty packet ]";
    }
    return toString(paramPacket.getMessage());
  }
  
  public static String toStringNoIndent(Packet paramPacket)
  {
    if (paramPacket == null) {
      return "[ Null packet ]";
    }
    if (paramPacket.getMessage() == null) {
      return "[ Empty packet ]";
    }
    return toStringNoIndent(paramPacket.getMessage());
  }
  
  public static String toString(Message paramMessage)
  {
    return toString(paramMessage, true);
  }
  
  public static String toStringNoIndent(Message paramMessage)
  {
    return toString(paramMessage, false);
  }
  
  private static String toString(Message paramMessage, boolean paramBoolean)
  {
    if (paramMessage == null) {
      return "[ Null message ]";
    }
    StringWriter localStringWriter = null;
    try
    {
      localStringWriter = new StringWriter();
      XMLStreamWriter localXMLStreamWriter = null;
      try
      {
        localXMLStreamWriter = ((XMLOutputFactory)xmlOutputFactory.get()).createXMLStreamWriter(localStringWriter);
        if (paramBoolean) {
          localXMLStreamWriter = createIndenter(localXMLStreamWriter);
        }
        paramMessage.copy().writeTo(localXMLStreamWriter);
        if (localXMLStreamWriter != null) {
          try
          {
            localXMLStreamWriter.close();
          }
          catch (XMLStreamException localXMLStreamException1)
          {
            LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", localXMLStreamException1);
          }
        }
        str = localStringWriter.toString();
      }
      catch (Exception localException)
      {
        LOGGER.log(Level.WARNING, "Unexpected exception occured while dumping message", localException);
      }
      finally
      {
        if (localXMLStreamWriter != null) {
          try
          {
            localXMLStreamWriter.close();
          }
          catch (XMLStreamException localXMLStreamException3)
          {
            LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", localXMLStreamException3);
          }
        }
      }
      String str;
      return str;
    }
    finally
    {
      if (localStringWriter != null) {
        try
        {
          localStringWriter.close();
        }
        catch (IOException localIOException2)
        {
          LOGGER.finest("An exception occured when trying to close StringWriter", localIOException2);
        }
      }
    }
  }
  
  public static byte[] toBytes(Message paramMessage, String paramString)
    throws XMLStreamException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      Object localObject1;
      if (paramMessage != null)
      {
        localObject1 = ((XMLOutputFactory)xmlOutputFactory.get()).createXMLStreamWriter(localByteArrayOutputStream, paramString);
        try
        {
          paramMessage.writeTo((XMLStreamWriter)localObject1);
          try
          {
            ((XMLStreamWriter)localObject1).close();
          }
          catch (XMLStreamException localXMLStreamException1)
          {
            LOGGER.warning("Unexpected exception occured while closing XMLStreamWriter", localXMLStreamException1);
          }
          localObject1 = localByteArrayOutputStream.toByteArray();
        }
        finally
        {
          try
          {
            ((XMLStreamWriter)localObject1).close();
          }
          catch (XMLStreamException localXMLStreamException2)
          {
            LOGGER.warning("Unexpected exception occured while closing XMLStreamWriter", localXMLStreamException2);
          }
        }
      }
      return localObject1;
    }
    finally
    {
      try
      {
        localByteArrayOutputStream.close();
      }
      catch (IOException localIOException2)
      {
        LOGGER.warning("Unexpected exception occured while closing ByteArrayOutputStream", localIOException2);
      }
    }
  }
  
  public static Message toMessage(@NotNull InputStream paramInputStream, String paramString)
    throws XMLStreamException
  {
    XMLStreamReader localXMLStreamReader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(paramInputStream, paramString);
    return Messages.create(localXMLStreamReader);
  }
  
  public static String messageDataToString(byte[] paramArrayOfByte, String paramString)
  {
    try
    {
      return toString(toMessage(new ByteArrayInputStream(paramArrayOfByte), paramString));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      LOGGER.warning("Unexpected exception occured while converting message data to string", localXMLStreamException);
    }
    return "[ Message Data Conversion Failed ]";
  }
  
  private static XMLStreamWriter createIndenter(XMLStreamWriter paramXMLStreamWriter)
  {
    try
    {
      Class localClass = Converter.class.getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
      Constructor localConstructor = localClass.getConstructor(new Class[] { XMLStreamWriter.class });
      paramXMLStreamWriter = (XMLStreamWriter)XMLStreamWriter.class.cast(localConstructor.newInstance(new Object[] { paramXMLStreamWriter }));
    }
    catch (Exception localException)
    {
      if (logMissingStaxUtilsWarning.compareAndSet(false, true)) {
        LOGGER.log(Level.WARNING, "Put stax-utils.jar to the classpath to indent the dump output", localException);
      }
    }
    return paramXMLStreamWriter;
  }
}
