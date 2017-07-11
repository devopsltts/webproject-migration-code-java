package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizerSpi;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public abstract class CanonicalizerBase
  extends CanonicalizerSpi
{
  public static final String XML = "xml";
  public static final String XMLNS = "xmlns";
  protected static final AttrCompare COMPARE = new AttrCompare();
  private static final byte[] END_PI = { 63, 62 };
  private static final byte[] BEGIN_PI = { 60, 63 };
  private static final byte[] END_COMM = { 45, 45, 62 };
  private static final byte[] BEGIN_COMM = { 60, 33, 45, 45 };
  private static final byte[] XA = { 38, 35, 120, 65, 59 };
  private static final byte[] X9 = { 38, 35, 120, 57, 59 };
  private static final byte[] QUOT = { 38, 113, 117, 111, 116, 59 };
  private static final byte[] XD = { 38, 35, 120, 68, 59 };
  private static final byte[] GT = { 38, 103, 116, 59 };
  private static final byte[] LT = { 38, 108, 116, 59 };
  private static final byte[] END_TAG = { 60, 47 };
  private static final byte[] AMP = { 38, 97, 109, 112, 59 };
  private static final byte[] EQUALS_STR = { 61, 34 };
  protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
  protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
  protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
  private List<NodeFilter> nodeFilter;
  private boolean includeComments;
  private Set<Node> xpathNodeSet;
  private Node excludeNode;
  private OutputStream writer = new ByteArrayOutputStream();
  private Attr nullNode;
  
  public CanonicalizerBase(boolean paramBoolean)
  {
    this.includeComments = paramBoolean;
  }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode)
    throws CanonicalizationException
  {
    return engineCanonicalizeSubTree(paramNode, (Node)null);
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet)
    throws CanonicalizationException
  {
    this.xpathNodeSet = paramSet;
    return engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this.xpathNodeSet));
  }
  
  public byte[] engineCanonicalize(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException
  {
    try
    {
      if (paramXMLSignatureInput.isExcludeComments()) {
        this.includeComments = false;
      }
      if (paramXMLSignatureInput.isOctetStream()) {
        return engineCanonicalize(paramXMLSignatureInput.getBytes());
      }
      if (paramXMLSignatureInput.isElement()) {
        return engineCanonicalizeSubTree(paramXMLSignatureInput.getSubNode(), paramXMLSignatureInput.getExcludeNode());
      }
      if (paramXMLSignatureInput.isNodeSet())
      {
        this.nodeFilter = paramXMLSignatureInput.getNodeFilters();
        circumventBugIfNeeded(paramXMLSignatureInput);
        if (paramXMLSignatureInput.getSubNode() != null) {
          return engineCanonicalizeXPathNodeSetInternal(paramXMLSignatureInput.getSubNode());
        }
        return engineCanonicalizeXPathNodeSet(paramXMLSignatureInput.getNodeSet());
      }
      return null;
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new CanonicalizationException("empty", localCanonicalizationException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new CanonicalizationException("empty", localParserConfigurationException);
    }
    catch (IOException localIOException)
    {
      throw new CanonicalizationException("empty", localIOException);
    }
    catch (SAXException localSAXException)
    {
      throw new CanonicalizationException("empty", localSAXException);
    }
  }
  
  public void setWriter(OutputStream paramOutputStream)
  {
    this.writer = paramOutputStream;
  }
  
  protected byte[] engineCanonicalizeSubTree(Node paramNode1, Node paramNode2)
    throws CanonicalizationException
  {
    this.excludeNode = paramNode2;
    try
    {
      NameSpaceSymbTable localNameSpaceSymbTable = new NameSpaceSymbTable();
      int i = -1;
      if ((paramNode1 != null) && (1 == paramNode1.getNodeType()))
      {
        getParentNameSpaces((Element)paramNode1, localNameSpaceSymbTable);
        i = 0;
      }
      canonicalizeSubTree(paramNode1, localNameSpaceSymbTable, paramNode1, i);
      this.writer.flush();
      byte[] arrayOfByte;
      if ((this.writer instanceof ByteArrayOutputStream))
      {
        arrayOfByte = ((ByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((ByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        }
        return arrayOfByte;
      }
      if ((this.writer instanceof UnsyncByteArrayOutputStream))
      {
        arrayOfByte = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((UnsyncByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        }
        return arrayOfByte;
      }
      this.writer.close();
      return null;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new CanonicalizationException("empty", localUnsupportedEncodingException);
    }
    catch (IOException localIOException)
    {
      throw new CanonicalizationException("empty", localIOException);
    }
  }
  
  protected final void canonicalizeSubTree(Node paramNode1, NameSpaceSymbTable paramNameSpaceSymbTable, Node paramNode2, int paramInt)
    throws CanonicalizationException, IOException
  {
    if (isVisibleInt(paramNode1) == -1) {
      return;
    }
    Node localNode1 = null;
    Object localObject = null;
    OutputStream localOutputStream = this.writer;
    Node localNode2 = this.excludeNode;
    boolean bool = this.includeComments;
    HashMap localHashMap = new HashMap();
    for (;;)
    {
      switch (paramNode1.getNodeType())
      {
      case 2: 
      case 6: 
      case 12: 
        throw new CanonicalizationException("empty");
      case 9: 
      case 11: 
        paramNameSpaceSymbTable.outputNodePush();
        localNode1 = paramNode1.getFirstChild();
        break;
      case 8: 
        if (bool) {
          outputCommentToWriter((Comment)paramNode1, localOutputStream, paramInt);
        }
        break;
      case 7: 
        outputPItoWriter((ProcessingInstruction)paramNode1, localOutputStream, paramInt);
        break;
      case 3: 
      case 4: 
        outputTextToWriter(paramNode1.getNodeValue(), localOutputStream);
        break;
      case 1: 
        paramInt = 0;
        if (paramNode1 != localNode2)
        {
          Element localElement = (Element)paramNode1;
          paramNameSpaceSymbTable.outputNodePush();
          localOutputStream.write(60);
          String str = localElement.getTagName();
          UtfHelpper.writeByte(str, localOutputStream, localHashMap);
          Iterator localIterator = handleAttributesSubtree(localElement, paramNameSpaceSymbTable);
          if (localIterator != null) {
            while (localIterator.hasNext())
            {
              Attr localAttr = (Attr)localIterator.next();
              outputAttrToWriter(localAttr.getNodeName(), localAttr.getNodeValue(), localOutputStream, localHashMap);
            }
          }
          localOutputStream.write(62);
          localNode1 = paramNode1.getFirstChild();
          if (localNode1 == null)
          {
            localOutputStream.write((byte[])END_TAG.clone());
            UtfHelpper.writeStringToUtf8(str, localOutputStream);
            localOutputStream.write(62);
            paramNameSpaceSymbTable.outputNodePop();
            if (localObject != null) {
              localNode1 = paramNode1.getNextSibling();
            }
          }
          else
          {
            localObject = localElement;
          }
        }
        break;
      }
      while ((localNode1 == null) && (localObject != null))
      {
        localOutputStream.write((byte[])END_TAG.clone());
        UtfHelpper.writeByte(((Element)localObject).getTagName(), localOutputStream, localHashMap);
        localOutputStream.write(62);
        paramNameSpaceSymbTable.outputNodePop();
        if (localObject == paramNode2) {
          return;
        }
        localNode1 = ((Node)localObject).getNextSibling();
        localObject = ((Node)localObject).getParentNode();
        if ((localObject == null) || (1 != ((Node)localObject).getNodeType()))
        {
          paramInt = 1;
          localObject = null;
        }
      }
      if (localNode1 == null) {
        return;
      }
      paramNode1 = localNode1;
      localNode1 = paramNode1.getNextSibling();
    }
  }
  
  private byte[] engineCanonicalizeXPathNodeSetInternal(Node paramNode)
    throws CanonicalizationException
  {
    try
    {
      canonicalizeXPathNodeSet(paramNode, paramNode);
      this.writer.flush();
      byte[] arrayOfByte;
      if ((this.writer instanceof ByteArrayOutputStream))
      {
        arrayOfByte = ((ByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((ByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        }
        return arrayOfByte;
      }
      if ((this.writer instanceof UnsyncByteArrayOutputStream))
      {
        arrayOfByte = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((UnsyncByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        }
        return arrayOfByte;
      }
      this.writer.close();
      return null;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new CanonicalizationException("empty", localUnsupportedEncodingException);
    }
    catch (IOException localIOException)
    {
      throw new CanonicalizationException("empty", localIOException);
    }
  }
  
  protected final void canonicalizeXPathNodeSet(Node paramNode1, Node paramNode2)
    throws CanonicalizationException, IOException
  {
    if (isVisibleInt(paramNode1) == -1) {
      return;
    }
    int i = 0;
    NameSpaceSymbTable localNameSpaceSymbTable = new NameSpaceSymbTable();
    if ((paramNode1 != null) && (1 == paramNode1.getNodeType())) {
      getParentNameSpaces((Element)paramNode1, localNameSpaceSymbTable);
    }
    if (paramNode1 == null) {
      return;
    }
    Node localNode = null;
    Object localObject1 = null;
    OutputStream localOutputStream = this.writer;
    int j = -1;
    HashMap localHashMap = new HashMap();
    for (;;)
    {
      Object localObject2;
      switch (paramNode1.getNodeType())
      {
      case 2: 
      case 6: 
      case 12: 
        throw new CanonicalizationException("empty");
      case 9: 
      case 11: 
        localNameSpaceSymbTable.outputNodePush();
        localNode = paramNode1.getFirstChild();
        break;
      case 8: 
        if ((this.includeComments) && (isVisibleDO(paramNode1, localNameSpaceSymbTable.getLevel()) == 1)) {
          outputCommentToWriter((Comment)paramNode1, localOutputStream, j);
        }
        break;
      case 7: 
        if (isVisible(paramNode1)) {
          outputPItoWriter((ProcessingInstruction)paramNode1, localOutputStream, j);
        }
        break;
      case 3: 
      case 4: 
        if (isVisible(paramNode1))
        {
          outputTextToWriter(paramNode1.getNodeValue(), localOutputStream);
          for (localObject2 = paramNode1.getNextSibling(); (localObject2 != null) && ((((Node)localObject2).getNodeType() == 3) || (((Node)localObject2).getNodeType() == 4)); localObject2 = ((Node)localObject2).getNextSibling())
          {
            outputTextToWriter(((Node)localObject2).getNodeValue(), localOutputStream);
            paramNode1 = (Node)localObject2;
            localNode = paramNode1.getNextSibling();
          }
        }
        break;
      case 1: 
        j = 0;
        localObject2 = (Element)paramNode1;
        String str = null;
        int k = isVisibleDO(paramNode1, localNameSpaceSymbTable.getLevel());
        if (k == -1)
        {
          localNode = paramNode1.getNextSibling();
        }
        else
        {
          i = k == 1 ? 1 : 0;
          if (i != 0)
          {
            localNameSpaceSymbTable.outputNodePush();
            localOutputStream.write(60);
            str = ((Element)localObject2).getTagName();
            UtfHelpper.writeByte(str, localOutputStream, localHashMap);
          }
          else
          {
            localNameSpaceSymbTable.push();
          }
          Iterator localIterator = handleAttributes((Element)localObject2, localNameSpaceSymbTable);
          if (localIterator != null) {
            while (localIterator.hasNext())
            {
              Attr localAttr = (Attr)localIterator.next();
              outputAttrToWriter(localAttr.getNodeName(), localAttr.getNodeValue(), localOutputStream, localHashMap);
            }
          }
          if (i != 0) {
            localOutputStream.write(62);
          }
          localNode = paramNode1.getFirstChild();
          if (localNode == null)
          {
            if (i != 0)
            {
              localOutputStream.write((byte[])END_TAG.clone());
              UtfHelpper.writeByte(str, localOutputStream, localHashMap);
              localOutputStream.write(62);
              localNameSpaceSymbTable.outputNodePop();
            }
            else
            {
              localNameSpaceSymbTable.pop();
            }
            if (localObject1 != null) {
              localNode = paramNode1.getNextSibling();
            }
          }
          else
          {
            localObject1 = localObject2;
          }
        }
        break;
      }
      while ((localNode == null) && (localObject1 != null))
      {
        if (isVisible((Node)localObject1))
        {
          localOutputStream.write((byte[])END_TAG.clone());
          UtfHelpper.writeByte(((Element)localObject1).getTagName(), localOutputStream, localHashMap);
          localOutputStream.write(62);
          localNameSpaceSymbTable.outputNodePop();
        }
        else
        {
          localNameSpaceSymbTable.pop();
        }
        if (localObject1 == paramNode2) {
          return;
        }
        localNode = ((Node)localObject1).getNextSibling();
        localObject1 = ((Node)localObject1).getParentNode();
        if ((localObject1 == null) || (1 != ((Node)localObject1).getNodeType()))
        {
          localObject1 = null;
          j = 1;
        }
      }
      if (localNode == null) {
        return;
      }
      paramNode1 = localNode;
      localNode = paramNode1.getNextSibling();
    }
  }
  
  protected int isVisibleDO(Node paramNode, int paramInt)
  {
    if (this.nodeFilter != null)
    {
      Iterator localIterator = this.nodeFilter.iterator();
      while (localIterator.hasNext())
      {
        int i = ((NodeFilter)localIterator.next()).isNodeIncludeDO(paramNode, paramInt);
        if (i != 1) {
          return i;
        }
      }
    }
    if ((this.xpathNodeSet != null) && (!this.xpathNodeSet.contains(paramNode))) {
      return 0;
    }
    return 1;
  }
  
  protected int isVisibleInt(Node paramNode)
  {
    if (this.nodeFilter != null)
    {
      Iterator localIterator = this.nodeFilter.iterator();
      while (localIterator.hasNext())
      {
        int i = ((NodeFilter)localIterator.next()).isNodeInclude(paramNode);
        if (i != 1) {
          return i;
        }
      }
    }
    if ((this.xpathNodeSet != null) && (!this.xpathNodeSet.contains(paramNode))) {
      return 0;
    }
    return 1;
  }
  
  protected boolean isVisible(Node paramNode)
  {
    if (this.nodeFilter != null)
    {
      Iterator localIterator = this.nodeFilter.iterator();
      while (localIterator.hasNext()) {
        if (((NodeFilter)localIterator.next()).isNodeInclude(paramNode) != 1) {
          return false;
        }
      }
    }
    return (this.xpathNodeSet == null) || (this.xpathNodeSet.contains(paramNode));
  }
  
  protected void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
  {
    if ((!paramElement.hasAttributes()) && (paramElement.getNamespaceURI() == null)) {
      return;
    }
    NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
    int i = localNamedNodeMap.getLength();
    Object localObject1;
    String str2;
    Object localObject2;
    for (int j = 0; j < i; j++)
    {
      localObject1 = (Attr)localNamedNodeMap.item(j);
      str2 = ((Attr)localObject1).getLocalName();
      localObject2 = ((Attr)localObject1).getNodeValue();
      if (("http://www.w3.org/2000/xmlns/".equals(((Attr)localObject1).getNamespaceURI())) && ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(localObject2)))) {
        paramNameSpaceSymbTable.addMapping(str2, (String)localObject2, (Attr)localObject1);
      }
    }
    if (paramElement.getNamespaceURI() != null)
    {
      String str1 = paramElement.getPrefix();
      localObject1 = paramElement.getNamespaceURI();
      if ((str1 == null) || (str1.equals("")))
      {
        str1 = "xmlns";
        str2 = "xmlns";
      }
      else
      {
        str2 = "xmlns:" + str1;
      }
      localObject2 = paramElement.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", str2);
      ((Attr)localObject2).setValue((String)localObject1);
      paramNameSpaceSymbTable.addMapping(str1, (String)localObject1, (Attr)localObject2);
    }
  }
  
  protected final void getParentNameSpaces(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
  {
    Node localNode1 = paramElement.getParentNode();
    if ((localNode1 == null) || (1 != localNode1.getNodeType())) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    for (Node localNode2 = localNode1; (localNode2 != null) && (1 == localNode2.getNodeType()); localNode2 = localNode2.getParentNode()) {
      localArrayList.add((Element)localNode2);
    }
    ListIterator localListIterator = localArrayList.listIterator(localArrayList.size());
    Object localObject;
    while (localListIterator.hasPrevious())
    {
      localObject = (Element)localListIterator.previous();
      handleParent((Element)localObject, paramNameSpaceSymbTable);
    }
    localArrayList.clear();
    if (((localObject = paramNameSpaceSymbTable.getMappingWithoutRendered("xmlns")) != null) && ("".equals(((Attr)localObject).getValue()))) {
      paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", getNullNode(((Attr)localObject).getOwnerDocument()));
    }
  }
  
  abstract Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException;
  
  abstract Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException;
  
  abstract void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException;
  
  protected static final void outputAttrToWriter(String paramString1, String paramString2, OutputStream paramOutputStream, Map<String, byte[]> paramMap)
    throws IOException
  {
    paramOutputStream.write(32);
    UtfHelpper.writeByte(paramString1, paramOutputStream, paramMap);
    paramOutputStream.write((byte[])EQUALS_STR.clone());
    int i = paramString2.length();
    int j = 0;
    while (j < i)
    {
      char c = paramString2.charAt(j++);
      byte[] arrayOfByte;
      switch (c)
      {
      case '&': 
        arrayOfByte = (byte[])AMP.clone();
        break;
      case '<': 
        arrayOfByte = (byte[])LT.clone();
        break;
      case '"': 
        arrayOfByte = (byte[])QUOT.clone();
        break;
      case '\t': 
        arrayOfByte = (byte[])X9.clone();
        break;
      case '\n': 
        arrayOfByte = (byte[])XA.clone();
        break;
      case '\r': 
        arrayOfByte = (byte[])XD.clone();
        break;
      default: 
        if (c < '')
        {
          paramOutputStream.write(c);
          continue;
        }
        UtfHelpper.writeCharToUtf8(c, paramOutputStream);
        break;
      }
      paramOutputStream.write(arrayOfByte);
    }
    paramOutputStream.write(34);
  }
  
  protected void outputPItoWriter(ProcessingInstruction paramProcessingInstruction, OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    if (paramInt == 1) {
      paramOutputStream.write(10);
    }
    paramOutputStream.write((byte[])BEGIN_PI.clone());
    String str1 = paramProcessingInstruction.getTarget();
    int i = str1.length();
    int k;
    for (int j = 0; j < i; j++)
    {
      k = str1.charAt(j);
      if (k == 13) {
        paramOutputStream.write((byte[])XD.clone());
      } else if (k < 128) {
        paramOutputStream.write(k);
      } else {
        UtfHelpper.writeCharToUtf8(k, paramOutputStream);
      }
    }
    String str2 = paramProcessingInstruction.getData();
    i = str2.length();
    if (i > 0)
    {
      paramOutputStream.write(32);
      for (k = 0; k < i; k++)
      {
        char c = str2.charAt(k);
        if (c == '\r') {
          paramOutputStream.write((byte[])XD.clone());
        } else {
          UtfHelpper.writeCharToUtf8(c, paramOutputStream);
        }
      }
    }
    paramOutputStream.write((byte[])END_PI.clone());
    if (paramInt == -1) {
      paramOutputStream.write(10);
    }
  }
  
  protected void outputCommentToWriter(Comment paramComment, OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    if (paramInt == 1) {
      paramOutputStream.write(10);
    }
    paramOutputStream.write((byte[])BEGIN_COMM.clone());
    String str = paramComment.getData();
    int i = str.length();
    for (int j = 0; j < i; j++)
    {
      char c = str.charAt(j);
      if (c == '\r') {
        paramOutputStream.write((byte[])XD.clone());
      } else if (c < '') {
        paramOutputStream.write(c);
      } else {
        UtfHelpper.writeCharToUtf8(c, paramOutputStream);
      }
    }
    paramOutputStream.write((byte[])END_COMM.clone());
    if (paramInt == -1) {
      paramOutputStream.write(10);
    }
  }
  
  protected static final void outputTextToWriter(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      byte[] arrayOfByte;
      switch (c)
      {
      case '&': 
        arrayOfByte = (byte[])AMP.clone();
        break;
      case '<': 
        arrayOfByte = (byte[])LT.clone();
        break;
      case '>': 
        arrayOfByte = (byte[])GT.clone();
        break;
      case '\r': 
        arrayOfByte = (byte[])XD.clone();
        break;
      default: 
        if (c < '') {
          paramOutputStream.write(c);
        } else {
          UtfHelpper.writeCharToUtf8(c, paramOutputStream);
        }
        break;
      }
      paramOutputStream.write(arrayOfByte);
    }
  }
  
  protected Attr getNullNode(Document paramDocument)
  {
    if (this.nullNode == null) {
      try
      {
        this.nullNode = paramDocument.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
        this.nullNode.setValue("");
      }
      catch (Exception localException)
      {
        throw new RuntimeException("Unable to create nullNode: " + localException);
      }
    }
    return this.nullNode;
  }
}
