package javax.print.event;

import java.util.EventObject;

public class PrintEvent
  extends EventObject
{
  private static final long serialVersionUID = 2286914924430763847L;
  
  public PrintEvent(Object paramObject)
  {
    super(paramObject);
  }
  
  public String toString()
  {
    return "PrintEvent on " + getSource().toString();
  }
}
