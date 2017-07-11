package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.server.DraconianValidationErrorHandler;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class SchemaValidationFeature
  extends WebServiceFeature
{
  public static final String ID = "http://jax-ws.dev.java.net/features/schema-validation";
  private final Class<? extends ValidationErrorHandler> clazz;
  private final boolean inbound;
  private final boolean outbound;
  
  public SchemaValidationFeature()
  {
    this(true, true, DraconianValidationErrorHandler.class);
  }
  
  public SchemaValidationFeature(Class<? extends ValidationErrorHandler> paramClass)
  {
    this(true, true, paramClass);
  }
  
  public SchemaValidationFeature(boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramBoolean1, paramBoolean2, DraconianValidationErrorHandler.class);
  }
  
  @FeatureConstructor({"inbound", "outbound", "handler"})
  public SchemaValidationFeature(boolean paramBoolean1, boolean paramBoolean2, Class<? extends ValidationErrorHandler> paramClass)
  {
    this.enabled = true;
    this.inbound = paramBoolean1;
    this.outbound = paramBoolean2;
    this.clazz = paramClass;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://jax-ws.dev.java.net/features/schema-validation";
  }
  
  @ManagedAttribute
  public Class<? extends ValidationErrorHandler> getErrorHandler()
  {
    return this.clazz;
  }
  
  public boolean isInbound()
  {
    return this.inbound;
  }
  
  public boolean isOutbound()
  {
    return this.outbound;
  }
}
