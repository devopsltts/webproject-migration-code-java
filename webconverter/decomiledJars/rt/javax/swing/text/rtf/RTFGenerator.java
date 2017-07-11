package javax.swing.text.rtf;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabStop;

class RTFGenerator
{
  Dictionary<Object, Integer> colorTable = new Hashtable();
  int colorCount;
  Dictionary<String, Integer> fontTable;
  int fontCount;
  Dictionary<AttributeSet, Integer> styleTable;
  int styleCount;
  OutputStream outputStream;
  boolean afterKeyword;
  MutableAttributeSet outputAttributes;
  int unicodeCount;
  private Segment workingSegment;
  int[] outputConversion;
  public static final Color defaultRTFColor = Color.black;
  public static final float defaultFontSize = 12.0F;
  public static final String defaultFontFamily = "Helvetica";
  private static final Object MagicToken = new Object();
  protected static CharacterKeywordPair[] textKeywords;
  static final char[] hexdigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  public static void writeDocument(Document paramDocument, OutputStream paramOutputStream)
    throws IOException
  {
    RTFGenerator localRTFGenerator = new RTFGenerator(paramOutputStream);
    Element localElement = paramDocument.getDefaultRootElement();
    localRTFGenerator.examineElement(localElement);
    localRTFGenerator.writeRTFHeader();
    localRTFGenerator.writeDocumentProperties(paramDocument);
    int i = localElement.getElementCount();
    for (int j = 0; j < i; j++) {
      localRTFGenerator.writeParagraphElement(localElement.getElement(j));
    }
    localRTFGenerator.writeRTFTrailer();
  }
  
  public RTFGenerator(OutputStream paramOutputStream)
  {
    this.colorTable.put(defaultRTFColor, Integer.valueOf(0));
    this.colorCount = 1;
    this.fontTable = new Hashtable();
    this.fontCount = 0;
    this.styleTable = new Hashtable();
    this.styleCount = 0;
    this.workingSegment = new Segment();
    this.outputStream = paramOutputStream;
    this.unicodeCount = 1;
  }
  
