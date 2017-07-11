package javax.swing.text;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TextUI;
import sun.swing.SwingUtilities2;

public class DefaultCaret
  extends Rectangle
  implements Caret, FocusListener, MouseListener, MouseMotionListener
{
  public static final int UPDATE_WHEN_ON_EDT = 0;
  public static final int NEVER_UPDATE = 1;
  public static final int ALWAYS_UPDATE = 2;
  protected EventListenerList listenerList = new EventListenerList();
  protected transient ChangeEvent changeEvent = null;
  JTextComponent component;
  int updatePolicy = 0;
  boolean visible;
  boolean active;
  int dot;
  int mark;
  Object selectionTag;
  boolean selectionVisible;
  Timer flasher;
  Point magicCaretPosition;
  transient Position.Bias dotBias;
  transient Position.Bias markBias;
  boolean dotLTR;
  boolean markLTR;
  transient Handler handler = new Handler();
  private transient int[] flagXPoints = new int[3];
  private transient int[] flagYPoints = new int[3];
  private transient NavigationFilter.FilterBypass filterBypass;
  private static transient Action selectWord = null;
  private static transient Action selectLine = null;
  private boolean ownsSelection;
  private boolean forceCaretPositionChange;
  private transient boolean shouldHandleRelease;
  private transient MouseEvent selectedWordEvent = null;
  private int caretWidth = -1;
  private float aspectRatio = -1.0F;
  
  public DefaultCaret() {}
  
  public void setUpdatePolicy(int paramInt)
  {
    this.updatePolicy = paramInt;
  }
  
  public int getUpdatePolicy()
  {
    return this.updatePolicy;
  }
  
  protected final JTextComponent getComponent()
  {
    return this.component;
  }
  
  protected final synchronized void repaint()
  {
    if (this.component != null) {
      this.component.repaint(this.x, this.y, this.width, this.height);
    }
  }
  
  protected synchronized void damage(Rectangle paramRectangle)
  {
    if (paramRectangle != null)
    {
      int i = getCaretWidth(paramRectangle.height);
      this.x = (paramRectangle.x - 4 - (i >> 1));
      this.y = paramRectangle.y;
      this.width = (9 + i);
      this.height = paramRectangle.height;
      repaint();
    }
  }
  
  protected void adjustVisibility(Rectangle paramRectangle)
  {
    if (this.component == null) {
      return;
    }
    if (SwingUtilities.isEventDispatchThread()) {
      this.component.scrollRectToVisible(paramRectangle);
    } else {
      SwingUtilities.invokeLater(new SafeScroller(paramRectangle));
    }
  }
  
  protected Highlighter.HighlightPainter getSelectionPainter()
  {
    return DefaultHighlighter.DefaultPainter;
  }
  
  protected void positionCaret(MouseEvent paramMouseEvent)
  {
    Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = this.component.getUI().viewToModel(this.component, localPoint, arrayOfBias);
    if (arrayOfBias[0] == null) {
      arrayOfBias[0] = Position.Bias.Forward;
    }
    if (i >= 0) {
      setDot(i, arrayOfBias[0]);
    }
  }
  
  protected void moveCaret(MouseEvent paramMouseEvent)
  {
    Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = this.component.getUI().viewToModel(this.component, localPoint, arrayOfBias);
    if (arrayOfBias[0] == null) {
      arrayOfBias[0] = Position.Bias.Forward;
    }
    if (i >= 0) {
      moveDot(i, arrayOfBias[0]);
    }
  }
  
  public void focusGained(FocusEvent paramFocusEvent)
  {
    if (this.component.isEnabled())
    {
      if (this.component.isEditable()) {
        setVisible(true);
      }
      setSelectionVisible(true);
    }
  }
  
  public void focusLost(FocusEvent paramFocusEvent)
  {
    setVisible(false);
    setSelectionVisible((this.ownsSelection) || (paramFocusEvent.isTemporary()));
  }
  
  private void selectWord(MouseEvent paramMouseEvent)
  {
    if ((this.selectedWordEvent != null) && (this.selectedWordEvent.getX() == paramMouseEvent.getX()) && (this.selectedWordEvent.getY() == paramMouseEvent.getY())) {
      return;
    }
    Action localAction = null;
    ActionMap localActionMap = getComponent().getActionMap();
    if (localActionMap != null) {
      localAction = localActionMap.get("select-word");
    }
    if (localAction == null)
    {
      if (selectWord == null) {
        selectWord = new DefaultEditorKit.SelectWordAction();
      }
      localAction = selectWord;
    }
    localAction.actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
    this.selectedWordEvent = paramMouseEvent;
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    if (getComponent() == null) {
      return;
    }
    int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
    if (!paramMouseEvent.isConsumed())
    {
      Object localObject1;
      Object localObject2;
      if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
      {
        if (i == 1)
        {
          this.selectedWordEvent = null;
        }
        else if ((i == 2) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
        {
          selectWord(paramMouseEvent);
          this.selectedWordEvent = null;
        }
        else if ((i == 3) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
        {
          localObject1 = null;
          localObject2 = getComponent().getActionMap();
          if (localObject2 != null) {
            localObject1 = ((ActionMap)localObject2).get("select-line");
          }
          if (localObject1 == null)
          {
            if (selectLine == null) {
              selectLine = new DefaultEditorKit.SelectLineAction();
            }
            localObject1 = selectLine;
          }
          ((Action)localObject1).actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
        }
      }
      else if ((SwingUtilities.isMiddleMouseButton(paramMouseEvent)) && (i == 1) && (this.component.isEditable()) && (this.component.isEnabled()) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
      {
        localObject1 = (JTextComponent)paramMouseEvent.getSource();
        if (localObject1 != null) {
          try
          {
            localObject2 = ((JTextComponent)localObject1).getToolkit();
            Clipboard localClipboard = ((Toolkit)localObject2).getSystemSelection();
            if (localClipboard != null)
            {
              adjustCaret(paramMouseEvent);
              TransferHandler localTransferHandler = ((JTextComponent)localObject1).getTransferHandler();
              if (localTransferHandler != null)
              {
                Transferable localTransferable = null;
                try
                {
                  localTransferable = localClipboard.getContents(null);
                }
                catch (IllegalStateException localIllegalStateException)
                {
                  UIManager.getLookAndFeel().provideErrorFeedback((Component)localObject1);
                }
                if (localTransferable != null) {
                  localTransferHandler.importData((JComponent)localObject1, localTransferable);
                }
              }
              adjustFocus(true);
            }
          }
          catch (HeadlessException localHeadlessException) {}
        }
      }
    }
  }
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
      if (paramMouseEvent.isConsumed())
      {
        this.shouldHandleRelease = true;
      }
      else
      {
        this.shouldHandleRelease = false;
        adjustCaretAndFocus(paramMouseEvent);
        if ((i == 2) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent))) {
          selectWord(paramMouseEvent);
        }
      }
    }
  }
  
  void adjustCaretAndFocus(MouseEvent paramMouseEvent)
  {
    adjustCaret(paramMouseEvent);
    adjustFocus(false);
  }
  
  private void adjustCaret(MouseEvent paramMouseEvent)
  {
    if (((paramMouseEvent.getModifiers() & 0x1) != 0) && (getDot() != -1)) {
      moveCaret(paramMouseEvent);
    } else if (!paramMouseEvent.isPopupTrigger()) {
      positionCaret(paramMouseEvent);
    }
  }
  
  private void adjustFocus(boolean paramBoolean)
  {
    if ((this.component != null) && (this.component.isEnabled()) && (this.component.isRequestFocusEnabled())) {
      if (paramBoolean) {
        this.component.requestFocusInWindow();
      } else {
        this.component.requestFocus();
      }
    }
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    if ((!paramMouseEvent.isConsumed()) && (this.shouldHandleRelease) && (SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      adjustCaretAndFocus(paramMouseEvent);
    }
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    if ((!paramMouseEvent.isConsumed()) && (SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      moveCaret(paramMouseEvent);
    }
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void paint(Graphics paramGraphics)
  {
    if (isVisible()) {
      try
      {
        TextUI localTextUI = this.component.getUI();
        Rectangle localRectangle1 = localTextUI.modelToView(this.component, this.dot, this.dotBias);
        if ((localRectangle1 == null) || ((localRectangle1.width == 0) && (localRectangle1.height == 0))) {
          return;
        }
        if ((this.width > 0) && (this.height > 0) && (!_contains(localRectangle1.x, localRectangle1.y, localRectangle1.width, localRectangle1.height)))
        {
          Rectangle localRectangle2 = paramGraphics.getClipBounds();
          if ((localRectangle2 != null) && (!localRectangle2.contains(this))) {
            repaint();
          }
          damage(localRectangle1);
        }
        paramGraphics.setColor(this.component.getCaretColor());
        int i = getCaretWidth(localRectangle1.height);
        localRectangle1.x -= (i >> 1);
        paramGraphics.fillRect(localRectangle1.x, localRectangle1.y, i, localRectangle1.height);
        Document localDocument = this.component.getDocument();
        if ((localDocument instanceof AbstractDocument))
        {
          Element localElement = ((AbstractDocument)localDocument).getBidiRootElement();
          if ((localElement != null) && (localElement.getElementCount() > 1))
          {
            this.flagXPoints[0] = (localRectangle1.x + (this.dotLTR ? i : 0));
            this.flagYPoints[0] = localRectangle1.y;
            this.flagXPoints[1] = this.flagXPoints[0];
            this.flagYPoints[1] = (this.flagYPoints[0] + 4);
            this.flagXPoints[2] = (this.flagXPoints[0] + (this.dotLTR ? 4 : -4));
            this.flagYPoints[2] = this.flagYPoints[0];
            paramGraphics.fillPolygon(this.flagXPoints, this.flagYPoints, 3);
          }
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
  }
  
  public void install(JTextComponent paramJTextComponent)
  {
    this.component = paramJTextComponent;
    Document localDocument = paramJTextComponent.getDocument();
    this.dot = (this.mark = 0);
    this.dotLTR = (this.markLTR = 1);
    this.dotBias = (this.markBias = Position.Bias.Forward);
    if (localDocument != null) {
      localDocument.addDocumentListener(this.handler);
    }
    paramJTextComponent.addPropertyChangeListener(this.handler);
    paramJTextComponent.addFocusListener(this);
    paramJTextComponent.addMouseListener(this);
    paramJTextComponent.addMouseMotionListener(this);
    if (this.component.hasFocus()) {
      focusGained(null);
    }
    Number localNumber = (Number)paramJTextComponent.getClientProperty("caretAspectRatio");
    if (localNumber != null) {
      this.aspectRatio = localNumber.floatValue();
    } else {
      this.aspectRatio = -1.0F;
    }
    Integer localInteger = (Integer)paramJTextComponent.getClientProperty("caretWidth");
    if (localInteger != null) {
      this.caretWidth = localInteger.intValue();
    } else {
      this.caretWidth = -1;
    }
  }
  
  public void deinstall(JTextComponent paramJTextComponent)
  {
    paramJTextComponent.removeMouseListener(this);
    paramJTextComponent.removeMouseMotionListener(this);
    paramJTextComponent.removeFocusListener(this);
    paramJTextComponent.removePropertyChangeListener(this.handler);
    Document localDocument = paramJTextComponent.getDocument();
    if (localDocument != null) {
      localDocument.removeDocumentListener(this.handler);
    }
    synchronized (this)
    {
      this.component = null;
    }
    if (this.flasher != null) {
      this.flasher.stop();
    }
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    this.listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    this.listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (this.changeEvent == null) {
          this.changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(this.changeEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return this.listenerList.getListeners(paramClass);
  }
  
  public void setSelectionVisible(boolean paramBoolean)
  {
    if (paramBoolean != this.selectionVisible)
    {
      this.selectionVisible = paramBoolean;
      Highlighter localHighlighter;
      if (this.selectionVisible)
      {
        localHighlighter = this.component.getHighlighter();
        if ((this.dot != this.mark) && (localHighlighter != null) && (this.selectionTag == null))
        {
          int i = Math.min(this.dot, this.mark);
          int j = Math.max(this.dot, this.mark);
          Highlighter.HighlightPainter localHighlightPainter = getSelectionPainter();
          try
          {
            this.selectionTag = localHighlighter.addHighlight(i, j, localHighlightPainter);
          }
          catch (BadLocationException localBadLocationException)
          {
            this.selectionTag = null;
          }
        }
      }
      else if (this.selectionTag != null)
      {
        localHighlighter = this.component.getHighlighter();
        localHighlighter.removeHighlight(this.selectionTag);
        this.selectionTag = null;
      }
    }
  }
  
  public boolean isSelectionVisible()
  {
    return this.selectionVisible;
  }
  
  public boolean isActive()
  {
    return this.active;
  }
  
  public boolean isVisible()
  {
    return this.visible;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.active = paramBoolean;
    if (this.component != null)
    {
      TextUI localTextUI = this.component.getUI();
      if (this.visible != paramBoolean)
      {
        this.visible = paramBoolean;
        try
        {
          Rectangle localRectangle = localTextUI.modelToView(this.component, this.dot, this.dotBias);
          damage(localRectangle);
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
    if (this.flasher != null) {
      if (this.visible) {
        this.flasher.start();
      } else {
        this.flasher.stop();
      }
    }
  }
  
  public void setBlinkRate(int paramInt)
  {
    if (paramInt != 0)
    {
      if (this.flasher == null) {
        this.flasher = new Timer(paramInt, this.handler);
      }
      this.flasher.setDelay(paramInt);
    }
    else if (this.flasher != null)
    {
      this.flasher.stop();
      this.flasher.removeActionListener(this.handler);
      this.flasher = null;
    }
  }
  
  public int getBlinkRate()
  {
    return this.flasher == null ? 0 : this.flasher.getDelay();
  }
  
  public int getDot()
  {
    return this.dot;
  }
  
  public int getMark()
  {
    return this.mark;
  }
  
  public void setDot(int paramInt)
  {
    setDot(paramInt, Position.Bias.Forward);
  }
  
  public void moveDot(int paramInt)
  {
    moveDot(paramInt, Position.Bias.Forward);
  }
  
  public void moveDot(int paramInt, Position.Bias paramBias)
  {
    if (paramBias == null) {
      throw new IllegalArgumentException("null bias");
    }
    if (!this.component.isEnabled())
    {
      setDot(paramInt, paramBias);
      return;
    }
    if (paramInt != this.dot)
    {
      NavigationFilter localNavigationFilter = this.component.getNavigationFilter();
      if (localNavigationFilter != null) {
        localNavigationFilter.moveDot(getFilterBypass(), paramInt, paramBias);
      } else {
        handleMoveDot(paramInt, paramBias);
      }
    }
  }
  
  void handleMoveDot(int paramInt, Position.Bias paramBias)
  {
    changeCaretPosition(paramInt, paramBias);
    if (this.selectionVisible)
    {
      Highlighter localHighlighter = this.component.getHighlighter();
      if (localHighlighter != null)
      {
        int i = Math.min(paramInt, this.mark);
        int j = Math.max(paramInt, this.mark);
        if (i == j)
        {
          if (this.selectionTag != null)
          {
            localHighlighter.removeHighlight(this.selectionTag);
            this.selectionTag = null;
          }
        }
        else {
          try
          {
            if (this.selectionTag != null)
            {
              localHighlighter.changeHighlight(this.selectionTag, i, j);
            }
            else
            {
              Highlighter.HighlightPainter localHighlightPainter = getSelectionPainter();
              this.selectionTag = localHighlighter.addHighlight(i, j, localHighlightPainter);
            }
          }
          catch (BadLocationException localBadLocationException)
          {
            throw new StateInvariantError("Bad caret position");
          }
        }
      }
    }
  }
  
  public void setDot(int paramInt, Position.Bias paramBias)
  {
    if (paramBias == null) {
      throw new IllegalArgumentException("null bias");
    }
    NavigationFilter localNavigationFilter = this.component.getNavigationFilter();
    if (localNavigationFilter != null) {
      localNavigationFilter.setDot(getFilterBypass(), paramInt, paramBias);
    } else {
      handleSetDot(paramInt, paramBias);
    }
  }
  
  void handleSetDot(int paramInt, Position.Bias paramBias)
  {
    Document localDocument = this.component.getDocument();
    if (localDocument != null) {
      paramInt = Math.min(paramInt, localDocument.getLength());
    }
    paramInt = Math.max(paramInt, 0);
    if (paramInt == 0) {
      paramBias = Position.Bias.Forward;
    }
    this.mark = paramInt;
    if ((this.dot != paramInt) || (this.dotBias != paramBias) || (this.selectionTag != null) || (this.forceCaretPositionChange)) {
      changeCaretPosition(paramInt, paramBias);
    }
    this.markBias = this.dotBias;
    this.markLTR = this.dotLTR;
    Highlighter localHighlighter = this.component.getHighlighter();
    if ((localHighlighter != null) && (this.selectionTag != null))
    {
      localHighlighter.removeHighlight(this.selectionTag);
      this.selectionTag = null;
    }
  }
  
  public Position.Bias getDotBias()
  {
    return this.dotBias;
  }
  
  public Position.Bias getMarkBias()
  {
    return this.markBias;
  }
  
  boolean isDotLeftToRight()
  {
    return this.dotLTR;
  }
  
  boolean isMarkLeftToRight()
  {
    return this.markLTR;
  }
  
  boolean isPositionLTR(int paramInt, Position.Bias paramBias)
  {
    Document localDocument = this.component.getDocument();
    if (paramBias == Position.Bias.Backward)
    {
      paramInt--;
      if (paramInt < 0) {
        paramInt = 0;
      }
    }
    return AbstractDocument.isLeftToRight(localDocument, paramInt, paramInt);
  }
  
  Position.Bias guessBiasForOffset(int paramInt, Position.Bias paramBias, boolean paramBoolean)
  {
    if (paramBoolean != isPositionLTR(paramInt, paramBias)) {
      paramBias = Position.Bias.Backward;
    } else if ((paramBias != Position.Bias.Backward) && (paramBoolean != isPositionLTR(paramInt, Position.Bias.Backward))) {
      paramBias = Position.Bias.Backward;
    }
    if ((paramBias == Position.Bias.Backward) && (paramInt > 0)) {
      try
      {
        Segment localSegment = new Segment();
        this.component.getDocument().getText(paramInt - 1, 1, localSegment);
        if ((localSegment.count > 0) && (localSegment.array[localSegment.offset] == '\n')) {
          paramBias = Position.Bias.Forward;
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
    return paramBias;
  }
  
  void changeCaretPosition(int paramInt, Position.Bias paramBias)
  {
    repaint();
    if ((this.flasher != null) && (this.flasher.isRunning()))
    {
      this.visible = true;
      this.flasher.restart();
    }
    this.dot = paramInt;
    this.dotBias = paramBias;
    this.dotLTR = isPositionLTR(paramInt, paramBias);
    fireStateChanged();
    updateSystemSelection();
    setMagicCaretPosition(null);
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        DefaultCaret.this.repaintNewCaret();
      }
    };
    SwingUtilities.invokeLater(local1);
  }
  
  void repaintNewCaret()
  {
    if (this.component != null)
    {
      TextUI localTextUI = this.component.getUI();
      Document localDocument = this.component.getDocument();
      if ((localTextUI != null) && (localDocument != null))
      {
        Rectangle localRectangle;
        try
        {
          localRectangle = localTextUI.modelToView(this.component, this.dot, this.dotBias);
        }
        catch (BadLocationException localBadLocationException)
        {
          localRectangle = null;
        }
        if (localRectangle != null)
        {
          adjustVisibility(localRectangle);
          if (getMagicCaretPosition() == null) {
            setMagicCaretPosition(new Point(localRectangle.x, localRectangle.y));
          }
        }
        damage(localRectangle);
      }
    }
  }
  
  private void updateSystemSelection()
  {
    if (!SwingUtilities2.canCurrentEventAccessSystemClipboard()) {
      return;
    }
    if ((this.dot != this.mark) && (this.component != null) && (this.component.hasFocus()))
    {
      Clipboard localClipboard = getSystemSelection();
      if (localClipboard != null)
      {
        String str;
        if (((this.component instanceof JPasswordField)) && (this.component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE))
        {
          StringBuilder localStringBuilder = null;
          char c = ((JPasswordField)this.component).getEchoChar();
          int i = Math.min(getDot(), getMark());
          int j = Math.max(getDot(), getMark());
          for (int k = i; k < j; k++)
          {
            if (localStringBuilder == null) {
              localStringBuilder = new StringBuilder();
            }
            localStringBuilder.append(c);
          }
          str = localStringBuilder != null ? localStringBuilder.toString() : null;
        }
        else
        {
          str = this.component.getSelectedText();
        }
        try
        {
          localClipboard.setContents(new StringSelection(str), getClipboardOwner());
          this.ownsSelection = true;
        }
        catch (IllegalStateException localIllegalStateException) {}
      }
    }
  }
  
  private Clipboard getSystemSelection()
  {
    try
    {
      return this.component.getToolkit().getSystemSelection();
    }
    catch (HeadlessException localHeadlessException) {}catch (SecurityException localSecurityException) {}
    return null;
  }
  
  private ClipboardOwner getClipboardOwner()
  {
    return this.handler;
  }
  
  private void ensureValidPosition()
  {
    int i = this.component.getDocument().getLength();
    if ((this.dot > i) || (this.mark > i)) {
      handleSetDot(i, Position.Bias.Forward);
    }
  }
  
  public void setMagicCaretPosition(Point paramPoint)
  {
    this.magicCaretPosition = paramPoint;
  }
  
  public Point getMagicCaretPosition()
  {
    return this.magicCaretPosition;
  }
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public String toString()
  {
    String str = "Dot=(" + this.dot + ", " + this.dotBias + ")";
    str = str + " Mark=(" + this.mark + ", " + this.markBias + ")";
    return str;
  }
  
  private NavigationFilter.FilterBypass getFilterBypass()
  {
    if (this.filterBypass == null) {
      this.filterBypass = new DefaultFilterBypass(null);
    }
    return this.filterBypass;
  }
  
  private boolean _contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.width;
    int j = this.height;
    if ((i | j | paramInt3 | paramInt4) < 0) {
      return false;
    }
    int k = this.x;
    int m = this.y;
    if ((paramInt1 < k) || (paramInt2 < m)) {
      return false;
    }
    if (paramInt3 > 0)
    {
      i += k;
      paramInt3 += paramInt1;
      if (paramInt3 <= paramInt1)
      {
        if ((i >= k) || (paramInt3 > i)) {
          return false;
        }
      }
      else if ((i >= k) && (paramInt3 > i)) {
        return false;
      }
    }
    else if (k + i < paramInt1)
    {
      return false;
    }
    if (paramInt4 > 0)
    {
      j += m;
      paramInt4 += paramInt2;
      if (paramInt4 <= paramInt2)
      {
        if ((j >= m) || (paramInt4 > j)) {
          return false;
        }
      }
      else if ((j >= m) && (paramInt4 > j)) {
        return false;
      }
    }
    else if (m + j < paramInt2)
    {
      return false;
    }
    return true;
  }
  
  int getCaretWidth(int paramInt)
  {
    if (this.aspectRatio > -1.0F) {
      return (int)(this.aspectRatio * paramInt) + 1;
    }
    if (this.caretWidth > -1) {
      return this.caretWidth;
    }
    Object localObject = UIManager.get("Caret.width");
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    return 1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    this.handler = new Handler();
    if (!paramObjectInputStream.readBoolean()) {
      this.dotBias = Position.Bias.Forward;
    } else {
      this.dotBias = Position.Bias.Backward;
    }
    if (!paramObjectInputStream.readBoolean()) {
      this.markBias = Position.Bias.Forward;
    } else {
      this.markBias = Position.Bias.Backward;
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeBoolean(this.dotBias == Position.Bias.Backward);
    paramObjectOutputStream.writeBoolean(this.markBias == Position.Bias.Backward);
  }
  
  private class DefaultFilterBypass
    extends NavigationFilter.FilterBypass
  {
    private DefaultFilterBypass() {}
    
    public Caret getCaret()
    {
      return DefaultCaret.this;
    }
    
    public void setDot(int paramInt, Position.Bias paramBias)
    {
      DefaultCaret.this.handleSetDot(paramInt, paramBias);
    }
    
    public void moveDot(int paramInt, Position.Bias paramBias)
    {
      DefaultCaret.this.handleMoveDot(paramInt, paramBias);
    }
  }
  
  class Handler
    implements PropertyChangeListener, DocumentListener, ActionListener, ClipboardOwner
  {
    Handler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (((DefaultCaret.this.width == 0) || (DefaultCaret.this.height == 0)) && (DefaultCaret.this.component != null))
      {
        TextUI localTextUI = DefaultCaret.this.component.getUI();
        try
        {
          Rectangle localRectangle = localTextUI.modelToView(DefaultCaret.this.component, DefaultCaret.this.dot, DefaultCaret.this.dotBias);
          if ((localRectangle != null) && (localRectangle.width != 0) && (localRectangle.height != 0)) {
            DefaultCaret.this.damage(localRectangle);
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
      DefaultCaret.this.visible = (!DefaultCaret.this.visible);
      DefaultCaret.this.repaint();
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      if ((DefaultCaret.this.getUpdatePolicy() == 1) || ((DefaultCaret.this.getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
      {
        if (((paramDocumentEvent.getOffset() <= DefaultCaret.this.dot) || (paramDocumentEvent.getOffset() <= DefaultCaret.this.mark)) && (DefaultCaret.this.selectionTag != null)) {
          try
          {
            DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
          }
          catch (BadLocationException localBadLocationException1)
          {
            localBadLocationException1.printStackTrace();
          }
        }
        return;
      }
      int i = paramDocumentEvent.getOffset();
      int j = paramDocumentEvent.getLength();
      int k = DefaultCaret.this.dot;
      int m = 0;
      if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent))
      {
        DefaultCaret.this.setDot(i + j);
        return;
      }
      if (k >= i)
      {
        k += j;
        m = (short)(m | 0x1);
      }
      int n = DefaultCaret.this.mark;
      if (n >= i)
      {
        n += j;
        m = (short)(m | 0x2);
      }
      if (m != 0)
      {
        Position.Bias localBias = DefaultCaret.this.dotBias;
        if (DefaultCaret.this.dot == i)
        {
          Document localDocument = DefaultCaret.this.component.getDocument();
          int i1;
          try
          {
            Segment localSegment = new Segment();
            localDocument.getText(k - 1, 1, localSegment);
            i1 = (localSegment.count > 0) && (localSegment.array[localSegment.offset] == '\n') ? 1 : 0;
          }
          catch (BadLocationException localBadLocationException2)
          {
            i1 = 0;
          }
          if (i1 != 0) {
            localBias = Position.Bias.Forward;
          } else {
            localBias = Position.Bias.Backward;
          }
        }
        if (n == k)
        {
          DefaultCaret.this.setDot(k, localBias);
          DefaultCaret.this.ensureValidPosition();
        }
        else
        {
          DefaultCaret.this.setDot(n, DefaultCaret.this.markBias);
          if (DefaultCaret.this.getDot() == n) {
            DefaultCaret.this.moveDot(k, localBias);
          }
          DefaultCaret.this.ensureValidPosition();
        }
      }
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      if ((DefaultCaret.this.getUpdatePolicy() == 1) || ((DefaultCaret.this.getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
      {
        i = DefaultCaret.this.component.getDocument().getLength();
        DefaultCaret.this.dot = Math.min(DefaultCaret.this.dot, i);
        DefaultCaret.this.mark = Math.min(DefaultCaret.this.mark, i);
        if (((paramDocumentEvent.getOffset() < DefaultCaret.this.dot) || (paramDocumentEvent.getOffset() < DefaultCaret.this.mark)) && (DefaultCaret.this.selectionTag != null)) {
          try
          {
            DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
          }
          catch (BadLocationException localBadLocationException)
          {
            localBadLocationException.printStackTrace();
          }
        }
        return;
      }
      int i = paramDocumentEvent.getOffset();
      int j = i + paramDocumentEvent.getLength();
      int k = DefaultCaret.this.dot;
      int m = 0;
      int n = DefaultCaret.this.mark;
      int i1 = 0;
      if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent))
      {
        DefaultCaret.this.setDot(i);
        return;
      }
      if (k >= j)
      {
        k -= j - i;
        if (k == j) {
          m = 1;
        }
      }
      else if (k >= i)
      {
        k = i;
        m = 1;
      }
      if (n >= j)
      {
        n -= j - i;
        if (n == j) {
          i1 = 1;
        }
      }
      else if (n >= i)
      {
        n = i;
        i1 = 1;
      }
      if (n == k)
      {
        DefaultCaret.this.forceCaretPositionChange = true;
        try
        {
          DefaultCaret.this.setDot(k, DefaultCaret.this.guessBiasForOffset(k, DefaultCaret.this.dotBias, DefaultCaret.this.dotLTR));
        }
        finally
        {
          DefaultCaret.this.forceCaretPositionChange = false;
        }
        DefaultCaret.this.ensureValidPosition();
      }
      else
      {
        Position.Bias localBias1 = DefaultCaret.this.dotBias;
        Position.Bias localBias2 = DefaultCaret.this.markBias;
        if (m != 0) {
          localBias1 = DefaultCaret.this.guessBiasForOffset(k, localBias1, DefaultCaret.this.dotLTR);
        }
        if (i1 != 0) {
          localBias2 = DefaultCaret.this.guessBiasForOffset(DefaultCaret.this.mark, localBias2, DefaultCaret.this.markLTR);
        }
        DefaultCaret.this.setDot(n, localBias2);
        if (DefaultCaret.this.getDot() == n) {
          DefaultCaret.this.moveDot(k, localBias1);
        }
        DefaultCaret.this.ensureValidPosition();
      }
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      if ((DefaultCaret.this.getUpdatePolicy() == 1) || ((DefaultCaret.this.getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread()))) {
        return;
      }
      if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent)) {
        DefaultCaret.this.setDot(paramDocumentEvent.getOffset() + paramDocumentEvent.getLength());
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      Object localObject1 = paramPropertyChangeEvent.getOldValue();
      Object localObject2 = paramPropertyChangeEvent.getNewValue();
      if (((localObject1 instanceof Document)) || ((localObject2 instanceof Document)))
      {
        DefaultCaret.this.setDot(0);
        if (localObject1 != null) {
          ((Document)localObject1).removeDocumentListener(this);
        }
        if (localObject2 != null) {
          ((Document)localObject2).addDocumentListener(this);
        }
      }
      else
      {
        Object localObject3;
        if ("enabled".equals(paramPropertyChangeEvent.getPropertyName()))
        {
          localObject3 = (Boolean)paramPropertyChangeEvent.getNewValue();
          if (DefaultCaret.this.component.isFocusOwner()) {
            if (localObject3 == Boolean.TRUE)
            {
              if (DefaultCaret.this.component.isEditable()) {
                DefaultCaret.this.setVisible(true);
              }
              DefaultCaret.this.setSelectionVisible(true);
            }
            else
            {
              DefaultCaret.this.setVisible(false);
              DefaultCaret.this.setSelectionVisible(false);
            }
          }
        }
        else if ("caretWidth".equals(paramPropertyChangeEvent.getPropertyName()))
        {
          localObject3 = (Integer)paramPropertyChangeEvent.getNewValue();
          if (localObject3 != null) {
            DefaultCaret.this.caretWidth = ((Integer)localObject3).intValue();
          } else {
            DefaultCaret.this.caretWidth = -1;
          }
          DefaultCaret.this.repaint();
        }
        else if ("caretAspectRatio".equals(paramPropertyChangeEvent.getPropertyName()))
        {
          localObject3 = (Number)paramPropertyChangeEvent.getNewValue();
          if (localObject3 != null) {
            DefaultCaret.this.aspectRatio = ((Number)localObject3).floatValue();
          } else {
            DefaultCaret.this.aspectRatio = -1.0F;
          }
          DefaultCaret.this.repaint();
        }
      }
    }
    
    public void lostOwnership(Clipboard paramClipboard, Transferable paramTransferable)
    {
      if (DefaultCaret.this.ownsSelection)
      {
        DefaultCaret.this.ownsSelection = false;
        if ((DefaultCaret.this.component != null) && (!DefaultCaret.this.component.hasFocus())) {
          DefaultCaret.this.setSelectionVisible(false);
        }
      }
    }
  }
  
  class SafeScroller
    implements Runnable
  {
    Rectangle r;
    
    SafeScroller(Rectangle paramRectangle)
    {
      this.r = paramRectangle;
    }
    
    public void run()
    {
      if (DefaultCaret.this.component != null) {
        DefaultCaret.this.component.scrollRectToVisible(this.r);
      }
    }
  }
}
