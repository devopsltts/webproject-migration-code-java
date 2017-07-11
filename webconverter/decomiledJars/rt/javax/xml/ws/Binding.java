package javax.xml.ws;

import java.util.List;
import javax.xml.ws.handler.Handler;

public abstract interface Binding
{
  public abstract List<Handler> getHandlerChain();
  
  public abstract void setHandlerChain(List<Handler> paramList);
  
  public abstract String getBindingID();
}
