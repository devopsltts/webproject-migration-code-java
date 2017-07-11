package com.sun.xml.internal.fastinfoset;

import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.ExternalVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetParser;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Decoder
  implements FastInfosetParser
{
  private static final char[] XML_NAMESPACE_NAME_CHARS = "http://www.w3.org/XML/1998/namespace".toCharArray();
  private static final char[] XMLNS_NAMESPACE_PREFIX_CHARS = "xmlns".toCharArray();
  private static final char[] XMLNS_NAMESPACE_NAME_CHARS = "http://www.w3.org/2000/xmlns/".toCharArray();
  public static final String STRING_INTERNING_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.parser.string-interning";
  public static final String BUFFER_SIZE_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.parser.buffer-size";
  private static boolean _stringInterningSystemDefault = false;
  private static int _bufferSizeSystemDefault = 1024;
  private boolean _stringInterning = _stringInterningSystemDefault;
  private InputStream _s;
  private Map _externalVocabularies;
  protected boolean _parseFragments;
  protected boolean _needForceStreamClose;
  private boolean _vIsInternal = true;
  protected List _notations;
  protected List _unparsedEntities;
  protected Map _registeredEncodingAlgorithms = new HashMap();
  protected ParserVocabulary _v = new ParserVocabulary();
  protected PrefixArray _prefixTable = this._v.prefix;
  protected QualifiedNameArray _elementNameTable = this._v.elementName;
  protected QualifiedNameArray _attributeNameTable = this._v.attributeName;
  protected ContiguousCharArrayArray _characterContentChunkTable = this._v.characterContentChunk;
  protected StringArray _attributeValueTable = this._v.attributeValue;
  protected int _b;
  protected boolean _terminate;
  protected boolean _doubleTerminate;
  protected boolean _addToTable;
  protected int _integer;
  protected int _identifier;
  protected int _bufferSize = _bufferSizeSystemDefault;
  protected byte[] _octetBuffer = new byte[_bufferSizeSystemDefault];
  protected int _octetBufferStart;
  protected int _octetBufferOffset;
  protected int _octetBufferEnd;
  protected int _octetBufferLength;
  protected char[] _charBuffer = new char['Ȁ'];
  protected int _charBufferLength;
  protected DuplicateAttributeVerifier _duplicateAttributeVerifier = new DuplicateAttributeVerifier();
  protected static final int NISTRING_STRING = 0;
  protected static final int NISTRING_INDEX = 1;
  protected static final int NISTRING_ENCODING_ALGORITHM = 2;
  protected static final int NISTRING_EMPTY_STRING = 3;
  protected int _prefixIndex;
  protected int _namespaceNameIndex;
  private int _bitsLeftInOctet;
  private char _utf8_highSurrogate;
  private char _utf8_lowSurrogate;
  
  protected Decoder() {}
  
  public void setStringInterning(boolean paramBoolean)
  {
    this._stringInterning = paramBoolean;
  }
  
  public boolean getStringInterning()
  {
    return this._stringInterning;
  }
  
  public void setBufferSize(int paramInt)
  {
    if (this._bufferSize > this._octetBuffer.length) {
      this._bufferSize = paramInt;
    }
  }
  
  public int getBufferSize()
  {
    return this._bufferSize;
  }
  
  public void setRegisteredEncodingAlgorithms(Map paramMap)
  {
    this._registeredEncodingAlgorithms = paramMap;
    if (this._registeredEncodingAlgorithms == null) {
      this._registeredEncodingAlgorithms = new HashMap();
    }
  }
  
  public Map getRegisteredEncodingAlgorithms()
  {
    return this._registeredEncodingAlgorithms;
  }
  
  public void setExternalVocabularies(Map paramMap)
  {
    if (paramMap != null)
    {
      this._externalVocabularies = new HashMap();
      this._externalVocabularies.putAll(paramMap);
    }
    else
    {
      this._externalVocabularies = null;
    }
  }
  
  public Map getExternalVocabularies()
  {
    return this._externalVocabularies;
  }
  
  public void setParseFragments(boolean paramBoolean)
  {
    this._parseFragments = paramBoolean;
  }
  
  public boolean getParseFragments()
  {
    return this._parseFragments;
  }
  
  public void setForceStreamClose(boolean paramBoolean)
  {
    this._needForceStreamClose = paramBoolean;
  }
  
  public boolean getForceStreamClose()
  {
    return this._needForceStreamClose;
  }
  
  public void reset()
  {
    this._terminate = (this._doubleTerminate = 0);
  }
  
  public void setVocabulary(ParserVocabulary paramParserVocabulary)
  {
    this._v = paramParserVocabulary;
    this._prefixTable = this._v.prefix;
    this._elementNameTable = this._v.elementName;
    this._attributeNameTable = this._v.attributeName;
    this._characterContentChunkTable = this._v.characterContentChunk;
    this._attributeValueTable = this._v.attributeValue;
    this._vIsInternal = false;
  }
  
  public void setInputStream(InputStream paramInputStream)
  {
    this._s = paramInputStream;
    this._octetBufferOffset = 0;
    this._octetBufferEnd = 0;
    if (this._vIsInternal == true) {
      this._v.clear();
    }
  }
  
  protected final void decodeDII()
    throws FastInfosetException, IOException
  {
    int i = read();
    if (i == 32) {
      decodeInitialVocabulary();
    } else if (i != 0) {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.optinalValues"));
    }
  }
  
  protected final void decodeAdditionalData()
    throws FastInfosetException, IOException
  {
    int i = decodeNumberOfItemsOfSequence();
    for (int j = 0; j < i; j++)
    {
      decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
      decodeNonEmptyOctetStringLengthOnSecondBit();
      ensureOctetBufferSize();
      this._octetBufferStart = this._octetBufferOffset;
      this._octetBufferOffset += this._octetBufferLength;
    }
  }
  
  protected final void decodeInitialVocabulary()
    throws FastInfosetException, IOException
  {
    int i = read();
    int j = read();
    if ((i == 16) && (j == 0))
    {
      decodeExternalVocabularyURI();
      return;
    }
    if ((i & 0x10) > 0) {
      decodeExternalVocabularyURI();
    }
    if ((i & 0x8) > 0) {
      decodeTableItems(this._v.restrictedAlphabet);
    }
    if ((i & 0x4) > 0) {
      decodeTableItems(this._v.encodingAlgorithm);
    }
    if ((i & 0x2) > 0) {
      decodeTableItems(this._v.prefix);
    }
    if ((i & 0x1) > 0) {
      decodeTableItems(this._v.namespaceName);
    }
    if ((j & 0x80) > 0) {
      decodeTableItems(this._v.localName);
    }
    if ((j & 0x40) > 0) {
      decodeTableItems(this._v.otherNCName);
    }
    if ((j & 0x20) > 0) {
      decodeTableItems(this._v.otherURI);
    }
    if ((j & 0x10) > 0) {
      decodeTableItems(this._v.attributeValue);
    }
    if ((j & 0x8) > 0) {
      decodeTableItems(this._v.characterContentChunk);
    }
    if ((j & 0x4) > 0) {
      decodeTableItems(this._v.otherString);
    }
    if ((j & 0x2) > 0) {
      decodeTableItems(this._v.elementName, false);
    }
    if ((j & 0x1) > 0) {
      decodeTableItems(this._v.attributeName, true);
    }
  }
  
  private void decodeExternalVocabularyURI()
    throws FastInfosetException, IOException
  {
    if (this._externalVocabularies == null) {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.noExternalVocabularies"));
    }
    String str = decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
    Object localObject = this._externalVocabularies.get(str);
    if ((localObject instanceof ParserVocabulary))
    {
      this._v.setReferencedVocabulary(str, (ParserVocabulary)localObject, false);
    }
    else if ((localObject instanceof ExternalVocabulary))
    {
      ExternalVocabulary localExternalVocabulary = (ExternalVocabulary)localObject;
      ParserVocabulary localParserVocabulary = new ParserVocabulary(localExternalVocabulary.vocabulary);
      this._externalVocabularies.put(str, localParserVocabulary);
      this._v.setReferencedVocabulary(str, localParserVocabulary, false);
    }
    else
    {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.externalVocabularyNotRegistered", new Object[] { str }));
    }
  }
  
  private void decodeTableItems(StringArray paramStringArray)
    throws FastInfosetException, IOException
  {
    int i = decodeNumberOfItemsOfSequence();
    for (int j = 0; j < i; j++) {
      paramStringArray.add(decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
    }
  }
  
  private void decodeTableItems(PrefixArray paramPrefixArray)
    throws FastInfosetException, IOException
  {
    int i = decodeNumberOfItemsOfSequence();
    for (int j = 0; j < i; j++) {
      paramPrefixArray.add(decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
    }
  }
  
  private void decodeTableItems(ContiguousCharArrayArray paramContiguousCharArrayArray)
    throws FastInfosetException, IOException
  {
    int i = decodeNumberOfItemsOfSequence();
    for (int j = 0; j < i; j++) {
      switch (decodeNonIdentifyingStringOnFirstBit())
      {
      case 0: 
        paramContiguousCharArrayArray.add(this._charBuffer, this._charBufferLength);
        break;
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.illegalState"));
      }
    }
  }
  
  private void decodeTableItems(CharArrayArray paramCharArrayArray)
    throws FastInfosetException, IOException
  {
    int i = decodeNumberOfItemsOfSequence();
    for (int j = 0; j < i; j++) {
      switch (decodeNonIdentifyingStringOnFirstBit())
      {
      case 0: 
        paramCharArrayArray.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
        break;
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.illegalState"));
      }
    }
  }
  
  private void decodeTableItems(QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    int i = decodeNumberOfItemsOfSequence();
    for (int j = 0; j < i; j++)
    {
      int k = read();
      String str1 = "";
      int m = -1;
      if ((k & 0x2) > 0)
      {
        m = decodeIntegerIndexOnSecondBit();
        str1 = this._v.prefix.get(m);
      }
      String str2 = "";
      int n = -1;
      if ((k & 0x1) > 0)
      {
        n = decodeIntegerIndexOnSecondBit();
        str2 = this._v.namespaceName.get(n);
      }
      if ((str2 == "") && (str1 != "")) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.missingNamespace"));
      }
      int i1 = decodeIntegerIndexOnSecondBit();
      String str3 = this._v.localName.get(i1);
      QualifiedName localQualifiedName = new QualifiedName(str1, str2, str3, m, n, i1, this._charBuffer);
      if (paramBoolean) {
        localQualifiedName.createAttributeValues(256);
      }
      paramQualifiedNameArray.add(localQualifiedName);
    }
  }
  
  private int decodeNumberOfItemsOfSequence()
    throws IOException
  {
    int i = read();
    if (i < 128) {
      return i + 1;
    }
    return ((i & 0xF) << 16 | read() << 8 | read()) + 129;
  }
  
  protected final void decodeNotations()
    throws FastInfosetException, IOException
  {
    if (this._notations == null) {
      this._notations = new ArrayList();
    } else {
      this._notations.clear();
    }
    for (int i = read(); (i & 0xFC) == 192; i = read())
    {
      String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
      String str2 = (this._b & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
      String str3 = (this._b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
      Notation localNotation = new Notation(str1, str2, str3);
      this._notations.add(localNotation);
    }
    if (i != 240) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IIsNotTerminatedCorrectly"));
    }
  }
  
  protected final void decodeUnparsedEntities()
    throws FastInfosetException, IOException
  {
    if (this._unparsedEntities == null) {
      this._unparsedEntities = new ArrayList();
    } else {
      this._unparsedEntities.clear();
    }
    for (int i = read(); (i & 0xFE) == 208; i = read())
    {
      String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
      String str2 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
      String str3 = (this._b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
      String str4 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
      UnparsedEntity localUnparsedEntity = new UnparsedEntity(str1, str2, str3, str4);
      this._unparsedEntities.add(localUnparsedEntity);
    }
    if (i != 240) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unparsedEntities"));
    }
  }
  
  protected final String decodeCharacterEncodingScheme()
    throws FastInfosetException, IOException
  {
    return decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
  }
  
  protected final String decodeVersion()
    throws FastInfosetException, IOException
  {
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      String str = new String(this._charBuffer, 0, this._charBufferLength);
      if (this._addToTable) {
        this._v.otherString.add(new CharArrayString(str));
      }
      return str;
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingNotSupported"));
    case 1: 
      return this._v.otherString.get(this._integer).toString();
    }
    return "";
  }
  
  protected final QualifiedName decodeEIIIndexMedium()
    throws FastInfosetException, IOException
  {
    int i = ((this._b & 0x7) << 8 | read()) + 32;
    return this._v.elementName._array[i];
  }
  
  protected final QualifiedName decodeEIIIndexLarge()
    throws FastInfosetException, IOException
  {
    int i;
    if ((this._b & 0x30) == 32) {
      i = ((this._b & 0x7) << 16 | read() << 8 | read()) + 2080;
    } else {
      i = ((read() & 0xF) << 16 | read() << 8 | read()) + 526368;
    }
    return this._v.elementName._array[i];
  }
  
  protected final QualifiedName decodeLiteralQualifiedName(int paramInt, QualifiedName paramQualifiedName)
    throws FastInfosetException, IOException
  {
    if (paramQualifiedName == null) {
      paramQualifiedName = new QualifiedName();
    }
    switch (paramInt)
    {
    case 0: 
      return paramQualifiedName.set("", "", decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
    case 1: 
      return paramQualifiedName.set("", decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
    case 3: 
      return paramQualifiedName.set(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
  }
  
  protected final int decodeNonIdentifyingStringOnFirstBit()
    throws FastInfosetException, IOException
  {
    int i = read();
    int j;
    switch (DecoderStateTables.NISTRING(i))
    {
    case 0: 
      this._addToTable = ((i & 0x40) > 0);
      this._octetBufferLength = ((i & 0x7) + 1);
      decodeUtf8StringAsCharBuffer();
      return 0;
    case 1: 
      this._addToTable = ((i & 0x40) > 0);
      this._octetBufferLength = (read() + 9);
      decodeUtf8StringAsCharBuffer();
      return 0;
    case 2: 
      this._addToTable = ((i & 0x40) > 0);
      j = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (j + 265);
      decodeUtf8StringAsCharBuffer();
      return 0;
    case 3: 
      this._addToTable = ((i & 0x40) > 0);
      this._octetBufferLength = ((i & 0x7) + 1);
      decodeUtf16StringAsCharBuffer();
      return 0;
    case 4: 
      this._addToTable = ((i & 0x40) > 0);
      this._octetBufferLength = (read() + 9);
      decodeUtf16StringAsCharBuffer();
      return 0;
    case 5: 
      this._addToTable = ((i & 0x40) > 0);
      j = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (j + 265);
      decodeUtf16StringAsCharBuffer();
      return 0;
    case 6: 
      this._addToTable = ((i & 0x40) > 0);
      this._identifier = ((i & 0xF) << 4);
      j = read();
      this._identifier |= (j & 0xF0) >> 4;
      decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(j);
      decodeRestrictedAlphabetAsCharBuffer();
      return 0;
    case 7: 
      this._addToTable = ((i & 0x40) > 0);
      this._identifier = ((i & 0xF) << 4);
      j = read();
      this._identifier |= (j & 0xF0) >> 4;
      decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(j);
      return 2;
    case 8: 
      this._integer = (i & 0x3F);
      return 1;
    case 9: 
      this._integer = (((i & 0x1F) << 8 | read()) + 64);
      return 1;
    case 10: 
      this._integer = (((i & 0xF) << 16 | read() << 8 | read()) + 8256);
      return 1;
    case 11: 
      return 3;
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingNonIdentifyingString"));
  }
  
  protected final void decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(int paramInt)
    throws FastInfosetException, IOException
  {
    paramInt &= 0xF;
    switch (DecoderStateTables.NISTRING(paramInt))
    {
    case 0: 
      this._octetBufferLength = (paramInt + 1);
      break;
    case 1: 
      this._octetBufferLength = (read() + 9);
      break;
    case 2: 
      int i = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (i + 265);
      break;
    default: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingOctets"));
    }
    ensureOctetBufferSize();
    this._octetBufferStart = this._octetBufferOffset;
    this._octetBufferOffset += this._octetBufferLength;
  }
  
  protected final void decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(int paramInt)
    throws FastInfosetException, IOException
  {
    switch (paramInt & 0x3)
    {
    case 0: 
      this._octetBufferLength = 1;
      break;
    case 1: 
      this._octetBufferLength = 2;
      break;
    case 2: 
      this._octetBufferLength = (read() + 3);
      break;
    case 3: 
      this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read());
      this._octetBufferLength += 259;
    }
    ensureOctetBufferSize();
    this._octetBufferStart = this._octetBufferOffset;
    this._octetBufferOffset += this._octetBufferLength;
  }
  
  protected final String decodeIdentifyingNonEmptyStringOnFirstBit(StringArray paramStringArray)
    throws FastInfosetException, IOException
  {
    int i = read();
    String str1;
    switch (DecoderStateTables.ISTRING(i))
    {
    case 0: 
      this._octetBufferLength = (i + 1);
      str1 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._identifier = (paramStringArray.add(str1) - 1);
      return str1;
    case 1: 
      this._octetBufferLength = (read() + 65);
      str1 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._identifier = (paramStringArray.add(str1) - 1);
      return str1;
    case 2: 
      int j = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (j + 321);
      String str2 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._identifier = (paramStringArray.add(str2) - 1);
      return str2;
    case 3: 
      this._identifier = (i & 0x3F);
      return paramStringArray._array[this._identifier];
    case 4: 
      this._identifier = (((i & 0x1F) << 8 | read()) + 64);
      return paramStringArray._array[this._identifier];
    case 5: 
      this._identifier = (((i & 0xF) << 16 | read() << 8 | read()) + 8256);
      return paramStringArray._array[this._identifier];
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIdentifyingString"));
  }
  
  protected final String decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    int i = read();
    String str1;
    switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(i))
    {
    case 6: 
      this._octetBufferLength = EncodingConstants.XML_NAMESPACE_PREFIX_LENGTH;
      decodeUtf8StringAsCharBuffer();
      if ((this._charBuffer[0] == 'x') && (this._charBuffer[1] == 'm') && (this._charBuffer[2] == 'l')) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.prefixIllegal"));
      }
      str1 = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
      this._prefixIndex = this._v.prefix.add(str1);
      return str1;
    case 7: 
      this._octetBufferLength = EncodingConstants.XMLNS_NAMESPACE_PREFIX_LENGTH;
      decodeUtf8StringAsCharBuffer();
      if ((this._charBuffer[0] == 'x') && (this._charBuffer[1] == 'm') && (this._charBuffer[2] == 'l') && (this._charBuffer[3] == 'n') && (this._charBuffer[4] == 's')) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.xmlns"));
      }
      str1 = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
      this._prefixIndex = this._v.prefix.add(str1);
      return str1;
    case 0: 
    case 8: 
    case 9: 
      this._octetBufferLength = (i + 1);
      str1 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._prefixIndex = this._v.prefix.add(str1);
      return str1;
    case 1: 
      this._octetBufferLength = (read() + 65);
      str1 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._prefixIndex = this._v.prefix.add(str1);
      return str1;
    case 2: 
      int j = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (j + 321);
      String str2 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._prefixIndex = this._v.prefix.add(str2);
      return str2;
    case 10: 
      if (paramBoolean)
      {
        this._prefixIndex = 0;
        if (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(peek()) != 10) {
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.wrongNamespaceName"));
        }
        return "xml";
      }
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.missingNamespaceName"));
    case 3: 
      this._prefixIndex = (i & 0x3F);
      return this._v.prefix._array[(this._prefixIndex - 1)];
    case 4: 
      this._prefixIndex = (((i & 0x1F) << 8 | read()) + 64);
      return this._v.prefix._array[(this._prefixIndex - 1)];
    case 5: 
      this._prefixIndex = (((i & 0xF) << 16 | read() << 8 | read()) + 8256);
      return this._v.prefix._array[(this._prefixIndex - 1)];
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIdentifyingStringForPrefix"));
  }
  
  protected final String decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    int i = read();
    switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(i))
    {
    case 10: 
      if (paramBoolean)
      {
        this._prefixIndex = 0;
        if (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(peek()) != 10) {
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.wrongNamespaceName"));
        }
        return "xml";
      }
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.missingNamespaceName"));
    case 3: 
      this._prefixIndex = (i & 0x3F);
      return this._v.prefix._array[(this._prefixIndex - 1)];
    case 4: 
      this._prefixIndex = (((i & 0x1F) << 8 | read()) + 64);
      return this._v.prefix._array[(this._prefixIndex - 1)];
    case 5: 
      this._prefixIndex = (((i & 0xF) << 16 | read() << 8 | read()) + 8256);
      return this._v.prefix._array[(this._prefixIndex - 1)];
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIdentifyingStringForPrefix"));
  }
  
  protected final String decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    int i = read();
    String str1;
    switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(i))
    {
    case 0: 
    case 6: 
    case 7: 
      this._octetBufferLength = (i + 1);
      str1 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._namespaceNameIndex = this._v.namespaceName.add(str1);
      return str1;
    case 8: 
      this._octetBufferLength = EncodingConstants.XMLNS_NAMESPACE_NAME_LENGTH;
      decodeUtf8StringAsCharBuffer();
      if (compareCharsWithCharBufferFromEndToStart(XMLNS_NAMESPACE_NAME_CHARS)) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.xmlnsConnotBeBoundToPrefix"));
      }
      str1 = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
      this._namespaceNameIndex = this._v.namespaceName.add(str1);
      return str1;
    case 9: 
      this._octetBufferLength = EncodingConstants.XML_NAMESPACE_NAME_LENGTH;
      decodeUtf8StringAsCharBuffer();
      if (compareCharsWithCharBufferFromEndToStart(XML_NAMESPACE_NAME_CHARS)) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.illegalNamespaceName"));
      }
      str1 = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
      this._namespaceNameIndex = this._v.namespaceName.add(str1);
      return str1;
    case 1: 
      this._octetBufferLength = (read() + 65);
      str1 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._namespaceNameIndex = this._v.namespaceName.add(str1);
      return str1;
    case 2: 
      int j = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (j + 321);
      String str2 = this._stringInterning ? decodeUtf8StringAsString().intern() : decodeUtf8StringAsString();
      this._namespaceNameIndex = this._v.namespaceName.add(str2);
      return str2;
    case 10: 
      if (paramBoolean)
      {
        this._namespaceNameIndex = 0;
        return "http://www.w3.org/XML/1998/namespace";
      }
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.namespaceWithoutPrefix"));
    case 3: 
      this._namespaceNameIndex = (i & 0x3F);
      return this._v.namespaceName._array[(this._namespaceNameIndex - 1)];
    case 4: 
      this._namespaceNameIndex = (((i & 0x1F) << 8 | read()) + 64);
      return this._v.namespaceName._array[(this._namespaceNameIndex - 1)];
    case 5: 
      this._namespaceNameIndex = (((i & 0xF) << 16 | read() << 8 | read()) + 8256);
      return this._v.namespaceName._array[(this._namespaceNameIndex - 1)];
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingForNamespaceName"));
  }
  
  protected final String decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    int i = read();
    switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(i))
    {
    case 10: 
      if (paramBoolean)
      {
        this._namespaceNameIndex = 0;
        return "http://www.w3.org/XML/1998/namespace";
      }
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.namespaceWithoutPrefix"));
    case 3: 
      this._namespaceNameIndex = (i & 0x3F);
      return this._v.namespaceName._array[(this._namespaceNameIndex - 1)];
    case 4: 
      this._namespaceNameIndex = (((i & 0x1F) << 8 | read()) + 64);
      return this._v.namespaceName._array[(this._namespaceNameIndex - 1)];
    case 5: 
      this._namespaceNameIndex = (((i & 0xF) << 16 | read() << 8 | read()) + 8256);
      return this._v.namespaceName._array[(this._namespaceNameIndex - 1)];
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingForNamespaceName"));
  }
  
  private boolean compareCharsWithCharBufferFromEndToStart(char[] paramArrayOfChar)
  {
    int i = this._charBufferLength;
    do
    {
      i--;
      if (i < 0) {
        break;
      }
    } while (paramArrayOfChar[i] == this._charBuffer[i]);
    return false;
    return true;
  }
  
  protected final String decodeNonEmptyOctetStringOnSecondBitAsUtf8String()
    throws FastInfosetException, IOException
  {
    decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray();
    return new String(this._charBuffer, 0, this._charBufferLength);
  }
  
  protected final void decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray()
    throws FastInfosetException, IOException
  {
    decodeNonEmptyOctetStringLengthOnSecondBit();
    decodeUtf8StringAsCharBuffer();
  }
  
  protected final void decodeNonEmptyOctetStringLengthOnSecondBit()
    throws FastInfosetException, IOException
  {
    int i = read();
    switch (DecoderStateTables.ISTRING(i))
    {
    case 0: 
      this._octetBufferLength = (i + 1);
      break;
    case 1: 
      this._octetBufferLength = (read() + 65);
      break;
    case 2: 
      int j = read() << 24 | read() << 16 | read() << 8 | read();
      this._octetBufferLength = (j + 321);
      break;
    case 3: 
    case 4: 
    case 5: 
    default: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingNonEmptyOctet"));
    }
  }
  
  protected final int decodeIntegerIndexOnSecondBit()
    throws FastInfosetException, IOException
  {
    int i = read() | 0x80;
    switch (DecoderStateTables.ISTRING(i))
    {
    case 3: 
      return i & 0x3F;
    case 4: 
      return ((i & 0x1F) << 8 | read()) + 64;
    case 5: 
      return ((i & 0xF) << 16 | read() << 8 | read()) + 8256;
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIndexOnSecondBit"));
  }
  
  protected final void decodeHeader()
    throws FastInfosetException, IOException
  {
    if (!_isFastInfosetDocument()) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.notFIDocument"));
    }
  }
  
  protected final void decodeRestrictedAlphabetAsCharBuffer()
    throws FastInfosetException, IOException
  {
    if (this._identifier <= 1)
    {
      decodeFourBitAlphabetOctetsAsCharBuffer(com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table[this._identifier]);
    }
    else if (this._identifier >= 32)
    {
      CharArray localCharArray = this._v.restrictedAlphabet.get(this._identifier - 32);
      if (localCharArray == null) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.alphabetNotPresent", new Object[] { Integer.valueOf(this._identifier) }));
      }
      decodeAlphabetOctetsAsCharBuffer(localCharArray.ch);
    }
    else
    {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.alphabetIdentifiersReserved"));
    }
  }
  
  protected final String decodeRestrictedAlphabetAsString()
    throws FastInfosetException, IOException
  {
    decodeRestrictedAlphabetAsCharBuffer();
    return new String(this._charBuffer, 0, this._charBufferLength);
  }
  
  protected final String decodeRAOctetsAsString(char[] paramArrayOfChar)
    throws FastInfosetException, IOException
  {
    decodeAlphabetOctetsAsCharBuffer(paramArrayOfChar);
    return new String(this._charBuffer, 0, this._charBufferLength);
  }
  
  protected final void decodeFourBitAlphabetOctetsAsCharBuffer(char[] paramArrayOfChar)
    throws FastInfosetException, IOException
  {
    this._charBufferLength = 0;
    int i = this._octetBufferLength * 2;
    if (this._charBuffer.length < i) {
      this._charBuffer = new char[i];
    }
    int j = 0;
    for (int k = 0; k < this._octetBufferLength - 1; k++)
    {
      j = this._octetBuffer[(this._octetBufferStart++)] & 0xFF;
      this._charBuffer[(this._charBufferLength++)] = paramArrayOfChar[(j >> 4)];
      this._charBuffer[(this._charBufferLength++)] = paramArrayOfChar[(j & 0xF)];
    }
    j = this._octetBuffer[(this._octetBufferStart++)] & 0xFF;
    this._charBuffer[(this._charBufferLength++)] = paramArrayOfChar[(j >> 4)];
    j &= 0xF;
    if (j != 15) {
      this._charBuffer[(this._charBufferLength++)] = paramArrayOfChar[(j & 0xF)];
    }
  }
  
  protected final void decodeAlphabetOctetsAsCharBuffer(char[] paramArrayOfChar)
    throws FastInfosetException, IOException
  {
    if (paramArrayOfChar.length < 2) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.alphabetMustContain2orMoreChars"));
    }
    for (int i = 1; 1 << i <= paramArrayOfChar.length; i++) {}
    int j = (1 << i) - 1;
    int k = (this._octetBufferLength << 3) / i;
    if (k == 0) {
      throw new IOException("");
    }
    this._charBufferLength = 0;
    if (this._charBuffer.length < k) {
      this._charBuffer = new char[k];
    }
    resetBits();
    for (int m = 0; m < k; m++)
    {
      int n = readBits(i);
      if ((i < 8) && (n == j))
      {
        int i1 = m * i >>> 3;
        if (i1 == this._octetBufferLength - 1) {
          break;
        }
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.alphabetIncorrectlyTerminated"));
      }
      this._charBuffer[(this._charBufferLength++)] = paramArrayOfChar[n];
    }
  }
  
  private void resetBits()
  {
    this._bitsLeftInOctet = 0;
  }
  
  private int readBits(int paramInt)
    throws IOException
  {
    int i = 0;
    while (paramInt > 0)
    {
      if (this._bitsLeftInOctet == 0)
      {
        this._b = (this._octetBuffer[(this._octetBufferStart++)] & 0xFF);
        this._bitsLeftInOctet = 8;
      }
      int j = (this._b & 1 << --this._bitsLeftInOctet) > 0 ? 1 : 0;
      i |= j << --paramInt;
    }
    return i;
  }
  
  protected final void decodeUtf8StringAsCharBuffer()
    throws IOException
  {
    ensureOctetBufferSize();
    decodeUtf8StringIntoCharBuffer();
  }
  
  protected final void decodeUtf8StringAsCharBuffer(char[] paramArrayOfChar, int paramInt)
    throws IOException
  {
    ensureOctetBufferSize();
    decodeUtf8StringIntoCharBuffer(paramArrayOfChar, paramInt);
  }
  
  protected final String decodeUtf8StringAsString()
    throws IOException
  {
    decodeUtf8StringAsCharBuffer();
    return new String(this._charBuffer, 0, this._charBufferLength);
  }
  
  protected final void decodeUtf16StringAsCharBuffer()
    throws IOException
  {
    ensureOctetBufferSize();
    decodeUtf16StringIntoCharBuffer();
  }
  
  protected final String decodeUtf16StringAsString()
    throws IOException
  {
    decodeUtf16StringAsCharBuffer();
    return new String(this._charBuffer, 0, this._charBufferLength);
  }
  
  private void ensureOctetBufferSize()
    throws IOException
  {
    if (this._octetBufferEnd < this._octetBufferOffset + this._octetBufferLength)
    {
      int i = this._octetBufferEnd - this._octetBufferOffset;
      if (this._octetBuffer.length < this._octetBufferLength)
      {
        byte[] arrayOfByte = new byte[this._octetBufferLength];
        System.arraycopy(this._octetBuffer, this._octetBufferOffset, arrayOfByte, 0, i);
        this._octetBuffer = arrayOfByte;
      }
      else
      {
        System.arraycopy(this._octetBuffer, this._octetBufferOffset, this._octetBuffer, 0, i);
      }
      this._octetBufferOffset = 0;
      int j = this._s.read(this._octetBuffer, i, this._octetBuffer.length - i);
      if (j < 0) {
        throw new EOFException("Unexpeceted EOF");
      }
      this._octetBufferEnd = (i + j);
      if (this._octetBufferEnd < this._octetBufferLength) {
        repeatedRead();
      }
    }
  }
  
  private void repeatedRead()
    throws IOException
  {
    while (this._octetBufferEnd < this._octetBufferLength)
    {
      int i = this._s.read(this._octetBuffer, this._octetBufferEnd, this._octetBuffer.length - this._octetBufferEnd);
      if (i < 0) {
        throw new EOFException("Unexpeceted EOF");
      }
      this._octetBufferEnd += i;
    }
  }
  
  protected final void decodeUtf8StringIntoCharBuffer()
    throws IOException
  {
    if (this._charBuffer.length < this._octetBufferLength) {
      this._charBuffer = new char[this._octetBufferLength];
    }
    this._charBufferLength = 0;
    int i = this._octetBufferLength + this._octetBufferOffset;
    while (i != this._octetBufferOffset)
    {
      int j = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if (DecoderStateTables.UTF8(j) == 1) {
        this._charBuffer[(this._charBufferLength++)] = ((char)j);
      } else {
        decodeTwoToFourByteUtf8Character(j, i);
      }
    }
  }
  
  protected final void decodeUtf8StringIntoCharBuffer(char[] paramArrayOfChar, int paramInt)
    throws IOException
  {
    this._charBufferLength = paramInt;
    int i = this._octetBufferLength + this._octetBufferOffset;
    while (i != this._octetBufferOffset)
    {
      int j = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if (DecoderStateTables.UTF8(j) == 1) {
        paramArrayOfChar[(this._charBufferLength++)] = ((char)j);
      } else {
        decodeTwoToFourByteUtf8Character(paramArrayOfChar, j, i);
      }
    }
    this._charBufferLength -= paramInt;
  }
  
  private void decodeTwoToFourByteUtf8Character(int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    switch (DecoderStateTables.UTF8(paramInt1))
    {
    case 2: 
      if (paramInt2 == this._octetBufferOffset) {
        decodeUtf8StringLengthTooSmall();
      }
      i = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if ((i & 0xC0) != 128) {
        decodeUtf8StringIllegalState();
      }
      this._charBuffer[(this._charBufferLength++)] = ((char)((paramInt1 & 0x1F) << 6 | i & 0x3F));
      break;
    case 3: 
      i = decodeUtf8ThreeByteChar(paramInt2, paramInt1);
      if (XMLChar.isContent(i)) {
        this._charBuffer[(this._charBufferLength++)] = i;
      } else {
        decodeUtf8StringIllegalState();
      }
      break;
    case 4: 
      int j = decodeUtf8FourByteChar(paramInt2, paramInt1);
      if (XMLChar.isContent(j))
      {
        this._charBuffer[(this._charBufferLength++)] = this._utf8_highSurrogate;
        this._charBuffer[(this._charBufferLength++)] = this._utf8_lowSurrogate;
      }
      else
      {
        decodeUtf8StringIllegalState();
      }
      break;
    default: 
      decodeUtf8StringIllegalState();
    }
  }
  
  private void decodeTwoToFourByteUtf8Character(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    switch (DecoderStateTables.UTF8(paramInt1))
    {
    case 2: 
      if (paramInt2 == this._octetBufferOffset) {
        decodeUtf8StringLengthTooSmall();
      }
      i = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if ((i & 0xC0) != 128) {
        decodeUtf8StringIllegalState();
      }
      paramArrayOfChar[(this._charBufferLength++)] = ((char)((paramInt1 & 0x1F) << 6 | i & 0x3F));
      break;
    case 3: 
      i = decodeUtf8ThreeByteChar(paramInt2, paramInt1);
      if (XMLChar.isContent(i)) {
        paramArrayOfChar[(this._charBufferLength++)] = i;
      } else {
        decodeUtf8StringIllegalState();
      }
      break;
    case 4: 
      int j = decodeUtf8FourByteChar(paramInt2, paramInt1);
      if (XMLChar.isContent(j))
      {
        paramArrayOfChar[(this._charBufferLength++)] = this._utf8_highSurrogate;
        paramArrayOfChar[(this._charBufferLength++)] = this._utf8_lowSurrogate;
      }
      else
      {
        decodeUtf8StringIllegalState();
      }
      break;
    default: 
      decodeUtf8StringIllegalState();
    }
  }
  
  protected final void decodeUtf8NCNameIntoCharBuffer()
    throws IOException
  {
    this._charBufferLength = 0;
    if (this._charBuffer.length < this._octetBufferLength) {
      this._charBuffer = new char[this._octetBufferLength];
    }
    int i = this._octetBufferLength + this._octetBufferOffset;
    int j = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    if (DecoderStateTables.UTF8_NCNAME(j) == 0) {
      this._charBuffer[(this._charBufferLength++)] = ((char)j);
    } else {
      decodeUtf8NCNameStartTwoToFourByteCharacters(j, i);
    }
    while (i != this._octetBufferOffset)
    {
      j = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if (DecoderStateTables.UTF8_NCNAME(j) < 2) {
        this._charBuffer[(this._charBufferLength++)] = ((char)j);
      } else {
        decodeUtf8NCNameTwoToFourByteCharacters(j, i);
      }
    }
  }
  
  private void decodeUtf8NCNameStartTwoToFourByteCharacters(int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    int j;
    switch (DecoderStateTables.UTF8_NCNAME(paramInt1))
    {
    case 2: 
      if (paramInt2 == this._octetBufferOffset) {
        decodeUtf8StringLengthTooSmall();
      }
      i = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if ((i & 0xC0) != 128) {
        decodeUtf8StringIllegalState();
      }
      j = (char)((paramInt1 & 0x1F) << 6 | i & 0x3F);
      if (XMLChar.isNCNameStart(j)) {
        this._charBuffer[(this._charBufferLength++)] = j;
      } else {
        decodeUtf8NCNameIllegalState();
      }
      break;
    case 3: 
      i = decodeUtf8ThreeByteChar(paramInt2, paramInt1);
      if (XMLChar.isNCNameStart(i)) {
        this._charBuffer[(this._charBufferLength++)] = i;
      } else {
        decodeUtf8NCNameIllegalState();
      }
      break;
    case 4: 
      j = decodeUtf8FourByteChar(paramInt2, paramInt1);
      if (XMLChar.isNCNameStart(j))
      {
        this._charBuffer[(this._charBufferLength++)] = this._utf8_highSurrogate;
        this._charBuffer[(this._charBufferLength++)] = this._utf8_lowSurrogate;
      }
      else
      {
        decodeUtf8NCNameIllegalState();
      }
      break;
    case 1: 
    default: 
      decodeUtf8NCNameIllegalState();
    }
  }
  
  private void decodeUtf8NCNameTwoToFourByteCharacters(int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    int j;
    switch (DecoderStateTables.UTF8_NCNAME(paramInt1))
    {
    case 2: 
      if (paramInt2 == this._octetBufferOffset) {
        decodeUtf8StringLengthTooSmall();
      }
      i = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
      if ((i & 0xC0) != 128) {
        decodeUtf8StringIllegalState();
      }
      j = (char)((paramInt1 & 0x1F) << 6 | i & 0x3F);
      if (XMLChar.isNCName(j)) {
        this._charBuffer[(this._charBufferLength++)] = j;
      } else {
        decodeUtf8NCNameIllegalState();
      }
      break;
    case 3: 
      i = decodeUtf8ThreeByteChar(paramInt2, paramInt1);
      if (XMLChar.isNCName(i)) {
        this._charBuffer[(this._charBufferLength++)] = i;
      } else {
        decodeUtf8NCNameIllegalState();
      }
      break;
    case 4: 
      j = decodeUtf8FourByteChar(paramInt2, paramInt1);
      if (XMLChar.isNCName(j))
      {
        this._charBuffer[(this._charBufferLength++)] = this._utf8_highSurrogate;
        this._charBuffer[(this._charBufferLength++)] = this._utf8_lowSurrogate;
      }
      else
      {
        decodeUtf8NCNameIllegalState();
      }
      break;
    default: 
      decodeUtf8NCNameIllegalState();
    }
  }
  
  private char decodeUtf8ThreeByteChar(int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt1 == this._octetBufferOffset) {
      decodeUtf8StringLengthTooSmall();
    }
    int i = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    if (((i & 0xC0) != 128) || ((paramInt2 == 237) && (i >= 160)) || (((paramInt2 & 0xF) == 0) && ((i & 0x20) == 0))) {
      decodeUtf8StringIllegalState();
    }
    if (paramInt1 == this._octetBufferOffset) {
      decodeUtf8StringLengthTooSmall();
    }
    int j = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    if ((j & 0xC0) != 128) {
      decodeUtf8StringIllegalState();
    }
    return (char)((paramInt2 & 0xF) << 12 | (i & 0x3F) << 6 | j & 0x3F);
  }
  
  private int decodeUtf8FourByteChar(int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt1 == this._octetBufferOffset) {
      decodeUtf8StringLengthTooSmall();
    }
    int i = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    if (((i & 0xC0) != 128) || (((i & 0x30) == 0) && ((paramInt2 & 0x7) == 0))) {
      decodeUtf8StringIllegalState();
    }
    if (paramInt1 == this._octetBufferOffset) {
      decodeUtf8StringLengthTooSmall();
    }
    int j = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    if ((j & 0xC0) != 128) {
      decodeUtf8StringIllegalState();
    }
    if (paramInt1 == this._octetBufferOffset) {
      decodeUtf8StringLengthTooSmall();
    }
    int k = this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    if ((k & 0xC0) != 128) {
      decodeUtf8StringIllegalState();
    }
    int m = paramInt2 << 2 & 0x1C | i >> 4 & 0x3;
    if (m > 16) {
      decodeUtf8StringIllegalState();
    }
    int n = m - 1;
    this._utf8_highSurrogate = ((char)(0xD800 | n << 6 & 0x3C0 | i << 2 & 0x3C | j >> 4 & 0x3));
    this._utf8_lowSurrogate = ((char)(0xDC00 | j << 6 & 0x3C0 | k & 0x3F));
    return XMLChar.supplemental(this._utf8_highSurrogate, this._utf8_lowSurrogate);
  }
  
  private void decodeUtf8StringLengthTooSmall()
    throws IOException
  {
    throw new IOException(CommonResourceBundle.getInstance().getString("message.deliminatorTooSmall"));
  }
  
  private void decodeUtf8StringIllegalState()
    throws IOException
  {
    throw new IOException(CommonResourceBundle.getInstance().getString("message.UTF8Encoded"));
  }
  
  private void decodeUtf8NCNameIllegalState()
    throws IOException
  {
    throw new IOException(CommonResourceBundle.getInstance().getString("message.UTF8EncodedNCName"));
  }
  
  private void decodeUtf16StringIntoCharBuffer()
    throws IOException
  {
    this._charBufferLength = (this._octetBufferLength / 2);
    if (this._charBuffer.length < this._charBufferLength) {
      this._charBuffer = new char[this._charBufferLength];
    }
    for (int i = 0; i < this._charBufferLength; i++)
    {
      int j = (char)(read() << 8 | read());
      this._charBuffer[i] = j;
    }
  }
  
  protected String createQualifiedNameString(String paramString)
  {
    return createQualifiedNameString(XMLNS_NAMESPACE_PREFIX_CHARS, paramString);
  }
  
  protected String createQualifiedNameString(char[] paramArrayOfChar, String paramString)
  {
    int i = paramArrayOfChar.length;
    int j = paramString.length();
    int k = i + j + 1;
    if (k < this._charBuffer.length)
    {
      System.arraycopy(paramArrayOfChar, 0, this._charBuffer, 0, i);
      this._charBuffer[i] = ':';
      paramString.getChars(0, j, this._charBuffer, i + 1);
      return new String(this._charBuffer, 0, k);
    }
    StringBuilder localStringBuilder = new StringBuilder(new String(paramArrayOfChar));
    localStringBuilder.append(':');
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  protected final int read()
    throws IOException
  {
    if (this._octetBufferOffset < this._octetBufferEnd) {
      return this._octetBuffer[(this._octetBufferOffset++)] & 0xFF;
    }
    this._octetBufferEnd = this._s.read(this._octetBuffer);
    if (this._octetBufferEnd < 0) {
      throw new EOFException(CommonResourceBundle.getInstance().getString("message.EOF"));
    }
    this._octetBufferOffset = 1;
    return this._octetBuffer[0] & 0xFF;
  }
  
  protected final void closeIfRequired()
    throws IOException
  {
    if ((this._s != null) && (this._needForceStreamClose)) {
      this._s.close();
    }
  }
  
  protected final int peek()
    throws IOException
  {
    return peek(null);
  }
  
  protected final int peek(OctetBufferListener paramOctetBufferListener)
    throws IOException
  {
    if (this._octetBufferOffset < this._octetBufferEnd) {
      return this._octetBuffer[this._octetBufferOffset] & 0xFF;
    }
    if (paramOctetBufferListener != null) {
      paramOctetBufferListener.onBeforeOctetBufferOverwrite();
    }
    this._octetBufferEnd = this._s.read(this._octetBuffer);
    if (this._octetBufferEnd < 0) {
      throw new EOFException(CommonResourceBundle.getInstance().getString("message.EOF"));
    }
    this._octetBufferOffset = 0;
    return this._octetBuffer[0] & 0xFF;
  }
  
  protected final int peek2(OctetBufferListener paramOctetBufferListener)
    throws IOException
  {
    if (this._octetBufferOffset + 1 < this._octetBufferEnd) {
      return this._octetBuffer[(this._octetBufferOffset + 1)] & 0xFF;
    }
    if (paramOctetBufferListener != null) {
      paramOctetBufferListener.onBeforeOctetBufferOverwrite();
    }
    int i = 0;
    if (this._octetBufferOffset < this._octetBufferEnd)
    {
      this._octetBuffer[0] = this._octetBuffer[this._octetBufferOffset];
      i = 1;
    }
    this._octetBufferEnd = this._s.read(this._octetBuffer, i, this._octetBuffer.length - i);
    if (this._octetBufferEnd < 0) {
      throw new EOFException(CommonResourceBundle.getInstance().getString("message.EOF"));
    }
    this._octetBufferOffset = 0;
    return this._octetBuffer[1] & 0xFF;
  }
  
  protected final boolean _isFastInfosetDocument()
    throws IOException
  {
    peek();
    this._octetBufferLength = EncodingConstants.BINARY_HEADER.length;
    ensureOctetBufferSize();
    this._octetBufferOffset += this._octetBufferLength;
    if ((this._octetBuffer[0] != EncodingConstants.BINARY_HEADER[0]) || (this._octetBuffer[1] != EncodingConstants.BINARY_HEADER[1]) || (this._octetBuffer[2] != EncodingConstants.BINARY_HEADER[2]) || (this._octetBuffer[3] != EncodingConstants.BINARY_HEADER[3]))
    {
      for (int i = 0; i < EncodingConstants.XML_DECLARATION_VALUES.length; i++)
      {
        this._octetBufferLength = (EncodingConstants.XML_DECLARATION_VALUES[i].length - this._octetBufferOffset);
        ensureOctetBufferSize();
        this._octetBufferOffset += this._octetBufferLength;
        if (arrayEquals(this._octetBuffer, 0, EncodingConstants.XML_DECLARATION_VALUES[i], EncodingConstants.XML_DECLARATION_VALUES[i].length))
        {
          this._octetBufferLength = EncodingConstants.BINARY_HEADER.length;
          ensureOctetBufferSize();
          return (this._octetBuffer[(this._octetBufferOffset++)] == EncodingConstants.BINARY_HEADER[0]) && (this._octetBuffer[(this._octetBufferOffset++)] == EncodingConstants.BINARY_HEADER[1]) && (this._octetBuffer[(this._octetBufferOffset++)] == EncodingConstants.BINARY_HEADER[2]) && (this._octetBuffer[(this._octetBufferOffset++)] == EncodingConstants.BINARY_HEADER[3]);
        }
      }
      return false;
    }
    return true;
  }
  
  private boolean arrayEquals(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      if (paramArrayOfByte1[(paramInt1 + i)] != paramArrayOfByte2[i]) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isFastInfosetDocument(InputStream paramInputStream)
    throws IOException
  {
    int i = 4;
    byte[] arrayOfByte = new byte[4];
    int j = paramInputStream.read(arrayOfByte);
    return (j >= 4) && (arrayOfByte[0] == EncodingConstants.BINARY_HEADER[0]) && (arrayOfByte[1] == EncodingConstants.BINARY_HEADER[1]) && (arrayOfByte[2] == EncodingConstants.BINARY_HEADER[2]) && (arrayOfByte[3] == EncodingConstants.BINARY_HEADER[3]);
  }
  
  static
  {
    String str = System.getProperty("com.sun.xml.internal.fastinfoset.parser.string-interning", Boolean.toString(_stringInterningSystemDefault));
    _stringInterningSystemDefault = Boolean.valueOf(str).booleanValue();
    str = System.getProperty("com.sun.xml.internal.fastinfoset.parser.buffer-size", Integer.toString(_bufferSizeSystemDefault));
    try
    {
      int i = Integer.valueOf(str).intValue();
      if (i > 0) {
        _bufferSizeSystemDefault = i;
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
  }
  
  protected class EncodingAlgorithmInputStream
    extends InputStream
  {
    protected EncodingAlgorithmInputStream() {}
    
    public int read()
      throws IOException
    {
      if (Decoder.this._octetBufferStart < Decoder.this._octetBufferOffset) {
        return Decoder.this._octetBuffer[(Decoder.this._octetBufferStart++)] & 0xFF;
      }
      return -1;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramArrayOfByte == null) {
        throw new NullPointerException();
      }
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return 0;
      }
      int i = Decoder.this._octetBufferStart + paramInt2;
      if (i < Decoder.this._octetBufferOffset)
      {
        System.arraycopy(Decoder.this._octetBuffer, Decoder.this._octetBufferStart, paramArrayOfByte, paramInt1, paramInt2);
        Decoder.this._octetBufferStart = i;
        return paramInt2;
      }
      if (Decoder.this._octetBufferStart < Decoder.this._octetBufferOffset)
      {
        int j = Decoder.this._octetBufferOffset - Decoder.this._octetBufferStart;
        System.arraycopy(Decoder.this._octetBuffer, Decoder.this._octetBufferStart, paramArrayOfByte, paramInt1, j);
        Decoder.this._octetBufferStart += j;
        return j;
      }
      return -1;
    }
  }
}
