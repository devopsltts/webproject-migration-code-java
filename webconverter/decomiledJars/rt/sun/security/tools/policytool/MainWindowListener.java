package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import javax.swing.JList;

class MainWindowListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  
  MainWindowListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow)
  {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    Object localObject;
    if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Add.Policy.Entry") == 0)
    {
      localObject = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
      ((ToolDialog)localObject).displayPolicyEntryDialog(false);
    }
    else
    {
      int i;
      ToolDialog localToolDialog;
      if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Remove.Policy.Entry") == 0)
      {
        localObject = (JList)this.tw.getComponent(3);
        i = ((JList)localObject).getSelectedIndex();
        if (i < 0)
        {
          this.tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
          return;
        }
        localToolDialog = new ToolDialog(PolicyTool.getMessage("Remove.Policy.Entry"), this.tool, this.tw, true);
        localToolDialog.displayConfirmRemovePolicyEntry();
      }
      else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Edit.Policy.Entry") == 0)
      {
        localObject = (JList)this.tw.getComponent(3);
        i = ((JList)localObject).getSelectedIndex();
        if (i < 0)
        {
          this.tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
          return;
        }
        localToolDialog = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
        localToolDialog.displayPolicyEntryDialog(true);
      }
      else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Edit") == 0)
      {
        localObject = new ToolDialog(PolicyTool.getMessage("KeyStore"), this.tool, this.tw, true);
        ((ToolDialog)localObject).keyStoreDialog(0);
      }
    }
  }
}
