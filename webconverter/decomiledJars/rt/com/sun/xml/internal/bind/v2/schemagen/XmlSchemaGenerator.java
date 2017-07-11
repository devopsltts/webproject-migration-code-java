package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.api.ErrorListener;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.MaybeElement;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.impl.ClassInfoImpl;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import com.sun.xml.internal.bind.v2.schemagen.episode.Bindings;
import com.sun.xml.internal.bind.v2.schemagen.episode.Klass;
import com.sun.xml.internal.bind.v2.schemagen.episode.SchemaBindings;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Any;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttrDecls;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexContent;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.NoFixedFacet;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleContent;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleRestriction;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleRestrictionModel;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeDefParticle;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Wildcard;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import com.sun.xml.internal.bind.v2.util.StackRecorder;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TxwException;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXParseException;

public final class XmlSchemaGenerator<T, C, F, M>
{
  private static final Logger logger = com.sun.xml.internal.bind.Util.getClassLogger();
  private final Map<String, XmlSchemaGenerator<T, C, F, M>.Namespace> namespaces = new TreeMap(NAMESPACE_COMPARATOR);
  private ErrorListener errorListener;
  private Navigator<T, C, F, M> navigator;
  private final TypeInfoSet<T, C, F, M> types;
  private final NonElement<T, C> stringType;
  private final NonElement<T, C> anyType;
  private final CollisionCheckStack<ClassInfo<T, C>> collisionChecker = new CollisionCheckStack();
  private static final Comparator<String> NAMESPACE_COMPARATOR = new Comparator()
  {
    public int compare(String paramAnonymousString1, String paramAnonymousString2)
    {
      return -paramAnonymousString1.compareTo(paramAnonymousString2);
    }
  };
  private static final String newline = "\n";
  
