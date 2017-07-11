package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public final class Util
{
  public Util() {}
  
  public static String baseName(String paramString)
  {
    return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.baseName(paramString);
  }
  
  public static String noExtName(String paramString)
  {
    return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.noExtName(paramString);
  }
  
  public static String toJavaName(String paramString)
  {
    return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.toJavaName(paramString);
  }
  
  public static InputSource getInputSource(XSLTC paramXSLTC, Source paramSource)
    throws TransformerConfigurationException
  {
    InputSource localInputSource = null;
    String str = paramSource.getSystemId();
    try
    {
      Object localObject1;
      if ((paramSource instanceof SAXSource))
      {
        localObject1 = (SAXSource)paramSource;
        localInputSource = ((SAXSource)localObject1).getInputSource();
        try
        {
          XMLReader localXMLReader = ((SAXSource)localObject1).getXMLReader();
          if (localXMLReader == null) {
            try
            {
              localXMLReader = XMLReaderFactory.createXMLReader();
              try
              {
                localXMLReader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", paramXSLTC.isSecureProcessing());
              }
              catch (SAXNotRecognizedException localSAXNotRecognizedException2)
              {
                System.err.println("Warning:  " + localXMLReader.getClass().getName() + ": " + localSAXNotRecognizedException2.getMessage());
              }
            }
            catch (Exception localException)
            {
              try
              {
                SAXParserFactory localSAXParserFactory = FactoryImpl.getSAXFactory(paramXSLTC.useServicesMechnism());
                localSAXParserFactory.setNamespaceAware(true);
                if (paramXSLTC.isSecureProcessing()) {
                  try
                  {
                    localSAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                  }
                  catch (SAXException localSAXException3) {}
                }
                localXMLReader = localSAXParserFactory.newSAXParser().getXMLReader();
              }
              catch (ParserConfigurationException localParserConfigurationException)
              {
                throw new TransformerConfigurationException("ParserConfigurationException", localParserConfigurationException);
              }
            }
          }
          localXMLReader.setFeature("http://xml.org/sax/features/namespaces", true);
          localXMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
          try
          {
            localXMLReader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", paramXSLTC.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"));
          }
          catch (SAXNotRecognizedException localSAXNotRecognizedException3)
          {
            System.err.println("Warning:  " + localXMLReader.getClass().getName() + ": " + localSAXNotRecognizedException3.getMessage());
          }
          try
          {
            XMLSecurityManager localXMLSecurityManager = (XMLSecurityManager)paramXSLTC.getProperty("http://apache.org/xml/properties/security-manager");
            if (localXMLSecurityManager != null)
            {
              for (XMLSecurityManager.Limit localLimit : XMLSecurityManager.Limit.values()) {
                localXMLReader.setProperty(localLimit.apiProperty(), localXMLSecurityManager.getLimitValueAsString(localLimit));
              }
              if (localXMLSecurityManager.printEntityCountInfo()) {
                localXMLReader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
              }
            }
          }
          catch (SAXException localSAXException2)
          {
            System.err.println("Warning:  " + localXMLReader.getClass().getName() + ": " + localSAXException2.getMessage());
          }
          paramXSLTC.setXMLReader(localXMLReader);
        }
        catch (SAXNotRecognizedException localSAXNotRecognizedException1)
        {
          throw new TransformerConfigurationException("SAXNotRecognizedException ", localSAXNotRecognizedException1);
        }
        catch (SAXNotSupportedException localSAXNotSupportedException)
        {
          throw new TransformerConfigurationException("SAXNotSupportedException ", localSAXNotSupportedException);
        }
        catch (SAXException localSAXException1)
        {
          throw new TransformerConfigurationException("SAXException ", localSAXException1);
        }
      }
      else
      {
        Object localObject3;
        if ((paramSource instanceof DOMSource))
        {
          localObject1 = (DOMSource)paramSource;
          localObject2 = (Document)((DOMSource)localObject1).getNode();
          localObject3 = new DOM2SAX((Node)localObject2);
          paramXSLTC.setXMLReader((XMLReader)localObject3);
          localInputSource = SAXSource.sourceToInputSource(paramSource);
          if (localInputSource == null) {
            localInputSource = new InputSource(((DOMSource)localObject1).getSystemId());
          }
        }
        else if ((paramSource instanceof StAXSource))
        {
          localObject1 = (StAXSource)paramSource;
          localObject2 = null;
          localObject3 = null;
          if (((StAXSource)localObject1).getXMLEventReader() != null)
          {
            ??? = ((StAXSource)localObject1).getXMLEventReader();
            localObject2 = new StAXEvent2SAX((XMLEventReader)???);
            paramXSLTC.setXMLReader((XMLReader)localObject2);
          }
          else if (((StAXSource)localObject1).getXMLStreamReader() != null)
          {
            ??? = ((StAXSource)localObject1).getXMLStreamReader();
            localObject3 = new StAXStream2SAX((XMLStreamReader)???);
            paramXSLTC.setXMLReader((XMLReader)localObject3);
          }
          localInputSource = SAXSource.sourceToInputSource(paramSource);
          if (localInputSource == null) {
            localInputSource = new InputSource(((StAXSource)localObject1).getSystemId());
          }
        }
        else if ((paramSource instanceof StreamSource))
        {
          localObject1 = (StreamSource)paramSource;
          localObject2 = ((StreamSource)localObject1).getInputStream();
          localObject3 = ((StreamSource)localObject1).getReader();
          paramXSLTC.setXMLReader(null);
          if (localObject2 != null) {
            localInputSource = new InputSource((InputStream)localObject2);
          } else if (localObject3 != null) {
            localInputSource = new InputSource((Reader)localObject3);
          } else {
            localInputSource = new InputSource(str);
          }
        }
        else
        {
          localObject1 = new ErrorMsg("JAXP_UNKNOWN_SOURCE_ERR");
          throw new TransformerConfigurationException(((ErrorMsg)localObject1).toString());
        }
      }
      localInputSource.setSystemId(str);
    }
    catch (NullPointerException localNullPointerException)
    {
      localObject2 = new ErrorMsg("JAXP_NO_SOURCE_ERR", "TransformerFactory.newTemplates()");
      throw new TransformerConfigurationException(((ErrorMsg)localObject2).toString());
    }
    catch (SecurityException localSecurityException)
    {
      Object localObject2 = new ErrorMsg("FILE_ACCESS_ERR", str);
      throw new TransformerConfigurationException(((ErrorMsg)localObject2).toString());
    }
    return localInputSource;
  }
}
