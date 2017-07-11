package java.awt.dnd.peer;

import java.awt.dnd.DropTarget;

public abstract interface DropTargetPeer
{
  public abstract void addDropTarget(DropTarget paramDropTarget);
  
  public abstract void removeDropTarget(DropTarget paramDropTarget);
}
