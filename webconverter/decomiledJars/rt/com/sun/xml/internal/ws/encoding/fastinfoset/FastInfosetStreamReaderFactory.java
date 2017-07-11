package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;

public final class FastInfosetStreamReaderFactory
  extends XMLStreamReaderFactory
{
  private static final FastInfosetStreamReaderFactory factory = new FastInfosetStreamReaderFactory();
  private ThreadLocal<StAXDocumentParser> pool = new ThreadLocal();
  
  public FastInfosetStreamReaderFactory() {}
  
  public static FastInfosetStreamReaderFactory getInstance()
  {
    return factory;
  }
  
  public XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean)
  {
    StAXDocumentParser localStAXDocumentParser = fetch();
    if (localStAXDocumentParser == null) {
      return FastInfosetCodec.createNewStreamReaderRecyclable(paramInputStream, false);
    }
    localStAXDocumentParser.setInputStream(paramInputStream);
    return localStAXDocumentParser;
  }
  
  public XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  private StAXDocumentParser fetch()
  {
    StAXDocumentParser localStAXDocumentParser = (StAXDocumentParser)this.pool.get();
    this.pool.set(null);
    return localStAXDocumentParser;
  }
  
  public void doRecycle(XMLStreamReader paramXMLStreamReader)
  {
    if ((paramXMLStreamReader instanceof StAXDocumentParser)) {
      this.pool.set((StAXDocumentParser)paramXMLStreamReader);
    }
  }
}
