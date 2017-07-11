package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.io.InvalidObjectException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataInvocationHandler;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultMXBeanMappingFactory
  extends MXBeanMappingFactory
{
  private static final Mappings mappings;
  private static final List<MXBeanMapping> permanentMappings;
  private static final String[] keyArray = { "key" };
  private static final String[] keyValueArray = { "key", "value" };
  private static final Map<Type, Type> inProgress = Util.newIdentityHashMap();
  
  public DefaultMXBeanMappingFactory() {}
  
  static boolean isIdentity(MXBeanMapping paramMXBeanMapping)
  {
    return ((paramMXBeanMapping instanceof NonNullMXBeanMapping)) && (((NonNullMXBeanMapping)paramMXBeanMapping).isIdentity());
  }
  
  private static synchronized MXBeanMapping getMapping(Type paramType)
  {
    WeakReference localWeakReference = (WeakReference)mappings.get(paramType);
    return localWeakReference == null ? null : (MXBeanMapping)localWeakReference.get();
  }
  
  private static synchronized void putMapping(Type paramType, MXBeanMapping paramMXBeanMapping)
  {
    WeakReference localWeakReference = new WeakReference(paramMXBeanMapping);
    mappings.put(paramType, localWeakReference);
  }
  
  private static synchronized void putPermanentMapping(Type paramType, MXBeanMapping paramMXBeanMapping)
  {
    putMapping(paramType, paramMXBeanMapping);
    permanentMappings.add(paramMXBeanMapping);
  }
  
  public synchronized MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException
  {
    if (inProgress.containsKey(paramType)) {
      throw new OpenDataException("Recursive data structure, including " + MXBeanIntrospector.typeName(paramType));
    }
    MXBeanMapping localMXBeanMapping = getMapping(paramType);
    if (localMXBeanMapping != null) {
      return localMXBeanMapping;
    }
    inProgress.put(paramType, paramType);
    try
    {
      localMXBeanMapping = makeMapping(paramType, paramMXBeanMappingFactory);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw openDataException("Cannot convert type: " + MXBeanIntrospector.typeName(paramType), localOpenDataException);
    }
    finally
    {
      inProgress.remove(paramType);
    }
    putMapping(paramType, localMXBeanMapping);
    return localMXBeanMapping;
  }
  
  private MXBeanMapping makeMapping(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException
  {
    Object localObject;
    if ((paramType instanceof GenericArrayType))
    {
      localObject = ((GenericArrayType)paramType).getGenericComponentType();
      return makeArrayOrCollectionMapping(paramType, (Type)localObject, paramMXBeanMappingFactory);
    }
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      if (((Class)localObject).isEnum()) {
        return makeEnumMapping((Class)localObject, ElementType.class);
      }
      if (((Class)localObject).isArray())
      {
        Class localClass = ((Class)localObject).getComponentType();
        return makeArrayOrCollectionMapping((Type)localObject, localClass, paramMXBeanMappingFactory);
      }
      if (JMX.isMXBeanInterface((Class)localObject)) {
        return makeMXBeanRefMapping((Type)localObject);
      }
      return makeCompositeMapping((Class)localObject, paramMXBeanMappingFactory);
    }
    if ((paramType instanceof ParameterizedType)) {
      return makeParameterizedTypeMapping((ParameterizedType)paramType, paramMXBeanMappingFactory);
    }
    throw new OpenDataException("Cannot map type: " + paramType);
  }
  
  private static <T extends Enum<T>> MXBeanMapping makeEnumMapping(Class<?> paramClass, Class<T> paramClass1)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return new EnumMapping((Class)Util.cast(paramClass));
  }
  
  private MXBeanMapping makeArrayOrCollectionMapping(Type paramType1, Type paramType2, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException
  {
    MXBeanMapping localMXBeanMapping = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
    OpenType localOpenType = localMXBeanMapping.getOpenType();
    ArrayType localArrayType = ArrayType.getArrayType(localOpenType);
    Class localClass1 = localMXBeanMapping.getOpenClass();
    String str;
    if (localClass1.isArray()) {
      str = "[" + localClass1.getName();
    } else {
      str = "[L" + localClass1.getName() + ";";
    }
    Class localClass2;
    try
    {
      localClass2 = Class.forName(str);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw openDataException("Cannot obtain array class", localClassNotFoundException);
    }
    if ((paramType1 instanceof ParameterizedType)) {
      return new CollectionMapping(paramType1, localArrayType, localClass2, localMXBeanMapping);
    }
    if (isIdentity(localMXBeanMapping)) {
      return new IdentityMapping(paramType1, localArrayType);
    }
    return new ArrayMapping(paramType1, localArrayType, localClass2, localMXBeanMapping);
  }
  
  private MXBeanMapping makeTabularMapping(Type paramType1, boolean paramBoolean, Type paramType2, Type paramType3, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException
  {
    String str = MXBeanIntrospector.typeName(paramType1);
    MXBeanMapping localMXBeanMapping1 = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
    MXBeanMapping localMXBeanMapping2 = paramMXBeanMappingFactory.mappingForType(paramType3, paramMXBeanMappingFactory);
    OpenType localOpenType1 = localMXBeanMapping1.getOpenType();
    OpenType localOpenType2 = localMXBeanMapping2.getOpenType();
    CompositeType localCompositeType = new CompositeType(str, str, keyValueArray, keyValueArray, new OpenType[] { localOpenType1, localOpenType2 });
    TabularType localTabularType = new TabularType(str, str, localCompositeType, keyArray);
    return new TabularMapping(paramType1, paramBoolean, localTabularType, localMXBeanMapping1, localMXBeanMapping2);
  }
  
  private MXBeanMapping makeParameterizedTypeMapping(ParameterizedType paramParameterizedType, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException
  {
    Type localType = paramParameterizedType.getRawType();
    if ((localType instanceof Class))
    {
      Class localClass = (Class)localType;
      if ((localClass == List.class) || (localClass == Set.class) || (localClass == SortedSet.class))
      {
        Type[] arrayOfType1 = paramParameterizedType.getActualTypeArguments();
        assert (arrayOfType1.length == 1);
        if (localClass == SortedSet.class) {
          mustBeComparable(localClass, arrayOfType1[0]);
        }
        return makeArrayOrCollectionMapping(paramParameterizedType, arrayOfType1[0], paramMXBeanMappingFactory);
      }
      boolean bool = localClass == SortedMap.class;
      if ((localClass == Map.class) || (bool))
      {
        Type[] arrayOfType2 = paramParameterizedType.getActualTypeArguments();
        assert (arrayOfType2.length == 2);
        if (bool) {
          mustBeComparable(localClass, arrayOfType2[0]);
        }
        return makeTabularMapping(paramParameterizedType, bool, arrayOfType2[0], arrayOfType2[1], paramMXBeanMappingFactory);
      }
    }
    throw new OpenDataException("Cannot convert type: " + paramParameterizedType);
  }
  
  private static MXBeanMapping makeMXBeanRefMapping(Type paramType)
    throws OpenDataException
  {
    return new MXBeanRefMapping(paramType);
  }
  
  private MXBeanMapping makeCompositeMapping(Class<?> paramClass, MXBeanMappingFactory paramMXBeanMappingFactory)
    throws OpenDataException
  {
    int i = (paramClass.getName().equals("com.sun.management.GcInfo")) && (paramClass.getClassLoader() == null) ? 1 : 0;
    ReflectUtil.checkPackageAccess(paramClass);
    List localList = MBeanAnalyzer.eliminateCovariantMethods(Arrays.asList(paramClass.getMethods()));
    SortedMap localSortedMap = Util.newSortedMap();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Method)localIterator.next();
      localObject2 = propertyName((Method)localObject1);
      if ((localObject2 != null) && ((i == 0) || (!((String)localObject2).equals("CompositeType"))))
      {
        localObject3 = (Method)localSortedMap.put(decapitalize((String)localObject2), localObject1);
        if (localObject3 != null)
        {
          String str = "Class " + paramClass.getName() + " has method name clash: " + ((Method)localObject3).getName() + ", " + ((Method)localObject1).getName();
          throw new OpenDataException(str);
        }
      }
    }
    int j = localSortedMap.size();
    if (j == 0) {
      throw new OpenDataException("Can't map " + paramClass.getName() + " to an open data type");
    }
    Object localObject1 = new Method[j];
    Object localObject2 = new String[j];
    Object localObject3 = new OpenType[j];
    int k = 0;
    Object localObject4 = localSortedMap.entrySet().iterator();
    while (((Iterator)localObject4).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject4).next();
      localObject2[k] = ((String)localEntry.getKey());
      Method localMethod = (Method)localEntry.getValue();
      localObject1[k] = localMethod;
      Type localType = localMethod.getGenericReturnType();
      localObject3[k] = paramMXBeanMappingFactory.mappingForType(localType, paramMXBeanMappingFactory).getOpenType();
      k++;
    }
    localObject4 = new CompositeType(paramClass.getName(), paramClass.getName(), (String[])localObject2, (String[])localObject2, (OpenType[])localObject3);
    return new CompositeMapping(paramClass, (CompositeType)localObject4, (String[])localObject2, (Method[])localObject1, paramMXBeanMappingFactory);
  }
  
  static InvalidObjectException invalidObjectException(String paramString, Throwable paramThrowable)
  {
    return (InvalidObjectException)EnvHelp.initCause(new InvalidObjectException(paramString), paramThrowable);
  }
  
  static InvalidObjectException invalidObjectException(Throwable paramThrowable)
  {
    return invalidObjectException(paramThrowable.getMessage(), paramThrowable);
  }
  
  static OpenDataException openDataException(String paramString, Throwable paramThrowable)
  {
    return (OpenDataException)EnvHelp.initCause(new OpenDataException(paramString), paramThrowable);
  }
  
  static OpenDataException openDataException(Throwable paramThrowable)
  {
    return openDataException(paramThrowable.getMessage(), paramThrowable);
  }
  
  static void mustBeComparable(Class<?> paramClass, Type paramType)
    throws OpenDataException
  {
    if ((!(paramType instanceof Class)) || (!Comparable.class.isAssignableFrom((Class)paramType)))
    {
      String str = "Parameter class " + paramType + " of " + paramClass.getName() + " does not implement " + Comparable.class.getName();
      throw new OpenDataException(str);
    }
  }
  
  public static String decapitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    int i = Character.offsetByCodePoints(paramString, 0, 1);
    if ((i < paramString.length()) && (Character.isUpperCase(paramString.codePointAt(i)))) {
      return paramString;
    }
    return paramString.substring(0, i).toLowerCase() + paramString.substring(i);
  }
  
  static String capitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    int i = paramString.offsetByCodePoints(0, 1);
    return paramString.substring(0, i).toUpperCase() + paramString.substring(i);
  }
  
  public static String propertyName(Method paramMethod)
  {
    String str1 = null;
    String str2 = paramMethod.getName();
    if (str2.startsWith("get")) {
      str1 = str2.substring(3);
    } else if ((str2.startsWith("is")) && (paramMethod.getReturnType() == Boolean.TYPE)) {
      str1 = str2.substring(2);
    }
    if ((str1 == null) || (str1.length() == 0) || (paramMethod.getParameterTypes().length > 0) || (paramMethod.getReturnType() == Void.TYPE) || (str2.equals("getClass"))) {
      return null;
    }
    return str1;
  }
  
  static
  {
    mappings = new Mappings(null);
    permanentMappings = Util.newList();
    OpenType[] arrayOfOpenType = { SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
    for (int i = 0; i < arrayOfOpenType.length; i++)
    {
      OpenType localOpenType = arrayOfOpenType[i];
      Class localClass1;
      try
      {
        localClass1 = Class.forName(localOpenType.getClassName(), false, ObjectName.class.getClassLoader());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new Error(localClassNotFoundException);
      }
      IdentityMapping localIdentityMapping1 = new IdentityMapping(localClass1, localOpenType);
      putPermanentMapping(localClass1, localIdentityMapping1);
      if (localClass1.getName().startsWith("java.lang.")) {
        try
        {
          Field localField = localClass1.getField("TYPE");
          Class localClass2 = (Class)localField.get(null);
          IdentityMapping localIdentityMapping2 = new IdentityMapping(localClass2, localOpenType);
          putPermanentMapping(localClass2, localIdentityMapping2);
          if (localClass2 != Void.TYPE)
          {
            Class localClass3 = Array.newInstance(localClass2, 0).getClass();
            ArrayType localArrayType = ArrayType.getPrimitiveArrayType(localClass3);
            IdentityMapping localIdentityMapping3 = new IdentityMapping(localClass3, localArrayType);
            putPermanentMapping(localClass3, localIdentityMapping3);
          }
        }
        catch (NoSuchFieldException localNoSuchFieldException) {}catch (IllegalAccessException localIllegalAccessException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      }
    }
  }
  
  private static final class ArrayMapping
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    private final MXBeanMapping elementMapping;
    
    ArrayMapping(Type paramType, ArrayType<?> paramArrayType, Class<?> paramClass, MXBeanMapping paramMXBeanMapping)
    {
      super(paramArrayType);
      this.elementMapping = paramMXBeanMapping;
    }
    
    final Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException
    {
      Object[] arrayOfObject1 = (Object[])paramObject;
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = (Object[])Array.newInstance(getOpenClass().getComponentType(), i);
      for (int j = 0; j < i; j++) {
        arrayOfObject2[j] = this.elementMapping.toOpenValue(arrayOfObject1[j]);
      }
      return arrayOfObject2;
    }
    
    final Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      Object[] arrayOfObject1 = (Object[])paramObject;
      Type localType = getJavaType();
      Object localObject;
      if ((localType instanceof GenericArrayType)) {
        localObject = ((GenericArrayType)localType).getGenericComponentType();
      } else if (((localType instanceof Class)) && (((Class)localType).isArray())) {
        localObject = ((Class)localType).getComponentType();
      } else {
        throw new IllegalArgumentException("Not an array: " + localType);
      }
      Object[] arrayOfObject2 = (Object[])Array.newInstance((Class)localObject, arrayOfObject1.length);
      for (int i = 0; i < arrayOfObject1.length; i++) {
        arrayOfObject2[i] = this.elementMapping.fromOpenValue(arrayOfObject1[i]);
      }
      return arrayOfObject2;
    }
    
    public void checkReconstructible()
      throws InvalidObjectException
    {
      this.elementMapping.checkReconstructible();
    }
  }
  
  private static final class CollectionMapping
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    private final Class<? extends Collection<?>> collectionClass;
    private final MXBeanMapping elementMapping;
    
    CollectionMapping(Type paramType, ArrayType<?> paramArrayType, Class<?> paramClass, MXBeanMapping paramMXBeanMapping)
    {
      super(paramArrayType);
      this.elementMapping = paramMXBeanMapping;
      Type localType = ((ParameterizedType)paramType).getRawType();
      Class localClass = (Class)localType;
      Object localObject;
      if (localClass == List.class)
      {
        localObject = ArrayList.class;
      }
      else if (localClass == Set.class)
      {
        localObject = HashSet.class;
      }
      else if (localClass == SortedSet.class)
      {
        localObject = TreeSet.class;
      }
      else
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        localObject = null;
      }
      this.collectionClass = ((Class)Util.cast(localObject));
    }
    
    final Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException
    {
      Collection localCollection = (Collection)paramObject;
      if ((localCollection instanceof SortedSet))
      {
        localObject1 = ((SortedSet)localCollection).comparator();
        if (localObject1 != null)
        {
          String str = "Cannot convert SortedSet with non-null comparator: " + localObject1;
          throw DefaultMXBeanMappingFactory.openDataException(str, new IllegalArgumentException(str));
        }
      }
      Object localObject1 = (Object[])Array.newInstance(getOpenClass().getComponentType(), localCollection.size());
      int i = 0;
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        localObject1[(i++)] = this.elementMapping.toOpenValue(localObject2);
      }
      return localObject1;
    }
    
    final Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      Object[] arrayOfObject1 = (Object[])paramObject;
      Collection localCollection;
      try
      {
        localCollection = (Collection)Util.cast(this.collectionClass.newInstance());
      }
      catch (Exception localException)
      {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot create collection", localException);
      }
      for (Object localObject1 : arrayOfObject1)
      {
        Object localObject2 = this.elementMapping.fromOpenValue(localObject1);
        if (!localCollection.add(localObject2))
        {
          String str = "Could not add " + localObject1 + " to " + this.collectionClass.getName() + " (duplicate set element?)";
          throw new InvalidObjectException(str);
        }
      }
      return localCollection;
    }
    
    public void checkReconstructible()
      throws InvalidObjectException
    {
      this.elementMapping.checkReconstructible();
    }
  }
  
  private static abstract class CompositeBuilder
  {
    private final Class<?> targetClass;
    private final String[] itemNames;
    
    CompositeBuilder(Class<?> paramClass, String[] paramArrayOfString)
    {
      this.targetClass = paramClass;
      this.itemNames = paramArrayOfString;
    }
    
    Class<?> getTargetClass()
    {
      return this.targetClass;
    }
    
    String[] getItemNames()
    {
      return this.itemNames;
    }
    
    abstract String applicable(Method[] paramArrayOfMethod)
      throws InvalidObjectException;
    
    Throwable possibleCause()
    {
      return null;
    }
    
    abstract Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
      throws InvalidObjectException;
  }
  
  private static class CompositeBuilderCheckGetters
    extends DefaultMXBeanMappingFactory.CompositeBuilder
  {
    private final MXBeanMapping[] getterConverters;
    private Throwable possibleCause;
    
    CompositeBuilderCheckGetters(Class<?> paramClass, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
    {
      super(paramArrayOfString);
      this.getterConverters = paramArrayOfMXBeanMapping;
    }
    
    String applicable(Method[] paramArrayOfMethod)
    {
      for (int i = 0; i < paramArrayOfMethod.length; i++) {
        try
        {
          this.getterConverters[i].checkReconstructible();
        }
        catch (InvalidObjectException localInvalidObjectException)
        {
          this.possibleCause = localInvalidObjectException;
          return "method " + paramArrayOfMethod[i].getName() + " returns type " + "that cannot be mapped back from OpenData";
        }
      }
      return "";
    }
    
    Throwable possibleCause()
    {
      return this.possibleCause;
    }
    
    final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
    {
      throw new Error();
    }
  }
  
  private static final class CompositeBuilderViaConstructor
    extends DefaultMXBeanMappingFactory.CompositeBuilder
  {
    private List<Constr> annotatedConstructors;
    
    CompositeBuilderViaConstructor(Class<?> paramClass, String[] paramArrayOfString)
    {
      super(paramArrayOfString);
    }
    
    String applicable(Method[] paramArrayOfMethod)
      throws InvalidObjectException
    {
      if (!AnnotationHelper.isAvailable()) {
        return "@ConstructorProperties annotation not available";
      }
      Class localClass = getTargetClass();
      Constructor[] arrayOfConstructor = localClass.getConstructors();
      List localList = Util.newList();
      for (localObject2 : arrayOfConstructor) {
        if ((Modifier.isPublic(((Constructor)localObject2).getModifiers())) && (AnnotationHelper.getPropertyNames((Constructor)localObject2) != null)) {
          localList.add(localObject2);
        }
      }
      if (localList.isEmpty()) {
        return "no constructor has @ConstructorProperties annotation";
      }
      this.annotatedConstructors = Util.newList();
      ??? = Util.newMap();
      String[] arrayOfString1 = getItemNames();
      for (??? = 0; ??? < arrayOfString1.length; ???++) {
        ((Map)???).put(arrayOfString1[???], Integer.valueOf(???));
      }
      Set localSet = Util.newSet();
      Object localObject2 = localList.iterator();
      Object localObject3;
      Object localObject4;
      Object localObject5;
      BitSet localBitSet;
      Object localObject6;
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Constructor)((Iterator)localObject2).next();
        String[] arrayOfString2 = AnnotationHelper.getPropertyNames((Constructor)localObject3);
        localObject4 = ((Constructor)localObject3).getGenericParameterTypes();
        if (localObject4.length != arrayOfString2.length)
        {
          localObject5 = "Number of constructor params does not match @ConstructorProperties annotation: " + localObject3;
          throw new InvalidObjectException((String)localObject5);
        }
        localObject5 = new int[paramArrayOfMethod.length];
        for (int m = 0; m < paramArrayOfMethod.length; m++) {
          localObject5[m] = -1;
        }
        localBitSet = new BitSet();
        for (int n = 0; n < arrayOfString2.length; n++)
        {
          String str1 = arrayOfString2[n];
          if (!((Map)???).containsKey(str1))
          {
            String str3 = "@ConstructorProperties includes name " + str1 + " which does not correspond to a property";
            localObject7 = ((Map)???).keySet().iterator();
            while (((Iterator)localObject7).hasNext())
            {
              localObject8 = (String)((Iterator)localObject7).next();
              if (((String)localObject8).equalsIgnoreCase(str1)) {
                str3 = str3 + " (differs only in case from property " + (String)localObject8 + ")";
              }
            }
            str3 = str3 + ": " + localObject3;
            throw new InvalidObjectException(str3);
          }
          int i2 = ((Integer)((Map)???).get(str1)).intValue();
          localObject5[i2] = n;
          if (localBitSet.get(i2))
          {
            localObject7 = "@ConstructorProperties contains property " + str1 + " more than once: " + localObject3;
            throw new InvalidObjectException((String)localObject7);
          }
          localBitSet.set(i2);
          Object localObject7 = paramArrayOfMethod[i2];
          Object localObject8 = ((Method)localObject7).getGenericReturnType();
          if (!localObject8.equals(localObject4[n]))
          {
            String str4 = "@ConstructorProperties gives property " + str1 + " of type " + localObject8 + " for parameter " + " of type " + localObject4[n] + ": " + localObject3;
            throw new InvalidObjectException(str4);
          }
        }
        if (!localSet.add(localBitSet))
        {
          localObject6 = "More than one constructor has a @ConstructorProperties annotation with this set of names: " + Arrays.toString(arrayOfString2);
          throw new InvalidObjectException((String)localObject6);
        }
        localObject6 = new Constr((Constructor)localObject3, (int[])localObject5, localBitSet);
        this.annotatedConstructors.add(localObject6);
      }
      localObject2 = localSet.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (BitSet)((Iterator)localObject2).next();
        int k = 0;
        localObject4 = localSet.iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (BitSet)((Iterator)localObject4).next();
          if (localObject3 == localObject5)
          {
            k = 1;
          }
          else if (k != 0)
          {
            localBitSet = new BitSet();
            localBitSet.or((BitSet)localObject3);
            localBitSet.or((BitSet)localObject5);
            if (!localSet.contains(localBitSet))
            {
              localObject6 = new TreeSet();
              for (int i1 = localBitSet.nextSetBit(0); i1 >= 0; i1 = localBitSet.nextSetBit(i1 + 1)) {
                ((Set)localObject6).add(arrayOfString1[i1]);
              }
              String str2 = "Constructors with @ConstructorProperties annotation  would be ambiguous for these items: " + localObject6;
              throw new InvalidObjectException(str2);
            }
          }
        }
      }
      return null;
    }
    
    final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
      throws InvalidObjectException
    {
      CompositeType localCompositeType = paramCompositeData.getCompositeType();
      BitSet localBitSet = new BitSet();
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (localCompositeType.getType(paramArrayOfString[i]) != null) {
          localBitSet.set(i);
        }
      }
      Object localObject1 = null;
      Object localObject2 = this.annotatedConstructors.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Constr localConstr = (Constr)((Iterator)localObject2).next();
        if ((subset(localConstr.presentParams, localBitSet)) && ((localObject1 == null) || (subset(localObject1.presentParams, localConstr.presentParams)))) {
          localObject1 = localConstr;
        }
      }
      if (localObject1 == null)
      {
        localObject2 = "No constructor has a @ConstructorProperties for this set of items: " + localCompositeType.keySet();
        throw new InvalidObjectException((String)localObject2);
      }
      localObject2 = new Object[localObject1.presentParams.cardinality()];
      Object localObject3;
      for (int j = 0; j < paramArrayOfString.length; j++) {
        if (localObject1.presentParams.get(j))
        {
          localObject3 = paramCompositeData.get(paramArrayOfString[j]);
          Object localObject4 = paramArrayOfMXBeanMapping[j].fromOpenValue(localObject3);
          int k = localObject1.paramIndexes[j];
          if (k >= 0) {
            localObject2[k] = localObject4;
          }
        }
      }
      try
      {
        ReflectUtil.checkPackageAccess(localObject1.constructor.getDeclaringClass());
        return localObject1.constructor.newInstance((Object[])localObject2);
      }
      catch (Exception localException)
      {
        localObject3 = "Exception constructing " + getTargetClass().getName();
        throw DefaultMXBeanMappingFactory.invalidObjectException((String)localObject3, localException);
      }
    }
    
    private static boolean subset(BitSet paramBitSet1, BitSet paramBitSet2)
    {
      BitSet localBitSet = (BitSet)paramBitSet1.clone();
      localBitSet.andNot(paramBitSet2);
      return localBitSet.isEmpty();
    }
    
    static class AnnotationHelper
    {
      private static Class<? extends Annotation> constructorPropertiesClass;
      private static Method valueMethod;
      
      AnnotationHelper() {}
      
      private static void findConstructorPropertiesClass()
      {
        try
        {
          constructorPropertiesClass = Class.forName("java.beans.ConstructorProperties", false, DefaultMXBeanMappingFactory.class.getClassLoader());
          valueMethod = constructorPropertiesClass.getMethod("value", new Class[0]);
        }
        catch (ClassNotFoundException localClassNotFoundException) {}catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw new InternalError(localNoSuchMethodException);
        }
      }
      
      static boolean isAvailable()
      {
        return constructorPropertiesClass != null;
      }
      
      static String[] getPropertyNames(Constructor<?> paramConstructor)
      {
        if (!isAvailable()) {
          return null;
        }
        Annotation localAnnotation = paramConstructor.getAnnotation(constructorPropertiesClass);
        if (localAnnotation == null) {
          return null;
        }
        try
        {
          return (String[])valueMethod.invoke(localAnnotation, new Object[0]);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new InternalError(localInvocationTargetException);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new InternalError(localIllegalAccessException);
        }
      }
      
      static {}
    }
    
    private static class Constr
    {
      final Constructor<?> constructor;
      final int[] paramIndexes;
      final BitSet presentParams;
      
      Constr(Constructor<?> paramConstructor, int[] paramArrayOfInt, BitSet paramBitSet)
      {
        this.constructor = paramConstructor;
        this.paramIndexes = paramArrayOfInt;
        this.presentParams = paramBitSet;
      }
    }
  }
  
  private static final class CompositeBuilderViaFrom
    extends DefaultMXBeanMappingFactory.CompositeBuilder
  {
    private Method fromMethod;
    
    CompositeBuilderViaFrom(Class<?> paramClass, String[] paramArrayOfString)
    {
      super(paramArrayOfString);
    }
    
    String applicable(Method[] paramArrayOfMethod)
      throws InvalidObjectException
    {
      Class localClass = getTargetClass();
      try
      {
        Method localMethod = localClass.getMethod("from", new Class[] { CompositeData.class });
        if (!Modifier.isStatic(localMethod.getModifiers())) {
          throw new InvalidObjectException("Method from(CompositeData) is not static");
        }
        if (localMethod.getReturnType() != getTargetClass())
        {
          String str = "Method from(CompositeData) returns " + MXBeanIntrospector.typeName(localMethod.getReturnType()) + " not " + MXBeanIntrospector.typeName(localClass);
          throw new InvalidObjectException(str);
        }
        this.fromMethod = localMethod;
        return null;
      }
      catch (InvalidObjectException localInvalidObjectException)
      {
        throw localInvalidObjectException;
      }
      catch (Exception localException) {}
      return "no method from(CompositeData)";
    }
    
    final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
      throws InvalidObjectException
    {
      try
      {
        return MethodUtil.invoke(this.fromMethod, null, new Object[] { paramCompositeData });
      }
      catch (Exception localException)
      {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Failed to invoke from(CompositeData)", localException);
      }
    }
  }
  
  private static final class CompositeBuilderViaProxy
    extends DefaultMXBeanMappingFactory.CompositeBuilder
  {
    CompositeBuilderViaProxy(Class<?> paramClass, String[] paramArrayOfString)
    {
      super(paramArrayOfString);
    }
    
    String applicable(Method[] paramArrayOfMethod)
    {
      Class localClass = getTargetClass();
      if (!localClass.isInterface()) {
        return "not an interface";
      }
      Set localSet = Util.newSet(Arrays.asList(localClass.getMethods()));
      localSet.removeAll(Arrays.asList(paramArrayOfMethod));
      Object localObject = null;
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Method localMethod1 = (Method)localIterator.next();
        String str = localMethod1.getName();
        Class[] arrayOfClass = localMethod1.getParameterTypes();
        try
        {
          Method localMethod2 = Object.class.getMethod(str, arrayOfClass);
          if (!Modifier.isPublic(localMethod2.getModifiers())) {
            localObject = str;
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          localObject = str;
        }
      }
      if (localObject != null) {
        return "contains methods other than getters (" + localObject + ")";
      }
      return null;
    }
    
    final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
    {
      Class localClass = getTargetClass();
      return Proxy.newProxyInstance(localClass.getClassLoader(), new Class[] { localClass }, new CompositeDataInvocationHandler(paramCompositeData));
    }
  }
  
  private static class CompositeBuilderViaSetters
    extends DefaultMXBeanMappingFactory.CompositeBuilder
  {
    private Method[] setters;
    
    CompositeBuilderViaSetters(Class<?> paramClass, String[] paramArrayOfString)
    {
      super(paramArrayOfString);
    }
    
    String applicable(Method[] paramArrayOfMethod)
    {
      try
      {
        Constructor localConstructor = getTargetClass().getConstructor(new Class[0]);
      }
      catch (Exception localException1)
      {
        return "does not have a public no-arg constructor";
      }
      Method[] arrayOfMethod = new Method[paramArrayOfMethod.length];
      for (int i = 0; i < paramArrayOfMethod.length; i++)
      {
        Method localMethod1 = paramArrayOfMethod[i];
        Class localClass = localMethod1.getReturnType();
        String str1 = DefaultMXBeanMappingFactory.propertyName(localMethod1);
        String str2 = "set" + str1;
        Method localMethod2;
        try
        {
          localMethod2 = getTargetClass().getMethod(str2, new Class[] { localClass });
          if (localMethod2.getReturnType() != Void.TYPE) {
            throw new Exception();
          }
        }
        catch (Exception localException2)
        {
          return "not all getters have corresponding setters (" + localMethod1 + ")";
        }
        arrayOfMethod[i] = localMethod2;
      }
      this.setters = arrayOfMethod;
      return null;
    }
    
    Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
      throws InvalidObjectException
    {
      Object localObject1;
      try
      {
        Class localClass = getTargetClass();
        ReflectUtil.checkPackageAccess(localClass);
        localObject1 = localClass.newInstance();
        for (int i = 0; i < paramArrayOfString.length; i++) {
          if (paramCompositeData.containsKey(paramArrayOfString[i]))
          {
            Object localObject2 = paramCompositeData.get(paramArrayOfString[i]);
            Object localObject3 = paramArrayOfMXBeanMapping[i].fromOpenValue(localObject2);
            MethodUtil.invoke(this.setters[i], localObject1, new Object[] { localObject3 });
          }
        }
      }
      catch (Exception localException)
      {
        throw DefaultMXBeanMappingFactory.invalidObjectException(localException);
      }
      return localObject1;
    }
  }
  
  private final class CompositeMapping
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    private final String[] itemNames;
    private final Method[] getters;
    private final MXBeanMapping[] getterMappings;
    private DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder;
    
    CompositeMapping(CompositeType paramCompositeType, String[] paramArrayOfString, Method[] paramArrayOfMethod, MXBeanMappingFactory paramMXBeanMappingFactory)
      throws OpenDataException
    {
      super(paramArrayOfString);
      assert (paramArrayOfMethod.length == paramMXBeanMappingFactory.length);
      this.itemNames = paramArrayOfMethod;
      this.getters = paramMXBeanMappingFactory;
      this.getterMappings = new MXBeanMapping[paramMXBeanMappingFactory.length];
      for (int i = 0; i < paramMXBeanMappingFactory.length; i++)
      {
        Type localType = paramMXBeanMappingFactory[i].getGenericReturnType();
        MXBeanMappingFactory localMXBeanMappingFactory;
        this.getterMappings[i] = localMXBeanMappingFactory.mappingForType(localType, localMXBeanMappingFactory);
      }
    }
    
    final Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException
    {
      CompositeType localCompositeType = (CompositeType)getOpenType();
      if ((paramObject instanceof CompositeDataView)) {
        return ((CompositeDataView)paramObject).toCompositeData(localCompositeType);
      }
      if (paramObject == null) {
        return null;
      }
      Object[] arrayOfObject = new Object[this.getters.length];
      for (int i = 0; i < this.getters.length; i++) {
        try
        {
          Object localObject = MethodUtil.invoke(this.getters[i], paramObject, (Object[])null);
          arrayOfObject[i] = this.getterMappings[i].toOpenValue(localObject);
        }
        catch (Exception localException)
        {
          throw DefaultMXBeanMappingFactory.openDataException("Error calling getter for " + this.itemNames[i] + ": " + localException, localException);
        }
      }
      return new CompositeDataSupport(localCompositeType, this.itemNames, arrayOfObject);
    }
    
    private synchronized void makeCompositeBuilder()
      throws InvalidObjectException
    {
      if (this.compositeBuilder != null) {
        return;
      }
      Class localClass = (Class)getJavaType();
      DefaultMXBeanMappingFactory.CompositeBuilder[][] arrayOfCompositeBuilder; = { { new DefaultMXBeanMappingFactory.CompositeBuilderViaFrom(localClass, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor(localClass, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderCheckGetters(localClass, this.itemNames, this.getterMappings), new DefaultMXBeanMappingFactory.CompositeBuilderViaSetters(localClass, this.itemNames), new DefaultMXBeanMappingFactory.CompositeBuilderViaProxy(localClass, this.itemNames) } };
      Object localObject1 = null;
      StringBuilder localStringBuilder = new StringBuilder();
      Object localObject2 = null;
      for (Object localObject4 : arrayOfCompositeBuilder;) {
        for (int k = 0; k < localObject4.length; k++)
        {
          Object localObject5 = localObject4[k];
          String str = localObject5.applicable(this.getters);
          if (str == null)
          {
            localObject1 = localObject5;
            break label268;
          }
          Throwable localThrowable = localObject5.possibleCause();
          if (localThrowable != null) {
            localObject2 = localThrowable;
          }
          if (str.length() > 0)
          {
            if (localStringBuilder.length() > 0) {
              localStringBuilder.append("; ");
            }
            localStringBuilder.append(str);
            if (k == 0) {
              break;
            }
          }
        }
      }
      label268:
      if (localObject1 == null)
      {
        ??? = "Do not know how to make a " + localClass.getName() + " from a CompositeData: " + localStringBuilder;
        if (localObject2 != null) {
          ??? = (String)??? + ". Remaining exceptions show a POSSIBLE cause.";
        }
        throw DefaultMXBeanMappingFactory.invalidObjectException((String)???, localObject2);
      }
      this.compositeBuilder = localObject1;
    }
    
    public void checkReconstructible()
      throws InvalidObjectException
    {
      makeCompositeBuilder();
    }
    
    final Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      makeCompositeBuilder();
      return this.compositeBuilder.fromCompositeData((CompositeData)paramObject, this.itemNames, this.getterMappings);
    }
  }
  
  private static final class EnumMapping<T extends Enum<T>>
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    private final Class<T> enumClass;
    
    EnumMapping(Class<T> paramClass)
    {
      super(SimpleType.STRING);
      this.enumClass = paramClass;
    }
    
    final Object toNonNullOpenValue(Object paramObject)
    {
      return ((Enum)paramObject).name();
    }
    
    final T fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      try
      {
        return Enum.valueOf(this.enumClass, (String)paramObject);
      }
      catch (Exception localException)
      {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot convert to enum: " + paramObject, localException);
      }
    }
  }
  
  private static final class IdentityMapping
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    IdentityMapping(Type paramType, OpenType<?> paramOpenType)
    {
      super(paramOpenType);
    }
    
    boolean isIdentity()
    {
      return true;
    }
    
    Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      return paramObject;
    }
    
    Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException
    {
      return paramObject;
    }
  }
  
  private static final class MXBeanRefMapping
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    MXBeanRefMapping(Type paramType)
    {
      super(SimpleType.OBJECTNAME);
    }
    
    final Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException
    {
      MXBeanLookup localMXBeanLookup = lookupNotNull(OpenDataException.class);
      ObjectName localObjectName = localMXBeanLookup.mxbeanToObjectName(paramObject);
      if (localObjectName == null) {
        throw new OpenDataException("No name for object: " + paramObject);
      }
      return localObjectName;
    }
    
    final Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      MXBeanLookup localMXBeanLookup = lookupNotNull(InvalidObjectException.class);
      ObjectName localObjectName = (ObjectName)paramObject;
      Object localObject = localMXBeanLookup.objectNameToMXBean(localObjectName, (Class)getJavaType());
      if (localObject == null)
      {
        String str = "No MXBean for name: " + localObjectName;
        throw new InvalidObjectException(str);
      }
      return localObject;
    }
    
    private <T extends Exception> MXBeanLookup lookupNotNull(Class<T> paramClass)
      throws Exception
    {
      MXBeanLookup localMXBeanLookup = MXBeanLookup.getLookup();
      if (localMXBeanLookup == null)
      {
        Exception localException1;
        try
        {
          Constructor localConstructor = paramClass.getConstructor(new Class[] { String.class });
          localException1 = (Exception)localConstructor.newInstance(new Object[] { "Cannot convert MXBean interface in this context" });
        }
        catch (Exception localException2)
        {
          throw new RuntimeException(localException2);
        }
        throw localException1;
      }
      return localMXBeanLookup;
    }
  }
  
  private static final class Mappings
    extends WeakHashMap<Type, WeakReference<MXBeanMapping>>
  {
    private Mappings() {}
  }
  
  static abstract class NonNullMXBeanMapping
    extends MXBeanMapping
  {
    NonNullMXBeanMapping(Type paramType, OpenType<?> paramOpenType)
    {
      super(paramOpenType);
    }
    
    public final Object fromOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      if (paramObject == null) {
        return null;
      }
      return fromNonNullOpenValue(paramObject);
    }
    
    public final Object toOpenValue(Object paramObject)
      throws OpenDataException
    {
      if (paramObject == null) {
        return null;
      }
      return toNonNullOpenValue(paramObject);
    }
    
    abstract Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException;
    
    abstract Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException;
    
    boolean isIdentity()
    {
      return false;
    }
  }
  
  private static final class TabularMapping
    extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
  {
    private final boolean sortedMap;
    private final MXBeanMapping keyMapping;
    private final MXBeanMapping valueMapping;
    
    TabularMapping(Type paramType, boolean paramBoolean, TabularType paramTabularType, MXBeanMapping paramMXBeanMapping1, MXBeanMapping paramMXBeanMapping2)
    {
      super(paramTabularType);
      this.sortedMap = paramBoolean;
      this.keyMapping = paramMXBeanMapping1;
      this.valueMapping = paramMXBeanMapping2;
    }
    
    final Object toNonNullOpenValue(Object paramObject)
      throws OpenDataException
    {
      Map localMap = (Map)Util.cast(paramObject);
      if ((localMap instanceof SortedMap))
      {
        localObject1 = ((SortedMap)localMap).comparator();
        if (localObject1 != null)
        {
          localObject2 = "Cannot convert SortedMap with non-null comparator: " + localObject1;
          throw DefaultMXBeanMappingFactory.openDataException((String)localObject2, new IllegalArgumentException((String)localObject2));
        }
      }
      Object localObject1 = (TabularType)getOpenType();
      Object localObject2 = new TabularDataSupport((TabularType)localObject1);
      CompositeType localCompositeType = ((TabularType)localObject1).getRowType();
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject3 = this.keyMapping.toOpenValue(localEntry.getKey());
        Object localObject4 = this.valueMapping.toOpenValue(localEntry.getValue());
        CompositeDataSupport localCompositeDataSupport = new CompositeDataSupport(localCompositeType, DefaultMXBeanMappingFactory.keyValueArray, new Object[] { localObject3, localObject4 });
        ((TabularData)localObject2).put(localCompositeDataSupport);
      }
      return localObject2;
    }
    
    final Object fromNonNullOpenValue(Object paramObject)
      throws InvalidObjectException
    {
      TabularData localTabularData = (TabularData)paramObject;
      Collection localCollection = (Collection)Util.cast(localTabularData.values());
      Map localMap = this.sortedMap ? Util.newSortedMap() : Util.newInsertionOrderMap();
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        CompositeData localCompositeData = (CompositeData)localIterator.next();
        Object localObject1 = this.keyMapping.fromOpenValue(localCompositeData.get("key"));
        Object localObject2 = this.valueMapping.fromOpenValue(localCompositeData.get("value"));
        if (localMap.put(localObject1, localObject2) != null)
        {
          String str = "Duplicate entry in TabularData: key=" + localObject1;
          throw new InvalidObjectException(str);
        }
      }
      return localMap;
    }
    
    public void checkReconstructible()
      throws InvalidObjectException
    {
      this.keyMapping.checkReconstructible();
      this.valueMapping.checkReconstructible();
    }
  }
}
