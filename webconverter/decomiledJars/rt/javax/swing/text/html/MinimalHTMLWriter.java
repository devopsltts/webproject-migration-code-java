package javax.swing.text.html;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleConstants.CharacterConstants;
import javax.swing.text.StyleConstants.ColorConstants;
import javax.swing.text.StyleConstants.FontConstants;
import javax.swing.text.StyleConstants.ParagraphConstants;
import javax.swing.text.StyleContext.NamedStyle;
import javax.swing.text.StyledDocument;

public class MinimalHTMLWriter
  extends AbstractWriter
{
  private static final int BOLD = 1;
  private static final int ITALIC = 2;
  private static final int UNDERLINE = 4;
  private static final CSS css = new CSS();
  private int fontMask = 0;
  int startOffset = 0;
  int endOffset = 0;
  private AttributeSet fontAttributes;
  private Hashtable<String, String> styleNameMapping;
  
  public MinimalHTMLWriter(Writer paramWriter, StyledDocument paramStyledDocument)
  {
    super(paramWriter, paramStyledDocument);
  }
  
  public MinimalHTMLWriter(Writer paramWriter, StyledDocument paramStyledDocument, int paramInt1, int paramInt2)
  {
    super(paramWriter, paramStyledDocument, paramInt1, paramInt2);
  }
  
  public void write()
    throws IOException, BadLocationException
  {
    this.styleNameMapping = new Hashtable();
    writeStartTag("<html>");
    writeHeader();
    writeBody();
    writeEndTag("</html>");
  }
  
  protected void writeAttributes(AttributeSet paramAttributeSet)
    throws IOException
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      if (((localObject instanceof StyleConstants.ParagraphConstants)) || ((localObject instanceof StyleConstants.CharacterConstants)) || ((localObject instanceof StyleConstants.FontConstants)) || ((localObject instanceof StyleConstants.ColorConstants)))
      {
        indent();
        write(localObject.toString());
        write(':');
        write(css.styleConstantsValueToCSSValue((StyleConstants)localObject, paramAttributeSet.getAttribute(localObject)).toString());
        write(';');
        write('\n');
      }
    }
  }
  
  protected void text(Element paramElement)
    throws IOException, BadLocationException
  {
    String str = getText(paramElement);
    if ((str.length() > 0) && (str.charAt(str.length() - 1) == '\n')) {
      str = str.substring(0, str.length() - 1);
    }
    if (str.length() > 0) {
      write(str);
    }
  }
  
  protected void writeStartTag(String paramString)
    throws IOException
  {
    indent();
    write(paramString);
    write('\n');
    incrIndent();
  }
  
  protected void writeEndTag(String paramString)
    throws IOException
  {
    decrIndent();
    indent();
    write(paramString);
    write('\n');
  }
  
  protected void writeHeader()
    throws IOException
  {
    writeStartTag("<head>");
    writeStartTag("<style>");
    writeStartTag("<!--");
    writeStyles();
    writeEndTag("-->");
    writeEndTag("</style>");
    writeEndTag("</head>");
  }
  
  protected void writeStyles()
    throws IOException
  {
    DefaultStyledDocument localDefaultStyledDocument = (DefaultStyledDocument)getDocument();
    Enumeration localEnumeration = localDefaultStyledDocument.getStyleNames();
    while (localEnumeration.hasMoreElements())
    {
      Style localStyle = localDefaultStyledDocument.getStyle((String)localEnumeration.nextElement());
      if ((localStyle.getAttributeCount() != 1) || (!localStyle.isDefined(StyleConstants.NameAttribute)))
      {
        indent();
        write("p." + addStyleName(localStyle.getName()));
        write(" {\n");
        incrIndent();
        writeAttributes(localStyle);
        decrIndent();
        indent();
        write("}\n");
      }
    }
  }
  
  protected void writeBody()
    throws IOException, BadLocationException
  {
    ElementIterator localElementIterator = getElementIterator();
    localElementIterator.current();
    writeStartTag("<body>");
    int i = 0;
    Element localElement;
    while ((localElement = localElementIterator.next()) != null) {
      if (inRange(localElement)) {
        if ((localElement instanceof AbstractDocument.BranchElement))
        {
          if (i != 0)
          {
            writeEndParagraph();
            i = 0;
            this.fontMask = 0;
          }
          writeStartParagraph(localElement);
        }
        else if (isText(localElement))
        {
          writeContent(localElement, i == 0);
          i = 1;
        }
        else
        {
          writeLeaf(localElement);
          i = 1;
        }
      }
    }
    if (i != 0) {
      writeEndParagraph();
    }
    writeEndTag("</body>");
  }
  
  protected void writeEndParagraph()
    throws IOException
  {
    writeEndMask(this.fontMask);
    if (inFontTag()) {
      endSpanTag();
    } else {
      write('\n');
    }
    writeEndTag("</p>");
  }
  
  protected void writeStartParagraph(Element paramElement)
    throws IOException
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    Object localObject = localAttributeSet.getAttribute(StyleConstants.ResolveAttribute);
    if ((localObject instanceof StyleContext.NamedStyle)) {
      writeStartTag("<p class=" + mapStyleName(((StyleContext.NamedStyle)localObject).getName()) + ">");
    } else {
      writeStartTag("<p>");
    }
  }
  
  protected void writeLeaf(Element paramElement)
    throws IOException
  {
    indent();
    if (paramElement.getName() == "icon") {
      writeImage(paramElement);
    } else if (paramElement.getName() == "component") {
      writeComponent(paramElement);
    }
  }
  
  protected void writeImage(Element paramElement)
    throws IOException
  {}
  
  protected void writeComponent(Element paramElement)
    throws IOException
  {}
  
  protected boolean isText(Element paramElement)
  {
    return paramElement.getName() == "content";
  }
  
  protected void writeContent(Element paramElement, boolean paramBoolean)
    throws IOException, BadLocationException
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    writeNonHTMLAttributes(localAttributeSet);
    if (paramBoolean) {
      indent();
    }
    writeHTMLTags(localAttributeSet);
    text(paramElement);
  }
  
  protected void writeHTMLTags(AttributeSet paramAttributeSet)
    throws IOException
  {
    int i = this.fontMask;
    setFontMask(paramAttributeSet);
    int j = 0;
    int k = 0;
    if ((i & 0x1) != 0)
    {
      if ((this.fontMask & 0x1) == 0) {
        j |= 0x1;
      }
    }
    else if ((this.fontMask & 0x1) != 0) {
      k |= 0x1;
    }
    if ((i & 0x2) != 0)
    {
      if ((this.fontMask & 0x2) == 0) {
        j |= 0x2;
      }
    }
    else if ((this.fontMask & 0x2) != 0) {
      k |= 0x2;
    }
    if ((i & 0x4) != 0)
    {
      if ((this.fontMask & 0x4) == 0) {
        j |= 0x4;
      }
    }
    else if ((this.fontMask & 0x4) != 0) {
      k |= 0x4;
    }
    writeEndMask(j);
    writeStartMask(k);
  }
  
  private void setFontMask(AttributeSet paramAttributeSet)
  {
    if (StyleConstants.isBold(paramAttributeSet)) {
      this.fontMask |= 0x1;
    }
    if (StyleConstants.isItalic(paramAttributeSet)) {
      this.fontMask |= 0x2;
    }
    if (StyleConstants.isUnderline(paramAttributeSet)) {
      this.fontMask |= 0x4;
    }
  }
  
  private void writeStartMask(int paramInt)
    throws IOException
  {
    if (paramInt != 0)
    {
      if ((paramInt & 0x4) != 0) {
        write("<u>");
      }
      if ((paramInt & 0x2) != 0) {
        write("<i>");
      }
      if ((paramInt & 0x1) != 0) {
        write("<b>");
      }
    }
  }
  
  private void writeEndMask(int paramInt)
    throws IOException
  {
    if (paramInt != 0)
    {
      if ((paramInt & 0x1) != 0) {
        write("</b>");
      }
      if ((paramInt & 0x2) != 0) {
        write("</i>");
      }
      if ((paramInt & 0x4) != 0) {
        write("</u>");
      }
    }
  }
  
  protected void writeNonHTMLAttributes(AttributeSet paramAttributeSet)
    throws IOException
  {
    String str1 = "";
    String str2 = "; ";
    if ((inFontTag()) && (this.fontAttributes.isEqual(paramAttributeSet))) {
      return;
    }
    int i = 1;
    Color localColor = (Color)paramAttributeSet.getAttribute(StyleConstants.Foreground);
    if (localColor != null)
    {
      str1 = str1 + "color: " + css.styleConstantsValueToCSSValue((StyleConstants)StyleConstants.Foreground, localColor);
      i = 0;
    }
    Integer localInteger = (Integer)paramAttributeSet.getAttribute(StyleConstants.FontSize);
    if (localInteger != null)
    {
      if (i == 0) {
        str1 = str1 + str2;
      }
      str1 = str1 + "font-size: " + localInteger.intValue() + "pt";
      i = 0;
    }
    String str3 = (String)paramAttributeSet.getAttribute(StyleConstants.FontFamily);
    if (str3 != null)
    {
      if (i == 0) {
        str1 = str1 + str2;
      }
      str1 = str1 + "font-family: " + str3;
      i = 0;
    }
    if (str1.length() > 0)
    {
      if (this.fontMask != 0)
      {
        writeEndMask(this.fontMask);
        this.fontMask = 0;
      }
      startSpanTag(str1);
      this.fontAttributes = paramAttributeSet;
    }
    else if (this.fontAttributes != null)
    {
      writeEndMask(this.fontMask);
      this.fontMask = 0;
      endSpanTag();
    }
  }
  
  protected boolean inFontTag()
  {
    return this.fontAttributes != null;
  }
  
  protected void endFontTag()
    throws IOException
  {
    write('\n');
    writeEndTag("</font>");
    this.fontAttributes = null;
  }
  
  protected void startFontTag(String paramString)
    throws IOException
  {
    int i = 0;
    if (inFontTag())
    {
      endFontTag();
      i = 1;
    }
    writeStartTag("<font style=\"" + paramString + "\">");
    if (i != 0) {
      indent();
    }
  }
  
  private void startSpanTag(String paramString)
    throws IOException
  {
    int i = 0;
    if (inFontTag())
    {
      endSpanTag();
      i = 1;
    }
    writeStartTag("<span style=\"" + paramString + "\">");
    if (i != 0) {
      indent();
    }
  }
  
  private void endSpanTag()
    throws IOException
  {
    write('\n');
    writeEndTag("</span>");
    this.fontAttributes = null;
  }
  
  private String addStyleName(String paramString)
  {
    if (this.styleNameMapping == null) {
      return paramString;
    }
    StringBuilder localStringBuilder = null;
    for (int i = paramString.length() - 1; i >= 0; i--) {
      if (!isValidCharacter(paramString.charAt(i)))
      {
        if (localStringBuilder == null) {
          localStringBuilder = new StringBuilder(paramString);
        }
        localStringBuilder.setCharAt(i, 'a');
      }
    }
    for (String str = localStringBuilder != null ? localStringBuilder.toString() : paramString; this.styleNameMapping.get(str) != null; str = str + 'x') {}
    this.styleNameMapping.put(paramString, str);
    return str;
  }
  
  private String mapStyleName(String paramString)
  {
    if (this.styleNameMapping == null) {
      return paramString;
    }
    String str = (String)this.styleNameMapping.get(paramString);
    return str == null ? paramString : str;
  }
  
  private boolean isValidCharacter(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
  }
}
