package javax.swing.text;

import com.sun.beans.util.Cache;
import com.sun.beans.util.Cache.Kind;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleExtendedText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleTextSequence;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JComponent.AccessibleJComponent;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.DropLocation;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import sun.awt.AppContext;
import sun.swing.PrintingStatus;
import sun.swing.SwingAccessor;
import sun.swing.SwingAccessor.JTextComponentAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.text.TextComponentPrintable;

public abstract class JTextComponent
  extends JComponent
  implements Scrollable, Accessible
{
  public static final String FOCUS_ACCELERATOR_KEY = "focusAcceleratorKey";
  private Document model;
  private transient Caret caret;
  private NavigationFilter navigationFilter;
  private transient Highlighter highlighter;
  private transient Keymap keymap;
  private transient MutableCaretEvent caretEvent;
  private Color caretColor;
  private Color selectionColor;
  private Color selectedTextColor;
  private Color disabledTextColor;
  private boolean editable;
  private Insets margin;
  private char focusAccelerator;
  private boolean dragEnabled;
  private DropMode dropMode = DropMode.USE_SELECTION;
  private transient DropLocation dropLocation;
  private static DefaultTransferHandler defaultTransferHandler;
  private static Cache<Class<?>, Boolean> METHOD_OVERRIDDEN = new Cache(Cache.Kind.WEAK, Cache.Kind.STRONG)
  {
    public Boolean create(final Class<?> paramAnonymousClass)
    {
      if (JTextComponent.class == paramAnonymousClass) {
        return Boolean.FALSE;
      }
      if (((Boolean)get(paramAnonymousClass.getSuperclass())).booleanValue()) {
        return Boolean.TRUE;
      }
      (Boolean)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Boolean run()
        {
          try
          {
            paramAnonymousClass.getDeclaredMethod("processInputMethodEvent", new Class[] { InputMethodEvent.class });
            return Boolean.TRUE;
          }
          catch (NoSuchMethodException localNoSuchMethodException) {}
          return Boolean.FALSE;
        }
      });
    }
  };
  private static final Object KEYMAP_TABLE = new StringBuilder("JTextComponent_KeymapTable");
  private transient InputMethodRequests inputMethodRequestsHandler;
  private SimpleAttributeSet composedTextAttribute;
  private String composedTextContent;
  private Position composedTextStart;
  private Position composedTextEnd;
  private Position latestCommittedTextStart;
  private Position latestCommittedTextEnd;
  private ComposedTextCaret composedTextCaret;
  private transient Caret originalCaret;
  private boolean checkedInputOverride;
  private boolean needToSendKeyTypedEvent;
  private static final Object FOCUSED_COMPONENT = new StringBuilder("JTextComponent_FocusedComponent");
  public static final String DEFAULT_KEYMAP = "default";
  
  public JTextComponent()
  {
    enableEvents(2056L);
    this.caretEvent = new MutableCaretEvent(this);
    addMouseListener(this.caretEvent);
    addFocusListener(this.caretEvent);
    setEditable(true);
    setDragEnabled(false);
    setLayout(null);
    updateUI();
  }
  
  public TextUI getUI()
  {
    return (TextUI)this.ui;
  }
  
  public void setUI(TextUI paramTextUI)
  {
    super.setUI(paramTextUI);
  }
  
  public void updateUI()
  {
    setUI((TextUI)UIManager.getUI(this));
    invalidate();
  }
  
  public void addCaretListener(CaretListener paramCaretListener)
  {
    this.listenerList.add(CaretListener.class, paramCaretListener);
  }
  
  public void removeCaretListener(CaretListener paramCaretListener)
  {
    this.listenerList.remove(CaretListener.class, paramCaretListener);
  }
  
  public CaretListener[] getCaretListeners()
  {
    return (CaretListener[])this.listenerList.getListeners(CaretListener.class);
  }
  
  protected void fireCaretUpdate(CaretEvent paramCaretEvent)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == CaretListener.class) {
        ((CaretListener)arrayOfObject[(i + 1)]).caretUpdate(paramCaretEvent);
      }
    }
  }
  
  public void setDocument(Document paramDocument)
  {
    Document localDocument = this.model;
    try
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readLock();
      }
      if (this.accessibleContext != null) {
        this.model.removeDocumentListener((AccessibleJTextComponent)this.accessibleContext);
      }
      if (this.inputMethodRequestsHandler != null) {
        this.model.removeDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
      }
      this.model = paramDocument;
      Boolean localBoolean = getComponentOrientation().isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
      if (localBoolean != paramDocument.getProperty(TextAttribute.RUN_DIRECTION)) {
        paramDocument.putProperty(TextAttribute.RUN_DIRECTION, localBoolean);
      }
      firePropertyChange("document", localDocument, paramDocument);
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    revalidate();
    repaint();
    if (this.accessibleContext != null) {
      this.model.addDocumentListener((AccessibleJTextComponent)this.accessibleContext);
    }
    if (this.inputMethodRequestsHandler != null) {
      this.model.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
    }
  }
  
  public Document getDocument()
  {
    return this.model;
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    Document localDocument = getDocument();
    if (localDocument != null)
    {
      Boolean localBoolean = paramComponentOrientation.isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
      localDocument.putProperty(TextAttribute.RUN_DIRECTION, localBoolean);
    }
    super.setComponentOrientation(paramComponentOrientation);
  }
  
  public Action[] getActions()
  {
    return getUI().getEditorKit(this).getActions();
  }
  
  public void setMargin(Insets paramInsets)
  {
    Insets localInsets = this.margin;
    this.margin = paramInsets;
    firePropertyChange("margin", localInsets, paramInsets);
    invalidate();
  }
  
  public Insets getMargin()
  {
    return this.margin;
  }
  
  public void setNavigationFilter(NavigationFilter paramNavigationFilter)
  {
    this.navigationFilter = paramNavigationFilter;
  }
  
  public NavigationFilter getNavigationFilter()
  {
    return this.navigationFilter;
  }
  
  @Transient
  public Caret getCaret()
  {
    return this.caret;
  }
  
  public void setCaret(Caret paramCaret)
  {
    if (this.caret != null)
    {
      this.caret.removeChangeListener(this.caretEvent);
      this.caret.deinstall(this);
    }
    Caret localCaret = this.caret;
    this.caret = paramCaret;
    if (this.caret != null)
    {
      this.caret.install(this);
      this.caret.addChangeListener(this.caretEvent);
    }
    firePropertyChange("caret", localCaret, this.caret);
  }
  
  public Highlighter getHighlighter()
  {
    return this.highlighter;
  }
  
  public void setHighlighter(Highlighter paramHighlighter)
  {
    if (this.highlighter != null) {
      this.highlighter.deinstall(this);
    }
    Highlighter localHighlighter = this.highlighter;
    this.highlighter = paramHighlighter;
    if (this.highlighter != null) {
      this.highlighter.install(this);
    }
    firePropertyChange("highlighter", localHighlighter, paramHighlighter);
  }
  
  public void setKeymap(Keymap paramKeymap)
  {
    Keymap localKeymap = this.keymap;
    this.keymap = paramKeymap;
    firePropertyChange("keymap", localKeymap, this.keymap);
    updateInputMap(localKeymap, paramKeymap);
  }
  
  public void setDragEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
      throw new HeadlessException();
    }
    this.dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled()
  {
    return this.dragEnabled;
  }
  
  public final void setDropMode(DropMode paramDropMode)
  {
    if (paramDropMode != null) {
      switch (5.$SwitchMap$javax$swing$DropMode[paramDropMode.ordinal()])
      {
      case 1: 
      case 2: 
        this.dropMode = paramDropMode;
        return;
      }
    }
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for text");
  }
  
  public final DropMode getDropMode()
  {
    return this.dropMode;
  }
  
  DropLocation dropLocationForPoint(Point paramPoint)
  {
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = getUI().viewToModel(this, paramPoint, arrayOfBias);
    if (arrayOfBias[0] == null) {
      arrayOfBias[0] = Position.Bias.Forward;
    }
    return new DropLocation(paramPoint, i, arrayOfBias[0], null);
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
  {
    Object localObject = null;
    DropLocation localDropLocation1 = (DropLocation)paramDropLocation;
    boolean bool;
    if (this.dropMode == DropMode.USE_SELECTION)
    {
      if (localDropLocation1 == null)
      {
        if (paramObject != null)
        {
          Object[] arrayOfObject = (Object[])paramObject;
          if (!paramBoolean) {
            if ((this.caret instanceof DefaultCaret))
            {
              ((DefaultCaret)this.caret).setDot(((Integer)arrayOfObject[0]).intValue(), (Position.Bias)arrayOfObject[3]);
              ((DefaultCaret)this.caret).moveDot(((Integer)arrayOfObject[1]).intValue(), (Position.Bias)arrayOfObject[4]);
            }
            else
            {
              this.caret.setDot(((Integer)arrayOfObject[0]).intValue());
              this.caret.moveDot(((Integer)arrayOfObject[1]).intValue());
            }
          }
          this.caret.setVisible(((Boolean)arrayOfObject[2]).booleanValue());
        }
      }
      else
      {
        if (this.dropLocation == null)
        {
          if ((this.caret instanceof DefaultCaret))
          {
            DefaultCaret localDefaultCaret = (DefaultCaret)this.caret;
            bool = localDefaultCaret.isActive();
            localObject = new Object[] { Integer.valueOf(localDefaultCaret.getMark()), Integer.valueOf(localDefaultCaret.getDot()), Boolean.valueOf(bool), localDefaultCaret.getMarkBias(), localDefaultCaret.getDotBias() };
          }
          else
          {
            bool = this.caret.isVisible();
            localObject = new Object[] { Integer.valueOf(this.caret.getMark()), Integer.valueOf(this.caret.getDot()), Boolean.valueOf(bool) };
          }
          this.caret.setVisible(true);
        }
        else
        {
          localObject = paramObject;
        }
        if ((this.caret instanceof DefaultCaret)) {
          ((DefaultCaret)this.caret).setDot(localDropLocation1.getIndex(), localDropLocation1.getBias());
        } else {
          this.caret.setDot(localDropLocation1.getIndex());
        }
      }
    }
    else if (localDropLocation1 == null)
    {
      if (paramObject != null) {
        this.caret.setVisible(((Boolean)paramObject).booleanValue());
      }
    }
    else if (this.dropLocation == null)
    {
      bool = (this.caret instanceof DefaultCaret) ? ((DefaultCaret)this.caret).isActive() : this.caret.isVisible();
      localObject = Boolean.valueOf(bool);
      this.caret.setVisible(false);
    }
    else
    {
      localObject = paramObject;
    }
    DropLocation localDropLocation2 = this.dropLocation;
    this.dropLocation = localDropLocation1;
    firePropertyChange("dropLocation", localDropLocation2, this.dropLocation);
    return localObject;
  }
  
  public final DropLocation getDropLocation()
  {
    return this.dropLocation;
  }
  
  void updateInputMap(Keymap paramKeymap1, Keymap paramKeymap2)
  {
    InputMap localInputMap1 = getInputMap(0);
    InputMap localInputMap2 = localInputMap1;
    while ((localInputMap1 != null) && (!(localInputMap1 instanceof KeymapWrapper)))
    {
      localInputMap2 = localInputMap1;
      localInputMap1 = localInputMap1.getParent();
    }
    if (localInputMap1 != null)
    {
      if (paramKeymap2 == null)
      {
        if (localInputMap2 != localInputMap1) {
          localInputMap2.setParent(localInputMap1.getParent());
        } else {
          localInputMap2.setParent(null);
        }
      }
      else
      {
        localObject1 = new KeymapWrapper(paramKeymap2);
        localInputMap2.setParent((InputMap)localObject1);
        if (localInputMap2 != localInputMap1) {
          ((InputMap)localObject1).setParent(localInputMap1.getParent());
        }
      }
    }
    else if (paramKeymap2 != null)
    {
      localInputMap1 = getInputMap(0);
      if (localInputMap1 != null)
      {
        localObject1 = new KeymapWrapper(paramKeymap2);
        ((InputMap)localObject1).setParent(localInputMap1.getParent());
        localInputMap1.setParent((InputMap)localObject1);
      }
    }
    Object localObject1 = getActionMap();
    Object localObject2 = localObject1;
    while ((localObject1 != null) && (!(localObject1 instanceof KeymapActionMap)))
    {
      localObject2 = localObject1;
      localObject1 = ((ActionMap)localObject1).getParent();
    }
    KeymapActionMap localKeymapActionMap;
    if (localObject1 != null)
    {
      if (paramKeymap2 == null)
      {
        if (localObject2 != localObject1) {
          localObject2.setParent(((ActionMap)localObject1).getParent());
        } else {
          localObject2.setParent(null);
        }
      }
      else
      {
        localKeymapActionMap = new KeymapActionMap(paramKeymap2);
        localObject2.setParent(localKeymapActionMap);
        if (localObject2 != localObject1) {
          localKeymapActionMap.setParent(((ActionMap)localObject1).getParent());
        }
      }
    }
    else if (paramKeymap2 != null)
    {
      localObject1 = getActionMap();
      if (localObject1 != null)
      {
        localKeymapActionMap = new KeymapActionMap(paramKeymap2);
        localKeymapActionMap.setParent(((ActionMap)localObject1).getParent());
        ((ActionMap)localObject1).setParent(localKeymapActionMap);
      }
    }
  }
  
  public Keymap getKeymap()
  {
    return this.keymap;
  }
  
  public static Keymap addKeymap(String paramString, Keymap paramKeymap)
  {
    DefaultKeymap localDefaultKeymap = new DefaultKeymap(paramString, paramKeymap);
    if (paramString != null) {
      getKeymapTable().put(paramString, localDefaultKeymap);
    }
    return localDefaultKeymap;
  }
  
  public static Keymap removeKeymap(String paramString)
  {
    return (Keymap)getKeymapTable().remove(paramString);
  }
  
  public static Keymap getKeymap(String paramString)
  {
    return (Keymap)getKeymapTable().get(paramString);
  }
  
  private static HashMap<String, Keymap> getKeymapTable()
  {
    synchronized (KEYMAP_TABLE)
    {
      AppContext localAppContext = AppContext.getAppContext();
      HashMap localHashMap = (HashMap)localAppContext.get(KEYMAP_TABLE);
      if (localHashMap == null)
      {
        localHashMap = new HashMap(17);
        localAppContext.put(KEYMAP_TABLE, localHashMap);
        Keymap localKeymap = addKeymap("default", null);
        localKeymap.setDefaultAction(new DefaultEditorKit.DefaultKeyTypedAction());
      }
      return localHashMap;
    }
  }
  
  public static void loadKeymap(Keymap paramKeymap, KeyBinding[] paramArrayOfKeyBinding, Action[] paramArrayOfAction)
  {
    Hashtable localHashtable = new Hashtable();
    Object localObject2;
    Object localObject3;
    for (localObject2 : paramArrayOfAction)
    {
      localObject3 = (String)localObject2.getValue("Name");
      localHashtable.put(localObject3 != null ? localObject3 : "", localObject2);
    }
    for (localObject2 : paramArrayOfKeyBinding)
    {
      localObject3 = (Action)localHashtable.get(localObject2.actionName);
      if (localObject3 != null) {
        paramKeymap.addActionForKeyStroke(localObject2.key, (Action)localObject3);
      }
    }
  }
  
  public Color getCaretColor()
  {
    return this.caretColor;
  }
  
  public void setCaretColor(Color paramColor)
  {
    Color localColor = this.caretColor;
    this.caretColor = paramColor;
    firePropertyChange("caretColor", localColor, this.caretColor);
  }
  
  public Color getSelectionColor()
  {
    return this.selectionColor;
  }
  
  public void setSelectionColor(Color paramColor)
  {
    Color localColor = this.selectionColor;
    this.selectionColor = paramColor;
    firePropertyChange("selectionColor", localColor, this.selectionColor);
  }
  
  public Color getSelectedTextColor()
  {
    return this.selectedTextColor;
  }
  
  public void setSelectedTextColor(Color paramColor)
  {
    Color localColor = this.selectedTextColor;
    this.selectedTextColor = paramColor;
    firePropertyChange("selectedTextColor", localColor, this.selectedTextColor);
  }
  
  public Color getDisabledTextColor()
  {
    return this.disabledTextColor;
  }
  
  public void setDisabledTextColor(Color paramColor)
  {
    Color localColor = this.disabledTextColor;
    this.disabledTextColor = paramColor;
    firePropertyChange("disabledTextColor", localColor, this.disabledTextColor);
  }
  
  public void replaceSelection(String paramString)
  {
    Document localDocument = getDocument();
    if (localDocument != null) {
      try
      {
        boolean bool = saveComposedText(this.caret.getDot());
        int i = Math.min(this.caret.getDot(), this.caret.getMark());
        int j = Math.max(this.caret.getDot(), this.caret.getMark());
        if ((localDocument instanceof AbstractDocument))
        {
          ((AbstractDocument)localDocument).replace(i, j - i, paramString, null);
        }
        else
        {
          if (i != j) {
            localDocument.remove(i, j - i);
          }
          if ((paramString != null) && (paramString.length() > 0)) {
            localDocument.insertString(i, paramString, null);
          }
        }
        if (bool) {
          restoreComposedText();
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
      }
    }
  }
  
  public String getText(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    return getDocument().getText(paramInt1, paramInt2);
  }
  
  public Rectangle modelToView(int paramInt)
    throws BadLocationException
  {
    return getUI().modelToView(this, paramInt);
  }
  
  public int viewToModel(Point paramPoint)
  {
    return getUI().viewToModel(this, paramPoint);
  }
  
  public void cut()
  {
    if ((isEditable()) && (isEnabled())) {
      invokeAction("cut", TransferHandler.getCutAction());
    }
  }
  
  public void copy()
  {
    invokeAction("copy", TransferHandler.getCopyAction());
  }
  
  public void paste()
  {
    if ((isEditable()) && (isEnabled())) {
      invokeAction("paste", TransferHandler.getPasteAction());
    }
  }
  
  private void invokeAction(String paramString, Action paramAction)
  {
    ActionMap localActionMap = getActionMap();
    Action localAction = null;
    if (localActionMap != null) {
      localAction = localActionMap.get(paramString);
    }
    if (localAction == null)
    {
      installDefaultTransferHandlerIfNecessary();
      localAction = paramAction;
    }
    localAction.actionPerformed(new ActionEvent(this, 1001, (String)localAction.getValue("Name"), EventQueue.getMostRecentEventTime(), getCurrentEventModifiers()));
  }
  
  private void installDefaultTransferHandlerIfNecessary()
  {
    if (getTransferHandler() == null)
    {
      if (defaultTransferHandler == null) {
        defaultTransferHandler = new DefaultTransferHandler();
      }
      setTransferHandler(defaultTransferHandler);
    }
  }
  
  public void moveCaretPosition(int paramInt)
  {
    Document localDocument = getDocument();
    if (localDocument != null)
    {
      if ((paramInt > localDocument.getLength()) || (paramInt < 0)) {
        throw new IllegalArgumentException("bad position: " + paramInt);
      }
      this.caret.moveDot(paramInt);
    }
  }
  
  public void setFocusAccelerator(char paramChar)
  {
    paramChar = Character.toUpperCase(paramChar);
    char c = this.focusAccelerator;
    this.focusAccelerator = paramChar;
    firePropertyChange("focusAcceleratorKey", c, this.focusAccelerator);
    firePropertyChange("focusAccelerator", c, this.focusAccelerator);
  }
  
  public char getFocusAccelerator()
  {
    return this.focusAccelerator;
  }
  
  public void read(Reader paramReader, Object paramObject)
    throws IOException
  {
    EditorKit localEditorKit = getUI().getEditorKit(this);
    Document localDocument = localEditorKit.createDefaultDocument();
    if (paramObject != null) {
      localDocument.putProperty("stream", paramObject);
    }
    try
    {
      localEditorKit.read(paramReader, localDocument, 0);
      setDocument(localDocument);
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new IOException(localBadLocationException.getMessage());
    }
  }
  
  public void write(Writer paramWriter)
    throws IOException
  {
    Document localDocument = getDocument();
    try
    {
      getUI().getEditorKit(this).write(paramWriter, localDocument, 0, localDocument.getLength());
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new IOException(localBadLocationException.getMessage());
    }
  }
  
  public void removeNotify()
  {
    super.removeNotify();
    if (getFocusedComponent() == this) {
      AppContext.getAppContext().remove(FOCUSED_COMPONENT);
    }
  }
  
  public void setCaretPosition(int paramInt)
  {
    Document localDocument = getDocument();
    if (localDocument != null)
    {
      if ((paramInt > localDocument.getLength()) || (paramInt < 0)) {
        throw new IllegalArgumentException("bad position: " + paramInt);
      }
      this.caret.setDot(paramInt);
    }
  }
  
  @Transient
  public int getCaretPosition()
  {
    return this.caret.getDot();
  }
  
  public void setText(String paramString)
  {
    try
    {
      Document localDocument = getDocument();
      if ((localDocument instanceof AbstractDocument))
      {
        ((AbstractDocument)localDocument).replace(0, localDocument.getLength(), paramString, null);
      }
      else
      {
        localDocument.remove(0, localDocument.getLength());
        localDocument.insertString(0, paramString, null);
      }
    }
    catch (BadLocationException localBadLocationException)
    {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    }
  }
  
  public String getText()
  {
    Document localDocument = getDocument();
    String str;
    try
    {
      str = localDocument.getText(0, localDocument.getLength());
    }
    catch (BadLocationException localBadLocationException)
    {
      str = null;
    }
    return str;
  }
  
  public String getSelectedText()
  {
    String str = null;
    int i = Math.min(this.caret.getDot(), this.caret.getMark());
    int j = Math.max(this.caret.getDot(), this.caret.getMark());
    if (i != j) {
      try
      {
        Document localDocument = getDocument();
        str = localDocument.getText(i, j - i);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new IllegalArgumentException(localBadLocationException.getMessage());
      }
    }
    return str;
  }
  
  public boolean isEditable()
  {
    return this.editable;
  }
  
  public void setEditable(boolean paramBoolean)
  {
    if (paramBoolean != this.editable)
    {
      boolean bool = this.editable;
      this.editable = paramBoolean;
      enableInputMethods(this.editable);
      firePropertyChange("editable", Boolean.valueOf(bool), Boolean.valueOf(this.editable));
      repaint();
    }
  }
  
  @Transient
  public int getSelectionStart()
  {
    int i = Math.min(this.caret.getDot(), this.caret.getMark());
    return i;
  }
  
  public void setSelectionStart(int paramInt)
  {
    select(paramInt, getSelectionEnd());
  }
  
  @Transient
  public int getSelectionEnd()
  {
    int i = Math.max(this.caret.getDot(), this.caret.getMark());
    return i;
  }
  
  public void setSelectionEnd(int paramInt)
  {
    select(getSelectionStart(), paramInt);
  }
  
  public void select(int paramInt1, int paramInt2)
  {
    int i = getDocument().getLength();
    if (paramInt1 < 0) {
      paramInt1 = 0;
    }
    if (paramInt1 > i) {
      paramInt1 = i;
    }
    if (paramInt2 > i) {
      paramInt2 = i;
    }
    if (paramInt2 < paramInt1) {
      paramInt2 = paramInt1;
    }
    setCaretPosition(paramInt1);
    moveCaretPosition(paramInt2);
  }
  
  public void selectAll()
  {
    Document localDocument = getDocument();
    if (localDocument != null)
    {
      setCaretPosition(0);
      moveCaretPosition(localDocument.getLength());
    }
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    String str = super.getToolTipText(paramMouseEvent);
    if (str == null)
    {
      TextUI localTextUI = getUI();
      if (localTextUI != null) {
        str = localTextUI.getToolTipText(this, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()));
      }
    }
    return str;
  }
  
  public Dimension getPreferredScrollableViewportSize()
  {
    return getPreferredSize();
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 1: 
      return paramRectangle.height / 10;
    case 0: 
      return paramRectangle.width / 10;
    }
    throw new IllegalArgumentException("Invalid orientation: " + paramInt1);
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 1: 
      return paramRectangle.height;
    case 0: 
      return paramRectangle.width;
    }
    throw new IllegalArgumentException("Invalid orientation: " + paramInt1);
  }
  
  public boolean getScrollableTracksViewportWidth()
  {
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getWidth() > getPreferredSize().width;
    }
    return false;
  }
  
  public boolean getScrollableTracksViewportHeight()
  {
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getHeight() > getPreferredSize().height;
    }
    return false;
  }
  
  public boolean print()
    throws PrinterException
  {
    return print(null, null, true, null, null, true);
  }
  
  public boolean print(MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
    throws PrinterException
  {
    return print(paramMessageFormat1, paramMessageFormat2, true, null, null, true);
  }
  
  public boolean print(MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2, boolean paramBoolean1, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet, boolean paramBoolean2)
    throws PrinterException
  {
    final PrinterJob localPrinterJob = PrinterJob.getPrinterJob();
    boolean bool1 = GraphicsEnvironment.isHeadless();
    final boolean bool2 = SwingUtilities.isEventDispatchThread();
    Printable localPrintable2 = getPrintable(paramMessageFormat1, paramMessageFormat2);
    final PrintingStatus localPrintingStatus;
    Printable localPrintable1;
    if ((paramBoolean2) && (!bool1))
    {
      localPrintingStatus = PrintingStatus.createPrintingStatus(this, localPrinterJob);
      localPrintable1 = localPrintingStatus.createNotificationPrintable(localPrintable2);
    }
    else
    {
      localPrintingStatus = null;
      localPrintable1 = localPrintable2;
    }
    if (paramPrintService != null) {
      localPrinterJob.setPrintService(paramPrintService);
    }
    localPrinterJob.setPrintable(localPrintable1);
    final PrintRequestAttributeSet localPrintRequestAttributeSet = paramPrintRequestAttributeSet == null ? new HashPrintRequestAttributeSet() : paramPrintRequestAttributeSet;
    if ((paramBoolean1) && (!bool1) && (!localPrinterJob.printDialog(localPrintRequestAttributeSet))) {
      return false;
    }
    Callable local2 = new Callable()
    {
      public Object call()
        throws Exception
      {
        try
        {
          localPrinterJob.print(localPrintRequestAttributeSet);
          if (localPrintingStatus != null) {
            localPrintingStatus.dispose();
          }
        }
        finally
        {
          if (localPrintingStatus != null) {
            localPrintingStatus.dispose();
          }
        }
        return null;
      }
    };
    final FutureTask localFutureTask = new FutureTask(local2);
    Runnable local3 = new Runnable()
    {
      public void run()
      {
        boolean bool = false;
        Throwable localThrowable;
        if (bool2)
        {
          if (JTextComponent.this.isEnabled())
          {
            bool = true;
            JTextComponent.this.setEnabled(false);
          }
        }
        else {
          try
          {
            bool = ((Boolean)SwingUtilities2.submit(new Callable()
            {
              public Boolean call()
                throws Exception
              {
                boolean bool = JTextComponent.this.isEnabled();
                if (bool) {
                  JTextComponent.this.setEnabled(false);
                }
                return Boolean.valueOf(bool);
              }
            }).get()).booleanValue();
          }
          catch (InterruptedException localInterruptedException1)
          {
            throw new RuntimeException(localInterruptedException1);
          }
          catch (ExecutionException localExecutionException1)
          {
            localThrowable = localExecutionException1.getCause();
            if ((localThrowable instanceof Error)) {
              throw ((Error)localThrowable);
            }
            if ((localThrowable instanceof RuntimeException)) {
              throw ((RuntimeException)localThrowable);
            }
            throw new AssertionError(localThrowable);
          }
        }
        JTextComponent.this.getDocument().render(localFutureTask);
        if (bool) {
          if (bool2) {
            JTextComponent.this.setEnabled(true);
          } else {
            try
            {
              SwingUtilities2.submit(new Runnable()
              {
                public void run()
                {
                  JTextComponent.this.setEnabled(true);
                }
              }, null).get();
            }
            catch (InterruptedException localInterruptedException2)
            {
              throw new RuntimeException(localInterruptedException2);
            }
            catch (ExecutionException localExecutionException2)
            {
              localThrowable = localExecutionException2.getCause();
              if ((localThrowable instanceof Error)) {
                throw ((Error)localThrowable);
              }
              if ((localThrowable instanceof RuntimeException)) {
                throw ((RuntimeException)localThrowable);
              }
              throw new AssertionError(localThrowable);
            }
          }
        }
      }
    };
    if ((!paramBoolean2) || (bool1))
    {
      local3.run();
    }
    else if (bool2)
    {
      new Thread(local3).start();
      localPrintingStatus.showModal(true);
    }
    else
    {
      localPrintingStatus.showModal(false);
      local3.run();
    }
    try
    {
      localFutureTask.get();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException(localInterruptedException);
    }
    catch (ExecutionException localExecutionException)
    {
      Throwable localThrowable = localExecutionException.getCause();
      if ((localThrowable instanceof PrinterAbortException))
      {
        if ((localPrintingStatus != null) && (localPrintingStatus.isAborted())) {
          return false;
        }
        throw ((PrinterAbortException)localThrowable);
      }
      if ((localThrowable instanceof PrinterException)) {
        throw ((PrinterException)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new AssertionError(localThrowable);
    }
    return true;
  }
  
  public Printable getPrintable(MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
  {
    return TextComponentPrintable.getPrintable(this, paramMessageFormat1, paramMessageFormat2);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJTextComponent();
    }
    return this.accessibleContext;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.caretEvent = new MutableCaretEvent(this);
    addMouseListener(this.caretEvent);
    addFocusListener(this.caretEvent);
  }
  
  protected String paramString()
  {
    String str1 = this.editable ? "true" : "false";
    String str2 = this.caretColor != null ? this.caretColor.toString() : "";
    String str3 = this.selectionColor != null ? this.selectionColor.toString() : "";
    String str4 = this.selectedTextColor != null ? this.selectedTextColor.toString() : "";
    String str5 = this.disabledTextColor != null ? this.disabledTextColor.toString() : "";
    String str6 = this.margin != null ? this.margin.toString() : "";
    return super.paramString() + ",caretColor=" + str2 + ",disabledTextColor=" + str5 + ",editable=" + str1 + ",margin=" + str6 + ",selectedTextColor=" + str4 + ",selectionColor=" + str3;
  }
  
  static final JTextComponent getFocusedComponent()
  {
    return (JTextComponent)AppContext.getAppContext().get(FOCUSED_COMPONENT);
  }
  
  private int getCurrentEventModifiers()
  {
    int i = 0;
    AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
    if ((localAWTEvent instanceof InputEvent)) {
      i = ((InputEvent)localAWTEvent).getModifiers();
    } else if ((localAWTEvent instanceof ActionEvent)) {
      i = ((ActionEvent)localAWTEvent).getModifiers();
    }
    return i;
  }
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent)
  {
    super.processInputMethodEvent(paramInputMethodEvent);
    if (!paramInputMethodEvent.isConsumed())
    {
      if (!isEditable()) {
        return;
      }
      switch (paramInputMethodEvent.getID())
      {
      case 1100: 
        replaceInputMethodText(paramInputMethodEvent);
      case 1101: 
        setInputMethodCaretPosition(paramInputMethodEvent);
      }
      paramInputMethodEvent.consume();
    }
  }
  
  public InputMethodRequests getInputMethodRequests()
  {
    if (this.inputMethodRequestsHandler == null)
    {
      this.inputMethodRequestsHandler = new InputMethodRequestsHandler();
      Document localDocument = getDocument();
      if (localDocument != null) {
        localDocument.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
      }
    }
    return this.inputMethodRequestsHandler;
  }
  
  public void addInputMethodListener(InputMethodListener paramInputMethodListener)
  {
    super.addInputMethodListener(paramInputMethodListener);
    if (paramInputMethodListener != null)
    {
      this.needToSendKeyTypedEvent = false;
      this.checkedInputOverride = true;
    }
  }
  
  private void replaceInputMethodText(InputMethodEvent paramInputMethodEvent)
  {
    int i = paramInputMethodEvent.getCommittedCharacterCount();
    AttributedCharacterIterator localAttributedCharacterIterator = paramInputMethodEvent.getText();
    Document localDocument = getDocument();
    if (composedTextExists())
    {
      try
      {
        localDocument.remove(this.composedTextStart.getOffset(), this.composedTextEnd.getOffset() - this.composedTextStart.getOffset());
      }
      catch (BadLocationException localBadLocationException1) {}
      this.composedTextStart = (this.composedTextEnd = null);
      this.composedTextAttribute = null;
      this.composedTextContent = null;
    }
    if (localAttributedCharacterIterator != null)
    {
      localAttributedCharacterIterator.first();
      int k = 0;
      int m = 0;
      if (i > 0)
      {
        k = this.caret.getDot();
        if (shouldSynthensizeKeyEvents())
        {
          char c1 = localAttributedCharacterIterator.current();
          while (i > 0)
          {
            KeyEvent localKeyEvent = new KeyEvent(this, 400, EventQueue.getMostRecentEventTime(), 0, 0, c1);
            processKeyEvent(localKeyEvent);
            c1 = localAttributedCharacterIterator.next();
            i--;
          }
        }
        else
        {
          StringBuilder localStringBuilder = new StringBuilder();
          char c2 = localAttributedCharacterIterator.current();
          while (i > 0)
          {
            localStringBuilder.append(c2);
            c2 = localAttributedCharacterIterator.next();
            i--;
          }
          mapCommittedTextToAction(localStringBuilder.toString());
        }
        m = this.caret.getDot();
      }
      int j = localAttributedCharacterIterator.getIndex();
      if (j < localAttributedCharacterIterator.getEndIndex())
      {
        createComposedTextAttribute(j, localAttributedCharacterIterator);
        try
        {
          replaceSelection(null);
          localDocument.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
          this.composedTextStart = localDocument.createPosition(this.caret.getDot() - this.composedTextContent.length());
          this.composedTextEnd = localDocument.createPosition(this.caret.getDot());
        }
        catch (BadLocationException localBadLocationException2)
        {
          this.composedTextStart = (this.composedTextEnd = null);
          this.composedTextAttribute = null;
          this.composedTextContent = null;
        }
      }
      if (k != m) {
        try
        {
          this.latestCommittedTextStart = localDocument.createPosition(k);
          this.latestCommittedTextEnd = localDocument.createPosition(m);
        }
        catch (BadLocationException localBadLocationException3)
        {
          this.latestCommittedTextStart = (this.latestCommittedTextEnd = null);
        }
      } else {
        this.latestCommittedTextStart = (this.latestCommittedTextEnd = null);
      }
    }
  }
  
  private void createComposedTextAttribute(int paramInt, AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    Document localDocument = getDocument();
    StringBuilder localStringBuilder = new StringBuilder();
    int j;
    for (int i = paramAttributedCharacterIterator.setIndex(paramInt); i != 65535; j = paramAttributedCharacterIterator.next()) {
      localStringBuilder.append(i);
    }
    this.composedTextContent = localStringBuilder.toString();
    this.composedTextAttribute = new SimpleAttributeSet();
    this.composedTextAttribute.addAttribute(StyleConstants.ComposedTextAttribute, new AttributedString(paramAttributedCharacterIterator, paramInt, paramAttributedCharacterIterator.getEndIndex()));
  }
  
  protected boolean saveComposedText(int paramInt)
  {
    if (composedTextExists())
    {
      int i = this.composedTextStart.getOffset();
      int j = this.composedTextEnd.getOffset() - this.composedTextStart.getOffset();
      if ((paramInt >= i) && (paramInt <= i + j)) {
        try
        {
          getDocument().remove(i, j);
          return true;
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
    return false;
  }
  
  protected void restoreComposedText()
  {
    Document localDocument = getDocument();
    try
    {
      localDocument.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
      this.composedTextStart = localDocument.createPosition(this.caret.getDot() - this.composedTextContent.length());
      this.composedTextEnd = localDocument.createPosition(this.caret.getDot());
    }
    catch (BadLocationException localBadLocationException) {}
  }
  
  private void mapCommittedTextToAction(String paramString)
  {
    Keymap localKeymap = getKeymap();
    if (localKeymap != null)
    {
      Action localAction = null;
      Object localObject;
      if (paramString.length() == 1)
      {
        localObject = KeyStroke.getKeyStroke(paramString.charAt(0));
        localAction = localKeymap.getAction((KeyStroke)localObject);
      }
      if (localAction == null) {
        localAction = localKeymap.getDefaultAction();
      }
      if (localAction != null)
      {
        localObject = new ActionEvent(this, 1001, paramString, EventQueue.getMostRecentEventTime(), getCurrentEventModifiers());
        localAction.actionPerformed((ActionEvent)localObject);
      }
    }
  }
  
  private void setInputMethodCaretPosition(InputMethodEvent paramInputMethodEvent)
  {
    int i;
    if (composedTextExists())
    {
      i = this.composedTextStart.getOffset();
      if (!(this.caret instanceof ComposedTextCaret))
      {
        if (this.composedTextCaret == null) {
          this.composedTextCaret = new ComposedTextCaret();
        }
        this.originalCaret = this.caret;
        exchangeCaret(this.originalCaret, this.composedTextCaret);
      }
      TextHitInfo localTextHitInfo = paramInputMethodEvent.getCaret();
      if (localTextHitInfo != null)
      {
        int j = localTextHitInfo.getInsertionIndex();
        i += j;
        if (j == 0) {
          try
          {
            Rectangle localRectangle1 = modelToView(i);
            Rectangle localRectangle2 = modelToView(this.composedTextEnd.getOffset());
            Rectangle localRectangle3 = getBounds();
            localRectangle1.x += Math.min(localRectangle2.x - localRectangle1.x, localRectangle3.width);
            scrollRectToVisible(localRectangle1);
          }
          catch (BadLocationException localBadLocationException) {}
        }
      }
      this.caret.setDot(i);
    }
    else if ((this.caret instanceof ComposedTextCaret))
    {
      i = this.caret.getDot();
      exchangeCaret(this.caret, this.originalCaret);
      this.caret.setDot(i);
    }
  }
  
  private void exchangeCaret(Caret paramCaret1, Caret paramCaret2)
  {
    int i = paramCaret1.getBlinkRate();
    setCaret(paramCaret2);
    this.caret.setBlinkRate(i);
    this.caret.setVisible(hasFocus());
  }
  
  private boolean shouldSynthensizeKeyEvents()
  {
    if (!this.checkedInputOverride)
    {
      this.needToSendKeyTypedEvent = (!((Boolean)METHOD_OVERRIDDEN.get(getClass())).booleanValue());
      this.checkedInputOverride = true;
    }
    return this.needToSendKeyTypedEvent;
  }
  
  boolean composedTextExists()
  {
    return this.composedTextStart != null;
  }
  
  static
  {
    SwingAccessor.setJTextComponentAccessor(new SwingAccessor.JTextComponentAccessor()
    {
      public TransferHandler.DropLocation dropLocationForPoint(JTextComponent paramAnonymousJTextComponent, Point paramAnonymousPoint)
      {
        return paramAnonymousJTextComponent.dropLocationForPoint(paramAnonymousPoint);
      }
      
      public Object setDropLocation(JTextComponent paramAnonymousJTextComponent, TransferHandler.DropLocation paramAnonymousDropLocation, Object paramAnonymousObject, boolean paramAnonymousBoolean)
      {
        return paramAnonymousJTextComponent.setDropLocation(paramAnonymousDropLocation, paramAnonymousObject, paramAnonymousBoolean);
      }
    });
  }
  
  public class AccessibleJTextComponent
    extends JComponent.AccessibleJComponent
    implements AccessibleText, CaretListener, DocumentListener, AccessibleAction, AccessibleEditableText, AccessibleExtendedText
  {
    int caretPos;
    Point oldLocationOnScreen;
    
    public AccessibleJTextComponent()
    {
      super();
      Document localDocument = JTextComponent.this.getDocument();
      if (localDocument != null) {
        localDocument.addDocumentListener(this);
      }
      JTextComponent.this.addCaretListener(this);
      this.caretPos = getCaretPosition();
      try
      {
        this.oldLocationOnScreen = getLocationOnScreen();
      }
      catch (IllegalComponentStateException localIllegalComponentStateException) {}
      JTextComponent.this.addComponentListener(new ComponentAdapter()
      {
        public void componentMoved(ComponentEvent paramAnonymousComponentEvent)
        {
          try
          {
            Point localPoint = JTextComponent.AccessibleJTextComponent.this.getLocationOnScreen();
            JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleVisibleData", JTextComponent.AccessibleJTextComponent.this.oldLocationOnScreen, localPoint);
            JTextComponent.AccessibleJTextComponent.this.oldLocationOnScreen = localPoint;
          }
          catch (IllegalComponentStateException localIllegalComponentStateException) {}
        }
      });
    }
    
    public void caretUpdate(CaretEvent paramCaretEvent)
    {
      int i = paramCaretEvent.getDot();
      int j = paramCaretEvent.getMark();
      if (this.caretPos != i)
      {
        firePropertyChange("AccessibleCaret", new Integer(this.caretPos), new Integer(i));
        this.caretPos = i;
        try
        {
          this.oldLocationOnScreen = getLocationOnScreen();
        }
        catch (IllegalComponentStateException localIllegalComponentStateException) {}
      }
      if (j != i) {
        firePropertyChange("AccessibleSelection", null, getSelectedText());
      }
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      final Integer localInteger = new Integer(paramDocumentEvent.getOffset());
      if (SwingUtilities.isEventDispatchThread())
      {
        firePropertyChange("AccessibleText", null, localInteger);
      }
      else
      {
        Runnable local2 = new Runnable()
        {
          public void run()
          {
            JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, localInteger);
          }
        };
        SwingUtilities.invokeLater(local2);
      }
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      final Integer localInteger = new Integer(paramDocumentEvent.getOffset());
      if (SwingUtilities.isEventDispatchThread())
      {
        firePropertyChange("AccessibleText", null, localInteger);
      }
      else
      {
        Runnable local3 = new Runnable()
        {
          public void run()
          {
            JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, localInteger);
          }
        };
        SwingUtilities.invokeLater(local3);
      }
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      final Integer localInteger = new Integer(paramDocumentEvent.getOffset());
      if (SwingUtilities.isEventDispatchThread())
      {
        firePropertyChange("AccessibleText", null, localInteger);
      }
      else
      {
        Runnable local4 = new Runnable()
        {
          public void run()
          {
            JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, localInteger);
          }
        };
        SwingUtilities.invokeLater(local4);
      }
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (JTextComponent.this.isEditable()) {
        localAccessibleStateSet.add(AccessibleState.EDITABLE);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TEXT;
    }
    
    public AccessibleText getAccessibleText()
    {
      return this;
    }
    
    public int getIndexAtPoint(Point paramPoint)
    {
      if (paramPoint == null) {
        return -1;
      }
      return JTextComponent.this.viewToModel(paramPoint);
    }
    
    Rectangle getRootEditorRect()
    {
      Rectangle localRectangle = JTextComponent.this.getBounds();
      if ((localRectangle.width > 0) && (localRectangle.height > 0))
      {
        localRectangle.x = (localRectangle.y = 0);
        Insets localInsets = JTextComponent.this.getInsets();
        localRectangle.x += localInsets.left;
        localRectangle.y += localInsets.top;
        localRectangle.width -= localInsets.left + localInsets.right;
        localRectangle.height -= localInsets.top + localInsets.bottom;
        return localRectangle;
      }
      return null;
    }
    
    public Rectangle getCharacterBounds(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > JTextComponent.this.model.getLength() - 1)) {
        return null;
      }
      TextUI localTextUI = JTextComponent.this.getUI();
      if (localTextUI == null) {
        return null;
      }
      Rectangle localRectangle1 = null;
      Rectangle localRectangle2 = getRootEditorRect();
      if (localRectangle2 == null) {
        return null;
      }
      if ((JTextComponent.this.model instanceof AbstractDocument)) {
        ((AbstractDocument)JTextComponent.this.model).readLock();
      }
      try
      {
        View localView = localTextUI.getRootView(JTextComponent.this);
        if (localView != null)
        {
          localView.setSize(localRectangle2.width, localRectangle2.height);
          Shape localShape = localView.modelToView(paramInt, Position.Bias.Forward, paramInt + 1, Position.Bias.Backward, localRectangle2);
          localRectangle1 = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
        }
      }
      catch (BadLocationException localBadLocationException) {}finally
      {
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readUnlock();
        }
      }
      return localRectangle1;
    }
    
    public int getCharCount()
    {
      return JTextComponent.this.model.getLength();
    }
    
    public int getCaretPosition()
    {
      return JTextComponent.this.getCaretPosition();
    }
    
    public AttributeSet getCharacterAttribute(int paramInt)
    {
      Element localElement = null;
      if ((JTextComponent.this.model instanceof AbstractDocument)) {
        ((AbstractDocument)JTextComponent.this.model).readLock();
      }
      try
      {
        int i;
        for (localElement = JTextComponent.this.model.getDefaultRootElement(); !localElement.isLeaf(); localElement = localElement.getElement(i)) {
          i = localElement.getElementIndex(paramInt);
        }
      }
      finally
      {
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readUnlock();
        }
      }
      return localElement.getAttributes();
    }
    
    public int getSelectionStart()
    {
      return JTextComponent.this.getSelectionStart();
    }
    
    public int getSelectionEnd()
    {
      return JTextComponent.this.getSelectionEnd();
    }
    
    public String getSelectedText()
    {
      return JTextComponent.this.getSelectedText();
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      return getAtIndex(paramInt1, paramInt2, 0);
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      return getAtIndex(paramInt1, paramInt2, 1);
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      return getAtIndex(paramInt1, paramInt2, -1);
    }
    
    private String getAtIndex(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((JTextComponent.this.model instanceof AbstractDocument)) {
        ((AbstractDocument)JTextComponent.this.model).readLock();
      }
      try
      {
        Object localObject1;
        if ((paramInt2 < 0) || (paramInt2 >= JTextComponent.this.model.getLength()))
        {
          localObject1 = null;
          return localObject1;
        }
        switch (paramInt1)
        {
        case 1: 
          if ((paramInt2 + paramInt3 < JTextComponent.this.model.getLength()) && (paramInt2 + paramInt3 >= 0))
          {
            localObject1 = JTextComponent.this.model.getText(paramInt2 + paramInt3, 1);
            return localObject1;
          }
          break;
        case 2: 
        case 3: 
          localObject1 = getSegmentAt(paramInt1, paramInt2);
          if (localObject1 != null)
          {
            if (paramInt3 != 0)
            {
              int i;
              if (paramInt3 < 0) {
                i = ((IndexedSegment)localObject1).modelOffset - 1;
              } else {
                i = ((IndexedSegment)localObject1).modelOffset + paramInt3 * ((IndexedSegment)localObject1).count;
              }
              if ((i >= 0) && (i <= JTextComponent.this.model.getLength())) {
                localObject1 = getSegmentAt(paramInt1, i);
              } else {
                localObject1 = null;
              }
            }
            if (localObject1 != null)
            {
              String str = new String(((IndexedSegment)localObject1).array, ((IndexedSegment)localObject1).offset, ((IndexedSegment)localObject1).count);
              return str;
            }
          }
          break;
        }
      }
      catch (BadLocationException localBadLocationException) {}finally
      {
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readUnlock();
        }
      }
      return null;
    }
    
    private Element getParagraphElement(int paramInt)
    {
      if ((JTextComponent.this.model instanceof PlainDocument))
      {
        localObject = (PlainDocument)JTextComponent.this.model;
        return ((PlainDocument)localObject).getParagraphElement(paramInt);
      }
      if ((JTextComponent.this.model instanceof StyledDocument))
      {
        localObject = (StyledDocument)JTextComponent.this.model;
        return ((StyledDocument)localObject).getParagraphElement(paramInt);
      }
      int i;
      for (Object localObject = JTextComponent.this.model.getDefaultRootElement(); !((Element)localObject).isLeaf(); localObject = ((Element)localObject).getElement(i)) {
        i = ((Element)localObject).getElementIndex(paramInt);
      }
      if (localObject == null) {
        return null;
      }
      return ((Element)localObject).getParentElement();
    }
    
    private IndexedSegment getParagraphElementText(int paramInt)
      throws BadLocationException
    {
      Element localElement = getParagraphElement(paramInt);
      if (localElement != null)
      {
        IndexedSegment localIndexedSegment = new IndexedSegment(null);
        try
        {
          int i = localElement.getEndOffset() - localElement.getStartOffset();
          JTextComponent.this.model.getText(localElement.getStartOffset(), i, localIndexedSegment);
        }
        catch (BadLocationException localBadLocationException)
        {
          return null;
        }
        localIndexedSegment.modelOffset = localElement.getStartOffset();
        return localIndexedSegment;
      }
      return null;
    }
    
    private IndexedSegment getSegmentAt(int paramInt1, int paramInt2)
      throws BadLocationException
    {
      IndexedSegment localIndexedSegment = getParagraphElementText(paramInt2);
      if (localIndexedSegment == null) {
        return null;
      }
      BreakIterator localBreakIterator;
      switch (paramInt1)
      {
      case 2: 
        localBreakIterator = BreakIterator.getWordInstance(getLocale());
        break;
      case 3: 
        localBreakIterator = BreakIterator.getSentenceInstance(getLocale());
        break;
      default: 
        return null;
      }
      localIndexedSegment.first();
      localBreakIterator.setText(localIndexedSegment);
      int i = localBreakIterator.following(paramInt2 - localIndexedSegment.modelOffset + localIndexedSegment.offset);
      if (i == -1) {
        return null;
      }
      if (i > localIndexedSegment.offset + localIndexedSegment.count) {
        return null;
      }
      int j = localBreakIterator.previous();
      if ((j == -1) || (j >= localIndexedSegment.offset + localIndexedSegment.count)) {
        return null;
      }
      localIndexedSegment.modelOffset = (localIndexedSegment.modelOffset + j - localIndexedSegment.offset);
      localIndexedSegment.offset = j;
      localIndexedSegment.count = (i - j);
      return localIndexedSegment;
    }
    
    public AccessibleEditableText getAccessibleEditableText()
    {
      return this;
    }
    
    public void setTextContents(String paramString)
    {
      JTextComponent.this.setText(paramString);
    }
    
    public void insertTextAtIndex(int paramInt, String paramString)
    {
      Document localDocument = JTextComponent.this.getDocument();
      if (localDocument != null) {
        try
        {
          if ((paramString != null) && (paramString.length() > 0))
          {
            boolean bool = JTextComponent.this.saveComposedText(paramInt);
            localDocument.insertString(paramInt, paramString, null);
            if (bool) {
              JTextComponent.this.restoreComposedText();
            }
          }
        }
        catch (BadLocationException localBadLocationException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
        }
      }
    }
    
    public String getTextRange(int paramInt1, int paramInt2)
    {
      String str = null;
      int i = Math.min(paramInt1, paramInt2);
      int j = Math.max(paramInt1, paramInt2);
      if (i != j) {
        try
        {
          Document localDocument = JTextComponent.this.getDocument();
          str = localDocument.getText(i, j - i);
        }
        catch (BadLocationException localBadLocationException)
        {
          throw new IllegalArgumentException(localBadLocationException.getMessage());
        }
      }
      return str;
    }
    
    public void delete(int paramInt1, int paramInt2)
    {
      if ((JTextComponent.this.isEditable()) && (isEnabled())) {
        try
        {
          int i = Math.min(paramInt1, paramInt2);
          int j = Math.max(paramInt1, paramInt2);
          if (i != j)
          {
            Document localDocument = JTextComponent.this.getDocument();
            localDocument.remove(i, j - i);
          }
        }
        catch (BadLocationException localBadLocationException) {}
      } else {
        UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
      }
    }
    
    public void cut(int paramInt1, int paramInt2)
    {
      selectText(paramInt1, paramInt2);
      JTextComponent.this.cut();
    }
    
    public void paste(int paramInt)
    {
      JTextComponent.this.setCaretPosition(paramInt);
      JTextComponent.this.paste();
    }
    
    public void replaceText(int paramInt1, int paramInt2, String paramString)
    {
      selectText(paramInt1, paramInt2);
      JTextComponent.this.replaceSelection(paramString);
    }
    
    public void selectText(int paramInt1, int paramInt2)
    {
      JTextComponent.this.select(paramInt1, paramInt2);
    }
    
    public void setAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet)
    {
      Document localDocument = JTextComponent.this.getDocument();
      if ((localDocument != null) && ((localDocument instanceof StyledDocument)))
      {
        StyledDocument localStyledDocument = (StyledDocument)localDocument;
        int i = paramInt1;
        int j = paramInt2 - paramInt1;
        localStyledDocument.setCharacterAttributes(i, j, paramAttributeSet, true);
      }
    }
    
    private AccessibleTextSequence getSequenceAtIndex(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((paramInt2 < 0) || (paramInt2 >= JTextComponent.this.model.getLength())) {
        return null;
      }
      if ((paramInt3 < -1) || (paramInt3 > 1)) {
        return null;
      }
      int i;
      int k;
      switch (paramInt1)
      {
      case 1: 
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readLock();
        }
        AccessibleTextSequence localAccessibleTextSequence1 = null;
        try
        {
          if ((paramInt2 + paramInt3 < JTextComponent.this.model.getLength()) && (paramInt2 + paramInt3 >= 0)) {
            localAccessibleTextSequence1 = new AccessibleTextSequence(paramInt2 + paramInt3, paramInt2 + paramInt3 + 1, JTextComponent.this.model.getText(paramInt2 + paramInt3, 1));
          }
        }
        catch (BadLocationException localBadLocationException1) {}finally
        {
          if ((JTextComponent.this.model instanceof AbstractDocument)) {
            ((AbstractDocument)JTextComponent.this.model).readUnlock();
          }
        }
        return localAccessibleTextSequence1;
      case 2: 
      case 3: 
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readLock();
        }
        AccessibleTextSequence localAccessibleTextSequence2 = null;
        try
        {
          IndexedSegment localIndexedSegment = getSegmentAt(paramInt1, paramInt2);
          if (localIndexedSegment != null)
          {
            if (paramInt3 != 0)
            {
              if (paramInt3 < 0) {
                i = localIndexedSegment.modelOffset - 1;
              } else {
                i = localIndexedSegment.modelOffset + localIndexedSegment.count;
              }
              if ((i >= 0) && (i <= JTextComponent.this.model.getLength())) {
                localIndexedSegment = getSegmentAt(paramInt1, i);
              } else {
                localIndexedSegment = null;
              }
            }
            if ((localIndexedSegment != null) && (localIndexedSegment.offset + localIndexedSegment.count <= JTextComponent.this.model.getLength())) {
              localAccessibleTextSequence2 = new AccessibleTextSequence(localIndexedSegment.offset, localIndexedSegment.offset + localIndexedSegment.count, new String(localIndexedSegment.array, localIndexedSegment.offset, localIndexedSegment.count));
            }
          }
        }
        catch (BadLocationException localBadLocationException2) {}finally
        {
          if ((JTextComponent.this.model instanceof AbstractDocument)) {
            ((AbstractDocument)JTextComponent.this.model).readUnlock();
          }
        }
        return localAccessibleTextSequence2;
      case 4: 
        AccessibleTextSequence localAccessibleTextSequence3 = null;
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readLock();
        }
        try
        {
          i = Utilities.getRowStart(JTextComponent.this, paramInt2);
          k = Utilities.getRowEnd(JTextComponent.this, paramInt2);
          if ((i >= 0) && (k >= i)) {
            if (paramInt3 == 0)
            {
              localAccessibleTextSequence3 = new AccessibleTextSequence(i, k, JTextComponent.this.model.getText(i, k - i + 1));
            }
            else if ((paramInt3 == -1) && (i > 0))
            {
              k = Utilities.getRowEnd(JTextComponent.this, i - 1);
              i = Utilities.getRowStart(JTextComponent.this, i - 1);
              if ((i >= 0) && (k >= i)) {
                localAccessibleTextSequence3 = new AccessibleTextSequence(i, k, JTextComponent.this.model.getText(i, k - i + 1));
              }
            }
            else if ((paramInt3 == 1) && (k < JTextComponent.this.model.getLength()))
            {
              i = Utilities.getRowStart(JTextComponent.this, k + 1);
              k = Utilities.getRowEnd(JTextComponent.this, k + 1);
              if ((i >= 0) && (k >= i)) {
                localAccessibleTextSequence3 = new AccessibleTextSequence(i, k, JTextComponent.this.model.getText(i, k - i + 1));
              }
            }
          }
        }
        catch (BadLocationException localBadLocationException3) {}finally
        {
          if ((JTextComponent.this.model instanceof AbstractDocument)) {
            ((AbstractDocument)JTextComponent.this.model).readUnlock();
          }
        }
        return localAccessibleTextSequence3;
      case 5: 
        String str = null;
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readLock();
        }
        int j;
        try
        {
          j = k = Integer.MIN_VALUE;
          int m = paramInt2;
          switch (paramInt3)
          {
          case -1: 
            k = getRunEdge(paramInt2, paramInt3);
            m = k - 1;
            break;
          case 1: 
            j = getRunEdge(paramInt2, paramInt3);
            m = j;
            break;
          case 0: 
            break;
          default: 
            throw new AssertionError(paramInt3);
          }
          j = j != Integer.MIN_VALUE ? j : getRunEdge(m, -1);
          k = k != Integer.MIN_VALUE ? k : getRunEdge(m, 1);
          str = JTextComponent.this.model.getText(j, k - j);
        }
        catch (BadLocationException localBadLocationException4)
        {
          AccessibleTextSequence localAccessibleTextSequence4 = null;
          return localAccessibleTextSequence4;
        }
        finally
        {
          if ((JTextComponent.this.model instanceof AbstractDocument)) {
            ((AbstractDocument)JTextComponent.this.model).readUnlock();
          }
        }
        return new AccessibleTextSequence(j, k, str);
      }
      return null;
    }
    
    private int getRunEdge(int paramInt1, int paramInt2)
      throws BadLocationException
    {
      if ((paramInt1 < 0) || (paramInt1 >= JTextComponent.this.model.getLength())) {
        throw new BadLocationException("Location out of bounds", paramInt1);
      }
      int i = -1;
      for (Element localElement1 = JTextComponent.this.model.getDefaultRootElement(); !localElement1.isLeaf(); localElement1 = localElement1.getElement(i)) {
        i = localElement1.getElementIndex(paramInt1);
      }
      if (i == -1) {
        throw new AssertionError(paramInt1);
      }
      AttributeSet localAttributeSet = localElement1.getAttributes();
      Element localElement2 = localElement1.getParentElement();
      Element localElement3;
      switch (paramInt2)
      {
      case -1: 
      case 1: 
        int j = i;
        int k = localElement2.getElementCount();
        while ((j + paramInt2 > 0) && (j + paramInt2 < k) && (localElement2.getElement(j + paramInt2).getAttributes().isEqual(localAttributeSet))) {
          j += paramInt2;
        }
        localElement3 = localElement2.getElement(j);
        break;
      default: 
        throw new AssertionError(paramInt2);
      }
      switch (paramInt2)
      {
      case -1: 
        return localElement3.getStartOffset();
      case 1: 
        return localElement3.getEndOffset();
      }
      return Integer.MIN_VALUE;
    }
    
    public AccessibleTextSequence getTextSequenceAt(int paramInt1, int paramInt2)
    {
      return getSequenceAtIndex(paramInt1, paramInt2, 0);
    }
    
    public AccessibleTextSequence getTextSequenceAfter(int paramInt1, int paramInt2)
    {
      return getSequenceAtIndex(paramInt1, paramInt2, 1);
    }
    
    public AccessibleTextSequence getTextSequenceBefore(int paramInt1, int paramInt2)
    {
      return getSequenceAtIndex(paramInt1, paramInt2, -1);
    }
    
    public Rectangle getTextBounds(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt1 > JTextComponent.this.model.getLength() - 1) || (paramInt2 < 0) || (paramInt2 > JTextComponent.this.model.getLength() - 1) || (paramInt1 > paramInt2)) {
        return null;
      }
      TextUI localTextUI = JTextComponent.this.getUI();
      if (localTextUI == null) {
        return null;
      }
      Rectangle localRectangle1 = null;
      Rectangle localRectangle2 = getRootEditorRect();
      if (localRectangle2 == null) {
        return null;
      }
      if ((JTextComponent.this.model instanceof AbstractDocument)) {
        ((AbstractDocument)JTextComponent.this.model).readLock();
      }
      try
      {
        View localView = localTextUI.getRootView(JTextComponent.this);
        if (localView != null)
        {
          Shape localShape = localView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, localRectangle2);
          localRectangle1 = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
        }
      }
      catch (BadLocationException localBadLocationException) {}finally
      {
        if ((JTextComponent.this.model instanceof AbstractDocument)) {
          ((AbstractDocument)JTextComponent.this.model).readUnlock();
        }
      }
      return localRectangle1;
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public int getAccessibleActionCount()
    {
      Action[] arrayOfAction = JTextComponent.this.getActions();
      return arrayOfAction.length;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      Action[] arrayOfAction = JTextComponent.this.getActions();
      if ((paramInt < 0) || (paramInt >= arrayOfAction.length)) {
        return null;
      }
      return (String)arrayOfAction[paramInt].getValue("Name");
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      Action[] arrayOfAction = JTextComponent.this.getActions();
      if ((paramInt < 0) || (paramInt >= arrayOfAction.length)) {
        return false;
      }
      ActionEvent localActionEvent = new ActionEvent(JTextComponent.this, 1001, null, EventQueue.getMostRecentEventTime(), JTextComponent.this.getCurrentEventModifiers());
      arrayOfAction[paramInt].actionPerformed(localActionEvent);
      return true;
    }
    
    private class IndexedSegment
      extends Segment
    {
      public int modelOffset;
      
      private IndexedSegment() {}
    }
  }
  
  class ComposedTextCaret
    extends DefaultCaret
    implements Serializable
  {
    Color bg;
    
    ComposedTextCaret() {}
    
    public void install(JTextComponent paramJTextComponent)
    {
      super.install(paramJTextComponent);
      Document localDocument = paramJTextComponent.getDocument();
      if ((localDocument instanceof StyledDocument))
      {
        StyledDocument localStyledDocument = (StyledDocument)localDocument;
        Element localElement = localStyledDocument.getCharacterElement(paramJTextComponent.composedTextStart.getOffset());
        AttributeSet localAttributeSet = localElement.getAttributes();
        this.bg = localStyledDocument.getBackground(localAttributeSet);
      }
      if (this.bg == null) {
        this.bg = paramJTextComponent.getBackground();
      }
    }
    
    public void paint(Graphics paramGraphics)
    {
      if (isVisible()) {
        try
        {
          Rectangle localRectangle = this.component.modelToView(getDot());
          paramGraphics.setXORMode(this.bg);
          paramGraphics.drawLine(localRectangle.x, localRectangle.y, localRectangle.x, localRectangle.y + localRectangle.height - 1);
          paramGraphics.setPaintMode();
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
    
    protected void positionCaret(MouseEvent paramMouseEvent)
    {
      JTextComponent localJTextComponent = this.component;
      Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
      int i = localJTextComponent.viewToModel(localPoint);
      int j = localJTextComponent.composedTextStart.getOffset();
      if ((i < j) || (i > JTextComponent.this.composedTextEnd.getOffset())) {
        try
        {
          Position localPosition = localJTextComponent.getDocument().createPosition(i);
          localJTextComponent.getInputContext().endComposition();
          EventQueue.invokeLater(new JTextComponent.DoSetCaretPosition(JTextComponent.this, localJTextComponent, localPosition));
        }
        catch (BadLocationException localBadLocationException)
        {
          System.err.println(localBadLocationException);
        }
      } else {
        super.positionCaret(paramMouseEvent);
      }
    }
  }
  
  static class DefaultKeymap
    implements Keymap
  {
    String nm;
    Keymap parent;
    Hashtable<KeyStroke, Action> bindings;
    Action defaultAction;
    
    DefaultKeymap(String paramString, Keymap paramKeymap)
    {
      this.nm = paramString;
      this.parent = paramKeymap;
      this.bindings = new Hashtable();
    }
    
    public Action getDefaultAction()
    {
      if (this.defaultAction != null) {
        return this.defaultAction;
      }
      return this.parent != null ? this.parent.getDefaultAction() : null;
    }
    
    public void setDefaultAction(Action paramAction)
    {
      this.defaultAction = paramAction;
    }
    
    public String getName()
    {
      return this.nm;
    }
    
    public Action getAction(KeyStroke paramKeyStroke)
    {
      Action localAction = (Action)this.bindings.get(paramKeyStroke);
      if ((localAction == null) && (this.parent != null)) {
        localAction = this.parent.getAction(paramKeyStroke);
      }
      return localAction;
    }
    
    public KeyStroke[] getBoundKeyStrokes()
    {
      KeyStroke[] arrayOfKeyStroke = new KeyStroke[this.bindings.size()];
      int i = 0;
      Enumeration localEnumeration = this.bindings.keys();
      while (localEnumeration.hasMoreElements()) {
        arrayOfKeyStroke[(i++)] = ((KeyStroke)localEnumeration.nextElement());
      }
      return arrayOfKeyStroke;
    }
    
    public Action[] getBoundActions()
    {
      Action[] arrayOfAction = new Action[this.bindings.size()];
      int i = 0;
      Enumeration localEnumeration = this.bindings.elements();
      while (localEnumeration.hasMoreElements()) {
        arrayOfAction[(i++)] = ((Action)localEnumeration.nextElement());
      }
      return arrayOfAction;
    }
    
    public KeyStroke[] getKeyStrokesForAction(Action paramAction)
    {
      if (paramAction == null) {
        return null;
      }
      Object localObject1 = null;
      Vector localVector = null;
      Object localObject2 = this.bindings.keys();
      while (((Enumeration)localObject2).hasMoreElements())
      {
        KeyStroke localKeyStroke = (KeyStroke)((Enumeration)localObject2).nextElement();
        if (this.bindings.get(localKeyStroke) == paramAction)
        {
          if (localVector == null) {
            localVector = new Vector();
          }
          localVector.addElement(localKeyStroke);
        }
      }
      if (this.parent != null)
      {
        localObject2 = this.parent.getKeyStrokesForAction(paramAction);
        if (localObject2 != null)
        {
          int i = 0;
          for (int j = localObject2.length - 1; j >= 0; j--) {
            if (isLocallyDefined(localObject2[j]))
            {
              localObject2[j] = null;
              i++;
            }
          }
          if ((i > 0) && (i < localObject2.length))
          {
            if (localVector == null) {
              localVector = new Vector();
            }
            for (j = localObject2.length - 1; j >= 0; j--) {
              if (localObject2[j] != null) {
                localVector.addElement(localObject2[j]);
              }
            }
          }
          else if (i == 0)
          {
            if (localVector == null)
            {
              localObject1 = localObject2;
            }
            else
            {
              localObject1 = new KeyStroke[localVector.size() + localObject2.length];
              localVector.copyInto((Object[])localObject1);
              System.arraycopy(localObject2, 0, localObject1, localVector.size(), localObject2.length);
              localVector = null;
            }
          }
        }
      }
      if (localVector != null)
      {
        localObject1 = new KeyStroke[localVector.size()];
        localVector.copyInto((Object[])localObject1);
      }
      return localObject1;
    }
    
    public boolean isLocallyDefined(KeyStroke paramKeyStroke)
    {
      return this.bindings.containsKey(paramKeyStroke);
    }
    
    public void addActionForKeyStroke(KeyStroke paramKeyStroke, Action paramAction)
    {
      this.bindings.put(paramKeyStroke, paramAction);
    }
    
    public void removeKeyStrokeBinding(KeyStroke paramKeyStroke)
    {
      this.bindings.remove(paramKeyStroke);
    }
    
    public void removeBindings()
    {
      this.bindings.clear();
    }
    
    public Keymap getResolveParent()
    {
      return this.parent;
    }
    
    public void setResolveParent(Keymap paramKeymap)
    {
      this.parent = paramKeymap;
    }
    
    public String toString()
    {
      return "Keymap[" + this.nm + "]" + this.bindings;
    }
  }
  
  static class DefaultTransferHandler
    extends TransferHandler
    implements UIResource
  {
    DefaultTransferHandler() {}
    
    public void exportToClipboard(JComponent paramJComponent, Clipboard paramClipboard, int paramInt)
      throws IllegalStateException
    {
      if ((paramJComponent instanceof JTextComponent))
      {
        JTextComponent localJTextComponent = (JTextComponent)paramJComponent;
        int i = localJTextComponent.getSelectionStart();
        int j = localJTextComponent.getSelectionEnd();
        if (i != j) {
          try
          {
            Document localDocument = localJTextComponent.getDocument();
            String str = localDocument.getText(i, j - i);
            StringSelection localStringSelection = new StringSelection(str);
            paramClipboard.setContents(localStringSelection, null);
            if (paramInt == 2) {
              localDocument.remove(i, j - i);
            }
          }
          catch (BadLocationException localBadLocationException) {}
        }
      }
    }
    
    public boolean importData(JComponent paramJComponent, Transferable paramTransferable)
    {
      if ((paramJComponent instanceof JTextComponent))
      {
        DataFlavor localDataFlavor = getFlavor(paramTransferable.getTransferDataFlavors());
        if (localDataFlavor != null)
        {
          InputContext localInputContext = paramJComponent.getInputContext();
          if (localInputContext != null) {
            localInputContext.endComposition();
          }
          try
          {
            String str = (String)paramTransferable.getTransferData(localDataFlavor);
            ((JTextComponent)paramJComponent).replaceSelection(str);
            return true;
          }
          catch (UnsupportedFlavorException localUnsupportedFlavorException) {}catch (IOException localIOException) {}
        }
      }
      return false;
    }
    
    public boolean canImport(JComponent paramJComponent, DataFlavor[] paramArrayOfDataFlavor)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramJComponent;
      if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled())) {
        return false;
      }
      return getFlavor(paramArrayOfDataFlavor) != null;
    }
    
    public int getSourceActions(JComponent paramJComponent)
    {
      return 0;
    }
    
    private DataFlavor getFlavor(DataFlavor[] paramArrayOfDataFlavor)
    {
      if (paramArrayOfDataFlavor != null) {
        for (DataFlavor localDataFlavor : paramArrayOfDataFlavor) {
          if (localDataFlavor.equals(DataFlavor.stringFlavor)) {
            return localDataFlavor;
          }
        }
      }
      return null;
    }
  }
  
  private class DoSetCaretPosition
    implements Runnable
  {
    JTextComponent host;
    Position newPos;
    
    DoSetCaretPosition(JTextComponent paramJTextComponent, Position paramPosition)
    {
      this.host = paramJTextComponent;
      this.newPos = paramPosition;
    }
    
    public void run()
    {
      this.host.setCaretPosition(this.newPos.getOffset());
    }
  }
  
  public static final class DropLocation
    extends TransferHandler.DropLocation
  {
    private final int index;
    private final Position.Bias bias;
    
    private DropLocation(Point paramPoint, int paramInt, Position.Bias paramBias)
    {
      super();
      this.index = paramInt;
      this.bias = paramBias;
    }
    
    public int getIndex()
    {
      return this.index;
    }
    
    public Position.Bias getBias()
    {
      return this.bias;
    }
    
    public String toString()
    {
      return getClass().getName() + "[dropPoint=" + getDropPoint() + "," + "index=" + this.index + "," + "bias=" + this.bias + "]";
    }
  }
  
  class InputMethodRequestsHandler
    implements InputMethodRequests, DocumentListener
  {
    InputMethodRequestsHandler() {}
    
    public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
    {
      Document localDocument = JTextComponent.this.getDocument();
      if ((localDocument != null) && (JTextComponent.this.latestCommittedTextStart != null) && (!JTextComponent.this.latestCommittedTextStart.equals(JTextComponent.this.latestCommittedTextEnd))) {
        try
        {
          int i = JTextComponent.this.latestCommittedTextStart.getOffset();
          int j = JTextComponent.this.latestCommittedTextEnd.getOffset();
          String str = localDocument.getText(i, j - i);
          localDocument.remove(i, j - i);
          return new AttributedString(str).getIterator();
        }
        catch (BadLocationException localBadLocationException) {}
      }
      return null;
    }
    
    public AttributedCharacterIterator getCommittedText(int paramInt1, int paramInt2, AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
    {
      int i = 0;
      int j = 0;
      if (JTextComponent.this.composedTextExists())
      {
        i = JTextComponent.this.composedTextStart.getOffset();
        j = JTextComponent.this.composedTextEnd.getOffset();
      }
      String str;
      try
      {
        if (paramInt1 < i)
        {
          if (paramInt2 <= i)
          {
            str = JTextComponent.this.getText(paramInt1, paramInt2 - paramInt1);
          }
          else
          {
            int k = i - paramInt1;
            str = JTextComponent.this.getText(paramInt1, k) + JTextComponent.this.getText(j, paramInt2 - paramInt1 - k);
          }
        }
        else {
          str = JTextComponent.this.getText(paramInt1 + (j - i), paramInt2 - paramInt1);
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new IllegalArgumentException("Invalid range");
      }
      return new AttributedString(str).getIterator();
    }
    
    public int getCommittedTextLength()
    {
      Document localDocument = JTextComponent.this.getDocument();
      int i = 0;
      if (localDocument != null)
      {
        i = localDocument.getLength();
        if (JTextComponent.this.composedTextContent != null) {
          if ((JTextComponent.this.composedTextEnd == null) || (JTextComponent.this.composedTextStart == null)) {
            i -= JTextComponent.this.composedTextContent.length();
          } else {
            i -= JTextComponent.this.composedTextEnd.getOffset() - JTextComponent.this.composedTextStart.getOffset();
          }
        }
      }
      return i;
    }
    
    public int getInsertPositionOffset()
    {
      int i = 0;
      int j = 0;
      if (JTextComponent.this.composedTextExists())
      {
        i = JTextComponent.this.composedTextStart.getOffset();
        j = JTextComponent.this.composedTextEnd.getOffset();
      }
      int k = JTextComponent.this.getCaretPosition();
      if (k < i) {
        return k;
      }
      if (k < j) {
        return i;
      }
      return k - (j - i);
    }
    
    public TextHitInfo getLocationOffset(int paramInt1, int paramInt2)
    {
      if (JTextComponent.this.composedTextAttribute == null) {
        return null;
      }
      Point localPoint = JTextComponent.this.getLocationOnScreen();
      localPoint.x = (paramInt1 - localPoint.x);
      localPoint.y = (paramInt2 - localPoint.y);
      int i = JTextComponent.this.viewToModel(localPoint);
      if ((i >= JTextComponent.this.composedTextStart.getOffset()) && (i <= JTextComponent.this.composedTextEnd.getOffset())) {
        return TextHitInfo.leading(i - JTextComponent.this.composedTextStart.getOffset());
      }
      return null;
    }
    
    public Rectangle getTextLocation(TextHitInfo paramTextHitInfo)
    {
      Rectangle localRectangle;
      try
      {
        localRectangle = JTextComponent.this.modelToView(JTextComponent.this.getCaretPosition());
        if (localRectangle != null)
        {
          Point localPoint = JTextComponent.this.getLocationOnScreen();
          localRectangle.translate(localPoint.x, localPoint.y);
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        localRectangle = null;
      }
      if (localRectangle == null) {
        localRectangle = new Rectangle();
      }
      return localRectangle;
    }
    
    public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
    {
      String str = JTextComponent.this.getSelectedText();
      if (str != null) {
        return new AttributedString(str).getIterator();
      }
      return null;
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      JTextComponent.this.latestCommittedTextStart = JTextComponent.access$602(JTextComponent.this, null);
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      JTextComponent.this.latestCommittedTextStart = JTextComponent.access$602(JTextComponent.this, null);
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      JTextComponent.this.latestCommittedTextStart = JTextComponent.access$602(JTextComponent.this, null);
    }
  }
  
  public static class KeyBinding
  {
    public KeyStroke key;
    public String actionName;
    
    public KeyBinding(KeyStroke paramKeyStroke, String paramString)
    {
      this.key = paramKeyStroke;
      this.actionName = paramString;
    }
  }
  
  static class KeymapActionMap
    extends ActionMap
  {
    private Keymap keymap;
    
    KeymapActionMap(Keymap paramKeymap)
    {
      this.keymap = paramKeymap;
    }
    
    public Object[] keys()
    {
      Object[] arrayOfObject1 = super.keys();
      Action[] arrayOfAction = this.keymap.getBoundActions();
      int i = arrayOfObject1 == null ? 0 : arrayOfObject1.length;
      int j = arrayOfAction == null ? 0 : arrayOfAction.length;
      int k = this.keymap.getDefaultAction() != null ? 1 : 0;
      if (k != 0) {
        j++;
      }
      if (i == 0)
      {
        if (k != 0)
        {
          arrayOfObject2 = new Object[j];
          if (j > 1) {
            System.arraycopy(arrayOfAction, 0, arrayOfObject2, 0, j - 1);
          }
          arrayOfObject2[(j - 1)] = JTextComponent.KeymapWrapper.DefaultActionKey;
          return arrayOfObject2;
        }
        return arrayOfAction;
      }
      if (j == 0) {
        return arrayOfObject1;
      }
      Object[] arrayOfObject2 = new Object[i + j];
      System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, i);
      if (k != 0)
      {
        if (j > 1) {
          System.arraycopy(arrayOfAction, 0, arrayOfObject2, i, j - 1);
        }
        arrayOfObject2[(i + j - 1)] = JTextComponent.KeymapWrapper.DefaultActionKey;
      }
      else
      {
        System.arraycopy(arrayOfAction, 0, arrayOfObject2, i, j);
      }
      return arrayOfObject2;
    }
    
    public int size()
    {
      Action[] arrayOfAction = this.keymap.getBoundActions();
      int i = arrayOfAction == null ? 0 : arrayOfAction.length;
      if (this.keymap.getDefaultAction() != null) {
        i++;
      }
      return super.size() + i;
    }
    
    public Action get(Object paramObject)
    {
      Action localAction = super.get(paramObject);
      if (localAction == null) {
        if (paramObject == JTextComponent.KeymapWrapper.DefaultActionKey) {
          localAction = this.keymap.getDefaultAction();
        } else if ((paramObject instanceof Action)) {
          localAction = (Action)paramObject;
        }
      }
      return localAction;
    }
  }
  
  static class KeymapWrapper
    extends InputMap
  {
    static final Object DefaultActionKey = new Object();
    private Keymap keymap;
    
    KeymapWrapper(Keymap paramKeymap)
    {
      this.keymap = paramKeymap;
    }
    
    public KeyStroke[] keys()
    {
      KeyStroke[] arrayOfKeyStroke1 = super.keys();
      KeyStroke[] arrayOfKeyStroke2 = this.keymap.getBoundKeyStrokes();
      int i = arrayOfKeyStroke1 == null ? 0 : arrayOfKeyStroke1.length;
      int j = arrayOfKeyStroke2 == null ? 0 : arrayOfKeyStroke2.length;
      if (i == 0) {
        return arrayOfKeyStroke2;
      }
      if (j == 0) {
        return arrayOfKeyStroke1;
      }
      KeyStroke[] arrayOfKeyStroke3 = new KeyStroke[i + j];
      System.arraycopy(arrayOfKeyStroke1, 0, arrayOfKeyStroke3, 0, i);
      System.arraycopy(arrayOfKeyStroke2, 0, arrayOfKeyStroke3, i, j);
      return arrayOfKeyStroke3;
    }
    
    public int size()
    {
      KeyStroke[] arrayOfKeyStroke = this.keymap.getBoundKeyStrokes();
      int i = arrayOfKeyStroke == null ? 0 : arrayOfKeyStroke.length;
      return super.size() + i;
    }
    
    public Object get(KeyStroke paramKeyStroke)
    {
      Object localObject = this.keymap.getAction(paramKeyStroke);
      if (localObject == null)
      {
        localObject = super.get(paramKeyStroke);
        if ((localObject == null) && (paramKeyStroke.getKeyChar() != 65535) && (this.keymap.getDefaultAction() != null)) {
          localObject = DefaultActionKey;
        }
      }
      return localObject;
    }
  }
  
  static class MutableCaretEvent
    extends CaretEvent
    implements ChangeListener, FocusListener, MouseListener
  {
    private boolean dragActive;
    private int dot;
    private int mark;
    
    MutableCaretEvent(JTextComponent paramJTextComponent)
    {
      super();
    }
    
    final void fire()
    {
      JTextComponent localJTextComponent = (JTextComponent)getSource();
      if (localJTextComponent != null)
      {
        Caret localCaret = localJTextComponent.getCaret();
        this.dot = localCaret.getDot();
        this.mark = localCaret.getMark();
        localJTextComponent.fireCaretUpdate(this);
      }
    }
    
    public final String toString()
    {
      return "dot=" + this.dot + "," + "mark=" + this.mark;
    }
    
    public final int getDot()
    {
      return this.dot;
    }
    
    public final int getMark()
    {
      return this.mark;
    }
    
    public final void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (!this.dragActive) {
        fire();
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      AppContext.getAppContext().put(JTextComponent.FOCUSED_COMPONENT, paramFocusEvent.getSource());
    }
    
    public void focusLost(FocusEvent paramFocusEvent) {}
    
    public final void mousePressed(MouseEvent paramMouseEvent)
    {
      this.dragActive = true;
    }
    
    public final void mouseReleased(MouseEvent paramMouseEvent)
    {
      this.dragActive = false;
      fire();
    }
    
    public final void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public final void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public final void mouseExited(MouseEvent paramMouseEvent) {}
  }
}
