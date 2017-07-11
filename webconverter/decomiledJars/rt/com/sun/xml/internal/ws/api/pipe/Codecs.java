package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;

public abstract class Codecs
{
  public Codecs() {}
  
  @NotNull
  public static SOAPBindingCodec createSOAPBindingCodec(WSFeatureList paramWSFeatureList)
  {
    return new com.sun.xml.internal.ws.encoding.SOAPBindingCodec(paramWSFeatureList);
  }
  
  @NotNull
  public static Codec createXMLCodec(WSFeatureList paramWSFeatureList)
  {
    return new XMLHTTPBindingCodec(paramWSFeatureList);
  }
  
  @NotNull
  public static SOAPBindingCodec createSOAPBindingCodec(WSBinding paramWSBinding, StreamSOAPCodec paramStreamSOAPCodec)
  {
    return new com.sun.xml.internal.ws.encoding.SOAPBindingCodec(paramWSBinding.getFeatures(), paramStreamSOAPCodec);
  }
  
  @NotNull
  public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull SOAPVersion paramSOAPVersion)
  {
    return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(paramSOAPVersion);
  }
  
  /**
   * @deprecated
   */
  @NotNull
  public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSBinding paramWSBinding)
  {
    return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(paramWSBinding);
  }
  
  @NotNull
  public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSFeatureList paramWSFeatureList)
  {
    return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(paramWSFeatureList);
  }
}
