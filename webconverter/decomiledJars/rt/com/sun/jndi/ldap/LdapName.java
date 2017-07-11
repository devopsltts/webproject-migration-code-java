package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public final class LdapName
  implements Name
{
  private transient String unparsed;
  private transient Vector<Rdn> rdns;
  private transient boolean valuesCaseSensitive = false;
  static final long serialVersionUID = -1595520034788997356L;
  
  public LdapName(String paramString)
    throws InvalidNameException
  {
    this.unparsed = paramString;
    parse();
  }
  
  private LdapName(String paramString, Vector<Rdn> paramVector)
  {
    this.unparsed = paramString;
    this.rdns = ((Vector)paramVector.clone());
  }
  
  private LdapName(String paramString, Vector<Rdn> paramVector, int paramInt1, int paramInt2)
  {
    this.unparsed = paramString;
    this.rdns = new Vector();
    for (int i = paramInt1; i < paramInt2; i++) {
      this.rdns.addElement(paramVector.elementAt(i));
    }
  }
  
  public Object clone()
  {
    return new LdapName(this.unparsed, this.rdns);
  }
  
  public String toString()
  {
    if (this.unparsed != null) {
      return this.unparsed;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = this.rdns.size() - 1; i >= 0; i--)
    {
      if (i < this.rdns.size() - 1) {
        localStringBuffer.append(',');
      }
      Rdn localRdn = (Rdn)this.rdns.elementAt(i);
      localStringBuffer.append(localRdn);
    }
    this.unparsed = new String(localStringBuffer);
    return this.unparsed;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof LdapName)) && (compareTo(paramObject) == 0);
  }
  
  public int compareTo(Object paramObject)
  {
    LdapName localLdapName = (LdapName)paramObject;
    if ((paramObject == this) || ((this.unparsed != null) && (this.unparsed.equals(localLdapName.unparsed)))) {
      return 0;
    }
    int i = Math.min(this.rdns.size(), localLdapName.rdns.size());
    for (int j = 0; j < i; j++)
    {
      Rdn localRdn1 = (Rdn)this.rdns.elementAt(j);
      Rdn localRdn2 = (Rdn)localLdapName.rdns.elementAt(j);
      int k = localRdn1.compareTo(localRdn2);
      if (k != 0) {
        return k;
      }
    }
    return this.rdns.size() - localLdapName.rdns.size();
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < this.rdns.size(); j++)
    {
      Rdn localRdn = (Rdn)this.rdns.elementAt(j);
      i += localRdn.hashCode();
    }
    return i;
  }
  
  public int size()
  {
    return this.rdns.size();
  }
  
  public boolean isEmpty()
  {
    return this.rdns.isEmpty();
  }
  
  public Enumeration<String> getAll()
  {
    final Enumeration localEnumeration = this.rdns.elements();
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return localEnumeration.hasMoreElements();
      }
      
      public String nextElement()
      {
        return ((LdapName.Rdn)localEnumeration.nextElement()).toString();
      }
    };
  }
  
  public String get(int paramInt)
  {
    return ((Rdn)this.rdns.elementAt(paramInt)).toString();
  }
  
  public Name getPrefix(int paramInt)
  {
    return new LdapName(null, this.rdns, 0, paramInt);
  }
  
  public Name getSuffix(int paramInt)
  {
    return new LdapName(null, this.rdns, paramInt, this.rdns.size());
  }
  
  public boolean startsWith(Name paramName)
  {
    int i = this.rdns.size();
    int j = paramName.size();
    return (i >= j) && (matches(0, j, paramName));
  }
  
  public boolean endsWith(Name paramName)
  {
    int i = this.rdns.size();
    int j = paramName.size();
    return (i >= j) && (matches(i - j, i, paramName));
  }
  
  public void setValuesCaseSensitive(boolean paramBoolean)
  {
    toString();
    this.rdns = null;
    try
    {
      parse();
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new IllegalStateException("Cannot parse name: " + this.unparsed);
    }
    this.valuesCaseSensitive = paramBoolean;
  }
  
  private boolean matches(int paramInt1, int paramInt2, Name paramName)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      Object localObject;
      Rdn localRdn;
      if ((paramName instanceof LdapName))
      {
        localObject = (LdapName)paramName;
        localRdn = (Rdn)((LdapName)localObject).rdns.elementAt(i - paramInt1);
      }
      else
      {
        localObject = paramName.get(i - paramInt1);
        try
        {
          localRdn = new DnParser((String)localObject, this.valuesCaseSensitive).getRdn();
        }
        catch (InvalidNameException localInvalidNameException)
        {
          return false;
        }
      }
      if (!localRdn.equals(this.rdns.elementAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public Name addAll(Name paramName)
    throws InvalidNameException
  {
    return addAll(size(), paramName);
  }
  
  public Name addAll(int paramInt, Name paramName)
    throws InvalidNameException
  {
    Object localObject;
    if ((paramName instanceof LdapName))
    {
      localObject = (LdapName)paramName;
      for (int i = 0; i < ((LdapName)localObject).rdns.size(); i++) {
        this.rdns.insertElementAt(((LdapName)localObject).rdns.elementAt(i), paramInt++);
      }
    }
    else
    {
      localObject = paramName.getAll();
      while (((Enumeration)localObject).hasMoreElements())
      {
        DnParser localDnParser = new DnParser((String)((Enumeration)localObject).nextElement(), this.valuesCaseSensitive);
        this.rdns.insertElementAt(localDnParser.getRdn(), paramInt++);
      }
    }
    this.unparsed = null;
    return this;
  }
  
  public Name add(String paramString)
    throws InvalidNameException
  {
    return add(size(), paramString);
  }
  
  public Name add(int paramInt, String paramString)
    throws InvalidNameException
  {
    Rdn localRdn = new DnParser(paramString, this.valuesCaseSensitive).getRdn();
    this.rdns.insertElementAt(localRdn, paramInt);
    this.unparsed = null;
    return this;
  }
  
  public Object remove(int paramInt)
    throws InvalidNameException
  {
    String str = get(paramInt);
    this.rdns.removeElementAt(paramInt);
    this.unparsed = null;
    return str;
  }
  
  private void parse()
    throws InvalidNameException
  {
    this.rdns = new DnParser(this.unparsed, this.valuesCaseSensitive).getDn();
  }
  
  private static boolean isWhitespace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\r');
  }
  
  public static String escapeAttributeValue(Object paramObject)
  {
    return TypeAndValue.escapeValue(paramObject);
  }
  
  public static Object unescapeAttributeValue(String paramString)
  {
    return TypeAndValue.unescapeValue(paramString);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeObject(toString());
    paramObjectOutputStream.writeBoolean(this.valuesCaseSensitive);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.unparsed = ((String)paramObjectInputStream.readObject());
    this.valuesCaseSensitive = paramObjectInputStream.readBoolean();
    try
    {
      parse();
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name: " + this.unparsed);
    }
  }
  
  static class DnParser
  {
    private final String name;
    private final char[] chars;
    private final int len;
    private int cur = 0;
    private boolean valuesCaseSensitive;
    
    DnParser(String paramString, boolean paramBoolean)
      throws InvalidNameException
    {
      this.name = paramString;
      this.len = paramString.length();
      this.chars = paramString.toCharArray();
      this.valuesCaseSensitive = paramBoolean;
    }
    
    Vector<LdapName.Rdn> getDn()
      throws InvalidNameException
    {
      this.cur = 0;
      Vector localVector = new Vector(this.len / 3 + 10);
      if (this.len == 0) {
        return localVector;
      }
      localVector.addElement(parseRdn());
      while (this.cur < this.len) {
        if ((this.chars[this.cur] == ',') || (this.chars[this.cur] == ';'))
        {
          this.cur += 1;
          localVector.insertElementAt(parseRdn(), 0);
        }
        else
        {
          throw new InvalidNameException("Invalid name: " + this.name);
        }
      }
      return localVector;
    }
    
    LdapName.Rdn getRdn()
      throws InvalidNameException
    {
      LdapName.Rdn localRdn = parseRdn();
      if (this.cur < this.len) {
        throw new InvalidNameException("Invalid RDN: " + this.name);
      }
      return localRdn;
    }
    
    private LdapName.Rdn parseRdn()
      throws InvalidNameException
    {
      LdapName.Rdn localRdn = new LdapName.Rdn();
      while (this.cur < this.len)
      {
        consumeWhitespace();
        String str1 = parseAttrType();
        consumeWhitespace();
        if ((this.cur >= this.len) || (this.chars[this.cur] != '=')) {
          throw new InvalidNameException("Invalid name: " + this.name);
        }
        this.cur += 1;
        consumeWhitespace();
        String str2 = parseAttrValue();
        consumeWhitespace();
        localRdn.add(new LdapName.TypeAndValue(str1, str2, this.valuesCaseSensitive));
        if ((this.cur >= this.len) || (this.chars[this.cur] != '+')) {
          break;
        }
        this.cur += 1;
      }
      return localRdn;
    }
    
    private String parseAttrType()
      throws InvalidNameException
    {
      int i = this.cur;
      while (this.cur < this.len)
      {
        char c = this.chars[this.cur];
        if ((!Character.isLetterOrDigit(c)) && (c != '.') && (c != '-') && (c != ' ')) {
          break;
        }
        this.cur += 1;
      }
      while ((this.cur > i) && (this.chars[(this.cur - 1)] == ' ')) {
        this.cur -= 1;
      }
      if (i == this.cur) {
        throw new InvalidNameException("Invalid name: " + this.name);
      }
      return new String(this.chars, i, this.cur - i);
    }
    
    private String parseAttrValue()
      throws InvalidNameException
    {
      if ((this.cur < this.len) && (this.chars[this.cur] == '#')) {
        return parseBinaryAttrValue();
      }
      if ((this.cur < this.len) && (this.chars[this.cur] == '"')) {
        return parseQuotedAttrValue();
      }
      return parseStringAttrValue();
    }
    
    private String parseBinaryAttrValue()
      throws InvalidNameException
    {
      int i = this.cur;
      for (this.cur += 1; (this.cur < this.len) && (Character.isLetterOrDigit(this.chars[this.cur])); this.cur += 1) {}
      return new String(this.chars, i, this.cur - i);
    }
    
    private String parseQuotedAttrValue()
      throws InvalidNameException
    {
      int i = this.cur;
      for (this.cur += 1; (this.cur < this.len) && (this.chars[this.cur] != '"'); this.cur += 1) {
        if (this.chars[this.cur] == '\\') {
          this.cur += 1;
        }
      }
      if (this.cur >= this.len) {
        throw new InvalidNameException("Invalid name: " + this.name);
      }
      this.cur += 1;
      return new String(this.chars, i, this.cur - i);
    }
    
    private String parseStringAttrValue()
      throws InvalidNameException
    {
      int i = this.cur;
      int j = -1;
      while ((this.cur < this.len) && (!atTerminator()))
      {
        if (this.chars[this.cur] == '\\')
        {
          this.cur += 1;
          j = this.cur;
        }
        this.cur += 1;
      }
      if (this.cur > this.len) {
        throw new InvalidNameException("Invalid name: " + this.name);
      }
      for (int k = this.cur; (k > i) && (LdapName.isWhitespace(this.chars[(k - 1)])) && (j != k - 1); k--) {}
      return new String(this.chars, i, k - i);
    }
    
    private void consumeWhitespace()
    {
      while ((this.cur < this.len) && (LdapName.isWhitespace(this.chars[this.cur]))) {
        this.cur += 1;
      }
    }
    
    private boolean atTerminator()
    {
      return (this.cur < this.len) && ((this.chars[this.cur] == ',') || (this.chars[this.cur] == ';') || (this.chars[this.cur] == '+'));
    }
  }
  
  static class Rdn
  {
    private final Vector<LdapName.TypeAndValue> tvs = new Vector();
    
    Rdn() {}
    
    void add(LdapName.TypeAndValue paramTypeAndValue)
    {
      for (int i = 0; i < this.tvs.size(); i++)
      {
        int j = paramTypeAndValue.compareTo(this.tvs.elementAt(i));
        if (j == 0) {
          return;
        }
        if (j < 0) {
          break;
        }
      }
      this.tvs.insertElementAt(paramTypeAndValue, i);
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < this.tvs.size(); i++)
      {
        if (i > 0) {
          localStringBuffer.append('+');
        }
        localStringBuffer.append(this.tvs.elementAt(i));
      }
      return new String(localStringBuffer);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Rdn)) && (compareTo(paramObject) == 0);
    }
    
    public int compareTo(Object paramObject)
    {
      Rdn localRdn = (Rdn)paramObject;
      int i = Math.min(this.tvs.size(), localRdn.tvs.size());
      for (int j = 0; j < i; j++)
      {
        LdapName.TypeAndValue localTypeAndValue = (LdapName.TypeAndValue)this.tvs.elementAt(j);
        int k = localTypeAndValue.compareTo(localRdn.tvs.elementAt(j));
        if (k != 0) {
          return k;
        }
      }
      return this.tvs.size() - localRdn.tvs.size();
    }
    
    public int hashCode()
    {
      int i = 0;
      for (int j = 0; j < this.tvs.size(); j++) {
        i += ((LdapName.TypeAndValue)this.tvs.elementAt(j)).hashCode();
      }
      return i;
    }
    
    Attributes toAttributes()
    {
      BasicAttributes localBasicAttributes = new BasicAttributes(true);
      for (int i = 0; i < this.tvs.size(); i++)
      {
        LdapName.TypeAndValue localTypeAndValue = (LdapName.TypeAndValue)this.tvs.elementAt(i);
        Attribute localAttribute;
        if ((localAttribute = localBasicAttributes.get(localTypeAndValue.getType())) == null) {
          localBasicAttributes.put(localTypeAndValue.getType(), localTypeAndValue.getUnescapedValue());
        } else {
          localAttribute.add(localTypeAndValue.getUnescapedValue());
        }
      }
      return localBasicAttributes;
    }
  }
  
  static class TypeAndValue
  {
    private final String type;
    private final String value;
    private final boolean binary;
    private final boolean valueCaseSensitive;
    private String comparable = null;
    
    TypeAndValue(String paramString1, String paramString2, boolean paramBoolean)
    {
      this.type = paramString1;
      this.value = paramString2;
      this.binary = paramString2.startsWith("#");
      this.valueCaseSensitive = paramBoolean;
    }
    
    public String toString()
    {
      return this.type + "=" + this.value;
    }
    
    public int compareTo(Object paramObject)
    {
      TypeAndValue localTypeAndValue = (TypeAndValue)paramObject;
      int i = this.type.compareToIgnoreCase(localTypeAndValue.type);
      if (i != 0) {
        return i;
      }
      if (this.value.equals(localTypeAndValue.value)) {
        return 0;
      }
      return getValueComparable().compareTo(localTypeAndValue.getValueComparable());
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof TypeAndValue)) {
        return false;
      }
      TypeAndValue localTypeAndValue = (TypeAndValue)paramObject;
      return (this.type.equalsIgnoreCase(localTypeAndValue.type)) && ((this.value.equals(localTypeAndValue.value)) || (getValueComparable().equals(localTypeAndValue.getValueComparable())));
    }
    
    public int hashCode()
    {
      return this.type.toUpperCase(Locale.ENGLISH).hashCode() + getValueComparable().hashCode();
    }
    
    String getType()
    {
      return this.type;
    }
    
    Object getUnescapedValue()
    {
      return unescapeValue(this.value);
    }
    
    private String getValueComparable()
    {
      if (this.comparable != null) {
        return this.comparable;
      }
      if (this.binary)
      {
        this.comparable = this.value.toUpperCase(Locale.ENGLISH);
      }
      else
      {
        this.comparable = ((String)unescapeValue(this.value));
        if (!this.valueCaseSensitive) {
          this.comparable = this.comparable.toUpperCase(Locale.ENGLISH);
        }
      }
      return this.comparable;
    }
    
    static String escapeValue(Object paramObject)
    {
      return (paramObject instanceof byte[]) ? escapeBinaryValue((byte[])paramObject) : escapeStringValue((String)paramObject);
    }
    
    private static String escapeStringValue(String paramString)
    {
      char[] arrayOfChar = paramString.toCharArray();
      StringBuffer localStringBuffer = new StringBuffer(2 * paramString.length());
      for (int i = 0; (i < arrayOfChar.length) && (LdapName.isWhitespace(arrayOfChar[i])); i++) {}
      for (int j = arrayOfChar.length - 1; (j >= 0) && (LdapName.isWhitespace(arrayOfChar[j])); j--) {}
      for (int k = 0; k < arrayOfChar.length; k++)
      {
        char c = arrayOfChar[k];
        if ((k < i) || (k > j) || (",=+<>#;\"\\".indexOf(c) >= 0)) {
          localStringBuffer.append('\\');
        }
        localStringBuffer.append(c);
      }
      return new String(localStringBuffer);
    }
    
    private static String escapeBinaryValue(byte[] paramArrayOfByte)
    {
      StringBuffer localStringBuffer = new StringBuffer(1 + 2 * paramArrayOfByte.length);
      localStringBuffer.append("#");
      for (int i = 0; i < paramArrayOfByte.length; i++)
      {
        int j = paramArrayOfByte[i];
        localStringBuffer.append(Character.forDigit(0xF & j >>> 4, 16));
        localStringBuffer.append(Character.forDigit(0xF & j, 16));
      }
      return new String(localStringBuffer).toUpperCase(Locale.ENGLISH);
    }
    
    static Object unescapeValue(String paramString)
    {
      char[] arrayOfChar = paramString.toCharArray();
      int i = 0;
      int j = arrayOfChar.length;
      while ((i < j) && (LdapName.isWhitespace(arrayOfChar[i]))) {
        i++;
      }
      while ((i < j) && (LdapName.isWhitespace(arrayOfChar[(j - 1)]))) {
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
      StringBuffer localStringBuffer = new StringBuffer(j - i);
      int k = -1;
      for (int m = i; m < j; m++) {
        if ((arrayOfChar[m] == '\\') && (m + 1 < j))
        {
          if (!Character.isLetterOrDigit(arrayOfChar[(m + 1)]))
          {
            m++;
            localStringBuffer.append(arrayOfChar[m]);
            k = m;
          }
          else
          {
            byte[] arrayOfByte = getUtf8Octets(arrayOfChar, m, j);
            if (arrayOfByte.length > 0)
            {
              try
              {
                localStringBuffer.append(new String(arrayOfByte, "UTF8"));
              }
              catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
              m += arrayOfByte.length * 3 - 1;
            }
            else
            {
              throw new IllegalArgumentException("Not a valid attribute string value:" + paramString + ", improper usage of backslash");
            }
          }
        }
        else {
          localStringBuffer.append(arrayOfChar[m]);
        }
      }
      m = localStringBuffer.length();
      if ((LdapName.isWhitespace(localStringBuffer.charAt(m - 1))) && (k != j - 1)) {
        localStringBuffer.setLength(m - 1);
      }
      return new String(localStringBuffer);
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
        throw new IllegalArgumentException("Illegal attribute value: #" + new String(paramArrayOfChar));
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
  }
}
