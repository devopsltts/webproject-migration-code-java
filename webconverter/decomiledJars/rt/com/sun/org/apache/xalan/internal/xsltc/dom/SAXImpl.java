package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.InternalAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.NamespaceIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.NthDescendantIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.RootIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.SingletonIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.AncestorIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.AttributeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.ChildrenIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.DescendantIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.FollowingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.FollowingSiblingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.ParentIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.PrecedingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.PrecedingSiblingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedAncestorIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedAttributeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedChildrenIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedDescendantIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedFollowingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedFollowingSiblingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedPrecedingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedPrecedingSiblingIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedRootIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2.TypedSingletonIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class SAXImpl
  extends SAX2DTM2
  implements DOMEnhancedForDTM, DOMBuilder
{
  private int _uriCount = 0;
  private int _prefixCount = 0;
  private int[] _xmlSpaceStack;
  private int _idx = 1;
  private boolean _preserve = false;
  private static final String XML_STRING = "xml:";
  private static final String XML_PREFIX = "xml";
  private static final String XMLSPACE_STRING = "xml:space";
  private static final String PRESERVE_STRING = "preserve";
  private static final String XMLNS_PREFIX = "xmlns";
  private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  private boolean _escaping = true;
  private boolean _disableEscaping = false;
  private int _textNodeToProcess = -1;
  private static final String EMPTYSTRING = "";
  private static final DTMAxisIterator EMPTYITERATOR = ;
  private int _namesSize = -1;
  private Map<Integer, Integer> _nsIndex = new HashMap();
  private int _size = 0;
  private BitArray _dontEscape = null;
  private String _documentURI = null;
  private static int _documentURIIndex = 0;
  private Document _document;
  private Map<Node, Integer> _node2Ids = null;
  private boolean _hasDOMSource = false;
  private XSLTCDTMManager _dtmManager;
  private Node[] _nodes;
  private NodeList[] _nodeLists;
  private static final String XML_LANG_ATTRIBUTE = "http://www.w3.org/XML/1998/namespace:@lang";
  
  public void setDocumentURI(String paramString)
  {
    if (paramString != null) {
      setDocumentBaseURI(SystemIDResolver.getAbsoluteURI(paramString));
    }
  }
  
  public String getDocumentURI()
  {
    String str = getDocumentBaseURI();
    return "rtf" + _documentURIIndex++;
  }
  
  public String getDocumentURI(int paramInt)
  {
    return getDocumentURI();
  }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {}
  
  public String lookupNamespace(int paramInt, String paramString)
    throws TransletException
  {
    SAX2DTM2.AncestorIterator localAncestorIterator = new SAX2DTM2.AncestorIterator(this);
    if (isElement(paramInt)) {
      localAncestorIterator.includeSelf();
    }
    localAncestorIterator.setStartNode(paramInt);
    int i;
    while ((i = localAncestorIterator.next()) != -1)
    {
      DTMDefaultBaseIterators.NamespaceIterator localNamespaceIterator = new DTMDefaultBaseIterators.NamespaceIterator(this);
      localNamespaceIterator.setStartNode(i);
      int j;
      while ((j = localNamespaceIterator.next()) != -1) {
        if (getLocalName(j).equals(paramString)) {
          return getNodeValue(j);
        }
      }
    }
    BasisLibrary.runTimeError("NAMESPACE_PREFIX_ERR", paramString);
    return null;
  }
  
  public boolean isElement(int paramInt)
  {
    return getNodeType(paramInt) == 1;
  }
  
  public boolean isAttribute(int paramInt)
  {
    return getNodeType(paramInt) == 2;
  }
  
  public int getSize()
  {
    return getNumberOfNodes();
  }
  
  public void setFilter(StripFilter paramStripFilter) {}
  
  public boolean lessThan(int paramInt1, int paramInt2)
  {
    if (paramInt1 == -1) {
      return false;
    }
    if (paramInt2 == -1) {
      return true;
    }
    return paramInt1 < paramInt2;
  }
  
  public Node makeNode(int paramInt)
  {
    if (this._nodes == null) {
      this._nodes = new Node[this._namesSize];
    }
    int i = makeNodeIdentity(paramInt);
    if (i < 0) {
      return null;
    }
    if (i < this._nodes.length) {
      return this._nodes[i] = (this._nodes[i] != null ? this._nodes[i] : ) = new DTMNodeProxy(this, paramInt);
    }
    return new DTMNodeProxy(this, paramInt);
  }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator)
  {
    return makeNode(paramDTMAxisIterator.next());
  }
  
  public NodeList makeNodeList(int paramInt)
  {
    if (this._nodeLists == null) {
      this._nodeLists = new NodeList[this._namesSize];
    }
    int i = makeNodeIdentity(paramInt);
    if (i < 0) {
      return null;
    }
    if (i < this._nodeLists.length) {
      return this._nodeLists[i] = (this._nodeLists[i] != null ? this._nodeLists[i] : ) = new DTMAxisIterNodeList(this, new DTMDefaultBaseIterators.SingletonIterator(this, paramInt));
    }
    return new DTMAxisIterNodeList(this, new DTMDefaultBaseIterators.SingletonIterator(this, paramInt));
  }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator)
  {
    return new DTMAxisIterNodeList(this, paramDTMAxisIterator);
  }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean)
  {
    return new NodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean);
  }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt)
  {
    return new DupFilterIterator(paramDTMAxisIterator);
  }
  
  public DTMAxisIterator getIterator()
  {
    return new DTMDefaultBaseIterators.SingletonIterator(this, getDocument(), true);
  }
  
  public int getNSType(int paramInt)
  {
    String str = getNamespaceURI(paramInt);
    if (str == null) {
      return 0;
    }
    int i = getIdForNamespace(str);
    return ((Integer)this._nsIndex.get(new Integer(i))).intValue();
  }
  
  public int getNamespaceType(int paramInt)
  {
    return super.getNamespaceType(paramInt);
  }
  
  private int[] setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt = new int[this.m_expandedNameTable.getSize()];
    for (int i = 0; i < paramInt; i++)
    {
      int j = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[i], paramArrayOfString1[i], paramArrayOfInt[i], false);
      arrayOfInt[j] = j;
    }
    return arrayOfInt;
  }
  
  public int getGeneralizedType(String paramString)
  {
    return getGeneralizedType(paramString, true);
  }
  
  public int getGeneralizedType(String paramString, boolean paramBoolean)
  {
    String str2 = null;
    int i = -1;
    if ((i = paramString.lastIndexOf(":")) > -1) {
      str2 = paramString.substring(0, i);
    }
    int k = i + 1;
    int j;
    if (paramString.charAt(k) == '@')
    {
      j = 2;
      k++;
    }
    else
    {
      j = 1;
    }
    String str1 = k == 0 ? paramString : paramString.substring(k);
    return this.m_expandedNameTable.getExpandedTypeID(str2, str1, j, paramBoolean);
  }
  
  public short[] getMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt)
  {
    if (this._namesSize < 0) {
      return getMapping2(paramArrayOfString1, paramArrayOfString2, paramArrayOfInt);
    }
    int j = paramArrayOfString1.length;
    int k = this.m_expandedNameTable.getSize();
    short[] arrayOfShort = new short[k];
    for (int i = 0; i < 14; i++) {
      arrayOfShort[i] = ((short)i);
    }
    for (i = 14; i < k; i++) {
      arrayOfShort[i] = this.m_expandedNameTable.getType(i);
    }
    for (i = 0; i < j; i++)
    {
      int m = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[i], paramArrayOfString1[i], paramArrayOfInt[i], true);
      if ((m >= 0) && (m < k)) {
        arrayOfShort[m] = ((short)(i + 14));
      }
    }
    return arrayOfShort;
  }
  
  public int[] getReverseMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt)
  {
    int[] arrayOfInt = new int[paramArrayOfString1.length + 14];
    for (int i = 0; i < 14; i++) {
      arrayOfInt[i] = i;
    }
    for (i = 0; i < paramArrayOfString1.length; i++)
    {
      int j = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[i], paramArrayOfString1[i], paramArrayOfInt[i], true);
      arrayOfInt[(i + 14)] = j;
    }
    return arrayOfInt;
  }
  
  private short[] getMapping2(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt)
  {
    int j = paramArrayOfString1.length;
    int k = this.m_expandedNameTable.getSize();
    int[] arrayOfInt = null;
    if (j > 0) {
      arrayOfInt = new int[j];
    }
    int m = k;
    for (int i = 0; i < j; i++)
    {
      arrayOfInt[i] = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[i], paramArrayOfString1[i], paramArrayOfInt[i], false);
      if ((this._namesSize < 0) && (arrayOfInt[i] >= m)) {
        m = arrayOfInt[i] + 1;
      }
    }
    short[] arrayOfShort = new short[m];
    for (i = 0; i < 14; i++) {
      arrayOfShort[i] = ((short)i);
    }
    for (i = 14; i < k; i++) {
      arrayOfShort[i] = this.m_expandedNameTable.getType(i);
    }
    for (i = 0; i < j; i++)
    {
      int n = arrayOfInt[i];
      if ((n >= 0) && (n < m)) {
        arrayOfShort[n] = ((short)(i + 14));
      }
    }
    return arrayOfShort;
  }
  
  public short[] getNamespaceMapping(String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int k = this._uriCount;
    short[] arrayOfShort = new short[k];
    for (int i = 0; i < k; i++) {
      arrayOfShort[i] = -1;
    }
    for (i = 0; i < j; i++)
    {
      int m = getIdForNamespace(paramArrayOfString[i]);
      Integer localInteger = (Integer)this._nsIndex.get(Integer.valueOf(m));
      if (localInteger != null) {
        arrayOfShort[localInteger.intValue()] = ((short)i);
      }
    }
    return arrayOfShort;
  }
  
  public short[] getReverseNamespaceMapping(String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    short[] arrayOfShort = new short[j];
    for (int i = 0; i < j; i++)
    {
      int k = getIdForNamespace(paramArrayOfString[i]);
      Integer localInteger = (Integer)this._nsIndex.get(Integer.valueOf(k));
      arrayOfShort[i] = (localInteger == null ? -1 : localInteger.shortValue());
    }
    return arrayOfShort;
  }
  
  public SAXImpl(XSLTCDTMManager paramXSLTCDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramXSLTCDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, 512, paramBoolean2, false);
  }
  
  public SAXImpl(XSLTCDTMManager paramXSLTCDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramXSLTCDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, false, paramBoolean2, paramBoolean3);
    this._dtmManager = paramXSLTCDTMManager;
    this._size = paramInt2;
    this._xmlSpaceStack = new int[paramInt2 <= 64 ? 4 : 64];
    this._xmlSpaceStack[0] = 0;
    if ((paramSource instanceof DOMSource))
    {
      this._hasDOMSource = true;
      DOMSource localDOMSource = (DOMSource)paramSource;
      Node localNode = localDOMSource.getNode();
      if ((localNode instanceof Document)) {
        this._document = ((Document)localNode);
      } else {
        this._document = localNode.getOwnerDocument();
      }
      this._node2Ids = new HashMap();
    }
  }
  
  public void migrateTo(DTMManager paramDTMManager)
  {
    super.migrateTo(paramDTMManager);
    if ((paramDTMManager instanceof XSLTCDTMManager)) {
      this._dtmManager = ((XSLTCDTMManager)paramDTMManager);
    }
  }
  
  public int getElementById(String paramString)
  {
    Element localElement = this._document.getElementById(paramString);
    if (localElement != null)
    {
      Integer localInteger = (Integer)this._node2Ids.get(localElement);
      return localInteger != null ? localInteger.intValue() : -1;
    }
    return -1;
  }
  
  public boolean hasDOMSource()
  {
    return this._hasDOMSource;
  }
  
  private void xmlSpaceDefine(String paramString, int paramInt)
  {
    boolean bool = paramString.equals("preserve");
    if (bool != this._preserve)
    {
      this._xmlSpaceStack[(this._idx++)] = paramInt;
      this._preserve = bool;
    }
  }
  
  private void xmlSpaceRevert(int paramInt)
  {
    if (paramInt == this._xmlSpaceStack[(this._idx - 1)])
    {
      this._idx -= 1;
      this._preserve = (!this._preserve);
    }
  }
  
  protected boolean getShouldStripWhitespace()
  {
    return this._preserve ? false : super.getShouldStripWhitespace();
  }
  
  private void handleTextEscaping()
  {
    if ((this._disableEscaping) && (this._textNodeToProcess != -1) && (_type(this._textNodeToProcess) == 3))
    {
      if (this._dontEscape == null) {
        this._dontEscape = new BitArray(this._size);
      }
      if (this._textNodeToProcess >= this._dontEscape.size()) {
        this._dontEscape.resize(this._dontEscape.size() * 2);
      }
      this._dontEscape.setBit(this._textNodeToProcess);
      this._disableEscaping = false;
    }
    this._textNodeToProcess = -1;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
    this._disableEscaping = (!this._escaping);
    this._textNodeToProcess = getNumberOfNodes();
  }
  
  public void startDocument()
    throws SAXException
  {
    super.startDocument();
    this._nsIndex.put(Integer.valueOf(0), Integer.valueOf(this._uriCount++));
    definePrefixAndUri("xml", "http://www.w3.org/XML/1998/namespace");
  }
  
  public void endDocument()
    throws SAXException
  {
    super.endDocument();
    handleTextEscaping();
    this._namesSize = this.m_expandedNameTable.getSize();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, Node paramNode)
    throws SAXException
  {
    startElement(paramString1, paramString2, paramString3, paramAttributes);
    if (this.m_buildIdIndex) {
      this._node2Ids.put(paramNode, new Integer(this.m_parents.peek()));
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    handleTextEscaping();
    if (this.m_wsfilter != null)
    {
      int i = paramAttributes.getIndex("xml:space");
      if (i >= 0) {
        xmlSpaceDefine(paramAttributes.getValue(i), this.m_parents.peek());
      }
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    super.endElement(paramString1, paramString2, paramString3);
    handleTextEscaping();
    if (this.m_wsfilter != null) {
      xmlSpaceRevert(this.m_previous);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    super.processingInstruction(paramString1, paramString2);
    handleTextEscaping();
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    this._textNodeToProcess = getNumberOfNodes();
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    super.startPrefixMapping(paramString1, paramString2);
    handleTextEscaping();
    definePrefixAndUri(paramString1, paramString2);
  }
  
  private void definePrefixAndUri(String paramString1, String paramString2)
    throws SAXException
  {
    Integer localInteger = new Integer(getIdForNamespace(paramString2));
    if (this._nsIndex.get(localInteger) == null) {
      this._nsIndex.put(localInteger, Integer.valueOf(this._uriCount++));
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    handleTextEscaping();
  }
  
  public boolean setEscaping(boolean paramBoolean)
  {
    boolean bool = this._escaping;
    this._escaping = paramBoolean;
    return bool;
  }
  
  public void print(int paramInt1, int paramInt2)
  {
    switch (getNodeType(paramInt1))
    {
    case 0: 
    case 9: 
      print(getFirstChild(paramInt1), paramInt2);
      break;
    case 3: 
    case 7: 
    case 8: 
      System.out.print(getStringValueX(paramInt1));
      break;
    case 1: 
    case 2: 
    case 4: 
    case 5: 
    case 6: 
    default: 
      String str = getNodeName(paramInt1);
      System.out.print("<" + str);
      for (int i = getFirstAttribute(paramInt1); i != -1; i = getNextAttribute(i)) {
        System.out.print("\n" + getNodeName(i) + "=\"" + getStringValueX(i) + "\"");
      }
      System.out.print('>');
      for (i = getFirstChild(paramInt1); i != -1; i = getNextSibling(i)) {
        print(i, paramInt2 + 1);
      }
      System.out.println("</" + str + '>');
    }
  }
  
  public String getNodeName(int paramInt)
  {
    int i = paramInt;
    int j = getNodeType(i);
    switch (j)
    {
    case 0: 
    case 3: 
    case 8: 
    case 9: 
      return "";
    case 13: 
      return getLocalName(i);
    }
    return super.getNodeName(i);
  }
  
  public String getNamespaceName(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    String str;
    return (str = getNamespaceURI(paramInt)) == null ? "" : str;
  }
  
  public int getAttributeNode(int paramInt1, int paramInt2)
  {
    for (int i = getFirstAttribute(paramInt2); i != -1; i = getNextAttribute(i)) {
      if (getExpandedTypeID(i) == paramInt1) {
        return i;
      }
    }
    return -1;
  }
  
  public String getAttributeValue(int paramInt1, int paramInt2)
  {
    int i = getAttributeNode(paramInt1, paramInt2);
    return i != -1 ? getStringValueX(i) : "";
  }
  
  public String getAttributeValue(String paramString, int paramInt)
  {
    return getAttributeValue(getGeneralizedType(paramString), paramInt);
  }
  
  public DTMAxisIterator getChildren(int paramInt)
  {
    return new SAX2DTM2.ChildrenIterator(this).setStartNode(paramInt);
  }
  
  public DTMAxisIterator getTypedChildren(int paramInt)
  {
    return new SAX2DTM2.TypedChildrenIterator(this, paramInt);
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt)
  {
    switch (paramInt)
    {
    case 13: 
      return new DTMDefaultBaseIterators.SingletonIterator(this);
    case 3: 
      return new SAX2DTM2.ChildrenIterator(this);
    case 10: 
      return new SAX2DTM2.ParentIterator(this);
    case 0: 
      return new SAX2DTM2.AncestorIterator(this);
    case 1: 
      return new SAX2DTM2.AncestorIterator(this).includeSelf();
    case 2: 
      return new SAX2DTM2.AttributeIterator(this);
    case 4: 
      return new SAX2DTM2.DescendantIterator(this);
    case 5: 
      return new SAX2DTM2.DescendantIterator(this).includeSelf();
    case 6: 
      return new SAX2DTM2.FollowingIterator(this);
    case 11: 
      return new SAX2DTM2.PrecedingIterator(this);
    case 7: 
      return new SAX2DTM2.FollowingSiblingIterator(this);
    case 12: 
      return new SAX2DTM2.PrecedingSiblingIterator(this);
    case 9: 
      return new DTMDefaultBaseIterators.NamespaceIterator(this);
    case 19: 
      return new DTMDefaultBaseIterators.RootIterator(this);
    }
    BasisLibrary.runTimeError("AXIS_SUPPORT_ERR", Axis.getNames(paramInt));
    return null;
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 3) {
      return new SAX2DTM2.TypedChildrenIterator(this, paramInt2);
    }
    if (paramInt2 == -1) {
      return EMPTYITERATOR;
    }
    switch (paramInt1)
    {
    case 13: 
      return new SAX2DTM2.TypedSingletonIterator(this, paramInt2);
    case 3: 
      return new SAX2DTM2.TypedChildrenIterator(this, paramInt2);
    case 10: 
      return new SAX2DTM2.ParentIterator(this).setNodeType(paramInt2);
    case 0: 
      return new SAX2DTM2.TypedAncestorIterator(this, paramInt2);
    case 1: 
      return new SAX2DTM2.TypedAncestorIterator(this, paramInt2).includeSelf();
    case 2: 
      return new SAX2DTM2.TypedAttributeIterator(this, paramInt2);
    case 4: 
      return new SAX2DTM2.TypedDescendantIterator(this, paramInt2);
    case 5: 
      return new SAX2DTM2.TypedDescendantIterator(this, paramInt2).includeSelf();
    case 6: 
      return new SAX2DTM2.TypedFollowingIterator(this, paramInt2);
    case 11: 
      return new SAX2DTM2.TypedPrecedingIterator(this, paramInt2);
    case 7: 
      return new SAX2DTM2.TypedFollowingSiblingIterator(this, paramInt2);
    case 12: 
      return new SAX2DTM2.TypedPrecedingSiblingIterator(this, paramInt2);
    case 9: 
      return new TypedNamespaceIterator(paramInt2);
    case 19: 
      return new SAX2DTM2.TypedRootIterator(this, paramInt2);
    }
    BasisLibrary.runTimeError("TYPED_AXIS_SUPPORT_ERR", Axis.getNames(paramInt1));
    return null;
  }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2)
  {
    Object localObject = null;
    if (paramInt2 == -1) {
      return EMPTYITERATOR;
    }
    switch (paramInt1)
    {
    case 3: 
      return new NamespaceChildrenIterator(paramInt2);
    case 2: 
      return new NamespaceAttributeIterator(paramInt2);
    }
    return new NamespaceWildcardIterator(paramInt1, paramInt2);
  }
  
  public DTMAxisIterator getTypedDescendantIterator(int paramInt)
  {
    return new SAX2DTM2.TypedDescendantIterator(this, paramInt);
  }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    SAX2DTM2.TypedDescendantIterator localTypedDescendantIterator = new SAX2DTM2.TypedDescendantIterator(this, paramInt1);
    return new DTMDefaultBaseIterators.NthDescendantIterator(this, paramInt2);
  }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (paramInt != -1) {
      try
      {
        dispatchCharactersEvents(paramInt, paramSerializationHandler, false);
      }
      catch (SAXException localSAXException)
      {
        throw new TransletException(localSAXException);
      }
    }
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1) {
      copy(i, paramSerializationHandler);
    }
  }
  
  public void copy(SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    copy(getDocument(), paramSerializationHandler);
  }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    copy(paramInt, paramSerializationHandler, false);
  }
  
  private final void copy(int paramInt, SerializationHandler paramSerializationHandler, boolean paramBoolean)
    throws TransletException
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    int k = _exptype2Type(j);
    try
    {
      int m;
      switch (k)
      {
      case 0: 
      case 9: 
        for (m = _firstch2(i); m != -1; m = _nextsib2(m)) {
          copy(makeNodeHandle(m), paramSerializationHandler, true);
        }
        break;
      case 7: 
        copyPI(paramInt, paramSerializationHandler);
        break;
      case 8: 
        paramSerializationHandler.comment(getStringValueX(paramInt));
        break;
      case 3: 
        m = 0;
        boolean bool2 = false;
        boolean bool1;
        if (this._dontEscape != null)
        {
          bool2 = this._dontEscape.getBit(getNodeIdent(paramInt));
          if (bool2) {
            bool1 = paramSerializationHandler.setEscaping(false);
          }
        }
        copyTextNode(i, paramSerializationHandler);
        if (bool2) {
          paramSerializationHandler.setEscaping(bool1);
        }
        break;
      case 2: 
        copyAttribute(i, j, paramSerializationHandler);
        break;
      case 13: 
        paramSerializationHandler.namespaceAfterStartElement(getNodeNameX(paramInt), getNodeValue(paramInt));
        break;
      case 1: 
      case 4: 
      case 5: 
      case 6: 
      case 10: 
      case 11: 
      case 12: 
      default: 
        String str1;
        if (k == 1)
        {
          str1 = copyElement(i, j, paramSerializationHandler);
          copyNS(i, paramSerializationHandler, !paramBoolean);
          copyAttributes(i, paramSerializationHandler);
          for (int n = _firstch2(i); n != -1; n = _nextsib2(n)) {
            copy(makeNodeHandle(n), paramSerializationHandler, true);
          }
          paramSerializationHandler.endElement(str1);
        }
        else
        {
          str1 = getNamespaceName(paramInt);
          if (str1.length() != 0)
          {
            String str2 = getPrefix(paramInt);
            paramSerializationHandler.namespaceAfterStartElement(str2, str1);
          }
          paramSerializationHandler.addAttribute(getNodeName(paramInt), getNodeValue(paramInt));
        }
        break;
      }
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  private void copyPI(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    String str1 = getNodeName(paramInt);
    String str2 = getStringValueX(paramInt);
    try
    {
      paramSerializationHandler.processingInstruction(str1, str2);
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    int k = _exptype2Type(j);
    try
    {
      switch (k)
      {
      case 1: 
        String str1 = copyElement(i, j, paramSerializationHandler);
        copyNS(i, paramSerializationHandler, true);
        return str1;
      case 0: 
      case 9: 
        return "";
      case 3: 
        copyTextNode(i, paramSerializationHandler);
        return null;
      case 7: 
        copyPI(paramInt, paramSerializationHandler);
        return null;
      case 8: 
        paramSerializationHandler.comment(getStringValueX(paramInt));
        return null;
      case 13: 
        paramSerializationHandler.namespaceAfterStartElement(getNodeNameX(paramInt), getNodeValue(paramInt));
        return null;
      case 2: 
        copyAttribute(i, j, paramSerializationHandler);
        return null;
      }
      String str2 = getNamespaceName(paramInt);
      if (str2.length() != 0)
      {
        String str3 = getPrefix(paramInt);
        paramSerializationHandler.namespaceAfterStartElement(str3, str2);
      }
      paramSerializationHandler.addAttribute(getNodeName(paramInt), getNodeValue(paramInt));
      return null;
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  public String getLanguage(int paramInt)
  {
    for (int i = paramInt; -1 != i; i = getParent(i)) {
      if (1 == getNodeType(i))
      {
        int j = getAttributeNode(i, "http://www.w3.org/XML/1998/namespace", "lang");
        if (-1 != j) {
          return getNodeValue(j);
        }
      }
    }
    return null;
  }
  
  public DOMBuilder getBuilder()
  {
    return this;
  }
  
  public SerializationHandler getOutputDomBuilder()
  {
    return new ToXMLSAXHandler(this, "UTF-8");
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2)
  {
    return getResultTreeFrag(paramInt1, paramInt2, true);
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    Object localObject;
    if (paramInt2 == 0)
    {
      if (paramBoolean)
      {
        i = this._dtmManager.getFirstFreeDTMID();
        localObject = new SimpleResultTreeImpl(this._dtmManager, i << 16);
        this._dtmManager.addDTM((DTM)localObject, i, 0);
        return localObject;
      }
      return new SimpleResultTreeImpl(this._dtmManager, 0);
    }
    if (paramInt2 == 1)
    {
      if (paramBoolean)
      {
        i = this._dtmManager.getFirstFreeDTMID();
        localObject = new AdaptiveResultTreeImpl(this._dtmManager, i << 16, this.m_wsfilter, paramInt1, this.m_buildIdIndex);
        this._dtmManager.addDTM((DTM)localObject, i, 0);
        return localObject;
      }
      return new AdaptiveResultTreeImpl(this._dtmManager, 0, this.m_wsfilter, paramInt1, this.m_buildIdIndex);
    }
    return (DOM)this._dtmManager.getDTM(null, true, this.m_wsfilter, true, false, false, paramInt1, this.m_buildIdIndex);
  }
  
  public Map<String, Integer> getElementsWithIDs()
  {
    return this.m_idAttributes;
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    if (this._document != null)
    {
      String str1 = "";
      DocumentType localDocumentType = this._document.getDoctype();
      if (localDocumentType != null)
      {
        NamedNodeMap localNamedNodeMap = localDocumentType.getEntities();
        if (localNamedNodeMap == null) {
          return str1;
        }
        Entity localEntity = (Entity)localNamedNodeMap.getNamedItem(paramString);
        if (localEntity == null) {
          return str1;
        }
        String str2 = localEntity.getNotationName();
        if (str2 != null)
        {
          str1 = localEntity.getSystemId();
          if (str1 == null) {
            str1 = localEntity.getPublicId();
          }
        }
      }
      return str1;
    }
    return super.getUnparsedEntityURI(paramString);
  }
  
  public final class NamespaceAttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nsType;
    
    public NamespaceAttributeIterator(int paramInt)
    {
      super();
      this._nsType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAXImpl.this.getDocument();
      }
      if (this._isRestartable)
      {
        int i = this._nsType;
        this._startNode = paramInt;
        for (paramInt = SAXImpl.this.getFirstAttribute(paramInt); (paramInt != -1) && (SAXImpl.this.getNSType(paramInt) != i); paramInt = SAXImpl.this.getNextAttribute(paramInt)) {}
        this._currentNode = paramInt;
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = this._currentNode;
      int j = this._nsType;
      if (i == -1) {
        return -1;
      }
      for (int k = SAXImpl.this.getNextAttribute(i); (k != -1) && (SAXImpl.this.getNSType(k) != j); k = SAXImpl.this.getNextAttribute(k)) {}
      this._currentNode = k;
      return returnNode(i);
    }
  }
  
  public final class NamespaceChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nsType;
    
    public NamespaceChildrenIterator(int paramInt)
    {
      super();
      this._nsType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = SAXImpl.this.getDocument();
      }
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this._currentNode = (paramInt == -1 ? -1 : -2);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if (this._currentNode != -1) {
        for (int i = -2 == this._currentNode ? SAXImpl.this._firstch(SAXImpl.this.makeNodeIdentity(this._startNode)) : SAXImpl.this._nextsib(this._currentNode); i != -1; i = SAXImpl.this._nextsib(i))
        {
          int j = SAXImpl.this.makeNodeHandle(i);
          if (SAXImpl.this.getNSType(j) == this._nsType)
          {
            this._currentNode = i;
            return returnNode(j);
          }
        }
      }
      return -1;
    }
  }
  
  public final class NamespaceWildcardIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    protected int m_nsType;
    protected DTMAxisIterator m_baseIterator;
    
    public NamespaceWildcardIterator(int paramInt1, int paramInt2)
    {
      super();
      this.m_nsType = paramInt2;
      switch (paramInt1)
      {
      case 2: 
        this.m_baseIterator = SAXImpl.this.getAxisIterator(paramInt1);
      case 9: 
        this.m_baseIterator = SAXImpl.this.getAxisIterator(paramInt1);
      }
      this.m_baseIterator = SAXImpl.this.getTypedAxisIterator(paramInt1, 1);
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (this._isRestartable)
      {
        this._startNode = paramInt;
        this.m_baseIterator.setStartNode(paramInt);
        resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i;
      while ((i = this.m_baseIterator.next()) != -1) {
        if (SAXImpl.this.getNSType(i) == this.m_nsType) {
          return returnNode(i);
        }
      }
      return -1;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      try
      {
        DTMAxisIterator localDTMAxisIterator = this.m_baseIterator.cloneIterator();
        NamespaceWildcardIterator localNamespaceWildcardIterator = (NamespaceWildcardIterator)super.clone();
        localNamespaceWildcardIterator.m_baseIterator = localDTMAxisIterator;
        localNamespaceWildcardIterator.m_nsType = this.m_nsType;
        localNamespaceWildcardIterator._isRestartable = false;
        return localNamespaceWildcardIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
      }
      return null;
    }
    
    public boolean isReverse()
    {
      return this.m_baseIterator.isReverse();
    }
    
    public void setMark()
    {
      this.m_baseIterator.setMark();
    }
    
    public void gotoMark()
    {
      this.m_baseIterator.gotoMark();
    }
  }
  
  private final class NodeValueIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private DTMAxisIterator _source;
    private String _value;
    private boolean _op;
    private final boolean _isReverse;
    private int _returnType = 1;
    
    public NodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean)
    {
      super();
      this._source = paramDTMAxisIterator;
      this._returnType = paramInt;
      this._value = paramString;
      this._op = paramBoolean;
      this._isReverse = paramDTMAxisIterator.isReverse();
    }
    
    public boolean isReverse()
    {
      return this._isReverse;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      try
      {
        NodeValueIterator localNodeValueIterator = (NodeValueIterator)super.clone();
        localNodeValueIterator._isRestartable = false;
        localNodeValueIterator._source = this._source.cloneIterator();
        localNodeValueIterator._value = this._value;
        localNodeValueIterator._op = this._op;
        return localNodeValueIterator.reset();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
      }
      return null;
    }
    
    public void setRestartable(boolean paramBoolean)
    {
      this._isRestartable = paramBoolean;
      this._source.setRestartable(paramBoolean);
    }
    
    public DTMAxisIterator reset()
    {
      this._source.reset();
      return resetPosition();
    }
    
    public int next()
    {
      int i;
      while ((i = this._source.next()) != -1)
      {
        String str = SAXImpl.this.getStringValueX(i);
        if (this._value.equals(str) == this._op)
        {
          if (this._returnType == 0) {
            return returnNode(i);
          }
          return returnNode(SAXImpl.this.getParent(i));
        }
      }
      return -1;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (this._isRestartable)
      {
        this._source.setStartNode(this._startNode = paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public void setMark()
    {
      this._source.setMark();
    }
    
    public void gotoMark()
    {
      this._source.gotoMark();
    }
  }
  
  public class TypedNamespaceIterator
    extends DTMDefaultBaseIterators.NamespaceIterator
  {
    private String _nsPrefix;
    
    public TypedNamespaceIterator(int paramInt)
    {
      super();
      if (SAXImpl.this.m_expandedNameTable != null) {
        this._nsPrefix = SAXImpl.this.m_expandedNameTable.getLocalName(paramInt);
      }
    }
    
    public int next()
    {
      if ((this._nsPrefix == null) || (this._nsPrefix.length() == 0)) {
        return -1;
      }
      int i = -1;
      for (i = super.next(); i != -1; i = super.next()) {
        if (this._nsPrefix.compareTo(SAXImpl.this.getLocalName(i)) == 0) {
          return returnNode(i);
        }
      }
      return -1;
    }
  }
}
