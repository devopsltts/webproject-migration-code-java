package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import javax.activation.MimeType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;

abstract class PropertyInfoImpl<T, C, F, M>
  implements PropertyInfo<T, C>, Locatable, Comparable<PropertyInfoImpl>
{
  protected final PropertySeed<T, C, F, M> seed;
  private final boolean isCollection;
  private final ID id;
  private final MimeType expectedMimeType;
  private final boolean inlineBinary;
  private final QName schemaType;
  protected final ClassInfoImpl<T, C, F, M> parent;
  private final Adapter<T, C> adapter;
  
  protected PropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed)
  {
    this.seed = paramPropertySeed;
    this.parent = paramClassInfoImpl;
    if (paramClassInfoImpl == null) {
      throw new AssertionError();
    }
    MimeType localMimeType = Util.calcExpectedMediaType(this.seed, paramClassInfoImpl.builder);
    if ((localMimeType != null) && (!kind().canHaveXmlMimeType))
    {
      paramClassInfoImpl.builder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_ANNOTATION.format(new Object[] { XmlMimeType.class.getName() }), this.seed.readAnnotation(XmlMimeType.class)));
      localMimeType = null;
    }
    this.expectedMimeType = localMimeType;
    this.inlineBinary = this.seed.hasAnnotation(XmlInlineBinaryData.class);
    Object localObject1 = this.seed.getRawType();
    XmlJavaTypeAdapter localXmlJavaTypeAdapter = getApplicableAdapter(localObject1);
    if (localXmlJavaTypeAdapter != null)
    {
      this.isCollection = false;
      this.adapter = new Adapter(localXmlJavaTypeAdapter, reader(), nav());
    }
    else
    {
      this.isCollection = ((nav().isSubClassOf(localObject1, nav().ref(Collection.class))) || (nav().isArrayButNotByteArray(localObject1)));
      localXmlJavaTypeAdapter = getApplicableAdapter(getIndividualType());
      if (localXmlJavaTypeAdapter == null)
      {
        XmlAttachmentRef localXmlAttachmentRef = (XmlAttachmentRef)this.seed.readAnnotation(XmlAttachmentRef.class);
        if (localXmlAttachmentRef != null)
        {
          paramClassInfoImpl.builder.hasSwaRef = true;
          this.adapter = new Adapter(nav().asDecl(SwaRefAdapter.class), nav());
        }
        else
        {
          this.adapter = null;
          localXmlJavaTypeAdapter = (XmlJavaTypeAdapter)this.seed.readAnnotation(XmlJavaTypeAdapter.class);
          if (localXmlJavaTypeAdapter != null)
          {
            Object localObject2 = reader().getClassValue(localXmlJavaTypeAdapter, "value");
            paramClassInfoImpl.builder.reportError(new IllegalAnnotationException(Messages.UNMATCHABLE_ADAPTER.format(new Object[] { nav().getTypeName(localObject2), nav().getTypeName(localObject1) }), localXmlJavaTypeAdapter));
          }
        }
      }
      else
      {
        this.adapter = new Adapter(localXmlJavaTypeAdapter, reader(), nav());
      }
    }
    this.id = calcId();
    this.schemaType = Util.calcSchemaType(reader(), this.seed, paramClassInfoImpl.clazz, getIndividualType(), this);
  }
  
  public ClassInfoImpl<T, C, F, M> parent()
  {
    return this.parent;
  }
  
  protected final Navigator<T, C, F, M> nav()
  {
    return this.parent.nav();
  }
  
  protected final AnnotationReader<T, C, F, M> reader()
  {
    return this.parent.reader();
  }
  
  public T getRawType()
  {
    return this.seed.getRawType();
  }
  
  public T getIndividualType()
  {
    if (this.adapter != null) {
      return this.adapter.defaultType;
    }
    Object localObject1 = getRawType();
    if (!isCollection()) {
      return localObject1;
    }
    if (nav().isArrayButNotByteArray(localObject1)) {
      return nav().getComponentType(localObject1);
    }
    Object localObject2 = nav().getBaseClass(localObject1, nav().asDecl(Collection.class));
    if (nav().isParameterizedType(localObject2)) {
      return nav().getTypeArgument(localObject2, 0);
    }
    return nav().ref(Object.class);
  }
  
  public final String getName()
  {
    return this.seed.getName();
  }
  
  private boolean isApplicable(XmlJavaTypeAdapter paramXmlJavaTypeAdapter, T paramT)
  {
    if (paramXmlJavaTypeAdapter == null) {
      return false;
    }
    Object localObject1 = reader().getClassValue(paramXmlJavaTypeAdapter, "type");
    if (nav().isSameType(paramT, localObject1)) {
      return true;
    }
    Object localObject2 = reader().getClassValue(paramXmlJavaTypeAdapter, "value");
    Object localObject3 = nav().getBaseClass(localObject2, nav().asDecl(XmlAdapter.class));
    if (!nav().isParameterizedType(localObject3)) {
      return true;
    }
    Object localObject4 = nav().getTypeArgument(localObject3, 1);
    return nav().isSubClassOf(paramT, localObject4);
  }
  
  private XmlJavaTypeAdapter getApplicableAdapter(T paramT)
  {
    XmlJavaTypeAdapter localXmlJavaTypeAdapter1 = (XmlJavaTypeAdapter)this.seed.readAnnotation(XmlJavaTypeAdapter.class);
    if ((localXmlJavaTypeAdapter1 != null) && (isApplicable(localXmlJavaTypeAdapter1, paramT))) {
      return localXmlJavaTypeAdapter1;
    }
    XmlJavaTypeAdapters localXmlJavaTypeAdapters = (XmlJavaTypeAdapters)reader().getPackageAnnotation(XmlJavaTypeAdapters.class, this.parent.clazz, this.seed);
    if (localXmlJavaTypeAdapters != null) {
      for (XmlJavaTypeAdapter localXmlJavaTypeAdapter2 : localXmlJavaTypeAdapters.value()) {
        if (isApplicable(localXmlJavaTypeAdapter2, paramT)) {
          return localXmlJavaTypeAdapter2;
        }
      }
    }
    localXmlJavaTypeAdapter1 = (XmlJavaTypeAdapter)reader().getPackageAnnotation(XmlJavaTypeAdapter.class, this.parent.clazz, this.seed);
    if (isApplicable(localXmlJavaTypeAdapter1, paramT)) {
      return localXmlJavaTypeAdapter1;
    }
    ??? = nav().asDecl(paramT);
    if (??? != null)
    {
      localXmlJavaTypeAdapter1 = (XmlJavaTypeAdapter)reader().getClassAnnotation(XmlJavaTypeAdapter.class, ???, this.seed);
      if ((localXmlJavaTypeAdapter1 != null) && (isApplicable(localXmlJavaTypeAdapter1, paramT))) {
        return localXmlJavaTypeAdapter1;
      }
    }
    return null;
  }
  
  public Adapter<T, C> getAdapter()
  {
    return this.adapter;
  }
  
  public final String displayName()
  {
    return nav().getClassName(this.parent.getClazz()) + '#' + getName();
  }
  
  public final ID id()
  {
    return this.id;
  }
  
  private ID calcId()
  {
    if (this.seed.hasAnnotation(XmlID.class))
    {
      if (!nav().isSameType(getIndividualType(), nav().ref(String.class))) {
        this.parent.builder.reportError(new IllegalAnnotationException(Messages.ID_MUST_BE_STRING.format(new Object[] { getName() }), this.seed));
      }
      return ID.ID;
    }
    if (this.seed.hasAnnotation(XmlIDREF.class)) {
      return ID.IDREF;
    }
    return ID.NONE;
  }
  
  public final MimeType getExpectedMimeType()
  {
    return this.expectedMimeType;
  }
  
  public final boolean inlineBinaryData()
  {
    return this.inlineBinary;
  }
  
  public final QName getSchemaType()
  {
    return this.schemaType;
  }
  
  public final boolean isCollection()
  {
    return this.isCollection;
  }
  
  protected void link()
  {
    if (this.id == ID.IDREF)
    {
      Iterator localIterator = ref().iterator();
      while (localIterator.hasNext())
      {
        TypeInfo localTypeInfo = (TypeInfo)localIterator.next();
        if (!localTypeInfo.canBeReferencedByIDREF()) {
          this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_IDREF.format(new Object[] { this.parent.builder.nav.getTypeName(localTypeInfo.getType()) }), this));
        }
      }
    }
  }
  
  public Locatable getUpstream()
  {
    return this.parent;
  }
  
  public Location getLocation()
  {
    return this.seed.getLocation();
  }
  
  protected final QName calcXmlName(XmlElement paramXmlElement)
  {
    if (paramXmlElement != null) {
      return calcXmlName(paramXmlElement.namespace(), paramXmlElement.name());
    }
    return calcXmlName("##default", "##default");
  }
  
  protected final QName calcXmlName(XmlElementWrapper paramXmlElementWrapper)
  {
    if (paramXmlElementWrapper != null) {
      return calcXmlName(paramXmlElementWrapper.namespace(), paramXmlElementWrapper.name());
    }
    return calcXmlName("##default", "##default");
  }
  
  private QName calcXmlName(String paramString1, String paramString2)
  {
    
    if ((paramString2.length() == 0) || (paramString2.equals("##default"))) {
      paramString2 = this.seed.getName();
    }
    if (paramString1.equals("##default"))
    {
      XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
      if (localXmlSchema != null) {
        switch (1.$SwitchMap$javax$xml$bind$annotation$XmlNsForm[localXmlSchema.elementFormDefault().ordinal()])
        {
        case 1: 
          QName localQName = this.parent.getTypeName();
          if (localQName != null) {
            paramString1 = localQName.getNamespaceURI();
          } else {
            paramString1 = localXmlSchema.namespace();
          }
          if (paramString1.length() == 0) {
            paramString1 = this.parent.builder.defaultNsUri;
          }
          break;
        case 2: 
        case 3: 
          paramString1 = "";
        }
      } else {
        paramString1 = "";
      }
    }
    return new QName(paramString1.intern(), paramString2.intern());
  }
  
  public int compareTo(PropertyInfoImpl paramPropertyInfoImpl)
  {
    return getName().compareTo(paramPropertyInfoImpl.getName());
  }
  
  public final <A extends Annotation> A readAnnotation(Class<A> paramClass)
  {
    return this.seed.readAnnotation(paramClass);
  }
  
  public final boolean hasAnnotation(Class<? extends Annotation> paramClass)
  {
    return this.seed.hasAnnotation(paramClass);
  }
}
