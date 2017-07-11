package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class FI_SAX_XML
  extends TransformInputOutput
{
  public FI_SAX_XML() {}
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Exception
  {
    Transformer localTransformer = TransformerFactory.newInstance().newTransformer();
    localTransformer.transform(new FastInfosetSource(paramInputStream), new StreamResult(paramOutputStream));
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    FI_SAX_XML localFI_SAX_XML = new FI_SAX_XML();
    localFI_SAX_XML.parse(paramArrayOfString);
  }
}
