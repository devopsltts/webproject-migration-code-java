package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;

public abstract class AbstractInlineAnnotationReaderImpl<T, C, F, M>
  implements AnnotationReader<T, C, F, M>
{
  private ErrorHandler errorHandler;
  
  public AbstractInlineAnnotationReaderImpl() {}
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    if (paramErrorHandler == null) {
      throw new IllegalArgumentException();
    }
    this.errorHandler = paramErrorHandler;
  }
  
  public final ErrorHandler getErrorHandler()
  {
    assert (this.errorHandler != null) : "error handler must be set before use";
    return this.errorHandler;
  }
  
  public final <A extends Annotation> A getMethodAnnotation(Class<A> paramClass, M paramM1, M paramM2, Locatable paramLocatable)
  {
    Annotation localAnnotation1 = paramM1 == null ? null : getMethodAnnotation(paramClass, paramM1, paramLocatable);
    Annotation localAnnotation2 = paramM2 == null ? null : getMethodAnnotation(paramClass, paramM2, paramLocatable);
    if (localAnnotation1 == null)
    {
      if (localAnnotation2 == null) {
        return null;
      }
      return localAnnotation2;
    }
    if (localAnnotation2 == null) {
      return localAnnotation1;
    }
    getErrorHandler().error(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(new Object[] { paramClass.getName(), fullName(paramM1), fullName(paramM2) }), localAnnotation1, localAnnotation2));
    return localAnnotation1;
  }
  
  public boolean hasMethodAnnotation(Class<? extends Annotation> paramClass, String paramString, M paramM1, M paramM2, Locatable paramLocatable)
  {
    int i = (paramM1 != null) && (hasMethodAnnotation(paramClass, paramM1)) ? 1 : 0;
    int j = (paramM2 != null) && (hasMethodAnnotation(paramClass, paramM2)) ? 1 : 0;
    if ((i != 0) && (j != 0)) {
      getMethodAnnotation(paramClass, paramM1, paramM2, paramLocatable);
    }
    return (i != 0) || (j != 0);
  }
  
  protected abstract String fullName(M paramM);
}
