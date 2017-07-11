package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import sun.reflect.misc.MethodUtil;

public class BasicComboBoxEditor
  implements ComboBoxEditor, FocusListener
{
  protected JTextField editor = createEditorComponent();
  private Object oldValue;
  
  public BasicComboBoxEditor() {}
  
  public Component getEditorComponent()
  {
    return this.editor;
  }
  
  protected JTextField createEditorComponent()
  {
    BorderlessTextField localBorderlessTextField = new BorderlessTextField("", 9);
    localBorderlessTextField.setBorder(null);
    return localBorderlessTextField;
  }
  
  public void setItem(Object paramObject)
  {
    String str;
    if (paramObject != null)
    {
      str = paramObject.toString();
      if (str == null) {
        str = "";
      }
      this.oldValue = paramObject;
    }
    else
    {
      str = "";
    }
    if (!str.equals(this.editor.getText())) {
      this.editor.setText(str);
    }
  }
  
  public Object getItem()
  {
    Object localObject = this.editor.getText();
    if ((this.oldValue != null) && (!(this.oldValue instanceof String)))
    {
      if (localObject.equals(this.oldValue.toString())) {
        return this.oldValue;
      }
      Class localClass = this.oldValue.getClass();
      try
      {
        Method localMethod = MethodUtil.getMethod(localClass, "valueOf", new Class[] { String.class });
        localObject = MethodUtil.invoke(localMethod, this.oldValue, new Object[] { this.editor.getText() });
      }
      catch (Exception localException) {}
    }
    return localObject;
  }
  
  public void selectAll()
  {
    this.editor.selectAll();
    this.editor.requestFocus();
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {}
  
  public void focusLost(FocusEvent paramFocusEvent) {}
  
  public void addActionListener(ActionListener paramActionListener)
  {
    this.editor.addActionListener(paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener)
  {
    this.editor.removeActionListener(paramActionListener);
  }
  
  static class BorderlessTextField
    extends JTextField
  {
    public BorderlessTextField(String paramString, int paramInt)
    {
      super(paramInt);
    }
    
    public void setText(String paramString)
    {
      if (getText().equals(paramString)) {
        return;
      }
      super.setText(paramString);
    }
    
    public void setBorder(Border paramBorder)
    {
      if (!(paramBorder instanceof BasicComboBoxEditor.UIResource)) {
        super.setBorder(paramBorder);
      }
    }
  }
  
  public static class UIResource
    extends BasicComboBoxEditor
    implements UIResource
  {
    public UIResource() {}
  }
}
