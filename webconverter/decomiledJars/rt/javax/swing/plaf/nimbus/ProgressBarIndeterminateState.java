package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarIndeterminateState
  extends State
{
  ProgressBarIndeterminateState()
  {
    super("Indeterminate");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JProgressBar)) && (((JProgressBar)paramJComponent).isIndeterminate());
  }
}
