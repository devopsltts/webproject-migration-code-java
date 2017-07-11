package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalMessageContext;

class LogicalMessageContextImpl
  extends MessageUpdatableContext
  implements LogicalMessageContext
{
  private LogicalMessageImpl lm;
  private WSBinding binding;
  private BindingContext defaultJaxbContext;
  
  public LogicalMessageContextImpl(WSBinding paramWSBinding, BindingContext paramBindingContext, Packet paramPacket)
  {
    super(paramPacket);
    this.binding = paramWSBinding;
    this.defaultJaxbContext = paramBindingContext;
  }
  
  public LogicalMessage getMessage()
  {
    if (this.lm == null) {
      this.lm = new LogicalMessageImpl(this.defaultJaxbContext, this.packet);
    }
    return this.lm;
  }
  
  void setPacketMessage(Message paramMessage)
  {
    if (paramMessage != null)
    {
      this.packet.setMessage(paramMessage);
      this.lm = null;
    }
  }
  
  protected void updateMessage()
  {
    if (this.lm != null)
    {
      if (this.lm.isPayloadModifed())
      {
        Message localMessage1 = this.packet.getMessage();
        Message localMessage2 = this.lm.getMessage(localMessage1.getHeaders(), localMessage1.getAttachments(), this.binding);
        this.packet.setMessage(localMessage2);
      }
      this.lm = null;
    }
  }
}
