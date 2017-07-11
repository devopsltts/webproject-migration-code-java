package javax.sound.sampled;

import java.util.EventListener;

public abstract interface LineListener
  extends EventListener
{
  public abstract void update(LineEvent paramLineEvent);
}
