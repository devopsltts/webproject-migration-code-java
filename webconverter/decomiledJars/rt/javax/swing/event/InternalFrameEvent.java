package javax.swing.event;

import java.awt.AWTEvent;
import javax.swing.JInternalFrame;

public class InternalFrameEvent
  extends AWTEvent
{
  public static final int INTERNAL_FRAME_FIRST = 25549;
  public static final int INTERNAL_FRAME_LAST = 25555;
  public static final int INTERNAL_FRAME_OPENED = 25549;
  public static final int INTERNAL_FRAME_CLOSING = 25550;
  public static final int INTERNAL_FRAME_CLOSED = 25551;
  public static final int INTERNAL_FRAME_ICONIFIED = 25552;
  public static final int INTERNAL_FRAME_DEICONIFIED = 25553;
  public static final int INTERNAL_FRAME_ACTIVATED = 25554;
  public static final int INTERNAL_FRAME_DEACTIVATED = 25555;
  
  public InternalFrameEvent(JInternalFrame paramJInternalFrame, int paramInt)
  {
    super(paramJInternalFrame, paramInt);
  }
  
  public String paramString()
  {
    String str;
    switch (this.id)
    {
    case 25549: 
      str = "INTERNAL_FRAME_OPENED";
      break;
    case 25550: 
      str = "INTERNAL_FRAME_CLOSING";
      break;
    case 25551: 
      str = "INTERNAL_FRAME_CLOSED";
      break;
    case 25552: 
      str = "INTERNAL_FRAME_ICONIFIED";
      break;
    case 25553: 
      str = "INTERNAL_FRAME_DEICONIFIED";
      break;
    case 25554: 
      str = "INTERNAL_FRAME_ACTIVATED";
      break;
    case 25555: 
      str = "INTERNAL_FRAME_DEACTIVATED";
      break;
    default: 
      str = "unknown type";
    }
    return str;
  }
  
  public JInternalFrame getInternalFrame()
  {
    return (this.source instanceof JInternalFrame) ? (JInternalFrame)this.source : null;
  }
}
