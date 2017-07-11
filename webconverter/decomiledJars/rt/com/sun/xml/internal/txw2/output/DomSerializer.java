package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomSerializer
  implements XmlSerializer
{
  private final SaxSerializer serializer;
  
  public DomSerializer(Node paramNode)
  {
    Dom2SaxAdapter localDom2SaxAdapter = new Dom2SaxAdapter(paramNode);
    this.serializer = new SaxSerializer(localDom2SaxAdapter, localDom2SaxAdapter, false);
  }
  
  public DomSerializer(DOMResult paramDOMResult)
  {
    Node localNode = paramDOMResult.getNode();
    if (localNode == null) {
      try
      {
        DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        localDocumentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
        Document localDocument = localDocumentBuilder.newDocument();
        paramDOMResult.setNode(localDocument);
        this.serializer = new SaxSerializer(new Dom2SaxAdapter(localDocument), null, false);
      }
      catch (ParserConfigurationException localParserConfigurationException)
      {
        throw new TxwException(localParserConfigurationException);
      }
    } else {
      this.serializer = new SaxSerializer(new Dom2SaxAdapter(localNode), null, false);
    }
  }
  
  public void startDocument()
  {
    this.serializer.startDocument();
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3)
  {
    this.serializer.beginStartTag(paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
  {
    this.serializer.writeAttribute(paramString1, paramString2, paramString3, paramStringBuilder);
  }
  
  public void writeXmlns(String paramString1, String paramString2)
  {
    this.serializer.writeXmlns(paramString1, paramString2);
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3)
  {
    this.serializer.endStartTag(paramString1, paramString2, paramString3);
  }
  
  public void endTag()
  {
    this.serializer.endTag();
  }
  
  public void text(StringBuilder paramStringBuilder)
  {
    this.serializer.text(paramStringBuilder);
  }
  
  public void cdata(StringBuilder paramStringBuilder)
  {
    this.serializer.cdata(paramStringBuilder);
  }
  
  public void comment(StringBuilder paramStringBuilder)
  {
    this.serializer.comment(paramStringBuilder);
  }
  
  public void endDocument()
  {
    this.serializer.endDocument();
  }
  
  public void flush() {}
}
