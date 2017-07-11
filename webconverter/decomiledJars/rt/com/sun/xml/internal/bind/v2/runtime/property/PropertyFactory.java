package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

public abstract class PropertyFactory
{
  private static final Constructor<? extends Property>[] propImpls;
  
  private PropertyFactory() {}
  
  public static Property create(JAXBContextImpl paramJAXBContextImpl, RuntimePropertyInfo paramRuntimePropertyInfo)
  {
    PropertyKind localPropertyKind = paramRuntimePropertyInfo.kind();
    switch (1.$SwitchMap$com$sun$xml$internal$bind$v2$model$core$PropertyKind[localPropertyKind.ordinal()])
    {
    case 1: 
      return new AttributeProperty(paramJAXBContextImpl, (RuntimeAttributePropertyInfo)paramRuntimePropertyInfo);
    case 2: 
      return new ValueProperty(paramJAXBContextImpl, (RuntimeValuePropertyInfo)paramRuntimePropertyInfo);
    case 3: 
      if (((RuntimeElementPropertyInfo)paramRuntimePropertyInfo).isValueList()) {
        return new ListElementProperty(paramJAXBContextImpl, (RuntimeElementPropertyInfo)paramRuntimePropertyInfo);
      }
      break;
    case 4: 
    case 5: 
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    boolean bool1 = paramRuntimePropertyInfo.isCollection();
    boolean bool2 = isLeaf(paramRuntimePropertyInfo);
    Constructor localConstructor = propImpls[(6 + 0 + localPropertyKind.propertyIndex)];
    try
    {
      return (Property)localConstructor.newInstance(new Object[] { paramJAXBContextImpl, paramRuntimePropertyInfo });
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new InstantiationError(localInstantiationException.getMessage());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalAccessError(localIllegalAccessException.getMessage());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getCause();
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      throw new AssertionError(localThrowable);
    }
  }
  
  static boolean isLeaf(RuntimePropertyInfo paramRuntimePropertyInfo)
  {
    Collection localCollection = paramRuntimePropertyInfo.ref();
    if (localCollection.size() != 1) {
      return false;
    }
    RuntimeTypeInfo localRuntimeTypeInfo = (RuntimeTypeInfo)localCollection.iterator().next();
    if (!(localRuntimeTypeInfo instanceof RuntimeNonElement)) {
      return false;
    }
    if (paramRuntimePropertyInfo.id() == ID.IDREF) {
      return true;
    }
    if (((RuntimeNonElement)localRuntimeTypeInfo).getTransducer() == null) {
      return false;
    }
    return paramRuntimePropertyInfo.getIndividualType().equals(localRuntimeTypeInfo.getType());
  }
  
  static
  {
    Class[] arrayOfClass = { SingleElementLeafProperty.class, null, null, ArrayElementLeafProperty.class, null, null, SingleElementNodeProperty.class, SingleReferenceNodeProperty.class, SingleMapNodeProperty.class, ArrayElementNodeProperty.class, ArrayReferenceNodeProperty.class, null };
    propImpls = new Constructor[arrayOfClass.length];
    for (int i = 0; i < propImpls.length; i++) {
      if (arrayOfClass[i] != null) {
        propImpls[i] = arrayOfClass[i].getConstructors()[0];
      }
    }
  }
}
