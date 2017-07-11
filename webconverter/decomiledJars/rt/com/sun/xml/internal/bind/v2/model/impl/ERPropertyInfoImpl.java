package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

abstract class ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  extends PropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
{
  private final QName xmlName;
  private final boolean wrapperNillable;
  private final boolean wrapperRequired;
  
  public ERPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlElementWrapper localXmlElementWrapper = (XmlElementWrapper)this.seed.readAnnotation(XmlElementWrapper.class);
    boolean bool1 = false;
    boolean bool2 = false;
    if (!isCollection())
    {
      this.xmlName = null;
      if (localXmlElementWrapper != null) {
        paramClassInfoImpl.builder.reportError(new IllegalAnnotationException(Messages.XML_ELEMENT_WRAPPER_ON_NON_COLLECTION.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '.' + this.seed.getName() }), localXmlElementWrapper));
      }
    }
    else if (localXmlElementWrapper != null)
    {
      this.xmlName = calcXmlName(localXmlElementWrapper);
      bool1 = localXmlElementWrapper.nillable();
      bool2 = localXmlElementWrapper.required();
    }
    else
    {
      this.xmlName = null;
    }
    this.wrapperNillable = bool1;
    this.wrapperRequired = bool2;
  }
  
  public final QName getXmlName()
  {
    return this.xmlName;
  }
  
  public final boolean isCollectionNillable()
  {
    return this.wrapperNillable;
  }
  
  public final boolean isCollectionRequired()
  {
    return this.wrapperRequired;
  }
}
