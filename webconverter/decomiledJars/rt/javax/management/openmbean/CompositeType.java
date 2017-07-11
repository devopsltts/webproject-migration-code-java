package javax.management.openmbean;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class CompositeType
  extends OpenType<CompositeData>
{
  static final long serialVersionUID = -5366242454346948798L;
  private TreeMap<String, String> nameToDescription;
  private TreeMap<String, OpenType<?>> nameToType;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  private transient Set<String> myNamesSet = null;
  
  public CompositeType(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, OpenType<?>[] paramArrayOfOpenType)
    throws OpenDataException
  {
    super(CompositeData.class.getName(), paramString1, paramString2, false);
    checkForNullElement(paramArrayOfString1, "itemNames");
    checkForNullElement(paramArrayOfString2, "itemDescriptions");
    checkForNullElement(paramArrayOfOpenType, "itemTypes");
    checkForEmptyString(paramArrayOfString1, "itemNames");
    checkForEmptyString(paramArrayOfString2, "itemDescriptions");
    if ((paramArrayOfString1.length != paramArrayOfString2.length) || (paramArrayOfString1.length != paramArrayOfOpenType.length)) {
      throw new IllegalArgumentException("Array arguments itemNames[], itemDescriptions[] and itemTypes[] should be of same length (got " + paramArrayOfString1.length + ", " + paramArrayOfString2.length + " and " + paramArrayOfOpenType.length + ").");
    }
    this.nameToDescription = new TreeMap();
    this.nameToType = new TreeMap();
    for (int i = 0; i < paramArrayOfString1.length; i++)
    {
      String str = paramArrayOfString1[i].trim();
      if (this.nameToDescription.containsKey(str)) {
        throw new OpenDataException("Argument's element itemNames[" + i + "]=\"" + paramArrayOfString1[i] + "\" duplicates a previous item names.");
      }
      this.nameToDescription.put(str, paramArrayOfString2[i].trim());
      this.nameToType.put(str, paramArrayOfOpenType[i]);
    }
  }
  
  private static void checkForNullElement(Object[] paramArrayOfObject, String paramString)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) {
      throw new IllegalArgumentException("Argument " + paramString + "[] cannot be null or empty.");
    }
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if (paramArrayOfObject[i] == null) {
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + i + "] cannot be null.");
      }
    }
  }
  
  private static void checkForEmptyString(String[] paramArrayOfString, String paramString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].trim().equals("")) {
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + i + "] cannot be an empty string.");
      }
    }
  }
  
  public boolean containsKey(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    return this.nameToDescription.containsKey(paramString);
  }
  
  public String getDescription(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return (String)this.nameToDescription.get(paramString);
  }
  
  public OpenType<?> getType(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return (OpenType)this.nameToType.get(paramString);
  }
  
  public Set<String> keySet()
  {
    if (this.myNamesSet == null) {
      this.myNamesSet = Collections.unmodifiableSet(this.nameToDescription.keySet());
    }
    return this.myNamesSet;
  }
  
  public boolean isValue(Object paramObject)
  {
    if (!(paramObject instanceof CompositeData)) {
      return false;
    }
    CompositeData localCompositeData = (CompositeData)paramObject;
    CompositeType localCompositeType = localCompositeData.getCompositeType();
    return isAssignableFrom(localCompositeType);
  }
  
  boolean isAssignableFrom(OpenType<?> paramOpenType)
  {
    if (!(paramOpenType instanceof CompositeType)) {
      return false;
    }
    CompositeType localCompositeType = (CompositeType)paramOpenType;
    if (!localCompositeType.getTypeName().equals(getTypeName())) {
      return false;
    }
    Iterator localIterator = keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      OpenType localOpenType1 = localCompositeType.getType(str);
      OpenType localOpenType2 = getType(str);
      if ((localOpenType1 == null) || (!localOpenType2.isAssignableFrom(localOpenType1))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    CompositeType localCompositeType;
    try
    {
      localCompositeType = (CompositeType)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    if (!getTypeName().equals(localCompositeType.getTypeName())) {
      return false;
    }
    return this.nameToType.equals(localCompositeType.nameToType);
  }
  
  public int hashCode()
  {
    if (this.myHashCode == null)
    {
      int i = 0;
      i += getTypeName().hashCode();
      Iterator localIterator = this.nameToDescription.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        i += str.hashCode();
        i += ((OpenType)this.nameToType.get(str)).hashCode();
      }
      this.myHashCode = Integer.valueOf(i);
    }
    return this.myHashCode.intValue();
  }
  
  public String toString()
  {
    if (this.myToString == null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(getClass().getName());
      localStringBuilder.append("(name=");
      localStringBuilder.append(getTypeName());
      localStringBuilder.append(",items=(");
      int i = 0;
      Iterator localIterator = this.nameToType.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (i > 0) {
          localStringBuilder.append(",");
        }
        localStringBuilder.append("(itemName=");
        localStringBuilder.append(str);
        localStringBuilder.append(",itemType=");
        localStringBuilder.append(((OpenType)this.nameToType.get(str)).toString() + ")");
        i++;
      }
      localStringBuilder.append("))");
      this.myToString = localStringBuilder.toString();
    }
    return this.myToString;
  }
}
