package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

class SplitPaneDividerVerticalState
  extends State
{
  SplitPaneDividerVerticalState()
  {
    super("Vertical");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JSplitPane)) && (((JSplitPane)paramJComponent).getOrientation() == 1);
  }
}
