package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RemovePermButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog td;
  private boolean edit;
  
  RemovePermButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean)
  {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.td = paramToolDialog;
    this.edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    TaggedList localTaggedList = (TaggedList)this.td.getComponent(8);
    int i = localTaggedList.getSelectedIndex();
    if (i < 0)
    {
      this.tw.displayErrorDialog(this.td, new Exception(PolicyTool.getMessage("No.permission.selected")));
      return;
    }
    localTaggedList.removeTaggedItem(i);
  }
}
