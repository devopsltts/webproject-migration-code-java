package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEMessage
{
  private static final Logger LOGGER = Logger.getLogger(MIMEMessage.class.getName());
  MIMEConfig config;
  private final InputStream in;
  private final List<MIMEPart> partsList;
  private final Map<String, MIMEPart> partsMap;
  private final Iterator<MIMEEvent> it;
  private boolean parsed;
  private MIMEPart currentPart;
  private int currentIndex;
  
  public MIMEMessage(InputStream paramInputStream, String paramString)
  {
    this(paramInputStream, paramString, new MIMEConfig());
  }
  
  public MIMEMessage(InputStream paramInputStream, String paramString, MIMEConfig paramMIMEConfig)
  {
    this.in = paramInputStream;
    this.config = paramMIMEConfig;
    MIMEParser localMIMEParser = new MIMEParser(paramInputStream, paramString, paramMIMEConfig);
    this.it = localMIMEParser.iterator();
    this.partsList = new ArrayList();
    this.partsMap = new HashMap();
    if (paramMIMEConfig.isParseEagerly()) {
      parseAll();
    }
  }
  
  public List<MIMEPart> getAttachments()
  {
    if (!this.parsed) {
      parseAll();
    }
    return this.partsList;
  }
  
  public MIMEPart getPart(int paramInt)
  {
    LOGGER.log(Level.FINE, "index={0}", Integer.valueOf(paramInt));
    MIMEPart localMIMEPart = paramInt < this.partsList.size() ? (MIMEPart)this.partsList.get(paramInt) : null;
    if ((this.parsed) && (localMIMEPart == null)) {
      throw new MIMEParsingException("There is no " + paramInt + " attachment part ");
    }
    if (localMIMEPart == null)
    {
      localMIMEPart = new MIMEPart(this);
      this.partsList.add(paramInt, localMIMEPart);
    }
    LOGGER.log(Level.FINE, "Got attachment at index={0} attachment={1}", new Object[] { Integer.valueOf(paramInt), localMIMEPart });
    return localMIMEPart;
  }
  
  public MIMEPart getPart(String paramString)
  {
    LOGGER.log(Level.FINE, "Content-ID={0}", paramString);
    MIMEPart localMIMEPart = getDecodedCidPart(paramString);
    if ((this.parsed) && (localMIMEPart == null)) {
      throw new MIMEParsingException("There is no attachment part with Content-ID = " + paramString);
    }
    if (localMIMEPart == null)
    {
      localMIMEPart = new MIMEPart(this, paramString);
      this.partsMap.put(paramString, localMIMEPart);
    }
    LOGGER.log(Level.FINE, "Got attachment for Content-ID={0} attachment={1}", new Object[] { paramString, localMIMEPart });
    return localMIMEPart;
  }
  
  private MIMEPart getDecodedCidPart(String paramString)
  {
    MIMEPart localMIMEPart = (MIMEPart)this.partsMap.get(paramString);
    if ((localMIMEPart == null) && (paramString.indexOf('%') != -1)) {
      try
      {
        String str = URLDecoder.decode(paramString, "utf-8");
        localMIMEPart = (MIMEPart)this.partsMap.get(str);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    }
    return localMIMEPart;
  }
  
  public final void parseAll()
  {
    while (makeProgress()) {}
  }
  
  public synchronized boolean makeProgress()
  {
    if (!this.it.hasNext()) {
      return false;
    }
    MIMEEvent localMIMEEvent = (MIMEEvent)this.it.next();
    switch (1.$SwitchMap$com$sun$xml$internal$org$jvnet$mimepull$MIMEEvent$EVENT_TYPE[localMIMEEvent.getEventType().ordinal()])
    {
    case 1: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_MESSAGE);
      break;
    case 2: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_PART);
      break;
    case 3: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.HEADERS);
      MIMEEvent.Headers localHeaders = (MIMEEvent.Headers)localMIMEEvent;
      InternetHeaders localInternetHeaders = localHeaders.getHeaders();
      List localList = localInternetHeaders.getHeader("content-id");
      String str = this.currentIndex + "";
      if ((str.length() > 2) && (str.charAt(0) == '<')) {
        str = str.substring(1, str.length() - 1);
      }
      Object localObject = this.currentIndex < this.partsList.size() ? (MIMEPart)this.partsList.get(this.currentIndex) : null;
      MIMEPart localMIMEPart = getDecodedCidPart(str);
      if ((localObject == null) && (localMIMEPart == null))
      {
        this.currentPart = getPart(str);
        this.partsList.add(this.currentIndex, this.currentPart);
      }
      else if (localObject == null)
      {
        this.currentPart = localMIMEPart;
        this.partsList.add(this.currentIndex, localMIMEPart);
      }
      else if (localMIMEPart == null)
      {
        this.currentPart = localObject;
        this.currentPart.setContentId(str);
        this.partsMap.put(str, this.currentPart);
      }
      else if (localObject != localMIMEPart)
      {
        throw new MIMEParsingException("Created two different attachments using Content-ID and index");
      }
      this.currentPart.setHeaders(localInternetHeaders);
      break;
    case 4: 
      LOGGER.log(Level.FINER, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.CONTENT);
      MIMEEvent.Content localContent = (MIMEEvent.Content)localMIMEEvent;
      ByteBuffer localByteBuffer = localContent.getData();
      this.currentPart.addBody(localByteBuffer);
      break;
    case 5: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_PART);
      this.currentPart.doneParsing();
      this.currentIndex += 1;
      break;
    case 6: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_MESSAGE);
      this.parsed = true;
      try
      {
        this.in.close();
      }
      catch (IOException localIOException)
      {
        throw new MIMEParsingException(localIOException);
      }
    default: 
      throw new MIMEParsingException("Unknown Parser state = " + localMIMEEvent.getEventType());
    }
    return true;
  }
}
