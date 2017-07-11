package javax.xml.bind.annotation.adapters;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PARAMETER})
public @interface XmlJavaTypeAdapter
{
  Class<? extends XmlAdapter> value();
  
  Class type() default DEFAULT.class;
  
  public static final class DEFAULT
  {
    public DEFAULT() {}
  }
}
