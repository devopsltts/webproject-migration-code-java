package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public class Rdn
  implements Serializable, Comparable<Object>
{
  private transient ArrayList<RdnEntry> entries;
  private static final int DEFAULT_SIZE = 1;
  private static final long serialVersionUID = -5994465067210009656L;
  private static final String escapees = ",=+<>#;\"\\";
  
  public Rdn(Attributes paramAttributes)
    throws InvalidNameException
  {
    if (paramAttributes.size() == 0) {
      throw new InvalidNameException("Attributes cannot be empty");
    }
    this.entries = new ArrayList(paramAttributes.size());
    NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
    try
    {
      for (int i = 0; localNamingEnumeration.hasMore(); i++)
      {
        localObject = new RdnEntry(null);
        Attribute localAttribute = (Attribute)localNamingEnumeration.next();
        ((RdnEntry)localObject).type = localAttribute.getID();
        ((RdnEntry)localObject).value = localAttribute.get();
        this.entries.add(i, localObject);
      }
    }
    catch (NamingException localNamingException)
    {
      Object localObject = new InvalidNameException(localNamingException.getMessage());
      ((InvalidNameException)localObject).initCause(localNamingException);
      throw ((Throwable)localObject);
    }
    sort();
  }
  
  public Rdn(String paramString)
    throws InvalidNameException
  {
    this.entries = new ArrayList(1);
    new Rfc2253Parser(paramString).parseRdn(this);
  }
  
  public Rdn(Rdn paramRdn)
  {
    this.entries = new ArrayList(paramRdn.entries.size());
    this.entries.addAll(paramRdn.entries);
  }
  
  public Rdn(String paramString, Object paramObject)
    throws InvalidNameException
  {
    if (paramObject == null) {
      throw new NullPointerException("Cannot set value to null");
    }
    if ((paramString.equals("")) || (isEmptyValue(paramObject))) {
      throw new InvalidNameException("type or value cannot be empty, type:" + paramString + " value:" + paramObject);
    }
    this.entries = new ArrayList(1);
    put(paramString, paramObject);
  }
  
  private boolean isEmptyValue(Object paramObject)
  {
    return (((paramObject instanceof String)) && (paramObject.equals(""))) || (((paramObject instanceof byte[])) && (((byte[])paramObject).length == 0));
  }
  
  Rdn()
  {
    this.entries = new ArrayList(1);
  }
  
  Rdn put(String paramString, Object paramObject)
  {
    RdnEntry localRdnEntry = new RdnEntry(null);
    localRdnEntry.type = paramString;
    if ((paramObject instanceof byte[])) {
      localRdnEntry.value = ((byte[])paramObject).clone();
    } else {
      localRdnEntry.value = paramObject;
    }
    this.entries.add(localRdnEntry);
    return this;
  }
  
  void sort()
  {
    if (this.entries.size() > 1) {
      Collections.sort(this.entries);
    }
  }
  
  public Object getValue()
  {
    return ((RdnEntry)this.entries.get(0)).getValue();
  }
  
  public String getType()
  {
    return ((RdnEntry)this.entries.get(0)).getType();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = this.entries.size();
    if (i > 0) {
      localStringBuilder.append(this.entries.get(0));
    }
    for (int j = 1; j < i; j++)
    {
      localStringBuilder.append('+');
      localStringBuilder.append(this.entries.get(j));
    }
    return localStringBuilder.toString();
  }
  
  public int compareTo(Object paramObject)
  {
    if (!(paramObject instanceof Rdn)) {
      throw new ClassCastException("The obj is not a Rdn");
    }
    if (paramObject == this) {
      return 0;
    }
    Rdn localRdn = (Rdn)paramObject;
    int i = Math.min(this.entries.size(), localRdn.entries.size());
    for (int j = 0; j < i; j++)
    {
      int k = ((RdnEntry)this.entries.get(j)).compareTo((RdnEntry)localRdn.entries.get(j));
      if (k != 0) {
        return k;
      }
    }
    return this.entries.size() - localRdn.entries.size();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Rdn)) {
      return false;
    }
    Rdn localRdn = (Rdn)paramObject;
    if (this.entries.size() != localRdn.size()) {
      return false;
    }
    for (int i = 0; i < this.entries.size(); i++) {
      if (!((RdnEntry)this.entries.get(i)).equals(localRdn.entries.get(i))) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < this.entries.size(); j++) {
      i += ((RdnEntry)this.entries.get(j)).hashCode();
    }
    return i;
  }
  
  public Attributes toAttributes()
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    for (int i = 0; i < this.entries.size(); i++)
    {
      RdnEntry localRdnEntry = (RdnEntry)this.entries.get(i);
      Attribute localAttribute = localBasicAttributes.put(localRdnEntry.getType(), localRdnEntry.getValue());
      if (localAttribute != null)
      {
        localAttribute.add(localRdnEntry.getValue());
        localBasicAttributes.put(localAttribute);
      }
    }
    return localBasicAttributes;
  }
  
  public int size()
  {
    return this.entries.size();
  }
  
  public static String escapeValue(Object paramObject)
  {
    return (paramObject instanceof byte[]) ? escapeBinaryValue((byte[])paramObject) : escapeStringValue((String)paramObject);
  }
  
  private static String escapeStringValue(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuilder localStringBuilder = new StringBuilder(2 * paramString.length());
    for (int i = 0; (i < arrayOfChar.length) && (isWhitespace(arrayOfChar[i])); i++) {}
    for (int j = arrayOfChar.length - 1; (j >= 0) && (isWhitespace(arrayOfChar[j])); j--) {}
    for (int k = 0; k < arrayOfChar.length; k++)
    {
      char c = arrayOfChar[k];
      if ((k < i) || (k > j) || (",=+<>#;\"\\".indexOf(c) >= 0)) {
        localStringBuilder.append('\\');
      }
      localStringBuilder.append(c);
    }
    return localStringBuilder.toString();
  }
  
  private static String escapeBinaryValue(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(1 + 2 * paramArrayOfByte.length);
    localStringBuilder.append("#");
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i];
      localStringBuilder.append(Character.forDigit(0xF & j >>> 4, 16));
      localStringBuilder.append(Character.forDigit(0xF & j, 16));
    }
    return localStringBuilder.toString();
  }
  
  public static Object unescapeValue(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    int j = arrayOfChar.length;
    while ((i < j) && (isWhitespace(arrayOfChar[i]))) {
      i++;
    }
    while ((i < j) && (isWhitespace(arrayOfChar[(j - 1)]))) {
      j--;
    }
    if ((j != arrayOfChar.length) && (i < j) && (arrayOfChar[(j - 1)] == '\\')) {
      j++;
    }
    if (i >= j) {
      return "";
    }
    if (arrayOfChar[i] == '#') {
      return decodeHexPairs(arrayOfChar, ++i, j);
    }
    if ((arrayOfChar[i] == '"') && (arrayOfChar[(j - 1)] == '"'))
    {
      i++;
      j--;
    }
    StringBuilder localStringBuilder = new StringBuilder(j - i);
    int k = -1;
    for (int m = i; m < j; m++) {
      if ((arrayOfChar[m] == '\\') && (m + 1 < j))
      {
        if (!Character.isLetterOrDigit(arrayOfChar[(m + 1)]))
        {
          m++;
          localStringBuilder.append(arrayOfChar[m]);
          k = m;
        }
        else
        {
          byte[] arrayOfByte = getUtf8Octets(arrayOfChar, m, j);
          if (arrayOfByte.length > 0)
          {
            try
            {
              localStringBuilder.append(new String(arrayOfByte, "UTF8"));
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
            m += arrayOfByte.length * 3 - 1;
          }
          else
          {
            throw new IllegalArgumentException("Not a valid attribute string value:" + paramString + ",improper usage of backslash");
          }
        }
      }
      else {
        localStringBuilder.append(arrayOfChar[m]);
      }
    }
    m = localStringBuilder.length();
    if ((isWhitespace(localStringBuilder.charAt(m - 1))) && (k != j - 1)) {
      localStringBuilder.setLength(m - 1);
    }
    return localStringBuilder.toString();
  }
  
  private static byte[] decodeHexPairs(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[(paramInt2 - paramInt1) / 2];
    for (int i = 0; paramInt1 + 1 < paramInt2; i++)
    {
      int j = Character.digit(paramArrayOfChar[paramInt1], 16);
      int k = Character.digit(paramArrayOfChar[(paramInt1 + 1)], 16);
      if ((j < 0) || (k < 0)) {
        break;
      }
      arrayOfByte[i] = ((byte)((j << 4) + k));
      paramInt1 += 2;
    }
    if (paramInt1 != paramInt2) {
      throw new IllegalArgumentException("Illegal attribute value: " + new String(paramArrayOfChar));
    }
    return arrayOfByte;
  }
  
  private static byte[] getUtf8Octets(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte1 = new byte[(paramInt2 - paramInt1) / 3];
    int i = 0;
    while ((paramInt1 + 2 < paramInt2) && (paramArrayOfChar[(paramInt1++)] == '\\'))
    {
      int j = Character.digit(paramArrayOfChar[(paramInt1++)], 16);
      int k = Character.digit(paramArrayOfChar[(paramInt1++)], 16);
      if ((j < 0) || (k < 0)) {
        break;
      }
      arrayOfByte1[(i++)] = ((byte)((j << 4) + k));
    }
    if (i == arrayOfByte1.length) {
      return arrayOfByte1;
    }
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }
  
  private static boolean isWhitespace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\r');
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.entries = new ArrayList(1);
    String str = (String)paramObjectInputStream.readObject();
    try
    {
      new Rfc2253Parser(str).parseRdn(this);
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name: " + str);
    }
  }
  
  private static class RdnEntry
    implements Comparable<RdnEntry>
  {
    private String type;
    private Object value;
    private String comparable = null;
    
    private RdnEntry() {}
    
    String getType()
    {
      return this.type;
    }
    
    Object getValue()
    {
      return this.value;
    }
    
    public int compareTo(RdnEntry paramRdnEntry)
    {
      int i = this.type.compareToIgnoreCase(paramRdnEntry.type);
      if (i != 0) {
        return i;
      }
      if (this.value.equals(paramRdnEntry.value)) {
        return 0;
      }
      return getValueComparable().compareTo(paramRdnEntry.getValueComparable());
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof RdnEntry)) {
        return false;
      }
      RdnEntry localRdnEntry = (RdnEntry)paramObject;
      return (this.type.equalsIgnoreCase(localRdnEntry.type)) && (getValueComparable().equals(localRdnEntry.getValueComparable()));
    }
    
    public int hashCode()
    {
      return this.type.toUpperCase(Locale.ENGLISH).hashCode() + getValueComparable().hashCode();
    }
    
    public String toString()
    {
      return this.type + "=" + Rdn.escapeValue(this.value);
    }
    
    private String getValueComparable()
    {
      if (this.comparable != null) {
        return this.comparable;
      }
      if ((this.value instanceof byte[])) {
        this.comparable = Rdn.escapeBinaryValue((byte[])this.value);
      } else {
        this.comparable = ((String)this.value).toUpperCase(Locale.ENGLISH);
      }
      return this.comparable;
    }
  }
}
