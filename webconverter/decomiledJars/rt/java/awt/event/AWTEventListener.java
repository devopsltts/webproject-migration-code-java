package java.awt.event;

import java.awt.AWTEvent;
import java.util.EventListener;

public abstract interface AWTEventListener
  extends EventListener
{
  public abstract void eventDispatched(AWTEvent paramAWTEvent);
}
