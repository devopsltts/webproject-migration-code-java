package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.FontUIResource;

public class DefaultTreeCellEditor
  implements ActionListener, TreeCellEditor, TreeSelectionListener
{
  protected TreeCellEditor realEditor;
  protected DefaultTreeCellRenderer renderer;
  protected Container editingContainer;
  protected transient Component editingComponent;
  protected boolean canEdit;
  protected transient int offset;
  protected transient JTree tree;
  protected transient TreePath lastPath;
  protected transient Timer timer;
  protected transient int lastRow;
  protected Color borderSelectionColor;
  protected transient Icon editingIcon;
  protected Font font;
  
  public DefaultTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer)
  {
    this(paramJTree, paramDefaultTreeCellRenderer, null);
  }
  
  public DefaultTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer, TreeCellEditor paramTreeCellEditor)
  {
    this.renderer = paramDefaultTreeCellRenderer;
    this.realEditor = paramTreeCellEditor;
    if (this.realEditor == null) {
      this.realEditor = createTreeCellEditor();
    }
    this.editingContainer = createContainer();
    setTree(paramJTree);
    setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
  }
  
  public void setBorderSelectionColor(Color paramColor)
  {
    this.borderSelectionColor = paramColor;
  }
  
  public Color getBorderSelectionColor()
  {
    return this.borderSelectionColor;
  }
  
  public void setFont(Font paramFont)
  {
    this.font = paramFont;
  }
  
  public Font getFont()
  {
    return this.font;
  }
  
  public Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    setTree(paramJTree);
    this.lastRow = paramInt;
    determineOffset(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    if (this.editingComponent != null) {
      this.editingContainer.remove(this.editingComponent);
    }
    this.editingComponent = this.realEditor.getTreeCellEditorComponent(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    TreePath localTreePath = paramJTree.getPathForRow(paramInt);
    this.canEdit = ((this.lastPath != null) && (localTreePath != null) && (this.lastPath.equals(localTreePath)));
    Font localFont = getFont();
    if (localFont == null)
    {
      if (this.renderer != null) {
        localFont = this.renderer.getFont();
      }
      if (localFont == null) {
        localFont = paramJTree.getFont();
      }
    }
    this.editingContainer.setFont(localFont);
    prepareForEditing();
    return this.editingContainer;
  }
  
  public Object getCellEditorValue()
  {
    return this.realEditor.getCellEditorValue();
  }
  
  public boolean isCellEditable(EventObject paramEventObject)
  {
    boolean bool1 = false;
    int i = 0;
    if ((paramEventObject != null) && ((paramEventObject.getSource() instanceof JTree)))
    {
      setTree((JTree)paramEventObject.getSource());
      if ((paramEventObject instanceof MouseEvent))
      {
        TreePath localTreePath = this.tree.getPathForLocation(((MouseEvent)paramEventObject).getX(), ((MouseEvent)paramEventObject).getY());
        i = (this.lastPath != null) && (localTreePath != null) && (this.lastPath.equals(localTreePath)) ? 1 : 0;
        if (localTreePath != null)
        {
          this.lastRow = this.tree.getRowForPath(localTreePath);
          Object localObject = localTreePath.getLastPathComponent();
          boolean bool2 = this.tree.isRowSelected(this.lastRow);
          boolean bool3 = this.tree.isExpanded(localTreePath);
          TreeModel localTreeModel = this.tree.getModel();
          boolean bool4 = localTreeModel.isLeaf(localObject);
          determineOffset(this.tree, localObject, bool2, bool3, bool4, this.lastRow);
        }
      }
    }
    if (!this.realEditor.isCellEditable(paramEventObject)) {
      return false;
    }
    if (canEditImmediately(paramEventObject)) {
      bool1 = true;
    } else if ((i != 0) && (shouldStartEditingTimer(paramEventObject))) {
      startEditingTimer();
    } else if ((this.timer != null) && (this.timer.isRunning())) {
      this.timer.stop();
    }
    if (bool1) {
      prepareForEditing();
    }
    return bool1;
  }
  
  public boolean shouldSelectCell(EventObject paramEventObject)
  {
    return this.realEditor.shouldSelectCell(paramEventObject);
  }
  
  public boolean stopCellEditing()
  {
    if (this.realEditor.stopCellEditing())
    {
      cleanupAfterEditing();
      return true;
    }
    return false;
  }
  
  public void cancelCellEditing()
  {
    this.realEditor.cancelCellEditing();
    cleanupAfterEditing();
  }
  
  public void addCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    this.realEditor.addCellEditorListener(paramCellEditorListener);
  }
  
  public void removeCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    this.realEditor.removeCellEditorListener(paramCellEditorListener);
  }
  
  public CellEditorListener[] getCellEditorListeners()
  {
    return ((DefaultCellEditor)this.realEditor).getCellEditorListeners();
  }
  
  public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
  {
    if (this.tree != null) {
      if (this.tree.getSelectionCount() == 1) {
        this.lastPath = this.tree.getSelectionPath();
      } else {
        this.lastPath = null;
      }
    }
    if (this.timer != null) {
      this.timer.stop();
    }
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if ((this.tree != null) && (this.lastPath != null)) {
      this.tree.startEditingAtPath(this.lastPath);
    }
  }
  
  protected void setTree(JTree paramJTree)
  {
    if (this.tree != paramJTree)
    {
      if (this.tree != null) {
        this.tree.removeTreeSelectionListener(this);
      }
      this.tree = paramJTree;
      if (this.tree != null) {
        this.tree.addTreeSelectionListener(this);
      }
      if (this.timer != null) {
        this.timer.stop();
      }
    }
  }
  
  protected boolean shouldStartEditingTimer(EventObject paramEventObject)
  {
    if (((paramEventObject instanceof MouseEvent)) && (SwingUtilities.isLeftMouseButton((MouseEvent)paramEventObject)))
    {
      MouseEvent localMouseEvent = (MouseEvent)paramEventObject;
      return (localMouseEvent.getClickCount() == 1) && (inHitRegion(localMouseEvent.getX(), localMouseEvent.getY()));
    }
    return false;
  }
  
  protected void startEditingTimer()
  {
    if (this.timer == null)
    {
      this.timer = new Timer(1200, this);
      this.timer.setRepeats(false);
    }
    this.timer.start();
  }
  
  protected boolean canEditImmediately(EventObject paramEventObject)
  {
    if (((paramEventObject instanceof MouseEvent)) && (SwingUtilities.isLeftMouseButton((MouseEvent)paramEventObject)))
    {
      MouseEvent localMouseEvent = (MouseEvent)paramEventObject;
      return (localMouseEvent.getClickCount() > 2) && (inHitRegion(localMouseEvent.getX(), localMouseEvent.getY()));
    }
    return paramEventObject == null;
  }
  
  protected boolean inHitRegion(int paramInt1, int paramInt2)
  {
    if ((this.lastRow != -1) && (this.tree != null))
    {
      Rectangle localRectangle = this.tree.getRowBounds(this.lastRow);
      ComponentOrientation localComponentOrientation = this.tree.getComponentOrientation();
      if (localComponentOrientation.isLeftToRight())
      {
        if ((localRectangle != null) && (paramInt1 <= localRectangle.x + this.offset) && (this.offset < localRectangle.width - 5)) {
          return false;
        }
      }
      else if ((localRectangle != null) && ((paramInt1 >= localRectangle.x + localRectangle.width - this.offset + 5) || (paramInt1 <= localRectangle.x + 5)) && (this.offset < localRectangle.width - 5)) {
        return false;
      }
    }
    return true;
  }
  
  protected void determineOffset(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    if (this.renderer != null)
    {
      if (paramBoolean3) {
        this.editingIcon = this.renderer.getLeafIcon();
      } else if (paramBoolean2) {
        this.editingIcon = this.renderer.getOpenIcon();
      } else {
        this.editingIcon = this.renderer.getClosedIcon();
      }
      if (this.editingIcon != null) {
        this.offset = (this.renderer.getIconTextGap() + this.editingIcon.getIconWidth());
      } else {
        this.offset = this.renderer.getIconTextGap();
      }
    }
    else
    {
      this.editingIcon = null;
      this.offset = 0;
    }
  }
  
  protected void prepareForEditing()
  {
    if (this.editingComponent != null) {
      this.editingContainer.add(this.editingComponent);
    }
  }
  
  protected Container createContainer()
  {
    return new EditorContainer();
  }
  
  protected TreeCellEditor createTreeCellEditor()
  {
    Border localBorder = UIManager.getBorder("Tree.editorBorder");
    DefaultCellEditor local1 = new DefaultCellEditor(new DefaultTextField(localBorder))
    {
      public boolean shouldSelectCell(EventObject paramAnonymousEventObject)
      {
        boolean bool = super.shouldSelectCell(paramAnonymousEventObject);
        return bool;
      }
    };
    local1.setClickCountToStart(1);
    return local1;
  }
  
  private void cleanupAfterEditing()
  {
    if (this.editingComponent != null) {
      this.editingContainer.remove(this.editingComponent);
    }
    this.editingComponent = null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if ((this.realEditor != null) && ((this.realEditor instanceof Serializable)))
    {
      localVector.addElement("realEditor");
      localVector.addElement(this.realEditor);
    }
    paramObjectOutputStream.writeObject(localVector);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Vector localVector = (Vector)paramObjectInputStream.readObject();
    int i = 0;
    int j = localVector.size();
    if ((i < j) && (localVector.elementAt(i).equals("realEditor")))
    {
      this.realEditor = ((TreeCellEditor)localVector.elementAt(++i));
      i++;
    }
  }
  
  public class DefaultTextField
    extends JTextField
  {
    protected Border border;
    
    public DefaultTextField(Border paramBorder)
    {
      setBorder(paramBorder);
    }
    
    public void setBorder(Border paramBorder)
    {
      super.setBorder(paramBorder);
      this.border = paramBorder;
    }
    
    public Border getBorder()
    {
      return this.border;
    }
    
    public Font getFont()
    {
      Font localFont = super.getFont();
      if ((localFont instanceof FontUIResource))
      {
        Container localContainer = getParent();
        if ((localContainer != null) && (localContainer.getFont() != null)) {
          localFont = localContainer.getFont();
        }
      }
      return localFont;
    }
    
    public Dimension getPreferredSize()
    {
      Dimension localDimension1 = super.getPreferredSize();
      if ((DefaultTreeCellEditor.this.renderer != null) && (DefaultTreeCellEditor.this.getFont() == null))
      {
        Dimension localDimension2 = DefaultTreeCellEditor.this.renderer.getPreferredSize();
        localDimension1.height = localDimension2.height;
      }
      return localDimension1;
    }
  }
  
  public class EditorContainer
    extends Container
  {
    public EditorContainer()
    {
      setLayout(null);
    }
    
    public void EditorContainer()
    {
      setLayout(null);
    }
    
    public void paint(Graphics paramGraphics)
    {
      int i = getWidth();
      int j = getHeight();
      if (DefaultTreeCellEditor.this.editingIcon != null)
      {
        int k = calculateIconY(DefaultTreeCellEditor.this.editingIcon);
        if (getComponentOrientation().isLeftToRight()) {
          DefaultTreeCellEditor.this.editingIcon.paintIcon(this, paramGraphics, 0, k);
        } else {
          DefaultTreeCellEditor.this.editingIcon.paintIcon(this, paramGraphics, i - DefaultTreeCellEditor.this.editingIcon.getIconWidth(), k);
        }
      }
      Color localColor = DefaultTreeCellEditor.this.getBorderSelectionColor();
      if (localColor != null)
      {
        paramGraphics.setColor(localColor);
        paramGraphics.drawRect(0, 0, i - 1, j - 1);
      }
      super.paint(paramGraphics);
    }
    
    public void doLayout()
    {
      if (DefaultTreeCellEditor.this.editingComponent != null)
      {
        int i = getWidth();
        int j = getHeight();
        if (getComponentOrientation().isLeftToRight()) {
          DefaultTreeCellEditor.this.editingComponent.setBounds(DefaultTreeCellEditor.this.offset, 0, i - DefaultTreeCellEditor.this.offset, j);
        } else {
          DefaultTreeCellEditor.this.editingComponent.setBounds(0, 0, i - DefaultTreeCellEditor.this.offset, j);
        }
      }
    }
    
    private int calculateIconY(Icon paramIcon)
    {
      int i = paramIcon.getIconHeight();
      int j = DefaultTreeCellEditor.this.editingComponent.getFontMetrics(DefaultTreeCellEditor.this.editingComponent.getFont()).getHeight();
      int k = i / 2 - j / 2;
      int m = Math.min(0, k);
      int n = Math.max(i, k + j) - m;
      return getHeight() / 2 - (m + n / 2);
    }
    
    public Dimension getPreferredSize()
    {
      if (DefaultTreeCellEditor.this.editingComponent != null)
      {
        Dimension localDimension = DefaultTreeCellEditor.this.editingComponent.getPreferredSize();
        localDimension.width += DefaultTreeCellEditor.this.offset + 5;
        Object localObject = DefaultTreeCellEditor.this.renderer != null ? DefaultTreeCellEditor.this.renderer.getPreferredSize() : null;
        if (localObject != null) {
          localDimension.height = Math.max(localDimension.height, localObject.height);
        }
        if (DefaultTreeCellEditor.this.editingIcon != null) {
          localDimension.height = Math.max(localDimension.height, DefaultTreeCellEditor.this.editingIcon.getIconHeight());
        }
        localDimension.width = Math.max(localDimension.width, 100);
        return localDimension;
      }
      return new Dimension(0, 0);
    }
  }
}
