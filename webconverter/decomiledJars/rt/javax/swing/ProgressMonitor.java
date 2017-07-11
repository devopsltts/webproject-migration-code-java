package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;

public class ProgressMonitor
  implements Accessible
{
  private ProgressMonitor root;
  private JDialog dialog;
  private JOptionPane pane;
  private JProgressBar myBar;
  private JLabel noteLabel;
  private Component parentComponent;
  private String note;
  private Object[] cancelOption = null;
  private Object message;
  private long T0;
  private int millisToDecideToPopup = 500;
  private int millisToPopup = 2000;
  private int min;
  private int max;
  protected AccessibleContext accessibleContext = null;
  private AccessibleContext accessibleJOptionPane = null;
  
  public ProgressMonitor(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2)
  {
    this(paramComponent, paramObject, paramString, paramInt1, paramInt2, null);
  }
  
  private ProgressMonitor(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, ProgressMonitor paramProgressMonitor)
  {
    this.min = paramInt1;
    this.max = paramInt2;
    this.parentComponent = paramComponent;
    this.cancelOption = new Object[1];
    this.cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");
    this.message = paramObject;
    this.note = paramString;
    if (paramProgressMonitor != null)
    {
      this.root = (paramProgressMonitor.root != null ? paramProgressMonitor.root : paramProgressMonitor);
      this.T0 = this.root.T0;
      this.dialog = this.root.dialog;
    }
    else
    {
      this.T0 = System.currentTimeMillis();
    }
  }
  
  public void setProgress(int paramInt)
  {
    if (paramInt >= this.max)
    {
      close();
    }
    else if (this.myBar != null)
    {
      this.myBar.setValue(paramInt);
    }
    else
    {
      long l1 = System.currentTimeMillis();
      long l2 = (int)(l1 - this.T0);
      if (l2 >= this.millisToDecideToPopup)
      {
        int i;
        if (paramInt > this.min) {
          i = (int)(l2 * (this.max - this.min) / (paramInt - this.min));
        } else {
          i = this.millisToPopup;
        }
        if (i >= this.millisToPopup)
        {
          this.myBar = new JProgressBar();
          this.myBar.setMinimum(this.min);
          this.myBar.setMaximum(this.max);
          this.myBar.setValue(paramInt);
          if (this.note != null) {
            this.noteLabel = new JLabel(this.note);
          }
          this.pane = new ProgressOptionPane(new Object[] { this.message, this.noteLabel, this.myBar });
          this.dialog = this.pane.createDialog(this.parentComponent, UIManager.getString("ProgressMonitor.progressText"));
          this.dialog.show();
        }
      }
    }
  }
  
  public void close()
  {
    if (this.dialog != null)
    {
      this.dialog.setVisible(false);
      this.dialog.dispose();
      this.dialog = null;
      this.pane = null;
      this.myBar = null;
    }
  }
  
  public int getMinimum()
  {
    return this.min;
  }
  
  public void setMinimum(int paramInt)
  {
    if (this.myBar != null) {
      this.myBar.setMinimum(paramInt);
    }
    this.min = paramInt;
  }
  
  public int getMaximum()
  {
    return this.max;
  }
  
  public void setMaximum(int paramInt)
  {
    if (this.myBar != null) {
      this.myBar.setMaximum(paramInt);
    }
    this.max = paramInt;
  }
  
  public boolean isCanceled()
  {
    if (this.pane == null) {
      return false;
    }
    Object localObject = this.pane.getValue();
    return (localObject != null) && (this.cancelOption.length == 1) && (localObject.equals(this.cancelOption[0]));
  }
  
  public void setMillisToDecideToPopup(int paramInt)
  {
    this.millisToDecideToPopup = paramInt;
  }
  
  public int getMillisToDecideToPopup()
  {
    return this.millisToDecideToPopup;
  }
  
  public void setMillisToPopup(int paramInt)
  {
    this.millisToPopup = paramInt;
  }
  
  public int getMillisToPopup()
  {
    return this.millisToPopup;
  }
  
  public void setNote(String paramString)
  {
    this.note = paramString;
    if (this.noteLabel != null) {
      this.noteLabel.setText(paramString);
    }
  }
  
  public String getNote()
  {
    return this.note;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleProgressMonitor();
    }
    if ((this.pane != null) && (this.accessibleJOptionPane == null) && ((this.accessibleContext instanceof AccessibleProgressMonitor))) {
      ((AccessibleProgressMonitor)this.accessibleContext).optionPaneCreated();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleProgressMonitor
    extends AccessibleContext
    implements AccessibleText, ChangeListener, PropertyChangeListener
  {
    private Object oldModelValue;
    
    protected AccessibleProgressMonitor() {}
    
    private void optionPaneCreated()
    {
      ProgressMonitor.this.accessibleJOptionPane = ProgressMonitor.ProgressOptionPane.access$400((ProgressMonitor.ProgressOptionPane)ProgressMonitor.this.pane);
      if (ProgressMonitor.this.myBar != null) {
        ProgressMonitor.this.myBar.addChangeListener(this);
      }
      if (ProgressMonitor.this.noteLabel != null) {
        ProgressMonitor.this.noteLabel.addPropertyChangeListener(this);
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (paramChangeEvent == null) {
        return;
      }
      if (ProgressMonitor.this.myBar != null)
      {
        Integer localInteger = Integer.valueOf(ProgressMonitor.this.myBar.getValue());
        firePropertyChange("AccessibleValue", this.oldModelValue, localInteger);
        this.oldModelValue = localInteger;
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if ((paramPropertyChangeEvent.getSource() == ProgressMonitor.this.noteLabel) && (paramPropertyChangeEvent.getPropertyName() == "text")) {
        firePropertyChange("AccessibleText", null, Integer.valueOf(0));
      }
    }
    
    public String getAccessibleName()
    {
      if (this.accessibleName != null) {
        return this.accessibleName;
      }
      if (ProgressMonitor.this.accessibleJOptionPane != null) {
        return ProgressMonitor.this.accessibleJOptionPane.getAccessibleName();
      }
      return null;
    }
    
    public String getAccessibleDescription()
    {
      if (this.accessibleDescription != null) {
        return this.accessibleDescription;
      }
      if (ProgressMonitor.this.accessibleJOptionPane != null) {
        return ProgressMonitor.this.accessibleJOptionPane.getAccessibleDescription();
      }
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PROGRESS_MONITOR;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      if (ProgressMonitor.this.accessibleJOptionPane != null) {
        return ProgressMonitor.this.accessibleJOptionPane.getAccessibleStateSet();
      }
      return null;
    }
    
    public Accessible getAccessibleParent()
    {
      return ProgressMonitor.this.dialog;
    }
    
    private AccessibleContext getParentAccessibleContext()
    {
      if (ProgressMonitor.this.dialog != null) {
        return ProgressMonitor.this.dialog.getAccessibleContext();
      }
      return null;
    }
    
    public int getAccessibleIndexInParent()
    {
      if (ProgressMonitor.this.accessibleJOptionPane != null) {
        return ProgressMonitor.this.accessibleJOptionPane.getAccessibleIndexInParent();
      }
      return -1;
    }
    
    public int getAccessibleChildrenCount()
    {
      AccessibleContext localAccessibleContext = getPanelAccessibleContext();
      if (localAccessibleContext != null) {
        return localAccessibleContext.getAccessibleChildrenCount();
      }
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      AccessibleContext localAccessibleContext = getPanelAccessibleContext();
      if (localAccessibleContext != null) {
        return localAccessibleContext.getAccessibleChild(paramInt);
      }
      return null;
    }
    
    private AccessibleContext getPanelAccessibleContext()
    {
      if (ProgressMonitor.this.myBar != null)
      {
        Container localContainer = ProgressMonitor.this.myBar.getParent();
        if ((localContainer instanceof Accessible)) {
          return localContainer.getAccessibleContext();
        }
      }
      return null;
    }
    
    public Locale getLocale()
      throws IllegalComponentStateException
    {
      if (ProgressMonitor.this.accessibleJOptionPane != null) {
        return ProgressMonitor.this.accessibleJOptionPane.getLocale();
      }
      return null;
    }
    
    public AccessibleComponent getAccessibleComponent()
    {
      if (ProgressMonitor.this.accessibleJOptionPane != null) {
        return ProgressMonitor.this.accessibleJOptionPane.getAccessibleComponent();
      }
      return null;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      if (ProgressMonitor.this.myBar != null) {
        return ProgressMonitor.this.myBar.getAccessibleContext().getAccessibleValue();
      }
      return null;
    }
    
    public AccessibleText getAccessibleText()
    {
      if (getNoteLabelAccessibleText() != null) {
        return this;
      }
      return null;
    }
    
    private AccessibleText getNoteLabelAccessibleText()
    {
      if (ProgressMonitor.this.noteLabel != null) {
        return ProgressMonitor.this.noteLabel.getAccessibleContext().getAccessibleText();
      }
      return null;
    }
    
    public int getIndexAtPoint(Point paramPoint)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if ((localAccessibleText != null) && (sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)))
      {
        Point localPoint = SwingUtilities.convertPoint(ProgressMonitor.this.pane, paramPoint, ProgressMonitor.this.noteLabel);
        if (localPoint != null) {
          return localAccessibleText.getIndexAtPoint(localPoint);
        }
      }
      return -1;
    }
    
    public Rectangle getCharacterBounds(int paramInt)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if ((localAccessibleText != null) && (sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)))
      {
        Rectangle localRectangle = localAccessibleText.getCharacterBounds(paramInt);
        if (localRectangle != null) {
          return SwingUtilities.convertRectangle(ProgressMonitor.this.noteLabel, localRectangle, ProgressMonitor.this.pane);
        }
      }
      return null;
    }
    
    private boolean sameWindowAncestor(Component paramComponent1, Component paramComponent2)
    {
      if ((paramComponent1 == null) || (paramComponent2 == null)) {
        return false;
      }
      return SwingUtilities.getWindowAncestor(paramComponent1) == SwingUtilities.getWindowAncestor(paramComponent2);
    }
    
    public int getCharCount()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCharCount();
      }
      return -1;
    }
    
    public int getCaretPosition()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCaretPosition();
      }
      return -1;
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getAtIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getAfterIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getBeforeIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public AttributeSet getCharacterAttribute(int paramInt)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCharacterAttribute(paramInt);
      }
      return null;
    }
    
    public int getSelectionStart()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectionStart();
      }
      return -1;
    }
    
    public int getSelectionEnd()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectionEnd();
      }
      return -1;
    }
    
    public String getSelectedText()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectedText();
      }
      return null;
    }
  }
  
  private class ProgressOptionPane
    extends JOptionPane
  {
    ProgressOptionPane(Object paramObject)
    {
      super(1, -1, null, ProgressMonitor.this.cancelOption, null);
    }
    
    public int getMaxCharactersPerLineCount()
    {
      return 60;
    }
    
    public JDialog createDialog(Component paramComponent, String paramString)
    {
      Window localWindow = JOptionPane.getWindowForComponent(paramComponent);
      final JDialog localJDialog;
      if ((localWindow instanceof Frame)) {
        localJDialog = new JDialog((Frame)localWindow, paramString, false);
      } else {
        localJDialog = new JDialog((Dialog)localWindow, paramString, false);
      }
      if ((localWindow instanceof SwingUtilities.SharedOwnerFrame))
      {
        localObject = SwingUtilities.getSharedOwnerFrameShutdownListener();
        localJDialog.addWindowListener((WindowListener)localObject);
      }
      Object localObject = localJDialog.getContentPane();
      ((Container)localObject).setLayout(new BorderLayout());
      ((Container)localObject).add(this, "Center");
      localJDialog.pack();
      localJDialog.setLocationRelativeTo(paramComponent);
      localJDialog.addWindowListener(new WindowAdapter()
      {
        boolean gotFocus = false;
        
        public void windowClosing(WindowEvent paramAnonymousWindowEvent)
        {
          ProgressMonitor.ProgressOptionPane.this.setValue(ProgressMonitor.this.cancelOption[0]);
        }
        
        public void windowActivated(WindowEvent paramAnonymousWindowEvent)
        {
          if (!this.gotFocus)
          {
            ProgressMonitor.ProgressOptionPane.this.selectInitialValue();
            this.gotFocus = true;
          }
        }
      });
      addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
        {
          if ((localJDialog.isVisible()) && (paramAnonymousPropertyChangeEvent.getSource() == ProgressMonitor.ProgressOptionPane.this) && ((paramAnonymousPropertyChangeEvent.getPropertyName().equals("value")) || (paramAnonymousPropertyChangeEvent.getPropertyName().equals("inputValue"))))
          {
            localJDialog.setVisible(false);
            localJDialog.dispose();
          }
        }
      });
      return localJDialog;
    }
    
    public AccessibleContext getAccessibleContext()
    {
      return ProgressMonitor.this.getAccessibleContext();
    }
    
    private AccessibleContext getAccessibleJOptionPane()
    {
      return super.getAccessibleContext();
    }
  }
}
