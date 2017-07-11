package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import java.io.File;
import java.util.Map;

public class DatabindingProviderImpl
  implements DatabindingProvider
{
  private static final String CachedDatabinding = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
  Map<String, Object> properties;
  
  public DatabindingProviderImpl() {}
  
  public void init(Map<String, Object> paramMap)
  {
    this.properties = paramMap;
  }
  
  DatabindingImpl getCachedDatabindingImpl(DatabindingConfig paramDatabindingConfig)
  {
    Object localObject = paramDatabindingConfig.properties().get("com.sun.xml.internal.ws.db.DatabindingProviderImpl");
    return (localObject != null) && ((localObject instanceof DatabindingImpl)) ? (DatabindingImpl)localObject : null;
  }
  
  public Databinding create(DatabindingConfig paramDatabindingConfig)
  {
    DatabindingImpl localDatabindingImpl = getCachedDatabindingImpl(paramDatabindingConfig);
    if (localDatabindingImpl == null)
    {
      localDatabindingImpl = new DatabindingImpl(this, paramDatabindingConfig);
      paramDatabindingConfig.properties().put("com.sun.xml.internal.ws.db.DatabindingProviderImpl", localDatabindingImpl);
    }
    return localDatabindingImpl;
  }
  
  public WSDLGenerator wsdlGen(DatabindingConfig paramDatabindingConfig)
  {
    DatabindingImpl localDatabindingImpl = (DatabindingImpl)create(paramDatabindingConfig);
    return new JaxwsWsdlGen(localDatabindingImpl);
  }
  
  public boolean isFor(String paramString)
  {
    return true;
  }
  
  public static class JaxwsWsdlGen
    implements WSDLGenerator
  {
    DatabindingImpl databinding;
    WSDLGenInfo wsdlGenInfo;
    
    JaxwsWsdlGen(DatabindingImpl paramDatabindingImpl)
    {
      this.databinding = paramDatabindingImpl;
      this.wsdlGenInfo = new WSDLGenInfo();
    }
    
    public WSDLGenerator inlineSchema(boolean paramBoolean)
    {
      this.wsdlGenInfo.setInlineSchemas(paramBoolean);
      return this;
    }
    
    public WSDLGenerator property(String paramString, Object paramObject)
    {
      return this;
    }
    
    public void generate(WSDLResolver paramWSDLResolver)
    {
      this.wsdlGenInfo.setWsdlResolver(paramWSDLResolver);
      this.databinding.generateWSDL(this.wsdlGenInfo);
    }
    
    public void generate(File paramFile, String paramString)
    {
      this.databinding.generateWSDL(this.wsdlGenInfo);
    }
  }
}
