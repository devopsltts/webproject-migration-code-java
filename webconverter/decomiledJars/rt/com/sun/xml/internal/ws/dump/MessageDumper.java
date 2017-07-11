package com.sun.xml.internal.ws.dump;

import java.util.logging.Level;
import java.util.logging.Logger;

final class MessageDumper
{
  private final String tubeName;
  private final Logger logger;
  private Level loggingLevel;
  
  public MessageDumper(String paramString, Logger paramLogger, Level paramLevel)
  {
    this.tubeName = paramString;
    this.logger = paramLogger;
    this.loggingLevel = paramLevel;
  }
  
  final boolean isLoggable()
  {
    return this.logger.isLoggable(this.loggingLevel);
  }
  
  final void setLoggingLevel(Level paramLevel)
  {
    this.loggingLevel = paramLevel;
  }
  
  final String createLogMessage(MessageType paramMessageType, ProcessingState paramProcessingState, int paramInt, String paramString1, String paramString2)
  {
    return String.format("%s %s in Tube [ %s ] Instance [ %d ] Engine [ %s ] Thread [ %s ]:%n%s", new Object[] { paramMessageType, paramProcessingState, this.tubeName, Integer.valueOf(paramInt), paramString1, Thread.currentThread().getName(), paramString2 });
  }
  
  final String dump(MessageType paramMessageType, ProcessingState paramProcessingState, String paramString1, int paramInt, String paramString2)
  {
    String str = createLogMessage(paramMessageType, paramProcessingState, paramInt, paramString2, paramString1);
    this.logger.log(this.loggingLevel, str);
    return str;
  }
  
  static enum MessageType
  {
    Request("Request message"),  Response("Response message"),  Exception("Response exception");
    
    private final String name;
    
    private MessageType(String paramString)
    {
      this.name = paramString;
    }
    
    public String toString()
    {
      return this.name;
    }
  }
  
  static enum ProcessingState
  {
    Received("received"),  Processed("processed");
    
    private final String name;
    
    private ProcessingState(String paramString)
    {
      this.name = paramString;
    }
    
    public String toString()
    {
      return this.name;
    }
  }
}
