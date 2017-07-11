package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.Databinding.Builder;
import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MappingInfo;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingFactoryImpl
  extends DatabindingFactory
{
  static final String WsRuntimeFactoryDefaultImpl = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
  protected Map<String, Object> properties = new HashMap();
  protected DatabindingProvider defaultRuntimeFactory;
  protected List<DatabindingProvider> providers;
  
  private static List<DatabindingProvider> providers()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ServiceFinder.find(DatabindingProvider.class).iterator();
    while (localIterator.hasNext())
    {
      DatabindingProvider localDatabindingProvider = (DatabindingProvider)localIterator.next();
      localArrayList.add(localDatabindingProvider);
    }
    return localArrayList;
  }
  
  public DatabindingFactoryImpl() {}
  
  public Map<String, Object> properties()
  {
    return this.properties;
  }
  
  <T> T property(Class<T> paramClass, String paramString)
  {
    if (paramString == null) {
      paramString = paramClass.getName();
    }
    return paramClass.cast(this.properties.get(paramString));
  }
  
  public DatabindingProvider provider(DatabindingConfig paramDatabindingConfig)
  {
    String str = databindingMode(paramDatabindingConfig);
    if (this.providers == null) {
      this.providers = providers();
    }
    Object localObject = null;
    if (this.providers != null)
    {
      Iterator localIterator = this.providers.iterator();
      while (localIterator.hasNext())
      {
        DatabindingProvider localDatabindingProvider = (DatabindingProvider)localIterator.next();
        if (localDatabindingProvider.isFor(str)) {
          localObject = localDatabindingProvider;
        }
      }
    }
    if (localObject == null) {
      localObject = new DatabindingProviderImpl();
    }
    return localObject;
  }
  
  public Databinding createRuntime(DatabindingConfig paramDatabindingConfig)
  {
    DatabindingProvider localDatabindingProvider = provider(paramDatabindingConfig);
    return localDatabindingProvider.create(paramDatabindingConfig);
  }
  
  public WSDLGenerator createWsdlGen(DatabindingConfig paramDatabindingConfig)
  {
    DatabindingProvider localDatabindingProvider = provider(paramDatabindingConfig);
    return localDatabindingProvider.wsdlGen(paramDatabindingConfig);
  }
  
  String databindingMode(DatabindingConfig paramDatabindingConfig)
  {
    if ((paramDatabindingConfig.getMappingInfo() != null) && (paramDatabindingConfig.getMappingInfo().getDatabindingMode() != null)) {
      return paramDatabindingConfig.getMappingInfo().getDatabindingMode();
    }
    if (paramDatabindingConfig.getFeatures() != null)
    {
      Iterator localIterator = paramDatabindingConfig.getFeatures().iterator();
      while (localIterator.hasNext())
      {
        WebServiceFeature localWebServiceFeature = (WebServiceFeature)localIterator.next();
        if ((localWebServiceFeature instanceof DatabindingModeFeature))
        {
          DatabindingModeFeature localDatabindingModeFeature = (DatabindingModeFeature)localWebServiceFeature;
          paramDatabindingConfig.properties().putAll(localDatabindingModeFeature.getProperties());
          return localDatabindingModeFeature.getMode();
        }
      }
    }
    return null;
  }
  
  ClassLoader classLoader()
  {
    ClassLoader localClassLoader = (ClassLoader)property(ClassLoader.class, null);
    if (localClassLoader == null) {
      localClassLoader = Thread.currentThread().getContextClassLoader();
    }
    return localClassLoader;
  }
  
  Properties loadPropertiesFile(String paramString)
  {
    ClassLoader localClassLoader = classLoader();
    Properties localProperties = new Properties();
    try
    {
      InputStream localInputStream = null;
      if (localClassLoader == null) {
        localInputStream = ClassLoader.getSystemResourceAsStream(paramString);
      } else {
        localInputStream = localClassLoader.getResourceAsStream(paramString);
      }
      if (localInputStream != null) {
        localProperties.load(localInputStream);
      }
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    return localProperties;
  }
  
  public Databinding.Builder createBuilder(Class<?> paramClass1, Class<?> paramClass2)
  {
    return new ConfigBuilder(this, paramClass1, paramClass2);
  }
  
  static class ConfigBuilder
    implements Databinding.Builder
  {
    DatabindingConfig config;
    DatabindingFactoryImpl factory;
    
    ConfigBuilder(DatabindingFactoryImpl paramDatabindingFactoryImpl, Class<?> paramClass1, Class<?> paramClass2)
    {
      this.factory = paramDatabindingFactoryImpl;
      this.config = new DatabindingConfig();
      this.config.setContractClass(paramClass1);
      this.config.setEndpointClass(paramClass2);
    }
    
    public Databinding.Builder targetNamespace(String paramString)
    {
      this.config.getMappingInfo().setTargetNamespace(paramString);
      return this;
    }
    
    public Databinding.Builder serviceName(QName paramQName)
    {
      this.config.getMappingInfo().setServiceName(paramQName);
      return this;
    }
    
    public Databinding.Builder portName(QName paramQName)
    {
      this.config.getMappingInfo().setPortName(paramQName);
      return this;
    }
    
    public Databinding.Builder wsdlURL(URL paramURL)
    {
      this.config.setWsdlURL(paramURL);
      return this;
    }
    
    public Databinding.Builder wsdlSource(Source paramSource)
    {
      this.config.setWsdlSource(paramSource);
      return this;
    }
    
    public Databinding.Builder entityResolver(EntityResolver paramEntityResolver)
    {
      this.config.setEntityResolver(paramEntityResolver);
      return this;
    }
    
    public Databinding.Builder classLoader(ClassLoader paramClassLoader)
    {
      this.config.setClassLoader(paramClassLoader);
      return this;
    }
    
    public Databinding.Builder feature(WebServiceFeature... paramVarArgs)
    {
      this.config.setFeatures(paramVarArgs);
      return this;
    }
    
    public Databinding.Builder property(String paramString, Object paramObject)
    {
      this.config.properties().put(paramString, paramObject);
      if (isfor(BindingID.class, paramString, paramObject)) {
        this.config.getMappingInfo().setBindingID((BindingID)paramObject);
      }
      if (isfor(WSBinding.class, paramString, paramObject)) {
        this.config.setWSBinding((WSBinding)paramObject);
      }
      if (isfor(WSDLPort.class, paramString, paramObject)) {
        this.config.setWsdlPort((WSDLPort)paramObject);
      }
      if (isfor(MetadataReader.class, paramString, paramObject)) {
        this.config.setMetadataReader((MetadataReader)paramObject);
      }
      return this;
    }
    
    boolean isfor(Class<?> paramClass, String paramString, Object paramObject)
    {
      return (paramClass.getName().equals(paramString)) && (paramClass.isInstance(paramObject));
    }
    
    public Databinding build()
    {
      return this.factory.createRuntime(this.config);
    }
    
    public WSDLGenerator createWSDLGenerator()
    {
      return this.factory.createWsdlGen(this.config);
    }
  }
}
