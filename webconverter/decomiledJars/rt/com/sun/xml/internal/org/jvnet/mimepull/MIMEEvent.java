package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

abstract class MIMEEvent
{
  static final StartMessage START_MESSAGE = new StartMessage();
  static final StartPart START_PART = new StartPart();
  static final EndPart END_PART = new EndPart();
  static final EndMessage END_MESSAGE = new EndMessage();
  
  MIMEEvent() {}
  
  abstract EVENT_TYPE getEventType();
  
  static final class Content
    extends MIMEEvent
  {
    private final ByteBuffer buf;
    
    Content(ByteBuffer paramByteBuffer)
    {
      this.buf = paramByteBuffer;
    }
    
    MIMEEvent.EVENT_TYPE getEventType()
    {
      return MIMEEvent.EVENT_TYPE.CONTENT;
    }
    
    ByteBuffer getData()
    {
      return this.buf;
    }
  }
  
  static enum EVENT_TYPE
  {
    START_MESSAGE,  START_PART,  HEADERS,  CONTENT,  END_PART,  END_MESSAGE;
    
    private EVENT_TYPE() {}
  }
  
  static final class EndMessage
    extends MIMEEvent
  {
    EndMessage() {}
    
    MIMEEvent.EVENT_TYPE getEventType()
    {
      return MIMEEvent.EVENT_TYPE.END_MESSAGE;
    }
  }
  
  static final class EndPart
    extends MIMEEvent
  {
    EndPart() {}
    
    MIMEEvent.EVENT_TYPE getEventType()
    {
      return MIMEEvent.EVENT_TYPE.END_PART;
    }
  }
  
  static final class Headers
    extends MIMEEvent
  {
    InternetHeaders ih;
    
    Headers(InternetHeaders paramInternetHeaders)
    {
      this.ih = paramInternetHeaders;
    }
    
    MIMEEvent.EVENT_TYPE getEventType()
    {
      return MIMEEvent.EVENT_TYPE.HEADERS;
    }
    
    InternetHeaders getHeaders()
    {
      return this.ih;
    }
  }
  
  static final class StartMessage
    extends MIMEEvent
  {
    StartMessage() {}
    
    MIMEEvent.EVENT_TYPE getEventType()
    {
      return MIMEEvent.EVENT_TYPE.START_MESSAGE;
    }
  }
  
  static final class StartPart
    extends MIMEEvent
  {
    StartPart() {}
    
    MIMEEvent.EVENT_TYPE getEventType()
    {
      return MIMEEvent.EVENT_TYPE.START_PART;
    }
  }
}
