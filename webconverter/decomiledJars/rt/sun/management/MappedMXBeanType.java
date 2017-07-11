package sun.management;

import com.sun.management.VMOption;
import java.io.InvalidObjectException;
import java.lang.management.LockInfo;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public abstract class MappedMXBeanType
{
  private static final WeakHashMap<Type, MappedMXBeanType> convertedTypes = new WeakHashMap();
  boolean isBasicType = false;
  OpenType<?> openType = inProgress;
  Class<?> mappedTypeClass;
  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final String[] mapIndexNames = { "key" };
  private static final String[] mapItemNames = { "key", "value" };
  private static final Class<?> COMPOSITE_DATA_CLASS = CompositeData.class;
  private static final OpenType<?> inProgress;
  private static final OpenType[] simpleTypes;
  
  public MappedMXBeanType() {}
  
  static synchronized MappedMXBeanType newMappedType(Type paramType)
    throws OpenDataException
  {
    Object localObject1 = null;
    Object localObject2;
    if ((paramType instanceof Class))
    {
      localObject2 = (Class)paramType;
      if (((Class)localObject2).isEnum()) {
        localObject1 = new EnumMXBeanType((Class)localObject2);
      } else if (((Class)localObject2).isArray()) {
        localObject1 = new ArrayMXBeanType((Class)localObject2);
      } else {
        localObject1 = new CompositeDataMXBeanType((Class)localObject2);
      }
    }
    else if ((paramType instanceof ParameterizedType))
    {
      localObject2 = (ParameterizedType)paramType;
      Type localType = ((ParameterizedType)localObject2).getRawType();
      if ((localType instanceof Class))
      {
        Class localClass = (Class)localType;
        if (localClass == List.class) {
          localObject1 = new ListMXBeanType((ParameterizedType)localObject2);
        } else if (localClass == Map.class) {
          localObject1 = new MapMXBeanType((ParameterizedType)localObject2);
        }
      }
    }
    else if ((paramType instanceof GenericArrayType))
    {
      localObject2 = (GenericArrayType)paramType;
      localObject1 = new GenericArrayMXBeanType((GenericArrayType)localObject2);
    }
    if (localObject1 == null) {
      throw new OpenDataException(paramType + " is not a supported MXBean type.");
    }
    convertedTypes.put(paramType, localObject1);
    return localObject1;
  }
  
  static synchronized MappedMXBeanType newBasicType(Class<?> paramClass, OpenType<?> paramOpenType)
    throws OpenDataException
  {
    BasicMXBeanType localBasicMXBeanType = new BasicMXBeanType(paramClass, paramOpenType);
    convertedTypes.put(paramClass, localBasicMXBeanType);
    return localBasicMXBeanType;
  }
  
  static synchronized MappedMXBeanType getMappedType(Type paramType)
    throws OpenDataException
  {
    MappedMXBeanType localMappedMXBeanType = (MappedMXBeanType)convertedTypes.get(paramType);
    if (localMappedMXBeanType == null) {
      localMappedMXBeanType = newMappedType(paramType);
    }
    if ((localMappedMXBeanType.getOpenType() instanceof InProgress)) {
      throw new OpenDataException("Recursive data structure");
    }
    return localMappedMXBeanType;
  }
  
  public static synchronized OpenType<?> toOpenType(Type paramType)
    throws OpenDataException
  {
    MappedMXBeanType localMappedMXBeanType = getMappedType(paramType);
    return localMappedMXBeanType.getOpenType();
  }
  
  public static Object toJavaTypeData(Object paramObject, Type paramType)
    throws OpenDataException, InvalidObjectException
  {
    if (paramObject == null) {
      return null;
    }
    MappedMXBeanType localMappedMXBeanType = getMappedType(paramType);
    return localMappedMXBeanType.toJavaTypeData(paramObject);
  }
  
  public static Object toOpenTypeData(Object paramObject, Type paramType)
    throws OpenDataException
  {
    if (paramObject == null) {
      return null;
    }
    MappedMXBeanType localMappedMXBeanType = getMappedType(paramType);
    return localMappedMXBeanType.toOpenTypeData(paramObject);
  }
  
  OpenType<?> getOpenType()
  {
    return this.openType;
  }
  
  boolean isBasicType()
  {
    return this.isBasicType;
  }
  
  String getTypeName()
  {
    return getMappedTypeClass().getName();
  }
  
  Class<?> getMappedTypeClass()
  {
    return this.mappedTypeClass;
  }
  
  abstract Type getJavaType();
  
  abstract String getName();
  
  abstract Object toOpenTypeData(Object paramObject)
    throws OpenDataException;
  
  abstract Object toJavaTypeData(Object paramObject)
    throws OpenDataException, InvalidObjectException;
  
  private static String decapitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    if ((paramString.length() > 1) && (Character.isUpperCase(paramString.charAt(1))) && (Character.isUpperCase(paramString.charAt(0)))) {
      return paramString;
    }
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toLowerCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  static
  {
    InProgress localInProgress;
    try
    {
      localInProgress = new InProgress();
    }
    catch (OpenDataException localOpenDataException2)
    {
      throw new AssertionError(localOpenDataException2);
    }
    inProgress = localInProgress;
    simpleTypes = new OpenType[] { SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
    try
    {
      for (int i = 0; i < simpleTypes.length; i++)
      {
        OpenType localOpenType = simpleTypes[i];
        Class localClass1;
        try
        {
          localClass1 = Class.forName(localOpenType.getClassName(), false, MappedMXBeanType.class.getClassLoader());
          newBasicType(localClass1, localOpenType);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new AssertionError(localClassNotFoundException);
        }
        catch (OpenDataException localOpenDataException3)
        {
          throw new AssertionError(localOpenDataException3);
        }
        if (localClass1.getName().startsWith("java.lang.")) {
          try
          {
            Field localField = localClass1.getField("TYPE");
            Class localClass2 = (Class)localField.get(null);
            newBasicType(localClass2, localOpenType);
          }
          catch (NoSuchFieldException localNoSuchFieldException) {}catch (IllegalAccessException localIllegalAccessException)
          {
            throw new AssertionError(localIllegalAccessException);
          }
        }
      }
    }
    catch (OpenDataException localOpenDataException1)
    {
      throw new AssertionError(localOpenDataException1);
    }
  }
  
  static class ArrayMXBeanType
    extends MappedMXBeanType
  {
    final Class<?> arrayClass;
    protected MappedMXBeanType componentType;
    protected MappedMXBeanType baseElementType;
    
    ArrayMXBeanType(Class<?> paramClass)
      throws OpenDataException
    {
      this.arrayClass = paramClass;
      this.componentType = getMappedType(paramClass.getComponentType());
      StringBuilder localStringBuilder = new StringBuilder();
      Object localObject = paramClass;
      for (int i = 0; ((Class)localObject).isArray(); i++)
      {
        localStringBuilder.append('[');
        localObject = ((Class)localObject).getComponentType();
      }
      this.baseElementType = getMappedType((Type)localObject);
      if (((Class)localObject).isPrimitive()) {
        localStringBuilder = new StringBuilder(paramClass.getName());
      } else {
        localStringBuilder.append("L" + this.baseElementType.getTypeName() + ";");
      }
      try
      {
        this.mappedTypeClass = Class.forName(localStringBuilder.toString());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        OpenDataException localOpenDataException = new OpenDataException("Cannot obtain array class");
        localOpenDataException.initCause(localClassNotFoundException);
        throw localOpenDataException;
      }
      this.openType = new ArrayType(i, this.baseElementType.getOpenType());
    }
    
    protected ArrayMXBeanType()
    {
      this.arrayClass = null;
    }
    
    Type getJavaType()
    {
      return this.arrayClass;
    }
    
    String getName()
    {
      return this.arrayClass.getName();
    }
    
    Object toOpenTypeData(Object paramObject)
      throws OpenDataException
    {
      if (this.baseElementType.isBasicType()) {
        return paramObject;
      }
      Object[] arrayOfObject1 = (Object[])paramObject;
      Object[] arrayOfObject2 = (Object[])Array.newInstance(this.componentType.getMappedTypeClass(), arrayOfObject1.length);
      int i = 0;
      for (Object localObject : arrayOfObject1)
      {
        if (localObject == null) {
          arrayOfObject2[i] = null;
        } else {
          arrayOfObject2[i] = this.componentType.toOpenTypeData(localObject);
        }
        i++;
      }
      return arrayOfObject2;
    }
    
    Object toJavaTypeData(Object paramObject)
      throws OpenDataException, InvalidObjectException
    {
      if (this.baseElementType.isBasicType()) {
        return paramObject;
      }
      Object[] arrayOfObject1 = (Object[])paramObject;
      Object[] arrayOfObject2 = (Object[])Array.newInstance((Class)this.componentType.getJavaType(), arrayOfObject1.length);
      int i = 0;
      for (Object localObject : arrayOfObject1)
      {
        if (localObject == null) {
          arrayOfObject2[i] = null;
        } else {
          arrayOfObject2[i] = this.componentType.toJavaTypeData(localObject);
        }
        i++;
      }
      return arrayOfObject2;
    }
  }
  
  static class BasicMXBeanType
    extends MappedMXBeanType
  {
    final Class<?> basicType;
    
    BasicMXBeanType(Class<?> paramClass, OpenType<?> paramOpenType)
    {
      this.basicType = paramClass;
      this.openType = paramOpenType;
      this.mappedTypeClass = paramClass;
      this.isBasicType = true;
    }
    
    Type getJavaType()
    {
      return this.basicType;
    }
    
    String getName()
    {
      return this.basicType.getName();
    }
    
    Object toOpenTypeData(Object paramObject)
      throws OpenDataException
    {
      return paramObject;
    }
    
    Object toJavaTypeData(Object paramObject)
      throws OpenDataException, InvalidObjectException
    {
      return paramObject;
    }
  }
  
  static class CompositeDataMXBeanType
    extends MappedMXBeanType
  {
    final Class<?> javaClass;
    final boolean isCompositeData;
    Method fromMethod = null;
    
    CompositeDataMXBeanType(Class<?> paramClass)
      throws OpenDataException
    {
      this.javaClass = paramClass;
      this.mappedTypeClass = MappedMXBeanType.COMPOSITE_DATA_CLASS;
      try
      {
        this.fromMethod = ((Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Method run()
            throws NoSuchMethodException
          {
            return MappedMXBeanType.CompositeDataMXBeanType.this.javaClass.getMethod("from", new Class[] { MappedMXBeanType.COMPOSITE_DATA_CLASS });
          }
        }));
      }
      catch (PrivilegedActionException localPrivilegedActionException) {}
      if (MappedMXBeanType.COMPOSITE_DATA_CLASS.isAssignableFrom(paramClass))
      {
        this.isCompositeData = true;
        this.openType = null;
      }
      else
      {
        this.isCompositeData = false;
        Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction()
        {
          public Method[] run()
          {
            return MappedMXBeanType.CompositeDataMXBeanType.this.javaClass.getMethods();
          }
        });
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        for (int i = 0; i < arrayOfMethod.length; i++)
        {
          Method localMethod = arrayOfMethod[i];
          String str1 = localMethod.getName();
          Type localType = localMethod.getGenericReturnType();
          String str2;
          if (str1.startsWith("get"))
          {
            str2 = str1.substring(3);
          }
          else
          {
            if ((!str1.startsWith("is")) || (!(localType instanceof Class)) || ((Class)localType != Boolean.TYPE)) {
              continue;
            }
            str2 = str1.substring(2);
          }
          if ((!str2.equals("")) && (localMethod.getParameterTypes().length <= 0) && (localType != Void.TYPE) && (!str2.equals("Class")))
          {
            localArrayList1.add(MappedMXBeanType.decapitalize(str2));
            localArrayList2.add(toOpenType(localType));
          }
        }
        String[] arrayOfString = (String[])localArrayList1.toArray(new String[0]);
        this.openType = new CompositeType(paramClass.getName(), paramClass.getName(), arrayOfString, arrayOfString, (OpenType[])localArrayList2.toArray(new OpenType[0]));
      }
    }
    
    Type getJavaType()
    {
      return this.javaClass;
    }
    
    String getName()
    {
      return this.javaClass.getName();
    }
    
    Object toOpenTypeData(Object paramObject)
      throws OpenDataException
    {
      if ((paramObject instanceof MemoryUsage)) {
        return MemoryUsageCompositeData.toCompositeData((MemoryUsage)paramObject);
      }
      if ((paramObject instanceof ThreadInfo)) {
        return ThreadInfoCompositeData.toCompositeData((ThreadInfo)paramObject);
      }
      if ((paramObject instanceof LockInfo))
      {
        if ((paramObject instanceof MonitorInfo)) {
          return MonitorInfoCompositeData.toCompositeData((MonitorInfo)paramObject);
        }
        return LockInfoCompositeData.toCompositeData((LockInfo)paramObject);
      }
      if ((paramObject instanceof MemoryNotificationInfo)) {
        return MemoryNotifInfoCompositeData.toCompositeData((MemoryNotificationInfo)paramObject);
      }
      if ((paramObject instanceof VMOption)) {
        return VMOptionCompositeData.toCompositeData((VMOption)paramObject);
      }
      if (this.isCompositeData)
      {
        CompositeData localCompositeData = (CompositeData)paramObject;
        CompositeType localCompositeType = localCompositeData.getCompositeType();
        String[] arrayOfString = (String[])localCompositeType.keySet().toArray(new String[0]);
        Object[] arrayOfObject = localCompositeData.getAll(arrayOfString);
        return new CompositeDataSupport(localCompositeType, arrayOfString, arrayOfObject);
      }
      throw new OpenDataException(this.javaClass.getName() + " is not supported for platform MXBeans");
    }
    
    Object toJavaTypeData(Object paramObject)
      throws OpenDataException, InvalidObjectException
    {
      if (this.fromMethod == null) {
        throw new AssertionError("Does not support data conversion");
      }
      try
      {
        return this.fromMethod.invoke(null, new Object[] { paramObject });
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        OpenDataException localOpenDataException = new OpenDataException("Failed to invoke " + this.fromMethod.getName() + " to convert CompositeData " + " to " + this.javaClass.getName());
        localOpenDataException.initCause(localInvocationTargetException);
        throw localOpenDataException;
      }
    }
  }
  
  static class EnumMXBeanType
    extends MappedMXBeanType
  {
    final Class enumClass;
    
    EnumMXBeanType(Class<?> paramClass)
    {
      this.enumClass = paramClass;
      this.openType = SimpleType.STRING;
      this.mappedTypeClass = String.class;
    }
    
    Type getJavaType()
    {
      return this.enumClass;
    }
    
    String getName()
    {
      return this.enumClass.getName();
    }
    
    Object toOpenTypeData(Object paramObject)
      throws OpenDataException
    {
      return ((Enum)paramObject).name();
    }
    
    Object toJavaTypeData(Object paramObject)
      throws OpenDataException, InvalidObjectException
    {
      try
      {
        return Enum.valueOf(this.enumClass, (String)paramObject);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        InvalidObjectException localInvalidObjectException = new InvalidObjectException("Enum constant named " + (String)paramObject + " is missing");
        localInvalidObjectException.initCause(localIllegalArgumentException);
        throw localInvalidObjectException;
      }
    }
  }
  
  static class GenericArrayMXBeanType
    extends MappedMXBeanType.ArrayMXBeanType
  {
    final GenericArrayType gtype;
    
    GenericArrayMXBeanType(GenericArrayType paramGenericArrayType)
      throws OpenDataException
    {
      this.gtype = paramGenericArrayType;
      this.componentType = getMappedType(paramGenericArrayType.getGenericComponentType());
      StringBuilder localStringBuilder = new StringBuilder();
      Object localObject = paramGenericArrayType;
      for (int i = 0; (localObject instanceof GenericArrayType); i++)
      {
        localStringBuilder.append('[');
        GenericArrayType localGenericArrayType = (GenericArrayType)localObject;
        localObject = localGenericArrayType.getGenericComponentType();
      }
      this.baseElementType = getMappedType((Type)localObject);
      if (((localObject instanceof Class)) && (((Class)localObject).isPrimitive())) {
        localStringBuilder = new StringBuilder(paramGenericArrayType.toString());
      } else {
        localStringBuilder.append("L" + this.baseElementType.getTypeName() + ";");
      }
      try
      {
        this.mappedTypeClass = Class.forName(localStringBuilder.toString());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        OpenDataException localOpenDataException = new OpenDataException("Cannot obtain array class");
        localOpenDataException.initCause(localClassNotFoundException);
        throw localOpenDataException;
      }
      this.openType = new ArrayType(i, this.baseElementType.getOpenType());
    }
    
    Type getJavaType()
    {
      return this.gtype;
    }
    
    String getName()
    {
      return this.gtype.toString();
    }
  }
  
  private static class InProgress
    extends OpenType
  {
    private static final String description = "Marker to detect recursive type use -- internal use only!";
    private static final long serialVersionUID = -3413063475064374490L;
    
    InProgress()
      throws OpenDataException
    {
      super("java.lang.String", "Marker to detect recursive type use -- internal use only!");
    }
    
    public String toString()
    {
      return "Marker to detect recursive type use -- internal use only!";
    }
    
    public int hashCode()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      return false;
    }
    
    public boolean isValue(Object paramObject)
    {
      return false;
    }
  }
  
  static class ListMXBeanType
    extends MappedMXBeanType
  {
    final ParameterizedType javaType;
    final MappedMXBeanType paramType;
    final String typeName;
    
    ListMXBeanType(ParameterizedType paramParameterizedType)
      throws OpenDataException
    {
      this.javaType = paramParameterizedType;
      Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
      assert (arrayOfType.length == 1);
      if (!(arrayOfType[0] instanceof Class)) {
        throw new OpenDataException("Element Type for " + paramParameterizedType + " not supported");
      }
      Class localClass = (Class)arrayOfType[0];
      if (localClass.isArray()) {
        throw new OpenDataException("Element Type for " + paramParameterizedType + " not supported");
      }
      this.paramType = getMappedType(localClass);
      this.typeName = ("List<" + this.paramType.getName() + ">");
      try
      {
        this.mappedTypeClass = Class.forName("[L" + this.paramType.getTypeName() + ";");
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        OpenDataException localOpenDataException = new OpenDataException("Array class not found");
        localOpenDataException.initCause(localClassNotFoundException);
        throw localOpenDataException;
      }
      this.openType = new ArrayType(1, this.paramType.getOpenType());
    }
    
    Type getJavaType()
    {
      return this.javaType;
    }
    
    String getName()
    {
      return this.typeName;
    }
    
    Object toOpenTypeData(Object paramObject)
      throws OpenDataException
    {
      List localList = (List)paramObject;
      Object[] arrayOfObject = (Object[])Array.newInstance(this.paramType.getMappedTypeClass(), localList.size());
      int i = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        arrayOfObject[(i++)] = this.paramType.toOpenTypeData(localObject);
      }
      return arrayOfObject;
    }
    
    Object toJavaTypeData(Object paramObject)
      throws OpenDataException, InvalidObjectException
    {
      Object[] arrayOfObject1 = (Object[])paramObject;
      ArrayList localArrayList = new ArrayList(arrayOfObject1.length);
      for (Object localObject : arrayOfObject1) {
        localArrayList.add(this.paramType.toJavaTypeData(localObject));
      }
      return localArrayList;
    }
  }
  
  static class MapMXBeanType
    extends MappedMXBeanType
  {
    final ParameterizedType javaType;
    final MappedMXBeanType keyType;
    final MappedMXBeanType valueType;
    final String typeName;
    
    MapMXBeanType(ParameterizedType paramParameterizedType)
      throws OpenDataException
    {
      this.javaType = paramParameterizedType;
      Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
      assert (arrayOfType.length == 2);
      this.keyType = getMappedType(arrayOfType[0]);
      this.valueType = getMappedType(arrayOfType[1]);
      this.typeName = ("Map<" + this.keyType.getName() + "," + this.valueType.getName() + ">");
      OpenType[] arrayOfOpenType = { this.keyType.getOpenType(), this.valueType.getOpenType() };
      CompositeType localCompositeType = new CompositeType(this.typeName, this.typeName, MappedMXBeanType.mapItemNames, MappedMXBeanType.mapItemNames, arrayOfOpenType);
      this.openType = new TabularType(this.typeName, this.typeName, localCompositeType, MappedMXBeanType.mapIndexNames);
      this.mappedTypeClass = TabularData.class;
    }
    
    Type getJavaType()
    {
      return this.javaType;
    }
    
    String getName()
    {
      return this.typeName;
    }
    
    Object toOpenTypeData(Object paramObject)
      throws OpenDataException
    {
      Map localMap = (Map)paramObject;
      TabularType localTabularType = (TabularType)this.openType;
      TabularDataSupport localTabularDataSupport = new TabularDataSupport(localTabularType);
      CompositeType localCompositeType = localTabularType.getRowType();
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = this.keyType.toOpenTypeData(localEntry.getKey());
        Object localObject2 = this.valueType.toOpenTypeData(localEntry.getValue());
        CompositeDataSupport localCompositeDataSupport = new CompositeDataSupport(localCompositeType, MappedMXBeanType.mapItemNames, new Object[] { localObject1, localObject2 });
        localTabularDataSupport.put(localCompositeDataSupport);
      }
      return localTabularDataSupport;
    }
    
    Object toJavaTypeData(Object paramObject)
      throws OpenDataException, InvalidObjectException
    {
      TabularData localTabularData = (TabularData)paramObject;
      HashMap localHashMap = new HashMap();
      Iterator localIterator = localTabularData.values().iterator();
      while (localIterator.hasNext())
      {
        CompositeData localCompositeData = (CompositeData)localIterator.next();
        Object localObject1 = this.keyType.toJavaTypeData(localCompositeData.get("key"));
        Object localObject2 = this.valueType.toJavaTypeData(localCompositeData.get("value"));
        localHashMap.put(localObject1, localObject2);
      }
      return localHashMap;
    }
  }
}
