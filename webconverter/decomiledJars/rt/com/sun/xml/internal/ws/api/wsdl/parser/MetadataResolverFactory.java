package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xml.sax.EntityResolver;

public abstract class MetadataResolverFactory
{
  public MetadataResolverFactory() {}
  
  @NotNull
  public abstract MetaDataResolver metadataResolver(@Nullable EntityResolver paramEntityResolver);
}
