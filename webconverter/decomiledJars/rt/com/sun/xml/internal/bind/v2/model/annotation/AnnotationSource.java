package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;

public abstract interface AnnotationSource
{
  public abstract <A extends Annotation> A readAnnotation(Class<A> paramClass);
  
  public abstract boolean hasAnnotation(Class<? extends Annotation> paramClass);
}
