package javax.swing.text.html.parser;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;

public class Parser
  implements DTDConstants
{
  private char[] text = new char['Ѐ'];
  private int textpos = 0;
  private TagElement last;
  private boolean space;
  private char[] str = new char[''];
  private int strpos = 0;
  protected DTD dtd = null;
  private int ch;
  private int ln;
  private Reader in;
  private Element recent;
  private TagStack stack;
  private boolean skipTag = false;
  private TagElement lastFormSent = null;
  private SimpleAttributeSet attributes = new SimpleAttributeSet();
  private boolean seenHtml = false;
  private boolean seenHead = false;
  private boolean seenBody = false;
  private boolean ignoreSpace;
  protected boolean strict = false;
  private int crlfCount;
  private int crCount;
  private int lfCount;
  private int currentBlockStartPos;
  private int lastBlockStartPos;
  private static final char[] cp1252Map = { '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '', '', '', '', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '', '', 'Ÿ' };
  private static final String START_COMMENT = "<!--";
  private static final String END_COMMENT = "-->";
  private static final char[] SCRIPT_END_TAG = "</script>".toCharArray();
  private static final char[] SCRIPT_END_TAG_UPPER_CASE = "</SCRIPT>".toCharArray();
  private char[] buf = new char[1];
  private int pos;
  private int len;
  private int currentPosition;
  
  public Parser(DTD paramDTD)
  {
    this.dtd = paramDTD;
  }
  
  protected int getCurrentLine()
  {
    return this.ln;
  }
  
  int getBlockStartPosition()
  {
    return Math.max(0, this.lastBlockStartPos - 1);
  }
  
  protected TagElement makeTag(Element paramElement, boolean paramBoolean)
  {
    return new TagElement(paramElement, paramBoolean);
  }
  
  protected TagElement makeTag(Element paramElement)
  {
    return makeTag(paramElement, false);
  }
  
  protected SimpleAttributeSet getAttributes()
  {
    return this.attributes;
  }
  
  protected void flushAttributes()
  {
    this.attributes.removeAttributes(this.attributes);
  }
  
  protected void handleText(char[] paramArrayOfChar) {}
  
  protected void handleTitle(char[] paramArrayOfChar)
  {
    handleText(paramArrayOfChar);
  }
  
  protected void handleComment(char[] paramArrayOfChar) {}
  
  protected void handleEOFInComment()
  {
    int i = strIndexOf('\n');
    if (i >= 0)
    {
      handleComment(getChars(0, i));
      try
      {
        this.in.close();
        this.in = new CharArrayReader(getChars(i + 1));
        this.ch = 62;
      }
      catch (IOException localIOException)
      {
        error("ioexception");
      }
      resetStrBuffer();
    }
    else
    {
      error("eof.comment");
    }
  }
  
  protected void handleEmptyTag(TagElement paramTagElement)
    throws ChangedCharSetException
  {}
  
  protected void handleStartTag(TagElement paramTagElement) {}
  
  protected void handleEndTag(TagElement paramTagElement) {}
  
  protected void handleError(int paramInt, String paramString) {}
  
  void handleText(TagElement paramTagElement)
  {
    if (paramTagElement.breaksFlow())
    {
      this.space = false;
      if (!this.strict) {
        this.ignoreSpace = true;
      }
    }
    if ((this.textpos == 0) && ((!this.space) || (this.stack == null) || (this.last.breaksFlow()) || (!this.stack.advance(this.dtd.pcdata))))
    {
      this.last = paramTagElement;
      this.space = false;
      this.lastBlockStartPos = this.currentBlockStartPos;
      return;
    }
    if (this.space)
    {
      if (!this.ignoreSpace)
      {
        if (this.textpos + 1 > this.text.length)
        {
          arrayOfChar = new char[this.text.length + 200];
          System.arraycopy(this.text, 0, arrayOfChar, 0, this.text.length);
          this.text = arrayOfChar;
        }
        this.text[(this.textpos++)] = ' ';
        if ((!this.strict) && (!paramTagElement.getElement().isEmpty())) {
          this.ignoreSpace = true;
        }
      }
      this.space = false;
    }
    char[] arrayOfChar = new char[this.textpos];
    System.arraycopy(this.text, 0, arrayOfChar, 0, this.textpos);
    if (paramTagElement.getElement().getName().equals("title")) {
      handleTitle(arrayOfChar);
    } else {
      handleText(arrayOfChar);
    }
    this.lastBlockStartPos = this.currentBlockStartPos;
    this.textpos = 0;
    this.last = paramTagElement;
    this.space = false;
  }
  
  protected void error(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    handleError(this.ln, paramString1 + " " + paramString2 + " " + paramString3 + " " + paramString4);
  }
  
  protected void error(String paramString1, String paramString2, String paramString3)
  {
    error(paramString1, paramString2, paramString3, "?");
  }
  
  protected void error(String paramString1, String paramString2)
  {
    error(paramString1, paramString2, "?", "?");
  }
  
  protected void error(String paramString)
  {
    error(paramString, "?", "?", "?");
  }
  
  protected void startTag(TagElement paramTagElement)
    throws ChangedCharSetException
  {
    Element localElement = paramTagElement.getElement();
    if ((!localElement.isEmpty()) || ((this.last != null) && (!this.last.breaksFlow())) || (this.textpos != 0))
    {
      handleText(paramTagElement);
    }
    else
    {
      this.last = paramTagElement;
      this.space = false;
    }
    this.lastBlockStartPos = this.currentBlockStartPos;
    for (AttributeList localAttributeList = localElement.atts; localAttributeList != null; localAttributeList = localAttributeList.next) {
      if ((localAttributeList.modifier == 2) && ((this.attributes.isEmpty()) || ((!this.attributes.isDefined(localAttributeList.name)) && (!this.attributes.isDefined(HTML.getAttributeKey(localAttributeList.name)))))) {
        error("req.att ", localAttributeList.getName(), localElement.getName());
      }
    }
    if (localElement.isEmpty())
    {
      handleEmptyTag(paramTagElement);
    }
    else
    {
      this.recent = localElement;
      this.stack = new TagStack(paramTagElement, this.stack);
      handleStartTag(paramTagElement);
    }
  }
  
  protected void endTag(boolean paramBoolean)
  {
    handleText(this.stack.tag);
    if ((paramBoolean) && (!this.stack.elem.omitEnd())) {
      error("end.missing", this.stack.elem.getName());
    } else if (!this.stack.terminate()) {
      error("end.unexpected", this.stack.elem.getName());
    }
    handleEndTag(this.stack.tag);
    this.stack = this.stack.next;
    this.recent = (this.stack != null ? this.stack.elem : null);
  }
  
  boolean ignoreElement(Element paramElement)
  {
    String str1 = this.stack.elem.getName();
    String str2 = paramElement.getName();
    if (((str2.equals("html")) && (this.seenHtml)) || ((str2.equals("head")) && (this.seenHead)) || ((str2.equals("body")) && (this.seenBody))) {
      return true;
    }
    if ((str2.equals("dt")) || (str2.equals("dd")))
    {
      for (TagStack localTagStack = this.stack; (localTagStack != null) && (!localTagStack.elem.getName().equals("dl")); localTagStack = localTagStack.next) {}
      if (localTagStack == null) {
        return true;
      }
    }
    return ((str1.equals("table")) && (!str2.equals("#pcdata")) && (!str2.equals("input"))) || ((str2.equals("font")) && ((str1.equals("ul")) || (str1.equals("ol")))) || ((str2.equals("meta")) && (this.stack != null)) || ((str2.equals("style")) && (this.seenBody)) || ((str1.equals("table")) && (str2.equals("a")));
  }
  
  protected void markFirstTime(Element paramElement)
  {
    String str1 = paramElement.getName();
    if (str1.equals("html"))
    {
      this.seenHtml = true;
    }
    else if (str1.equals("head"))
    {
      this.seenHead = true;
    }
    else if (str1.equals("body"))
    {
      if (this.buf.length == 1)
      {
        char[] arrayOfChar = new char['Ā'];
        arrayOfChar[0] = this.buf[0];
        this.buf = arrayOfChar;
      }
      this.seenBody = true;
    }
  }
  
  boolean legalElementContext(Element paramElement)
    throws ChangedCharSetException
  {
    if (this.stack == null)
    {
      if (paramElement != this.dtd.html)
      {
        startTag(makeTag(this.dtd.html, true));
        return legalElementContext(paramElement);
      }
      return true;
    }
    if (this.stack.advance(paramElement))
    {
      markFirstTime(paramElement);
      return true;
    }
    int i = 0;
    String str1 = this.stack.elem.getName();
    String str2 = paramElement.getName();
    if ((!this.strict) && (((str1.equals("table")) && (str2.equals("td"))) || ((str1.equals("table")) && (str2.equals("th"))) || ((str1.equals("tr")) && (!str2.equals("tr"))))) {
      i = 1;
    }
    if ((!this.strict) && (i == 0) && ((this.stack.elem.getName() != paramElement.getName()) || (paramElement.getName().equals("body"))) && ((this.skipTag = ignoreElement(paramElement))))
    {
      error("tag.ignore", paramElement.getName());
      return this.skipTag;
    }
    Object localObject2;
    if ((!this.strict) && (str1.equals("table")) && (!str2.equals("tr")) && (!str2.equals("td")) && (!str2.equals("th")) && (!str2.equals("caption")))
    {
      localObject1 = this.dtd.getElement("tr");
      localObject2 = makeTag((Element)localObject1, true);
      legalTagContext((TagElement)localObject2);
      startTag((TagElement)localObject2);
      error("start.missing", paramElement.getName());
      return legalElementContext(paramElement);
    }
    if ((i == 0) && (this.stack.terminate()) && ((!this.strict) || (this.stack.elem.omitEnd()))) {
      for (localObject1 = this.stack.next; localObject1 != null; localObject1 = ((TagStack)localObject1).next)
      {
        if (((TagStack)localObject1).advance(paramElement))
        {
          while (this.stack != localObject1) {
            endTag(true);
          }
          return true;
        }
        if ((!((TagStack)localObject1).terminate()) || ((this.strict) && (!((TagStack)localObject1).elem.omitEnd()))) {
          break;
        }
      }
    }
    Object localObject1 = this.stack.first();
    if ((localObject1 != null) && ((!this.strict) || (((Element)localObject1).omitStart())) && ((localObject1 != this.dtd.head) || (paramElement != this.dtd.pcdata)))
    {
      localObject2 = makeTag((Element)localObject1, true);
      legalTagContext((TagElement)localObject2);
      startTag((TagElement)localObject2);
      if (!((Element)localObject1).omitStart()) {
        error("start.missing", paramElement.getName());
      }
      return legalElementContext(paramElement);
    }
    if (!this.strict)
    {
      localObject2 = this.stack.contentModel();
      Vector localVector = new Vector();
      if (localObject2 != null)
      {
        ((ContentModel)localObject2).getElements(localVector);
        Iterator localIterator = localVector.iterator();
        while (localIterator.hasNext())
        {
          Element localElement = (Element)localIterator.next();
          if (!this.stack.excluded(localElement.getIndex()))
          {
            int j = 0;
            for (Object localObject3 = localElement.getAttributes(); localObject3 != null; localObject3 = ((AttributeList)localObject3).next) {
              if (((AttributeList)localObject3).modifier == 2)
              {
                j = 1;
                break;
              }
            }
            if (j == 0)
            {
              localObject3 = localElement.getContent();
              if ((localObject3 != null) && (((ContentModel)localObject3).first(paramElement)))
              {
                TagElement localTagElement = makeTag(localElement, true);
                legalTagContext(localTagElement);
                startTag(localTagElement);
                error("start.missing", localElement.getName());
                return legalElementContext(paramElement);
              }
            }
          }
        }
      }
    }
    if ((this.stack.terminate()) && (this.stack.elem != this.dtd.body) && ((!this.strict) || (this.stack.elem.omitEnd())))
    {
      if (!this.stack.elem.omitEnd()) {
        error("end.missing", paramElement.getName());
      }
      endTag(true);
      return legalElementContext(paramElement);
    }
    return false;
  }
  
  void legalTagContext(TagElement paramTagElement)
    throws ChangedCharSetException
  {
    if (legalElementContext(paramTagElement.getElement()))
    {
      markFirstTime(paramTagElement.getElement());
      return;
    }
    if ((paramTagElement.breaksFlow()) && (this.stack != null) && (!this.stack.tag.breaksFlow()))
    {
      endTag(true);
      legalTagContext(paramTagElement);
      return;
    }
    for (TagStack localTagStack = this.stack; localTagStack != null; localTagStack = localTagStack.next) {
      if (localTagStack.tag.getElement() == this.dtd.head)
      {
        while (this.stack != localTagStack) {
          endTag(true);
        }
        endTag(true);
        legalTagContext(paramTagElement);
        return;
      }
    }
    error("tag.unexpected", paramTagElement.getElement().getName());
  }
  
  void errorContext()
    throws ChangedCharSetException
  {
    while ((this.stack != null) && (this.stack.tag.getElement() != this.dtd.body))
    {
      handleEndTag(this.stack.tag);
      this.stack = this.stack.next;
    }
    if (this.stack == null)
    {
      legalElementContext(this.dtd.body);
      startTag(makeTag(this.dtd.body, true));
    }
  }
  
  void addString(int paramInt)
  {
    if (this.strpos == this.str.length)
    {
      char[] arrayOfChar = new char[this.str.length + 128];
      System.arraycopy(this.str, 0, arrayOfChar, 0, this.str.length);
      this.str = arrayOfChar;
    }
    this.str[(this.strpos++)] = ((char)paramInt);
  }
  
  String getString(int paramInt)
  {
    char[] arrayOfChar = new char[this.strpos - paramInt];
    System.arraycopy(this.str, paramInt, arrayOfChar, 0, this.strpos - paramInt);
    this.strpos = paramInt;
    return new String(arrayOfChar);
  }
  
  char[] getChars(int paramInt)
  {
    char[] arrayOfChar = new char[this.strpos - paramInt];
    System.arraycopy(this.str, paramInt, arrayOfChar, 0, this.strpos - paramInt);
    this.strpos = paramInt;
    return arrayOfChar;
  }
  
  char[] getChars(int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[paramInt2 - paramInt1];
    System.arraycopy(this.str, paramInt1, arrayOfChar, 0, paramInt2 - paramInt1);
    return arrayOfChar;
  }
  
  void resetStrBuffer()
  {
    this.strpos = 0;
  }
  
  int strIndexOf(char paramChar)
  {
    for (int i = 0; i < this.strpos; i++) {
      if (this.str[i] == paramChar) {
        return i;
      }
    }
    return -1;
  }
  
  void skipSpace()
    throws IOException
  {
    for (;;)
    {
      switch (this.ch)
      {
      case 10: 
        this.ln += 1;
        this.ch = readCh();
        this.lfCount += 1;
        break;
      case 13: 
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
        break;
      case 9: 
      case 32: 
        this.ch = readCh();
      }
    }
  }
  
  boolean parseIdentifier(boolean paramBoolean)
    throws IOException
  {
    switch (this.ch)
    {
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
    case 87: 
    case 88: 
    case 89: 
    case 90: 
      if (paramBoolean) {
        this.ch = (97 + (this.ch - 65));
      }
    case 97: 
    case 98: 
    case 99: 
    case 100: 
    case 101: 
    case 102: 
    case 103: 
    case 104: 
    case 105: 
    case 106: 
    case 107: 
    case 108: 
    case 109: 
    case 110: 
    case 111: 
    case 112: 
    case 113: 
    case 114: 
    case 115: 
    case 116: 
    case 117: 
    case 118: 
    case 119: 
    case 120: 
    case 121: 
    case 122: 
      break;
    }
    return false;
    for (;;)
    {
      addString(this.ch);
      switch (this.ch = readCh())
      {
      case 65: 
      case 66: 
      case 67: 
      case 68: 
      case 69: 
      case 70: 
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
        if (paramBoolean) {
          this.ch = (97 + (this.ch - 65));
        }
        break;
      }
    }
    return true;
  }
  
  private char[] parseEntityReference()
    throws IOException
  {
    int i = this.strpos;
    if ((this.ch = readCh()) == 35)
    {
      int j = 0;
      this.ch = readCh();
      if (((this.ch >= 48) && (this.ch <= 57)) || (this.ch == 120) || (this.ch == 88))
      {
        if ((this.ch >= 48) && (this.ch <= 57)) {}
        while ((this.ch >= 48) && (this.ch <= 57))
        {
          j = j * 10 + this.ch - 48;
          this.ch = readCh();
          continue;
          this.ch = readCh();
          for (int m = (char)Character.toLowerCase(this.ch); ((m >= 48) && (m <= 57)) || ((m >= 97) && (m <= 102)); m = (char)Character.toLowerCase(this.ch))
          {
            if ((m >= 48) && (m <= 57)) {
              j = j * 16 + m - 48;
            } else {
              j = j * 16 + m - 97 + 10;
            }
            this.ch = readCh();
          }
        }
        switch (this.ch)
        {
        case 10: 
          this.ln += 1;
          this.ch = readCh();
          this.lfCount += 1;
          break;
        case 13: 
          this.ln += 1;
          if ((this.ch = readCh()) == 10)
          {
            this.ch = readCh();
            this.crlfCount += 1;
          }
          else
          {
            this.crCount += 1;
          }
          break;
        case 59: 
          this.ch = readCh();
        }
        localObject = mapNumericReference(j);
        return localObject;
      }
      addString(35);
      if (!parseIdentifier(false))
      {
        error("ident.expected");
        this.strpos = i;
        localObject = new char[] { '&', '#' };
        return localObject;
      }
    }
    else if (!parseIdentifier(false))
    {
      char[] arrayOfChar1 = { '&' };
      return arrayOfChar1;
    }
    int k = 0;
    switch (this.ch)
    {
    case 10: 
      this.ln += 1;
      this.ch = readCh();
      this.lfCount += 1;
      break;
    case 13: 
      this.ln += 1;
      if ((this.ch = readCh()) == 10)
      {
        this.ch = readCh();
        this.crlfCount += 1;
      }
      else
      {
        this.crCount += 1;
      }
      break;
    case 59: 
      k = 1;
      this.ch = readCh();
    }
    Object localObject = getString(i);
    Entity localEntity = this.dtd.getEntity((String)localObject);
    if ((!this.strict) && (localEntity == null)) {
      localEntity = this.dtd.getEntity(((String)localObject).toLowerCase());
    }
    if ((localEntity == null) || (!localEntity.isGeneral()))
    {
      if (((String)localObject).length() == 0)
      {
        error("invalid.entref", (String)localObject);
        return new char[0];
      }
      String str1 = "&" + (String)localObject + (k != 0 ? ";" : "");
      char[] arrayOfChar2 = new char[str1.length()];
      str1.getChars(0, arrayOfChar2.length, arrayOfChar2, 0);
      return arrayOfChar2;
    }
    return localEntity.getData();
  }
  
  private char[] mapNumericReference(int paramInt)
  {
    char[] arrayOfChar;
    if (paramInt >= 65535)
    {
      try
      {
        arrayOfChar = Character.toChars(paramInt);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        arrayOfChar = new char[0];
      }
    }
    else
    {
      arrayOfChar = new char[1];
      arrayOfChar[0] = ((paramInt < 130) || (paramInt > 159) ? (char)paramInt : cp1252Map[(paramInt - 130)]);
    }
    return arrayOfChar;
  }
  
  void parseComment()
    throws IOException
  {
    for (;;)
    {
      int i = this.ch;
      switch (i)
      {
      case 45: 
        if ((!this.strict) && (this.strpos != 0) && (this.str[(this.strpos - 1)] == '-'))
        {
          if ((this.ch = readCh()) == 62) {
            return;
          }
          if (this.ch == 33)
          {
            if ((this.ch = readCh()) == 62) {
              return;
            }
            addString(45);
            addString(33);
          }
        }
        else if ((this.ch = readCh()) == 45)
        {
          this.ch = readCh();
          if ((this.strict) || (this.ch == 62)) {
            return;
          }
          if (this.ch == 33)
          {
            if ((this.ch = readCh()) == 62) {
              return;
            }
            addString(45);
            addString(33);
            continue;
          }
          addString(45);
        }
        break;
      case -1: 
        handleEOFInComment();
        return;
      case 10: 
        this.ln += 1;
        this.ch = readCh();
        this.lfCount += 1;
        break;
      case 62: 
        this.ch = readCh();
        break;
      case 13: 
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
        i = 10;
        break;
      default: 
        this.ch = readCh();
        addString(i);
      }
    }
  }
  
  void parseLiteral(boolean paramBoolean)
    throws IOException
  {
    for (;;)
    {
      int i = this.ch;
      switch (i)
      {
      case -1: 
        error("eof.literal", this.stack.elem.getName());
        endTag(true);
        return;
      case 62: 
        this.ch = readCh();
        int j = this.textpos - (this.stack.elem.name.length() + 2);
        int k = 0;
        if ((j >= 0) && (this.text[(j++)] == '<') && (this.text[j] == '/'))
        {
          do
          {
            j++;
          } while ((j < this.textpos) && (Character.toLowerCase(this.text[j]) == this.stack.elem.name.charAt(k++)));
          if (j == this.textpos)
          {
            this.textpos -= this.stack.elem.name.length() + 2;
            if ((this.textpos > 0) && (this.text[(this.textpos - 1)] == '\n')) {
              this.textpos -= 1;
            }
            endTag(false);
            return;
          }
        }
      case 38: 
        char[] arrayOfChar2 = parseEntityReference();
        if (this.textpos + arrayOfChar2.length > this.text.length)
        {
          char[] arrayOfChar3 = new char[Math.max(this.textpos + arrayOfChar2.length + 128, this.text.length * 2)];
          System.arraycopy(this.text, 0, arrayOfChar3, 0, this.text.length);
          this.text = arrayOfChar3;
        }
        System.arraycopy(arrayOfChar2, 0, this.text, this.textpos, arrayOfChar2.length);
        this.textpos += arrayOfChar2.length;
        break;
      case 10: 
        this.ln += 1;
        this.ch = readCh();
        this.lfCount += 1;
        break;
      case 13: 
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
        i = 10;
        break;
      default: 
        this.ch = readCh();
        if (this.textpos == this.text.length)
        {
          char[] arrayOfChar1 = new char[this.text.length + 128];
          System.arraycopy(this.text, 0, arrayOfChar1, 0, this.text.length);
          this.text = arrayOfChar1;
        }
        this.text[(this.textpos++)] = ((char)i);
      }
    }
  }
  
  String parseAttributeValue(boolean paramBoolean)
    throws IOException
  {
    int i = -1;
    switch (this.ch)
    {
    case 34: 
    case 39: 
      i = this.ch;
      this.ch = readCh();
    }
    for (;;)
    {
      int j = this.ch;
      switch (j)
      {
      case 10: 
        this.ln += 1;
        this.ch = readCh();
        this.lfCount += 1;
        if (i < 0) {
          return getString(0);
        }
      case 13: 
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
        if (i < 0) {
          return getString(0);
        }
      case 9: 
        if (i < 0) {
          j = 32;
        }
      case 32: 
        this.ch = readCh();
        if (i < 0) {
          return getString(0);
        }
      case 60: 
      case 62: 
        if (i < 0) {
          return getString(0);
        }
        this.ch = readCh();
        break;
      case 34: 
      case 39: 
        this.ch = readCh();
        if (j == i) {
          return getString(0);
        }
        if (i == -1)
        {
          error("attvalerr");
          if ((!this.strict) && (this.ch != 32)) {
            continue;
          }
          return getString(0);
        }
      case 61: 
        if (i < 0)
        {
          error("attvalerr");
          if (this.strict) {
            return getString(0);
          }
        }
        this.ch = readCh();
        break;
      case 38: 
        if ((this.strict) && (i < 0))
        {
          this.ch = readCh();
        }
        else
        {
          char[] arrayOfChar = parseEntityReference();
          for (int k = 0; k < arrayOfChar.length; k++)
          {
            j = arrayOfChar[k];
            addString((paramBoolean) && (j >= 65) && (j <= 90) ? 97 + j - 65 : j);
          }
        }
        break;
      case -1: 
        return getString(0);
      default: 
        if ((paramBoolean) && (j >= 65) && (j <= 90)) {
          j = 97 + j - 65;
        }
        this.ch = readCh();
        addString(j);
      }
    }
  }
  
  void parseAttributeSpecificationList(Element paramElement)
    throws IOException
  {
    for (;;)
    {
      skipSpace();
      switch (this.ch)
      {
      case -1: 
      case 47: 
      case 60: 
      case 62: 
        return;
      case 45: 
        if ((this.ch = readCh()) == 45)
        {
          this.ch = readCh();
          parseComment();
          this.strpos = 0;
        }
        else
        {
          error("invalid.tagchar", "-", paramElement.getName());
          this.ch = readCh();
        }
        break;
      default: 
        String str1;
        AttributeList localAttributeList;
        String str2;
        if (parseIdentifier(true))
        {
          str1 = getString(0);
          skipSpace();
          if (this.ch == 61)
          {
            this.ch = readCh();
            skipSpace();
            localAttributeList = paramElement.getAttribute(str1);
            str2 = parseAttributeValue((localAttributeList != null) && (localAttributeList.type != 1) && (localAttributeList.type != 11) && (localAttributeList.type != 7));
          }
          else
          {
            str2 = str1;
            localAttributeList = paramElement.getAttributeByValue(str2);
            if (localAttributeList == null)
            {
              localAttributeList = paramElement.getAttribute(str1);
              if (localAttributeList != null) {
                str2 = localAttributeList.getValue();
              } else {
                str2 = null;
              }
            }
          }
        }
        else
        {
          if ((!this.strict) && (this.ch == 44))
          {
            this.ch = readCh();
            continue;
          }
          if ((!this.strict) && (this.ch == 34))
          {
            this.ch = readCh();
            skipSpace();
            if (parseIdentifier(true))
            {
              str1 = getString(0);
              if (this.ch == 34) {
                this.ch = readCh();
              }
              skipSpace();
              if (this.ch == 61)
              {
                this.ch = readCh();
                skipSpace();
                localAttributeList = paramElement.getAttribute(str1);
                str2 = parseAttributeValue((localAttributeList != null) && (localAttributeList.type != 1) && (localAttributeList.type != 11));
              }
              else
              {
                str2 = str1;
                localAttributeList = paramElement.getAttributeByValue(str2);
                if (localAttributeList == null)
                {
                  localAttributeList = paramElement.getAttribute(str1);
                  if (localAttributeList != null) {
                    str2 = localAttributeList.getValue();
                  }
                }
              }
            }
            else
            {
              localObject = new char[] { (char)this.ch };
              error("invalid.tagchar", new String((char[])localObject), paramElement.getName());
              this.ch = readCh();
            }
          }
          else if ((!this.strict) && (this.attributes.isEmpty()) && (this.ch == 61))
          {
            this.ch = readCh();
            skipSpace();
            str1 = paramElement.getName();
            localAttributeList = paramElement.getAttribute(str1);
            str2 = parseAttributeValue((localAttributeList != null) && (localAttributeList.type != 1) && (localAttributeList.type != 11));
          }
          else
          {
            if ((!this.strict) && (this.ch == 61))
            {
              this.ch = readCh();
              skipSpace();
              str2 = parseAttributeValue(true);
              error("attvalerr");
              return;
            }
            localObject = new char[] { (char)this.ch };
            error("invalid.tagchar", new String((char[])localObject), paramElement.getName());
            if (!this.strict)
            {
              this.ch = readCh();
              continue;
            }
            return;
          }
        }
        if (localAttributeList != null) {
          str1 = localAttributeList.getName();
        } else {
          error("invalid.tagatt", str1, paramElement.getName());
        }
        if (this.attributes.isDefined(str1)) {
          error("multi.tagatt", str1, paramElement.getName());
        }
        if (str2 == null) {
          str2 = (localAttributeList != null) && (localAttributeList.value != null) ? localAttributeList.value : "#DEFAULT";
        } else if ((localAttributeList != null) && (localAttributeList.values != null) && (!localAttributeList.values.contains(str2))) {
          error("invalid.tagattval", str1, paramElement.getName());
        }
        Object localObject = HTML.getAttributeKey(str1);
        if (localObject == null) {
          this.attributes.addAttribute(str1, str2);
        } else {
          this.attributes.addAttribute(localObject, str2);
        }
        break;
      }
    }
  }
  
  public String parseDTDMarkup()
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    this.ch = readCh();
    for (;;)
    {
      switch (this.ch)
      {
      case 62: 
        this.ch = readCh();
        return localStringBuilder.toString();
      case -1: 
        error("invalid.markup");
        return localStringBuilder.toString();
      case 10: 
        this.ln += 1;
        this.ch = readCh();
        this.lfCount += 1;
        break;
      case 34: 
        this.ch = readCh();
        break;
      case 13: 
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
        break;
      default: 
        localStringBuilder.append((char)(this.ch & 0xFF));
        this.ch = readCh();
      }
    }
  }
  
  protected boolean parseMarkupDeclarations(StringBuffer paramStringBuffer)
    throws IOException
  {
    if ((paramStringBuffer.length() == "DOCTYPE".length()) && (paramStringBuffer.toString().toUpperCase().equals("DOCTYPE")))
    {
      parseDTDMarkup();
      return true;
    }
    return false;
  }
  
  void parseInvalidTag()
    throws IOException
  {
    for (;;)
    {
      skipSpace();
      switch (this.ch)
      {
      case -1: 
      case 62: 
        this.ch = readCh();
        return;
      case 60: 
        return;
      }
      this.ch = readCh();
    }
  }
  
  void parseTag()
    throws IOException
  {
    boolean bool = false;
    int i = 0;
    int j = 0;
    Element localElement;
    switch (this.ch = readCh())
    {
    case 33: 
      switch (this.ch = readCh())
      {
      case 45: 
        for (;;)
        {
          if (this.ch == 45)
          {
            if ((!this.strict) || ((this.ch = readCh()) == 45))
            {
              this.ch = readCh();
              if ((!this.strict) && (this.ch == 45)) {
                this.ch = readCh();
              }
              if (this.textpos != 0)
              {
                localObject = new char[this.textpos];
                System.arraycopy(this.text, 0, localObject, 0, this.textpos);
                handleText((char[])localObject);
                this.lastBlockStartPos = this.currentBlockStartPos;
                this.textpos = 0;
              }
              parseComment();
              this.last = makeTag(this.dtd.getElement("comment"), true);
              handleComment(getChars(0));
              continue;
            }
            if (i == 0)
            {
              i = 1;
              error("invalid.commentchar", "-");
            }
          }
          skipSpace();
          switch (this.ch)
          {
          case 45: 
            break;
          case 62: 
            this.ch = readCh();
          case -1: 
            return;
          default: 
            this.ch = readCh();
            if (i == 0)
            {
              i = 1;
              error("invalid.commentchar", String.valueOf((char)this.ch));
            }
            break;
          }
        }
      }
      localObject = new StringBuffer();
      for (;;)
      {
        ((StringBuffer)localObject).append((char)this.ch);
        if (parseMarkupDeclarations((StringBuffer)localObject)) {
          return;
        }
        switch (this.ch)
        {
        case 62: 
          this.ch = readCh();
        case -1: 
          error("invalid.markup");
          return;
        case 10: 
          this.ln += 1;
          this.ch = readCh();
          this.lfCount += 1;
          break;
        case 13: 
          this.ln += 1;
          if ((this.ch = readCh()) == 10)
          {
            this.ch = readCh();
            this.crlfCount += 1;
          }
          else
          {
            this.crCount += 1;
          }
          break;
        default: 
          this.ch = readCh();
        }
      }
    case 47: 
      switch (this.ch = readCh())
      {
      case 62: 
        this.ch = readCh();
      case 60: 
        if (this.recent == null)
        {
          error("invalid.shortend");
          return;
        }
        localElement = this.recent;
        break;
      default: 
        if (!parseIdentifier(true))
        {
          error("expected.endtagname");
          return;
        }
        skipSpace();
        switch (this.ch)
        {
        case 62: 
          this.ch = readCh();
        case 60: 
          break;
        default: 
          error("expected", "'>'");
          while ((this.ch != -1) && (this.ch != 10) && (this.ch != 62)) {
            this.ch = readCh();
          }
          if (this.ch == 62) {
            this.ch = readCh();
          }
          break;
        }
        localObject = getString(0);
        if (!this.dtd.elementExists((String)localObject))
        {
          error("end.unrecognized", (String)localObject);
          if ((this.textpos > 0) && (this.text[(this.textpos - 1)] == '\n')) {
            this.textpos -= 1;
          }
          localElement = this.dtd.getElement("unknown");
          localElement.name = ((String)localObject);
          j = 1;
        }
        else
        {
          localElement = this.dtd.getElement((String)localObject);
        }
        break;
      }
      if (this.stack == null)
      {
        error("end.extra.tag", localElement.getName());
        return;
      }
      if ((this.textpos > 0) && (this.text[(this.textpos - 1)] == '\n')) {
        if (this.stack.pre)
        {
          if ((this.textpos > 1) && (this.text[(this.textpos - 2)] != '\n')) {
            this.textpos -= 1;
          }
        }
        else {
          this.textpos -= 1;
        }
      }
      if (j != 0)
      {
        localObject = makeTag(localElement);
        handleText((TagElement)localObject);
        this.attributes.addAttribute(HTML.Attribute.ENDTAG, "true");
        handleEmptyTag(makeTag(localElement));
        j = 0;
        return;
      }
      if (!this.strict)
      {
        localObject = this.stack.elem.getName();
        if ((((String)localObject).equals("table")) && (!localElement.getName().equals(localObject)))
        {
          error("tag.ignore", localElement.getName());
          return;
        }
        if (((((String)localObject).equals("tr")) || (((String)localObject).equals("td"))) && (!localElement.getName().equals("table")) && (!localElement.getName().equals(localObject)))
        {
          error("tag.ignore", localElement.getName());
          return;
        }
      }
      for (localObject = this.stack; (localObject != null) && (localElement != ((TagStack)localObject).elem); localObject = ((TagStack)localObject).next) {}
      if (localObject == null)
      {
        error("unmatched.endtag", localElement.getName());
        return;
      }
      String str1 = localElement.getName();
      if ((this.stack != localObject) && ((str1.equals("font")) || (str1.equals("center"))))
      {
        if (str1.equals("center"))
        {
          while ((this.stack.elem.omitEnd()) && (this.stack != localObject)) {
            endTag(true);
          }
          if (this.stack.elem == localElement) {
            endTag(false);
          }
        }
        return;
      }
      while (this.stack != localObject) {
        endTag(true);
      }
      endTag(false);
      return;
    case -1: 
      error("eof");
      return;
    }
    if (!parseIdentifier(true))
    {
      localElement = this.recent;
      if ((this.ch != 62) || (localElement == null)) {
        error("expected.tagname");
      }
    }
    else
    {
      localObject = getString(0);
      if (((String)localObject).equals("image")) {
        localObject = "img";
      }
      if (!this.dtd.elementExists((String)localObject))
      {
        error("tag.unrecognized ", (String)localObject);
        localElement = this.dtd.getElement("unknown");
        localElement.name = ((String)localObject);
        j = 1;
      }
      else
      {
        localElement = this.dtd.getElement((String)localObject);
      }
    }
    parseAttributeSpecificationList(localElement);
    switch (this.ch)
    {
    case 47: 
      bool = true;
    case 62: 
      this.ch = readCh();
      if ((this.ch == 62) && (bool)) {
        this.ch = readCh();
      }
    case 60: 
      break;
    }
    error("expected", "'>'");
    if ((!this.strict) && (localElement.getName().equals("script"))) {
      error("javascript.unsupported");
    }
    if (!localElement.isEmpty()) {
      if (this.ch == 10)
      {
        this.ln += 1;
        this.lfCount += 1;
        this.ch = readCh();
      }
      else if (this.ch == 13)
      {
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
      }
    }
    Object localObject = makeTag(localElement, false);
    if (j == 0)
    {
      legalTagContext((TagElement)localObject);
      if ((!this.strict) && (this.skipTag))
      {
        this.skipTag = false;
        return;
      }
    }
    startTag((TagElement)localObject);
    if (!localElement.isEmpty()) {
      switch (localElement.getType())
      {
      case 1: 
        parseLiteral(false);
        break;
      case 16: 
        parseLiteral(true);
        break;
      default: 
        if (this.stack != null) {
          this.stack.net = bool;
        }
        break;
      }
    }
  }
  
  void parseScript()
    throws IOException
  {
    char[] arrayOfChar = new char[SCRIPT_END_TAG.length];
    int i = 0;
    for (;;)
    {
      for (int j = 0; (i == 0) && (j < SCRIPT_END_TAG.length) && ((SCRIPT_END_TAG[j] == this.ch) || (SCRIPT_END_TAG_UPPER_CASE[j] == this.ch)); j++)
      {
        arrayOfChar[j] = ((char)this.ch);
        this.ch = readCh();
      }
      if (j == SCRIPT_END_TAG.length) {
        return;
      }
      for (int k = 0; k < j; k++) {
        addString(arrayOfChar[k]);
      }
      switch (this.ch)
      {
      case -1: 
        error("eof.script");
        return;
      case 10: 
        this.ln += 1;
        this.ch = readCh();
        this.lfCount += 1;
        addString(10);
        break;
      case 13: 
        this.ln += 1;
        if ((this.ch = readCh()) == 10)
        {
          this.ch = readCh();
          this.crlfCount += 1;
        }
        else
        {
          this.crCount += 1;
        }
        addString(10);
        break;
      default: 
        addString(this.ch);
        String str1 = new String(getChars(0, this.strpos));
        if ((i == 0) && (str1.endsWith("<!--"))) {
          i = 1;
        }
        if ((i != 0) && (str1.endsWith("-->"))) {
          i = 0;
        }
        this.ch = readCh();
      }
    }
  }
  
  void parseContent()
    throws IOException
  {
    Thread localThread = Thread.currentThread();
    for (;;)
    {
      if (localThread.isInterrupted())
      {
        localThread.interrupt();
        break;
      }
      int i = this.ch;
      this.currentBlockStartPos = this.currentPosition;
      Object localObject;
      if (this.recent == this.dtd.script)
      {
        parseScript();
        this.last = makeTag(this.dtd.getElement("comment"), true);
        localObject = new String(getChars(0)).trim();
        int j = "<!--".length() + "-->".length();
        if ((((String)localObject).startsWith("<!--")) && (((String)localObject).endsWith("-->")) && (((String)localObject).length() >= j)) {
          localObject = ((String)localObject).substring("<!--".length(), ((String)localObject).length() - "-->".length());
        }
        handleComment(((String)localObject).toCharArray());
        endTag(false);
        this.lastBlockStartPos = this.currentPosition;
      }
      else
      {
        switch (i)
        {
        case 60: 
          parseTag();
          this.lastBlockStartPos = this.currentPosition;
          break;
        case 47: 
          this.ch = readCh();
          if ((this.stack != null) && (this.stack.net))
          {
            endTag(false);
          }
          else if (this.textpos == 0)
          {
            if (!legalElementContext(this.dtd.pcdata)) {
              error("unexpected.pcdata");
            }
            if (this.last.breaksFlow()) {
              this.space = false;
            }
          }
          break;
        case -1: 
          return;
        case 38: 
          if (this.textpos == 0)
          {
            if (!legalElementContext(this.dtd.pcdata)) {
              error("unexpected.pcdata");
            }
            if (this.last.breaksFlow()) {
              this.space = false;
            }
          }
          localObject = parseEntityReference();
          if (this.textpos + localObject.length + 1 > this.text.length)
          {
            char[] arrayOfChar = new char[Math.max(this.textpos + localObject.length + 128, this.text.length * 2)];
            System.arraycopy(this.text, 0, arrayOfChar, 0, this.text.length);
            this.text = arrayOfChar;
          }
          if (this.space)
          {
            this.space = false;
            this.text[(this.textpos++)] = ' ';
          }
          System.arraycopy(localObject, 0, this.text, this.textpos, localObject.length);
          this.textpos += localObject.length;
          this.ignoreSpace = false;
          break;
        case 10: 
          this.ln += 1;
          this.lfCount += 1;
          this.ch = readCh();
          if ((this.stack == null) || (!this.stack.pre))
          {
            if (this.textpos == 0) {
              this.lastBlockStartPos = this.currentPosition;
            }
            if (this.ignoreSpace) {
              continue;
            }
            this.space = true;
          }
          break;
        case 13: 
          this.ln += 1;
          i = 10;
          if ((this.ch = readCh()) == 10)
          {
            this.ch = readCh();
            this.crlfCount += 1;
          }
          else
          {
            this.crCount += 1;
          }
          if ((this.stack == null) || (!this.stack.pre))
          {
            if (this.textpos == 0) {
              this.lastBlockStartPos = this.currentPosition;
            }
            if (this.ignoreSpace) {
              continue;
            }
            this.space = true;
          }
          break;
        case 9: 
        case 32: 
          this.ch = readCh();
          if ((this.stack == null) || (!this.stack.pre))
          {
            if (this.textpos == 0) {
              this.lastBlockStartPos = this.currentPosition;
            }
            if (this.ignoreSpace) {
              continue;
            }
            this.space = true;
          }
          break;
        default: 
          if (this.textpos == 0)
          {
            if (!legalElementContext(this.dtd.pcdata)) {
              error("unexpected.pcdata");
            }
            if (this.last.breaksFlow()) {
              this.space = false;
            }
          }
          this.ch = readCh();
          if (this.textpos + 2 > this.text.length)
          {
            localObject = new char[this.text.length + 128];
            System.arraycopy(this.text, 0, localObject, 0, this.text.length);
            this.text = ((char[])localObject);
          }
          if (this.space)
          {
            if (this.textpos == 0) {
              this.lastBlockStartPos -= 1;
            }
            this.text[(this.textpos++)] = ' ';
            this.space = false;
          }
          this.text[(this.textpos++)] = ((char)i);
          this.ignoreSpace = false;
        }
      }
    }
  }
  
  String getEndOfLineString()
  {
    if (this.crlfCount >= this.crCount)
    {
      if (this.lfCount >= this.crlfCount) {
        return "\n";
      }
      return "\r\n";
    }
    if (this.crCount > this.lfCount) {
      return "\r";
    }
    return "\n";
  }
  
  public synchronized void parse(Reader paramReader)
    throws IOException
  {
    this.in = paramReader;
    this.ln = 1;
    this.seenHtml = false;
    this.seenHead = false;
    this.seenBody = false;
    this.crCount = (this.lfCount = this.crlfCount = 0);
    try
    {
      this.ch = readCh();
      this.text = new char['Ѐ'];
      this.str = new char[''];
      parseContent();
      while (this.stack != null) {
        endTag(true);
      }
      paramReader.close();
    }
    catch (IOException localIOException)
    {
      errorContext();
      error("ioexception");
      throw localIOException;
    }
    catch (Exception localException)
    {
      errorContext();
      error("exception", localException.getClass().getName(), localException.getMessage());
      localException.printStackTrace();
    }
    catch (ThreadDeath localThreadDeath)
    {
      errorContext();
      error("terminated");
      localThreadDeath.printStackTrace();
      throw localThreadDeath;
    }
    finally
    {
      while (this.stack != null)
      {
        handleEndTag(this.stack.tag);
        this.stack = this.stack.next;
      }
      this.text = null;
      this.str = null;
    }
  }
  
  private final int readCh()
    throws IOException
  {
    if (this.pos >= this.len)
    {
      try
      {
        this.len = this.in.read(this.buf);
      }
      catch (InterruptedIOException localInterruptedIOException)
      {
        throw localInterruptedIOException;
      }
      if (this.len <= 0) {
        return -1;
      }
      this.pos = 0;
    }
    this.currentPosition += 1;
    return this.buf[(this.pos++)];
  }
  
  protected int getCurrentPos()
  {
    return this.currentPosition;
  }
}
