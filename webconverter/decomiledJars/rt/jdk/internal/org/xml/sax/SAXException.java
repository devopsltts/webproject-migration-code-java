package jdk.internal.org.xml.sax;

public class SAXException
  extends Exception
{
  private Exception exception;
  static final long serialVersionUID = 583241635256073760L;
  
  public SAXException()
  {
    this.exception = null;
  }
  
  public SAXException(String paramString)
  {
    super(paramString);
    this.exception = null;
  }
  
  public SAXException(Exception paramException)
  {
    this.exception = paramException;
  }
  
  public SAXException(String paramString, Exception paramException)
  {
    super(paramString);
    this.exception = paramException;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if ((str == null) && (this.exception != null)) {
      return this.exception.getMessage();
    }
    return str;
  }
  
  public Exception getException()
  {
    return this.exception;
  }
  
  public Throwable getCause()
  {
    return this.exception;
  }
  
  public String toString()
  {
    if (this.exception != null) {
      return super.toString() + "\n" + this.exception.toString();
    }
    return super.toString();
  }
}
