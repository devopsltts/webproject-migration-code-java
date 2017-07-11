package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ComponentUI;

public class JRadioButtonMenuItem
  extends JMenuItem
  implements Accessible
{
  private static final String uiClassID = "RadioButtonMenuItemUI";
  
  public JRadioButtonMenuItem()
  {
    this(null, null, false);
  }
  
  public JRadioButtonMenuItem(Icon paramIcon)
  {
    this(null, paramIcon, false);
  }
  
  public JRadioButtonMenuItem(String paramString)
  {
    this(paramString, null, false);
  }
  
  public JRadioButtonMenuItem(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JRadioButtonMenuItem(String paramString, Icon paramIcon)
  {
    this(paramString, paramIcon, false);
  }
  
  public JRadioButtonMenuItem(String paramString, boolean paramBoolean)
  {
    this(paramString);
    setSelected(paramBoolean);
  }
  
  public JRadioButtonMenuItem(Icon paramIcon, boolean paramBoolean)
  {
    this(null, paramIcon, paramBoolean);
  }
  
  public JRadioButtonMenuItem(String paramString, Icon paramIcon, boolean paramBoolean)
  {
    super(paramString, paramIcon);
    setModel(new JToggleButton.ToggleButtonModel());
    setSelected(paramBoolean);
    setFocusable(false);
  }
  
  public String getUIClassID()
  {
    return "RadioButtonMenuItemUI";
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("RadioButtonMenuItemUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (this.ui != null)) {
        this.ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  boolean shouldUpdateSelectedStateFromAction()
  {
    return true;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJRadioButtonMenuItem();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleJRadioButtonMenuItem
    extends JMenuItem.AccessibleJMenuItem
  {
    protected AccessibleJRadioButtonMenuItem()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.RADIO_BUTTON;
    }
  }
}
