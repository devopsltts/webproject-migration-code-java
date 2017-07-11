package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.beans.Introspector;
import java.lang.annotation.Annotation;

class GetterSetterPropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
  implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
{
  protected final MethodT getter;
  protected final MethodT setter;
  private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;
  
  GetterSetterPropertySeed(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, MethodT paramMethodT1, MethodT paramMethodT2)
  {
    this.parent = paramClassInfoImpl;
    this.getter = paramMethodT1;
    this.setter = paramMethodT2;
    if ((paramMethodT1 == null) && (paramMethodT2 == null)) {
      throw new IllegalArgumentException();
    }
  }
  
  public TypeT getRawType()
  {
    if (this.getter != null) {
      return this.parent.nav().getReturnType(this.getter);
    }
    return this.parent.nav().getMethodParameters(this.setter)[0];
  }
  
  public <A extends Annotation> A readAnnotation(Class<A> paramClass)
  {
    return this.parent.reader().getMethodAnnotation(paramClass, this.getter, this.setter, this);
  }
  
  public boolean hasAnnotation(Class<? extends Annotation> paramClass)
  {
    return this.parent.reader().hasMethodAnnotation(paramClass, getName(), this.getter, this.setter, this);
  }
  
  public String getName()
  {
    if (this.getter != null) {
      return getName(this.getter);
    }
    return getName(this.setter);
  }
  
  private String getName(MethodT paramMethodT)
  {
    String str1 = this.parent.nav().getMethodName(paramMethodT);
    String str2 = str1.toLowerCase();
    if ((str2.startsWith("get")) || (str2.startsWith("set"))) {
      return camelize(str1.substring(3));
    }
    if (str2.startsWith("is")) {
      return camelize(str1.substring(2));
    }
    return str1;
  }
  
  private static String camelize(String paramString)
  {
    return Introspector.decapitalize(paramString);
  }
  
  public Locatable getUpstream()
  {
    return this.parent;
  }
  
  public Location getLocation()
  {
    if (this.getter != null) {
      return this.parent.nav().getMethodLocation(this.getter);
    }
    return this.parent.nav().getMethodLocation(this.setter);
  }
}
