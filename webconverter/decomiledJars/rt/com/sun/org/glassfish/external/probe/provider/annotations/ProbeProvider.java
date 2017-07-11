package com.sun.org.glassfish.external.probe.provider.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface ProbeProvider
{
  String providerName() default "";
  
  String moduleProviderName() default "";
  
  String moduleName() default "";
  
  String probeProviderName() default "";
}