  public XmlSchemaGenerator(Navigator<T, C, F, M> paramNavigator, TypeInfoSet<T, C, F, M> paramTypeInfoSet)
  {
    this.navigator = paramNavigator;
    this.types = paramTypeInfoSet;
    this.stringType = paramTypeInfoSet.getTypeInfo(paramNavigator.ref(String.class));
    this.anyType = paramTypeInfoSet.getAnyTypeInfo();
    Iterator localIterator = paramTypeInfoSet.beans().values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (ClassInfo)localIterator.next();
      add((ClassInfo)localObject);
    }
    localIterator = paramTypeInfoSet.getElementMappings(null).values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (ElementInfo)localIterator.next();
      add((ElementInfo)localObject);
    }
    localIterator = paramTypeInfoSet.enums().values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (EnumLeafInfo)localIterator.next();
      add((EnumLeafInfo)localObject);
    }
    localIterator = paramTypeInfoSet.arrays().values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (ArrayInfo)localIterator.next();
      add((ArrayInfo)localObject);
    }
  }
  
  private XmlSchemaGenerator<T, C, F, M>.Namespace getNamespace(String paramString)
  {
    Namespace localNamespace = (Namespace)this.namespaces.get(paramString);
    if (localNamespace == null) {
      this.namespaces.put(paramString, localNamespace = new Namespace(paramString));
    }
    return localNamespace;
  }
  
  public void add(ClassInfo<T, C> paramClassInfo)
  {
    assert (paramClassInfo != null);
    String str1 = null;
    if (paramClassInfo.getClazz() == this.navigator.asDecl(CompositeStructure.class)) {
      return;
    }
    if (paramClassInfo.isElement())
    {
      str1 = paramClassInfo.getElementName().getNamespaceURI();
      localObject1 = getNamespace(str1);
      ((Namespace)localObject1).classes.add(paramClassInfo);
      ((Namespace)localObject1).addDependencyTo(paramClassInfo.getTypeName());
      add(paramClassInfo.getElementName(), false, paramClassInfo);
    }
    Object localObject1 = paramClassInfo.getTypeName();
    if (localObject1 != null) {
      str1 = ((QName)localObject1).getNamespaceURI();
    } else if (str1 == null) {
      return;
    }
    Namespace localNamespace = getNamespace(str1);
    localNamespace.classes.add(paramClassInfo);
    Object localObject2 = paramClassInfo.getProperties().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      PropertyInfo localPropertyInfo = (PropertyInfo)((Iterator)localObject2).next();
      localNamespace.processForeignNamespaces(localPropertyInfo, 1);
      Object localObject4;
      if ((localPropertyInfo instanceof AttributePropertyInfo))
      {
        localObject3 = (AttributePropertyInfo)localPropertyInfo;
        localObject4 = ((AttributePropertyInfo)localObject3).getXmlName().getNamespaceURI();
        if (((String)localObject4).length() > 0)
        {
          getNamespace((String)localObject4).addGlobalAttribute((AttributePropertyInfo)localObject3);
          localNamespace.addDependencyTo(((AttributePropertyInfo)localObject3).getXmlName());
        }
      }
      if ((localPropertyInfo instanceof ElementPropertyInfo))
      {
        localObject3 = (ElementPropertyInfo)localPropertyInfo;
        localObject4 = ((ElementPropertyInfo)localObject3).getTypes().iterator();
        while (((Iterator)localObject4).hasNext())
        {
          TypeRef localTypeRef = (TypeRef)((Iterator)localObject4).next();
          String str2 = localTypeRef.getTagName().getNamespaceURI();
          if ((str2.length() > 0) && (!str2.equals(localNamespace.uri)))
          {
            getNamespace(str2).addGlobalElement(localTypeRef);
            localNamespace.addDependencyTo(localTypeRef.getTagName());
          }
        }
      }
      if (generateSwaRefAdapter(localPropertyInfo)) {
        localNamespace.useSwaRef = true;
      }
      Object localObject3 = localPropertyInfo.getExpectedMimeType();
      if (localObject3 != null) {
        localNamespace.useMimeNs = true;
      }
    }
    localObject2 = paramClassInfo.getBaseClass();
    if (localObject2 != null)
    {
      add((ClassInfo)localObject2);
      localNamespace.addDependencyTo(((ClassInfo)localObject2).getTypeName());
    }
  }
  
  public void add(ElementInfo<T, C> paramElementInfo)
  {
    assert (paramElementInfo != null);
    boolean bool = false;
    QName localQName = paramElementInfo.getElementName();
    Namespace localNamespace = getNamespace(localQName.getNamespaceURI());
    ElementInfo localElementInfo;
    if (paramElementInfo.getScope() != null) {
      localElementInfo = this.types.getElementInfo(paramElementInfo.getScope().getClazz(), localQName);
    } else {
      localElementInfo = this.types.getElementInfo(null, localQName);
    }
    XmlElement localXmlElement = (XmlElement)localElementInfo.getProperty().readAnnotation(XmlElement.class);
    if (localXmlElement == null) {
      bool = false;
    } else {
      bool = localXmlElement.nillable();
    }
    Namespace tmp137_135 = localNamespace;
    tmp137_135.getClass();
    localNamespace.elementDecls.put(localQName.getLocalPart(), new XmlSchemaGenerator.Namespace.ElementWithType(tmp137_135, bool, paramElementInfo.getContentType()));
    localNamespace.processForeignNamespaces(paramElementInfo.getProperty(), 1);
  }
  
  public void add(EnumLeafInfo<T, C> paramEnumLeafInfo)
  {
    assert (paramEnumLeafInfo != null);
    String str = null;
    if (paramEnumLeafInfo.isElement())
    {
      str = paramEnumLeafInfo.getElementName().getNamespaceURI();
      localObject = getNamespace(str);
      ((Namespace)localObject).enums.add(paramEnumLeafInfo);
      ((Namespace)localObject).addDependencyTo(paramEnumLeafInfo.getTypeName());
      add(paramEnumLeafInfo.getElementName(), false, paramEnumLeafInfo);
    }
    Object localObject = paramEnumLeafInfo.getTypeName();
    if (localObject != null) {
      str = ((QName)localObject).getNamespaceURI();
    } else if (str == null) {
      return;
    }
    Namespace localNamespace = getNamespace(str);
    localNamespace.enums.add(paramEnumLeafInfo);
    localNamespace.addDependencyTo(paramEnumLeafInfo.getBaseType().getTypeName());
  }
  
  public void add(ArrayInfo<T, C> paramArrayInfo)
  {
    assert (paramArrayInfo != null);
    String str = paramArrayInfo.getTypeName().getNamespaceURI();
    Namespace localNamespace = getNamespace(str);
    localNamespace.arrays.add(paramArrayInfo);
    localNamespace.addDependencyTo(paramArrayInfo.getItemType().getTypeName());
  }
  
  public void add(QName paramQName, boolean paramBoolean, NonElement<T, C> paramNonElement)
  {
    if ((paramNonElement != null) && (paramNonElement.getType() == this.navigator.ref(CompositeStructure.class))) {
      return;
    }
    Namespace localNamespace = getNamespace(paramQName.getNamespaceURI());
    Namespace tmp50_48 = localNamespace;
    tmp50_48.getClass();
    localNamespace.elementDecls.put(paramQName.getLocalPart(), new XmlSchemaGenerator.Namespace.ElementWithType(tmp50_48, paramBoolean, paramNonElement));
    if (paramNonElement != null) {
      localNamespace.addDependencyTo(paramNonElement.getTypeName());
    }
  }
  
  public void writeEpisodeFile(XmlSerializer paramXmlSerializer)
  {
    Bindings localBindings1 = (Bindings)TXW.create(Bindings.class, paramXmlSerializer);
    if (this.namespaces.containsKey("")) {
      localBindings1._namespace("http://java.sun.com/xml/ns/jaxb", "jaxb");
    }
    localBindings1.version("2.1");
    Iterator localIterator1 = this.namespaces.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      Bindings localBindings2 = localBindings1.bindings();
      String str2 = (String)localEntry.getKey();
      String str1;
      if (!str2.equals(""))
      {
        localBindings2._namespace(str2, "tns");
        str1 = "tns:";
      }
      else
      {
        str1 = "";
      }
      localBindings2.scd("x-schema::" + (str2.equals("") ? "" : "tns"));
      localBindings2.schemaBindings().map(false);
      Iterator localIterator2 = ((Namespace)localEntry.getValue()).classes.iterator();
      Object localObject;
      Bindings localBindings3;
      while (localIterator2.hasNext())
      {
        localObject = (ClassInfo)localIterator2.next();
        if (((ClassInfo)localObject).getTypeName() != null)
        {
          if (((ClassInfo)localObject).getTypeName().getNamespaceURI().equals(str2))
          {
            localBindings3 = localBindings2.bindings();
            localBindings3.scd('~' + str1 + ((ClassInfo)localObject).getTypeName().getLocalPart());
            localBindings3.klass().ref(((ClassInfo)localObject).getName());
          }
          if ((((ClassInfo)localObject).isElement()) && (((ClassInfo)localObject).getElementName().getNamespaceURI().equals(str2)))
          {
            localBindings3 = localBindings2.bindings();
            localBindings3.scd(str1 + ((ClassInfo)localObject).getElementName().getLocalPart());
            localBindings3.klass().ref(((ClassInfo)localObject).getName());
          }
        }
      }
      localIterator2 = ((Namespace)localEntry.getValue()).enums.iterator();
      while (localIterator2.hasNext())
      {
        localObject = (EnumLeafInfo)localIterator2.next();
        if (((EnumLeafInfo)localObject).getTypeName() != null)
        {
          localBindings3 = localBindings2.bindings();
          localBindings3.scd('~' + str1 + ((EnumLeafInfo)localObject).getTypeName().getLocalPart());
          localBindings3.klass().ref(this.navigator.getClassName(((EnumLeafInfo)localObject).getClazz()));
        }
      }
      localBindings2.commit(true);
    }
    localBindings1.commit();
  }
  
  public void write(SchemaOutputResolver paramSchemaOutputResolver, ErrorListener paramErrorListener)
    throws IOException
  {
    if (paramSchemaOutputResolver == null) {
      throw new IllegalArgumentException();
    }
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Wrigin XML Schema for " + toString(), new StackRecorder());
    }
    paramSchemaOutputResolver = new FoolProofResolver(paramSchemaOutputResolver);
    this.errorListener = paramErrorListener;
    Map localMap = this.types.getSchemaLocations();
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    this.namespaces.remove("http://www.w3.org/2001/XMLSchema");
    Iterator localIterator = this.namespaces.values().iterator();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    while (localIterator.hasNext())
    {
      localObject1 = (Namespace)localIterator.next();
      localObject2 = (String)localMap.get(((Namespace)localObject1).uri);
      if (localObject2 != null)
      {
        localHashMap2.put(localObject1, localObject2);
      }
      else
      {
        localObject3 = paramSchemaOutputResolver.createOutput(((Namespace)localObject1).uri, "schema" + (localHashMap1.size() + 1) + ".xsd");
        if (localObject3 != null)
        {
          localHashMap1.put(localObject1, localObject3);
          localHashMap2.put(localObject1, ((Result)localObject3).getSystemId());
        }
      }
    }
    localIterator = localHashMap1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      localObject2 = (Result)((Map.Entry)localObject1).getValue();
      ((Namespace)((Map.Entry)localObject1).getKey()).writeTo((Result)localObject2, localHashMap2);
      if ((localObject2 instanceof StreamResult))
      {
        localObject3 = ((StreamResult)localObject2).getOutputStream();
        if (localObject3 != null)
        {
          ((OutputStream)localObject3).close();
        }
        else
        {
          Writer localWriter = ((StreamResult)localObject2).getWriter();
          if (localWriter != null) {
            localWriter.close();
          }
        }
      }
    }
  }
  
  private boolean generateSwaRefAdapter(NonElementRef<T, C> paramNonElementRef)
  {
    return generateSwaRefAdapter(paramNonElementRef.getSource());
  }
  
  private boolean generateSwaRefAdapter(PropertyInfo<T, C> paramPropertyInfo)
  {
    Adapter localAdapter = paramPropertyInfo.getAdapter();
    if (localAdapter == null) {
      return false;
    }
    Object localObject = this.navigator.asDecl(SwaRefAdapter.class);
    if (localObject == null) {
      return false;
    }
    return localObject.equals(localAdapter.adapterType);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.namespaces.values().iterator();
    while (localIterator.hasNext())
    {
      Namespace localNamespace = (Namespace)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append(localNamespace.uri).append('=').append(localNamespace);
    }
    return super.toString() + '[' + localStringBuilder + ']';
  }
  
  private static String getProcessContentsModeName(WildcardMode paramWildcardMode)
  {
    switch (2.$SwitchMap$com$sun$xml$internal$bind$v2$model$core$WildcardMode[paramWildcardMode.ordinal()])
    {
    case 1: 
    case 2: 
      return paramWildcardMode.name().toLowerCase();
    case 3: 
      return null;
    }
    throw new IllegalStateException();
  }
  
  protected static String relativize(String paramString1, String paramString2)
  {
    try
    {
      assert (paramString1 != null);
      if (paramString2 == null) {
        return paramString1;
      }
      URI localURI1 = new URI(Util.escapeURI(paramString1));
      URI localURI2 = new URI(Util.escapeURI(paramString2));
      if ((localURI1.isOpaque()) || (localURI2.isOpaque())) {
        return paramString1;
      }
      if ((!Util.equalsIgnoreCase(localURI1.getScheme(), localURI2.getScheme())) || (!Util.equal(localURI1.getAuthority(), localURI2.getAuthority()))) {
        return paramString1;
      }
      String str1 = localURI1.getPath();
      String str2 = localURI2.getPath();
      if (!str2.endsWith("/")) {
        str2 = Util.normalizeUriPath(str2);
      }
      if (str1.equals(str2)) {
        return ".";
      }
      String str3 = calculateRelativePath(str1, str2, fixNull(localURI1.getScheme()).equals("file"));
      if (str3 == null) {
        return paramString1;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(str3);
      if (localURI1.getQuery() != null) {
        localStringBuilder.append('?').append(localURI1.getQuery());
      }
      if (localURI1.getFragment() != null) {
        localStringBuilder.append('#').append(localURI1.getFragment());
      }
      return localStringBuilder.toString();
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new InternalError("Error escaping one of these uris:\n\t" + paramString1 + "\n\t" + paramString2);
    }
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  private static String calculateRelativePath(String paramString1, String paramString2, boolean paramBoolean)
  {
    int i = File.pathSeparatorChar == ';' ? 1 : 0;
    if (paramString2 == null) {
      return null;
    }
    if (((paramBoolean) && (i != 0) && (startsWithIgnoreCase(paramString1, paramString2))) || (paramString1.startsWith(paramString2))) {
      return paramString1.substring(paramString2.length());
    }
    return "../" + calculateRelativePath(paramString1, Util.getParentUriPath(paramString2), paramBoolean);
  }
  
  private static boolean startsWithIgnoreCase(String paramString1, String paramString2)
  {
    return paramString1.toUpperCase().startsWith(paramString2.toUpperCase());
  }
  
  private class Namespace
  {
    @NotNull
    final String uri;
    private final Set<XmlSchemaGenerator<T, C, F, M>.Namespace> depends = new LinkedHashSet();
    private boolean selfReference;
    private final Set<ClassInfo<T, C>> classes = new LinkedHashSet();
    private final Set<EnumLeafInfo<T, C>> enums = new LinkedHashSet();
    private final Set<ArrayInfo<T, C>> arrays = new LinkedHashSet();
    private final MultiMap<String, AttributePropertyInfo<T, C>> attributeDecls = new MultiMap(null);
    private final MultiMap<String, XmlSchemaGenerator<T, C, F, M>.Namespace.ElementDeclaration> elementDecls = new MultiMap(new ElementWithType(true, XmlSchemaGenerator.this.anyType));
    private Form attributeFormDefault;
    private Form elementFormDefault;
    private boolean useSwaRef;
    private boolean useMimeNs;
    
    public Namespace(String paramString)
    {
      this.uri = paramString;
      assert (!XmlSchemaGenerator.this.namespaces.containsKey(paramString));
      XmlSchemaGenerator.this.namespaces.put(paramString, this);
    }
    
    private void processForeignNamespaces(PropertyInfo<T, C> paramPropertyInfo, int paramInt)
    {
      Iterator localIterator1 = paramPropertyInfo.ref().iterator();
      while (localIterator1.hasNext())
      {
        TypeInfo localTypeInfo = (TypeInfo)localIterator1.next();
        if (((localTypeInfo instanceof ClassInfo)) && (paramInt > 0))
        {
          java.util.List localList = ((ClassInfo)localTypeInfo).getProperties();
          Iterator localIterator2 = localList.iterator();
          while (localIterator2.hasNext())
          {
            PropertyInfo localPropertyInfo = (PropertyInfo)localIterator2.next();
            processForeignNamespaces(localPropertyInfo, --paramInt);
          }
        }
        if ((localTypeInfo instanceof Element)) {
          addDependencyTo(((Element)localTypeInfo).getElementName());
        }
        if ((localTypeInfo instanceof NonElement)) {
          addDependencyTo(((NonElement)localTypeInfo).getTypeName());
        }
      }
    }
    
    private void addDependencyTo(@Nullable QName paramQName)
    {
      if (paramQName == null) {
        return;
      }
      String str = paramQName.getNamespaceURI();
      if (str.equals("http://www.w3.org/2001/XMLSchema")) {
        return;
      }
      if (str.equals(this.uri))
      {
        this.selfReference = true;
        return;
      }
      this.depends.add(XmlSchemaGenerator.this.getNamespace(str));
    }
    
    private void writeTo(Result paramResult, Map<XmlSchemaGenerator<T, C, F, M>.Namespace, String> paramMap)
      throws IOException
    {
      try
      {
        Schema localSchema = (Schema)TXW.create(Schema.class, ResultFactory.createSerializer(paramResult));
        Map localMap = XmlSchemaGenerator.this.types.getXmlNs(this.uri);
        Iterator localIterator = localMap.entrySet().iterator();
        Object localObject1;
        while (localIterator.hasNext())
        {
          localObject1 = (Map.Entry)localIterator.next();
          localSchema._namespace((String)((Map.Entry)localObject1).getValue(), (String)((Map.Entry)localObject1).getKey());
        }
        if (this.useSwaRef) {
          localSchema._namespace("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef");
        }
        if (this.useMimeNs) {
          localSchema._namespace("http://www.w3.org/2005/05/xmlmime", "xmime");
        }
        this.attributeFormDefault = Form.get(XmlSchemaGenerator.this.types.getAttributeFormDefault(this.uri));
        this.attributeFormDefault.declare("attributeFormDefault", localSchema);
        this.elementFormDefault = Form.get(XmlSchemaGenerator.this.types.getElementFormDefault(this.uri));
        this.elementFormDefault.declare("elementFormDefault", localSchema);
        if ((!localMap.containsValue("http://www.w3.org/2001/XMLSchema")) && (!localMap.containsKey("xs"))) {
          localSchema._namespace("http://www.w3.org/2001/XMLSchema", "xs");
        }
        localSchema.version("1.0");
        if (this.uri.length() != 0) {
          localSchema.targetNamespace(this.uri);
        }
        localIterator = this.depends.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (Namespace)localIterator.next();
          localSchema._namespace(((Namespace)localObject1).uri);
        }
        if ((this.selfReference) && (this.uri.length() != 0)) {
          localSchema._namespace(this.uri, "tns");
        }
        localSchema._pcdata("\n");
        localIterator = this.depends.iterator();
        Object localObject2;
        while (localIterator.hasNext())
        {
          localObject1 = (Namespace)localIterator.next();
          localObject2 = localSchema._import();
          if (((Namespace)localObject1).uri.length() != 0) {
            ((Import)localObject2).namespace(((Namespace)localObject1).uri);
          }
          String str = (String)paramMap.get(localObject1);
          if ((str != null) && (!str.equals(""))) {
            ((Import)localObject2).schemaLocation(XmlSchemaGenerator.relativize(str, paramResult.getSystemId()));
          }
          localSchema._pcdata("\n");
        }
        if (this.useSwaRef) {
          localSchema._import().namespace("http://ws-i.org/profiles/basic/1.1/xsd").schemaLocation("http://ws-i.org/profiles/basic/1.1/swaref.xsd");
        }
        if (this.useMimeNs) {
          localSchema._import().namespace("http://www.w3.org/2005/05/xmlmime").schemaLocation("http://www.w3.org/2005/05/xmlmime");
        }
        localIterator = this.elementDecls.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (Map.Entry)localIterator.next();
          ((ElementDeclaration)((Map.Entry)localObject1).getValue()).writeTo((String)((Map.Entry)localObject1).getKey(), localSchema);
          localSchema._pcdata("\n");
        }
        localIterator = this.classes.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (ClassInfo)localIterator.next();
          if (((ClassInfo)localObject1).getTypeName() != null)
          {
            if (this.uri.equals(((ClassInfo)localObject1).getTypeName().getNamespaceURI())) {
              writeClass((ClassInfo)localObject1, localSchema);
            }
            localSchema._pcdata("\n");
          }
        }
        localIterator = this.enums.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (EnumLeafInfo)localIterator.next();
          if (((EnumLeafInfo)localObject1).getTypeName() != null)
          {
            if (this.uri.equals(((EnumLeafInfo)localObject1).getTypeName().getNamespaceURI())) {
              writeEnum((EnumLeafInfo)localObject1, localSchema);
            }
            localSchema._pcdata("\n");
          }
        }
        localIterator = this.arrays.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (ArrayInfo)localIterator.next();
          writeArray((ArrayInfo)localObject1, localSchema);
          localSchema._pcdata("\n");
        }
        localIterator = this.attributeDecls.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (Map.Entry)localIterator.next();
          localObject2 = localSchema.attribute();
          ((TopLevelAttribute)localObject2).name((String)((Map.Entry)localObject1).getKey());
          if (((Map.Entry)localObject1).getValue() == null) {
            writeTypeRef((TypeHost)localObject2, XmlSchemaGenerator.this.stringType, "type");
          } else {
            writeAttributeTypeRef((AttributePropertyInfo)((Map.Entry)localObject1).getValue(), (AttributeType)localObject2);
          }
          localSchema._pcdata("\n");
        }
        localSchema.commit();
      }
      catch (TxwException localTxwException)
      {
        XmlSchemaGenerator.logger.log(Level.INFO, localTxwException.getMessage(), localTxwException);
        throw new IOException(localTxwException.getMessage());
      }
    }
    
    private void writeTypeRef(TypeHost paramTypeHost, NonElementRef<T, C> paramNonElementRef, String paramString)
    {
      switch (XmlSchemaGenerator.2.$SwitchMap$com$sun$xml$internal$bind$v2$model$core$ID[paramNonElementRef.getSource().id().ordinal()])
      {
      case 1: 
        paramTypeHost._attribute(paramString, new QName("http://www.w3.org/2001/XMLSchema", "ID"));
        return;
      case 2: 
        paramTypeHost._attribute(paramString, new QName("http://www.w3.org/2001/XMLSchema", "IDREF"));
        return;
      case 3: 
        break;
      default: 
        throw new IllegalStateException();
      }
      MimeType localMimeType = paramNonElementRef.getSource().getExpectedMimeType();
      if (localMimeType != null) {
        paramTypeHost._attribute(new QName("http://www.w3.org/2005/05/xmlmime", "expectedContentTypes", "xmime"), localMimeType.toString());
      }
      if (XmlSchemaGenerator.this.generateSwaRefAdapter(paramNonElementRef))
      {
        paramTypeHost._attribute(paramString, new QName("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef", "ref"));
        return;
      }
      if (paramNonElementRef.getSource().getSchemaType() != null)
      {
        paramTypeHost._attribute(paramString, paramNonElementRef.getSource().getSchemaType());
        return;
      }
      writeTypeRef(paramTypeHost, paramNonElementRef.getTarget(), paramString);
    }
    
    private void writeTypeRef(TypeHost paramTypeHost, NonElement<T, C> paramNonElement, String paramString)
    {
      Element localElement = null;
      if ((paramNonElement instanceof MaybeElement))
      {
        MaybeElement localMaybeElement = (MaybeElement)paramNonElement;
        boolean bool = localMaybeElement.isElement();
        if (bool) {
          localElement = localMaybeElement.asElement();
        }
      }
      if ((paramNonElement instanceof Element)) {
        localElement = (Element)paramNonElement;
      }
      if (paramNonElement.getTypeName() == null)
      {
        if ((localElement != null) && (localElement.getElementName() != null))
        {
          paramTypeHost.block();
          if ((paramNonElement instanceof ClassInfo)) {
            writeClass((ClassInfo)paramNonElement, paramTypeHost);
          } else {
            writeEnum((EnumLeafInfo)paramNonElement, (SimpleTypeHost)paramTypeHost);
          }
        }
        else
        {
          paramTypeHost.block();
          if ((paramNonElement instanceof ClassInfo))
          {
            if (XmlSchemaGenerator.this.collisionChecker.push((ClassInfo)paramNonElement)) {
              XmlSchemaGenerator.this.errorListener.warning(new SAXParseException(Messages.ANONYMOUS_TYPE_CYCLE.format(new Object[] { XmlSchemaGenerator.this.collisionChecker.getCycleString() }), null));
            } else {
              writeClass((ClassInfo)paramNonElement, paramTypeHost);
            }
            XmlSchemaGenerator.this.collisionChecker.pop();
          }
          else
          {
            writeEnum((EnumLeafInfo)paramNonElement, (SimpleTypeHost)paramTypeHost);
          }
        }
      }
      else {
        paramTypeHost._attribute(paramString, paramNonElement.getTypeName());
      }
    }
    
    private void writeArray(ArrayInfo<T, C> paramArrayInfo, Schema paramSchema)
    {
      ComplexType localComplexType = paramSchema.complexType().name(paramArrayInfo.getTypeName().getLocalPart());
      localComplexType._final("#all");
      LocalElement localLocalElement = localComplexType.sequence().element().name("item");
      localLocalElement.type(paramArrayInfo.getItemType().getTypeName());
      localLocalElement.minOccurs(0).maxOccurs("unbounded");
      localLocalElement.nillable(true);
      localComplexType.commit();
    }
    
    private void writeEnum(EnumLeafInfo<T, C> paramEnumLeafInfo, SimpleTypeHost paramSimpleTypeHost)
    {
      SimpleType localSimpleType = paramSimpleTypeHost.simpleType();
      writeName(paramEnumLeafInfo, localSimpleType);
      SimpleRestriction localSimpleRestriction = localSimpleType.restriction();
      writeTypeRef(localSimpleRestriction, paramEnumLeafInfo.getBaseType(), "base");
      Iterator localIterator = paramEnumLeafInfo.getConstants().iterator();
      while (localIterator.hasNext())
      {
        EnumConstant localEnumConstant = (EnumConstant)localIterator.next();
        localSimpleRestriction.enumeration().value(localEnumConstant.getLexicalValue());
      }
      localSimpleType.commit();
    }
    
    private void writeClass(ClassInfo<T, C> paramClassInfo, TypeHost paramTypeHost)
    {
      if (containsValueProp(paramClassInfo))
      {
        if (paramClassInfo.getProperties().size() == 1)
        {
          localObject1 = (ValuePropertyInfo)paramClassInfo.getProperties().get(0);
          localObject2 = ((SimpleTypeHost)paramTypeHost).simpleType();
          writeName(paramClassInfo, (TypedXmlWriter)localObject2);
          if (((ValuePropertyInfo)localObject1).isCollection()) {
            writeTypeRef(((SimpleType)localObject2).list(), ((ValuePropertyInfo)localObject1).getTarget(), "itemType");
          } else {
            writeTypeRef(((SimpleType)localObject2).restriction(), ((ValuePropertyInfo)localObject1).getTarget(), "base");
          }
          return;
        }
        localObject1 = ((ComplexTypeHost)paramTypeHost).complexType();
        writeName(paramClassInfo, (TypedXmlWriter)localObject1);
        if (paramClassInfo.isFinal()) {
          ((ComplexType)localObject1)._final("extension restriction");
        }
        localObject2 = ((ComplexType)localObject1).simpleContent().extension();
        ((SimpleExtension)localObject2).block();
        localObject3 = paramClassInfo.getProperties().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (PropertyInfo)((Iterator)localObject3).next();
          switch (XmlSchemaGenerator.2.$SwitchMap$com$sun$xml$internal$bind$v2$model$core$PropertyKind[localObject4.kind().ordinal()])
          {
          case 1: 
            handleAttributeProp((AttributePropertyInfo)localObject4, (AttrDecls)localObject2);
            break;
          case 2: 
            TODO.checkSpec("what if vp.isCollection() == true?");
            localObject5 = (ValuePropertyInfo)localObject4;
            ((SimpleExtension)localObject2).base(((ValuePropertyInfo)localObject5).getTarget().getTypeName());
            break;
          case 3: 
          case 4: 
          default: 
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
            throw new IllegalStateException();
          }
        }
        ((SimpleExtension)localObject2).commit();
        TODO.schemaGenerator("figure out what to do if bc != null");
        TODO.checkSpec("handle sec 8.9.5.2, bullet #4");
        return;
      }
      Object localObject1 = ((ComplexTypeHost)paramTypeHost).complexType();
      writeName(paramClassInfo, (TypedXmlWriter)localObject1);
      if (paramClassInfo.isFinal()) {
        ((ComplexType)localObject1)._final("extension restriction");
      }
      if (paramClassInfo.isAbstract()) {
        ((ComplexType)localObject1)._abstract(true);
      }
      Object localObject2 = localObject1;
      Object localObject3 = localObject1;
      Object localObject4 = paramClassInfo.getBaseClass();
      if (localObject4 != null) {
        if (((ClassInfo)localObject4).hasValueProperty())
        {
          localObject5 = ((ComplexType)localObject1).simpleContent().extension();
          localObject2 = localObject5;
          localObject3 = null;
          ((SimpleExtension)localObject5).base(((ClassInfo)localObject4).getTypeName());
        }
        else
        {
          localObject5 = ((ComplexType)localObject1).complexContent().extension();
          localObject2 = localObject5;
          localObject3 = localObject5;
          ((ComplexExtension)localObject5).base(((ClassInfo)localObject4).getTypeName());
        }
      }
      Object localObject6;
      if (localObject3 != null)
      {
        localObject5 = new ArrayList();
        localObject6 = paramClassInfo.getProperties().iterator();
        while (((Iterator)localObject6).hasNext())
        {
          PropertyInfo localPropertyInfo = (PropertyInfo)((Iterator)localObject6).next();
          if (((localPropertyInfo instanceof ReferencePropertyInfo)) && (((ReferencePropertyInfo)localPropertyInfo).isMixed())) {
            ((ComplexType)localObject1).mixed(true);
          }
          Tree localTree = buildPropertyContentModel(localPropertyInfo);
          if (localTree != null) {
            ((ArrayList)localObject5).add(localTree);
          }
        }
        localObject6 = Tree.makeGroup(paramClassInfo.isOrdered() ? GroupKind.SEQUENCE : GroupKind.ALL, (java.util.List)localObject5);
        ((Tree)localObject6).write((TypeDefParticle)localObject3);
      }
      Object localObject5 = paramClassInfo.getProperties().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (PropertyInfo)((Iterator)localObject5).next();
        if ((localObject6 instanceof AttributePropertyInfo)) {
          handleAttributeProp((AttributePropertyInfo)localObject6, (AttrDecls)localObject2);
        }
      }
      if (paramClassInfo.hasAttributeWildcard()) {
        ((AttrDecls)localObject2).anyAttribute().namespace("##other").processContents("skip");
      }
      ((ComplexType)localObject1).commit();
    }
    
    private void writeName(NonElement<T, C> paramNonElement, TypedXmlWriter paramTypedXmlWriter)
    {
      QName localQName = paramNonElement.getTypeName();
      if (localQName != null) {
        paramTypedXmlWriter._attribute("name", localQName.getLocalPart());
      }
    }
    
    private boolean containsValueProp(ClassInfo<T, C> paramClassInfo)
    {
      Iterator localIterator = paramClassInfo.getProperties().iterator();
      while (localIterator.hasNext())
      {
        PropertyInfo localPropertyInfo = (PropertyInfo)localIterator.next();
        if ((localPropertyInfo instanceof ValuePropertyInfo)) {
          return true;
        }
      }
      return false;
    }
    
    private Tree buildPropertyContentModel(PropertyInfo<T, C> paramPropertyInfo)
    {
      switch (XmlSchemaGenerator.2.$SwitchMap$com$sun$xml$internal$bind$v2$model$core$PropertyKind[paramPropertyInfo.kind().ordinal()])
      {
      case 3: 
        return handleElementProp((ElementPropertyInfo)paramPropertyInfo);
      case 1: 
        return null;
      case 4: 
        return handleReferenceProp((ReferencePropertyInfo)paramPropertyInfo);
      case 5: 
        return handleMapProp((MapPropertyInfo)paramPropertyInfo);
      case 2: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        throw new IllegalStateException();
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      throw new IllegalStateException();
    }
    
    private Tree handleElementProp(final ElementPropertyInfo<T, C> paramElementPropertyInfo)
    {
      if (paramElementPropertyInfo.isValueList()) {
        new Tree.Term()
        {
          protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
          {
            TypeRef localTypeRef = (TypeRef)paramElementPropertyInfo.getTypes().get(0);
            LocalElement localLocalElement = paramAnonymousContentModelContainer.element();
            localLocalElement.block();
            QName localQName = localTypeRef.getTagName();
            localLocalElement.name(localQName.getLocalPart());
            com.sun.xml.internal.bind.v2.schemagen.xmlschema.List localList = localLocalElement.simpleType().list();
            XmlSchemaGenerator.Namespace.this.writeTypeRef(localList, localTypeRef, "itemType");
            XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localLocalElement, localQName);
            writeOccurs(localLocalElement, (paramAnonymousBoolean1) || (!paramElementPropertyInfo.isRequired()), paramAnonymousBoolean2);
          }
        };
      }
      ArrayList localArrayList = new ArrayList();
      Object localObject1 = paramElementPropertyInfo.getTypes().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (TypeRef)((Iterator)localObject1).next();
        localArrayList.add(new Tree.Term()
        {
          protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
          {
            LocalElement localLocalElement = paramAnonymousContentModelContainer.element();
            QName localQName1 = this.val$t.getTagName();
            PropertyInfo localPropertyInfo = this.val$t.getSource();
            TypeInfo localTypeInfo1 = localPropertyInfo == null ? null : localPropertyInfo.parent();
            if (XmlSchemaGenerator.Namespace.this.canBeDirectElementRef(this.val$t, localQName1, localTypeInfo1))
            {
              if ((!this.val$t.getTarget().isSimpleType()) && ((this.val$t.getTarget() instanceof ClassInfo)) && (XmlSchemaGenerator.this.collisionChecker.findDuplicate((ClassInfo)this.val$t.getTarget())))
              {
                localLocalElement.ref(new QName(XmlSchemaGenerator.Namespace.this.uri, localQName1.getLocalPart()));
              }
              else
              {
                QName localQName2 = null;
                if ((this.val$t.getTarget() instanceof Element))
                {
                  localObject = (Element)this.val$t.getTarget();
                  localQName2 = ((Element)localObject).getElementName();
                }
                Object localObject = localPropertyInfo.ref();
                TypeInfo localTypeInfo2;
                if ((localObject != null) && (!((Collection)localObject).isEmpty()) && (localQName2 != null) && (((localTypeInfo2 = (TypeInfo)((Collection)localObject).iterator().next()) == null) || ((localTypeInfo2 instanceof ClassInfoImpl))))
                {
                  ClassInfoImpl localClassInfoImpl = (ClassInfoImpl)localTypeInfo2;
                  if ((localClassInfoImpl != null) && (localClassInfoImpl.getElementName() != null)) {
                    localLocalElement.ref(new QName(localClassInfoImpl.getElementName().getNamespaceURI(), localQName1.getLocalPart()));
                  } else {
                    localLocalElement.ref(new QName("", localQName1.getLocalPart()));
                  }
                }
                else
                {
                  localLocalElement.ref(localQName1);
                }
              }
            }
            else
            {
              localLocalElement.name(localQName1.getLocalPart());
              XmlSchemaGenerator.Namespace.this.writeTypeRef(localLocalElement, this.val$t, "type");
              XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localLocalElement, localQName1);
            }
            if (this.val$t.isNillable()) {
              localLocalElement.nillable(true);
            }
            if (this.val$t.getDefaultValue() != null) {
              localLocalElement._default(this.val$t.getDefaultValue());
            }
            writeOccurs(localLocalElement, paramAnonymousBoolean1, paramAnonymousBoolean2);
          }
        });
      }
      localObject1 = Tree.makeGroup(GroupKind.CHOICE, localArrayList).makeOptional(!paramElementPropertyInfo.isRequired()).makeRepeated(paramElementPropertyInfo.isCollection());
      Object localObject2 = paramElementPropertyInfo.getXmlName();
      if (localObject2 != null) {
        new Tree.Term()
        {
          protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
          {
            LocalElement localLocalElement = paramAnonymousContentModelContainer.element();
            if ((this.val$ename.getNamespaceURI().length() > 0) && (!this.val$ename.getNamespaceURI().equals(XmlSchemaGenerator.Namespace.this.uri)))
            {
              localLocalElement.ref(new QName(this.val$ename.getNamespaceURI(), this.val$ename.getLocalPart()));
              return;
            }
            localLocalElement.name(this.val$ename.getLocalPart());
            XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localLocalElement, this.val$ename);
            if (paramElementPropertyInfo.isCollectionNillable()) {
              localLocalElement.nillable(true);
            }
            writeOccurs(localLocalElement, !paramElementPropertyInfo.isCollectionRequired(), paramAnonymousBoolean2);
            ComplexType localComplexType = localLocalElement.complexType();
            this.val$choice.write(localComplexType);
          }
        };
      }
      return localObject1;
    }
    
    private boolean canBeDirectElementRef(TypeRef<T, C> paramTypeRef, QName paramQName, TypeInfo paramTypeInfo)
    {
      Element localElement = null;
      ClassInfo localClassInfo = null;
      QName localQName = null;
      if ((paramTypeRef.isNillable()) || (paramTypeRef.getDefaultValue() != null)) {
        return false;
      }
      if ((paramTypeRef.getTarget() instanceof Element))
      {
        localElement = (Element)paramTypeRef.getTarget();
        localQName = localElement.getElementName();
        if ((localElement instanceof ClassInfo)) {
          localClassInfo = (ClassInfo)localElement;
        }
      }
      String str = paramQName.getNamespaceURI();
      if ((!str.equals(this.uri)) && (str.length() > 0) && ((!(paramTypeInfo instanceof ClassInfo)) || (((ClassInfo)paramTypeInfo).getTypeName() != null))) {
        return true;
      }
      if ((localClassInfo != null) && (localQName != null) && (localElement.getScope() == null) && (localQName.getNamespaceURI() == null) && (localQName.equals(paramQName))) {
        return true;
      }
      if (localElement != null) {
        return (localQName != null) && (localQName.equals(paramQName));
      }
      return false;
    }
    
    private void handleAttributeProp(AttributePropertyInfo<T, C> paramAttributePropertyInfo, AttrDecls paramAttrDecls)
    {
      LocalAttribute localLocalAttribute = paramAttrDecls.attribute();
      String str = paramAttributePropertyInfo.getXmlName().getNamespaceURI();
      if (str.equals(""))
      {
        localLocalAttribute.name(paramAttributePropertyInfo.getXmlName().getLocalPart());
        writeAttributeTypeRef(paramAttributePropertyInfo, localLocalAttribute);
        this.attributeFormDefault.writeForm(localLocalAttribute, paramAttributePropertyInfo.getXmlName());
      }
      else
      {
        localLocalAttribute.ref(paramAttributePropertyInfo.getXmlName());
      }
      if (paramAttributePropertyInfo.isRequired()) {
        localLocalAttribute.use("required");
      }
    }
    
    private void writeAttributeTypeRef(AttributePropertyInfo<T, C> paramAttributePropertyInfo, AttributeType paramAttributeType)
    {
      if (paramAttributePropertyInfo.isCollection()) {
        writeTypeRef(paramAttributeType.simpleType().list(), paramAttributePropertyInfo, "itemType");
      } else {
        writeTypeRef(paramAttributeType, paramAttributePropertyInfo, "type");
      }
    }
    
    private Tree handleReferenceProp(final ReferencePropertyInfo<T, C> paramReferencePropertyInfo)
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1 = paramReferencePropertyInfo.getElements().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Element)((Iterator)localObject1).next();
        localArrayList.add(new Tree.Term()
        {
          protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
          {
            LocalElement localLocalElement = paramAnonymousContentModelContainer.element();
            int i = 0;
            QName localQName = this.val$e.getElementName();
            if (this.val$e.getScope() != null)
            {
              boolean bool1 = localQName.getNamespaceURI().equals(XmlSchemaGenerator.Namespace.this.uri);
              boolean bool2 = localQName.getNamespaceURI().equals("");
              if ((bool1) || (bool2))
              {
                if (bool2)
                {
                  if (XmlSchemaGenerator.Namespace.this.elementFormDefault.isEffectivelyQualified) {
                    localLocalElement.form("unqualified");
                  }
                }
                else if (!XmlSchemaGenerator.Namespace.this.elementFormDefault.isEffectivelyQualified) {
                  localLocalElement.form("qualified");
                }
                i = 1;
                localLocalElement.name(localQName.getLocalPart());
                if ((this.val$e instanceof ClassInfo)) {
                  XmlSchemaGenerator.Namespace.this.writeTypeRef(localLocalElement, (ClassInfo)this.val$e, "type");
                } else {
                  XmlSchemaGenerator.Namespace.this.writeTypeRef(localLocalElement, ((ElementInfo)this.val$e).getContentType(), "type");
                }
              }
            }
            if (i == 0) {
              localLocalElement.ref(localQName);
            }
            writeOccurs(localLocalElement, paramAnonymousBoolean1, paramAnonymousBoolean2);
          }
        });
      }
      localObject1 = paramReferencePropertyInfo.getWildcard();
      if (localObject1 != null) {
        localArrayList.add(new Tree.Term()
        {
          protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
          {
            Any localAny = paramAnonymousContentModelContainer.any();
            String str = XmlSchemaGenerator.getProcessContentsModeName(this.val$wc);
            if (str != null) {
              localAny.processContents(str);
            }
            localAny.namespace("##other");
            writeOccurs(localAny, paramAnonymousBoolean1, paramAnonymousBoolean2);
          }
        });
      }
      Object localObject2 = Tree.makeGroup(GroupKind.CHOICE, localArrayList).makeRepeated(paramReferencePropertyInfo.isCollection()).makeOptional(!paramReferencePropertyInfo.isRequired());
      final QName localQName = paramReferencePropertyInfo.getXmlName();
      if (localQName != null) {
        new Tree.Term()
        {
          protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
          {
            LocalElement localLocalElement = paramAnonymousContentModelContainer.element().name(localQName.getLocalPart());
            XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localLocalElement, localQName);
            if (paramReferencePropertyInfo.isCollectionNillable()) {
              localLocalElement.nillable(true);
            }
            writeOccurs(localLocalElement, true, paramAnonymousBoolean2);
            ComplexType localComplexType = localLocalElement.complexType();
            this.val$choice.write(localComplexType);
          }
        };
      }
      return localObject2;
    }
    
    private Tree handleMapProp(final MapPropertyInfo<T, C> paramMapPropertyInfo)
    {
      new Tree.Term()
      {
        protected void write(ContentModelContainer paramAnonymousContentModelContainer, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
        {
          QName localQName = paramMapPropertyInfo.getXmlName();
          LocalElement localLocalElement = paramAnonymousContentModelContainer.element();
          XmlSchemaGenerator.Namespace.this.elementFormDefault.writeForm(localLocalElement, localQName);
          if (paramMapPropertyInfo.isCollectionNillable()) {
            localLocalElement.nillable(true);
          }
          localLocalElement = localLocalElement.name(localQName.getLocalPart());
          writeOccurs(localLocalElement, paramAnonymousBoolean1, paramAnonymousBoolean2);
          ComplexType localComplexType = localLocalElement.complexType();
          localLocalElement = localComplexType.sequence().element();
          localLocalElement.name("entry").minOccurs(0).maxOccurs("unbounded");
          ExplicitGroup localExplicitGroup = localLocalElement.complexType().sequence();
          XmlSchemaGenerator.Namespace.this.writeKeyOrValue(localExplicitGroup, "key", paramMapPropertyInfo.getKeyType());
          XmlSchemaGenerator.Namespace.this.writeKeyOrValue(localExplicitGroup, "value", paramMapPropertyInfo.getValueType());
        }
      };
    }
    
    private void writeKeyOrValue(ExplicitGroup paramExplicitGroup, String paramString, NonElement<T, C> paramNonElement)
    {
      LocalElement localLocalElement = paramExplicitGroup.element().name(paramString);
      localLocalElement.minOccurs(0);
      writeTypeRef(localLocalElement, paramNonElement, "type");
    }
    
    public void addGlobalAttribute(AttributePropertyInfo<T, C> paramAttributePropertyInfo)
    {
      this.attributeDecls.put(paramAttributePropertyInfo.getXmlName().getLocalPart(), paramAttributePropertyInfo);
      addDependencyTo(paramAttributePropertyInfo.getTarget().getTypeName());
    }
    
    public void addGlobalElement(TypeRef<T, C> paramTypeRef)
    {
      this.elementDecls.put(paramTypeRef.getTagName().getLocalPart(), new ElementWithType(false, paramTypeRef.getTarget()));
      addDependencyTo(paramTypeRef.getTarget().getTypeName());
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[classes=").append(this.classes);
      localStringBuilder.append(",elementDecls=").append(this.elementDecls);
      localStringBuilder.append(",enums=").append(this.enums);
      localStringBuilder.append("]");
      return super.toString();
    }
    
    abstract class ElementDeclaration
    {
      ElementDeclaration() {}
      
      public abstract boolean equals(Object paramObject);
      
      public abstract int hashCode();
      
      public abstract void writeTo(String paramString, Schema paramSchema);
    }
    
    class ElementWithType
      extends XmlSchemaGenerator<T, C, F, M>.Namespace.ElementDeclaration
    {
      private final boolean nillable;
      private final NonElement<T, C> type;
      
      public ElementWithType(NonElement<T, C> paramNonElement)
      {
        super();
        Object localObject;
        this.type = localObject;
        this.nillable = paramNonElement;
      }
      
      public void writeTo(String paramString, Schema paramSchema)
      {
        TopLevelElement localTopLevelElement = paramSchema.element().name(paramString);
        if (this.nillable) {
          localTopLevelElement.nillable(true);
        }
        if (this.type != null) {
          XmlSchemaGenerator.Namespace.this.writeTypeRef(localTopLevelElement, this.type, "type");
        } else {
          localTopLevelElement.complexType();
        }
        localTopLevelElement.commit();
      }
      
      public boolean equals(Object paramObject)
      {
        if (this == paramObject) {
          return true;
        }
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        ElementWithType localElementWithType = (ElementWithType)paramObject;
        return this.type.equals(localElementWithType.type);
      }
      
      public int hashCode()
      {
        return this.type.hashCode();
      }
    }
  }
}
