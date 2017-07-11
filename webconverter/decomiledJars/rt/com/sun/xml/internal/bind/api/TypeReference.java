package com.sun.xml.internal.bind.api;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.namespace.QName;

public final class TypeReference
{
  public final QName tagName;
  public final Type type;
  public final Annotation[] annotations;
  
  public TypeReference(QName paramQName, Type paramType, Annotation... paramVarArgs)
  {
    if ((paramQName == null) || (paramType == null) || (paramVarArgs == null))
    {
      String str = "";
      if (paramQName == null) {
        str = "tagName";
      }
      if (paramType == null) {
        str = str + (str.length() > 0 ? ", type" : "type");
      }
      if (paramVarArgs == null) {
        str = str + (str.length() > 0 ? ", annotations" : "annotations");
      }
      Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { str });
      throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { str }));
    }
    this.tagName = new QName(paramQName.getNamespaceURI().intern(), paramQName.getLocalPart().intern(), paramQName.getPrefix());
    this.type = paramType;
    this.annotations = paramVarArgs;
  }
  
  public <A extends Annotation> A get(Class<A> paramClass)
  {
    for (Annotation localAnnotation : this.annotations) {
      if (localAnnotation.annotationType() == paramClass) {
        return (Annotation)paramClass.cast(localAnnotation);
      }
    }
    return null;
  }
  
  public TypeReference toItemType()
  {
    Type localType = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(this.type, Collection.class);
    if (localType == null) {
      return this;
    }
    return new TypeReference(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(localType, 0), new Annotation[0]);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    TypeReference localTypeReference = (TypeReference)paramObject;
    if (!Arrays.equals(this.annotations, localTypeReference.annotations)) {
      return false;
    }
    if (!this.tagName.equals(localTypeReference.tagName)) {
      return false;
    }
    return this.type.equals(localTypeReference.type);
  }
  
  public int hashCode()
  {
    int i = this.tagName.hashCode();
    i = 31 * i + this.type.hashCode();
    i = 31 * i + Arrays.hashCode(this.annotations);
    return i;
  }
}
