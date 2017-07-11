package javax.sound.midi;

import java.util.EventListener;

public abstract interface ControllerEventListener
  extends EventListener
{
  public abstract void controlChange(ShortMessage paramShortMessage);
}
