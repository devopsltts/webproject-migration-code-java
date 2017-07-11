package javax.management;

import com.sun.jmx.mbeanserver.Util;
import java.io.InvalidObjectException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ImmutableDescriptor
  implements Descriptor
{
  private static final long serialVersionUID = 8853308591080540165L;
  private final String[] names;
  private final Object[] values;
  private transient int hashCode = -1;
  public static final ImmutableDescriptor EMPTY_DESCRIPTOR = new ImmutableDescriptor(new String[0]);
  
  public ImmutableDescriptor(String[] paramArrayOfString, Object[] paramArrayOfObject)
  {
    this(makeMap(paramArrayOfString, paramArrayOfObject));
  }
  
  public ImmutableDescriptor(String... paramVarArgs)
  {
    this(makeMap(paramVarArgs));
  }
  
  public ImmutableDescriptor(Map<String, ?> paramMap)
  {
    if (paramMap == null) {
      throw new IllegalArgumentException("Null Map");
    }
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      if ((str == null) || (str.equals(""))) {
        throw new IllegalArgumentException("Empty or null field name");
      }
      if (localTreeMap.containsKey(str)) {
        throw new IllegalArgumentException("Duplicate name: " + str);
      }
      localTreeMap.put(str, localEntry.getValue());
    }
    int i = localTreeMap.size();
    this.names = ((String[])localTreeMap.keySet().toArray(new String[i]));
    this.values = localTreeMap.values().toArray(new Object[i]);
  }
  
  private Object readResolve()
    throws InvalidObjectException
  {
    int i = 0;
    if ((this.names == null) || (this.values == null) || (this.names.length != this.values.length)) {
      i = 1;
    }
    if (i == 0)
    {
      if ((this.names.length == 0) && (getClass() == ImmutableDescriptor.class)) {
        return EMPTY_DESCRIPTOR;
      }
      Comparator localComparator = String.CASE_INSENSITIVE_ORDER;
      String str = "";
      for (int j = 0; j < this.names.length; j++)
      {
        if ((this.names[j] == null) || (localComparator.compare(str, this.names[j]) >= 0))
        {
          i = 1;
          break;
        }
        str = this.names[j];
      }
    }
    if (i != 0) {
      throw new InvalidObjectException("Bad names or values");
    }
    return this;
  }
  
  private static SortedMap<String, ?> makeMap(String[] paramArrayOfString, Object[] paramArrayOfObject)
  {
    if ((paramArrayOfString == null) || (paramArrayOfObject == null)) {
      throw new IllegalArgumentException("Null array parameter");
    }
    if (paramArrayOfString.length != paramArrayOfObject.length) {
      throw new IllegalArgumentException("Different size arrays");
    }
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = paramArrayOfString[i];
      if ((str == null) || (str.equals(""))) {
        throw new IllegalArgumentException("Empty or null field name");
      }
      Object localObject = localTreeMap.put(str, paramArrayOfObject[i]);
      if (localObject != null) {
        throw new IllegalArgumentException("Duplicate field name: " + str);
      }
    }
    return localTreeMap;
  }
  
  private static SortedMap<String, ?> makeMap(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      throw new IllegalArgumentException("Null fields parameter");
    }
    String[] arrayOfString1 = new String[paramArrayOfString.length];
    String[] arrayOfString2 = new String[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = paramArrayOfString[i];
      int j = str.indexOf('=');
      if (j < 0) {
        throw new IllegalArgumentException("Missing = character: " + str);
      }
      arrayOfString1[i] = str.substring(0, j);
      arrayOfString2[i] = str.substring(j + 1);
    }
    return makeMap(arrayOfString1, arrayOfString2);
  }
  
  public static ImmutableDescriptor union(Descriptor... paramVarArgs)
  {
    int i = findNonEmpty(paramVarArgs, 0);
    if (i < 0) {
      return EMPTY_DESCRIPTOR;
    }
    if (((paramVarArgs[i] instanceof ImmutableDescriptor)) && (findNonEmpty(paramVarArgs, i + 1) < 0)) {
      return (ImmutableDescriptor)paramVarArgs[i];
    }
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    Object localObject1 = EMPTY_DESCRIPTOR;
    for (Descriptor localDescriptor : paramVarArgs) {
      if (localDescriptor != null)
      {
        Object localObject2;
        String[] arrayOfString;
        if ((localDescriptor instanceof ImmutableDescriptor))
        {
          localObject2 = (ImmutableDescriptor)localDescriptor;
          arrayOfString = ((ImmutableDescriptor)localObject2).names;
          if ((localObject2.getClass() == ImmutableDescriptor.class) && (arrayOfString.length > ((ImmutableDescriptor)localObject1).names.length)) {
            localObject1 = localObject2;
          }
        }
        else
        {
          arrayOfString = localDescriptor.getFieldNames();
        }
        for (String str1 : arrayOfString)
        {
          Object localObject3 = localDescriptor.getFieldValue(str1);
          Object localObject4 = localTreeMap.put(str1, localObject3);
          if (localObject4 != null)
          {
            boolean bool;
            if (localObject4.getClass().isArray()) {
              bool = Arrays.deepEquals(new Object[] { localObject4 }, new Object[] { localObject3 });
            } else {
              bool = localObject4.equals(localObject3);
            }
            if (!bool)
            {
              String str2 = "Inconsistent values for descriptor field " + str1 + ": " + localObject4 + " :: " + localObject3;
              throw new IllegalArgumentException(str2);
            }
          }
        }
      }
    }
    if (((ImmutableDescriptor)localObject1).names.length == localTreeMap.size()) {
      return localObject1;
    }
    return new ImmutableDescriptor(localTreeMap);
  }
  
  private static boolean isEmpty(Descriptor paramDescriptor)
  {
    if (paramDescriptor == null) {
      return true;
    }
    if ((paramDescriptor instanceof ImmutableDescriptor)) {
      return ((ImmutableDescriptor)paramDescriptor).names.length == 0;
    }
    return paramDescriptor.getFieldNames().length == 0;
  }
  
  private static int findNonEmpty(Descriptor[] paramArrayOfDescriptor, int paramInt)
  {
    for (int i = paramInt; i < paramArrayOfDescriptor.length; i++) {
      if (!isEmpty(paramArrayOfDescriptor[i])) {
        return i;
      }
    }
    return -1;
  }
  
  private int fieldIndex(String paramString)
  {
    return Arrays.binarySearch(this.names, paramString, String.CASE_INSENSITIVE_ORDER);
  }
  
  public final Object getFieldValue(String paramString)
  {
    checkIllegalFieldName(paramString);
    int i = fieldIndex(paramString);
    if (i < 0) {
      return null;
    }
    Object localObject1 = this.values[i];
    if ((localObject1 == null) || (!localObject1.getClass().isArray())) {
      return localObject1;
    }
    if ((localObject1 instanceof Object[])) {
      return ((Object[])localObject1).clone();
    }
    int j = Array.getLength(localObject1);
    Object localObject2 = Array.newInstance(localObject1.getClass().getComponentType(), j);
    System.arraycopy(localObject1, 0, localObject2, 0, j);
    return localObject2;
  }
  
  public final String[] getFields()
  {
    String[] arrayOfString = new String[this.names.length];
    for (int i = 0; i < arrayOfString.length; i++)
    {
      Object localObject = this.values[i];
      if (localObject == null) {
        localObject = "";
      } else if (!(localObject instanceof String)) {
        localObject = "(" + localObject + ")";
      }
      arrayOfString[i] = (this.names[i] + "=" + localObject);
    }
    return arrayOfString;
  }
  
  public final Object[] getFieldValues(String... paramVarArgs)
  {
    if (paramVarArgs == null) {
      return (Object[])this.values.clone();
    }
    Object[] arrayOfObject = new Object[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      String str = paramVarArgs[i];
      if ((str != null) && (!str.equals(""))) {
        arrayOfObject[i] = getFieldValue(str);
      }
    }
    return arrayOfObject;
  }
  
  public final String[] getFieldNames()
  {
    return (String[])this.names.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Descriptor)) {
      return false;
    }
    String[] arrayOfString;
    if ((paramObject instanceof ImmutableDescriptor))
    {
      arrayOfString = ((ImmutableDescriptor)paramObject).names;
    }
    else
    {
      arrayOfString = ((Descriptor)paramObject).getFieldNames();
      Arrays.sort(arrayOfString, String.CASE_INSENSITIVE_ORDER);
    }
    if (this.names.length != arrayOfString.length) {
      return false;
    }
    for (int i = 0; i < this.names.length; i++) {
      if (!this.names[i].equalsIgnoreCase(arrayOfString[i])) {
        return false;
      }
    }
    Object[] arrayOfObject;
    if ((paramObject instanceof ImmutableDescriptor)) {
      arrayOfObject = ((ImmutableDescriptor)paramObject).values;
    } else {
      arrayOfObject = ((Descriptor)paramObject).getFieldValues(arrayOfString);
    }
    return Arrays.deepEquals(this.values, arrayOfObject);
  }
  
  public int hashCode()
  {
    if (this.hashCode == -1) {
      this.hashCode = Util.hashCode(this.names, this.values);
    }
    return this.hashCode;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("{");
    for (int i = 0; i < this.names.length; i++)
    {
      if (i > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(this.names[i]).append("=");
      Object localObject = this.values[i];
      if ((localObject != null) && (localObject.getClass().isArray()))
      {
        String str = Arrays.deepToString(new Object[] { localObject });
        str = str.substring(1, str.length() - 1);
        localObject = str;
      }
      localStringBuilder.append(String.valueOf(localObject));
    }
    return "}";
  }
  
  public boolean isValid()
  {
    return true;
  }
  
  public Descriptor clone()
  {
    return this;
  }
  
  public final void setFields(String[] paramArrayOfString, Object[] paramArrayOfObject)
    throws RuntimeOperationsException
  {
    if ((paramArrayOfString == null) || (paramArrayOfObject == null)) {
      illegal("Null argument");
    }
    if (paramArrayOfString.length != paramArrayOfObject.length) {
      illegal("Different array sizes");
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      checkIllegalFieldName(paramArrayOfString[i]);
    }
    for (i = 0; i < paramArrayOfString.length; i++) {
      setField(paramArrayOfString[i], paramArrayOfObject[i]);
    }
  }
  
  public final void setField(String paramString, Object paramObject)
    throws RuntimeOperationsException
  {
    checkIllegalFieldName(paramString);
    int i = fieldIndex(paramString);
    if (i < 0) {
      unsupported();
    }
    Object localObject = this.values[i];
    if (localObject == null ? paramObject != null : !localObject.equals(paramObject)) {
      unsupported();
    }
  }
  
  public final void removeField(String paramString)
  {
    if ((paramString != null) && (fieldIndex(paramString) >= 0)) {
      unsupported();
    }
  }
  
  static Descriptor nonNullDescriptor(Descriptor paramDescriptor)
  {
    if (paramDescriptor == null) {
      return EMPTY_DESCRIPTOR;
    }
    return paramDescriptor;
  }
  
  private static void checkIllegalFieldName(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      illegal("Null or empty field name");
    }
  }
  
  private static void unsupported()
  {
    UnsupportedOperationException localUnsupportedOperationException = new UnsupportedOperationException("Descriptor is read-only");
    throw new RuntimeOperationsException(localUnsupportedOperationException);
  }
  
  private static void illegal(String paramString)
  {
    IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(paramString);
    throw new RuntimeOperationsException(localIllegalArgumentException);
  }
}
