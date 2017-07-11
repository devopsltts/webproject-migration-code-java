package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.internal.messaging.saaj.util.ParserPool;
import com.sun.xml.internal.messaging.saaj.util.RejectDoctypeSaxFilter;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class EnvelopeFactory
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  private static ContextClassloaderLocal<ParserPool> parserPool = new ContextClassloaderLocal()
  {
    protected ParserPool initialValue()
      throws Exception
    {
      return new ParserPool(5);
    }
  };
  
  public EnvelopeFactory() {}
  
  public static Envelope createEnvelope(Source paramSource, SOAPPartImpl paramSOAPPartImpl)
    throws SOAPException
  {
    SAXParser localSAXParser = null;
    Object localObject1;
    Object localObject2;
    if ((paramSource instanceof StreamSource))
    {
      if ((paramSource instanceof JAXMStreamSource)) {
        try
        {
          if (!SOAPPartImpl.lazyContentLength) {
            ((JAXMStreamSource)paramSource).reset();
          }
        }
        catch (IOException localIOException)
        {
          log.severe("SAAJ0515.source.reset.exception");
          throw new SOAPExceptionImpl(localIOException);
        }
      }
      try
      {
        localSAXParser = ((ParserPool)parserPool.get()).get();
      }
      catch (Exception localException1)
      {
        log.severe("SAAJ0601.util.newSAXParser.exception");
        throw new SOAPExceptionImpl("Couldn't get a SAX parser while constructing a envelope", localException1);
      }
      localObject1 = SAXSource.sourceToInputSource(paramSource);
      if ((((InputSource)localObject1).getEncoding() == null) && (paramSOAPPartImpl.getSourceCharsetEncoding() != null)) {
        ((InputSource)localObject1).setEncoding(paramSOAPPartImpl.getSourceCharsetEncoding());
      }
      try
      {
        localObject2 = new RejectDoctypeSaxFilter(localSAXParser);
      }
      catch (Exception localException3)
      {
        log.severe("SAAJ0510.soap.cannot.create.envelope");
        throw new SOAPExceptionImpl("Unable to create envelope from given source: ", localException3);
      }
      paramSource = new SAXSource((XMLReader)localObject2, (InputSource)localObject1);
    }
    try
    {
      localObject1 = EfficientStreamingTransformer.newTransformer();
      localObject2 = new DOMResult(paramSOAPPartImpl);
      ((Transformer)localObject1).transform(paramSource, (Result)localObject2);
      Envelope localEnvelope1 = (Envelope)paramSOAPPartImpl.getEnvelope();
      Envelope localEnvelope2 = localEnvelope1;
      return localEnvelope2;
    }
    catch (Exception localException2)
    {
      if ((localException2 instanceof SOAPVersionMismatchException)) {
        throw ((SOAPVersionMismatchException)localException2);
      }
      log.severe("SAAJ0511.soap.cannot.create.envelope");
      throw new SOAPExceptionImpl("Unable to create envelope from given source: ", localException2);
    }
    finally
    {
      if (localSAXParser != null) {
        ((ParserPool)parserPool.get()).returnParser(localSAXParser);
      }
    }
  }
}
