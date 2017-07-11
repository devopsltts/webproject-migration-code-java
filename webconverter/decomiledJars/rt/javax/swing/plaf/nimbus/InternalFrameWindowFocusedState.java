package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameWindowFocusedState
  extends State
{
  InternalFrameWindowFocusedState()
  {
    super("WindowFocused");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JInternalFrame)) && (((JInternalFrame)paramJComponent).isSelected());
  }
}
