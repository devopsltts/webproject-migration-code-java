package com.sun.activation.registries;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogSupport
{
  private static boolean debug = false;
  private static Logger logger = Logger.getLogger("javax.activation");
  private static final Level level = Level.FINE;
  
  private LogSupport() {}
  
  public static void log(String paramString)
  {
    if (debug) {
      System.out.println(paramString);
    }
    logger.log(level, paramString);
  }
  
  public static void log(String paramString, Throwable paramThrowable)
  {
    if (debug) {
      System.out.println(paramString + "; Exception: " + paramThrowable);
    }
    logger.log(level, paramString, paramThrowable);
  }
  
  public static boolean isLoggable()
  {
    return (debug) || (logger.isLoggable(level));
  }
  
  static
  {
    try
    {
      debug = Boolean.getBoolean("javax.activation.debug");
    }
    catch (Throwable localThrowable) {}
  }
}
