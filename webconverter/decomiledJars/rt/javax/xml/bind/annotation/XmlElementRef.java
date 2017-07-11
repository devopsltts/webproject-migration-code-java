package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD})
public @interface XmlElementRef
{
  Class type() default DEFAULT.class;
  
  String namespace() default "";
  
  String name() default "##default";
  
  boolean required() default true;
  
  public static final class DEFAULT
  {
    public DEFAULT() {}
  }
}
