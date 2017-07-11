package javax.lang.model;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;

public abstract interface AnnotatedConstruct
{
  public abstract List<? extends AnnotationMirror> getAnnotationMirrors();
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
  
  public abstract <A extends Annotation> A[] getAnnotationsByType(Class<A> paramClass);
}
