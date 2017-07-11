package javax.swing.text.rtf;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabStop;

class RTFReader
  extends RTFParser
{
  StyledDocument target;
  Dictionary<Object, Object> parserState;
  Destination rtfDestination;
  MutableAttributeSet documentAttributes;
  Dictionary<Integer, String> fontTable;
  Color[] colorTable;
  Style[] characterStyles;
  Style[] paragraphStyles;
  Style[] sectionStyles;
  int rtfversion;
  boolean ignoreGroupIfUnknownKeyword;
  int skippingCharacters;
  private static Dictionary<String, RTFAttribute> straightforwardAttributes = ;
  private MockAttributeSet mockery;
  static Dictionary<String, String> textKeywords = null;
  static final String TabAlignmentKey = "tab_alignment";
  static final String TabLeaderKey = "tab_leader";
  static Dictionary<String, char[]> characterSets = new Hashtable();
  static boolean useNeXTForAnsi;
  
  public RTFReader(StyledDocument paramStyledDocument)
  {
    this.target = paramStyledDocument;
    this.parserState = new Hashtable();
    this.fontTable = new Hashtable();
    this.rtfversion = -1;
    this.mockery = new MockAttributeSet();
    this.documentAttributes = new SimpleAttributeSet();
  }
  
  public void handleBinaryBlob(byte[] paramArrayOfByte)
  {
    if (this.skippingCharacters > 0)
    {
      this.skippingCharacters -= 1;
      return;
    }
  }
  
  public void handleText(String paramString)
  {
    if (this.skippingCharacters > 0)
    {
      if (this.skippingCharacters >= paramString.length())
      {
        this.skippingCharacters -= paramString.length();
        return;
      }
      paramString = paramString.substring(this.skippingCharacters);
      this.skippingCharacters = 0;
    }
    if (this.rtfDestination != null)
    {
      this.rtfDestination.handleText(paramString);
      return;
    }
    warning("Text with no destination. oops.");
  }
  
  Color defaultColor()
  {
    return Color.black;
  }
  
  public void begingroup()
  {
    if (this.skippingCharacters > 0) {
      this.skippingCharacters = 0;
    }
    Object localObject = this.parserState.get("_savedState");
    if (localObject != null) {
      this.parserState.remove("_savedState");
    }
    Dictionary localDictionary = (Dictionary)((Hashtable)this.parserState).clone();
    if (localObject != null) {
      localDictionary.put("_savedState", localObject);
    }
    this.parserState.put("_savedState", localDictionary);
    if (this.rtfDestination != null) {
      this.rtfDestination.begingroup();
    }
  }
  
  public void endgroup()
  {
    if (this.skippingCharacters > 0) {
      this.skippingCharacters = 0;
    }
    Dictionary localDictionary1 = (Dictionary)this.parserState.get("_savedState");
    Destination localDestination = (Destination)localDictionary1.get("dst");
    if (localDestination != this.rtfDestination)
    {
      this.rtfDestination.close();
      this.rtfDestination = localDestination;
    }
    Dictionary localDictionary2 = this.parserState;
    this.parserState = localDictionary1;
    if (this.rtfDestination != null) {
      this.rtfDestination.endgroup(localDictionary2);
    }
  }
  
  protected void setRTFDestination(Destination paramDestination)
  {
    Dictionary localDictionary = (Dictionary)this.parserState.get("_savedState");
    if ((localDictionary != null) && (this.rtfDestination != localDictionary.get("dst")))
    {
      warning("Warning, RTF destination overridden, invalid RTF.");
      this.rtfDestination.close();
    }
    this.rtfDestination = paramDestination;
    this.parserState.put("dst", this.rtfDestination);
  }
  
  public void close()
    throws IOException
  {
    Enumeration localEnumeration = this.documentAttributes.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      this.target.putProperty(localObject, this.documentAttributes.getAttribute(localObject));
    }
    warning("RTF filter done.");
    super.close();
  }
  
  public boolean handleKeyword(String paramString)
  {
    boolean bool = this.ignoreGroupIfUnknownKeyword;
    if (this.skippingCharacters > 0)
    {
      this.skippingCharacters -= 1;
      return true;
    }
    this.ignoreGroupIfUnknownKeyword = false;
    String str;
    if ((str = (String)textKeywords.get(paramString)) != null)
    {
      handleText(str);
      return true;
    }
    if (paramString.equals("fonttbl"))
    {
      setRTFDestination(new FonttblDestination());
      return true;
    }
    if (paramString.equals("colortbl"))
    {
      setRTFDestination(new ColortblDestination());
      return true;
    }
    if (paramString.equals("stylesheet"))
    {
      setRTFDestination(new StylesheetDestination());
      return true;
    }
    if (paramString.equals("info"))
    {
      setRTFDestination(new InfoDestination());
      return false;
    }
    if (paramString.equals("mac"))
    {
      setCharacterSet("mac");
      return true;
    }
    if (paramString.equals("ansi"))
    {
      if (useNeXTForAnsi) {
        setCharacterSet("NeXT");
      } else {
        setCharacterSet("ansi");
      }
      return true;
    }
    if (paramString.equals("next"))
    {
      setCharacterSet("NeXT");
      return true;
    }
    if (paramString.equals("pc"))
    {
      setCharacterSet("cpg437");
      return true;
    }
    if (paramString.equals("pca"))
    {
      setCharacterSet("cpg850");
      return true;
    }
    if (paramString.equals("*"))
    {
      this.ignoreGroupIfUnknownKeyword = true;
      return true;
    }
    if ((this.rtfDestination != null) && (this.rtfDestination.handleKeyword(paramString))) {
      return true;
    }
    if ((paramString.equals("aftncn")) || (paramString.equals("aftnsep")) || (paramString.equals("aftnsepc")) || (paramString.equals("annotation")) || (paramString.equals("atnauthor")) || (paramString.equals("atnicn")) || (paramString.equals("atnid")) || (paramString.equals("atnref")) || (paramString.equals("atntime")) || (paramString.equals("atrfend")) || (paramString.equals("atrfstart")) || (paramString.equals("bkmkend")) || (paramString.equals("bkmkstart")) || (paramString.equals("datafield")) || (paramString.equals("do")) || (paramString.equals("dptxbxtext")) || (paramString.equals("falt")) || (paramString.equals("field")) || (paramString.equals("file")) || (paramString.equals("filetbl")) || (paramString.equals("fname")) || (paramString.equals("fontemb")) || (paramString.equals("fontfile")) || (paramString.equals("footer")) || (paramString.equals("footerf")) || (paramString.equals("footerl")) || (paramString.equals("footerr")) || (paramString.equals("footnote")) || (paramString.equals("ftncn")) || (paramString.equals("ftnsep")) || (paramString.equals("ftnsepc")) || (paramString.equals("header")) || (paramString.equals("headerf")) || (paramString.equals("headerl")) || (paramString.equals("headerr")) || (paramString.equals("keycode")) || (paramString.equals("nextfile")) || (paramString.equals("object")) || (paramString.equals("pict")) || (paramString.equals("pn")) || (paramString.equals("pnseclvl")) || (paramString.equals("pntxtb")) || (paramString.equals("pntxta")) || (paramString.equals("revtbl")) || (paramString.equals("rxe")) || (paramString.equals("tc")) || (paramString.equals("template")) || (paramString.equals("txe")) || (paramString.equals("xe"))) {
      bool = true;
    }
    if (bool) {
      setRTFDestination(new DiscardingDestination());
    }
    return false;
  }
  
  public boolean handleKeyword(String paramString, int paramInt)
  {
    boolean bool = this.ignoreGroupIfUnknownKeyword;
    if (this.skippingCharacters > 0)
    {
      this.skippingCharacters -= 1;
      return true;
    }
    this.ignoreGroupIfUnknownKeyword = false;
    if (paramString.equals("uc"))
    {
      this.parserState.put("UnicodeSkip", Integer.valueOf(paramInt));
      return true;
    }
    if (paramString.equals("u"))
    {
      if (paramInt < 0) {
        paramInt += 65536;
      }
      handleText((char)paramInt);
      Number localNumber = (Number)this.parserState.get("UnicodeSkip");
      if (localNumber != null) {
        this.skippingCharacters = localNumber.intValue();
      } else {
        this.skippingCharacters = 1;
      }
      return true;
    }
    if (paramString.equals("rtf"))
    {
      this.rtfversion = paramInt;
      setRTFDestination(new DocumentDestination());
      return true;
    }
    if ((paramString.startsWith("NeXT")) || (paramString.equals("private"))) {
      bool = true;
    }
    if ((this.rtfDestination != null) && (this.rtfDestination.handleKeyword(paramString, paramInt))) {
      return true;
    }
    if (bool) {
      setRTFDestination(new DiscardingDestination());
    }
    return false;
  }
  
  private void setTargetAttribute(String paramString, Object paramObject) {}
  
  public void setCharacterSet(String paramString)
  {
    Object localObject;
    try
    {
      localObject = getCharacterSet(paramString);
    }
    catch (Exception localException)
    {
      warning("Exception loading RTF character set \"" + paramString + "\": " + localException);
      localObject = null;
    }
    if (localObject != null)
    {
      this.translationTable = ((char[])localObject);
    }
    else
    {
      warning("Unknown RTF character set \"" + paramString + "\"");
      if (!paramString.equals("ansi")) {
        try
        {
          this.translationTable = ((char[])getCharacterSet("ansi"));
        }
        catch (IOException localIOException)
        {
          throw new InternalError("RTFReader: Unable to find character set resources (" + localIOException + ")", localIOException);
        }
      }
    }
    setTargetAttribute("rtfCharacterSet", paramString);
  }
  
  public static void defineCharacterSet(String paramString, char[] paramArrayOfChar)
  {
    if (paramArrayOfChar.length < 256) {
      throw new IllegalArgumentException("Translation table must have 256 entries.");
    }
    characterSets.put(paramString, paramArrayOfChar);
  }
  
  public static Object getCharacterSet(String paramString)
    throws IOException
  {
    char[] arrayOfChar = (char[])characterSets.get(paramString);
    if (arrayOfChar == null)
    {
      InputStream localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
      {
        public InputStream run()
        {
          return RTFReader.class.getResourceAsStream("charsets/" + this.val$name + ".txt");
        }
      });
      arrayOfChar = readCharset(localInputStream);
      defineCharacterSet(paramString, arrayOfChar);
    }
    return arrayOfChar;
  }
  
  static char[] readCharset(InputStream paramInputStream)
    throws IOException
  {
    char[] arrayOfChar = new char['Ā'];
    StreamTokenizer localStreamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(paramInputStream, "ISO-8859-1")));
    localStreamTokenizer.eolIsSignificant(false);
    localStreamTokenizer.commentChar(35);
    localStreamTokenizer.slashSlashComments(true);
    localStreamTokenizer.slashStarComments(true);
    for (int i = 0; i < 256; i++)
    {
      int j;
      try
      {
        j = localStreamTokenizer.nextToken();
      }
      catch (Exception localException)
      {
        throw new IOException("Unable to read from character set file (" + localException + ")");
      }
      if (j != -2) {
        throw new IOException("Unexpected token in character set file");
      }
      arrayOfChar[i] = ((char)(int)localStreamTokenizer.nval);
    }
    return arrayOfChar;
  }
  
  static char[] readCharset(URL paramURL)
    throws IOException
  {
    return readCharset(paramURL.openStream());
  }
  
  static
  {
    textKeywords = new Hashtable();
    textKeywords.put("\\", "\\");
    textKeywords.put("{", "{");
    textKeywords.put("}", "}");
    textKeywords.put(" ", " ");
    textKeywords.put("~", " ");
    textKeywords.put("_", "‑");
    textKeywords.put("bullet", "•");
    textKeywords.put("emdash", "—");
    textKeywords.put("emspace", " ");
    textKeywords.put("endash", "–");
    textKeywords.put("enspace", " ");
    textKeywords.put("ldblquote", "“");
    textKeywords.put("lquote", "‘");
    textKeywords.put("ltrmark", "‎");
    textKeywords.put("rdblquote", "”");
    textKeywords.put("rquote", "’");
    textKeywords.put("rtlmark", "‏");
    textKeywords.put("tab", "\t");
    textKeywords.put("zwj", "‍");
    textKeywords.put("zwnj", "‌");
    textKeywords.put("-", "‧");
    useNeXTForAnsi = false;
  }
  
  abstract class AttributeTrackingDestination
    implements RTFReader.Destination
  {
    MutableAttributeSet characterAttributes = rootCharacterAttributes();
    MutableAttributeSet paragraphAttributes;
    MutableAttributeSet sectionAttributes;
    
    public AttributeTrackingDestination()
    {
      RTFReader.this.parserState.put("chr", this.characterAttributes);
      this.paragraphAttributes = rootParagraphAttributes();
      RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
      this.sectionAttributes = rootSectionAttributes();
      RTFReader.this.parserState.put("sec", this.sectionAttributes);
    }
    
    public abstract void handleText(String paramString);
    
    public void handleBinaryBlob(byte[] paramArrayOfByte)
    {
      RTFReader.this.warning("Unexpected binary data in RTF file.");
    }
    
    public void begingroup()
    {
      MutableAttributeSet localMutableAttributeSet1 = currentTextAttributes();
      MutableAttributeSet localMutableAttributeSet2 = currentParagraphAttributes();
      AttributeSet localAttributeSet = currentSectionAttributes();
      this.characterAttributes = new SimpleAttributeSet();
      this.characterAttributes.addAttributes(localMutableAttributeSet1);
      RTFReader.this.parserState.put("chr", this.characterAttributes);
      this.paragraphAttributes = new SimpleAttributeSet();
      this.paragraphAttributes.addAttributes(localMutableAttributeSet2);
      RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
      this.sectionAttributes = new SimpleAttributeSet();
      this.sectionAttributes.addAttributes(localAttributeSet);
      RTFReader.this.parserState.put("sec", this.sectionAttributes);
    }
    
    public void endgroup(Dictionary paramDictionary)
    {
      this.characterAttributes = ((MutableAttributeSet)RTFReader.this.parserState.get("chr"));
      this.paragraphAttributes = ((MutableAttributeSet)RTFReader.this.parserState.get("pgf"));
      this.sectionAttributes = ((MutableAttributeSet)RTFReader.this.parserState.get("sec"));
    }
    
    public void close() {}
    
    public boolean handleKeyword(String paramString)
    {
      if (paramString.equals("ulnone")) {
        return handleKeyword("ul", 0);
      }
      RTFAttribute localRTFAttribute = (RTFAttribute)RTFReader.straightforwardAttributes.get(paramString);
      if (localRTFAttribute != null)
      {
        boolean bool;
        switch (localRTFAttribute.domain())
        {
        case 0: 
          bool = localRTFAttribute.set(this.characterAttributes);
          break;
        case 1: 
          bool = localRTFAttribute.set(this.paragraphAttributes);
          break;
        case 2: 
          bool = localRTFAttribute.set(this.sectionAttributes);
          break;
        case 4: 
          RTFReader.this.mockery.backing = RTFReader.this.parserState;
          bool = localRTFAttribute.set(RTFReader.this.mockery);
          RTFReader.this.mockery.backing = null;
          break;
        case 3: 
          bool = localRTFAttribute.set(RTFReader.this.documentAttributes);
          break;
        default: 
          bool = false;
        }
        if (bool) {
          return true;
        }
      }
      if (paramString.equals("plain"))
      {
        resetCharacterAttributes();
        return true;
      }
      if (paramString.equals("pard"))
      {
        resetParagraphAttributes();
        return true;
      }
      if (paramString.equals("sectd"))
      {
        resetSectionAttributes();
        return true;
      }
      return false;
    }
    
    public boolean handleKeyword(String paramString, int paramInt)
    {
      int i = paramInt != 0 ? 1 : 0;
      if (paramString.equals("fc")) {
        paramString = "cf";
      }
      if (paramString.equals("f"))
      {
        RTFReader.this.parserState.put(paramString, Integer.valueOf(paramInt));
        return true;
      }
      if (paramString.equals("cf"))
      {
        RTFReader.this.parserState.put(paramString, Integer.valueOf(paramInt));
        return true;
      }
      RTFAttribute localRTFAttribute = (RTFAttribute)RTFReader.straightforwardAttributes.get(paramString);
      boolean bool;
      if (localRTFAttribute != null)
      {
        switch (localRTFAttribute.domain())
        {
        case 0: 
          bool = localRTFAttribute.set(this.characterAttributes, paramInt);
          break;
        case 1: 
          bool = localRTFAttribute.set(this.paragraphAttributes, paramInt);
          break;
        case 2: 
          bool = localRTFAttribute.set(this.sectionAttributes, paramInt);
          break;
        case 4: 
          RTFReader.this.mockery.backing = RTFReader.this.parserState;
          bool = localRTFAttribute.set(RTFReader.this.mockery, paramInt);
          RTFReader.this.mockery.backing = null;
          break;
        case 3: 
          bool = localRTFAttribute.set(RTFReader.this.documentAttributes, paramInt);
          break;
        default: 
          bool = false;
        }
        if (bool) {
          return true;
        }
      }
      if (paramString.equals("fs"))
      {
        StyleConstants.setFontSize(this.characterAttributes, paramInt / 2);
        return true;
      }
      if (paramString.equals("sl"))
      {
        if (paramInt == 1000) {
          this.characterAttributes.removeAttribute(StyleConstants.LineSpacing);
        } else {
          StyleConstants.setLineSpacing(this.characterAttributes, paramInt / 20.0F);
        }
        return true;
      }
      if ((paramString.equals("tx")) || (paramString.equals("tb")))
      {
        float f = paramInt / 20.0F;
        bool = false;
        Number localNumber = (Number)RTFReader.this.parserState.get("tab_alignment");
        int j;
        if (localNumber != null) {
          j = localNumber.intValue();
        }
        int k = 0;
        localNumber = (Number)RTFReader.this.parserState.get("tab_leader");
        if (localNumber != null) {
          k = localNumber.intValue();
        }
        if (paramString.equals("tb")) {
          j = 5;
        }
        RTFReader.this.parserState.remove("tab_alignment");
        RTFReader.this.parserState.remove("tab_leader");
        TabStop localTabStop = new TabStop(f, j, k);
        Object localObject = (Dictionary)RTFReader.this.parserState.get("_tabs");
        Integer localInteger;
        if (localObject == null)
        {
          localObject = new Hashtable();
          RTFReader.this.parserState.put("_tabs", localObject);
          localInteger = Integer.valueOf(1);
        }
        else
        {
          localInteger = (Integer)((Dictionary)localObject).get("stop count");
          localInteger = Integer.valueOf(1 + localInteger.intValue());
        }
        ((Dictionary)localObject).put(localInteger, localTabStop);
        ((Dictionary)localObject).put("stop count", localInteger);
        RTFReader.this.parserState.remove("_tabs_immutable");
        return true;
      }
      if ((paramString.equals("s")) && (RTFReader.this.paragraphStyles != null))
      {
        RTFReader.this.parserState.put("paragraphStyle", RTFReader.this.paragraphStyles[paramInt]);
        return true;
      }
      if ((paramString.equals("cs")) && (RTFReader.this.characterStyles != null))
      {
        RTFReader.this.parserState.put("characterStyle", RTFReader.this.characterStyles[paramInt]);
        return true;
      }
      if ((paramString.equals("ds")) && (RTFReader.this.sectionStyles != null))
      {
        RTFReader.this.parserState.put("sectionStyle", RTFReader.this.sectionStyles[paramInt]);
        return true;
      }
      return false;
    }
    
    protected MutableAttributeSet rootCharacterAttributes()
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      StyleConstants.setItalic(localSimpleAttributeSet, false);
      StyleConstants.setBold(localSimpleAttributeSet, false);
      StyleConstants.setUnderline(localSimpleAttributeSet, false);
      StyleConstants.setForeground(localSimpleAttributeSet, RTFReader.this.defaultColor());
      return localSimpleAttributeSet;
    }
    
    protected MutableAttributeSet rootParagraphAttributes()
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      StyleConstants.setLeftIndent(localSimpleAttributeSet, 0.0F);
      StyleConstants.setRightIndent(localSimpleAttributeSet, 0.0F);
      StyleConstants.setFirstLineIndent(localSimpleAttributeSet, 0.0F);
      localSimpleAttributeSet.setResolveParent(RTFReader.this.target.getStyle("default"));
      return localSimpleAttributeSet;
    }
    
    protected MutableAttributeSet rootSectionAttributes()
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      return localSimpleAttributeSet;
    }
    
    MutableAttributeSet currentTextAttributes()
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet(this.characterAttributes);
      Integer localInteger1 = (Integer)RTFReader.this.parserState.get("f");
      String str;
      if (localInteger1 != null) {
        str = (String)RTFReader.this.fontTable.get(localInteger1);
      } else {
        str = null;
      }
      if (str != null) {
        StyleConstants.setFontFamily(localSimpleAttributeSet, str);
      } else {
        localSimpleAttributeSet.removeAttribute(StyleConstants.FontFamily);
      }
      Integer localInteger2;
      if (RTFReader.this.colorTable != null)
      {
        localInteger2 = (Integer)RTFReader.this.parserState.get("cf");
        if (localInteger2 != null)
        {
          localObject = RTFReader.this.colorTable[localInteger2.intValue()];
          StyleConstants.setForeground(localSimpleAttributeSet, (Color)localObject);
        }
        else
        {
          localSimpleAttributeSet.removeAttribute(StyleConstants.Foreground);
        }
      }
      if (RTFReader.this.colorTable != null)
      {
        localInteger2 = (Integer)RTFReader.this.parserState.get("cb");
        if (localInteger2 != null)
        {
          localObject = RTFReader.this.colorTable[localInteger2.intValue()];
          localSimpleAttributeSet.addAttribute(StyleConstants.Background, localObject);
        }
        else
        {
          localSimpleAttributeSet.removeAttribute(StyleConstants.Background);
        }
      }
      Object localObject = (Style)RTFReader.this.parserState.get("characterStyle");
      if (localObject != null) {
        localSimpleAttributeSet.setResolveParent((AttributeSet)localObject);
      }
      return localSimpleAttributeSet;
    }
    
    MutableAttributeSet currentParagraphAttributes()
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet(this.paragraphAttributes);
      TabStop[] arrayOfTabStop = (TabStop[])RTFReader.this.parserState.get("_tabs_immutable");
      if (arrayOfTabStop == null)
      {
        localObject = (Dictionary)RTFReader.this.parserState.get("_tabs");
        if (localObject != null)
        {
          int i = ((Integer)((Dictionary)localObject).get("stop count")).intValue();
          arrayOfTabStop = new TabStop[i];
          for (int j = 1; j <= i; j++) {
            arrayOfTabStop[(j - 1)] = ((TabStop)((Dictionary)localObject).get(Integer.valueOf(j)));
          }
          RTFReader.this.parserState.put("_tabs_immutable", arrayOfTabStop);
        }
      }
      if (arrayOfTabStop != null) {
        localSimpleAttributeSet.addAttribute("tabs", arrayOfTabStop);
      }
      Object localObject = (Style)RTFReader.this.parserState.get("paragraphStyle");
      if (localObject != null) {
        localSimpleAttributeSet.setResolveParent((AttributeSet)localObject);
      }
      return localSimpleAttributeSet;
    }
    
    public AttributeSet currentSectionAttributes()
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet(this.sectionAttributes);
      Style localStyle = (Style)RTFReader.this.parserState.get("sectionStyle");
      if (localStyle != null) {
        localSimpleAttributeSet.setResolveParent(localStyle);
      }
      return localSimpleAttributeSet;
    }
    
    protected void resetCharacterAttributes()
    {
      handleKeyword("f", 0);
      handleKeyword("cf", 0);
      handleKeyword("fs", 24);
      Enumeration localEnumeration = RTFReader.straightforwardAttributes.elements();
      while (localEnumeration.hasMoreElements())
      {
        RTFAttribute localRTFAttribute = (RTFAttribute)localEnumeration.nextElement();
        if (localRTFAttribute.domain() == 0) {
          localRTFAttribute.setDefault(this.characterAttributes);
        }
      }
      handleKeyword("sl", 1000);
      RTFReader.this.parserState.remove("characterStyle");
    }
    
    protected void resetParagraphAttributes()
    {
      RTFReader.this.parserState.remove("_tabs");
      RTFReader.this.parserState.remove("_tabs_immutable");
      RTFReader.this.parserState.remove("paragraphStyle");
      StyleConstants.setAlignment(this.paragraphAttributes, 0);
      Enumeration localEnumeration = RTFReader.straightforwardAttributes.elements();
      while (localEnumeration.hasMoreElements())
      {
        RTFAttribute localRTFAttribute = (RTFAttribute)localEnumeration.nextElement();
        if (localRTFAttribute.domain() == 1) {
          localRTFAttribute.setDefault(this.characterAttributes);
        }
      }
    }
    
    protected void resetSectionAttributes()
    {
      Enumeration localEnumeration = RTFReader.straightforwardAttributes.elements();
      while (localEnumeration.hasMoreElements())
      {
        RTFAttribute localRTFAttribute = (RTFAttribute)localEnumeration.nextElement();
        if (localRTFAttribute.domain() == 2) {
          localRTFAttribute.setDefault(this.characterAttributes);
        }
      }
      RTFReader.this.parserState.remove("sectionStyle");
    }
  }
  
  class ColortblDestination
    implements RTFReader.Destination
  {
    int red = 0;
    int green = 0;
    int blue = 0;
    Vector<Color> proTemTable = new Vector();
    
    public ColortblDestination() {}
    
    public void handleText(String paramString)
    {
      for (int i = 0; i < paramString.length(); i++) {
        if (paramString.charAt(i) == ';')
        {
          Color localColor = new Color(this.red, this.green, this.blue);
          this.proTemTable.addElement(localColor);
        }
      }
    }
    
    public void close()
    {
      int i = this.proTemTable.size();
      RTFReader.this.warning("Done reading color table, " + i + " entries.");
      RTFReader.this.colorTable = new Color[i];
      this.proTemTable.copyInto(RTFReader.this.colorTable);
    }
    
    public boolean handleKeyword(String paramString, int paramInt)
    {
      if (paramString.equals("red")) {
        this.red = paramInt;
      } else if (paramString.equals("green")) {
        this.green = paramInt;
      } else if (paramString.equals("blue")) {
        this.blue = paramInt;
      } else {
        return false;
      }
      return true;
    }
    
    public boolean handleKeyword(String paramString)
    {
      return false;
    }
    
    public void begingroup() {}
    
    public void endgroup(Dictionary paramDictionary) {}
    
    public void handleBinaryBlob(byte[] paramArrayOfByte) {}
  }
  
  static abstract interface Destination
  {
    public abstract void handleBinaryBlob(byte[] paramArrayOfByte);
    
    public abstract void handleText(String paramString);
    
    public abstract boolean handleKeyword(String paramString);
    
    public abstract boolean handleKeyword(String paramString, int paramInt);
    
    public abstract void begingroup();
    
    public abstract void endgroup(Dictionary paramDictionary);
    
    public abstract void close();
  }
  
  class DiscardingDestination
    implements RTFReader.Destination
  {
    DiscardingDestination() {}
    
    public void handleBinaryBlob(byte[] paramArrayOfByte) {}
    
    public void handleText(String paramString) {}
    
    public boolean handleKeyword(String paramString)
    {
      return true;
    }
    
    public boolean handleKeyword(String paramString, int paramInt)
    {
      return true;
    }
    
    public void begingroup() {}
    
    public void endgroup(Dictionary paramDictionary) {}
    
    public void close() {}
  }
  
  class DocumentDestination
    extends RTFReader.TextHandlingDestination
    implements RTFReader.Destination
  {
    DocumentDestination()
    {
      super();
    }
    
    public void deliverText(String paramString, AttributeSet paramAttributeSet)
    {
      try
      {
        RTFReader.this.target.insertString(RTFReader.this.target.getLength(), paramString, currentTextAttributes());
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new InternalError(localBadLocationException.getMessage(), localBadLocationException);
      }
    }
    
    public void finishParagraph(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2)
    {
      int i = RTFReader.this.target.getLength();
      try
      {
        RTFReader.this.target.insertString(i, "\n", paramAttributeSet2);
        RTFReader.this.target.setParagraphAttributes(i, 1, paramAttributeSet1, true);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new InternalError(localBadLocationException.getMessage(), localBadLocationException);
      }
    }
    
    public void endSection() {}
  }
  
  class FonttblDestination
    implements RTFReader.Destination
  {
    int nextFontNumber;
    Integer fontNumberKey = null;
    String nextFontFamily;
    
    FonttblDestination() {}
    
    public void handleBinaryBlob(byte[] paramArrayOfByte) {}
    
    public void handleText(String paramString)
    {
      int i = paramString.indexOf(';');
      String str;
      if (i > -1) {
        str = paramString.substring(0, i);
      } else {
        str = paramString;
      }
      if ((this.nextFontNumber == -1) && (this.fontNumberKey != null)) {
        str = (String)RTFReader.this.fontTable.get(this.fontNumberKey) + str;
      } else {
        this.fontNumberKey = Integer.valueOf(this.nextFontNumber);
      }
      RTFReader.this.fontTable.put(this.fontNumberKey, str);
      this.nextFontNumber = -1;
      this.nextFontFamily = null;
    }
    
    public boolean handleKeyword(String paramString)
    {
      if (paramString.charAt(0) == 'f')
      {
        this.nextFontFamily = paramString.substring(1);
        return true;
      }
      return false;
    }
    
    public boolean handleKeyword(String paramString, int paramInt)
    {
      if (paramString.equals("f"))
      {
        this.nextFontNumber = paramInt;
        return true;
      }
      return false;
    }
    
    public void begingroup() {}
    
    public void endgroup(Dictionary paramDictionary) {}
    
    public void close()
    {
      Enumeration localEnumeration = RTFReader.this.fontTable.keys();
      RTFReader.this.warning("Done reading font table.");
      while (localEnumeration.hasMoreElements())
      {
        Integer localInteger = (Integer)localEnumeration.nextElement();
        RTFReader.this.warning("Number " + localInteger + ": " + (String)RTFReader.this.fontTable.get(localInteger));
      }
    }
  }
  
  class InfoDestination
    extends RTFReader.DiscardingDestination
    implements RTFReader.Destination
  {
    InfoDestination()
    {
      super();
    }
  }
  
  class StylesheetDestination
    extends RTFReader.DiscardingDestination
    implements RTFReader.Destination
  {
    Dictionary<Integer, StyleDefiningDestination> definedStyles = new Hashtable();
    
    public StylesheetDestination()
    {
      super();
    }
    
    public void begingroup()
    {
      RTFReader.this.setRTFDestination(new StyleDefiningDestination());
    }
    
    public void close()
    {
      Vector localVector1 = new Vector();
      Vector localVector2 = new Vector();
      Vector localVector3 = new Vector();
      Enumeration localEnumeration = this.definedStyles.elements();
      Object localObject;
      while (localEnumeration.hasMoreElements())
      {
        localObject = (StyleDefiningDestination)localEnumeration.nextElement();
        Style localStyle = ((StyleDefiningDestination)localObject).realize();
        RTFReader.this.warning("Style " + ((StyleDefiningDestination)localObject).number + " (" + ((StyleDefiningDestination)localObject).styleName + "): " + localStyle);
        String str = (String)localStyle.getAttribute("style:type");
        Vector localVector4;
        if (str.equals("section")) {
          localVector4 = localVector3;
        } else if (str.equals("character")) {
          localVector4 = localVector1;
        } else {
          localVector4 = localVector2;
        }
        if (localVector4.size() <= ((StyleDefiningDestination)localObject).number) {
          localVector4.setSize(((StyleDefiningDestination)localObject).number + 1);
        }
        localVector4.setElementAt(localStyle, ((StyleDefiningDestination)localObject).number);
      }
      if (!localVector1.isEmpty())
      {
        localObject = new Style[localVector1.size()];
        localVector1.copyInto((Object[])localObject);
        RTFReader.this.characterStyles = ((Style[])localObject);
      }
      if (!localVector2.isEmpty())
      {
        localObject = new Style[localVector2.size()];
        localVector2.copyInto((Object[])localObject);
        RTFReader.this.paragraphStyles = ((Style[])localObject);
      }
      if (!localVector3.isEmpty())
      {
        localObject = new Style[localVector3.size()];
        localVector3.copyInto((Object[])localObject);
        RTFReader.this.sectionStyles = ((Style[])localObject);
      }
    }
    
    class StyleDefiningDestination
      extends RTFReader.AttributeTrackingDestination
      implements RTFReader.Destination
    {
      final int STYLENUMBER_NONE = 222;
      boolean additive = false;
      boolean characterStyle = false;
      boolean sectionStyle = false;
      public String styleName = null;
      public int number = 0;
      int basedOn = 222;
      int nextStyle = 222;
      boolean hidden = false;
      Style realizedStyle;
      
      public StyleDefiningDestination()
      {
        super();
      }
      
      public void handleText(String paramString)
      {
        if (this.styleName != null) {
          this.styleName += paramString;
        } else {
          this.styleName = paramString;
        }
      }
      
      public void close()
      {
        int i = this.styleName == null ? 0 : this.styleName.indexOf(';');
        if (i > 0) {
          this.styleName = this.styleName.substring(0, i);
        }
        RTFReader.StylesheetDestination.this.definedStyles.put(Integer.valueOf(this.number), this);
        super.close();
      }
      
      public boolean handleKeyword(String paramString)
      {
        if (paramString.equals("additive"))
        {
          this.additive = true;
          return true;
        }
        if (paramString.equals("shidden"))
        {
          this.hidden = true;
          return true;
        }
        return super.handleKeyword(paramString);
      }
      
      public boolean handleKeyword(String paramString, int paramInt)
      {
        if (paramString.equals("s"))
        {
          this.characterStyle = false;
          this.sectionStyle = false;
          this.number = paramInt;
        }
        else if (paramString.equals("cs"))
        {
          this.characterStyle = true;
          this.sectionStyle = false;
          this.number = paramInt;
        }
        else if (paramString.equals("ds"))
        {
          this.characterStyle = false;
          this.sectionStyle = true;
          this.number = paramInt;
        }
        else if (paramString.equals("sbasedon"))
        {
          this.basedOn = paramInt;
        }
        else if (paramString.equals("snext"))
        {
          this.nextStyle = paramInt;
        }
        else
        {
          return super.handleKeyword(paramString, paramInt);
        }
        return true;
      }
      
      public Style realize()
      {
        Style localStyle1 = null;
        Style localStyle2 = null;
        if (this.realizedStyle != null) {
          return this.realizedStyle;
        }
        StyleDefiningDestination localStyleDefiningDestination;
        if (this.basedOn != 222)
        {
          localStyleDefiningDestination = (StyleDefiningDestination)RTFReader.StylesheetDestination.this.definedStyles.get(Integer.valueOf(this.basedOn));
          if ((localStyleDefiningDestination != null) && (localStyleDefiningDestination != this)) {
            localStyle1 = localStyleDefiningDestination.realize();
          }
        }
        this.realizedStyle = RTFReader.this.target.addStyle(this.styleName, localStyle1);
        if (this.characterStyle)
        {
          this.realizedStyle.addAttributes(currentTextAttributes());
          this.realizedStyle.addAttribute("style:type", "character");
        }
        else if (this.sectionStyle)
        {
          this.realizedStyle.addAttributes(currentSectionAttributes());
          this.realizedStyle.addAttribute("style:type", "section");
        }
        else
        {
          this.realizedStyle.addAttributes(currentParagraphAttributes());
          this.realizedStyle.addAttribute("style:type", "paragraph");
        }
        if (this.nextStyle != 222)
        {
          localStyleDefiningDestination = (StyleDefiningDestination)RTFReader.StylesheetDestination.this.definedStyles.get(Integer.valueOf(this.nextStyle));
          if (localStyleDefiningDestination != null) {
            localStyle2 = localStyleDefiningDestination.realize();
          }
        }
        if (localStyle2 != null) {
          this.realizedStyle.addAttribute("style:nextStyle", localStyle2);
        }
        this.realizedStyle.addAttribute("style:additive", Boolean.valueOf(this.additive));
        this.realizedStyle.addAttribute("style:hidden", Boolean.valueOf(this.hidden));
        return this.realizedStyle;
      }
    }
  }
  
  abstract class TextHandlingDestination
    extends RTFReader.AttributeTrackingDestination
    implements RTFReader.Destination
  {
    boolean inParagraph = false;
    
    public TextHandlingDestination()
    {
      super();
    }
    
    public void handleText(String paramString)
    {
      if (!this.inParagraph) {
        beginParagraph();
      }
      deliverText(paramString, currentTextAttributes());
    }
    
    abstract void deliverText(String paramString, AttributeSet paramAttributeSet);
    
    public void close()
    {
      if (this.inParagraph) {
        endParagraph();
      }
      super.close();
    }
    
    public boolean handleKeyword(String paramString)
    {
      if ((paramString.equals("\r")) || (paramString.equals("\n"))) {
        paramString = "par";
      }
      if (paramString.equals("par"))
      {
        endParagraph();
        return true;
      }
      if (paramString.equals("sect"))
      {
        endSection();
        return true;
      }
      return super.handleKeyword(paramString);
    }
    
    protected void beginParagraph()
    {
      this.inParagraph = true;
    }
    
    protected void endParagraph()
    {
      MutableAttributeSet localMutableAttributeSet1 = currentParagraphAttributes();
      MutableAttributeSet localMutableAttributeSet2 = currentTextAttributes();
      finishParagraph(localMutableAttributeSet1, localMutableAttributeSet2);
      this.inParagraph = false;
    }
    
    abstract void finishParagraph(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2);
    
    abstract void endSection();
  }
}
