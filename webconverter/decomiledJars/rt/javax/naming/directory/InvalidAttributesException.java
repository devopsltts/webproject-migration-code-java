package javax.naming.directory;

import javax.naming.NamingException;

public class InvalidAttributesException
  extends NamingException
{
  private static final long serialVersionUID = 2607612850539889765L;
  
  public InvalidAttributesException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidAttributesException() {}
}
