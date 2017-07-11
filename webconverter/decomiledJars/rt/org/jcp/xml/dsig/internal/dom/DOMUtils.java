package org.jcp.xml.dsig.internal.dom;

import java.security.spec.AlgorithmParameterSpec;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtils
{
  private DOMUtils() {}
  
  public static Document getOwnerDocument(Node paramNode)
  {
    if (paramNode.getNodeType() == 9) {
      return (Document)paramNode;
    }
    return paramNode.getOwnerDocument();
  }
  
  public static Element createElement(Document paramDocument, String paramString1, String paramString2, String paramString3)
  {
    String str = paramString3 + ":" + paramString1;
    return paramDocument.createElementNS(paramString2, str);
  }
  
  public static void setAttribute(Element paramElement, String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return;
    }
    paramElement.setAttributeNS(null, paramString1, paramString2);
  }
  
  public static void setAttributeID(Element paramElement, String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return;
    }
    paramElement.setAttributeNS(null, paramString1, paramString2);
    paramElement.setIdAttributeNS(null, paramString1, true);
  }
  
  public static Element getFirstChildElement(Node paramNode)
  {
    for (Node localNode = paramNode.getFirstChild(); (localNode != null) && (localNode.getNodeType() != 1); localNode = localNode.getNextSibling()) {}
    return (Element)localNode;
  }
  
  public static Element getFirstChildElement(Node paramNode, String paramString)
    throws MarshalException
  {
    return verifyElement(getFirstChildElement(paramNode), paramString);
  }
  
  private static Element verifyElement(Element paramElement, String paramString)
    throws MarshalException
  {
    if (paramElement == null) {
      throw new MarshalException("Missing " + paramString + " element");
    }
    String str = paramElement.getLocalName();
    if (!str.equals(paramString)) {
      throw new MarshalException("Invalid element name: " + str + ", expected " + paramString);
    }
    return paramElement;
  }
  
  public static Element getLastChildElement(Node paramNode)
  {
    for (Node localNode = paramNode.getLastChild(); (localNode != null) && (localNode.getNodeType() != 1); localNode = localNode.getPreviousSibling()) {}
    return (Element)localNode;
  }
  
  public static Element getNextSiblingElement(Node paramNode)
  {
    for (Node localNode = paramNode.getNextSibling(); (localNode != null) && (localNode.getNodeType() != 1); localNode = localNode.getNextSibling()) {}
    return (Element)localNode;
  }
  
  public static Element getNextSiblingElement(Node paramNode, String paramString)
    throws MarshalException
  {
    return verifyElement(getNextSiblingElement(paramNode), paramString);
  }
  
  public static String getAttributeValue(Element paramElement, String paramString)
  {
    Attr localAttr = paramElement.getAttributeNodeNS(null, paramString);
    return localAttr == null ? null : localAttr.getValue();
  }
  
  public static Set<Node> nodeSet(NodeList paramNodeList)
  {
    return new NodeSet(paramNodeList);
  }
  
  public static String getNSPrefix(XMLCryptoContext paramXMLCryptoContext, String paramString)
  {
    if (paramXMLCryptoContext != null) {
      return paramXMLCryptoContext.getNamespacePrefix(paramString, paramXMLCryptoContext.getDefaultNamespacePrefix());
    }
    return null;
  }
  
  public static String getSignaturePrefix(XMLCryptoContext paramXMLCryptoContext)
  {
    return getNSPrefix(paramXMLCryptoContext, "http://www.w3.org/2000/09/xmldsig#");
  }
  
  public static void removeAllChildren(Node paramNode)
  {
    NodeList localNodeList = paramNode.getChildNodes();
    int i = 0;
    int j = localNodeList.getLength();
    while (i < j)
    {
      paramNode.removeChild(localNodeList.item(i));
      i++;
    }
  }
  
  public static boolean nodesEqual(Node paramNode1, Node paramNode2)
  {
    if (paramNode1 == paramNode2) {
      return true;
    }
    return paramNode1.getNodeType() == paramNode2.getNodeType();
  }
  
  public static void appendChild(Node paramNode1, Node paramNode2)
  {
    Document localDocument = getOwnerDocument(paramNode1);
    if (paramNode2.getOwnerDocument() != localDocument) {
      paramNode1.appendChild(localDocument.importNode(paramNode2, true));
    } else {
      paramNode1.appendChild(paramNode2);
    }
  }
  
  public static boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec1, AlgorithmParameterSpec paramAlgorithmParameterSpec2)
  {
    if (paramAlgorithmParameterSpec1 == paramAlgorithmParameterSpec2) {
      return true;
    }
    if (((paramAlgorithmParameterSpec1 instanceof XPathFilter2ParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof XPathFilter2ParameterSpec))) {
      return paramsEqual((XPathFilter2ParameterSpec)paramAlgorithmParameterSpec1, (XPathFilter2ParameterSpec)paramAlgorithmParameterSpec2);
    }
    if (((paramAlgorithmParameterSpec1 instanceof ExcC14NParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof ExcC14NParameterSpec))) {
      return paramsEqual((ExcC14NParameterSpec)paramAlgorithmParameterSpec1, (ExcC14NParameterSpec)paramAlgorithmParameterSpec2);
    }
    if (((paramAlgorithmParameterSpec1 instanceof XPathFilterParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof XPathFilterParameterSpec))) {
      return paramsEqual((XPathFilterParameterSpec)paramAlgorithmParameterSpec1, (XPathFilterParameterSpec)paramAlgorithmParameterSpec2);
    }
    if (((paramAlgorithmParameterSpec1 instanceof XSLTTransformParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof XSLTTransformParameterSpec))) {
      return paramsEqual((XSLTTransformParameterSpec)paramAlgorithmParameterSpec1, (XSLTTransformParameterSpec)paramAlgorithmParameterSpec2);
    }
    return false;
  }
  
  private static boolean paramsEqual(XPathFilter2ParameterSpec paramXPathFilter2ParameterSpec1, XPathFilter2ParameterSpec paramXPathFilter2ParameterSpec2)
  {
    List localList1 = paramXPathFilter2ParameterSpec1.getXPathList();
    List localList2 = paramXPathFilter2ParameterSpec2.getXPathList();
    int i = localList1.size();
    if (i != localList2.size()) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      XPathType localXPathType1 = (XPathType)localList1.get(j);
      XPathType localXPathType2 = (XPathType)localList2.get(j);
      if ((!localXPathType1.getExpression().equals(localXPathType2.getExpression())) || (!localXPathType1.getNamespaceMap().equals(localXPathType2.getNamespaceMap())) || (localXPathType1.getFilter() != localXPathType2.getFilter())) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean paramsEqual(ExcC14NParameterSpec paramExcC14NParameterSpec1, ExcC14NParameterSpec paramExcC14NParameterSpec2)
  {
    return paramExcC14NParameterSpec1.getPrefixList().equals(paramExcC14NParameterSpec2.getPrefixList());
  }
  
  private static boolean paramsEqual(XPathFilterParameterSpec paramXPathFilterParameterSpec1, XPathFilterParameterSpec paramXPathFilterParameterSpec2)
  {
    return (paramXPathFilterParameterSpec1.getXPath().equals(paramXPathFilterParameterSpec2.getXPath())) && (paramXPathFilterParameterSpec1.getNamespaceMap().equals(paramXPathFilterParameterSpec2.getNamespaceMap()));
  }
  
  private static boolean paramsEqual(XSLTTransformParameterSpec paramXSLTTransformParameterSpec1, XSLTTransformParameterSpec paramXSLTTransformParameterSpec2)
  {
    XMLStructure localXMLStructure1 = paramXSLTTransformParameterSpec2.getStylesheet();
    if (!(localXMLStructure1 instanceof DOMStructure)) {
      return false;
    }
    Node localNode1 = ((DOMStructure)localXMLStructure1).getNode();
    XMLStructure localXMLStructure2 = paramXSLTTransformParameterSpec1.getStylesheet();
    Node localNode2 = ((DOMStructure)localXMLStructure2).getNode();
    return nodesEqual(localNode2, localNode1);
  }
  
  static class NodeSet
    extends AbstractSet<Node>
  {
    private NodeList nl;
    
    public NodeSet(NodeList paramNodeList)
    {
      this.nl = paramNodeList;
    }
    
    public int size()
    {
      return this.nl.getLength();
    }
    
    public Iterator<Node> iterator()
    {
      new Iterator()
      {
        int index = 0;
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
        
        public Node next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return DOMUtils.NodeSet.this.nl.item(this.index++);
        }
        
        public boolean hasNext()
        {
          return this.index < DOMUtils.NodeSet.this.nl.getLength();
        }
      };
    }
  }
}
