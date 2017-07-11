package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class SynthTableHeaderUI
  extends BasicTableHeaderUI
  implements PropertyChangeListener, SynthUI
{
  private TableCellRenderer prevRenderer = null;
  private SynthStyle style;
  
  public SynthTableHeaderUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthTableHeaderUI();
  }
  
  protected void installDefaults()
  {
    this.prevRenderer = this.header.getDefaultRenderer();
    if ((this.prevRenderer instanceof UIResource)) {
      this.header.setDefaultRenderer(new HeaderRenderer());
    }
    updateStyle(this.header);
  }
  
  private void updateStyle(JTableHeader paramJTableHeader)
  {
    SynthContext localSynthContext = getContext(paramJTableHeader, 1);
    SynthStyle localSynthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if ((this.style != localSynthStyle) && (localSynthStyle != null))
    {
      uninstallKeyboardActions();
      installKeyboardActions();
    }
    localSynthContext.dispose();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    this.header.addPropertyChangeListener(this);
  }
  
  protected void uninstallDefaults()
  {
    if ((this.header.getDefaultRenderer() instanceof HeaderRenderer)) {
      this.header.setDefaultRenderer(this.prevRenderer);
    }
    SynthContext localSynthContext = getContext(this.header, 1);
    this.style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners()
  {
    this.header.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintTableHeaderBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    super.paint(paramGraphics, paramSynthContext.getComponent());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintTableHeaderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, this.style, paramInt);
  }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2)
  {
    this.header.repaint(this.header.getHeaderRect(paramInt1));
    this.header.repaint(this.header.getHeaderRect(paramInt2));
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JTableHeader)paramPropertyChangeEvent.getSource());
    }
  }
  
  private class HeaderRenderer
    extends DefaultTableCellHeaderRenderer
  {
    HeaderRenderer()
    {
      setHorizontalAlignment(10);
      setName("TableHeader.renderer");
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      boolean bool = paramInt2 == SynthTableHeaderUI.this.getRolloverColumn();
      if ((paramBoolean1) || (bool) || (paramBoolean2)) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), paramBoolean1, paramBoolean2, paramJTable.isEnabled(), bool);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      }
      RowSorter localRowSorter = paramJTable == null ? null : paramJTable.getRowSorter();
      List localList = localRowSorter == null ? null : localRowSorter.getSortKeys();
      if ((localList != null) && (localList.size() > 0) && (((RowSorter.SortKey)localList.get(0)).getColumn() == paramJTable.convertColumnIndexToModel(paramInt2))) {}
      switch (SynthTableHeaderUI.1.$SwitchMap$javax$swing$SortOrder[((RowSorter.SortKey)localList.get(0)).getSortOrder().ordinal()])
      {
      case 1: 
        putClientProperty("Table.sortOrder", "ASCENDING");
        break;
      case 2: 
        putClientProperty("Table.sortOrder", "DESCENDING");
        break;
      case 3: 
        putClientProperty("Table.sortOrder", "UNSORTED");
        break;
      default: 
        throw new AssertionError("Cannot happen");
        putClientProperty("Table.sortOrder", "UNSORTED");
      }
      super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
      return this;
    }
    
    public void setBorder(Border paramBorder)
    {
      if ((paramBorder instanceof SynthBorder)) {
        super.setBorder(paramBorder);
      }
    }
  }
}
