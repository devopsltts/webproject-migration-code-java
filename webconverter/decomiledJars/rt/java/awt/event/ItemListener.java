package java.awt.event;

import java.util.EventListener;

public abstract interface ItemListener
  extends EventListener
{
  public abstract void itemStateChanged(ItemEvent paramItemEvent);
}
