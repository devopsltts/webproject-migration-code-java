package jdk.internal.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import sun.util.spi.XmlPropertiesProvider;

public class BasicXmlPropertiesProvider
  extends XmlPropertiesProvider
{
  public BasicXmlPropertiesProvider() {}
  
  public void load(Properties paramProperties, InputStream paramInputStream)
    throws IOException, InvalidPropertiesFormatException
  {
    PropertiesDefaultHandler localPropertiesDefaultHandler = new PropertiesDefaultHandler();
    localPropertiesDefaultHandler.load(paramProperties, paramInputStream);
  }
  
  public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2)
    throws IOException
  {
    PropertiesDefaultHandler localPropertiesDefaultHandler = new PropertiesDefaultHandler();
    localPropertiesDefaultHandler.store(paramProperties, paramOutputStream, paramString1, paramString2);
  }
}
