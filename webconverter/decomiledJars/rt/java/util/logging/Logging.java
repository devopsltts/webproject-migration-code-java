package java.util.logging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class Logging
  implements LoggingMXBean
{
  private static LogManager logManager = ;
  private static String EMPTY_STRING = "";
  
  Logging() {}
  
  public List<String> getLoggerNames()
  {
    Enumeration localEnumeration = logManager.getLoggerNames();
    ArrayList localArrayList = new ArrayList();
    while (localEnumeration.hasMoreElements()) {
      localArrayList.add(localEnumeration.nextElement());
    }
    return localArrayList;
  }
  
  public String getLoggerLevel(String paramString)
  {
    Logger localLogger = logManager.getLogger(paramString);
    if (localLogger == null) {
      return null;
    }
    Level localLevel = localLogger.getLevel();
    if (localLevel == null) {
      return EMPTY_STRING;
    }
    return localLevel.getLevelName();
  }
  
  public void setLoggerLevel(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("loggerName is null");
    }
    Logger localLogger = logManager.getLogger(paramString1);
    if (localLogger == null) {
      throw new IllegalArgumentException("Logger " + paramString1 + "does not exist");
    }
    Level localLevel = null;
    if (paramString2 != null)
    {
      localLevel = Level.findLevel(paramString2);
      if (localLevel == null) {
        throw new IllegalArgumentException("Unknown level \"" + paramString2 + "\"");
      }
    }
    localLogger.setLevel(localLevel);
  }
  
  public String getParentLoggerName(String paramString)
  {
    Logger localLogger1 = logManager.getLogger(paramString);
    if (localLogger1 == null) {
      return null;
    }
    Logger localLogger2 = localLogger1.getParent();
    if (localLogger2 == null) {
      return EMPTY_STRING;
    }
    return localLogger2.getName();
  }
}
