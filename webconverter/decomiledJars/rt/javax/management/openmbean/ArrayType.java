package javax.management.openmbean;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;

public class ArrayType<T>
  extends OpenType<T>
{
  static final long serialVersionUID = 720504429830309770L;
  private int dimension;
  private OpenType<?> elementType;
  private boolean primitiveArray;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  private static final int PRIMITIVE_WRAPPER_NAME_INDEX = 0;
  private static final int PRIMITIVE_TYPE_NAME_INDEX = 1;
  private static final int PRIMITIVE_TYPE_KEY_INDEX = 2;
  private static final int PRIMITIVE_OPEN_TYPE_INDEX = 3;
  private static final Object[][] PRIMITIVE_ARRAY_TYPES = { { Boolean.class.getName(), Boolean.TYPE.getName(), "Z", SimpleType.BOOLEAN }, { Character.class.getName(), Character.TYPE.getName(), "C", SimpleType.CHARACTER }, { Byte.class.getName(), Byte.TYPE.getName(), "B", SimpleType.BYTE }, { Short.class.getName(), Short.TYPE.getName(), "S", SimpleType.SHORT }, { Integer.class.getName(), Integer.TYPE.getName(), "I", SimpleType.INTEGER }, { Long.class.getName(), Long.TYPE.getName(), "J", SimpleType.LONG }, { Float.class.getName(), Float.TYPE.getName(), "F", SimpleType.FLOAT }, { Double.class.getName(), Double.TYPE.getName(), "D", SimpleType.DOUBLE } };
  
  static boolean isPrimitiveContentType(String paramString)
  {
    for (Object[] arrayOfObject1 : PRIMITIVE_ARRAY_TYPES) {
      if (arrayOfObject1[2].equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  static String getPrimitiveTypeKey(String paramString)
  {
    for (Object[] arrayOfObject1 : PRIMITIVE_ARRAY_TYPES) {
      if (paramString.equals(arrayOfObject1[0])) {
        return (String)arrayOfObject1[2];
      }
    }
    return null;
  }
  
  static String getPrimitiveTypeName(String paramString)
  {
    for (Object[] arrayOfObject1 : PRIMITIVE_ARRAY_TYPES) {
      if (paramString.equals(arrayOfObject1[0])) {
        return (String)arrayOfObject1[1];
      }
    }
    return null;
  }
  
  static SimpleType<?> getPrimitiveOpenType(String paramString)
  {
    for (Object[] arrayOfObject1 : PRIMITIVE_ARRAY_TYPES) {
      if (paramString.equals(arrayOfObject1[1])) {
        return (SimpleType)arrayOfObject1[3];
      }
    }
    return null;
  }
  
  public ArrayType(int paramInt, OpenType<?> paramOpenType)
    throws OpenDataException
  {
    super(buildArrayClassName(paramInt, paramOpenType), buildArrayClassName(paramInt, paramOpenType), buildArrayDescription(paramInt, paramOpenType));
    if (paramOpenType.isArray())
    {
      ArrayType localArrayType = (ArrayType)paramOpenType;
      this.dimension = (localArrayType.getDimension() + paramInt);
      this.elementType = localArrayType.getElementOpenType();
      this.primitiveArray = localArrayType.isPrimitiveArray();
    }
    else
    {
      this.dimension = paramInt;
      this.elementType = paramOpenType;
      this.primitiveArray = false;
    }
  }
  
  public ArrayType(SimpleType<?> paramSimpleType, boolean paramBoolean)
    throws OpenDataException
  {
    super(buildArrayClassName(1, paramSimpleType, paramBoolean), buildArrayClassName(1, paramSimpleType, paramBoolean), buildArrayDescription(1, paramSimpleType, paramBoolean), true);
    this.dimension = 1;
    this.elementType = paramSimpleType;
    this.primitiveArray = paramBoolean;
  }
  
  ArrayType(String paramString1, String paramString2, String paramString3, int paramInt, OpenType<?> paramOpenType, boolean paramBoolean)
  {
    super(paramString1, paramString2, paramString3, true);
    this.dimension = paramInt;
    this.elementType = paramOpenType;
    this.primitiveArray = paramBoolean;
  }
  
  private static String buildArrayClassName(int paramInt, OpenType<?> paramOpenType)
    throws OpenDataException
  {
    boolean bool = false;
    if (paramOpenType.isArray()) {
      bool = ((ArrayType)paramOpenType).isPrimitiveArray();
    }
    return buildArrayClassName(paramInt, paramOpenType, bool);
  }
  
  private static String buildArrayClassName(int paramInt, OpenType<?> paramOpenType, boolean paramBoolean)
    throws OpenDataException
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Value of argument dimension must be greater than 0");
    }
    StringBuilder localStringBuilder = new StringBuilder();
    String str1 = paramOpenType.getClassName();
    for (int i = 1; i <= paramInt; i++) {
      localStringBuilder.append('[');
    }
    if (paramOpenType.isArray())
    {
      localStringBuilder.append(str1);
    }
    else if (paramBoolean)
    {
      String str2 = getPrimitiveTypeKey(str1);
      if (str2 == null) {
        throw new OpenDataException("Element type is not primitive: " + str1);
      }
      localStringBuilder.append(str2);
    }
    else
    {
      localStringBuilder.append("L");
      localStringBuilder.append(str1);
      localStringBuilder.append(';');
    }
    return localStringBuilder.toString();
  }
  
  private static String buildArrayDescription(int paramInt, OpenType<?> paramOpenType)
    throws OpenDataException
  {
    boolean bool = false;
    if (paramOpenType.isArray()) {
      bool = ((ArrayType)paramOpenType).isPrimitiveArray();
    }
    return buildArrayDescription(paramInt, paramOpenType, bool);
  }
  
  private static String buildArrayDescription(int paramInt, OpenType<?> paramOpenType, boolean paramBoolean)
    throws OpenDataException
  {
    if (paramOpenType.isArray())
    {
      localObject = (ArrayType)paramOpenType;
      paramInt += ((ArrayType)localObject).getDimension();
      paramOpenType = ((ArrayType)localObject).getElementOpenType();
      paramBoolean = ((ArrayType)localObject).isPrimitiveArray();
    }
    Object localObject = new StringBuilder(paramInt + "-dimension array of ");
    String str1 = paramOpenType.getClassName();
    if (paramBoolean)
    {
      String str2 = getPrimitiveTypeName(str1);
      if (str2 == null) {
        throw new OpenDataException("Element is not a primitive type: " + str1);
      }
      ((StringBuilder)localObject).append(str2);
    }
    else
    {
      ((StringBuilder)localObject).append(str1);
    }
    return ((StringBuilder)localObject).toString();
  }
  
  public int getDimension()
  {
    return this.dimension;
  }
  
  public OpenType<?> getElementOpenType()
  {
    return this.elementType;
  }
  
  public boolean isPrimitiveArray()
  {
    return this.primitiveArray;
  }
  
  public boolean isValue(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Class localClass1 = paramObject.getClass();
    String str = localClass1.getName();
    if (!localClass1.isArray()) {
      return false;
    }
    if (getClassName().equals(str)) {
      return true;
    }
    if ((this.elementType.getClassName().equals(TabularData.class.getName())) || (this.elementType.getClassName().equals(CompositeData.class.getName())))
    {
      boolean bool = this.elementType.getClassName().equals(TabularData.class.getName());
      int[] arrayOfInt = new int[getDimension()];
      CompositeData localCompositeData = bool ? TabularData.class : CompositeData.class;
      Class localClass2 = Array.newInstance(localCompositeData, arrayOfInt).getClass();
      if (!localClass2.isAssignableFrom(localClass1)) {
        return false;
      }
      return checkElementsType((Object[])paramObject, this.dimension);
    }
    return false;
  }
  
  private boolean checkElementsType(Object[] paramArrayOfObject, int paramInt)
  {
    if (paramInt > 1)
    {
      for (i = 0; i < paramArrayOfObject.length; i++) {
        if (!checkElementsType((Object[])paramArrayOfObject[i], paramInt - 1)) {
          return false;
        }
      }
      return true;
    }
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if ((paramArrayOfObject[i] != null) && (!getElementOpenType().isValue(paramArrayOfObject[i]))) {
        return false;
      }
    }
    return true;
  }
  
  boolean isAssignableFrom(OpenType<?> paramOpenType)
  {
    if (!(paramOpenType instanceof ArrayType)) {
      return false;
    }
    ArrayType localArrayType = (ArrayType)paramOpenType;
    return (localArrayType.getDimension() == getDimension()) && (localArrayType.isPrimitiveArray() == isPrimitiveArray()) && (localArrayType.getElementOpenType().isAssignableFrom(getElementOpenType()));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof ArrayType)) {
      return false;
    }
    ArrayType localArrayType = (ArrayType)paramObject;
    if (this.dimension != localArrayType.dimension) {
      return false;
    }
    if (!this.elementType.equals(localArrayType.elementType)) {
      return false;
    }
    return this.primitiveArray == localArrayType.primitiveArray;
  }
  
  public int hashCode()
  {
    if (this.myHashCode == null)
    {
      int i = 0;
      i += this.dimension;
      i += this.elementType.hashCode();
      i += Boolean.valueOf(this.primitiveArray).hashCode();
      this.myHashCode = Integer.valueOf(i);
    }
    return this.myHashCode.intValue();
  }
  
  public String toString()
  {
    if (this.myToString == null) {
      this.myToString = (getClass().getName() + "(name=" + getTypeName() + ",dimension=" + this.dimension + ",elementType=" + this.elementType + ",primitiveArray=" + this.primitiveArray + ")");
    }
    return this.myToString;
  }
  
  public static <E> ArrayType<E[]> getArrayType(OpenType<E> paramOpenType)
    throws OpenDataException
  {
    return new ArrayType(1, paramOpenType);
  }
  
  public static <T> ArrayType<T> getPrimitiveArrayType(Class<T> paramClass)
  {
    if (!paramClass.isArray()) {
      throw new IllegalArgumentException("arrayClass must be an array");
    }
    int i = 1;
    for (Class localClass = paramClass.getComponentType(); localClass.isArray(); localClass = localClass.getComponentType()) {
      i++;
    }
    String str = localClass.getName();
    if (!localClass.isPrimitive()) {
      throw new IllegalArgumentException("component type of the array must be a primitive type");
    }
    SimpleType localSimpleType = getPrimitiveOpenType(str);
    try
    {
      ArrayType localArrayType = new ArrayType(localSimpleType, true);
      if (i > 1) {
        localArrayType = new ArrayType(i - 1, localArrayType);
      }
      return localArrayType;
    }
    catch (OpenDataException localOpenDataException)
    {
      throw new IllegalArgumentException(localOpenDataException);
    }
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    if (this.primitiveArray) {
      return convertFromWrapperToPrimitiveTypes();
    }
    return this;
  }
  
  private <T> ArrayType<T> convertFromWrapperToPrimitiveTypes()
  {
    String str1 = getClassName();
    String str2 = getTypeName();
    String str3 = getDescription();
    for (Object[] arrayOfObject1 : PRIMITIVE_ARRAY_TYPES) {
      if (str1.indexOf((String)arrayOfObject1[0]) != -1)
      {
        str1 = str1.replaceFirst("L" + arrayOfObject1[0] + ";", (String)arrayOfObject1[2]);
        str2 = str2.replaceFirst("L" + arrayOfObject1[0] + ";", (String)arrayOfObject1[2]);
        str3 = str3.replaceFirst((String)arrayOfObject1[0], (String)arrayOfObject1[1]);
        break;
      }
    }
    return new ArrayType(str1, str2, str3, this.dimension, this.elementType, this.primitiveArray);
  }
  
  private Object writeReplace()
    throws ObjectStreamException
  {
    if (this.primitiveArray) {
      return convertFromPrimitiveToWrapperTypes();
    }
    return this;
  }
  
  private <T> ArrayType<T> convertFromPrimitiveToWrapperTypes()
  {
    String str1 = getClassName();
    String str2 = getTypeName();
    String str3 = getDescription();
    for (Object[] arrayOfObject1 : PRIMITIVE_ARRAY_TYPES) {
      if (str1.indexOf((String)arrayOfObject1[2]) != -1)
      {
        str1 = str1.replaceFirst((String)arrayOfObject1[2], "L" + arrayOfObject1[0] + ";");
        str2 = str2.replaceFirst((String)arrayOfObject1[2], "L" + arrayOfObject1[0] + ";");
        str3 = str3.replaceFirst((String)arrayOfObject1[1], (String)arrayOfObject1[0]);
        break;
      }
    }
    return new ArrayType(str1, str2, str3, this.dimension, this.elementType, this.primitiveArray);
  }
}
