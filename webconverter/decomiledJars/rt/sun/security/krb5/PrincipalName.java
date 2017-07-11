package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import sun.misc.Unsafe;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PrincipalName
  implements Cloneable
{
  public static final int KRB_NT_UNKNOWN = 0;
  public static final int KRB_NT_PRINCIPAL = 1;
  public static final int KRB_NT_SRV_INST = 2;
  public static final int KRB_NT_SRV_HST = 3;
  public static final int KRB_NT_SRV_XHST = 4;
  public static final int KRB_NT_UID = 5;
  public static final String TGS_DEFAULT_SRV_NAME = "krbtgt";
  public static final int TGS_DEFAULT_NT = 2;
  public static final char NAME_COMPONENT_SEPARATOR = '/';
  public static final char NAME_REALM_SEPARATOR = '@';
  public static final char REALM_COMPONENT_SEPARATOR = '.';
  public static final String NAME_COMPONENT_SEPARATOR_STR = "/";
  public static final String NAME_REALM_SEPARATOR_STR = "@";
  public static final String REALM_COMPONENT_SEPARATOR_STR = ".";
  private final int nameType;
  private final String[] nameStrings;
  private final Realm nameRealm;
  private final boolean realmDeduced;
  private transient String salt = null;
  private static final long NAME_STRINGS_OFFSET;
  private static final Unsafe UNSAFE;
  
  public PrincipalName(int paramInt, String[] paramArrayOfString, Realm paramRealm)
  {
    if (paramRealm == null) {
      throw new IllegalArgumentException("Null realm not allowed");
    }
    validateNameStrings(paramArrayOfString);
    this.nameType = paramInt;
    this.nameStrings = ((String[])paramArrayOfString.clone());
    this.nameRealm = paramRealm;
    this.realmDeduced = false;
  }
  
  public PrincipalName(String[] paramArrayOfString, String paramString)
    throws RealmException
  {
    this(0, paramArrayOfString, new Realm(paramString));
  }
  
  private static void validateNameStrings(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      throw new IllegalArgumentException("Null nameStrings not allowed");
    }
    if (paramArrayOfString.length == 0) {
      throw new IllegalArgumentException("Empty nameStrings not allowed");
    }
    for (String str : paramArrayOfString)
    {
      if (str == null) {
        throw new IllegalArgumentException("Null nameString not allowed");
      }
      if (str.isEmpty()) {
        throw new IllegalArgumentException("Empty nameString not allowed");
      }
    }
  }
  
  public Object clone()
  {
    try
    {
      PrincipalName localPrincipalName = (PrincipalName)super.clone();
      UNSAFE.putObject(this, NAME_STRINGS_OFFSET, this.nameStrings.clone());
      return localPrincipalName;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError("Should never happen");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof PrincipalName))
    {
      PrincipalName localPrincipalName = (PrincipalName)paramObject;
      return (this.nameRealm.equals(localPrincipalName.nameRealm)) && (Arrays.equals(this.nameStrings, localPrincipalName.nameStrings));
    }
    return false;
  }
  
  public PrincipalName(DerValue paramDerValue, Realm paramRealm)
    throws Asn1Exception, IOException
  {
    if (paramRealm == null) {
      throw new IllegalArgumentException("Null realm not allowed");
    }
    this.realmDeduced = false;
    this.nameRealm = paramRealm;
    if (paramDerValue == null) {
      throw new IllegalArgumentException("Null encoding not allowed");
    }
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    Object localObject;
    if ((localDerValue1.getTag() & 0x1F) == 0)
    {
      localObject = localDerValue1.getData().getBigInteger();
      this.nameType = ((BigInteger)localObject).intValue();
    }
    else
    {
      throw new Asn1Exception(906);
    }
    localDerValue1 = paramDerValue.getData().getDerValue();
    if ((localDerValue1.getTag() & 0x1F) == 1)
    {
      localObject = localDerValue1.getData().getDerValue();
      if (((DerValue)localObject).getTag() != 48) {
        throw new Asn1Exception(906);
      }
      Vector localVector = new Vector();
      while (((DerValue)localObject).getData().available() > 0)
      {
        DerValue localDerValue2 = ((DerValue)localObject).getData().getDerValue();
        String str = new KerberosString(localDerValue2).toString();
        localVector.addElement(str);
      }
      this.nameStrings = new String[localVector.size()];
      localVector.copyInto(this.nameStrings);
      validateNameStrings(this.nameStrings);
    }
    else
    {
      throw new Asn1Exception(906);
    }
  }
  
  public static PrincipalName parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean, Realm paramRealm)
    throws Asn1Exception, IOException, RealmException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    if (paramRealm == null) {
      paramRealm = Realm.getDefault();
    }
    return new PrincipalName(localDerValue2, paramRealm);
  }
  
  private static String[] parseName(String paramString)
  {
    Vector localVector = new Vector();
    String str1 = paramString;
    int i = 0;
    int j = 0;
    String str2;
    while (i < str1.length())
    {
      if (str1.charAt(i) == '/')
      {
        if ((i > 0) && (str1.charAt(i - 1) == '\\'))
        {
          str1 = str1.substring(0, i - 1) + str1.substring(i, str1.length());
          continue;
        }
        if (j <= i)
        {
          str2 = str1.substring(j, i);
          localVector.addElement(str2);
        }
        j = i + 1;
      }
      else if (str1.charAt(i) == '@')
      {
        if ((i > 0) && (str1.charAt(i - 1) == '\\'))
        {
          str1 = str1.substring(0, i - 1) + str1.substring(i, str1.length());
          continue;
        }
        if (j < i)
        {
          str2 = str1.substring(j, i);
          localVector.addElement(str2);
        }
        j = i + 1;
        break;
      }
      i++;
    }
    if (i == str1.length())
    {
      str2 = str1.substring(j, i);
      localVector.addElement(str2);
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public PrincipalName(String paramString1, int paramInt, String paramString2)
    throws RealmException
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("Null name not allowed");
    }
    String[] arrayOfString = parseName(paramString1);
    validateNameStrings(arrayOfString);
    if (paramString2 == null) {
      paramString2 = Realm.parseRealmAtSeparator(paramString1);
    }
    this.realmDeduced = (paramString2 == null);
    switch (paramInt)
    {
    case 3: 
      Object localObject;
      if (arrayOfString.length >= 2)
      {
        localObject = arrayOfString[1];
        try
        {
          String str = InetAddress.getByName((String)localObject).getCanonicalHostName();
          if (str.toLowerCase(Locale.ENGLISH).startsWith(((String)localObject).toLowerCase(Locale.ENGLISH) + ".")) {
            localObject = str;
          }
        }
        catch (UnknownHostException|SecurityException localUnknownHostException) {}
        arrayOfString[1] = ((String)localObject).toLowerCase(Locale.ENGLISH);
      }
      this.nameStrings = arrayOfString;
      this.nameType = paramInt;
      if (paramString2 != null)
      {
        this.nameRealm = new Realm(paramString2);
      }
      else
      {
        localObject = mapHostToRealm(arrayOfString[1]);
        if (localObject != null) {
          this.nameRealm = new Realm((String)localObject);
        } else {
          this.nameRealm = Realm.getDefault();
        }
      }
      break;
    case 0: 
    case 1: 
    case 2: 
    case 4: 
    case 5: 
      this.nameStrings = arrayOfString;
      this.nameType = paramInt;
      if (paramString2 != null) {
        this.nameRealm = new Realm(paramString2);
      } else {
        this.nameRealm = Realm.getDefault();
      }
      break;
    default: 
      throw new IllegalArgumentException("Illegal name type");
    }
  }
  
  public PrincipalName(String paramString, int paramInt)
    throws RealmException
  {
    this(paramString, paramInt, (String)null);
  }
  
  public PrincipalName(String paramString)
    throws RealmException
  {
    this(paramString, 0);
  }
  
  public PrincipalName(String paramString1, String paramString2)
    throws RealmException
  {
    this(paramString1, 0, paramString2);
  }
  
  public static PrincipalName tgsService(String paramString1, String paramString2)
    throws KrbException
  {
    return new PrincipalName(2, new String[] { "krbtgt", paramString1 }, new Realm(paramString2));
  }
  
  public String getRealmAsString()
  {
    return getRealmString();
  }
  
  public String getPrincipalNameAsString()
  {
    StringBuffer localStringBuffer = new StringBuffer(this.nameStrings[0]);
    for (int i = 1; i < this.nameStrings.length; i++) {
      localStringBuffer.append(this.nameStrings[i]);
    }
    return localStringBuffer.toString();
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
  
  public String getName()
  {
    return toString();
  }
  
  public int getNameType()
  {
    return this.nameType;
  }
  
  public String[] getNameStrings()
  {
    return (String[])this.nameStrings.clone();
  }
  
  public byte[][] toByteArray()
  {
    byte[][] arrayOfByte = new byte[this.nameStrings.length][];
    for (int i = 0; i < this.nameStrings.length; i++)
    {
      arrayOfByte[i] = new byte[this.nameStrings[i].length()];
      arrayOfByte[i] = this.nameStrings[i].getBytes();
    }
    return arrayOfByte;
  }
  
  public String getRealmString()
  {
    return this.nameRealm.toString();
  }
  
  public Realm getRealm()
  {
    return this.nameRealm;
  }
  
  public String getSalt()
  {
    if (this.salt == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(this.nameRealm.toString());
      for (int i = 0; i < this.nameStrings.length; i++) {
        localStringBuffer.append(this.nameStrings[i]);
      }
      return localStringBuffer.toString();
    }
    return this.salt;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.nameStrings.length; i++)
    {
      if (i > 0) {
        localStringBuffer.append("/");
      }
      localStringBuffer.append(this.nameStrings[i]);
    }
    localStringBuffer.append("@");
    localStringBuffer.append(this.nameRealm.toString());
    return localStringBuffer.toString();
  }
  
  public String getNameString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.nameStrings.length; i++)
    {
      if (i > 0) {
        localStringBuffer.append("/");
      }
      localStringBuffer.append(this.nameStrings[i]);
    }
    return localStringBuffer.toString();
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    BigInteger localBigInteger = BigInteger.valueOf(this.nameType);
    localDerOutputStream2.putInteger(localBigInteger);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[this.nameStrings.length];
    for (int i = 0; i < this.nameStrings.length; i++) {
      arrayOfDerValue[i] = new KerberosString(this.nameStrings[i]).toDerValue();
    }
    localDerOutputStream2.putSequence(arrayOfDerValue);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public boolean match(PrincipalName paramPrincipalName)
  {
    boolean bool = true;
    if ((this.nameRealm != null) && (paramPrincipalName.nameRealm != null) && (!this.nameRealm.toString().equalsIgnoreCase(paramPrincipalName.nameRealm.toString()))) {
      bool = false;
    }
    if (this.nameStrings.length != paramPrincipalName.nameStrings.length) {
      bool = false;
    } else {
      for (int i = 0; i < this.nameStrings.length; i++) {
        if (!this.nameStrings[i].equalsIgnoreCase(paramPrincipalName.nameStrings[i])) {
          bool = false;
        }
      }
    }
    return bool;
  }
  
  public void writePrincipal(CCacheOutputStream paramCCacheOutputStream)
    throws IOException
  {
    paramCCacheOutputStream.write32(this.nameType);
    paramCCacheOutputStream.write32(this.nameStrings.length);
    byte[] arrayOfByte1 = null;
    arrayOfByte1 = this.nameRealm.toString().getBytes();
    paramCCacheOutputStream.write32(arrayOfByte1.length);
    paramCCacheOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte2 = null;
    for (int i = 0; i < this.nameStrings.length; i++)
    {
      arrayOfByte2 = this.nameStrings[i].getBytes();
      paramCCacheOutputStream.write32(arrayOfByte2.length);
      paramCCacheOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
    }
  }
  
  public String getInstanceComponent()
  {
    if ((this.nameStrings != null) && (this.nameStrings.length >= 2)) {
      return new String(this.nameStrings[1]);
    }
    return null;
  }
  
  static String mapHostToRealm(String paramString)
  {
    String str1 = null;
    try
    {
      String str2 = null;
      Config localConfig = Config.getInstance();
      if ((str1 = localConfig.get(new String[] { "domain_realm", paramString })) != null) {
        return str1;
      }
      for (int i = 1; i < paramString.length(); i++) {
        if ((paramString.charAt(i) == '.') && (i != paramString.length() - 1))
        {
          str2 = paramString.substring(i);
          str1 = localConfig.get(new String[] { "domain_realm", str2 });
          if (str1 != null) {
            break;
          }
          str2 = paramString.substring(i + 1);
          str1 = localConfig.get(new String[] { "domain_realm", str2 });
          if (str1 != null) {
            break;
          }
        }
      }
    }
    catch (KrbException localKrbException) {}
    return str1;
  }
  
  public boolean isRealmDeduced()
  {
    return this.realmDeduced;
  }
  
  static
  {
    try
    {
      Unsafe localUnsafe = Unsafe.getUnsafe();
      NAME_STRINGS_OFFSET = localUnsafe.objectFieldOffset(PrincipalName.class.getDeclaredField("nameStrings"));
      UNSAFE = localUnsafe;
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new Error(localReflectiveOperationException);
    }
  }
}
