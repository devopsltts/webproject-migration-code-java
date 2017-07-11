package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer20010315
  extends CanonicalizerBase
{
  private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
  private boolean firstCall = true;
  private final SortedSet<Attr> result = new TreeSet(COMPARE);
  private XmlAttrStack xmlattrStack = new XmlAttrStack(null);
  
  public Canonicalizer20010315(boolean paramBoolean)
  {
    super(paramBoolean);
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString)
    throws CanonicalizationException
  {
    throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
  }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString)
    throws CanonicalizationException
  {
    throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
  }
  
  protected Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException
  {
    if ((!paramElement.hasAttributes()) && (!this.firstCall)) {
      return null;
    }
    SortedSet localSortedSet = this.result;
    localSortedSet.clear();
    if (paramElement.hasAttributes())
    {
      NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
      int i = localNamedNodeMap.getLength();
      for (int j = 0; j < i; j++)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(j);
        String str1 = localAttr.getNamespaceURI();
        String str2 = localAttr.getLocalName();
        String str3 = localAttr.getValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(str1))
        {
          localSortedSet.add(localAttr);
        }
        else if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3)))
        {
          Node localNode = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, localAttr);
          if (localNode != null)
          {
            localSortedSet.add((Attr)localNode);
            if (C14nHelper.namespaceIsRelative(localAttr))
            {
              Object[] arrayOfObject = { paramElement.getTagName(), str2, localAttr.getNodeValue() };
              throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
            }
          }
        }
      }
    }
    if (this.firstCall)
    {
      paramNameSpaceSymbTable.getUnrenderedNodes(localSortedSet);
      this.xmlattrStack.getXmlnsAttr(localSortedSet);
      this.firstCall = false;
    }
    return localSortedSet.iterator();
  }
  
  protected Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException
  {
    this.xmlattrStack.push(paramNameSpaceSymbTable.getLevel());
    int i = isVisibleDO(paramElement, paramNameSpaceSymbTable.getLevel()) == 1 ? 1 : 0;
    SortedSet localSortedSet = this.result;
    localSortedSet.clear();
    Object localObject1;
    if (paramElement.hasAttributes())
    {
      localObject1 = paramElement.getAttributes();
      int j = ((NamedNodeMap)localObject1).getLength();
      for (int k = 0; k < j; k++)
      {
        Attr localAttr = (Attr)((NamedNodeMap)localObject1).item(k);
        String str1 = localAttr.getNamespaceURI();
        String str2 = localAttr.getLocalName();
        String str3 = localAttr.getValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(str1))
        {
          if ("http://www.w3.org/XML/1998/namespace".equals(str1)) {
            this.xmlattrStack.addXmlnsAttr(localAttr);
          } else if (i != 0) {
            localSortedSet.add(localAttr);
          }
        }
        else if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3))) {
          if (isVisible(localAttr))
          {
            if ((i != 0) || (!paramNameSpaceSymbTable.removeMappingIfRender(str2)))
            {
              Node localNode = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, localAttr);
              if (localNode != null)
              {
                localSortedSet.add((Attr)localNode);
                if (C14nHelper.namespaceIsRelative(localAttr))
                {
                  Object[] arrayOfObject = { paramElement.getTagName(), str2, localAttr.getNodeValue() };
                  throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
                }
              }
            }
          }
          else if ((i != 0) && (!"xmlns".equals(str2))) {
            paramNameSpaceSymbTable.removeMapping(str2);
          } else {
            paramNameSpaceSymbTable.addMapping(str2, str3, localAttr);
          }
        }
      }
    }
    if (i != 0)
    {
      localObject1 = paramElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
      Object localObject2 = null;
      if (localObject1 == null) {
        localObject2 = paramNameSpaceSymbTable.getMapping("xmlns");
      } else if (!isVisible((Node)localObject1)) {
        localObject2 = paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", getNullNode(((Attr)localObject1).getOwnerDocument()));
      }
      if (localObject2 != null) {
        localSortedSet.add((Attr)localObject2);
      }
      this.xmlattrStack.getXmlnsAttr(localSortedSet);
      paramNameSpaceSymbTable.getUnrenderedNodes(localSortedSet);
    }
    return localSortedSet.iterator();
  }
  
  protected void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
  {
    if (!paramXMLSignatureInput.isNeedsToBeExpanded()) {
      return;
    }
    Document localDocument = null;
    if (paramXMLSignatureInput.getSubNode() != null) {
      localDocument = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
    } else {
      localDocument = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
    }
    XMLUtils.circumventBug2650(localDocument);
  }
  
  protected void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
  {
    if ((!paramElement.hasAttributes()) && (paramElement.getNamespaceURI() == null)) {
      return;
    }
    this.xmlattrStack.push(-1);
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
      if ("http://www.w3.org/2000/xmlns/".equals(((Attr)localObject1).getNamespaceURI()))
      {
        if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(localObject2))) {
          paramNameSpaceSymbTable.addMapping(str2, (String)localObject2, (Attr)localObject1);
        }
      }
      else if ("http://www.w3.org/XML/1998/namespace".equals(((Attr)localObject1).getNamespaceURI())) {
        this.xmlattrStack.addXmlnsAttr((Attr)localObject1);
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
  
  private static class XmlAttrStack
  {
    int currentLevel = 0;
    int lastlevel = 0;
    XmlsStackElement cur;
    List<XmlsStackElement> levels = new ArrayList();
    
    private XmlAttrStack() {}
    
    void push(int paramInt)
    {
      this.currentLevel = paramInt;
      if (this.currentLevel == -1) {
        return;
      }
      this.cur = null;
      while (this.lastlevel >= this.currentLevel)
      {
        this.levels.remove(this.levels.size() - 1);
        int i = this.levels.size();
        if (i == 0)
        {
          this.lastlevel = 0;
          return;
        }
        this.lastlevel = ((XmlsStackElement)this.levels.get(i - 1)).level;
      }
    }
    
    void addXmlnsAttr(Attr paramAttr)
    {
      if (this.cur == null)
      {
        this.cur = new XmlsStackElement();
        this.cur.level = this.currentLevel;
        this.levels.add(this.cur);
        this.lastlevel = this.currentLevel;
      }
      this.cur.nodes.add(paramAttr);
    }
    
    void getXmlnsAttr(Collection<Attr> paramCollection)
    {
      int i = this.levels.size() - 1;
      if (this.cur == null)
      {
        this.cur = new XmlsStackElement();
        this.cur.level = this.currentLevel;
        this.lastlevel = this.currentLevel;
        this.levels.add(this.cur);
      }
      int j = 0;
      XmlsStackElement localXmlsStackElement = null;
      if (i == -1)
      {
        j = 1;
      }
      else
      {
        localXmlsStackElement = (XmlsStackElement)this.levels.get(i);
        if ((localXmlsStackElement.rendered) && (localXmlsStackElement.level + 1 == this.currentLevel)) {
          j = 1;
        }
      }
      if (j != 0)
      {
        paramCollection.addAll(this.cur.nodes);
        this.cur.rendered = true;
        return;
      }
      HashMap localHashMap = new HashMap();
      while (i >= 0)
      {
        localXmlsStackElement = (XmlsStackElement)this.levels.get(i);
        Iterator localIterator = localXmlsStackElement.nodes.iterator();
        while (localIterator.hasNext())
        {
          Attr localAttr = (Attr)localIterator.next();
          if (!localHashMap.containsKey(localAttr.getName())) {
            localHashMap.put(localAttr.getName(), localAttr);
          }
        }
        i--;
      }
      this.cur.rendered = true;
      paramCollection.addAll(localHashMap.values());
    }
    
    static class XmlsStackElement
    {
      int level;
      boolean rendered = false;
      List<Attr> nodes = new ArrayList();
      
      XmlsStackElement() {}
    }
  }
}
