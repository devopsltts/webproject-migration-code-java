package java.util.logging;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class Formatter
{
  protected Formatter() {}
  
  public abstract String format(LogRecord paramLogRecord);
  
  public String getHead(Handler paramHandler)
  {
    return "";
  }
  
  public String getTail(Handler paramHandler)
  {
    return "";
  }
  
  public synchronized String formatMessage(LogRecord paramLogRecord)
  {
    String str = paramLogRecord.getMessage();
    ResourceBundle localResourceBundle = paramLogRecord.getResourceBundle();
    if (localResourceBundle != null) {
      try
      {
        str = localResourceBundle.getString(paramLogRecord.getMessage());
      }
      catch (MissingResourceException localMissingResourceException)
      {
        str = paramLogRecord.getMessage();
      }
    }
    try
    {
      Object[] arrayOfObject = paramLogRecord.getParameters();
      if ((arrayOfObject == null) || (arrayOfObject.length == 0)) {
        return str;
      }
      if ((str.indexOf("{0") >= 0) || (str.indexOf("{1") >= 0) || (str.indexOf("{2") >= 0) || (str.indexOf("{3") >= 0)) {
        return MessageFormat.format(str, arrayOfObject);
      }
      return str;
    }
    catch (Exception localException) {}
    return str;
  }
}
