package java.time.format;

import java.time.DateTimeException;

public class DateTimeParseException
  extends DateTimeException
{
  private static final long serialVersionUID = 4304633501674722597L;
  private final String parsedString;
  private final int errorIndex;
  
  public DateTimeParseException(String paramString, CharSequence paramCharSequence, int paramInt)
  {
    super(paramString);
    this.parsedString = paramCharSequence.toString();
    this.errorIndex = paramInt;
  }
  
  public DateTimeParseException(String paramString, CharSequence paramCharSequence, int paramInt, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.parsedString = paramCharSequence.toString();
    this.errorIndex = paramInt;
  }
  
  public String getParsedString()
  {
    return this.parsedString;
  }
  
  public int getErrorIndex()
  {
    return this.errorIndex;
  }
}
