package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlDataContentHandler
  implements DataContentHandler
{
  public static final String STR_SRC = "javax.xml.transform.stream.StreamSource";
  private static Class streamSourceClass = null;
  
  public XmlDataContentHandler()
    throws ClassNotFoundException
  {
    if (streamSourceClass == null) {
      streamSourceClass = Class.forName("javax.xml.transform.stream.StreamSource");
    }
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[2];
    arrayOfDataFlavor[0] = new ActivationDataFlavor(streamSourceClass, "text/xml", "XML");
    arrayOfDataFlavor[1] = new ActivationDataFlavor(streamSourceClass, "application/xml", "XML");
    return arrayOfDataFlavor;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    if (((paramDataFlavor.getMimeType().startsWith("text/xml")) || (paramDataFlavor.getMimeType().startsWith("application/xml"))) && (paramDataFlavor.getRepresentationClass().getName().equals("javax.xml.transform.stream.StreamSource"))) {
      return new StreamSource(paramDataSource.getInputStream());
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    return new StreamSource(paramDataSource.getInputStream());
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if ((!paramString.startsWith("text/xml")) && (!paramString.startsWith("application/xml"))) {
      throw new IOException("Invalid content type \"" + paramString + "\" for XmlDCH");
    }
    try
    {
      Transformer localTransformer = EfficientStreamingTransformer.newTransformer();
      StreamResult localStreamResult = new StreamResult(paramOutputStream);
      if ((paramObject instanceof DataSource))
      {
        localTransformer.transform((Source)getContent((DataSource)paramObject), localStreamResult);
      }
      else
      {
        Object localObject = null;
        if ((paramObject instanceof String)) {
          localObject = new StreamSource(new StringReader((String)paramObject));
        } else {
          localObject = (Source)paramObject;
        }
        localTransformer.transform((Source)localObject, localStreamResult);
      }
    }
    catch (Exception localException)
    {
      throw new IOException("Unable to run the JAXP transformer on a stream " + localException.getMessage());
    }
  }
}
