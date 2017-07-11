package javax.xml.bind.helpers;

import java.text.MessageFormat;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;

public class ValidationEventImpl
  implements ValidationEvent
{
  private int severity;
  private String message;
  private Throwable linkedException;
  private ValidationEventLocator locator;
  
  public ValidationEventImpl(int paramInt, String paramString, ValidationEventLocator paramValidationEventLocator)
  {
    this(paramInt, paramString, paramValidationEventLocator, null);
  }
  
  public ValidationEventImpl(int paramInt, String paramString, ValidationEventLocator paramValidationEventLocator, Throwable paramThrowable)
  {
    setSeverity(paramInt);
    this.message = paramString;
    this.locator = paramValidationEventLocator;
    this.linkedException = paramThrowable;
  }
  
  public int getSeverity()
  {
    return this.severity;
  }
  
  public void setSeverity(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException(Messages.format("ValidationEventImpl.IllegalSeverity"));
    }
    this.severity = paramInt;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String paramString)
  {
    this.message = paramString;
  }
  
  public Throwable getLinkedException()
  {
    return this.linkedException;
  }
  
  public void setLinkedException(Throwable paramThrowable)
  {
    this.linkedException = paramThrowable;
  }
  
  public ValidationEventLocator getLocator()
  {
    return this.locator;
  }
  
  public void setLocator(ValidationEventLocator paramValidationEventLocator)
  {
    this.locator = paramValidationEventLocator;
  }
  
  public String toString()
  {
    String str;
    switch (getSeverity())
    {
    case 0: 
      str = "WARNING";
      break;
    case 1: 
      str = "ERROR";
      break;
    case 2: 
      str = "FATAL_ERROR";
      break;
    default: 
      str = String.valueOf(getSeverity());
    }
    return MessageFormat.format("[severity={0},message={1},locator={2}]", new Object[] { str, getMessage(), getLocator() });
  }
}
