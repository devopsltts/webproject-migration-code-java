package java.awt.dnd;

import java.util.EventListener;

public abstract interface DragGestureListener
  extends EventListener
{
  public abstract void dragGestureRecognized(DragGestureEvent paramDragGestureEvent);
}
