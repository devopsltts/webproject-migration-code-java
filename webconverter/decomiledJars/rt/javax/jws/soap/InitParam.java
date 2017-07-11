package javax.jws.soap;

import java.lang.annotation.Annotation;

@Deprecated
public @interface InitParam
{
  String name();
  
  String value();
}
