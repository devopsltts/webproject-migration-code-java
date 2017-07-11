package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.EnvelopeStyle.Style;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MessageContextFactory
  extends com.oracle.webservices.internal.api.message.MessageContextFactory
{
  private WSFeatureList features;
  private Codec soapCodec;
  private Codec xmlCodec;
  private EnvelopeStyleFeature envelopeStyle;
  private EnvelopeStyle.Style singleSoapStyle;
  
  public MessageContextFactory(WebServiceFeature[] paramArrayOfWebServiceFeature)
  {
    this(new WebServiceFeatureList(paramArrayOfWebServiceFeature));
  }
  
  public MessageContextFactory(WSFeatureList paramWSFeatureList)
  {
    this.features = paramWSFeatureList;
    this.envelopeStyle = ((EnvelopeStyleFeature)this.features.get(EnvelopeStyleFeature.class));
    if (this.envelopeStyle == null)
    {
      this.envelopeStyle = new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP11 });
      this.features.mergeFeatures(new WebServiceFeature[] { this.envelopeStyle }, false);
    }
    for (EnvelopeStyle.Style localStyle : this.envelopeStyle.getStyles()) {
      if (localStyle.isXML())
      {
        if (this.xmlCodec == null) {
          this.xmlCodec = Codecs.createXMLCodec(this.features);
        }
      }
      else
      {
        if (this.soapCodec == null) {
          this.soapCodec = Codecs.createSOAPBindingCodec(this.features);
        }
        this.singleSoapStyle = localStyle;
      }
    }
  }
  
  protected com.oracle.webservices.internal.api.message.MessageContextFactory newFactory(WebServiceFeature... paramVarArgs)
  {
    return new MessageContextFactory(paramVarArgs);
  }
  
  public MessageContext createContext()
  {
    return packet(null);
  }
  
  public MessageContext createContext(SOAPMessage paramSOAPMessage)
  {
    throwIfIllegalMessageArgument(paramSOAPMessage);
    return packet(Messages.create(paramSOAPMessage));
  }
  
  public MessageContext createContext(Source paramSource, EnvelopeStyle.Style paramStyle)
  {
    throwIfIllegalMessageArgument(paramSource);
    return packet(Messages.create(paramSource, SOAPVersion.from(paramStyle)));
  }
  
  public MessageContext createContext(Source paramSource)
  {
    throwIfIllegalMessageArgument(paramSource);
    return packet(Messages.create(paramSource, SOAPVersion.from(this.singleSoapStyle)));
  }
  
  public MessageContext createContext(InputStream paramInputStream, String paramString)
    throws IOException
  {
    throwIfIllegalMessageArgument(paramInputStream);
    Packet localPacket = packet(null);
    this.soapCodec.decode(paramInputStream, paramString, localPacket);
    return localPacket;
  }
  
  @Deprecated
  public MessageContext createContext(InputStream paramInputStream, MimeHeaders paramMimeHeaders)
    throws IOException
  {
    String str = getHeader(paramMimeHeaders, "Content-Type");
    Packet localPacket = (Packet)createContext(paramInputStream, str);
    localPacket.acceptableMimeTypes = getHeader(paramMimeHeaders, "Accept");
    localPacket.soapAction = HttpAdapter.fixQuotesAroundSoapAction(getHeader(paramMimeHeaders, "SOAPAction"));
    return localPacket;
  }
  
  static String getHeader(MimeHeaders paramMimeHeaders, String paramString)
  {
    String[] arrayOfString = paramMimeHeaders.getHeader(paramString);
    return (arrayOfString != null) && (arrayOfString.length > 0) ? arrayOfString[0] : null;
  }
  
  static Map<String, List<String>> toMap(MimeHeaders paramMimeHeaders)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramMimeHeaders.getAllHeaders();
    while (localIterator.hasNext())
    {
      MimeHeader localMimeHeader = (MimeHeader)localIterator.next();
      Object localObject = (List)localHashMap.get(localMimeHeader.getName());
      if (localObject == null)
      {
        localObject = new ArrayList();
        localHashMap.put(localMimeHeader.getName(), localObject);
      }
      ((List)localObject).add(localMimeHeader.getValue());
    }
    return localHashMap;
  }
  
  public MessageContext createContext(Message paramMessage)
  {
    throwIfIllegalMessageArgument(paramMessage);
    return packet(paramMessage);
  }
  
  private Packet packet(Message paramMessage)
  {
    Packet localPacket = new Packet();
    localPacket.codec = this.soapCodec;
    if (paramMessage != null) {
      localPacket.setMessage(paramMessage);
    }
    MTOMFeature localMTOMFeature = (MTOMFeature)this.features.get(MTOMFeature.class);
    if (localMTOMFeature != null) {
      localPacket.setMtomFeature(localMTOMFeature);
    }
    return localPacket;
  }
  
  private void throwIfIllegalMessageArgument(Object paramObject)
    throws IllegalArgumentException
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("null messages are not allowed.  Consider using MessageContextFactory.createContext()");
    }
  }
  
  @Deprecated
  public MessageContext doCreate()
  {
    return packet(null);
  }
  
  @Deprecated
  public MessageContext doCreate(SOAPMessage paramSOAPMessage)
  {
    return createContext(paramSOAPMessage);
  }
  
  @Deprecated
  public MessageContext doCreate(Source paramSource, SOAPVersion paramSOAPVersion)
  {
    return packet(Messages.create(paramSource, paramSOAPVersion));
  }
}
