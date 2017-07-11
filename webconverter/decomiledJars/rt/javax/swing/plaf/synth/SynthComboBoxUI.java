package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.CellRendererPane;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class SynthComboBoxUI
  extends BasicComboBoxUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private boolean useListColors;
  Insets popupInsets;
  private boolean buttonWhenNotEditable;
  private boolean pressedWhenPopupVisible;
  private ButtonHandler buttonHandler;
  private EditorFocusHandler editorFocusHandler;
  private boolean forceOpaque = false;
  
  public SynthComboBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthComboBoxUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    this.buttonHandler = new ButtonHandler(null);
    super.installUI(paramJComponent);
  }
  
  protected void installDefaults()
  {
    updateStyle(this.comboBox);
  }
  
  private void updateStyle(JComboBox paramJComboBox)
  {
    SynthStyle localSynthStyle = this.style;
    SynthContext localSynthContext = getContext(paramJComboBox, 1);
    this.style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (this.style != localSynthStyle)
    {
      this.padding = ((Insets)this.style.get(localSynthContext, "ComboBox.padding"));
      this.popupInsets = ((Insets)this.style.get(localSynthContext, "ComboBox.popupInsets"));
      this.useListColors = this.style.getBoolean(localSynthContext, "ComboBox.rendererUseListColors", true);
      this.buttonWhenNotEditable = this.style.getBoolean(localSynthContext, "ComboBox.buttonWhenNotEditable", false);
      this.pressedWhenPopupVisible = this.style.getBoolean(localSynthContext, "ComboBox.pressedWhenPopupVisible", false);
      this.squareButton = this.style.getBoolean(localSynthContext, "ComboBox.squareButton", true);
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
      this.forceOpaque = this.style.getBoolean(localSynthContext, "ComboBox.forceOpaque", false);
    }
    localSynthContext.dispose();
    if (this.listBox != null) {
      SynthLookAndFeel.updateStyles(this.listBox);
    }
  }
  
  protected void installListeners()
  {
    this.comboBox.addPropertyChangeListener(this);
    this.comboBox.addMouseListener(this.buttonHandler);
    this.editorFocusHandler = new EditorFocusHandler(this.comboBox, null);
    super.installListeners();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if ((this.popup instanceof SynthComboPopup)) {
      ((SynthComboPopup)this.popup).removePopupMenuListener(this.buttonHandler);
    }
    super.uninstallUI(paramJComponent);
    this.buttonHandler = null;
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(this.comboBox, 1);
    this.style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners()
  {
    this.editorFocusHandler.unregister();
    this.comboBox.removePropertyChangeListener(this);
    this.comboBox.removeMouseListener(this.buttonHandler);
    this.buttonHandler.pressed = false;
    this.buttonHandler.over = false;
    super.uninstallListeners();
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, this.style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    if (!(paramJComponent instanceof JComboBox)) {
      return SynthLookAndFeel.getComponentState(paramJComponent);
    }
    JComboBox localJComboBox = (JComboBox)paramJComponent;
    if (shouldActLikeButton())
    {
      i = 1;
      if (!paramJComponent.isEnabled()) {
        i = 8;
      }
      if (this.buttonHandler.isPressed()) {
        i |= 0x4;
      }
      if (this.buttonHandler.isRollover()) {
        i |= 0x2;
      }
      if (localJComboBox.isFocusOwner()) {
        i |= 0x100;
      }
      return i;
    }
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if ((localJComboBox.isEditable()) && (localJComboBox.getEditor().getEditorComponent().isFocusOwner())) {
      i |= 0x100;
    }
    return i;
  }
  
  protected ComboPopup createPopup()
  {
    SynthComboPopup localSynthComboPopup = new SynthComboPopup(this.comboBox);
    localSynthComboPopup.addPopupMenuListener(this.buttonHandler);
    return localSynthComboPopup;
  }
  
  protected ListCellRenderer createRenderer()
  {
    return new SynthComboBoxRenderer();
  }
  
  protected ComboBoxEditor createEditor()
  {
    return new SynthComboBoxEditor(null);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle(this.comboBox);
    }
  }
  
  protected JButton createArrowButton()
  {
    SynthArrowButton localSynthArrowButton = new SynthArrowButton(5);
    localSynthArrowButton.setName("ComboBox.arrowButton");
    localSynthArrowButton.setModel(this.buttonHandler);
    return localSynthArrowButton;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintComboBoxBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    this.hasFocus = this.comboBox.hasFocus();
    if (!this.comboBox.isEditable())
    {
      Rectangle localRectangle = rectangleForCurrentValue();
      paintCurrentValue(paramGraphics, localRectangle, this.hasFocus);
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintComboBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    ListCellRenderer localListCellRenderer = this.comboBox.getRenderer();
    Component localComponent = localListCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
    boolean bool = false;
    if ((localComponent instanceof JPanel)) {
      bool = true;
    }
    if ((localComponent instanceof UIResource)) {
      localComponent.setName("ComboBox.renderer");
    }
    int i = (this.forceOpaque) && ((localComponent instanceof JComponent)) ? 1 : 0;
    if (i != 0) {
      ((JComponent)localComponent).setOpaque(false);
    }
    int j = paramRectangle.x;
    int k = paramRectangle.y;
    int m = paramRectangle.width;
    int n = paramRectangle.height;
    if (this.padding != null)
    {
      j = paramRectangle.x + this.padding.left;
      k = paramRectangle.y + this.padding.top;
      m = paramRectangle.width - (this.padding.left + this.padding.right);
      n = paramRectangle.height - (this.padding.top + this.padding.bottom);
    }
    this.currentValuePane.paintComponent(paramGraphics, localComponent, this.comboBox, j, k, m, n, bool);
    if (i != 0) {
      ((JComponent)localComponent).setOpaque(true);
    }
  }
  
  private boolean shouldActLikeButton()
  {
    return (this.buttonWhenNotEditable) && (!this.comboBox.isEditable());
  }
  
  protected Dimension getDefaultSize()
  {
    SynthComboBoxRenderer localSynthComboBoxRenderer = new SynthComboBoxRenderer();
    Dimension localDimension = getSizeForComponent(localSynthComboBoxRenderer.getListCellRendererComponent(this.listBox, " ", -1, false, false));
    return new Dimension(localDimension.width, localDimension.height);
  }
  
  private final class ButtonHandler
    extends DefaultButtonModel
    implements MouseListener, PopupMenuListener
  {
    private boolean over;
    private boolean pressed;
    
    private ButtonHandler() {}
    
    private void updatePressed(boolean paramBoolean)
    {
      this.pressed = ((paramBoolean) && (isEnabled()));
      if (SynthComboBoxUI.this.shouldActLikeButton()) {
        SynthComboBoxUI.this.comboBox.repaint();
      }
    }
    
    private void updateOver(boolean paramBoolean)
    {
      boolean bool1 = isRollover();
      this.over = ((paramBoolean) && (isEnabled()));
      boolean bool2 = isRollover();
      if ((SynthComboBoxUI.this.shouldActLikeButton()) && (bool1 != bool2)) {
        SynthComboBoxUI.this.comboBox.repaint();
      }
    }
    
    public boolean isPressed()
    {
      boolean bool = SynthComboBoxUI.this.shouldActLikeButton() ? this.pressed : super.isPressed();
      return (bool) || ((SynthComboBoxUI.this.pressedWhenPopupVisible) && (SynthComboBoxUI.this.comboBox.isPopupVisible()));
    }
    
    public boolean isArmed()
    {
      int i = (SynthComboBoxUI.this.shouldActLikeButton()) || ((SynthComboBoxUI.this.pressedWhenPopupVisible) && (SynthComboBoxUI.this.comboBox.isPopupVisible())) ? 1 : 0;
      return i != 0 ? isPressed() : super.isArmed();
    }
    
    public boolean isRollover()
    {
      return SynthComboBoxUI.this.shouldActLikeButton() ? this.over : super.isRollover();
    }
    
    public void setPressed(boolean paramBoolean)
    {
      super.setPressed(paramBoolean);
      updatePressed(paramBoolean);
    }
    
    public void setRollover(boolean paramBoolean)
    {
      super.setRollover(paramBoolean);
      updateOver(paramBoolean);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      updateOver(true);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      updateOver(false);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      updatePressed(true);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      updatePressed(false);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent)
    {
      if ((SynthComboBoxUI.this.shouldActLikeButton()) || (SynthComboBoxUI.this.pressedWhenPopupVisible)) {
        SynthComboBoxUI.this.comboBox.repaint();
      }
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent) {}
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {}
  }
  
  private static class EditorFocusHandler
    implements FocusListener, PropertyChangeListener
  {
    private JComboBox comboBox;
    private ComboBoxEditor editor = null;
    private Component editorComponent = null;
    
    private EditorFocusHandler(JComboBox paramJComboBox)
    {
      this.comboBox = paramJComboBox;
      this.editor = paramJComboBox.getEditor();
      if (this.editor != null)
      {
        this.editorComponent = this.editor.getEditorComponent();
        if (this.editorComponent != null) {
          this.editorComponent.addFocusListener(this);
        }
      }
      paramJComboBox.addPropertyChangeListener("editor", this);
    }
    
    public void unregister()
    {
      this.comboBox.removePropertyChangeListener(this);
      if (this.editorComponent != null) {
        this.editorComponent.removeFocusListener(this);
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      this.comboBox.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      this.comboBox.repaint();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      ComboBoxEditor localComboBoxEditor = this.comboBox.getEditor();
      if (this.editor != localComboBoxEditor)
      {
        if (this.editorComponent != null) {
          this.editorComponent.removeFocusListener(this);
        }
        this.editor = localComboBoxEditor;
        if (this.editor != null)
        {
          this.editorComponent = this.editor.getEditorComponent();
          if (this.editorComponent != null) {
            this.editorComponent.addFocusListener(this);
          }
        }
      }
    }
  }
  
  private static class SynthComboBoxEditor
    extends BasicComboBoxEditor.UIResource
  {
    private SynthComboBoxEditor() {}
    
    public JTextField createEditorComponent()
    {
      JTextField localJTextField = new JTextField("", 9);
      localJTextField.setName("ComboBox.textField");
      return localJTextField;
    }
  }
  
  private class SynthComboBoxRenderer
    extends JLabel
    implements ListCellRenderer<Object>, UIResource
  {
    public SynthComboBoxRenderer()
    {
      setText(" ");
    }
    
    public String getName()
    {
      String str = super.getName();
      return str == null ? "ComboBox.renderer" : str;
    }
    
    public Component getListCellRendererComponent(JList<?> paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      setName("ComboBox.listRenderer");
      SynthLookAndFeel.resetSelectedUI();
      if (paramBoolean1)
      {
        setBackground(paramJList.getSelectionBackground());
        setForeground(paramJList.getSelectionForeground());
        if (!SynthComboBoxUI.this.useListColors) {
          SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), paramBoolean1, paramBoolean2, paramJList.isEnabled(), false);
        }
      }
      else
      {
        setBackground(paramJList.getBackground());
        setForeground(paramJList.getForeground());
      }
      setFont(paramJList.getFont());
      if ((paramObject instanceof Icon))
      {
        setIcon((Icon)paramObject);
        setText("");
      }
      else
      {
        String str = paramObject == null ? " " : paramObject.toString();
        if ("".equals(str)) {
          str = " ";
        }
        setText(str);
      }
      if (SynthComboBoxUI.this.comboBox != null)
      {
        setEnabled(SynthComboBoxUI.this.comboBox.isEnabled());
        setComponentOrientation(SynthComboBoxUI.this.comboBox.getComponentOrientation());
      }
      return this;
    }
    
    public void paint(Graphics paramGraphics)
    {
      super.paint(paramGraphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}
