package java.awt;

import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

abstract class AttributeValue
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.AttributeValue");
  private final int value;
  private final String[] names;
  
  protected AttributeValue(int paramInt, String[] paramArrayOfString)
  {
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      log.finest("value = " + paramInt + ", names = " + paramArrayOfString);
    }
    if ((log.isLoggable(PlatformLogger.Level.FINER)) && ((paramInt < 0) || (paramArrayOfString == null) || (paramInt >= paramArrayOfString.length))) {
      log.finer("Assertion failed");
    }
    this.value = paramInt;
    this.names = paramArrayOfString;
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public String toString()
  {
    return this.names[this.value];
  }
}
