package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXSource
  implements Source
{
  public static final String FEATURE = "http://javax.xml.transform.sax.SAXSource/feature";
  private XMLReader reader;
  private InputSource inputSource;
  
  public SAXSource() {}
  
  public SAXSource(XMLReader paramXMLReader, InputSource paramInputSource)
  {
    this.reader = paramXMLReader;
    this.inputSource = paramInputSource;
  }
  
  public SAXSource(InputSource paramInputSource)
  {
    this.inputSource = paramInputSource;
  }
  
  public void setXMLReader(XMLReader paramXMLReader)
  {
    this.reader = paramXMLReader;
  }
  
  public XMLReader getXMLReader()
  {
    return this.reader;
  }
  
  public void setInputSource(InputSource paramInputSource)
  {
    this.inputSource = paramInputSource;
  }
  
  public InputSource getInputSource()
  {
    return this.inputSource;
  }
  
  public void setSystemId(String paramString)
  {
    if (null == this.inputSource) {
      this.inputSource = new InputSource(paramString);
    } else {
      this.inputSource.setSystemId(paramString);
    }
  }
  
  public String getSystemId()
  {
    if (this.inputSource == null) {
      return null;
    }
    return this.inputSource.getSystemId();
  }
  
  public static InputSource sourceToInputSource(Source paramSource)
  {
    if ((paramSource instanceof SAXSource)) {
      return ((SAXSource)paramSource).getInputSource();
    }
    if ((paramSource instanceof StreamSource))
    {
      StreamSource localStreamSource = (StreamSource)paramSource;
      InputSource localInputSource = new InputSource(localStreamSource.getSystemId());
      localInputSource.setByteStream(localStreamSource.getInputStream());
      localInputSource.setCharacterStream(localStreamSource.getReader());
      localInputSource.setPublicId(localStreamSource.getPublicId());
      return localInputSource;
    }
    return null;
  }
}
