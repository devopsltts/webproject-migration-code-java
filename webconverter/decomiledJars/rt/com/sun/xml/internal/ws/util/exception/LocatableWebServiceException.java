package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.resources.UtilMessages;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class LocatableWebServiceException
  extends WebServiceException
{
  private final Locator[] location;
  
  public LocatableWebServiceException(String paramString, Locator... paramVarArgs)
  {
    this(paramString, null, paramVarArgs);
  }
  
  public LocatableWebServiceException(String paramString, Throwable paramThrowable, Locator... paramVarArgs)
  {
    super(appendLocationInfo(paramString, paramVarArgs), paramThrowable);
    this.location = paramVarArgs;
  }
  
  public LocatableWebServiceException(Throwable paramThrowable, Locator... paramVarArgs)
  {
    this(paramThrowable.toString(), paramThrowable, paramVarArgs);
  }
  
  public LocatableWebServiceException(String paramString, XMLStreamReader paramXMLStreamReader)
  {
    this(paramString, new Locator[] { toLocation(paramXMLStreamReader) });
  }
  
  public LocatableWebServiceException(String paramString, Throwable paramThrowable, XMLStreamReader paramXMLStreamReader)
  {
    this(paramString, paramThrowable, new Locator[] { toLocation(paramXMLStreamReader) });
  }
  
  public LocatableWebServiceException(Throwable paramThrowable, XMLStreamReader paramXMLStreamReader)
  {
    this(paramThrowable, new Locator[] { toLocation(paramXMLStreamReader) });
  }
  
  @NotNull
  public List<Locator> getLocation()
  {
    return Arrays.asList(this.location);
  }
  
  private static String appendLocationInfo(String paramString, Locator[] paramArrayOfLocator)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    for (Locator localLocator : paramArrayOfLocator) {
      localStringBuilder.append('\n').append(UtilMessages.UTIL_LOCATION(Integer.valueOf(localLocator.getLineNumber()), localLocator.getSystemId()));
    }
    return localStringBuilder.toString();
  }
  
  private static Locator toLocation(XMLStreamReader paramXMLStreamReader)
  {
    LocatorImpl localLocatorImpl = new LocatorImpl();
    Location localLocation = paramXMLStreamReader.getLocation();
    localLocatorImpl.setSystemId(localLocation.getSystemId());
    localLocatorImpl.setPublicId(localLocation.getPublicId());
    localLocatorImpl.setLineNumber(localLocation.getLineNumber());
    localLocatorImpl.setColumnNumber(localLocation.getColumnNumber());
    return localLocatorImpl;
  }
}
