package javax.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import sun.swing.SwingUtilities2;

class ColorChooserDialog
  extends JDialog
{
  private Color initialColor;
  private JColorChooser chooserPane;
  private JButton cancelButton;
  
  public ColorChooserDialog(Dialog paramDialog, String paramString, boolean paramBoolean, Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
    throws HeadlessException
  {
    super(paramDialog, paramString, paramBoolean);
    initColorChooserDialog(paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
  }
  
  public ColorChooserDialog(Frame paramFrame, String paramString, boolean paramBoolean, Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
    throws HeadlessException
  {
    super(paramFrame, paramString, paramBoolean);
    initColorChooserDialog(paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
  }
  
  protected void initColorChooserDialog(Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
  {
    this.chooserPane = paramJColorChooser;
    Locale localLocale = getLocale();
    String str1 = UIManager.getString("ColorChooser.okText", localLocale);
    String str2 = UIManager.getString("ColorChooser.cancelText", localLocale);
    String str3 = UIManager.getString("ColorChooser.resetText", localLocale);
    Container localContainer = getContentPane();
    localContainer.setLayout(new BorderLayout());
    localContainer.add(paramJColorChooser, "Center");
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new FlowLayout(1));
    JButton localJButton1 = new JButton(str1);
    getRootPane().setDefaultButton(localJButton1);
    localJButton1.getAccessibleContext().setAccessibleDescription(str1);
    localJButton1.setActionCommand("OK");
    localJButton1.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ColorChooserDialog.this.hide();
      }
    });
    if (paramActionListener1 != null) {
      localJButton1.addActionListener(paramActionListener1);
    }
    localJPanel.add(localJButton1);
    this.cancelButton = new JButton(str2);
    this.cancelButton.getAccessibleContext().setAccessibleDescription(str2);
    AbstractAction local2 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ((AbstractButton)paramAnonymousActionEvent.getSource()).fireActionPerformed(paramAnonymousActionEvent);
      }
    };
    KeyStroke localKeyStroke = KeyStroke.getKeyStroke(27, 0);
    InputMap localInputMap = this.cancelButton.getInputMap(2);
    ActionMap localActionMap = this.cancelButton.getActionMap();
    if ((localInputMap != null) && (localActionMap != null))
    {
      localInputMap.put(localKeyStroke, "cancel");
      localActionMap.put("cancel", local2);
    }
    this.cancelButton.setActionCommand("cancel");
    this.cancelButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ColorChooserDialog.this.hide();
      }
    });
    if (paramActionListener2 != null) {
      this.cancelButton.addActionListener(paramActionListener2);
    }
    localJPanel.add(this.cancelButton);
    JButton localJButton2 = new JButton(str3);
    localJButton2.getAccessibleContext().setAccessibleDescription(str3);
    localJButton2.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ColorChooserDialog.this.reset();
      }
    });
    int i = SwingUtilities2.getUIDefaultsInt("ColorChooser.resetMnemonic", localLocale, -1);
    if (i != -1) {
      localJButton2.setMnemonic(i);
    }
    localJPanel.add(localJButton2);
    localContainer.add(localJPanel, "South");
    if (JDialog.isDefaultLookAndFeelDecorated())
    {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool) {
        getRootPane().setWindowDecorationStyle(5);
      }
    }
    applyComponentOrientation((paramComponent == null ? getRootPane() : paramComponent).getComponentOrientation());
    pack();
    setLocationRelativeTo(paramComponent);
    addWindowListener(new Closer());
  }
  
  public void show()
  {
    this.initialColor = this.chooserPane.getColor();
    super.show();
  }
  
  public void reset()
  {
    this.chooserPane.setColor(this.initialColor);
  }
  
  class Closer
    extends WindowAdapter
    implements Serializable
  {
    Closer() {}
    
    public void windowClosing(WindowEvent paramWindowEvent)
    {
      ColorChooserDialog.this.cancelButton.doClick(0);
      Window localWindow = paramWindowEvent.getWindow();
      localWindow.hide();
    }
  }
  
  static class DisposeOnClose
    extends ComponentAdapter
    implements Serializable
  {
    DisposeOnClose() {}
    
    public void componentHidden(ComponentEvent paramComponentEvent)
    {
      Window localWindow = (Window)paramComponentEvent.getComponent();
      localWindow.dispose();
    }
  }
}
