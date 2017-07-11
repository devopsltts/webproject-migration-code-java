package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRef.DEFAULT;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

class ReferencePropertyInfoImpl<T, C, F, M>
  extends ERPropertyInfoImpl<T, C, F, M>
  implements ReferencePropertyInfo<T, C>, DummyPropertyInfo<T, C, F, M>
{
  private Set<Element<T, C>> types;
  private Set<ReferencePropertyInfoImpl<T, C, F, M>> subTypes = new LinkedHashSet();
  private final boolean isMixed;
  private final WildcardMode wildcard;
  private final C domHandler;
  private Boolean isRequired;
  private static boolean is2_2 = true;
  
  public ReferencePropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    this.isMixed = (paramPropertySeed.readAnnotation(XmlMixed.class) != null);
    XmlAnyElement localXmlAnyElement = (XmlAnyElement)paramPropertySeed.readAnnotation(XmlAnyElement.class);
    if (localXmlAnyElement == null)
    {
      this.wildcard = null;
      this.domHandler = null;
    }
    else
    {
      this.wildcard = (localXmlAnyElement.lax() ? WildcardMode.LAX : WildcardMode.SKIP);
      this.domHandler = nav().asDecl(reader().getClassValue(localXmlAnyElement, "value"));
    }
  }
  
  public Set<? extends Element<T, C>> ref()
  {
    return getElements();
  }
  
  public PropertyKind kind()
  {
    return PropertyKind.REFERENCE;
  }
  
  public Set<? extends Element<T, C>> getElements()
  {
    if (this.types == null) {
      calcTypes(false);
    }
    assert (this.types != null);
    return this.types;
  }
  
  private void calcTypes(boolean paramBoolean)
  {
    this.types = new LinkedHashSet();
    XmlElementRefs localXmlElementRefs = (XmlElementRefs)this.seed.readAnnotation(XmlElementRefs.class);
    XmlElementRef localXmlElementRef = (XmlElementRef)this.seed.readAnnotation(XmlElementRef.class);
    if ((localXmlElementRefs != null) && (localXmlElementRef != null)) {
      this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), localXmlElementRef.annotationType().getName(), localXmlElementRefs.annotationType().getName() }), localXmlElementRef, localXmlElementRefs));
    }
    XmlElementRef[] arrayOfXmlElementRef;
    if (localXmlElementRefs != null) {
      arrayOfXmlElementRef = localXmlElementRefs.value();
    } else if (localXmlElementRef != null) {
      arrayOfXmlElementRef = new XmlElementRef[] { localXmlElementRef };
    } else {
      arrayOfXmlElementRef = null;
    }
    this.isRequired = Boolean.valueOf(!isCollection());
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject8;
    if (arrayOfXmlElementRef != null)
    {
      localObject1 = nav();
      localObject2 = reader();
      localObject3 = ((Navigator)localObject1).ref(XmlElementRef.DEFAULT.class);
      localObject4 = ((Navigator)localObject1).asDecl(JAXBElement.class);
      for (localObject8 : arrayOfXmlElementRef)
      {
        Object localObject9 = ((AnnotationReader)localObject2).getClassValue((Annotation)localObject8, "type");
        if (nav().isSameType(localObject9, localObject3)) {
          localObject9 = ((Navigator)localObject1).erasure(getIndividualType());
        }
        boolean bool1;
        if (((Navigator)localObject1).getBaseClass(localObject9, localObject4) != null) {
          bool1 = addGenericElement((XmlElementRef)localObject8);
        } else {
          bool1 = addAllSubtypes(localObject9);
        }
        if ((this.isRequired.booleanValue()) && (!isRequired((XmlElementRef)localObject8))) {
          this.isRequired = Boolean.valueOf(false);
        }
        if ((paramBoolean) && (!bool1))
        {
          if (nav().isSameType(localObject9, ((Navigator)localObject1).ref(JAXBElement.class))) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(new Object[] { getEffectiveNamespaceFor((XmlElementRef)localObject8), ((XmlElementRef)localObject8).name() }), this));
          } else {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(new Object[] { localObject9 }), this));
          }
          return;
        }
      }
    }
    Object localObject1 = this.subTypes.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ReferencePropertyInfoImpl)((Iterator)localObject1).next();
      localObject3 = ((ReferencePropertyInfoImpl)localObject2).seed;
      localXmlElementRefs = (XmlElementRefs)((PropertySeed)localObject3).readAnnotation(XmlElementRefs.class);
      localXmlElementRef = (XmlElementRef)((PropertySeed)localObject3).readAnnotation(XmlElementRef.class);
      if ((localXmlElementRefs != null) && (localXmlElementRef != null)) {
        this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), localXmlElementRef.annotationType().getName(), localXmlElementRefs.annotationType().getName() }), localXmlElementRef, localXmlElementRefs));
      }
      if (localXmlElementRefs != null) {
        arrayOfXmlElementRef = localXmlElementRefs.value();
      } else if (localXmlElementRef != null) {
        arrayOfXmlElementRef = new XmlElementRef[] { localXmlElementRef };
      } else {
        arrayOfXmlElementRef = null;
      }
      if (arrayOfXmlElementRef != null)
      {
        localObject4 = nav();
        ??? = reader();
        Object localObject6 = ((Navigator)localObject4).ref(XmlElementRef.DEFAULT.class);
        Object localObject7 = ((Navigator)localObject4).asDecl(JAXBElement.class);
        for (Annotation localAnnotation : arrayOfXmlElementRef)
        {
          Object localObject10 = ((AnnotationReader)???).getClassValue(localAnnotation, "type");
          if (nav().isSameType(localObject10, localObject6)) {
            localObject10 = ((Navigator)localObject4).erasure(getIndividualType());
          }
          boolean bool2;
          if (((Navigator)localObject4).getBaseClass(localObject10, localObject7) != null) {
            bool2 = addGenericElement(localAnnotation, (ReferencePropertyInfoImpl)localObject2);
          } else {
            bool2 = addAllSubtypes(localObject10);
          }
          if ((paramBoolean) && (!bool2))
          {
            if (nav().isSameType(localObject10, ((Navigator)localObject4).ref(JAXBElement.class))) {
              this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(new Object[] { getEffectiveNamespaceFor(localAnnotation), localAnnotation.name() }), this));
            } else {
              this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(new Object[0]), this));
            }
            return;
          }
        }
      }
    }
    this.types = Collections.unmodifiableSet(this.types);
  }
  
  public boolean isRequired()
  {
    if (this.isRequired == null) {
      calcTypes(false);
    }
    return this.isRequired.booleanValue();
  }
  
  private boolean isRequired(XmlElementRef paramXmlElementRef)
  {
    if (!is2_2) {
      return true;
    }
    try
    {
      return paramXmlElementRef.required();
    }
    catch (LinkageError localLinkageError)
    {
      is2_2 = false;
    }
    return true;
  }
  
  private boolean addGenericElement(XmlElementRef paramXmlElementRef)
  {
    String str = getEffectiveNamespaceFor(paramXmlElementRef);
    return addGenericElement(this.parent.owner.getElementInfo(this.parent.getClazz(), new QName(str, paramXmlElementRef.name())));
  }
  
  private boolean addGenericElement(XmlElementRef paramXmlElementRef, ReferencePropertyInfoImpl<T, C, F, M> paramReferencePropertyInfoImpl)
  {
    String str = paramReferencePropertyInfoImpl.getEffectiveNamespaceFor(paramXmlElementRef);
    ElementInfoImpl localElementInfoImpl = this.parent.owner.getElementInfo(paramReferencePropertyInfoImpl.parent.getClazz(), new QName(str, paramXmlElementRef.name()));
    this.types.add(localElementInfoImpl);
    return true;
  }
  
  private String getEffectiveNamespaceFor(XmlElementRef paramXmlElementRef)
  {
    String str = paramXmlElementRef.namespace();
    XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
    if ((localXmlSchema != null) && (localXmlSchema.attributeFormDefault() == XmlNsForm.QUALIFIED) && (str.length() == 0)) {
      str = this.parent.builder.defaultNsUri;
    }
    return str;
  }
  
  private boolean addGenericElement(ElementInfo<T, C> paramElementInfo)
  {
    if (paramElementInfo == null) {
      return false;
    }
    this.types.add(paramElementInfo);
    Iterator localIterator = paramElementInfo.getSubstitutionMembers().iterator();
    while (localIterator.hasNext())
    {
      ElementInfo localElementInfo = (ElementInfo)localIterator.next();
      addGenericElement(localElementInfo);
    }
    return true;
  }
  
  private boolean addAllSubtypes(T paramT)
  {
    Navigator localNavigator = nav();
    NonElement localNonElement = this.parent.builder.getClassInfo(localNavigator.asDecl(paramT), this);
    if (!(localNonElement instanceof ClassInfo)) {
      return false;
    }
    boolean bool = false;
    ClassInfo localClassInfo = (ClassInfo)localNonElement;
    if (localClassInfo.isElement())
    {
      this.types.add(localClassInfo.asElement());
      bool = true;
    }
    Iterator localIterator = this.parent.owner.beans().values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (ClassInfo)localIterator.next();
      if ((((ClassInfo)localObject).isElement()) && (localNavigator.isSubClassOf(((ClassInfo)localObject).getType(), paramT)))
      {
        this.types.add(((ClassInfo)localObject).asElement());
        bool = true;
      }
    }
    localIterator = this.parent.owner.getElementMappings(null).values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (ElementInfo)localIterator.next();
      if (localNavigator.isSubClassOf(((ElementInfo)localObject).getType(), paramT))
      {
        this.types.add(localObject);
        bool = true;
      }
    }
    return bool;
  }
  
  protected void link()
  {
    super.link();
    calcTypes(true);
  }
  
  public final void addType(PropertyInfoImpl<T, C, F, M> paramPropertyInfoImpl)
  {
    this.subTypes.add((ReferencePropertyInfoImpl)paramPropertyInfoImpl);
  }
  
  public final boolean isMixed()
  {
    return this.isMixed;
  }
  
  public final WildcardMode getWildcard()
  {
    return this.wildcard;
  }
  
  public final C getDOMHandler()
  {
    return this.domHandler;
  }
}
