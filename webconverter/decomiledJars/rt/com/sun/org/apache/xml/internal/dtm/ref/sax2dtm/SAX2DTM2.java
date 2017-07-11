package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.InternalAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.RootIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.SingletonIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMStringPool;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.ExtendedType;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringDefault;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.Vector;
import javax.xml.transform.Source;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SAX2DTM2
  extends SAX2DTM
{
  private int[] m_exptype_map0;
  private int[] m_nextsib_map0;
  private int[] m_firstch_map0;
  private int[] m_parent_map0;
  private int[][] m_exptype_map;
  private int[][] m_nextsib_map;
  private int[][] m_firstch_map;
  private int[][] m_parent_map;
  protected ExtendedType[] m_extendedTypes;
  protected Vector m_values;
  private int m_valueIndex = 0;
  private int m_maxNodeIndex;
  protected int m_SHIFT;
  protected int m_MASK;
  protected int m_blocksize;
  protected static final int TEXT_LENGTH_BITS = 10;
  protected static final int TEXT_OFFSET_BITS = 21;
  protected static final int TEXT_LENGTH_MAX = 1023;
  protected static final int TEXT_OFFSET_MAX = 2097151;
  protected boolean m_buildIdIndex = true;
  private static final String EMPTY_STR = "";
  private static final XMLString EMPTY_XML_STR = new XMLStringDefault("");
  
  public SAX2DTM2(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    this(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean, 512, true, true, false);
  }
  
  public SAX2DTM2(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean4);
    for (int i = 0; paramInt2 >>>= 1 != 0; i++) {}
    this.m_blocksize = (1 << i);
    this.m_SHIFT = i;
    this.m_MASK = (this.m_blocksize - 1);
    this.m_buildIdIndex = paramBoolean3;
    this.m_values = new Vector(32, 512);
    this.m_maxNodeIndex = 65536;
    this.m_exptype_map0 = this.m_exptype.getMap0();
    this.m_nextsib_map0 = this.m_nextsib.getMap0();
    this.m_firstch_map0 = this.m_firstch.getMap0();
    this.m_parent_map0 = this.m_parent.getMap0();
  }
  
  public final int _exptype(int paramInt)
  {
    return this.m_exptype.elementAt(paramInt);
  }
  
  public final int _exptype2(int paramInt)
  {
    if (paramInt < this.m_blocksize) {
      return this.m_exptype_map0[paramInt];
    }
    return this.m_exptype_map[(paramInt >>> this.m_SHIFT)][(paramInt & this.m_MASK)];
  }
  
  public final int _nextsib2(int paramInt)
  {
    if (paramInt < this.m_blocksize) {
      return this.m_nextsib_map0[paramInt];
    }
    return this.m_nextsib_map[(paramInt >>> this.m_SHIFT)][(paramInt & this.m_MASK)];
  }
  
  public final int _firstch2(int paramInt)
  {
    if (paramInt < this.m_blocksize) {
      return this.m_firstch_map0[paramInt];
    }
    return this.m_firstch_map[(paramInt >>> this.m_SHIFT)][(paramInt & this.m_MASK)];
  }
  
  public final int _parent2(int paramInt)
  {
    if (paramInt < this.m_blocksize) {
      return this.m_parent_map0[paramInt];
    }
    return this.m_parent_map[(paramInt >>> this.m_SHIFT)][(paramInt & this.m_MASK)];
  }
  
  public final int _type2(int paramInt)
  {
    int i;
    if (paramInt < this.m_blocksize) {
      i = this.m_exptype_map0[paramInt];
    } else {
      i = this.m_exptype_map[(paramInt >>> this.m_SHIFT)][(paramInt & this.m_MASK)];
    }
    if (-1 != i) {
      return this.m_extendedTypes[i].getNodeType();
    }
    return -1;
  }
  
  public final int getExpandedTypeID2(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i != -1)
    {
      if (i < this.m_blocksize) {
        return this.m_exptype_map0[i];
      }
      return this.m_exptype_map[(i >>> this.m_SHIFT)][(i & this.m_MASK)];
    }
    return -1;
  }
  
  public final int _exptype2Type(int paramInt)
  {
    if (-1 != paramInt) {
      return this.m_extendedTypes[paramInt].getNodeType();
    }
    return -1;
  }
  
  public int getIdForNamespace(String paramString)
  {
    int i = this.m_values.indexOf(paramString);
    if (i < 0)
    {
      this.m_values.addElement(paramString);
      return this.m_valueIndex++;
    }
    return i;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    charactersFlush();
    int i = this.m_expandedNameTable.getExpandedTypeID(paramString1, paramString2, 1);
    int j = paramString3.length() != paramString2.length() ? this.m_valuesOrPrefixes.stringToIndex(paramString3) : 0;
    int k = addNode(1, i, this.m_parents.peek(), this.m_previous, j, true);
    if (this.m_indexing) {
      indexNode(i, k);
    }
    this.m_parents.push(k);
    String str1 = this.m_contextIndexes.peek();
    String str2 = this.m_prefixMappings.size();
    String str3;
    if (!this.m_pastFirstElement)
    {
      str3 = "xml";
      str4 = "http://www.w3.org/XML/1998/namespace";
      i = this.m_expandedNameTable.getExpandedTypeID(null, str3, 13);
      this.m_values.addElement(str4);
      int n = this.m_valueIndex++;
      addNode(13, i, k, -1, n, false);
      this.m_pastFirstElement = true;
    }
    for (String str4 = str1; str4 < str2; str4 += 2)
    {
      str3 = (String)this.m_prefixMappings.elementAt(str4);
      if (str3 != null)
      {
        String str5 = (String)this.m_prefixMappings.elementAt(str4 + 1);
        i = this.m_expandedNameTable.getExpandedTypeID(null, str3, 13);
        this.m_values.addElement(str5);
        int i2 = this.m_valueIndex++;
        addNode(13, i, k, -1, i2, false);
      }
    }
    int m = paramAttributes.getLength();
    for (int i1 = 0; i1 < m; i1++)
    {
      String str6 = paramAttributes.getURI(i1);
      String str7 = paramAttributes.getQName(i1);
      String str8 = paramAttributes.getValue(i1);
      String str9 = paramAttributes.getLocalName(i1);
      int i3;
      if ((null != str7) && ((str7.equals("xmlns")) || (str7.startsWith("xmlns:"))))
      {
        str3 = getPrefix(str7, str6);
        if (declAlreadyDeclared(str3)) {
          continue;
        }
        i3 = 13;
      }
      else
      {
        i3 = 2;
        if ((this.m_buildIdIndex) && (paramAttributes.getType(i1).equalsIgnoreCase("ID"))) {
          setIDAttribute(str8, k);
        }
      }
      if (null == str8) {
        str8 = "";
      }
      this.m_values.addElement(str8);
      int i4 = this.m_valueIndex++;
      if (str9.length() != str7.length())
      {
        j = this.m_valuesOrPrefixes.stringToIndex(str7);
        int i5 = this.m_data.size();
        this.m_data.addElement(j);
        this.m_data.addElement(i4);
        i4 = -i5;
      }
      i = this.m_expandedNameTable.getExpandedTypeID(str6, str9, i3);
      addNode(i3, i, k, -1, i4, false);
    }
    if (null != this.m_wsfilter)
    {
      i1 = this.m_wsfilter.getShouldStripSpace(makeNodeHandle(k), this);
      boolean bool = 2 == i1 ? true : 3 == i1 ? getShouldStripWhitespace() : false;
      pushShouldStripWhitespace(bool);
    }
    this.m_previous = -1;
    this.m_contextIndexes.push(this.m_prefixMappings.size());
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    charactersFlush();
    this.m_contextIndexes.quickPop(1);
    int i = this.m_contextIndexes.peek();
    if (i != this.m_prefixMappings.size()) {
      this.m_prefixMappings.setSize(i);
    }
    this.m_previous = this.m_parents.pop();
    popShouldStripWhitespace();
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this.m_insideDTD) {
      return;
    }
    charactersFlush();
    this.m_values.addElement(new String(paramArrayOfChar, paramInt1, paramInt2));
    int i = this.m_valueIndex++;
    this.m_previous = addNode(8, 8, this.m_parents.peek(), this.m_previous, i, false);
  }
  
  public void startDocument()
    throws SAXException
  {
    int i = addNode(9, 9, -1, -1, 0, true);
    this.m_parents.push(i);
    this.m_previous = -1;
    this.m_contextIndexes.push(this.m_prefixMappings.size());
  }
  
  public void endDocument()
    throws SAXException
  {
    super.endDocument();
    this.m_exptype.addElement(-1);
    this.m_parent.addElement(-1);
    this.m_nextsib.addElement(-1);
    this.m_firstch.addElement(-1);
    this.m_extendedTypes = this.m_expandedNameTable.getExtendedTypes();
    this.m_exptype_map = this.m_exptype.getMap();
    this.m_nextsib_map = this.m_nextsib.getMap();
    this.m_firstch_map = this.m_firstch.getMap();
    this.m_parent_map = this.m_parent.getMap();
  }
  
  protected final int addNode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    int i = this.m_size++;
    if (i == this.m_maxNodeIndex)
    {
      addNewDTMID(i);
      this.m_maxNodeIndex += 65536;
    }
    this.m_firstch.addElement(-1);
    this.m_nextsib.addElement(-1);
    this.m_parent.addElement(paramInt3);
    this.m_exptype.addElement(paramInt2);
    this.m_dataOrQName.addElement(paramInt5);
    if (this.m_prevsib != null) {
      this.m_prevsib.addElement(paramInt4);
    }
    if ((this.m_locator != null) && (this.m_useSourceLocationProperty)) {
      setSourceLocation();
    }
    switch (paramInt1)
    {
    case 13: 
      declareNamespaceInContext(paramInt3, i);
      break;
    case 2: 
      break;
    default: 
      if (-1 != paramInt4) {
        this.m_nextsib.setElementAt(i, paramInt4);
      } else if (-1 != paramInt3) {
        this.m_firstch.setElementAt(i, paramInt3);
      }
      break;
    }
    return i;
  }
  
  protected final void charactersFlush()
  {
    if (this.m_textPendingStart >= 0)
    {
      int i = this.m_chars.size() - this.m_textPendingStart;
      boolean bool = false;
      if (getShouldStripWhitespace()) {
        bool = this.m_chars.isWhitespace(this.m_textPendingStart, i);
      }
      if (bool) {
        this.m_chars.setLength(this.m_textPendingStart);
      } else if (i > 0) {
        if ((i <= 1023) && (this.m_textPendingStart <= 2097151))
        {
          this.m_previous = addNode(this.m_coalescedTextType, 3, this.m_parents.peek(), this.m_previous, i + (this.m_textPendingStart << 10), false);
        }
        else
        {
          int j = this.m_data.size();
          this.m_previous = addNode(this.m_coalescedTextType, 3, this.m_parents.peek(), this.m_previous, -j, false);
          this.m_data.addElement(this.m_textPendingStart);
          this.m_data.addElement(i);
        }
      }
      this.m_textPendingStart = -1;
      this.m_textType = (this.m_coalescedTextType = 3);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    charactersFlush();
    int i = this.m_data.size();
    this.m_previous = addNode(7, 7, this.m_parents.peek(), this.m_previous, -i, false);
    this.m_data.addElement(this.m_valuesOrPrefixes.stringToIndex(paramString1));
    this.m_values.addElement(paramString2);
    this.m_data.addElement(this.m_valueIndex++);
  }
  
  public final int getFirstAttribute(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return -1;
    }
    int j = _type2(i);
    if (1 == j) {
      for (;;)
      {
        i++;
        j = _type2(i);
        if (j == 2) {
          return makeNodeHandle(i);
        }
        if (13 != j) {
          break;
        }
      }
    }
    return -1;
  }
  
  protected int getFirstAttributeIdentity(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    int i = _type2(paramInt);
    if (1 == i) {
      for (;;)
      {
        paramInt++;
        i = _type2(paramInt);
        if (i == 2) {
          return paramInt;
        }
        if (13 != i) {
          break;
        }
      }
    }
    return -1;
  }
  
  protected int getNextAttributeIdentity(int paramInt)
  {
    for (;;)
    {
      paramInt++;
      int i = _type2(paramInt);
      if (i == 2) {
        return paramInt;
      }
      if (i != 13) {
        break;
      }
    }
    return -1;
  }
  
  protected final int getTypedAttribute(int paramInt1, int paramInt2)
  {
    int i = makeNodeIdentity(paramInt1);
    if (i == -1) {
      return -1;
    }
    int j = _type2(i);
    if (1 == j) {
      for (;;)
      {
        i++;
        int k = _exptype2(i);
        if (k != -1) {
          j = this.m_extendedTypes[k].getNodeType();
        } else {
          return -1;
        }
        if (j == 2)
        {
          if (k == paramInt2) {
            return makeNodeHandle(i);
          }
        }
        else if (13 != j) {
          break;
        }
      }
    }
    return -1;
  }
  
  public String getLocalName(int paramInt)
  {
    int i = _exptype(makeNodeIdentity(paramInt));
    if (i == 7)
    {
      int j = _dataOrQName(makeNodeIdentity(paramInt));
      j = this.m_data.elementAt(-j);
      return this.m_valuesOrPrefixes.indexToString(j);
    }
    return this.m_expandedNameTable.getLocalName(i);
  }
  
  public final String getNodeNameX(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    if (j == 7)
    {
      int k = _dataOrQName(i);
      k = this.m_data.elementAt(-k);
      return this.m_valuesOrPrefixes.indexToString(k);
    }
    ExtendedType localExtendedType = this.m_extendedTypes[j];
    if (localExtendedType.getNamespace().length() == 0) {
      return localExtendedType.getLocalName();
    }
    int m = this.m_dataOrQName.elementAt(i);
    if (m == 0) {
      return localExtendedType.getLocalName();
    }
    if (m < 0)
    {
      m = -m;
      m = this.m_data.elementAt(m);
    }
    return this.m_valuesOrPrefixes.indexToString(m);
  }
  
  public String getNodeName(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    ExtendedType localExtendedType = this.m_extendedTypes[j];
    if (localExtendedType.getNamespace().length() == 0)
    {
      k = localExtendedType.getNodeType();
      String str = localExtendedType.getLocalName();
      if (k == 13)
      {
        if (str.length() == 0) {
          return "xmlns";
        }
        return "xmlns:" + str;
      }
      if (k == 7)
      {
        int m = _dataOrQName(i);
        m = this.m_data.elementAt(-m);
        return this.m_valuesOrPrefixes.indexToString(m);
      }
      if (str.length() == 0) {
        return getFixedNames(k);
      }
      return str;
    }
    int k = this.m_dataOrQName.elementAt(i);
    if (k == 0) {
      return localExtendedType.getLocalName();
    }
    if (k < 0)
    {
      k = -k;
      k = this.m_data.elementAt(k);
    }
    return this.m_valuesOrPrefixes.indexToString(k);
  }
  
  public XMLString getStringValue(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return EMPTY_XML_STR;
    }
    int j = _type2(i);
    if ((j == 1) || (j == 9))
    {
      k = i;
      i = _firstch2(i);
      if (-1 != i)
      {
        int m = -1;
        int n = 0;
        do
        {
          j = _exptype2(i);
          if ((j == 3) || (j == 4))
          {
            int i1 = this.m_dataOrQName.elementAt(i);
            if (i1 >= 0)
            {
              if (-1 == m) {
                m = i1 >>> 10;
              }
              n += (i1 & 0x3FF);
            }
            else
            {
              if (-1 == m) {
                m = this.m_data.elementAt(-i1);
              }
              n += this.m_data.elementAt(-i1 + 1);
            }
          }
          i++;
        } while (_parent2(i) >= k);
        if (n > 0)
        {
          if (this.m_xstrf != null) {
            return this.m_xstrf.newstr(this.m_chars, m, n);
          }
          return new XMLStringDefault(this.m_chars.getString(m, n));
        }
        return EMPTY_XML_STR;
      }
      return EMPTY_XML_STR;
    }
    if ((3 == j) || (4 == j))
    {
      k = this.m_dataOrQName.elementAt(i);
      if (k >= 0)
      {
        if (this.m_xstrf != null) {
          return this.m_xstrf.newstr(this.m_chars, k >>> 10, k & 0x3FF);
        }
        return new XMLStringDefault(this.m_chars.getString(k >>> 10, k & 0x3FF));
      }
      if (this.m_xstrf != null) {
        return this.m_xstrf.newstr(this.m_chars, this.m_data.elementAt(-k), this.m_data.elementAt(-k + 1));
      }
      return new XMLStringDefault(this.m_chars.getString(this.m_data.elementAt(-k), this.m_data.elementAt(-k + 1)));
    }
    int k = this.m_dataOrQName.elementAt(i);
    if (k < 0)
    {
      k = -k;
      k = this.m_data.elementAt(k + 1);
    }
    if (this.m_xstrf != null) {
      return this.m_xstrf.newstr((String)this.m_values.elementAt(k));
    }
    return new XMLStringDefault((String)this.m_values.elementAt(k));
  }
  
  public final String getStringValueX(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return "";
    }
    int j = _type2(i);
    if ((j == 1) || (j == 9))
    {
      k = i;
      i = _firstch2(i);
      if (-1 != i)
      {
        int m = -1;
        int n = 0;
        do
        {
          j = _exptype2(i);
          if ((j == 3) || (j == 4))
          {
            int i1 = this.m_dataOrQName.elementAt(i);
            if (i1 >= 0)
            {
              if (-1 == m) {
                m = i1 >>> 10;
              }
              n += (i1 & 0x3FF);
            }
            else
            {
              if (-1 == m) {
                m = this.m_data.elementAt(-i1);
              }
              n += this.m_data.elementAt(-i1 + 1);
            }
          }
          i++;
        } while (_parent2(i) >= k);
        if (n > 0) {
          return this.m_chars.getString(m, n);
        }
        return "";
      }
      return "";
    }
    if ((3 == j) || (4 == j))
    {
      k = this.m_dataOrQName.elementAt(i);
      if (k >= 0) {
        return this.m_chars.getString(k >>> 10, k & 0x3FF);
      }
      return this.m_chars.getString(this.m_data.elementAt(-k), this.m_data.elementAt(-k + 1));
    }
    int k = this.m_dataOrQName.elementAt(i);
    if (k < 0)
    {
      k = -k;
      k = this.m_data.elementAt(k + 1);
    }
    return (String)this.m_values.elementAt(k);
  }
  
  public String getStringValue()
  {
    int i = _firstch2(0);
    if (i == -1) {
      return "";
    }
    if ((_exptype2(i) == 3) && (_nextsib2(i) == -1))
    {
      int j = this.m_dataOrQName.elementAt(i);
      if (j >= 0) {
        return this.m_chars.getString(j >>> 10, j & 0x3FF);
      }
      return this.m_chars.getString(this.m_data.elementAt(-j), this.m_data.elementAt(-j + 1));
    }
    return getStringValueX(getDocument());
  }
  
  public final void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return;
    }
    int j = _type2(i);
    int k;
    if ((j == 1) || (j == 9))
    {
      k = i;
      i = _firstch2(i);
      if (-1 != i)
      {
        int m = -1;
        int n = 0;
        do
        {
          j = _exptype2(i);
          if ((j == 3) || (j == 4))
          {
            int i1 = this.m_dataOrQName.elementAt(i);
            if (i1 >= 0)
            {
              if (-1 == m) {
                m = i1 >>> 10;
              }
              n += (i1 & 0x3FF);
            }
            else
            {
              if (-1 == m) {
                m = this.m_data.elementAt(-i1);
              }
              n += this.m_data.elementAt(-i1 + 1);
            }
          }
          i++;
        } while (_parent2(i) >= k);
        if (n > 0) {
          if (paramBoolean) {
            this.m_chars.sendNormalizedSAXcharacters(paramContentHandler, m, n);
          } else {
            this.m_chars.sendSAXcharacters(paramContentHandler, m, n);
          }
        }
      }
    }
    else if ((3 == j) || (4 == j))
    {
      k = this.m_dataOrQName.elementAt(i);
      if (k >= 0)
      {
        if (paramBoolean) {
          this.m_chars.sendNormalizedSAXcharacters(paramContentHandler, k >>> 10, k & 0x3FF);
        } else {
          this.m_chars.sendSAXcharacters(paramContentHandler, k >>> 10, k & 0x3FF);
        }
      }
      else if (paramBoolean) {
        this.m_chars.sendNormalizedSAXcharacters(paramContentHandler, this.m_data.elementAt(-k), this.m_data.elementAt(-k + 1));
      } else {
        this.m_chars.sendSAXcharacters(paramContentHandler, this.m_data.elementAt(-k), this.m_data.elementAt(-k + 1));
      }
    }
    else
    {
      k = this.m_dataOrQName.elementAt(i);
      if (k < 0)
      {
        k = -k;
        k = this.m_data.elementAt(k + 1);
      }
      String str = (String)this.m_values.elementAt(k);
      if (paramBoolean) {
        FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), paramContentHandler);
      } else {
        paramContentHandler.characters(str.toCharArray(), 0, str.length());
      }
    }
  }
  
  public String getNodeValue(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _type2(i);
    if ((j == 3) || (j == 4))
    {
      k = _dataOrQName(i);
      if (k > 0) {
        return this.m_chars.getString(k >>> 10, k & 0x3FF);
      }
      return this.m_chars.getString(this.m_data.elementAt(-k), this.m_data.elementAt(-k + 1));
    }
    if ((1 == j) || (11 == j) || (9 == j)) {
      return null;
    }
    int k = this.m_dataOrQName.elementAt(i);
    if (k < 0)
    {
      k = -k;
      k = this.m_data.elementAt(k + 1);
    }
    return (String)this.m_values.elementAt(k);
  }
  
  protected final void copyTextNode(int paramInt, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    if (paramInt != -1)
    {
      int i = this.m_dataOrQName.elementAt(paramInt);
      if (i >= 0) {
        this.m_chars.sendSAXcharacters(paramSerializationHandler, i >>> 10, i & 0x3FF);
      } else {
        this.m_chars.sendSAXcharacters(paramSerializationHandler, this.m_data.elementAt(-i), this.m_data.elementAt(-i + 1));
      }
    }
  }
  
  protected final String copyElement(int paramInt1, int paramInt2, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    ExtendedType localExtendedType = this.m_extendedTypes[paramInt2];
    String str1 = localExtendedType.getNamespace();
    String str2 = localExtendedType.getLocalName();
    if (str1.length() == 0)
    {
      paramSerializationHandler.startElement(str2);
      return str2;
    }
    int i = this.m_dataOrQName.elementAt(paramInt1);
    if (i == 0)
    {
      paramSerializationHandler.startElement(str2);
      paramSerializationHandler.namespaceAfterStartElement("", str1);
      return str2;
    }
    if (i < 0)
    {
      i = -i;
      i = this.m_data.elementAt(i);
    }
    String str3 = this.m_valuesOrPrefixes.indexToString(i);
    paramSerializationHandler.startElement(str3);
    int j = str3.indexOf(':');
    String str4;
    if (j > 0) {
      str4 = str3.substring(0, j);
    } else {
      str4 = null;
    }
    paramSerializationHandler.namespaceAfterStartElement(str4, str1);
    return str3;
  }
  
  protected final void copyNS(int paramInt, SerializationHandler paramSerializationHandler, boolean paramBoolean)
    throws SAXException
  {
    if ((this.m_namespaceDeclSetElements != null) && (this.m_namespaceDeclSetElements.size() == 1) && (this.m_namespaceDeclSets != null) && (((SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(0)).size() == 1)) {
      return;
    }
    SuballocatedIntVector localSuballocatedIntVector = null;
    int i;
    if (paramBoolean)
    {
      localSuballocatedIntVector = findNamespaceContext(paramInt);
      if ((localSuballocatedIntVector == null) || (localSuballocatedIntVector.size() < 1)) {
        return;
      }
      i = makeNodeIdentity(localSuballocatedIntVector.elementAt(0));
    }
    else
    {
      i = getNextNamespaceNode2(paramInt);
    }
    int j = 1;
    while (i != -1)
    {
      int k = _exptype2(i);
      String str1 = this.m_extendedTypes[k].getLocalName();
      int m = this.m_dataOrQName.elementAt(i);
      if (m < 0)
      {
        m = -m;
        m = this.m_data.elementAt(m + 1);
      }
      String str2 = (String)this.m_values.elementAt(m);
      paramSerializationHandler.namespaceAfterStartElement(str1, str2);
      if (paramBoolean)
      {
        if (j < localSuballocatedIntVector.size())
        {
          i = makeNodeIdentity(localSuballocatedIntVector.elementAt(j));
          j++;
        }
      }
      else {
        i = getNextNamespaceNode2(i);
      }
    }
  }
  
  protected final int getNextNamespaceNode2(int paramInt)
  {
    int i;
    while ((i = _type2(++paramInt)) == 2) {}
    if (i == 13) {
      return paramInt;
    }
    return -1;
  }
  
  protected final void copyAttributes(int paramInt, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    for (int i = getFirstAttributeIdentity(paramInt); i != -1; i = getNextAttributeIdentity(i))
    {
      int j = _exptype2(i);
      copyAttribute(i, j, paramSerializationHandler);
    }
  }
  
  protected final void copyAttribute(int paramInt1, int paramInt2, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    ExtendedType localExtendedType = this.m_extendedTypes[paramInt2];
    String str1 = localExtendedType.getNamespace();
    String str2 = localExtendedType.getLocalName();
    String str3 = null;
    String str4 = null;
    int i = _dataOrQName(paramInt1);
    int j = i;
    if (i <= 0)
    {
      int k = this.m_data.elementAt(-i);
      j = this.m_data.elementAt(-i + 1);
      str4 = this.m_valuesOrPrefixes.indexToString(k);
      int m = str4.indexOf(':');
      if (m > 0) {
        str3 = str4.substring(0, m);
      }
    }
    if (str1.length() != 0) {
      paramSerializationHandler.namespaceAfterStartElement(str3, str1);
    }
    String str5 = str3 != null ? str4 : str2;
    String str6 = (String)this.m_values.elementAt(j);
    paramSerializationHandler.addAttribute(str1, str2, str5, "CDATA", str6);
  }
  
  public class AncestorIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private static final int m_blocksize = 32;
    int[] m_ancestors = new int[32];
    int m_size = 0;
    int m_ancestorsPos;
    int m_markedPos;
    int m_realStartNode;
    
    public AncestorIterator()
    {
      super();
    }
    
    public int getStartNode()
    {
      return this.m_realStartNode;
    }
    
    public final boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      this._isRestartable = false;
      try
      {
        AncestorIterator localAncestorIterator = (AncestorIterator)super.clone();
        localAncestorIterator._startNode = this._startNode;
        return localAncestorIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      this.m_realStartNode = paramInt;
      if (this._isRestartable)
      {
        int i = SAX2DTM2.this.makeNodeIdentity(paramInt);
        this.m_size = 0;
        if (i == -1)
        {
          this._currentNode = -1;
          this.m_ancestorsPos = 0;
          return this;
        }
        if (!this._includeSelf)
        {
          i = SAX2DTM2.this._parent2(i);
          paramInt = SAX2DTM2.this.makeNodeHandle(i);
        }
        this._startNode = paramInt;
        while (i != -1)
        {
          if (this.m_size >= this.m_ancestors.length)
          {
            int[] arrayOfInt = new int[this.m_size * 2];
            System.arraycopy(this.m_ancestors, 0, arrayOfInt, 0, this.m_ancestors.length);
            this.m_ancestors = arrayOfInt;
          }
          this.m_ancestors[(this.m_size++)] = paramInt;
          i = SAX2DTM2.this._parent2(i);
          paramInt = SAX2DTM2.this.makeNodeHandle(i);
        }
        this.m_ancestorsPos = (this.m_size - 1);
        this._currentNode = (this.m_ancestorsPos >= 0 ? this.m_ancestors[this.m_ancestorsPos] : -1);
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator reset()
    {
      this.m_ancestorsPos = (this.m_size - 1);
      this._currentNode = (this.m_ancestorsPos >= 0 ? this.m_ancestors[this.m_ancestorsPos] : -1);
      return resetPosition();
    }
    
    public int next()
    {
      int i = this._currentNode;
      int j = --this.m_ancestorsPos;
      this._currentNode = (j >= 0 ? this.m_ancestors[this.m_ancestorsPos] : -1);
      return returnNode(i);
    }
    
    public void setMark()
    {
      this.m_markedPos = this.m_ancestorsPos;
    }
    
    public void gotoMark()
    {
      this.m_ancestorsPos = this.m_markedPos;
      this._currentNode = (this.m_ancestorsPos >= 0 ? this.m_ancestors[this.m_ancestorsPos] : -1);
    }
  }
  
  public final class AttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public AttributeIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this._currentNode = SAX2DTM2.this.getFirstAttributeIdentity(SAX2DTM2.this.makeNodeIdentity(paramInt));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = this._currentNode;
      if (i != -1)
      {
        this._currentNode = SAX2DTM2.this.getNextAttributeIdentity(i);
        return returnNode(SAX2DTM2.this.makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public final class ChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public ChildrenIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this._currentNode = (paramInt == -1 ? -1 : SAX2DTM2.this._firstch2(SAX2DTM2.this.makeNodeIdentity(paramInt)));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if (this._currentNode != -1)
      {
        int i = this._currentNode;
        this._currentNode = SAX2DTM2.this._nextsib2(i);
        return returnNode(SAX2DTM2.this.makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public class DescendantIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public DescendantIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        paramInt = SAX2DTM2.this.makeNodeIdentity(paramInt);
        this._startNode = paramInt;
        if (this._includeSelf) {
          paramInt--;
        }
        this._currentNode = paramInt;
        return resetPosition();
      }
      return this;
    }
    
    protected final boolean isDescendant(int paramInt)
    {
      return (SAX2DTM2.this._parent2(paramInt) >= this._startNode) || (this._startNode == paramInt);
    }
    
    public int next()
    {
      int i = this._startNode;
      if (i == -1) {
        return -1;
      }
      if ((this._includeSelf) && (this._currentNode + 1 == i)) {
        return returnNode(SAX2DTM2.this.makeNodeHandle(++this._currentNode));
      }
      int j = this._currentNode;
      int k;
      if (i == 0)
      {
        int m;
        do
        {
          j++;
          m = SAX2DTM2.this._exptype2(j);
          if (-1 == m)
          {
            this._currentNode = -1;
            return -1;
          }
        } while ((m == 3) || ((k = SAX2DTM2.this.m_extendedTypes[m].getNodeType()) == 2) || (k == 13));
      }
      else
      {
        do
        {
          j++;
          k = SAX2DTM2.this._type2(j);
          if ((-1 == k) || (!isDescendant(j)))
          {
            this._currentNode = -1;
            return -1;
          }
        } while ((2 == k) || (3 == k) || (13 == k));
      }
      this._currentNode = j;
      return returnNode(SAX2DTM2.this.makeNodeHandle(j));
    }
    
    public DTMAxisIterator reset()
    {
      boolean bool = this._isRestartable;
      this._isRestartable = true;
      setStartNode(SAX2DTM2.this.makeNodeHandle(this._startNode));
      this._isRestartable = bool;
      return this;
    }
  }
  
  public class FollowingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public FollowingIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        paramInt = SAX2DTM2.this.makeNodeIdentity(paramInt);
        int j = SAX2DTM2.this._type2(paramInt);
        int i;
        if ((2 == j) || (13 == j))
        {
          paramInt = SAX2DTM2.this._parent2(paramInt);
          i = SAX2DTM2.this._firstch2(paramInt);
          if (-1 != i)
          {
            this._currentNode = SAX2DTM2.this.makeNodeHandle(i);
            return resetPosition();
          }
        }
        do
        {
          i = SAX2DTM2.this._nextsib2(paramInt);
          if (-1 == i) {
            paramInt = SAX2DTM2.this._parent2(paramInt);
          }
        } while ((-1 == i) && (-1 != paramInt));
        this._currentNode = SAX2DTM2.this.makeNodeHandle(i);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = this._currentNode;
      int j = SAX2DTM2.this.makeNodeIdentity(i);
      int k;
      do
      {
        j++;
        k = SAX2DTM2.this._type2(j);
        if (-1 == k)
        {
          this._currentNode = -1;
          return returnNode(i);
        }
      } while ((2 == k) || (13 == k));
      this._currentNode = SAX2DTM2.this.makeNodeHandle(j);
      return returnNode(i);
    }
  }
  
  public class FollowingSiblingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public FollowingSiblingIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this._currentNode = SAX2DTM2.this.makeNodeIdentity(paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      this._currentNode = (this._currentNode == -1 ? -1 : SAX2DTM2.this._nextsib2(this._currentNode));
      return returnNode(SAX2DTM2.this.makeNodeHandle(this._currentNode));
    }
  }
  
  public final class ParentIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private int _nodeType = -1;
    
    public ParentIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        if (paramInt != -1) {
          this._currentNode = SAX2DTM2.this._parent2(SAX2DTM2.this.makeNodeIdentity(paramInt));
        } else {
          this._currentNode = -1;
        }
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator setNodeType(int paramInt)
    {
      this._nodeType = paramInt;
      return this;
    }
    
    public int next()
    {
      int i = this._currentNode;
      if (i == -1) {
        return -1;
      }
      if (this._nodeType == -1)
      {
        this._currentNode = -1;
        return returnNode(SAX2DTM2.this.makeNodeHandle(i));
      }
      if (this._nodeType >= 14)
      {
        if (this._nodeType == SAX2DTM2.this._exptype2(i))
        {
          this._currentNode = -1;
          return returnNode(SAX2DTM2.this.makeNodeHandle(i));
        }
      }
      else if (this._nodeType == SAX2DTM2.this._type2(i))
      {
        this._currentNode = -1;
        return returnNode(SAX2DTM2.this.makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public class PrecedingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _maxAncestors = 8;
    protected int[] _stack = new int[8];
    protected int _sp;
    protected int _oldsp;
    protected int _markedsp;
    protected int _markedNode;
    protected int _markedDescendant;
    
    public PrecedingIterator()
    {
      super();
    }
    
    public boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      this._isRestartable = false;
      try
      {
        PrecedingIterator localPrecedingIterator = (PrecedingIterator)super.clone();
        int[] arrayOfInt = new int[this._stack.length];
        System.arraycopy(this._stack, 0, arrayOfInt, 0, this._stack.length);
        localPrecedingIterator._stack = arrayOfInt;
        return localPrecedingIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        paramInt = SAX2DTM2.this.makeNodeIdentity(paramInt);
        if (SAX2DTM2.this._type2(paramInt) == 2) {
          paramInt = SAX2DTM2.this._parent2(paramInt);
        }
        this._startNode = paramInt;
        int tmp59_58 = 0;
        int j = tmp59_58;
        this._stack[tmp59_58] = paramInt;
        int i = paramInt;
        while ((i = SAX2DTM2.this._parent2(i)) != -1)
        {
          j++;
          if (j == this._stack.length)
          {
            int[] arrayOfInt = new int[j * 2];
            System.arraycopy(this._stack, 0, arrayOfInt, 0, j);
            this._stack = arrayOfInt;
          }
          this._stack[j] = i;
        }
        if (j > 0) {
          j--;
        }
        this._currentNode = this._stack[j];
        this._oldsp = (this._sp = j);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      for (this._currentNode += 1; this._sp >= 0; this._currentNode += 1) {
        if (this._currentNode < this._stack[this._sp])
        {
          int i = SAX2DTM2.this._type2(this._currentNode);
          if ((i != 2) && (i != 13)) {
            return returnNode(SAX2DTM2.this.makeNodeHandle(this._currentNode));
          }
        }
        else
        {
          this._sp -= 1;
        }
      }
      return -1;
    }
    
    public DTMAxisIterator reset()
    {
      this._sp = this._oldsp;
      return resetPosition();
    }
    
    public void setMark()
    {
      this._markedsp = this._sp;
      this._markedNode = this._currentNode;
      this._markedDescendant = this._stack[0];
    }
    
    public void gotoMark()
    {
      this._sp = this._markedsp;
      this._currentNode = this._markedNode;
    }
  }
  
  public class PrecedingSiblingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    protected int _startNodeID;
    
    public PrecedingSiblingIterator()
    {
      super();
    }
    
    public boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        paramInt = this._startNodeID = SAX2DTM2.this.makeNodeIdentity(paramInt);
        if (paramInt == -1)
        {
          this._currentNode = paramInt;
          return resetPosition();
        }
        int i = SAX2DTM2.this._type2(paramInt);
        if ((2 == i) || (13 == i))
        {
          this._currentNode = paramInt;
        }
        else
        {
          this._currentNode = SAX2DTM2.this._parent2(paramInt);
          if (-1 != this._currentNode) {
            this._currentNode = SAX2DTM2.this._firstch2(this._currentNode);
          } else {
            this._currentNode = paramInt;
          }
        }
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if ((this._currentNode == this._startNodeID) || (this._currentNode == -1)) {
        return -1;
      }
      int i = this._currentNode;
      this._currentNode = SAX2DTM2.this._nextsib2(i);
      return returnNode(SAX2DTM2.this.makeNodeHandle(i));
    }
  }
  
  public final class TypedAncestorIterator
    extends SAX2DTM2.AncestorIterator
  {
    private final int _nodeType;
    
    public TypedAncestorIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      this.m_realStartNode = paramInt;
      if (this._isRestartable)
      {
        int i = SAX2DTM2.this.makeNodeIdentity(paramInt);
        this.m_size = 0;
        if (i == -1)
        {
          this._currentNode = -1;
          this.m_ancestorsPos = 0;
          return this;
        }
        int j = this._nodeType;
        if (!this._includeSelf)
        {
          i = SAX2DTM2.this._parent2(i);
          paramInt = SAX2DTM2.this.makeNodeHandle(i);
        }
        this._startNode = paramInt;
        int k;
        int[] arrayOfInt;
        if (j >= 14) {
          while (i != -1)
          {
            k = SAX2DTM2.this._exptype2(i);
            if (k == j)
            {
              if (this.m_size >= this.m_ancestors.length)
              {
                arrayOfInt = new int[this.m_size * 2];
                System.arraycopy(this.m_ancestors, 0, arrayOfInt, 0, this.m_ancestors.length);
                this.m_ancestors = arrayOfInt;
              }
              this.m_ancestors[(this.m_size++)] = SAX2DTM2.this.makeNodeHandle(i);
            }
            i = SAX2DTM2.this._parent2(i);
          }
        }
        while (i != -1)
        {
          k = SAX2DTM2.this._exptype2(i);
          if (((k < 14) && (k == j)) || ((k >= 14) && (SAX2DTM2.this.m_extendedTypes[k].getNodeType() == j)))
          {
            if (this.m_size >= this.m_ancestors.length)
            {
              arrayOfInt = new int[this.m_size * 2];
              System.arraycopy(this.m_ancestors, 0, arrayOfInt, 0, this.m_ancestors.length);
              this.m_ancestors = arrayOfInt;
            }
            this.m_ancestors[(this.m_size++)] = SAX2DTM2.this.makeNodeHandle(i);
          }
          i = SAX2DTM2.this._parent2(i);
        }
        this.m_ancestorsPos = (this.m_size - 1);
        this._currentNode = (this.m_ancestorsPos >= 0 ? this.m_ancestors[this.m_ancestorsPos] : -1);
        return resetPosition();
      }
      return this;
    }
    
    public int getNodeByPosition(int paramInt)
    {
      if ((paramInt > 0) && (paramInt <= this.m_size)) {
        return this.m_ancestors[(paramInt - 1)];
      }
      return -1;
    }
    
    public int getLast()
    {
      return this.m_size;
    }
  }
  
  public final class TypedAttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nodeType;
    
    public TypedAttributeIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this._currentNode = SAX2DTM2.this.getTypedAttribute(paramInt, this._nodeType);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = this._currentNode;
      this._currentNode = -1;
      return returnNode(i);
    }
  }
  
  public final class TypedChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nodeType;
    
    public TypedChildrenIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAX2DTM2.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this._currentNode = (paramInt == -1 ? -1 : SAX2DTM2.this._firstch2(SAX2DTM2.this.makeNodeIdentity(this._startNode)));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = this._currentNode;
      if (i == -1) {
        return -1;
      }
      int j = this._nodeType;
      if (j != 1) {
        while ((i != -1) && (SAX2DTM2.this._exptype2(i) != j)) {
          i = SAX2DTM2.this._nextsib2(i);
        }
      }
      while (i != -1)
      {
        int k = SAX2DTM2.this._exptype2(i);
        if (k >= 14) {
          break;
        }
        i = SAX2DTM2.this._nextsib2(i);
      }
      if (i == -1)
      {
        this._currentNode = -1;
        return -1;
      }
      this._currentNode = SAX2DTM2.this._nextsib2(i);
      return returnNode(SAX2DTM2.this.makeNodeHandle(i));
    }
    
    public int getNodeByPosition(int paramInt)
    {
      if (paramInt <= 0) {
        return -1;
      }
      int i = this._currentNode;
      int j = 0;
      int k = this._nodeType;
      if (k != 1)
      {
        while (i != -1)
        {
          if (SAX2DTM2.this._exptype2(i) == k)
          {
            j++;
            if (j == paramInt) {
              return SAX2DTM2.this.makeNodeHandle(i);
            }
          }
          i = SAX2DTM2.this._nextsib2(i);
        }
        return -1;
      }
      while (i != -1)
      {
        if (SAX2DTM2.this._exptype2(i) >= 14)
        {
          j++;
          if (j == paramInt) {
            return SAX2DTM2.this.makeNodeHandle(i);
          }
        }
        i = SAX2DTM2.this._nextsib2(i);
      }
      return -1;
    }
  }
  
  public final class TypedDescendantIterator
    extends SAX2DTM2.DescendantIterator
  {
    private final int _nodeType;
    
    public TypedDescendantIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      int i = this._startNode;
      if (this._startNode == -1) {
        return -1;
      }
      int j = this._currentNode;
      int m = this._nodeType;
      int k;
      if (m != 1) {
        do
        {
          j++;
          k = SAX2DTM2.this._exptype2(j);
          if ((-1 == k) || ((SAX2DTM2.this._parent2(j) < i) && (i != j)))
          {
            this._currentNode = -1;
            return -1;
          }
        } while (k != m);
      } else if (i == 0) {
        do
        {
          j++;
          k = SAX2DTM2.this._exptype2(j);
          if (-1 == k)
          {
            this._currentNode = -1;
            return -1;
          }
        } while ((k < 14) || (SAX2DTM2.this.m_extendedTypes[k].getNodeType() != 1));
      } else {
        do
        {
          j++;
          k = SAX2DTM2.this._exptype2(j);
          if ((-1 == k) || ((SAX2DTM2.this._parent2(j) < i) && (i != j)))
          {
            this._currentNode = -1;
            return -1;
          }
        } while ((k < 14) || (SAX2DTM2.this.m_extendedTypes[k].getNodeType() != 1));
      }
      this._currentNode = j;
      return returnNode(SAX2DTM2.this.makeNodeHandle(j));
    }
  }
  
  public final class TypedFollowingIterator
    extends SAX2DTM2.FollowingIterator
  {
    private final int _nodeType;
    
    public TypedFollowingIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      int m = this._nodeType;
      int n = SAX2DTM2.this.makeNodeIdentity(this._currentNode);
      int j;
      int i;
      int k;
      if (m >= 14) {
        do
        {
          j = n;
          i = j;
          do
          {
            i++;
            k = SAX2DTM2.this._type2(i);
          } while ((k != -1) && ((2 == k) || (13 == k)));
          n = k != -1 ? i : -1;
          if (j == -1) {
            break;
          }
        } while (SAX2DTM2.this._exptype2(j) != m);
      } else {
        do
        {
          j = n;
          i = j;
          do
          {
            i++;
            k = SAX2DTM2.this._type2(i);
          } while ((k != -1) && ((2 == k) || (13 == k)));
          n = k != -1 ? i : -1;
        } while ((j != -1) && (SAX2DTM2.this._exptype2(j) != m) && (SAX2DTM2.this._type2(j) != m));
      }
      this._currentNode = SAX2DTM2.this.makeNodeHandle(n);
      return j == -1 ? -1 : returnNode(SAX2DTM2.this.makeNodeHandle(j));
    }
  }
  
  public final class TypedFollowingSiblingIterator
    extends SAX2DTM2.FollowingSiblingIterator
  {
    private final int _nodeType;
    
    public TypedFollowingSiblingIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      if (this._currentNode == -1) {
        return -1;
      }
      int i = this._currentNode;
      int j = this._nodeType;
      if (j != 1) {
        while (((i = SAX2DTM2.this._nextsib2(i)) != -1) && (SAX2DTM2.this._exptype2(i) != j)) {}
      }
      while (((i = SAX2DTM2.this._nextsib2(i)) != -1) && (SAX2DTM2.this._exptype2(i) < 14)) {}
      this._currentNode = i;
      return i == -1 ? -1 : returnNode(SAX2DTM2.this.makeNodeHandle(i));
    }
  }
  
  public final class TypedPrecedingIterator
    extends SAX2DTM2.PrecedingIterator
  {
    private final int _nodeType;
    
    public TypedPrecedingIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      int i = this._currentNode;
      int j = this._nodeType;
      if (j >= 14) {
        do
        {
          do
          {
            i++;
            if (this._sp < 0)
            {
              i = -1;
              break label167;
            }
            if (i < this._stack[this._sp]) {
              break;
            }
          } while (--this._sp >= 0);
          i = -1;
          break;
        } while (SAX2DTM2.this._exptype2(i) != j);
      } else {
        for (;;)
        {
          i++;
          if (this._sp < 0)
          {
            i = -1;
          }
          else if (i >= this._stack[this._sp])
          {
            if (--this._sp < 0) {
              i = -1;
            }
          }
          else
          {
            int k = SAX2DTM2.this._exptype2(i);
            if (k < 14)
            {
              if (k == j) {
                break;
              }
            }
            else if (SAX2DTM2.this.m_extendedTypes[k].getNodeType() == j) {
              break;
            }
          }
        }
      }
      label167:
      this._currentNode = i;
      return i == -1 ? -1 : returnNode(SAX2DTM2.this.makeNodeHandle(i));
    }
  }
  
  public final class TypedPrecedingSiblingIterator
    extends SAX2DTM2.PrecedingSiblingIterator
  {
    private final int _nodeType;
    
    public TypedPrecedingSiblingIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      int i = this._currentNode;
      int j = this._nodeType;
      int k = this._startNodeID;
      if (j != 1) {
        while ((i != -1) && (i != k) && (SAX2DTM2.this._exptype2(i) != j)) {
          i = SAX2DTM2.this._nextsib2(i);
        }
      }
      while ((i != -1) && (i != k) && (SAX2DTM2.this._exptype2(i) < 14)) {
        i = SAX2DTM2.this._nextsib2(i);
      }
      if ((i == -1) || (i == k))
      {
        this._currentNode = -1;
        return -1;
      }
      this._currentNode = SAX2DTM2.this._nextsib2(i);
      return returnNode(SAX2DTM2.this.makeNodeHandle(i));
    }
    
    public int getLast()
    {
      if (this._last != -1) {
        return this._last;
      }
      setMark();
      int i = this._currentNode;
      int j = this._nodeType;
      int k = this._startNodeID;
      int m = 0;
      if (j != 1) {
        while ((i != -1) && (i != k))
        {
          if (SAX2DTM2.this._exptype2(i) == j) {
            m++;
          }
          i = SAX2DTM2.this._nextsib2(i);
        }
      }
      while ((i != -1) && (i != k))
      {
        if (SAX2DTM2.this._exptype2(i) >= 14) {
          m++;
        }
        i = SAX2DTM2.this._nextsib2(i);
      }
      gotoMark();
      return this._last = m;
    }
  }
  
  public class TypedRootIterator
    extends DTMDefaultBaseIterators.RootIterator
  {
    private final int _nodeType;
    
    public TypedRootIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      if (this._startNode == this._currentNode) {
        return -1;
      }
      int i = this._startNode;
      int j = SAX2DTM2.this._exptype2(SAX2DTM2.this.makeNodeIdentity(i));
      this._currentNode = i;
      if (this._nodeType >= 14)
      {
        if (this._nodeType == j) {
          return returnNode(i);
        }
      }
      else if (j < 14)
      {
        if (j == this._nodeType) {
          return returnNode(i);
        }
      }
      else if (SAX2DTM2.this.m_extendedTypes[j].getNodeType() == this._nodeType) {
        return returnNode(i);
      }
      return -1;
    }
  }
  
  public final class TypedSingletonIterator
    extends DTMDefaultBaseIterators.SingletonIterator
  {
    private final int _nodeType;
    
    public TypedSingletonIterator(int paramInt)
    {
      super();
      this._nodeType = paramInt;
    }
    
    public int next()
    {
      int i = this._currentNode;
      if (i == -1) {
        return -1;
      }
      this._currentNode = -1;
      if (this._nodeType >= 14)
      {
        if (SAX2DTM2.this._exptype2(SAX2DTM2.this.makeNodeIdentity(i)) == this._nodeType) {
          return returnNode(i);
        }
      }
      else if (SAX2DTM2.this._type2(SAX2DTM2.this.makeNodeIdentity(i)) == this._nodeType) {
        return returnNode(i);
      }
      return -1;
    }
  }
}
