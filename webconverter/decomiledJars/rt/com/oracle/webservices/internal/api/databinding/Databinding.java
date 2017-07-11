package com.oracle.webservices.internal.api.databinding;

import com.oracle.webservices.internal.api.message.MessageContext;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public abstract interface Databinding
{
  public abstract JavaCallInfo createJavaCallInfo(Method paramMethod, Object[] paramArrayOfObject);
  
  public abstract MessageContext serializeRequest(JavaCallInfo paramJavaCallInfo);
  
  public abstract JavaCallInfo deserializeResponse(MessageContext paramMessageContext, JavaCallInfo paramJavaCallInfo);
  
  public abstract JavaCallInfo deserializeRequest(MessageContext paramMessageContext);
  
  public abstract MessageContext serializeResponse(JavaCallInfo paramJavaCallInfo);
  
  public static abstract interface Builder
  {
    public abstract Builder targetNamespace(String paramString);
    
    public abstract Builder serviceName(QName paramQName);
    
    public abstract Builder portName(QName paramQName);
    
    /**
     * @deprecated
     */
    public abstract Builder wsdlURL(URL paramURL);
    
    /**
     * @deprecated
     */
    public abstract Builder wsdlSource(Source paramSource);
    
    /**
     * @deprecated
     */
    public abstract Builder entityResolver(EntityResolver paramEntityResolver);
    
    public abstract Builder classLoader(ClassLoader paramClassLoader);
    
    public abstract Builder feature(WebServiceFeature... paramVarArgs);
    
    public abstract Builder property(String paramString, Object paramObject);
    
    public abstract Databinding build();
    
    public abstract WSDLGenerator createWSDLGenerator();
  }
}
