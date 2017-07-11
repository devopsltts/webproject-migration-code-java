package javax.swing.text.html;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class TextAreaDocument
  extends PlainDocument
{
  String initialText;
  
  TextAreaDocument() {}
  
  void reset()
  {
    try
    {
      remove(0, getLength());
      if (this.initialText != null) {
        insertString(0, this.initialText, null);
      }
    }
    catch (BadLocationException localBadLocationException) {}
  }
  
  void storeInitialText()
  {
    try
    {
      this.initialText = getText(0, getLength());
    }
    catch (BadLocationException localBadLocationException) {}
  }
}
