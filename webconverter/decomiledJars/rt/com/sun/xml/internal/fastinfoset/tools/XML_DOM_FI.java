package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.dom.DOMDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class XML_DOM_FI
  extends TransformInputOutput
{
  public XML_DOM_FI() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
    if (paramString != null) {
      localDocumentBuilder.setEntityResolver(createRelativePathResolver(paramString));
    }
    Document localDocument = localDocumentBuilder.parse(paramInputStream);
    DOMDocumentSerializer localDOMDocumentSerializer = new DOMDocumentSerializer();
    localDOMDocumentSerializer.setOutputStream(paramOutputStream);
    localDOMDocumentSerializer.serialize(localDocument);
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    XML_DOM_FI localXML_DOM_FI = new XML_DOM_FI();
    localXML_DOM_FI.parse(paramArrayOfString);
  }
}
