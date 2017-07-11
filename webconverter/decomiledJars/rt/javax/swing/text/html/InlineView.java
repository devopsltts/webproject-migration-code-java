package javax.swing.text.html;

import java.awt.Color;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class InlineView
  extends LabelView
{
  private boolean nowrap;
  private AttributeSet attr;
  
  public InlineView(Element paramElement)
  {
    super(paramElement);
    StyleSheet localStyleSheet = getStyleSheet();
    this.attr = localStyleSheet.getViewAttributes(this);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    StyleSheet localStyleSheet = getStyleSheet();
    this.attr = localStyleSheet.getViewAttributes(this);
    preferenceChanged(null, true, true);
  }
  
  public AttributeSet getAttributes()
  {
    return this.attr;
  }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (this.nowrap) {
      return 0;
    }
    return super.getBreakWeight(paramInt, paramFloat1, paramFloat2);
  }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    return super.breakView(paramInt1, paramInt2, paramFloat1, paramFloat2);
  }
  
  protected void setPropertiesFromAttributes()
  {
    super.setPropertiesFromAttributes();
    AttributeSet localAttributeSet = getAttributes();
    Object localObject1 = localAttributeSet.getAttribute(CSS.Attribute.TEXT_DECORATION);
    boolean bool1 = localObject1.toString().indexOf("underline") >= 0;
    setUnderline(bool1);
    boolean bool2 = localObject1.toString().indexOf("line-through") >= 0;
    setStrikeThrough(bool2);
    Object localObject2 = localAttributeSet.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
    bool2 = localObject2.toString().indexOf("sup") >= 0;
    setSuperscript(bool2);
    bool2 = localObject2.toString().indexOf("sub") >= 0;
    setSubscript(bool2);
    Object localObject3 = localAttributeSet.getAttribute(CSS.Attribute.WHITE_SPACE);
    if ((localObject3 != null) && (localObject3.equals("nowrap"))) {
      this.nowrap = true;
    } else {
      this.nowrap = false;
    }
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    Color localColor = localHTMLDocument.getBackground(localAttributeSet);
    if (localColor != null) {
      setBackground(localColor);
    }
  }
  
  protected StyleSheet getStyleSheet()
  {
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    return localHTMLDocument.getStyleSheet();
  }
}
