package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class FI_StAX_SAX_Or_XML_SAX_SAXEvent
  extends TransformInputOutput
{
  public FI_StAX_SAX_Or_XML_SAX_SAXEvent() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    if (!paramInputStream.markSupported()) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (bool)
    {
      localObject1 = new StAXDocumentParser();
      ((StAXDocumentParser)localObject1).setInputStream(paramInputStream);
      localObject2 = new SAXEventSerializer(paramOutputStream);
      localObject3 = new StAX2SAXReader((XMLStreamReader)localObject1, (ContentHandler)localObject2);
      ((StAX2SAXReader)localObject3).setLexicalHandler((LexicalHandler)localObject2);
      ((StAX2SAXReader)localObject3).adapt();
    }
    else
    {
      localObject1 = SAXParserFactory.newInstance();
      ((SAXParserFactory)localObject1).setNamespaceAware(true);
      localObject2 = ((SAXParserFactory)localObject1).newSAXParser();
      localObject3 = new SAXEventSerializer(paramOutputStream);
      ((SAXParser)localObject2).setProperty("http://xml.org/sax/properties/lexical-handler", localObject3);
      ((SAXParser)localObject2).parse(paramInputStream, (DefaultHandler)localObject3);
    }
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    FI_StAX_SAX_Or_XML_SAX_SAXEvent localFI_StAX_SAX_Or_XML_SAX_SAXEvent = new FI_StAX_SAX_Or_XML_SAX_SAXEvent();
    localFI_StAX_SAX_Or_XML_SAX_SAXEvent.parse(paramArrayOfString);
  }
}
