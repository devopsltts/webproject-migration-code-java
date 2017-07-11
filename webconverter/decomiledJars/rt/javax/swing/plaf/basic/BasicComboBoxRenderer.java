package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

public class BasicComboBoxRenderer
  extends JLabel
  implements ListCellRenderer, Serializable
{
  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  public BasicComboBoxRenderer()
  {
    setOpaque(true);
    setBorder(getNoFocusBorder());
  }
  
  private static Border getNoFocusBorder()
  {
    if (System.getSecurityManager() != null) {
      return SAFE_NO_FOCUS_BORDER;
    }
    return noFocusBorder;
  }
  
  public Dimension getPreferredSize()
  {
    Dimension localDimension;
    if ((getText() == null) || (getText().equals("")))
    {
      setText(" ");
      localDimension = super.getPreferredSize();
      setText("");
    }
    else
    {
      localDimension = super.getPreferredSize();
    }
    return localDimension;
  }
  
  public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      setBackground(paramJList.getSelectionBackground());
      setForeground(paramJList.getSelectionForeground());
    }
    else
    {
      setBackground(paramJList.getBackground());
      setForeground(paramJList.getForeground());
    }
    setFont(paramJList.getFont());
    if ((paramObject instanceof Icon)) {
      setIcon((Icon)paramObject);
    } else {
      setText(paramObject == null ? "" : paramObject.toString());
    }
    return this;
  }
  
  public static class UIResource
    extends BasicComboBoxRenderer
    implements UIResource
  {
    public UIResource() {}
  }
}
