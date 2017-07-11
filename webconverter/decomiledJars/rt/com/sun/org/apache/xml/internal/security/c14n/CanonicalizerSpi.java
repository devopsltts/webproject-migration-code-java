package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class CanonicalizerSpi
{
  protected boolean reset = false;
  
  public CanonicalizerSpi() {}
  
  public byte[] engineCanonicalize(byte[] paramArrayOfByte)
    throws ParserConfigurationException, IOException, SAXException, CanonicalizationException
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    InputSource localInputSource = new InputSource(localByteArrayInputStream);
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
    localDocumentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
    Document localDocument = localDocumentBuilder.parse(localInputSource);
    return engineCanonicalizeSubTree(localDocument);
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(NodeList paramNodeList)
    throws CanonicalizationException
  {
    return engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(paramNodeList));
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(NodeList paramNodeList, String paramString)
    throws CanonicalizationException
  {
    return engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(paramNodeList), paramString);
  }
  
  public abstract String engineGetURI();
  
  public abstract boolean engineGetIncludeComments();
  
  public abstract byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet)
    throws CanonicalizationException;
  
  public abstract byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString)
    throws CanonicalizationException;
  
  public abstract byte[] engineCanonicalizeSubTree(Node paramNode)
    throws CanonicalizationException;
  
  public abstract byte[] engineCanonicalizeSubTree(Node paramNode, String paramString)
    throws CanonicalizationException;
  
  public abstract void setWriter(OutputStream paramOutputStream);
}
