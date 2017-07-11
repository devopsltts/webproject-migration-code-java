package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PolicyListListener
  extends MouseAdapter
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  
  PolicyListListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow)
  {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
    localToolDialog.displayPolicyEntryDialog(true);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent.getClickCount() == 2) {
      actionPerformed(null);
    }
  }
}
