package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import sun.swing.SwingUtilities2;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class WindowsTableHeaderUI
  extends BasicTableHeaderUI
{
  private TableCellRenderer originalHeaderRenderer;
  
  public WindowsTableHeaderUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTableHeaderUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    if (XPStyle.getXP() != null)
    {
      this.originalHeaderRenderer = this.header.getDefaultRenderer();
      if ((this.originalHeaderRenderer instanceof UIResource)) {
        this.header.setDefaultRenderer(new XPDefaultRenderer());
      }
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if ((this.header.getDefaultRenderer() instanceof XPDefaultRenderer)) {
      this.header.setDefaultRenderer(this.originalHeaderRenderer);
    }
    super.uninstallUI(paramJComponent);
  }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2)
  {
    if (XPStyle.getXP() != null)
    {
      this.header.repaint(this.header.getHeaderRect(paramInt1));
      this.header.repaint(this.header.getHeaderRect(paramInt2));
    }
  }
  
  private static class IconBorder
    implements Border, UIResource
  {
    private final Icon icon;
    private final int top;
    private final int left;
    private final int bottom;
    private final int right;
    
    public IconBorder(Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.icon = paramIcon;
      this.top = paramInt1;
      this.left = paramInt2;
      this.bottom = paramInt3;
      this.right = paramInt4;
    }
    
    public Insets getBorderInsets(Component paramComponent)
    {
      return new Insets(this.icon.getIconHeight() + this.top, this.left, this.bottom, this.right);
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.icon.paintIcon(paramComponent, paramGraphics, paramInt1 + this.left + (paramInt3 - this.left - this.right - this.icon.getIconWidth()) / 2, paramInt2 + this.top);
    }
  }
  
  private class XPDefaultRenderer
    extends DefaultTableCellHeaderRenderer
  {
    XPStyle.Skin skin;
    boolean isSelected;
    boolean hasFocus;
    boolean hasRollover;
    int column;
    
    XPDefaultRenderer()
    {
      setHorizontalAlignment(10);
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
      this.isSelected = paramBoolean1;
      this.hasFocus = paramBoolean2;
      this.column = paramInt2;
      this.hasRollover = (paramInt2 == WindowsTableHeaderUI.this.getRolloverColumn());
      if (this.skin == null)
      {
        localXPStyle = XPStyle.getXP();
        this.skin = (localXPStyle != null ? localXPStyle.getSkin(WindowsTableHeaderUI.this.header, TMSchema.Part.HP_HEADERITEM) : null);
      }
      XPStyle localXPStyle = this.skin != null ? this.skin.getContentMargin() : null;
      Object localObject = null;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      if (localXPStyle != null)
      {
        i = localXPStyle.top;
        j = localXPStyle.left;
        k = localXPStyle.bottom;
        m = localXPStyle.right;
      }
      j += 5;
      k += 4;
      m += 5;
      Icon localIcon;
      if ((WindowsLookAndFeel.isOnVista()) && ((((localIcon = getIcon()) instanceof UIResource)) || (localIcon == null)))
      {
        i++;
        setIcon(null);
        localIcon = null;
        SortOrder localSortOrder = getColumnSortOrder(paramJTable, paramInt2);
        if (localSortOrder != null) {
          switch (WindowsTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[localSortOrder.ordinal()])
          {
          case 1: 
            localIcon = UIManager.getIcon("Table.ascendingSortIcon");
            break;
          case 2: 
            localIcon = UIManager.getIcon("Table.descendingSortIcon");
          }
        }
        if (localIcon != null)
        {
          k = localIcon.getIconHeight();
          localObject = new WindowsTableHeaderUI.IconBorder(localIcon, i, j, k, m);
        }
        else
        {
          localIcon = UIManager.getIcon("Table.ascendingSortIcon");
          int n = localIcon != null ? localIcon.getIconHeight() : 0;
          if (n != 0) {
            k = n;
          }
          localObject = new EmptyBorder(n + i, j, k, m);
        }
      }
      else
      {
        i += 3;
        localObject = new EmptyBorder(i, j, k, m);
      }
      setBorder((Border)localObject);
      return this;
    }
    
    public void paint(Graphics paramGraphics)
    {
      Dimension localDimension = getSize();
      TMSchema.State localState = TMSchema.State.NORMAL;
      TableColumn localTableColumn = WindowsTableHeaderUI.this.header.getDraggedColumn();
      if ((localTableColumn != null) && (this.column == SwingUtilities2.convertColumnIndexToView(WindowsTableHeaderUI.this.header.getColumnModel(), localTableColumn.getModelIndex()))) {
        localState = TMSchema.State.PRESSED;
      } else if ((this.isSelected) || (this.hasFocus) || (this.hasRollover)) {
        localState = TMSchema.State.HOT;
      }
      if (WindowsLookAndFeel.isOnVista())
      {
        SortOrder localSortOrder = getColumnSortOrder(WindowsTableHeaderUI.this.header.getTable(), this.column);
        if (localSortOrder != null) {
          switch (WindowsTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[localSortOrder.ordinal()])
          {
          case 1: 
          case 2: 
            switch (WindowsTableHeaderUI.1.$SwitchMap$com$sun$java$swing$plaf$windows$TMSchema$State[localState.ordinal()])
            {
            case 1: 
              localState = TMSchema.State.SORTEDNORMAL;
              break;
            case 2: 
              localState = TMSchema.State.SORTEDPRESSED;
              break;
            case 3: 
              localState = TMSchema.State.SORTEDHOT;
            }
            break;
          }
        }
      }
      this.skin.paintSkin(paramGraphics, 0, 0, localDimension.width - 1, localDimension.height - 1, localState);
      super.paint(paramGraphics);
    }
  }
}
