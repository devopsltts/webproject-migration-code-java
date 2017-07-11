package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

class CommentView
  extends HiddenTagView
{
  static final Border CBorder = new CommentBorder();
  static final int commentPadding = 3;
  static final int commentPaddingD = 9;
  
  CommentView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected Component createComponent()
  {
    Container localContainer = getContainer();
    if ((localContainer != null) && (!((JTextComponent)localContainer).isEditable())) {
      return null;
    }
    JTextArea localJTextArea = new JTextArea(getRepresentedText());
    Document localDocument = getDocument();
    Font localFont;
    if ((localDocument instanceof StyledDocument))
    {
      localFont = ((StyledDocument)localDocument).getFont(getAttributes());
      localJTextArea.setFont(localFont);
    }
    else
    {
      localFont = localJTextArea.getFont();
    }
    updateYAlign(localFont);
    localJTextArea.setBorder(CBorder);
    localJTextArea.getDocument().addDocumentListener(this);
    localJTextArea.setFocusable(isVisible());
    return localJTextArea;
  }
  
  void resetBorder() {}
  
  void _updateModelFromText()
  {
    JTextComponent localJTextComponent = getTextComponent();
    Document localDocument = getDocument();
    if ((localJTextComponent != null) && (localDocument != null))
    {
      String str = localJTextComponent.getText();
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      this.isSettingAttributes = true;
      try
      {
        localSimpleAttributeSet.addAttribute(HTML.Attribute.COMMENT, str);
        ((StyledDocument)localDocument).setCharacterAttributes(getStartOffset(), getEndOffset() - getStartOffset(), localSimpleAttributeSet, false);
      }
      finally
      {
        this.isSettingAttributes = false;
      }
    }
  }
  
  JTextComponent getTextComponent()
  {
    return (JTextComponent)getComponent();
  }
  
  String getRepresentedText()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    if (localAttributeSet != null)
    {
      Object localObject = localAttributeSet.getAttribute(HTML.Attribute.COMMENT);
      if ((localObject instanceof String)) {
        return (String)localObject;
      }
    }
    return "";
  }
  
  static class CommentBorder
    extends LineBorder
  {
    CommentBorder()
    {
      super(1);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.paintBorder(paramComponent, paramGraphics, paramInt1 + 3, paramInt2, paramInt3 - 9, paramInt4);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets = super.getBorderInsets(paramComponent, paramInsets);
      localInsets.left += 3;
      localInsets.right += 3;
      return localInsets;
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
  }
}
