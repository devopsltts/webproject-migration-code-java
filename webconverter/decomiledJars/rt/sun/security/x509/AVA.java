package sun.security.x509;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.security.AccessController;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import sun.security.action.GetBooleanAction;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class AVA
  implements DerEncoder
{
  private static final Debug debug = Debug.getInstance("x509", "\t[AVA]");
  private static final boolean PRESERVE_OLD_DC_ENCODING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.preserveOldDCEncoding"))).booleanValue();
  static final int DEFAULT = 1;
  static final int RFC1779 = 2;
  static final int RFC2253 = 3;
  final ObjectIdentifier oid;
  final DerValue value;
  private static final String specialChars1779 = ",=\n+<>#;\\\"";
  private static final String specialChars2253 = ",=+<>#;\\\"";
  private static final String specialCharsDefault = ",=\n+<>#;\\\" ";
  private static final String escapedDefault = ",+<>;\"";
  private static final String hexDigits = "0123456789ABCDEF";
  
  public AVA(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue)
  {
    if ((paramObjectIdentifier == null) || (paramDerValue == null)) {
      throw new NullPointerException();
    }
    this.oid = paramObjectIdentifier;
    this.value = paramDerValue;
  }
  
  AVA(Reader paramReader)
    throws IOException
  {
    this(paramReader, 1);
  }
  
  AVA(Reader paramReader, Map<String, String> paramMap)
    throws IOException
  {
    this(paramReader, 1, paramMap);
  }
  
  AVA(Reader paramReader, int paramInt)
    throws IOException
  {
    this(paramReader, paramInt, Collections.emptyMap());
  }
  
  AVA(Reader paramReader, int paramInt, Map<String, String> paramMap)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i;
    for (;;)
    {
      i = readChar(paramReader, "Incorrect AVA format");
      if (i == 61) {
        break;
      }
      localStringBuilder.append((char)i);
    }
    this.oid = AVAKeyword.getOID(localStringBuilder.toString(), paramInt, paramMap);
    localStringBuilder.setLength(0);
    if (paramInt == 3)
    {
      i = paramReader.read();
      if (i == 32) {
        throw new IOException("Incorrect AVA RFC2253 format - leading space must be escaped");
      }
    }
    else
    {
      do
      {
        i = paramReader.read();
      } while ((i == 32) || (i == 10));
    }
    if (i == -1)
    {
      this.value = new DerValue("");
      return;
    }
    if (i == 35) {
      this.value = parseHexString(paramReader, paramInt);
    } else if ((i == 34) && (paramInt != 3)) {
      this.value = parseQuotedString(paramReader, localStringBuilder);
    } else {
      this.value = parseString(paramReader, i, paramInt, localStringBuilder);
    }
  }
  
  public ObjectIdentifier getObjectIdentifier()
  {
    return this.oid;
  }
  
  public DerValue getDerValue()
  {
    return this.value;
  }
  
  public String getValueString()
  {
    try
    {
      String str = this.value.getAsString();
      if (str == null) {
        throw new RuntimeException("AVA string is null");
      }
      return str;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("AVA error: " + localIOException, localIOException);
    }
  }
  
  private static DerValue parseHexString(Reader paramReader, int paramInt)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int j = 0;
    for (int k = 0;; k++)
    {
      int i = paramReader.read();
      if (isTerminator(i, paramInt)) {
        break;
      }
      int m = "0123456789ABCDEF".indexOf(Character.toUpperCase((char)i));
      if (m == -1) {
        throw new IOException("AVA parse, invalid hex digit: " + (char)i);
      }
      if (k % 2 == 1)
      {
        j = (byte)(j * 16 + (byte)m);
        localByteArrayOutputStream.write(j);
      }
      else
      {
        j = (byte)m;
      }
    }
    if (k == 0) {
      throw new IOException("AVA parse, zero hex digits");
    }
    if (k % 2 == 1) {
      throw new IOException("AVA parse, odd number of hex digits");
    }
    return new DerValue(localByteArrayOutputStream.toByteArray());
  }
  
  private DerValue parseQuotedString(Reader paramReader, StringBuilder paramStringBuilder)
    throws IOException
  {
    int i = readChar(paramReader, "Quoted string did not end in quote");
    ArrayList localArrayList = new ArrayList();
    boolean bool = true;
    Object localObject;
    while (i != 34)
    {
      if (i == 92)
      {
        i = readChar(paramReader, "Quoted string did not end in quote");
        localObject = null;
        if ((localObject = getEmbeddedHexPair(i, paramReader)) != null)
        {
          bool = false;
          localArrayList.add(localObject);
          i = paramReader.read();
          continue;
        }
        if (",=\n+<>#;\\\"".indexOf((char)i) < 0) {
          throw new IOException("Invalid escaped character in AVA: " + (char)i);
        }
      }
      if (localArrayList.size() > 0)
      {
        localObject = getEmbeddedHexString(localArrayList);
        paramStringBuilder.append((String)localObject);
        localArrayList.clear();
      }
      bool &= DerValue.isPrintableStringChar((char)i);
      paramStringBuilder.append((char)i);
      i = readChar(paramReader, "Quoted string did not end in quote");
    }
    if (localArrayList.size() > 0)
    {
      localObject = getEmbeddedHexString(localArrayList);
      paramStringBuilder.append((String)localObject);
      localArrayList.clear();
    }
    do
    {
      i = paramReader.read();
    } while ((i == 10) || (i == 32));
    if (i != -1) {
      throw new IOException("AVA had characters other than whitespace after terminating quote");
    }
    if ((this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) || ((this.oid.equals(X500Name.DOMAIN_COMPONENT_OID)) && (!PRESERVE_OLD_DC_ENCODING))) {
      return new DerValue((byte)22, paramStringBuilder.toString().trim());
    }
    if (bool) {
      return new DerValue(paramStringBuilder.toString().trim());
    }
    return new DerValue((byte)12, paramStringBuilder.toString().trim());
  }
  
  private DerValue parseString(Reader paramReader, int paramInt1, int paramInt2, StringBuilder paramStringBuilder)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    boolean bool = true;
    int i = 0;
    int j = 1;
    int k = 0;
    do
    {
      i = 0;
      if (paramInt1 == 92)
      {
        i = 1;
        paramInt1 = readChar(paramReader, "Invalid trailing backslash");
        Byte localByte = null;
        if ((localByte = getEmbeddedHexPair(paramInt1, paramReader)) != null)
        {
          bool = false;
          localArrayList.add(localByte);
          paramInt1 = paramReader.read();
          j = 0;
          continue;
        }
        if ((paramInt2 == 1) && (",=\n+<>#;\\\" ".indexOf((char)paramInt1) == -1)) {
          throw new IOException("Invalid escaped character in AVA: '" + (char)paramInt1 + "'");
        }
        if (paramInt2 == 3) {
          if (paramInt1 == 32)
          {
            if ((j == 0) && (!trailingSpace(paramReader))) {
              throw new IOException("Invalid escaped space character in AVA.  Only a leading or trailing space character can be escaped.");
            }
          }
          else if (paramInt1 == 35)
          {
            if (j == 0) {
              throw new IOException("Invalid escaped '#' character in AVA.  Only a leading '#' can be escaped.");
            }
          }
          else if (",=+<>#;\\\"".indexOf((char)paramInt1) == -1) {
            throw new IOException("Invalid escaped character in AVA: '" + (char)paramInt1 + "'");
          }
        }
      }
      else if (paramInt2 == 3)
      {
        if (",=+<>#;\\\"".indexOf((char)paramInt1) != -1) {
          throw new IOException("Character '" + (char)paramInt1 + "' in AVA appears without escape");
        }
      }
      else if (",+<>;\"".indexOf((char)paramInt1) != -1)
      {
        throw new IOException("Character '" + (char)paramInt1 + "' in AVA appears without escape");
      }
      if (localArrayList.size() > 0)
      {
        for (int m = 0; m < k; m++) {
          paramStringBuilder.append(" ");
        }
        k = 0;
        String str1 = getEmbeddedHexString(localArrayList);
        paramStringBuilder.append(str1);
        localArrayList.clear();
      }
      bool &= DerValue.isPrintableStringChar((char)paramInt1);
      if ((paramInt1 == 32) && (i == 0))
      {
        k++;
      }
      else
      {
        for (int n = 0; n < k; n++) {
          paramStringBuilder.append(" ");
        }
        k = 0;
        paramStringBuilder.append((char)paramInt1);
      }
      paramInt1 = paramReader.read();
      j = 0;
    } while (!isTerminator(paramInt1, paramInt2));
    if ((paramInt2 == 3) && (k > 0)) {
      throw new IOException("Incorrect AVA RFC2253 format - trailing space must be escaped");
    }
    if (localArrayList.size() > 0)
    {
      String str2 = getEmbeddedHexString(localArrayList);
      paramStringBuilder.append(str2);
      localArrayList.clear();
    }
    if ((this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) || ((this.oid.equals(X500Name.DOMAIN_COMPONENT_OID)) && (!PRESERVE_OLD_DC_ENCODING))) {
      return new DerValue((byte)22, paramStringBuilder.toString());
    }
    if (bool) {
      return new DerValue(paramStringBuilder.toString());
    }
    return new DerValue((byte)12, paramStringBuilder.toString());
  }
  
  private static Byte getEmbeddedHexPair(int paramInt, Reader paramReader)
    throws IOException
  {
    if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)paramInt)) >= 0)
    {
      int i = readChar(paramReader, "unexpected EOF - escaped hex value must include two valid digits");
      if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)i)) >= 0)
      {
        int j = Character.digit((char)paramInt, 16);
        int k = Character.digit((char)i, 16);
        return new Byte((byte)((j << 4) + k));
      }
      throw new IOException("escaped hex value must include two valid digits");
    }
    return null;
  }
  
  private static String getEmbeddedHexString(List<Byte> paramList)
    throws IOException
  {
    int i = paramList.size();
    byte[] arrayOfByte = new byte[i];
    for (int j = 0; j < i; j++) {
      arrayOfByte[j] = ((Byte)paramList.get(j)).byteValue();
    }
    return new String(arrayOfByte, "UTF8");
  }
  
  private static boolean isTerminator(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case -1: 
    case 43: 
    case 44: 
      return true;
    case 59: 
      return paramInt2 != 3;
    }
    return false;
  }
  
  private static int readChar(Reader paramReader, String paramString)
    throws IOException
  {
    int i = paramReader.read();
    if (i == -1) {
      throw new IOException(paramString);
    }
    return i;
  }
  
  private static boolean trailingSpace(Reader paramReader)
    throws IOException
  {
    boolean bool = false;
    if (!paramReader.markSupported()) {
      return true;
    }
    paramReader.mark(9999);
    for (;;)
    {
      int i = paramReader.read();
      if (i == -1)
      {
        bool = true;
        break;
      }
      if (i != 32) {
        if (i == 92)
        {
          int j = paramReader.read();
          if (j != 32)
          {
            bool = false;
            break;
          }
        }
        else
        {
          bool = false;
          break;
        }
      }
    }
    paramReader.reset();
    return bool;
  }
  
  AVA(DerValue paramDerValue)
    throws IOException
  {
    if (paramDerValue.tag != 48) {
      throw new IOException("AVA not a sequence");
    }
    this.oid = X500Name.intern(paramDerValue.data.getOID());
    this.value = paramDerValue.data.getDerValue();
    if (paramDerValue.data.available() != 0) {
      throw new IOException("AVA, extra bytes = " + paramDerValue.data.available());
    }
  }
  
  AVA(DerInputStream paramDerInputStream)
    throws IOException
  {
    this(paramDerInputStream.getDerValue());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AVA)) {
      return false;
    }
    AVA localAVA = (AVA)paramObject;
    return toRFC2253CanonicalString().equals(localAVA.toRFC2253CanonicalString());
  }
  
  public int hashCode()
  {
    return toRFC2253CanonicalString().hashCode();
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    derEncode(paramDerOutputStream);
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.putOID(this.oid);
    this.value.encode(localDerOutputStream1);
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream2.toByteArray());
  }
  
  private String toKeyword(int paramInt, Map<String, String> paramMap)
  {
    return AVAKeyword.getKeyword(this.oid, paramInt, paramMap);
  }
  
  public String toString()
  {
    return toKeywordValueString(toKeyword(1, Collections.emptyMap()));
  }
  
  public String toRFC1779String()
  {
    return toRFC1779String(Collections.emptyMap());
  }
  
  public String toRFC1779String(Map<String, String> paramMap)
  {
    return toKeywordValueString(toKeyword(2, paramMap));
  }
  
  public String toRFC2253String()
  {
    return toRFC2253String(Collections.emptyMap());
  }
  
  public String toRFC2253String(Map<String, String> paramMap)
  {
    StringBuilder localStringBuilder1 = new StringBuilder(100);
    localStringBuilder1.append(toKeyword(3, paramMap));
    localStringBuilder1.append('=');
    Object localObject;
    if (((localStringBuilder1.charAt(0) >= '0') && (localStringBuilder1.charAt(0) <= '9')) || (!isDerString(this.value, false)))
    {
      localObject = null;
      try
      {
        localObject = this.value.toByteArray();
      }
      catch (IOException localIOException1)
      {
        throw new IllegalArgumentException("DER Value conversion");
      }
      localStringBuilder1.append('#');
      for (int i = 0; i < localObject.length; i++)
      {
        int j = localObject[i];
        localStringBuilder1.append(Character.forDigit(0xF & j >>> 4, 16));
        localStringBuilder1.append(Character.forDigit(0xF & j, 16));
      }
    }
    else
    {
      localObject = null;
      try
      {
        localObject = new String(this.value.getDataBytes(), "UTF8");
      }
      catch (IOException localIOException2)
      {
        throw new IllegalArgumentException("DER Value conversion");
      }
      StringBuilder localStringBuilder2 = new StringBuilder();
      char c;
      for (int k = 0; k < ((String)localObject).length(); k++)
      {
        m = ((String)localObject).charAt(k);
        if ((DerValue.isPrintableStringChar(m)) || (",=+<>#;\"\\".indexOf(m) >= 0))
        {
          if (",=+<>#;\"\\".indexOf(m) >= 0) {
            localStringBuilder2.append('\\');
          }
          localStringBuilder2.append(m);
        }
        else if (m == 0)
        {
          localStringBuilder2.append("\\00");
        }
        else if ((debug != null) && (Debug.isOn("ava")))
        {
          byte[] arrayOfByte = null;
          try
          {
            arrayOfByte = Character.toString(m).getBytes("UTF8");
          }
          catch (IOException localIOException3)
          {
            throw new IllegalArgumentException("DER Value conversion");
          }
          for (i1 = 0; i1 < arrayOfByte.length; i1++)
          {
            localStringBuilder2.append('\\');
            c = Character.forDigit(0xF & arrayOfByte[i1] >>> 4, 16);
            localStringBuilder2.append(Character.toUpperCase(c));
            c = Character.forDigit(0xF & arrayOfByte[i1], 16);
            localStringBuilder2.append(Character.toUpperCase(c));
          }
        }
        else
        {
          localStringBuilder2.append(m);
        }
      }
      char[] arrayOfChar = localStringBuilder2.toString().toCharArray();
      localStringBuilder2 = new StringBuilder();
      for (int m = 0; (m < arrayOfChar.length) && ((arrayOfChar[m] == ' ') || (arrayOfChar[m] == '\r')); m++) {}
      for (int n = arrayOfChar.length - 1; (n >= 0) && ((arrayOfChar[n] == ' ') || (arrayOfChar[n] == '\r')); n--) {}
      for (int i1 = 0; i1 < arrayOfChar.length; i1++)
      {
        c = arrayOfChar[i1];
        if ((i1 < m) || (i1 > n)) {
          localStringBuilder2.append('\\');
        }
        localStringBuilder2.append(c);
      }
      localStringBuilder1.append(localStringBuilder2.toString());
    }
    return localStringBuilder1.toString();
  }
  
  public String toRFC2253CanonicalString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder(40);
    localStringBuilder1.append(toKeyword(3, Collections.emptyMap()));
    localStringBuilder1.append('=');
    if (((localStringBuilder1.charAt(0) >= '0') && (localStringBuilder1.charAt(0) <= '9')) || (!isDerString(this.value, true)))
    {
      localObject = null;
      try
      {
        localObject = this.value.toByteArray();
      }
      catch (IOException localIOException1)
      {
        throw new IllegalArgumentException("DER Value conversion");
      }
      localStringBuilder1.append('#');
      for (int i = 0; i < localObject.length; i++)
      {
        int j = localObject[i];
        localStringBuilder1.append(Character.forDigit(0xF & j >>> 4, 16));
        localStringBuilder1.append(Character.forDigit(0xF & j, 16));
      }
    }
    else
    {
      localObject = null;
      try
      {
        localObject = new String(this.value.getDataBytes(), "UTF8");
      }
      catch (IOException localIOException2)
      {
        throw new IllegalArgumentException("DER Value conversion");
      }
      StringBuilder localStringBuilder2 = new StringBuilder();
      int k = 0;
      for (int m = 0; m < ((String)localObject).length(); m++)
      {
        char c = ((String)localObject).charAt(m);
        if ((DerValue.isPrintableStringChar(c)) || (",+<>;\"\\".indexOf(c) >= 0) || ((m == 0) && (c == '#')))
        {
          if (((m == 0) && (c == '#')) || (",+<>;\"\\".indexOf(c) >= 0)) {
            localStringBuilder2.append('\\');
          }
          if (!Character.isWhitespace(c))
          {
            k = 0;
            localStringBuilder2.append(c);
          }
          else if (k == 0)
          {
            k = 1;
            localStringBuilder2.append(c);
          }
        }
        else if ((debug != null) && (Debug.isOn("ava")))
        {
          k = 0;
          byte[] arrayOfByte = null;
          try
          {
            arrayOfByte = Character.toString(c).getBytes("UTF8");
          }
          catch (IOException localIOException3)
          {
            throw new IllegalArgumentException("DER Value conversion");
          }
          for (int n = 0; n < arrayOfByte.length; n++)
          {
            localStringBuilder2.append('\\');
            localStringBuilder2.append(Character.forDigit(0xF & arrayOfByte[n] >>> 4, 16));
            localStringBuilder2.append(Character.forDigit(0xF & arrayOfByte[n], 16));
          }
        }
        else
        {
          k = 0;
          localStringBuilder2.append(c);
        }
      }
      localStringBuilder1.append(localStringBuilder2.toString().trim());
    }
    Object localObject = localStringBuilder1.toString();
    localObject = ((String)localObject).toUpperCase(Locale.US).toLowerCase(Locale.US);
    return Normalizer.normalize((CharSequence)localObject, Normalizer.Form.NFKD);
  }
  
  private static boolean isDerString(DerValue paramDerValue, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      switch (paramDerValue.tag)
      {
      case 12: 
      case 19: 
        return true;
      }
      return false;
    }
    switch (paramDerValue.tag)
    {
    case 12: 
    case 19: 
    case 20: 
    case 22: 
    case 27: 
    case 30: 
      return true;
    }
    return false;
  }
  
  boolean hasRFC2253Keyword()
  {
    return AVAKeyword.hasKeyword(this.oid, 3);
  }
  
  private String toKeywordValueString(String paramString)
  {
    StringBuilder localStringBuilder1 = new StringBuilder(40);
    localStringBuilder1.append(paramString);
    localStringBuilder1.append("=");
    try
    {
      String str = this.value.getAsString();
      if (str == null)
      {
        byte[] arrayOfByte1 = this.value.toByteArray();
        localStringBuilder1.append('#');
        for (int j = 0; j < arrayOfByte1.length; j++)
        {
          localStringBuilder1.append("0123456789ABCDEF".charAt(arrayOfByte1[j] >> 4 & 0xF));
          localStringBuilder1.append("0123456789ABCDEF".charAt(arrayOfByte1[j] & 0xF));
        }
      }
      else
      {
        int i = 0;
        StringBuilder localStringBuilder2 = new StringBuilder();
        int k = 0;
        int m = str.length();
        int n = (m > 1) && (str.charAt(0) == '"') && (str.charAt(m - 1) == '"') ? 1 : 0;
        for (int i1 = 0; i1 < m; i1++)
        {
          char c1 = str.charAt(i1);
          if ((n != 0) && ((i1 == 0) || (i1 == m - 1)))
          {
            localStringBuilder2.append(c1);
          }
          else if ((DerValue.isPrintableStringChar(c1)) || (",+=\n<>#;\\\"".indexOf(c1) >= 0))
          {
            if ((i == 0) && (((i1 == 0) && ((c1 == ' ') || (c1 == '\n'))) || (",+=\n<>#;\\\"".indexOf(c1) >= 0))) {
              i = 1;
            }
            if ((c1 != ' ') && (c1 != '\n'))
            {
              if ((c1 == '"') || (c1 == '\\')) {
                localStringBuilder2.append('\\');
              }
              k = 0;
            }
            else
            {
              if ((i == 0) && (k != 0)) {
                i = 1;
              }
              k = 1;
            }
            localStringBuilder2.append(c1);
          }
          else if ((debug != null) && (Debug.isOn("ava")))
          {
            k = 0;
            byte[] arrayOfByte2 = Character.toString(c1).getBytes("UTF8");
            for (int i2 = 0; i2 < arrayOfByte2.length; i2++)
            {
              localStringBuilder2.append('\\');
              char c2 = Character.forDigit(0xF & arrayOfByte2[i2] >>> 4, 16);
              localStringBuilder2.append(Character.toUpperCase(c2));
              c2 = Character.forDigit(0xF & arrayOfByte2[i2], 16);
              localStringBuilder2.append(Character.toUpperCase(c2));
            }
          }
          else
          {
            k = 0;
            localStringBuilder2.append(c1);
          }
        }
        if (localStringBuilder2.length() > 0)
        {
          i1 = localStringBuilder2.charAt(localStringBuilder2.length() - 1);
          if ((i1 == 32) || (i1 == 10)) {
            i = 1;
          }
        }
        if ((n == 0) && (i != 0)) {
          localStringBuilder1.append("\"" + localStringBuilder2.toString() + "\"");
        } else {
          localStringBuilder1.append(localStringBuilder2.toString());
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new IllegalArgumentException("DER Value conversion");
    }
    return localStringBuilder1.toString();
  }
}
