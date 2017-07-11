package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil
{
  private static DocumentBuilder db;
  
  public DOMUtil() {}
  
  public static Document createDom()
  {
    synchronized (DOMUtil.class)
    {
      if (db == null) {
        try
        {
          DocumentBuilderFactory localDocumentBuilderFactory = XmlUtil.newDocumentBuilderFactory();
          localDocumentBuilderFactory.setNamespaceAware(true);
          db = localDocumentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
          throw new FactoryConfigurationError(localParserConfigurationException);
        }
      }
      return db.newDocument();
    }
  }
  
  public static void serializeNode(Element paramElement, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    writeTagWithAttributes(paramElement, paramXMLStreamWriter);
    if (paramElement.hasChildNodes())
    {
      NodeList localNodeList = paramElement.getChildNodes();
      for (int i = 0; i < localNodeList.getLength(); i++)
      {
        Node localNode = localNodeList.item(i);
        switch (localNode.getNodeType())
        {
        case 7: 
          paramXMLStreamWriter.writeProcessingInstruction(localNode.getNodeValue());
          break;
        case 10: 
          break;
        case 4: 
          paramXMLStreamWriter.writeCData(localNode.getNodeValue());
          break;
        case 8: 
          paramXMLStreamWriter.writeComment(localNode.getNodeValue());
          break;
        case 3: 
          paramXMLStreamWriter.writeCharacters(localNode.getNodeValue());
          break;
        case 1: 
          serializeNode((Element)localNode, paramXMLStreamWriter);
        }
      }
    }
    paramXMLStreamWriter.writeEndElement();
  }
  
  public static void writeTagWithAttributes(Element paramElement, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    String str1 = fixNull(paramElement.getPrefix());
    String str2 = fixNull(paramElement.getNamespaceURI());
    String str3 = paramElement.getLocalName() == null ? paramElement.getNodeName() : paramElement.getLocalName();
    boolean bool1 = isPrefixDeclared(paramXMLStreamWriter, str2, str1);
    paramXMLStreamWriter.writeStartElement(str1, str3, str2);
    NamedNodeMap localNamedNodeMap;
    int i;
    int j;
    Node localNode;
    String str4;
    String str5;
    if (paramElement.hasAttributes())
    {
      localNamedNodeMap = paramElement.getAttributes();
      i = localNamedNodeMap.getLength();
      for (j = 0; j < i; j++)
      {
        localNode = localNamedNodeMap.item(j);
        str4 = fixNull(localNode.getNamespaceURI());
        if (str4.equals("http://www.w3.org/2000/xmlns/"))
        {
          str5 = localNode.getLocalName().equals("xmlns") ? "" : localNode.getLocalName();
          if ((str5.equals(str1)) && (localNode.getNodeValue().equals(str2))) {
            bool1 = true;
          }
          if (str5.equals(""))
          {
            paramXMLStreamWriter.writeDefaultNamespace(localNode.getNodeValue());
          }
          else
          {
            paramXMLStreamWriter.setPrefix(localNode.getLocalName(), localNode.getNodeValue());
            paramXMLStreamWriter.writeNamespace(localNode.getLocalName(), localNode.getNodeValue());
          }
        }
      }
    }
    if (!bool1) {
      paramXMLStreamWriter.writeNamespace(str1, str2);
    }
    if (paramElement.hasAttributes())
    {
      localNamedNodeMap = paramElement.getAttributes();
      i = localNamedNodeMap.getLength();
      for (j = 0; j < i; j++)
      {
        localNode = localNamedNodeMap.item(j);
        str4 = fixNull(localNode.getPrefix());
        str5 = fixNull(localNode.getNamespaceURI());
        if (!str5.equals("http://www.w3.org/2000/xmlns/"))
        {
          String str6 = localNode.getLocalName();
          if (str6 == null) {
            str6 = localNode.getNodeName();
          }
          boolean bool2 = isPrefixDeclared(paramXMLStreamWriter, str5, str4);
          if ((!str4.equals("")) && (!bool2))
          {
            paramXMLStreamWriter.setPrefix(localNode.getLocalName(), localNode.getNodeValue());
            paramXMLStreamWriter.writeNamespace(str4, str5);
          }
          paramXMLStreamWriter.writeAttribute(str4, str5, str6, localNode.getNodeValue());
        }
      }
    }
  }
  
  private static boolean isPrefixDeclared(XMLStreamWriter paramXMLStreamWriter, String paramString1, String paramString2)
  {
    boolean bool = false;
    NamespaceContext localNamespaceContext = paramXMLStreamWriter.getNamespaceContext();
    Iterator localIterator = localNamespaceContext.getPrefixes(paramString1);
    while (localIterator.hasNext()) {
      if (paramString2.equals(localIterator.next())) {
        bool = true;
      }
    }
    return bool;
  }
  
  public static Element getFirstChild(Element paramElement, String paramString1, String paramString2)
  {
    for (Node localNode = paramElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if (localNode.getNodeType() == 1)
      {
        Element localElement = (Element)localNode;
        if ((localElement.getLocalName().equals(paramString2)) && (localElement.getNamespaceURI().equals(paramString1))) {
          return localElement;
        }
      }
    }
    return null;
  }
  
  @NotNull
  private static String fixNull(@Nullable String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  @Nullable
  public static Element getFirstElementChild(Node paramNode)
  {
    for (Node localNode = paramNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if (localNode.getNodeType() == 1) {
        return (Element)localNode;
      }
    }
    return null;
  }
  
  @NotNull
  public static List<Element> getChildElements(Node paramNode)
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = paramNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if (localNode.getNodeType() == 1) {
        localArrayList.add((Element)localNode);
      }
    }
    return localArrayList;
  }
}
