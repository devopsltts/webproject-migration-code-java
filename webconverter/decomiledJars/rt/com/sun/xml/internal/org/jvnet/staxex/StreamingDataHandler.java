package com.sun.xml.internal.org.jvnet.staxex;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public abstract class StreamingDataHandler
  extends DataHandler
  implements Closeable
{
  public StreamingDataHandler(Object paramObject, String paramString)
  {
    super(paramObject, paramString);
  }
  
  public StreamingDataHandler(URL paramURL)
  {
    super(paramURL);
  }
  
  public StreamingDataHandler(DataSource paramDataSource)
  {
    super(paramDataSource);
  }
  
  public abstract InputStream readOnce()
    throws IOException;
  
  public abstract void moveTo(File paramFile)
    throws IOException;
  
  public abstract void close()
    throws IOException;
}
