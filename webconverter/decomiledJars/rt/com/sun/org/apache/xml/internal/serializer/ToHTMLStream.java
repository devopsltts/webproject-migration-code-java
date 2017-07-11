package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class ToHTMLStream
  extends ToStream
{
  protected boolean m_inDTD = false;
  private boolean m_inBlockElem = false;
  private static final CharInfo m_htmlcharInfo = CharInfo.getCharInfoInternal("com.sun.org.apache.xml.internal.serializer.HTMLEntities", "html");
  static final Trie m_elementFlags = new Trie();
  private static final ElemDesc m_dummy = new ElemDesc(8);
  private boolean m_specialEscapeURLs = true;
  private boolean m_omitMetaTag = false;
  private Trie m_htmlInfo = new Trie(m_elementFlags);
  
  static void initTagReference(Trie paramTrie)
  {
    paramTrie.put("BASEFONT", new ElemDesc(2));
    paramTrie.put("FRAME", new ElemDesc(10));
    paramTrie.put("FRAMESET", new ElemDesc(8));
    paramTrie.put("NOFRAMES", new ElemDesc(8));
    paramTrie.put("ISINDEX", new ElemDesc(10));
    paramTrie.put("APPLET", new ElemDesc(2097152));
    paramTrie.put("CENTER", new ElemDesc(8));
    paramTrie.put("DIR", new ElemDesc(8));
    paramTrie.put("MENU", new ElemDesc(8));
    paramTrie.put("TT", new ElemDesc(4096));
    paramTrie.put("I", new ElemDesc(4096));
    paramTrie.put("B", new ElemDesc(4096));
    paramTrie.put("BIG", new ElemDesc(4096));
    paramTrie.put("SMALL", new ElemDesc(4096));
    paramTrie.put("EM", new ElemDesc(8192));
    paramTrie.put("STRONG", new ElemDesc(8192));
    paramTrie.put("DFN", new ElemDesc(8192));
    paramTrie.put("CODE", new ElemDesc(8192));
    paramTrie.put("SAMP", new ElemDesc(8192));
    paramTrie.put("KBD", new ElemDesc(8192));
    paramTrie.put("VAR", new ElemDesc(8192));
    paramTrie.put("CITE", new ElemDesc(8192));
    paramTrie.put("ABBR", new ElemDesc(8192));
    paramTrie.put("ACRONYM", new ElemDesc(8192));
    paramTrie.put("SUP", new ElemDesc(98304));
    paramTrie.put("SUB", new ElemDesc(98304));
    paramTrie.put("SPAN", new ElemDesc(98304));
    paramTrie.put("BDO", new ElemDesc(98304));
    paramTrie.put("BR", new ElemDesc(98314));
    paramTrie.put("BODY", new ElemDesc(8));
    paramTrie.put("ADDRESS", new ElemDesc(56));
    paramTrie.put("DIV", new ElemDesc(56));
    paramTrie.put("A", new ElemDesc(32768));
    paramTrie.put("MAP", new ElemDesc(98312));
    paramTrie.put("AREA", new ElemDesc(10));
    paramTrie.put("LINK", new ElemDesc(131082));
    paramTrie.put("IMG", new ElemDesc(2195458));
    paramTrie.put("OBJECT", new ElemDesc(2326528));
    paramTrie.put("PARAM", new ElemDesc(2));
    paramTrie.put("HR", new ElemDesc(58));
    paramTrie.put("P", new ElemDesc(56));
    paramTrie.put("H1", new ElemDesc(262152));
    paramTrie.put("H2", new ElemDesc(262152));
    paramTrie.put("H3", new ElemDesc(262152));
    paramTrie.put("H4", new ElemDesc(262152));
    paramTrie.put("H5", new ElemDesc(262152));
    paramTrie.put("H6", new ElemDesc(262152));
    paramTrie.put("PRE", new ElemDesc(1048584));
    paramTrie.put("Q", new ElemDesc(98304));
    paramTrie.put("BLOCKQUOTE", new ElemDesc(56));
    paramTrie.put("INS", new ElemDesc(0));
    paramTrie.put("DEL", new ElemDesc(0));
    paramTrie.put("DL", new ElemDesc(56));
    paramTrie.put("DT", new ElemDesc(8));
    paramTrie.put("DD", new ElemDesc(8));
    paramTrie.put("OL", new ElemDesc(524296));
    paramTrie.put("UL", new ElemDesc(524296));
    paramTrie.put("LI", new ElemDesc(8));
    paramTrie.put("FORM", new ElemDesc(8));
    paramTrie.put("LABEL", new ElemDesc(16384));
    paramTrie.put("INPUT", new ElemDesc(18434));
    paramTrie.put("SELECT", new ElemDesc(18432));
    paramTrie.put("OPTGROUP", new ElemDesc(0));
    paramTrie.put("OPTION", new ElemDesc(0));
    paramTrie.put("TEXTAREA", new ElemDesc(18432));
    paramTrie.put("FIELDSET", new ElemDesc(24));
    paramTrie.put("LEGEND", new ElemDesc(0));
    paramTrie.put("BUTTON", new ElemDesc(18432));
    paramTrie.put("TABLE", new ElemDesc(56));
    paramTrie.put("CAPTION", new ElemDesc(8));
    paramTrie.put("THEAD", new ElemDesc(8));
    paramTrie.put("TFOOT", new ElemDesc(8));
    paramTrie.put("TBODY", new ElemDesc(8));
    paramTrie.put("COLGROUP", new ElemDesc(8));
    paramTrie.put("COL", new ElemDesc(10));
    paramTrie.put("TR", new ElemDesc(8));
    paramTrie.put("TH", new ElemDesc(0));
    paramTrie.put("TD", new ElemDesc(0));
    paramTrie.put("HEAD", new ElemDesc(4194312));
    paramTrie.put("TITLE", new ElemDesc(8));
    paramTrie.put("BASE", new ElemDesc(10));
    paramTrie.put("META", new ElemDesc(131082));
    paramTrie.put("STYLE", new ElemDesc(131336));
    paramTrie.put("SCRIPT", new ElemDesc(229632));
    paramTrie.put("NOSCRIPT", new ElemDesc(56));
    paramTrie.put("HTML", new ElemDesc(8));
    paramTrie.put("FONT", new ElemDesc(4096));
    paramTrie.put("S", new ElemDesc(4096));
    paramTrie.put("STRIKE", new ElemDesc(4096));
    paramTrie.put("U", new ElemDesc(4096));
    paramTrie.put("NOBR", new ElemDesc(4096));
    paramTrie.put("IFRAME", new ElemDesc(56));
    paramTrie.put("LAYER", new ElemDesc(56));
    paramTrie.put("ILAYER", new ElemDesc(56));
    ElemDesc localElemDesc = (ElemDesc)paramTrie.get("A");
    localElemDesc.setAttr("HREF", 2);
    localElemDesc.setAttr("NAME", 2);
    localElemDesc = (ElemDesc)paramTrie.get("AREA");
    localElemDesc.setAttr("HREF", 2);
    localElemDesc.setAttr("NOHREF", 4);
    localElemDesc = (ElemDesc)paramTrie.get("BASE");
    localElemDesc.setAttr("HREF", 2);
    localElemDesc = (ElemDesc)paramTrie.get("BUTTON");
    localElemDesc.setAttr("DISABLED", 4);
    localElemDesc = (ElemDesc)paramTrie.get("BLOCKQUOTE");
    localElemDesc.setAttr("CITE", 2);
    localElemDesc = (ElemDesc)paramTrie.get("DEL");
    localElemDesc.setAttr("CITE", 2);
    localElemDesc = (ElemDesc)paramTrie.get("DIR");
    localElemDesc.setAttr("COMPACT", 4);
    localElemDesc = (ElemDesc)paramTrie.get("DIV");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc.setAttr("NOWRAP", 4);
    localElemDesc = (ElemDesc)paramTrie.get("DL");
    localElemDesc.setAttr("COMPACT", 4);
    localElemDesc = (ElemDesc)paramTrie.get("FORM");
    localElemDesc.setAttr("ACTION", 2);
    localElemDesc = (ElemDesc)paramTrie.get("FRAME");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc.setAttr("LONGDESC", 2);
    localElemDesc.setAttr("NORESIZE", 4);
    localElemDesc = (ElemDesc)paramTrie.get("HEAD");
    localElemDesc.setAttr("PROFILE", 2);
    localElemDesc = (ElemDesc)paramTrie.get("HR");
    localElemDesc.setAttr("NOSHADE", 4);
    localElemDesc = (ElemDesc)paramTrie.get("IFRAME");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc.setAttr("LONGDESC", 2);
    localElemDesc = (ElemDesc)paramTrie.get("ILAYER");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc = (ElemDesc)paramTrie.get("IMG");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc.setAttr("LONGDESC", 2);
    localElemDesc.setAttr("USEMAP", 2);
    localElemDesc.setAttr("ISMAP", 4);
    localElemDesc = (ElemDesc)paramTrie.get("INPUT");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc.setAttr("USEMAP", 2);
    localElemDesc.setAttr("CHECKED", 4);
    localElemDesc.setAttr("DISABLED", 4);
    localElemDesc.setAttr("ISMAP", 4);
    localElemDesc.setAttr("READONLY", 4);
    localElemDesc = (ElemDesc)paramTrie.get("INS");
    localElemDesc.setAttr("CITE", 2);
    localElemDesc = (ElemDesc)paramTrie.get("LAYER");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc = (ElemDesc)paramTrie.get("LINK");
    localElemDesc.setAttr("HREF", 2);
    localElemDesc = (ElemDesc)paramTrie.get("MENU");
    localElemDesc.setAttr("COMPACT", 4);
    localElemDesc = (ElemDesc)paramTrie.get("OBJECT");
    localElemDesc.setAttr("CLASSID", 2);
    localElemDesc.setAttr("CODEBASE", 2);
    localElemDesc.setAttr("DATA", 2);
    localElemDesc.setAttr("ARCHIVE", 2);
    localElemDesc.setAttr("USEMAP", 2);
    localElemDesc.setAttr("DECLARE", 4);
    localElemDesc = (ElemDesc)paramTrie.get("OL");
    localElemDesc.setAttr("COMPACT", 4);
    localElemDesc = (ElemDesc)paramTrie.get("OPTGROUP");
    localElemDesc.setAttr("DISABLED", 4);
    localElemDesc = (ElemDesc)paramTrie.get("OPTION");
    localElemDesc.setAttr("SELECTED", 4);
    localElemDesc.setAttr("DISABLED", 4);
    localElemDesc = (ElemDesc)paramTrie.get("Q");
    localElemDesc.setAttr("CITE", 2);
    localElemDesc = (ElemDesc)paramTrie.get("SCRIPT");
    localElemDesc.setAttr("SRC", 2);
    localElemDesc.setAttr("FOR", 2);
    localElemDesc.setAttr("DEFER", 4);
    localElemDesc = (ElemDesc)paramTrie.get("SELECT");
    localElemDesc.setAttr("DISABLED", 4);
    localElemDesc.setAttr("MULTIPLE", 4);
    localElemDesc = (ElemDesc)paramTrie.get("TABLE");
    localElemDesc.setAttr("NOWRAP", 4);
    localElemDesc = (ElemDesc)paramTrie.get("TD");
    localElemDesc.setAttr("NOWRAP", 4);
    localElemDesc = (ElemDesc)paramTrie.get("TEXTAREA");
    localElemDesc.setAttr("DISABLED", 4);
    localElemDesc.setAttr("READONLY", 4);
    localElemDesc = (ElemDesc)paramTrie.get("TH");
    localElemDesc.setAttr("NOWRAP", 4);
    localElemDesc = (ElemDesc)paramTrie.get("TR");
    localElemDesc.setAttr("NOWRAP", 4);
    localElemDesc = (ElemDesc)paramTrie.get("UL");
    localElemDesc.setAttr("COMPACT", 4);
  }
  
  public void setSpecialEscapeURLs(boolean paramBoolean)
  {
    this.m_specialEscapeURLs = paramBoolean;
  }
  
  public void setOmitMetaTag(boolean paramBoolean)
  {
    this.m_omitMetaTag = paramBoolean;
  }
  
  public void setOutputFormat(Properties paramProperties)
  {
    this.m_specialEscapeURLs = OutputPropertyUtils.getBooleanProperty("{http://xml.apache.org/xalan}use-url-escaping", paramProperties);
    this.m_omitMetaTag = OutputPropertyUtils.getBooleanProperty("{http://xml.apache.org/xalan}omit-meta-tag", paramProperties);
    super.setOutputFormat(paramProperties);
  }
  
  private final boolean getSpecialEscapeURLs()
  {
    return this.m_specialEscapeURLs;
  }
  
  private final boolean getOmitMetaTag()
  {
    return this.m_omitMetaTag;
  }
  
  public static final ElemDesc getElemDesc(String paramString)
  {
    Object localObject = m_elementFlags.get(paramString);
    if (null != localObject) {
      return (ElemDesc)localObject;
    }
    return m_dummy;
  }
  
  private ElemDesc getElemDesc2(String paramString)
  {
    Object localObject = this.m_htmlInfo.get2(paramString);
    if (null != localObject) {
      return (ElemDesc)localObject;
    }
    return m_dummy;
  }
  
  public ToHTMLStream()
  {
    this.m_charInfo = m_htmlcharInfo;
    this.m_prefixMap = new NamespaceMappings();
  }
  
  protected void startDocumentInternal()
    throws SAXException
  {
    super.startDocumentInternal();
    this.m_needToCallStartDocument = false;
    this.m_needToOutputDocTypeDecl = true;
    this.m_startNewLine = false;
    setOmitXMLDeclaration(true);
    if (true == this.m_needToOutputDocTypeDecl)
    {
      String str1 = getDoctypeSystem();
      String str2 = getDoctypePublic();
      if ((null != str1) || (null != str2))
      {
        Writer localWriter = this.m_writer;
        try
        {
          localWriter.write("<!DOCTYPE html");
          if (null != str2)
          {
            localWriter.write(" PUBLIC \"");
            localWriter.write(str2);
            localWriter.write(34);
          }
          if (null != str1)
          {
            if (null == str2) {
              localWriter.write(" SYSTEM \"");
            } else {
              localWriter.write(" \"");
            }
            localWriter.write(str1);
            localWriter.write(34);
          }
          localWriter.write(62);
          outputLineSep();
        }
        catch (IOException localIOException)
        {
          throw new SAXException(localIOException);
        }
      }
    }
    this.m_needToOutputDocTypeDecl = false;
  }
  
  public final void endDocument()
    throws SAXException
  {
    flushPending();
    if ((this.m_doIndent) && (!this.m_isprevtext)) {
      try
      {
        outputLineSep();
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
    }
    flushWriter();
    if (this.m_tracer != null) {
      super.fireEndDoc();
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    ElemContext localElemContext = this.m_elemContext;
    if (localElemContext.m_startTagOpen)
    {
      closeStartTag();
      localElemContext.m_startTagOpen = false;
    }
    else if (this.m_cdataTagOpen)
    {
      closeCDATA();
      this.m_cdataTagOpen = false;
    }
    else if (this.m_needToCallStartDocument)
    {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    }
    if ((null != paramString1) && (paramString1.length() > 0))
    {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
      return;
    }
    try
    {
      ElemDesc localElemDesc = getElemDesc2(paramString3);
      int i = localElemDesc.getFlags();
      if (this.m_doIndent)
      {
        int j = (i & 0x8) != 0 ? 1 : 0;
        if (this.m_ispreserve)
        {
          this.m_ispreserve = false;
        }
        else if ((null != localElemContext.m_elementName) && ((!this.m_inBlockElem) || (j != 0)))
        {
          this.m_startNewLine = true;
          indent();
        }
        this.m_inBlockElem = (j == 0);
      }
      if (paramAttributes != null) {
        addAttributes(paramAttributes);
      }
      this.m_isprevtext = false;
      Writer localWriter = this.m_writer;
      localWriter.write(60);
      localWriter.write(paramString3);
      if (this.m_tracer != null) {
        firePseudoAttributes();
      }
      if ((i & 0x2) != 0)
      {
        this.m_elemContext = localElemContext.push();
        this.m_elemContext.m_elementName = paramString3;
        this.m_elemContext.m_elementDesc = localElemDesc;
        return;
      }
      localElemContext = localElemContext.push(paramString1, paramString2, paramString3);
      this.m_elemContext = localElemContext;
      localElemContext.m_elementDesc = localElemDesc;
      localElemContext.m_isRaw = ((i & 0x100) != 0);
      if ((i & 0x400000) != 0)
      {
        closeStartTag();
        localElemContext.m_startTagOpen = false;
        if (!this.m_omitMetaTag)
        {
          if (this.m_doIndent) {
            indent();
          }
          localWriter.write("<META http-equiv=\"Content-Type\" content=\"text/html; charset=");
          String str1 = getEncoding();
          String str2 = Encodings.getMimeEncoding(str1);
          localWriter.write(str2);
          localWriter.write("\">");
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public final void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (this.m_cdataTagOpen) {
      closeCDATA();
    }
    if ((null != paramString1) && (paramString1.length() > 0))
    {
      super.endElement(paramString1, paramString2, paramString3);
      return;
    }
    try
    {
      ElemContext localElemContext = this.m_elemContext;
      ElemDesc localElemDesc = localElemContext.m_elementDesc;
      int i = localElemDesc.getFlags();
      int j = (i & 0x2) != 0 ? 1 : 0;
      int m;
      if (this.m_doIndent)
      {
        int k = (i & 0x8) != 0 ? 1 : 0;
        m = 0;
        if (this.m_ispreserve)
        {
          this.m_ispreserve = false;
        }
        else if ((this.m_doIndent) && ((!this.m_inBlockElem) || (k != 0)))
        {
          this.m_startNewLine = true;
          m = 1;
        }
        if ((!localElemContext.m_startTagOpen) && (m != 0)) {
          indent(localElemContext.m_currentElemDepth - 1);
        }
        this.m_inBlockElem = (k == 0);
      }
      Writer localWriter = this.m_writer;
      if (!localElemContext.m_startTagOpen)
      {
        localWriter.write("</");
        localWriter.write(paramString3);
        localWriter.write(62);
      }
      else
      {
        if (this.m_tracer != null) {
          super.fireStartElem(paramString3);
        }
        m = this.m_attributes.getLength();
        if (m > 0)
        {
          processAttributes(this.m_writer, m);
          this.m_attributes.clear();
        }
        if (j == 0)
        {
          localWriter.write("></");
          localWriter.write(paramString3);
          localWriter.write(62);
        }
        else
        {
          localWriter.write(62);
        }
      }
      if ((i & 0x200000) != 0) {
        this.m_ispreserve = true;
      }
      this.m_isprevtext = false;
      if (this.m_tracer != null) {
        super.fireEndElem(paramString3);
      }
      if (j != 0)
      {
        this.m_elemContext = localElemContext.m_prev;
        return;
      }
      if ((!localElemContext.m_startTagOpen) && (this.m_doIndent) && (!this.m_preserves.isEmpty())) {
        this.m_preserves.pop();
      }
      this.m_elemContext = localElemContext.m_prev;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  protected void processAttribute(Writer paramWriter, String paramString1, String paramString2, ElemDesc paramElemDesc)
    throws IOException
  {
    paramWriter.write(32);
    if (((paramString2.length() == 0) || (paramString2.equalsIgnoreCase(paramString1))) && (paramElemDesc != null) && (paramElemDesc.isAttrFlagSet(paramString1, 4)))
    {
      paramWriter.write(paramString1);
    }
    else
    {
      paramWriter.write(paramString1);
      paramWriter.write("=\"");
      if ((paramElemDesc != null) && (paramElemDesc.isAttrFlagSet(paramString1, 2))) {
        writeAttrURI(paramWriter, paramString2, this.m_specialEscapeURLs);
      } else {
        writeAttrString(paramWriter, paramString2, getEncoding());
      }
      paramWriter.write(34);
    }
  }
  
  private boolean isASCIIDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  private static String makeHHString(int paramInt)
  {
    String str = Integer.toHexString(paramInt).toUpperCase();
    if (str.length() == 1) {
      str = "0" + str;
    }
    return str;
  }
  
  private boolean isHHSign(String paramString)
  {
    boolean bool = true;
    try
    {
      int i = (char)Integer.parseInt(paramString, 16);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      bool = false;
    }
    return bool;
  }
  
  public void writeAttrURI(Writer paramWriter, String paramString, boolean paramBoolean)
    throws IOException
  {
    int i = paramString.length();
    if (i > this.m_attrBuff.length) {
      this.m_attrBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, this.m_attrBuff, 0);
    char[] arrayOfChar = this.m_attrBuff;
    int j = 0;
    int k = 0;
    char c = '\000';
    for (int m = 0; m < i; m++)
    {
      c = arrayOfChar[m];
      if ((c < ' ') || (c > '~'))
      {
        if (k > 0)
        {
          paramWriter.write(arrayOfChar, j, k);
          k = 0;
        }
        if (paramBoolean)
        {
          if (c <= '')
          {
            paramWriter.write(37);
            paramWriter.write(makeHHString(c));
          }
          else
          {
            int n;
            int i1;
            if (c <= '߿')
            {
              n = c >> '\006' | 0xC0;
              i1 = c & 0x3F | 0x80;
              paramWriter.write(37);
              paramWriter.write(makeHHString(n));
              paramWriter.write(37);
              paramWriter.write(makeHHString(i1));
            }
            else
            {
              int i2;
              if (Encodings.isHighUTF16Surrogate(c))
              {
                n = c & 0x3FF;
                i1 = (n & 0x3C0) >> 6;
                i2 = i1 + 1;
                int i3 = (n & 0x3C) >> 2;
                int i4 = (n & 0x3) << 4 & 0x30;
                c = arrayOfChar[(++m)];
                int i5 = c & 0x3FF;
                i4 |= (i5 & 0x3C0) >> 6;
                int i6 = i5 & 0x3F;
                int i7 = 0xF0 | i2 >> 2;
                int i8 = 0x80 | (i2 & 0x3) << 4 & 0x30 | i3;
                int i9 = 0x80 | i4;
                int i10 = 0x80 | i6;
                paramWriter.write(37);
                paramWriter.write(makeHHString(i7));
                paramWriter.write(37);
                paramWriter.write(makeHHString(i8));
                paramWriter.write(37);
                paramWriter.write(makeHHString(i9));
                paramWriter.write(37);
                paramWriter.write(makeHHString(i10));
              }
              else
              {
                n = c >> '\f' | 0xE0;
                i1 = (c & 0xFC0) >> '\006' | 0x80;
                i2 = c & 0x3F | 0x80;
                paramWriter.write(37);
                paramWriter.write(makeHHString(n));
                paramWriter.write(37);
                paramWriter.write(makeHHString(i1));
                paramWriter.write(37);
                paramWriter.write(makeHHString(i2));
              }
            }
          }
        }
        else if (escapingNotNeeded(c))
        {
          paramWriter.write(c);
        }
        else
        {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(c));
          paramWriter.write(59);
        }
        j = m + 1;
      }
      else if (c == '"')
      {
        if (k > 0)
        {
          paramWriter.write(arrayOfChar, j, k);
          k = 0;
        }
        if (paramBoolean) {
          paramWriter.write("%22");
        } else {
          paramWriter.write("&quot;");
        }
        j = m + 1;
      }
      else if (c == '&')
      {
        if (k > 0)
        {
          paramWriter.write(arrayOfChar, j, k);
          k = 0;
        }
        paramWriter.write("&amp;");
        j = m + 1;
      }
      else
      {
        k++;
      }
    }
    if (k > 1)
    {
      if (j == 0) {
        paramWriter.write(paramString);
      } else {
        paramWriter.write(arrayOfChar, j, k);
      }
    }
    else if (k == 1) {
      paramWriter.write(c);
    }
  }
  
  public void writeAttrString(Writer paramWriter, String paramString1, String paramString2)
    throws IOException
  {
    int i = paramString1.length();
    if (i > this.m_attrBuff.length) {
      this.m_attrBuff = new char[i * 2 + 1];
    }
    paramString1.getChars(0, i, this.m_attrBuff, 0);
    char[] arrayOfChar = this.m_attrBuff;
    int j = 0;
    int k = 0;
    char c = '\000';
    for (int m = 0; m < i; m++)
    {
      c = arrayOfChar[m];
      if ((escapingNotNeeded(c)) && (!this.m_charInfo.isSpecialAttrChar(c)))
      {
        k++;
      }
      else if (('<' == c) || ('>' == c))
      {
        k++;
      }
      else if (('&' == c) && (m + 1 < i) && ('{' == arrayOfChar[(m + 1)]))
      {
        k++;
      }
      else
      {
        if (k > 0)
        {
          paramWriter.write(arrayOfChar, j, k);
          k = 0;
        }
        int n = accumDefaultEntity(paramWriter, c, m, arrayOfChar, i, false, true);
        if (m != n)
        {
          m = n - 1;
        }
        else
        {
          if (Encodings.isHighUTF16Surrogate(c))
          {
            writeUTF16Surrogate(c, arrayOfChar, m, i);
            m++;
          }
          String str = this.m_charInfo.getOutputStringForChar(c);
          if (null != str)
          {
            paramWriter.write(str);
          }
          else if (escapingNotNeeded(c))
          {
            paramWriter.write(c);
          }
          else
          {
            paramWriter.write("&#");
            paramWriter.write(Integer.toString(c));
            paramWriter.write(59);
          }
        }
        j = m + 1;
      }
    }
    if (k > 1)
    {
      if (j == 0) {
        paramWriter.write(paramString1);
      } else {
        paramWriter.write(arrayOfChar, j, k);
      }
    }
    else if (k == 1) {
      paramWriter.write(c);
    }
  }
  
  public final void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.m_elemContext.m_isRaw) {
      try
      {
        if (this.m_elemContext.m_startTagOpen)
        {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        }
        this.m_ispreserve = true;
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, false, this.m_lineSepUse);
        if (this.m_tracer != null) {
          super.fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
        }
        return;
      }
      catch (IOException localIOException)
      {
        throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), localIOException);
      }
    }
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void cdata(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if ((null != this.m_elemContext.m_elementName) && ((this.m_elemContext.m_elementName.equalsIgnoreCase("SCRIPT")) || (this.m_elemContext.m_elementName.equalsIgnoreCase("STYLE")))) {
      try
      {
        if (this.m_elemContext.m_startTagOpen)
        {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        }
        this.m_ispreserve = true;
        if (shouldIndent()) {
          indent();
        }
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, true, this.m_lineSepUse);
      }
      catch (IOException localIOException)
      {
        throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), localIOException);
      }
    } else {
      super.cdata(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    flushPending();
    if (paramString1.equals("javax.xml.transform.disable-output-escaping")) {
      startNonEscaping();
    } else if (paramString1.equals("javax.xml.transform.enable-output-escaping")) {
      endNonEscaping();
    } else {
      try
      {
        if (this.m_elemContext.m_startTagOpen)
        {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        }
        else if (this.m_needToCallStartDocument)
        {
          startDocumentInternal();
        }
        if (shouldIndent()) {
          indent();
        }
        Writer localWriter = this.m_writer;
        localWriter.write("<?");
        localWriter.write(paramString1);
        if ((paramString2.length() > 0) && (!Character.isSpaceChar(paramString2.charAt(0)))) {
          localWriter.write(32);
        }
        localWriter.write(paramString2);
        localWriter.write(62);
        if (this.m_elemContext.m_currentElemDepth <= 0) {
          outputLineSep();
        }
        this.m_startNewLine = true;
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
    }
    if (this.m_tracer != null) {
      super.fireEscapingEvent(paramString1, paramString2);
    }
  }
  
  public final void entityReference(String paramString)
    throws SAXException
  {
    try
    {
      Writer localWriter = this.m_writer;
      localWriter.write(38);
      localWriter.write(paramString);
      localWriter.write(59);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public final void endElement(String paramString)
    throws SAXException
  {
    endElement(null, null, paramString);
  }
  
  public void processAttributes(Writer paramWriter, int paramInt)
    throws IOException, SAXException
  {
    for (int i = 0; i < paramInt; i++) {
      processAttribute(paramWriter, this.m_attributes.getQName(i), this.m_attributes.getValue(i), this.m_elemContext.m_elementDesc);
    }
  }
  
  protected void closeStartTag()
    throws SAXException
  {
    try
    {
      if (this.m_tracer != null) {
        super.fireStartElem(this.m_elemContext.m_elementName);
      }
      int i = this.m_attributes.getLength();
      if (i > 0)
      {
        processAttributes(this.m_writer, i);
        this.m_attributes.clear();
      }
      this.m_writer.write(62);
      if (this.m_cdataSectionElements != null) {
        this.m_elemContext.m_isCdataSection = isCdataSection();
      }
      if (this.m_doIndent)
      {
        this.m_isprevtext = false;
        this.m_preserves.push(this.m_ispreserve);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  protected synchronized void init(OutputStream paramOutputStream, Properties paramProperties)
    throws UnsupportedEncodingException
  {
    if (null == paramProperties) {
      paramProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
    }
    super.init(paramOutputStream, paramProperties, false);
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    try
    {
      Properties localProperties;
      if (null == this.m_format) {
        localProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
      } else {
        localProperties = this.m_format;
      }
      init(paramOutputStream, localProperties, true);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {
    if (this.m_elemContext.m_elementURI == null)
    {
      String str = getPrefixPart(this.m_elemContext.m_elementName);
      if ((str == null) && ("".equals(paramString1))) {
        this.m_elemContext.m_elementURI = paramString2;
      }
    }
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    this.m_inDTD = true;
    super.startDTD(paramString1, paramString2, paramString3);
  }
  
  public void endDTD()
    throws SAXException
  {
    this.m_inDTD = false;
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {}
  
  public void elementDecl(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void internalEntityDecl(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt)
    throws SAXException
  {
    try
    {
      Writer localWriter = this.m_writer;
      if (((paramInt & 0x1) > 0) && (m_htmlcharInfo.onlyQuotAmpLtGt))
      {
        localWriter.write(32);
        localWriter.write(paramString1);
        localWriter.write("=\"");
        localWriter.write(paramString2);
        localWriter.write(34);
      }
      else if (((paramInt & 0x2) > 0) && ((paramString2.length() == 0) || (paramString2.equalsIgnoreCase(paramString1))))
      {
        localWriter.write(32);
        localWriter.write(paramString1);
      }
      else
      {
        localWriter.write(32);
        localWriter.write(paramString1);
        localWriter.write("=\"");
        if ((paramInt & 0x4) > 0) {
          writeAttrURI(localWriter, paramString2, this.m_specialEscapeURLs);
        } else {
          writeAttrString(localWriter, paramString2, getEncoding());
        }
        localWriter.write(34);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.m_inDTD) {
      return;
    }
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public boolean reset()
  {
    boolean bool = super.reset();
    if (!bool) {
      return false;
    }
    initToHTMLStream();
    return true;
  }
  
  private void initToHTMLStream()
  {
    this.m_inBlockElem = false;
    this.m_inDTD = false;
    this.m_omitMetaTag = false;
    this.m_specialEscapeURLs = true;
  }
  
  static
  {
    initTagReference(m_elementFlags);
  }
  
  static class Trie
  {
    public static final int ALPHA_SIZE = 128;
    final Node m_Root;
    private char[] m_charBuffer = new char[0];
    private final boolean m_lowerCaseOnly;
    
    public Trie()
    {
      this.m_Root = new Node();
      this.m_lowerCaseOnly = false;
    }
    
    public Trie(boolean paramBoolean)
    {
      this.m_Root = new Node();
      this.m_lowerCaseOnly = paramBoolean;
    }
    
    public Object put(String paramString, Object paramObject)
    {
      int i = paramString.length();
      if (i > this.m_charBuffer.length) {
        this.m_charBuffer = new char[i];
      }
      Object localObject1 = this.m_Root;
      for (int j = 0; j < i; j++)
      {
        Node localNode1 = localObject1.m_nextChar[Character.toLowerCase(paramString.charAt(j))];
        if (localNode1 != null) {
          localObject1 = localNode1;
        } else {
          while (j < i)
          {
            Node localNode2 = new Node();
            if (this.m_lowerCaseOnly)
            {
              ((Node)localObject1).m_nextChar[Character.toLowerCase(paramString.charAt(j))] = localNode2;
            }
            else
            {
              ((Node)localObject1).m_nextChar[Character.toUpperCase(paramString.charAt(j))] = localNode2;
              ((Node)localObject1).m_nextChar[Character.toLowerCase(paramString.charAt(j))] = localNode2;
            }
            localObject1 = localNode2;
            j++;
          }
        }
      }
      Object localObject2 = ((Node)localObject1).m_Value;
      ((Node)localObject1).m_Value = paramObject;
      return localObject2;
    }
    
    public Object get(String paramString)
    {
      int i = paramString.length();
      if (this.m_charBuffer.length < i) {
        return null;
      }
      Node localNode = this.m_Root;
      switch (i)
      {
      case 0: 
        return null;
      case 1: 
        j = paramString.charAt(0);
        if (j < 128)
        {
          localNode = localNode.m_nextChar[j];
          if (localNode != null) {
            return localNode.m_Value;
          }
        }
        return null;
      }
      for (int j = 0; j < i; j++)
      {
        int k = paramString.charAt(j);
        if (128 <= k) {
          return null;
        }
        localNode = localNode.m_nextChar[k];
        if (localNode == null) {
          return null;
        }
      }
      return localNode.m_Value;
    }
    
    public Trie(Trie paramTrie)
    {
      this.m_Root = paramTrie.m_Root;
      this.m_lowerCaseOnly = paramTrie.m_lowerCaseOnly;
      int i = paramTrie.getLongestKeyLength();
      this.m_charBuffer = new char[i];
    }
    
    public Object get2(String paramString)
    {
      int i = paramString.length();
      if (this.m_charBuffer.length < i) {
        return null;
      }
      Node localNode = this.m_Root;
      switch (i)
      {
      case 0: 
        return null;
      case 1: 
        j = paramString.charAt(0);
        if (j < 128)
        {
          localNode = localNode.m_nextChar[j];
          if (localNode != null) {
            return localNode.m_Value;
          }
        }
        return null;
      }
      paramString.getChars(0, i, this.m_charBuffer, 0);
      for (int j = 0; j < i; j++)
      {
        int k = this.m_charBuffer[j];
        if (128 <= k) {
          return null;
        }
        localNode = localNode.m_nextChar[k];
        if (localNode == null) {
          return null;
        }
      }
      return localNode.m_Value;
    }
    
    public int getLongestKeyLength()
    {
      return this.m_charBuffer.length;
    }
    
    private class Node
    {
      final Node[] m_nextChar = new Node[''];
      Object m_Value = null;
      
      Node() {}
    }
  }
}
