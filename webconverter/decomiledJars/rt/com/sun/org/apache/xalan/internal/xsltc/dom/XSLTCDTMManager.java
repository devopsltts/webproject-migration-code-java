package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXEvent2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXStream2SAX;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.PrintStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XSLTCDTMManager
  extends DTMManagerDefault
{
  private static final boolean DUMPTREE = false;
  private static final boolean DEBUG = false;
  
  public XSLTCDTMManager() {}
  
  public static XSLTCDTMManager newInstance()
  {
    return new XSLTCDTMManager();
  }
  
  public static XSLTCDTMManager createNewDTMManagerInstance()
  {
    return newInstance();
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3)
  {
    return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, false, 0, true, false);
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, false, 0, paramBoolean4, false);
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, false, 0, paramBoolean4, paramBoolean5);
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, boolean paramBoolean5)
  {
    return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, paramBoolean4, paramInt, paramBoolean5, false);
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, boolean paramBoolean5, boolean paramBoolean6)
  {
    int i = getFirstFreeDTMID();
    int j = i << 16;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if ((null != paramSource) && ((paramSource instanceof StAXSource)))
    {
      localObject1 = (StAXSource)paramSource;
      localObject2 = null;
      localObject3 = null;
      if (((StAXSource)localObject1).getXMLEventReader() != null)
      {
        localObject4 = ((StAXSource)localObject1).getXMLEventReader();
        localObject2 = new StAXEvent2SAX((XMLEventReader)localObject4);
      }
      else if (((StAXSource)localObject1).getXMLStreamReader() != null)
      {
        localObject4 = ((StAXSource)localObject1).getXMLStreamReader();
        localObject3 = new StAXStream2SAX((XMLStreamReader)localObject4);
      }
      if (paramInt <= 0) {
        localObject4 = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, 512, paramBoolean5, paramBoolean6);
      } else {
        localObject4 = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, paramInt, paramBoolean5, paramBoolean6);
      }
      ((SAXImpl)localObject4).setDocumentURI(paramSource.getSystemId());
      addDTM((DTM)localObject4, i, 0);
      try
      {
        if (localObject2 != null)
        {
          ((StAXEvent2SAX)localObject2).setContentHandler((ContentHandler)localObject4);
          ((StAXEvent2SAX)localObject2).parse();
        }
        else if (localObject3 != null)
        {
          ((StAXStream2SAX)localObject3).setContentHandler((ContentHandler)localObject4);
          ((StAXStream2SAX)localObject3).parse();
        }
      }
      catch (RuntimeException localRuntimeException1)
      {
        throw localRuntimeException1;
      }
      catch (Exception localException1)
      {
        throw new WrappedRuntimeException(localException1);
      }
      return localObject4;
    }
    if ((null != paramSource) && ((paramSource instanceof DOMSource)))
    {
      localObject1 = (DOMSource)paramSource;
      localObject2 = ((DOMSource)localObject1).getNode();
      localObject3 = new DOM2SAX((Node)localObject2);
      if (paramInt <= 0) {
        localObject4 = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, 512, paramBoolean5, paramBoolean6);
      } else {
        localObject4 = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, paramInt, paramBoolean5, paramBoolean6);
      }
      ((SAXImpl)localObject4).setDocumentURI(paramSource.getSystemId());
      addDTM((DTM)localObject4, i, 0);
      ((DOM2SAX)localObject3).setContentHandler((ContentHandler)localObject4);
      try
      {
        ((DOM2SAX)localObject3).parse();
      }
      catch (RuntimeException localRuntimeException2)
      {
        throw localRuntimeException2;
      }
      catch (Exception localException2)
      {
        throw new WrappedRuntimeException(localException2);
      }
      return localObject4;
    }
    int k = null != paramSource ? paramSource instanceof SAXSource : 1;
    int m = null != paramSource ? paramSource instanceof StreamSource : 0;
    if ((k != 0) || (m != 0))
    {
      Object localObject5;
      if (null == paramSource)
      {
        localObject4 = null;
        localObject3 = null;
        paramBoolean4 = false;
      }
      else
      {
        localObject3 = getXMLReader(paramSource);
        localObject4 = SAXSource.sourceToInputSource(paramSource);
        localObject5 = ((InputSource)localObject4).getSystemId();
        if (null != localObject5)
        {
          try
          {
            localObject5 = SystemIDResolver.getAbsoluteURI((String)localObject5);
          }
          catch (Exception localException3)
          {
            System.err.println("Can not absolutize URL: " + (String)localObject5);
          }
          ((InputSource)localObject4).setSystemId((String)localObject5);
        }
      }
      if (paramInt <= 0) {
        localObject5 = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, 512, paramBoolean5, paramBoolean6);
      } else {
        localObject5 = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, paramInt, paramBoolean5, paramBoolean6);
      }
      addDTM((DTM)localObject5, i, 0);
      if (null == localObject3) {
        return localObject5;
      }
      ((XMLReader)localObject3).setContentHandler(((SAXImpl)localObject5).getBuilder());
      if ((!paramBoolean4) || (null == ((XMLReader)localObject3).getDTDHandler())) {
        ((XMLReader)localObject3).setDTDHandler((DTDHandler)localObject5);
      }
      if ((!paramBoolean4) || (null == ((XMLReader)localObject3).getErrorHandler())) {
        ((XMLReader)localObject3).setErrorHandler((ErrorHandler)localObject5);
      }
      try
      {
        ((XMLReader)localObject3).setProperty("http://xml.org/sax/properties/lexical-handler", localObject5);
      }
      catch (SAXNotRecognizedException localSAXNotRecognizedException) {}catch (SAXNotSupportedException localSAXNotSupportedException) {}
      try
      {
        ((XMLReader)localObject3).parse((InputSource)localObject4);
      }
      catch (RuntimeException localRuntimeException3)
      {
        throw localRuntimeException3;
      }
      catch (Exception localException4)
      {
        throw new WrappedRuntimeException(localException4);
      }
      finally
      {
        if (!paramBoolean4) {
          releaseXMLReader((XMLReader)localObject3);
        }
      }
      return localObject5;
    }
    throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[] { paramSource }));
  }
}
