package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;

class EnumLeafInfoImpl<T, C, F, M>
  extends TypeInfoImpl<T, C, F, M>
  implements EnumLeafInfo<T, C>, Element<T, C>, Iterable<EnumConstantImpl<T, C, F, M>>
{
  final C clazz;
  NonElement<T, C> baseType;
  private final T type;
  private final QName typeName;
  private EnumConstantImpl<T, C, F, M> firstConstant;
  private QName elementName;
  protected boolean tokenStringType;
  
  public EnumLeafInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC, T paramT)
  {
    super(paramModelBuilder, paramLocatable);
    this.clazz = paramC;
    this.type = paramT;
    this.elementName = parseElementName(paramC);
    this.typeName = parseTypeName(paramC);
    XmlEnum localXmlEnum = (XmlEnum)paramModelBuilder.reader.getClassAnnotation(XmlEnum.class, paramC, this);
    if (localXmlEnum != null)
    {
      Object localObject = paramModelBuilder.reader.getClassValue(localXmlEnum, "value");
      this.baseType = paramModelBuilder.getTypeInfo(localObject, this);
    }
    else
    {
      this.baseType = paramModelBuilder.getTypeInfo(paramModelBuilder.nav.ref(String.class), this);
    }
  }
  
  protected void calcConstants()
  {
    EnumConstantImpl localEnumConstantImpl = null;
    Collection localCollection = nav().getDeclaredFields(this.clazz);
    Object localObject1 = localCollection.iterator();
    XmlSchemaType localXmlSchemaType;
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = ((Iterator)localObject1).next();
      if (nav().isSameType(nav().getFieldType(localObject2), nav().ref(String.class)))
      {
        localXmlSchemaType = (XmlSchemaType)this.builder.reader.getFieldAnnotation(XmlSchemaType.class, localObject2, this);
        if ((localXmlSchemaType != null) && ("token".equals(localXmlSchemaType.name())))
        {
          this.tokenStringType = true;
          break;
        }
      }
    }
    localObject1 = nav().getEnumConstants(this.clazz);
    for (int i = localObject1.length - 1; i >= 0; i--)
    {
      localXmlSchemaType = localObject1[i];
      String str1 = nav().getFieldName(localXmlSchemaType);
      XmlEnumValue localXmlEnumValue = (XmlEnumValue)this.builder.reader.getFieldAnnotation(XmlEnumValue.class, localXmlSchemaType, this);
      String str2;
      if (localXmlEnumValue == null) {
        str2 = str1;
      } else {
        str2 = localXmlEnumValue.value();
      }
      localEnumConstantImpl = createEnumConstant(str1, str2, localXmlSchemaType, localEnumConstantImpl);
    }
    this.firstConstant = localEnumConstantImpl;
  }
  
  protected EnumConstantImpl<T, C, F, M> createEnumConstant(String paramString1, String paramString2, F paramF, EnumConstantImpl<T, C, F, M> paramEnumConstantImpl)
  {
    return new EnumConstantImpl(this, paramString1, paramString2, paramEnumConstantImpl);
  }
  
  public T getType()
  {
    return this.type;
  }
  
  public boolean isToken()
  {
    return this.tokenStringType;
  }
  
  /**
   * @deprecated
   */
  public final boolean canBeReferencedByIDREF()
  {
    return false;
  }
  
  public QName getTypeName()
  {
    return this.typeName;
  }
  
  public C getClazz()
  {
    return this.clazz;
  }
  
  public NonElement<T, C> getBaseType()
  {
    return this.baseType;
  }
  
  public boolean isSimpleType()
  {
    return true;
  }
  
  public Location getLocation()
  {
    return nav().getClassLocation(this.clazz);
  }
  
  public Iterable<? extends EnumConstantImpl<T, C, F, M>> getConstants()
  {
    if (this.firstConstant == null) {
      calcConstants();
    }
    return this;
  }
  
  public void link()
  {
    getConstants();
    super.link();
  }
  
  /**
   * @deprecated
   */
  public Element<T, C> getSubstitutionHead()
  {
    return null;
  }
  
  public QName getElementName()
  {
    return this.elementName;
  }
  
  public boolean isElement()
  {
    return this.elementName != null;
  }
  
  public Element<T, C> asElement()
  {
    if (isElement()) {
      return this;
    }
    return null;
  }
  
  /**
   * @deprecated
   */
  public ClassInfo<T, C> getScope()
  {
    return null;
  }
  
  public Iterator<EnumConstantImpl<T, C, F, M>> iterator()
  {
    new Iterator()
    {
      private EnumConstantImpl<T, C, F, M> next = EnumLeafInfoImpl.this.firstConstant;
      
      public boolean hasNext()
      {
        return this.next != null;
      }
      
      public EnumConstantImpl<T, C, F, M> next()
      {
        EnumConstantImpl localEnumConstantImpl = this.next;
        this.next = this.next.next;
        return localEnumConstantImpl;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}