  public void examineElement(Element paramElement)
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    tallyStyles(localAttributeSet);
    if (localAttributeSet != null)
    {
      Color localColor = StyleConstants.getForeground(localAttributeSet);
      if ((localColor != null) && (this.colorTable.get(localColor) == null))
      {
        this.colorTable.put(localColor, new Integer(this.colorCount));
        this.colorCount += 1;
      }
      Object localObject = localAttributeSet.getAttribute(StyleConstants.Background);
      if ((localObject != null) && (this.colorTable.get(localObject) == null))
      {
        this.colorTable.put(localObject, new Integer(this.colorCount));
        this.colorCount += 1;
      }
      String str = StyleConstants.getFontFamily(localAttributeSet);
      if (str == null) {
        str = "Helvetica";
      }
      if ((str != null) && (this.fontTable.get(str) == null))
      {
        this.fontTable.put(str, new Integer(this.fontCount));
        this.fontCount += 1;
      }
    }
    int i = paramElement.getElementCount();
    for (int j = 0; j < i; j++) {
      examineElement(paramElement.getElement(j));
    }
  }
  
  private void tallyStyles(AttributeSet paramAttributeSet)
  {
    while (paramAttributeSet != null)
    {
      if ((paramAttributeSet instanceof Style))
      {
        Integer localInteger = (Integer)this.styleTable.get(paramAttributeSet);
        if (localInteger == null)
        {
          this.styleCount += 1;
          localInteger = new Integer(this.styleCount);
          this.styleTable.put(paramAttributeSet, localInteger);
        }
      }
      paramAttributeSet = paramAttributeSet.getResolveParent();
    }
  }
  
  private Style findStyle(AttributeSet paramAttributeSet)
  {
    while (paramAttributeSet != null)
    {
      if ((paramAttributeSet instanceof Style))
      {
        Object localObject = this.styleTable.get(paramAttributeSet);
        if (localObject != null) {
          return (Style)paramAttributeSet;
        }
      }
      paramAttributeSet = paramAttributeSet.getResolveParent();
    }
    return null;
  }
  
  private Integer findStyleNumber(AttributeSet paramAttributeSet, String paramString)
  {
    while (paramAttributeSet != null)
    {
      if ((paramAttributeSet instanceof Style))
      {
        Integer localInteger = (Integer)this.styleTable.get(paramAttributeSet);
        if ((localInteger != null) && ((paramString == null) || (paramString.equals(paramAttributeSet.getAttribute("style:type"))))) {
          return localInteger;
        }
      }
      paramAttributeSet = paramAttributeSet.getResolveParent();
    }
    return null;
  }
  
  private static Object attrDiff(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2)
  {
    Object localObject1 = paramMutableAttributeSet.getAttribute(paramObject1);
    Object localObject2 = paramAttributeSet.getAttribute(paramObject1);
    if (localObject2 == localObject1) {
      return null;
    }
    if (localObject2 == null)
    {
      paramMutableAttributeSet.removeAttribute(paramObject1);
      if ((paramObject2 != null) && (!paramObject2.equals(localObject1))) {
        return paramObject2;
      }
      return null;
    }
    if ((localObject1 == null) || (!equalArraysOK(localObject1, localObject2)))
    {
      paramMutableAttributeSet.addAttribute(paramObject1, localObject2);
      return localObject2;
    }
    return null;
  }
  
  private static boolean equalArraysOK(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return false;
    }
    if (paramObject1.equals(paramObject2)) {
      return true;
    }
    if ((!paramObject1.getClass().isArray()) || (!paramObject2.getClass().isArray())) {
      return false;
    }
    Object[] arrayOfObject1 = (Object[])paramObject1;
    Object[] arrayOfObject2 = (Object[])paramObject2;
    if (arrayOfObject1.length != arrayOfObject2.length) {
      return false;
    }
    int j = arrayOfObject1.length;
    for (int i = 0; i < j; i++) {
      if (!equalArraysOK(arrayOfObject1[i], arrayOfObject2[i])) {
        return false;
      }
    }
    return true;
  }
  
  public void writeLineBreak()
    throws IOException
  {
    writeRawString("\n");
    this.afterKeyword = false;
  }
  
  public void writeRTFHeader()
    throws IOException
  {
    writeBegingroup();
    writeControlWord("rtf", 1);
    writeControlWord("ansi");
    this.outputConversion = outputConversionForName("ansi");
    writeLineBreak();
    String[] arrayOfString = new String[this.fontCount];
    Enumeration localEnumeration = this.fontTable.keys();
    Object localObject1;
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      localObject1 = (Integer)this.fontTable.get(str);
      arrayOfString[localObject1.intValue()] = str;
    }
    writeBegingroup();
    writeControlWord("fonttbl");
    for (int i = 0; i < this.fontCount; i++)
    {
      writeControlWord("f", i);
      writeControlWord("fnil");
      writeText(arrayOfString[i]);
      writeText(";");
    }
    writeEndgroup();
    writeLineBreak();
    Object localObject2;
    Object localObject3;
    if (this.colorCount > 1)
    {
      localObject1 = new Color[this.colorCount];
      localObject2 = this.colorTable.keys();
      Color localColor;
      while (((Enumeration)localObject2).hasMoreElements())
      {
        localColor = (Color)((Enumeration)localObject2).nextElement();
        localObject3 = (Integer)this.colorTable.get(localColor);
        localObject1[localObject3.intValue()] = localColor;
      }
      writeBegingroup();
      writeControlWord("colortbl");
      for (i = 0; i < this.colorCount; i++)
      {
        localColor = localObject1[i];
        if (localColor != null)
        {
          writeControlWord("red", localColor.getRed());
          writeControlWord("green", localColor.getGreen());
          writeControlWord("blue", localColor.getBlue());
        }
        writeRawString(";");
      }
      writeEndgroup();
      writeLineBreak();
    }
    if (this.styleCount > 1)
    {
      writeBegingroup();
      writeControlWord("stylesheet");
      localObject1 = this.styleTable.keys();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (Style)((Enumeration)localObject1).nextElement();
        int j = ((Integer)this.styleTable.get(localObject2)).intValue();
        writeBegingroup();
        localObject3 = (String)((Style)localObject2).getAttribute("style:type");
        if (localObject3 == null) {
          localObject3 = "paragraph";
        }
        if (((String)localObject3).equals("character"))
        {
          writeControlWord("*");
          writeControlWord("cs", j);
        }
        else if (((String)localObject3).equals("section"))
        {
          writeControlWord("*");
          writeControlWord("ds", j);
        }
        else
        {
          writeControlWord("s", j);
        }
        AttributeSet localAttributeSet = ((Style)localObject2).getResolveParent();
        SimpleAttributeSet localSimpleAttributeSet;
        if (localAttributeSet == null) {
          localSimpleAttributeSet = new SimpleAttributeSet();
        } else {
          localSimpleAttributeSet = new SimpleAttributeSet(localAttributeSet);
        }
        updateSectionAttributes(localSimpleAttributeSet, (AttributeSet)localObject2, false);
        updateParagraphAttributes(localSimpleAttributeSet, (AttributeSet)localObject2, false);
        updateCharacterAttributes(localSimpleAttributeSet, (AttributeSet)localObject2, false);
        localAttributeSet = ((Style)localObject2).getResolveParent();
        if ((localAttributeSet != null) && ((localAttributeSet instanceof Style)))
        {
          localObject4 = (Integer)this.styleTable.get(localAttributeSet);
          if (localObject4 != null) {
            writeControlWord("sbasedon", ((Integer)localObject4).intValue());
          }
        }
        Object localObject4 = (Style)((Style)localObject2).getAttribute("style:nextStyle");
        if (localObject4 != null)
        {
          localObject5 = (Integer)this.styleTable.get(localObject4);
          if (localObject5 != null) {
            writeControlWord("snext", ((Integer)localObject5).intValue());
          }
        }
        Object localObject5 = (Boolean)((Style)localObject2).getAttribute("style:hidden");
        if ((localObject5 != null) && (((Boolean)localObject5).booleanValue())) {
          writeControlWord("shidden");
        }
        Boolean localBoolean = (Boolean)((Style)localObject2).getAttribute("style:additive");
        if ((localBoolean != null) && (localBoolean.booleanValue())) {
          writeControlWord("additive");
        }
        writeText(((Style)localObject2).getName());
        writeText(";");
        writeEndgroup();
      }
      writeEndgroup();
      writeLineBreak();
    }
    this.outputAttributes = new SimpleAttributeSet();
  }
  
  void writeDocumentProperties(Document paramDocument)
    throws IOException
  {
    int j = 0;
    for (int i = 0; i < RTFAttributes.attributes.length; i++)
    {
      RTFAttribute localRTFAttribute = RTFAttributes.attributes[i];
      if (localRTFAttribute.domain() == 3)
      {
        Object localObject = paramDocument.getProperty(localRTFAttribute.swingName());
        boolean bool = localRTFAttribute.writeValue(localObject, this, false);
        if (bool) {
          j = 1;
        }
      }
    }
    if (j != 0) {
      writeLineBreak();
    }
  }
  
  public void writeRTFTrailer()
    throws IOException
  {
    writeEndgroup();
    writeLineBreak();
  }
  
  protected void checkNumericControlWord(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, Object paramObject, String paramString, float paramFloat1, float paramFloat2)
    throws IOException
  {
    Object localObject;
    if ((localObject = attrDiff(paramMutableAttributeSet, paramAttributeSet, paramObject, MagicToken)) != null)
    {
      float f;
      if (localObject == MagicToken) {
        f = paramFloat1;
      } else {
        f = ((Number)localObject).floatValue();
      }
      writeControlWord(paramString, Math.round(f * paramFloat2));
    }
  }
  
  protected void checkControlWord(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, RTFAttribute paramRTFAttribute)
    throws IOException
  {
    Object localObject;
    if ((localObject = attrDiff(paramMutableAttributeSet, paramAttributeSet, paramRTFAttribute.swingName(), MagicToken)) != null)
    {
      if (localObject == MagicToken) {
        localObject = null;
      }
      paramRTFAttribute.writeValue(localObject, this, true);
    }
  }
  
  protected void checkControlWords(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, RTFAttribute[] paramArrayOfRTFAttribute, int paramInt)
    throws IOException
  {
    int j = paramArrayOfRTFAttribute.length;
    for (int i = 0; i < j; i++)
    {
      RTFAttribute localRTFAttribute = paramArrayOfRTFAttribute[i];
      if (localRTFAttribute.domain() == paramInt) {
        checkControlWord(paramMutableAttributeSet, paramAttributeSet, localRTFAttribute);
      }
    }
  }
  
  void updateSectionAttributes(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
    {
      Object localObject = paramMutableAttributeSet.getAttribute("sectionStyle");
      Integer localInteger = findStyleNumber(paramAttributeSet, "section");
      if (localObject != localInteger)
      {
        if (localObject != null) {
          resetSectionAttributes(paramMutableAttributeSet);
        }
        if (localInteger != null)
        {
          writeControlWord("ds", ((Integer)localInteger).intValue());
          paramMutableAttributeSet.addAttribute("sectionStyle", localInteger);
        }
        else
        {
          paramMutableAttributeSet.removeAttribute("sectionStyle");
        }
      }
    }
    checkControlWords(paramMutableAttributeSet, paramAttributeSet, RTFAttributes.attributes, 2);
  }
  
  protected void resetSectionAttributes(MutableAttributeSet paramMutableAttributeSet)
    throws IOException
  {
    writeControlWord("sectd");
    int j = RTFAttributes.attributes.length;
    for (int i = 0; i < j; i++)
    {
      RTFAttribute localRTFAttribute = RTFAttributes.attributes[i];
      if (localRTFAttribute.domain() == 2) {
        localRTFAttribute.setDefault(paramMutableAttributeSet);
      }
    }
    paramMutableAttributeSet.removeAttribute("sectionStyle");
  }
  
  void updateParagraphAttributes(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, boolean paramBoolean)
    throws IOException
  {
    Object localObject1;
    Integer localInteger;
    if (paramBoolean)
    {
      localObject1 = paramMutableAttributeSet.getAttribute("paragraphStyle");
      localInteger = findStyleNumber(paramAttributeSet, "paragraph");
      if ((localObject1 != localInteger) && (localObject1 != null))
      {
        resetParagraphAttributes(paramMutableAttributeSet);
        localObject1 = null;
      }
    }
    else
    {
      localObject1 = null;
      localInteger = null;
    }
    Object localObject2 = paramMutableAttributeSet.getAttribute("tabs");
    Object localObject3 = paramAttributeSet.getAttribute("tabs");
    if ((localObject2 != localObject3) && (localObject2 != null))
    {
      resetParagraphAttributes(paramMutableAttributeSet);
      localObject2 = null;
      localObject1 = null;
    }
    if ((localObject1 != localInteger) && (localInteger != null))
    {
      writeControlWord("s", ((Integer)localInteger).intValue());
      paramMutableAttributeSet.addAttribute("paragraphStyle", localInteger);
    }
    checkControlWords(paramMutableAttributeSet, paramAttributeSet, RTFAttributes.attributes, 1);
    if ((localObject2 != localObject3) && (localObject3 != null))
    {
      TabStop[] arrayOfTabStop = (TabStop[])localObject3;
      for (int i = 0; i < arrayOfTabStop.length; i++)
      {
        TabStop localTabStop = arrayOfTabStop[i];
        switch (localTabStop.getAlignment())
        {
        case 0: 
        case 5: 
          break;
        case 1: 
          writeControlWord("tqr");
          break;
        case 2: 
          writeControlWord("tqc");
          break;
        case 4: 
          writeControlWord("tqdec");
        }
        switch (localTabStop.getLeader())
        {
        case 0: 
          break;
        case 1: 
          writeControlWord("tldot");
          break;
        case 2: 
          writeControlWord("tlhyph");
          break;
        case 3: 
          writeControlWord("tlul");
          break;
        case 4: 
          writeControlWord("tlth");
          break;
        case 5: 
          writeControlWord("tleq");
        }
        int j = Math.round(20.0F * localTabStop.getPosition());
        if (localTabStop.getAlignment() == 5) {
          writeControlWord("tb", j);
        } else {
          writeControlWord("tx", j);
        }
      }
      paramMutableAttributeSet.addAttribute("tabs", arrayOfTabStop);
    }
  }
  
  public void writeParagraphElement(Element paramElement)
    throws IOException
  {
    updateParagraphAttributes(this.outputAttributes, paramElement.getAttributes(), true);
    int i = paramElement.getElementCount();
    for (int j = 0; j < i; j++) {
      writeTextElement(paramElement.getElement(j));
    }
    writeControlWord("par");
    writeLineBreak();
  }
  
  protected void resetParagraphAttributes(MutableAttributeSet paramMutableAttributeSet)
    throws IOException
  {
    writeControlWord("pard");
    paramMutableAttributeSet.addAttribute(StyleConstants.Alignment, Integer.valueOf(0));
    int j = RTFAttributes.attributes.length;
    for (int i = 0; i < j; i++)
    {
      RTFAttribute localRTFAttribute = RTFAttributes.attributes[i];
      if (localRTFAttribute.domain() == 1) {
        localRTFAttribute.setDefault(paramMutableAttributeSet);
      }
    }
    paramMutableAttributeSet.removeAttribute("paragraphStyle");
    paramMutableAttributeSet.removeAttribute("tabs");
  }
  
  void updateCharacterAttributes(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, boolean paramBoolean)
    throws IOException
  {
    Object localObject2;
    if (paramBoolean)
    {
      localObject2 = paramMutableAttributeSet.getAttribute("characterStyle");
      Integer localInteger = findStyleNumber(paramAttributeSet, "character");
      if (localObject2 != localInteger)
      {
        if (localObject2 != null) {
          resetCharacterAttributes(paramMutableAttributeSet);
        }
        if (localInteger != null)
        {
          writeControlWord("cs", ((Integer)localInteger).intValue());
          paramMutableAttributeSet.addAttribute("characterStyle", localInteger);
        }
        else
        {
          paramMutableAttributeSet.removeAttribute("characterStyle");
        }
      }
    }
    Object localObject1;
    if ((localObject1 = attrDiff(paramMutableAttributeSet, paramAttributeSet, StyleConstants.FontFamily, null)) != null)
    {
      localObject2 = (Integer)this.fontTable.get(localObject1);
      writeControlWord("f", ((Integer)localObject2).intValue());
    }
    checkNumericControlWord(paramMutableAttributeSet, paramAttributeSet, StyleConstants.FontSize, "fs", 12.0F, 2.0F);
    checkControlWords(paramMutableAttributeSet, paramAttributeSet, RTFAttributes.attributes, 0);
    checkNumericControlWord(paramMutableAttributeSet, paramAttributeSet, StyleConstants.LineSpacing, "sl", 0.0F, 20.0F);
    int i;
    if ((localObject1 = attrDiff(paramMutableAttributeSet, paramAttributeSet, StyleConstants.Background, MagicToken)) != null)
    {
      if (localObject1 == MagicToken) {
        i = 0;
      } else {
        i = ((Integer)this.colorTable.get(localObject1)).intValue();
      }
      writeControlWord("cb", i);
    }
    if ((localObject1 = attrDiff(paramMutableAttributeSet, paramAttributeSet, StyleConstants.Foreground, null)) != null)
    {
      if (localObject1 == MagicToken) {
        i = 0;
      } else {
        i = ((Integer)this.colorTable.get(localObject1)).intValue();
      }
      writeControlWord("cf", i);
    }
  }
  
  protected void resetCharacterAttributes(MutableAttributeSet paramMutableAttributeSet)
    throws IOException
  {
    writeControlWord("plain");
    int j = RTFAttributes.attributes.length;
    for (int i = 0; i < j; i++)
    {
      RTFAttribute localRTFAttribute = RTFAttributes.attributes[i];
      if (localRTFAttribute.domain() == 0) {
        localRTFAttribute.setDefault(paramMutableAttributeSet);
      }
    }
    StyleConstants.setFontFamily(paramMutableAttributeSet, "Helvetica");
    paramMutableAttributeSet.removeAttribute(StyleConstants.FontSize);
    paramMutableAttributeSet.removeAttribute(StyleConstants.Background);
    paramMutableAttributeSet.removeAttribute(StyleConstants.Foreground);
    paramMutableAttributeSet.removeAttribute(StyleConstants.LineSpacing);
    paramMutableAttributeSet.removeAttribute("characterStyle");
  }
  
  public void writeTextElement(Element paramElement)
    throws IOException
  {
    updateCharacterAttributes(this.outputAttributes, paramElement.getAttributes(), true);
    if (paramElement.isLeaf())
    {
      try
      {
        paramElement.getDocument().getText(paramElement.getStartOffset(), paramElement.getEndOffset() - paramElement.getStartOffset(), this.workingSegment);
      }
      catch (BadLocationException localBadLocationException)
      {
        localBadLocationException.printStackTrace();
        throw new InternalError(localBadLocationException.getMessage());
      }
      writeText(this.workingSegment);
    }
    else
    {
      int i = paramElement.getElementCount();
      for (int j = 0; j < i; j++) {
        writeTextElement(paramElement.getElement(j));
      }
    }
  }
  
  public void writeText(Segment paramSegment)
    throws IOException
  {
    int i = paramSegment.offset;
    int j = i + paramSegment.count;
    char[] arrayOfChar = paramSegment.array;
    while (i < j)
    {
      writeCharacter(arrayOfChar[i]);
      i++;
    }
  }
  
  public void writeText(String paramString)
    throws IOException
  {
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      writeCharacter(paramString.charAt(i));
      i++;
    }
  }
  
  public void writeRawString(String paramString)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      this.outputStream.write(paramString.charAt(j));
    }
  }
  
  public void writeControlWord(String paramString)
    throws IOException
  {
    this.outputStream.write(92);
    writeRawString(paramString);
    this.afterKeyword = true;
  }
  
  public void writeControlWord(String paramString, int paramInt)
    throws IOException
  {
    this.outputStream.write(92);
    writeRawString(paramString);
    writeRawString(String.valueOf(paramInt));
    this.afterKeyword = true;
  }
  
  public void writeBegingroup()
    throws IOException
  {
    this.outputStream.write(123);
    this.afterKeyword = false;
  }
  
  public void writeEndgroup()
    throws IOException
  {
    this.outputStream.write(125);
    this.afterKeyword = false;
  }
  
  public void writeCharacter(char paramChar)
    throws IOException
  {
    if (paramChar == ' ')
    {
      this.outputStream.write(92);
      this.outputStream.write(126);
      this.afterKeyword = false;
      return;
    }
    if (paramChar == '\t')
    {
      writeControlWord("tab");
      return;
    }
    if ((paramChar == '\n') || (paramChar == '\r')) {
      return;
    }
    int i = convertCharacter(this.outputConversion, paramChar);
    int j;
    if (i == 0)
    {
      for (j = 0; j < textKeywords.length; j++) {
        if (textKeywords[j].character == paramChar)
        {
          writeControlWord(textKeywords[j].keyword);
          return;
        }
      }
      String str = approximationForUnicode(paramChar);
      if (str.length() != this.unicodeCount)
      {
        this.unicodeCount = str.length();
        writeControlWord("uc", this.unicodeCount);
      }
      writeControlWord("u", paramChar);
      writeRawString(" ");
      writeRawString(str);
      this.afterKeyword = false;
      return;
    }
    if (i > 127)
    {
      this.outputStream.write(92);
      this.outputStream.write(39);
      j = (i & 0xF0) >>> 4;
      this.outputStream.write(hexdigits[j]);
      j = i & 0xF;
      this.outputStream.write(hexdigits[j]);
      this.afterKeyword = false;
      return;
    }
    switch (i)
    {
    case 92: 
    case 123: 
    case 125: 
      this.outputStream.write(92);
      this.afterKeyword = false;
    }
    if (this.afterKeyword)
    {
      this.outputStream.write(32);
      this.afterKeyword = false;
    }
    this.outputStream.write(i);
  }
  
  String approximationForUnicode(char paramChar)
  {
    return "?";
  }
  
  static int[] outputConversionFromTranslationTable(char[] paramArrayOfChar)
  {
    int[] arrayOfInt = new int[2 * paramArrayOfChar.length];
    for (int i = 0; i < paramArrayOfChar.length; i++)
    {
      arrayOfInt[(i * 2)] = paramArrayOfChar[i];
      arrayOfInt[(i * 2 + 1)] = i;
    }
    return arrayOfInt;
  }
  
  static int[] outputConversionForName(String paramString)
    throws IOException
  {
    char[] arrayOfChar = (char[])RTFReader.getCharacterSet(paramString);
    return outputConversionFromTranslationTable(arrayOfChar);
  }
  
  protected static int convertCharacter(int[] paramArrayOfInt, char paramChar)
  {
    for (int i = 0; i < paramArrayOfInt.length; i += 2) {
      if (paramArrayOfInt[i] == paramChar) {
        return paramArrayOfInt[(i + 1)];
      }
    }
    return 0;
  }
  
  static
  {
    Dictionary localDictionary = RTFReader.textKeywords;
    Enumeration localEnumeration = localDictionary.keys();
    Vector localVector = new Vector();
    while (localEnumeration.hasMoreElements())
    {
      CharacterKeywordPair localCharacterKeywordPair = new CharacterKeywordPair();
      localCharacterKeywordPair.keyword = ((String)localEnumeration.nextElement());
      localCharacterKeywordPair.character = ((String)localDictionary.get(localCharacterKeywordPair.keyword)).charAt(0);
      localVector.addElement(localCharacterKeywordPair);
    }
    textKeywords = new CharacterKeywordPair[localVector.size()];
    localVector.copyInto(textKeywords);
  }
  
  static class CharacterKeywordPair
  {
    public char character;
    public String keyword;
    
    CharacterKeywordPair() {}
  }
}
