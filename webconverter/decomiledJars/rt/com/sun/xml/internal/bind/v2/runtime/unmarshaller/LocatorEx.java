package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.net.URL;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

public abstract interface LocatorEx
  extends Locator
{
  public abstract ValidationEventLocator getLocation();
  
  public static final class Snapshot
    implements LocatorEx, ValidationEventLocator
  {
    private final int columnNumber;
    private final int lineNumber;
    private final int offset;
    private final String systemId;
    private final String publicId;
    private final URL url;
    private final Object object;
    private final Node node;
    
    public Snapshot(LocatorEx paramLocatorEx)
    {
      this.columnNumber = paramLocatorEx.getColumnNumber();
      this.lineNumber = paramLocatorEx.getLineNumber();
      this.systemId = paramLocatorEx.getSystemId();
      this.publicId = paramLocatorEx.getPublicId();
      ValidationEventLocator localValidationEventLocator = paramLocatorEx.getLocation();
      this.offset = localValidationEventLocator.getOffset();
      this.url = localValidationEventLocator.getURL();
      this.object = localValidationEventLocator.getObject();
      this.node = localValidationEventLocator.getNode();
    }
    
    public Object getObject()
    {
      return this.object;
    }
    
    public Node getNode()
    {
      return this.node;
    }
    
    public int getOffset()
    {
      return this.offset;
    }
    
    public URL getURL()
    {
      return this.url;
    }
    
    public int getColumnNumber()
    {
      return this.columnNumber;
    }
    
    public int getLineNumber()
    {
      return this.lineNumber;
    }
    
    public String getSystemId()
    {
      return this.systemId;
    }
    
    public String getPublicId()
    {
      return this.publicId;
    }
    
    public ValidationEventLocator getLocation()
    {
      return this;
    }
  }
}
