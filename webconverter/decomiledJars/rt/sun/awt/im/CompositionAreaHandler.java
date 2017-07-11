package sun.awt.im;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.lang.ref.WeakReference;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;

class CompositionAreaHandler
  implements InputMethodListener, InputMethodRequests
{
  private static CompositionArea compositionArea;
  private static Object compositionAreaLock = new Object();
  private static CompositionAreaHandler compositionAreaOwner;
  private AttributedCharacterIterator composedText;
  private TextHitInfo caret = null;
  private WeakReference<Component> clientComponent = new WeakReference(null);
  private InputMethodContext inputMethodContext;
  private static final AttributedCharacterIterator.Attribute[] IM_ATTRIBUTES = { TextAttribute.INPUT_METHOD_HIGHLIGHT };
  private static final AttributedCharacterIterator EMPTY_TEXT = new AttributedString("").getIterator();
  
  CompositionAreaHandler(InputMethodContext paramInputMethodContext)
  {
    this.inputMethodContext = paramInputMethodContext;
  }
  
  private void createCompositionArea()
  {
    synchronized (compositionAreaLock)
    {
      compositionArea = new CompositionArea();
      if (compositionAreaOwner != null) {
        compositionArea.setHandlerInfo(compositionAreaOwner, this.inputMethodContext);
      }
      Component localComponent = (Component)this.clientComponent.get();
      if (localComponent != null)
      {
        InputMethodRequests localInputMethodRequests = localComponent.getInputMethodRequests();
        if ((localInputMethodRequests != null) && (this.inputMethodContext.useBelowTheSpotInput())) {
          setCompositionAreaUndecorated(true);
        }
      }
    }
  }
  
  void setClientComponent(Component paramComponent)
  {
    this.clientComponent = new WeakReference(paramComponent);
  }
  
  void grabCompositionArea(boolean paramBoolean)
  {
    synchronized (compositionAreaLock)
    {
      if (compositionAreaOwner != this)
      {
        compositionAreaOwner = this;
        if (compositionArea != null) {
          compositionArea.setHandlerInfo(this, this.inputMethodContext);
        }
        if (paramBoolean)
        {
          if ((this.composedText != null) && (compositionArea == null)) {
            createCompositionArea();
          }
          if (compositionArea != null) {
            compositionArea.setText(this.composedText, this.caret);
          }
        }
      }
    }
  }
  
  void releaseCompositionArea()
  {
    synchronized (compositionAreaLock)
    {
      if (compositionAreaOwner == this)
      {
        compositionAreaOwner = null;
        if (compositionArea != null)
        {
          compositionArea.setHandlerInfo(null, null);
          compositionArea.setText(null, null);
        }
      }
    }
  }
  
  static void closeCompositionArea()
  {
    if (compositionArea != null) {
      synchronized (compositionAreaLock)
      {
        compositionAreaOwner = null;
        compositionArea.setHandlerInfo(null, null);
        compositionArea.setText(null, null);
      }
    }
  }
  
  boolean isCompositionAreaVisible()
  {
    if (compositionArea != null) {
      return compositionArea.isCompositionAreaVisible();
    }
    return false;
  }
  
  void setCompositionAreaVisible(boolean paramBoolean)
  {
    if (compositionArea != null) {
      compositionArea.setCompositionAreaVisible(paramBoolean);
    }
  }
  
  void processInputMethodEvent(InputMethodEvent paramInputMethodEvent)
  {
    if (paramInputMethodEvent.getID() == 1100) {
      inputMethodTextChanged(paramInputMethodEvent);
    } else {
      caretPositionChanged(paramInputMethodEvent);
    }
  }
  
  void setCompositionAreaUndecorated(boolean paramBoolean)
  {
    if (compositionArea != null) {
      compositionArea.setCompositionAreaUndecorated(paramBoolean);
    }
  }
  
  public void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent)
  {
    AttributedCharacterIterator localAttributedCharacterIterator = paramInputMethodEvent.getText();
    int i = paramInputMethodEvent.getCommittedCharacterCount();
    this.composedText = null;
    this.caret = null;
    if ((localAttributedCharacterIterator != null) && (i < localAttributedCharacterIterator.getEndIndex() - localAttributedCharacterIterator.getBeginIndex()))
    {
      if (compositionArea == null) {
        createCompositionArea();
      }
      AttributedString localAttributedString = new AttributedString(localAttributedCharacterIterator, localAttributedCharacterIterator.getBeginIndex() + i, localAttributedCharacterIterator.getEndIndex(), IM_ATTRIBUTES);
      localAttributedString.addAttribute(TextAttribute.FONT, compositionArea.getFont());
      this.composedText = localAttributedString.getIterator();
      this.caret = paramInputMethodEvent.getCaret();
    }
    if (compositionArea != null) {
      compositionArea.setText(this.composedText, this.caret);
    }
    if (i > 0)
    {
      this.inputMethodContext.dispatchCommittedText((Component)paramInputMethodEvent.getSource(), localAttributedCharacterIterator, i);
      if (isCompositionAreaVisible()) {
        compositionArea.updateWindowLocation();
      }
    }
    paramInputMethodEvent.consume();
  }
  
  public void caretPositionChanged(InputMethodEvent paramInputMethodEvent)
  {
    if (compositionArea != null) {
      compositionArea.setCaret(paramInputMethodEvent.getCaret());
    }
    paramInputMethodEvent.consume();
  }
  
  InputMethodRequests getClientInputMethodRequests()
  {
    Component localComponent = (Component)this.clientComponent.get();
    if (localComponent != null) {
      return localComponent.getInputMethodRequests();
    }
    return null;
  }
  
  public Rectangle getTextLocation(TextHitInfo paramTextHitInfo)
  {
    synchronized (compositionAreaLock)
    {
      if ((compositionAreaOwner == this) && (isCompositionAreaVisible())) {
        return compositionArea.getTextLocation(paramTextHitInfo);
      }
      if (this.composedText != null) {
        return new Rectangle(0, 0, 0, 10);
      }
      InputMethodRequests localInputMethodRequests = getClientInputMethodRequests();
      if (localInputMethodRequests != null) {
        return localInputMethodRequests.getTextLocation(paramTextHitInfo);
      }
      return new Rectangle(0, 0, 0, 10);
    }
  }
  
  public TextHitInfo getLocationOffset(int paramInt1, int paramInt2)
  {
    synchronized (compositionAreaLock)
    {
      if ((compositionAreaOwner == this) && (isCompositionAreaVisible())) {
        return compositionArea.getLocationOffset(paramInt1, paramInt2);
      }
      return null;
    }
  }
  
  public int getInsertPositionOffset()
  {
    InputMethodRequests localInputMethodRequests = getClientInputMethodRequests();
    if (localInputMethodRequests != null) {
      return localInputMethodRequests.getInsertPositionOffset();
    }
    return 0;
  }
  
  public AttributedCharacterIterator getCommittedText(int paramInt1, int paramInt2, AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
  {
    InputMethodRequests localInputMethodRequests = getClientInputMethodRequests();
    if (localInputMethodRequests != null) {
      return localInputMethodRequests.getCommittedText(paramInt1, paramInt2, paramArrayOfAttribute);
    }
    return EMPTY_TEXT;
  }
  
  public int getCommittedTextLength()
  {
    InputMethodRequests localInputMethodRequests = getClientInputMethodRequests();
    if (localInputMethodRequests != null) {
      return localInputMethodRequests.getCommittedTextLength();
    }
    return 0;
  }
  
  public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
  {
    InputMethodRequests localInputMethodRequests = getClientInputMethodRequests();
    if (localInputMethodRequests != null) {
      return localInputMethodRequests.cancelLatestCommittedText(paramArrayOfAttribute);
    }
    return null;
  }
  
  public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
  {
    InputMethodRequests localInputMethodRequests = getClientInputMethodRequests();
    if (localInputMethodRequests != null) {
      return localInputMethodRequests.getSelectedText(paramArrayOfAttribute);
    }
    return EMPTY_TEXT;
  }
}
