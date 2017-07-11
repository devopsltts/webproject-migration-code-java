package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

class MapPropertyInfoImpl<T, C, F, M>
  extends PropertyInfoImpl<T, C, F, M>
  implements MapPropertyInfo<T, C>
{
  private final QName xmlName;
  private boolean nil;
  private final T keyType;
  private final T valueType;
  private NonElement<T, C> keyTypeInfo;
  private NonElement<T, C> valueTypeInfo;
  
  public MapPropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlElementWrapper localXmlElementWrapper = (XmlElementWrapper)paramPropertySeed.readAnnotation(XmlElementWrapper.class);
    this.xmlName = calcXmlName(localXmlElementWrapper);
    this.nil = ((localXmlElementWrapper != null) && (localXmlElementWrapper.nillable()));
    Object localObject1 = getRawType();
    Object localObject2 = nav().getBaseClass(localObject1, nav().asDecl(Map.class));
    assert (localObject2 != null);
    if (nav().isParameterizedType(localObject2))
    {
      this.keyType = nav().getTypeArgument(localObject2, 0);
      this.valueType = nav().getTypeArgument(localObject2, 1);
    }
    else
    {
      this.keyType = (this.valueType = nav().ref(Object.class));
    }
  }
  
  public Collection<? extends TypeInfo<T, C>> ref()
  {
    return Arrays.asList(new NonElement[] { getKeyType(), getValueType() });
  }
  
  public final PropertyKind kind()
  {
    return PropertyKind.MAP;
  }
  
  public QName getXmlName()
  {
    return this.xmlName;
  }
  
  public boolean isCollectionNillable()
  {
    return this.nil;
  }
  
  public NonElement<T, C> getKeyType()
  {
    if (this.keyTypeInfo == null) {
      this.keyTypeInfo = getTarget(this.keyType);
    }
    return this.keyTypeInfo;
  }
  
  public NonElement<T, C> getValueType()
  {
    if (this.valueTypeInfo == null) {
      this.valueTypeInfo = getTarget(this.valueType);
    }
    return this.valueTypeInfo;
  }
  
  public NonElement<T, C> getTarget(T paramT)
  {
    assert (this.parent.builder != null) : "this method must be called during the build stage";
    return this.parent.builder.getTypeInfo(paramT, this);
  }
}
