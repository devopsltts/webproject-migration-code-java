package javax.swing.plaf.basic;

import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import sun.awt.AppContext;
import sun.awt.dnd.SunDragSourceContextPeer;

class DragRecognitionSupport
{
  private int motionThreshold;
  private MouseEvent dndArmedEvent;
  private JComponent component;
  
  DragRecognitionSupport() {}
  
  private static DragRecognitionSupport getDragRecognitionSupport()
  {
    DragRecognitionSupport localDragRecognitionSupport = (DragRecognitionSupport)AppContext.getAppContext().get(DragRecognitionSupport.class);
    if (localDragRecognitionSupport == null)
    {
      localDragRecognitionSupport = new DragRecognitionSupport();
      AppContext.getAppContext().put(DragRecognitionSupport.class, localDragRecognitionSupport);
    }
    return localDragRecognitionSupport;
  }
  
  public static boolean mousePressed(MouseEvent paramMouseEvent)
  {
    return getDragRecognitionSupport().mousePressedImpl(paramMouseEvent);
  }
  
  public static MouseEvent mouseReleased(MouseEvent paramMouseEvent)
  {
    return getDragRecognitionSupport().mouseReleasedImpl(paramMouseEvent);
  }
  
  public static boolean mouseDragged(MouseEvent paramMouseEvent, BeforeDrag paramBeforeDrag)
  {
    return getDragRecognitionSupport().mouseDraggedImpl(paramMouseEvent, paramBeforeDrag);
  }
  
  private void clearState()
  {
    this.dndArmedEvent = null;
    this.component = null;
  }
  
  private int mapDragOperationFromModifiers(MouseEvent paramMouseEvent, TransferHandler paramTransferHandler)
  {
    if ((paramTransferHandler == null) || (!SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      return 0;
    }
    return SunDragSourceContextPeer.convertModifiersToDropAction(paramMouseEvent.getModifiersEx(), paramTransferHandler.getSourceActions(this.component));
  }
  
  private boolean mousePressedImpl(MouseEvent paramMouseEvent)
  {
    this.component = ((JComponent)paramMouseEvent.getSource());
    if (mapDragOperationFromModifiers(paramMouseEvent, this.component.getTransferHandler()) != 0)
    {
      this.motionThreshold = DragSource.getDragThreshold();
      this.dndArmedEvent = paramMouseEvent;
      return true;
    }
    clearState();
    return false;
  }
  
  private MouseEvent mouseReleasedImpl(MouseEvent paramMouseEvent)
  {
    if (this.dndArmedEvent == null) {
      return null;
    }
    MouseEvent localMouseEvent = null;
    if (paramMouseEvent.getSource() == this.component) {
      localMouseEvent = this.dndArmedEvent;
    }
    clearState();
    return localMouseEvent;
  }
  
  private boolean mouseDraggedImpl(MouseEvent paramMouseEvent, BeforeDrag paramBeforeDrag)
  {
    if (this.dndArmedEvent == null) {
      return false;
    }
    if (paramMouseEvent.getSource() != this.component)
    {
      clearState();
      return false;
    }
    int i = Math.abs(paramMouseEvent.getX() - this.dndArmedEvent.getX());
    int j = Math.abs(paramMouseEvent.getY() - this.dndArmedEvent.getY());
    if ((i > this.motionThreshold) || (j > this.motionThreshold))
    {
      TransferHandler localTransferHandler = this.component.getTransferHandler();
      int k = mapDragOperationFromModifiers(paramMouseEvent, localTransferHandler);
      if (k != 0)
      {
        if (paramBeforeDrag != null) {
          paramBeforeDrag.dragStarting(this.dndArmedEvent);
        }
        localTransferHandler.exportAsDrag(this.component, this.dndArmedEvent, k);
        clearState();
      }
    }
    return true;
  }
  
  public static abstract interface BeforeDrag
  {
    public abstract void dragStarting(MouseEvent paramMouseEvent);
  }
}
