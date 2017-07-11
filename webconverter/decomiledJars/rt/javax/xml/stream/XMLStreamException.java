package javax.xml.stream;

public class XMLStreamException
  extends Exception
{
  protected Throwable nested;
  protected Location location;
  
  public XMLStreamException() {}
  
  public XMLStreamException(String paramString)
  {
    super(paramString);
  }
  
  public XMLStreamException(Throwable paramThrowable)
  {
    super(paramThrowable);
    this.nested = paramThrowable;
  }
  
  public XMLStreamException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.nested = paramThrowable;
  }
  
  public XMLStreamException(String paramString, Location paramLocation, Throwable paramThrowable)
  {
    super("ParseError at [row,col]:[" + paramLocation.getLineNumber() + "," + paramLocation.getColumnNumber() + "]\n" + "Message: " + paramString);
    this.nested = paramThrowable;
    this.location = paramLocation;
  }
  
  public XMLStreamException(String paramString, Location paramLocation)
  {
    super("ParseError at [row,col]:[" + paramLocation.getLineNumber() + "," + paramLocation.getColumnNumber() + "]\n" + "Message: " + paramString);
    this.location = paramLocation;
  }
  
  public Throwable getNestedException()
  {
    return this.nested;
  }
  
  public Location getLocation()
  {
    return this.location;
  }
}
