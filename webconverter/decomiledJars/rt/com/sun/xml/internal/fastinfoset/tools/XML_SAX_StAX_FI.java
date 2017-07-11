package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_StAX_FI
  extends TransformInputOutput
{
  public XML_SAX_StAX_FI() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString)
    throws Exception
  {
    StAXDocumentSerializer localStAXDocumentSerializer = new StAXDocumentSerializer();
    localStAXDocumentSerializer.setOutputStream(paramOutputStream);
    SAX2StAXWriter localSAX2StAXWriter = new SAX2StAXWriter(localStAXDocumentSerializer);
    SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
    localSAXParserFactory.setNamespaceAware(true);
    SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
    XMLReader localXMLReader = localSAXParser.getXMLReader();
    localXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", localSAX2StAXWriter);
    localXMLReader.setContentHandler(localSAX2StAXWriter);
    if (paramString != null) {
      localXMLReader.setEntityResolver(createRelativePathResolver(paramString));
    }
    localXMLReader.parse(new InputSource(paramInputStream));
    paramInputStream.close();
    paramOutputStream.close();
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    parse(paramInputStream, paramOutputStream, null);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    XML_SAX_StAX_FI localXML_SAX_StAX_FI = new XML_SAX_StAX_FI();
    localXML_SAX_StAX_FI.parse(paramArrayOfString);
  }
}
