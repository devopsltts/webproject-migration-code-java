package javax.swing.text;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class StyledEditorKit
  extends DefaultEditorKit
{
  private static final ViewFactory defaultFactory = new StyledViewFactory();
  Element currentRun;
  Element currentParagraph;
  MutableAttributeSet inputAttributes;
  private AttributeTracker inputAttributeUpdater;
  private static final Action[] defaultActions = { new FontFamilyAction("font-family-SansSerif", "SansSerif"), new FontFamilyAction("font-family-Monospaced", "Monospaced"), new FontFamilyAction("font-family-Serif", "Serif"), new FontSizeAction("font-size-8", 8), new FontSizeAction("font-size-10", 10), new FontSizeAction("font-size-12", 12), new FontSizeAction("font-size-14", 14), new FontSizeAction("font-size-16", 16), new FontSizeAction("font-size-18", 18), new FontSizeAction("font-size-24", 24), new FontSizeAction("font-size-36", 36), new FontSizeAction("font-size-48", 48), new AlignmentAction("left-justify", 0), new AlignmentAction("center-justify", 1), new AlignmentAction("right-justify", 2), new BoldAction(), new ItalicAction(), new StyledInsertBreakAction(), new UnderlineAction() };
  
  public StyledEditorKit()
  {
    createInputAttributeUpdated();
    createInputAttributes();
  }
  
  public MutableAttributeSet getInputAttributes()
  {
    return this.inputAttributes;
  }
  
  public Element getCharacterAttributeRun()
  {
    return this.currentRun;
  }
  
  public Action[] getActions()
  {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  
  public Document createDefaultDocument()
  {
    return new DefaultStyledDocument();
  }
  
  public void install(JEditorPane paramJEditorPane)
  {
    paramJEditorPane.addCaretListener(this.inputAttributeUpdater);
    paramJEditorPane.addPropertyChangeListener(this.inputAttributeUpdater);
    Caret localCaret = paramJEditorPane.getCaret();
    if (localCaret != null) {
      this.inputAttributeUpdater.updateInputAttributes(localCaret.getDot(), localCaret.getMark(), paramJEditorPane);
    }
  }
  
  public void deinstall(JEditorPane paramJEditorPane)
  {
    paramJEditorPane.removeCaretListener(this.inputAttributeUpdater);
    paramJEditorPane.removePropertyChangeListener(this.inputAttributeUpdater);
    this.currentRun = null;
    this.currentParagraph = null;
  }
  
  public ViewFactory getViewFactory()
  {
    return defaultFactory;
  }
  
  public Object clone()
  {
    StyledEditorKit localStyledEditorKit = (StyledEditorKit)super.clone();
    localStyledEditorKit.currentRun = (localStyledEditorKit.currentParagraph = null);
    localStyledEditorKit.createInputAttributeUpdated();
    localStyledEditorKit.createInputAttributes();
    return localStyledEditorKit;
  }
  
  private void createInputAttributes()
  {
    this.inputAttributes = new SimpleAttributeSet()
    {
      public AttributeSet getResolveParent()
      {
        return StyledEditorKit.this.currentParagraph != null ? StyledEditorKit.this.currentParagraph.getAttributes() : null;
      }
      
      public Object clone()
      {
        return new SimpleAttributeSet(this);
      }
    };
  }
  
  private void createInputAttributeUpdated()
  {
    this.inputAttributeUpdater = new AttributeTracker();
  }
  
  protected void createInputAttributes(Element paramElement, MutableAttributeSet paramMutableAttributeSet)
  {
    if ((paramElement.getAttributes().getAttributeCount() > 0) || (paramElement.getEndOffset() - paramElement.getStartOffset() > 1) || (paramElement.getEndOffset() < paramElement.getDocument().getLength()))
    {
      paramMutableAttributeSet.removeAttributes(paramMutableAttributeSet);
      paramMutableAttributeSet.addAttributes(paramElement.getAttributes());
      paramMutableAttributeSet.removeAttribute(StyleConstants.ComponentAttribute);
      paramMutableAttributeSet.removeAttribute(StyleConstants.IconAttribute);
      paramMutableAttributeSet.removeAttribute("$ename");
      paramMutableAttributeSet.removeAttribute(StyleConstants.ComposedTextAttribute);
    }
  }
  
  public static class AlignmentAction
    extends StyledEditorKit.StyledTextAction
  {
    private int a;
    
    public AlignmentAction(String paramString, int paramInt)
    {
      super();
      this.a = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        int i = this.a;
        if ((paramActionEvent != null) && (paramActionEvent.getSource() == localJEditorPane))
        {
          localObject = paramActionEvent.getActionCommand();
          try
          {
            i = Integer.parseInt((String)localObject, 10);
          }
          catch (NumberFormatException localNumberFormatException) {}
        }
        Object localObject = new SimpleAttributeSet();
        StyleConstants.setAlignment((MutableAttributeSet)localObject, i);
        setParagraphAttributes(localJEditorPane, (AttributeSet)localObject, false);
      }
    }
  }
  
  class AttributeTracker
    implements CaretListener, PropertyChangeListener, Serializable
  {
    AttributeTracker() {}
    
    void updateInputAttributes(int paramInt1, int paramInt2, JTextComponent paramJTextComponent)
    {
      Document localDocument = paramJTextComponent.getDocument();
      if (!(localDocument instanceof StyledDocument)) {
        return;
      }
      int i = Math.min(paramInt1, paramInt2);
      StyledDocument localStyledDocument = (StyledDocument)localDocument;
      StyledEditorKit.this.currentParagraph = localStyledDocument.getParagraphElement(i);
      Element localElement;
      if ((StyledEditorKit.this.currentParagraph.getStartOffset() == i) || (paramInt1 != paramInt2)) {
        localElement = localStyledDocument.getCharacterElement(i);
      } else {
        localElement = localStyledDocument.getCharacterElement(Math.max(i - 1, 0));
      }
      if (localElement != StyledEditorKit.this.currentRun)
      {
        StyledEditorKit.this.currentRun = localElement;
        StyledEditorKit.this.createInputAttributes(StyledEditorKit.this.currentRun, StyledEditorKit.this.getInputAttributes());
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      Object localObject1 = paramPropertyChangeEvent.getNewValue();
      Object localObject2 = paramPropertyChangeEvent.getSource();
      if (((localObject2 instanceof JTextComponent)) && ((localObject1 instanceof Document))) {
        updateInputAttributes(0, 0, (JTextComponent)localObject2);
      }
    }
    
    public void caretUpdate(CaretEvent paramCaretEvent)
    {
      updateInputAttributes(paramCaretEvent.getDot(), paramCaretEvent.getMark(), (JTextComponent)paramCaretEvent.getSource());
    }
  }
  
  public static class BoldAction
    extends StyledEditorKit.StyledTextAction
  {
    public BoldAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        StyledEditorKit localStyledEditorKit = getStyledEditorKit(localJEditorPane);
        MutableAttributeSet localMutableAttributeSet = localStyledEditorKit.getInputAttributes();
        boolean bool = !StyleConstants.isBold(localMutableAttributeSet);
        SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(localSimpleAttributeSet, bool);
        setCharacterAttributes(localJEditorPane, localSimpleAttributeSet, false);
      }
    }
  }
  
  public static class FontFamilyAction
    extends StyledEditorKit.StyledTextAction
  {
    private String family;
    
    public FontFamilyAction(String paramString1, String paramString2)
    {
      super();
      this.family = paramString2;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        Object localObject1 = this.family;
        Object localObject2;
        if ((paramActionEvent != null) && (paramActionEvent.getSource() == localJEditorPane))
        {
          localObject2 = paramActionEvent.getActionCommand();
          if (localObject2 != null) {
            localObject1 = localObject2;
          }
        }
        if (localObject1 != null)
        {
          localObject2 = new SimpleAttributeSet();
          StyleConstants.setFontFamily((MutableAttributeSet)localObject2, (String)localObject1);
          setCharacterAttributes(localJEditorPane, (AttributeSet)localObject2, false);
        }
        else
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
        }
      }
    }
  }
  
  public static class FontSizeAction
    extends StyledEditorKit.StyledTextAction
  {
    private int size;
    
    public FontSizeAction(String paramString, int paramInt)
    {
      super();
      this.size = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        int i = this.size;
        Object localObject;
        if ((paramActionEvent != null) && (paramActionEvent.getSource() == localJEditorPane))
        {
          localObject = paramActionEvent.getActionCommand();
          try
          {
            i = Integer.parseInt((String)localObject, 10);
          }
          catch (NumberFormatException localNumberFormatException) {}
        }
        if (i != 0)
        {
          localObject = new SimpleAttributeSet();
          StyleConstants.setFontSize((MutableAttributeSet)localObject, i);
          setCharacterAttributes(localJEditorPane, (AttributeSet)localObject, false);
        }
        else
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
        }
      }
    }
  }
  
  public static class ForegroundAction
    extends StyledEditorKit.StyledTextAction
  {
    private Color fg;
    
    public ForegroundAction(String paramString, Color paramColor)
    {
      super();
      this.fg = paramColor;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        Color localColor = this.fg;
        Object localObject;
        if ((paramActionEvent != null) && (paramActionEvent.getSource() == localJEditorPane))
        {
          localObject = paramActionEvent.getActionCommand();
          try
          {
            localColor = Color.decode((String)localObject);
          }
          catch (NumberFormatException localNumberFormatException) {}
        }
        if (localColor != null)
        {
          localObject = new SimpleAttributeSet();
          StyleConstants.setForeground((MutableAttributeSet)localObject, localColor);
          setCharacterAttributes(localJEditorPane, (AttributeSet)localObject, false);
        }
        else
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
        }
      }
    }
  }
  
  public static class ItalicAction
    extends StyledEditorKit.StyledTextAction
  {
    public ItalicAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        StyledEditorKit localStyledEditorKit = getStyledEditorKit(localJEditorPane);
        MutableAttributeSet localMutableAttributeSet = localStyledEditorKit.getInputAttributes();
        boolean bool = !StyleConstants.isItalic(localMutableAttributeSet);
        SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setItalic(localSimpleAttributeSet, bool);
        setCharacterAttributes(localJEditorPane, localSimpleAttributeSet, false);
      }
    }
  }
  
  static class StyledInsertBreakAction
    extends StyledEditorKit.StyledTextAction
  {
    private SimpleAttributeSet tempSet;
    
    StyledInsertBreakAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      Object localObject;
      if (localJEditorPane != null)
      {
        if ((!localJEditorPane.isEditable()) || (!localJEditorPane.isEnabled()))
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
          return;
        }
        localObject = getStyledEditorKit(localJEditorPane);
        if (this.tempSet != null) {
          this.tempSet.removeAttributes(this.tempSet);
        } else {
          this.tempSet = new SimpleAttributeSet();
        }
        this.tempSet.addAttributes(((StyledEditorKit)localObject).getInputAttributes());
        localJEditorPane.replaceSelection("\n");
        MutableAttributeSet localMutableAttributeSet = ((StyledEditorKit)localObject).getInputAttributes();
        localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
        localMutableAttributeSet.addAttributes(this.tempSet);
        this.tempSet.removeAttributes(this.tempSet);
      }
      else
      {
        localObject = getTextComponent(paramActionEvent);
        if (localObject != null)
        {
          if ((!((JTextComponent)localObject).isEditable()) || (!((JTextComponent)localObject).isEnabled()))
          {
            UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
            return;
          }
          ((JTextComponent)localObject).replaceSelection("\n");
        }
      }
    }
  }
  
  public static abstract class StyledTextAction
    extends TextAction
  {
    public StyledTextAction(String paramString)
    {
      super();
    }
    
    protected final JEditorPane getEditor(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if ((localJTextComponent instanceof JEditorPane)) {
        return (JEditorPane)localJTextComponent;
      }
      return null;
    }
    
    protected final StyledDocument getStyledDocument(JEditorPane paramJEditorPane)
    {
      Document localDocument = paramJEditorPane.getDocument();
      if ((localDocument instanceof StyledDocument)) {
        return (StyledDocument)localDocument;
      }
      throw new IllegalArgumentException("document must be StyledDocument");
    }
    
    protected final StyledEditorKit getStyledEditorKit(JEditorPane paramJEditorPane)
    {
      EditorKit localEditorKit = paramJEditorPane.getEditorKit();
      if ((localEditorKit instanceof StyledEditorKit)) {
        return (StyledEditorKit)localEditorKit;
      }
      throw new IllegalArgumentException("EditorKit must be StyledEditorKit");
    }
    
    protected final void setCharacterAttributes(JEditorPane paramJEditorPane, AttributeSet paramAttributeSet, boolean paramBoolean)
    {
      int i = paramJEditorPane.getSelectionStart();
      int j = paramJEditorPane.getSelectionEnd();
      if (i != j)
      {
        localObject = getStyledDocument(paramJEditorPane);
        ((StyledDocument)localObject).setCharacterAttributes(i, j - i, paramAttributeSet, paramBoolean);
      }
      Object localObject = getStyledEditorKit(paramJEditorPane);
      MutableAttributeSet localMutableAttributeSet = ((StyledEditorKit)localObject).getInputAttributes();
      if (paramBoolean) {
        localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
      }
      localMutableAttributeSet.addAttributes(paramAttributeSet);
    }
    
    protected final void setParagraphAttributes(JEditorPane paramJEditorPane, AttributeSet paramAttributeSet, boolean paramBoolean)
    {
      int i = paramJEditorPane.getSelectionStart();
      int j = paramJEditorPane.getSelectionEnd();
      StyledDocument localStyledDocument = getStyledDocument(paramJEditorPane);
      localStyledDocument.setParagraphAttributes(i, j - i, paramAttributeSet, paramBoolean);
    }
  }
  
  static class StyledViewFactory
    implements ViewFactory
  {
    StyledViewFactory() {}
    
    public View create(Element paramElement)
    {
      String str = paramElement.getName();
      if (str != null)
      {
        if (str.equals("content")) {
          return new LabelView(paramElement);
        }
        if (str.equals("paragraph")) {
          return new ParagraphView(paramElement);
        }
        if (str.equals("section")) {
          return new BoxView(paramElement, 1);
        }
        if (str.equals("component")) {
          return new ComponentView(paramElement);
        }
        if (str.equals("icon")) {
          return new IconView(paramElement);
        }
      }
      return new LabelView(paramElement);
    }
  }
  
  public static class UnderlineAction
    extends StyledEditorKit.StyledTextAction
  {
    public UnderlineAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        StyledEditorKit localStyledEditorKit = getStyledEditorKit(localJEditorPane);
        MutableAttributeSet localMutableAttributeSet = localStyledEditorKit.getInputAttributes();
        boolean bool = !StyleConstants.isUnderline(localMutableAttributeSet);
        SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setUnderline(localSimpleAttributeSet, bool);
        setCharacterAttributes(localJEditorPane, localSimpleAttributeSet, false);
      }
    }
  }
}
